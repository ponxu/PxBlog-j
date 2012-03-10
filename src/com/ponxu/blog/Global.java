package com.ponxu.blog;

import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ponxu.utils.StringUtils;

/**
 * 系统配置,修改了此处,往往需要重启tomcat
 * 
 * @author xwz
 *
 */
public final class Global {
	private static final Log LOG = LogFactory.getLog(Global.class);
	private static final String SEPARATOR = ",";
	private static final String FROM_TO_SEPARATOR = "=>";
	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	// 系统级设置
	public static boolean debug;
	public static String encoding;
	public static String tablePrefix;
	public static String authPathPrefix;
	public static int sessionMax;

	// URLRewriteFilter 设置
	public static String[] ignorePrefix;
	public static String[] ignoreSuffix;
	public static String[] rulesFrom;
	public static String[] rulesTo;

	public static final Properties init(Properties p) {
		try {
			if (p == null) {
				p = new Properties();
				p.load(Global.class.getResourceAsStream("/global.properties"));
			}

			// 系统级设置
			debug = Boolean.parseBoolean(p.getProperty("debug", "false"));
			encoding = p.getProperty("encoding", "UTF-8");
			tablePrefix = p.getProperty("tablePrefix", "pxb_");
			authPathPrefix = p.getProperty("authPathPrefix", "/admin/");
			sessionMax = Integer.parseInt(p.getProperty("sessionMax", "3600"));

			// URLRewriteFilter 设置
			// 前缀
			String ignorePrefixStr = p.getProperty("ignorePrefix");
			if (StringUtils.isNotEmpty(ignorePrefixStr)) {
				ignorePrefix = ignorePrefixStr.replaceAll("\\s", "").split(SEPARATOR);
			} else {
				ignorePrefix = EMPTY_STRING_ARRAY;
			}
			// 后缀
			String ignoreSuffixStr = p.getProperty("ignoreSuffix");
			if (StringUtils.isNotEmpty(ignoreSuffixStr)) {
				ignoreSuffix = ignoreSuffixStr.replaceAll("\\s", "").split(SEPARATOR);
			} else {
				ignoreSuffix = EMPTY_STRING_ARRAY;
			}
			// URLRewrite规则
			String rules = p.getProperty("rules");
			if (StringUtils.isNotEmpty(rules)) {
				rules = rules.replaceAll("\\s", "");
				rulesFrom = rules.split(SEPARATOR);
				rulesTo = rules.split(SEPARATOR);
				for (int i = 0; i < rulesFrom.length; i++) {
					String fromTo = rulesFrom[i];
					String[] fromTos = fromTo.split(FROM_TO_SEPARATOR);
					rulesFrom[i] = fromTos[0];
					rulesTo[i] = fromTos[1];
					LOG.info("rewrite: " + rulesFrom[i] + " => " + rulesTo[i]);
				}
			} else {
				rulesFrom = EMPTY_STRING_ARRAY;
				rulesTo = EMPTY_STRING_ARRAY;
			}
			LOG.info("prefix: " + Arrays.toString(ignorePrefix));
			LOG.info("suffix: " + Arrays.toString(ignoreSuffix));
		} catch (Exception e) {
			LOG.error("Init Global failed!! ", e);
		}

		return p;
	}
}
