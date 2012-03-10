/**
 * 2012-2-27 Post.java
 */
package com.ponxu.blog.web.action.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ponxu.blog.service.PostService;
import com.ponxu.blog.service.Service;
import com.ponxu.blog.web.PageInfo;
import com.ponxu.blog.web.action.BlogAction;
import com.ponxu.utils.CollectionUtils;
import com.ponxu.utils.StringUtils;
import com.ponxu.utils.TimeUtils;

/**
 * @author xwz
 * 
 */
public class Page extends BlogAction {
	public static final String EDIT_FTL = "page.ftl";
	public static final String LIST_FTL = "page_list.ftl";

	private static PostService postService = Service.get(PostService.class);
	private static long[] empty = {};

	public String del() {
		long id = getIntParameter("id");
		writeJSON(postService.del(id));
		return DONT_FTL;
	}

	public String edit() {
		long id = getIntParameter("id");

		Map<String, String> post = null;
		if (id > 0) {
			post = postService.load(id);
		} else {
			String type = getStringParameter("type");
			if ("about".equalsIgnoreCase(type) || "board".equalsIgnoreCase(type)) {
				String where = "post_type=?";
				PageInfo page = new PageInfo(1, 1);
				List<Map<String, String>> list = postService.queryPage(where, page, type);
				if (CollectionUtils.isNotEmpty(list)) {
					post = list.get(0);
				}
			}

			if (post == null) {
				post = new HashMap<String, String>();
				post.put("post_type", StringUtils.defaultString(type, "page"));
			}
		}
		put("post", post);
		return EDIT_FTL;
	}

	@SuppressWarnings("unchecked")
	public String save() {
		long id = getIntParameter("id");
		String title = getStringParameter("title");
		String content = getStringParameter("content");
		String postStatus = getStringParameter("post_status");
		String commentStatus = getStringParameter("comment_status");
		String type = getStringParameter("post_type");

		Map<String, String> user = (Map<String, String>) sessionGet("user");
		long author = Long.parseLong(user.get("id"));

		Map<String, Object> post = new HashMap<String, Object>();
		post.put("post_author", author);
		post.put("post_content", content);
		post.put("post_title", title);
		post.put("post_status", postStatus);
		post.put("post_type", type);
		post.put("comment_status", commentStatus);

		// 保存文章
		if (id == 0) {
			post.put("post_date", TimeUtils.now());
			id = postService.save(post, empty, empty);
		} else {
			post.put("id", id);
			post.put("post_modified", TimeUtils.now());
			postService.update(post, empty, empty);
		}

		redirect("/admin/Page_edit.do?id=" + id);
		return DONT_FTL;
	}

	public String all() {
		String where = "post_type<>'post'";
		Object[] params = {};

		PageInfo page = new PageInfo(request, blogService.getPageSize());

		List<Map<String, String>> list = postService.queryPage(where, page, params);

		put("list", list);
		put("page", page);

		return LIST_FTL;
	}
}
