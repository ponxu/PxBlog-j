package com.ponxu.blog.web.action;

import javax.servlet.http.HttpServletResponse;

public class Error extends BlogAction {
	public static final String E404_FTL = "/404.ftl";
	public static final String E500_FTL = "/500.ftl";

	public String execute() {
		int code = getIntParameter("code");
		switch (code) {
		case HttpServletResponse.SC_NOT_FOUND:
			return e404();
		default:
			return e500();
		}
	}

	public String e404() {
		System.out.println(404);
		return E404_FTL;
	}

	public String e500() {
		System.out.println(500);
		return E500_FTL;
	}
}
