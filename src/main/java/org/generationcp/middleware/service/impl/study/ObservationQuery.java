
package org.generationcp.middleware.service.impl.study;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.fest.util.Collections;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

class ObservationQuery {

	final String DEFAULT_SORT_COLUMN = "PLOT_NO";
	final String DEFAULT_SORT_ORDER = "asc";

	final String whereText = "where (pr.object_project_id = ? and name LIKE '%PLOTDATA'))";
	final String selectText = "SELECT \n" + "    nde.nd_experiment_id,\n" + "    gl.description AS TRIAL_INSTANCE,\n" + "    (SELECT \n"
			+ "            iispcvt.definition\n" + "        FROM\n" + "            stockprop isp\n" + "                INNER JOIN\n"
			+ "            cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id\n" + "                INNER JOIN\n"
			+ "            cvterm iispcvt ON iispcvt.cvterm_id = isp.value\n" + "        WHERE\n"
			+ "            isp.stock_id = s.stock_id\n" + "                AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE,\n"
			+ "    s.dbxref_id AS GID,\n" + "    s.name DESIGNATION,\n" + "    s.uniquename ENTRY_NO,\n"
			+ "    s.value as ENTRY_CODE,\n"
			+ "    (SELECT \n"
			+ "            isp.value\n" + "        FROM\n" + "            stockprop isp\n" + "                INNER JOIN\n"
			+ "            cvterm ispcvt1 ON ispcvt1.cvterm_id = isp.type_id\n" + "        WHERE\n"
			+ "            isp.stock_id = s.stock_id\n" + "                AND ispcvt1.name = 'SEED_SOURCE') SEED_SOURCE,\n"
			+ "    (SELECT \n" + "            ndep.value\n" + "        FROM\n" + "            nd_experimentprop ndep\n"
			+ "                INNER JOIN\n" + "            cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id\n" + "        WHERE\n"
			+ "            ndep.nd_experiment_id = ep.nd_experiment_id\n" + "                AND ispcvt.name = 'REP_NO') REP_NO,\n"
			+ "    (SELECT \n" + "            ndep.value\n" + "        FROM\n" + "            nd_experimentprop ndep\n"
			+ "                INNER JOIN\n" + "            cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id\n" + "        WHERE\n"
			+ "            ndep.nd_experiment_id = ep.nd_experiment_id\n" + "                AND ispcvt.name = 'PLOT_NO') PLOT_NO,\n"
			+ "    nde.plot_id as PLOT_ID \n";

	final String locationNameSubQuery = "(SELECT \n" +
			"            l.lname \n" +
			"        FROM \n" +
			"            nd_geolocationprop gp \n" +
			"                INNER JOIN \n" +
			"            location l ON l.locid = gp.value \n" +
			"        WHERE \n" +
			"            gp.type_id = " + TermId.LOCATION_ID.getId() + " \n" +
			"                AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS LocationName";

	final String locationAbbreviationSubQuery = "(SELECT \n" +
			"            gp.value \n" +
			"        FROM \n" +
			"            nd_geolocationprop gp \n" +
			"        WHERE \n" +
			"            gp.type_id = " + TermId.LOCATION_ABBR.getId() + " \n" +
			"                AND gp.nd_geolocation_id = gl.nd_geolocation_id) AS LocationAbbreviation";

	final String fieldmapRowText = "FieldMapRow.value FieldMapRow";
	final String fieldmapColumnText = "FieldMapCol.value FieldMapColumn";

	final String blockNoText = "    (SELECT \n" + "            ndep.value\n" + "        FROM\n" + "            nd_experimentprop ndep\n"
			+ "                INNER JOIN\n" + "            cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id\n" + "        WHERE\n"
			+ "            ndep.nd_experiment_id = ep.nd_experiment_id\n" + "                AND ispcvt.name = 'BLOCK_NO') BLOCK_NO\n";

	final String rowNumberText = "(SELECT  ndep.value   FROM    nd_experimentprop ndep"
			+ "            INNER JOIN  cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id"
			+ "            WHERE ndep.nd_experiment_id = ep.nd_experiment_id  AND ispcvt.name = 'ROW') ROW";

	final String columnNumberText = "(SELECT  ndep.value   FROM    nd_experimentprop ndep"
			+ "            INNER JOIN  cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id"
			+ "            WHERE ndep.nd_experiment_id = ep.nd_experiment_id  AND ispcvt.name = 'COL') COL";


	String getAllObservationsQuery(final List<MeasurementVariableDto> measurementVariables, List<String> germplasmDescriptors, final String sortBy,
			final String sortOrder) {
		return this.getObservationsMainQuery(measurementVariables, germplasmDescriptors) + getInstanceNumberClause() + getGroupingClause()
				+ getOrderingClause(sortBy, sortOrder);
	}

	/**
	 * Constructs a query that will enable us to retrieve information about all plots, associated metadata and measurements in one go, for a
	 * trial/nursery.
	 *
	 * @param measurementVariables list of measurementVariables that we need to construct a query for.
	 */
	String getObservationQuery(final List<MeasurementVariableDto> measurementVariables) {

		final String columnNamesFromTraitNames = this.getColumnNamesFromTraitNames(measurementVariables);
		final String orderByTraitId = getOrderByTraitId(measurementVariables);

		final String fromText = getFromExpression(measurementVariables);

		final String orderByText = getOrderByExpression(measurementVariables, orderByTraitId);

		return selectText + columnNamesFromTraitNames +

				fromText + whereText + orderByText;
	}

	String getObservationQueryWithBlockRowCol(final List<MeasurementVariableDto> measurementVariables, Integer instanceId) {
		final String columnNamesFromTraitNames = this.getColumnNamesFromTraitNames(measurementVariables);
		final String orderByTraitId = getOrderByTraitId(measurementVariables);

		final String fromText = getFromExpression(measurementVariables);

		final String orderByText = getOrderByExpression(measurementVariables, orderByTraitId);

		String whereText = this.whereText;

		if (instanceId != null) {
			whereText += " AND gl.nd_geolocation_id = :instanceId \n";
		}

		return selectText + ", " + blockNoText + ", " + rowNumberText + "," + columnNumberText +
				", " + locationNameSubQuery +
				", " + locationAbbreviationSubQuery +
				", " + fieldmapColumnText +
				", " + fieldmapRowText +
				columnNamesFromTraitNames +
				fromText + whereText + orderByText;
	}

	private String getOrderByExpression(final List<MeasurementVariableDto> variables, final String orderByTraitId) {
		final String orderByText = Collections.isNullOrEmpty(variables) ? "" : " ORDER BY " + orderByTraitId;
		return orderByText;
	}

	private String getFromExpression(final List<MeasurementVariableDto> variables) {
		final String fromText = " FROM\n" + "    Project p\n" + "        INNER JOIN\n"
				+ "    project_relationship pr ON p.project_id = pr.subject_project_id\n" + "        INNER JOIN\n"
				+ "    nd_experiment_project ep ON pr.subject_project_id = ep.project_id\n" + "        INNER JOIN\n"
				+ "    nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id\n" + "        INNER JOIN\n"
				+ "    nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id\n" + "        INNER JOIN\n"
				+ "    nd_experiment_stock es ON ep.nd_experiment_id = es.nd_experiment_id\n" + "        INNER JOIN\n"
				+ "    Stock s ON s.stock_id = es.stock_id\n" + this.getTraitDeatilsJoin(variables)

				+ "    LEFT JOIN nd_experimentprop FieldMapRow ON FieldMapRow.nd_experiment_id = ep.nd_experiment_id AND FieldMapRow.type_id = " + TermId.RANGE_NO.getId() + "\n"
				+ "    LEFT JOIN nd_experimentprop FieldMapCol ON FieldMapCol.nd_experiment_id = ep.nd_experiment_id AND FieldMapCol.type_id = " + TermId.COLUMN_NO.getId() + "\n"

				+ "WHERE\n" + "    p.project_id = ("
				+ "Select p.project_id from project_relationship pr\n" + "INNER JOIN project p on p.project_id = pr.subject_project_id\n";
		return fromText;
	}

	String getSingleObservationQueryNonOrdered(final List<MeasurementVariableDto> measurementVariables) {
		return this.getObservationQuery(measurementVariables) + "AND nde.nd_experiment_id = ?";

	}

	String getSingleObservationQuery(final List<MeasurementVariableDto> measurementVariables, List<String> germplasmDescriptors) {
		return this.getObservationsMainQuery(measurementVariables, germplasmDescriptors) + " AND nde.nd_experiment_id = :experiment_id \n"
				+ getGroupingClause();
	}

	private String getColumnNamesFromTraitNames(final List<MeasurementVariableDto> measurementVariables) {
		final StringBuffer columnNames = new StringBuffer();
		int size = measurementVariables.size();
		for (int i = 0; i < size; i++) {
			if (i == 0) {
				columnNames.append(", \n");
			}
			columnNames.append(measurementVariables.get(i).getName() + "." + "PhenotypeValue AS " + measurementVariables.get(i).getName() + ",\n");
			columnNames.append(measurementVariables.get(i).getName() + "." + "phenotype_id AS " + measurementVariables.get(i).getName() + "_PhenotypeId"
					+ "\n");

			if (!(i == size - 1)) {
				columnNames.append(" , ");
			}
		}
		return columnNames.toString();
	}

	String getObservationsMainQuery(final List<MeasurementVariableDto> measurementVariables, List<String> germplasmDescriptors) {
		StringBuilder sqlBuilder = new StringBuilder();
		
		sqlBuilder.append( 
				"SELECT \n" + 
				"    nde.nd_experiment_id,\n" + 
				"    gl.description AS TRIAL_INSTANCE,\n" +
				"    (SELECT iispcvt.definition FROM stockprop isp INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id INNER JOIN cvterm iispcvt ON iispcvt.cvterm_id = isp.value WHERE isp.stock_id = s.stock_id AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE, \n" +
				"    s.dbxref_id AS GID,\n" + 
				"    s.name DESIGNATION,\n" + 
				"    s.uniquename ENTRY_NO,\n" + 
				"    s.value as ENTRY_CODE,\n" +
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'REP_NO') REP_NO, \n" + 
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'PLOT_NO') PLOT_NO, \n" + 
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'BLOCK_NO') BLOCK_NO, \n" + 
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'ROW') ROW, \n" + 
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'COL') COL, \n" +
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'FIELDMAP COLUMN') 'FIELDMAP COLUMN', \n" +
				"    (SELECT ndep.value FROM nd_experimentprop ndep INNER JOIN cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id WHERE ndep.nd_experiment_id = ep.nd_experiment_id AND ispcvt.name = 'FIELDMAP RANGE') 'FIELDMAP RANGE', \n" +
		        "    nde.plot_id as PLOT_ID, \n");
		
		String traitClauseFormat = 
				" MAX(IF(cvterm_variable.name = '%s', ph.value, NULL)) AS '%s', \n" +
				" MAX(IF(cvterm_variable.name = '%s', ph.phenotype_id, NULL)) AS '%s', \n";

		for (MeasurementVariableDto measurementVariable : measurementVariables) {
			sqlBuilder.append(String.format(traitClauseFormat, measurementVariable.getName(), measurementVariable.getName(),
				measurementVariable.getName(), measurementVariable.getName() + "_PhenotypeId"));
		}
		
		if (!germplasmDescriptors.isEmpty()) {
			String germplasmDescriptorClauseFormat =
					"    (SELECT sprop.value FROM stockprop sprop INNER JOIN cvterm spropcvt ON spropcvt.cvterm_id = sprop.type_id WHERE sprop.stock_id = s.stock_id AND spropcvt.name = '%s') '%s', \n";
			for (String gpFactor : germplasmDescriptors) {
				sqlBuilder.append(String.format(germplasmDescriptorClauseFormat, gpFactor, gpFactor));
			}
		}

		sqlBuilder.append(
				" 1=1 FROM \n" + 
				"    project p \n" + 
				"        INNER JOIN project_relationship pr ON p.project_id = pr.subject_project_id \n" + 
				"        INNER JOIN nd_experiment_project ep ON pr.subject_project_id = ep.project_id \n" + 
				"        INNER JOIN nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id \n" + 
				"        INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id \n" + 
				"        INNER JOIN nd_experiment_stock es ON ep.nd_experiment_id = es.nd_experiment_id \n" + 
				"        INNER JOIN stock s ON s.stock_id = es.stock_id \n" + 
				"        LEFT JOIN nd_experiment_phenotype neph ON neph.nd_experiment_id = nde.nd_experiment_id \n" + 
				"        LEFT JOIN phenotype ph ON neph.phenotype_id = ph.phenotype_id \n" + 
				"        LEFT JOIN cvterm cvterm_variable ON cvterm_variable.cvterm_id = ph.observable_id \n" + 
				" WHERE p.project_id = (SELECT  p.project_id FROM project_relationship pr INNER JOIN project p ON p.project_id = pr.subject_project_id WHERE (pr.object_project_id = :studyId AND name LIKE '%PLOTDATA')) \n");				
				
		return sqlBuilder.toString();
	}

	String getInstanceNumberClause() {
		return " AND gl.nd_geolocation_id = :instanceId \n";
	}

	String getOrderingClause(final String sortBy, final String sortOrder) {
		String orderColumn = StringUtils.isNotBlank(sortBy) ? sortBy : DEFAULT_SORT_COLUMN;
		String direction = StringUtils.isNotBlank(sortOrder) ? sortOrder : DEFAULT_SORT_ORDER;
		/**
		 * Values of these columns are numbers but the database stores it in string format (facepalm). Sorting on them requires multiplying
		 * with 1 so that they turn into number and are sorted as numbers rather than strings.
		 */
		List<String> columnsWithNumbersAsStrings = Lists.newArrayList("ENTRY_NO", "REP_NO", "PLOT_NO", "ROW", "COL", "BLOCK_NO");
		if (columnsWithNumbersAsStrings.contains(orderColumn)) {
			orderColumn = "(1 * " + orderColumn + ")";
		}
		return " ORDER BY " + orderColumn + " " + direction + " ";
	}

	String getGroupingClause() {
		return " GROUP BY nde.nd_experiment_id ";
	}

	private static String getOrderByTraitId(final List<MeasurementVariableDto> measurementVariables) {
		return Joiner.on(",").join(Lists.transform(measurementVariables, new Function<MeasurementVariableDto, String>() {

			@Nullable
			@Override
			public String apply(final MeasurementVariableDto measurementVariables) {
				return measurementVariables.getName() + "_PhenotypeId";
			}
		}));
	}

	private String getTraitDeatilsJoin(final List<MeasurementVariableDto> measurementVariables) {

		final StringBuffer leftOuterJoinQuery = new StringBuffer();
		for (MeasurementVariableDto measurementVariable : measurementVariables) {
			leftOuterJoinQuery.append(this.getTraitDeatilsJoinQuery(measurementVariable));
		}
		return leftOuterJoinQuery.toString();

	}

	// use the id
	private String getTraitDeatilsJoinQuery(final MeasurementVariableDto measurementVariabl) {
		return "        LEFT OUTER JOIN\n" + "    (SELECT \n" + "        nep.nd_experiment_id,\n" + "            pt.phenotype_id,\n"
				+ "            IF(cvterm_id = cvterm_id, pt.value, NULL) AS PhenotypeValue\n" + "    FROM\n" + "        phenotype pt\n"
				+ "    INNER JOIN cvterm svdo ON svdo.cvterm_id = pt.observable_id\n"
				+ "    INNER JOIN nd_experiment_phenotype nep ON nep.phenotype_id = pt.phenotype_id\n" + "    WHERE\n"
				+ "        svdo.name = ? ) " + measurementVariabl.getName() + " ON " + measurementVariabl.getName()
				+ ".nd_experiment_id = nde.nd_experiment_id\n";
	}
}
