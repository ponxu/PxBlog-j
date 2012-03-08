/**
 * 2012-2-27 Init.java
 */
package com.ponxu.blog.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ponxu.dbutils.DBManager;
import com.ponxu.utils.StringUtils;

/**
 * 首次初始化Blog
 * 
 * @author xwz
 * 
 */
public class InstallBlog {
	private static Log LOG = LogFactory.getLog(InstallBlog.class);

	public static void createTables(String fileName) {
		String prefix = "pxb_";
		String charsetCollate = "DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci";
		try {
			if (StringUtils.isEmpty(fileName)) fileName = "/db.sql";
			InputStream in = InstallBlog.class.getResourceAsStream(fileName);
			StringBuilder sql = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.replace("$wpdb->", prefix).replace("$charset_collate", charsetCollate);
				sql.append(line).append("\n");
				if (line.endsWith(";")) {
					LOG.debug(sql + "\n-----------------------------------");
					DBManager.executeUpdate(sql.toString());
					sql = new StringBuilder();
				}
			}

			if (br != null) br.close();
			if (br != null) in.close();
		} catch (IOException e) {
			LOG.error(fileName, e);
		}
	}
}
