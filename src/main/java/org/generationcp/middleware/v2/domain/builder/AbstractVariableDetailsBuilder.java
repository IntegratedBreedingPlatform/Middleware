package org.generationcp.middleware.v2.domain.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.VariableDetails;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.helper.VariableInfo;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

public abstract class AbstractVariableDetailsBuilder <T extends VariableDetails> extends Builder {

	protected static final List<Integer> VARIATE_TYPES = Arrays.asList(
			TermId.OBSERVATION_VARIATE.getId(), TermId.CATEGORICAL_VARIATE.getId()
	);

	protected AbstractVariableDetailsBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public abstract boolean isAccepted(VariableType variable);
	
	public abstract T createNewObject(VariableType variable);
	
	public List<T> build(Integer projectId) throws MiddlewareQueryException {
		setWorkingDatabase(projectId);
		List<DmsProject> projects = getDmsProjectDao().getStudyAndDatasetsById(projectId);
		Set<T> variableSet = new HashSet<T>();
		if (projects != null && projects.size() > 0) {
			for (DmsProject project : projects) {
				variableSet.addAll(create(project.getProjectId(), project.getProperties()));
			}
		}
		return new ArrayList<T>(variableSet);
	}
	
	private List<T> create(Integer projectId, List<ProjectProperty> properties) throws MiddlewareQueryException {
		Set<VariableInfo> variableInfoSet = getVariableInfoBuilder().create(properties);
		Set<VariableType> variableTypeSet = new HashSet<VariableType>();
		for (VariableInfo info : variableInfoSet) {
			variableTypeSet.add(getVariableTypeBuilder().create(info));
		}
		List<T> variableList = new ArrayList<T>();
		
		for (VariableType variable : variableTypeSet) {
			if (isAccepted(variable)) {
				variableList.add(createVariableDetails(projectId, variable));
			}
		}
		return variableList;
	}
	
	public T createVariableDetails(Integer projectId, VariableType variable) throws MiddlewareQueryException {
		T details = createNewObject(variable);

		details.setId(variable.getId());
		details.setName(variable.getLocalName());
		details.setDescription(variable.getLocalDescription());
		details.setProperty(variable.getStandardVariable().getProperty().getName());
		details.setMethod(variable.getStandardVariable().getMethod().getName());
		details.setScale(variable.getStandardVariable().getScale().getName());
		details.setDataType(getDataType(variable.getStandardVariable().getDataType().getId()));
		details.setStudyId(projectId);
		
		return details;
	}
	
	private String getDataType(Integer termId) {
		return (TermId.CHARACTER_DBID_VARIABLE.getId() == termId
				|| TermId.CHARACTER_VARIABLE.getId() == termId
				? "C" : "N");
	}
}
