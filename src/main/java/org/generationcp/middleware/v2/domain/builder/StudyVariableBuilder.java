package org.generationcp.middleware.v2.domain.builder;

import java.util.List;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.Experiment;
import org.generationcp.middleware.v2.domain.TermId;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableList;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

public class StudyVariableBuilder extends Builder {

	protected StudyVariableBuilder(HibernateSessionProvider sessionProviderForLocal,
			                  HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public VariableList create(DmsProject project, Experiment experiment, VariableTypeList variableTypes) {
		VariableList variables = new VariableList();
		
		for (VariableType variableType : variableTypes.getVariableTypes()) {
			System.out.println("DON: " + variableType.getId());
			variables.add(createVariable(variableType, project, experiment));
		}
		
		return variables;
	}

	private Variable createVariable(VariableType variableType, DmsProject project, Experiment experiment) {
		Variable variable = new Variable();
		variable.setVariableType(variableType);
		System.out.println("Stored in: " + variableType.getStandardVariable().getStoredIn());
		if (storedIn(variableType, TermId.STUDY_NAME_STORAGE)) {
			variable.setValue(project.getName());
		}
		else if (storedIn(variableType, TermId.STUDY_TITLE_STORAGE)) {
			variable.setValue(project.getDescription());
		}
		else if (storedIn(variableType, TermId.STUDY_INFO_STORAGE)) {
			variable.setValue(getPropertyValue(variableType.getId(), project.getProperties()));
		}
		else {
			Variable factor = experiment.getFactors().findById(variableType.getId());
			if (factor != null) {
				variable.setValue(factor.getValue());
			}
			else {
				Variable variate = experiment.getVariates().findById(variableType.getId());
				if (variate != null) {
					variable.setValue(variate.getValue());
				}
			}
		}
		return variable;
	}

	private boolean storedIn(VariableType variableType, TermId termId) {
		return variableType.getStandardVariable().getStoredIn().getId() == termId.getId();
	}
	
	private String getPropertyValue(int id, List<ProjectProperty> properties) {
		for (ProjectProperty property : properties) {
			if (property.getTypeId() == id) {
				return property.getValue();
			}
		}
		return null;
	}
}
