package com.ponxu.blog.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ponxu.blog.service.CommentService;
import com.ponxu.blog.service.Service;
import com.ponxu.blog.web.PageInfo;
import com.ponxu.utils.StringUtils;
import com.ponxu.utils.TimeUtils;

public class Comment extends BlogAction {
	public static final String COMMENT_LIST_FTL = "comment.ftl";
	private static CommentService commentService = Service.get(CommentService.class);

	@SuppressWarnings("unchecked")
	public String add() {
		Map<String, Object> ret = new HashMap<String, Object>();
		String checkcode = getStringParameter("checkcode");
		String rightCheckcode = (String) sessionGetAndRemove("checkcode");
		if (rightCheckcode == null || !rightCheckcode.equalsIgnoreCase(checkcode)) {
			// ret.put("message", "验证码不正确!");
			// writeJSON(ret);
			// return DONT_FTL;
		}

		long postid = getIntParameter("postid");
		long pid = getIntParameter("pid");
		String author = getStringParameter("author");
		String email = getStringParameter("email");
		String url = getStringParameter("url");
		String content = getStringParameter("ccontent");
		String type = getStringParameter("type");

		String approved = StringUtils.defaultString(blogService.g("comment_approved"), "0");
		String ip = request.getRemoteAddr();
		String agent = request.getHeader("User-Agent");

		Map<String, Object> comment = new HashMap<String, Object>();
		comment.put("comment_post_id", postid);
		comment.put("comment_author", author);
		comment.put("comment_author_email", email);
		comment.put("comment_author_url", url);
		comment.put("comment_author_ip", ip);
		comment.put("comment_date", TimeUtils.now());
		comment.put("comment_content", content);
		comment.put("comment_approved", approved);
		comment.put("comment_agent", agent);
		comment.put("comment_parent", pid);
		comment.put("comment_type", type);

		Map<String, String> user = (Map<String, String>) sessionGet("user");
		if (user != null) {
			comment.put("user_id", user.get("id"));
		}

		long cid = commentService.save(comment);
		cookieSet("pxb_comment_author", author);
		cookieSet("pxb_comment_author_email", email);
		cookieSet("pxb_comment_author_url", url);
		ret.put("message", "成功!");
		ret.put("status", true);
		ret.put("comment_id", cid);
		writeJSON(ret);

		return DONT_FTL;
	}

	public String list() {
		// 文章id
		long id = getIntParameter("postid");

		int pageIndex = getIntParameter("pageIndex");
		PageInfo page = new PageInfo(pageIndex, blogService.getPageSize());

		List<Map<String, String>> list = null;
		String where = "comment_approved='1' and comment_post_id=?";
		Object[] params = { id };
		if (pageIndex == 0) {
			// 不分页
			list = commentService.queryAll(where, params);
		} else {
			list = commentService.queryPage(where, page, params);
		}

		put("comments", list);
		put("commentPage", page);

		return COMMENT_LIST_FTL;
	}
}
