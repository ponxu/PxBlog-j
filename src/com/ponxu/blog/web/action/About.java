package com.ponxu.blog.web.action;

import java.util.List;
import java.util.Map;

import com.ponxu.blog.service.PostService;
import com.ponxu.blog.service.Service;
import com.ponxu.blog.web.PageInfo;
import com.ponxu.utils.CollectionUtils;

public class About extends BlogAction {
	public static final String ABOUT_FTL = "about.ftl";

	private static PostService postService = Service.get(PostService.class);

	public String execute() {
		String where = "post_type='about'";
		PageInfo page = new PageInfo(1, 1);
		List<Map<String, String>> list = postService.queryPage(where, null, page, Service.EMPTY_PARAMS);
		if (CollectionUtils.isNotEmpty(list)) {
			String idstr = list.get(0).get("id");
			Map<String, String> post = postService.load(Long.parseLong(idstr));
			put("post", post);
		}
		return ABOUT_FTL;
	}
}
