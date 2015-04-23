package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.oms.TermId;

//Put class into one query file

public class TraitNamesQuery {
    /**
     * The query needs to be generated dynamically.
     * 
     * @param projectUUID
     *            the project for which we should generate a query.
     */
    public String getTraitQuery() {
    	
    	return "SELECT \n" + 
    			"    value\n" + 
    			"FROM\n" + 
    			"    projectprop\n" + 
    			"WHERE\n" + 
    			"    type_id = " + TermId.OBSERVATION_VARIATE.getId() + "\n" + 
    			"        AND project_id = (SELECT \n" + 
    			"            p.project_id\n" + 
    			"        FROM\n" + 
    			"            project_relationship pr\n" + 
    			"                INNER JOIN\n" + 
    			"            project p ON p.project_id = pr.subject_project_id\n" + 
    			"        WHERE\n" + 
    			"            pr.object_project_id = ?\n" + 
    			"                AND name LIKE '%PLOTDATA');";
    }
}
