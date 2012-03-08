/**
 * 2012-2-27 Post.java
 */
package com.ponxu.blog.web.action.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ponxu.blog.service.PostService;
import com.ponxu.blog.service.Service;
import com.ponxu.blog.service.TaxonomyService;
import com.ponxu.blog.web.PageInfo;
import com.ponxu.blog.web.action.BlogAction;
import com.ponxu.utils.StringUtils;
import com.ponxu.utils.TimeUtils;

/**
 * @author xwz
 * 
 */
public class Post extends BlogAction {
	public static final String EDIT_FTL = "post.ftl";

	private static PostService postService = Service.get(PostService.class);
	private static TaxonomyService taxonomyService = Service.get(TaxonomyService.class);

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
			put("post_terms", taxonomyService.queryForObject(new long[] { id }, PostService.TAXONOMY));
		} else {
			post = new HashMap<String, String>();
		}
		put("post", post);
		put("terms", taxonomyService.queryAll(null, Service.EMPTY_PARAMS));
		return EDIT_FTL;
	}

	@SuppressWarnings("unchecked")
	public String save() {
		long id = getIntParameter("id");
		String title = getStringParameter("title");
		long cat = getIntParameter("category");
		String newcat = getStringParameter("newcategory");
		String content = getStringParameter("content");
		String[] tag = request.getParameterValues("tag");
		String postStatus = getStringParameter("post_status");
		String commentStatus = getStringParameter("comment_status");

		// 新建分类
		if (cat == -1) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", newcat);
			map.put("description", newcat);
			map.put("taxonomy", "category");
			cat = taxonomyService.save(map);
			if (cat == 0) { // 分类保存失败
				msg("新建分类失败!");
				put("post", request.getParameterMap());
				return EDIT_FTL;
			}
		}

		// 标签
		long[] tagNum = new long[tag.length];
		for (int i = 0; i < tag.length; i++) {
			long tagId = 0;
			if (StringUtils.isNotNumeric(tag[i])) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", tag[i]);
				map.put("description", tag[i]);
				map.put("taxonomy", "post_tag");
				tagId = taxonomyService.save(map);
			} else {
				tagId = Long.parseLong(tag[i]);
			}
			tagNum[i] = tagId;
		}

		Map<String, String> user = (Map<String, String>) sessionGet("user");
		long author = Long.parseLong(user.get("id"));

		Map<String, Object> post = new HashMap<String, Object>();
		post.put("post_author", author);
		post.put("post_content", content);
		post.put("post_title", title);
		post.put("post_status", postStatus);
		post.put("comment_status", commentStatus);

		// 保存文章
		if (id == 0) {
			post.put("post_date", TimeUtils.now());
			id = postService.save(post, new long[] { cat }, tagNum);
		} else {
			post.put("id", id);
			post.put("post_modified", TimeUtils.now());
			postService.update(post, new long[] { cat }, tagNum);
		}

		redirect("/admin/Post_edit.do?id=" + id);
		return DONT_FTL;
	}

	public String all() {
		String where = "";
		Object[] params = {};

		PageInfo page = new PageInfo(request, blogService.getPageSize());

		List<Map<String, String>> list = postService.queryPage(where, page, params);
		List<Map<String, String>> postTerms = postService.queryTaxonomy(list);

		put("list", list);
		put("post_terms", postTerms);
		put("page", page);

		return "post_list.ftl";
	}
}
