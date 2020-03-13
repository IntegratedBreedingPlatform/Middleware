package org.generationcp.middleware.dao.dms;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.service.api.dataset.FilteredPhenotypesInstancesCountDTO;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ObservationUnitsSearchDao extends GenericDAO<ExperimentModel, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(ObservationUnitsSearchDao.class);
	private static final String SUM_OF_SAMPLES_ID = "-2";
	private static final String OBSERVATION_UNIT_ID = "observationUnitId";
	protected static final String LOCATION_ID = "LOCATION_ID";
	protected static final String EXPT_DESIGN = "EXPT_DESIGN";
	static final String FIELD_MAP_COLUMN = "FIELDMAP COLUMN";
	protected static final String OBS_UNIT_ID = "OBS_UNIT_ID";
	static final String PARENT_OBS_UNIT_ID = "PARENT_OBS_UNIT_ID";
	protected static final String COL = "COL";
	protected static final String ROW = "ROW";
	protected static final String BLOCK_NO = "BLOCK_NO";
	protected static final String PLOT_NO = "PLOT_NO";
	protected static final String REP_NO = "REP_NO";
	protected static final String ENTRY_CODE = "ENTRY_CODE";
	protected static final String ENTRY_NO = "ENTRY_NO";
	protected static final String DESIGNATION = "DESIGNATION";
	protected static final String GID = "GID";
	protected static final String ENTRY_TYPE = "ENTRY_TYPE";
	protected static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	static final String FIELD_MAP_RANGE = "FIELDMAP RANGE";
	protected static final String SUM_OF_SAMPLES = "SUM_OF_SAMPLES";
	private static final String OBSERVATION_UNIT_NO = "OBSERVATION_UNIT_NO";
	private static final Map<String, String> factorsFilterMap = new HashMap<>();
	private static final String ENVIRONMENT_COLUMN_NAME_SUFFIX = "_ENVIRONMENT";
	private static final List<String> EXP_PROPS_VAR_TYPES =
		Arrays.asList(VariableType.EXPERIMENTAL_DESIGN.name(), VariableType.TREATMENT_FACTOR.name());

	static {
		factorsFilterMap.put(String.valueOf(TermId.GID.getId()), "s.dbxref_id");
		factorsFilterMap.put(String.valueOf(TermId.DESIG.getId()), "s.name");
		factorsFilterMap.put(String.valueOf(TermId.ENTRY_NO.getId()), "s.uniquename");
		factorsFilterMap.put(String.valueOf(TermId.ENTRY_CODE.getId()), "s.value");
		factorsFilterMap.put(String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()), "env.observation_unit_no");
		factorsFilterMap.put(SUM_OF_SAMPLES_ID,
			"EXISTS ( SELECT 1 FROM sample AS sp WHERE nde.nd_experiment_id = sp.nd_experiment_id HAVING count(sample_id)");
	}

	private static final Map<String, String> mainVariablesMap = new HashMap<>();

	static {

		mainVariablesMap.put(OBSERVATION_UNIT_ID, "    nde.nd_experiment_id as observationUnitId");
		mainVariablesMap.put(TRIAL_INSTANCE, "    env.observation_unit_no AS TRIAL_INSTANCE");
		mainVariablesMap.put(LOCATION_ID,
			"    (SELECT loc.lname FROM nd_experimentprop xprop INNER JOIN location loc on loc.locid = xprop.value WHERE xprop.nd_experiment_id = env.nd_experiment_id and xprop.type_id = 8190) 'LOCATION_ID'");
		mainVariablesMap.put(EXPT_DESIGN,
			"    (SELECT edesign.name FROM nd_experimentprop xprop INNER JOIN cvterm edesign on edesign.cvterm_id = xprop.value WHERE xprop.nd_experiment_id = env.nd_experiment_id and xprop.type_id = 8135) 'EXPT_DESIGN'");
		mainVariablesMap.put(ENTRY_TYPE,
			"    (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.value WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') AS ENTRY_TYPE");
		mainVariablesMap.put(GID, "    s.dbxref_id AS GID");
		mainVariablesMap.put(DESIGNATION, "    s.name AS DESIGNATION");
		mainVariablesMap.put(ENTRY_NO, "    s.uniquename AS ENTRY_NO");
		mainVariablesMap.put(ENTRY_CODE, "    s.value AS ENTRY_CODE");
		mainVariablesMap.put(REP_NO,
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ispcvt.name = 'REP_NO') AS REP_NO");
		mainVariablesMap.put(PLOT_NO,
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ispcvt.name = 'PLOT_NO') AS PLOT_NO");
		mainVariablesMap.put(BLOCK_NO,
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') AS BLOCK_NO");
		mainVariablesMap.put(ROW,
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ispcvt.name = 'ROW') AS ROW");
		mainVariablesMap.put(COL,
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ispcvt.name = 'COL') AS COL");
		mainVariablesMap.put(FIELD_MAP_COLUMN,
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ispcvt.name = 'FIELDMAP COLUMN') AS 'FIELDMAP COLUMN'");
		mainVariablesMap.put(FIELD_MAP_RANGE,
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ispcvt.name = 'FIELDMAP RANGE') AS 'FIELDMAP RANGE'");
		mainVariablesMap.put(OBS_UNIT_ID, "    nde.obs_unit_id as OBS_UNIT_ID");
		mainVariablesMap.put(PARENT_OBS_UNIT_ID, "    parent.obs_unit_id as PARENT_OBS_UNIT_ID");
		mainVariablesMap.put(SUM_OF_SAMPLES, "    coalesce(nullif((SELECT count(sp.sample_id) "
			+ "        FROM sample sp "
			+ "        WHERE sp.nd_experiment_id = nde.nd_experiment_id) "
			+ "         + coalesce(child_sample_count.count, 0), 0), '-') AS 'SUM_OF_SAMPLES'");

	}

	public Integer countObservationUnitsForDataset(final Integer datasetId, final Integer instanceId, final Boolean draftMode,
		final ObservationUnitsSearchDTO.Filter filter) {

		try {
			final StringBuilder sql = new StringBuilder("select count(*) as totalObservationUnits from " //
				+ "nd_experiment nde " //
				+ "    inner join project p on p.project_id = nde.project_id " //
				+ "    inner join stock s ON s.stock_id = nde.stock_id " //
				// FIXME won't work for sub-sub-obs
				+ " INNER JOIN nd_experiment plot ON plot.nd_experiment_id = nde.parent_id OR ( plot.nd_experiment_id = nde.nd_experiment_id) AND plot.type_id = 1155 "
				+ " INNER JOIN nd_experiment env ON plot.parent_id = env.nd_experiment_id AND env.type_id = " + TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()
				//
				+ " where p.project_id = :datasetId ");

			if (instanceId != null) {
				sql.append(" and env.nd_experiment_id = :instanceId ");
			}

			if (Boolean.TRUE.equals(draftMode)) {
				sql.append(" and exists(select 1" //
					+ "   from phenotype ph" //
					+ "   where ph.nd_experiment_id = nde.nd_experiment_id " //
					+ "         and (ph.draft_value is not null " //
					+ "                or ph.draft_cvalue_id is not null)) ");
			}

			if (filter != null) {
				this.addFilters(sql, filter, draftMode);
			}

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			addFilteredValueParams(query, filter);

			query.addScalar("totalObservationUnits", new IntegerType());
			query.setParameter("datasetId", datasetId);

			if (instanceId != null) {
				query.setParameter("instanceId", instanceId);
			}

			return (Integer) query.uniqueResult();
		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
				String.format("Unexpected error in executing countTotalObservations(studyId = %s, instanceNumber = %s) : ",
					datasetId, instanceId) + he.getMessage(),
				he);
		}
	}

	public FilteredPhenotypesInstancesCountDTO countFilteredInstancesAndPhenotypes(final Integer datasetId,
		final ObservationUnitsSearchDTO observationUnitsSearchDTO) {

		final ObservationUnitsSearchDTO.Filter filter = observationUnitsSearchDTO.getFilter();

		Preconditions.checkNotNull(filter.getVariableId());

		try {
			final StringBuilder sql = new StringBuilder(
				"select count(*) as totalObservationUnits, count(distinct(env.nd_experiment_id)) as totalInstances from " //
					+ "nd_experiment nde " //
					+ "    inner join project p on p.project_id = nde.project_id " //
					+ " INNER JOIN project env_ds ON env_ds.study_id = p.study_id AND env_ds.dataset_type_id = " + DatasetTypeEnum.SUMMARY_DATA.getId()
					+ " INNER JOIN nd_experiment env ON env_ds.project_id = env.project_id AND env.type_id = " + TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()
					+ " where " //
					+ "	p.project_id = :datasetId ");

			if (observationUnitsSearchDTO.getInstanceId() != null) {
				sql.append(" and env.nd_experiment_id = :instanceId ");
			}

			final String filterByVariableSQL =
				(filter.getVariableId() == null) ? StringUtils.EMPTY : "and ph.observable_id = " + filter.getVariableId() + " ";

			if (Boolean.TRUE.equals(observationUnitsSearchDTO.getDraftMode())) {
				sql.append(" and exists(select 1" //
					+ "   from phenotype ph" //
					+ "   where ph.nd_experiment_id = nde.nd_experiment_id " //
					+ filterByVariableSQL //
					+ "         and (ph.draft_value is not null " //
					+ "                or ph.draft_cvalue_id is not null)) ");
			}

			this.addFilters(sql, filter, observationUnitsSearchDTO.getDraftMode());

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			addFilteredValueParams(query, filter);

			query.addScalar("totalObservationUnits", new IntegerType());
			query.addScalar("totalInstances", new IntegerType());

			query.setParameter("datasetId", datasetId);

			if (observationUnitsSearchDTO.getInstanceId() != null) {
				query.setParameter("instanceId", observationUnitsSearchDTO.getInstanceId());
			}

			final Object[] result = (Object[]) query.uniqueResult();

			return new FilteredPhenotypesInstancesCountDTO((Integer) result[0], (Integer) result[1]);

		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
				String.format("Unexpected error in executing countTotalObservations(studyId = %s, instanceNumber = %s) : ",
					datasetId, observationUnitsSearchDTO.getInstanceId()) + he.getMessage(),
				he);
		}
	}

	public List<ObservationUnitRow> getObservationUnitsByVariable(final ObservationUnitsSearchDTO params) {
		try {

			final String generateQuery = this.getObservationUnitsByVariableQuery(params);
			final SQLQuery query = this.getSession().createSQLQuery(generateQuery);

			query.addScalar(ObservationUnitsSearchDao.OBSERVATION_UNIT_ID);

			final String measurementVariableName = this.addScalarForSpecificTrait(params, query);

			query.setParameter("datasetId", params.getDatasetId());

			if (params.getInstanceId() != null) {
				query.setParameter("instanceId", String.valueOf(params.getInstanceId()));
			}

			addFilteredValueParams(query, params.getFilter());

			query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			final List<Map<String, Object>> results = query.list();

			return this.mapToObservationUnitRow(results, params, measurementVariableName);

		} catch (final Exception e) {
			final String error = "An internal error has ocurred when trying to execute the operation " + e.getMessage();
			ObservationUnitsSearchDao.LOG.error(error);
			throw new MiddlewareException(error, e);
		}
	}

	private String getObservationUnitsByVariableQuery(final ObservationUnitsSearchDTO searchDto) {

		final StringBuilder sql = new StringBuilder("SELECT  " //
			+ "    nde.nd_experiment_id as observationUnitId, "); //

		final String traitClauseFormat = " MAX(IF(cvterm_variable.name = '%s', ph.value, NULL)) AS '%s'," //
			+ " MAX(IF(cvterm_variable.name = '%s', ph.phenotype_id, NULL)) AS '%s'," //
			+ " MAX(IF(cvterm_variable.name = '%s', ph.status, NULL)) AS '%s'," //
			+ " MAX(IF(cvterm_variable.name = '%s', ph.cvalue_id, NULL)) AS '%s', " //
			+ " MAX(IF(cvterm_variable.name = '%s', ph.draft_value, NULL)) AS '%s'," //
			+ " MAX(IF(cvterm_variable.name = '%s', ph.draft_cvalue_id, NULL)) AS '%s', " //
			;

		for (final MeasurementVariableDto measurementVariable : searchDto.getSelectionMethodsAndTraits()) {
			if (measurementVariable.getId().equals(searchDto.getFilter().getVariableId())) {
				sql.append(String.format( //
					traitClauseFormat, //
					measurementVariable.getName(), //
					measurementVariable.getName(), // Value
					measurementVariable.getName(), //
					measurementVariable.getName() + "_PhenotypeId", //
					measurementVariable.getName(), //
					measurementVariable.getName() + "_Status", //
					measurementVariable.getName(), //
					measurementVariable.getName() + "_CvalueId", //
					measurementVariable.getName(), //
					measurementVariable.getName() + "_DraftValue", //
					measurementVariable.getName(), //
					measurementVariable.getName() + "_DraftCvalueId" //
				));
				break;
			}
		}

		sql.append(" 1 FROM " //
			+ "	project p " //
			+ "	INNER JOIN nd_experiment nde ON nde.project_id = p.project_id " //
			+ "	INNER JOIN nd_experiment env ON nde.parent_id = env.nd_experiment_id AND env.type_id = " + TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()
			+ "	INNER JOIN stock s ON s.stock_id = nde.stock_id " //
			+ "	LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id " //
			+ "	LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id " //
			+ " WHERE p.project_id = :datasetId "); //

		if (searchDto.getInstanceId() != null) {
			sql.append(" AND env.nd_experiment_id = :instanceId"); //
		}

		final ObservationUnitsSearchDTO.Filter filter = searchDto.getFilter();
		this.addFilters(sql, filter, searchDto.getDraftMode());

		final String filterByVariableSQL =
			(filter.getVariableId() == null) ? StringUtils.EMPTY : "and ph.observable_id = " + filter.getVariableId() + " ";

		if (Boolean.TRUE.equals(searchDto.getDraftMode())) {
			sql.append(" and exists(select 1" //
				+ "   from phenotype ph" //
				+ "   where ph.nd_experiment_id = nde.nd_experiment_id " //
				+ filterByVariableSQL //
				+ "         and (ph.draft_value is not null " //
				+ "                or ph.draft_cvalue_id is not null)) ");
		}

		sql.append(" GROUP BY observationUnitId "); //

		return sql.toString();
	}

	public String getObservationVariableName(final int datasetId) {
		final SQLQuery query = this.getSession().createSQLQuery("SELECT pp.alias AS OBSERVATION_UNIT_NO_NAME" //
			+ " FROM projectprop pp" //
			+ "        INNER JOIN cvterm cvt ON cvt.cvterm_id = pp.type_id" //
			+ " WHERE pp.project_id = :datasetId  AND cvt.cvterm_id = " + TermId.OBSERVATION_UNIT.getId()
			+ " LIMIT 1");
		query.addScalar("OBSERVATION_UNIT_NO_NAME", new StringType());
		query.setParameter("datasetId", datasetId);
		final String result = (String) query.uniqueResult();
		// TODO change type_id of PLOT_NO to OBSERVATION_UNIT
		if (result == null) {
			return TermId.PLOT_NO.name();
		}
		return result;
	}

	public List<ObservationUnitRow> getObservationUnitTable(final ObservationUnitsSearchDTO searchDto) {
		try {
			final String observationVariableName = this.getObservationVariableName(searchDto.getDatasetId());
			final List<Map<String, Object>> results = this.getObservationUnitsQueryResult(
				searchDto,
				observationVariableName);
			return this.convertToObservationUnitRows(results, searchDto, observationVariableName);
		} catch (final Exception e) {
			ObservationUnitsSearchDao.LOG.error(e.getMessage(), e);
			final String error = "An internal error has ocurred when trying to retrieve observation unit rows " + e.getMessage();
			throw new MiddlewareException(error, e);
		}
	}

	public List<Map<String, Object>> getObservationUnitTableMapList(final ObservationUnitsSearchDTO searchDto) {
		try {
			final String observationVariableName = this.getObservationVariableName(searchDto.getDatasetId());
			return this.getObservationUnitTableAsMapListResult(
				searchDto,
				observationVariableName);
		} catch (final Exception e) {
			ObservationUnitsSearchDao.LOG.error(e.getMessage(), e);
			final String error =
				"An internal error has ocurred when trying to retrieve observation unit rows as list of map" + e.getMessage();
			throw new MiddlewareException(error, e);
		}
	}

	private List<Map<String, Object>> getObservationUnitsQueryResult(final ObservationUnitsSearchDTO searchDto,
		final String observationVariableName) {
		try {

			final String observationUnitTableQuery = this.getObservationUnitTableQuery(searchDto, observationVariableName);
			final SQLQuery query = this.createQueryAndAddScalar(searchDto, observationUnitTableQuery);
			this.setParameters(searchDto, query);
			return query.list();

		} catch (final Exception e) {
			final String error = "An internal error has ocurred when trying to execute the operation " + e.getMessage();
			ObservationUnitsSearchDao.LOG.error(error);
			throw new MiddlewareException(error, e);
		}
	}

	private void setParameters(final ObservationUnitsSearchDTO searchDto, final SQLQuery query) {
		query.setParameter("datasetId", searchDto.getDatasetId());

		if (searchDto.getInstanceId() != null) {
			query.setParameter("instanceId", String.valueOf(searchDto.getInstanceId()));
		}

		addFilteredValueParams(query, searchDto.getFilter());

		final Integer pageNumber = searchDto.getSortedRequest() != null ? searchDto.getSortedRequest().getPageNumber() : null;
		final Integer pageSize = searchDto.getSortedRequest() != null ? searchDto.getSortedRequest().getPageSize() : null;
		if (pageNumber != null && pageSize != null) {
			query.setFirstResult(pageSize * (pageNumber - 1));
			query.setMaxResults(pageSize);
		}

		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
	}

	private SQLQuery createQueryAndAddScalar(
		final ObservationUnitsSearchDTO searchDto, final String generateQuery) {
		final SQLQuery query = this.getSession().createSQLQuery(generateQuery);

		this.addScalar(query);
		query.addScalar(FIELD_MAP_COLUMN);
		query.addScalar(FIELD_MAP_RANGE);
		query.addScalar(LOCATION_ID);
		query.addScalar(EXPT_DESIGN);

		this.addScalarForTraits(searchDto.getSelectionMethodsAndTraits(), query, true);

		for (final String gpDescriptor : searchDto.getGenericGermplasmDescriptors()) {
			query.addScalar(gpDescriptor, new StringType());
		}

		for (final String designFactor : searchDto.getAdditionalDesignFactors()) {
			query.addScalar(designFactor, new StringType());
		}

		for (final MeasurementVariableDto envFactor : searchDto.getEnvironmentDetails()) {
			query.addScalar(this.getEnvironmentColumnName(envFactor.getName()), new StringType());
		}
		for (final MeasurementVariableDto envCondition : searchDto.getEnvironmentConditions()) {
			query.addScalar(this.getEnvironmentColumnName(envCondition.getName()), new StringType());
		}

		query.addScalar(ObservationUnitsSearchDao.OBSERVATION_UNIT_NO, new StringType());
		return query;
	}

	private void addScalar(final SQLQuery createSQLQuery) {
		createSQLQuery.addScalar(ObservationUnitsSearchDao.OBSERVATION_UNIT_ID);
		createSQLQuery.addScalar(ObservationUnitsSearchDao.TRIAL_INSTANCE);
		createSQLQuery.addScalar(ObservationUnitsSearchDao.ENTRY_TYPE);
		createSQLQuery.addScalar(ObservationUnitsSearchDao.GID);
		createSQLQuery.addScalar(ObservationUnitsSearchDao.DESIGNATION);
		createSQLQuery.addScalar(ObservationUnitsSearchDao.ENTRY_NO);
		createSQLQuery.addScalar(ObservationUnitsSearchDao.ENTRY_CODE);
		createSQLQuery.addScalar(ObservationUnitsSearchDao.REP_NO);
		createSQLQuery.addScalar(ObservationUnitsSearchDao.PLOT_NO);
		createSQLQuery.addScalar(ObservationUnitsSearchDao.BLOCK_NO);
		createSQLQuery.addScalar(ObservationUnitsSearchDao.ROW);
		createSQLQuery.addScalar(ObservationUnitsSearchDao.COL);
		createSQLQuery.addScalar(ObservationUnitsSearchDao.PARENT_OBS_UNIT_ID, new StringType());
		createSQLQuery.addScalar(ObservationUnitsSearchDao.OBS_UNIT_ID, new StringType());
		createSQLQuery.addScalar(ObservationUnitsSearchDao.SUM_OF_SAMPLES);
	}

	private String getObservationUnitTableQuery(
		final ObservationUnitsSearchDTO searchDto, final String observationUnitNoName) {

		// FIXME some props should be fetched from plot, not immediate parent. It won't work for sub-sub obs
		//  same for columns -> DatasetServiceImpl.getSubObservationSetColumns

		// If filterColumns has values, we should only include the specified columns to the query. This is to optimize the query to only
		// get the information it needs (reduce subqueries and minimize the amount of data returned to the client)
		// ObservationUnitsSearchDTO is only applicable in Observation table, if filterColumns has values it means that the request is called
		// for Visualization table
		final List<String> filterColumns = searchDto.getFilterColumns();
		final boolean noFilterVariables = CollectionUtils.isEmpty(filterColumns);
		final List<String> columns = new ArrayList<>();

		if (noFilterVariables) {
			columns.addAll(mainVariablesMap.values());
		} else {
			for (final String columnName : filterColumns) {
				if (mainVariablesMap.containsKey(columnName)) {
					columns.add(mainVariablesMap.get(columnName));
				}
			}
		}

		if (noFilterVariables) {
			final String traitClauseFormat = " MAX(IF(cvterm_variable.name = '%s', ph.value, NULL)) AS '%s',"
				+ " MAX(IF(cvterm_variable.name = '%s', ph.phenotype_id, NULL)) AS '%s',"
				+ " MAX(IF(cvterm_variable.name = '%s', ph.status, NULL)) AS '%s',"
				+ " MAX(IF(cvterm_variable.name = '%s', ph.cvalue_id, NULL)) AS '%s', "
				+ " MAX(IF(cvterm_variable.name = '%s', ph.draft_value, NULL)) AS '%s',"
				+ " MAX(IF(cvterm_variable.name = '%s', ph.draft_cvalue_id, NULL)) AS '%s'";

			for (final MeasurementVariableDto measurementVariable : searchDto.getSelectionMethodsAndTraits()) {
				columns.add(String.format(
					traitClauseFormat,
					measurementVariable.getName(),
					measurementVariable.getName(),
					measurementVariable.getName(),
					measurementVariable.getName() + "_PhenotypeId",
					measurementVariable.getName(),
					measurementVariable.getName() + "_Status",
					measurementVariable.getName(),
					measurementVariable.getName() + "_CvalueId",
					measurementVariable.getName(),
					measurementVariable.getName() + "_DraftValue",
					measurementVariable.getName(),
					measurementVariable.getName() + "_DraftCvalueId"
				));
			}
		} else {

			final String traitClauseFormat = " MAX(IF(cvterm_variable.name = '%s', ph.value, NULL)) AS '%s'";
			final String traitDraftClauseFormat = " MAX(IF(cvterm_variable.name = '%s', ph.draft_value, NULL)) AS '%s'";

			for (final MeasurementVariableDto measurementVariable : searchDto.getSelectionMethodsAndTraits()) {
				if (filterColumns.contains(measurementVariable.getName())) {
					columns.add(String.format(
						Boolean.TRUE.equals(searchDto.getDraftMode()) ? traitDraftClauseFormat : traitClauseFormat,
						measurementVariable.getName(),
						measurementVariable.getName()));
				}
			}
		}

		if (!CollectionUtils.isEmpty(searchDto.getGenericGermplasmDescriptors())) {
			final String germplasmDescriptorClauseFormat =
				"    (SELECT sprop.value FROM stockprop sprop INNER JOIN cvterm spropcvt ON spropcvt.cvterm_id = sprop.type_id WHERE sprop.stock_id = s.stock_id AND spropcvt.name = '%s') AS '%s'";
			for (final String gpFactor : searchDto.getGenericGermplasmDescriptors()) {
				if (noFilterVariables || filterColumns.contains(gpFactor)) {
					columns.add(String.format(germplasmDescriptorClauseFormat, gpFactor, gpFactor));
				}
			}
		}

		if (!CollectionUtils.isEmpty(searchDto.getAdditionalDesignFactors())) {
			final String designFactorClauseFormat =
				"    (SELECT xprop.value FROM nd_experimentprop xprop INNER JOIN cvterm xpropcvt ON xpropcvt.cvterm_id = xprop.type_id WHERE xprop.nd_experiment_id = plot.nd_experiment_id AND xpropcvt.name = '%s') AS '%s'";
			for (final String designFactor : searchDto.getAdditionalDesignFactors()) {
				if (noFilterVariables || filterColumns.contains(designFactor)) {
					columns.add(String.format(designFactorClauseFormat, designFactor, designFactor));
				}
			}
		}

		// Only variables at observation level are supported in filtering columns. Variables at environment level are automatically excluded if filterColumns has values.
		if (noFilterVariables && !CollectionUtils.isEmpty(searchDto.getEnvironmentDetails())) {
			final String envFactorFormat =
				"    (SELECT xprop.value FROM nd_experimentprop xprop INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = xprop.type_id AND ispcvt.name = '%s' WHERE xprop.nd_experiment_id = env.nd_experiment_id ) '%s'";
			for (final MeasurementVariableDto envFactor : searchDto.getEnvironmentDetails()) {
				columns.add(String.format(envFactorFormat, envFactor.getName(), this.getEnvironmentColumnName(envFactor.getName())));
			}
		}

		// Only variables at observation level are supported in filtering columns. Variables at environment level are automatically excluded if filterColumns has values.
		if (noFilterVariables && !CollectionUtils.isEmpty(searchDto.getEnvironmentConditions())) {
			final String envConditionFormat =
				"    (SELECT pheno.value from phenotype pheno "
					+ "		INNER JOIN cvterm envcvt ON envcvt.cvterm_id = pheno.observable_id AND envcvt.name = '%s' "
					+ "		WHERE pheno.nd_experiment_id = env.nd_experiment_id) '%s'";
			for (final MeasurementVariableDto envCondition : searchDto.getEnvironmentConditions()) {
				columns.add(
					String.format(envConditionFormat, envCondition.getName(), this.getEnvironmentColumnName(envCondition.getName())));
			}
		}

		// TODO move PLOT_NO to nd_exp
		// If the request is for Observation/Sub-observation table (noFilterVariables is true), always add the OBSERVATION_UNIT_NO column.
		// If the request is for Visualization (noFilterVariables is false) and dataset is sub-observation (observationUnitNoName with "PLOT_NO" value means the dataset is observation),
		// we add the OBSERVATION_UNIT_NO column with the observationUnitNoName as its column alias.
		if (noFilterVariables || (filterColumns.contains(observationUnitNoName) && !PLOT_NO.equals(observationUnitNoName))) {
			columns.add(" COALESCE(nde.observation_unit_no, ("
				+ "		SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ispcvt.name = 'PLOT_NO' "
				+ " )) AS " + (noFilterVariables ? OBSERVATION_UNIT_NO : observationUnitNoName));
		}

		final StringBuilder sql = new StringBuilder("SELECT * FROM (SELECT  ");

		sql.append(Joiner.on(", ").join(columns));

		this.addFromClause(sql, searchDto);

		this.addFilters(sql, searchDto.getFilter(), searchDto.getDraftMode());

		sql.append(" GROUP BY nde.nd_experiment_id ");

		if (noFilterVariables) {
			this.addOrder(sql, searchDto, observationUnitNoName);
		} else {
			sql.append(") T ");
		}

		return sql.toString();
	}

	private void addFromClause(final StringBuilder sql, final ObservationUnitsSearchDTO searchDto) {

		sql.append(" FROM " //
			+ "	project p " //
			+ "	INNER JOIN nd_experiment nde ON nde.project_id = p.project_id " //
			+ "	INNER JOIN stock s ON s.stock_id = nde.stock_id " //
			+ "	LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id " //
			+ "	LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id " //
			+ " LEFT JOIN nd_experiment parent ON parent.nd_experiment_id = nde.parent_id " //
			// Count samples for child dataset (sub-obs)
			+ " LEFT JOIN (SELECT parent.nd_experiment_id, " //
			+ "       nullif(count(child_sample.sample_id), 0) AS count " //
			+ "     FROM nd_experiment child " // start the join with child to avoid parent_id full index scan
			+ "            LEFT JOIN sample child_sample ON child.nd_experiment_id = child_sample.nd_experiment_id " //
			+ "            INNER JOIN nd_experiment parent ON child.parent_id = parent.nd_experiment_id " //
			+ "     GROUP BY parent.nd_experiment_id) child_sample_count ON child_sample_count.nd_experiment_id = nde.nd_experiment_id " //
			// FIXME won't work for sub-sub-obs
			+ " INNER JOIN nd_experiment plot ON plot.nd_experiment_id = nde.parent_id OR (plot.nd_experiment_id = nde.nd_experiment_id) AND plot.type_id = 1155 "
			+ " INNER JOIN nd_experiment env ON plot.parent_id = env.nd_experiment_id AND env.type_id = " + TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()
			//
			+ " WHERE p.project_id = :datasetId "); //

		if (searchDto.getInstanceId() != null) {
			sql.append(" AND env.nd_experiment_id = :instanceId"); //
		}

		if (Boolean.TRUE.equals(searchDto.getDraftMode())) {
			sql.append(" AND (ph.draft_value is not null or ph.draft_cvalue_id is not null) "); //
		}
	}

	private void addFilters(final StringBuilder sql, final ObservationUnitsSearchDTO.Filter filter, final Boolean draftMode) {

		if (filter == null) {
			return;
		}

		final String filterByDraftOrValue = Boolean.TRUE.equals(draftMode) ? "draft_value" : "value";

		final Integer variableId = filter.getVariableId();
		String filterByVariableSQL = StringUtils.EMPTY;
		if (variableId != null) {
			filterByVariableSQL = "and ph2.observable_id = " + variableId + " ";
		}

		if (Boolean.TRUE.equals(filter.getByOutOfBound())) {
			this.appendOutOfBoundsTraitsFilteringToQuery(sql, filterByDraftOrValue, filterByVariableSQL);
		}

		if (filter.getFilteredValues() != null && !filter.getFilteredValues().isEmpty()) {
			// Perform IN operation on variable values
			this.appendVariableIdAndOperationToFilterQuery(sql, filter, filterByDraftOrValue, filter.getFilteredValues().keySet(),
				false);
		}

		if (filter.getFilteredTextValues() != null && !filter.getFilteredTextValues().isEmpty()) {
			// Perform LIKE operation on variable value
			this.appendVariableIdAndOperationToFilterQuery(sql, filter, filterByDraftOrValue,
				filter.getFilteredTextValues().keySet(), true);
		}

		if (Boolean.TRUE.equals(filter.getByOverwritten())) {
			this.appendTraitStatusFilterToQuery(sql, filterByVariableSQL, " and ph2.value is not null and ph2.draft_value is not null");
		}

		if (Boolean.TRUE.equals(filter.getByOutOfSync())) {
			this.appendTraitStatusFilterToQuery(sql, filterByVariableSQL,
				" AND ph2.status = '" + Phenotype.ValueStatus.OUT_OF_SYNC.getName() + "'");
		}

		// TODO check if missing also applies to draft mode
		if (Boolean.TRUE.equals(filter.getByMissing())) {
			this.appendTraitStatusFilterToQuery(sql, filterByVariableSQL, " AND ph2.value =  '" + Phenotype.MISSING_VALUE + "'");
		}

	}

	private void addOrder(final StringBuilder sql, final ObservationUnitsSearchDTO searchDto, final String observationUnitNoName) {

		String orderColumn;
		final String sortBy = searchDto.getSortedRequest() != null ? searchDto.getSortedRequest().getSortBy() : "";
		if (observationUnitNoName != null && StringUtils.isNotBlank(sortBy) && observationUnitNoName.equalsIgnoreCase(sortBy)
			&& !ObservationUnitsSearchDao.PLOT_NO.equals(observationUnitNoName)) {
			orderColumn = ObservationUnitsSearchDao.OBSERVATION_UNIT_NO;
		} else if (SUM_OF_SAMPLES_ID.equals(sortBy)) {
			orderColumn = ObservationUnitsSearchDao.SUM_OF_SAMPLES;
		} else {
			orderColumn = StringUtils.isNotBlank(sortBy) ? sortBy : ObservationUnitsSearchDao.PLOT_NO;
		}

		final String sortOrder = searchDto.getSortedRequest() != null ? searchDto.getSortedRequest().getSortOrder() : "";
		final String direction = StringUtils.isNotBlank(sortOrder) ? sortOrder : "asc";

		if (Boolean.TRUE.equals(searchDto.getDraftMode())) {
			for (final MeasurementVariableDto selectionMethodsAndTrait : searchDto.getSelectionMethodsAndTraits()) {
				if (orderColumn.equals(selectionMethodsAndTrait.getName())) {
					orderColumn = orderColumn + "_DraftValue";
					break;
				}
			}
		}

		/**
		 * Since we are using MAX(IF(, NULL)) to group the different phenotypes
		 * we can't order by these colunms
		 * https://bugs.mysql.com/bug.php?id=80802
		 * Workaround: use a derived table and order the outer one
		 * 		select * from (...) T order by ...
		 *
		 * Sort first numeric data casting string values to numbers
		 * and then text data (which casts to 0)
		 */
		sql.append(" ) T ORDER BY " + "(1 * `" + orderColumn + "`) " + direction
			+ ", `" + orderColumn + "` " + direction);
	}

	private void appendVariableIdAndOperationToFilterQuery(final StringBuilder sql, final ObservationUnitsSearchDTO.Filter filter,
		final String filterByDraftOrValue, final Set<String> variableIds, final boolean performLikeOperation) {
		final Integer variableId = filter.getVariableId();
		final List<String> traitAndSelectionVariableTypes = Arrays.asList(VariableType.TRAIT.name(), VariableType.SELECTION_METHOD.name());
		for (final String observableId : variableIds) {
			if (variableId != null && !variableId.equals(Integer.valueOf(observableId))) {
				continue;
			}
			final String variableTypeString = filter.getVariableTypeMap().get(observableId);
			if (traitAndSelectionVariableTypes.contains(variableTypeString)) {
				this.appendTraitValueFilteringToQuery(sql, filterByDraftOrValue, observableId, performLikeOperation);

			} else {
				this.applyFactorsFilter(sql, observableId, variableTypeString, performLikeOperation);
			}
		}
	}

	private void appendOutOfBoundsTraitsFilteringToQuery(final StringBuilder sql, final String filterByDraftOrValue,
		final String filterByVariableSQL) {
		sql.append(" and nde.nd_experiment_id in (select ph2.nd_experiment_id " //
			+ "      from cvterm_relationship cvtrscale " //
			+ "           inner join cvterm scale on cvtrscale.object_id = scale.cvterm_id " //
			+ "           inner join cvterm_relationship cvtrdataType on scale.cvterm_id = cvtrdataType.subject_id and cvtrdataType.type_id = "
			+ TermId.HAS_TYPE.getId()
			+ "           inner join cvterm dataType on cvtrdataType.object_id = dataType.cvterm_id " //
			+ "           left join cvtermprop scaleMaxRange on scale.cvterm_id = scaleMaxRange.cvterm_id and scaleMaxRange.type_id = "
			+ TermId.MAX_VALUE.getId()
			+ "           left join cvtermprop scaleMinRange on scale.cvterm_id = scaleMinRange.cvterm_id and scaleMinRange.type_id = "
			+ TermId.MIN_VALUE.getId()
			+ " inner join phenotype ph2 on cvtrscale.subject_id = ph2.observable_id " //
			+ "    inner join nd_experiment nde2 on ph2.nd_experiment_id = nde2.nd_experiment_id " //
			+ "           inner join project p2 on nde2.project_id = p2.project_id " //
			+ "           left join variable_overrides vo on vo.cvterm_id = ph2.observable_id and p2.program_uuid = vo.program_uuid " //
			+ "      where ph2." + filterByDraftOrValue + " is not null  and ph2." + filterByDraftOrValue + "!= 'missing'" //
			+ filterByVariableSQL
			+ "        and cvtrscale.type_id = " + TermId.HAS_SCALE.getId() //
			+ "        and case " //
			+ "        when dataType.cvterm_id = " + TermId.CATEGORICAL_VARIABLE.getId() //
				/* get the categoricals whose value != category value (out-of-bound)
				in other words, the set where ph.value = category value NOT exists*/
			+ "          then not exists( " //
			+ "          select 1 " //
			+ "            from cvterm_relationship cvtrcategory " //
			+ "                 inner join cvterm category on cvtrcategory.object_id = category.cvterm_id " //
			+ "            where scale.cvterm_id = cvtrcategory.subject_id " //
			+ "              and cvtrcategory.type_id = " + TermId.HAS_VALUE.getId() //
			+ "              and ph2." + filterByDraftOrValue + " = category.name " //
			+ "          ) " //
			+ "        when dataType.cvterm_id = " + TermId.NUMERIC_VARIABLE.getId() //
			// get the numericals whose value is not within bounds
			+ "          then cast(ph2." + filterByDraftOrValue + " as unsigned) < scaleMinRange.value or cast(ph2." + filterByDraftOrValue
			+ " as unsigned) > scaleMaxRange.value " //
			+ "            or cast(ph2." + filterByDraftOrValue + " as unsigned) < vo.expected_min or cast(ph2." + filterByDraftOrValue
			+ " as unsigned) > vo.expected_max "
			//
			+ "        else false " //
			+ "        end " //
			+ "    )"); //
	}

	private void appendTraitStatusFilterToQuery(final StringBuilder sql, final String filterByVariableSQL, final String filterClause) {
		sql.append(
			" and EXISTS ( " //
				+ "    SELECT 1 " //
				+ "    FROM phenotype ph2 " //
				+ "    WHERE ph2.nd_experiment_id = nde.nd_experiment_id " //
				+ filterByVariableSQL
				+ filterClause + ") "); //
	}

	private void appendTraitValueFilteringToQuery(final StringBuilder sql, final String filterByDraftOrValue, final String variableId,
		final boolean performLikeOperation) {
		final String matchClause = performLikeOperation ? " LIKE :" + variableId + "_text " : " IN (:" + variableId + "_values) ";
		sql.append(
			" and EXISTS ( " //
				+ "    SELECT 1 " //
				+ "    FROM phenotype ph2 " //
				+ "    WHERE ph2.observable_id = :" + variableId + "_Id"
				+ "    AND ph2.nd_experiment_id = nde.nd_experiment_id " //
				+ "    and ph2.").append(filterByDraftOrValue).append(matchClause).append(") ");
	}

	private void applyFactorsFilter(final StringBuilder sql, final String variableId, final String variableType,
		final boolean performLikeOperation) {
		// Check if the variable to be filtered is in one of the columns in stock, nd_experiment, or sum of samples
		final String observationUnitClause = VariableType.OBSERVATION_UNIT.name().equals(variableType) ? "nde.observation_unit_no" : null;
		final String filterClause = factorsFilterMap.get(variableId);
		// Sum of Samples, whose Id is -2, will cause an error as query parameter. Remove the "-" from the ID as workaround
		final String finalId = variableId.replace("-", "");
		final String matchClause = performLikeOperation ? " LIKE :" + finalId + "_text " : " IN (:" + finalId + "_values) ";
		if (filterClause != null || observationUnitClause != null) {
			sql.append(" AND ").append(observationUnitClause != null ? observationUnitClause : filterClause).append(matchClause);
			// If Sum of Samples, append extra closing parenthesis for the EXISTS clause it uses
			if (SUM_OF_SAMPLES_ID.equals(variableId)) {
				sql.append(") ");
			}
			return;
		}

		// Otherwise, look in "props" tables
		// If doing text searching, perform LIKE operation. Otherwise perform value "IN" operation
		if (EXP_PROPS_VAR_TYPES.contains(variableType)) {
			sql.append(" AND EXISTS ( SELECT 1 FROM nd_experimentprop xp "
				+ "WHERE xp.nd_experiment_id = plot.nd_experiment_id AND xp.type_id = :" + variableId
				+ "_Id AND value ").append(matchClause).append(" )");

		} else if (VariableType.GERMPLASM_DESCRIPTOR.name().equals(variableType)) {
			// IF searching by list of values, search for the values in:
			// 1)cvterm.name (for categorical variables) or
			// 2)perform IN operation on stockprop.value
			// Otherwise, search the value like a text by LIKE operation
			final String stockMatchClause = performLikeOperation ? "sp.value LIKE :" + variableId + "_text " :
				" (cvt.name IN (:" + variableId + "_values) OR sp.value IN (:" + variableId + "_values ))";
			sql.append(" AND EXISTS ( SELECT 1 FROM stockprop sp "
				+ "LEFT JOIN cvterm cvt ON cvt.cvterm_id = sp.value "
				+ "WHERE sp.stock_id = s.stock_id AND sp.type_id = :" + variableId
				+ "_Id AND ").append(stockMatchClause).append(" )");
		}
	}

	private static void addFilteredValueParams(final SQLQuery query, final ObservationUnitsSearchDTO.Filter filter) {
		if (filter == null) {
			return;
		}

		final Map<String, List<String>> filteredValues = filter.getFilteredValues();

		if (filteredValues != null && !filteredValues.isEmpty()) {
			final Integer variableId = filter.getVariableId();
			for (final Map.Entry<String, List<String>> entry : filteredValues.entrySet()) {
				final String observableId = entry.getKey();
				if (variableId != null && !variableId.equals(Integer.valueOf(observableId))) {
					continue;
				}
				final String variableType = filter.getVariableTypeMap().get(observableId);
				if (!VariableType.OBSERVATION_UNIT.name().equals(variableType) && factorsFilterMap.get(observableId) == null) {
					query.setParameter(observableId + "_Id", observableId);
				}
				// Sum of Samples, whose Id is -2, will cause an error as query parameter. Remove the "-" from the ID as workaround
				final String finalId = observableId.replace("-", "");
				final List<String> values = filteredValues.get(observableId);
				// Treat "-" as "0: for Sum of Samples variable value
				if (SUM_OF_SAMPLES_ID.equals(observableId)) {
					Collections.replaceAll(values, "-", "0");
				}
				query.setParameterList(finalId + "_values", values);
			}
		}

		final Map<String, String> filteredTextValues = filter.getFilteredTextValues();
		if (filteredTextValues != null && !filteredTextValues.isEmpty()) {
			final Integer variableId = filter.getVariableId();
			for (final Map.Entry<String, String> entry : filteredTextValues.entrySet()) {
				final String observableId = entry.getKey();
				if (variableId != null && !variableId.equals(Integer.valueOf(observableId))) {
					continue;
				}
				final String variableType = filter.getVariableTypeMap().get(observableId);
				if (!VariableType.OBSERVATION_UNIT.name().equals(variableType) && factorsFilterMap.get(observableId) == null) {
					query.setParameter(observableId + "_Id", observableId);
				}
				query.setParameter(observableId + "_text", "%" + filteredTextValues.get(observableId) + "%");
			}
		}
	}

	private String addScalarForSpecificTrait(final ObservationUnitsSearchDTO params, final SQLQuery query) {
		for (final MeasurementVariableDto measurementVariable : params.getSelectionMethodsAndTraits()) {
			if (measurementVariable.getId().equals(params.getFilter().getVariableId())) {
				query.addScalar(measurementVariable.getName()); // Value
				query.addScalar(measurementVariable.getName() + "_PhenotypeId", new IntegerType());
				query.addScalar(measurementVariable.getName() + "_Status");
				query.addScalar(measurementVariable.getName() + "_CvalueId", new IntegerType());
				query.addScalar(measurementVariable.getName() + "_DraftValue");
				query.addScalar(measurementVariable.getName() + "_DraftCvalueId", new IntegerType());
				return measurementVariable.getName();
			}
		}
		return StringUtils.EMPTY;
	}

	private List<ObservationUnitRow> mapToObservationUnitRow(
		final List<Map<String, Object>> results, final ObservationUnitsSearchDTO searchDto, final String measurementVariableName) {
		final List<ObservationUnitRow> dataList = new ArrayList<>();

		if (results != null && !results.isEmpty()) {
			for (final Map<String, Object> row : results) {
				final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
				final Map<String, ObservationUnitData> variables = new HashMap<>();

				for (final MeasurementVariableDto variable : searchDto.getSelectionMethodsAndTraits()) {

					final Integer observationUnitId = (Integer) row.get(OBSERVATION_UNIT_ID);
					if (variable.getId().equals(searchDto.getFilter().getVariableId())) {
						final String value = (String) row.get(measurementVariableName);
						final String draftValue = (String) row.get(measurementVariableName + "_DraftValue");
						final String status = (String) row.get(measurementVariableName + "_Status");
						final Integer variableId = searchDto.getFilter().getVariableId();
						final Integer categoricalValueId = (Integer) row.get(measurementVariableName + "_CvalueId");
						final Integer observationId = (Integer) row.get(measurementVariableName + "_PhenotypeId");
						final Phenotype.ValueStatus valueStatus = status != null ? Phenotype.ValueStatus.valueOf(status) : null;
						final Integer draftCategoricalValueId = (Integer) row.get(measurementVariableName + "_DraftCvalueId");

						final ObservationUnitData observationUnitData = new ObservationUnitData(
							observationId,
							categoricalValueId,
							value,
							valueStatus,
							variableId, draftCategoricalValueId, draftValue);

						variables.put(variableId.toString(), observationUnitData);
					}

					observationUnitRow.setObservationUnitId(observationUnitId);
					observationUnitRow.setVariables(variables);
				}

				dataList.add(observationUnitRow);
			}
		}

		return dataList;
	}

	private void addScalarForTraits(
		final List<MeasurementVariableDto> selectionMethodsAndTraits, final SQLQuery createSQLQuery, final Boolean addStatus) {
		for (final MeasurementVariableDto measurementVariable : selectionMethodsAndTraits) {
			createSQLQuery.addScalar(measurementVariable.getName()); // Value
			createSQLQuery.addScalar(measurementVariable.getName() + "_PhenotypeId", new IntegerType());
			if (addStatus) {
				createSQLQuery.addScalar(measurementVariable.getName() + "_Status");
			}
			createSQLQuery.addScalar(measurementVariable.getName() + "_CvalueId", new IntegerType());
			createSQLQuery.addScalar(measurementVariable.getName() + "_DraftValue");
			createSQLQuery.addScalar(measurementVariable.getName() + "_DraftCvalueId", new IntegerType());
		}
	}

	private List<ObservationUnitRow> convertToObservationUnitRows(final List<Map<String, Object>> results,
		final ObservationUnitsSearchDTO searchDto,
		final String observationVariableName) {
		final List<ObservationUnitRow> observationUnitRows = new ArrayList<>();

		if (results != null && !results.isEmpty()) {
			for (final Map<String, Object> row : results) {
				final ObservationUnitRow observationUnitRow = this.getObservationUnitRow(searchDto, observationVariableName, row);
				observationUnitRows.add(observationUnitRow);
			}
		}

		return observationUnitRows;
	}

	private ObservationUnitRow getObservationUnitRow(final ObservationUnitsSearchDTO searchDto, final String observationVariableName,
		final Map<String, Object> row) {
		final Map<String, ObservationUnitData> environmentVariables = new HashMap<>();
		final Map<String, ObservationUnitData> observationVariables = new HashMap<>();

		for (final MeasurementVariableDto variable : searchDto.getSelectionMethodsAndTraits()) {
			final String status = (String) row.get(variable.getName() + "_Status");
			final ObservationUnitData observationUnitData = new ObservationUnitData( //
				(Integer) row.get(variable.getName() + "_PhenotypeId"), //
				(Integer) row.get(variable.getName() + "_CvalueId"), //
				(String) row.get(variable.getName()), // Value
				(status != null ? Phenotype.ValueStatus.valueOf(status) : null), //
				variable.getId());
			observationUnitData.setDraftValue((String) row.get(variable.getName() + "_DraftValue"));
			observationUnitData.setDraftCategoricalValueId((Integer) row.get(variable.getName() + "_DraftCvalueId"));

			observationVariables.put(variable.getName(), observationUnitData);
		}
		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();

		observationUnitRow.setObservationUnitId((Integer) row.get(OBSERVATION_UNIT_ID));
		observationUnitRow.setAction(((Integer) row.get(OBSERVATION_UNIT_ID)).toString());
		observationUnitRow.setObsUnitId((String) row.get(OBS_UNIT_ID));
		observationUnitRow.setSamplesCount((String) row.get(SUM_OF_SAMPLES));
		final Integer gid = (Integer) row.get(GID);
		observationUnitRow.setGid(gid);
		observationVariables.put(GID, new ObservationUnitData(gid.toString()));

		final String designation = (String) row.get(DESIGNATION);
		observationUnitRow.setDesignation(designation);
		observationVariables.put(DESIGNATION, new ObservationUnitData(designation));

		final Integer trialInstance = (Integer) row.get(TRIAL_INSTANCE);
		observationUnitRow.setTrialInstance(trialInstance);

		observationVariables.put(TRIAL_INSTANCE, new ObservationUnitData(String.valueOf(trialInstance)));

		final String entryNumber = (String) row.get(ENTRY_NO);
		if (trialInstance != null) {
			observationUnitRow.setEntryNumber(Integer.valueOf(entryNumber));
		}
		observationVariables.put(ENTRY_NO, new ObservationUnitData(entryNumber));

		observationVariables.put(ENTRY_TYPE, new ObservationUnitData((String) row.get(ENTRY_TYPE)));
		observationVariables.put(ENTRY_CODE, new ObservationUnitData((String) row.get(ENTRY_CODE)));
		observationVariables.put(REP_NO, new ObservationUnitData((String) row.get(REP_NO)));
		observationVariables.put(PLOT_NO, new ObservationUnitData((String) row.get(PLOT_NO)));
		observationVariables.put(BLOCK_NO, new ObservationUnitData((String) row.get(BLOCK_NO)));
		observationVariables.put(ROW, new ObservationUnitData((String) row.get(ROW)));
		observationVariables.put(COL, new ObservationUnitData((String) row.get(COL)));
		observationVariables.put(OBS_UNIT_ID, new ObservationUnitData((String) row.get(OBS_UNIT_ID)));
		observationVariables.put(PARENT_OBS_UNIT_ID, new ObservationUnitData((String) row.get(PARENT_OBS_UNIT_ID)));
		observationVariables.put(FIELD_MAP_COLUMN, new ObservationUnitData((String) row.get(FIELD_MAP_COLUMN)));
		observationVariables.put(FIELD_MAP_RANGE, new ObservationUnitData((String) row.get(FIELD_MAP_RANGE)));
		observationVariables.put(LOCATION_ID, new ObservationUnitData((String) row.get(LOCATION_ID)));
		observationVariables.put(EXPT_DESIGN, new ObservationUnitData((String) row.get(EXPT_DESIGN)));
		observationVariables.put(observationVariableName, new ObservationUnitData((String) row.get(OBSERVATION_UNIT_NO)));

		for (final String gpDesc : searchDto.getGenericGermplasmDescriptors()) {
			observationVariables.put(gpDesc, new ObservationUnitData((String) row.get(gpDesc)));
		}
		for (final String designFactor : searchDto.getAdditionalDesignFactors()) {
			observationVariables.put(designFactor, new ObservationUnitData((String) row.get(designFactor)));
		}

		// Variables retrieved from Environment Details/Conditions are loaded in a separate Map object to ensure that no duplicate variables are
		// added to a map. Because it's possible that a variable exists in both environment and observation/subobservation
		// levels.
		for (final MeasurementVariableDto envFactor : searchDto.getEnvironmentDetails()) {
			final ObservationUnitData observationUnitData = new ObservationUnitData();
			final String environmentFactorColumnName = this.getEnvironmentColumnName(envFactor.getName());
			observationUnitData.setVariableId(envFactor.getId());
			observationUnitData.setValue((String) row.get(environmentFactorColumnName));
			environmentVariables.put(envFactor.getName(), observationUnitData);
		}
		for (final MeasurementVariableDto envCondition : searchDto.getEnvironmentConditions()) {
			final ObservationUnitData observationUnitData = new ObservationUnitData();
			final String environmentConditionColumnName = this.getEnvironmentColumnName(envCondition.getName());
			observationUnitData.setVariableId(envCondition.getId());
			observationUnitData.setValue((String) row.get(environmentConditionColumnName));
			environmentVariables.put(envCondition.getName(), observationUnitData);
		}

		observationUnitRow.setVariables(observationVariables);
		observationUnitRow.setEnvironmentVariables(environmentVariables);
		return observationUnitRow;
	}

	private String getEnvironmentColumnName(final String variableName) {
		return variableName + ENVIRONMENT_COLUMN_NAME_SUFFIX;
	}

	private List<Map<String, Object>> getObservationUnitTableAsMapListResult(final ObservationUnitsSearchDTO searchDto,
		final String observationVariableName) {
		try {

			final String sql = this.getObservationUnitTableQuery(searchDto, observationVariableName);
			final SQLQuery query = this.getSession().createSQLQuery(sql);

			for (final String columnName : searchDto.getFilterColumns()) {
				query.addScalar(columnName);
			}

			this.setParameters(searchDto, query);
			return this.convertSelectionAndTraitColumnsValueType(query.list(), searchDto.getSelectionMethodsAndTraits());

		} catch (final Exception e) {
			final String error = "An internal error has ocurred when trying to execute the operation " + e.getMessage();
			ObservationUnitsSearchDao.LOG.error(error);
			throw new MiddlewareException(error, e);
		}
	}

	protected List<Map<String, Object>> convertSelectionAndTraitColumnsValueType(final List<Map<String, Object>> result,
		final List<MeasurementVariableDto> selectionAndTraits) {
		final Iterator<Map<String, Object>> iterator = result.iterator();
		while (iterator.hasNext()) {
			final Map<String, Object> rowMap = iterator.next();
			for (final MeasurementVariableDto measurementVariableDto : selectionAndTraits) {
				// Unfortunately, since the trait values (Numerical or Categorical with numeric code) are stored and returned as String from the database,
				// We have to manually convert them to numeric data type (if possible) so that the data returned to the client will be properly processed
				// when we send it to the OpenCPU API. Also, if a trait's value is "missing", we should return it as a null value in order for
				// OpenCPU to recognize it as 'NA' (Not Available).
				if (rowMap.containsKey(measurementVariableDto.getName())) {
					final String stringValue = String.valueOf(rowMap.get(measurementVariableDto.getName()));
					if (Phenotype.MISSING_VALUE.equals(stringValue)) {
						rowMap.put(measurementVariableDto.getName(), null);
					} else {
						// Convert numeric sting to BigDecimal to support signed decimal number
						rowMap.put(measurementVariableDto.getName(),
							NumberUtils.isNumber(stringValue) ? NumberUtils.createBigDecimal(stringValue) :
								rowMap.get(measurementVariableDto.getName()));
					}
				}
			}
		}
		return result;
	}

}
