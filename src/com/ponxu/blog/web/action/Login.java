package com.ponxu.blog.web.action;

import java.util.Map;

import javax.servlet.http.HttpSession;

import com.ponxu.blog.service.Service;
import com.ponxu.blog.service.UserService;
import com.ponxu.utils.StringUtils;

public class Login extends BlogAction {
	public static final String LOGIN_FTL = "/admin/admin_login.ftl";

	private static UserService userService = Service.get(UserService.class);

	/**
	 * 把登陆用户信息放入session
	 * 
	 * @param session
	 * @param u
	 */
	public static void setLoginUser(HttpSession session, Map<String, String> u) {
		if (u == null) return;

		session.setAttribute("admin", true);
		session.setAttribute("user", u);
	}

	public String execute() {
		String username = cookieGet("pxb_username");
		String password = cookieGet("pxb_password");

		if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
			String where = "user_login=? and user_pass=?";
			Map<String, String> u = userService.getUni(where, new Object[] { username, password });
			if (u != null) {
				setLoginUser(session, u);
				redirect("/admin/Index.do");
				return DONT_FTL;
			}
		}

		return LOGIN_FTL;
	}

	public String in() {
		String username = getStringParameter("username");
		String password = getStringParameter("password");

		if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
			msg("信息不完整!");
			return LOGIN_FTL;
		}

		String where = "user_login=? and user_pass=?";
		Map<String, String> u = userService.getUni(where, new Object[] { username, StringUtils.md5(password) });

		if (u != null) {
			setLoginUser(session, u);

			cookieSet("pxb_username", username);
			cookieSet("pxb_password", StringUtils.md5(password));

			redirect("/admin/Index.do");
			return DONT_FTL;
		}

		msg("未找到用户!");
		return LOGIN_FTL;
	}

	public String out() {
		sessionRemove("admin");
		sessionRemove("user");
		return LOGIN_FTL;
	}
}
