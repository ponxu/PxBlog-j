package com.ponxu.blog.web.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ponxu.blog.Global;
import com.ponxu.blog.web.WebUtils;
import com.ponxu.dbutils.DBManager;
import com.ponxu.utils.StringUtils;

/**
 * 全局过滤器; 职责: 1 加载配置, 2 路径过滤/重写, 3 编码设置, 4 权限, 5 释放数据库连接
 * 
 * @author xwz
 * 
 */
public class URLRewriteFilter implements Filter {
	private static Log LOG = LogFactory.getLog(URLRewriteFilter.class);
	private static final String LOGIN_PAGE = "/login";
	private Map<String, Pattern> patterns;

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;

			request.setCharacterEncoding(Global.encoding);
			response.setCharacterEncoding(Global.encoding);
			response.setContentType("text/html; charset=" + Global.encoding);

			String uri = request.getServletPath();
			LOG.debug("uri: " + uri);

			// 权限校验
			if (!checkAuth(request, response)) {
				WebUtils.redirect(request, response, LOGIN_PAGE);
				return;
			}

			// 前缀校验 /后缀校验
			if (checkPrefix(request, response) || checkSuffix(request, response)) {
				chain.doFilter(request, response);
				return;
			}

			// 重写校验
			String right = checkRewrite(request, response);
			if (StringUtils.isNotEmpty(right)) {
				WebUtils.forward(request, response, right);
				return;
			}

			chain.doFilter(request, response);
		} catch (Exception e) {
			DBManager.rollback();
		} finally {
			DBManager.commit();
			DBManager.release();
		}
	}

	/**
	 * 权限校验
	 * 
	 * @return true:有权限
	 */
	private boolean checkAuth(HttpServletRequest request, HttpServletResponse response) {
		boolean canNext = true;
		String uri = request.getServletPath();
		if (uri.startsWith(Global.authPathPrefix) && WebUtils.sessionGet(request, "admin") == null) {
			LOG.debug("Can't access: " + uri);
			
			canNext = false;
		}
		return canNext;
	}

	/**
	 * 检查前缀
	 * 
	 * @return true: 在前缀过滤中找到
	 */
	private boolean checkPrefix(HttpServletRequest request, HttpServletResponse response) {
		boolean funded = false;
		String uri = request.getServletPath();
		for (int i = 0; i < Global.ignorePrefix.length; i++) {
			if (uri.startsWith(Global.ignorePrefix[i])) {
				LOG.debug("前缀: " + Global.ignorePrefix[i]);
				funded = true;
				break;
			}
		}
		return funded;
	}

	/**
	 * 检查后缀
	 * 
	 * @return true: 在后缀过滤中找到
	 */
	private boolean checkSuffix(HttpServletRequest request, HttpServletResponse response) {
		boolean funded = false;
		String uri = request.getServletPath();
		for (int i = 0; i < Global.ignoreSuffix.length; i++) {
			if (uri.endsWith(Global.ignoreSuffix[i])) {
				LOG.debug("后缀: " + Global.ignoreSuffix[i]);
				funded = true;
				break;
			}
		}
		return funded;
	}

	/**
	 * 检查重写规则
	 * 
	 * @return null:不需要重写 否则返回正确路径
	 */
	private String checkRewrite(HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getServletPath();
		String queryString = request.getQueryString();
		for (int i = 0; i < Global.rulesFrom.length; i++) {
			String from = Global.rulesFrom[i];
			String to = Global.rulesTo[i];
			String right = buildRightUri(uri, from, to, queryString);
			if (StringUtils.isNotEmpty(right)) {
				LOG.debug("URLRewrite: " + from + "=>" + to + " : " + right);
				return right;
			}
		}
		return null;
	}

	/**
	 * 检查正则,并生成正确路径
	 * 
	 * @param uri
	 * @param from
	 * @param to
	 * @param queryString
	 * @return null:无需重写 否则返回正确路径
	 */
	private String buildRightUri(String uri, String from, String to, String queryString) {
		if (StringUtils.isEmpty(uri)) return null;
		Pattern p = patterns.get(from);
		if (p == null) {
			p = Pattern.compile(from);
			patterns.put(from, p); // 缓存正则
		}
		Matcher m = p.matcher(uri);
		if (m.find()) {
			String right = to;
			int count = m.groupCount();
			for (int i = 0; i < count; i++) {
				right = right.replace("${" + i + "}", m.group(i + 1));
			}
			if (queryString != null && !"".equals(queryString.trim())) {
				if (right.indexOf("?") >= 0) right += "&";
				else right += "?";

				right += queryString;
			}
			return right;
		}
		return null;
	}

	public void init(FilterConfig fConfig) throws ServletException {
		// 全局配置
		Properties p = Global.init(null);
		// 初始化数据库连接
		DBManager.initDataSource(p);

		// 正则缓存
		patterns = new HashMap<String, Pattern>();
	}

	public void destroy() {
	}
}
