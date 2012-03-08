package com.ponxu.blog.web.action;

import java.io.Writer;

import com.ponxu.blog.service.CheckcodeService;
import com.ponxu.blog.service.Service;

public class Checkcode extends BlogAction {
	CheckcodeService checkcodeService = Service.get(CheckcodeService.class);

	@Override
	public String execute() throws Exception {
		Writer out = response.getWriter();
		String code = checkcodeService.writeCheckcode(out);
		sessionAdd("checkcode", code);
		return DONT_FTL;
	}
}
