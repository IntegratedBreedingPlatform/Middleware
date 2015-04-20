package org.generationcp.middleware.service.impl.study;

import java.util.List;

public class MeasurementQuery {

    /**
     * The query needs to be generated dynamically.
     * 
     * @param projectUUID
     *            the project for which we should generate a query.
     */
    public String generateQuery(final List<String> traitNames) {
        return "SELECT \n" + "    nde.nd_experiment_id,\n" + "    gl.description AS TRIAL_INSTANCE,\n"
                + "    (SELECT \n" + "            iispcvt.definition\n" + "        FROM\n"
                + "            stockprop isp\n" + "                INNER JOIN\n"
                + "            cvterm ispcvt ON ispcvt.cvterm_id = isp.type_id\n"
                + "                INNER JOIN\n"
                + "            cvterm iispcvt ON iispcvt.cvterm_id = isp.value\n" + "        WHERE\n"
                + "            isp.stock_id = s.stock_id\n"
                + "                AND ispcvt.name = 'ENTRY_TYPE') ENTRY_TYPE,\n"
                + "    s.dbxref_id AS GID,\n" + "    s.name DESIGNATION,\n"
                + "    s.uniquename ENTRY_NO,\n" + "    (SELECT \n" + "            isp.value\n"
                + "        FROM\n" + "            stockprop isp\n" + "                INNER JOIN\n"
                + "            cvterm ispcvt1 ON ispcvt1.cvterm_id = isp.type_id\n" + "        WHERE\n"
                + "            isp.stock_id = s.stock_id\n"
                + "                AND ispcvt1.name = 'SEED_SOURCE') SEED_SOURCE,\n" + "    (SELECT \n"
                + "            ndep.value\n" + "        FROM\n" + "            nd_experimentprop ndep\n"
                + "                INNER JOIN\n"
                + "            cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id\n" + "        WHERE\n"
                + "            ndep.nd_experiment_id = ep.nd_experiment_id\n"
                + "                AND ispcvt.name = 'REP_NO') REP_NO,\n" + "    (SELECT \n"
                + "            ndep.value\n" + "        FROM\n" + "            nd_experimentprop ndep\n"
                + "                INNER JOIN\n"
                + "            cvterm ispcvt ON ispcvt.cvterm_id = ndep.type_id\n" + "        WHERE\n"
                + "            ndep.nd_experiment_id = ep.nd_experiment_id\n"
                + "                AND ispcvt.name = 'PLOT_NO') PLOT_NO,\n"
                + getColumnNamesFromTraitNames(traitNames) +

                "FROM\n" + "    Project p\n" + "        INNER JOIN\n"
                + "    project_relationship pr ON p.project_id = pr.subject_project_id\n"
                + "        INNER JOIN\n"
                + "    nd_experiment_project ep ON pr.subject_project_id = ep.project_id\n"
                + "        INNER JOIN\n"
                + "    nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id\n"
                + "        INNER JOIN\n"
                + "    nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id\n"
                + "        INNER JOIN\n"
                + "    nd_experiment_stock es ON ep.nd_experiment_id = es.nd_experiment_id\n"
                + "        INNER JOIN\n" + "    Stock s ON s.stock_id = es.stock_id\n"
                + getLeftOuterJoin(traitNames) + "WHERE\n" + "    p.project_id = ("
                		+ "Select p.project_id from project_relationship pr\n" + 
                		"INNER JOIN project p on p.project_id = pr.subject_project_id\n" + 
                		"where pr.object_project_id = ? and name LIKE '%PLOTDATA')" 
                + "\n" + "ORDER BY nde.nd_experiment_id;";
    }

    private String getColumnNamesFromTraitNames(List<String> traitNames) {
        final StringBuffer columnNames = new StringBuffer();
        int size = traitNames.size();
        for (int i = 0; i < size; i++) {
            columnNames.append(traitNames.get(0) + "." + "PhenotypeValue \n");
            if (!(i == (size - 1))) {
                columnNames.append(",");
            }
        }
        return columnNames.toString();
    }

    private String getLeftOuterJoin(final List<String> traitNames) {

        final StringBuffer leftOuterJoinQuery = new StringBuffer();
        for (String traitName : traitNames) {
            leftOuterJoinQuery.append(getLeftOuterJoinQuery(traitName));
        }
        return leftOuterJoinQuery.toString();

    }

    private String getLeftOuterJoinQuery(final String traitName) {
        return "        LEFT OUTER JOIN\n"
                + "    (SELECT \n"
                + "        nep.nd_experiment_id,\n"
                + "            pt.phenotype_id,\n"
                + "            IF(cvterm_id = cvterm_id, pt.value, NULL) AS PhenotypeValue\n"
                + "    FROM\n"
                + "        phenotype pt\n"
                + "    INNER JOIN cvterm svdo ON svdo.cvterm_id = pt.observable_id\n"
                + "    INNER JOIN nd_experiment_phenotype nep ON nep.phenotype_id = pt.phenotype_id\n"
                + "    WHERE\n" + "        svdo.name = ? ) " + traitName + " ON "
                + traitName + ".nd_experiment_id = nde.nd_experiment_id\n";
    }
}
