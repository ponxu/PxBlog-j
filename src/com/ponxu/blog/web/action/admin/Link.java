/**
 * 2012-2-27 Post.java
 */
package com.ponxu.blog.web.action.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ponxu.blog.service.LinkService;
import com.ponxu.blog.service.Service;
import com.ponxu.blog.web.action.BlogAction;

/**
 * @author xwz
 * 
 */
public class Link extends BlogAction {
	public static final String LINK_FTL = "links.ftl";

	private static LinkService linkService = Service.get(LinkService.class);

	@Override
	public String execute() throws Exception {
		List<Map<String, String>> list = linkService.queryAll(null, Service.EMPTY_PARAMS);
		put("list", list);
		return LINK_FTL;
	}
	
	public String del() throws Exception {
		long id = getIntParameter("id");
		writeJSON(linkService.del(id));
		return DONT_FTL;
	}

	public String add() throws Exception {
		String url = getStringParameter("url");
		String name = getStringParameter("name");
		String target = getStringParameter("target");
		String description = getStringParameter("description");

		Map<String, Object> link = new HashMap<String, Object>();
		link.put("link_url", url);
		link.put("link_name", name);
		link.put("link_target", target);
		link.put("link_description", description);
		link.put("link_notes", description);
		
		linkService.save(link);

		redirect("/admin/Link.do");
		return DONT_FTL;
	}
}
