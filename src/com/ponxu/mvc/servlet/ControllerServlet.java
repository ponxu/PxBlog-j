package com.ponxu.mvc.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ponxu.mvc.action.Action;
import com.ponxu.mvc.exception.MVCException;
import com.ponxu.utils.StringUtils;

public class ControllerServlet extends HttpServlet {
	private static Log LOG = LogFactory.getLog(ControllerServlet.class);
	private static final long serialVersionUID = 3879257176504745391L;
	private static final String ACTION_DO = ".do";
	private static final String METHOD_SEPARATOR = "_";
	private static final String DEFAULT_METHOD = "execute";

	private String errorPage;
	/** Action的根包 */
	private String actionRootPackage;
	/** 是否缓存Method; 若缓存(methods), 下次方便查找 */
	private boolean isCacheMethod = true;
	/** 所有Method, 方便查找 */
	private Map<String, Method> methods;

	/** 根据类名 初始化Action */
	private Action getAction(String fullClassName, HttpServletRequest request, HttpServletResponse response) {
		try {
			Action a = (Action) Class.forName(fullClassName).newInstance();
			a.set(request, response);
			a.init(); // 初始化
			return a;
		} catch (Exception e) {
			throw new MVCException(e);
		}
	}

	/** 执行Action */
	private void executeAction(Action action, String methodName, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isEmpty(methodName)) methodName = DEFAULT_METHOD;
		String methodFullName = action.getClass().getCanonicalName() + METHOD_SEPARATOR + methodName;

		try {
			Method method = null;
			// 先找方法的缓存
			if (isCacheMethod) {
				method = methods.get(methodFullName);
				LOG.debug("Look up in methodCache!" + methodFullName);
			}
			if (method == null) {
				method = action.getClass().getMethod(methodName);
				// 缓存方法
				if (isCacheMethod) {
					methods.put(methodFullName, method);
					LOG.debug("Put into methodCache!" + methodFullName);
				}
			}

			action.executeBefore(methodName); // 执行之前
			String result = (String) method.invoke(action); // 执行
			LOG.debug("result: " + result);
			action.executeAfter(methodName, result); // 执行之后
		} catch (Exception e) {
			throw new MVCException(e);
		}
	}

	public void init(ServletConfig config) throws ServletException {
		errorPage = config.getInitParameter("errorPage");
		LOG.debug("errorPage: " + errorPage);

		actionRootPackage = config.getInitParameter("actionRootPackage");
		LOG.debug("actionRootPackage: " + actionRootPackage);

		isCacheMethod = Boolean.parseBoolean(StringUtils.defaultString(config.getInitParameter("isCacheMethod"), "true"));
		if (isCacheMethod) methods = new ConcurrentHashMap<String, Method>();
		LOG.debug("isCacheMethod: " + isCacheMethod);

		Action.setServletContext(config.getServletContext());
	}

	private void error(int code, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(code);
		if (StringUtils.isNotEmpty(errorPage)) {
			String path = errorPage + "?code=" + code;
			request.getRequestDispatcher(path).forward(request, response);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getServletPath();
		String className = uri.substring(0, uri.length() - ACTION_DO.length()).replace("/", ".");
		String methodName = DEFAULT_METHOD;
		int _index = -1;
		if ((_index = className.indexOf(METHOD_SEPARATOR)) > -1) {
			String oldClassName = className;
			className = oldClassName.substring(0, _index);
			methodName = oldClassName.substring(_index + 1);
		}
		String fullClassName = actionRootPackage + className;
		LOG.debug("Action: " + fullClassName + "   Method: " + methodName);

		try {
			Action action = getAction(fullClassName, request, response);
			if (action != null) {
				executeAction(action, methodName, request, response);
			}
		} catch (MVCException e) {
			error(HttpServletResponse.SC_NOT_FOUND, request, response);
			LOG.error("Error: ", e);
			throw e;
		}
	}
}
