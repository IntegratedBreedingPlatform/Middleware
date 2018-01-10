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
import java.util.Arrays;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListDAO.class);

	public static final String GET_GERMPLASM_USED_IN_MORE_THAN_ONE_LIST = " SELECT \n" + "   ld.gid, \n"
			+ "   group_concat(l.listname) \n" + " FROM listnms l \n"
			+ "   INNER JOIN listdata ld ON l.listid = ld.listid \n" + "   INNER JOIN germplsm g ON ld.gid = g.gid"
			+ " WHERE ld.gid IN (:gids) \n" + "       AND l.liststatus != " + GermplasmListDAO.STATUS_DELETED + " \n"
			+ " GROUP BY ld.gid \n" + " HAVING count(1) > 1";
	
	private static final String HIDDEN_LIST_TYPES_PARAM = "hiddenListTypes";
	
	private static final String GET_GERMPLASM_LIST_TYPES = "SELECT fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, fuid, fdate, scaleid "
			+ "FROM udflds " + "WHERE ftable = 'LISTNMS' AND ftype = 'LISTTYPE' " + "and fcode not in (:" + GermplasmListDAO.HIDDEN_LIST_TYPES_PARAM + ")";

	private static final String GET_GERMPLASM_NAME_TYPES = "SELECT fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, fuid, fdate, scaleid "
			+ "FROM udflds " + "WHERE ftable = 'NAMES' AND ftype = 'NAME'";

	private static final String SEARCH_FOR_GERMPLASM_LIST = "SELECT DISTINCT listnms.* " + "FROM listnms "
			+ "      LEFT JOIN listdata ON (listdata.listid=listnms.listid AND lrstatus!=9) "
			+ "      LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.deleted = 0) "
			+ "WHERE listtype not in (:" + GermplasmListDAO.HIDDEN_LIST_TYPES_PARAM + ") "
			+ " AND liststatus!=9 AND ((listdata.gid=:gid AND 0!=:gid AND length(listdata.gid)=:gidLength) "
			+ "      OR desig LIKE :q OR listname LIKE :q " + "      OR desig LIKE :qNoSpaces "
			+ "      OR desig LIKE :qStandardized " + ")";

	private static final String SEARCH_FOR_GERMPLASM_LIST_GID_LIKE = "SELECT DISTINCT listnms.* " + "FROM listnms "
			+ "      LEFT JOIN listdata ON (listdata.listid=listnms.listid AND lrstatus!=9) "
			+ "      LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.deleted = 0) "
			+ "WHERE listtype not in (:" + GermplasmListDAO.HIDDEN_LIST_TYPES_PARAM + ")"
			+ " AND liststatus!=9 AND (listdata.gid LIKE :gid OR desig LIKE :q OR listname LIKE :q"
			+ "      OR desig LIKE :qNoSpaces " + "      OR desig LIKE :qStandardized " + ")";

	private static final String SEARCH_FOR_GERMPLASM_LIST_EQUAL = "SELECT DISTINCT listnms.* " + "FROM listnms "
			+ "      LEFT JOIN listdata ON (listdata.listid=listnms.listid AND lrstatus!=9) "
			+ "      LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.deleted = 0) " 
			+ "WHERE listtype not in (:" + GermplasmListDAO.HIDDEN_LIST_TYPES_PARAM + ")"
			+ " AND liststatus!=9 AND ((listdata.gid=:gid AND 0!=:gid AND length(listdata.gid)=:gidLength) "
			+ "      OR desig = :q OR listname = :q " + "      OR desig = :qNoSpaces "
			+ "      OR desig = :qStandardized " + ")";

	private static final String FILTER_BY_PROGRAM_UUID = " AND (program_uuid = :programUUID OR program_uuid IS NULL)";
	
	private static final List<String> SNAPSHOT_LIST_TYPES = Arrays.asList(GermplasmListType.NURSERY.name(), GermplasmListType.TRIAL.name(), 
			GermplasmListType.CHECK.name(), GermplasmListType.ADVANCED.name(), GermplasmListType.CROSSES.name(), GermplasmListType.CRT_CROSS.name(),
			GermplasmListType.IMP_CROSS.name());
	
	private static final List<String> HIDDEN_LIST_TYPES_ON_SEARCH;

	private static final Criterion RESTRICTED_LIST;

	static {
		RESTRICTED_LIST = Restrictions.not(Restrictions.in("type", GermplasmListDAO.SNAPSHOT_LIST_TYPES.toArray(new String[0])));
		HIDDEN_LIST_TYPES_ON_SEARCH = new ArrayList<>(GermplasmListDAO.SNAPSHOT_LIST_TYPES);
		HIDDEN_LIST_TYPES_ON_SEARCH.add(GermplasmList.FOLDER_TYPE);
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getAllExceptDeleted(final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));

			this.hideSnapshotListTypes(criteria);

			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getAllExceptDeleted() query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countAllExceptDeleted() {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));

			this.hideSnapshotListTypes(criteria);

			criteria.setProjection(Projections.rowCount());
			final Long count = (Long) criteria.uniqueResult();

			if (count == null) {
				return 0;
			}
			return count.longValue();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countAllExceptDeleted() query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
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
			final String errorMessage = "Error with getByGid(gid=" + gid + ") query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getByGIDandProgramUUID(final Integer gid, final int start, final int numOfRows,
			final String programUUID) {
		try {
			if (gid != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class, "germplasmList");
				final DetachedCriteria gidCriteria = DetachedCriteria.forClass(GermplasmListData.class, "listData");
				gidCriteria.add(Restrictions.eq("listData.gid", gid));
				gidCriteria.add(Property.forName("germplasmList.id").eqProperty("listData.list.id"));
				criteria.add(Subqueries.exists(gidCriteria.setProjection(Projections.property("listData.gid"))));
				criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));
				this.addCriteriaForProgramUUIDInLists(programUUID, criteria);
				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				criteria.addOrder(Order.asc("name"));
				this.hideSnapshotListTypes(criteria);
				return criteria.list();
			}
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getByGIDandProgramUUID(gid=" + gid + ",programUUID=" + programUUID
					+ ") query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return new ArrayList<>();
	}

	private void addCriteriaForProgramUUIDInLists(final String programUUID, final Criteria criteria) {
		final Criterion sameProgramUUID = Restrictions.eq(GermplasmListDAO.PROGRAM_UUID, programUUID);
		final Criterion nullProgramUUID = Restrictions.isNull(GermplasmListDAO.PROGRAM_UUID);
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
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countByGID(gid=" + gid + ") query from GermplasmList " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
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
				criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));
				this.addCriteriaForProgramUUIDInLists(programUUID, criteria);
				this.hideSnapshotListTypes(criteria);
				criteria.setProjection(Projections.countDistinct("id"));
				return ((Long) criteria.uniqueResult()).longValue(); // count
			}
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countByGIDandProgramUUID(gid=" + gid + ",programUUID=" + programUUID
					+ ") query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getByName(final String name, final String programUUID, final Operation operation,
			final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);

			this.hideSnapshotListTypes(criteria);

			if (operation == null || operation == Operation.EQUAL) {
				criteria.add(Restrictions.eq("name", name));
			} else if (operation == Operation.LIKE) {
				criteria.add(Restrictions.like("name", name));
			}

			criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));

			this.addCriteriaForProgramUUIDInLists(programUUID, criteria);

			criteria.setFirstResult(start);
			criteria.setMaxResults(numOfRows);
			return criteria.list();
		} catch (final HibernateException e) {
			final String errorMessage ="Error with getByName(name=" + name + ") query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countByName(final String name, final Operation operation) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));
			criteria.setProjection(Projections.rowCount());

			this.hideSnapshotListTypes(criteria);

			if (operation == null || operation == Operation.EQUAL) {
				criteria.add(Restrictions.eq("name", name));
			} else if (operation == Operation.LIKE) {
				criteria.add(Restrictions.like("name", name));
			}
			return ((Long) criteria.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countByName(name=" + name + ") query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getByStatus(final Integer status, final int start, final int numOfRows) {
		try {
			if (status != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
				criteria.add(Restrictions.eq(GermplasmListDAO.STATUS, status));
				criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));

				this.hideSnapshotListTypes(criteria);

				criteria.setFirstResult(start);
				criteria.setMaxResults(numOfRows);
				return criteria.list();
			}
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getByStatus(status=" + status + ") query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return new ArrayList<>();
	}

	public long countByStatus(final Integer status) {
		try {
			if (status != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
				criteria.add(Restrictions.eq(GermplasmListDAO.STATUS, status));
				criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));

				this.hideSnapshotListTypes(criteria);

				criteria.setProjection(Projections.rowCount());
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countByStatus(status=" + status + ") query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getAllTopLevelLists(final String programUUID) {
		try {
			final Criterion topFolder = Restrictions.eq("parent.id", 0);
			final Criterion nullFolder = Restrictions.isNull("parent");
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.or(topFolder, nullFolder));
			criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));

			if (programUUID == null) {
				final Criterion nullProgramUUID = Restrictions.isNull(GermplasmListDAO.PROGRAM_UUID);
				criteria.add(nullProgramUUID);
			} else {
				final Criterion sameProgramUUID = Restrictions.eq(GermplasmListDAO.PROGRAM_UUID, programUUID);
				criteria.add(sameProgramUUID);
			}

			this.hideSnapshotListTypes(criteria);

			criteria.addOrder(Order.asc("name"));
			return criteria.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getAllTopLevelLists() query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	/**
	 * Gets the germplasm list children.
	 *
	 * @param parentId
	 *            the parent id
	 * @param programUUID
	 *            the program UUID
	 * @param start
	 *            the start
	 * @param numOfRows
	 *            the num of rows
	 * @return the germplasm list children
	 */
	@SuppressWarnings("unchecked")
	public List<GermplasmList> getByParentFolderId(final Integer parentId, final String programUUID) {
		try {
			if (parentId != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
				criteria.add(Restrictions.eq("parent.id", parentId));
				criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));

				this.addCriteriaForProgramUUIDInLists(programUUID, criteria);

				this.hideSnapshotListTypes(criteria);

				criteria.addOrder(Order.asc("name"));
				return criteria.list();
			}
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getByParentFolderId(parentId=" + parentId
					+ ") query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return new ArrayList<>();
	}

	@Nullable
	public GermplasmList getLastCreatedByUserID(final Integer userID, final String programUUID) {
		try {
			if (userID != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
				criteria.add(Restrictions.eq("userId", userID));
				criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));

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
			final String errorMessage = "Error with getByUserID(userID=" + userID + ") query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
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
	 * Return a List of UserDefinedField POJOs representing records from the
	 * udflds table of IBDB which are the types of germplasm lists.
	 *
	 * @return List of germplasm list types
	 */
	@SuppressWarnings("rawtypes")
	public List getGermplasmListTypes() {
		try {
			final Session session = this.getSession();
			final SQLQuery query = session.createSQLQuery(GermplasmListDAO.GET_GERMPLASM_LIST_TYPES);
			query.setParameterList(GermplasmListDAO.HIDDEN_LIST_TYPES_PARAM, GermplasmListDAO.HIDDEN_LIST_TYPES_ON_SEARCH);
			return query.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getGermplasmListTypes() query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	/**
	 * Get Germplasm Name Types
	 *
	 * Return a List of UserDefinedField POJOs representing records from the
	 * udflds table of IBDB which are the types of germplasm names.
	 *
	 * @return List of germplasm name types
	 */
	@SuppressWarnings("rawtypes")
	public List getGermplasmNameTypes() {
		try {
			final Session session = this.getSession();
			final SQLQuery query = session.createSQLQuery(GermplasmListDAO.GET_GERMPLASM_NAME_TYPES);
			return query.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getGermplasmNameTypes() query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	/**
	 * Get Germplasm Lists with names like Q or germplasms with name like Q or
	 * gid equal to Q
	 *
	 * @param q
	 * @param o
	 *            - like or equal
	 * @return List of GermplasmLists
	 */
	@SuppressWarnings("unchecked")
	public List<GermplasmList> searchForGermplasmLists(final String searchedString, final String programUUID,
			final Operation o) {
		final String q = searchedString.trim();
		if ("".equals(q)) {
			return new ArrayList<>();
		}
		try {
			final SQLQuery query;

			if (o.equals(Operation.EQUAL)) {
				query = this.getSession().createSQLQuery(this.getSearchForGermplasmListsQueryString(
						GermplasmListDAO.SEARCH_FOR_GERMPLASM_LIST_EQUAL, programUUID));
				query.setParameter("gidLength", q.length());
				query.setParameter("q", q);
				query.setParameter("qNoSpaces", q.replace(" ", ""));
				query.setParameter("qStandardized", GermplasmDataManagerUtil.standardizeName(q));
			} else {
				if (q.contains("%") || q.contains("_")) {
					query = this.getSession().createSQLQuery(this.getSearchForGermplasmListsQueryString(
							GermplasmListDAO.SEARCH_FOR_GERMPLASM_LIST_GID_LIKE, programUUID));
					query.setParameter("q", q);
					query.setParameter("qNoSpaces", q.replace(" ", ""));
					query.setParameter("qStandardized", GermplasmDataManagerUtil.standardizeName(q));
				} else {
					query = this.getSession().createSQLQuery(this.getSearchForGermplasmListsQueryString(
							GermplasmListDAO.SEARCH_FOR_GERMPLASM_LIST, programUUID));
					query.setParameter("gidLength", q.length());
					query.setParameter("q", q + "%");
					query.setParameter("qNoSpaces", q.replace(" ", "") + "%");
					query.setParameter("qStandardized", GermplasmDataManagerUtil.standardizeName(q) + "%");
				}

			}
			query.setParameter("gid", q);
			query.setParameterList(GermplasmListDAO.HIDDEN_LIST_TYPES_PARAM, GermplasmListDAO.HIDDEN_LIST_TYPES_ON_SEARCH);

			if (programUUID != null) {
				query.setParameter(GermplasmListDAO.PROGRAM_UUID, programUUID);
			}

			query.addEntity("listnms", GermplasmList.class);
			return query.list();

		} catch (final Exception e) {
			final String errorMessage = "Error with searchGermplasmLists(" + q + ") " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	private String getSearchForGermplasmListsQueryString(final String initialQueryString, final String programUUID) {
		String queryString = initialQueryString;
		if (programUUID != null) {
			queryString += GermplasmListDAO.FILTER_BY_PROGRAM_UUID;
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
			criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));

			return criteria.list();

		} catch (final HibernateException e) {
			final String errorMessage = "Error with getByProjectId(projectId=" + projectId
					+ ") query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public Integer getListDataListIDFromListDataProjectListID(final Integer listDataProjectListID) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.eq("id", listDataProjectListID));
			criteria.setProjection(Projections.property("listRef"));

			return (Integer) criteria.uniqueResult();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getListDataListIDFromListDataProjectListID(listDataProjectListID="
					+ listDataProjectListID + ") query from GermplasmList: " + e.getMessage();
			GermplasmListDAO.LOG.error(errorMessage);
			throw new MiddlewareQueryException(errorMessage, e);
		}

	}

	// returns all the list of the program regardless of the type and status
	@SuppressWarnings("unchecked")
	public List<GermplasmList> getListsByProgram(final String programUUID) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
		criteria.add(Restrictions.eq(GermplasmListDAO.PROGRAM_UUID, programUUID));
		return criteria.list();
	}

	// returns all the list of the program except the deleted ones and snapshot
	// list
	@SuppressWarnings("unchecked")
	public List<GermplasmList> getListsByProgramUUID(final String programUUID) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
		criteria.add(Restrictions.eq(GermplasmListDAO.PROGRAM_UUID, programUUID));
		criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));
		this.hideSnapshotListTypes(criteria);
		return criteria.list();
	}

	public List<GermplasmList> getByListRef(final Integer listRef) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
		criteria.add(Restrictions.eq("listRef", listRef));
		return criteria.list();
	}

	public List<Object[]> getAllListMetadata(final List<Integer> listIdsFromGermplasmList) {

		if (listIdsFromGermplasmList.isEmpty()) {
			return Collections.emptyList();
		}

		final StringBuilder sql = new StringBuilder(
				"SELECT ln.listid as listId, COUNT(ld.listid) as count, lu.uname as userName, lp.fname as firstName, lp.lname lastName")
						.append(" FROM listnms ln ").append("	INNER JOIN listdata ld ON ln.listid = ld.listid ")
						.append("   LEFT OUTER JOIN users lu ON ln.listuid = lu.userid ")
						.append("   LEFT OUTER JOIN persons lp ON lp.personid = lu.personid ")
						.append(" WHERE ln.listid in (:listids) AND").append(" ln.listtype != 'FOLDER' ")
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
	 * @param listIds
	 *            a group of ids for which we want to retrieve germplasm list
	 * @return the resultant germplasm list
	 */
	public List<GermplasmList> getAllGermplasmListsById(final List<Integer> listIds) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
		criteria.add(Restrictions.in("id", listIds));
		criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));
		criteria.add(Restrictions.eq("type", GermplasmListType.LST.toString()));
		return criteria.list();
	}

	/**
	 * @param folderIds
	 *            a group of folder ids for which we want to return children
	 * @return the resultant map which contains the folder meta data
	 */
	public Map<Integer, GermplasmFolderMetadata> getGermplasmFolderMetadata(final List<Integer> folderIds) {

		if (folderIds.isEmpty()) {
			return Collections.<Integer, GermplasmFolderMetadata> emptyMap();
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
	 * @param gids
	 *            gids to check
	 * @return Map with GID as key and CSV of list where it is used
	 */
	public Map<Integer, String> getGermplasmUsedInMoreThanOneList(final List<Integer> gids) {
		final Map<Integer, String> resultMap = new HashMap<>();

		final SQLQuery query = this.getSession()
				.createSQLQuery(GermplasmListDAO.GET_GERMPLASM_USED_IN_MORE_THAN_ONE_LIST);
		query.setParameterList("gids", gids);

		final List<Object[]> results = query.list();
		for (final Object[] result : results) {
			resultMap.put((Integer) result[0], (String) result[1]);
		}
		return resultMap;
	}
}
