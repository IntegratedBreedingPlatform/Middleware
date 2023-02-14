package org.generationcp.middleware.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.germplasm.GermplasmOriginDto;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmStudySource;
import org.generationcp.middleware.pojos.ims.LotStatus;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceDto;
import org.generationcp.middleware.service.api.study.germplasm.source.GermplasmStudySourceSearchRequest;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GermplasmStudySourceDAO extends GenericDAO<GermplasmStudySource, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GermplasmStudySourceDAO.class);

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
		+ "IFNULL (plot_no.value,"
		+ " (SELECT ep.value FROM nd_experimentprop ep WHERE ep.nd_experiment_id = e.parent_id AND ep.type_id = 8200)) as `plotNumber`,\n"
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

	private static final String GET_GERMPLASM_STUDY_ORIGIN = "select study.project_id as studyId, " //
		+ "  study.name as studyName, " //
		+ "  study.program_uuid as programUUID, " //
		+ "  ne.obs_unit_id as observationUnitId, " //
		+ "  ne.json_props AS jsonProps, " //
		+ "  fieldMapRow.value AS fieldMapRow, " //
		+ "  fieldMapCol.value AS fieldMapCol, " //
		+ "  plot.value AS plotNumber, " //
		+ "  (SELECT ndep.value " //
		+ "   FROM nd_experimentprop ndep " //
		+ "          INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id " //
		+ "   WHERE ndep.nd_experiment_id = ne.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') AS blockNumber, " //
		+ "  (SELECT ndep.value " //
		+ "   FROM nd_experimentprop ndep " //
		+ "          INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id " //
		+ "   WHERE ndep.nd_experiment_id = ne.nd_experiment_id AND ispcvt.name = 'REP_NO') AS repNumber, " //
		+ "  (SELECT ndep.value " //
		+ "   FROM nd_experimentprop ndep " //
		+ "          INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id " //
		+ "   WHERE ndep.nd_experiment_id = ne.nd_experiment_id AND ispcvt.name = 'COL') AS col, " //
		+ "  (SELECT ndep.value " //
		+ "   FROM nd_experimentprop ndep " //
		+ "          INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id " //
		+ "   WHERE ndep.nd_experiment_id = ne.nd_experiment_id AND ispcvt.name = 'ROW') AS row " //
		+ "from germplasm_study_source source " //
		+ "       inner join germplsm g on source.gid = g.gid " //
		+ "       inner join nd_experiment ne on source.nd_experiment_id = ne.nd_experiment_id " //
		+ "       inner join project plot_dataset on ne.project_id = plot_dataset.project_id " //
		+ "       inner join project study on plot_dataset.study_id = study.project_id " //
		+ "       LEFT JOIN nd_experimentprop plot ON plot.nd_experiment_id = ne.nd_experiment_id AND plot.type_id = "
		+ TermId.PLOT_NO.getId() //
		+ "       LEFT JOIN nd_experimentprop fieldMapRow ON fieldMapRow.nd_experiment_id = ne.nd_experiment_id AND fieldMapRow.type_id = "
		+ TermId.FIELDMAP_RANGE.getId() //
		+ "       LEFT JOIN nd_experimentprop fieldMapCol ON fieldMapCol.nd_experiment_id = ne.nd_experiment_id AND fieldMapCol.type_id = "
		+ TermId.FIELDMAP_COLUMN.getId() //
		+ " where source.gid = :gid AND g.deleted = 0 AND g.grplce = 0 and study.deleted = 0";

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

	public GermplasmOriginDto getGermplasmOrigin(final int gid) {
		try {
			final StringBuilder queryString = new StringBuilder(GET_GERMPLASM_STUDY_ORIGIN);
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryString.toString());
			sqlQuery.setParameter("gid", gid);

			sqlQuery.addScalar("programUUID").addScalar("studyId").addScalar("studyName")
				.addScalar("observationUnitId").addScalar("jsonProps").addScalar("fieldMapRow")
				.addScalar("fieldMapCol").addScalar("plotNumber", new IntegerType()).addScalar("blockNumber", new IntegerType())
				.addScalar("repNumber", new IntegerType())
				.addScalar("col")
				.addScalar("row");

			sqlQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);

			final List<Map<String, Object>> results = sqlQuery.list();

			if (!results.isEmpty()) {
				final Map<String, Object> result = results.get(0);
				final GermplasmOriginDto germplasmOriginDto = new GermplasmOriginDto();
				germplasmOriginDto.setProgramUUID((String) result.get("programUUID"));
				germplasmOriginDto.setStudyId((Integer) result.get("studyId"));
				germplasmOriginDto.setStudyName((String) result.get("studyName"));
				germplasmOriginDto.setObservationUnitId((String) result.get("observationUnitId"));
				germplasmOriginDto.setPlotNumber((Integer) result.get("plotNumber"));
				germplasmOriginDto.setBlockNumber(((result.get("blockNumber") != null)) ? (Integer) result.get("blockNumber") : null);
				germplasmOriginDto.setRepNumber((Integer) result.get("repNumber"));
				String x = result.get("row") != null ? (String) result.get("row") : null;
				String y = result.get("col") != null ? (String) result.get("col") : null;
				if (StringUtils.isBlank(x) || StringUtils.isBlank(y)) {
					x = result.get("fieldMapRow") != null ? (String) result.get("fieldMapRow") : null;
					y = result.get("fieldMapCol") != null ? (String) result.get("fieldMapCol") : null;
				}
				final String jsonProps = (String) result.get("jsonProps");
				if (jsonProps != null) {
					try {
						final HashMap jsonProp = new ObjectMapper().readValue(jsonProps, HashMap.class);
						germplasmOriginDto.setGeoCoordinates((Map<String, Object>) jsonProp.get("geoCoordinates"));
					} catch (final IOException e) {
						LOG.error("couldn't parse json_props column for observationUnitDbId=" + germplasmOriginDto.getObservationUnitId(),
							e);
					}
				}
				germplasmOriginDto.setPositionCoordinateX(x);
				germplasmOriginDto.setPositionCoordinateY(y);

				return germplasmOriginDto;
			}
			return null;
		} catch (final Exception e) {
			final String message = "Error with getGermplasmOrigin query for gid " + gid + " " + e.getMessage();
			LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
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
