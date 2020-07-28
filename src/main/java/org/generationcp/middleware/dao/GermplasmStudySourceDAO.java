package org.generationcp.middleware.dao;

import liquibase.util.StringUtils;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.SortedPageRequest;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceDto;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceSearchRequest;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GermplasmStudySourceDAO extends GenericDAO<GermplasmStudySource, Integer> {

	protected static final String GERMPLASM_STUDY_SOURCE_SEARCH_QUERY = "SELECT \n"
		+ "gss.germplasm_study_source_id as `germplasmStudySourceId`,\n"
		+ "gss.gid as `gid`,\n"
		+ "g.mgid as `groupId`,\n"
		+ "n.nval as `designation`,\n"
		+ "m.mcode as `breedingMethodAbbreviation`,\n"
		+ "m.mname as `breedingMethodName`,\n"
		+ "m.mtype as `breedingMethodType`,\n"
		+ "concat(geo.description, ' - ' , loc.lname) as `trialInstance`,\n"
		+ "breedingLoc.lname as `breedingLocationName`,\n"
		+ "rep_no.value as `replicationNumber`,\n"
		+ "plot_no.value as `plotNumber`,\n"
		+ "g.gdate as `germplasmDate`,\n"
		+ "count(lot.lotid) as `numberOfLots` "
		+ "FROM germplasm_study_source gss \n"
		+ "INNER JOIN germplsm g ON g.gid = gss.gid and g.deleted = 0 AND g.grplce = 0 \n"
		+ "INNER JOIN project p ON p.project_id = gss.project_id\n"
		+ "LEFT JOIN nd_experiment e ON e.nd_experiment_id = gss.nd_experiment_id\n"
		+ "LEFT JOIN nd_experimentprop rep_no ON rep_no.nd_experiment_id = e.nd_experiment_id AND rep_no.type_id = " + TermId.REP_NO.getId()
		+ "\n"
		+ "LEFT JOIN nd_experimentprop plot_no ON plot_no.nd_experiment_id = e.nd_experiment_id AND plot_no.type_id = " + TermId.PLOT_NO
		.getId() + "\n"
		+ "LEFT JOIN nd_geolocation geo ON e.nd_geolocation_id = geo.nd_geolocation_id\n"
		+ "LEFT JOIN nd_geolocationprop locationProp ON geo.nd_geolocation_id = locationProp.nd_geolocation_id AND locationProp.type_id = "
		+ TermId.LOCATION_ID.getId() + "\n"
		+ "LEFT JOIN location loc ON locationProp.value = loc.locid\n"
		+ "LEFT JOIN methods m ON m.mid = g.methn\n"
		+ "LEFT JOIN location breedingLoc ON breedingLoc.locid = g.glocn\n"
		+ "LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1\n"
		+ "LEFT JOIN ims_lot lot ON lot.eid = gss.gid \n"
		+ "WHERE gss.project_id = :studyId ";

	public Map<Integer, GermplasmStudySource> getGermplasmStudySourcesMap(final Set<Integer> gids) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.createAlias("germplasm", "germplasm");
		criteria.add(Restrictions.in("germplasm.gid", gids));
		final List<GermplasmStudySource> result = criteria.list();
		return result.stream().collect(Collectors.toMap(a -> a.getGermplasm().getGid(), Function.identity()));
	}

	public List<GermplasmStudySourceDto> getGermplasmStudySourceList(
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest) {

		final StringBuilder sql = new StringBuilder(GERMPLASM_STUDY_SOURCE_SEARCH_QUERY);
		addSearchQueryFilters(new SqlQueryParamBuilder(sql), germplasmStudySourceSearchRequest.getFilter());
		addGroupByAndLotsFilter(new SqlQueryParamBuilder(sql), germplasmStudySourceSearchRequest.getFilter());
		addOrder(sql, germplasmStudySourceSearchRequest.getSortedRequest());

		final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
		addSearchQueryFilters(new SqlQueryParamBuilder(query), germplasmStudySourceSearchRequest.getFilter());
		addGroupByAndLotsFilter(new SqlQueryParamBuilder(query), germplasmStudySourceSearchRequest.getFilter());

		query.addScalar("germplasmStudySourceId");
		query.addScalar("gid");
		query.addScalar("groupId");
		query.addScalar("designation");
		query.addScalar("breedingMethodAbbreviation");
		query.addScalar("breedingMethodName");
		query.addScalar("breedingMethodType");
		query.addScalar("trialInstance");
		query.addScalar("breedingLocationName");
		query.addScalar("replicationNumber", new IntegerType());
		query.addScalar("plotNumber", new IntegerType());
		query.addScalar("germplasmDate");
		query.addScalar("numberOfLots", new IntegerType());
		query.setParameter("studyId", germplasmStudySourceSearchRequest.getStudyId());

		GenericDAO.addSortedPageRequestPagination(query, germplasmStudySourceSearchRequest.getSortedRequest());

		query.setResultTransformer(Transformers.aliasToBean(GermplasmStudySourceDto.class));
		return query.list();
	}

	public long countFilteredGermplasmStudySourceList(final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest) {
		final StringBuilder subQuery = new StringBuilder(GERMPLASM_STUDY_SOURCE_SEARCH_QUERY);
		addSearchQueryFilters(new SqlQueryParamBuilder(subQuery), germplasmStudySourceSearchRequest.getFilter());
		addGroupByAndLotsFilter(new SqlQueryParamBuilder(subQuery), germplasmStudySourceSearchRequest.getFilter());

		final StringBuilder mainSql = new StringBuilder("SELECT COUNT(*) FROM ( \n");
		mainSql.append(subQuery.toString());
		mainSql.append(") a \n");

		final SQLQuery query = this.getSession().createSQLQuery(mainSql.toString());
		addSearchQueryFilters(new SqlQueryParamBuilder(query), germplasmStudySourceSearchRequest.getFilter());
		addGroupByAndLotsFilter(new SqlQueryParamBuilder(query), germplasmStudySourceSearchRequest.getFilter());

		query.setParameter("studyId", germplasmStudySourceSearchRequest.getStudyId());
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public long countGermplasmStudySourceList(final GermplasmStudySourceSearchRequest searchParameters) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmStudySource.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq("study.projectId", searchParameters.getStudyId()));
		criteria.setMaxResults(Integer.MAX_VALUE);
		return (long) criteria.uniqueResult();
	}

	private static void addSearchQueryFilters(
		final SqlQueryParamBuilder paramBuilder,
		final GermplasmStudySourceSearchRequest.Filter filter) {

		if (filter != null) {
			final Integer germplasmStudySourceId = filter.getGermplasmStudySourceId();
			if (germplasmStudySourceId != null) {
				paramBuilder.append(" and gss.germplasm_study_source_id = :germplasmStudySourceId");
				paramBuilder.setParameter("germplasmStudySourceId", germplasmStudySourceId);
			}
			final Integer gid = filter.getGid();
			if (gid != null) {
				paramBuilder.append(" and gss.gid = :gid");
				paramBuilder.setParameter("gid", gid);
			}
			final Integer groupId = filter.getGroupId();
			if (groupId != null) {
				paramBuilder.append(" and g.mgid = :groupId");
				paramBuilder.setParameter("groupId", groupId);
			}
			final Integer germplasmDate = filter.getGermplasmDate();
			if (germplasmDate != null) {
				paramBuilder.append(" and g.gdate = :germplasmDate");
				paramBuilder.setParameter("germplasmDate", germplasmDate);
			}
			final Integer plotNumber = filter.getPlotNumber();
			if (plotNumber != null) {
				paramBuilder.append(" and plot_no.value = :plotNumber");
				paramBuilder.setParameter("plotNumber", plotNumber);
			}
			final Integer replicationNumber = filter.getReplicationNumber();
			if (replicationNumber != null) {
				paramBuilder.append(" and rep_no.value = :replicationNumber");
				paramBuilder.setParameter("replicationNumber", replicationNumber);
			}
			final String breedingMethodAbbreviation = filter.getBreedingMethodAbbreviation();
			if (!StringUtils.isEmpty(breedingMethodAbbreviation)) {
				paramBuilder.append(" and m.mcode = :breedingMethodAbbreviation");
				paramBuilder.setParameter("breedingMethodAbbreviation", breedingMethodAbbreviation);
			}
			final String breedingMethodName = filter.getBreedingMethodName();
			if (!StringUtils.isEmpty(breedingMethodName)) {
				paramBuilder.append(" and m.mname = :breedingMethodName");
				paramBuilder.setParameter("breedingMethodName", breedingMethodName);
			}
			final String breedingMethodType = filter.getBreedingMethodType();
			if (!StringUtils.isEmpty(breedingMethodType)) {
				paramBuilder.append(" and m.mtype = :breedingMethodType");
				paramBuilder.setParameter("breedingMethodType", breedingMethodType);
			}
			final String designation = filter.getDesignation();
			if (!StringUtils.isEmpty(designation)) {
				paramBuilder.append(" and n.nval = :designation");
				paramBuilder.setParameter("designation", designation);
			}
			final String breedingLocationName = filter.getBreedingLocationName();
			if (!StringUtils.isEmpty(breedingLocationName)) {
				paramBuilder.append(" and breedingLoc.lname = :breedingLocationName");
				paramBuilder.setParameter("breedingLocationName", breedingLocationName);
			}
			final String trialInstance = filter.getTrialInstance();
			if (!StringUtils.isEmpty(trialInstance)) {
				paramBuilder.append(" and geo.description = :trialInstance");
				paramBuilder.setParameter("trialInstance", trialInstance);
			}

		}
	}

	private static void addOrder(final StringBuilder sql, final SortedPageRequest sortedPageRequest) {
		if (sortedPageRequest != null && sortedPageRequest.getSortBy() != null && sortedPageRequest.getSortOrder() != null
			&& GermplasmStudySourceSearchRequest.Filter.SORTABLE_FIELDS.contains(sortedPageRequest.getSortBy())) {
			sql.append(" ORDER BY " + sortedPageRequest.getSortBy() + " " + sortedPageRequest.getSortOrder() + "\n ");
		}
	}

	private static void addGroupByAndLotsFilter(final SqlQueryParamBuilder paramBuilder,
		final GermplasmStudySourceSearchRequest.Filter filter) {

		paramBuilder.append(" GROUP BY gss.germplasm_study_source_id\n");

		if (filter != null && filter.getNumberOfLots() != null) {
			paramBuilder.append(" HAVING `numberOfLots` = :numberOfLots\n");
			paramBuilder.setParameter("numberOfLots", filter.getNumberOfLots());
		}

	}

}
