/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p>
 * Generation Challenge Programme (GCP)
 * <p>
 * <p>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.generationcp.middleware.domain.gms.search.GermplasmSearchParameter;
import org.generationcp.middleware.domain.gms.search.GermplasmSortableColumn;
import org.generationcp.middleware.domain.inventory.GermplasmInventory;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * DAO class for {@link Germplasm}.
 *
 */
public class GermplasmDAO extends GenericDAO<Germplasm, Integer> {


	private static final String GRPLCE = "grplce";
	private static final String DELETED = "deleted";
	private static final String QUERY_FROM_GERMPLASM = ") query from Germplasm: ";

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmDAO.class);


	@Override
	public Germplasm getById(final Integer gid, final boolean lock)  {
		return this.getById(gid);
	}

	@Override
	public Germplasm getById(final Integer gid) {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT g.* FROM germplsm g WHERE g.deleted = 0 AND gid=:gid LIMIT 1");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("gid", gid);
			query.addEntity("g", Germplasm.class);

			return (Germplasm) query.uniqueResult();

		} catch (final HibernateException e) {
			final String errorMessage = "Error with getById(gid=" + gid + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByNamePermutations(final String name, final Operation operation, final int start, final int numOfRows) {

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
			final String errorMessage = "Error with getByName(names=" + names + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countByNamePermutations(final String name, final Operation operation) {

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
			final String errorMessage = "Error with countByName(names=" + names + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countMatchGermplasmInList(Set<Integer> gids) {

		if (gids == null || gids.isEmpty()) {
			return 0;
		}

		try {

			final Query query = this.getSession().getNamedQuery(Germplasm.COUNT_MATCH_GERMPLASM_IN_LIST);
			query.setParameterList("gids", gids);
			return ((Long) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage = "Error with countMatchGermplasmInList(gids) query from Germplasm: " + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}

	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByMethodNameUsingEqual(final String name, final int start, final int numOfRows) {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.GET_BY_METHOD_NAME_USING_EQUAL);
			query.setParameter("name", name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getByMethodNameUsingEqual(name=" + name + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countByMethodNameUsingEqual(final String name) {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.COUNT_BY_METHOD_NAME_USING_EQUAL);
			query.setParameter("name", name);
			return ((Long) query.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countByMethodNameUsingEqual(name=" + name + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByMethodNameUsingLike(final String name, final int start, final int numOfRows) {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.GET_BY_METHOD_NAME_USING_LIKE);
			query.setParameter("name", name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getByMethodNameUsingLike(name=" + name + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countByMethodNameUsingLike(final String name) {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.COUNT_BY_METHOD_NAME_USING_LIKE);
			query.setParameter("name", name);
			return ((Long) query.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countByMethodNameUsingLike(name=" + name + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByLocationNameUsingEqual(final String name, final int start, final int numOfRows) {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.GET_BY_LOCATION_NAME_USING_EQUAL);
			query.setParameter("name", name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getByLocationNameUsingEqual(name=" + name + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countByLocationNameUsingEqual(final String name) {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.COUNT_BY_LOCATION_NAME_USING_EQUAL);
			query.setParameter("name", name);
			return ((Long) query.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countByLocationNameUsingEqual(name=" + name + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByLocationNameUsingLike(final String name, final int start, final int numOfRows) {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.GET_BY_LOCATION_NAME_USING_LIKE);
			query.setParameter("name", name);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);

			return query.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getByLocationNameUsingLike(name=" + name + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countByLocationNameUsingLike(final String name) {
		try {
			final Query query = this.getSession().getNamedQuery(Germplasm.COUNT_BY_LOCATION_NAME_USING_LIKE);
			query.setParameter("name", name);
			return ((Long) query.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countByLocationNameUsingLike(name=" + name + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("rawtypes")
	public Germplasm getByGIDWithPrefName(final Integer gid) {
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
			final String errorMessage = "Error with getByGIDWithPrefName(gid=" + gid + ") from Germplasm: " + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Germplasm getByGIDWithPrefAbbrev(final Integer gid) {
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
			final String errorMessage = "Error with getByGIDWithPrefAbbrev(gid=" + gid + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getProgenitorsByGIDWithPrefName(final Integer gid) {
		try {
			if (gid != null) {
				final List<Germplasm> progenitors = new ArrayList<>();

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
			final String errorMessage = "Error with getProgenitorsByGIDWithPrefName(gid=" + gid + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return new ArrayList<>();
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getGermplasmDescendantByGID(final Integer gid, final int start, final int numOfRows) {
		try {
			if (gid != null) {
				final Query query = this.getSession().getNamedQuery(Germplasm.GET_DESCENDANTS);
				query.setParameter("gid", gid);
				query.setFirstResult(start);
				query.setMaxResults(numOfRows);
				return query.list();
			}
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getGermplasmDescendantByGID(gid=" + gid + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return new ArrayList<>();
	}

	public Germplasm getProgenitorByGID(final Integer gid, final Integer proNo) {
		try {
			if (gid != null && proNo != null) {
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
			final String errorMessage = "Error with getProgenitorByGID(gid=" + gid + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return null;
	}

	public long countGermplasmDescendantByGID(final Integer gid) {
		try {
			if (gid != null) {
				final Query query = this.getSession().createSQLQuery(Germplasm.COUNT_DESCENDANTS);
				query.setParameter("gid", gid);
				return ((BigInteger) query.uniqueResult()).longValue();
			}
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countGermplasmDescendantByGID(gid=" + gid + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return 0;
	}

	public List<Germplasm> getManagementNeighbors(final Integer gid, final int start, final int numOfRows) {
		final List<Germplasm> toreturn = new ArrayList<>();
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
			final String errorMessage = "Error with getManagementNeighbors(gid=" + gid + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return toreturn;
	}

	public long countManagementNeighbors(final Integer gid) {
		try {
			if (gid != null) {
				final SQLQuery query = this.getSession().createSQLQuery(Germplasm.COUNT_MANAGEMENT_NEIGHBORS);
				query.setParameter("gid", gid);
				final BigInteger count = (BigInteger) query.uniqueResult();
				return count.longValue();
			}
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countManagementNeighbors(gid=" + gid + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return 0;
	}

	public long countGroupRelatives(final Integer gid)  {
		try {
			if (gid != null) {
				final SQLQuery query = this.getSession().createSQLQuery(Germplasm.COUNT_GROUP_RELATIVES);
				query.setParameter("gid", gid);
				final BigInteger count = (BigInteger) query.uniqueResult();
				return count.longValue();
			}
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countGroupRelatives(gid=" + gid + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return 0;
	}

	public List<Germplasm> getGroupRelatives(final Integer gid, final int start, final int numRows)  {
		final List<Germplasm> toreturn = new ArrayList<>();
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
			final String errorMessage = "Error with getGroupRelatives(gid=" + gid + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return toreturn;
	}

	public List<Germplasm> getChildren(final Integer gid, final char methodType)  {
		final List<Germplasm> toreturn = new ArrayList<>();
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
			final String errorMessage = "Error with getChildren(gid=" + gid + ", methodType=" + methodType + ") query: " + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return toreturn;

	}

	public List<Germplasm> getAllChildren(final Integer gid) {
		try {
			final List<Germplasm> children = new ArrayList<>();
			// Get all derivative children
			children.addAll(this.getChildren(gid, 'D'));

			// Get all maintenance children
			children.addAll(this.getChildren(gid, 'M'));

			// Get all generative childern
			children.addAll(this.getGenerativeChildren(gid));
			return children;
		} catch (final HibernateException e) {
			final String message = "Error executing GermplasmDAO.getAllChildren(gid={}) : {}";
			GermplasmDAO.LOG.error(message, gid, e.getMessage());
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getGenerativeChildren(final Integer gid) {
		try {
			final List<Germplasm> children = new ArrayList<>();
			// Find generative children (gnpgs > 2)
			final DetachedCriteria generativeChildrenCriteria = DetachedCriteria.forClass(Germplasm.class);
			generativeChildrenCriteria.add(Restrictions.or(Restrictions.eq("gpid1", gid), Restrictions.eq("gpid2", gid)));
			// = Two or more parents
			generativeChildrenCriteria.add(Restrictions.ge("gnpgs", 2));
			// = Record is unchanged
			generativeChildrenCriteria.add(Restrictions.eq(GRPLCE, 0));
			 // = Record is not deleted or replaced.
			generativeChildrenCriteria.add(Restrictions.eq(DELETED, Boolean.FALSE));

			children.addAll(generativeChildrenCriteria.getExecutableCriteria(this.getSession()).list());

			// Find additional children via progenitor linkage
			final DetachedCriteria otherChildrenCriteria = DetachedCriteria.forClass(Progenitor.class);
			otherChildrenCriteria.add(Restrictions.eq("pid", gid));

			final List<Progenitor> otherChildren = otherChildrenCriteria.getExecutableCriteria(this.getSession()).list();
			final Set<Integer> otherChildrenGids = new HashSet<>();
			for (final Progenitor progenitor : otherChildren) {
				otherChildrenGids.add(progenitor.getProgntrsPK().getGid());
			}

			if (!otherChildrenGids.isEmpty()) {
				children.addAll(this.getByGIDList(new ArrayList<>(otherChildrenGids)));
			}
			return children;
		} catch (final HibernateException e) {
			final String message = "Error executing GermplasmDAO.getGenerativeChildren(gid={}) : {}";
			GermplasmDAO.LOG.error(message, gid, e.getMessage());
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<Germplasm> getManagementGroupMembers(final Integer mgid) {
		if (mgid == null || mgid == 0) {
			return Collections.emptyList();
		}
		try {
			final DetachedCriteria criteria = DetachedCriteria.forClass(Germplasm.class);
			criteria.add(Restrictions.eq("mgid", mgid));
			// = Record is unchanged
			criteria.add(Restrictions.eq(GRPLCE, 0));
			 // = Record is not deleted or replaced.
			criteria.add(Restrictions.eq(DELETED, Boolean.FALSE));

			@SuppressWarnings("unchecked") final List<Germplasm> groupMembers = criteria.getExecutableCriteria(this.getSession()).list();
			// Prime the names collection before returning ;)
			for (final Germplasm g : groupMembers) {
				g.getNames().size();
			}
			return groupMembers;
		} catch (final HibernateException e) {
			final String message = "Error executing GermplasmDAO.getGroupMembersByGroupId(mgid={}) : {}";
			GermplasmDAO.LOG.error(message, mgid, e.getMessage());
			throw new MiddlewareQueryException(message, e);
		}
	}

	/**
	 * <strong>Algorithm for checking parent groups for crosses</strong>
	 * <p>
	 * Graham provided the following thoughts on the approach for retrieving the germplasm in male and female MGID groups for crosses:
	 * <ol>
	 * <li>Get GID of all lines which have the same MGID as the female -whether the female is a cross or a line, same thing - e.g. 1,2,3
	 * (all members of the female management group)
	 * <li>Get all lines which have same MGID as male - 4,5 (all members of the male management group)
	 * <li>See if any of the crosses 1x4, 1x5, 2x4, 2x5, 3x4 or 3x5 were made before.
	 * <li>If so assign the new cross to the same group.
	 * </ol>
	 *
	 * <p>
	 * Graham also noted that this query is similar to the existing one to retrieve the management group of a germplasm in the germplasm
	 * details pop-up.
	 */
	public List<Germplasm> getPreviousCrossesBetweenParentGroups(final Germplasm currentCross) {
		final Germplasm femaleParent = this.getById(currentCross.getGpid1());
		final Germplasm maleParent = this.getById(currentCross.getGpid2());

		final List<Germplasm> femaleGroupMembers = this.getManagementGroupMembers(femaleParent.getMgid());
		final List<Germplasm> maleGroupMembers = this.getManagementGroupMembers(maleParent.getMgid());

		final List<Germplasm> previousCrossesInGroup = new ArrayList<>();
		for (final Germplasm femaleGroupMember : femaleGroupMembers) {
			for (final Germplasm maleGroupMember : maleGroupMembers) {
				previousCrossesInGroup.addAll(this.getPreviousCrosses(currentCross, femaleGroupMember, maleGroupMember));
			}
		}

		// Sort oldest to newest cross : ascending order of gid
		Collections.sort(previousCrossesInGroup, new Comparator<Germplasm>() {

			@Override
			public int compare(final Germplasm o1, final Germplasm o2) {
				return o1.getGid() < o2.getGid() ? -1 : o1.getGid().equals(o2.getGid()) ? 0 : 1;
			}
		});

		return previousCrossesInGroup;
	}

	public List<Germplasm> getPreviousCrosses(final Germplasm currentCross, final Germplasm female, final Germplasm male) {
		try {
			final DetachedCriteria criteria = DetachedCriteria.forClass(Germplasm.class);

			// (female x male) is not the same as (male x female) so the order is important.
			criteria.add(Restrictions.eq("gpid1", female.getGid()));
			criteria.add(Restrictions.eq("gpid2", male.getGid()));
			// Restrict to cases where two parents are involved.
			criteria.add(Restrictions.eq("gnpgs", 2));
			// = Record is unchanged.
			criteria.add(Restrictions.eq(GRPLCE, 0));
			// Exclude current cross. We are finding "previous" crosses.
			criteria.add(Restrictions.ne("gid", currentCross.getGid()));
			// = Record is not  or replaced.
			criteria.add(Restrictions.eq(DELETED, Boolean.FALSE));
			// Oldest created cross will be first in list.
			criteria.addOrder(Order.asc("gid"));

			@SuppressWarnings("unchecked") final List<Germplasm> previousCrosses = criteria.getExecutableCriteria(this.getSession()).list();
			return previousCrosses;
		} catch (final HibernateException e) {
			final String message = "Error executing GermplasmDAO.getPreviousCrosses(female = {}, male = {}): {}";
			GermplasmDAO.LOG.error(message, female, male, e.getMessage());
			throw new MiddlewareQueryException(message, e);
		}
	}

	public String getNextSequenceNumberForCrossName(final String prefix)  {
		String nextInSequence = "1";

		try {
			/*
			 * This query will generate next number will be queried on first "word" of cross name.
			 * 
			 * eg. input prefix: "IR" output: next number in "IRNNNNN -----" sequence
			 */

			final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_NEXT_IN_SEQUENCE_FOR_CROSS_NAME_PREFIX);
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
			final String message = "Error with getNextSequenceNumberForCrossName(prefix=" + prefix + ") " + "query : " + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

		return nextInSequence;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByLocationId(final String name, final int locationID)  {
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
			final String message = "Error with getByLocationId(name=" + name + ", locationID=" + locationID + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("rawtypes")
	public Germplasm getByGIDWithMethodType(final Integer gid)  {
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
			final String message = "Error with getByGIDWithMethodType(gid=" + gid + ") from Germplasm: " + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByGIDRange(final int startGID, final int endGID)  {
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
			final String message = "Error with getByGIDRange(startGID=" + startGID + ", endGID=" + endGID + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByGIDList(final List<Integer> gids)  {

		if (gids.isEmpty()) {
			return new ArrayList<>();
		}

		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT {g.*} FROM germplsm g WHERE ");
			queryString.append("g.gid IN( :gids ) ");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("gids", gids);
			query.addEntity("g", Germplasm.class);

			return query.list();

		} catch (final HibernateException e) {
			final String message = "Error with getByGIDList(gids=" + gids.toString() + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Map<Integer, Integer> getGermplasmDatesByGids(final List<Integer> gids) {
		final Map<Integer, Integer> resultMap = new HashMap<>();
		final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_GERMPLASM_DATES_BY_GIDS);
		query.setParameterList("gids", gids);
		@SuppressWarnings("rawtypes") final List results = query.list();
		for (final Object result : results) {
			final Object[] resultArray = (Object[]) result;
			final Integer gid = (Integer) resultArray[0];
			final Integer gdate = (Integer) resultArray[1];
			resultMap.put(gid, gdate);
		}
		return resultMap;
	}

	public Map<Integer, Integer> getMethodIdsByGids(final List<Integer> gids) {
		final Map<Integer, Integer> resultMap = new HashMap<>();
		final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_METHOD_IDS_BY_GIDS);
		query.setParameterList("gids", gids);
		@SuppressWarnings("rawtypes") final List results = query.list();
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

		@SuppressWarnings("rawtypes") final List resultNames = queryNames.list();

		Name name;
		final Map<Integer, Map<GermplasmNameType, Name>> names = new HashMap<>();
		for (final Object result : resultNames) {
			final Object[] resultArray = (Object[]) result;
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
		final SQLQuery queryGermplasms = this.getSession().createSQLQuery(Germplasm.GET_KNOWN_PARENT_GIDS_BY_STUDY_ID);
		queryGermplasms.setParameter("projId", studyId);

		final List<Germplasm> germplasms = new ArrayList<>();
		Germplasm g;

		@SuppressWarnings("rawtypes") final List resultGermplasms = queryGermplasms.list();
		for (final Object result : resultGermplasms) {
			final Object[] resultArray = (Object[]) result;
			g = new Germplasm(Integer.valueOf(resultArray[0].toString()));
			g.setGpid1(Integer.valueOf(resultArray[1].toString()));
			g.setGpid2(Integer.valueOf(resultArray[2].toString()));
			g.setGrplce(Integer.valueOf(resultArray[3].toString()));

			germplasms.add(g);
		}
		return germplasms;
	}

	public Germplasm getByLGid(final Integer lgid)  {
		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT g.* FROM germplsm g WHERE g.deleted = 0 AND lgid=:lgid LIMIT 1");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameter("lgid", lgid);
			query.addEntity("g", Germplasm.class);

			return (Germplasm) query.uniqueResult();

		} catch (final HibernateException e) {
			final String message = "Error with getByLGid(lgid=" + lgid + QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

  	@SuppressWarnings("unchecked")
	public Map<Integer, String[]> getParentsInfoByGIDList (List<Integer> gidList) {
	  try {
		Map<Integer, String[]> pedigreeMap = new HashMap<>();
		final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_PREFERRED_NAME_AND_PARENT_FOR_A_GID_LIST);
		query.setParameterList("gidList", gidList);
		query.addScalar("gid");
		query.addScalar("pedigree");
		query.addScalar("nval");

		List<Object[]> results = query.list();
		for (Object[] result : results) {
		  pedigreeMap.put((Integer) result[0], new String[] {(String) result[1],(String) result[2]});
		}
		return pedigreeMap;
	  } catch (final HibernateException e) {
		String message = "Error with getPedigreeByGIDList(GIDS=" + gidList + ") : " + e.getMessage();
		GermplasmDAO.LOG.error(message, e);
		throw new MiddlewareQueryException(message, e);
	  }
	}

	public void deleteGermplasms(final List<Integer> gids) {
		final StringBuilder queryString = new StringBuilder();

		try {
			this.getSession().flush();
			queryString.append("UPDATE germplsm SET deleted = 1 where gid in (:gids)");
			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("gids", gids);
			query.executeUpdate();

		} catch (final HibernateException e) {
			String message = "Error with deleteGermplasms(GIDS=" + gids + ")  " + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	/**
	 * Get the immediate descendants of a list of gids
	 * @param gids the gids
	 * @return a map of gids and its offspring
	 * @
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, Set<Integer>> getGermplasmOffspringByGIDs(List<Integer> gids) {

		if (gids != null && !gids.isEmpty()) {
			Map<Integer, Set<Integer>> resultMap = new HashMap<>();

			final Query query = this.getSession().createSQLQuery(Germplasm.GET_GERMPLASM_OFFSPRING_BY_GID);
			query.setParameterList("gids", gids);

			/**
			 * Returns two columns:
			 * - gid
			 * - CSV of parents of the gid (gpid1, gpid2 or progntrs.pid)
			 */
			List<Object[]> results = query.list();

			// Transform to Map of gid -> list of offspring
			for (Object[] result : results) {
				String[] parentsStr = ObjectUtils.toString(result[1]).split(",");
				Set<Integer> parents = new HashSet<>();

				for (String parentStr : parentsStr) {
					try {
						parents.add(Integer.parseInt(parentStr));
					} catch (NumberFormatException e) {
						GermplasmDAO.LOG.warn("Could not cast " + parentStr);
					}
				}

				Integer offspring = (Integer) result[0];

				for (Integer parent : parents) {
					if (!resultMap.containsKey(parent) && gids.contains(parent)) {
						resultMap.put(parent, new HashSet<Integer>());
					}
					else if (gids.contains(parent)) {
						resultMap.get(parent).add(offspring);
					}
				}
			}

			return resultMap;
		}

		return new HashMap<>();
	}
}
