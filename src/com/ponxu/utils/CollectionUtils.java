package com.ponxu.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CollectionUtils {
	public static final List<Map<String, String>> EMPTY_LIST = new ArrayList<Map<String, String>>();

	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isNotEmpty(Object[] array) {
		return !isEmpty(array);
	}

	public static boolean isEmpty(Collection<?> c) {
		return c == null || c.size() == 0;
	}

	public static boolean isNotEmpty(Collection<?> c) {
		return !isEmpty(c);
	}
}
