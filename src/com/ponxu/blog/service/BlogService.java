package com.ponxu.blog.service;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ponxu.blog.Global;
import com.ponxu.blog.web.PageInfo;
import com.ponxu.dbutils.DBManager;
import com.ponxu.dbutils.wrap.impl.StringRowWrapper;
import com.ponxu.utils.CollectionUtils;
import com.ponxu.utils.StringUtils;

/**
 * 提供访问 options表的能力<br>
 * 此类主要在页面访问较多<br>
 * 此类具有缓存功能
 * 
 * @author xwz
 * 
 */
@SuppressWarnings("unchecked")
public class BlogService extends Service {
	private static final String TABLE_OPTION = "options";

	private static TaxonomyService taxonomyService = Service.get(TaxonomyService.class);
	private static PostService postService = Service.get(PostService.class);
	private static LinkService linkService = Service.get(LinkService.class);
	private static CommentService commentService = Service.get(CommentService.class);

	/**
	 * <b>缓存时间</b><br>
	 * 调试模式缓存9秒,非调试缓存5分钟<br>
	 * 此时间只对全局缓存有效
	 */
	private static final long CACHE_TIME = Global.debug ? 9000 : 1000 * 60 * 5;
	/** 全局 */
	private static final Map<String, SoftReference<Object>> GLOBAL_CACHE = new ConcurrentHashMap<String, SoftReference<Object>>();
	/** 线程 */
	// private static final ThreadLocal<Map<String, SoftReference<Object>>>
	// THREAD_CACHE = new ThreadLocal<Map<String, SoftReference<Object>>>();

	private static final String CACHE_KEY_CATEGORY = "CACHE_KEY_CATEGORY";
	private static final String CACHE_KEY_LASTED_COMMENT = "CACHE_KEY_LASTED_COMMENT";
	private static final String CACHE_KEY_LASTED_POST = "CACHE_KEY_LASTED_POST";
	private static final String CACHE_KEY_RANDOM_POST = "CACHE_KEY_RANDOM_POST";
	private static final String CACHE_KEY_LINK = "CACHE_KEY_LINK";

	/**
	 * 放入缓存
	 * 
	 * @param key
	 * @param val
	 */
	private static void setGlobalCache(String key, Object val) {
		if (Global.debug) {
			// 调试模式不全局缓存
			return;
		}

		// 缓存到全局
		GLOBAL_CACHE.put(key, new SoftReference<Object>(val));
	}

	/**
	 * 读取缓存
	 * 
	 * @param key
	 * @return
	 */
	private static Object getCache(String key) {
		Object val = null;
		// 全局读取
		SoftReference<Object> soft = GLOBAL_CACHE.get(key);
		if (soft != null) {
			val = soft.get();
		}
		return val;
	}

	/**
	 * 分页大小
	 * 
	 * @return
	 */
	public int getPageSize() {
		return gint("post_page_size");
	}

	/**
	 * 读取 options表
	 * 
	 * @param name
	 * @return
	 */
	public String g(String name) {
		String val = (String) getCache(name);
		if (val == null) {
			String sql = "select option_value from " + tableName(TABLE_OPTION) + " where option_name=?";
			val = DBManager.executeQuerySingleOne(StringRowWrapper.getInstance(), sql, new Object[] { name });
			if (StringUtils.isNotEmpty(val)) {
				setGlobalCache(name, val);
			}
		}
		return StringUtils.defaultString(val);
	}

	/**
	 * 读取 options表 返回 数字,默认 15
	 * 
	 * @param name
	 * @return
	 */
	public int gint(String name) {
		String l = StringUtils.defaultString(g(name), "15");
		if (StringUtils.isNotNumeric(l)) return 15;
		return Integer.parseInt(l);
	}

	/**
	 * 获取 主题所在全路径
	 * 
	 * @return eg:http://www.ponxu.com/themes/主题
	 */
	public String getTheme() {
		String blogurl = g("blogurl");
		String theme = g("theme");
		return blogurl + "/themes/" + theme;
	}

	/**
	 * 生成文章路径 文章通配 {title},{id}
	 * 
	 * @param post
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String url(Map<String, String> post) throws UnsupportedEncodingException {
		String blogurl = g("blogurl");
		String posturl = g("posturl");

		String title = URLEncoder.encode(post.get("post_title"), Global.encoding);
		String id = post.get("id");
		return blogurl + posturl.replace("{title}", title).replace("{id}", id);
	}

	/**
	 * 分类目录最后查询时间
	 */
	private long categorysLastQuery = 0;

	/**
	 * 查询所有分类目录
	 * 
	 * @return
	 */
	public List<Map<String, String>> getCategorys() {
		// 缓存获取
		List<Map<String, String>> list = (List<Map<String, String>>) getCache(CACHE_KEY_CATEGORY);
		if (list == null || (System.currentTimeMillis() - categorysLastQuery) > CACHE_TIME) {

			// 数据库查询
			list = taxonomyService.queryAll("taxonomy='category'", EMPTY_PARAMS);
			list = CollectionUtils.isEmpty(list) ? CollectionUtils.EMPTY_LIST : list;

			// 缓存
			setGlobalCache(CACHE_KEY_CATEGORY, list);

			categorysLastQuery = System.currentTimeMillis();
		}
		return list;
	}

	/**
	 * 最新评论最后查询时间
	 */
	private long latestCommentsLastQuery = 0;

	/**
	 * 最新评论
	 * 
	 * @return
	 */
	public List<Map<String, String>> getLatestComments() {
		// 缓存获取
		List<Map<String, String>> list = (List<Map<String, String>>) getCache(CACHE_KEY_LASTED_COMMENT);
		if (list == null || (System.currentTimeMillis() - latestCommentsLastQuery) > CACHE_TIME) {
			// 数据库查询
			PageInfo page = new PageInfo(1, gint("latest_comment_size"));
			list = commentService.queryPage("comment_type='post'", page, EMPTY_PARAMS);
			list = CollectionUtils.isEmpty(list) ? CollectionUtils.EMPTY_LIST : list;

			// 缓存
			setGlobalCache(CACHE_KEY_LASTED_COMMENT, list);

			latestCommentsLastQuery = System.currentTimeMillis();
		}
		return list;
	}

	/**
	 * 查询文章,方便页面调用
	 * 
	 * @param where
	 * @param order
	 * @param pageIndex
	 * @param pageSize
	 * @return 结果不会为null, 便于页面使用
	 */
	public List<Map<String, String>> posts(String where, String order, int pageIndex, int pageSize) {
		// String cacheKey = StringUtils.defaultString(where) +
		// StringUtils.defaultString(order) + pageIndex + pageSize;
		PageInfo page = new PageInfo(pageIndex, pageSize);

		List<Map<String, String>> list = postService.queryPageForBlog(where, order, page, EMPTY_PARAMS);
		list = CollectionUtils.isEmpty(list) ? CollectionUtils.EMPTY_LIST : list;

		return list;
	}

	/**
	 * 最新文章最后查询时间
	 */
	private long latestPostsLastQuery = 0;

	/**
	 * 最新文章
	 * 
	 * @return
	 */
	public List<Map<String, String>> getLatestPosts() {
		// 读取缓存
		List<Map<String, String>> list = (List<Map<String, String>>) getCache(CACHE_KEY_LASTED_POST);
		if (list == null || (System.currentTimeMillis() - latestPostsLastQuery) > CACHE_TIME) {
			// 数据库查询
			list = posts(null, null, 1, gint("latest_post_size"));
			// 缓存
			setGlobalCache(CACHE_KEY_LASTED_POST, list);

			latestPostsLastQuery = System.currentTimeMillis();
		}
		return list;
	}

	/**
	 * 随机文章最后查询时间
	 */
	private long randomPostsLastQuery = 0;

	/**
	 * 随机文章
	 * 
	 * @return
	 */
	public List<Map<String, String>> getRandomPosts() {
		// 读取缓存
		List<Map<String, String>> list = (List<Map<String, String>>) getCache(CACHE_KEY_RANDOM_POST);
		if (list == null || (System.currentTimeMillis() - randomPostsLastQuery) > CACHE_TIME) {
			// 数据库查询
			list = posts(null, "rand()", 1, gint("random_comment_size"));
			// 缓存
			setGlobalCache(CACHE_KEY_RANDOM_POST, list);

			latestPostsLastQuery = System.currentTimeMillis();
		}
		return list;
	}

	public int getQueryCount() {
		return DBManager.getQueryCount();
	}

	/**
	 * 连接最后查询时间
	 */
	private long linksQuery = 0;

	/**
	 * 连接
	 * 
	 * @return
	 */
	public List<Map<String, String>> getLinks() {
		// 读缓存
		List<Map<String, String>> list = (List<Map<String, String>>) getCache(CACHE_KEY_LINK);

		if (list == null || (System.currentTimeMillis() - linksQuery) > CACHE_TIME) {
			// 查询数据库
			String where = "link_visible='Y'";
			list = linkService.queryAll(where, EMPTY_PARAMS);
			list = CollectionUtils.isEmpty(list) ? CollectionUtils.EMPTY_LIST : list;

			// 缓存
			setGlobalCache(CACHE_KEY_LINK, list);

			linksQuery = System.currentTimeMillis();
		}

		return list;
	}
}
