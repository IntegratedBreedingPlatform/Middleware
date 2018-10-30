package org.generationcp.middleware.service.impl.study;

import java.util.Collections;
import java.util.List;

import org.generationcp.middleware.domain.ontology.VariableType;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class GermplasmDescriptors {

	private final Session session;
	
	public GermplasmDescriptors(final Session session) {
		this.session = session;
	}

	final static String QUERY =
		" SELECT name" +
			" FROM  projectprop pp INNER JOIN cvterm cvt ON cvt.cvterm_id = pp.variable_id " +
			" WHERE pp.type_id = " + VariableType.GERMPLASM_DESCRIPTOR.getId() +
			" AND project_id = ( " +
			"		SELECT p.project_id " +
			"        FROM project_relationship pr " +
			"		 INNER JOIN  project p ON p.project_id = pr.subject_project_id " +
			"        WHERE pr.object_project_id = :studyId AND p.name LIKE '%PLOTDATA' " +
			" )";

	public List<String> find(final int studyIdentifier) {
		final List<String> list = this.query(studyIdentifier);
		if (list != null && !list.isEmpty()) {
			return Collections.unmodifiableList(list);
		}
		return Collections.unmodifiableList(Collections.<String>emptyList());
	}

	@SuppressWarnings("unchecked")
	private List<String> query(final int studyIdentifier) {
		final SQLQuery sqlQuery = this.session.createSQLQuery(GermplasmDescriptors.QUERY);
		sqlQuery.addScalar("name");
		sqlQuery.setParameter("studyId", studyIdentifier);
		return sqlQuery.list();
	}
}
