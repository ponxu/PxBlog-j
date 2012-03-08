/**
 * 2012-2-26 DBManager.java
 */
package com.ponxu.dbutils;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ponxu.dbutils.exception.DBException;
import com.ponxu.dbutils.wrap.RowWrapper;
import com.ponxu.utils.StringUtils;

/**
 * 管理数据库连接, 以及执行简单SQL
 * 
 * @author xwz
 * 
 */
public final class DBManager {
	private static final Log LOG = LogFactory.getLog(DBManager.class);
	private static final ThreadLocal<Integer> QUERY_COUNT = new ThreadLocal<Integer>();
	private static final ThreadLocal<Connection> threadConnection = new ThreadLocal<Connection>();
	private static final String DEFAULT_DATASOURCE_CLASS = "com.mchange.v2.c3p0.ComboPooledDataSource";
	private static final String DEFAULT_CONFIG_FILE = "/db.properties";

	private static boolean debug = false;
	private static DataSource dataSource;

	/**
	 * 初始化连接池
	 * 
	 * @param props
	 */
	public final static void initDataSource(Properties dbProperties) {
		try {
			if (dbProperties == null) {
				dbProperties = new Properties();
				dbProperties.load(DBManager.class.getResourceAsStream(DEFAULT_CONFIG_FILE));
			}

			String dataSourceClass = DEFAULT_DATASOURCE_CLASS;
			for (Object key : dbProperties.keySet()) {
				String keyStr = (String) key;
				// 查找jdbc.datasource
				if ("datasource".equalsIgnoreCase(keyStr)) {
					dataSourceClass = dbProperties.getProperty(keyStr);
				}
				if ("debug".equalsIgnoreCase(keyStr)) {
					debug = Boolean.parseBoolean(dbProperties.getProperty(keyStr, "false"));
				}
			}

			dataSource = (DataSource) Class.forName(dataSourceClass).newInstance();

			if (debug && dataSource.getClass().getName().indexOf("c3p0") > 0) {
				// Disable JMX in C3P0
				System.setProperty("com.mchange.v2.c3p0.management.ManagementCoordinator", "com.mchange.v2.c3p0.management.NullManagementCoordinator");
			}
			LOG.info("Using DataSource : " + dataSource.getClass().getName());
			LOG.info("Debuging : " + debug);

			// 设置属性
			Method[] setters = dataSource.getClass().getDeclaredMethods();
			for (Object key : dbProperties.keySet()) {
				String keyStr = (String) key;
				if (keyStr.startsWith("jdbc.")) {
					String val = dbProperties.getProperty(keyStr);
					String name = keyStr.substring(5);

					String setterName = "set" + StringUtils.firstUpperCase(name);
					for (Method setter : setters) {
						if (setterName.equals(setter.getName())) {
							Class<?> type = setter.getParameterTypes()[0];
							String typeName = type.getSimpleName();
							Object paramVal = null;
							if (typeName.equalsIgnoreCase("String")) {
								paramVal = val;
							} else if (typeName.equalsIgnoreCase("Integer") || typeName.equalsIgnoreCase("int")) {
								paramVal = Integer.parseInt(val);
							} else if (typeName.equalsIgnoreCase("boolean")) {
								paramVal = Boolean.parseBoolean(val);
							} else if (typeName.equalsIgnoreCase("long")) {
								paramVal = Long.parseLong(val);
							}

							if (paramVal != null) {
								setter.invoke(dataSource, paramVal);
								LOG.info(setter.getName() + ": " + typeName + " : " + paramVal);
								break;
							}
						}
					}
				}
			}

			// 测试连接
			Connection conn = getConnection();
			DatabaseMetaData mdm = conn.getMetaData();
			LOG.info("Connected to " + mdm.getDatabaseProductName() + " " + mdm.getDatabaseProductVersion());
			release();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	public static Connection getConnection() throws SQLException {
		Connection conn = threadConnection.get();
		if (conn == null || conn.isClosed()) {
			conn = dataSource.getConnection();
			conn.setAutoCommit(false);
			threadConnection.set(conn);
		}
		return conn;
	}

	private static PreparedStatement prepareStatement(String sql) {
		LOG.debug("sql: " + sql);
		try {
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			return pstmt;
		} catch (SQLException e) {
			LOG.error(String.format("PrepareStatement failed!!!\nsql: %s", sql));
			throw new DBException(e);
		}
	}

	private static void setPreparedStatement(PreparedStatement pstmt, Object... params) {
		if (params == null) return;

		try {
			for (int i = 0; i < params.length; i++) {
				pstmt.setObject(i + 1, params[i]);
			}
		} catch (SQLException e) {
			LOG.error(String.format("Set params failed!!!\nparams: %s", Arrays.toString(params)));
			throw new DBException(e);
		}
	}

	public static int executeUpdate(String sql, Object... params) {
		PreparedStatement pstmt = prepareStatement(sql);
		setPreparedStatement(pstmt, params);
		try {
			return pstmt.executeUpdate();
		} catch (SQLException e) {
			LOG.error(String.format("Update failed!!! \nsql: %s \nparams: %s", sql, Arrays.toString(params)));
			throw new DBException(e);
		}
	}

	private static void queryAdd() {
		Integer c = QUERY_COUNT.get();
		if (c == null) c = 0;
		c++;
		QUERY_COUNT.set(c);
	}

	public static int getQueryCount() {
		return QUERY_COUNT.get();
	}

	public static <T> List<T> executeQuery(RowWrapper<T> wrapper, String sql, Object... params) {
		if (wrapper == null) return null;

		PreparedStatement pstmt = prepareStatement(sql);
		setPreparedStatement(pstmt, params);
		try {
			List<T> list = new ArrayList<T>();
			ResultSet rs = pstmt.executeQuery();
			queryAdd();
			while (rs.next()) {
				list.add(wrapper.wrap(rs));
			}
			return list;
		} catch (SQLException e) {
			LOG.error(String.format("Query failed!!! \nsql: %s \nparams: %s", sql, Arrays.toString(params)));
			throw new DBException(e);
		}
	}

	public static <T> T executeQuerySingleOne(RowWrapper<T> wrapper, String sql, Object... params) {
		if (wrapper == null) return null;
		if (sql.indexOf("limit") == -1) {
			sql += " limit 0,1";
		}

		PreparedStatement pstmt = prepareStatement(sql);
		setPreparedStatement(pstmt, params);
		try {
			ResultSet rs = pstmt.executeQuery();
			queryAdd();
			if (rs.next()) {
				return wrapper.wrap(rs);
			}
			return null;
		} catch (SQLException e) {
			LOG.error(String.format("Query failed!!! \nsql: %s \nparams: %s", sql, Arrays.toString(params)));
			throw new DBException(e);
		}
	}

	public static void release() {
		LOG.debug("This thread query count: " + QUERY_COUNT.get());
		QUERY_COUNT.set(0);
		Connection conn = threadConnection.get();
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			LOG.error("Unabled to close connection!!!");
			throw new DBException(e);
		} finally {
			threadConnection.set(null);
		}
	}

	public static void commit() {
		Connection conn = threadConnection.get();
		try {
			if (conn != null && !conn.isClosed()) {
				conn.commit();
			}
		} catch (SQLException e) {
			LOG.error("Unabled to commit!!! ");
			throw new DBException(e);
		}
	}

	public static void rollback() {
		Connection conn = threadConnection.get();
		try {
			if (conn != null && !conn.isClosed()) {
				conn.rollback();
			}
		} catch (SQLException e) {
			LOG.error("Rollback failed!!! ");
			throw new DBException(e);
		}
	}
}
