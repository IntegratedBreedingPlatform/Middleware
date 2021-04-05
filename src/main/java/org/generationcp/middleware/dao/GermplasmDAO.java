/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.dao;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.brapi.v1.germplasm.GermplasmDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.GermplasmImportRequest;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.domain.germplasm.ParentType;
import org.generationcp.middleware.domain.germplasm.PedigreeDTO;
import org.generationcp.middleware.domain.germplasm.ProgenyDTO;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmMatchRequestDto;
import org.generationcp.middleware.domain.search_request.brapi.v1.GermplasmSearchRequestDto;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.germplasm.GermplasmParent;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.generationcp.middleware.util.Util;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DAO class for {@link Germplasm}.
 */
public class GermplasmDAO extends GenericDAO<Germplasm, Integer> {

	private static final String GRPLCE = "grplce";
	private static final String DELETED = "deleted";

	private static final String QUERY_FROM_GERMPLASM = ") query from Germplasm: ";

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmDAO.class);

	private static final String FIND_GERMPLASM_MATCHES_MAIN_QUERY = "select " //
		+ "    g.gid as gid, " //
		+ "    g.germplsm_uuid as germplasmUUID, " //
		+ "    n.nval as preferredName, " //
		+ "    cast(g.gdate as char) as creationDate, " //
		+ "    r.analyt as reference, " //
		+ "    l.locid as breedingLocationId, " //
		+ "    l.lname as breedingLocation, " //
		+ "    m.mid as breedingMethodId, " //
		+ "    m.mname as breedingMethod, " //
		+ "    if(g.mgid > 0, true, false) as isGroupedLine, " //
		+ "    g.mgid as groupId, " //
		+ "    g.gpid1 as gpid1, "  //
		+ "    g.gpid2 as gpid2 " //
		+ "    from " //
		+ "    germplsm g " //
		+ "        left join " //
		+ "    methods m on m.mid = g.methn " //
		+ "        left join " //
		+ "    location l on l.locid = g.glocn " //
		+ "        left join " //
		+ "    names n on n.gid = g.gid and n.nstat = 1 " //
		+ "        left join " //
		+ "    bibrefs r on g.gref = r.refid ";

	private static final String FIND_GERMPLASM_MATCHES_BY_GUID = " g.germplsm_uuid in (:guidList)  ";

	private static final String FIND_GERMPLASM_MATCHES_BY_NAMES =
		" g.gid in (select gid from names n where n.nval in (:nameList) and n.nstat <> 9)";

	private static final String FIND_GERMPLASM_BY_GIDS = " g.gid in (:gids) ";

	@Override
	public Germplasm getById(final Integer gid, final boolean lock) {
		return this.getById(gid);
	}

	@Override
	public Germplasm getById(final Integer gid) {
		try {
			final String queryString = "SELECT g.* FROM germplsm g WHERE g.deleted = 0 AND g.grplce = 0 AND gid=:gid LIMIT 1";
			final SQLQuery query = this.getSession().createSQLQuery(queryString);
			query.setParameter("gid", gid);
			query.addEntity("g", Germplasm.class);

			return (Germplasm) query.uniqueResult();

		} catch (final HibernateException e) {
			final String errorMessage = "Error with getById(gid=" + gid + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByNamePermutations(final String name, final Operation operation, final int start, final int numOfRows) {

		// Converting supplied value to combination of names that can exists in names
		final List<String> names = GermplasmDataManagerUtil.createNamePermutations(name);

		if (CollectionUtils.isEmpty(names)) {
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
			final String errorMessage = "Error with getByName(names=" + names + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countByNamePermutations(final String name, final Operation operation) {

		// Converting supplied value to combination of names that can exists in names
		final List<String> names = GermplasmDataManagerUtil.createNamePermutations(name);

		if (CollectionUtils.isEmpty(names)) {
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
			final String errorMessage = "Error with countByName(names=" + names + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countMatchGermplasmInList(final Set<Integer> gids) {

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
			final String errorMessage =
				"Error with getByMethodNameUsingEqual(name=" + name + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
			final String errorMessage =
				"Error with countByMethodNameUsingEqual(name=" + name + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
			final String errorMessage =
				"Error with getByMethodNameUsingLike(name=" + name + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
			final String errorMessage =
				"Error with countByMethodNameUsingLike(name=" + name + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
			final String errorMessage =
				"Error with getByLocationNameUsingEqual(name=" + name + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
			final String errorMessage =
				"Error with countByLocationNameUsingEqual(name=" + name + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
			final String errorMessage =
				"Error with getByLocationNameUsingLike(name=" + name + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
			final String errorMessage =
				"Error with countByLocationNameUsingLike(name=" + name + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
			final String errorMessage = "Error with getByGIDWithPrefAbbrev(gid=" + gid + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return null;
	}

	public List<Germplasm> getProgenitorsByGIDWithPrefName(final Integer gid) {
		Preconditions.checkNotNull(gid);
		try {
			final List<Germplasm> progenitors = new ArrayList<>();

			final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_PROGENITORS_BY_GIDS_WITH_PREF_NAME);
			query.addScalar("gid");
			query.addEntity("g", Germplasm.class);
			query.addEntity("n", Name.class);
			query.addScalar("malePedigree");
			query.setParameterList("gidList", Lists.newArrayList(gid));
			final List<Object[]> results = query.list();
			for (final Object[] result : results) {
				final Germplasm germplasm = (Germplasm) result[1];
				final Name prefName = (Name) result[2];
				germplasm.setPreferredName(prefName);
				progenitors.add(germplasm);
			}

			return progenitors;
		} catch (final HibernateException e) {
			final String errorMessage =
				"Error with getProgenitorsByGIDWithPrefName(gid=" + gid + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public Map<Integer, List<GermplasmParent>> getParentsFromProgenitorsForGIDsMap(final List<Integer> gids) {
		Preconditions.checkNotNull(gids);
		Preconditions.checkArgument(!gids.isEmpty());

		final Map<Integer, List<GermplasmParent>> map = new HashMap<>();
		try {
			final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_PROGENITORS_BY_GIDS_WITH_PREF_NAME);
			query.addScalar("gid");
			query.addEntity("g", Germplasm.class);
			query.addEntity("n", Name.class);
			query.addScalar("malePedigree");
			query.setParameterList("gidList", gids);
			final List<Object[]> results = query.list();

			List<GermplasmParent> progenitors = new ArrayList<>();
			Integer lastGid = 0;
			for (final Object[] result : results) {
				final Integer crossGid = (Integer) result[0];
				if (lastGid == 0) {
					lastGid = crossGid;
				}
				if (!crossGid.equals(lastGid)) {
					map.put(lastGid, progenitors);
					lastGid = crossGid;
					progenitors = new ArrayList<>();
				}
				final Germplasm germplasm = (Germplasm) result[1];
				final Name prefName = (Name) result[2];
				final String pedigree = (String) result[3];
				germplasm.setPreferredName(prefName);
				progenitors.add(new GermplasmParent(germplasm.getGid(), prefName.getNval(), pedigree));
			}
			// Set last cross GID to map
			map.put(lastGid, progenitors);

			return map;
		} catch (final HibernateException e) {
			final String errorMessage =
				"Error with getProgenitorsForGIDsMap(gids=" + gids + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
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
			final String errorMessage =
				"Error with getGermplasmDescendantByGID(gid=" + gid + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return new ArrayList<>();
	}

	/**
	 * Only returns IDs of germplasm with progeny (Germplasm Id is assigned to other germplasm's gpid1, gpid2 OR progntrs.pid)
	 *
	 * @param gids - gids to be checked for descendants
	 * @return
	 */
	public Set<Integer> getGidsOfGermplasmWithDescendants(final Set<Integer> gids) {
		try {
			if (!CollectionUtils.isEmpty(gids)) {
				final SQLQuery query = this.getSession().createSQLQuery("SELECT g.gid as gid FROM germplsm g "
					+ "WHERE (EXISTS (SELECT 1 FROM germplsm descendant WHERE g.gid = descendant.gpid1 OR g.gid = descendant.gpid2 ) "
					+ "OR EXISTS (SELECT 1 FROM progntrs p WHERE  g.gid = p.gid)) "
					+ "AND g.gid IN (:gids) AND  g.deleted = 0 AND g.grplce = 0 ");
				query.addScalar("gid", new IntegerType());
				query.setParameterList("gids", gids);
				return Sets.newHashSet(query.list());
			}
			return Sets.newHashSet();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getGidsOfGermplasmWithDescendants(gids=" + gids + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
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
			final String errorMessage = "Error with getProgenitorByGID(gid=" + gid + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
			final String errorMessage =
				"Error with countGermplasmDescendantByGID(gid=" + gid + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
			final String errorMessage = "Error with getManagementNeighbors(gid=" + gid + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
			final String errorMessage =
				"Error with countManagementNeighbors(gid=" + gid + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return 0;
	}

	public long countGroupRelatives(final Integer gid) {
		try {
			if (gid != null) {
				final SQLQuery query = this.getSession().createSQLQuery(Germplasm.COUNT_GROUP_RELATIVES);
				query.setParameter("gid", gid);
				final BigInteger count = (BigInteger) query.uniqueResult();
				return count.longValue();
			}
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countGroupRelatives(gid=" + gid + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return 0;
	}

	public List<Germplasm> getGroupRelatives(final Integer gid, final int start, final int numRows) {
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
			final String errorMessage = "Error with getGroupRelatives(gid=" + gid + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return toreturn;
	}

	public List<Germplasm> getChildren(final Integer gid, final char methodType) {
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
			// Find generative children (gnpgs > 2)
			final DetachedCriteria generativeChildrenCriteria = DetachedCriteria.forClass(Germplasm.class);
			generativeChildrenCriteria.add(Restrictions.or(Restrictions.eq("gpid1", gid), Restrictions.eq("gpid2", gid)));
			// = Two or more parents
			generativeChildrenCriteria.add(Restrictions.ge("gnpgs", 2));
			// = Record is unchanged
			generativeChildrenCriteria.add(Restrictions.eq(GermplasmDAO.GRPLCE, 0));
			// = Record is not deleted or replaced.
			generativeChildrenCriteria.add(Restrictions.eq(GermplasmDAO.DELETED, Boolean.FALSE));

			final List<Germplasm> children = new ArrayList<Germplasm>(generativeChildrenCriteria.getExecutableCriteria(this.getSession()).list());

			// Find additional children via progenitor linkage
			final DetachedCriteria otherChildrenCriteria = DetachedCriteria.forClass(Progenitor.class);
			otherChildrenCriteria.add(Restrictions.eq("progenitorGid", gid));

			final List<Progenitor> otherChildren = otherChildrenCriteria.getExecutableCriteria(this.getSession()).list();
			final Set<Integer> otherChildrenGids = new HashSet<>();
			for (final Progenitor progenitor : otherChildren) {
				otherChildrenGids.add(progenitor.getGermplasm().getGid());
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
			criteria.add(Restrictions.eq(GermplasmDAO.GRPLCE, 0));
			// = Record is not deleted or replaced.
			criteria.add(Restrictions.eq(GermplasmDAO.DELETED, Boolean.FALSE));

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

	public PedigreeDTO getPedigree(final Integer germplasmDbId, final String notation, final Boolean includeSiblings) {
		try {
			final String query = "SELECT "
				+ "   g.germplsm_uuid as germplasmDbId," // use gid as germplasmDbId
				+ "   (select n.nval from names n where n.gid = g.gid AND n.nstat = 1) as defaultDisplayName," //
				+ "   CONCAT(m.mcode, '|', m.mname, '|', m.mtype) AS crossingPlan," //
				+ "   year(str_to_date(g.gdate, '%Y%m%d')) as crossingYear," //
				+ "   femaleParent.gid as parent1DbId," //
				+ "   femaleParentName.nval as parent1Name," //
				// If germplasmDbId is a cross (gnpgs > 0), the parents' type should be FEMALE and MALE
				// If germplasmDbId is advanced (gnpgs < 0), the parents type should be POPULATION and SELF
				+ "   CASE WHEN femaleParent.gid is not null AND g.gnpgs > 0 THEN '" + ParentType.FEMALE.name() + "' "
				+ "	  WHEN femaleParent.gid is not null AND g.gnpgs < 0 THEN '" + ParentType.POPULATION.name()
				+ "' ELSE NULL END as parent1Type," //
				+ "   maleParent.gid as parent2DbId," //
				+ "   maleParentName.nval as parent2Name," //
				+ "   CASE WHEN maleParent.gid is not null AND g.gnpgs > 0 THEN '" + ParentType.MALE.name() + "' "
				+ "	  WHEN maleParent.gid is not null AND g.gnpgs < 0  THEN '" + ParentType.SELF.name()
				+ "' ELSE NULL END as parent2Type" //
				+ " FROM germplsm g" //
				+ "   LEFT JOIN methods m ON m.mid = g.methn" //
				+ "   LEFT JOIN germplsm femaleParent ON g.gpid1 = femaleParent.gid" //
				+ "   LEFT JOIN names femaleParentName ON femaleParent.gid = femaleParentName.gid AND femaleParentName.nstat = 1" //
				+ "   LEFT JOIN germplsm maleParent ON g.gpid2 = maleParent.gid" //
				+ "   LEFT JOIN names maleParentName ON maleParent.gid = maleParentName.gid AND maleParentName.nstat = 1" //
				+ " WHERE g.gid = :gid AND g.deleted = 0 AND g.grplce = 0";

			final PedigreeDTO pedigreeDTO = (PedigreeDTO) this.getSession().createSQLQuery(query) //
				.addScalar("germplasmDbId").addScalar("defaultDisplayName").addScalar("crossingPlan")
				.addScalar("crossingYear", new IntegerType()).addScalar("parent1DbId").addScalar("parent1Name").addScalar("parent1Type")
				.addScalar("parent2DbId").addScalar("parent2Name").addScalar("parent2Type")
				.setParameter("gid", germplasmDbId) //
				.setResultTransformer(Transformers.aliasToBean(PedigreeDTO.class)) //
				.uniqueResult();

			if (includeSiblings == null || !includeSiblings) {
				return pedigreeDTO;
			}

			final String siblingsQuery = "SELECT" //
				+ "   sibling.germplsm_uuid AS germplasmDbId," //
				+ "   n.nval AS defaultDisplayName" //
				+ " FROM germplsm g" //
				+ "   INNER JOIN germplsm sibling ON sibling.gpid1 = g.gpid1"//
				+ "                                  AND sibling.gnpgs = -1"//
				+ "                                  AND g.gpid1 != 0"//
				+ "                                  AND sibling.gid != g.gid"//
				+ "   LEFT JOIN names n ON sibling.gid = n.gid AND n.nstat = 1" //
				+ " WHERE g.gid = :gid";

			final List<PedigreeDTO.Sibling> siblings = this.getSession().createSQLQuery(siblingsQuery) //
				.addScalar("germplasmDbId").addScalar("defaultDisplayName")
				.setParameter("gid", germplasmDbId)
				.setResultTransformer(Transformers.aliasToBean(PedigreeDTO.Sibling.class)) //
				.list();

			pedigreeDTO.setSiblings(siblings);

			return pedigreeDTO;
		} catch (final HibernateException e) {
			GermplasmDAO.LOG.error(e.getMessage());
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}

	public ProgenyDTO getProgeny(final Integer germplasmDbId) {
		try {
			final Germplasm germplasm = this.getByGIDWithPrefName(germplasmDbId);
			if (germplasm == null) {
				return null;
			}

			final String query = "SELECT" //
				+ "   progeny.germplsm_uuid as germplasmDbId," //
				+ "   name.nval as defaultDisplayName," //
				+ "   CASE" //
				+ "   WHEN progeny.gnpgs = -1" //
				+ "     THEN '" + ParentType.SELF.name() + "'" //
				+ "   WHEN progeny.gnpgs >= 2" //
				+ "     THEN" //
				+ "       CASE" //
				+ "         WHEN progeny.gpid1 = progeny.gpid2" //
				+ "           THEN '" + ParentType.SELF.name() + "'" //
				+ "         WHEN progeny.gpid1 = parent.gid" //
				+ "           THEN '" + ParentType.FEMALE.name() + "'" //
				+ "         ELSE '" + ParentType.MALE.name() + "'" //
				+ "       END" //
				+ "   ELSE ''" //
				+ "   END as parentType" //
				+ " FROM germplsm parent" //
				+ "   LEFT JOIN germplsm progeny ON (progeny.gnpgs = -1 AND progeny.gpid2 = parent.gid)" //
				+ "                                 OR (progeny.gnpgs >= 2 AND (progeny.gpid1 = parent.gid OR progeny.gpid2 = parent.gid))"
				//
				+ "   LEFT JOIN names name ON progeny.gid = name.gid AND name.nstat = 1" //
				+ " WHERE parent.gid = :gid" //
				+ "       AND parent.deleted = 0 AND parent.grplce = 0" //
				+ "       AND progeny.deleted = 0 AND progeny.grplce = 0";

			final List<ProgenyDTO.Progeny> progeny = this.getSession().createSQLQuery(query) //
				.addScalar("germplasmDbId").addScalar("defaultDisplayName").addScalar("parentType")
				.setParameter("gid", germplasmDbId) //
				.setResultTransformer(Transformers.aliasToBean(ProgenyDTO.Progeny.class)) //
				.list();

			final ProgenyDTO progenyDTO = new ProgenyDTO();
			progenyDTO.setGermplasmDbId(germplasm.getGermplasmUUID());
			progenyDTO.setDefaultDisplayName(germplasm.getPreferredName().getNval());

			progenyDTO.setProgeny(progeny);

			return progenyDTO;

		} catch (final HibernateException e) {
			GermplasmDAO.LOG.error(e.getMessage());
			throw new MiddlewareQueryException(e.getMessage(), e);
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
		Collections.sort(previousCrossesInGroup, (o1, o2) -> o1.getGid() < o2.getGid() ? -1 : o1.getGid().equals(o2.getGid()) ? 0 : 1);

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
			criteria.add(Restrictions.eq(GermplasmDAO.GRPLCE, 0));
			// Exclude current cross. We are finding "previous" crosses.
			criteria.add(Restrictions.ne("gid", currentCross.getGid()));
			// = Record is not or replaced.
			criteria.add(Restrictions.eq(GermplasmDAO.DELETED, Boolean.FALSE));
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

	public String getNextSequenceNumber(String prefix) {
		String nextInSequence = "1";

		if (!prefix.isEmpty()) {
			try {
				prefix = prefix.trim();

				final String queryString = "SELECT CONVERT(REPLACE(UPPER(nval), :prefix, ''), SIGNED)+1 as next_number "
					// We used LIKE when matching names by prefix
					+ " FROM ( " + " 	SELECT  distinct nval " + "		FROM names " + "		WHERE names.nval LIKE :prefixLike "
					+ "   	AND NOT EXISTS (select 1 from germplsm g where g.gid = names.gid and g.deleted = 1)" + " ) matches "
					+ " ORDER BY next_number desc LIMIT 1";
				final SQLQuery query = this.getSession().createSQLQuery(queryString);
				query.setParameter("prefix", prefix.toUpperCase());
				query.setParameter("prefixLike", prefix + "%");

				final BigInteger nextNumberInSequence = (BigInteger) query.uniqueResult();

				if (nextNumberInSequence != null) {
					nextInSequence = String.valueOf(nextNumberInSequence);
				}

			} catch (final HibernateException e) {
				final String message = "Error with getNextSequenceNumber(prefix=" + prefix + ") " + "query : " + e.getMessage();
				GermplasmDAO.LOG.error(message, e);
				throw new MiddlewareQueryException(message, e);
			}
		}

		return nextInSequence;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByLocationId(final String name, final int locationID) {
		try {

			final String queryString = "SELECT {g.*} FROM germplsm g JOIN names n ON g.gid = n.gid WHERE "
				+ "n.nval = :name "
				+ "AND g.glocn = :locationID ";
			final SQLQuery query = this.getSession().createSQLQuery(queryString);
			query.setParameter("name", name);
			query.setParameter("locationID", locationID);
			query.addEntity("g", Germplasm.class);

			return query.list();

		} catch (final HibernateException e) {
			final String message = "Error with getByLocationId(name=" + name + ", locationID=" + locationID
				+ GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByGIDRange(final int startGID, final int endGID) {
		try {

			final String queryString = "SELECT {g.*} FROM germplsm g WHERE "
				+ "g.gid >= :startGID "
				+ "AND g.gid <= :endGID ";
			final SQLQuery query = this.getSession().createSQLQuery(queryString);
			query.setParameter("startGID", startGID);
			query.setParameter("endGID", endGID);
			query.addEntity("g", Germplasm.class);

			return query.list();

		} catch (final HibernateException e) {
			final String message = "Error with getByGIDRange(startGID=" + startGID + ", endGID=" + endGID
				+ GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByGIDList(final List<Integer> gids) {

		if (gids.isEmpty()) {
			return new ArrayList<>();
		}

		try {

			final String queryString = "SELECT {g.*} FROM germplsm g WHERE "
				+ "g.gid IN( :gids ) "
				+ " AND g.deleted = 0"
				+ " AND g.grplce = 0";
			final SQLQuery query = this.getSession().createSQLQuery(queryString);
			query.setParameterList("gids", gids);
			query.addEntity("g", Germplasm.class);

			return query.list();

		} catch (final HibernateException e) {
			final String message = "Error with getByGIDList(gids=" + gids.toString() + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<Germplasm> getByGIDsOrUUIDListWithMethodAndBibref(final Set<Integer> gids, final Set<String> uuids) {

		if (uuids.isEmpty() && gids.isEmpty()) {
			return new ArrayList<>();
		}

		try {
			final Criteria criteria = this.getSession().createCriteria(Germplasm.class);
			criteria.createAlias("method", "method", CriteriaSpecification.LEFT_JOIN);
			criteria.createAlias("bibref", "bibref", CriteriaSpecification.LEFT_JOIN);
			if (gids.isEmpty()) {
				criteria.add(Restrictions.in("germplasmUUID", uuids));
			} else if (uuids.isEmpty()) {
				criteria.add(Restrictions.in("gid", gids));
			} else {
				criteria.add(Restrictions.or(Restrictions.in("germplasmUUID", uuids), Restrictions.in("gid", gids)));
			}
			criteria.add(Restrictions.eq("deleted", false));
			criteria.add(Restrictions.eq("grplce", 0));

			return criteria.list();

		} catch (final HibernateException e) {
			final String message =
				"Error with getByUUIDListWithMethodAndBibref(gids=" + uuids.toString() + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
				names.put(gid, new HashMap<>());
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

	public Germplasm getByLGid(final Integer lgid) {
		try {
			final SQLQuery query = this.getSession().createSQLQuery("SELECT g.* FROM germplsm g WHERE g.deleted = 0 AND lgid=:lgid LIMIT 1");
			query.setParameter("lgid", lgid);
			query.addEntity("g", Germplasm.class);

			return (Germplasm) query.uniqueResult();

		} catch (final HibernateException e) {
			final String message = "Error with getByLGid(lgid=" + lgid + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("unchecked")
	public Map<Integer, String[]> getParentsInfoByGIDList(final List<Integer> gidList) {
		try {
			final Map<Integer, String[]> pedigreeMap = new HashMap<>();
			final SQLQuery query = this.getSession().createSQLQuery(Germplasm.GET_PREFERRED_NAME_AND_PARENT_FOR_A_GID_LIST);
			query.setParameterList("gidList", gidList);
			query.addScalar("gid");
			query.addScalar("pedigree");
			query.addScalar("nval");

			final List<Object[]> results = query.list();
			for (final Object[] result : results) {
				pedigreeMap.put((Integer) result[0], new String[] {(String) result[1], (String) result[2]});
			}
			if (gidList.contains(0)) {
				pedigreeMap.put(0, new String[] {Name.UNKNOWN, Name.UNKNOWN});
			}
			return pedigreeMap;
		} catch (final HibernateException e) {
			final String message = "Error with getPedigreeByGIDList(GIDS=" + gidList + ") : " + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void deleteGermplasm(final List<Integer> gids) {
		final StringBuilder queryString = new StringBuilder();

		try {
			queryString.append("UPDATE germplsm SET deleted = 1, germplsm_uuid = CONCAT (germplsm_uuid, '#', '" + Util
				.getCurrentDateAsStringValue("yyyyMMddHHmmssSSS") + "')  where gid in (:gids)");
			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("gids", gids);
			query.executeUpdate();

		} catch (final HibernateException e) {
			final String message = "Error with deleteGermplasms(GIDS=" + gids + ")  " + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	/**
	 * Get the immediate descendants of a list of gids
	 *
	 * @param gids the gids
	 * @return a map of gids and its offspring @
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, Set<Integer>> getGermplasmOffspringByGIDs(final List<Integer> gids) {

		if (gids != null && !gids.isEmpty()) {
			final Map<Integer, Set<Integer>> resultMap = new HashMap<>();

			final Query query = this.getSession().createSQLQuery(Germplasm.GET_GERMPLASM_OFFSPRING_BY_GID);
			query.setParameterList("gids", gids);

			/**
			 * Returns two columns: - gid - CSV of parents of the gid (gpid1, gpid2 or progntrs.pid)
			 */
			final List<Object[]> results = query.list();

			// Transform to Map of gid -> list of offspring
			for (final Object[] result : results) {
				final String[] parentsStr = ObjectUtils.toString(result[1]).split(",");
				final Set<Integer> parents = new HashSet<>();

				for (final String parentStr : parentsStr) {
					try {
						parents.add(Integer.parseInt(parentStr));
					} catch (final NumberFormatException e) {
						GermplasmDAO.LOG.warn("Could not cast " + parentStr);
					}
				}

				final Integer offspring = (Integer) result[0];

				for (final Integer parent : parents) {
					if (!resultMap.containsKey(parent) && gids.contains(parent)) {
						resultMap.put(parent, new HashSet<Integer>());
					} else if (gids.contains(parent)) {
						resultMap.get(parent).add(offspring);
					}
				}
			}

			return resultMap;
		}

		return new HashMap<>();
	}

	/**
	 * Only return germplasm with no group assigned (mgid = 0 or mgid is null)
	 *
	 * @param gids
	 * @return
	 */
	public List<Germplasm> getGermplasmWithoutGroup(final List<Integer> gids) {

		if (gids.isEmpty()) {
			return new ArrayList<>();
		}

		try {

			final String queryString = "SELECT {g.*} FROM germplsm g WHERE "
				+ "g.gid IN( :gids ) AND (g.mgid = 0 || g.mgid IS NULL)";
			final SQLQuery query = this.getSession().createSQLQuery(queryString);
			query.setParameterList("gids", gids);
			query.addEntity("g", Germplasm.class);

			return query.list();

		} catch (final HibernateException e) {
			final String message = "Error with getGermplasmWithoutGroup(gids=" + gids.toString() + ") " + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	/**
	 * Resets the mgids of a given list of germplasm to zero.
	 *
	 * @param gids
	 */
	public void resetGermplasmGroup(final List<Integer> gids) {

		try {

			final SQLQuery query = this.getSession().createSQLQuery("UPDATE germplsm SET mgid = 0 WHERE gid IN (:gids)");
			query.setParameterList("gids", gids);
			query.executeUpdate();

		} catch (final HibernateException e) {
			final String message = "Error with resetGermplasmGroup(gids=" + gids.toString() + ") " + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

	}

	private void addGermplasmSearchParameters(final SqlQueryParamBuilder paramBuilder, final GermplasmSearchRequestDto germplasmSearchRequestDTO) {
		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getPreferredName())) {
			paramBuilder.setParameter("preferredName", "%" + germplasmSearchRequestDTO.getPreferredName() + "%"); //
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getAccessionNumbers())) {
			paramBuilder.setParameterList("accessionNumbers", germplasmSearchRequestDTO.getAccessionNumbers());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getCommonCropNames())) {
			paramBuilder.setParameterList("commonCropNames", germplasmSearchRequestDTO.getCommonCropNames());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmDbIds())) {
			paramBuilder.setParameterList("germplasmDbIds", germplasmSearchRequestDTO.getGermplasmDbIds());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmGenus())) {
			paramBuilder.setParameterList("germplasmGenus", germplasmSearchRequestDTO.getGermplasmGenus());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmNames())) {
			paramBuilder.setParameterList("germplasmNames", germplasmSearchRequestDTO.getGermplasmNames());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmSpecies())) {
			paramBuilder.setParameterList("germplasmSpecies", germplasmSearchRequestDTO.getGermplasmSpecies());
		}

		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getStudyDbId())) {
			paramBuilder.setParameter("studyDbId", germplasmSearchRequestDTO.getStudyDbId());
		}

		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getParentDbId())) {
			paramBuilder.setParameter("parentDbId", germplasmSearchRequestDTO.getParentDbId());
		}

		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getProgenyDbId())) {
			paramBuilder.setParameter("progenyDbId", germplasmSearchRequestDTO.getProgenyDbId());
		}

		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getExternalReferenceSource())) {
			paramBuilder.setParameter("referenceSource", germplasmSearchRequestDTO.getExternalReferenceSource());
		}

		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getExternalReferenceId())) {
			paramBuilder.setParameter("referenceId", germplasmSearchRequestDTO.getExternalReferenceId());
		}
	}

	// These are appended to the MAIN where clause because they are filters on (or EXISTS clause related to) the main germplasm object
	private void addGermplasmSearchRequestFilters(final SqlQueryParamBuilder paramBuilder, final GermplasmSearchRequestDto germplasmSearchRequestDTO) {
		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmDbIds())) {
			paramBuilder.append(" AND g.germplsm_uuid IN (:germplasmDbIds) ");
		}

		// Search synonyms or non-default names
		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmNames())) {
			paramBuilder.append(" AND g.gid IN ( SELECT n.gid ");
			paramBuilder.append(" FROM names n WHERE n.nval IN (:germplasmNames) and n.nstat != 9 and n.nstat != 1 ) "); //
		}

		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getStudyDbId())) {
			paramBuilder.append(" AND g.gid IN ( "
				+ "  SELECT s.dbxref_id from stock s "
				+ "   INNER JOIN nd_experiment e ON e.stock_id = s.stock_id "
				+ "   WHERE e.nd_geolocation_id = :studyDbId ) ");
		}

		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getParentDbId())) {
			paramBuilder.append(" AND (g.gpid1 = :parentDbId OR g.gpid2 = :parentDbId) AND g.gnpgs >= 2");
		}

		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getProgenyDbId())) {
			paramBuilder.append(" AND ( g.gid = ( "
				+ "SELECT gpid1 from germplsm child "
				+ "WHERE child.gid = :progenyDbId AND child.gnpgs >= 2 ) OR g. gid = ( "
				+ "SELECT gpid2 from germplsm child "
				+ "WHERE child.gid = :progenyDbId AND child.gnpgs >= 2 ) )");
		}

		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getExternalReferenceId())) {
			paramBuilder.append(" AND g.gid IN ( ");
			paramBuilder.append(" SELECT ref.gid FROM external_reference ref WHERE ref.reference_id = :referenceId) "); //
		}

		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getExternalReferenceSource())) {
			paramBuilder.append(" AND g.gid IN (  ");
			paramBuilder.append(" SELECT ref.gid FROM external_reference ref WHERE ref.reference_source = :referenceSource) "); //
		}

		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getPreferredName())) {
			paramBuilder.append(" AND g.gid IN ( SELECT n.gid ");
			paramBuilder.append(" FROM names n WHERE n.nstat = 1 AND n.nval like :preferredName ) ");
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getAccessionNumbers())) {
			paramBuilder.append(" AND g.gid IN ( SELECT n.gid  ");
			paramBuilder.append(" FROM names n ");
			paramBuilder.append(" INNER JOIN udflds u on n.ntype = u.fldno AND u.fcode = '" + GermplasmImportRequest.ACCNO + "'");
			paramBuilder.append(" WHERE n.nval IN (:accessionNumbers)  ) ");
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getCommonCropNames())) {
			paramBuilder.append(" AND g.gid IN ( SELECT a.gid  ");
			paramBuilder.append(" FROM atributs a");
			paramBuilder.append(" INNER JOIN udflds u on a.atype = u.fldno AND u.fcode = '" + GermplasmImportRequest.CROPNM + "'");
			paramBuilder.append(" WHERE a.aval IN (:commonCropNames)  ) ");
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmGenus())) {
			paramBuilder.append(" AND g.gid IN ( SELECT n.gid  ");
			paramBuilder.append(" FROM names n ");
			paramBuilder.append(" INNER JOIN udflds u on n.ntype = u.fldno AND u.fcode = '" + GermplasmImportRequest.GENUS + "'");
			paramBuilder.append(" WHERE n.nval IN (:germplasmGenus)  ) ");
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmSpecies())) {
			paramBuilder.append(" AND g.gid IN ( SELECT a.gid  ");
			paramBuilder.append(" FROM atributs a");
			paramBuilder.append(" INNER JOIN udflds u on a.atype = u.fldno AND u.fcode = '" + GermplasmImportRequest.SPECIES + "'");
			paramBuilder.append(" WHERE a.aval IN (:germplasmSpecies)  ) ");
		}

	}

	public List<GermplasmDTO> getGermplasmDTOList(
		final GermplasmSearchRequestDto germplasmSearchRequestDTO, final Pageable pageable) {

		try {
			// Apply search filter and pagination on main germplasm entity
			final SQLQuery filterQuery = this.getSession().createSQLQuery(this.buildFilterGermplasmQuery(germplasmSearchRequestDTO));
			this.addGermplasmSearchParameters(new SqlQueryParamBuilder(filterQuery), germplasmSearchRequestDTO);
			addPaginationToSQLQuery(filterQuery, pageable);
			final List<String> gids = filterQuery.list();

			// If there is any match, build the list of  GermplasmDTO based on the matched GIDs
			if (!CollectionUtils.isEmpty(gids)) {
				final SQLQuery sqlQuery = this.getSession().createSQLQuery(this.buildGetGermplasmQuery());
				sqlQuery.addScalar("germplasmDbId").addScalar("gid").addScalar("accessionNumber").addScalar("acquisitionDate")
					.addScalar("countryOfOriginCode").addScalar("germplasmName").addScalar("genus").addScalar("germplasmSeedSource")
					.addScalar("species").addScalar("speciesAuthority").addScalar("subtaxa").addScalar("subtaxaAuthority")
					.addScalar("instituteCode").addScalar("instituteName").addScalar("germplasmOrigin").addScalar("commonCropName")
					.addScalar("breedingMethodDbId")
					.setResultTransformer(new AliasToBeanResultTransformer(GermplasmDTO.class));
				sqlQuery.setParameterList("gids", gids);
				return sqlQuery.list();
			}

		} catch (final HibernateException e) {
			final String message = "Error with getGermplasmDTOList" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

		return new ArrayList<>();
	}

	private String getSelectClauseForFilterGermplasm() {
		return "SELECT g.germplsm_uuid AS germplasmDbId, convert(g.gid, char) as gid, " //
			+ "  g.gpid1, g.gpid2, g.gnpgs, " //
			+ "  max(if(ntype.fcode = 'ACCNO', n.nval, null)) as accessionNumber, " //
			+ "   STR_TO_DATE (convert(g.gdate,char), '%Y%m%d') AS acquisitionDate," //
			+ "   loc.labbr AS countryOfOriginCode, " //
			+ "   max(if(n.nstat = 1, n.nval, null)) as germplasmName, " //
			+ "  max(if(ntype.fcode = 'GENUS', n.nval, null)) as genus," //
			+ "   max(if(atype.fcode = 'PLOTCODE', a.aval, null)) as germplasmSeedSource,  "
			+ "   max(if(atype.fcode = 'SPNAM', a.aval, null)) as species, "
			+ "   max(if(atype.fcode = 'SPAUTH', a.aval, null)) as speciesAUthority, "
			+ "   max(if(atype.fcode = 'SUBTAX', a.aval, null)) as subtaxa, "
			+ "   max(if(atype.fcode = 'STAUTH', a.aval, null)) as subtaxaAuthority, "
			+ "   max(if(atype.fcode = 'INSTCODE', a.aval, null)) as instituteCode, "
			+ "   max(if(atype.fcode = 'ORIGININST', a.aval, null)) as instituteName, "
			+ "   max(if(atype.fcode = 'SORIG', a.aval, null)) as germplasmOrigin, "
			+ "   max(if(atype.fcode = 'CROPNM', a.aval, null)) as commonCropName, "
			+ "   convert(g.methn, char) as breedingMethodDbId ";
	}

	private String getMainFromGermplasmClause() {
		return "  FROM germplsm g "
			+ "  	LEFT join location loc ON g.glocn = loc.locid "
			+ "  	LEFT JOIN atributs a ON a.gid = g.gid "
			+ "  	LEFT JOIN udflds atype ON atype.fldno = a.atype "
			+ "  	LEFT join names n ON n.gid = g.gid and n.nstat != 9 "
			+ "  	LEFT JOIN udflds ntype ON ntype.fldno = n.ntype ";
	}

	private String buildFilterGermplasmQuery(final GermplasmSearchRequestDto germplasmSearchRequestDTO) {
		final StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("  SELECT g.gid " );
		queryBuilder.append("  FROM germplsm g " );
		queryBuilder.append(" WHERE g.deleted = 0 AND g.grplce = 0 ");
		this.addGermplasmSearchRequestFilters(new SqlQueryParamBuilder(queryBuilder), germplasmSearchRequestDTO);
		return queryBuilder.toString();
	}

	private String buildGetGermplasmQuery() {
		final StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(this.getSelectClauseForFilterGermplasm());
		queryBuilder.append(this.getMainFromGermplasmClause()); //
		queryBuilder.append(" WHERE g.deleted = 0 AND g.grplce = 0 ");
		queryBuilder.append(" AND g.gid IN (:gids) ");
		queryBuilder.append(" GROUP by g.gid ");
		return queryBuilder.toString();
	}

	public long countGermplasmDTOs(final GermplasmSearchRequestDto germplasmSearchRequestDTO) {

		final String queryString = "SELECT COUNT(1) FROM ( " + this.buildFilterGermplasmQuery(germplasmSearchRequestDTO) + ") as T ";
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryString);
		this.addGermplasmSearchParameters(new SqlQueryParamBuilder(sqlQuery), germplasmSearchRequestDTO);

		return ((BigInteger) sqlQuery.uniqueResult()).longValue();
	}

	public long countGermplasmByStudy(final Integer studyDbId) {
		final String queryString = "select count(1) from (" + this.getGermplasmForStudyQuery() + ") AS T ";//

		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryString);
		sqlQuery.setParameter("studyDbId", studyDbId);

		return ((BigInteger) sqlQuery.uniqueResult()).longValue();
	}

	public String getGermplasmForStudyQuery() {
		final StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(this.getSelectClauseForFilterGermplasm());
		queryBuilder.append(" , s.uniquename AS entryNumber ");
		queryBuilder.append(this.getMainFromGermplasmClause());
		queryBuilder.append(" INNER JOIN stock s ON s.dbxref_id = g.gid "//
			+ " INNER JOIN nd_experiment e ON e.stock_id = s.stock_id "//
			+ " WHERE g.deleted = 0 AND g.grplce = 0 "//
			+ " AND e.nd_geolocation_id = :studyDbId ");
		queryBuilder.append(" GROUP by g.gid ");
		queryBuilder.append(" ORDER BY CAST(s.uniquename as SIGNED INTEGER) ");
		return queryBuilder.toString();
	}

	public List<GermplasmDTO> getGermplasmByStudy(final Integer studyDbId, final Pageable pageable) {
		try {
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(this.getGermplasmForStudyQuery());
			sqlQuery.setParameter("studyDbId", studyDbId);

			sqlQuery.addScalar("germplasmDbId").addScalar("gid").addScalar("accessionNumber").addScalar("acquisitionDate")
				.addScalar("countryOfOriginCode").addScalar("germplasmName").addScalar("genus").addScalar("germplasmSeedSource")
				.addScalar("species").addScalar("speciesAuthority").addScalar("subtaxa").addScalar("subtaxaAuthority")
				.addScalar("instituteCode").addScalar("instituteName").addScalar("germplasmOrigin").addScalar("commonCropName")
				.addScalar("breedingMethodDbId").addScalar("entryNumber") //
				.setResultTransformer(new AliasToBeanResultTransformer(GermplasmDTO.class));

			addPaginationToSQLQuery(sqlQuery, pageable);

			return sqlQuery.list();
		} catch (final HibernateException e) {
			final String message = "Error with getGermplasmByStudy(studyDbId=" + studyDbId + ") " + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<Germplasm> getExistingCrosses(final Integer femaleParent, final List<Integer> maleParentIds,
		final Optional<Integer> gid) {
		try {
			final StringBuilder builder = this.buildGetExistingCrossesQueryString(maleParentIds, gid);
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(builder.toString());
			sqlQuery.setParameterList("maleParentIds", maleParentIds);
			sqlQuery.setParameter("femaleParentId", femaleParent);
			gid.ifPresent(germplasmId -> sqlQuery.setParameter("gid", germplasmId));
			sqlQuery.addScalar("gid");
			sqlQuery.addScalar("nval");

			final List<Germplasm> existingCrosses = new ArrayList<>();
			final List<Object[]> results = sqlQuery.list();
			for (final Object[] result : results) {
				final Germplasm existingCross = new Germplasm();
				existingCross.setGid((Integer) result[0]);
				existingCross.setGermplasmPeferredName((String) result[1]);
				existingCrosses.add(existingCross);
			}
			return existingCrosses;
		} catch (final HibernateException e) {
			final String message = "Error with getExistingCrosses" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<Germplasm> getGermplasmByGUIDs(final List<String> guids) {
		if (guids == null || guids.isEmpty()) {
			return new ArrayList<>();
		}
		try {
			final Criteria criteria = this.getSession().createCriteria(Germplasm.class);
			criteria.add(Restrictions.in("germplasmUUID", guids));
			criteria.add(Restrictions.eq("deleted", Boolean.FALSE));
			return criteria.list();
		} catch (final HibernateException e) {
			GermplasmDAO.LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				this.getLogExceptionMessage("getGermplasmByGUIDs", "guids", "", e.getMessage(),
					"Germplasm"), e);
		}
	}

	public long countGermplasmMatches(final GermplasmMatchRequestDto germplasmMatchRequestDto) {
		final StringBuilder queryBuilder =
			new StringBuilder(" select count(1) from germplsm g ");
		this.addGermplasmMatchesFilter(new SqlQueryParamBuilder(queryBuilder), germplasmMatchRequestDto);
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
		this.addGermplasmMatchesFilter(new SqlQueryParamBuilder(sqlQuery), germplasmMatchRequestDto);
		try {
			return ((BigInteger) sqlQuery.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			final String message = "Error with countGermplasmMatches " + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	private void addScalarsToFindGermplasmMatchesQuery(final SQLQuery sqlQuery) {
		sqlQuery.addScalar("gid").addScalar("germplasmUUID").addScalar("preferredName").addScalar("creationDate").addScalar("reference")
			.addScalar("breedingLocationId")
			.addScalar("breedingLocation").addScalar("breedingMethodId").addScalar("breedingMethod")
			.addScalar("isGroupedLine", new BooleanType())
			.addScalar("groupId").addScalar("gpid1").addScalar("gpid2");
	}

	public List<GermplasmDto> findGermplasmMatches(final GermplasmMatchRequestDto germplasmMatchRequestDto, final Pageable pageable) {
		final StringBuilder queryBuilder =
			new StringBuilder(FIND_GERMPLASM_MATCHES_MAIN_QUERY);
		this.addGermplasmMatchesFilter(new SqlQueryParamBuilder(queryBuilder), germplasmMatchRequestDto);
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
		this.addScalarsToFindGermplasmMatchesQuery(sqlQuery);
		this.addGermplasmMatchesFilter(new SqlQueryParamBuilder(sqlQuery), germplasmMatchRequestDto);

		if (pageable != null) {
			addPaginationToSQLQuery(sqlQuery, pageable);
		}

		sqlQuery.setResultTransformer(Transformers.aliasToBean(GermplasmDto.class));

		try {
			return sqlQuery.list();
		} catch (final HibernateException e) {
			final String message = "Error with findGermplasmMatches" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	private void addGermplasmMatchesFilter(final SqlQueryParamBuilder sqlQueryParamBuilder,
		final GermplasmMatchRequestDto germplasmMatchRequestDto) {
		sqlQueryParamBuilder.append(" where g.deleted = 0 ");
		if (!CollectionUtils.isEmpty(germplasmMatchRequestDto.getGermplasmUUIDs()) && !CollectionUtils
			.isEmpty(germplasmMatchRequestDto.getNames())) {
			sqlQueryParamBuilder.append(" and (").append(FIND_GERMPLASM_MATCHES_BY_NAMES).append(" or ")
				.append(FIND_GERMPLASM_MATCHES_BY_GUID).append(") ");
			sqlQueryParamBuilder.setParameterList("guidList", germplasmMatchRequestDto.getGermplasmUUIDs());
			sqlQueryParamBuilder.setParameterList("nameList", germplasmMatchRequestDto.getNames());
		} else {
			if (!CollectionUtils.isEmpty(germplasmMatchRequestDto.getGermplasmUUIDs())) {
				sqlQueryParamBuilder.append(" and ").append(FIND_GERMPLASM_MATCHES_BY_GUID);
				sqlQueryParamBuilder.setParameterList("guidList", germplasmMatchRequestDto.getGermplasmUUIDs());
			}
			if (!CollectionUtils.isEmpty(germplasmMatchRequestDto.getNames())) {
				sqlQueryParamBuilder.append(" and ").append(FIND_GERMPLASM_MATCHES_BY_NAMES);
				sqlQueryParamBuilder.setParameterList("nameList", germplasmMatchRequestDto.getNames());
			}
		}
	}

	StringBuilder buildGetExistingCrossesQueryString(final List<Integer> maleParentIds, final Optional<Integer> gid) {
		final StringBuilder builder = new StringBuilder();
		builder.append("SELECT g.gid as gid, (select n.nval from names n where n.nstat=1 and n.gid = g.gid limit 1) as nval ");
		builder.append(" FROM germplsm g ");
		if (maleParentIds.size() > 1) {
			// For Polycrosses
			builder.append("INNER JOIN progntrs p ON p.gid = g.gid ");
			this.addCommonWhereConditions(gid, builder);
			// Check if all the pids in progenitors table are in the maleParentIds list
			builder.append("AND p.pid IN (:maleParentIds) ");
			builder.append("AND g.gpid2 IN ( ");
			// Create a "temporary table" that contains all the specified male parents and return the one that's not in the progenitors table
			builder.append("SELECT parentId FROM ( ");
			builder.append(maleParentIds.stream().map(maleParent -> " (SELECT " + maleParent + " AS parentId) ")
				.collect(Collectors.joining("UNION ALL ")));
			builder.append(") AS maleParentGIDs ");
			builder.append("WHERE parentId not IN (select prog.pid from progntrs prog where prog.gid = g.gid)) ");
			// Make sure that the number of progenitors are the same as the number of male parents specified
			builder.append("group by p.gid having count(distinct p.pid) = " + (maleParentIds.size() - 1));
		} else {
			this.addCommonWhereConditions(gid, builder);
			builder.append("AND g.gpid2 IN (:maleParentIds)");
		}
		return builder;
	}

	private void addCommonWhereConditions(final Optional<Integer> gid, final StringBuilder builder) {
		builder.append("WHERE g.deleted = 0 AND g.gpid1 = :femaleParentId ");
		// Crosses created using design crosses are saved before going back to the FB module,
		// this code excludes the pre-created germplasm from the results
		if (gid.isPresent()) {
			builder.append("AND g.gid <> :gid ");
		}
	}

	public boolean hasExistingCrosses(final Integer femaleParent, final List<Integer> maleParentIds,
		final Optional<Integer> gid) {
		try {
			final StringBuilder builder = new StringBuilder();
			builder.append("SELECT COUNT(1) FROM ( ");
			builder.append(this.buildGetExistingCrossesQueryString(maleParentIds, gid).toString());
			builder.append(") EXISTING_CROSSES");
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(builder.toString());
			sqlQuery.setParameterList("maleParentIds", maleParentIds);
			sqlQuery.setParameter("femaleParentId", femaleParent);
			gid.ifPresent(germplasmId -> sqlQuery.setParameter("gid", germplasmId));

			return ((BigInteger) sqlQuery.uniqueResult()).longValue() > 0;
		} catch (final HibernateException e) {
			final String message = "Error with hasExistingCrosses" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public GermplasmDto getGermplasmDtoByGid(final Integer gid) {
		final StringBuilder queryBuilder =
			new StringBuilder(FIND_GERMPLASM_MATCHES_MAIN_QUERY);
		queryBuilder.append(" WHERE ").append(FIND_GERMPLASM_BY_GIDS);
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
		this.addScalarsToFindGermplasmMatchesQuery(sqlQuery);
		sqlQuery.setParameterList("gids", Collections.singletonList(gid));
		sqlQuery.setResultTransformer(Transformers.aliasToBean(GermplasmDto.class));
		try {
			final Object result = sqlQuery.uniqueResult();
			return (result == null) ? null : (GermplasmDto) result;
		} catch (final HibernateException e) {
			final String message = "Error with getGermplasmDtoByGid" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<GermplasmDto> getGermplasmDtoByGids(final List<Integer> gids) {
		final StringBuilder queryBuilder =
			new StringBuilder(FIND_GERMPLASM_MATCHES_MAIN_QUERY);
		queryBuilder.append(" WHERE ").append(FIND_GERMPLASM_BY_GIDS);
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
		this.addScalarsToFindGermplasmMatchesQuery(sqlQuery);
		sqlQuery.setParameterList("gids", gids);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(GermplasmDto.class));
		try {
			return sqlQuery.list();
		} catch (final HibernateException e) {
			final String message = "Error with getGermplasmDtoByGid" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}
}
