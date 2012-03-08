/**
 * 2012-2-29 AnnotationParser.java
 */
package com.ponxu.dbutils.annotation.parser;

import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ponxu.dbutils.annotation.PK;
import com.ponxu.dbutils.annotation.Table;

/**
 * 解析注解
 * 
 * @author xwz
 * 
 */
public class AnnotationParser {
	private static Log LOG = LogFactory.getLog(AnnotationParser.class);
	private static String tablePrefix = "";
	private Class<?> entityClass;
	private String pk;
	private String tableName;

	public AnnotationParser(Class<?> entityClass) {
		this.entityClass = entityClass;
		parse();
	}

	public static void setTablePrefix(String tablePrefix) {
		AnnotationParser.tablePrefix = tablePrefix;
	}

	private void parse() {
		LOG.debug("entityClass: " + entityClass.getName());
		// 表名
		Table tn = entityClass.getAnnotation(Table.class);
		String tnvalue = "";
		if (tn != null) {
			tnvalue = tn.value();
		}
		if ("".equals(tnvalue)) {
			tableName = entityClass.getSimpleName();
		} else {
			tableName = tnvalue;
		}
		LOG.debug("tableName: " + tablePrefix + tableName);

		// 主键
		Field[] fs = entityClass.getDeclaredFields();
		String pvalue = "";
		for (Field f : fs) {
			PK p = f.getAnnotation(PK.class);
			if (p != null) {
				pvalue = p.value();
				if ("".equals(pvalue)) pvalue = f.getName();
				break;
			}
		}
		if ("".equals(pvalue)) pk = "id";
		else pk = pvalue;
		LOG.debug("pk: " + pk);
	}

	public String getPk() {
		return pk;
	}

	public String getTableName() {
		return tablePrefix + tableName;
	}

}
