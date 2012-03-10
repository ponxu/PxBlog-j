package com.ponxu.blog.web.action;

import java.util.List;
import java.util.Map;

import com.ponxu.blog.service.PostService;
import com.ponxu.blog.service.Service;
import com.ponxu.blog.web.PageInfo;
import com.ponxu.utils.CollectionUtils;

/**
 * 留言板
 * 
 * @author xwz
 * 
 */
public class Board extends BlogAction {
	public static final String BOARD_FTL = "board.ftl";
	private static PostService postService = Service.get(PostService.class);

	public String execute() {
		String where = "post_type='board'";
		PageInfo page = new PageInfo(1, 1);
		List<Map<String, String>> list = postService.queryPage(where, null, page, Service.EMPTY_PARAMS);
		if (CollectionUtils.isNotEmpty(list)) {
			String idstr = list.get(0).get("id");
			Map<String, String> post = postService.load(Long.parseLong(idstr));
			put("post", post);
			
			String author = cookieGet("pxb_comment_author");
			String email = cookieGet("pxb_comment_author_email");
			String url = cookieGet("pxb_comment_author_url");

			put("pxb_comment_author", author);
			put("pxb_comment_author_email", email);
			put("pxb_comment_author_url", url);
		}
		return BOARD_FTL;
	}
}
