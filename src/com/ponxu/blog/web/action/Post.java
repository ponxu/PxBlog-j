package com.ponxu.blog.web.action;

import java.util.HashMap;
import java.util.Map;

import com.ponxu.blog.service.PostService;
import com.ponxu.blog.service.Service;
import com.ponxu.blog.service.TaxonomyService;

public class Post extends BlogAction {
	public static final String POST_FTL = "post.ftl";

	private static PostService postService = Service.get(PostService.class);
	private static TaxonomyService taxonomyService = Service.get(TaxonomyService.class);

	public String execute() {
		long id = getIntParameter("id");
		Map<String, String> post = null;
		if (id > 0) {
			post = postService.load(id);
			put("post_terms", taxonomyService.queryForObject(new long[] { id }, PostService.TAXONOMY));
		} else {
			post = new HashMap<String, String>();
		}
		put("post", post);
		put("terms", taxonomyService.queryAll(null, Service.EMPTY_PARAMS));

		return POST_FTL;
	}
}
