package com.ponxu.blog.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ponxu.blog.web.PageInfo;
import com.ponxu.dbutils.DBManager;
import com.ponxu.dbutils.wrap.impl.LongRowWrapper;
import com.ponxu.utils.CollectionUtils;
import com.ponxu.utils.StringUtils;

public class PostService extends Service {
	public static final String[] TAXONOMY = { "category", "post_tag" };
	public static final String TABLE_POST = "posts";

	private static TaxonomyService taxonomyService = Service.get(TaxonomyService.class);
	private static BlogService blogService = Service.get(BlogService.class);

	/**
	 * 保存文章
	 * 
	 * @param post
	 * @param cat
	 * @param tag
	 * @return
	 */
	public long save(Map<String, Object> post, long[] cat, long[] tag) {
		long postId = save(TABLE_POST, post);
		if (postId > 0) {
			BlogService.removeCache(BlogService.CACHE_KEY_LASTED_POST);
			BlogService.removeCache(BlogService.CACHE_KEY_RANDOM_POST);
			// 删除所有关系
			taxonomyService.removeAllRelationship(postId, TAXONOMY);
			// 重新建立关系
			for (long c : cat) {
				taxonomyService.saveRelationship(postId, c);
			}
			for (long t : tag) {
				taxonomyService.saveRelationship(postId, t);
			}
			return postId;
		} else {
			return 0;
		}
	}

	/**
	 * 更新文章
	 * 
	 * @param post
	 * @param cat
	 * @param tag
	 * @return
	 */
	public int update(Map<String, Object> post, long[] cat, long[] tag) {
		long postId = (Long) post.get("id");
		Map<String, Object> wheres = new HashMap<String, Object>();
		wheres.put("id", postId);
		post.remove("id");

		int i = update(TABLE_POST, post, wheres);
		if (i > 0) {
			BlogService.removeCache(BlogService.CACHE_KEY_LASTED_POST);
			BlogService.removeCache(BlogService.CACHE_KEY_RANDOM_POST);

			// 删除所有关系
			taxonomyService.removeAllRelationship(postId, TAXONOMY);
			// 重新建立关系
			for (long c : cat) {
				taxonomyService.saveRelationship(postId, c);
			}
			for (long t : tag) {
				taxonomyService.saveRelationship(postId, t);
			}
			return i;
		} else {
			return 0;
		}
	}

	/**
	 * 可博客访问的文章
	 * 
	 * @param where
	 * @param order
	 * @param page
	 * @param params
	 * @return
	 */
	public List<Map<String, String>> queryPageForBlog(String where, String order, PageInfo page, Object... params) {
		where = StringUtils.defaultString(where) + (StringUtils.isNotEmpty(where) ? " and " : "") + " post_status='publish' and post_type='post'";
		return queryPage(where, order, page, params);
	}

	/**
	 * 文章查询
	 * 
	 * @param where
	 * @param order
	 * @param page
	 * @param params
	 * @return
	 */
	public List<Map<String, String>> queryPage(String where, String order, PageInfo page, Object... params) {
		int offset = (page.getIndex() - 1) * page.getSize();
		int cutSize = blogService.gint("cut_size");

		String sql = "select id,post_author,post_date,left(post_content, " + cutSize
				+ ") post_content,post_title,post_status,comment_status,ping_status,post_type,comment_count from " + tableName(TABLE_POST)
				+ prettyWhere(where) + " order by " + StringUtils.defaultString(order, "post_date desc") + " limit " + offset + "," + page.getSize();
		String sqlCount = "select count(*) from " + tableName(TABLE_POST) + prettyWhere(where);

		List<Map<String, String>> list = DBManager.executeQuery(WRAPPER, sql, params);
		Long total = DBManager.executeQuerySingleOne(LongRowWrapper.getInstance(), sqlCount, params);
		page.setTotal(total == null ? 0 : total.intValue());

		return list;
	}

	/**
	 * 文章查询
	 * 
	 * @param where
	 * @param page
	 * @param params
	 * @return
	 */
	public List<Map<String, String>> queryPage(String where, PageInfo page, Object... params) {
		return queryPage(where, null, page, params);
	}

	/**
	 * 查询文章列表所需的分类信息
	 * 
	 * @param postList
	 * @return
	 */
	public List<Map<String, String>> queryTaxonomy(List<Map<String, String>> postList) {
		if (CollectionUtils.isEmpty(postList)) return null;

		long[] postids = new long[postList.size()];
		int i = 0;
		for (Map<String, String> post : postList) {
			postids[i++] = Long.parseLong(post.get("id"));
		}

		return taxonomyService.queryForObject(postids, TAXONOMY);
	}

	/**
	 * 查询文章
	 * 
	 * @param id
	 * @return
	 */
	public Map<String, String> load(long id) {
		String sql = "select * from " + tableName(TABLE_POST) + " where id=?";
		Map<String, String> post = DBManager.executeQuerySingleOne(WRAPPER, sql, new Object[] { id });
		return post;
	}

	/**
	 * 删除文章
	 * 
	 * @param id
	 * @return
	 */
	public int del(long id) {
		// 删除文章
		String sql = "delete from " + tableName(TABLE_POST) + " where id=?";
		int i = DBManager.executeUpdate(sql, new Object[] { id });

		if (i > 0) {
			BlogService.removeCache(BlogService.CACHE_KEY_LASTED_POST);
			BlogService.removeCache(BlogService.CACHE_KEY_RANDOM_POST);

			// 删除关系
			List<Map<String, String>> list = taxonomyService.queryForObject(new long[] { id }, TAXONOMY);
			for (Map<String, String> map : list) {
				taxonomyService.delRelationship(id, Long.parseLong(map.get("term_taxonomy_id")));
			}

			// 删除评论
			sql = "delete from " + tableName(CommentService.TABLE_COMMENT) + " where comment_post_id=?";
			DBManager.executeUpdate(sql, id);
		}
		return i;
	}

	public int commentAdded(long id) {
		String sql = "update " + tableName(TABLE_POST) + " set comment_count=comment_count+1 where id=?";
		return DBManager.executeUpdate(sql, new Object[] { id });
	}
}
