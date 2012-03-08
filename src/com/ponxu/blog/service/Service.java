/**
 * 2012-2-27 Service.java
 */
package com.ponxu.blog.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ponxu.blog.Global;
import com.ponxu.dbutils.DBManager;
import com.ponxu.dbutils.wrap.RowWrapper;
import com.ponxu.dbutils.wrap.impl.LongRowWrapper;
import com.ponxu.dbutils.wrap.impl.MapRowWrapper;
import com.ponxu.utils.StringUtils;

/**
 * 所有Service基类, 提供常用工具,
 * 
 * 另提供了缓存所有子类的内力, 模拟实现所有子类单态
 * 
 * @author xwz
 * 
 */
public abstract class Service {
	private static final Log LOG = LogFactory.getLog(Service.class);
	public static final Object[] EMPTY_PARAMS = new Object[0];
	private static final Map<Class<?>, Service> SERVICES = new ConcurrentHashMap<Class<?>, Service>();
	protected static final RowWrapper<Map<String, String>> WRAPPER = MapRowWrapper.getInstance();

	@SuppressWarnings("unchecked")
	public static <S extends Service> S get(Class<S> cls) {
		try {
			Service instance = SERVICES.get(cls);
			if (instance == null) {
				instance = cls.newInstance();
				SERVICES.put(cls, instance);
			}
			return (S) instance;
		} catch (Exception e) {
			LOG.error("Unable to new instance: " + cls, e);
		}
		return null;
	}

	public final String tableName(String shortTableName) {
		return Global.tablePrefix + shortTableName;
	}

	/**
	 * 
	 * @param shortTableName
	 * @param values
	 * @return 新插入id
	 */
	public final long save(String shortTableName, Map<String, Object> values) {
		StringBuilder insertSql = new StringBuilder("insert into ").append(tableName(shortTableName)).append("(");
		// 占位"?"
		StringBuilder placeholderSql = new StringBuilder("(");
		Object[] params = new Object[values.size()];
		int i = 0;
		for (String name : values.keySet()) {
			if (i != 0) {
				insertSql.append(",");
				placeholderSql.append(",");
			}
			insertSql.append(name);
			placeholderSql.append("?");
			params[i++] = values.get(name);
		}
		insertSql.append(")");
		placeholderSql.append(")");
		insertSql.append(" values").append(placeholderSql);

		int ret = DBManager.executeUpdate(insertSql.toString(), params);
		return ret > 0 ? lastUpdateId() : 0;
	}

	/**
	 * @param shortTableName
	 * @param newValues
	 * @return 更新语句影响行数
	 */
	public final int update(String shortTableName, Map<String, Object> newValues, Map<String, Object> wheres) {
		StringBuilder sql = new StringBuilder("update ").append(tableName(shortTableName)).append(" set ");
		Object[] params = new Object[newValues.size() + wheres.size()];
		int i = 0;
		for (String name : newValues.keySet()) {
			if (i != 0) {
				sql.append(",");
			}
			sql.append(name).append("=?");
			params[i++] = newValues.get(name);
		}

		sql.append(" where ");
		int j = 0;
		for (String name : wheres.keySet()) {
			if (j != 0) {
				sql.append(" and ");
			}
			sql.append(name).append("=?");
			params[i++] = wheres.get(name);
			j++;
		}
		return DBManager.executeUpdate(sql.toString(), params);
	}

	/**
	 * 参数不空: "where子句 + 参数", 参数空: ""
	 * 
	 * @param where
	 * @return
	 */
	public final String prettyWhere(String where) {
		if (StringUtils.isEmpty(where)) return "";
		where = where.trim();
		if (where.startsWith("and")) {
			where = " 1=1 " + where;
		}
		return " where " + where;
	}

	public final String buildInClause(long[] ids) {
		if (ids == null || ids.length == 0) return "(-1)";
		StringBuilder in = new StringBuilder("(");
		for (int i = 0; i < ids.length; i++) {
			if (i != 0) in.append(",");
			in.append(ids[i]);
		}
		return in.append(")").toString();
	}

	public final String buildInClause(Object[] ids) {
		if (ids == null || ids.length == 0) return "('')";
		StringBuilder in = new StringBuilder("(");
		for (int i = 0; i < ids.length; i++) {
			if (i != 0) in.append(",");
			in.append("'").append(ids[i]).append("'");
		}
		return in.append(")").toString();
	}

	public final long lastUpdateId() {
		String sql = "select last_insert_id()";
		return DBManager.executeQuerySingleOne(LongRowWrapper.getInstance(), sql);
	}
}
