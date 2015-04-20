package org.generationcp.middleware.service.impl.study;


public class TraitNamesQuery {
    /**
     * The query needs to be generated dynamically.
     * 
     * @param projectUUID
     *            the project for which we should generate a query.
     */
    public String generateQuery() {
    	
    	return "SELECT \n" + 
    			"    value\n" + 
    			"FROM\n" + 
    			"    projectprop\n" + 
    			"WHERE\n" + 
    			"    type_id = 1043\n" + 
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
