
package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;

import javax.annotation.Nullable;
import java.util.List;

class ObservationQuery {

	public static final String FROM = " FROM ";
	public static final String INNER_JOIN = " INNER JOIN ";
	public static final String WHERE = " WHERE ";
	public static final String DEFAULT_SORT_COLUMN = "PLOT_NO";
	public static final String DEFAULT_SORT_ORDER = "asc";

	public static final String WHERE_TEXT = "where (pr.object_project_id = ? and name LIKE '%PLOTDATA'))";
	public static final String SELECT_TEXT = " SELECT\n"
		+ "   nde.nd_experiment_id,\n"
		+ "   gl.description                                      AS                      TRIAL_INSTANCE,\n"
		+ "   proj.name									  AS 					  PROJECT_NAME,\n"
		+ "   gl.nd_geolocation_id,\n"
		+ "   (SELECT iispcvt.definition\n"
		+ FROM
		+ "      stockprop isp\n"
		+ INNER_JOIN
		+ "      cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id\n"
		+ INNER_JOIN
		+ "      cvterm iispcvt ON iispcvt.cvterm_id = isp.value\n"
		+ WHERE
		+ "      isp.stock_id = s.stock_id\n"
		+ "      AND ispcvt.name = 'ENTRY_TYPE')                                          ENTRY_TYPE,\n"
		+ "   s.dbxref_id                                         AS                      GID,\n"
		+ "   s.name                                                                      DESIGNATION,\n"
		+ "   s.uniquename                                                                ENTRY_NO,\n"
		+ "   s.value                                             AS                      ENTRY_CODE,\n"
		+ "   (SELECT isp.value\n"
		+ FROM
		+ "      stockprop isp\n"
		+ INNER_JOIN
		+ "      cvterm ispcvt1 ON ispcvt1.cvterm_id = isp.type_id\n"
		+ WHERE
		+ "      isp.stock_id = s.stock_id\n"
		+ "      AND ispcvt1.name = 'SEED_SOURCE')                                        SEED_SOURCE,\n"
		+ "   (SELECT ndep.value\n"
		+ FROM
		+ "      nd_experimentprop ndep\n"
		+ INNER_JOIN
		+ "      cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id\n"
		+ WHERE
		+ "      ndep.nd_experiment_id = nde.nd_experiment_id\n"
		+ "      AND ispcvt.name = 'REP_NO')                                              REP_NO,\n"
		+ "   (SELECT ndep.value\n"
		+ FROM
		+ "      nd_experimentprop ndep\n"
		+ INNER_JOIN
		+ "      cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id\n"
		+ WHERE
		+ "      ndep.nd_experiment_id = nde.nd_experiment_id\n"
		+ "      AND ispcvt.name = 'PLOT_NO')                                             PLOT_NO,\n"
		+ "   nde.OBS_UNIT_ID                                         AS                      OBS_UNIT_ID\n";
	public static final String SELECT = "(SELECT ";
	public static final String ND_GEOLOCATIONPROP_GP = "            nd_geolocationprop gp \n";
	public static final String GP_TYPE_ID = "            gp.type_id = ";
	public static final String PHENOTYPE_ID = "_PhenotypeId";
	public static final String STATUS = "_Status";
	public static final String INSTANCE_NUMBER_CLAUSE = " AND gl.nd_geolocation_id = :instanceId \n";
	public static final String GROUPING_CLAUSE = " GROUP BY nde.nd_experiment_id ";

	final String locationNameSubQuery = SELECT +
			"            l.lname \n" +
			FROM + ND_GEOLOCATIONPROP_GP +
			"                INNER JOIN \n" +
			"            location l ON l.locid = gp.value \n" +
			WHERE + GP_TYPE_ID + TermId.LOCATION_ID.getId() + " \n" +
			"                AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS LocationName";

	final String locationDbIdSubQuery = SELECT +
		"            l.locid \n" +
		FROM + ND_GEOLOCATIONPROP_GP +
		"                INNER JOIN \n" +
		"            location l ON l.locid = gp.value \n" +
		"        WHERE \n" + GP_TYPE_ID + TermId.LOCATION_ID.getId() + " \n" +
		"                AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS locationDbId";

	final String locationAbbreviationSubQuery = SELECT +
			"            gp.value \n" +
			"        FROM \n" + ND_GEOLOCATIONPROP_GP +
			"        WHERE \n" + GP_TYPE_ID + TermId.LOCATION_ABBR.getId() + " \n" +
			"                AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS LocationAbbreviation";

	public static final String FIELDMAP_ROW_TEXT = "FieldMapRow.value FieldMapRow";
	public static final String FIELDMAP_COLUMN_TEXT = "FieldMapCol.value FieldMapColumn";

	public static final String BLOCK_NO_TEXT = "    (SELECT \n" + "            ndep.value\n" + "        FROM\n" + "            nd_experimentprop ndep\n"
			+ "                INNER JOIN\n" + "            cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id\n" + "        WHERE\n"
			+ "            ndep.nd_experiment_id = nde.nd_experiment_id\n" + "                AND ispcvt.name = 'BLOCK_NO') BLOCK_NO\n";

	public static final String ROW_NUMBER_TEXT = "(SELECT  ndep.value   FROM    nd_experimentprop ndep"
			+ "            INNER JOIN  cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id"
			+ "            WHERE ndep.nd_experiment_id = nde.nd_experiment_id  AND ispcvt.name = 'ROW') ROW";

	public static final String COLUMN_NUMBER_TEXT = "(SELECT  ndep.value   FROM    nd_experimentprop ndep"
			+ "            INNER JOIN  cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id"
			+ "            WHERE ndep.nd_experiment_id = nde.nd_experiment_id  AND ispcvt.name = 'COL') COL";

	public static final String OBSERVATIONS_FOR_SAMPLES = "SELECT \n" + "    nde.nd_experiment_id as nd_experiment_id,\n"
		+ "    (select na.nval from names na where na.gid = s.dbxref_id and na.nstat = 1 limit 1) as preferred_name,\n" + "    ph.value"
		+ " as value, s.dbxref_id as gid"
		+ " FROM \n" + "    project p \n" + "        INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id \n"
		+ "        INNER JOIN nd_experiment nde ON nde.project_id = pr.subject_project_id \n"
		+ "        INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n"
		+ "        INNER JOIN stock s ON s.stock_id = nde.stock_id \n"
		+ "        LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id \n"
		+ "        LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id \n" + " WHERE \n"
		+ "\tp.project_id = (SELECT  p.project_id FROM project_relationship pr INNER JOIN project p ON p.project_id = pr.subject_project_id WHERE (pr.object_project_id = :studyId \n"
		+ "    AND name LIKE '%PLOTDATA')) \n" + " AND gl.description IN (:instanceIds) \n"
		+ " and cvterm_variable.cvterm_id = :selectionVariableId\n" + " GROUP BY nde.nd_experiment_id";

	String getAllObservationsQuery(final List<MeasurementVariableDto> selectionMethodsAndTraits, final List<String> germplasmDescriptors,
			final List<String> designFactors, final String sortBy, final String sortOrder) {
		//FIXME remove inner join with max phenotype_id when BMS-5055 is solved
		return this.getObservationsMainQuery(selectionMethodsAndTraits, germplasmDescriptors, designFactors) + this.getInstanceNumberClause() + this
			.getGroupingClause()
				+ this.getOrderingClause(sortBy, sortOrder);
	}

	/**
	 * TODO BMS-4061 Merge with {@link ObservationQuery#getObservationsMainQuery(List, List)}
	 *
	 * This query is used by BMSAPI and is very similar to {@link ObservationQuery#getObservationsMainQuery(List, List)}
	 * which is used Trial and Nursery Manager
	 */
	String getObservationQueryWithBlockRowCol(final List<MeasurementVariableDto> measurementVariables, final Integer instanceId) {
		final String columnNamesFromTraitNames = this.getColumnNamesFromTraitNames(measurementVariables);
		final String orderByMeasurementVariableId = getOrderByMeasurementVariableId(measurementVariables);

		final String fromText = this.getFromExpression(measurementVariables);

		final String orderByText = this.getOrderByExpression(measurementVariables, orderByMeasurementVariableId);

		String whereText = WHERE_TEXT;

		if (instanceId != null) {
			whereText += INSTANCE_NUMBER_CLAUSE;
		}

		return SELECT_TEXT + ", " + BLOCK_NO_TEXT + ", " + ROW_NUMBER_TEXT + "," + COLUMN_NUMBER_TEXT +
				", " + this.locationDbIdSubQuery +
				", " + this.locationNameSubQuery +
				", " + this.locationAbbreviationSubQuery +
				", " + FIELDMAP_COLUMN_TEXT +
				", " + FIELDMAP_ROW_TEXT +
				columnNamesFromTraitNames +
				fromText + whereText + orderByText;
	}

	private String getOrderByExpression(final List<MeasurementVariableDto> variables, final String orderByTraitId) {
		return null == variables || variables.isEmpty() ? "" : " ORDER BY " + orderByTraitId;
	}

	private String getFromExpression(final List<MeasurementVariableDto> variables) {
		return " FROM\n" + "    Project p\n" + INNER_JOIN
				+ "    project_relationship pr ON p.project_id = pr.subject_project_id\n" + INNER_JOIN
				+ "    project proj ON proj.project_id =  pr.object_project_id\n" + INNER_JOIN
				+ "    nd_experiment nde ON nde.project_id = pr.subject_project_id\n" + INNER_JOIN
				+ "    nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id\n" + INNER_JOIN
				+ "    Stock s ON s.stock_id = nde.stock_id\n" + this.getVariableDetailsJoin(variables)

				+ "    LEFT JOIN nd_experimentprop FieldMapRow ON FieldMapRow.nd_experiment_id = nde.nd_experiment_id AND FieldMapRow.type_id = " + TermId.RANGE_NO.getId() + "\n"
				+ "    LEFT JOIN nd_experimentprop FieldMapCol ON FieldMapCol.nd_experiment_id = nde.nd_experiment_id AND FieldMapCol.type_id = " + TermId.COLUMN_NO.getId() + "\n"

				+ "WHERE\n" + "    p.project_id = ("
				+ "Select p.project_id from project_relationship pr\n" + "INNER JOIN project p on p.project_id = pr.subject_project_id\n";
	}

	String getSingleObservationQuery(final List<MeasurementVariableDto> traits, final List<String> germplasmDescriptors, final List<String> designFactors) {
		return this.getObservationsMainQuery(traits, germplasmDescriptors, designFactors) + " AND nde.nd_experiment_id = :experiment_id \n"
				+ this.getGroupingClause();
	}

	private String getColumnNamesFromTraitNames(final List<MeasurementVariableDto> measurementVariables) {
		final StringBuilder columnNames = new StringBuilder();
		final int size = measurementVariables.size();
		for (int i = 0; i < size; i++) {
			if (i == 0) {
				columnNames.append(", \n");
			}
			columnNames.append(measurementVariables.get(i).getName() + "." + "PhenotypeValue AS " + measurementVariables.get(i).getName() + ",\n");
			columnNames.append(measurementVariables.get(i).getName() + "." + "phenotype_id AS " + measurementVariables.get(i).getName() + PHENOTYPE_ID
					+ "\n");

			if (i != size - 1) {
				columnNames.append(" , ");
			}
		}
		return columnNames.toString();
	}

	String getObservationsMainQuery(final List<MeasurementVariableDto> selectionMethodsAndTraits, final List<String> germplasmDescriptors, final List<String> designFactors) {
		final StringBuilder sqlBuilder = new StringBuilder();

		sqlBuilder.append("SELECT \n")
			.append("    nde.nd_experiment_id,\n")
			.append("    gl.description AS TRIAL_INSTANCE,\n")
			.append("    (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.value WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE, \n")
			.append("    s.dbxref_id AS GID,\n")
			.append("    s.name DESIGNATION,\n")
			.append("    s.uniquename ENTRY_NO,\n")
			.append("    s.value as ENTRY_CODE,\n")
			.append("    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'REP_NO') REP_NO, \n")
			.append("    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'PLOT_NO') PLOT_NO, \n")
			.append("    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') BLOCK_NO, \n")
			.append("    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'ROW') ROW, \n")
			.append("    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'COL') COL, \n")
			.append("    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'FIELDMAP COLUMN') 'FIELDMAP COLUMN', \n")
			.append("    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = nde.nd_experiment_id AND ispcvt.name = 'FIELDMAP RANGE') 'FIELDMAP RANGE', \n")
			.append("    (SELECT coalesce(nullif(count(sp.sample_id), 0), '-') FROM plant pl INNER JOIN sample AS sp ON pl.plant_id = sp.plant_id WHERE nde.nd_experiment_id = pl.nd_experiment_id ) 'SUM_OF_SAMPLES', \n")
			.append("    nde.OBS_UNIT_ID as OBS_UNIT_ID, \n");

		final String traitClauseFormat =
			" MAX(IF(cvterm_variable.name = '%s', ph.value, NULL)) AS '%s', \n MAX(IF(cvterm_variable.name = '%s', ph.phenotype_id, NULL)) AS '%s', \n MAX(IF(cvterm_variable.name = '%s', ph.status, NULL)) AS '%s', ";

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
					"    (SELECT sprop.value FROM stockprop sprop INNER JOIN cvterm spropcvt ON spropcvt.cvterm_id = sprop.type_id WHERE sprop.stock_id = s.stock_id AND spropcvt.name = '%s') '%s', \n";
			for (final String gpFactor : germplasmDescriptors) {
				sqlBuilder.append(String.format(germplasmDescriptorClauseFormat, gpFactor, gpFactor));
			}
		}
		
		if (!designFactors.isEmpty()) {
			final String designFactorClauseFormat =
					"    (SELECT xprop.value FROM nd_experimentprop xprop INNER JOIN cvterm xpropcvt ON xpropcvt.cvterm_id = xprop.type_id WHERE xprop.nd_experiment_id = nde.nd_experiment_id AND xpropcvt.name = '%s') '%s', \n";
			for (final String designFactor : designFactors) {
				sqlBuilder.append(String.format(designFactorClauseFormat, designFactor, designFactor));
			}
		}

		sqlBuilder.append(" 1=1 FROM \n")
			.append("	project p \n")
			.append("	INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id \n")
			.append("	INNER JOIN nd_experiment nde ON nde.project_id = pr.subject_project_id \n")
			.append("	INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n")
			.append("	INNER JOIN stock s ON s.stock_id = nde.stock_id \n")
			.append("	LEFT JOIN phenotype ph ON nde.nd_experiment_id = ph.nd_experiment_id \n")
			.append("	LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id \n")
			.append("		WHERE p.project_id = (SELECT  p.project_id FROM project_relationship pr INNER JOIN project p ON p.project_id = pr.subject_project_id WHERE (pr.object_project_id = :studyId AND name LIKE '%PLOTDATA')) \n");

		return sqlBuilder.toString();
	}

	String getInstanceNumberClause() {
		return INSTANCE_NUMBER_CLAUSE;
	}

	String getOrderingClause(final String sortBy, final String sortOrder) {
		String orderColumn = StringUtils.isNotBlank(sortBy) ? sortBy : DEFAULT_SORT_COLUMN;
		final String direction = StringUtils.isNotBlank(sortOrder) ? sortOrder : DEFAULT_SORT_ORDER;
		/**
		 * Values of these columns are numbers but the database stores it in string format (facepalm). Sorting on them requires multiplying
		 * with 1 so that they turn into number and are sorted as numbers rather than strings.
		 */
		final List<String> columnsWithNumbersAsStrings = Lists.newArrayList("ENTRY_NO", "REP_NO", "PLOT_NO", "ROW", "COL", "BLOCK_NO");
		if (columnsWithNumbersAsStrings.contains(orderColumn)) {
			orderColumn = "(1 * " + orderColumn + ")";
		}
		return " ORDER BY " + orderColumn + " " + direction + " ";
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

	private String getVariableDetailsJoin(final List<MeasurementVariableDto> measurementVariables) {

		final StringBuilder leftOuterJoinQuery = new StringBuilder();
		for (final MeasurementVariableDto measurementVariable : measurementVariables) {
			leftOuterJoinQuery.append(this.getVariableDetailsJoinQuery(measurementVariable));
		}
		return leftOuterJoinQuery.toString();

	}

	// use the id
	private String getVariableDetailsJoinQuery(final MeasurementVariableDto measurementVariabl) {
		return "        LEFT OUTER JOIN\n" + "    (SELECT pt.nd_experiment_id, pt.phenotype_id, "
				+ "            IF(cvterm_id = cvterm_id, pt.value, NULL) AS PhenotypeValue " + FROM + " phenotype pt"
				+ "    INNER JOIN cvterm svdo ON svdo.cvterm_id = pt.observable_id\n"
				+ WHERE
				+ "        svdo.name = ? ) " + measurementVariabl.getName() + " ON " + measurementVariabl.getName()
				+ ".nd_experiment_id = nde.nd_experiment_id\n";
	}

	public String getSampleObservationQuery() {
		return OBSERVATIONS_FOR_SAMPLES;
	}
}
