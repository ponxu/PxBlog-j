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

			String author = cookieGet("pxb_comment_author");
			String email = cookieGet("pxb_comment_author_email");
			String url = cookieGet("pxb_comment_author_url");

			put("pxb_comment_author", author);
			put("pxb_comment_author_email", email);
			put("pxb_comment_author_url", url);
		} else {
			post = new HashMap<String, String>();
		}
		put("post", post);

		return POST_FTL;
	}
}
