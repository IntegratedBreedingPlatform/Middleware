package org.generationcp.middleware.service.impl.study;

import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.domain.ontology.VariableType;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class DesignFactors {
	
	private final Session session;
	
	/**
	 * Retrieve all project variables whose variable type is either EXPERIMENTAL DESIGN or TREATMENT FACTOR
	 */
	final static String QUERY = 
			" SELECT name" +
			" FROM  projectprop pp INNER JOIN cvterm cvt ON cvt.cvterm_id = pp.variable_id " +
			" WHERE pp.type_id IN (" + VariableType.EXPERIMENTAL_DESIGN.getId() +  "," + VariableType.TREATMENT_FACTOR.getId() + ") " +  
			" AND project_id = ( " + 
			"		SELECT p.project_id " + 
			"        FROM project_relationship pr " + 
			"		 INNER JOIN  project p ON p.project_id = pr.subject_project_id " + 
			"        WHERE pr.object_project_id = :studyId AND p.name LIKE '%PLOTDATA' " + 
			" )";

	public DesignFactors(final Session session) {
		this.session = session;
	}

	public List<String> find(final int studyIdentifier) {
		final List<String> list = this.query(studyIdentifier);
		if (list != null && !list.isEmpty()) {
			return Collections.unmodifiableList(list);
		}
		return Collections.unmodifiableList(Collections.<String>emptyList());
	}

	@SuppressWarnings("unchecked")
	private List<String> query(final int studyIdentifier) {
		final SQLQuery sqlQuery = this.session.createSQLQuery(DesignFactors.QUERY);
		sqlQuery.addScalar("name");
		sqlQuery.setParameter("studyId", studyIdentifier);
		return sqlQuery.list();
	}

}
