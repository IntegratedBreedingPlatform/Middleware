package org.generationcp.middleware.dao;

import liquibase.util.StringUtils;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.service.api.study.germplasm.source.StudyGermplasmSourceDto;
import org.generationcp.middleware.service.api.study.germplasm.source.StudyGermplasmSourceRequest;
import org.generationcp.middleware.service.api.study.germplasm.source.StudyGermplasmSourceSearchDto;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;

import java.math.BigInteger;
import java.util.List;

public class GermplasmStudySourceDAO extends GenericDAO<GermplasmStudySource, Integer> {

	protected static final String GERMPLASM_STUDY_SOURCE_SEARCH_QUERY = "SELECT \n"
		+ "gss.gid as `gid`,\n"
		+ "g.mgid as `groupId`,\n"
		+ "n.nval as `designation`,\n"
		+ "m.mcode as `breedingMethodAbbrevation`,\n"
		+ "m.mname as `breedingMethodName`,\n"
		+ "m.mtype as `breedingMethodType`,\n"
		+ "geo.description as `trialInstance`,\n"
		+ "loc.lname as `location`,\n"
		+ "rep_no.value as `replicationNumber`,\n"
		+ "plot_no.value as `plotNumber`,\n"
		+ "g.gdate as `germplasmDate`,\n"
		+ "count(lot.lotid) as `lots` "
		+ "FROM germplasm_study_source gss \n"
		+ "INNER JOIN germplsm g ON g.gid = gss.gid\n"
		+ "INNER JOIN project p ON p.project_id = gss.project_id\n"
		+ "LEFT JOIN nd_experiment e ON e.nd_experiment_id = gss.nd_experiment_id\n"
		+ "LEFT JOIN nd_experimentprop rep_no ON rep_no.nd_experiment_id = e.nd_experiment_id AND rep_no.type_id = 8210\n"
		+ "LEFT JOIN nd_experimentprop plot_no ON plot_no.nd_experiment_id = e.nd_experiment_id AND plot_no.type_id = 8200\n"
		+ "LEFT JOIN nd_geolocation geo ON e.nd_geolocation_id = geo.nd_geolocation_id\n"
		+ "LEFT JOIN nd_geolocationprop locationProp ON geo.nd_geolocation_id = locationProp.nd_geolocation_id AND locationProp.type_id = 8190\n"
		+ "LEFT JOIN location loc ON locationProp.value = loc.locid\n"
		+ "LEFT JOIN methods m ON m.mid = g.methn\n"
		+ "LEFT JOIN names n ON g.gid = n.gid AND n.ntype in (2, 5)\n"
		+ "LEFT JOIN ims_stock_transaction stockTransaction ON gss.source_id  = stockTransaction.source_id\n"
		+ "LEFT JOIN ims_transaction transaction ON stockTransaction.trnid = transaction.trnid\n"
		+ "LEFT JOIN ims_transaction lot ON  transaction.lotid = lot.lotid\n"
		+ "WHERE gss.project_id = :studyId ";

	public List<StudyGermplasmSourceDto> getGermplasmStudySourceList(final StudyGermplasmSourceRequest searchParameters) {

		final StringBuilder sql = new StringBuilder(GERMPLASM_STUDY_SOURCE_SEARCH_QUERY);
		addSearchQueryFilters(new SqlQueryParamBuilder(sql), searchParameters.getStudyGermplasmSourceSearchDto());
		addGroupByAndLotsFilter(new SqlQueryParamBuilder(sql), searchParameters.getStudyGermplasmSourceSearchDto());

		final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
		addSearchQueryFilters(new SqlQueryParamBuilder(query), searchParameters.getStudyGermplasmSourceSearchDto());
		addGroupByAndLotsFilter(new SqlQueryParamBuilder(query), searchParameters.getStudyGermplasmSourceSearchDto());

		query.addScalar("gid");
		query.addScalar("groupId");
		query.addScalar("designation");
		query.addScalar("breedingMethodAbbrevation");
		query.addScalar("breedingMethodName");
		query.addScalar("breedingMethodType");
		query.addScalar("trialInstance");
		query.addScalar("location");
		query.addScalar("replicationNumber", new IntegerType());
		query.addScalar("plotNumber", new IntegerType());
		query.addScalar("germplasmDate");
		query.addScalar("lots", new IntegerType());
		query.setParameter("studyId", searchParameters.getStudyId());

		GenericDAO.addSortedPageRequestPagination(query, searchParameters.getSortedRequest());

		query.setResultTransformer(Transformers.aliasToBean(StudyGermplasmSourceDto.class));
		return query.list();
	}

	public long countFilteredGermplasmStudySourceList(final StudyGermplasmSourceRequest searchParameters) {
		final StringBuilder subQuery = new StringBuilder(GERMPLASM_STUDY_SOURCE_SEARCH_QUERY);
		addSearchQueryFilters(new SqlQueryParamBuilder(subQuery), searchParameters.getStudyGermplasmSourceSearchDto());
		addGroupByAndLotsFilter(new SqlQueryParamBuilder(subQuery), searchParameters.getStudyGermplasmSourceSearchDto());

		final StringBuilder mainSql = new StringBuilder("SELECT COUNT(*) FROM ( \n");
		mainSql.append(subQuery.toString());
		mainSql.append(") a \n");

		final SQLQuery query = this.getSession().createSQLQuery(mainSql.toString());
		addSearchQueryFilters(new SqlQueryParamBuilder(query), searchParameters.getStudyGermplasmSourceSearchDto());
		addGroupByAndLotsFilter(new SqlQueryParamBuilder(query), searchParameters.getStudyGermplasmSourceSearchDto());

		query.setParameter("studyId", searchParameters.getStudyId());
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public long countGermplasmStudySourceList(final StudyGermplasmSourceRequest searchParameters) {
		final Criteria criteria = this.getSession().createCriteria(GermplasmStudySource.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq("study.projectId", searchParameters.getStudyId()));
		criteria.setMaxResults(Integer.MAX_VALUE);
		return (long) criteria.uniqueResult();
	}

	private static void addSearchQueryFilters(
		final SqlQueryParamBuilder paramBuilder,
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto) {

		if (studyGermplasmSourceSearchDto != null) {
			final Integer gid = studyGermplasmSourceSearchDto.getGid();
			if (gid != null) {
				paramBuilder.append(" and gss.gid = :gid");
				paramBuilder.setParameter("gid", gid);
			}
			final Integer groupId = studyGermplasmSourceSearchDto.getGroupId();
			if (groupId != null) {
				paramBuilder.append(" and g.mgid = :groupId");
				paramBuilder.setParameter("groupId", groupId);
			}
			final Integer germplasmDate = studyGermplasmSourceSearchDto.getGermplasmDate();
			if (germplasmDate != null) {
				paramBuilder.append(" and g.gdate = :germplasmDate");
				paramBuilder.setParameter("germplasmDate", germplasmDate);
			}
			final Integer plotNumber = studyGermplasmSourceSearchDto.getPlotNumber();
			if (plotNumber != null) {
				paramBuilder.append(" and plot_no.value = :plotNumber");
				paramBuilder.setParameter("plotNumber", plotNumber);
			}
			final Integer replicationNumber = studyGermplasmSourceSearchDto.getReplicationNumber();
			if (replicationNumber != null) {
				paramBuilder.append(" and rep_no.value = :replicationNumber");
				paramBuilder.setParameter("replicationNumber", replicationNumber);
			}
			final String breedingMethodAbbrevation = studyGermplasmSourceSearchDto.getBreedingMethodAbbrevation();
			if (!StringUtils.isEmpty(breedingMethodAbbrevation)) {
				paramBuilder.append(" and m.mcode = :breedingMethodAbbrevation");
				paramBuilder.setParameter("breedingMethodAbbrevation", breedingMethodAbbrevation);
			}
			final String breedingMethodName = studyGermplasmSourceSearchDto.getBreedingMethodName();
			if (!StringUtils.isEmpty(breedingMethodName)) {
				paramBuilder.append(" and m.mname = :breedingMethodName");
				paramBuilder.setParameter("breedingMethodName", breedingMethodName);
			}
			final String breedingMethodType = studyGermplasmSourceSearchDto.getBreedingMethodType();
			if (!StringUtils.isEmpty(breedingMethodType)) {
				paramBuilder.append(" and m.mtype = :breedingMethodType");
				paramBuilder.setParameter("breedingMethodType", breedingMethodType);
			}
			final String designation = studyGermplasmSourceSearchDto.getDesignation();
			if (!StringUtils.isEmpty(designation)) {
				paramBuilder.append(" and n.nval = :designation");
				paramBuilder.setParameter("designation", designation);
			}
			final String location = studyGermplasmSourceSearchDto.getLocation();
			if (!StringUtils.isEmpty(location)) {
				paramBuilder.append(" and loc.lname = :location");
				paramBuilder.setParameter("location", location);
			}
			final String trialInstance = studyGermplasmSourceSearchDto.getTrialInstance();
			if (!StringUtils.isEmpty(trialInstance)) {
				paramBuilder.append(" and geo.description = :trialInstance");
				paramBuilder.setParameter("trialInstance", trialInstance);
			}

		}
	}

	private static void addGroupByAndLotsFilter(final SqlQueryParamBuilder paramBuilder,
		final StudyGermplasmSourceSearchDto studyGermplasmSourceSearchDto) {

		paramBuilder.append(" GROUP BY gss.source_id\n");

		final Integer lots = studyGermplasmSourceSearchDto.getLots();
		if (lots != null) {
			paramBuilder.append(" HAVING `lots` = :lots\n");
			paramBuilder.setParameter("lots", lots);
		}

	}

}
