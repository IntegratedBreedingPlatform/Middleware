package org.generationcp.middleware.v2.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.generationcp.middleware.v2.helper.Variable;
import org.generationcp.middleware.v2.helper.Variables;
import org.generationcp.middleware.v2.pojos.CVTerm;
import org.generationcp.middleware.v2.pojos.CVTermRelationship;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.VariableDetails;

public abstract class VariableDetailsFactory <T extends VariableDetails> {

	abstract boolean isAccepted(Variable variable);

	abstract T getNewObject();
	
	public List<T> createDetails(DmsProject project, Collection<CVTermRelationship> relationships, Collection<CVTerm> terms) {
		
		List<T> variableList = new ArrayList<T>();

		if (project != null) {
			List<Variable> variables = new Variables(project.getProperties(), relationships, terms).getVariables();

			T variableDetails;
			for (Variable variable : variables) {
				variableDetails = createVariableDetails(variable, project.getProjectId());
				addDetails(variableList, variableDetails);
			}
		}
		return variableList;
	}
	
	private T createVariableDetails(Variable variable, Integer projectId) {
		T variableDetails = null;
		if (isAccepted(variable)) {
			variableDetails = getNewObject();
			variableDetails.setId(variable.getStandardVariableId());
			variableDetails.setName(variable.getName());
			variableDetails.setDescription(variable.getDescription());
			variableDetails.setProperty(variable.getProperty());
			variableDetails.setMethod(variable.getMethod());
			variableDetails.setScale(variable.getScale());
			variableDetails.setDataType(variable.getDataType());
			variableDetails.setStudyId(projectId);
		}
		return variableDetails;
	}
	
	private void addDetails(List<T> variableList, T variableDetails) {
		if (variableDetails != null) {
			variableList.add(variableDetails);
		}
	}
}
