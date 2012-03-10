/**
 * 2012-2-28 UserService.java
 */
package com.ponxu.blog.service;

import java.util.List;
import java.util.Map;

import com.ponxu.dbutils.DBManager;

/**
 * @author xwz
 * 
 */
public class LinkService extends Service {
	private static final String TABLE_LINK = "links";

	public List<Map<String, String>> queryAll(String where, Object... params) {
		String sql = "select * from " + tableName(TABLE_LINK) + prettyWhere(where);
		List<Map<String, String>> list = DBManager.executeQuery(WRAPPER, sql, params);
		return list;
	}

	public long save(Map<String, Object> link) {
		BlogService.removeCache(BlogService.CACHE_KEY_LINK);
		return save(TABLE_LINK, link);
	}

	public int del(long id) {
		BlogService.removeCache(BlogService.CACHE_KEY_LINK);
		String sql = "delete from " + tableName(TABLE_LINK) + " where link_id=?";
		return DBManager.executeUpdate(sql, id);
	}
}
