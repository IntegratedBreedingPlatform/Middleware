package org.generationcp.middleware.v2.domain.builder;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.DatasetReference;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.pojos.DmsProject;

public class StudyVariateBuilder extends Builder {

	public StudyVariateBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public VariableTypeList build(int studyId) throws MiddlewareQueryException {
		VariableTypeList variates = new VariableTypeList();
		if (setWorkingDatabase(studyId)) {
			addVariates(studyId, variates);
		
			List<DatasetReference> dataSetReferences = getDmsProjectDao().getDatasetNodesByStudyId(studyId);
			for (DatasetReference dataSetReference : dataSetReferences) {
				addVariates(dataSetReference.getId(), variates);
			}
		}
		return variates.sort();
	}
	
	private void addVariates(int projectId, VariableTypeList variates) throws MiddlewareQueryException {
		DmsProject project = getDmsProjectDao().getById(projectId);
		VariableTypeList variableTypes = getVariableTypeBuilder().create(project.getProperties());
		variates.addAll(variableTypes.getVariates());
	}
}
