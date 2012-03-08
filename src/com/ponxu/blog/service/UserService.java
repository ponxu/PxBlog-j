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
public class UserService extends Service {
	private static final String TABLE_COMMENT = "users";

	public List<Map<String, String>> queryAll(String where, Object... params) {
		String sql = "select * from " + tableName(TABLE_COMMENT) + prettyWhere(where);
		List<Map<String, String>> list = DBManager.executeQuery(WRAPPER, sql, params);
		return list;
	}

	/**
	 * 
	 * @param where
	 * @param params
	 * @return
	 */
	public Map<String, String> getUni(String where, Object... params) {
		String sql = "select * from " + tableName(TABLE_COMMENT) + prettyWhere(where);
		Map<String, String> u = DBManager.executeQuerySingleOne(WRAPPER, sql, params);
		return u;
	}
}
