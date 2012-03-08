package com.ponxu.blog.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebUtils {
	public static void forward(HttpServletRequest request, HttpServletResponse response, String url) {
		try {
			request.getRequestDispatcher(url).forward(request, response);
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void redirect(HttpServletRequest request, HttpServletResponse response, String url) {
		try {
			String appPath = request.getContextPath();
			if (!url.startsWith(appPath)) {
				url = appPath + url;
			}
			response.sendRedirect(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Object sessionGet(HttpServletRequest request, String name) {
		return request.getSession().getAttribute(name);
	}

}
