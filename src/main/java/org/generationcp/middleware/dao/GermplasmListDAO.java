/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.GermplasmFolderMetadata;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * DAO class for {@link GermplasmList}.
 *
 */
public class GermplasmListDAO extends GenericDAO<GermplasmList, Integer> {

	private static final String PROGRAM_UUID = "programUUID";

	private static final String STATUS = "status";

	public static final Integer STATUS_DELETED = 9;

	public static final String GET_GERMPLASM_USED_IN_MORE_THAN_ONE_LIST = " SELECT \n"
		+ "   ld.gid, \n"
		+ "   group_concat(l.listname) \n"
		+ " FROM listnms l \n"
		+ "   INNER JOIN listdata ld ON l.listid = ld.listid \n"
		+ "   INNER JOIN germplsm g ON ld.gid = g.gid"
		+ " WHERE ld.gid IN (:gids) \n"
		+ "       AND l.liststatus != " + STATUS_DELETED + " \n"
		+ " GROUP BY ld.gid \n"
		+ " HAVING count(1) > 1";

	protected static final Criterion RESTRICTED_LIST;
	
	static {
		RESTRICTED_LIST = Restrictions.not(Restrictions.in("type",
			new String[] {GermplasmListType.NURSERY.toString(), GermplasmListType.TRIAL.toString(), GermplasmListType.CHECK.toString(),
				GermplasmListType.ADVANCED.toString(), GermplasmListType.CROSSES.toString(), GermplasmListType.CRT_CROSS.toString(),
				GermplasmListType.IMP_CROSS.toString()}));
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getAllExceptDeleted(final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));

			this.hideSnapshotListTypes(criteria);

			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getAllExceptDeleted() query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public long countAllExceptDeleted() {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));

			this.hideSnapshotListTypes(criteria);

			criteria.setProjection(Projections.rowCount());
			final Long count = (Long) criteria.uniqueResult();

			if (count == null) {
				return 0;
			}
			return count.longValue();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with countAllExceptDeleted() query from GermplasmList: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getByGID(final Integer gid, final int start, final int numOfRows) {
		try {
			if (gid != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
				criteria.createAlias("list", "l");
				criteria.setProjection(Projections.distinct(Projections.property("list")));
				criteria.add(Restrictions.eq("gid", gid));
				criteria.add(Restrictions.ne("l.status", GermplasmListDAO.STATUS_DELETED));

				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				criteria.addOrder(Order.asc("l.id"));
				criteria.addOrder(Order.asc("entryId"));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByGid(gid=" + gid + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getByGIDandProgramUUID(final Integer gid, final int start, final int numOfRows, final String programUUID) {
		try {
			if (gid != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class, "germplasmList");
				DetachedCriteria gidCriteria = DetachedCriteria.forClass(GermplasmListData.class,"listData");
				gidCriteria.add(Restrictions.eq("listData.gid", gid));
				gidCriteria.add(Property.forName("germplasmList.id").eqProperty("listData.list.id"));
				criteria.add(Subqueries.exists(gidCriteria.setProjection(Projections.property("listData.gid"))));
				criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));
				this.addCriteriaForProgramUUIDInLists(programUUID, criteria);
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				criteria.addOrder(Order.asc("name"));
				this.hideSnapshotListTypes(criteria);
				return criteria.list();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByGid(gid=" + gid + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	private void addCriteriaForProgramUUIDInLists(final String programUUID, final Criteria criteria) {
		final Criterion sameProgramUUID = Restrictions.eq(PROGRAM_UUID, programUUID);
		final Criterion nullProgramUUID = Restrictions.isNull(PROGRAM_UUID);
		criteria.add(Restrictions.or(sameProgramUUID, nullProgramUUID));
	}

	public long countByGID(final Integer gid) {
		try {
			if (gid != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
				criteria.createAlias("list", "l");
				criteria.add(Restrictions.eq("gid", gid));
				criteria.add(Restrictions.ne("l.status", GermplasmListDAO.STATUS_DELETED));
				criteria.setProjection(Projections.countDistinct("l.id"));
				return ((Long) criteria.uniqueResult()).longValue(); // count
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with countByGID(gid=" + gid + ") query from GermplasmList " + e.getMessage(), e);
		}
		return 0;
	}

	public long countByGIDandProgramUUID(final Integer gid, final String programUUID) {
		try {
			if (gid != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class, "germplasmList");
				DetachedCriteria gidCriteria = DetachedCriteria.forClass(GermplasmListData.class,"listData");
				gidCriteria.add(Restrictions.eq("listData.gid", gid));
				gidCriteria.add(Property.forName("germplasmList.id").eqProperty("listData.list.id"));
				criteria.add(Subqueries.exists(gidCriteria.setProjection(Projections.property("listData.gid"))));
				criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));
				this.addCriteriaForProgramUUIDInLists(programUUID, criteria);
				this.hideSnapshotListTypes(criteria);
				criteria.setProjection(Projections.countDistinct("id"));
				return ((Long) criteria.uniqueResult()).longValue(); // count
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with countByGIDandProgramUUID(gid=" + gid + ") query from GermplasmList " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getByName(final String name, final String programUUID, final Operation operation, final int start,
			final int numOfRows) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);

			this.hideSnapshotListTypes(criteria);

			if (operation == null || operation == Operation.EQUAL) {
				criteria.add(Restrictions.eq("name", name));
			} else if (operation == Operation.LIKE) {
				criteria.add(Restrictions.like("name", name));
			}

			criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));

			this.addCriteriaForProgramUUIDInLists(programUUID, criteria);

			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByName(name=" + name + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public long countByName(final String name, final Operation operation) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));
			criteria.setProjection(Projections.rowCount());

			this.hideSnapshotListTypes(criteria);

			if (operation == null || operation == Operation.EQUAL) {
				criteria.add(Restrictions.eq("name", name));
			} else if (operation == Operation.LIKE) {
				criteria.add(Restrictions.like("name", name));
			}
			return ((Long) criteria.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with countByName(name=" + name + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getByStatus(final Integer status, final int start, final int numOfRows) {
		try {
			if (status != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
				criteria.add(Restrictions.eq(STATUS, status));
				criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));

				this.hideSnapshotListTypes(criteria);

				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByStatus(status=" + status + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public long countByStatus(final Integer status) {
		try {
			if (status != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
				criteria.add(Restrictions.eq(STATUS, status));
				criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));

				this.hideSnapshotListTypes(criteria);

				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with countByStatus(status=" + status + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getAllTopLevelLists(final String programUUID)
			throws MiddlewareQueryException {
		try {
			final Criterion topFolder = Restrictions.eq("parent.id", 0);
			final Criterion nullFolder = Restrictions.isNull("parent");
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.or(topFolder, nullFolder));
			criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));

			this.addCriteriaForProgramUUIDInLists(programUUID, criteria);
			this.hideSnapshotListTypes(criteria);

			criteria.addOrder(Order.asc("name"));
			return criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getAllTopLevelLists() query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	/**
	 * Gets the germplasm list children.
	 *
	 * @param parentId the parent id
	 * @param programUUID the program UUID
	 * @param start the start
	 * @param numOfRows the num of rows
	 * @return the germplasm list children
	 * @throws MiddlewareQueryException the MiddlewareQueryException
	 */
	@SuppressWarnings("unchecked")
	public List<GermplasmList> getByParentFolderId(final Integer parentId, final String programUUID)
			throws MiddlewareQueryException {
		try {
			if (parentId != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
				criteria.add(Restrictions.eq("parent.id", parentId));
				criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));

				this.addCriteriaForProgramUUIDInLists(programUUID, criteria);

				this.hideSnapshotListTypes(criteria);

				criteria.addOrder(Order.asc("name"));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error with getByParentFolderId(parentId=" + parentId + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	@Nullable
	public GermplasmList getLastCreatedByUserID(final Integer userID, final String programUUID) {
		try {
			if (userID != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
				criteria.add(Restrictions.eq("userId", userID));
				criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));

				this.addCriteriaForProgramUUIDInLists(programUUID, criteria);

				this.hideSnapshotListTypes(criteria);

				criteria.addOrder(Order.desc("id"));

				final List result = criteria.list();
				if (!result.isEmpty()) {
					return (GermplasmList) result.get(0);
				} else {
					return null;
				}
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByUserID(userID=" + userID + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return null;
	}

	protected void hideSnapshotListTypes(final Criteria criteria) {
		criteria.add(this.getRestrictedSnapshopTypes());

	}

	protected Criterion getRestrictedSnapshopTypes() {
		return GermplasmListDAO.RESTRICTED_LIST;
	}

	/**
	 * Get Germplasm List Types
	 *
	 * Return a List of UserDefinedField POJOs representing records from the udflds table of IBDB which are the types of germplasm lists.
	 *
	 * @return List of germplasm list types
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("rawtypes")
	public List getGermplasmListTypes() {
		try {
			final Session session = this.getSession();
			final SQLQuery query = session.createSQLQuery(GermplasmList.GET_GERMPLASM_LIST_TYPES);
			return query.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getGermplasmListTypes() query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList();
	}

	/**
	 * Get Germplasm Name Types
	 *
	 * Return a List of UserDefinedField POJOs representing records from the udflds table of IBDB which are the types of germplasm names.
	 *
	 * @return List of germplasm name types
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("rawtypes")
	public List getGermplasmNameTypes() {
		try {
			final Session session = this.getSession();
			final SQLQuery query = session.createSQLQuery(GermplasmList.GET_GERMPLASM_NAME_TYPES);
			return query.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getGermplasmListTypes() query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList();
	}

	/**
	 * Get Germplasm Lists with names like Q or germplasms with name like Q or gid equal to Q
	 *
	 * @param q
	 * @param o - like or equal
	 * @return List of GermplasmLists
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("unchecked")
	public List<GermplasmList> searchForGermplasmLists(final String searchedString, final String programUUID, final Operation o)
			throws MiddlewareQueryException {
		final String q = searchedString.trim();
		if ("".equals(q)) {
			return new ArrayList<>();
		}
		try {
			final SQLQuery query;

			if (o.equals(Operation.EQUAL)) {
				query = this.getSession().createSQLQuery(
						this.getSearchForGermplasmListsQueryString(GermplasmList.SEARCH_FOR_GERMPLASM_LIST_EQUAL, programUUID));
				query.setParameter("gidLength", q.length());
				query.setParameter("q", q);
				query.setParameter("qNoSpaces", q.replace(" ", ""));
				query.setParameter("qStandardized", GermplasmDataManagerUtil.standardizeName(q));
			} else {
				if (q.contains("%") || q.contains("_")) {
					query = this.getSession().createSQLQuery(
							this.getSearchForGermplasmListsQueryString(GermplasmList.SEARCH_FOR_GERMPLASM_LIST_GID_LIKE, programUUID));
					query.setParameter("q", q);
					query.setParameter("qNoSpaces", q.replace(" ", ""));
					query.setParameter("qStandardized", GermplasmDataManagerUtil.standardizeName(q));
				} else {
					query = this.getSession().createSQLQuery(
							this.getSearchForGermplasmListsQueryString(GermplasmList.SEARCH_FOR_GERMPLASM_LIST, programUUID));
					query.setParameter("gidLength", q.length());
					query.setParameter("q", q + "%");
					query.setParameter("qNoSpaces", q.replace(" ", "") + "%");
					query.setParameter("qStandardized", GermplasmDataManagerUtil.standardizeName(q) + "%");
				}

			}
			query.setParameter("gid", q);

			if (programUUID != null) {
				query.setParameter(PROGRAM_UUID, programUUID);
			}

			query.addEntity("listnms", GermplasmList.class);
			return query.list();

		} catch (final Exception e) {
			this.logAndThrowException("Error with searchGermplasmLists(" + q + ") " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	private String getSearchForGermplasmListsQueryString(final String initialQueryString, final String programUUID) {
		String queryString = initialQueryString;
		if (programUUID != null) {
			queryString += GermplasmList.FILTER_BY_PROGRAM_UUID;
		}
		return queryString;
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getByProjectIdAndType(final int projectId, final GermplasmListType type) {
		final List<GermplasmList> list = new ArrayList<>();
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.eq("projectId", projectId));
			criteria.add(Restrictions.eq("type", type.name()));
			criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));

			return criteria.list();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByProjectId(projectId=" + projectId + ") query from GermplasmList: " + e.getMessage(),
					e);
		}
		return list;
	}

	public Integer getListDataListIDFromListDataProjectListID(final Integer listDataProjectListID) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.eq("id", listDataProjectListID));
			criteria.setProjection(Projections.property("listRef"));

			return (Integer) criteria.uniqueResult();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getListDataListIDFromListDataProjectListID(listDataProjectListID=" + listDataProjectListID
					+ ") query from GermplasmList: " + e.getMessage(), e);
		}

		return 0;
	}

	// returns all the list of the program regardless of the type and status
	@SuppressWarnings("unchecked")
	public List<GermplasmList> getListsByProgram(final String programUUID) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
		criteria.add(Restrictions.eq(PROGRAM_UUID, programUUID));
		return criteria.list();
	}

	// returns all the list of the program except the deleted ones and snapshot list
	@SuppressWarnings("unchecked")
	public List<GermplasmList> getListsByProgramUUID(final String programUUID) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
		criteria.add(Restrictions.eq(PROGRAM_UUID, programUUID));
		criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));
		this.hideSnapshotListTypes(criteria);
		return criteria.list();
	}

	public List<GermplasmList> getByListRef(final Integer listRef) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
		criteria.add(Restrictions.eq("listRef", listRef));
		return (List<GermplasmList>) criteria.list();
	}

	public List<Object[]> getAllListMetadata(final List<Integer> listIdsFromGermplasmList) {
		
		if(listIdsFromGermplasmList.isEmpty()) {
			return Collections.emptyList();
		}
		
		final StringBuilder sql = new StringBuilder(
				"SELECT ln.listid as listId, COUNT(ld.listid) as count, lu.uname as userName, lp.fname as firstName, lp.lname lastName")
						.append(" FROM listnms ln ").append("	INNER JOIN listdata ld ON ln.listid = ld.listid ")
						.append("   LEFT OUTER JOIN users lu ON ln.listuid = lu.userid ")
						.append("   LEFT OUTER JOIN persons lp ON lp.personid = lu.personid ").append(" WHERE ln.listid in (:listids) AND")
						.append(" ln.listtype != 'FOLDER' ")
						.append(" GROUP BY ln.listid;");

		final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
		query.addScalar("listId", new IntegerType());
		query.addScalar("count", new IntegerType());
		query.addScalar("userName");
		query.addScalar("firstName");
		query.addScalar("lastName");
		query.setParameterList("listids", listIdsFromGermplasmList);
		
		@SuppressWarnings("unchecked")
		final List<Object[]> queryResults = query.list();
		return queryResults;
	}

	/**
	 * @param listIds a group of ids for which we want to retrieve germplasm list
	 * @return the resultant germplasm list
	 */
	public List<GermplasmList> getAllGermplasmListsById(final List<Integer> listIds) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
		criteria.add(Restrictions.in("id", listIds));
		criteria.add(Restrictions.ne(STATUS, GermplasmListDAO.STATUS_DELETED));
		criteria.add(Restrictions.eq("type", GermplasmListType.LST.toString()));
		return criteria.list();
	}

	/**
	 * @param folderIds a group of folder ids for which we want to return children
	 * @return the resultant map which contains the folder meta data
	 */
	public Map<Integer, GermplasmFolderMetadata> getGermplasmFolderMetadata(final List<Integer> folderIds) {
		
		if(folderIds.isEmpty()) {
			return Collections.<Integer, GermplasmFolderMetadata>emptyMap();
		}
		
		final String folderMetaDataQuery = "SELECT parent.listid AS listId, COUNT(child.listid) AS numberOfChildren FROM listnms parent "
				+ "LEFT OUTER JOIN listnms child ON child.lhierarchy = parent.listid "
				+ "WHERE parent.listid IN (:folderIds) GROUP BY parent.listid";
		final SQLQuery setResultTransformer = this.getSession().createSQLQuery(folderMetaDataQuery);
		setResultTransformer.setParameterList("folderIds", folderIds);
		setResultTransformer.addScalar("listId", new IntegerType());
		setResultTransformer.addScalar("numberOfChildren", new IntegerType());
		setResultTransformer.setResultTransformer(Transformers.aliasToBean(GermplasmFolderMetadata.class));
		final List<GermplasmFolderMetadata> list = setResultTransformer.list();
		return Maps.uniqueIndex(list, new Function<GermplasmFolderMetadata, Integer>() {
			@Override
			public Integer apply(final GermplasmFolderMetadata folderMetaData) {
				return folderMetaData.getListId();
			}
		});
	}

	public int deleteGermplasmListByListIdPhysically(final Integer listId) {
		final Query query = this.getSession().getNamedQuery(GermplasmList.DELETE_GERMPLASM_LIST_BY_LISTID_PHYSICALLY);
		query.setInteger(GermplasmList.GERMPLASM_LIST_LIST_ID_COLUMN, listId);
		return query.executeUpdate();
	}

	/**
	 * Verify if the gids are used in more than one list
	 *
	 * @param gids gids to check
	 * @return Map with GID as key and CSV of list where it is used
	 */
	public Map<Integer, String> getGermplasmUsedInMoreThanOneList(final List<Integer> gids) {
		final Map<Integer, String> resultMap = new HashMap<>();

		final SQLQuery query = this.getSession().createSQLQuery(GermplasmListDAO.GET_GERMPLASM_USED_IN_MORE_THAN_ONE_LIST);
		query.setParameterList("gids", gids);

		final List<Object[]> results = query.list();
		for (final Object[] result : results) {
			resultMap.put((Integer) result[0], (String) result[1]);
		}
		return resultMap;
	}
}
