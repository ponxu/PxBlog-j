/**
 * 2012-2-28 UserService.java
 */
package com.ponxu.blog.service;

import java.util.List;
import java.util.Map;

import com.ponxu.blog.web.PageInfo;
import com.ponxu.dbutils.DBManager;
import com.ponxu.dbutils.wrap.impl.LongRowWrapper;

/**
 * @author xwz
 * 
 */
public class CommentService extends Service {
	public static final String TABLE_COMMENT = "comments";
	private static PostService postService = Service.get(PostService.class);

	public List<Map<String, String>> queryAll(String where, Object... params) {
		String sql = "select * from " + tableName(TABLE_COMMENT) + prettyWhere(where);
		List<Map<String, String>> list = DBManager.executeQuery(WRAPPER, sql, params);
		return list;
	}

	public List<Map<String, String>> queryPage(String where, PageInfo page, Object... params) {
		int offset = (page.getIndex() - 1) * page.getSize();
		String sql = "select * from " + tableName(TABLE_COMMENT) + prettyWhere(where) + " order by comment_date desc limit " + offset + ","
				+ page.getSize();
		String sqlCount = "select count(*) from " + tableName(TABLE_COMMENT) + prettyWhere(where);

		List<Map<String, String>> list = DBManager.executeQuery(WRAPPER, sql, params);
		Long total = DBManager.executeQuerySingleOne(LongRowWrapper.getInstance(), sqlCount, params);
		page.setTotal(total == null ? 0 : total.intValue());

		return list;
	}

	public long save(Map<String, Object> comment) {
		long i = save(TABLE_COMMENT, comment);
		if (i > 0) {
			BlogService.removeCache(BlogService.CACHE_KEY_LASTED_COMMENT);
			// 增加评论时, 文章表应相应增加
			long postid = (Long) comment.get("comment_post_id");
			postService.commentAdded(postid);
		}

		return i;
	}

	public int del(long id) {
		BlogService.removeCache(BlogService.CACHE_KEY_LASTED_COMMENT);

		String sql = "delete from " + tableName(TABLE_COMMENT) + " where comment_id=?";
		return DBManager.executeUpdate(sql, new Object[] { id });
	}
}
