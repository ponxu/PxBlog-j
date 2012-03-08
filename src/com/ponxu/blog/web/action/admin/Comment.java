/**
 * 2012-2-28 Index.java
 */
package com.ponxu.blog.web.action.admin;

import java.util.List;
import java.util.Map;

import com.ponxu.blog.service.CommentService;
import com.ponxu.blog.service.Service;
import com.ponxu.blog.web.PageInfo;
import com.ponxu.blog.web.action.BlogAction;

/**
 * @author xwz
 * 
 */
public class Comment extends BlogAction {
	private static final String LIST_FTL = "comment_list.ftl";
	private static CommentService commentService = Service.get(CommentService.class);

	public String del() throws Exception {
		long id = getIntParameter("id");
		writeJSON(commentService.del(id));
		return DONT_FTL;
	}

	public String list() {
		long postid = getIntParameter("postid");
		PageInfo page = new PageInfo(request, blogService.getPageSize());

		String where = null;
		Object[] params = Service.EMPTY_PARAMS;
		if (postid != 0) {
			where = "comment_post_id=?";
			params = new Object[] { postid };
		}

		List<Map<String, String>> list = commentService.queryPage(where, page, params);
		put("list", list);
		put("page", page);
		put("postid", postid);

		return LIST_FTL;
	}
}
