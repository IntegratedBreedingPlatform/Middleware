package org.generationcp.middleware.dao;

import org.apache.commons.lang.BooleanUtils;
import org.generationcp.middleware.domain.dms.TrialSummary;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.search_request.brapi.v2.TrialSearchRequestDTO;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.util.Util;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.type.IntegerType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TrialSearchDao extends GenericDAO<DmsProject, Integer> {

	protected static final String PROGRAM_DB_ID = "programDbId";
	protected static final String PROGRAM_NAME = "programName";
	protected static final String TRIAL_DB_ID = "trialDbId";
	protected static final String TRIAL_NAME = "trialName";
	protected static final String START_DATE = "startDate";
	protected static final String END_DATE = "endDate";
	protected static final String LOCATION_DB_ID = "locationDbId";
	protected static final String TRIAL_DESCRIPTION = "trialDescription";
	protected static final String TRIAL_PUI = "trialPUI";
	protected static final String ACTIVE = "active";

	private static final List<String> BRAPI_TRIALS_SORTABLE_FIELDS = Arrays
		.asList(PROGRAM_DB_ID, PROGRAM_NAME, TRIAL_DB_ID, TRIAL_NAME, START_DATE, END_DATE, LOCATION_DB_ID);

	public TrialSearchDao(final Session session) {
		super(session);
	}

	public long countSearchTrials(final TrialSearchRequestDTO trialSearchRequestDTO) {
		final SQLQuery sqlQuery =
			this.getSession().createSQLQuery(this.createCountSearchTrialsQueryString(trialSearchRequestDTO));
		this.addFilterParameters(sqlQuery, trialSearchRequestDTO);
		return ((BigInteger) sqlQuery.uniqueResult()).longValue();
	}

	public List<TrialSummary> searchTrials(final TrialSearchRequestDTO trialSearchRequestDTO, final Pageable pageable) {
		final SQLQuery sqlQuery =
			this.getSession().createSQLQuery(this.createSearchTrialsQueryString(trialSearchRequestDTO, pageable));

		sqlQuery.addScalar(TRIAL_DB_ID);
		sqlQuery.addScalar(TRIAL_NAME);
		sqlQuery.addScalar(TRIAL_DESCRIPTION);
		sqlQuery.addScalar(TRIAL_PUI);
		sqlQuery.addScalar(START_DATE);
		sqlQuery.addScalar(END_DATE);
		sqlQuery.addScalar(ACTIVE, new IntegerType());
		sqlQuery.addScalar(PROGRAM_DB_ID);
		sqlQuery.addScalar(PROGRAM_NAME);
		sqlQuery.addScalar(LOCATION_DB_ID);

		this.addFilterParameters(sqlQuery, trialSearchRequestDTO);

		addPaginationToSQLQuery(sqlQuery, pageable);

		sqlQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		final List<Map<String, Object>> results = sqlQuery.list();

		final List<TrialSummary> studyList = new ArrayList<>();
		for (final Map<String, Object> result : results) {
			final TrialSummary trialSummary = new TrialSummary();
			trialSummary.setTrialDbId((Integer) result.get(TRIAL_DB_ID));
			trialSummary.setName(String.valueOf(result.get(TRIAL_NAME)));
			trialSummary.setDescription(String.valueOf(result.get(TRIAL_DESCRIPTION)));
			trialSummary.setObservationUnitId(String.valueOf(result.get(TRIAL_PUI)));
			trialSummary.setStartDate(Util.tryParseDate((String) result.get(START_DATE)));
			trialSummary.setEndDate(Util.tryParseDate((String) result.get(END_DATE)));
			trialSummary.setProgramDbId(String.valueOf(result.get(PROGRAM_DB_ID)));
			trialSummary.setProgramName(String.valueOf(result.get(PROGRAM_NAME)));
			trialSummary.setLocationId(String.valueOf(result.get(LOCATION_DB_ID)));
			trialSummary.setActive(((Integer) result.get(ACTIVE)) == 1);
			studyList.add(trialSummary);
		}
		return studyList;
	}

	private String createCountSearchTrialsQueryString(final TrialSearchRequestDTO trialSearchRequestDTO) {
		final StringBuilder sql = new StringBuilder(" SELECT COUNT(DISTINCT pmain.project_id) ");
		this.appendFromClause(sql);
		this.appendSearchFilter(sql, trialSearchRequestDTO);
		return sql.toString();
	}

	private String createSearchTrialsQueryString(final TrialSearchRequestDTO trialSearchRequestDTO, final Pageable pageable) {
		final StringBuilder sql = new StringBuilder(" SELECT  ");
		sql.append(" pmain.project_id AS trialDbId, ");
		sql.append(" pmain.name AS trialName, ");
		sql.append(" pmain.description AS trialDescription, ");
		sql.append(" study_exp.obs_unit_id AS trialPUI, ");
		sql.append(" pmain.start_date AS startDate, ");
		sql.append(" pmain.end_date AS endDate, ");
		sql.append(
			" CASE WHEN pmain.end_date IS NOT NULL AND LENGTH(pmain.end_date) > 0 AND CONVERT(pmain.end_date, UNSIGNED) < CONVERT(date_format(now(), '%Y%m%d'), UNSIGNED) "
				+ "THEN 0 ELSE 1 END AS active, ");
		sql.append(" wp.project_name AS programName, ");
		sql.append(" pmain.program_uuid AS programDbId, ");
		// locationDbId is not unique to study but can have different value per environment.
		// Get the MIN or MAX depending on sort parameter and direction
		if (pageable != null && pageable.getSort() != null && pageable.getSort().getOrderFor(LOCATION_DB_ID) != null
			&& Sort.Direction.DESC.equals(pageable.getSort().getOrderFor(LOCATION_DB_ID).getDirection())) {
			sql.append(" MAX(geopropLocation.value) as locationDbId ");
		} else {
			sql.append(" MIN(geopropLocation.value) as locationDbId ");
		}

		this.appendFromClause(sql);
		this.appendSearchFilter(sql, trialSearchRequestDTO);
		sql.append(" GROUP BY pmain.project_id ");

		addPageRequestOrderBy(sql, pageable, BRAPI_TRIALS_SORTABLE_FIELDS);

		return sql.toString();
	}

	private void appendFromClause(final StringBuilder sql) {
		sql.append(" FROM ");
		sql.append("     nd_geolocation geoloc ");
		sql.append("         INNER JOIN ");
		sql.append("     nd_experiment study_exp ON study_exp.nd_geolocation_id = geoloc.nd_geolocation_id AND study_exp.type_id = "
			+ TermId.STUDY_EXPERIMENT.getId());
		sql.append("         LEFT JOIN ");
		sql.append("     nd_experiment nde ON nde.nd_geolocation_id = geoloc.nd_geolocation_id AND nde.type_id = "
			+ TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId());
		sql.append("         INNER JOIN ");
		sql.append("     project pmain ON pmain.project_id = study_exp.project_id ");
		sql.append("         LEFT OUTER JOIN ");
		sql.append(
			"     nd_geolocationprop geopropLocation ON geopropLocation.nd_geolocation_id = geoloc.nd_geolocation_id AND geopropLocation.type_id = "
				+ TermId.LOCATION_ID.getId());
		sql.append("         LEFT OUTER JOIN workbench.workbench_project wp ON wp.project_uuid = pmain.program_uuid");
		sql.append("         LEFT JOIN external_reference_study er ON er.study_id = pmain.project_id ");
		sql.append(" WHERE pmain.deleted = 0 ");//Exclude Deleted Studies
	}

	private void appendSearchFilter(final StringBuilder sql, final TrialSearchRequestDTO trialSearchRequestDTO) {
		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getStudyDbIds())) {
			sql.append(" AND geoloc.nd_geolocation_id IN (:studyDbIds) ");
		}
		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getLocationDbIds())) {
			sql.append(" AND geopropLocation.value IN (:locationDbIds) ");
		}
		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getProgramDbIds())) {
			sql.append(" AND pmain.program_uuid IN (:programDbIds) ");
		}
		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getTrialDbIds())) {
			sql.append(" AND pmain.project_id IN (:trialDbIds) ");
		}
		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getTrialNames())) {
			sql.append(" AND pmain.name IN (:trialNames) ");
		}
		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getTrialPUIs())) {
			sql.append(" AND study_exp.obs_unit_id IN (:trialPUIs) ");
		}
		if (trialSearchRequestDTO.getActive() != null) {
			if (BooleanUtils.isTrue(trialSearchRequestDTO.getActive())) {
				sql.append(
					" AND (pmain.end_date IS NULL or LENGTH(pmain.end_date) = 0 OR CONVERT(pmain.end_date, UNSIGNED) >= CONVERT(date_format(now(), '%Y%m%d'), UNSIGNED) ) ");
			} else {
				sql.append(
					" AND pmain.end_date IS NOT NULL AND LENGTH(pmain.end_date) > 0 AND CONVERT(pmain.end_date, UNSIGNED) < CONVERT(date_format(now(), '%Y%m%d'), UNSIGNED) ");
			}

		}
		// Search Date Range
		if (trialSearchRequestDTO.getSearchDateRangeStart() != null) {
			sql.append(" AND :searchTrialDateStart <= pmain.end_date");

		} else if (trialSearchRequestDTO.getSearchDateRangeEnd() != null) {
			sql.append(" AND :searchTrialDateEnd >= pmain.start_date");
		}

		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getExternalReferenceIds())) {
			sql.append(" AND er.reference_id IN (:referenceIds) ");
		}
		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getExternalReferenceSources())) {
			sql.append(" AND er.reference_source IN (:referenceSources) ");
		}
	}

	private void addFilterParameters(final SQLQuery sqlQuery, final TrialSearchRequestDTO trialSearchRequestDTO) {

		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getStudyDbIds())) {
			sqlQuery.setParameterList("studyDbIds", trialSearchRequestDTO.getStudyDbIds());
		}
		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getLocationDbIds())) {
			sqlQuery.setParameterList("locationDbIds", trialSearchRequestDTO.getLocationDbIds());
		}
		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getProgramDbIds())) {
			sqlQuery.setParameterList("programDbIds", trialSearchRequestDTO.getProgramDbIds());
		}
		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getTrialDbIds())) {
			sqlQuery.setParameterList("trialDbIds", trialSearchRequestDTO.getTrialDbIds());
		}
		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getTrialNames())) {
			sqlQuery.setParameterList("trialNames", trialSearchRequestDTO.getTrialNames());
		}
		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getTrialPUIs())) {
			sqlQuery.setParameterList("trialPUIs", trialSearchRequestDTO.getTrialPUIs());
		}
		// Search Date Range
		if (trialSearchRequestDTO.getSearchDateRangeStart() != null) {
			sqlQuery.setParameter(
				"searchTrialDateStart",
				Util.formatDateAsStringValue(trialSearchRequestDTO.getSearchDateRangeStart(), Util.DATE_AS_NUMBER_FORMAT));

		} else if (trialSearchRequestDTO.getSearchDateRangeEnd() != null) {
			sqlQuery.setParameter(
				"searchTrialDateEnd",
				Util.formatDateAsStringValue(trialSearchRequestDTO.getSearchDateRangeEnd(), Util.DATE_AS_NUMBER_FORMAT));
		}

		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getExternalReferenceSources())) {
			sqlQuery.setParameterList("referenceSources", trialSearchRequestDTO.getExternalReferenceSources());
		}
		if (!CollectionUtils.isEmpty(trialSearchRequestDTO.getExternalReferenceIds())) {
			sqlQuery.setParameterList("referenceIds", trialSearchRequestDTO.getExternalReferenceIds());
		}

	}
}
