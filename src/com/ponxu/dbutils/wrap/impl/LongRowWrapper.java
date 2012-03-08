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
public class LongRowWrapper implements RowWrapper<Long> {
	private static LongRowWrapper instance = new LongRowWrapper();

	public static LongRowWrapper getInstance() {
		return instance;
	}

	private LongRowWrapper() {
	}

	@Override
	public Long wrap(ResultSet rs) throws SQLException {
		return rs.getLong(1);
	}

}
