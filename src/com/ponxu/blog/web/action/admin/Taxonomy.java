package com.ponxu.blog.web.action.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ponxu.blog.service.Service;
import com.ponxu.blog.service.TaxonomyService;
import com.ponxu.blog.web.action.BlogAction;

/**
 * 分类/标签
 * 
 * @author xwz
 * 
 */
public class Taxonomy extends BlogAction {
	private static final String TAG_FTL = "tags.ftl";
	private static final String CAT_FTL = "cats.ftl";

	private static TaxonomyService taxonomyService = Service.get(TaxonomyService.class);

	public String tag() {
		List<Map<String, String>> list = taxonomyService.queryAll("taxonomy='post_tag'", Service.EMPTY_PARAMS);
		put("list", list);
		return TAG_FTL;
	}

	public String cat() {
		List<Map<String, String>> list = taxonomyService.queryAll("taxonomy='category'", Service.EMPTY_PARAMS);
		put("list", list);
		return CAT_FTL;
	}

	public String delcat() {
		long id = getIntParameter("id");
		int i = taxonomyService.delcat(id);
		writeJSON(i);
		return DONT_FTL;
	}

	public String deltag() {
		long id = getIntParameter("id");
		int i = taxonomyService.deltag(id);
		writeJSON(i);
		return DONT_FTL;
	}

	public String add() {
		String name = getStringParameter("name");
		String type = getStringParameter("type");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("description", name);
		map.put("taxonomy", type);
		taxonomyService.save(map);

		if ("post_tag".equalsIgnoreCase(type)) {
			redirect("/admin/Taxonomy_tag.do");
		} else {
			redirect("/admin/Taxonomy_cat.do");
		}

		return DONT_FTL;
	}
}