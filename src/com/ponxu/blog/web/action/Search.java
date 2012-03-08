package com.ponxu.blog.web.action;

import java.util.List;
import java.util.Map;

import com.ponxu.blog.service.BlogService;
import com.ponxu.blog.service.PostService;
import com.ponxu.blog.service.Service;
import com.ponxu.blog.web.PageInfo;

public class Search extends BlogAction {
	private static final String SEARCH_FTL = "search.ftl";

	private static PostService postService = Service.get(PostService.class);
	private static BlogService blogService = Service.get(BlogService.class);

	@Override
	public String execute() throws Exception {
		String keywords = getStringParameter("keywords");

		PageInfo page = new PageInfo(request, blogService.getPageSize());
		String where = "post_title like ?";
		String fuzzy = "%" + keywords + "%";
		Object[] params = { fuzzy };

		List<Map<String, String>> list = postService.queryPageForBlog(where, null, page, params);
		List<Map<String, String>> postTerms = postService.queryTaxonomy(list);

		put("list", list);
		put("post_terms", postTerms);
		put("page", page);

		return SEARCH_FTL;
	}
}
