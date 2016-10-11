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

	public static final Integer STATUS_DELETED = 9;
	protected static final Criterion RESTRICTED_LIST;

	static {
		RESTRICTED_LIST =
				Restrictions.not(Restrictions.in("type",
						new String[] {GermplasmListType.NURSERY.toString(), GermplasmListType.TRIAL.toString(),
								GermplasmListType.CHECK.toString(), GermplasmListType.ADVANCED.toString(),
								GermplasmListType.CROSSES.toString()}));
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getAllExceptDeleted(final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));

			this.hideSnapshotListTypes(criteria);

			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getAllExceptDeleted() query from GermplasmList: " + e.getMessage(), e);
		}
	}

	public long countAllExceptDeleted() {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));

			this.hideSnapshotListTypes(criteria);

			criteria.setProjection(Projections.rowCount());
			final Long count = (Long) criteria.uniqueResult();

			if (count == null) {
				return 0;
			}
			return count.longValue();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with countAllExceptDeleted() query from GermplasmList: " + e.getMessage(), e);
		}
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
			throw new MiddlewareQueryException("Error with getByGid(gid=" + gid + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList<GermplasmList>();
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getByGIDandProgramUUID(final Integer gid, final int start, final int numOfRows, final String programUUID) {
		try {
			if (gid != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class, "germplasmList");
				final DetachedCriteria gidCriteria = DetachedCriteria.forClass(GermplasmListData.class, "listData");
				gidCriteria.add(Restrictions.eq("listData.gid", gid));
				gidCriteria.add(Property.forName("germplasmList.id").eqProperty("listData.list.id"));
				criteria.add(Subqueries.exists(gidCriteria.setProjection(Projections.property("listData.gid"))));
				criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));
				this.addCriteriaForProgramUUIDInLists(programUUID, criteria);
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				criteria.addOrder(Order.asc("name"));
				this.hideSnapshotListTypes(criteria);
				return criteria.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByGIDandProgramUUID(gid=" + gid + ",programUUID=" + programUUID
					+ ") query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList<GermplasmList>();
	}

	private void addCriteriaForProgramUUIDInLists(final String programUUID, final Criteria criteria) {
		final Criterion sameProgramUUID = Restrictions.eq("programUUID", programUUID);
		final Criterion nullProgramUUID = Restrictions.isNull("programUUID");
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
			throw new MiddlewareQueryException("Error with countByGID(gid=" + gid + ") query from GermplasmList " + e.getMessage(), e);
		}
		return 0;
	}

	public long countByGIDandProgramUUID(final Integer gid, final String programUUID) {
		try {
			if (gid != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class, "germplasmList");
				final DetachedCriteria gidCriteria = DetachedCriteria.forClass(GermplasmListData.class, "listData");
				gidCriteria.add(Restrictions.eq("listData.gid", gid));
				gidCriteria.add(Property.forName("germplasmList.id").eqProperty("listData.list.id"));
				criteria.add(Subqueries.exists(gidCriteria.setProjection(Projections.property("listData.gid"))));
				criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));
				this.addCriteriaForProgramUUIDInLists(programUUID, criteria);
				this.hideSnapshotListTypes(criteria);
				criteria.setProjection(Projections.countDistinct("id"));
				return ((Long) criteria.uniqueResult()).longValue(); // count
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with countByGIDandProgramUUID(gid=" + gid + ",programUUID=" + programUUID
					+ ") query from GermplasmList: " + e.getMessage(), e);
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

			criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));

			this.addCriteriaForProgramUUIDInLists(programUUID, criteria);

			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByName(name=" + name + ") query from GermplasmList: " + e.getMessage(), e);
		}
	}

	public long countByName(final String name, final Operation operation) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));
			criteria.setProjection(Projections.rowCount());

			this.hideSnapshotListTypes(criteria);

			if (operation == null || operation == Operation.EQUAL) {
				criteria.add(Restrictions.eq("name", name));
			} else if (operation == Operation.LIKE) {
				criteria.add(Restrictions.like("name", name));
			}
			return ((Long) criteria.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with countByName(name=" + name + ") query from GermplasmList: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getByStatus(final Integer status, final int start, final int numOfRows) {
		try {
			if (status != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
				criteria.add(Restrictions.eq("status", status));
				criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));

				this.hideSnapshotListTypes(criteria);

				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByStatus(status=" + status + ") query from GermplasmList: " + e.getMessage(),
					e);
		}
		return new ArrayList<GermplasmList>();
	}

	public long countByStatus(final Integer status) {
		try {
			if (status != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
				criteria.add(Restrictions.eq("status", status));
				criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));

				this.hideSnapshotListTypes(criteria);

				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with countByStatus(status=" + status + ") query from GermplasmList: " + e.getMessage(), e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getAllTopLevelLists(final String programUUID) throws MiddlewareQueryException {
		try {
			final Criterion topFolder = Restrictions.eq("parent.id", 0);
			final Criterion nullFolder = Restrictions.isNull("parent");
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.or(topFolder, nullFolder));
			criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));

			this.addCriteriaForProgramUUIDInLists(programUUID, criteria);
			this.hideSnapshotListTypes(criteria);

			criteria.addOrder(Order.asc("name"));
			return criteria.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with getAllTopLevelLists(programUUID=" + programUUID + ") query from GermplasmList: " + e.getMessage(), e);
		}
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
	public List<GermplasmList> getByParentFolderId(final Integer parentId, final String programUUID) throws MiddlewareQueryException {
		try {
			if (parentId != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
				criteria.add(Restrictions.eq("parent.id", parentId));
				criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));

				this.addCriteriaForProgramUUIDInLists(programUUID, criteria);

				this.hideSnapshotListTypes(criteria);

				criteria.addOrder(Order.asc("name"));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByParentFolderId(parentId=" + parentId + ", programUUID=" + programUUID
					+ ") query from GermplasmList: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("rawtypes")
	@Nullable
	public GermplasmList getLastCreatedByUserID(final Integer userID, final String programUUID) {
		try {
			if (userID != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
				criteria.add(Restrictions.eq("userId", userID));
				criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));

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
			throw new MiddlewareQueryException("Error with getLastCreatedByUserID(userID=" + userID + ", programUUID=" + programUUID + ") "
					+ "query from GermplasmList: " + e.getMessage(), e);
		}
		// FIXME we should not be returning nulls
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
			throw new MiddlewareQueryException("Error with getGermplasmListTypes() query from GermplasmList: " + e.getMessage(), e);
		}
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
			throw new MiddlewareQueryException("Error with getGermplasmNameTypes() query from GermplasmList: " + e.getMessage(), e);
		}
	}

	/**
	 * Get Germplasm Lists with: 1. names like query string or 2 germplasm with name like query string or 3. gid equal to query string
	 *
	 * @param searchString - the search string with the wildcard character "%" expected to be appended already for LIKE operations
	 * @param operation - LIKE or EQUAL matching operation
	 * @return List of GermplasmLists
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("unchecked")
	public List<GermplasmList> searchForGermplasmLists(final String searchString, final String programUUID, final Operation operation)
			throws MiddlewareQueryException {
		final String queryString = searchString.trim();
		if ("".equals(queryString)) {
			return new ArrayList<GermplasmList>();
		}
		try {

			final SQLQuery query =
					this.getSession().createSQLQuery(this.getSearchForGermplasmListsQueryString(queryString, programUUID, operation));

			if (operation.equals(Operation.EQUAL)) {
				query.setParameter("gidLength", queryString.length());

			}
			query.setParameter("gid", queryString);
			query.setParameter("q", queryString);
			query.setParameter("qNoSpaces", queryString.replace(" ", ""));
			query.setParameter("qStandardized", GermplasmDataManagerUtil.standardizeName(queryString));

			if (programUUID != null) {
				query.setParameter("programUUID", programUUID);
			}

			query.addEntity("listnms", GermplasmList.class);
			return query.list();

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Error with searchForGermplasmLists(" + queryString + ", programUUID=" + programUUID
					+ ", operation=" + operation + ") " + e.getMessage(), e);
		}
	}

	private String getSearchForGermplasmListsQueryString(final String initialQueryString, final String programUUID,
			final Operation operation) {
		final StringBuilder queryStringBuilder = new StringBuilder();
		queryStringBuilder.append("SELECT DISTINCT listnms.* " + "FROM listnms "
				+ "      LEFT JOIN listdata ON (listdata.listid=listnms.listid AND lrstatus!=9) "
				+ "      LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.gid!=germplsm.grplce) ");

		// Exclude snapshot lists, deleted lists and folder type lists
		queryStringBuilder.append(
				"WHERE listtype not in ('NURSERY', 'TRIAL', 'CHECK', 'ADVANCED', 'CROSSES') AND liststatus!=9 AND listtype!='FOLDER' AND ( ");

		// Match to GIDs in listdata
		if (Operation.EQUAL.equals(operation)) {
			queryStringBuilder.append("(listdata.gid=:gid AND 0!=:gid AND length(listdata.gid)=:gidLength) ");
		} else {
			queryStringBuilder.append("listdata.gid LIKE :gid ");
		}

		// Match to list name or listdata.desgination or
		if (Operation.EQUAL.equals(operation)) {
			queryStringBuilder.append(" OR desig = :q OR listname = :q OR desig = :qNoSpaces OR desig = :qStandardized )");
		} else {
			queryStringBuilder.append(" OR desig LIKE :q OR listname LIKE :q OR desig LIKE :qNoSpaces OR desig LIKE :qStandardized )");
		}

		// Filter to lists in current program plus historical lists
		if (programUUID != null) {
			queryStringBuilder.append(" AND (program_uuid = :programUUID OR program_uuid IS NULL)");
		}
		return queryStringBuilder.toString();
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getByProjectIdAndType(final int projectId, final GermplasmListType type) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.eq("projectId", projectId));
			criteria.add(Restrictions.eq("type", type.name()));
			criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));

			return criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByProjectIdAndType(projectId=" + projectId + ", type=" + type
					+ ") query from GermplasmList: " + e.getMessage(), e);
		}
	}

	public Integer getListDataListIDFromListDataProjectListID(final Integer listDataProjectListID) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.eq("id", listDataProjectListID));
			criteria.setProjection(Projections.property("listRef"));

			return (Integer) criteria.uniqueResult();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getListDataListIDFromListDataProjectListID(listDataProjectListID="
					+ listDataProjectListID + ") query from GermplasmList: " + e.getMessage(), e);
		}
	}

	// returns all the list of the program regardless of the type and status
	@SuppressWarnings("unchecked")
	public List<GermplasmList> getListsByProgram(final String programUUID) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
		criteria.add(Restrictions.eq("programUUID", programUUID));
		return criteria.list();
	}

	// returns all the list of the program except the deleted ones and snapshot list
	@SuppressWarnings("unchecked")
	public List<GermplasmList> getListsByProgramUUID(final String programUUID) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
		criteria.add(Restrictions.eq("programUUID", programUUID));
		criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));
		this.hideSnapshotListTypes(criteria);
		return criteria.list();
	}

	public GermplasmList getByListRef(final Integer listRef) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
		criteria.add(Restrictions.eq("listRef", listRef));
		return (GermplasmList) criteria.uniqueResult();
	}

	public List<Object[]> getAllListMetadata(final List<Integer> listIdsFromGermplasmList) {

		if (listIdsFromGermplasmList.isEmpty()) {
			return Collections.emptyList();
		}

		final StringBuilder sql = new StringBuilder(
				"SELECT ln.listid as listId, COUNT(ld.listid) as count, lu.uname as userName, lp.fname as firstName, lp.lname lastName")
						.append(" FROM listnms ln ").append("	INNER JOIN listdata ld ON ln.listid = ld.listid ")
						.append("   LEFT OUTER JOIN users lu ON ln.listuid = lu.userid ")
						.append("   LEFT OUTER JOIN persons lp ON lp.personid = lu.personid ").append(" WHERE ln.listid in (:listids) AND")
						.append(" ln.listtype != 'FOLDER' ").append(" GROUP BY ln.listid;");

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
	@SuppressWarnings("unchecked")
	public List<GermplasmList> getAllGermplasmListsById(final List<Integer> listIds) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
		criteria.add(Restrictions.in("id", listIds));
		criteria.add(Restrictions.ne("status", GermplasmListDAO.STATUS_DELETED));
		criteria.add(Restrictions.eq("type", GermplasmListType.LST.toString()));
		return criteria.list();
	}

	/**
	 * @param folderIds a group of folder ids for which we want to return children
	 * @return the resultant map which contains the folder meta data
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, GermplasmFolderMetadata> getGermplasmFolderMetadata(final List<Integer> folderIds) {

		if (folderIds.isEmpty()) {
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
}
