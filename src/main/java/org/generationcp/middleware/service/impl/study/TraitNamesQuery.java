
package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.ontology.VariableType;

public class TraitNamesQuery {

	/**
	 * TODO The query needs to be generated dynamically.
	 */
	public String getTraitQuery() {

		return "SELECT \n" + "    cvterm_id, name\n" + "FROM\n" + "    projectprop pp\n" + "        INNER JOIN\n"
				+ "    cvterm cvt ON cvt.name = pp.value " + "WHERE\n" + "    type_id = " + VariableType.TRAIT.getId() + "\n"
				+ "        AND project_id = (SELECT \n" + "            p.project_id\n" + "        FROM\n"
				+ "            project_relationship pr\n" + "                INNER JOIN\n"
				+ "            project p ON p.project_id = pr.subject_project_id \n" + "        WHERE \n"
				+ "            pr.object_project_id = ?\n" + "                AND name LIKE '%PLOTDATA')";
	}
}
