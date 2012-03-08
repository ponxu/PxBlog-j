/**
 * 2012-2-27 MapRowWrapper.java
 */
package com.ponxu.dbutils.wrap.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.ponxu.dbutils.wrap.RowWrapper;

/**
 * @author xwz
 * 
 */
public class MapRowWrapper implements RowWrapper<Map<String, String>> {
	private static MapRowWrapper instance = new MapRowWrapper();

	public static MapRowWrapper getInstance() {
		return instance;
	}

	private MapRowWrapper() {
	}

	@Override
	public Map<String, String> wrap(ResultSet rs) throws SQLException {
		Map<String, String> row = new HashMap<String, String>();
		ResultSetMetaData metaData = rs.getMetaData();
		int colCount = metaData.getColumnCount();
		for (int i = 1; i <= colCount; i++) {
			String colName = metaData.getColumnName(i);
			row.put(colName.toLowerCase(), rs.getString(i));
		}
		return row;
	}
}
