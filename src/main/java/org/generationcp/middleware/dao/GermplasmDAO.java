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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.germplasm.GermplasmDTO;
import org.generationcp.middleware.domain.germplasm.ParentType;
import org.generationcp.middleware.domain.germplasm.PedigreeDTO;
import org.generationcp.middleware.domain.germplasm.ProgenyDTO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search_request.brapi.v1.GermplasmSearchRequestDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.germplasm.GermplasmParent;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAO class for {@link Germplasm}.
 *
 */
public class GermplasmDAO extends GenericDAO<Germplasm, Integer> {

	private static final String GRPLCE = "grplce";
	private static final String DELETED = "deleted";

	private static final String QUERY_FROM_GERMPLASM = ") query from Germplasm: ";

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmDAO.class);
	private static final String SEARCH_GERMPLASM_BY_STUDYDBID =
		"SELECT DISTINCT convert(g.gid, char) AS germplasmDbId, reference.btable AS germplasmPUI, " //
			+ "  (SELECT n.nval FROM names n " //
			+ "   INNER JOIN udflds u ON (u.ftable = 'NAMES' AND u.fcode = 'ACCNO' AND u.fldno = n.ntype)" //
			+ "   WHERE (n.gid = g.gid) LIMIT 1) AS accessionNumber, " //
			+ "   STR_TO_DATE (convert(g.gdate,char), '%Y%m%d') AS acquisitionDate," //
			+ "  (SELECT a.aval FROM atributs a " //
			+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'ORI_COUN' AND u.fldno = a.atype)" //
			+ "   WHERE (a.gid = g.gid) LIMIT 1) AS countryOfOriginCode, " //
			+ "   (SELECT n.nval FROM names n WHERE n.nstat = 1 AND n.gid = g.gid LIMIT 1) AS germplasmName," //
			+ "  (SELECT n.nval FROM names n " //
			+ "   INNER JOIN udflds u ON (u.ftable = 'NAMES' AND u.fcode = 'GENUS' AND u.fldno = n.ntype)" //
			+ "   WHERE (n.gid = g.gid) LIMIT 1) AS genus," //
			+ "   (SELECT ld.source FROM listdata ld" //
			+ "   WHERE ld.gid = g.gid LIMIT 1) AS germplasmSeedSource, " //
			+ "   (SELECT a.aval FROM atributs a " //
			+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'SPNAM' AND u.fldno = a.atype)" //
			+ "   WHERE (a.gid = g.gid) LIMIT 1) AS species, " //
			+ "   (SELECT a.aval FROM atributs a " //
			+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'SPAUTH' AND u.fldno = a.atype)" //
			+ "   WHERE (a.gid = g.gid) LIMIT 1) AS speciesAuthority, " //
			+ "   (SELECT a.aval FROM atributs a " //
			+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'SUBTAX' AND u.fldno = a.atype)" //
			+ "   WHERE (a.gid = g.gid) LIMIT 1) AS subtaxa, " //
			+ "   (SELECT a.aval FROM atributs a " //
			+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'STAUTH' AND u.fldno = a.atype)" //
			+ "   WHERE (a.gid = g.gid) LIMIT 1) AS subtaxaAuthority, " //
			+ "   (SELECT a.aval FROM atributs a " //
			+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'PROGM' AND u.fldno = a.atype)" //
			+ "   WHERE (a.gid = g.gid) LIMIT 1) AS instituteCode, " //
			+ "   m.mname as breedingMethodDbId, " //
			+ "   s.uniquename AS entryNumber "
			+ "  FROM "//
			+ " germplsm g "//
			+ "  LEFT JOIN reflinks reference ON reference.brefid = g.gref " //
			+ "  LEFT join methods m ON g.methn = m.mid " //
			+ " INNER JOIN stock s ON s.dbxref_id = g.gid "//
			+ " INNER JOIN nd_experiment e ON e.stock_id = s.stock_id "//
			+ " INNER JOIN project p ON e.project_id = p.project_id "//
			+ " WHERE g.deleted = 0 AND g.grplce = 0 "//
			+ " AND e.parent_id = :studyDbId "
			+ " ORDER BY CAST(s.uniquename as SIGNED INTEGER) ";

	@Override
	public Germplasm getById(final Integer gid, final boolean lock) {
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
			final String errorMessage = "Error with getById(gid=" + gid + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
			final String errorMessage = "Error with getByName(names=" + names + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
				if (!crossGid.equals(lastGid)){
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
			final List<Germplasm> children = new ArrayList<>();
			// Find generative children (gnpgs > 2)
			final DetachedCriteria generativeChildrenCriteria = DetachedCriteria.forClass(Germplasm.class);
			generativeChildrenCriteria.add(Restrictions.or(Restrictions.eq("gpid1", gid), Restrictions.eq("gpid2", gid)));
			// = Two or more parents
			generativeChildrenCriteria.add(Restrictions.ge("gnpgs", 2));
			// = Record is unchanged
			generativeChildrenCriteria.add(Restrictions.eq(GermplasmDAO.GRPLCE, 0));
			// = Record is not deleted or replaced.
			generativeChildrenCriteria.add(Restrictions.eq(GermplasmDAO.DELETED, Boolean.FALSE));

			children.addAll(generativeChildrenCriteria.getExecutableCriteria(this.getSession()).list());

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

			@SuppressWarnings("unchecked")
			final List<Germplasm> groupMembers = criteria.getExecutableCriteria(this.getSession()).list();
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
				+ "   g.gid as germplasmDbId," //
				+ "   (select n.nval from names n where n.gid = g.gid AND n.nstat = 1) as defaultDisplayName," //
				+ "   CONCAT(m.mcode, '|', m.mname, '|', m.mtype) AS crossingPlan," //
				+ "   year(str_to_date(g.gdate, '%Y%m%d')) as crossingYear," //
				+ "   femaleParent.gid as parent1DbId," //
				+ "   femaleParentName.nval as parent1Name," //
				// If germplasmDbId is a cross (gnpgs > 0), the parents' type should be FEMALE and MALE
				// If germplasmDbId is advanced (gnpgs < 0), the parents type should be POPULATION and SELF
				+ "   CASE WHEN femaleParent.gid is not null AND g.gnpgs > 0 THEN '" + ParentType.FEMALE.name() + "' "
				+ "	  WHEN femaleParent.gid is not null AND g.gnpgs < 0 THEN '" + ParentType.POPULATION.name() + "' ELSE NULL END as parent1Type," //
				+ "   maleParent.gid as parent2DbId," //
				+ "   maleParentName.nval as parent2Name," //
				+ "   CASE WHEN maleParent.gid is not null AND g.gnpgs > 0 THEN '" + ParentType.MALE.name() + "' "
				+ "	  WHEN maleParent.gid is not null AND g.gnpgs < 0  THEN '" + ParentType.SELF.name() + "' ELSE NULL END as parent2Type" //
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
				+ "   sibling.gid AS germplasmDbId," //
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
				+ "   progeny.gid as germplasmDbId," //
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
				+ "                                 OR (progeny.gnpgs >= 2 AND (progeny.gpid1 = parent.gid OR progeny.gpid2 = parent.gid))" //
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
			progenyDTO.setGermplasmDbId(germplasm.getGid());
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
			criteria.add(Restrictions.eq(GermplasmDAO.GRPLCE, 0));
			// Exclude current cross. We are finding "previous" crosses.
			criteria.add(Restrictions.ne("gid", currentCross.getGid()));
			// = Record is not or replaced.
			criteria.add(Restrictions.eq(GermplasmDAO.DELETED, Boolean.FALSE));
			// Oldest created cross will be first in list.
			criteria.addOrder(Order.asc("gid"));

			@SuppressWarnings("unchecked")
			final List<Germplasm> previousCrosses = criteria.getExecutableCriteria(this.getSession()).list();
			return previousCrosses;
		} catch (final HibernateException e) {
			final String message = "Error executing GermplasmDAO.getPreviousCrosses(female = {}, male = {}): {}";
			GermplasmDAO.LOG.error(message, female, male, e.getMessage());
			throw new MiddlewareQueryException(message, e);
		}
	}

	public String getNextSequenceNumberForCrossName(String prefix) {
		String nextInSequence = "1";

		if (!prefix.isEmpty()) {
			try {
				prefix = prefix.trim();
				final StringBuilder sb = new StringBuilder();
				sb.append("SELECT CONVERT(REPLACE(UPPER(nval), :prefix, ''), SIGNED)+1 as next_number ");

				// We used LIKE when matching names by prefix
				sb.append(" FROM ( " + " 	SELECT  distinct nval " + "		FROM names " + "		WHERE names.nval LIKE :prefixLike "
						+ "   	AND NOT EXISTS (select 1 from germplsm g where g.gid = names.gid and g.deleted = 1)" + " ) matches ");
				sb.append(" ORDER BY next_number desc LIMIT 1");

				final SQLQuery query = this.getSession().createSQLQuery(sb.toString());
				query.setParameter("prefix", prefix.toUpperCase());
				query.setParameter("prefixLike", prefix + "%");

				final BigInteger nextNumberInSequence = (BigInteger) query.uniqueResult();

				if (nextNumberInSequence != null) {
					nextInSequence = String.valueOf(nextNumberInSequence);
				}

			} catch (final HibernateException e) {
				final String message = "Error with getNextSequenceNumberForCrossName(prefix=" + prefix + ") " + "query : " + e.getMessage();
				GermplasmDAO.LOG.error(message, e);
				throw new MiddlewareQueryException(message, e);
			}
		}

		return nextInSequence;
	}

	@SuppressWarnings("unchecked")
	public List<Germplasm> getByLocationId(final String name, final int locationID) {
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
			final String message = "Error with getByLocationId(name=" + name + ", locationID=" + locationID
					+ GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	@SuppressWarnings("rawtypes")
	public Germplasm getByGIDWithMethodType(final Integer gid) {
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
	public List<Germplasm> getByGIDRange(final int startGID, final int endGID) {
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
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT {g.*} FROM germplsm g WHERE ");
			queryString.append("g.gid IN( :gids ) ");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("gids", gids);
			query.addEntity("g", Germplasm.class);

			return query.list();

		} catch (final HibernateException e) {
			final String message = "Error with getByGIDList(gids=" + gids.toString() + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Map<Integer, Integer> getGermplasmDatesByGids(final List<Integer> gids) {
		final Map<Integer, Integer> resultMap = new HashMap<>();
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
		final Map<Integer, Integer> resultMap = new HashMap<>();
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

		@SuppressWarnings("rawtypes")
		final List resultNames = queryNames.list();

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

		@SuppressWarnings("rawtypes")
		final List resultGermplasms = queryGermplasms.list();
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
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT g.* FROM germplsm g WHERE g.deleted = 0 AND lgid=:lgid LIMIT 1");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
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

	public void deleteGermplasms(final List<Integer> gids) {
		final StringBuilder queryString = new StringBuilder();

		try {
			this.getSession().flush();
			queryString.append("UPDATE germplsm SET deleted = 1 where gid in (:gids)");
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
	 * @param gids
	 * @return
	 */
	public List<Germplasm> getGermplasmWithoutGroup(final List<Integer> gids) {

		if (gids.isEmpty()) {
			return new ArrayList<>();
		}

		try {
			final StringBuilder queryString = new StringBuilder();
			queryString.append("SELECT {g.*} FROM germplsm g WHERE ");
			queryString.append("g.gid IN( :gids ) AND (g.mgid = 0 || g.mgid IS NULL)");

			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
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

	public GermplasmDTO getGermplasmDTO(final Integer id) {

		try {
			final String sql = "SELECT convert(g.gid, char) AS germplasmDbId, reference.btable AS germplasmPUI, " //
					+ "  (SELECT n.nval FROM names n " //
					+ "   INNER JOIN udflds u ON (u.ftable = 'NAMES' AND u.fcode = 'ACCNO' AND u.fldno = n.ntype)" //
					+ "   WHERE (n.gid = g.gid) LIMIT 1) AS accessionNumber, " //
					+ "   STR_TO_DATE (convert(g.gdate,char), '%Y%m%d') AS acquisitionDate," //
					+ "  (SELECT a.aval FROM atributs a " //
					+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'ORI_COUN' AND u.fldno = a.atype)" //
					+ "   WHERE (a.gid = g.gid) LIMIT 1) AS countryOfOriginCode, " //
					+ "   (SELECT n.nval FROM names n WHERE n.nstat = 1 AND n.gid = g.gid LIMIT 1) AS germplasmName," //
					+ "  (SELECT n.nval FROM names n " //
					+ "   INNER JOIN udflds u ON (u.ftable = 'NAMES' AND u.fcode = 'GENUS' AND u.fldno = n.ntype)" //
					+ "   WHERE (n.gid = g.gid) LIMIT 1) AS genus," //
					+ "   (SELECT ld.source FROM listdata ld" //
					+ "   WHERE ld.gid = g.gid LIMIT 1) AS germplasmSeedSource, " //
					+ "   (SELECT a.aval FROM atributs a " //
					+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'SPNAM' AND u.fldno = a.atype)" //
					+ "   WHERE (a.gid = g.gid) LIMIT 1) AS species, " //
					+ "   (SELECT a.aval FROM atributs a " //
					+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'SPAUTH' AND u.fldno = a.atype)" //
					+ "   WHERE (a.gid = g.gid) LIMIT 1) AS speciesAuthority, " //
					+ "   (SELECT a.aval FROM atributs a " //
					+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'SUBTAX' AND u.fldno = a.atype)" //
					+ "   WHERE (a.gid = g.gid) LIMIT 1) AS subtaxa, " //
					+ "   (SELECT a.aval FROM atributs a " //
					+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'STAUTH' AND u.fldno = a.atype)" //
					+ "   WHERE (a.gid = g.gid) LIMIT 1) AS subtaxaAuthority, " //
					+ "   (SELECT a.aval FROM atributs a " //
					+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'PROGM' AND u.fldno = a.atype)" //
					+ "   WHERE (a.gid = g.gid) LIMIT 1) AS instituteCode " //
					+ "  FROM germplsm g " //
					+ "     LEFT JOIN reflinks reference ON reference.brefid = g.gref " //
					+ "  WHERE g.gid = :gid and g.deleted = 0 AND g.grplce = 0";

			final Object object = this.getSession().createSQLQuery(sql) //
				.addScalar("germplasmDbId").addScalar("germplasmPUI").addScalar("accessionNumber").addScalar("acquisitionDate")
				.addScalar("countryOfOriginCode").addScalar("germplasmName").addScalar("genus").addScalar("germplasmSeedSource")
				.addScalar("species").addScalar("speciesAuthority").addScalar("subtaxa").addScalar("subtaxaAuthority").addScalar(
					"instituteCode") //
				.setParameter("gid", id) //
				.setResultTransformer(new AliasToBeanResultTransformer(GermplasmDTO.class)) //
				.uniqueResult();
			return (object != null) ? (GermplasmDTO) object : null;
		} catch (final HibernateException e) {
			final String message = "Error with getGermplasmDTO(gid=" + id.toString() + ") " + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<GermplasmDTO> getGermplasmDTOList(
		final GermplasmSearchRequestDto germplasmSearchRequestDTO, final Integer page, final Integer pageSize) {

		try {

			String queryString = "SELECT convert(g.gid, char) AS germplasmDbId, reference.btable AS germplasmPUI, " //
				+ "  (SELECT n.nval FROM names n " //
				+ "   INNER JOIN udflds u ON (u.ftable = 'NAMES' AND u.fcode = 'ACCNO' AND u.fldno = n.ntype)" //
				+ "   WHERE (n.gid = g.gid) LIMIT 1) AS accessionNumber, " //
				+ "   STR_TO_DATE (convert(g.gdate,char), '%Y%m%d') AS acquisitionDate," //
				+ "  (SELECT a.aval FROM atributs a " //
				+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'ORI_COUN' AND u.fldno = a.atype)" //
				+ "   WHERE (a.gid = g.gid) LIMIT 1) AS countryOfOriginCode, " //
				+ "   (SELECT n.nval FROM names n WHERE n.nstat = 1 AND n.gid = g.gid LIMIT 1) AS germplasmName," //
				+ "  (SELECT n.nval FROM names n " //
				+ "   INNER JOIN udflds u ON (u.ftable = 'NAMES' AND u.fcode = 'GENUS' AND u.fldno = n.ntype)" //
				+ "   WHERE (n.gid = g.gid) LIMIT 1) AS genus," //
				+ "   (SELECT ld.source FROM listdata ld" //
				+ "   WHERE ld.gid = g.gid LIMIT 1) AS germplasmSeedSource, " //
				+ "   (SELECT a.aval FROM atributs a " //
				+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'SPNAM' AND u.fldno = a.atype)" //
				+ "   WHERE (a.gid = g.gid) LIMIT 1) AS species, " //
				+ "   (SELECT a.aval FROM atributs a " //
				+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'SPAUTH' AND u.fldno = a.atype)" //
				+ "   WHERE (a.gid = g.gid) LIMIT 1) AS speciesAuthority, " //
				+ "   (SELECT a.aval FROM atributs a " //
				+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'SUBTAX' AND u.fldno = a.atype)" //
				+ "   WHERE (a.gid = g.gid) LIMIT 1) AS subtaxa, " //
				+ "   (SELECT a.aval FROM atributs a " //
				+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'STAUTH' AND u.fldno = a.atype)" //
				+ "   WHERE (a.gid = g.gid) LIMIT 1) AS subtaxaAuthority, " //
				+ "   (SELECT a.aval FROM atributs a " //
				+ "   INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'PROGM' AND u.fldno = a.atype)" //
				+ "   WHERE (a.gid = g.gid) LIMIT 1) AS instituteCode, " //
				+ "   m.mname as breedingMethodDbId " //
				+ "  FROM germplsm g " //
				+ "  	LEFT JOIN reflinks reference ON reference.brefid = g.gref " //
				+ "  	LEFT join methods m ON g.methn = m.mid " //
				+ "	 WHERE g.deleted = 0" //
				+ "      AND g.grplce = 0"; //

			if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getPreferredName())) {
				queryString = queryString
					+ "      AND (SELECT n.nval" //
					+ "                    FROM names n" //
					+ "                    WHERE n.nstat = 1 AND n.gid = g.gid" //
					+ "                    LIMIT 1) like :likeCondition "; //
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getAccessionNumbers())) {
				queryString = queryString + " AND EXISTS (SELECT 1" //
					+ "         FROM names n" //
					+ "                  INNER JOIN udflds u ON (u.ftable = 'NAMES' AND u.fcode = 'ACCNO' AND u.fldno = n.ntype)" //
					+ "         WHERE n.gid = g.gid AND n.nval IN (:accessionNumbers)) "; //
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getCommonCropNames())) {
				queryString = queryString + " AND EXISTS (SELECT 1" //
					+ "         FROM atributs a" //
					+ "                  INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'CROPNM' AND u.fldno = a.atype)" //
					+ "         WHERE a.gid = g.gid AND a.aval IN (:commonCropNames)) "; //
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmDbIds())) {
				queryString = queryString + " AND g.gid IN (:germplasmDbIds) ";
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmGenus())) {
				queryString = queryString + " AND EXISTS (SELECT 1" //
					+ "         FROM names n" //
					+ "                  INNER JOIN udflds u ON (u.ftable = 'NAMES' AND u.fcode = 'GENUS' AND u.fldno = n.ntype)" //
					+ "         WHERE n.gid = g.gid AND n.nval IN (:germplasmGenus)) "; //
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmNames())) {
				queryString = queryString + " AND EXISTS (SELECT 1" //
					+ "         FROM names n" //
					+ "         WHERE n.gid = g.gid AND n.nval IN (:germplasmNames)) "; //
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmPUIs())) {
				queryString = queryString + " AND reference.btable IN (:germplasmPUIs) "; //
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmSpecies())) {
				queryString = queryString + " AND EXISTS (SELECT 1" //
					+ "         FROM atributs a" //
					+ "                  INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'SPNAM' AND u.fldno = a.atype)" //
					+ "         WHERE a.gid = g.gid AND a.aval IN (:germplasmSpecies)) "; //
			}

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryString);

			sqlQuery.addScalar("germplasmDbId").addScalar("germplasmPUI").addScalar("accessionNumber").addScalar("acquisitionDate")
				.addScalar("countryOfOriginCode").addScalar("germplasmName").addScalar("genus").addScalar("germplasmSeedSource")
				.addScalar("species").addScalar("speciesAuthority").addScalar("subtaxa").addScalar("subtaxaAuthority")
				.addScalar("instituteCode").addScalar("breedingMethodDbId") //
				.setResultTransformer(new AliasToBeanResultTransformer(GermplasmDTO.class));

			if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getPreferredName())) {
				sqlQuery.setParameter("likeCondition", "%" + germplasmSearchRequestDTO.getPreferredName() + "%"); //
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getAccessionNumbers())) {
				sqlQuery.setParameterList("accessionNumbers", germplasmSearchRequestDTO.getAccessionNumbers());
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getCommonCropNames())) {
				sqlQuery.setParameterList("commonCropNames", germplasmSearchRequestDTO.getCommonCropNames());
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmDbIds())) {
				sqlQuery.setParameterList("germplasmDbIds", germplasmSearchRequestDTO.getGermplasmDbIds());
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmGenus())) {
				sqlQuery.setParameterList("germplasmGenus", germplasmSearchRequestDTO.getGermplasmGenus());
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmNames())) {
				sqlQuery.setParameterList("germplasmNames", germplasmSearchRequestDTO.getGermplasmNames());
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmPUIs())) {
				sqlQuery.setParameterList("germplasmPUIs", germplasmSearchRequestDTO.getGermplasmPUIs());
			}

			if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmSpecies())) {
				sqlQuery.setParameterList("germplasmSpecies", germplasmSearchRequestDTO.getGermplasmSpecies());
			}

			if (page != null && pageSize != null) {
				sqlQuery.setFirstResult(pageSize * page);
				sqlQuery.setMaxResults(pageSize);
			}

			final List<GermplasmDTO> germplasmDTOList = sqlQuery.list();

			return germplasmDTOList;

		} catch (final HibernateException e) {
			final String message = "Error with getGermplasmDTOList" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public long countGermplasmDTOs(final GermplasmSearchRequestDto germplasmSearchRequestDTO) {

		String queryString = "SELECT COUNT(1) "
			+ "  FROM germplsm g " //
			+ "  LEFT JOIN reflinks reference ON reference.brefid = g.gref WHERE g.deleted = 0 AND g.grplce = 0"; //

		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getPreferredName())) {
			queryString = queryString
				+ "      AND (SELECT n.nval" //
				+ "                    FROM names n" //
				+ "                    WHERE n.nstat = 1 AND n.gid = g.gid" //
				+ "                    LIMIT 1) like :likeCondition "; //
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getAccessionNumbers())) {
			queryString = queryString + " AND EXISTS (SELECT 1" //
				+ "         FROM names n" //
				+ "                  INNER JOIN udflds u ON (u.ftable = 'NAMES' AND u.fcode = 'ACCNO' AND u.fldno = n.ntype)" //
				+ "         WHERE n.gid = g.gid AND n.nval IN (:accessionNumbers)) "; //
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getCommonCropNames())) {
			queryString = queryString + " AND EXISTS (SELECT 1" //
				+ "         FROM atributs a" //
				+ "                  INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'CROPNM' AND u.fldno = a.atype)" //
				+ "         WHERE a.gid = g.gid AND a.aval IN (:commonCropNames)) "; //
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmDbIds())) {
			queryString = queryString + " AND g.gid IN (:germplasmDbIds) ";
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmGenus())) {
			queryString = queryString + " AND EXISTS (SELECT 1" //
				+ "         FROM names n" //
				+ "                  INNER JOIN udflds u ON (u.ftable = 'NAMES' AND u.fcode = 'GENUS' AND u.fldno = n.ntype)" //
				+ "         WHERE n.gid = g.gid AND n.nval IN (:germplasmGenus)) "; //
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmNames())) {
			queryString = queryString + " AND EXISTS (SELECT 1" //
				+ "         FROM names n" //
				+ "         WHERE n.gid = g.gid AND n.nval IN (:germplasmNames)) "; //
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmPUIs())) {
			queryString = queryString + " AND reference.btable IN (:germplasmPUIs) "; //
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmSpecies())) {
			queryString = queryString + " AND EXISTS (SELECT 1" //
				+ "         FROM atributs a" //
				+ "                  INNER JOIN udflds u ON (u.ftable = 'ATRIBUTS' AND u.fcode = 'SPNAM' AND u.fldno = a.atype)" //
				+ "         WHERE a.gid = g.gid AND a.aval IN (:germplasmSpecies)) "; //
		}

		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryString);

		if (StringUtils.isNoneBlank(germplasmSearchRequestDTO.getPreferredName())) {
			sqlQuery.setParameter("likeCondition", "%" + germplasmSearchRequestDTO.getPreferredName() + "%"); //
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getAccessionNumbers())) {
			sqlQuery.setParameterList("accessionNumbers", germplasmSearchRequestDTO.getAccessionNumbers());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getCommonCropNames())) {
			sqlQuery.setParameterList("commonCropNames", germplasmSearchRequestDTO.getCommonCropNames());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmDbIds())) {
			sqlQuery.setParameterList("germplasmDbIds", germplasmSearchRequestDTO.getGermplasmDbIds());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmGenus())) {
			sqlQuery.setParameterList("germplasmGenus", germplasmSearchRequestDTO.getGermplasmGenus());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmNames())) {
			sqlQuery.setParameterList("germplasmNames", germplasmSearchRequestDTO.getGermplasmNames());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmPUIs())) {
			sqlQuery.setParameterList("germplasmPUIs", germplasmSearchRequestDTO.getGermplasmPUIs());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequestDTO.getGermplasmSpecies())) {
			sqlQuery.setParameterList("germplasmSpecies", germplasmSearchRequestDTO.getGermplasmSpecies());
		}

		return ((BigInteger) sqlQuery.uniqueResult()).longValue();

	}

	public long countGermplasmByStudy(final Integer studyDbId) {
		final String queryString = "select count(1) from (" + SEARCH_GERMPLASM_BY_STUDYDBID + ") AS T ";//

		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryString);
		sqlQuery.setParameter("studyDbId", studyDbId);

		return ((BigInteger) sqlQuery.uniqueResult()).longValue();
	}

	public List<GermplasmDTO> getGermplasmByStudy(final Integer studyDbId, final Integer pageNumber, final Integer pageSize) {
		try {
			final String queryString = SEARCH_GERMPLASM_BY_STUDYDBID; //

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryString);
			sqlQuery.setParameter("studyDbId", studyDbId);

			sqlQuery.addScalar("germplasmDbId").addScalar("germplasmPUI").addScalar("accessionNumber").addScalar("acquisitionDate")
				.addScalar("countryOfOriginCode").addScalar("germplasmName").addScalar("genus").addScalar("germplasmSeedSource")
				.addScalar("species").addScalar("speciesAuthority").addScalar("subtaxa").addScalar("subtaxaAuthority")
				.addScalar("instituteCode").addScalar("breedingMethodDbId").addScalar("entryNumber") //
				.setResultTransformer(new AliasToBeanResultTransformer(GermplasmDTO.class));

			if (pageNumber != null && pageSize != null) {
				sqlQuery.setFirstResult(pageSize * pageNumber);
				sqlQuery.setMaxResults(pageSize);
			}

			final List<GermplasmDTO> germplasmDTOList = sqlQuery.list();

			return germplasmDTOList;

		} catch (final HibernateException e) {
			final String message = "Error with getGermplasmDTOList" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public Map<Integer, StudyGermplasmDto> getPlotNoToStudyGermplasmDtoMap(final Integer studyId, final Set<Integer> plotNos) {
		final Map<Integer, StudyGermplasmDto> plotNoToImportedGermplasmParentMap = new HashMap<>();

		String queryString = "select  distinct(nd_ep.value) AS position, s.name AS designation, s.dbxref_id AS germplasmId "
			+ " FROM nd_experiment e "
			+ " INNER JOIN nd_experimentprop nd_ep ON e.nd_experiment_id = nd_ep.nd_experiment_id "
			+ " INNER JOIN stock s ON s.stock_id = e.stock_id "
			+ " INNER JOIN project p ON e.project_id = p.project_id "
			+ " WHERE nd_ep.type_id IN (:PLOT_NO_TERM_IDS) "
			+ " AND p.dataset_type_id = :DATASET_TYPE "
			+ " AND p.study_id = :STUDY_ID "
			+ " AND nd_ep.value in (:PLOT_NOS) "
			+ " AND nd_ep.nd_experiment_id = e.nd_experiment_id";

		final SQLQuery query = this.getSession().createSQLQuery(queryString);
		query.setParameter("STUDY_ID", studyId);
		query.setParameterList("PLOT_NOS", plotNos);
		query.setParameter("DATASET_TYPE", DatasetTypeEnum.PLOT_DATA.getId());
		query.setParameterList("PLOT_NO_TERM_IDS",
			new Integer[] { TermId.PLOT_NO.getId(), TermId.PLOT_NNO.getId() });
		query.addScalar("position", new StringType());
		query.addScalar("designation", new StringType());
		query.addScalar("germplasmId", new IntegerType());
		query.setResultTransformer(Transformers.aliasToBean(StudyGermplasmDto.class));
		final List<StudyGermplasmDto> result = query.list();
		for(StudyGermplasmDto parent: result) {
			plotNoToImportedGermplasmParentMap.put(Integer.valueOf(parent.getPosition()), parent);
		}
		return plotNoToImportedGermplasmParentMap;
	}
}
