package org.generationcp.middleware.dao.dms;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.pojos.ims.TransactionType;
import org.generationcp.middleware.service.api.dataset.FilteredPhenotypesInstancesCountDTO;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ObservationUnitsSearchDao extends GenericDAO<ExperimentModel, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(ObservationUnitsSearchDao.class);
	private static final List<Integer> STANDARD_DATASET_VARIABLE_IDS =
		Arrays.asList(TermId.TRIAL_INSTANCE_FACTOR.getId(), TermId.LOCATION_ID.getId(),
			TermId.EXPERIMENT_DESIGN_FACTOR.getId(), TermId.GID.getId(), TermId.ENTRY_TYPE.getId(), TermId.ENTRY_NO.getId(),
			TermId.REP_NO.getId(),
			TermId.PLOT_NO.getId(), TermId.BLOCK_NO.getId(), TermId.ROW.getId(), TermId.COL.getId(), TermId.FIELDMAP_RANGE.getId(),
			TermId.DESIG.getId(),
			TermId.FIELDMAP_COLUMN.getId(), TermId.OBS_UNIT_ID.getId(), TermId.CROSS.getId());

	private static final List<Integer> REMOVE_FILTERS = Lists.newArrayList(TermId.FEMALE_PARENT_GID.getId(),
		TermId.FEMALE_PARENT_NAME.getId(), TermId.MALE_PARENT_GID.getId(), TermId.MALE_PARENT_NAME.getId());
	private static final String LIMIT_CLAUSE = " LIMIT 5000 ";

	private static final String SUM_OF_SAMPLES_ID = "-2";
	private static final String OBSERVATION_UNIT_ID = "observationUnitId";
	static final String PARENT_OBS_UNIT_ID = "PARENT_OBS_UNIT_ID";
	protected static final String SUM_OF_SAMPLES = "SUM_OF_SAMPLES";
	protected static final String STOCK_ID = "STOCK_ID";
	private static final String OBSERVATION_UNIT_NO = "OBSERVATION_UNIT_NO";
	private static final String FILE_COUNT = "FILE_COUNT";
	private static final String FILE_TERM_IDS = "FILE_TERM_IDS";
	private static final String INSTANCE_ID = "instanceId";
	public static final String LOCATION_ID = "LOCATION_ID";
	public static final String EXPT_DESIGN = "EXPT_DESIGN";
	public static final String TRIAL_INSTANCE = "TRIAL_INSTANCE";
	public static final String GID = "GID";
	public static final String DESIGNATION = "DESIGNATION";
	public static final String ENTRY_NO = "ENTRY_NO";
	public static final String OBS_UNIT_ID = "OBS_UNIT_ID";

	protected static final Set<String> OBSERVATIONS_TABLE_SYSTEM_COLUMNS =
		Sets.newHashSet(TRIAL_INSTANCE, GID, DESIGNATION, ENTRY_NO, OBS_UNIT_ID, OBSERVATION_UNIT_ID, SUM_OF_SAMPLES, PARENT_OBS_UNIT_ID,
			FILE_TERM_IDS,
			LOCATION_ID, EXPT_DESIGN, FILE_COUNT);

	private static final Map<String, String> factorsFilterMap = new HashMap<>();
	private static final String ENVIRONMENT_COLUMN_NAME_SUFFIX = "_ENVIRONMENT";
	private static final List<String> EXP_PROPS_VAR_TYPES =
		Arrays.asList(VariableType.EXPERIMENTAL_DESIGN.name(), VariableType.TREATMENT_FACTOR.name());
	private static final String GERMPLASM_JOIN = " LEFT JOIN germplsm g on g.gid = s.dbxref_id ";
	private static final String NAME_JOIN = " INNER JOIN names name ON name.gid = s.dbxref_id and name.nstat = 1 ";
	private static final String IMMEDIATE_SOURCE_NAME_JOIN =
		" LEFT JOIN names immediateSource  ON g.gpid2 = immediateSource.gid AND immediateSource.nstat = 1 ";
	private static final String GROUP_SOURCE_NAME_JOIN =
		" LEFT JOIN names groupSourceName ON groupSourceName.gid = g.gpid1 AND g.gnpgs < 0 AND groupSourceName.nstat = 1 ";
	private static final String LOCATION_JOIN =
		" LEFT JOIN nd_geolocationprop gprop on gprop.nd_geolocation_id = gl.nd_geolocation_id and gprop.type_id = "
			+ TermId.LOCATION_ID.getId()
			+ " LEFT JOIN location loc on loc.locid = gprop.value ";
	private static final String BREEDING_METHODS_ABBR_JOIN =
		"LEFT JOIN methods m ON m.mid = g.methn ";

	static {
		factorsFilterMap.put(String.valueOf(TermId.GID.getId()), "s.dbxref_id");
		factorsFilterMap.put(String.valueOf(TermId.DESIG.getId()), "name.nval");
		factorsFilterMap.put(String.valueOf(TermId.ENTRY_NO.getId()), "s.uniquename");
		factorsFilterMap.put(String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()), "gl.description");
		factorsFilterMap.put(SUM_OF_SAMPLES_ID,
			"EXISTS ( SELECT 1 FROM sample AS sp WHERE nde.nd_experiment_id = sp.nd_experiment_id HAVING count(sample_id)");
		factorsFilterMap.put(String.valueOf(TermId.OBS_UNIT_ID.getId()), "nde.obs_unit_id");
		factorsFilterMap.put(String.valueOf(TermId.STOCK_ID.getId()),
			"EXISTS ( SELECT 1 FROM ims_experiment_transaction ndt \n"
				+ "   inner join ims_transaction tr on tr.trnid = ndt.trnid and ndt.type = " + ExperimentTransactionType.PLANTING.getId()
				+ "   and tr.trntype = " + TransactionType.WITHDRAWAL.getId() + "  and tr.trnstat != "
				+ TransactionStatus.CANCELLED.getIntValue()
				+ "   inner join ims_lot lot on lot.lotid = tr.lotid \n"
				+ "    WHERE ndt.nd_experiment_id = nde.nd_experiment_id \n"
				+ "     and lot.stock_id");
		factorsFilterMap.put(String.valueOf(TermId.GROUPGID.getId()), "g.mgid");
		factorsFilterMap.put(String.valueOf(TermId.GUID.getId()), "g.germplsm_uuid");
		factorsFilterMap.put(String.valueOf(TermId.GROUP_SOURCE_NAME.getId()), "groupSourceName.nval");
		factorsFilterMap.put(String.valueOf(TermId.IMMEDIATE_SOURCE_NAME.getId()), "immediateSource.nval");
		factorsFilterMap.put(String.valueOf(TermId.BREEDING_METHOD_ABBR.getId()), "m.mcode");
		factorsFilterMap.put(String.valueOf(TermId.LOCATION_ID.getId()), "loc.lname");
	}

	private static final Map<String, String> geolocSpecialFactorsMap = new HashMap<>();

	static {
		geolocSpecialFactorsMap.put("TRIAL_INSTANCE", "gl.description");
		geolocSpecialFactorsMap.put("SITE_LAT", "gl.latitude");
		geolocSpecialFactorsMap.put("SITE_LONG", "gl.longitude");
		geolocSpecialFactorsMap.put("SITE_ALT", "gl.altitude");
		geolocSpecialFactorsMap.put("SITE_DATUM", "gl.geodetic_datum");
	}

	private static final Map<String, String> mainVariablesMap = new HashMap<>();

	static {
		// NOTE: Column names will be replaced by queried standard variable names (not hardcoded)
		mainVariablesMap.put(OBSERVATION_UNIT_ID, "    nde.nd_experiment_id as observationUnitId");
		mainVariablesMap.put(String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()), "    gl.description AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.LOCATION_ID.getId()),
			"    (SELECT loc.lname FROM nd_geolocationprop gprop INNER JOIN location loc on loc.locid = gprop.value WHERE gprop.nd_geolocation_id = gl.nd_geolocation_id and gprop.type_id = 8190) AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.EXPERIMENT_DESIGN_FACTOR.getId()),
			"    (SELECT edesign.name FROM nd_geolocationprop gprop INNER JOIN cvterm edesign on edesign.cvterm_id = gprop.value WHERE gprop.nd_geolocation_id = gl.nd_geolocation_id and gprop.type_id = 8135) AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.GID.getId()), "    s.dbxref_id AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.DESIG.getId()), "    name.nval AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.REP_NO.getId()),
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ndep.type_id = 8210) AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.PLOT_NO.getId()),
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ndep.type_id = 8200) AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.BLOCK_NO.getId()),
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ndep.type_id = 8220) AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.ROW.getId()),
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ndep.type_id = 8581) AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.COL.getId()),
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ndep.type_id = 8582) AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.FIELDMAP_COLUMN.getId()),
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ndep.type_id = 8400) AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.FIELDMAP_RANGE.getId()),
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ndep.type_id = 8410) AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.OBS_UNIT_ID.getId()), "    nde.obs_unit_id AS '%s'");
		mainVariablesMap.put(PARENT_OBS_UNIT_ID, "    parent.obs_unit_id as PARENT_OBS_UNIT_ID");
		mainVariablesMap.put(SUM_OF_SAMPLES, "    coalesce(nullif((SELECT count(sp.sample_id) "
			+ "        FROM sample sp "
			+ "        WHERE sp.nd_experiment_id = nde.nd_experiment_id) "
			+ "         + coalesce(child_sample_count.count, 0), 0), '-') AS 'SUM_OF_SAMPLES'");
		mainVariablesMap.put(STOCK_ID, "    coalesce(nullif((SELECT distinct(lot.stock_id) "
			+ "        FROM ims_experiment_transaction  ndt "
			+ "        inner join ims_transaction tr on tr.trnid = ndt.trnid and ndt.type = "
			+ ExperimentTransactionType.PLANTING.getId()
			+ "        and tr.trntype = " + TransactionType.WITHDRAWAL.getId() + "  and tr.trnstat != "
			+ TransactionStatus.CANCELLED.getIntValue()
			+ "        inner join ims_lot lot on lot.lotid = tr.lotid \n"
			+ "        WHERE ndt.nd_experiment_id = nde.nd_experiment_id), '-') ) AS 'STOCK_ID'");
		mainVariablesMap.put(FILE_COUNT,
			"(select count(1) from file_metadata fm where fm.nd_experiment_id = nde.nd_experiment_id) as '" + FILE_COUNT + "'");
		mainVariablesMap.put(FILE_TERM_IDS, "(select group_concat(fcvt.cvterm_id separator ',') from file_metadata fm "
			+ " inner join file_metadata_cvterm fcvt on fm.file_id = fcvt.file_metadata_id"
			+ " where fm.nd_experiment_id = nde.nd_experiment_id) as '" + FILE_TERM_IDS + "'");
		mainVariablesMap.put(String.valueOf(TermId.CROSS.getId()), "    s.cross_value AS '%s'");
	}

	public ObservationUnitsSearchDao(final Session session) {
		super(session);
	}

	public Integer countObservationUnitsForDataset(final Integer datasetId, final List<Integer> instanceIds, final Boolean draftMode,
		final ObservationUnitsSearchDTO.Filter filter) {

		try {

			final StringBuilder sql = new StringBuilder("SELECT COUNT(1) as totalObservationUnits FROM ( ");

			sql.append("SELECT ");

			final List<String> columns = new ArrayList<>();

			columns.add("nde.nd_experiment_id ");
			if (filter != null && filter.getFilteredTextValues() != null && !filter.getFilteredTextValues().isEmpty()) {
				filter.getFilteredTextValues()
					.entrySet()
					.stream()
					.filter(mapEntry -> {
						final String variableType = filter.getVariableTypeMap().get(mapEntry.getKey());
						return VariableType.GERMPLASM_ATTRIBUTE.name().equals(variableType)
							|| VariableType.GERMPLASM_PASSPORT.name().equals(variableType);
					}).forEach(mapEntry -> {
						final String alias = this.formatVariableAlias(mapEntry.getKey());
						columns.add(
							String.format("(SELECT aval FROM atributs WHERE gid = s.dbxref_id AND atype = %1$s) AS %2$s",
								mapEntry.getKey(),
								alias));
					});

				filter.getFilteredTextValues()
					.entrySet()
					.stream()
					.filter(mapEntry -> {
						if (String.valueOf(TermId.IMMEDIATE_SOURCE_NAME.getId()).equals(mapEntry.getKey()) ||
							String.valueOf(TermId.GROUP_SOURCE_NAME.getId()).equals(mapEntry.getKey()) ||
							String.valueOf(TermId.DESIG.getId()).equals(mapEntry.getKey())) {
							return false;
						}
						final String variableType = filter.getVariableTypeMap().get(mapEntry.getKey());
						return variableType == null;
					}).forEach(mapEntry -> {
						final String alias = this.formatNameAlias(mapEntry.getKey());
						columns.add(
							String.format("(SELECT nval FROM names WHERE gid = s.dbxref_id AND ntype = %1$s) AS %2$s", mapEntry.getKey(),
								alias));
					});
			}

			sql.append(columns.stream().collect(Collectors.joining(", ")));

			sql.append(" FROM nd_experiment nde ")
				.append("    INNER JOIN project p on p.project_id = nde.project_id ")
				.append("    INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id ")
				.append("    LEFT JOIN stock s ON s.stock_id = nde.stock_id ")
				// FIXME won't work for sub-sub-obs
				.append(
					" INNER JOIN nd_experiment plot ON plot.nd_experiment_id = nde.parent_id OR ( plot.nd_experiment_id = nde.nd_experiment_id and nde.parent_id is null ) ");

			this.addCountQueryJoins(sql, filter);

			sql.append(" where p.project_id = :datasetId ");

			if (!CollectionUtils.isEmpty(instanceIds)) {
				sql.append(" and gl.nd_geolocation_id IN (:instanceIds) ");
			}

			if (Boolean.TRUE.equals(draftMode)) {
				sql.append(" and exists(select 1 ")
					.append(" from phenotype ph")
					.append(" where ph.nd_experiment_id = nde.nd_experiment_id ")
					.append(" and (ph.draft_value is not null ")
					.append(" or ph.draft_cvalue_id is not null)) ");
			}

			if (filter != null) {
				this.addFilters(sql, filter, draftMode);
			}

			this.addHavingClause(sql, filter);

			sql.append(") T ");

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			addQueryParams(query, filter);

			query.addScalar("totalObservationUnits", new IntegerType());
			query.setParameter("datasetId", datasetId);

			if (!CollectionUtils.isEmpty(instanceIds)) {
				query.setParameterList("instanceIds", instanceIds);
			}

			return (Integer) query.uniqueResult();
		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
				String.format("Unexpected error in executing countTotalObservations(studyId = %s, instanceNumber = %s) : ",
					datasetId, instanceIds) + he.getMessage(),
				he);
		}
	}

	public FilteredPhenotypesInstancesCountDTO countFilteredInstancesAndObservationUnits(final Integer datasetId,
		final ObservationUnitsSearchDTO observationUnitsSearchDTO) {

		final ObservationUnitsSearchDTO.Filter filter = observationUnitsSearchDTO.getFilter();

		Preconditions.checkNotNull(filter.getVariableId());

		try {
			final StringBuilder sql =
				new StringBuilder("select count(*) as totalObservationUnits, count(distinct(gl.nd_geolocation_id)) as totalInstances from ")
					.append("nd_experiment nde ")
					.append("    inner join project p on p.project_id = nde.project_id ")
					.append("    inner join nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id ")
					.append(" where ")
					.append(" p.project_id = :datasetId ");

			if (!CollectionUtils.isEmpty(observationUnitsSearchDTO.getInstanceIds())) {
				sql.append(" and gl.nd_geolocation_id IN (:instanceIds) ");
			}

			final String filterByVariableSQL =
				(filter.getVariableId() == null) ? StringUtils.EMPTY : "and ph.observable_id = " + filter.getVariableId() + " ";

			if (Boolean.TRUE.equals(observationUnitsSearchDTO.getDraftMode())) {
				sql.append(" and exists(select 1 ");
				sql.append(" from phenotype ph");
				sql.append(" where ph.nd_experiment_id = nde.nd_experiment_id ");
				sql.append(filterByVariableSQL);
				sql.append("         and (ph.draft_value is not null ");
				sql.append("                or ph.draft_cvalue_id is not null)) ");
			} else if (Boolean.TRUE.equals(filter.getVariableHasValue())) {
				sql.append(" and exists(select 1");
				sql.append("   from phenotype ph");
				sql.append("   where ph.nd_experiment_id = nde.nd_experiment_id ");
				sql.append(filterByVariableSQL);
				sql.append("         and (ph.value is not null ");
				sql.append("                or ph.cvalue_id is not null)) ");
			}

			this.addFilters(sql, filter, observationUnitsSearchDTO.getDraftMode());

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			addQueryParams(query, filter);

			query.addScalar("totalObservationUnits", new IntegerType());
			query.addScalar("totalInstances", new IntegerType());

			query.setParameter("datasetId", datasetId);

			if (!CollectionUtils.isEmpty(observationUnitsSearchDTO.getInstanceIds())) {
				query.setParameterList("instanceIds", observationUnitsSearchDTO.getInstanceIds());
			}

			final Object[] result = (Object[]) query.uniqueResult();

			return new FilteredPhenotypesInstancesCountDTO((Integer) result[0], (Integer) result[1]);

		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
				String.format("Unexpected error in executing countTotalObservations(studyId = %s, instanceNumber = %s) : ",
					datasetId, observationUnitsSearchDTO.getInstanceIds()) + he.getMessage(),
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

			if (!CollectionUtils.isEmpty(params.getInstanceIds())) {
				query.setParameterList("instanceIds", params.getInstanceIds());
			}

			if (!CollectionUtils.isEmpty(params.getEnvironmentConditions())) {
				query.setParameter("datasetEnvironmentId", String.valueOf(params.getEnvironmentDatasetId()));
			}

			addQueryParams(query, params.getFilter());

			query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			final List<Map<String, Object>> results = query.list();

			return this.mapToObservationUnitRow(results, params, measurementVariableName);

		} catch (final Exception e) {
			final String error = "An internal error has ocurred when trying to execute the operation " + e.getMessage();
			ObservationUnitsSearchDao.LOG.error(error);
			throw new MiddlewareException(error, e);
		}
	}

	private void addCountQueryJoins(final StringBuilder sql, final ObservationUnitsSearchDTO.Filter filter) {
		if (filter == null) {
			return;
		}

		final Set<String> joins = new LinkedHashSet<>();

		if ((!CollectionUtils.isEmpty(filter.getFilteredValues()) && filter.getFilteredValues().keySet()
			.contains(String.valueOf(TermId.GROUPGID.getId()))) ||
			this.checkFilterContainsFactor(filter, TermId.GUID.getId()) ||
			this.checkFilterContainsFactor(filter, TermId.IMMEDIATE_SOURCE_NAME.getId()) ||
			this.checkFilterContainsFactor(filter, TermId.BREEDING_METHOD_ABBR.getId()) ||
			this.checkFilterContainsFactor(filter, TermId.GROUP_SOURCE_NAME.getId()) ||
			this.checkFilterContainsFactor(filter, TermId.DESIG.getId())) {
			joins.add(GERMPLASM_JOIN);
			joins.add(NAME_JOIN);
		}

		if (this.checkFilterContainsFactor(filter, TermId.IMMEDIATE_SOURCE_NAME.getId())) {
			joins.add(IMMEDIATE_SOURCE_NAME_JOIN);
		}

		if (this.checkFilterContainsFactor(filter, TermId.GROUP_SOURCE_NAME.getId())) {
			joins.add(GROUP_SOURCE_NAME_JOIN);
		}

		if (this.checkFilterContainsFactor(filter, TermId.LOCATION_ID.getId())) {
			joins.add(LOCATION_JOIN);
		}

		if (this.checkFilterContainsFactor(filter, TermId.BREEDING_METHOD_ABBR.getId())) {
			joins.add(BREEDING_METHODS_ABBR_JOIN);
		}

		joins.forEach(sql::append);
	}

	private String getObservationUnitsByVariableQuery(final ObservationUnitsSearchDTO searchDto) {

		final StringBuilder sql = new StringBuilder("SELECT  "
			+ "    nde.nd_experiment_id as observationUnitId, ");

		final StringBuilder traitClauseFormat = new StringBuilder(" MAX(IF(cvterm_variable.name = '%s', ph.value, NULL)) AS '%s',")
			.append(" MAX(IF(cvterm_variable.name = '%s', ph.phenotype_id, NULL)) AS '%s',")
			.append(" MAX(IF(cvterm_variable.name = '%s', ph.status, NULL)) AS '%s',")
			.append(" MAX(IF(cvterm_variable.name = '%s', ph.cvalue_id, NULL)) AS '%s', ")
			.append(" MAX(IF(cvterm_variable.name = '%s', ph.draft_value, NULL)) AS '%s',")
			.append(" MAX(IF(cvterm_variable.name = '%s', ph.draft_cvalue_id, NULL)) AS '%s', ");

		for (final MeasurementVariableDto measurementVariable : searchDto.getDatasetVariables()) {
			if (measurementVariable.getId().equals(searchDto.getFilter().getVariableId())) {
				sql.append(String.format(
					traitClauseFormat.toString(),
					measurementVariable.getName(),
					measurementVariable.getName(), // Value
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
				break;
			}
		}

		sql.append(" 1 FROM ")
			.append(" project p ")
			.append(" INNER JOIN nd_experiment nde ON nde.project_id = p.project_id ")
			.append(" INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id ")
			.append(" INNER JOIN stock s ON s.stock_id = nde.stock_id ")
			.append(" LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id ")
			.append(" LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id ")
			.append(" WHERE p.project_id = :datasetId ");

		if (!CollectionUtils.isEmpty(searchDto.getInstanceIds())) {
			sql.append(" AND gl.nd_geolocation_id IN (:instanceIds)");
		}

		final ObservationUnitsSearchDTO.Filter filter = searchDto.getFilter();
		this.addFilters(sql, filter, searchDto.getDraftMode());

		final String filterByVariableSQL =
			(filter.getVariableId() == null) ? StringUtils.EMPTY : "and ph.observable_id = " + filter.getVariableId() + " ";

		if (Boolean.TRUE.equals(searchDto.getDraftMode())) {
			sql.append(" and exists(select 1")
				.append("   from phenotype ph")
				.append("   where ph.nd_experiment_id = nde.nd_experiment_id ")
				.append(filterByVariableSQL)
				.append("         and (ph.draft_value is not null ")
				.append("                or ph.draft_cvalue_id is not null)) ");
		}

		sql.append(" GROUP BY observationUnitId ");

		return sql.toString();
	}

	public String getObservationVariableName(final int datasetId) {
		final SQLQuery query = this.getSession().createSQLQuery("SELECT pp.alias AS OBSERVATION_UNIT_NO_NAME"
			+ " FROM projectprop pp"
			+ "        INNER JOIN cvterm cvt ON cvt.cvterm_id = pp.type_id"
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

	public Map<Integer, String> queryStandardDatasetVariables() {
		final SQLQuery query = this.getSession().createSQLQuery("SELECT cvterm_id, name from cvterm where cvterm_id in (:cvtermIds)");
		query.addScalar("cvterm_id", new IntegerType());
		query.addScalar("name", new StringType());
		query.setParameterList("cvtermIds", ObservationUnitsSearchDao.STANDARD_DATASET_VARIABLE_IDS);
		final List<Object[]> result = query.list();
		final Map<Integer, String> variableMap = new HashMap<>();
		for (final Object[] variableRow : result) {
			variableMap.put((Integer) variableRow[0], (String) variableRow[1]);
		}
		return variableMap;
	}

	public List<ObservationUnitRow> getObservationUnitTable(final ObservationUnitsSearchDTO searchDto, final Pageable pageable) {
		try {
			final String observationVariableName = this.getObservationVariableName(searchDto.getDatasetId());
			final Map<String, String> finalColumnsQueryMap = new HashMap<>();
			final Map<Integer, String> standardDatasetVariablesMap = this.getStandardDatasetVariablesMap(finalColumnsQueryMap);
			final List<Map<String, Object>> results = this.getObservationUnitsQueryResult(
				searchDto,
				observationVariableName, standardDatasetVariablesMap, finalColumnsQueryMap, pageable, false);
			return this.convertToObservationUnitRows(results, searchDto, observationVariableName, standardDatasetVariablesMap);
		} catch (final Exception e) {
			final String error = "An internal error has ocurred when trying to retrieve observation unit rows " + e.getMessage();
			throw new MiddlewareException(error, e);
		}
	}

	private Map<Integer, String> getStandardDatasetVariablesMap(final Map<String, String> finalColumnsQueryMap) {
		finalColumnsQueryMap.putAll(mainVariablesMap);
		// Set the actual standard variable names from ontology as column names in query
		final Map<Integer, String> standardDatasetVariablesMap = this.queryStandardDatasetVariables();
		standardDatasetVariablesMap.entrySet().forEach(entry -> {
				final String idKey = String.valueOf(entry.getKey());
				if (finalColumnsQueryMap.containsKey(idKey)) {
					finalColumnsQueryMap.put(entry.getValue(), String.format(mainVariablesMap.get(idKey), entry.getValue()));
					finalColumnsQueryMap.remove(idKey);
				}
			}

		);
		return standardDatasetVariablesMap;
	}

	public List<Map<String, Object>> getObservationUnitTableMapList(final ObservationUnitsSearchDTO searchDto,
		final Pageable pageable) {
		try {
			final String observationVariableName = this.getObservationVariableName(searchDto.getDatasetId());
			return this.getObservationUnitTableAsMapListResult(
				searchDto,
				observationVariableName, pageable);
		} catch (final Exception e) {
			final String error =
				"An internal error has ocurred when trying to retrieve observation unit rows as list of map" + e.getMessage();
			throw new MiddlewareException(error, e);
		}
	}

	private List<Map<String, Object>> getObservationUnitsQueryResult(final ObservationUnitsSearchDTO searchDto,
		final String observationVariableName, final Map<Integer, String> standardDatasetVariablesMap,
		final Map<String, String> finalColumnsQueryMap, final Pageable pageable, final boolean forVisualization) {
		try {

			final String sql = this.getObservationUnitTableQuery(searchDto, observationVariableName,
				standardDatasetVariablesMap.get(TermId.PLOT_NO.getId()), finalColumnsQueryMap, pageable, forVisualization);
			final SQLQuery query = this.createQueryAndAddScalar(searchDto, sql, standardDatasetVariablesMap, forVisualization);
			this.setParameters(searchDto, query, pageable);
			return query.list();

		} catch (final Exception e) {
			final String error = "An internal error has ocurred when trying to execute the operation " + e.getMessage();
			ObservationUnitsSearchDao.LOG.error(error);
			throw new MiddlewareException(error, e);
		}
	}

	private void setParameters(final ObservationUnitsSearchDTO searchDto, final SQLQuery query, final Pageable pageable) {
		query.setParameter("datasetId", searchDto.getDatasetId());

		if (!CollectionUtils.isEmpty(searchDto.getInstanceIds())) {
			query.setParameterList("instanceIds", searchDto.getInstanceIds());
		}

		if (!CollectionUtils.isEmpty(searchDto.getEnvironmentConditions()) &&
			searchDto.getEnvironmentConditions().stream().filter(dto -> this.isColumnVisible(dto.getName(), searchDto.getVisibleColumns()))
				.count() > 0) {
			query.setParameter("datasetEnvironmentId", String.valueOf(searchDto.getEnvironmentDatasetId()));
		}

		if (!CollectionUtils.isEmpty(searchDto.getFilter().getPreFilteredGids())) {
			query.setParameterList("preFilteredGids", searchDto.getFilter().getPreFilteredGids());
		}

		addQueryParams(query, searchDto.getFilter());

		addPaginationToSQLQuery(query, pageable);

		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
	}

	private SQLQuery createQueryAndAddScalar(
		final ObservationUnitsSearchDTO searchDto, final String generateQuery, final Map<Integer, String> standardVariableNamesMap,
		final boolean forVisualization) {
		final SQLQuery query = this.getSession().createSQLQuery(generateQuery);
		if (forVisualization) {
			searchDto.getVisibleColumns().forEach(query::addScalar);
		} else {
			this.addScalar(query, standardVariableNamesMap);

			this.addScalarForTraits(searchDto.getDatasetVariables(), query, true);
			this.addScalarForEntryDetails(searchDto.getEntryDetails(), query);

			if (!CollectionUtils.isEmpty(searchDto.getPassportAndAttributes())) {
				searchDto.getPassportAndAttributes().forEach(variableDto -> query.addScalar(this.formatVariableAlias(variableDto.getId())));
			}

			if (!CollectionUtils.isEmpty(searchDto.getNameTypes())) {
				searchDto.getNameTypes().forEach(variableDto -> query.addScalar(this.formatNameAlias(variableDto.getId())));

			}

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
		}

		return query;
	}

	private void addScalar(final SQLQuery createSQLQuery, final Map<Integer, String> standardVariableNames) {
		createSQLQuery.addScalar(ObservationUnitsSearchDao.OBSERVATION_UNIT_ID);
		createSQLQuery.addScalar(standardVariableNames.get(TermId.TRIAL_INSTANCE_FACTOR.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.GID.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.DESIG.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.REP_NO.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.PLOT_NO.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.BLOCK_NO.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.ROW.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.COL.getId()));
		createSQLQuery.addScalar(ObservationUnitsSearchDao.PARENT_OBS_UNIT_ID, new StringType());
		createSQLQuery.addScalar(standardVariableNames.get(TermId.OBS_UNIT_ID.getId()), new StringType());
		createSQLQuery.addScalar(ObservationUnitsSearchDao.SUM_OF_SAMPLES);
		createSQLQuery.addScalar(ObservationUnitsSearchDao.STOCK_ID, new StringType());
		createSQLQuery.addScalar(ObservationUnitsSearchDao.FILE_COUNT, new IntegerType());
		createSQLQuery.addScalar(ObservationUnitsSearchDao.FILE_TERM_IDS, new StringType());
		createSQLQuery.addScalar(standardVariableNames.get(TermId.FIELDMAP_COLUMN.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.FIELDMAP_RANGE.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.LOCATION_ID.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.EXPERIMENT_DESIGN_FACTOR.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.CROSS.getId()));
		createSQLQuery.addScalar(INSTANCE_ID);
	}

	protected boolean isColumnVisible(final String columnName, final Set<String> visibleColumns) {

		// If the visible columns list is not empty, we should only include the columns specified.
		// Exempted are the columns required by the system (e.g. OBSERVATION_UNIT_ID, FILE_TERM_IDS, FILE_COUNT, etc.)
		if (!CollectionUtils.isEmpty(visibleColumns) && OBSERVATIONS_TABLE_SYSTEM_COLUMNS.stream()
			.noneMatch(s -> s.equalsIgnoreCase(columnName))) {
			return visibleColumns.stream().anyMatch(s -> s.equalsIgnoreCase(columnName));
		}
		// If the visible columns list is not specified, process and retrieve the column by default.
		return true;
	}

	private String getObservationUnitTableQuery(
		final ObservationUnitsSearchDTO searchDto, final String observationUnitNoName, final String plotNoName,
		final Map<String, String> finalColumnsQueryMap, final Pageable pageable, final boolean forVisualization) {

		// FIXME some props should be fetched from plot, not immediate parent. It won't work for sub-sub obs
		//  same for columns -> DatasetServiceImpl.getSubObservationSetColumns

		/** NOTE: If ObservationUnitsSearchDTO#getVisibleColumns() has values, we should only include the specified columns to the query.
		 * This will optimize the query by processing only the columns it needs, reducing sub-queries and to minimize the amount of data
		 * that will be returned to the client.
		 *
		 * If visibleColumns is not specified, the query will process and return all columns by default. **/

		final List<String> columns = new ArrayList<>();

		columns.add("gl.nd_geolocation_id as " + INSTANCE_ID);

		finalColumnsQueryMap.forEach((columnName, columnQueryString) -> {
			if (this.isColumnVisible(columnName, searchDto.getVisibleColumns())) {
				columns.add(finalColumnsQueryMap.get(columnName));
			} else {
				columns.add(String.format("NULL as `%s`", columnName));
			}
		});

		searchDto.getEntryDetails().forEach(measurementVariable -> {
			final StringBuilder entryDetailsClauseFormat = new StringBuilder();

			if (this.isColumnVisible(measurementVariable.getName(), searchDto.getVisibleColumns())) {
				if (TermId.ENTRY_NO.name().equals(measurementVariable.getName())) {
					entryDetailsClauseFormat.append(" s.uniquename AS `%1$s`,");
				} else {
					entryDetailsClauseFormat.append(" MAX(IF(cvterm_entry_variable.name = '%1$s', sp.value, NULL)) AS `%1$s`,");
				}
				entryDetailsClauseFormat.append(
					" MAX(IF(cvterm_entry_variable.name = '%1$s', sp.stockprop_id, NULL)) AS `%1$s_StockPropId`,");
				entryDetailsClauseFormat.append(" MAX(IF(cvterm_entry_variable.name = '%1$s', sp.cvalue_id, NULL)) AS `%1$s_CvalueId`");
			} else {
				entryDetailsClauseFormat.append(" NULL AS `%1$s`,");
				entryDetailsClauseFormat.append(" NULL AS `%1$s_StockPropId`,");
				entryDetailsClauseFormat.append(" NULL AS `%1$s_CvalueId`");
			}

			columns.add(String.format(entryDetailsClauseFormat.toString(), measurementVariable.getName()));
		});

		if (forVisualization) {

			final String traitClauseFormat = " MAX(IF(cvterm_variable.name = '%s', ph.value, NULL)) AS `%s`";
			final String traitDraftClauseFormat = " MAX(IF(cvterm_variable.name = '%s', ph.draft_value, NULL)) AS `%s`";
			final String traitClauseFormatNullValue = " MAX(IF(cvterm_variable.name = '%s', ph.value, NULL)) AS `%s`";
			final String traitDraftClauseFormatNullValue = " MAX(IF(cvterm_variable.name = '%s', ph.draft_value, NULL)) AS `%s`";

			for (final MeasurementVariableDto measurementVariable : searchDto.getDatasetVariables()) {
				if (this.isColumnVisible(measurementVariable.getName(), searchDto.getVisibleColumns())) {
					columns.add(String.format(
						Boolean.TRUE.equals(searchDto.getDraftMode()) ? traitDraftClauseFormat : traitClauseFormat,
						measurementVariable.getName(),
						measurementVariable.getName()));
				} else {
					columns.add(String.format(
						Boolean.TRUE.equals(searchDto.getDraftMode()) ? traitDraftClauseFormatNullValue : traitClauseFormatNullValue,
						measurementVariable.getName(),
						measurementVariable.getName()));
				}
			}

		} else {

			final StringBuilder traitClauseFormat = new StringBuilder(" MAX(IF(cvterm_variable.name = '%1$s', ph.value, NULL)) AS `%1$s`,")
				.append(" MAX(IF(cvterm_variable.name = '%1$s', ph.phenotype_id, NULL)) AS `%1$s_PhenotypeId`,")
				.append(" MAX(IF(cvterm_variable.name = '%1$s', ph.status, NULL)) AS `%1$s_Status`,")
				.append(" MAX(IF(cvterm_variable.name = '%1$s', ph.cvalue_id, NULL)) AS `%1$s_CvalueId`, ")
				.append(" MAX(IF(cvterm_variable.name = '%1$s', ph.draft_value, NULL)) AS `%1$s_DraftValue`,")
				.append(" MAX(IF(cvterm_variable.name = '%1$s', ph.draft_cvalue_id, NULL)) AS `%1$s_DraftCvalueId`");

			final StringBuilder traitClauseFormatNullValues = new StringBuilder(" NULL AS `%1$s`,")
				.append(" NULL AS `%1$s_PhenotypeId`,")
				.append(" NULL AS `%1$s_Status`,")
				.append(" NULL AS `%1$s_CvalueId`, ")
				.append(" NULL AS `%1$s_DraftValue`,")
				.append(" NULL AS `%1$s_DraftCvalueId`");

			searchDto.getDatasetVariables().forEach(measurementVariable -> {
				if (this.isColumnVisible(measurementVariable.getName(), searchDto.getVisibleColumns())) {
					columns.add(String.format(traitClauseFormat.toString(), measurementVariable.getName()));
				} else {
					columns.add(String.format(traitClauseFormatNullValues.toString(), measurementVariable.getName()));
				}

			});
		}

		if (!CollectionUtils.isEmpty(searchDto.getGenericGermplasmDescriptors())) {
			final String germplasmDescriptorClauseFormat =
				"    (SELECT sprop.value FROM stockprop sprop INNER JOIN cvterm spropcvt ON spropcvt.cvterm_id = sprop.type_id WHERE sprop.stock_id = s.stock_id AND %s) AS `%s`";
			for (final String gpFactor : searchDto.getGenericGermplasmDescriptors()) {
				if (!gpFactor.equals(TermId.GROUPGID.name()) &&
					!gpFactor.equals(TermId.GUID.name())) {
					final StringBuilder cvtermQuery;
					final boolean isVisible = this.isColumnVisible(gpFactor, searchDto.getVisibleColumns());
					if (TermId.IMMEDIATE_SOURCE_NAME.name().equals(gpFactor) && isVisible) {
						cvtermQuery = new StringBuilder(" CASE ")
							.append(" WHEN g.gnpgs = -1 ")
							.append(" AND g.gpid2 IS NOT NULL ")
							.append(" AND g.gpid2 <> 0 THEN immediateSource.nval ")
							.append(" ELSE '-' ")
							.append(" END AS `" + TermId.IMMEDIATE_SOURCE_NAME.name() + "` ");
					} else if (TermId.GROUP_SOURCE_NAME.name().equals(gpFactor) && isVisible) {
						cvtermQuery = new StringBuilder(" CASE ")
							.append(" WHEN g.gnpgs = -1 ")
							.append(" AND g.gpid1 IS NOT NULL ")
							.append(" AND g.gpid1 <> 0 THEN groupSourceName.nval ")
							.append(" ELSE '-' ")
							.append(" END AS `").append(TermId.GROUP_SOURCE_NAME.name()).append("` ");
					} else if (TermId.BREEDING_METHOD_ABBR.name().equals(gpFactor) && isVisible) {
						cvtermQuery = new StringBuilder(" m.mcode AS `").append(TermId.BREEDING_METHOD_ABBR.name()).append("` ");
					} else if (isVisible) {
						cvtermQuery = new StringBuilder(
							String.format(germplasmDescriptorClauseFormat, "spropcvt.name = '" + gpFactor + "'", gpFactor));
					} else {
						cvtermQuery = new StringBuilder(String.format(" NULL AS `%s` ", gpFactor));
					}
					columns.add(cvtermQuery.toString());
				}
			}
		}

		if (!CollectionUtils.isEmpty(searchDto.getAdditionalDesignFactors())) {
			final String designFactorClauseFormat =
				"    (SELECT xprop.value FROM nd_experimentprop xprop INNER JOIN cvterm xpropcvt ON xpropcvt.cvterm_id = xprop.type_id WHERE xprop.nd_experiment_id = plot.nd_experiment_id AND xpropcvt.name = '%s') AS `%s`";
			for (final String designFactor : searchDto.getAdditionalDesignFactors()) {
				if (this.isColumnVisible(designFactor, searchDto.getVisibleColumns())) {
					columns.add(String.format(designFactorClauseFormat, designFactor, designFactor));
				} else {
					columns.add(String.format(" NULL AS `%s`", designFactor));
				}
			}
		}

		// Variables at environment level are automatically excluded if the query is for Visualization.
		if (!forVisualization && !CollectionUtils.isEmpty(searchDto.getEnvironmentDetails())) {
			for (final MeasurementVariableDto envFactor : searchDto.getEnvironmentDetails()) {
				if (this.isColumnVisible(envFactor.getName(), searchDto.getVisibleColumns()) && geolocSpecialFactorsMap.containsKey(
					envFactor.getName())) {
					final String geolocEnvFactorFormat =
						" %s AS `%s`";
					final String column = geolocSpecialFactorsMap.get(envFactor.getName());
					columns.add(String.format(geolocEnvFactorFormat, column, this.getEnvironmentColumnName(envFactor.getName())));
				} else if (this.isColumnVisible(envFactor.getName(), searchDto.getVisibleColumns())) {
					final String envFactorFormat =
						"    (SELECT gprop.value FROM nd_geolocationprop gprop INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = gprop.type_id AND ispcvt.name = '%s' WHERE gprop.nd_geolocation_id = gl.nd_geolocation_id ) `%s`";
					columns.add(String.format(envFactorFormat, envFactor.getName(), this.getEnvironmentColumnName(envFactor.getName())));
				} else {
					columns.add(String.format(" NULL AS `%s`", this.getEnvironmentColumnName(envFactor.getName())));
				}
			}
		}

		// Variables at environment level are automatically excluded if the query is for Visualization.
		if (!forVisualization && !CollectionUtils.isEmpty(searchDto.getEnvironmentConditions())) {
			for (final MeasurementVariableDto envCondition : searchDto.getEnvironmentConditions()) {
				if (this.isColumnVisible(envCondition.getName(), searchDto.getVisibleColumns())) {
					final StringBuilder envConditionFormat = new StringBuilder(" (SELECT pheno.value from phenotype pheno ")
						.append(" INNER JOIN cvterm envcvt ON envcvt.cvterm_id = pheno.observable_id AND envcvt.name = '%s' ")
						.append(
							" INNER JOIN nd_experiment envnde ON  pheno.nd_experiment_id = envnde.nd_experiment_id AND envnde.project_id = :datasetEnvironmentId ")
						.append(" WHERE envnde.nd_geolocation_id = gl.nd_geolocation_id) `%s`");
					columns.add(
						String.format(envConditionFormat.toString(), envCondition.getName(),
							this.getEnvironmentColumnName(envCondition.getName())));
				} else {
					columns.add(
						String.format(" NULL AS `%s`", this.getEnvironmentColumnName(envCondition.getName())));
				}
			}
		}

		// TODO move PLOT_NO to nd_exp
		if (!forVisualization) {
			if (this.isColumnVisible(observationUnitNoName, searchDto.getVisibleColumns())) {
				// If the request is for Observation/Sub-observation table (forVisualization is false), always add the OBSERVATION_UNIT_NO column.
				columns.add(new StringBuilder(" COALESCE(nde.observation_unit_no, (")
					.append(
						" SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ndep.type_id = 8200 ")
					.append(" )) AS `").append(OBSERVATION_UNIT_NO).append("` ").toString());
			} else {
				columns.add(new StringBuilder(" NULL AS ").append(OBSERVATION_UNIT_NO).toString());
			}
		} else if (this.isColumnVisible(observationUnitNoName, searchDto.getVisibleColumns()) && !plotNoName.equals(
			observationUnitNoName)) {
			// If the request is for Visualization and dataset is sub-observation (observationUnitNoName that is not "PLOT_NO" value means the dataset is sub-observation),
			// we add the OBSERVATION_UNIT_NO column with the observationUnitNoName as its column alias.
			columns.add(new StringBuilder(" COALESCE(nde.observation_unit_no, (")
				.append(
					" SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = plot.nd_experiment_id AND ndep.type_id = 8200 ")
				.append(" )) AS `").append(observationUnitNoName).append("` ").toString());
		}

		if (this.hasDescriptor(searchDto.getGenericGermplasmDescriptors(), TermId.GROUPGID)) {
			columns.add(" g.mgid AS `" + TermId.GROUPGID.name() + "`");
		}

		if (this.hasDescriptor(searchDto.getGenericGermplasmDescriptors(), TermId.GUID)) {
			columns.add(" g.germplsm_uuid AS `" + TermId.GUID.name() + "`");
		}

		if (!CollectionUtils.isEmpty(searchDto.getPassportAndAttributes())) {
			for (final MeasurementVariableDto measurementVariable : searchDto.getPassportAndAttributes()) {
				final String alias = this.formatVariableAlias(measurementVariable.getId());

				if (this.isColumnVisible(measurementVariable.getName(), searchDto.getVisibleColumns())) {
					columns.add(
						String.format("(SELECT aval FROM atributs WHERE gid = s.dbxref_id AND atype = %1$s) AS `%2$s`",
							measurementVariable.getId(),
							alias));
				} else {
					columns.add(
						String.format(" NULL AS `%1$s`", alias));
				}
			}
		}

		if (!CollectionUtils.isEmpty(searchDto.getNameTypes())) {
			for (final MeasurementVariableDto measurementVariable : searchDto.getNameTypes()) {
				final String alias = this.formatNameAlias(measurementVariable.getId());
				if (this.isColumnVisible(measurementVariable.getName(), searchDto.getVisibleColumns())) {
					columns.add(
						String.format("(SELECT nval FROM names WHERE gid = s.dbxref_id AND ntype = %1$s) AS `%2$s`",
							measurementVariable.getId(),
							alias));
				} else {
					columns.add(
						String.format(" NULL AS `%1$s`", alias));
				}
			}
		}

		final StringBuilder sql = new StringBuilder("SELECT * FROM (SELECT  ");

		sql.append(Joiner.on(", ").join(columns));

		this.addFromClause(sql, searchDto);

		this.addFilters(sql, searchDto.getFilter(), searchDto.getDraftMode());

		sql.append(" GROUP BY nde.nd_experiment_id ");

		this.addHavingClause(sql, searchDto.getFilter());

		if (forVisualization) {
			sql.append(") T ");
		} else {
			this.addOrder(sql, searchDto, observationUnitNoName, plotNoName, pageable);
		}

		return sql.toString();
	}

	private void addFromClause(final StringBuilder sql, final ObservationUnitsSearchDTO searchDto) {

		sql.append(" FROM ")
			.append(" project p ")
			.append(" INNER JOIN nd_experiment nde ON nde.project_id = p.project_id ")
			.append(" INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id ")
			.append(" LEFT JOIN stock s ON s.stock_id = nde.stock_id ")
			.append(" LEFT JOIN names name ON name.gid = s.dbxref_id and name.nstat = 1")
			.append(" LEFT JOIN stockprop sp ON sp.stock_id = s.stock_id ")
			.append(" LEFT JOIN cvterm cvterm_entry_variable ON (cvterm_entry_variable.cvterm_id = sp.type_id) ")
			.append(" LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id ")
			.append(" LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id ")
			.append(" LEFT JOIN nd_experiment parent ON parent.nd_experiment_id = nde.parent_id ")
			// Count samples for child dataset (sub-obs)
			.append(" LEFT JOIN (SELECT parent.nd_experiment_id, ")
			.append("       nullif(count(child_sample.sample_id), 0) AS count ")
			// Start the join with child to avoid parent_id full index scan
			.append("     FROM nd_experiment child ")
			.append("            LEFT JOIN sample child_sample ON child.nd_experiment_id = child_sample.nd_experiment_id ")
			.append("            INNER JOIN nd_experiment parent ON child.parent_id = parent.nd_experiment_id ")
			.append(
				"     GROUP BY parent.nd_experiment_id) child_sample_count ON child_sample_count.nd_experiment_id = nde.nd_experiment_id ")
			// FIXME won't work for sub-sub-obs
			.append(
				" INNER JOIN nd_experiment plot ON plot.nd_experiment_id = nde.parent_id OR ( plot.nd_experiment_id = nde.nd_experiment_id and nde.parent_id is null ) ");

		this.addSelectQueryJoins(sql, searchDto.getGenericGermplasmDescriptors(),
			searchDto.getFilter());

		sql.append(" WHERE p.project_id = :datasetId ");

		if (!CollectionUtils.isEmpty(searchDto.getInstanceIds())) {
			sql.append(" AND gl.nd_geolocation_id IN (:instanceIds)");
		}

		if (Boolean.TRUE.equals(searchDto.getDraftMode())) {
			sql.append(" AND (ph.draft_value is not null or ph.draft_cvalue_id is not null) ");
		}
	}

	private void addSelectQueryJoins(final StringBuilder sql, final List<String> genericGermplasmDescriptors,
		final ObservationUnitsSearchDTO.Filter filter) {
		final Set<String> joins = new LinkedHashSet<>();

		if (this.hasDescriptor(genericGermplasmDescriptors, TermId.GROUPGID) ||
			this.hasDescriptor(genericGermplasmDescriptors, TermId.GUID) ||
			this.hasDescriptor(genericGermplasmDescriptors, TermId.IMMEDIATE_SOURCE_NAME) ||
			this.hasDescriptor(genericGermplasmDescriptors, TermId.BREEDING_METHOD_ABBR) ||
			this.hasDescriptor(genericGermplasmDescriptors, TermId.GROUP_SOURCE_NAME) ||
			this.hasDescriptor(genericGermplasmDescriptors, TermId.DESIG)) {
			sql.append(GERMPLASM_JOIN);
		}

		if (this.hasDescriptor(genericGermplasmDescriptors, TermId.IMMEDIATE_SOURCE_NAME)) {
			sql.append(IMMEDIATE_SOURCE_NAME_JOIN);
		}

		if (this.hasDescriptor(genericGermplasmDescriptors, TermId.BREEDING_METHOD_ABBR)) {
			sql.append(BREEDING_METHODS_ABBR_JOIN);
		}

		if (this.hasDescriptor(genericGermplasmDescriptors, TermId.GROUP_SOURCE_NAME)) {
			sql.append(GROUP_SOURCE_NAME_JOIN);
		}

		if (this.checkFilterContainsFactor(filter, TermId.LOCATION_ID.getId())) {
			joins.add(LOCATION_JOIN);
		}

		joins.forEach(sql::append);
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

		if (filter.getFilteredNdExperimentIds() != null && !filter.getFilteredNdExperimentIds().isEmpty()) {
			sql.append(" and nde.nd_experiment_id in (:filteredNdExperimentIds) ");
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

		if (!CollectionUtils.isEmpty(filter.getPreFilteredGids())) {
			sql.append(" and s.dbxref_id in (:preFilteredGids) ");
		}

	}

	private void addOrder(final StringBuilder sql, final ObservationUnitsSearchDTO searchDto, final String observationUnitNoName,
		final String plotNoName, final Pageable pageable) {

		if (pageable != null && pageable.getSort() != null) {
			final String orderClause = StreamSupport.stream(pageable.getSort().spliterator(), false)
				.filter(order -> StringUtils.isNotBlank(order.getProperty()))
				.map(order -> {
					final String property = order.getProperty();
					final String sortDirection = order.getDirection().name();
					return "(1 * `" + property + "`) " + sortDirection + ", `" + property + "` " + sortDirection;
				})
				.collect(Collectors.joining(","));
			sql.append(" ) T ORDER BY ").append(orderClause);
			return;
		}

		String orderColumn;
		final String sortBy = "";
		final String direction = "asc";
		if (observationUnitNoName != null && StringUtils.isNotBlank(sortBy) && observationUnitNoName.equalsIgnoreCase(sortBy)
			&& !plotNoName.equals(observationUnitNoName)) {
			orderColumn = ObservationUnitsSearchDao.OBSERVATION_UNIT_NO;
		} else if (SUM_OF_SAMPLES_ID.equals(sortBy)) {
			orderColumn = ObservationUnitsSearchDao.SUM_OF_SAMPLES;
		} else if (String.valueOf(TermId.STOCK_ID.getId()).equals(sortBy)) {
			orderColumn = ObservationUnitsSearchDao.STOCK_ID;
		} else {
			orderColumn = StringUtils.isNotBlank(sortBy) ? sortBy : plotNoName;
		}

		if (Boolean.TRUE.equals(searchDto.getDraftMode())) {
			for (final MeasurementVariableDto selectionMethodsAndTrait : searchDto.getDatasetVariables()) {
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

	private void addHavingClause(final StringBuilder sql, final ObservationUnitsSearchDTO.Filter filter) {

		// For GERMPLASM NAME, PASSPORT AND ATTRIBUTES columns, we fetch first all the data before
		// applying the filter condition.

		if (filter == null) {
			return;
		}

		if (filter.getFilteredTextValues() != null && !filter.getFilteredTextValues().isEmpty()) {

			final List<String> havingConditions = new ArrayList<>();
			for (final String variableId : filter.getFilteredTextValues().keySet()) {

				if (variableId != null && REMOVE_FILTERS.contains(Integer.valueOf(variableId))) {
					continue;
				}

				if (String.valueOf(TermId.IMMEDIATE_SOURCE_NAME.getId()).equals(variableId) ||
					String.valueOf(TermId.GROUP_SOURCE_NAME.getId()).equals(variableId) ||
					String.valueOf(TermId.DESIG.getId()).equals(variableId)) {
					continue;
				}

				final String variableType = filter.getVariableTypeMap().get(variableId);
				if (null == variableType) {
					final String alias = this.formatNameAlias(variableId);
					havingConditions.add(String.format(" %s LIKE :%s_text ", alias, variableId));
					continue;
				}
				if (VariableType.GERMPLASM_PASSPORT.name().equals(variableType)
					|| VariableType.GERMPLASM_ATTRIBUTE.name()
					.equals(variableType)) {
					final String alias = this.formatVariableAlias(variableId);
					havingConditions.add(String.format(" %s LIKE :%s_text ", alias, variableId));
				}
			}

			if (!CollectionUtils.isEmpty(havingConditions)) {
				sql.append(" HAVING ");
				sql.append(havingConditions.stream().collect(Collectors.joining(" AND ")));
			}

		}

	}

	private void appendVariableIdAndOperationToFilterQuery(final StringBuilder sql, final ObservationUnitsSearchDTO.Filter filter,
		final String filterByDraftOrValue, final Set<String> variableIds, final boolean performLikeOperation) {
		final Integer variableId = filter.getVariableId();
		final List<String> variableTypes = Arrays.asList(VariableType.TRAIT.name(), VariableType.SELECTION_METHOD.name(),
			VariableType.ANALYSIS.name(), VariableType.ANALYSIS_SUMMARY.name());
		for (final String observableId : variableIds) {
			if (variableId != null && !variableId.equals(Integer.valueOf(observableId))) {
				continue;
			}

			if (observableId != null && REMOVE_FILTERS.contains(Integer.valueOf(observableId))) {
				continue;
			}

			final String variableTypeString = filter.getVariableTypeMap().get(observableId);
			if (variableTypes.contains(variableTypeString)) {
				this.appendTraitValueFilteringToQuery(sql, filterByDraftOrValue, observableId, performLikeOperation);

			} else {
				this.applyFactorsFilter(sql, observableId, variableTypeString, performLikeOperation);
			}
		}
	}

	private void appendOutOfBoundsTraitsFilteringToQuery(final StringBuilder sql, final String filterByDraftOrValue,
		final String filterByVariableSQL) {
		sql.append(" and nde.nd_experiment_id in (select ph2.nd_experiment_id ")
			.append("      from cvterm_relationship cvtrscale ")
			.append("           inner join cvterm scale on cvtrscale.object_id = scale.cvterm_id ")
			.append(
				"           inner join cvterm_relationship cvtrdataType on scale.cvterm_id = cvtrdataType.subject_id and cvtrdataType.type_id = ")
			.append(TermId.HAS_TYPE.getId())
			.append("           inner join cvterm dataType on cvtrdataType.object_id = dataType.cvterm_id ")
			.append(
				"           left join cvtermprop scaleMaxRange on scale.cvterm_id = scaleMaxRange.cvterm_id and scaleMaxRange.type_id = ")
			.append(TermId.MAX_VALUE.getId())
			.append(
				"           left join cvtermprop scaleMinRange on scale.cvterm_id = scaleMinRange.cvterm_id and scaleMinRange.type_id = ")
			.append(TermId.MIN_VALUE.getId())
			.append(" inner join phenotype ph2 on cvtrscale.subject_id = ph2.observable_id ")
			.append("    inner join nd_experiment nde2 on ph2.nd_experiment_id = nde2.nd_experiment_id ")
			.append("           inner join project p2 on nde2.project_id = p2.project_id ")
			.append("           left join variable_overrides vo on vo.cvterm_id = ph2.observable_id and p2.program_uuid = vo.program_uuid ")
			.append("      where ph2." + filterByDraftOrValue + " is not null  and ph2." + filterByDraftOrValue + "!= 'missing'")
			.append(filterByVariableSQL)
			.append("        and cvtrscale.type_id = " + TermId.HAS_SCALE.getId())
			.append("        and case ")
			.append("        when dataType.cvterm_id = " + TermId.CATEGORICAL_VARIABLE.getId())
			/* get the categoricals whose value != category value (out-of-bound)
			in other words, the set where ph.value = category value NOT exists*/
			.append("          then not exists( ")
			.append("          select 1 ")
			.append("            from cvterm_relationship cvtrcategory ")
			.append("                 inner join cvterm category on cvtrcategory.object_id = category.cvterm_id ")
			.append("            where scale.cvterm_id = cvtrcategory.subject_id ")
			.append("              and cvtrcategory.type_id = " + TermId.HAS_VALUE.getId())
			.append("              and ph2." + filterByDraftOrValue + " = category.name ")
			.append("          ) ")
			.append("        when dataType.cvterm_id = " + TermId.NUMERIC_VARIABLE.getId())
			// get the numericals whose value is not within bounds
			// cast strings to decimal (+ 0) to compare
			.append("          then ph2." + filterByDraftOrValue + " + 0 < scaleMinRange.value ")
			.append("            or ph2." + filterByDraftOrValue + " + 0 > scaleMaxRange.value ")
			.append("            or ph2." + filterByDraftOrValue + " + 0 < vo.expected_min ")
			.append("            or ph2." + filterByDraftOrValue + " + 0 > vo.expected_max ")
			.append("        else false ")
			.append("        end ")
			.append("    )");
	}

	private void appendTraitStatusFilterToQuery(final StringBuilder sql, final String filterByVariableSQL, final String filterClause) {
		sql.append(" and EXISTS ( ")
			.append("    SELECT 1 ")
			.append("    FROM phenotype ph2 ")
			.append("    WHERE ph2.nd_experiment_id = nde.nd_experiment_id ")
			.append(filterByVariableSQL)
			.append(filterClause + ") ");
	}

	private void appendTraitValueFilteringToQuery(final StringBuilder sql, final String filterByDraftOrValue, final String variableId,
		final boolean performLikeOperation) {
		final String matchClause = performLikeOperation ? " LIKE :" + variableId + "_text " : " IN (:" + variableId + "_values) ";
		sql.append(" and EXISTS ( ")
			.append("    SELECT 1 ")
			.append("    FROM phenotype ph2 ")
			.append("    WHERE ph2.observable_id = :" + variableId + "_Id")
			.append("    AND ph2.nd_experiment_id = nde.nd_experiment_id ")
			.append("    and ph2.").append(filterByDraftOrValue).append(matchClause).append(") ");
	}

	private void applyFactorsFilter(final StringBuilder sql, final String variableId, final String variableType,
		final boolean performLikeOperation) {
		// Check if the variable to be filtered is in one of the columns in stock, nd_experiment, geolocation or sum of samples
		final String observationUnitClause = VariableType.OBSERVATION_UNIT.name().equals(variableType) ? "nde.observation_unit_no" : null;
		final String filterClause = factorsFilterMap.get(variableId);
		// Sum of Samples, whose Id is -2, will cause an error as query parameter. Remove the "-" from the ID as workaround
		final String finalId = variableId.replace("-", "");
		final String matchClause = performLikeOperation ? " LIKE :" + finalId + "_text " : " IN (:" + finalId + "_values) ";
		if (filterClause != null || observationUnitClause != null) {
			sql.append(" AND ").append(observationUnitClause != null ? observationUnitClause : filterClause).append(matchClause);
			// If Sum of Samples, append extra closing parenthesis for the EXISTS clause it uses
			if (SUM_OF_SAMPLES_ID.equals(variableId) || String.valueOf(TermId.STOCK_ID.getId()).equals(variableId)) {
				sql.append(") ");
			}
			return;
		}

		// Otherwise, look in "props" tables
		// If doing text searching, perform LIKE operation. Otherwise perform value "IN" operation
		if (EXP_PROPS_VAR_TYPES.contains(variableType)) {
			sql.append(" AND EXISTS ( SELECT 1 FROM nd_experimentprop xp ")
				.append("WHERE xp.nd_experiment_id = plot.nd_experiment_id AND xp.type_id = :" + variableId)
				.append("_Id AND value ").append(matchClause).append(" )");

		} else if (VariableType.GERMPLASM_DESCRIPTOR.name().equals(variableType) || VariableType.ENTRY_DETAIL.name().equals(variableType)) {
			// IF searching by list of values, search for the values in:
			// 1)cvterm.name (for categorical variables) or
			// 2)perform IN operation on stockprop.value
			// Otherwise, search the value like a text by LIKE operation
			final String stockMatchClause = performLikeOperation ? "sp.value LIKE :" + variableId + "_text " :
				" (cvt.name IN (:" + variableId + "_values) OR sp.value IN (:" + variableId + "_values ))";
			sql.append(" AND EXISTS ( SELECT 1 FROM stockprop sp ")
				.append("LEFT JOIN cvterm cvt ON cvt.cvterm_id = sp.value ")
				.append("WHERE sp.stock_id = s.stock_id AND sp.type_id = :" + variableId)
				.append("_Id AND ").append(stockMatchClause).append(" )");
		}
	}

	private static void addQueryParams(final SQLQuery query, final ObservationUnitsSearchDTO.Filter filter) {
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

				if (observableId != null && REMOVE_FILTERS.contains(Integer.valueOf(observableId))) {
					continue;
				}

				final String variableType = filter.getVariableTypeMap().get(observableId);

				if (null == variableType) {
					final String finalId = observableId.replace("-", "");
					query.setParameter(finalId + "_text", "%" + filteredTextValues.get(observableId) + "%");
					continue;
				}

				if (!VariableType.OBSERVATION_UNIT.name().equals(variableType) &&
					!VariableType.GERMPLASM_ATTRIBUTE.name().equals(variableType) &&
					!VariableType.GERMPLASM_PASSPORT.name().equals(variableType) &&
					factorsFilterMap.get(observableId) == null) {
					query.setParameter(observableId + "_Id", observableId);
				}

				if (String.valueOf(TermId.STOCK_ID.getId()).equals(observableId)) {
					// Stock_Id, whose Id is -1727, will cause an error as query parameter. Remove the "-" from the ID as workaround
					final String finalId = observableId.replace("-", "");
					query.setParameter(finalId + "_text", "%" + filteredTextValues.get(observableId) + "%");
					continue;
				}
				query.setParameter(observableId + "_text", "%" + filteredTextValues.get(observableId) + "%");
			}
		}

		if (filter.getFilteredNdExperimentIds() != null && !filter.getFilteredNdExperimentIds().isEmpty()) {
			query.setParameterList("filteredNdExperimentIds", filter.getFilteredNdExperimentIds());
		}

		if (!CollectionUtils.isEmpty(filter.getPreFilteredGids())) {
			query.setParameterList("preFilteredGids", filter.getPreFilteredGids());
		}
	}

	private String addScalarForSpecificTrait(final ObservationUnitsSearchDTO params, final SQLQuery query) {
		for (final MeasurementVariableDto measurementVariable : params.getDatasetVariables()) {
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

				for (final MeasurementVariableDto variable : searchDto.getDatasetVariables()) {

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

	private void addScalarForEntryDetails(final List<MeasurementVariableDto> entryDetails, final SQLQuery createSQLQuery) {
		for (final MeasurementVariableDto measurementVariable : entryDetails) {
			createSQLQuery.addScalar(measurementVariable.getName()); // Value
			createSQLQuery.addScalar(measurementVariable.getName() + "_StockPropId", new IntegerType());
			createSQLQuery.addScalar(measurementVariable.getName() + "_CvalueId", new IntegerType());
		}
	}

	private List<ObservationUnitRow> convertToObservationUnitRows(final List<Map<String, Object>> results,
		final ObservationUnitsSearchDTO searchDto,
		final String observationVariableName, final Map<Integer, String> standardVariableNameMap) {
		final List<ObservationUnitRow> observationUnitRows = new ArrayList<>();

		if (results != null && !results.isEmpty()) {
			for (final Map<String, Object> row : results) {
				final ObservationUnitRow observationUnitRow =
					this.getObservationUnitRow(searchDto, observationVariableName, row, standardVariableNameMap);
				observationUnitRows.add(observationUnitRow);
			}
		}

		return observationUnitRows;
	}

	private ObservationUnitRow getObservationUnitRow(final ObservationUnitsSearchDTO searchDto, final String observationVariableName,
		final Map<String, Object> row, final Map<Integer, String> standardVariableNameMap) {
		final Map<String, ObservationUnitData> environmentVariables = new HashMap<>();
		final Map<String, ObservationUnitData> observationVariables = new HashMap<>();

		for (final MeasurementVariableDto variable : searchDto.getDatasetVariables()) {
			final String status = (String) row.get(variable.getName() + "_Status");
			final ObservationUnitData observationUnitData = new ObservationUnitData(
				(Integer) row.get(variable.getName() + "_PhenotypeId"),
				(Integer) row.get(variable.getName() + "_CvalueId"),
				(String) row.get(variable.getName()), // Value
				(status != null ? Phenotype.ValueStatus.valueOf(status) : null),
				variable.getId());
			observationUnitData.setDraftValue((String) row.get(variable.getName() + "_DraftValue"));
			observationUnitData.setDraftCategoricalValueId((Integer) row.get(variable.getName() + "_DraftCvalueId"));

			observationVariables.put(variable.getName(), observationUnitData);
		}

		for (final MeasurementVariableDto variable : searchDto.getEntryDetails()) {
			observationVariables.put(variable.getName(), new ObservationUnitData(
				(Integer) row.get(variable.getName() + "_StockPropId"),
				(Integer) row.get(variable.getName() + "_CvalueId"),
				(String) row.get(variable.getName()),
				null,
				variable.getId()
			));
		}

		final ObservationUnitRow observationUnitRow = new ObservationUnitRow();
		observationUnitRow.setInstanceId((Integer) row.get(INSTANCE_ID));
		observationUnitRow.setObservationUnitId((Integer) row.get(OBSERVATION_UNIT_ID));
		observationUnitRow.setAction(((Integer) row.get(OBSERVATION_UNIT_ID)).toString());
		observationUnitRow.setObsUnitId((String) row.get(standardVariableNameMap.get(TermId.OBS_UNIT_ID.getId())));
		observationUnitRow.setSamplesCount((String) row.get(SUM_OF_SAMPLES));
		observationUnitRow.setFileCount((Integer) row.get(FILE_COUNT));
		final Object fileTermIds = row.get(FILE_TERM_IDS);
		observationUnitRow.setFileVariableIds(fileTermIds != null ? ((String) fileTermIds).split(",") : new String[] {});

		final String gidColumnName = standardVariableNameMap.get(TermId.GID.getId());
		final Integer gid = (Integer) row.get(gidColumnName);
		if (gid != null) {
			observationUnitRow.setGid(gid);
			observationVariables.put(gidColumnName, new ObservationUnitData(TermId.GID.getId(), gid.toString()));
		}

		final String designationColumnName = standardVariableNameMap.get(TermId.DESIG.getId());
		final String designation = (String) row.get(designationColumnName);
		if (designation != null) {
			observationUnitRow.setDesignation(designation);
			observationVariables.put(designationColumnName, new ObservationUnitData(TermId.DESIG.getId(), designation));
		}

		if (row.containsKey(STOCK_ID)) {
			final String stockId = (String) row.get(STOCK_ID);
			observationUnitRow.setStockId(stockId);
			observationVariables.put(STOCK_ID, new ObservationUnitData(TermId.STOCK_ID.getId(), stockId));
		}

		final String trialInstanceColumnName = standardVariableNameMap.get(TermId.TRIAL_INSTANCE_FACTOR.getId());
		final String trialInstance = (String) row.get(trialInstanceColumnName);
		if (NumberUtils.isDigits(trialInstance)) {
			observationUnitRow.setTrialInstance(Integer.valueOf(trialInstance));
		}
		observationVariables.put(trialInstanceColumnName, new ObservationUnitData(TermId.TRIAL_INSTANCE_FACTOR.getId(), trialInstance));

		final String entryNoColumnName = standardVariableNameMap.get(TermId.ENTRY_NO.getId());
		final String entryNumber = (String) row.get(entryNoColumnName);
		if (NumberUtils.isDigits(entryNumber)) {
			observationUnitRow.setEntryNumber(Integer.valueOf(entryNumber));
		}
		observationVariables.put(entryNoColumnName, new ObservationUnitData(TermId.ENTRY_NO.getId(), entryNumber));

		standardVariableNameMap.entrySet().forEach(entry -> {
			final String column = entry.getValue();
			final Object value = row.get(column);
			observationVariables.put(column, new ObservationUnitData(entry.getKey(), value != null ? String.valueOf(value) : null));
		});
		observationVariables.put(PARENT_OBS_UNIT_ID, new ObservationUnitData((String) row.get(PARENT_OBS_UNIT_ID)));
		// Avoid overriding PLOT_NO variable if it was already set using standardVariableNameMap iteration
		observationVariables.putIfAbsent(observationVariableName, new ObservationUnitData((String) row.get(OBSERVATION_UNIT_NO)));

		for (final String gpDesc : searchDto.getGenericGermplasmDescriptors()) {
			observationVariables.put(gpDesc, new ObservationUnitData((String) row.get(gpDesc)));
		}
		for (final String designFactor : searchDto.getAdditionalDesignFactors()) {
			observationVariables.put(designFactor, new ObservationUnitData((String) row.get(designFactor)));
		}

		if (!CollectionUtils.isEmpty(searchDto.getPassportAndAttributes())) {
			searchDto.getPassportAndAttributes().forEach(variable ->
				observationVariables.put(variable.getName(),
					new ObservationUnitData(variable.getId(), (String) row.get(this.formatVariableAlias(variable.getId())))));
		}

		if (!CollectionUtils.isEmpty(searchDto.getNameTypes())) {
			searchDto.getNameTypes().forEach(variable ->
				observationVariables.put(variable.getName(),
					new ObservationUnitData((String) row.get(this.formatNameAlias(variable.getId())))));
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
		final String observationVariableName, final Pageable pageable) {
		try {
			final Map<String, String> finalColumnsQueryMap = new HashMap<>();
			final Map<Integer, String> standardDatasetVariablesMap = this.getStandardDatasetVariablesMap(finalColumnsQueryMap);
			final List<Map<String, Object>> resultList =
				this.getObservationUnitsQueryResult(searchDto, observationVariableName, standardDatasetVariablesMap, finalColumnsQueryMap,
					pageable, true);
			return this.convertSelectionAndTraitColumnsValueType(resultList, searchDto.getDatasetVariables());

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

	private boolean hasDescriptor(final List<String> genericGermplasmDescriptors, final TermId termId) {
		return !CollectionUtils.isEmpty(genericGermplasmDescriptors) && genericGermplasmDescriptors.contains(termId.name());
	}

	private boolean checkFilterContainsFactor(final ObservationUnitsSearchDTO.Filter filter, final int variableId) {
		return !CollectionUtils.isEmpty(filter.getFilteredTextValues()) && filter.getFilteredTextValues().keySet()
			.contains(String.valueOf(variableId));
	}

	private String formatVariableAlias(final Object variableId) {
		return String.format("VARIABLE_%s", variableId);
	}

	private String formatNameAlias(final Object variableId) {
		return String.format("NAME_%s", variableId);
	}

	public Set<Integer> addPreFilteredGids(final ObservationUnitsSearchDTO.Filter filter) {
		final Set<Integer> preFilteredGids = new HashSet<>();
		String femaleName = null;
		String maleName = null;
		String femaleGid = null;
		String maleGid = null;

		if (filter.getFilteredTextValues().containsKey(String.valueOf(TermId.FEMALE_PARENT_NAME.getId()))) {
			femaleName = filter.getFilteredTextValues().get(String.valueOf(TermId.FEMALE_PARENT_NAME.getId()));
		}

		if (filter.getFilteredTextValues().containsKey(String.valueOf(TermId.FEMALE_PARENT_GID.getId()))) {
			final String filterValue = filter.getFilteredTextValues().get(String.valueOf(TermId.FEMALE_PARENT_GID.getId()));
			femaleGid = filterValue.equalsIgnoreCase("UNKNOWN") ? "0" : filterValue;
		}

		if (filter.getFilteredTextValues().containsKey(String.valueOf(TermId.MALE_PARENT_NAME.getId()))) {
			maleName = filter.getFilteredTextValues().get(String.valueOf(TermId.MALE_PARENT_NAME.getId()));
		}

		if (filter.getFilteredTextValues().containsKey(String.valueOf(TermId.MALE_PARENT_GID.getId()))) {
			final String filterValue = filter.getFilteredTextValues().get(String.valueOf(TermId.MALE_PARENT_GID.getId()));
			maleGid = filterValue.equalsIgnoreCase("UNKNOWN") ? "0" : filterValue;
		}

		if (StringUtils.isNotBlank(femaleName) || StringUtils.isNotBlank(femaleGid)) {
			final StringBuilder sql = new StringBuilder("select g.gid from names n \n");//
			sql.append("   straight_join germplsm female_parent on n.gid = female_parent.gid \n");//
			sql.append("   straight_join germplsm group_source on female_parent.gid = group_source.gpid1 and group_source.gnpgs > 0 \n");//
			sql.append("   straight_join germplsm g on g.gnpgs < 0 and group_source.gid = g.gpid1 \n");  //
			sql.append("                            or g.gnpgs > 0 and group_source.gid = g.gid \n"); //
			sql.append(" where n.nstat = 1 \n");
			if (StringUtils.isNotBlank(femaleName)) {
				sql.append(" and n.nval = :femaleName "); //
			}
			if (StringUtils.isNotBlank(femaleGid)) {
				sql.append(" and female_parent.gid = :femaleGid "); //
			}
			sql.append(LIMIT_CLAUSE); //
			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());

			if (StringUtils.isNotBlank(femaleName)) {
				query.setParameter("femaleName", femaleName); //
			}
			if (StringUtils.isNotBlank(femaleGid)) {
				query.setParameter("femaleGid", femaleGid); //
			}

			final List<Integer> gids = query.list();
			if (!gids.isEmpty()) {
				preFilteredGids.addAll(gids);
			}
		}

		if (StringUtils.isNotBlank(maleName) || StringUtils.isNotBlank(maleGid)) {
			final StringBuilder sql = new StringBuilder("select g.gid from names n \n");//
			sql.append("   straight_join germplsm male_parent on n.gid = male_parent.gid \n");//
			sql.append("   straight_join germplsm group_source on male_parent.gid = group_source.gpid2 and group_source.gnpgs > 0 \n");//
			sql.append("   straight_join germplsm g on g.gnpgs < 0 and group_source.gid = g.gpid1 \n");  //
			sql.append("                            or g.gnpgs > 0 and group_source.gid = g.gid \n"); //
			sql.append(" where n.nstat = 1 \n");

			if (StringUtils.isNotBlank(maleName)) {
				sql.append(" and n.nval = :maleName "); //
			}
			if (StringUtils.isNotBlank(maleGid)) {
				sql.append(" and male_parent.gid = :maleGid "); //
			}
			sql.append(LIMIT_CLAUSE); //

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());

			if (StringUtils.isNotBlank(maleName)) {
				query.setParameter("maleName", maleName); //
			}
			if (StringUtils.isNotBlank(maleGid)) {
				query.setParameter("maleGid", maleGid); //
			}

			final List<Integer> gids = query.list();
			if (!gids.isEmpty()) {
				preFilteredGids.addAll(gids);
			}
		}
		return preFilteredGids;
	}
}
