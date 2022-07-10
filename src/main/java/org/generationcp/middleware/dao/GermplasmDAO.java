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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.brapi.v1.germplasm.GermplasmDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.GermplasmImportRequest;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeSearchRequest;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.domain.germplasm.GermplasmMergedDto;
import org.generationcp.middleware.domain.germplasm.ParentType;
import org.generationcp.middleware.domain.germplasm.PedigreeDTO;
import org.generationcp.middleware.domain.germplasm.ProgenyDTO;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmMatchRequestDto;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.search_request.brapi.v2.GermplasmSearchRequest;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.manager.GermplasmDataManagerUtil;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.MethodType;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.Progenitor;
import org.generationcp.middleware.pojos.germplasm.GermplasmParent;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.generationcp.middleware.util.Util;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
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

	private static final String VARIABLE_ID = "vid";
	private static final String VARIABLE_NAME = "vn";
	private static final String VARIABLE_DEFINITION = "vd";

	private static final String METHOD_ID = "mid";
	private static final String METHOD_NAME = "mn";
	private static final String METHOD_DEFINITION = "md";

	private static final String PROPERTY_ID = "pid";
	private static final String PROPERTY_NAME = "pn";
	private static final String PROPERTY_DEFINITION = "pd";

	private static final String SCALE_ID = "sid";
	private static final String SCALE_NAME = "sn";
	private static final String SCALE_DEFINITION = "sd";

	private static final String VARIABLE_ALIAS = "p_alias";
	private static final String VARIABLE_EXPECTED_MAX = "p_max_value";
	private static final String VARIABLE_EXPECTED_MIN = "p_min_value";

	private static final String PROGRAM_UUID = "programUUID";
	private static final String GIDS = "gids";

	private static final String DER_MAN = "'" + MethodType.DERIVATIVE.getCode() + "','" + MethodType.MAINTENANCE.getCode() + "'";

	private static final String GEN = "'" + MethodType.GENERATIVE.getCode() + "'";

	private static final String QUERY_FROM_GERMPLASM = ") query from Germplasm: ";

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmDAO.class);

	private static final String PUI_FROM_QUERY = " from names p INNER JOIN udflds u " //
		+ " ON u.fldno = p.ntype AND u.ftable = 'NAMES' and u.ftype='NAME' and u.fcode = 'PUI' and p.nstat <> 9 ";

	private static final String FIND_GERMPLASM_MATCHES_MAIN_QUERY = "select " //
		+ "    g.gid as gid, " //
		+ "    g.germplsm_uuid as germplasmUUID, " //
		+ "    n.nval as preferredName, " //
		+ "    pui.nval as germplasmPUI, "
		+ "    cast(g.gdate as char) as creationDate, " //
		+ "    r.analyt as reference, " //
		+ "    l.locid as breedingLocationId, " //
		+ "    l.lname as breedingLocation, " //
		+ "    m.mid as breedingMethodId, " //
		+ "    m.mname as breedingMethod, " //
		+ "    if(g.mgid > 0, true, false) as isGroupedLine, " //
		+ "    g.mgid as groupId, " //
		+ "    g.gpid1 as gpid1, "  //
		+ "    g.gpid2 as gpid2, "
		+ "    g.gnpgs as numberOfProgenitors, "
		+ "    g.created_by as createdByUserId " //
		+ "    from " //
		+ "    germplsm g " //
		+ "        left join " //
		+ "    methods m on m.mid = g.methn " //
		+ "        left join " //
		+ "    location l on l.locid = g.glocn " //
		+ "        left join " //
		+ "    names n on n.gid = g.gid and n.nstat = 1 " //
		+ " 		left join ("
		+ " 	select p.gid, p.nval " + PUI_FROM_QUERY + ") pui on pui.gid = g.gid "
		+ "        left join " //
		+ "    bibrefs r on g.gref = r.refid ";

	public GermplasmDAO(final Session session) {
		super(session);
	}

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
	 * @return gids with progeny
	 */
	public Set<Integer> getGidsOfGermplasmWithDescendants(final Set<Integer> gids) {
		try {
			if (!CollectionUtils.isEmpty(gids)) {
				final SQLQuery query = this.getSession().createSQLQuery("SELECT g.gid as gid FROM germplsm g "
					+ "WHERE (EXISTS (SELECT 1 FROM germplsm descendant WHERE descendant.deleted = 0 AND "
					+ " g.gid = descendant.gpid1) "
					+ " OR EXISTS (SELECT 1 FROM germplsm descendant WHERE descendant.deleted = 0 AND "
					+ " g.gid = descendant.gpid2) "
					+ " OR EXISTS (SELECT 1 FROM progntrs p INNER JOIN germplsm descendant ON descendant.gid = p.gid"
					+ " 					WHERE  g.gid = p.pid  AND descendant.deleted = 0)) "
					+ " AND g.gid IN (:gids) AND  g.deleted = 0 AND g.grplce = 0 ");
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

	public List<Germplasm> getDescendants(final Integer gid, final char methodType) {
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
			final String errorMessage =
				"Error with getDescendants(gid=" + gid + ", methodType=" + methodType + ") query: " + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return toreturn;

	}

	public List<Germplasm> getAllChildren(final Integer gid) {
		try {
			final List<Germplasm> children = new ArrayList<>();
			// Get all derivative children
			children.addAll(this.getDescendants(gid, 'D'));

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

			final List<Germplasm> children =
				new ArrayList<>(generativeChildrenCriteria.getExecutableCriteria(this.getSession()).list());

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

	public List<PedigreeNodeDTO> searchPedigreeNodes(final PedigreeNodeSearchRequest pedigreeNodeSearchRequest, final Pageable pageable) {
		final List<PedigreeNodeDTO> pedigreeNodeDTOList = new ArrayList<>();

		if (CollectionUtils.isEmpty(pedigreeNodeSearchRequest.getGermplasmDbIds())) {
			return pedigreeNodeDTOList;
		}

		final String query = "SELECT "
			+ "   {g.*},"
			+ "   (select n.nval from names n where n.gid = g.gid AND n.nstat = 1) as defaultDisplayName,"
			+ "   year(str_to_date(g.gdate, '%Y%m%d')) as crossingYear,"
			+ "   femaleParentName.nval as parent1Name,"
			+ "   maleParentName.nval as parent2Name"
			+ "   FROM germplsm g"
			+ "   LEFT JOIN names femaleParentName ON g.gpid1 = femaleParentName.gid AND femaleParentName.nstat = 1"
			+ "   LEFT JOIN names maleParentName ON g.gpid2 = maleParentName.gid AND maleParentName.nstat = 1"
			+ " WHERE g.germplsm_uuid IN (:germplasmDbIds) AND g.deleted = 0 AND g.grplce = 0";

		final SQLQuery sqlQuery = this.getSession().createSQLQuery(query);
		sqlQuery.addEntity("g", Germplasm.class);
		sqlQuery.addScalar("defaultDisplayName");
		sqlQuery.addScalar("crossingYear", new IntegerType());
		sqlQuery.addScalar("parent1Name");
		sqlQuery.addScalar("parent2Name");

		// TODO: This is an initial implementation of search pedigree nodes. Only support filtering by germplasmDbIds for now.
		sqlQuery.setParameterList("germplasmDbIds", pedigreeNodeSearchRequest.getGermplasmDbIds());

		final List<Object[]> results = sqlQuery.list();

		for (final Object[] row : results) {

			final Germplasm germplasm = (Germplasm) row[0];
			final String defaultDisplayName = (String) row[1];
			final Integer crossingYear = (Integer) row[2];
			final String parent1Name = (String) row[3];
			final String parent2Name = (String) row[4];

			final PedigreeNodeDTO pedigreeNodeDTO = new PedigreeNodeDTO();
			pedigreeNodeDTO.setGid(germplasm.getGid());
			pedigreeNodeDTO.setGermplasmDbId(germplasm.getGermplasmUUID());
			pedigreeNodeDTO.setDefaultDisplayName(defaultDisplayName);
			pedigreeNodeDTO.setBreedingMethodDbId(String.valueOf(germplasm.getMethod().getMid()));
			pedigreeNodeDTO.setBreedingMethodName(germplasm.getMethod().getMname());
			pedigreeNodeDTO.setCrossingYear(crossingYear);
			pedigreeNodeDTO.setParents(this.createParents(germplasm, parent1Name, parent2Name));
			pedigreeNodeDTOList.add(pedigreeNodeDTO);

		}

		return pedigreeNodeDTOList;
	}

	private List<PedigreeNodeReferenceDTO> createParents(final Germplasm germplasm, final String parent1Name,
		final String parent2Name) {
		final List<PedigreeNodeReferenceDTO> parents = new ArrayList<>();

		final PedigreeNodeReferenceDTO femalParentReference = new PedigreeNodeReferenceDTO();
		femalParentReference.setGermplasmDbId(germplasm.getFemaleParent() != null ? germplasm.getFemaleParent().getGermplasmUUID() : null);
		femalParentReference.setGermplasmName(parent1Name);
		if (germplasm.getGnpgs() > 0) {
			femalParentReference.setParentType(ParentType.FEMALE.name());
		} else {
			femalParentReference.setParentType(ParentType.POPULATION.name());
		}
		parents.add(femalParentReference);

		final PedigreeNodeReferenceDTO maleParentReference = new PedigreeNodeReferenceDTO();
		maleParentReference.setGermplasmDbId(germplasm.getMaleParent() != null ? germplasm.getMaleParent().getGermplasmUUID() : null);
		maleParentReference.setGermplasmName(parent2Name);
		if (germplasm.getGnpgs() > 0) {
			maleParentReference.setParentType(ParentType.MALE.name());
		} else {
			maleParentReference.setParentType(ParentType.SELF.name());
		}
		parents.add(maleParentReference);

		for (final Progenitor progenitor : germplasm.getOtherProgenitors()) {
			if (progenitor.getProgenitorGermplasm() != null) {
				parents.add(
					new PedigreeNodeReferenceDTO(progenitor.getProgenitorGermplasm().getGermplasmUUID(), "", ParentType.MALE.name()));
			}
		}
		return parents;
	}

	public PedigreeDTO getPedigree(final Integer gid, final String notation, final Boolean includeSiblings) {
		try {
			final String query = "SELECT "
				+ "   g.germplsm_uuid as germplasmDbId,"
				+ "   (select n.nval from names n where n.gid = g.gid AND n.nstat = 1) as defaultDisplayName," //
				+ "   CONCAT(m.mcode, '|', m.mname, '|', m.mtype) AS crossingPlan," //
				+ "   year(str_to_date(g.gdate, '%Y%m%d')) as crossingYear," //
				+ "   femaleParent.germplsm_uuid as parent1DbId," //
				+ "   femaleParentName.nval as parent1Name," //
				// If germplasmDbId is a cross (gnpgs > 0), the parents' type should be FEMALE and MALE
				// If germplasmDbId is advanced (gnpgs < 0), the parents type should be POPULATION and SELF
				+ "   CASE WHEN femaleParent.gid is not null AND g.gnpgs > 0 THEN '" + ParentType.FEMALE.name() + "' "
				+ "	  WHEN femaleParent.gid is not null AND g.gnpgs < 0 THEN '" + ParentType.POPULATION.name()
				+ "' ELSE NULL END as parent1Type," //
				+ "   maleParent.germplsm_uuid as parent2DbId," //
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
				.setParameter("gid", gid) //
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
				.setParameter("gid", gid)
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
			final String message = "Error with getByGIDList(gids=" + gids + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
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
				"Error with getByUUIDListWithMethodAndBibref(gids=" + uuids + GermplasmDAO.QUERY_FROM_GERMPLASM + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	/**
	 * @return first germplasm (even deleted) find by method
	 */
	public Optional<Germplasm> findOneByMethodId(final int breedingMethodDbId) {
		return Optional.ofNullable((Germplasm) this.getSession().createCriteria(this.getPersistentClass())
			.add(Restrictions.eq("method.mid", breedingMethodDbId))
			.setMaxResults(1)
			.uniqueResult());
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
		this.deleteGermplasm(gids, null);
	}

	public void deleteGermplasm(final List<Integer> gids, final Integer germplasmReplaceGid) {
		final StringBuilder queryString = new StringBuilder();
		try {
			queryString.append("UPDATE germplsm SET ");
			queryString.append("deleted = 1, ");
			queryString.append("germplsm_uuid = CONCAT (germplsm_uuid, '#', '" +
				Util.getCurrentDateAsStringValue("yyyyMMddHHmmssSSS") + "'), ");
			queryString.append("modified_date = CURRENT_TIMESTAMP, ");
			queryString.append("modified_by = :userId ");
			if (germplasmReplaceGid != null) {
				queryString.append(", grplce = :germplasmReplaceGid ");
			}
			queryString.append("WHERE gid in (:gids) ");
			final SQLQuery query = this.getSession().createSQLQuery(queryString.toString());
			query.setParameterList("gids", gids);
			query.setParameter("userId", ContextHolder.getLoggedInUserId());
			if (germplasmReplaceGid != null) {
				query.setParameter("germplasmReplaceGid", germplasmReplaceGid);
			}
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
						resultMap.put(parent, new HashSet<>());
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
			final String message = "Error with getGermplasmWithoutGroup(gids=" + gids + ") " + e.getMessage();
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
		if (CollectionUtils.isEmpty(gids)) {
			return;
		}

		try {

			final SQLQuery query = this.getSession().createSQLQuery("UPDATE germplsm SET mgid = 0, "
				+ "modified_date = CURRENT_TIMESTAMP, modified_by = :userId WHERE gid IN (:gids)");
			query.setParameterList("gids", gids);
			query.setParameter("userId", ContextHolder.getLoggedInUserId());
			query.executeUpdate();

		} catch (final HibernateException e) {
			final String message = "Error with resetGermplasmGroup(gids=" + gids + ") " + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}

	}

	private void addGermplasmSearchParameters(final SqlQueryParamBuilder paramBuilder,
		final GermplasmSearchRequest germplasmSearchRequest) {
		if (StringUtils.isNoneBlank(germplasmSearchRequest.getPreferredName())) {
			paramBuilder.setParameter("preferredName", germplasmSearchRequest.getPreferredName() + "%"); //
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getAccessionNumbers())) {
			paramBuilder.setParameterList("accessionNumbers", germplasmSearchRequest.getAccessionNumbers());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getCommonCropNames())) {
			paramBuilder.setParameterList("commonCropNames", germplasmSearchRequest.getCommonCropNames());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getGermplasmDbIds())) {
			paramBuilder.setParameterList("germplasmDbIds", germplasmSearchRequest.getGermplasmDbIds());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getGenus())) {
			paramBuilder.setParameterList("germplasmGenus", germplasmSearchRequest.getGenus());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getGermplasmPUIs())) {
			paramBuilder.setParameterList("germplasmPUIs", germplasmSearchRequest.getGermplasmPUIs());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getSynonyms())) {
			paramBuilder.setParameterList("synonyms", germplasmSearchRequest.getSynonyms());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getGermplasmNames())) {
			paramBuilder.setParameterList("germplasmNames", germplasmSearchRequest.getGermplasmNames());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getSpecies())) {
			paramBuilder.setParameterList("germplasmSpecies", germplasmSearchRequest.getSpecies());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getStudyDbIds())) {
			paramBuilder.setParameterList("studyDbIds", germplasmSearchRequest.getStudyDbIds());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getParentDbIds())) {
			paramBuilder.setParameterList("parentDbIds", germplasmSearchRequest.getParentDbIds());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getProgenyDbIds())) {
			paramBuilder.setParameterList("progenyDbIds", germplasmSearchRequest.getProgenyDbIds());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getExternalReferenceSources())) {
			paramBuilder.setParameterList("referenceSources", germplasmSearchRequest.getExternalReferenceSources());
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getExternalReferenceIDs())) {
			paramBuilder.setParameterList("referenceIds", germplasmSearchRequest.getExternalReferenceIDs());
		}
	}

	// These are appended to the MAIN where clause because they are filters on (or EXISTS clause related to) the main germplasm object
	private void addGermplasmSearchRequestFilters(final SqlQueryParamBuilder paramBuilder,
		final GermplasmSearchRequest germplasmSearchRequest) {
		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getGermplasmDbIds())) {
			paramBuilder.append(" AND g.germplsm_uuid IN (:germplasmDbIds) ");
		}

		// Search synonyms or non-default names
		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getSynonyms())) {
			paramBuilder.append(" AND g.gid IN ( SELECT n.gid ");
			paramBuilder.append(" FROM names n WHERE n.nval IN (:synonyms) and n.nstat != 9 and n.nstat != 1 ) "); //
		}

		// Search preferred names
		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getGermplasmNames())) {
			paramBuilder.append(" AND g.gid IN ( SELECT n.gid ");
			paramBuilder.append(" FROM names n WHERE n.nstat = 1 AND n.nval in (:germplasmNames) ) ");
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getStudyDbIds())) {
			paramBuilder.append(" AND g.gid IN ( "
				+ "  SELECT s.dbxref_id from stock s "
				+ "   INNER JOIN nd_experiment e ON e.stock_id = s.stock_id "
				+ "   WHERE e.nd_geolocation_id in (:studyDbIds)) ");
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getParentDbIds())) {
			paramBuilder.append(" AND (g.gpid1 = (select parent.gid from germplsm parent where parent.germplsm_uuid in (:parentDbIds)) "
				+ "OR g.gpid2 = (select parent.gid from germplsm parent where parent.germplsm_uuid in (:parentDbIds)) "
				+ "OR g.gid = (Select p.gid from progntrs p INNER JOIN germplsm parent ON p.pid = parent.gid "
				+ " WHERE parent.germplsm_uuid in (:parentDbIds))) AND g.gnpgs >= 1");
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getProgenyDbIds())) {
			paramBuilder
				.append(
					" AND ( g.gid = (SELECT gpid1 from germplsm child WHERE child.germplsm_uuid in (:progenyDbIds) AND child.gnpgs >= 1) "
						+ "OR g. gid = (SELECT gpid2 from germplsm child WHERE child.germplsm_uuid in (:progenyDbIds) AND child.gnpgs >= 1) "
						+ "OR g.gid = (Select p.pid from progntrs p INNER JOIN germplsm child ON p.gid = child.gid "
						+ "WHERE child.germplsm_uuid in (:progenyDbIds))) ");
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getExternalReferenceIDs())) {
			paramBuilder.append(" AND g.gid IN ( ");
			paramBuilder.append(" SELECT ref.gid FROM external_reference_germplasm ref WHERE ref.reference_id in (:referenceIds)) "); //
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getExternalReferenceSources())) {
			paramBuilder.append(" AND g.gid IN (  ");
			paramBuilder.append(
				" SELECT ref.gid FROM external_reference_germplasm ref WHERE ref.reference_source in (:referenceSources)) "); //
		}

		if (StringUtils.isNoneBlank(germplasmSearchRequest.getPreferredName())) {
			paramBuilder.append(" AND g.gid IN ( SELECT n.gid ");
			paramBuilder.append(" FROM names n WHERE n.nstat = 1 AND n.nval like :preferredName ) ");
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getAccessionNumbers())) {
			paramBuilder.append(" AND g.gid IN ( SELECT n.gid  ");
			paramBuilder.append(" FROM names n ");
			paramBuilder.append(" INNER JOIN udflds u on n.ntype = u.fldno AND u.fcode = '" + GermplasmImportRequest.ACCNO_NAME_TYPE + "'");
			paramBuilder.append(" WHERE n.nval IN (:accessionNumbers)  ) ");
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getCommonCropNames())) {
			paramBuilder.append(" AND g.gid IN ( SELECT a.gid  ");
			paramBuilder.append(" FROM atributs a");
			paramBuilder.append(
				" INNER JOIN cvterm c on a.atype = c.cvterm_id AND c.name = '" + GermplasmImportRequest.CROPNM_ATTR + "' and c.cv_id = "
					+ CvId.VARIABLES.getId());
			paramBuilder.append(" WHERE a.aval IN (:commonCropNames)  ) ");
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getGenus())) {
			paramBuilder.append(" AND g.gid IN ( SELECT n.gid  ");
			paramBuilder.append(" FROM names n ");
			paramBuilder.append(" INNER JOIN udflds u on n.ntype = u.fldno AND u.fcode = '" + GermplasmImportRequest.GENUS_NAME_TYPE + "'");
			paramBuilder.append(" WHERE n.nval IN (:germplasmGenus)  ) ");
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getGermplasmPUIs())) {
			paramBuilder.append(" AND g.gid IN ( SELECT n.gid  ");
			paramBuilder.append(" FROM names n ");
			paramBuilder.append(" INNER JOIN udflds u on n.ntype = u.fldno AND u.fcode = '" + GermplasmImportRequest.PUI_NAME_TYPE + "'");
			paramBuilder.append(" WHERE n.nval IN (:germplasmPUIs)  ) ");
		}

		if (!CollectionUtils.isEmpty(germplasmSearchRequest.getSpecies())) {
			paramBuilder.append(" AND g.gid IN ( SELECT a.gid  ");
			paramBuilder.append(" FROM atributs a");
			paramBuilder.append(
				" INNER JOIN cvterm c on a.atype = c.cvterm_id AND c.name = '" + GermplasmImportRequest.SPECIES_ATTR + "' and c.cv_id = "
					+ CvId.VARIABLES.getId());
			paramBuilder.append(" WHERE a.aval IN (:germplasmSpecies)  ) ");
		}

	}

	public List<GermplasmDTO> getGermplasmDTOList(
		final GermplasmSearchRequest germplasmSearchRequest, final Pageable pageable) {

		try {
			// Apply search filter and pagination on main germplasm entity
			final SQLQuery filterQuery = this.getSession().createSQLQuery(this.buildFilterGermplasmQuery(germplasmSearchRequest));
			this.addGermplasmSearchParameters(new SqlQueryParamBuilder(filterQuery), germplasmSearchRequest);
			addPaginationToSQLQuery(filterQuery, pageable);
			final List<String> gids = filterQuery.list();

			// If there is any match, build the list of  GermplasmDTO based on the matched GIDs
			if (!CollectionUtils.isEmpty(gids)) {
				final SQLQuery sqlQuery = this.getSession().createSQLQuery(this.buildGetGermplasmQuery());
				this.addScalarsForGermplasmDTO(sqlQuery).setResultTransformer(new AliasToBeanResultTransformer(GermplasmDTO.class));
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
		return "SELECT g.germplsm_uuid AS germplasmDbId, convert(g.gid, char) as gid, "
			+ "  g.gpid1, g.gpid2, g.gnpgs, "
			+ "  max(if(ntype.fcode = 'ACCNO', n.nval, null)) as accessionNumber, "
			+ "   STR_TO_DATE (convert(g.gdate,char), '%Y%m%d') AS acquisitionDate,"
			+ "   loc.labbr AS countryOfOriginCode, "
			+ "  ne.json_props AS germplasmOrigin, "
			+ "   max(if(n.nstat = 1, n.nval, null)) as germplasmName, "
			+ "  max(if(ntype.fcode = 'GENUS', n.nval, null)) as genus,"
			+ "  max(if(ntype.fcode = 'PUI', n.nval, null)) as germplasmPUI,"
			+ "   max(if(atype.name = 'PLOTCODE_AP_text', a.aval, null)) as germplasmSeedSource,  "
			+ "   max(if(atype.name = 'SPNAM_AP_text', a.aval, null)) as species, "
			+ "   max(if(atype.name = 'SPAUTH_AP_text', a.aval, null)) as speciesAUthority, "
			+ "   max(if(atype.name = 'SUBTAX_AP_text', a.aval, null)) as subtaxa, "
			+ "   max(if(atype.name = 'STAUTH_AP_text', a.aval, null)) as subtaxaAuthority, "
			+ "   max(if(atype.name = 'INSTCODE_AP_text', a.aval, null)) as instituteCode, "
			+ "   max(if(atype.name = 'ORIGININST_AP_text', a.aval, null)) as instituteName, "
			+ "   max(if(atype.name = 'CROPNM_AP_text', a.aval, null)) as commonCropName, "
			+ "   convert(g.methn, char) as breedingMethodDbId ";
	}

	private String getMainFromGermplasmClause() {
		return "  FROM germplsm g "
			+ "  	LEFT join location loc ON g.glocn = loc.locid "
			+ "  	LEFT JOIN atributs a ON a.gid = g.gid "
			+ "  	LEFT JOIN cvterm atype ON atype.cvterm_id = a.atype "
			+ "  	LEFT join names n ON n.gid = g.gid and n.nstat != 9 "
			+ "  	LEFT JOIN udflds ntype ON ntype.fldno = n.ntype "
			+ "     LEFT JOIN germplasm_study_source source ON source.gid = g.gid "
			+ "     LEFT JOIN nd_experiment ne on source.nd_experiment_id = ne.nd_experiment_id ";
	}

	private String buildFilterGermplasmQuery(final GermplasmSearchRequest germplasmSearchRequest) {
		final StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append(" SELECT g.gid ");
		queryBuilder.append(" FROM germplsm g ");
		queryBuilder.append(" WHERE g.deleted = 0 AND g.grplce = 0 ");
		this.addGermplasmSearchRequestFilters(new SqlQueryParamBuilder(queryBuilder), germplasmSearchRequest);
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

	public long countGermplasmDTOs(final GermplasmSearchRequest germplasmSearchRequest) {
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(this.buildCountGermplasmDTOsQuery((germplasmSearchRequest)));
		this.addGermplasmSearchParameters(new SqlQueryParamBuilder(sqlQuery), germplasmSearchRequest);

		final long count = ((BigInteger) sqlQuery.uniqueResult()).longValue();
		/* For unfiltered search and result of initial non-deleted germplasm count is < 5000, query the actual
		   non-deleted germplasm count. We are sure at this point that the germplasm DB is small enough for "g.deleted = 0 and grplce = 0"
		   clause to run in performant manner
		 */
		if (count < 5000 && germplasmSearchRequest.noFiltersSpecified()) {
			final SQLQuery sqlQuery2 = this.getSession().createSQLQuery(this.getCountFilteredGermplasmQuery((germplasmSearchRequest)));
			return ((BigInteger) sqlQuery2.uniqueResult()).longValue();
		}
		return count;

	}

	/*
	 In DBs with millions of germplasm, the clause to count non-deleted germplasm (g.deleted = 0 and grplce = 0) runs very slowly
	 on unfiltered search .
	 Workaround is for unfiltered search we "limit" count results to 5000 by getting the smaller of 5000 and
	 count of ALL germplasm (We basically avoid running the "g.deleted = 0 and grplce = 0" clause for unfiltered search)
	 */
	String buildCountGermplasmDTOsQuery(final GermplasmSearchRequest germplasmSearchRequest) {
		if (germplasmSearchRequest.noFiltersSpecified()) {
			return "SELECT LEAST(count(1), 5000) FROM germplsm ";
		} else {
			return this.getCountFilteredGermplasmQuery(germplasmSearchRequest);
		}
	}

	private String getCountFilteredGermplasmQuery(final GermplasmSearchRequest germplasmSearchRequest) {
		return "SELECT COUNT(1) FROM ( " + this.buildFilterGermplasmQuery(germplasmSearchRequest) + ") as T ";
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

			this.addScalarsForGermplasmDTO(sqlQuery).addScalar("entryNumber") //
				.setResultTransformer(new AliasToBeanResultTransformer(GermplasmDTO.class));

			addPaginationToSQLQuery(sqlQuery, pageable);

			return sqlQuery.list();
		} catch (final HibernateException e) {
			final String message = "Error with getGermplasmByStudy(studyDbId=" + studyDbId + ") " + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	private SQLQuery addScalarsForGermplasmDTO(final SQLQuery sqlQuery) {
		return sqlQuery.addScalar("germplasmDbId").addScalar("gid").addScalar("accessionNumber").addScalar("acquisitionDate")
			.addScalar("countryOfOriginCode").addScalar("germplasmName").addScalar("genus").addScalar("germplasmPUI")
			.addScalar("germplasmSeedSource")
			.addScalar("species").addScalar("speciesAuthority").addScalar("subtaxa").addScalar("subtaxaAuthority")
			.addScalar("instituteCode").addScalar("instituteName").addScalar("germplasmOrigin").addScalar("commonCropName")
			.addScalar("breedingMethodDbId");
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
				existingCross.setGermplasmPreferredName((String) result[1]);
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
			new StringBuilder(" select count(1) from germplsm g "
				+ " left join methods m on m.mid = g.methn "
				+ " left join location l on l.locid = g.glocn ");
		// If PUIS or other names were specified, pre-filter the GIDs matching by these names first to optimize performance
		final boolean preFilteredByName = this.preFilterGidsByNamesMatch(germplasmMatchRequestDto);
		if (preFilteredByName && CollectionUtils.isEmpty(germplasmMatchRequestDto.getGermplasmUUIDs()) && CollectionUtils
			.isEmpty(germplasmMatchRequestDto.getGids())) {
			// return zero count if there are no more filters to apply and no germplasm matched by name/PUI
			return 0L;
		}

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

	private boolean preFilterGidsByNamesMatch(final GermplasmMatchRequestDto germplasmMatchRequestDto) {
		final Set<Integer> matchedGids = new HashSet<>();
		boolean preFiltered = false;
		if (!CollectionUtils.isEmpty(germplasmMatchRequestDto.getGermplasmPUIs())) {
			final List<Integer> gids = this.getSession().createSQLQuery("select p.gid " + PUI_FROM_QUERY //
					+ " where p.nval IN (:puiList) ") //
				.setParameterList("puiList", germplasmMatchRequestDto.getGermplasmPUIs()) //
				.list();
			matchedGids.addAll(gids);
			preFiltered = true;
		}
		if (!CollectionUtils.isEmpty(germplasmMatchRequestDto.getNames())) {
			final StringBuilder mainQueryBuilder = new StringBuilder("select gid from names n  ");
			if (!CollectionUtils.isEmpty(germplasmMatchRequestDto.getNameTypes())) {
				mainQueryBuilder.append(
					"INNER JOIN udflds u ON u.fldno = n.ntype AND u.ftable = 'NAMES' and u.ftype='NAME' and u.fcode in (:nameTypes) ");
			}
			mainQueryBuilder.append(" WHERE n.nval in (:nameList) and n.nstat <> 9 ");
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(mainQueryBuilder.toString());
			sqlQuery.setParameterList("nameList", germplasmMatchRequestDto.getNames());
			if (!CollectionUtils.isEmpty(germplasmMatchRequestDto.getNameTypes())) {
				sqlQuery.setParameterList("nameTypes", germplasmMatchRequestDto.getNameTypes());
			}
			final List<Integer> gids = sqlQuery.list();
			matchedGids.addAll(gids);
			preFiltered = true;
		}
		if (!CollectionUtils.isEmpty(matchedGids)) {
			germplasmMatchRequestDto.getGids().addAll(matchedGids);
		}
		return preFiltered;
	}

	private void addScalarsToFindGermplasmMatchesQuery(final SQLQuery sqlQuery) {
		sqlQuery.addScalar("gid").addScalar("germplasmUUID").addScalar("preferredName").addScalar("germplasmPUI").addScalar("creationDate")
			.addScalar("reference")
			.addScalar("breedingLocationId")
			.addScalar("breedingLocation").addScalar("breedingMethodId").addScalar("breedingMethod")
			.addScalar("isGroupedLine", new BooleanType())
			.addScalar("groupId").addScalar("gpid1").addScalar("gpid2").addScalar("numberOfProgenitors").addScalar("createdByUserId");
	}

	public List<GermplasmDto> findGermplasmMatches(final GermplasmMatchRequestDto germplasmMatchRequestDto, final Pageable pageable) {
		final StringBuilder queryBuilder =
			new StringBuilder(FIND_GERMPLASM_MATCHES_MAIN_QUERY);
		// If PUIS or other names were specified, pre-filter the GIDs matching by these names first to optimize performance
		final boolean preFilteredByName = this.preFilterGidsByNamesMatch(germplasmMatchRequestDto);
		if (preFilteredByName && CollectionUtils.isEmpty(germplasmMatchRequestDto.getGermplasmUUIDs()) && CollectionUtils
			.isEmpty(germplasmMatchRequestDto.getGids())) {
			// return empty list if there are no more filters to apply and no germplasm matched by name/PUI
			return Collections.emptyList();
		}
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
		sqlQueryParamBuilder.append(" where g.deleted = 0 AND g.grplce = 0");
		sqlQueryParamBuilder.append(" and ( ");
		/**
		 * GID, GUUID, PUI and NAMES are the MAIN germplasm match parameters (PUI and names are pre-matched beforehand see @preFilterGidsByNamesMatch)
		 */
		if (!CollectionUtils.isEmpty(germplasmMatchRequestDto.getGermplasmUUIDs())) {
			sqlQueryParamBuilder.append(" g.germplsm_uuid in (:guidList) ");
			sqlQueryParamBuilder.setParameterList("guidList", germplasmMatchRequestDto.getGermplasmUUIDs());
		}
		if (!CollectionUtils.isEmpty(germplasmMatchRequestDto.getGids())) {
			// If GUUID filter specified beforehand, append "OR" operator to get union of matches
			if (!CollectionUtils.isEmpty(germplasmMatchRequestDto.getGermplasmUUIDs())) {
				sqlQueryParamBuilder.append(" or ");
			}
			sqlQueryParamBuilder.append(" g.gid in (:gidList) ");
			sqlQueryParamBuilder.setParameterList("gidList", germplasmMatchRequestDto.getGids());
		}
		sqlQueryParamBuilder.append(" ) ");

		// Restricting filters will limit the UNION of results from previous matches (by GID, GUID, PUIs, names)
		if (germplasmMatchRequestDto.restrictingFiltersSpecified()) {
			if (!CollectionUtils.isEmpty(germplasmMatchRequestDto.getMethods())) {
				sqlQueryParamBuilder.append(" and m.mcode in (:methodsList) ");
				sqlQueryParamBuilder.setParameterList("methodsList", germplasmMatchRequestDto.getMethods());
			}
			final SqlTextFilter locationNameFilter = germplasmMatchRequestDto.getLocationName();
			if (locationNameFilter != null) {
				final SqlTextFilter.Type type = locationNameFilter.getType();
				sqlQueryParamBuilder.append(" and l.lname " + getOperator(type) + " :locationName ");
				sqlQueryParamBuilder.setParameter("locationName", getParameter(type, locationNameFilter.getValue()));
			}
			final SqlTextFilter locationAbbrevationFilter = germplasmMatchRequestDto.getLocationAbbreviation();
			if (locationAbbrevationFilter != null) {
				final SqlTextFilter.Type type = locationAbbrevationFilter.getType();
				sqlQueryParamBuilder.append(" and l.labbr " + getOperator(type) + " :locationAbbr ");
				sqlQueryParamBuilder.setParameter("locationAbbr", getParameter(type, locationAbbrevationFilter.getValue()));
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
		queryBuilder.append(" WHERE ").append(" g.gid in (:gids) and g.deleted = 0 AND g.grplce = 0");
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
		queryBuilder.append(" WHERE ").append(" g.gid in (:gids) and g.deleted = 0 AND g.grplce = 0");
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

	public List<GermplasmDto> getManagementNeighbors(final Integer gid) {
		final StringBuilder queryBuilder =
			new StringBuilder(FIND_GERMPLASM_MATCHES_MAIN_QUERY);
		queryBuilder.append(" WHERE g.mgid = :gid AND  g.deleted = 0  and g.grplce = 0 ORDER BY g.gid");
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
		this.addScalarsToFindGermplasmMatchesQuery(sqlQuery);
		sqlQuery.setParameter("gid", gid);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(GermplasmDto.class));
		try {
			return sqlQuery.list();
		} catch (final HibernateException e) {
			final String message = "Error with getManagementNeighbors" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<GermplasmDto> getGroupRelatives(final Integer gid) {
		final StringBuilder queryBuilder =
			new StringBuilder(FIND_GERMPLASM_MATCHES_MAIN_QUERY);
		queryBuilder.append(" JOIN germplsm g2 ON g.gpid1 = g2.gpid1 WHERE ").append("g.gnpgs = -1 AND g.gid <> :gid AND g2.gid = :gid "
			+ "AND g.gpid1 != 0 AND  g.deleted = 0  AND g.grplce = 0");
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
		this.addScalarsToFindGermplasmMatchesQuery(sqlQuery);
		sqlQuery.setParameter("gid", gid);
		sqlQuery.setResultTransformer(Transformers.aliasToBean(GermplasmDto.class));
		try {
			return sqlQuery.list();
		} catch (final HibernateException e) {
			final String message = "Error with getGroupRelatives" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public long countGermplasmDerivativeProgeny(final Integer gid) {
		try {
			final StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("SELECT COUNT(DISTINCT g.gid) FROM germplsm g ");
			queryBuilder.append("WHERE g.gnpgs = -1 AND g.gpid2 = :gid AND g.deleted = 0  and g.grplce = 0");
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
			sqlQuery.setParameter("gid", gid);
			return ((BigInteger) sqlQuery.uniqueResult()).longValue();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countGermplasmDerivativeProgeny(gid=" + gid + ") " + e.getMessage();
			GermplasmDAO.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public List<Integer> getChildren(final Integer gid) {
		try {
			final SQLQuery sqlQuery = this.getSession().createSQLQuery("select gid "
				+ "from germplsm "
				+ "       inner join methods m on germplsm.methn = m.mid "
				+ "where (((mtype = " + GEN + " and (gpid1 = :parent or gpid2 = :parent))) "
				+ "  or (mtype in (" + DER_MAN
				+ ") and (gpid2 = :parent or (gpid2 = 0 and gpid1 = :parent)))) and deleted = 0 AND grplce = 0 ");
			sqlQuery.setParameter("parent", gid);
			return sqlQuery.list();
		} catch (final HibernateException e) {
			final String message = "Error with getChildren" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	/**
	 * Use this update when you are sure there is no need to traverse the progeny tree.
	 *
	 * @param oldGroupSource
	 * @param newGroupSource
	 */
	public void updateGroupSource(final Integer oldGroupSource, final Integer newGroupSource) {
		final String sql =
			"UPDATE germplsm SET gpid1 = :newGroupSource, modified_date = CURRENT_TIMESTAMP, modified_by = :userId WHERE gpid1 = :oldGroupSource and "
				+ " deleted = 0 and methn in (select mid from methods where mtype in (" + DER_MAN + "))";
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		sqlQuery.setParameter("newGroupSource", newGroupSource);
		sqlQuery.setParameter("oldGroupSource", oldGroupSource);
		sqlQuery.setParameter("userId", ContextHolder.getLoggedInUserId());
		try {
			final int updatedRows = sqlQuery.executeUpdate();
			GermplasmDAO.LOG.debug("updateGroupSource has updated the group source for " + updatedRows + " rows");
		} catch (final HibernateException e) {
			final String message = "Error with updateGroupSource" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void updateGroupSourceTraversingProgeny(final Integer parentNode, final Integer newGroupSource, final int maxRecursiveQueries) {
		final List<Integer> derivativeNodes = this.findDerivativeNodes(Collections.singletonList(parentNode), 0, maxRecursiveQueries);
		if (!derivativeNodes.isEmpty()) {
			this.updateGroupSource(derivativeNodes, newGroupSource);
		}
	}

	public boolean isNewParentANodeDescendant(final Set<Integer> newParents, final Integer node, final int maxRecursiveQueries) {
		return this.isNewParentANodeDescendant(new HashSet<>(), newParents, node, 0, maxRecursiveQueries);
	}

	private boolean isNewParentANodeDescendant(final Set<Integer> path, final Set<Integer> parents, final Integer node, int level,
		final int maxRecursiveQueries) {

		parents.remove(0);
		parents.removeAll(path);
		if (parents.contains(node)) {
			return true;
		}
		if (parents.isEmpty()) {
			return false;
		}
		path.addAll(parents);

		if (++level > maxRecursiveQueries) {
			throw new MiddlewareRequestException("", "germplasm.update.germplasm.max.recursive.queries.reached", "");
		}

		try {
			final SQLQuery sqlQuery = this.getSession()
				.createSQLQuery("select g.gpid1, g.gpid2 from germplsm g where g.deleted = 0 and g.grplce = 0 and g.gid in :childrenNodes");
			sqlQuery.setParameterList("childrenNodes", parents);
			final List<Object[]> results = sqlQuery.list();
			final Set<Integer> newParents = new HashSet<>();
			for (final Object[] r : results) {
				newParents.add((Integer) r[0]);
				newParents.add((Integer) r[1]);
			}
			return this.isNewParentANodeDescendant(path, newParents, node, level, maxRecursiveQueries);
		} catch (final HibernateException e) {
			final String message = "Error with isNewParentANodeDescendant" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	private List<Integer> findDerivativeNodes(final List<Integer> parentNodes, int level, final int maxRecursiveQueries) {
		if (++level > maxRecursiveQueries) {
			throw new MiddlewareRequestException("", "germplasm.update.germplasm.max.recursive.queries.reached", "");
		}
		final List<Integer> result = new ArrayList<>();
		final List<Integer> immediateDerivativeProgeny = this.getImmediateDerivativeProgeny(parentNodes);
		if (!immediateDerivativeProgeny.isEmpty()) {
			result.addAll(immediateDerivativeProgeny);
			result.addAll(this.findDerivativeNodes(immediateDerivativeProgeny, level, maxRecursiveQueries));
		}
		return result;
	}

	private List<Integer> getImmediateDerivativeProgeny(final List<Integer> parentNodes) {
		try {
			final SQLQuery sqlQuery = this.getSession().createSQLQuery("select g.gid "
				+ "from germplsm g "
				+ "       inner join methods m on g.methn = m.mid "
				+ "where g.deleted = 0 and m.mtype in (" + DER_MAN + ") and g.gpid2 in :parentNodes");
			sqlQuery.setParameterList("parentNodes", parentNodes);
			return sqlQuery.list();
		} catch (final HibernateException e) {
			final String message = "Error with getImmediateDerivativeProgeny" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	private void updateGroupSource(final List<Integer> gids, final Integer newGroupSource) {
		final String sql =
			"UPDATE germplsm SET gpid1 = :newGroupSource, modified_date = CURRENT_TIMESTAMP, modified_by = :userId WHERE gid in (:gids)";
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
		sqlQuery.setParameter("newGroupSource", newGroupSource);
		sqlQuery.setParameterList("gids", gids);
		sqlQuery.setParameter("userId", ContextHolder.getLoggedInUserId());
		try {
			GermplasmDAO.LOG.debug("updateGroupSource is trying to set the group source for the gids: " + gids);
			final Integer updatedRows = sqlQuery.executeUpdate();
			GermplasmDAO.LOG.debug("updateGroupSource has modified the group source for " + updatedRows + " germplasm");
		} catch (final HibernateException e) {
			final String message = "Error with updateGroupSource" + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<GermplasmMergedDto> getGermplasmMerged(final int gid) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Germplasm.class);
			criteria.add(Restrictions.eq("grplce", gid));
			final List<Germplasm> list = criteria.list();
			return list.stream().map(
					o -> new GermplasmMergedDto(o.getGid(), (o.getPreferredName() != null) ? o.getPreferredName().getNval() : StringUtils.EMPTY,
						o.getModifiedDate()))
				.collect(Collectors.toList());
		} catch (final HibernateException e) {
			final String message =
				"Error with getGermplasmMergeDtos(gids=" + gid + ") : " + e.getMessage();
			GermplasmDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public boolean isLocationUsedInGermplasm(final Integer locationId) {
		try {
			final String sql = "SELECT count(1) FROM germplsm WHERE glocn = :locationId";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameter("locationId", locationId);
			return ((BigInteger) query.uniqueResult()).longValue() > 0;
		} catch (final HibernateException e) {
			final String message = "Error with isLocationIdUsedInGermplasms(locationId=" + locationId + "): " + e.getMessage();
			GermplasmDAO.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
	}

	/**
	 * Returns the group and immediate source name for a given germplasms
	 *
	 * @param gids
	 * @return a {@link Map} of {@link Pair} where the left value corresponds to the group source name and the right value to the immediate source name
	 */
	public Map<Integer, Pair<String, String>> getDerivativeParentsMapByGids(final Set<Integer> gids) {
		final SQLQuery query = this.getSession().createSQLQuery("SELECT "
			+ " g.gid as gid, "
			+ " CASE WHEN g.gnpgs = -1 AND g.gpid1 IS NOT NULL AND g.gpid1 <> 0 THEN groupSource.nval ELSE '-' END AS groupSourceName, "
			+ " CASE WHEN g.gnpgs = -1 AND g.gpid2 IS NOT NULL AND g.gpid2 <> 0 THEN immediateSource.nval ELSE '-' END AS immediateSourceName "
			+ " 	FROM germplsm g "
			+ " 		LEFT JOIN names groupSource ON g.gpid1 = groupSource.gid AND groupSource.nstat = 1 "
			+ " 		LEFT JOIN names immediateSource ON g.gpid2 = immediateSource.gid AND immediateSource.nstat = 1 "
			+ " WHERE g.gid IN (:" + GermplasmDAO.GIDS + ")");
		query.addScalar("gid");
		query.addScalar("groupSourceName");
		query.addScalar("immediateSourceName");
		query.setParameterList(GermplasmDAO.GIDS, gids);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);

		final List<Map<String, Object>> queryResults = (List<Map<String, Object>>) query.list();
		final Map<Integer, Pair<String, String>> results = new HashMap<>();
		queryResults.forEach(row -> {
			results.put((Integer) row.get("gid"), new ImmutablePair<>((String) row.get("groupSourceName"), (String) row.get("immediateSourceName")));
		});
		return results;
	}

}
