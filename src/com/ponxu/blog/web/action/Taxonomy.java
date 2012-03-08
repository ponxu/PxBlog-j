package com.ponxu.blog.web.action;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ponxu.blog.service.PostService;
import com.ponxu.blog.service.Service;
import com.ponxu.blog.service.TaxonomyService;
import com.ponxu.blog.web.PageInfo;
import com.ponxu.utils.CollectionUtils;

/**
 * 分类/标签
 * 
 * @author xwz
 * 
 */
public class Taxonomy extends BlogAction {
	private static final String SEARCH_FTL = "search.ftl";
	private static final String TAG_FTL = "tags.ftl";

	private static TaxonomyService taxonomyService = Service.get(TaxonomyService.class);
	private static PostService postService = Service.get(PostService.class);

	public String cat() {
		long id = getIntParameter("id");
		if (id == 0) return SEARCH_FTL;

		List<Long> postIds = taxonomyService.queryObjectIds(id);
		if (CollectionUtils.isNotEmpty(postIds)) {
			PageInfo page = new PageInfo(request, blogService.getPageSize());
			String where = " id in " + postService.buildInClause(postIds.toArray());
			List<Map<String, String>> postList = postService.queryPageForBlog(where, null, page, Service.EMPTY_PARAMS);
			List<Map<String, String>> postTerms = postService.queryTaxonomy(postList);

			put("list", postList);
			put("post_terms", postTerms);
			put("page", page);
		}

		return SEARCH_FTL;
	}

	public String tag() {
		long id = getIntParameter("id");
		if (id == 0) {
			List<Map<String, String>> tagList = taxonomyService.queryAll("taxonomy='post_tag'", Service.EMPTY_PARAMS);
			Random random = new Random();
			put("tags", tagList);
			put("random", random);
			return TAG_FTL;
		}

		List<Long> postIds = taxonomyService.queryObjectIds(id);
		if (CollectionUtils.isNotEmpty(postIds)) {
			PageInfo page = new PageInfo(request, blogService.getPageSize());
			String where = " id in " + postService.buildInClause(postIds.toArray());
			List<Map<String, String>> postList = postService.queryPageForBlog(where, null, page, Service.EMPTY_PARAMS);
			List<Map<String, String>> postTerms = postService.queryTaxonomy(postList);

			put("list", postList);
			put("post_terms", postTerms);
			put("page", page);
		}

		return SEARCH_FTL;
	}
}