/**
 * 2012-2-27 MapRowWrapper.java
 */
package com.ponxu.dbutils.wrap.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ponxu.dbutils.wrap.RowWrapper;

/**
 * @author xwz
 * 
 */
public class StringRowWrapper implements RowWrapper<String> {
	private static StringRowWrapper instance = new StringRowWrapper();

	public static StringRowWrapper getInstance() {
		return instance;
	}

	private StringRowWrapper() {
	}

	@Override
	public String wrap(ResultSet rs) throws SQLException {
		return rs.getString(1);
	}

}
