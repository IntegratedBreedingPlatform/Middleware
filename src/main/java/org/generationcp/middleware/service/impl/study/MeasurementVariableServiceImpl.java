package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.MeasurementVariableService;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class MeasurementVariableServiceImpl implements MeasurementVariableService {

	private final Session session;

	static final String STUDY_VARIABLES_QUERY = " SELECT \n"
		+ "   cvterm_id, \n"
		+ "   cvt.name \n"
		+ " FROM cvterm cvt \n"
		+ "   INNER JOIN projectprop pp ON (pp.variable_id = cvt.cvterm_id) \n"
		+ "   INNER JOIN project p ON p.project_id = pp.project_id \n"
		+ " WHERE pp.type_id IN (:variablesTypes) AND p.study_id = :studyId and p.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId()
		+ " \n";

	static final String STUDY_VARIABLES_QUERY_FOR_DATASET = " SELECT \n"
		+ "   cvterm_id, \n"
		+ "   name \n"
		+ " FROM cvterm cvt \n"
		+ "   INNER JOIN projectprop pp ON (pp.variable_id = cvt.cvterm_id) \n"
		+ " WHERE pp.type_id IN (:variablesTypes) AND pp.project_id = :studyId ";

	public MeasurementVariableServiceImpl(final Session session) {
		this.session = session;
	}

	@Override
	public List<MeasurementVariableDto> getVariables(final int studyIdentifier, final Integer... variableTypes) {
		final List<MeasurementVariableDto> measurementVariables =
			this.getVariablesByStudy(studyIdentifier, STUDY_VARIABLES_QUERY, variableTypes);
		if (!measurementVariables.isEmpty()) {
			return Collections.unmodifiableList(measurementVariables);
		}
		return Collections.unmodifiableList(Collections.<MeasurementVariableDto>emptyList());
	}

	private List<MeasurementVariableDto> getVariablesByStudy(final int studyIdentifier, final String query,
		final Integer... variableTypes) {
		final SQLQuery variableSqlQuery = this.session.createSQLQuery(query);
		variableSqlQuery.addScalar("cvterm_id");
		variableSqlQuery.addScalar("name");
		variableSqlQuery.setParameter("studyId", studyIdentifier);
		variableSqlQuery.setParameterList("variablesTypes", variableTypes);
		final List<Object[]> measurementVariables = variableSqlQuery.list();
		final List<MeasurementVariableDto> variableList = new ArrayList<>();
		for (final Object[] rows : measurementVariables) {
			variableList.add(new MeasurementVariableDto((Integer) rows[0], (String) rows[1]));
		}
		return variableList;
	}

	@Override
	public List<MeasurementVariableDto> getVariablesForDataset(final int studyIdentifier, final Integer... variableTypes) {
		final List<MeasurementVariableDto> measurementVariables =
			this.getVariablesByStudy(studyIdentifier, STUDY_VARIABLES_QUERY_FOR_DATASET, variableTypes);
		if (!measurementVariables.isEmpty()) {
			return Collections.unmodifiableList(measurementVariables);
		}
		return Collections.unmodifiableList(Collections.<MeasurementVariableDto>emptyList());
	}
}
