package com.ponxu.mvc.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

/**
 * Action基类, 子类必须提供一个无参构造方法
 * 
 * @author xwz
 * 
 */
public abstract class Action {
	/** 不进行模板处理 */
	protected static final String DONT_FTL = null;
	protected static ServletContext application;
	protected static String appPath;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;

	public static ServletContext getServletContext() {
		return application;
	}

	public static void setServletContext(ServletContext application) {
		Action.application = application;
		Action.appPath = Action.application.getContextPath();
	}

	public final void set(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.session = request.getSession();
	}

	public void forward(String url) {
		try {
			request.getRequestDispatcher(url).forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void redirect(String url) {
		try {
			if (!url.startsWith(appPath)) {
				url = appPath + url;
			}
			response.sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String cookieGet(String name) {
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase(name)) return cookie.getValue();
		}
		return null;
	}

	public void cookieSet(String name, String value, String path, int time) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath(path);
		cookie.setMaxAge(time);
		response.addCookie(cookie);
	}

	public void cookieSet(String name, String value) {
		cookieSet(name, value, "/", 31536000); // 365天
	}

	public void sessionAdd(String name, Object value) {
		request.getSession().setAttribute(name, value);
	}

	public void sessionRemove(String name) {
		request.getSession().removeAttribute(name);
	}

	public Object sessionGet(String name) {
		return request.getSession().getAttribute(name);
	}

	public Object sessionGetAndRemove(String name) {
		Object obj = sessionGet(name);
		sessionRemove(name);
		return obj;
	}

	public String getStringParameter(String name) {
		return request.getParameter(name);
	}

	public int getIntParameter(String name) {
		String p = getStringParameter(name);
		try {
			if (p != null) {
				return Integer.parseInt(p);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void writeJSON(Object val) {
		try {
			PrintWriter writer = response.getWriter();
			writer.write(JSONObject.wrap(val).toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 默认执行器
	 * 
	 * @return
	 */
	public String execute() throws Exception {
		return DONT_FTL;
	}

	/**
	 * 刚初始化Action时 执行
	 */
	public void init() {
	}

	/**
	 * 执行action 之前执行
	 * 
	 * @param methodName
	 */
	public void executeBefore(String methodName) throws Exception {
	}

	/**
	 * 执行Action 之后执行
	 * 
	 * @param methodName
	 */
	public void executeAfter(String methodName, String result) throws Exception {
	}
}
