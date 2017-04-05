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
 		if(type != null) {
 			return getByGIDWithListTypeFilters(gid, status, Collections.<Integer>singletonList(Integer.valueOf(type.getUserDefinedFieldID())));
 		}
 		return getByGIDWithListTypeFilters(gid, status, null);
 	}

 	/**
 	 * Get the names associated with a GID
 	 * @param gid the gid for which we are getting names
 	 * @param status the status of the gid. Note if status is null or 0 we will omit deleted values i.e. status will be set to 9
 	 * @param type a list of name types to retrieve. Note if type is null or empty it will be omited from the query
 	 * @return
 	 * @throws MiddlewareQueryException
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

			/**
			 * List<Criterion> criterions = new ArrayList<Criterion>(); Criterion gidCriterion = Restrictions.eq("germplasmId", gid);
			 * criterions.add(gidCriterion);
			 *
			 * if(status != null && status != 0) { Criterion statusCriterion = Restrictions.eq("nstat", status);
			 * criterions.add(statusCriterion); }
			 *
			 * if(type != null) { Integer typeid = type.getUserDefinedFieldID(); Criterion typeCriterion = Restrictions.eq("type.fldno",
			 * typeid); criterions.add(typeCriterion); }
			 *
			 * List<Name> results = getByCriteria(criterions); return results;
			 **/
		} catch (final HibernateException e) {
			String message = "Error with getByGIDWithFilters(gid=" + gid + ", status=" + status + ", type=" + type
					+ ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
		return new ArrayList<Name>();
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
			String message = "Error with getByGIDAndNval(gid=" + gid + ", nval=" + nval + ") query from Name " + e.getMessage();
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
			String message = "Error with getNamesByNameIds(nIds=" + nIds + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
		return new ArrayList<Name>();
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
			String message = "Error with getPreferredIdsByListId(listId=" + listId + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
		return new ArrayList<Name>();
	}

	public Name getNameByNameId(final Integer nId) {
		try {
			if (nId != null) {
				final Criteria crit = this.getSession().createCriteria(Name.class);
				crit.add(Restrictions.eq("nid", nId));
				return (Name) crit.uniqueResult();
			}
		} catch (final HibernateException e) {
			String message = "Error with getNameByNameId(nId=" + nId + ") query from Name " + e.getMessage();
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
	public List<GermplasmNameDetails> getGermplasmNameDetailsByNames(final List<String> germplasmNames, final GetGermplasmByNameModes mode) {
		final List<GermplasmNameDetails> toReturn = new ArrayList<GermplasmNameDetails>();

		try {

			if (germplasmNames != null && !germplasmNames.isEmpty()) {

				// Default query if mode = NORMAL, STANDARDIZED, SPACES_REMOVED
				SQLQuery query = this.getSession().createSQLQuery(Name.GET_NAME_DETAILS_BY_NAME);

				if (mode == GetGermplasmByNameModes.SPACES_REMOVED_BOTH_SIDES) {
					query =
							this.getSession().createSQLQuery(
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
			String message = "Error with getGermplasmNameDetailsByNames(germplasmNames=" + germplasmNames + ") query from Name "
					+ e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
		return toReturn;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, String> getPrefferedIdsByGIDs(final List<Integer> gids) {
		final Map<Integer, String> toreturn = new HashMap<Integer, String>();
		for (final Integer gid : gids) {
			toreturn.put(gid, null);
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(Name.GET_PREFFERED_IDS_BY_GIDS);
			query.setParameterList("gids", gids);

			final List<Object> results = query.list();
			for (final Object result : results) {
				final Object[] resultArray = (Object[]) result;
				final Integer gid = (Integer) resultArray[0];
				final String preferredId = (String) resultArray[1];
				toreturn.put(gid, preferredId);
			}
		} catch (final HibernateException e) {
			String message = "Error with getPrefferedIdsByGIDs(gids=" + gids + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}

		return toreturn;
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, String> getPrefferedNamesByGIDs(final List<Integer> gids) {
		final Map<Integer, String> toreturn = new HashMap<Integer, String>();
		for (final Integer gid : gids) {
			toreturn.put(gid, null);
		}

		try {
			final SQLQuery query = this.getSession().createSQLQuery(Name.GET_PREFFERED_NAMES_BY_GIDS);
			query.setParameterList("gids", gids);

			final List<Object> results = query.list();
			for (final Object result : results) {
				final Object[] resultArray = (Object[]) result;
				final Integer gid = (Integer) resultArray[0];
				final String preferredId = (String) resultArray[1];
				toreturn.put(gid, preferredId);
			}
		} catch (final HibernateException e) {
			String message = "Error with getPrefferedNamesByGIDs(gids=" + gids + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}

		return toreturn;
	}

	@SuppressWarnings("unchecked")
	public List<Name> getNamesByGids(final List<Integer> gids) {
		List<Name> toReturn = new ArrayList<Name>();

		if (gids == null || gids.isEmpty()) {
			return toReturn;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Name.class);
			criteria.add(Restrictions.in("germplasmId", gids));

			toReturn = criteria.list();
		} catch (final HibernateException e) {
			String message = "Error with getNamesByGids(gids=" + gids + ") query from Name " + e.getMessage();
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
			String message = "Error with NameDAO.getGidsByName(" + name + ") " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, List<Name>> getNamesByGidsInMap(final List<Integer> gids) {
		final Map<Integer, List<Name>> map = new HashMap<Integer, List<Name>>();

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
					names = new ArrayList<Name>();
					map.put(name.getGermplasmId(), names);
				}
				names.add(name);
			}

		} catch (final HibernateException e) {
			String message = "Error with getNamesByGidsInMap(gids=" + gids + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}

		return map;
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer, List<Name>> getNamesByGidsAndNTypeIdsInMap(final List<Integer> gids, final List<Integer> ntypeIds) {
		final Map<Integer, List<Name>> map = new HashMap<Integer, List<Name>>();

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
					names = new ArrayList<Name>();
					map.put(name.getGermplasmId(), names);
				}
				names.add(name);
			}

		} catch (final HibernateException e) {
			String message = "Error with getNamesByGidsInMap(gids=" + gids + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}

		return map;
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllMatchingNames(final String prefix, final String suffix) {
		try {
			String keyword1 = prefix + "%" + suffix + "%";
			String keyword2 =
					GermplasmDataManagerUtil.standardizeName(prefix) + "%" + GermplasmDataManagerUtil.standardizeName(suffix) + "%";
			keyword1 = keyword1.replaceAll("\\s", "");
			keyword2 = keyword2.replaceAll("\\s", "");
			final StringBuilder sql = new StringBuilder();
			sql.append("SELECT nval FROM names ").append(" WHERE (REPLACE(nval, ' ', '') LIKE '").append(keyword1).append("'")
					.append(" OR REPLACE(nval, ' ', '') LIKE '").append(keyword2).append("')");

			final Query query = this.getSession().createSQLQuery(sql.toString());
			return query.list();

		} catch (final HibernateException e) {
			String message = "Error with getAllMatchingNames(" + prefix + ", " + suffix + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public boolean checkIfMatches(final String name) {
		try {
			final StringBuilder sql = new StringBuilder();
			sql.append("SELECT COUNT(nid) FROM names ");
			sql.append(" WHERE nval = '").append(name).append("'");

			final Query query = this.getSession().createSQLQuery(sql.toString());
			final List<BigInteger> result = query.list();
			return result.get(0).intValue() > 0;

		} catch (final HibernateException e) {
			String message = "Error with getAllMatchingNames(" + name + ") query from Name " + e.getMessage();
			NameDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, Integer> getCountByNamePermutations(final List<String> names) {

		final Monitor getCountByNamePermutations = MonitorFactory.start("Method Started : getCountByNamePermutations ");

		if (names == null || names.isEmpty()) {
			return new HashMap<>();
		}

		final Map<String, Integer> mapCountWithName = new HashMap<>();
		final Map<String, String> mapPermutationValue = new HashMap<>();

		// Converting supplied value to combination of names that can exists in names
		for (final String name : names) {
			mapCountWithName.put(name, 0);
			final List<String> permutations = GermplasmDataManagerUtil.createNamePermutations(name);
			mapPermutationValue.put(permutations.get(0), name);
			mapPermutationValue.put(permutations.get(1), name);
			mapPermutationValue.put(permutations.get(2), name);
		}

		final List<String> allDesignationValues = new ArrayList<>(mapPermutationValue.keySet());

		final Integer total = allDesignationValues.size();

		final Integer totalBatches = total / 1000;

		NameDAO.LOG.debug("Total batch to germplasm designations are {}", totalBatches + 1);

		final List<Object[]> allNameList = new ArrayList<>();

		for (Integer b = 0; b <= totalBatches; b++) {

			NameDAO.LOG.debug("Processing batch {}/{}", b + 1, totalBatches + 1);

			Integer start = b * 1000;

			if (start > total) {
				start = total - 1;
			}

			Integer end = (b + 1) * 1000;

			if (end > total) {
				end = total;
			}

			final List<String> batchDesignationValues = allDesignationValues.subList(start, end);

			// Count using = by default
			final SQLQuery query = this.getSession().createSQLQuery(
				"select n.* FROM names n inner join germplsm g on g.gid = n.gid where nval in (:namelist) and  g.deleted = 0  and g.grplce = 0");
			query.setParameterList("namelist", batchDesignationValues);
			allNameList.addAll(query.list());
		}

		for (final Object[] row : allNameList) {
			final String originalName = mapPermutationValue.get(row[5]);
			mapCountWithName.put(originalName, mapCountWithName.get(originalName) + 1);
		}

		NameDAO.LOG.debug("Method End : getCountByNamePermutations " + getCountByNamePermutations.stop());
		return mapCountWithName;
	}

}
