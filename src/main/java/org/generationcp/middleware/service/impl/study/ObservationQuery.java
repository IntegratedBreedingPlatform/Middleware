
package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;

import javax.annotation.Nullable;
import java.util.List;

class ObservationQuery {

	static final String DEFAULT_SORT_COLUMN = "PLOT_NO";
	static final String DEFAULT_SORT_ORDER = "asc";
	private static final String PHENOTYPE_ID = "_PhenotypeId";
	public static final String STATUS = "_Status";
	private static final String INSTANCE_NUMBER_CLAUSE = " AND gl.nd_geolocation_id = :instanceId  ";
	private static final String GROUPING_CLAUSE = " GROUP BY nde.nd_experiment_id ";
	private static final String OBSERVATIONS_FOR_SAMPLES = "SELECT  " + "    nde.nd_experiment_id as nd_experiment_id, "
		+ "    (select na.nval from names na where na.gid = s.dbxref_id and na.nstat = 1 limit 1) as preferred_name, " + "    ph.value"
		+ " as value, s.dbxref_id as gid"
		+ " FROM  " + "    project p  "
		+ "        INNER JOIN nd_experiment nde ON nde.project_id = p.project_id  "
		+ "        INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id  "
		+ "        INNER JOIN stock s ON s.stock_id = nde.stock_id  "
		+ "        LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id  "
		+ "        LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id  " + " WHERE  "
		+ " p.project_id = :datasetId "
		+ " AND gl.description IN (:instanceIds)  "
		+ " and cvterm_variable.cvterm_id = :selectionVariableId " + " GROUP BY nde.nd_experiment_id";

	String getAllObservationsQuery(final List<MeasurementVariableDto> selectionMethodsAndTraits, final List<String> germplasmDescriptors,
		final List<String> designFactors, final String sortBy, final String sortOrder) {
		//FIXME remove inner join with max phenotype_id when BMS-5055 is solved
		return this.getObservationsMainQuery(selectionMethodsAndTraits, germplasmDescriptors, designFactors) + this
			.getInstanceNumberClause() + this
			.getGroupingClause()
			+ this.getOrderingClause(sortBy, sortOrder);
	}

	/**
	 * TODO BMS-4061 Merge with {@link ObservationQuery#getObservationsMainQuery(List, List, List)}
	 * <p>
	 * This query is used by BMSAPI and is very similar to {@link ObservationQuery#getObservationsMainQuery(List, List, List)}
	 * which is used Trial and Nursery Manager
	 */
	String getObservationQueryWithBlockRowCol(final List<MeasurementVariableDto> measurementVariables, final Integer instanceId) {
		final String orderByMeasurementVariableId = getOrderByMeasurementVariableId(measurementVariables);
		final String orderByText =
			(null == measurementVariables || measurementVariables.isEmpty() ? "" : " ORDER BY " + orderByMeasurementVariableId);

		String whereText = " WHERE p.study_id = :projectId AND p.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId() + " \n";

		if (instanceId != null) {
			whereText += " AND gl.nd_geolocation_id = :instanceId ";
		}

		return " SELECT " //
			+ "   nde.nd_experiment_id, " //
			+ "   gl.description AS TRIAL_INSTANCE, " //
			+ "   proj.name AS PROJECT_NAME, " //
			+ "   gl.nd_geolocation_id, " //
			+ "   (SELECT iispcvt.definition " //
			+ " 	FROM " //
			+ "      stockprop isp " //
			+ " 	INNER JOIN " //
			+ "      cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id " //
			+ " 	INNER JOIN " //
			+ "      cvterm iispcvt ON iispcvt.cvterm_id = isp.cvalue_id " //
			+ " 	WHERE " //
			+ "      isp.stock_id = s.stock_id " //
			+ "      AND ispcvt.name = 'ENTRY_TYPE') AS ENTRY_TYPE, " //
			+ "   g.germplsm_uuid AS GERMPLSM_UUID, " //
			+ "   s.name AS DESIGNATION, " //
			+ "   s.uniquename AS ENTRY_NO, " //
			+ "   (SELECT isp.value FROM stockprop isp "
			+ "              INNER JOIN cvterm ispcvt1 ON ispcvt1.cvterm_id = isp.type_id "
			+ "     WHERE isp.stock_id = s.stock_id AND ispcvt1.name = 'ENTRY_CODE' ) AS ENTRY_CODE, " //
			+ "   (SELECT isp.value " //
			+ " 	FROM " //
			+ "      stockprop isp " //
			+ " 	INNER JOIN " //
			+ "      cvterm ispcvt1 ON ispcvt1.cvterm_id = isp.type_id " //
			+ " 	WHERE " //
			+ "      isp.stock_id = s.stock_id " //
			+ "      AND ispcvt1.name = 'SEED_SOURCE') AS SEED_SOURCE, " //
			+ "   (SELECT ndep.value " //
			+ " 	FROM " //
			+ "      nd_experimentprop ndep " //
			+ " 	INNER JOIN " //
			+ "      cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id " //
			+ " 	WHERE " //
			+ "      ndep.nd_experiment_id = nde.nd_experiment_id " //
			+ "      AND ispcvt.name = 'REP_NO') AS REP_NO, " //
			+ "   (SELECT ndep.value " //
			+ " 	FROM " //
			+ "      nd_experimentprop ndep " //
			+ " 	INNER JOIN " //
			+ "      cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id " //
			+ " 	WHERE " //
			+ "      ndep.nd_experiment_id = nde.nd_experiment_id " //
			+ "      AND ispcvt.name = 'PLOT_NO') AS PLOT_NO, " //
			+ "   nde.obs_unit_id AS OBS_UNIT_ID, " //
			+ "   (SELECT ndep.value " //
			+ "		FROM nd_experimentprop ndep " //
			+ "     INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id " //
			+ "		WHERE ndep.nd_experiment_id = nde.nd_experiment_id " //
			+ "		AND ispcvt.name = 'BLOCK_NO') AS BLOCK_NO, " //
			+ "	  (SELECT "
			+ "		(CASE WHEN (SELECT COUNT(*) FROM nd_experimentprop prop INNER JOIN cvterm_relationship crelprop ON crelprop.subject_id = prop.type_id AND crelprop.type_id = "
			+ TermId.HAS_PROPERTY.getId() + " AND crelprop.object_id=2170 WHERE prop.nd_experiment_id = nde.nd_experiment_id ) > 1 "
			//2170 = Row in Layout
			+ "		 THEN 'TBD' "
			+ "		 ELSE (SELECT  ("
			+ "				CASE WHEN scaletype.object_id = " + TermId.CATEGORICAL_VARIABLE.getId() //Identify if variable is categorical
			+ "				THEN (SELECT val.name from cvterm val WHERE val.cvterm_id = ndep.value) "
			//Using the name of the cvterm instead of the definition same as displayed in observation table
			+ "				ELSE ndep.value END"
			+ "				)"
			+ "			   FROM  nd_experimentprop ndep " //
			+ "     	   INNER JOIN  cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id " //
			+ "			   INNER JOIN  cvterm_relationship crelprop ON crelprop.subject_id = ispcvt.cvterm_id AND crelprop.type_id = "
			+ TermId.HAS_PROPERTY.getId() + " AND crelprop.object_id=2170 "//2170 = Row in Layout
			+ "     	   LEFT JOIN (SELECT scale.object_id as object_id, relation.subject_id as subject_id FROM cvterm_relationship relation INNER JOIN cvterm_relationship scale ON scale.subject_id = relation.object_id AND scale.type_id = "
			+ TermId.HAS_TYPE.getId() + " WHERE relation.type_id = " + TermId.HAS_SCALE.getId()
			+ " ) scaletype ON scaletype.subject_id = ispcvt.cvterm_id "
			+ "     	   WHERE ndep.nd_experiment_id = nde.nd_experiment_id ) END"
			+ "		) "
			+ "	  ) ROW,"//
			+ "	  (SELECT "
			+ "		(CASE WHEN (SELECT COUNT(*) FROM nd_experimentprop prop INNER JOIN  cvterm_relationship crelprop ON crelprop.subject_id = prop.type_id AND crelprop.type_id = "
			+ TermId.HAS_PROPERTY.getId() + " AND crelprop.object_id=2180  WHERE prop.nd_experiment_id = nde.nd_experiment_id ) > 1 "
			//2180 = Column in layout
			+ "		THEN 'TBD' "
			+ "		ELSE (SELECT  ("
			+ "				CASE WHEN scaletype.object_id = " + TermId.CATEGORICAL_VARIABLE.getId() //Identify if variable is categorical
			+ "				THEN (SELECT val.name from cvterm val WHERE val.cvterm_id = ndep.value) "
			//Using the name of the cvterm instead of the definition same as displayed in observation table
			+ "				ELSE ndep.value END"
			+ "				)"
			+ "			FROM    nd_experimentprop ndep" //
			+ "   		INNER JOIN  cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id " //
			+ "	  		INNER JOIN  cvterm_relationship crelprop ON crelprop.subject_id = ispcvt.cvterm_id AND crelprop.type_id = "
			+ TermId.HAS_PROPERTY.getId() + " AND crelprop.object_id = 2180"//2180 = Column in layout
			+ "   		LEFT JOIN (SELECT scale.object_id as object_id, relation.subject_id as subject_id FROM cvterm_relationship relation INNER JOIN cvterm_relationship scale ON scale.subject_id = relation.object_id AND scale.type_id = "
			+ TermId.HAS_TYPE.getId() + " WHERE relation.type_id = " + TermId.HAS_SCALE.getId()
			+ " ) scaletype ON scaletype.subject_id = ispcvt.cvterm_id "
			+ "   		WHERE ndep.nd_experiment_id = nde.nd_experiment_id) END"
			+ "		)"
			+ "	   ) COL," //
			+ "	 (SELECT l.locid  " //
			+ " 	FROM nd_geolocationprop gp " //
			+ "     INNER JOIN location l ON l.locid = gp.value  " //
			+ "		WHERE  gp.type_id = " + TermId.LOCATION_ID.getId() //
			+ "     AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS locationDbId, " //
			+ "(SELECT l.lname " //
			+ "	FROM nd_geolocationprop gp " //
			+ "	INNER JOIN location l ON l.locid = gp.value " //
			+ "	WHERE gp.type_id = " + TermId.LOCATION_ID.getId() //
			+ " AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS LocationName, " //
			+ "(SELECT  gp.value  " //
			+ " FROM nd_geolocationprop gp " //
			+ " WHERE gp.type_id = " + TermId.LOCATION_ABBR.getId() //
			+ " AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS LocationAbbreviation, " //
			+ "FieldMapCol.value AS FieldMapColumn, " //
			+ "FieldMapRow.value AS FieldMapRow, " //
			+ getColumnNamesFromTraitNames(measurementVariables) //
			+ " FROM Project p " //
			+ "    INNER JOIN project proj ON proj.project_id =  p.study_id " //
			+ "    INNER JOIN nd_experiment nde ON nde.project_id = p.project_id " //
			+ "    INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id " //
			+ "    INNER JOIN stock s ON s.stock_id = nde.stock_id " //
			+ "    INNER JOIN germplsm g ON g.gid = s.dbxref_id "
			+ "	   LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id " //
			+ "	   LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id " //
			+ "    LEFT JOIN nd_experimentprop FieldMapRow ON FieldMapRow.nd_experiment_id = nde.nd_experiment_id AND FieldMapRow.type_id = "
			//
			+ TermId.RANGE_NO.getId() //
			+ "    LEFT JOIN nd_experimentprop FieldMapCol ON FieldMapCol.nd_experiment_id = nde.nd_experiment_id AND FieldMapCol.type_id = "
			//
			+ TermId.COLUMN_NO.getId() //
			+ whereText + " GROUP BY nde.nd_experiment_id " + orderByText;
	}

	String getSingleObservationQuery(final List<MeasurementVariableDto> traits, final List<String> germplasmDescriptors,
		final List<String> designFactors) {
		return this.getObservationsMainQuery(traits, germplasmDescriptors, designFactors) + " AND nde.nd_experiment_id = :experiment_id "
			+ this.getGroupingClause();
	}

	private static String getColumnNamesFromTraitNames(final List<MeasurementVariableDto> measurementVariables) {
		final StringBuilder columnNames = new StringBuilder();
		final String traitClauseFormat =
			" MAX(IF(cvterm_variable.name = '%s', ph.value, NULL)) AS '%s', "
				+ " MAX(IF(cvterm_variable.name = '%s', ph.phenotype_id, NULL)) AS '%s', ";

		for (final MeasurementVariableDto measurementVariable : measurementVariables) {
			columnNames.append(String.format(
				traitClauseFormat,
				measurementVariable.getName(),
				measurementVariable.getName(),
				measurementVariable.getName(),
				measurementVariable.getName() + PHENOTYPE_ID));
		}

		columnNames.append(" 1=1 ");

		return columnNames.toString();
	}

	String getObservationsMainQuery(final List<MeasurementVariableDto> selectionMethodsAndTraits, final List<String> germplasmDescriptors,
		final List<String> designFactors) {
		final StringBuilder sqlBuilder = new StringBuilder();

		sqlBuilder.append("SELECT  ")
			.append("    nde.nd_experiment_id, ")
			.append("    gl.description AS TRIAL_INSTANCE, ")
			.append(
				"    (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.cvalue_id WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE,  ")
			.append("    s.dbxref_id AS GID, ")
			.append("    s.name DESIGNATION, ")
			.append("    s.uniquename ENTRY_NO, ")
			.append("    s.value as ENTRY_CODE, ")
			.append(
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'REP_NO') REP_NO,  ")
			.append(
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'PLOT_NO') PLOT_NO,  ")
			.append(
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') BLOCK_NO,  ")
			.append(
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'ROW') ROW,  ")
			.append(
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'COL') COL,  ")
			.append(
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'FIELDMAP COLUMN') 'FIELDMAP COLUMN',  ")
			.append(
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'FIELDMAP RANGE') 'FIELDMAP RANGE',  ")
			.append("    (SELECT coalesce(nullif(count(sp.sample_id), 0), '-') FROM sample AS sp "
				+ "INNER JOIN nd_experiment sp_nde ON sp.nd_experiment_id = sp_nde.nd_experiment_id WHERE sp_nde.nd_experiment_id = nde.nd_experiment_id OR sp_nde.parent_id = nde.nd_experiment_id) 'SUM_OF_SAMPLES',  ")
			.append("    nde.obs_unit_id as OBS_UNIT_ID,  ");

		final String traitClauseFormat =
			" MAX(IF(cvterm_variable.name = '%s', ph.value, NULL)) AS '%s',   MAX(IF(cvterm_variable.name = '%s', ph.phenotype_id, NULL)) AS '%s',   MAX(IF(cvterm_variable.name = '%s', ph.status, NULL)) AS '%s', ";

		for (final MeasurementVariableDto measurementVariable : selectionMethodsAndTraits) {
			sqlBuilder.append(String.format(traitClauseFormat,
				measurementVariable.getName(),
				measurementVariable.getName(),
				measurementVariable.getName(),
				measurementVariable.getName() + PHENOTYPE_ID,
				measurementVariable.getName(),
				measurementVariable.getName() + STATUS));
		}

		if (!germplasmDescriptors.isEmpty()) {
			final String germplasmDescriptorClauseFormat =
				"    (SELECT sprop.value FROM stockprop sprop INNER JOIN cvterm spropcvt ON spropcvt.cvterm_id = sprop.type_id WHERE sprop.stock_id = s.stock_id AND spropcvt.name = '%s') '%s',  ";
			for (final String gpFactor : germplasmDescriptors) {
				sqlBuilder.append(String.format(germplasmDescriptorClauseFormat, gpFactor, gpFactor));
			}
		}

		if (!designFactors.isEmpty()) {
			final String designFactorClauseFormat =
				"    (SELECT xprop.value FROM nd_experimentprop xprop INNER JOIN cvterm xpropcvt ON xpropcvt.cvterm_id = xprop.type_id WHERE xprop.nd_experiment_id = nde.nd_experiment_id AND xpropcvt.name = '%s') '%s',  ";
			for (final String designFactor : designFactors) {
				sqlBuilder.append(String.format(designFactorClauseFormat, designFactor, designFactor));
			}
		}

		sqlBuilder.append(" 1=1 FROM  ")
			.append("	project p  ")
			.append("	INNER JOIN nd_experiment nde ON nde.project_id = p.project_id  ")
			.append("	INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id  ")
			.append("	INNER JOIN stock s ON s.stock_id = nde.stock_id  ")
			.append("	LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id  ")
			.append("	LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id  ")
			.append("		WHERE p.study_id = :studyId AND p.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId() + " \n");

		return sqlBuilder.toString();
	}

	String getInstanceNumberClause() {
		return INSTANCE_NUMBER_CLAUSE;
	}

	String getOrderingClause(final String sortBy, final String sortOrder) {
		final String orderColumn = StringUtils.isNotBlank(sortBy) ? sortBy : DEFAULT_SORT_COLUMN;
		final String direction = StringUtils.isNotBlank(sortOrder) ? sortOrder : DEFAULT_SORT_ORDER;
		/**
		 * Values of these columns are numbers but the database stores it in string format (facepalm). Sorting on them requires multiplying
		 * with 1 so that they turn into number and are sorted as numbers rather than strings.
		 */
		final List<String> columnsWithNumbersAsStrings = Lists.newArrayList("ENTRY_NO", "REP_NO", "PLOT_NO", "ROW", "COL", "BLOCK_NO");
		if (columnsWithNumbersAsStrings.contains(orderColumn)) {
			return " ORDER BY (1 * " + orderColumn + ") " + direction + " ";
		}
		return " ORDER BY `" + orderColumn + "` " + direction + " ";
	}

	String getGroupingClause() {
		return GROUPING_CLAUSE;
	}

	private static String getOrderByMeasurementVariableId(final List<MeasurementVariableDto> measurementVariables) {
		return Joiner.on(",").join(Lists.transform(measurementVariables, new Function<MeasurementVariableDto, String>() {

			@Nullable
			@Override
			public String apply(final MeasurementVariableDto measurementVariables) {
				return measurementVariables.getName() + PHENOTYPE_ID;
			}
		}));
	}

	public String getSampleObservationQuery() {
		return OBSERVATIONS_FOR_SAMPLES;
	}
}
