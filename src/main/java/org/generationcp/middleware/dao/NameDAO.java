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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.GetGermplasmByNameModes;
import org.generationcp.middleware.pojos.GermplasmNameDetails;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * DAO class for {@link Name}.
 *
 */
public class NameDAO extends GenericDAO<Name, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(NameDAO.class);

	public List<Name> getByGIDWithFilters(final Integer gid, final Integer status, final GermplasmNameType type) {
		if (type != null) {
			return this.getByGIDWithListTypeFilters(gid, status,
					Collections.<Integer>singletonList(Integer.valueOf(type.getUserDefinedFieldID())));
		}
		return this.getByGIDWithListTypeFilters(gid, status, null);
	}

	/**
	 * Get the names associated with a GID
	 *
	 * @param gid the gid for which we are getting names
	 * @param status the status of the gid. Note if status is null or 0 we will omit deleted values i.e. status will be set to 9
	 * @param type a list of name types to retrieve. Note if type is null or empty it will be omited from the query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Name> getByGIDWithListTypeFilters(final Integer gid, final Integer status, final List<Integer> type) {
		try {
			if (gid != null) {
				final StringBuilder queryString = new StringBuilder();
				queryString.append("SELECT ");
				queryString.append("CASE n.nstat ");
				queryString.append("	WHEN NOT 1 THEN 9999 ");
				queryString.append("	ELSE n.nstat ");
				queryString.append("END AS 'nameOrdering', ");
				queryString.append("{n.*} from names n WHERE n.gid = :gid ");

				if (status != null && status != 0) {
					queryString.append("AND n.nstat = :nstat ");
				} else {
					queryString.append("AND n.nstat != 9 ");
				}

				if (type != null && !type.isEmpty()) {
					queryString.append("AND n.ntype IN (:ntype) ");
				}

				queryString.append("ORDER BY nameOrdering, n.nval");

				final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
				query.addEntity("n", Name.class);
				query.setParameter("gid", gid);

				if (status != null && status != 0) {
					query.setParameter("nstat", status);
				}

				if (type != null && !type.isEmpty()) {
					query.setParameterList("ntype", type);
				}

				return query.list();
			}

		} catch (final HibernateException e) {
			final String message = "Error with getByGIDWithFilters(gid=" + gid + ", status=" + status + ", type=" + type
					+ ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public Name getByGIDAndNval(final Integer gid, final String nval) {
		try {
			if (gid != null) {
				final Criteria crit = this.getSession().createCriteria(Name.class);
				crit.add(Restrictions.eq("germplasmId", gid));
				crit.add(Restrictions.eq("nval", nval));
				final List<Name> names = crit.list();
				if (names.isEmpty()) {
					// return null if no Name objects match
					return null;
				} else {
					// return first result in the case of multiple matches
					return names.get(0);
				}
			}
		} catch (final HibernateException e) {
			final String message = "Error with getByGIDAndNval(gid=" + gid + ", nval=" + nval + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Name> getNamesByNameIds(final List<Integer> nIds) {
		try {
			if (nIds != null && !nIds.isEmpty()) {
				final Criteria crit = this.getSession().createCriteria(Name.class);
				crit.add(Restrictions.in("nid", nIds));
				return crit.list();
			}
		} catch (final HibernateException e) {
			final String message = "Error with getNamesByNameIds(nIds=" + nIds + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public List<Name> getPreferredIdsByListId(final Integer listId) {
		try {
			if (listId != null) {
				final SQLQuery query = this.getSession().createSQLQuery(Name.GET_PREFERRED_IDS_BY_LIST_ID);
				query.setParameter("listId", listId);
				query.addEntity("n", Name.class);
				return query.list();
			}
		} catch (final HibernateException e) {
			final String message = "Error with getPreferredIdsByListId(listId=" + listId + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
		return new ArrayList<>();
	}

	public Name getNameByNameId(final Integer nId) {
		try {
			if (nId != null) {
				final Criteria crit = this.getSession().createCriteria(Name.class);
				crit.add(Restrictions.eq("nid", nId));
				return (Name) crit.uniqueResult();
			}
		} catch (final HibernateException e) {
			final String message = "Error with getNameByNameId(nId=" + nId + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
		return null;
	}

	/**
	 * Retrieves the gId and nId pairs for the given germplasm names
	 *
	 * @param germplasmNames the list of germplasm names
	 * @return the list of GidNidElement (gId and nId pairs) @
	 */
	@SuppressWarnings("rawtypes")
	public List<GermplasmNameDetails> getGermplasmNameDetailsByNames(final List<String> germplasmNames,
			final GetGermplasmByNameModes mode) {
		final List<GermplasmNameDetails> toReturn = new ArrayList<>();

		try {

			if (germplasmNames != null && !germplasmNames.isEmpty()) {

				// Default query if mode = NORMAL, STANDARDIZED, SPACES_REMOVED
				SQLQuery query = this.getSession().createSQLQuery(Name.GET_NAME_DETAILS_BY_NAME);

				if (mode == GetGermplasmByNameModes.SPACES_REMOVED_BOTH_SIDES) {
					query = this.getSession().createSQLQuery(
							"SELECT gid, nid, REPLACE(nval, ' ', '') " + "FROM names " + "WHERE nval IN (:germplasmNameList)");
				}

				query.setParameterList("germplasmNameList", germplasmNames);
				final List results = query.list();

				for (final Object o : results) {
					final Object[] result = (Object[]) o;
					if (result != null) {
						final Integer gId = (Integer) result[0];
						final Integer nId = (Integer) result[1];
						final String nVal = (String) result[2];
						final GermplasmNameDetails element = new GermplasmNameDetails(gId, nId, nVal);
						toReturn.add(element);
					}
				}
			}
		} catch (final HibernateException e) {
			final String message =
					"Error with getGermplasmNameDetailsByNames(germplasmNames=" + germplasmNames + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, String> getPreferredIdsByGIDs(final List<Integer> gids) {
		final Map<Integer, String> toreturn = new HashMap<>();
		for (final Integer gid : gids) {
			toreturn.put(gid, null);
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(Name.GET_PREFERRED_IDS_BY_GIDS);
			query.setParameterList("gids", gids);

			final List<Object> results = query.list();
			for (final Object result : results) {
				final Object[] resultArray = (Object[]) result;
				final Integer gid = (Integer) resultArray[0];
				final String preferredId = (String) resultArray[1];
				toreturn.put(gid, preferredId);
			}
		} catch (final HibernateException e) {
			final String message = "Error with getPreferredIdsByGIDs(gids=" + gids + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}

		return toreturn;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, String> getPreferredNamesByGIDs(final List<Integer> gids) {
		final Map<Integer, String> toreturn = new HashMap<>();
		for (final Integer gid : gids) {
			toreturn.put(gid, null);
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(Name.GET_PREFERRED_NAMES_BY_GIDS);
			query.setParameterList("gids", gids);

			final List<Object> results = query.list();
			for (final Object result : results) {
				final Object[] resultArray = (Object[]) result;
				final Integer gid = (Integer) resultArray[0];
				final String preferredId = (String) resultArray[1];
				toreturn.put(gid, preferredId);
			}
		} catch (final HibernateException e) {
			final String message = "Error with getPreferredNamesByGIDs(gids=" + gids + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}

		return toreturn;
	}

	public Map<Integer, Integer> getPreferredNameIdsByGIDs(final List<Integer> gids) {
		final Map<Integer, Integer> toreturn = new HashMap<>();
		for (final Integer gid : gids) {
			toreturn.put(gid, null);
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(Name.GET_PREFERRED_NAME_IDS_BY_GIDS);
			query.setParameterList("gids", gids);

			final List<Object> results = query.list();
			for (final Object result : results) {
				final Object[] resultArray = (Object[]) result;
				final Integer gid = (Integer) resultArray[0];
				final Integer preferredId = (Integer) resultArray[1];
				toreturn.put(gid, preferredId);
			}
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getPreferredNameIdsByGIDs(gids=" + gids + ") query from Name " + e.getMessage(),
					e);
		}

		return toreturn;
	}

	@SuppressWarnings("unchecked")
	public List<Name> getNamesByGids(final List<Integer> gids) {
		List<Name> toReturn = new ArrayList<>();

		if (gids == null || gids.isEmpty()) {
			return toReturn;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Name.class);
			criteria.add(Restrictions.in("germplasmId", gids));

			toReturn = criteria.list();
		} catch (final HibernateException e) {
			final String message = "Error with getNamesByGids(gids=" + gids + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}

		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getGidsByName(final String name) {
		try {
			final String sql = "SELECT gid FROM names where nval = :name";
			final Query query = this.getSession().createSQLQuery(sql).setParameter("name", name);
			return query.list();

		} catch (final Exception e) {
			final String message = "Error with NameDAO.getGidsByName(" + name + ") " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, List<Name>> getNamesByGidsInMap(final List<Integer> gids) {
		final Map<Integer, List<Name>> map = new HashMap<>();

		if (gids == null || gids.isEmpty()) {
			return map;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Name.class);
			criteria.add(Restrictions.in("germplasmId", gids));

			final List<Name> list = criteria.list();
			if (list == null) {
				return map;
			}
			for (final Name name : list) {
				List<Name> names = map.get(name.getGermplasmId());
				if (names == null) {
					names = new ArrayList<>();
					map.put(name.getGermplasmId(), names);
				}
				names.add(name);
			}

		} catch (final HibernateException e) {
			final String message = "Error with getNamesByGidsInMap(gids=" + gids + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, List<Name>> getNamesByGidsAndNTypeIdsInMap(final List<Integer> gids, final List<Integer> ntypeIds) {
		final Map<Integer, List<Name>> map = new HashMap<>();

		if (gids == null || gids.isEmpty()) {
			return map;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Name.class);
			criteria.add(Restrictions.in("germplasmId", gids));
			criteria.add(Restrictions.in("typeId", ntypeIds));

			final List<Name> list = criteria.list();
			if (list == null) {
				return map;
			}
			for (final Name name : list) {
				List<Name> names = map.get(name.getGermplasmId());
				if (names == null) {
					names = new ArrayList<>();
					map.put(name.getGermplasmId(), names);
				}
				names.add(name);
			}

		} catch (final HibernateException e) {
			final String message = "Error with getNamesByGidsInMap(gids=" + gids + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public boolean checkIfMatches(final String name) {
		try {
			final StringBuilder sql = new StringBuilder();
			sql.append("SELECT COUNT(n.nid) FROM names n ");
			sql.append(" INNER JOIN germplsm g ON g.gid = n.gid ");
			sql.append(" WHERE nval = '").append(name).append("'");
			sql.append(" AND g.deleted = 0 ");

			final Query query = this.getSession().createSQLQuery(sql.toString());
			final List<BigInteger> result = query.list();
			return result.get(0).intValue() > 0;

		} catch (final HibernateException e) {
			final String message = "Error with getAllMatchingNames(" + name + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Map<Integer, String> getSourcePreferredNamesByGids(final List<Integer> gids) {
		final Map<Integer, String> map;

		if (gids == null || gids.isEmpty()) {
			return new HashMap<>();
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(Name.GET_GROUP_SOURCE_PREFERRED_NAME_IDS_BY_GIDS);
			query.setParameterList("gids", gids);

			map = this.createGidAndPreferredNameMap(query.list());

		} catch (final HibernateException e) {
			final String message = "Error with getSourcePreferredNamesByGids(gids=" + gids + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}

		return map;

	}

	public Map<Integer, String> getImmediatePreferredNamesByGids(final List<Integer> gids) {
		final Map<Integer, String> map;

		if (gids == null || gids.isEmpty()) {
			return new HashMap<>();
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(Name.GET_IMMEDIATE_SOURCE_PREFERRED_NAME_IDS_BY_GIDS);
			query.setParameterList("gids", gids);

			map = this.createGidAndPreferredNameMap(query.list());

		} catch (final HibernateException e) {
			final String message = "Error with getImmediatePreferredNamesByGids(gids=" + gids + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
		return map;
	}

	public List<String> getNamesByGidsAndPrefixes(final List<Integer> gids, final List<String> prefixes) {
		try {
			final StringBuilder sql = new StringBuilder();
			sql.append("SELECT nval FROM names WHERE gid IN (:gids) ")
				.append(" AND ( ");

			final int prefixesSize = prefixes.size();
			for (int i=0; i<prefixesSize; i++) {
				String prefix = prefixes.get(i) + "%";
				prefix = prefix.replaceAll("\\s", "");
				sql.append(" nval LIKE '" + prefix + "'");
				if(i+1 != prefixesSize) {
					sql.append(" OR ");
				}
			}
			sql.append(" ) ");

			final Query query = this.getSession().createSQLQuery(sql.toString());
			query.setParameterList("gids", gids);
			return query.list();

		} catch (final HibernateException e) {
			final String message = "Error with getNamesByGidsAndPrefixes(gids=" + gids + ", prefixes=" + prefixes
				+") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}

	}

	@SuppressWarnings("unchecked")
	public List<Name> getNamesByTypeAndGIDList(final Integer nameType, final List<Integer> gidList) {
		List<Name> returnList = new ArrayList<>();
		if (gidList != null && !gidList.isEmpty()) {
			try {
				final String sql = "SELECT {n.*}" + " FROM names n" + " WHERE n.ntype = :nameType" + " AND n.gid in (:gidList)";
				final SQLQuery query = this.getSession().createSQLQuery(sql);
				query.addEntity("n", Name.class);
				query.setParameter("nameType", nameType);
				query.setParameterList("gidList", gidList);
				returnList = query.list();
			} catch (final HibernateException e) {
				throw new MiddlewareQueryException(
						"Error with getNamesByTypeAndGIDList(nameType=" + nameType + ", gidList=" + gidList + "): " + e.getMessage(), e);
			}
		}
		return returnList;
	}

	private Map<Integer, String> createGidAndPreferredNameMap(final List<Object> list) {
		final Map<Integer, String> map = new HashMap<>();

		for (final Object result : list) {
			final Object[] resultArray = (Object[]) result;
			final Integer gid = (Integer) resultArray[0];
			final String name = (String) resultArray[1];
			map.put(gid, name);
		}
		return map;
	}
}
