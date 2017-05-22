package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;


public class MeasurementVariableServiceImpl implements MeasurementVariableService {

	private final Session session;

	final static String STUDY_VARIABLES_QUERY = "SELECT cvterm_id, name \n" + "FROM cvterm cvt \n "
		+ "INNER JOIN projectprop ppTermId on (ppTermId.value = cvt.cvterm_id and ppTermId.type_id = " + TermId.STANDARD_VARIABLE.getId()
		+ ") \n" + "INNER JOIN projectprop ppVarType on (ppVarType.project_id = ppTermId.project_id and ppVarType.rank = ppTermId.rank) \n"
		+ "WHERE ppVarType.type_id in(:variablesType) " + " AND ppTermId.project_id = (SELECT \n" + "            p.project_id\n"
		+ "        FROM\n" + "            project_relationship pr\n" + "                INNER JOIN\n"
		+ "            project p ON p.project_id = pr.subject_project_id \n" + "        WHERE \n"
		+ "            pr.object_project_id = :studyId\n" + "                AND name LIKE '%PLOTDATA')";

	public MeasurementVariableServiceImpl(final Session session) {
		this.session = session;
	}

	@Override
	public List<MeasurementVariableDto> getVariables(final int studyIdentifier, Integer... variableTypes) {
		final List<MeasurementVariableDto> measurementVariables = this.getVariablesByStudy(studyIdentifier, variableTypes);
		if (measurementVariables != null && !measurementVariables.isEmpty()) {
			return Collections.unmodifiableList(measurementVariables);
		}
		return Collections.unmodifiableList(Collections.<MeasurementVariableDto>emptyList());
	}

	private List<MeasurementVariableDto> getVariablesByStudy(final int studyIdentifier, Integer... variableTypes) {
		final SQLQuery variableSqlQuery = this.session.createSQLQuery(this.STUDY_VARIABLES_QUERY);
		variableSqlQuery.addScalar("cvterm_id");
		variableSqlQuery.addScalar("name");
		variableSqlQuery.setParameter("studyId", studyIdentifier);
		variableSqlQuery.setParameterList("variablesType", variableTypes);
		final List<Object[]> measurementVariables = variableSqlQuery.list();
		final List<MeasurementVariableDto> variableList = new ArrayList<MeasurementVariableDto>();
		for (final Object[] rows : measurementVariables) {
			variableList.add(new MeasurementVariableDto((Integer) rows[0], (String) rows[1]));
		}
		return variableList;
	}

}
