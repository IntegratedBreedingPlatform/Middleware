package org.generationcp.middleware.dao;

import liquibase.util.StringUtils;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceDto;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceSearchRequest;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

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
		+ "LEFT JOIN ims_lot lot ON lot.eid = gss.gid and lot.status = " + LotStatus.ACTIVE.getIntValue() + " "
		+ "WHERE gss.project_id = :studyId ";

	public List<GermplasmStudySource> getByGids(final Set<Integer> gids) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.createAlias("germplasm", "germplasm");
		criteria.add(Restrictions.in("germplasm.gid", gids));
		return criteria.list();
	}

	public List<GermplasmStudySourceDto> getGermplasmStudySourceList(
		final GermplasmStudySourceSearchRequest germplasmStudySourceSearchRequest, final Pageable pageable) {

		final StringBuilder sql = new StringBuilder(GERMPLASM_STUDY_SOURCE_SEARCH_QUERY);
		addSearchQueryFilters(new SqlQueryParamBuilder(sql), germplasmStudySourceSearchRequest.getFilter());
		addGroupByAndLotsFilter(new SqlQueryParamBuilder(sql), germplasmStudySourceSearchRequest.getFilter());
		addPageRequestOrderBy(sql, pageable, GermplasmStudySourceSearchRequest.Filter.SORTABLE_FIELDS);

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

		addPaginationToSQLQuery(query, pageable);

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
			final List<Integer> gidList = filter.getGidList();
			if (!CollectionUtils.isEmpty(gidList)) {
				paramBuilder.append(" and gss.gid IN (:gidList)");
				paramBuilder.setParameterList("gidList", gidList);
			}
			final List<Integer> groupIdList = filter.getGroupIdList();
			if (!CollectionUtils.isEmpty(groupIdList)) {
				paramBuilder.append(" and g.mgid IN (:groupIdList)");
				paramBuilder.setParameterList("groupIdList", groupIdList);
			}
			final List<Integer> germplasmDateList = filter.getGermplasmDateList();
			if (!CollectionUtils.isEmpty(germplasmDateList)) {
				paramBuilder.append(" and g.gdate IN (:germplasmDateList)");
				paramBuilder.setParameterList("germplasmDateList", germplasmDateList);
			}
			final List<Integer> plotNumberList = filter.getPlotNumberList();
			if (!CollectionUtils.isEmpty(plotNumberList)) {
				paramBuilder.append(" and plot_no.value IN (:plotNumberList)");
				paramBuilder.setParameterList("plotNumberList", plotNumberList);
			}
			final List<Integer> replicationNumberList = filter.getReplicationNumberList();
			if (!CollectionUtils.isEmpty(replicationNumberList)) {
				paramBuilder.append(" and rep_no.value IN (:replicationNumberList)");
				paramBuilder.setParameterList("replicationNumberList", replicationNumberList);
			}
			final String breedingMethodAbbreviation = filter.getBreedingMethodAbbreviation();
			if (!StringUtils.isEmpty(breedingMethodAbbreviation)) {
				paramBuilder.append(" and m.mcode like :breedingMethodAbbreviation");
				paramBuilder.setParameter("breedingMethodAbbreviation", '%' + breedingMethodAbbreviation + '%');
			}
			final String breedingMethodName = filter.getBreedingMethodName();
			if (!StringUtils.isEmpty(breedingMethodName)) {
				paramBuilder.append(" and m.mname like :breedingMethodName");
				paramBuilder.setParameter("breedingMethodName", '%' + breedingMethodName + '%');
			}
			final String breedingMethodType = filter.getBreedingMethodType();
			if (!StringUtils.isEmpty(breedingMethodType)) {
				paramBuilder.append(" and m.mtype like :breedingMethodType");
				paramBuilder.setParameter("breedingMethodType", '%' + breedingMethodType + '%');
			}
			final String designation = filter.getDesignation();
			if (!StringUtils.isEmpty(designation)) {
				paramBuilder.append(" and n.nval like :designation"); //
				paramBuilder.setParameter("designation", '%' + designation + '%');
			}
			final String breedingLocationName = filter.getBreedingLocationName();
			if (!StringUtils.isEmpty(breedingLocationName)) {
				paramBuilder.append(" and breedingLoc.lname like :breedingLocationName");
				paramBuilder.setParameter("breedingLocationName", '%' + breedingLocationName + '%');
			}
			final List<String> trialInstanceList = filter.getTrialInstanceList();
			if (!CollectionUtils.isEmpty(trialInstanceList)) {
				paramBuilder.append(" and geo.description IN (:trialInstanceList)");
				paramBuilder.setParameterList("trialInstanceList", trialInstanceList);
			}

		}
	}

	private static void addGroupByAndLotsFilter(final SqlQueryParamBuilder paramBuilder,
		final GermplasmStudySourceSearchRequest.Filter filter) {

		paramBuilder.append(" GROUP BY gss.germplasm_study_source_id\n");

		if (filter != null) {
			final List<Integer> numberOfLotsList = filter.getNumberOfLotsList();
			if (!CollectionUtils.isEmpty(numberOfLotsList)) {
				paramBuilder.append(" HAVING `numberOfLots` IN (:numberOfLotsList)\n");
				paramBuilder.setParameterList("numberOfLotsList", numberOfLotsList);
			}
		}

	}

}
