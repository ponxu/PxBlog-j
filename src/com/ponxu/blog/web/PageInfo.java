/**
 * 2012-3-1 PageInfo.java
 */
package com.ponxu.blog.web;

import javax.servlet.http.HttpServletRequest;

import com.ponxu.utils.StringUtils;

/**
 * @author xwz
 * 
 */
public class PageInfo {
	private int index;
	private int size;
	private int count;
	private int total;

	public PageInfo(HttpServletRequest request, int size) {
		this.index = Integer.parseInt(StringUtils.defaultString(request.getParameter("pageIndex"), "1"));
		this.size = size;
	}

	public PageInfo(int index, int size) {
		this.index = index;
		this.size = size;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
		if (total == 0 || size == 0) setCount(0);
		setCount(total % size == 0 ? total / size : total / size + 1);
	}

}
