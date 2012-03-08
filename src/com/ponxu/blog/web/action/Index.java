package com.ponxu.blog.web.action;

import java.util.List;
import java.util.Map;

import com.ponxu.blog.service.PostService;
import com.ponxu.blog.service.Service;
import com.ponxu.blog.web.PageInfo;

public class Index extends BlogAction {
	public static final String HOME_FTL = "home.ftl";

	private static PostService postService = Service.get(PostService.class);

	public String execute() {
		PageInfo page = new PageInfo(request, blogService.getPageSize());

		List<Map<String, String>> list = postService.queryPageForBlog(null, null, page, Service.EMPTY_PARAMS);
		List<Map<String, String>> postTerms = postService.queryTaxonomy(list);

		put("list", list);
		put("post_terms", postTerms);
		put("page", page);

		return HOME_FTL;
	}
}
