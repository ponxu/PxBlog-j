/**
 * StringUtils.java
 * 2012-1-14
 */
package com.ponxu.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author xwz
 * 
 */
public class StringUtils {
	private static final String DEFAULT_STRING = "";

	public static boolean isEmpty(String str) {
		return str == null || str.trim().equals(DEFAULT_STRING);
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static String defaultString(String str) {
		return defaultString(str, DEFAULT_STRING);
	}

	public static String defaultString(String str, String defaultstr) {
		return isEmpty(str) ? defaultstr : str;
	}

	public static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; ++i) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotNumeric(String str) {
		return !isNumeric(str);
	}

	public static String firstUpperCase(String str) {
		if (isEmpty(str)) return str;
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	public static String md5(String str) {
		String ret = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			ret = new BigInteger(1, md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			ret = null;
		}

		return ret;
	}

	public static String htmlFilter(String str) {
		str = str.replaceAll("&", "&amp;");
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("\"", "&quot;");
		return str;
	}

}
