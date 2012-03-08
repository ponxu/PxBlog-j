/**
 * 2012-2-27 MapRowWrapper.java
 */
package com.ponxu.dbutils.wrap.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ponxu.dbutils.annotation.Col;
import com.ponxu.dbutils.wrap.RowWrapper;
import com.ponxu.utils.StringUtils;

/**
 * 根据注解信息,通过反射装载bean
 * 
 * @author xwz
 * 
 */
public class ReflectRowWrapper<T> implements RowWrapper<T> {
	private static Log LOG = LogFactory.getLog(ReflectRowWrapper.class);
	private Class<T> pojoClass;
	private Map<String, Method> setters;
	private Map<String, Class<?>> types;

	public ReflectRowWrapper(Class<T> pojoClass) {
		this.pojoClass = pojoClass;
		LOG.debug("pojoClass: " + pojoClass.getName());

		setters = new HashMap<String, Method>();
		types = new HashMap<String, Class<?>>();

		try {
			Field[] fs = pojoClass.getDeclaredFields();
			for (Field f : fs) {
				Col col = f.getAnnotation(Col.class);
				if (col != null) {
					String fname = f.getName();
					String colName = col.value();
					String setterName = "set" + StringUtils.firstUpperCase(fname);
					types.put(colName.toLowerCase(), f.getType());
					setters.put(colName.toLowerCase(), this.pojoClass.getDeclaredMethod(setterName, f.getType()));
				}
			}
		} catch (Exception e) {
			LOG.error(pojoClass.getName(), e);
		}
	}

	public T wrap(ResultSet rs) throws SQLException {
		try {
			T bean = pojoClass.newInstance();
			ResultSetMetaData metaData = rs.getMetaData();
			int colCount = metaData.getColumnCount();
			for (int i = 1; i <= colCount; i++) {
				String colName = metaData.getColumnName(i);

				Object val = null;
				Class<?> type = types.get(colName.toLowerCase());
				String typeName = type.getSimpleName();
				if (typeName.equalsIgnoreCase("long")) {
					val = rs.getLong(colName);
				} else if (typeName.equalsIgnoreCase("Date")) {
					val = rs.getDate(colName);
				} else {
					val = rs.getString(colName);
				}

				Method setter = setters.get(colName.toLowerCase());
				if (setter != null) setter.invoke(bean, val);
			}
			return bean;
		} catch (Exception e) {
			LOG.error(pojoClass.getName(), e);
		}
		return null;
	}
}
