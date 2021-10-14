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

package org.generationcp.middleware.dao.germplasmlist;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.generationcp.middleware.api.germplasmlist.GermplasmListDto;
import org.generationcp.middleware.api.germplasmlist.MyListsDTO;
import org.generationcp.middleware.api.germplasmlist.search.GermplasmListSearchRequest;
import org.generationcp.middleware.api.germplasmlist.search.GermplasmListSearchResponse;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.ListMetadata;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.generationcp.middleware.util.Util;
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
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * DAO class for {@link GermplasmList}.
 */
public class GermplasmListDAO extends GenericDAO<GermplasmList, Integer> {

	private static final String ID = "id";
	private static final String PROGRAM_UUID = "programUUID";
	private static final String NAME = "name";
	private static final String PARENT = "parent";
	private static final String TYPE = "type";

	private static final String STATUS = "status";

	// TODO: instead use GermplasmList.Status
	@Deprecated
	public static final Integer STATUS_DELETED = 9;

	// TODO: instead use GermplasmList.Status
	@Deprecated
	static final Integer LOCKED_LIST_STATUS = 101;

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmListDAO.class);

	private static final String GET_GERMPLASM_LIST_TYPES = "SELECT fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, fuid, fdate, scaleid "
			+ "FROM udflds " + "WHERE ftable = 'LISTNMS' AND ftype = 'LISTTYPE' ";

	private static final String GET_GERMPLASM_NAME_TYPES = "SELECT fldno, ftable, ftype, fcode, fname, ffmt, fdesc, lfldno, fuid, fdate, scaleid "
			+ "FROM udflds " + "WHERE ftable = 'NAMES' AND ftype = 'NAME'";

	private static final String SEARCH_FOR_GERMPLASM_LIST = "SELECT DISTINCT listnms.* " + "FROM listnms "
			+ "      LEFT JOIN listdata ON (listdata.listid=listnms.listid AND lrstatus!=9) "
			+ "      LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.deleted = 0) "
			+ "WHERE liststatus!=9 AND ((listdata.gid=:gid AND 0!=:gid AND length(listdata.gid)=:gidLength) "
			+ "      OR desig LIKE :q OR listname LIKE :q " + "      OR desig LIKE :qNoSpaces "
			+ "      OR desig LIKE :qStandardized " + ")";

	private static final String SEARCH_FOR_GERMPLASM_LIST_GID_LIKE = "SELECT DISTINCT listnms.* " + "FROM listnms "
			+ "      LEFT JOIN listdata ON (listdata.listid=listnms.listid AND lrstatus!=9) "
			+ "      LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.deleted = 0) "
			+ "WHERE liststatus!=9 AND (listdata.gid LIKE :gid OR desig LIKE :q OR listname LIKE :q"
			+ "      OR desig LIKE :qNoSpaces " + "      OR desig LIKE :qStandardized " + ")";

	private static final String SEARCH_FOR_GERMPLASM_LIST_EQUAL = "SELECT DISTINCT listnms.* " + "FROM listnms "
			+ "      LEFT JOIN listdata ON (listdata.listid=listnms.listid AND lrstatus!=9) "
			+ "      LEFT JOIN germplsm ON (listdata.gid=germplsm.gid AND germplsm.deleted = 0) "
			+ "WHERE liststatus!=9 AND ((listdata.gid=:gid AND 0!=:gid AND length(listdata.gid)=:gidLength) "
			+ "      OR desig = :q OR listname = :q " + "      OR desig = :qNoSpaces "
			+ "      OR desig = :qStandardized " + ")";

	private static final String FILTER_BY_PROGRAM_UUID = " AND (program_uuid = :programUUID OR program_uuid IS NULL)";

	@SuppressWarnings("unchecked")
	public List<GermplasmList> getAllExceptDeleted(final int start, final int numOfRows) {
		try {
			final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
			criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));

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

	public List<GermplasmListDto> getGermplasmListDtos(final Integer gid) {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT DISTINCT l.listid AS listId, ");
			queryString.append("l.listname AS listName, ");
			queryString.append("STR_TO_DATE (convert(l.listdate,char), '%Y%m%d') AS  creationDate, ");
			queryString.append("l.listdesc AS description, ");
			queryString.append("l.program_uuid AS programUUID, ");
			queryString.append("IF (l.liststatus = " + GermplasmList.Status.LOCKED_LIST.getCode() + ", true, false) AS locked, ");
			queryString.append("l.listuid AS ownerId ");
			queryString.append("FROM listnms l ");
			queryString.append("INNER JOIN listdata ld ON ld.listid = l.listid ");
			queryString.append("WHERE ld.gid = :gid AND l.liststatus != " + GermplasmListDAO.STATUS_DELETED);

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryString.toString());
			sqlQuery.addScalar("listId");
			sqlQuery.addScalar("listName");
			sqlQuery.addScalar("creationDate");
			sqlQuery.addScalar("description");
			sqlQuery.addScalar("programUUID");
			sqlQuery.addScalar("locked", BooleanType.INSTANCE);
			sqlQuery.addScalar("ownerId");
			sqlQuery.setParameter("gid", gid);
			sqlQuery.setResultTransformer(new AliasToBeanResultTransformer(GermplasmListDto.class));
			return sqlQuery.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getGermplasmListDtos(gid=" + gid + ") from GermplasmList: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	// TODO IBP-4457:  delete when phasing out old Germplasm Details popup
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

	public long countByGIDs(final List<Integer> gids) {
		try {
			if (!CollectionUtils.isEmpty(gids)) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmListData.class);
				criteria.createAlias("list", "l");
				criteria.add(Restrictions.in("gid", gids));
				criteria.add(Restrictions.ne("l.status", GermplasmListDAO.STATUS_DELETED));
				criteria.setProjection(Projections.countDistinct("l.id"));
				return ((Long) criteria.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countByGIDs(gids=" + gids + ") query from GermplasmList " + e.getMessage();
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
				criteria.setProjection(Projections.countDistinct(ID));
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

	public long countByStatus(final Integer status) {
		try {
			if (status != null) {
				final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
				criteria.add(Restrictions.eq(GermplasmListDAO.STATUS, status));
				criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));
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
				criteria.addOrder(Order.desc(ID));

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
		return criteria.list();
	}

	public List<Object[]> getAllListMetadata(final List<Integer> listIdsFromGermplasmList) {

		if (listIdsFromGermplasmList.isEmpty()) {
			return Collections.emptyList();
		}

		final StringBuilder sql = new StringBuilder(
				"SELECT ln.listid as listId, COUNT(ld.listid) as count, ln.listuid as ownerId ")
						.append(" FROM listnms ln ").append("	INNER JOIN listdata ld ON ln.listid = ld.listid ")
						.append(" WHERE ln.listid in (:listids) AND").append(" ln.listtype != 'FOLDER' ")
						.append(" GROUP BY ln.listid;");

		final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
		query.addScalar("listId", new IntegerType());
		query.addScalar("count", new IntegerType());
		query.addScalar("ownerId", new IntegerType());
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
		criteria.add(Restrictions.in(ID, listIds));
		criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));
		criteria.add(Restrictions.eq("type", GermplasmListType.LST.toString()));
		return criteria.list();
	}

	/**
	 * @param folderIds
	 *            a group of folder ids for which we want to return children
	 * @return the resultant map which contains the folder meta data
	 */
	public Map<Integer, ListMetadata> getGermplasmFolderMetadata(final List<Integer> folderIds) {

		if (folderIds.isEmpty()) {
			return Collections.emptyMap();
		}

		final String folderMetaDataQuery = "SELECT parent.listid AS listId, COUNT(child.listid) AS numberOfChildren FROM listnms parent "
				+ "LEFT OUTER JOIN listnms child ON child.lhierarchy = parent.listid "
				+ "WHERE parent.listid IN (:folderIds) GROUP BY parent.listid";
		final SQLQuery setResultTransformer = this.getSession().createSQLQuery(folderMetaDataQuery);
		setResultTransformer.setParameterList("folderIds", folderIds);
		setResultTransformer.addScalar("listId", new IntegerType());
		setResultTransformer.addScalar("numberOfChildren", new IntegerType());
		setResultTransformer.setResultTransformer(Transformers.aliasToBean(ListMetadata.class));
		final List<ListMetadata> list = setResultTransformer.list();
		return Maps.uniqueIndex(list, new Function<ListMetadata, Integer>() {

			@Override
			public Integer apply(final ListMetadata folderMetaData) {
				return folderMetaData.getListId();
			}
		});
	}

	public List<Integer> getListIdsByGIDs(final List<Integer> gids) {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT l.listid FROM listnms l ");
			queryString.append("INNER JOIN listdata ld ON ld.listid = l.listid ");
			queryString.append("WHERE ld.gid IN(:gids) AND l.liststatus != :status");
			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("gids", gids);
			query.setParameter("status", STATUS_DELETED);
			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error with getListIdsByGIDs(gids=" + gids.toString() + ") query from GermplasmListDAO: " + e.getMessage(), e);
		}
	}

	public int deleteGermplasmListByListIdPhysically(final Integer listId) {
		final Query query = this.getSession().getNamedQuery(GermplasmList.DELETE_GERMPLASM_LIST_BY_LISTID_PHYSICALLY);
		query.setInteger(GermplasmList.GERMPLASM_LIST_LIST_ID_COLUMN, listId);
		return query.executeUpdate();
	}

	/**

	/**
	 * Get germplasm that exist locked lists
	 *
	 * @param gids
	 */
	public List<Integer> getGermplasmUsedInLockedList(final List<Integer> gids) {
		if (CollectionUtils.isEmpty(gids)) {
			return Collections.emptyList();
		}
		final SQLQuery query = this.getSession()
			.createSQLQuery(" SELECT ld.gid as gid "
				+ " FROM listnms l"
				+ " INNER JOIN listdata ld ON l.listid = ld.listid INNER JOIN germplsm g ON ld.gid = g.gid"
				+ " WHERE ld.gid IN (:gids) AND l.liststatus = " + GermplasmListDAO.LOCKED_LIST_STATUS
				+ " GROUP BY ld.gid \n" + " HAVING count(1) >= 1");
		query.addScalar("gid", new IntegerType());
		query.setParameterList("gids", gids);
		return query.list();
	}

	/**
	 * @param folderIds a group of folder ids/germplasm lists for which we want to return metadata
	 * @return the resultant map which contains the object meta data
	 */
	public Map<Integer, ListMetadata> getGermplasmListMetadata(final List<Integer> folderIds) {
		final List<ListMetadata> list;
		if (folderIds.isEmpty()) {
			return Collections.emptyMap();
		}

		try {
			final String folderMetaDataQuery = "SELECT parent.listid AS listId," + "  COUNT(child.listid) AS numberOfChildren, "
					+ "  COUNT(s.gid) AS numberOfEntries " + " FROM listnms parent"
					+ "   LEFT OUTER JOIN listnms child ON child.lhierarchy = parent.listid "
					+ "   LEFT OUTER JOIN listdata s ON s.listid = parent.listid "
					+ " WHERE parent.listid IN (:folderIds) GROUP BY parent.listid";
			final SQLQuery setResultTransformer = this.getSession().createSQLQuery(folderMetaDataQuery);
			setResultTransformer.setParameterList("folderIds", folderIds);
			setResultTransformer.addScalar("listId", new IntegerType());
			setResultTransformer.addScalar("numberOfChildren", new IntegerType());
			setResultTransformer.addScalar("numberOfEntries", new IntegerType());
			setResultTransformer.setResultTransformer(Transformers.aliasToBean(ListMetadata.class));
			list = setResultTransformer.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
					"Error with getGermplasmListMetadata(folderIds=" + folderIds.toString() + ") query from listnms: " + e.getMessage(),
					e);
		}
		return Maps.uniqueIndex(list, new Function<ListMetadata, Integer>() {

			@Override
			public Integer apply(final ListMetadata folderMetaData) {
				return folderMetaData.getListId();
			}
		});
	}

	public GermplasmList getGermplasmListByParentAndName(final String germplasmListName, final Integer parentId, final String programUUID) {
		try {

			final DetachedCriteria criteria = DetachedCriteria.forClass(GermplasmList.class);
			criteria.add(Restrictions.eq(NAME, germplasmListName));
			criteria.add(Restrictions.eq(PROGRAM_UUID, programUUID));
			criteria.add(Restrictions.ne(STATUS, STATUS_DELETED));

			if (Objects.isNull(parentId)) {
				criteria.add(Restrictions.isNull(PARENT));
			} else {
				final GermplasmList parent = new GermplasmList();
				parent.setId(parentId);

				criteria.add(Restrictions.eq(PARENT, parent));
			}

			return (GermplasmList) criteria.getExecutableCriteria(this.getSession()).uniqueResult();
		} catch (final Exception e) {
			final String message = "Error with getGermplasmListByParentAndName(germplasmListName=" + germplasmListName + ", parentId= " + parentId
				+ " ) query from GermplasmList: " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message);
		}
	}

	public GermplasmList getByIdAndProgramUUID(final Integer id, final String programUUID) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmList.class);
		criteria.add(Restrictions.eq(ID, id));
		criteria.add(Restrictions.ne(GermplasmListDAO.STATUS, GermplasmListDAO.STATUS_DELETED));

		if (Objects.isNull(programUUID)) {
			criteria.add(Restrictions.isNull(PROGRAM_UUID));
		} else {
			criteria.add(Restrictions.eq(PROGRAM_UUID, programUUID));
		}

		return (GermplasmList) criteria.uniqueResult();
	}

	public long countMyLists(final String programUUID, final Integer userId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass())
				.add(Restrictions.eq("userId", userId));

			criteria.add(Restrictions.ne(STATUS, STATUS_DELETED));
			criteria.add(Restrictions.ne(TYPE, GermplasmList.FOLDER_TYPE));
			criteria.add(Restrictions.eq(PROGRAM_UUID, programUUID));
			criteria.setProjection(Projections.rowCount());
			return (long) criteria.uniqueResult();
		} catch (final Exception e) {
			final String message = "Error with countMyLists(programUUID=" + programUUID + ", userId= " + userId + " ): " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message);
		}
	}

	/**
	 * @return lists owned by user (possibly along with statistical information in the future)
	 */
	public List<MyListsDTO> getMyLists(final String programUUID, final Pageable pageable, final Integer userId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass())
				.add(Restrictions.eq("userId", userId));
			criteria.add(Restrictions.ne(STATUS, STATUS_DELETED));
			criteria.add(Restrictions.ne(TYPE, GermplasmList.FOLDER_TYPE));
			criteria.add(Restrictions.eq(PROGRAM_UUID, programUUID));
			// FIXME sort by parent (null) => "Program lists"
			criteria.createAlias("parent", "parent", JoinType.LEFT_OUTER_JOIN);
			addOrder(criteria, pageable);
			addPagination(criteria, pageable);

			final List<GermplasmList> list = criteria.list();

			if (list.isEmpty()) {
				return Collections.emptyList();
			}

			final List<MyListsDTO> mylists = new ArrayList<>();
			for (final GermplasmList germplasmList : list) {
				final MyListsDTO mylist = new MyListsDTO();
				mylist.setListId(germplasmList.getId());
				mylist.setName(germplasmList.getName());
				mylist.setDate(Util.tryConvertDate(String.valueOf(germplasmList.getDate()),
					Util.DATE_AS_NUMBER_FORMAT, Util.FRONTEND_DATE_FORMAT));
				final GermplasmList parent = germplasmList.getParent();
				if (parent != null) {
					mylist.setFolder(parent.getName());
				}
				mylist.setType(germplasmList.getType());
				mylists.add(mylist);
			}

			return mylists;
		} catch (final Exception e) {
			final String message = "Error with getMyLists(programUUID=" + programUUID + ", userId= " + userId + " ): " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message);
		}
	}

	public void markProgramGermplasmListsAsDeleted(final String programUUID) {
		try {
			final String sql = "UPDATE listnms SET liststatus = " + STATUS_DELETED + " where program_uuid = :programUUID";
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
			sqlQuery.setParameter("programUUID", programUUID);
			sqlQuery.executeUpdate();
		} catch (final Exception e) {
			final String message = "Error with markProgramGermplasmListsAsDeleted(programUUID=" + programUUID + " ): " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message);
		}
	}

	public List<GermplasmListSearchResponse> searchGermplasmList(final GermplasmListSearchRequest germplasmListSearchRequest,
		final Pageable pageable, final String programUUID) {

		final SQLQueryBuilder queryBuilder = GermplasmListSearchDAOQuery.getSelectQuery(germplasmListSearchRequest, pageable);
		queryBuilder.setParameter("programUUID", programUUID);

		final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
		queryBuilder.addParamsToQuery(query);

		query.addScalar(GermplasmListSearchDAOQuery.LIST_ID_ALIAS);
		query.addScalar(GermplasmListSearchDAOQuery.LIST_NAME_ALIAS);
		query.addScalar(GermplasmListSearchDAOQuery.PARENT_FOLDER_NAME_ALIAS);
		query.addScalar(GermplasmListSearchDAOQuery.DESCRIPTION_ALIAS);
		query.addScalar(GermplasmListSearchDAOQuery.LIST_OWNER_ALIAS);
		query.addScalar(GermplasmListSearchDAOQuery.LIST_TYPE_ALIAS);
		query.addScalar(GermplasmListSearchDAOQuery.NUMBER_OF_ENTRIES_ALIAS, IntegerType.INSTANCE);
		query.addScalar(GermplasmListSearchDAOQuery.LOCKED_ALIAS, BooleanType.INSTANCE);
		query.addScalar(GermplasmListSearchDAOQuery.NOTES_ALIAS);
		query.addScalar(GermplasmListSearchDAOQuery.CREATION_DATE_ALIAS);
		query.setResultTransformer(Transformers.aliasToBean(GermplasmListSearchResponse.class));

		GenericDAO.addPaginationToSQLQuery(query, pageable);

		return (List<GermplasmListSearchResponse>) query.list();
	}

	public long countSearchGermplasmList(final GermplasmListSearchRequest germplasmListSearchRequest, final String programUUID) {
		final SQLQueryBuilder queryBuilder = GermplasmListSearchDAOQuery.getCountQuery(germplasmListSearchRequest);
		queryBuilder.setParameter("programUUID", programUUID);

		final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
		queryBuilder.addParamsToQuery(query);

		return ((BigInteger) query.uniqueResult()).longValue();
	}

}
