package com.ponxu.blog.web.action;

import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ponxu.blog.Global;
import com.ponxu.blog.service.BlogService;
import com.ponxu.blog.service.Service;
import com.ponxu.mvc.action.Action;
import com.ponxu.utils.StringUtils;
import com.ponxu.utils.TimeUtils;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

/**
 * Blog Action 基类,
 * 
 * Action方法返回值为字符串类型 假设名为: result, result代表模板路径;<br>
 * 
 * 
 * 1. 若Action类在admin包中 模板路径为: admin/ + result, "admin/" 系统自动补全 <br>
 * 2. 普通Action类, 模板路径为: themes/模板目录 + result, "themes/模板目录/" 系统自动补全<br>
 * 3. 若result 以 "/" 开头, 不进行 1和2中的自动补全路径, 但会自动去掉开头的"/"
 * 
 * @author xwz
 * 
 */
public class BlogAction extends Action {
	private static final Log LOG = LogFactory.getLog(BlogAction.class);
	private static Configuration cfg;
	// 工具类
	private static Class<?>[] defaultStaticClasses = { StringUtils.class, TimeUtils.class };

	protected static BlogService blogService = Service.get(BlogService.class);

	private Map<Object, Object> root = new HashMap<Object, Object>();

	static {
		initFreeMarker();
	}

	public static void initFreeMarker() {
		cfg = new Configuration();
		cfg.setServletContextForTemplateLoading(getServletContext(), "");
		cfg.setTemplateUpdateDelay(Global.debug ? 0 : 60 * 5);
		cfg.setTemplateExceptionHandler(Global.debug ? TemplateExceptionHandler.HTML_DEBUG_HANDLER : TemplateExceptionHandler.IGNORE_HANDLER);
		cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
		cfg.setDefaultEncoding(Global.encoding);
		cfg.setOutputEncoding(Global.encoding);
		cfg.setLocale(Locale.CHINA);

		try {
			cfg.setSharedVariable("blog", blogService);
			cfg.setSharedVariable("appPath", application.getContextPath());

			// 工具类
			for (Class<?> cls : defaultStaticClasses) {
				BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
				TemplateHashModel statics = (TemplateHashModel) wrapper.getStaticModels().get(cls.getName());
				cfg.setSharedVariable(cls.getSimpleName(), statics);
				LOG.info("Static: " + cls.getSimpleName());
			}

		} catch (TemplateModelException e) {
			LOG.equals(e);
		}
	}

	protected final void put(Object name, Object val) {
		root.put(name, val);
	}

	protected final void msg(String val) {
		root.put("message", val);
	}

	protected final String error(int code) {
		forward("/Error_e" + code + ".do");
		return DONT_FTL;
	}

	private String buildRightFtlPath(String path) {
		if (StringUtils.isEmpty(path)) return path;

		if (path.startsWith("/")) {
			return path.substring(1);
		}

		String theme = blogService.g("theme");
		return getClass().getName().indexOf(".admin.") > -1 ? "admin/" + path : "themes/" + theme + "/" + path;
	}

	@Override
	public void executeAfter(String methodName, String result) throws Exception {
		if (StringUtils.isNotEmpty(result)) {
			Template t = cfg.getTemplate(buildRightFtlPath(result));
			response.setContentType("text/html; charset=" + cfg.getOutputEncoding());

			if (Global.debug) {
				response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
				response.setHeader("Pragma", "no-cache");
				response.setHeader("Expires", "Thu, 01 Dec 1994 00:00:00 GMT");
			}

			Writer out = response.getWriter();
			t.process(root, out);
		}
	}
}
