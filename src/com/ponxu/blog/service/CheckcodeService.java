/**
 * 2012-3-5 CheckcodeService.java
 */
package com.ponxu.blog.service;

import java.io.Writer;

import com.ponxu.utils.StringUtils;

/**
 * 验证码相关
 * 
 * @author xwz
 * 
 */
public class CheckcodeService extends Service {
	private static BlogService blogService = Service.get(BlogService.class);

	public String writeCheckcode(Writer writer) {
		return "1234";
	}

	/**
	 * 
	 * @param code
	 * @param rightCode
	 * @return true:验证通过 false:未通过
	 */
	public boolean check(String code, String rightCode) {
		String use = StringUtils.defaultString(blogService.g("use_checkcode"), "0");
		if (use.equalsIgnoreCase("0")) return true;

		return true;
	}
}
