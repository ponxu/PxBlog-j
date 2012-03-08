/**
 * 2012-2-28 Index.java
 */
package com.ponxu.blog.web.action.admin;

import com.ponxu.blog.web.action.BlogAction;

/**
 * @author xwz
 * 
 */
public class Index extends BlogAction {
	public static final String INDEX_FTL = "home.ftl";

	@Override
	public String execute() throws Exception {
		return INDEX_FTL;
	}
}
