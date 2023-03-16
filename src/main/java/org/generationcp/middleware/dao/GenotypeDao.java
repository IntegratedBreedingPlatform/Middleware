package org.generationcp.middleware.dao;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.genotype.SampleGenotypeDTO;
import org.generationcp.middleware.domain.genotype.SampleGenotypeData;
import org.generationcp.middleware.domain.genotype.SampleGenotypeSearchRequestDTO;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.Genotype;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GenotypeDao extends GenericDAO<Genotype, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(GenotypeDao.class);

	private static final List<Integer> STANDARD_SAMPLE_GENOTYPE_TABLE_VARIABLE_IDS =
		Arrays.asList(TermId.TRIAL_INSTANCE_FACTOR.getId(), TermId.GID.getId(), TermId.DESIG.getId(),
			TermId.ENTRY_TYPE.getId(), TermId.ENTRY_NO.getId(), TermId.REP_NO.getId(), TermId.PLOT_NO.getId(),
			TermId.OBS_UNIT_ID.getId());

	private static final String OBSERVATION_UNIT_ID = "observationUnitId";
	private static final String DATASET_ID = "datasetId";

	private static final String GENOTYPE_SEARCH_FROM_QUERY = "FROM sample s " +
		"LEFT JOIN nd_experiment nde ON nde.nd_experiment_id = s.nd_experiment_id " +
		"LEFT JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id " +
		"LEFT JOIN project p ON p.project_id = nde.project_id " +
		"INNER JOIN genotype geno ON s.sample_id = geno.sample_id " +
		"LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = geno.variabe_id " +
		"LEFT JOIN stock st ON st.stock_id = nde.stock_id " +
		"LEFT JOIN stockprop sp ON sp.stock_id = st.stock_id " +
		"LEFT JOIN cvterm cvterm_entry_variable ON (cvterm_entry_variable.cvterm_id = sp.type_id) " +
		"LEFT JOIN germplsm g ON g.gid = st.dbxref_id " +
		"LEFT JOIN names n ON g.gid = n.gid AND n.nstat = 1 " +
		"LEFT JOIN nd_experimentprop plot_no ON plot_no.nd_experiment_id = nde.nd_experiment_id AND plot_no.type_id = " +
		TermId.PLOT_NO.getId() + " " +
		"WHERE p.study_id = :studyId ";

	private static final Map<String, String> mainVariablesMap = new HashMap<>();

	static {
		// NOTE: Column names will be replaced by queried standard variable names (not hardcoded)
		mainVariablesMap.put(DATASET_ID, "    nde.nd_experiment_id as `datasetId`");
		mainVariablesMap.put(OBSERVATION_UNIT_ID, "    nde.nd_experiment_id as `observationUnitId`");
		mainVariablesMap.put(String.valueOf(TermId.TRIAL_INSTANCE_FACTOR.getId()), "    gl.description AS `%s`");
		mainVariablesMap.put(String.valueOf(TermId.GID.getId()), "    st.dbxref_id AS `%s`");
		mainVariablesMap.put(String.valueOf(TermId.DESIG.getId()), "    n.nval AS `%s`");
		mainVariablesMap.put(String.valueOf(TermId.ENTRY_NO.getId()), " st.uniquename AS `%s`");
		mainVariablesMap.put(String.valueOf(TermId.ENTRY_TYPE.getId()),
			" MAX(IF(cvterm_entry_variable.name = '%1$s', sp.value, NULL)) AS `%1$s`");
		mainVariablesMap.put(String.valueOf(TermId.REP_NO.getId()),
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ndep.type_id = 8210) AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.PLOT_NO.getId()),
			"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ndep.type_id = 8200) AS '%s'");
		mainVariablesMap.put(String.valueOf(TermId.OBS_UNIT_ID.getId()), "    nde.obs_unit_id AS '%s'");
	}

	public GenotypeDao(final Session session) {
		super(session);
	}

	public List<SampleGenotypeDTO> searchGenotypes(final SampleGenotypeSearchRequestDTO searchRequestDTO, final Pageable pageable) {
		final Map<String, String> finalColumnsQueryMap = new HashMap<>();
		final Map<Integer, String> standardSampleGenotypeVariables = this.getStandardSampleGenotypeVariables(finalColumnsQueryMap);
		final List<String> columns = new ArrayList<>();
		finalColumnsQueryMap.forEach((columnName, columnQueryString) -> {
			if (this.isColumnVisible(columnName, searchRequestDTO.getVisibleColumns())) {
				columns.add(finalColumnsQueryMap.get(columnName));
			} else {
				columns.add(String.format("NULL as `%s`", columnName));
			}
		});

		if (!CollectionUtils.isEmpty(searchRequestDTO.getSampleGenotypeVariables())) {

			final StringBuilder sampleGenotypeVariableClauseFormat =
				new StringBuilder(" MAX(IF(cvterm_variable.name = '%1$s', geno.value, NULL)) AS `%1$s`,")
					.append(" MAX(IF(cvterm_variable.name = '%1$s', geno.variabe_id, NULL)) AS `%1$s_variableId`,")
					.append(" MAX(IF(cvterm_variable.name = '%1$s', cvterm_variable.name, NULL)) AS `%1$s_variableName` ");

			final StringBuilder sampleGenotypeVariableClauseFormatNullValues = new StringBuilder(" NULL AS `%1$s`,")
				.append(" NULL AS `%1$s_variableId`,")
				.append(" NULL AS `%1$s_variableName` ");

			searchRequestDTO.getSampleGenotypeVariables().forEach(measurementVariable -> {
				if (this.isColumnVisible(measurementVariable.getName(), searchRequestDTO.getVisibleColumns())) {
					columns.add(String.format(sampleGenotypeVariableClauseFormat.toString(), measurementVariable.getName()));
				} else {
					columns.add(String.format(sampleGenotypeVariableClauseFormatNullValues.toString(), measurementVariable.getName()));
				}

			});
		}

		final StringBuilder sql = new StringBuilder("SELECT * FROM (SELECT ");
		sql.append(Joiner.on(", ").join(columns));
		sql.append(GENOTYPE_SEARCH_FROM_QUERY);
		sql.append(" GROUP BY s.sample_id ");
		this.addOrder(sql, searchRequestDTO, standardSampleGenotypeVariables.get(TermId.PLOT_NO.getId()), pageable);

		final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
		this.addScalar(query, standardSampleGenotypeVariables);
		if (!CollectionUtils.isEmpty(searchRequestDTO.getSampleGenotypeVariables())) {
			for (final MeasurementVariable sampleGenotypeVariable : searchRequestDTO.getSampleGenotypeVariables()) {
				final String variableName = sampleGenotypeVariable.getName();
				query.addScalar(variableName); // Value
				query.addScalar(variableName + "_variableId", new IntegerType()); // Variable Id
				query.addScalar(variableName + "_variableName", new StringType()); // Variable Name
			}
		}

		query.setParameter("studyId", searchRequestDTO.getStudyId());
		addPaginationToSQLQuery(query, pageable);
		query.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		LOG.error(query.getQueryString());
		return this.mapGenotypeResults(query.list(), searchRequestDTO, standardSampleGenotypeVariables);
	}

	private List<SampleGenotypeDTO> mapGenotypeResults(final List<Map<String, Object>> results,
		final SampleGenotypeSearchRequestDTO searchRequestDTO,
		final Map<Integer, String> standardSampleGenotypeVariables) {
		final List<SampleGenotypeDTO> sampleGenotypeDTOList = new ArrayList<>();
		if (results != null && !results.isEmpty()) {
			for (final Map<String, Object> row : results) {
				final SampleGenotypeDTO sampleGenotypeDTO = new SampleGenotypeDTO();
				sampleGenotypeDTO.setObservationUnitId((Integer) row.get(OBSERVATION_UNIT_ID));
				sampleGenotypeDTO.setGenotypeDataMap(new HashMap<>());

				final String gidColumnName = standardSampleGenotypeVariables.get(TermId.GID.getId());
				final Integer gid = (Integer) row.get(gidColumnName);
				if (gid != null) {
					sampleGenotypeDTO.setGid(gid);
					sampleGenotypeDTO.getGenotypeDataMap()
						.put(gidColumnName, new SampleGenotypeData(TermId.GID.getId(), gidColumnName, gid.toString()));
				}

				final String designationColumnName = standardSampleGenotypeVariables.get(TermId.DESIG.getId());
				final String designation = (String) row.get(designationColumnName);
				if (designation != null) {
					sampleGenotypeDTO.setDesignation(designation);
					sampleGenotypeDTO.getGenotypeDataMap()
						.put(designationColumnName, new SampleGenotypeData(TermId.DESIG.getId(), designationColumnName, designation));
				}

				final String entryNoColumnName = standardSampleGenotypeVariables.get(TermId.ENTRY_NO.getId());
				final String entryNo = (String) row.get(entryNoColumnName);
				if (entryNo != null) {
					sampleGenotypeDTO.getGenotypeDataMap()
						.put(entryNoColumnName, new SampleGenotypeData(TermId.ENTRY_NO.getId(), entryNoColumnName, entryNo));
				}

				final String entryTypeColumnName = standardSampleGenotypeVariables.get(TermId.ENTRY_TYPE.getId());
				final String entryType = (String) row.get(entryTypeColumnName);
				if (entryType != null) {
					sampleGenotypeDTO.getGenotypeDataMap()
						.put(entryTypeColumnName, new SampleGenotypeData(TermId.ENTRY_TYPE.getId(), entryTypeColumnName, entryType));
				}

				final String trialInstanceColumnName = standardSampleGenotypeVariables.get(TermId.TRIAL_INSTANCE_FACTOR.getId());
				final String trialInstance = (String) row.get(trialInstanceColumnName);
				if (NumberUtils.isDigits(trialInstance)) {
					sampleGenotypeDTO.setDesignation(designation);
					sampleGenotypeDTO.getGenotypeDataMap().put(trialInstanceColumnName,
						new SampleGenotypeData(TermId.TRIAL_INSTANCE_FACTOR.getId(), trialInstanceColumnName, trialInstance));
				}

				final String repNoColumnName = standardSampleGenotypeVariables.get(TermId.REP_NO.getId());
				final String repNo = (String) row.get(repNoColumnName);
				if (repNo != null) {
					sampleGenotypeDTO.getGenotypeDataMap()
						.put(repNoColumnName, new SampleGenotypeData(TermId.REP_NO.getId(), repNoColumnName, repNo));
				}

				final String plotNoColumnName = standardSampleGenotypeVariables.get(TermId.PLOT_NO.getId());
				final String plotNo = (String) row.get(plotNoColumnName);
				if (plotNoColumnName != null) {
					sampleGenotypeDTO.getGenotypeDataMap()
						.put(plotNoColumnName, new SampleGenotypeData(TermId.PLOT_NO.getId(), plotNoColumnName, plotNo));
				}

				final String obsUnitIdColumnName = standardSampleGenotypeVariables.get(TermId.OBS_UNIT_ID.getId());
				final String obsUnitId = (String) row.get(obsUnitIdColumnName);
				if (obsUnitId != null) {
					final SampleGenotypeData
						sampleGenotypeData = new SampleGenotypeData(TermId.OBS_UNIT_ID.getId(), obsUnitIdColumnName, obsUnitId);
					sampleGenotypeData.setDatasetId((Integer) row.get(DATASET_ID));
					sampleGenotypeDTO.getGenotypeDataMap().put(obsUnitIdColumnName, sampleGenotypeData);
				}

				if (!CollectionUtils.isEmpty(searchRequestDTO.getSampleGenotypeVariables())) {
					for (final MeasurementVariable sampleGenotypeVariable : searchRequestDTO.getSampleGenotypeVariables()) {
						final String varName = sampleGenotypeVariable.getName();
						final SampleGenotypeData data = new SampleGenotypeData();
						data.setValue((String) row.get(varName));
						data.setVariableId((Integer) row.get(varName + "_variableId"));
						data.setVariableName((String) row.get(varName + "_variableName"));
						sampleGenotypeDTO.getGenotypeDataMap().put(varName, data);
					}
				}
				sampleGenotypeDTOList.add(sampleGenotypeDTO);
			}
		}
		return sampleGenotypeDTOList;

	}

	private static void addSearchQueryFilters(
		final SqlQueryParamBuilder paramBuilder,
		final SampleGenotypeSearchRequestDTO.GenotypeFilter filter) {

		if (filter != null) {
			final Integer datasetId = filter.getDatasetId();
			if (datasetId != null) {
				paramBuilder.append(" and p.project_id = :datasetId");
				paramBuilder.setParameter("datasetId", datasetId);
			}
			final List<Integer> instanceIds = filter.getInstanceIds();
			if (!CollectionUtils.isEmpty(instanceIds)) {
				paramBuilder.append(" and nde.nd_geolocation_id IN (:instanceIds)");
				paramBuilder.setParameterList("instanceIds", instanceIds);
			}
			final List<Integer> sampleIds = filter.getSampleIds();
			if (!CollectionUtils.isEmpty(sampleIds)) {
				paramBuilder.append(" and s.sample_id in (:sampleIds)");
				paramBuilder.setParameterList("sampleIds", sampleIds);
			}
		}
	}

	public long countFilteredGenotypes(final SampleGenotypeSearchRequestDTO sampleGenotypeSearchRequestDTO) {
		final StringBuilder sql = new StringBuilder("SELECT COUNT(1) ");
		sql.append(GENOTYPE_SEARCH_FROM_QUERY);
		addSearchQueryFilters(new SqlQueryParamBuilder(sql), sampleGenotypeSearchRequestDTO.getFilter());

		final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
		addSearchQueryFilters(new SqlQueryParamBuilder(query), sampleGenotypeSearchRequestDTO.getFilter());

		query.setParameter("studyId", sampleGenotypeSearchRequestDTO.getStudyId());
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public long countGenotypes(final SampleGenotypeSearchRequestDTO sampleGenotypeSearchRequestDTO) {
		final StringBuilder subQuery = new StringBuilder("SELECT s.sample_id ");
		subQuery.append(GENOTYPE_SEARCH_FROM_QUERY);
		final StringBuilder mainSql = new StringBuilder("SELECT COUNT(*) FROM ( \n");
		mainSql.append(subQuery);
		mainSql.append(") a \n");

		final SQLQuery query = this.getSession().createSQLQuery(mainSql.toString());

		query.setParameter("studyId", sampleGenotypeSearchRequestDTO.getStudyId());
		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public Map<Integer, String> getStandardSampleGenotypeVariables(final Map<String, String> finalColumnsQueryMap) {
		finalColumnsQueryMap.putAll(mainVariablesMap);
		// Set the actual standard variable names from ontology as column names in query
		final Map<Integer, String> standardDatasetVariablesMap = this.queryStandardSampleGenotypeVariables();
		standardDatasetVariablesMap.entrySet().forEach(entry -> {
			final String idKey = String.valueOf(entry.getKey());
			if (finalColumnsQueryMap.containsKey(idKey)) {
				finalColumnsQueryMap.put(entry.getValue(), String.format(mainVariablesMap.get(idKey), entry.getValue()));
				finalColumnsQueryMap.remove(idKey);
			}
		});
		return standardDatasetVariablesMap;
	}

	public Map<Integer, String> queryStandardSampleGenotypeVariables() {
		final SQLQuery query = this.getSession().createSQLQuery("SELECT cvterm_id, name from cvterm where cvterm_id in (:cvtermIds)");
		query.addScalar("cvterm_id", new IntegerType());
		query.addScalar("name", new StringType());
		query.setParameterList("cvtermIds", GenotypeDao.STANDARD_SAMPLE_GENOTYPE_TABLE_VARIABLE_IDS);
		final List<Object[]> result = query.list();
		final Map<Integer, String> variableMap = new HashMap<>();
		for (final Object[] variableRow : result) {
			variableMap.put((Integer) variableRow[0], (String) variableRow[1]);
		}
		return variableMap;
	}

	protected boolean isColumnVisible(final String columnName, final Set<String> visibleColumns) {

		// If the visible columns list is not empty, we should only include the columns specified.
		if (!CollectionUtils.isEmpty(visibleColumns)) {
			return visibleColumns.stream().anyMatch(s -> s.equalsIgnoreCase(columnName));
		}
		// If the visible columns list is not specified, process and retrieve the column by default.
		return true;
	}

	private void addOrder(final StringBuilder sql, final SampleGenotypeSearchRequestDTO searchDto, final String plotNoName,
		final Pageable pageable) {

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
		} else {
			final String direction = "asc";
			sql.append(" ) T ORDER BY " + "(1 * `" + plotNoName + "`) " + direction
				+ ", `" + plotNoName + "` " + direction);
		}
	}

	private void addScalar(final SQLQuery createSQLQuery, final Map<Integer, String> standardVariableNames) {
		createSQLQuery.addScalar(GenotypeDao.OBSERVATION_UNIT_ID);
		createSQLQuery.addScalar(standardVariableNames.get(TermId.TRIAL_INSTANCE_FACTOR.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.GID.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.DESIG.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.ENTRY_NO.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.ENTRY_TYPE.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.REP_NO.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.PLOT_NO.getId()));
		createSQLQuery.addScalar(standardVariableNames.get(TermId.OBS_UNIT_ID.getId()), new StringType());
	}

	public void deleteSampleGenotypes(final List<Integer> sampleIds) {
		Preconditions.checkArgument(CollectionUtils.isNotEmpty(sampleIds),
			"sampleIds passed cannot be empty.");

		try {
			final String query = "DELETE g FROM genotype g  WHERE g.sample_id IN (:sampleIds) ";
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(query);
			sqlQuery.setParameterList("sampleIds", sampleIds);
			sqlQuery.executeUpdate();
		} catch (final HibernateException e) {
			final String message = "Error with deleteSampleGenotypes(sampleIds=" + sampleIds + "): " + e.getMessage();
			GenotypeDao.LOG.error(message);
			throw new MiddlewareQueryException(message, e);
		}
	}
}
