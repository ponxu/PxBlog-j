package com.ponxu.blog.service;

import java.util.List;
import java.util.Map;

import com.ponxu.dbutils.DBManager;
import com.ponxu.dbutils.wrap.impl.LongRowWrapper;

public class TaxonomyService extends Service {
	private static final String TABLE_TAXONOMY = "term_taxonomy";
	private static final String TABLE_TAXONOMY_RELATIONSHIPS = "term_relationships";

	/**
	 * 保存
	 * 
	 * @param taxonomy
	 * @return
	 */
	public long save(Map<String, Object> taxonomy) {
		return save(TABLE_TAXONOMY, taxonomy);
	}

	/**
	 * 根据分类id, 查询所有Object的id
	 * 
	 * @param termTaxonomyId
	 * @return
	 */
	public List<Long> queryObjectIds(long termTaxonomyId) {
		String sql = "select object_id from " + tableName(TABLE_TAXONOMY_RELATIONSHIPS) + " where term_taxonomy_id=?";
		List<Long> list = DBManager.executeQuery(LongRowWrapper.getInstance(), sql, new Object[] { termTaxonomyId });
		return list;
	}

	/**
	 * 根据 Object_id 和 分类的类型 查询分类信息
	 * 
	 * @param objectIds
	 * @param taxonomys
	 * @return
	 */
	public List<Map<String, String>> queryForObject(long[] objectIds, String[] taxonomys) {
		String sql = "select a.*,c.object_id from " + tableName(TABLE_TAXONOMY) + " a, " + tableName(TABLE_TAXONOMY_RELATIONSHIPS) + " c "
				+ "where a.term_taxonomy_id=c.term_taxonomy_id and a.taxonomy in " + buildInClause(taxonomys);
		if (objectIds != null && objectIds.length > 0) {
			sql += " and c.object_id in " + buildInClause(objectIds);
		}
		sql += " order by a.count desc";

		List<Map<String, String>> list = DBManager.executeQuery(WRAPPER, sql, EMPTY_PARAMS);
		return list;
	}

	/**
	 * 查询分类信息
	 * 
	 * @param where
	 * @param params
	 * @return
	 */
	public List<Map<String, String>> queryAll(String where, Object... params) {
		String sql = "select * from " + tableName(TABLE_TAXONOMY) + prettyWhere(where);
		List<Map<String, String>> list = DBManager.executeQuery(WRAPPER, sql, params);
		return list;
	}

	/**
	 * 根据Object id 删除其所有关系
	 * 
	 * @param objectId
	 *            对象id
	 * @param taxonomys
	 *            分类的类型
	 */
	public void removeAllRelationship(long objectId, String[] taxonomys) {
		// 从关系中找出分类id
		String sqlQueryTerm = "select a.term_taxonomy_id term_taxonomy_id from " + tableName(TABLE_TAXONOMY_RELATIONSHIPS) + " a, "
				+ tableName(TABLE_TAXONOMY) + " b where a.term_taxonomy_id=b.term_taxonomy_id and a.object_id=? and b.taxonomy in "
				+ buildInClause(taxonomys);
		List<Long> termTaxonomyIds = DBManager.executeQuery(LongRowWrapper.getInstance(), sqlQueryTerm, new Object[] { objectId });
		for (long termTaxonomyId : termTaxonomyIds) {
			delRelationship(objectId, termTaxonomyId);
		}
	}

	/**
	 * 删除关系, 并把此类的数量减一
	 * 
	 * @param objectId
	 * @param termTaxonomyId
	 * @return
	 */
	public int delRelationship(long objectId, long termTaxonomyId) {
		String sqlDelRelationship = "delete from " + tableName(TABLE_TAXONOMY_RELATIONSHIPS) + " where object_id=? and term_taxonomy_id=?";
		int i = DBManager.executeUpdate(sqlDelRelationship, new Object[] { objectId, termTaxonomyId });
		// 真实删除
		if (i > 0) {
			String sql = "update " + tableName(TABLE_TAXONOMY) + " set count=count-1 where term_taxonomy_id=?";
			DBManager.executeUpdate(sql, new Object[] { termTaxonomyId });
		}

		return i;
	}

	/**
	 * 添加关系, 并把此类的数量加一
	 * 
	 * @param objectId
	 * @param termTaxonomyId
	 * @return
	 */
	public int saveRelationship(long objectId, long termTaxonomyId) {
		String insertSql = "insert into " + tableName(TABLE_TAXONOMY_RELATIONSHIPS) + " (object_id,term_taxonomy_id) values(?,?)";
		int i = DBManager.executeUpdate(insertSql, new Object[] { objectId, termTaxonomyId });
		// 真实插入
		if (i > 0) {
			String sql = "update " + tableName(TABLE_TAXONOMY) + " set count=count+1 where term_taxonomy_id=?";
			DBManager.executeUpdate(sql, new Object[] { termTaxonomyId });
		}
		return i;
	}

	/**
	 * 删除标签
	 * 
	 * @param id
	 * @return
	 */
	public int deltag(long id) {
		String sql = "delete from " + tableName(TABLE_TAXONOMY_RELATIONSHIPS) + " where term_taxonomy_id=?";
		DBManager.executeUpdate(sql, id);

		sql = "delete from " + tableName(TABLE_TAXONOMY) + " where term_taxonomy_id=?";
		return DBManager.executeUpdate(sql, id);
	}

	/**
	 * 删除分类 及下属文章
	 * 
	 * @param id
	 * @return
	 */
	public int delcat(long id) {
		// String sql = "delete from " + tableName(PostService.TABLE_POST) +
		// " where id in (select object_id from "
		// + tableName(TABLE_TAXONOMY_RELATIONSHIPS) +
		// " where term_taxonomy_id=?" + ")";
		// DBManager.executeUpdate(sql, id);

		String sql = "select object_id from " + tableName(TABLE_TAXONOMY_RELATIONSHIPS) + " where term_taxonomy_id=?";
		List<Long> list = DBManager.executeQuery(LongRowWrapper.getInstance(), sql, id);

		PostService postService = Service.get(PostService.class);
		for (long postid : list) {
			postService.del(postid);
		}

		sql = "delete from " + tableName(TABLE_TAXONOMY) + " where term_taxonomy_id=?";
		return DBManager.executeUpdate(sql, id);
	}
}
