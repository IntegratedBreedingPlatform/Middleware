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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link Germplasm}.
 *
 */
public class GermplasmDAO extends GenericDAO<Germplasm, Integer> {

	private static final String STATUS_DELETED = "9";
	private static final String STOCK_IDS = "stockIDs";
	private static final String INVENTORY_ID = "inventoryID";
	private static final String GERMPLSM = "germplsm";
	private static final String Q_NO_SPACES = "qNoSpaces";
	private static final String Q_STANDARDIZED = "qStandardized";
	private static final String AVAIL_INV = "availInv";
	private static final String SEED_RES = "seedRes";
	private static final String GID = "gid";
	private static final String GRPLCE = "grplce";
	private static final String CLASS_NAME_GERMPLASM = "Germplasm";

	@Override
	public Germplasm getById(final Integer gid, final boolean lock) throws MiddlewareQueryException {
		return this.getById(gid);
	}

	@Override
	public Germplasm getById(final Integer gid) throws MiddlewareQueryException {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT g.* FROM germplsm g WHERE gid!=grplce AND gid=:gid LIMIT 1");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("gid", gid);
			query.addEntity("g", Germplasm.class);

			return (Germplasm) query.uniqueResult();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getById(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByNamePermutations(final String name, final Operation operation, final int start, final int numOfRows)
			throws MiddlewareQueryException {

		// Converting supplied value to combination of names that can exists in names
		final List<String> names = GermplasmDataManagerUtil.createNamePermutations(name);

		if (names == null || names.isEmpty()) {
			return new ArrayList<>();
		}

		try {

			final String originalName = names.get(0);
			final String standardizedName = names.get(1);
			final String noSpaceName = names.get(2);

			// Search using = by default
			SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_BY_NAME_ALL_MODES_USING_EQUAL);
			if (operation == Operation.LIKE) {
				query = this.getSession().createSQLQuery(Germplasm.GET_BY_NAME_ALL_MODES_USING_LIKE);
			}

			// Set the parameters
			query.setParameter("name", originalName);
			query.setParameter("noSpaceName", noSpaceName);
			query.setParameter("standardizedName", standardizedName);

			query.addEntity("g", Germplasm.class);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with getByName(names=" + names + ") query from Germplasm: " + e.getMessage(), e);
		}
	}

	public long countByNamePermutations(final String name, final Operation operation) throws MiddlewareQueryException {

		// Converting supplied value to combination of names that can exists in names
		final List<String> names = GermplasmDataManagerUtil.createNamePermutations(name);

		if (names == null || names.isEmpty()) {
			return 0;
		}

		try {
			final String originalName = names.get(0);
			final String standardizedName = names.get(1);
			final String noSpaceName = names.get(2);

			// Count using = by default
			SQLQuery query = this.getSession().createSQLQuery(Germplasm.COUNT_BY_NAME_ALL_MODES_USING_EQUAL);
			if (operation == Operation.LIKE) {
				query = this.getSession().createSQLQuery(Germplasm.COUNT_BY_NAME_ALL_MODES_USING_LIKE);
			}

			// Set the parameters
			query.setParameter("name", originalName);
			query.setParameter("noSpaceName", noSpaceName);
			query.setParameter("standardizedName", standardizedName);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error with countByName(names=" + names + ") query from Germplasm: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByMethodNameUsingEqual(final String name, final int start, final int numOfRows)
			throws MiddlewareQueryException {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.GET_BY_METHOD_NAME_USING_EQUAL);
			query.setParameter("name", name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByMethodNameUsingEqual(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	public long countByMethodNameUsingEqual(final String name) throws MiddlewareQueryException {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.COUNT_BY_METHOD_NAME_USING_EQUAL);
			query.setParameter("name", name);
			return ((Long) query.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with countByMethodNameUsingEqual(name=" + name + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByMethodNameUsingLike(final String name, final int start, final int numOfRows)
			throws MiddlewareQueryException {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.GET_BY_METHOD_NAME_USING_LIKE);
			query.setParameter("name", name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByMethodNameUsingLike(name=" + name + ") query from Germplasm: " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	public long countByMethodNameUsingLike(final String name) throws MiddlewareQueryException {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.COUNT_BY_METHOD_NAME_USING_LIKE);
			query.setParameter("name", name);
			return ((Long) query.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with countByMethodNameUsingLike(name=" + name + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByLocationNameUsingEqual(final String name, final int start, final int numOfRows)
			throws MiddlewareQueryException {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.GET_BY_LOCATION_NAME_USING_EQUAL);
			query.setParameter("name", name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByLocationNameUsingEqual(name=" + name + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return new ArrayList<Germplasm>();
	}

	public long countByLocationNameUsingEqual(final String name) throws MiddlewareQueryException {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.COUNT_BY_LOCATION_NAME_USING_EQUAL);
			query.setParameter("name", name);
			return ((Long) query.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with countByLocationNameUsingEqual(name=" + name + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByLocationNameUsingLike(final String name, final int start, final int numOfRows)
			throws MiddlewareQueryException {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.GET_BY_LOCATION_NAME_USING_LIKE);
			query.setParameter("name", name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByLocationNameUsingLike(name=" + name + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return new ArrayList<Germplasm>();
	}

	public long countByLocationNameUsingLike(final String name) throws MiddlewareQueryException {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.COUNT_BY_LOCATION_NAME_USING_LIKE);
			query.setParameter("name", name);
			return ((Long) query.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with countByLocationNameUsingLike(name=" + name + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return 0;
	}

	@SuppressWarnings("rawtypes")
	public Germplasm getByGIDWithPrefName(final Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_BY_GID_WITH_PREF_NAME);
				query.addEntity("g", Germplasm.class);
				query.addEntity("n", Name.class);
				query.setParameter("gid", gid);
				final List results = query.list();

				if (!results.isEmpty()) {
					final Object[] result = (Object[]) results.get(0);
					if (result != null) {
						final Germplasm germplasm = (Germplasm) result[0];
						final Name prefName = (Name) result[1];
						germplasm.setPreferredName(prefName);
						return germplasm;
					}
				}
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByGIDWithPrefName(gid=" + gid + ") from Germplasm: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Germplasm getByGIDWithPrefAbbrev(final Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_BY_GID_WITH_PREF_ABBREV);
				query.addEntity("g", Germplasm.class);
				query.addEntity("n", Name.class);
				query.addEntity("abbrev", Name.class);
				query.setParameter("gid", gid);
				final List results = query.list();

				if (results.isEmpty()) {
					return null;
				}
				final Object[] result = (Object[]) results.get(0);
				final Germplasm germplasm = (Germplasm) result[0];
				final Name prefName = (Name) result[1];
				final Name prefAbbrev = (Name) result[2];
				germplasm.setPreferredName(prefName);
				if (prefAbbrev != null) {
					germplasm.setPreferredAbbreviation(prefAbbrev.getNval());
				}
				return germplasm;
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByGIDWithPrefAbbrev(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getProgenitorsByGIDWithPrefName(final Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				final List<Germplasm> progenitors = new ArrayList<Germplasm>();

				final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_PROGENITORS_BY_GID_WITH_PREF_NAME);
				query.addEntity("g", Germplasm.class);
				query.addEntity("n", Name.class);
				query.setParameter("gid", gid);
				final List<Object[]> results = query.list();
				for (final Object[] result : results) {
					final Germplasm germplasm = (Germplasm) result[0];
					final Name prefName = (Name) result[1];
					germplasm.setPreferredName(prefName);
					progenitors.add(germplasm);
				}

				return progenitors;
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getProgenitorsByGIDWithPrefName(gid=" + gid + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return new ArrayList<Germplasm>();
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getGermplasmDescendantByGID(final Integer gid, final int start, final int numOfRows)
			throws MiddlewareQueryException {
		try {
			if (gid != null) {
				final Query query = this.getSession().getNamedQuery(Germplasm.GET_DESCENDANTS);
				query.setParameter("gid", gid);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getGermplasmDescendantByGID(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	public Germplasm getProgenitorByGID(final Integer gid, final Integer proNo) throws MiddlewareQueryException {
		try {
			if (gid != null & proNo != null) {
				String progenitorQuery = "";
				if (proNo == 1) {
					progenitorQuery = Germplasm.GET_PROGENITOR1;
				} else if (proNo == 2) {
					progenitorQuery = Germplasm.GET_PROGENITOR2;
				} else if (proNo > 2) {
					progenitorQuery = Germplasm.GET_PROGENITOR;
				}

				final Query query = this.getSession().getNamedQuery(progenitorQuery);
				query.setParameter("gid", gid);

				if (proNo > 2) {
					query.setParameter("pno", proNo);
				}

				return (Germplasm) query.uniqueResult();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getProgenitorByGID(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return null;
	}

	public long countGermplasmDescendantByGID(final Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				final Query query = this.getSession().createSQLQuery(Germplasm.COUNT_DESCENDANTS);
				query.setParameter("gid", gid);
				return ((BigInteger) query.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with countGermplasmDescendantByGID(gid=" + gid + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return 0;
	}

	public List<Germplasm> getManagementNeighbors(final Integer gid, final int start, final int numOfRows) throws MiddlewareQueryException {
		final List<Germplasm> toreturn = new ArrayList<Germplasm>();
		try {
			if (gid != null) {
				final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_MANAGEMENT_NEIGHBORS);
				query.addEntity("g", Germplasm.class);
				query.addEntity("n", Name.class);
				query.setParameter("gid", gid);

				query.setFirstResult(start);
				query.setMaxResults(numOfRows);

				for (final Object resultObject : query.list()) {
					final Object[] result = (Object[]) resultObject;
					final Germplasm germplasm = (Germplasm) result[0];
					final Name prefName = (Name) result[1];
					germplasm.setPreferredName(prefName);
					toreturn.add(germplasm);
				}
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getManagementNeighbors(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return toreturn;
	}

	public long countManagementNeighbors(final Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				final SQLQuery query = this.getSession().createSQLQuery(Germplasm.COUNT_MANAGEMENT_NEIGHBORS);
				query.setParameter("gid", gid);
				final BigInteger count = (BigInteger) query.uniqueResult();
				return count.longValue();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with countManagementNeighbors(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return 0;
	}

	public long countGroupRelatives(final Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				final SQLQuery query = this.getSession().createSQLQuery(Germplasm.COUNT_GROUP_RELATIVES);
				query.setParameter("gid", gid);
				final BigInteger count = (BigInteger) query.uniqueResult();
				return count.longValue();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with countGroupRelatives(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return 0;
	}

	public List<Germplasm> getGroupRelatives(final Integer gid, final int start, final int numRows) throws MiddlewareQueryException {
		final List<Germplasm> toreturn = new ArrayList<Germplasm>();
		try {
			if (gid != null) {
				final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_GROUP_RELATIVES);
				query.addEntity("g", Germplasm.class);
				query.addEntity("n", Name.class);
				query.setParameter("gid", gid);

				query.setFirstResult(start);
				query.setMaxResults(numRows);

				for (final Object resultObject : query.list()) {
					final Object[] result = (Object[]) resultObject;
					final Germplasm germplasm = (Germplasm) result[0];
					final Name prefName = (Name) result[1];
					germplasm.setPreferredName(prefName);
					toreturn.add(germplasm);
				}
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getGroupRelatives(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return toreturn;
	}

	public List<Germplasm> getChildren(final Integer gid, final char methodType) throws MiddlewareQueryException {
		final List<Germplasm> toreturn = new ArrayList<Germplasm>();
		try {
			final String queryString = methodType == 'D' ? Germplasm.GET_DERIVATIVE_CHILDREN : Germplasm.GET_MAINTENANCE_CHILDREN;
			final SQLQuery query = this.getSession().createSQLQuery(queryString);
			query.addEntity("g", Germplasm.class);
			query.addEntity("n", Name.class);
			query.setParameter("gid", gid);

			for (final Object resultObject : query.list()) {
				final Object[] result = (Object[]) resultObject;
				final Germplasm germplasm = (Germplasm) result[0];
				final Name prefName = (Name) result[1];
				germplasm.setPreferredName(prefName);
				toreturn.add(germplasm);
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getDerivativeChildren(gid=" + gid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return toreturn;

	}

	public String getNextSequenceNumberForCrossName(final String prefix) throws MiddlewareQueryException {
		String nextInSequence = "1";

		try {
			/*
			 * This query will generate next number will be queried on first "word" of cross name.
			 *
			 * eg. input prefix: "IR" output: next number in "IRNNNNN -----" sequence
			 */

			final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_NEXT_IN_SEQUENCE_FOR_CROSS_NAME_PREFIX3);
			query.setParameter("prefix", prefix);
			query.setParameter("prefixLike", prefix + "%");

			/*
			 * If the next number will be queried on second "word" of cross name. IMPORTANT: assumes that passed in prefix value has a
			 * whitespace at the end
			 *
			 * eg. input prefix: "IR " output: next number in "IR NNNNN..." sequence
			 */

			final BigInteger nextNumberInSequence = (BigInteger) query.uniqueResult();

			if (nextNumberInSequence != null) {
				nextInSequence = String.valueOf(nextNumberInSequence);
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getNextSequenceNumberForCrossName(prefix=" + prefix + ") " + "query : " + e.getMessage(),
					e);
		}

		return nextInSequence;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByLocationId(final String name, final int locationID) throws MiddlewareQueryException {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE ");
			queryString.append("n.nval = :name ");
			queryString.append("AND g.glocn = :locationID ");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("name", name);
			query.setParameter("locationID", locationID);
			query.addEntity("g", Germplasm.class);

			return query.list();

		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error with getByLocationId(name=" + name + ", locationID=" + locationID + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return new ArrayList<Germplasm>();
	}

	@SuppressWarnings("rawtypes")
	public Germplasm getByGIDWithMethodType(final Integer gid) throws MiddlewareQueryException {
		try {
			if (gid != null) {
				final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_BY_GID_WITH_METHOD_TYPE);
				query.addEntity("g", Germplasm.class);
				query.addEntity("m", Method.class);
				query.setParameter("gid", gid);
				final List results = query.list();

				if (!results.isEmpty()) {
					final Object[] result = (Object[]) results.get(0);
					if (result != null) {
						final Germplasm germplasm = (Germplasm) result[0];
						final Method method = (Method) result[1];
						germplasm.setMethod(method);
						return germplasm;
					}
				}
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByGIDWithMethodType(gid=" + gid + ") from Germplasm: " + e.getMessage(), e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByGIDRange(final int startGID, final int endGID) throws MiddlewareQueryException {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT {g.*} FROM germplsm g WHERE ");
			queryString.append("g.gid >= :startGID ");
			queryString.append("AND g.gid <= :endGID ");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("startGID", startGID);
			query.setParameter("endGID", endGID);
			query.addEntity("g", Germplasm.class);

			return query.list();

		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error with getByGIDRange(startGID=" + startGID + ", endGID=" + endGID + ") query from Germplasm: " + e.getMessage(),
					e);
		}
		return new ArrayList<Germplasm>();
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByGIDList(final List<Integer> gids) throws MiddlewareQueryException {

		if (gids.isEmpty()) {
			return new ArrayList<Germplasm>();
		}

		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT {g.*} FROM germplsm g WHERE ");
			queryString.append("g.gid IN( :gids )");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("gids", gids);
			query.addEntity("g", Germplasm.class);

			return query.list();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByGIDList(gids=" + gids.toString() + ") query from Germplasm: " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	/**
	 * Search for germplasms given a search term
	 *
	 * @param searchedString - the search term to be used
	 * @param o - like or equal
	 * @param includeParents boolean flag to denote whether parents will be included in search results
	 * @param withInventoryOnly - boolean flag to denote whether result will be filtered by those with inventories only
	 * @return List of Germplasms
	 * @throws MiddlewareQueryException
	 */
	public List<Germplasm> searchForGermplasms(final String searchedString, final Operation o, final boolean includeParents,
			final boolean withInventoryOnly) throws MiddlewareQueryException {
		final String q = searchedString.trim();
		if ("".equals(q)) {
			return new ArrayList<Germplasm>();
		}
		try {

			final Set<Germplasm> result = new LinkedHashSet<Germplasm>();
			final Set<Germplasm> resultParents = new LinkedHashSet<Germplasm>();

			final String additionalQuery = withInventoryOnly ? Germplasm.WHERE_WITH_INVENTORY : "";

			// find germplasms with GID = or like q
			if (q.matches("(-)?(%)?[(\\d+)(%|_)?]*(%)?")) {
				SQLQuery p1Query;
				if (o.equals(Operation.LIKE)) {
					p1Query = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_GID_LIKE + additionalQuery);
					p1Query.setParameter("gid", q);
				} else {
					p1Query = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_GID + additionalQuery);
					p1Query.setParameter("gidLength", q.length());
					p1Query.setParameter("gid", q);
				}

				p1Query.addEntity(GermplasmDAO.GERMPLSM, Germplasm.class);
				this.addInventoryInfo(p1Query);
				result.addAll(this.getSearchForGermplasmsResult(p1Query.list()));
			}
			// find germplasms with inventory_id = or like q
			result.addAll(this.searchForGermplasmsByInventoryId(q, o, additionalQuery));

			// find germplasms with nVal = or like q
			SQLQuery p2Query;
			if (o.equals(Operation.LIKE)) {
				p2Query = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_GERMPLASM_NAME_LIKE + additionalQuery);
			} else {
				p2Query = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_GERMPLASM_NAME + additionalQuery);
			}
			p2Query.setParameter("q", q);
			p2Query.setParameter(GermplasmDAO.Q_NO_SPACES, q.replaceAll(" ", ""));
			p2Query.setParameter(GermplasmDAO.Q_STANDARDIZED, GermplasmDataManagerUtil.standardizeName(q));
			p2Query.setParameter("deletedStatus", GermplasmDAO.STATUS_DELETED);
			p2Query.addEntity(GermplasmDAO.GERMPLSM, Germplasm.class);
			this.addInventoryInfo(p2Query);
			result.addAll(this.getSearchForGermplasmsResult(p2Query.list()));

			// Add parents to results if specified by "includeParents" flag
			if (includeParents) {
				for (final Germplasm g : result) {
					final List<Integer> parentGids = new ArrayList<Integer>();
					if (g.getGpid1() != null && g.getGpid1() != 0) {
						parentGids.add(g.getGpid1());
					}
					if (g.getGpid2() != null && g.getGpid2() != 0) {
						parentGids.add(g.getGpid2());
					}

					if (!parentGids.isEmpty()) {
						final SQLQuery pQuery = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_GIDS);
						pQuery.setParameterList("gids", parentGids);
						pQuery.addEntity(GermplasmDAO.GERMPLSM, Germplasm.class);
						this.addInventoryInfo(pQuery);
						resultParents.addAll(this.getSearchForGermplasmsResult(pQuery.list()));
					}
				}

				result.addAll(resultParents);
			}

			return new ArrayList<Germplasm>(result);

		} catch (final Exception e) {
			this.logAndThrowException("Error with searchGermplasms(" + q + ") " + e.getMessage(), e);
		}
		return new ArrayList<Germplasm>();
	}

	private void addInventoryInfo(final SQLQuery query) {
		query.addScalar(GermplasmDAO.STOCK_IDS);
		query.addScalar(GermplasmDAO.AVAIL_INV);
		query.addScalar(GermplasmDAO.SEED_RES);
	}

	private List<Germplasm> getSearchForGermplasmsResult(final List<Object[]> result) {
		final List<Germplasm> germplasms = new ArrayList<Germplasm>();
		if (result != null) {
			for (final Object[] row : result) {
				germplasms.add(this.mapToGermplasm(row));
			}
		}
		return germplasms;
	}

	private Germplasm mapToGermplasm(final Object[] row) {
		final Germplasm germplasm = (Germplasm) row[0];
		final GermplasmInventory inventoryInfo = new GermplasmInventory(germplasm.getGid());
		inventoryInfo.setStockIDs((String) row[1]);
		inventoryInfo.setActualInventoryLotCount(row[2] != null ? ((BigInteger) row[2]).intValue() : 0);
		inventoryInfo.setReservedLotCount(row[3] != null ? ((BigInteger) row[3]).intValue() : 0);
		germplasm.setInventoryInfo(inventoryInfo);
		return germplasm;
	}

	/**
	 * @param q - inventory / stock id to be searched
	 * @param o - operation (like, equal)
	 * @return list of germplasms
	 */
	@SuppressWarnings("unchecked")
	protected List<Germplasm> searchForGermplasmsByInventoryId(final String q, final Operation o, final String additionalQuery) {
		SQLQuery p1Query;
		if (o.equals(Operation.LIKE)) {
			p1Query = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_INVENTORY_ID_LIKE + additionalQuery);
		} else {
			p1Query = this.getSession().createSQLQuery(Germplasm.SEARCH_GERMPLASM_BY_INVENTORY_ID + additionalQuery);
		}
		p1Query.setParameter(GermplasmDAO.INVENTORY_ID, q);
		p1Query.addEntity(GermplasmDAO.GERMPLSM, Germplasm.class);
		this.addInventoryInfo(p1Query);
		return this.getSearchForGermplasmsResult(p1Query.list());
	}

	public Map<Integer, Integer> getGermplasmDatesByGids(final List<Integer> gids) {
		final Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
		final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_GERMPLASM_DATES_BY_GIDS);
		query.setParameterList("gids", gids);
		@SuppressWarnings("rawtypes")
		final List results = query.list();
		for (final Object result : results) {
			final Object[] resultArray = (Object[]) result;
			final Integer gid = (Integer) resultArray[0];
			final Integer gdate = (Integer) resultArray[1];
			resultMap.put(gid, gdate);
		}
		return resultMap;
	}

	public Map<Integer, Integer> getMethodIdsByGids(final List<Integer> gids) {
		final Map<Integer, Integer> resultMap = new HashMap<Integer, Integer>();
		final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_METHOD_IDS_BY_GIDS);
		query.setParameterList("gids", gids);
		@SuppressWarnings("rawtypes")
		final List results = query.list();
		for (final Object result : results) {
			final Object[] resultArray = (Object[]) result;
			final Integer gid = (Integer) resultArray[0];
			final Integer methodId = (Integer) resultArray[1];
			resultMap.put(gid, methodId);
		}
		return resultMap;
	}

	/**
	 * Returns a Map with the names of parental germplasm for a given study. These names are returned in a Map, where the key is the
	 * germplasm identifier (gid) and the value is a list with all the names ({@link Name}) for such germplasm. This method optimizes data
	 * returned, because in a study is common that many entries have common parents, so those duplicated parents are omitted in returned
	 * Map.
	 *
	 * @param studyId The ID of the study from which we need to get parents information. Usually this is the ID of a crossing block.
	 * @return
	 */
	public Map<Integer, Map<GermplasmNameType, Name>> getGermplasmParentNamesForStudy(final int studyId) {

		final SQLQuery queryNames = this.getSession().createSQLQuery(Germplasm.GET_PARENT_NAMES_BY_STUDY_ID);
		queryNames.setParameter("projId", studyId);

		final List resultNames = queryNames.list();

		Name name;
		final Map<Integer, Map<GermplasmNameType, Name>> names = new HashMap<>();
		int i = 0;

		for (final Object result : resultNames) {
			i++;
			final Object resultArray[] = (Object[]) result;
			final Integer gid = Integer.valueOf(resultArray[0].toString());
			final Integer ntype = Integer.valueOf(resultArray[1].toString());
			final String nval = resultArray[2].toString();
			final Integer nid = Integer.valueOf(resultArray[3].toString());
			final Integer nstat = Integer.valueOf(resultArray[4].toString());

			name = new Name(nid);
			name.setGermplasmId(gid);
			name.setNval(nval);
			name.setTypeId(ntype);
			name.setNstat(nstat);

			if (!names.containsKey(gid)) {
				names.put(gid, new HashMap<GermplasmNameType, Name>());
			}

			GermplasmNameType type = GermplasmNameType.valueOf(name.getTypeId());
			if (type == null) {
				type = GermplasmNameType.UNRESOLVED_NAME;
			}

			if (!names.get(gid).containsKey(type) || names.get(gid).get(type).getNstat() != 1) {
				names.get(gid).put(type, name);
			}

		}

		return names;
	}

	public List<Germplasm> getGermplasmParentsForStudy(final int studyId) {
		final SQLQuery queryGermplasms = this.getSession().createSQLQuery(Germplasm.GET_PARENT_GIDS_BY_STUDY_ID);
		queryGermplasms.setParameter("projId", studyId);

		final List<Germplasm> germplasms = new ArrayList<>();
		Germplasm g;

		final List resultGermplasms = queryGermplasms.list();
		for (final Object result : resultGermplasms) {
			final Object resultArray[] = (Object[]) result;
			g = new Germplasm(Integer.valueOf(resultArray[0].toString()));
			g.setGpid1(Integer.valueOf(resultArray[1].toString()));
			g.setGpid2(Integer.valueOf(resultArray[2].toString()));
			g.setGrplce(Integer.valueOf(resultArray[3].toString()));

			germplasms.add(g);
		}
		return germplasms;
	}

	public Germplasm getByLGid(final Integer lgid) throws MiddlewareQueryException {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT g.* FROM germplsm g WHERE gid!=grplce AND lgid=:lgid LIMIT 1");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("lgid", lgid);
			query.addEntity("g", Germplasm.class);

			return (Germplasm) query.uniqueResult();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getByLGid(lgid=" + lgid + ") query from Germplasm: " + e.getMessage(), e);
		}
		return null;
	}

	public List<Integer> getExistingGIDs(final List<Integer> ids) throws MiddlewareQueryException {
		List<Integer> gids = new ArrayList<Integer>();

		if (ids == null || ids.isEmpty()) {
			return gids;
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Germplasm.class);
			criteria.add(Restrictions.in(GermplasmDAO.GID, ids));
			criteria.add(Restrictions.neProperty(GermplasmDAO.GID, GermplasmDAO.GRPLCE));
			criteria.setProjection(Projections.property(GermplasmDAO.GID));
			gids = criteria.list();
		} catch (final HibernateException e) {
			this.logAndThrowException(this.getLogExceptionMessage("getByIdsAndProgramUUID", StringUtils.join(ids, ","), null,
					e.getMessage(), GermplasmDAO.CLASS_NAME_GERMPLASM), e);
		}
		return gids;
	}

}
