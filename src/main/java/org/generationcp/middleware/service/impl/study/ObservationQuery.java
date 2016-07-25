
package org.generationcp.middleware.service.impl.study;

import java.util.List;

import javax.annotation.Nullable;

import org.fest.util.Collections;
import org.generationcp.middleware.service.api.study.TraitDto;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

class ObservationQuery {
	
	final String whereText = "where (pr.object_project_id = ? and name LIKE '%PLOTDATA'))";
	final String selectText = "SELECT \n" + "    nde.nd_experiment_id,\n" + "    gl.description AS TRIAL_INSTANCE,\n" + "    (SELECT \n"
			+ "            iispcvt.definition\n" + "        FROM\n" + "            stockprop isp\n" + "                INNER JOIN\n"
			+ "            cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id\n" + "                INNER JOIN\n"
			+ "            cvterm iispcvt ON iispcvt.cvterm_id = isp.value\n" + "        WHERE\n"
			+ "            isp.stock_id = s.stock_id\n" + "                AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE,\n"
			+ "    s.dbxref_id AS GID,\n" + "    s.name DESIGNATION,\n" + "    s.uniquename ENTRY_NO,\n" + "    (SELECT \n"
			+ "            isp.value\n" + "        FROM\n" + "            stockprop isp\n" + "                INNER JOIN\n"
			+ "            cvterm ispcvt1 ON ispcvt1.cvterm_id = isp.type_id\n" + "        WHERE\n"
			+ "            isp.stock_id = s.stock_id\n" + "                AND ispcvt1.name = 'SEED_SOURCE') SEED_SOURCE,\n"
			+ "    (SELECT \n" + "            ndep.value\n" + "        FROM\n" + "            nd_experimentprop ndep\n"
			+ "                INNER JOIN\n" + "            cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id\n" + "        WHERE\n"
			+ "            ndep.nd_experiment_id = ep.nd_experiment_id\n" + "                AND ispcvt.name = 'REP_NO') REP_NO,\n"
			+ "    (SELECT \n" + "            ndep.value\n" + "        FROM\n" + "            nd_experimentprop ndep\n"
			+ "                INNER JOIN\n" + "            cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id\n" + "        WHERE\n"
			+ "            ndep.nd_experiment_id = ep.nd_experiment_id\n" + "                AND ispcvt.name = 'PLOT_NO') PLOT_NO\n";

	final String blockNoText = "    (SELECT \n" + "            ndep.value\n" + "        FROM\n" + "            nd_experimentprop ndep\n"
			+ "                INNER JOIN\n" + "            cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id\n" + "        WHERE\n"
			+ "            ndep.nd_experiment_id = ep.nd_experiment_id\n" + "                AND ispcvt.name = 'BLOCK_NO') BLOCK_NO\n";

	/**
	 * Constructs a query that will enable us to retrieve study measurement data.
	 * 
	 * @param traits list of traits that we need to construct a query for.
	 * @return A query that can be used to retrieve study measurements data including traits
	 */
	String getObservationQuery(final List<TraitDto> traits) {

		final String columnNamesFromTraitNames = this.getColumnNamesFromTraitNames(traits);
		final String orderByTraitId = getOrderByTraitId(traits);

		final String fromText = getFromExpression(traits);

		final String orderByText = getOrderByExpression(traits, orderByTraitId);

		return selectText + columnNamesFromTraitNames +

				fromText + whereText + orderByText;
	}

	String getObservationQueryWithBlockNo(final List<TraitDto> traits) {

		final String columnNamesFromTraitNames = this.getColumnNamesFromTraitNames(traits);
		final String orderByTraitId = getOrderByTraitId(traits);

		final String fromText = getFromExpression(traits);

		final String orderByText = getOrderByExpression(traits, orderByTraitId);

		return selectText + ", " + blockNoText + columnNamesFromTraitNames +

				fromText + whereText + orderByText;
	}

	private String getOrderByExpression(final List<TraitDto> traits, final String orderByTraitId) {
		final String orderByText = Collections.isNullOrEmpty(traits) ? "" : " ORDER BY " + orderByTraitId;
		return orderByText;
	}

	private String getFromExpression(final List<TraitDto> traits) {
		final String fromText = "FROM\n" + "    Project p\n" + "        INNER JOIN\n"
				+ "    project_relationship pr ON p.project_id = pr.subject_project_id\n" + "        INNER JOIN\n"
				+ "    nd_experiment_project ep ON pr.subject_project_id = ep.project_id\n" + "        INNER JOIN\n"
				+ "    nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id\n" + "        INNER JOIN\n"
				+ "    nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id\n" + "        INNER JOIN\n"
				+ "    nd_experiment_stock es ON ep.nd_experiment_id = es.nd_experiment_id\n" + "        INNER JOIN\n"
				+ "    Stock s ON s.stock_id = es.stock_id\n" + this.getTraitDeatilsJoin(traits) + "WHERE\n" + "    p.project_id = ("
				+ "Select p.project_id from project_relationship pr\n" + "INNER JOIN project p on p.project_id = pr.subject_project_id\n";
		return fromText;
	}

	String getSingleObservationQuery(final List<TraitDto> traits) {
		return this.getObservationQuery(traits) + "AND nde.nd_experiment_id = ?";

	}

	private String getColumnNamesFromTraitNames(final List<TraitDto> traits) {
		final StringBuffer columnNames = new StringBuffer();
		int size = traits.size();
		for (int i = 0; i < size; i++) {
			if (i == 0) {
				columnNames.append(", \n");
			}
			columnNames.append(traits.get(i).getTraitName() + "." + "PhenotypeValue AS " + traits.get(i).getTraitName() + ",\n");
			columnNames.append(traits.get(i).getTraitName() + "." + "phenotype_id AS " + traits.get(i).getTraitName() + "_PhenotypeId"
					+ "\n");

			if (!(i == size - 1)) {
				columnNames.append(" , ");
			}
		}
		return columnNames.toString();
	}

	private String getTraitDeatilsJoin(final List<TraitDto> traits) {

		final StringBuffer leftOuterJoinQuery = new StringBuffer();
		for (TraitDto trait : traits) {
			leftOuterJoinQuery.append(this.getTraitDeatilsJoinQuery(trait));
		}
		return leftOuterJoinQuery.toString();

	}

	// use the id
	private String getTraitDeatilsJoinQuery(final TraitDto trait) {
		return "        LEFT OUTER JOIN\n" + "    (SELECT \n" + "        nep.nd_experiment_id,\n" + "            pt.phenotype_id,\n"
				+ "            IF(cvterm_id = cvterm_id, pt.value, NULL) AS PhenotypeValue\n" + "    FROM\n" + "        phenotype pt\n"
				+ "    INNER JOIN cvterm svdo ON svdo.cvterm_id = pt.observable_id\n"
				+ "    INNER JOIN nd_experiment_phenotype nep ON nep.phenotype_id = pt.phenotype_id\n" + "    WHERE\n"
				+ "        svdo.name = ? ) " + trait.getTraitName() + " ON " + trait.getTraitName()
				+ ".nd_experiment_id = nde.nd_experiment_id\n";
	}
	
	private static String getOrderByTraitId(final List<TraitDto> traits) {
		return Joiner.on(",").join(Lists.transform(traits, new Function<TraitDto, String>() {

			@Nullable
			@Override
			public String apply(final TraitDto traitDto) {
				return traitDto.getTraitName() + "_PhenotypeId";
			}
		}));
	}

}
