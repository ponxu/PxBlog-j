/**
 * 2012-2-27 IRowWrapper.java
 */
package com.ponxu.dbutils.wrap;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 包装 ResultSet
 * 
 * @author xwz
 * 
 */
public interface RowWrapper<T> {
	public T wrap(ResultSet rs) throws SQLException;
}
