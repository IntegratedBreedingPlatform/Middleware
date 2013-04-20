package org.generationcp.middleware.v2.domain.builder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.Variable;
import org.generationcp.middleware.v2.domain.VariableInfo;
import org.generationcp.middleware.v2.pojos.ProjectProperty;
import org.generationcp.middleware.v2.util.Debug;

public class VariableBuilder extends Builder {

	protected VariableBuilder(HibernateSessionProvider sessionProviderForLocal,
			                  HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public Set<Variable> create(List<ProjectProperty> properties) throws MiddlewareQueryException {
		Set<Variable> variables = new HashSet<Variable>();
		
		Set<VariableInfo> variableInfoList = getVariableInfoBuilder().create(properties);
		for (VariableInfo variableInfo : variableInfoList) {
			variables.add(createVariable(variableInfo, properties));
		}
		
		return variables;
	}

	private Variable createVariable(VariableInfo variableInfo, List<ProjectProperty> properties) throws MiddlewareQueryException {
		 Variable variable = new Variable();
		 variable.setVariableType(getVariableTypeBuilder().create(variableInfo));
		 variable.setValue(getValue(properties, variableInfo.getStdVariableId()));
		 return variable;
	}

	private String getValue(List<ProjectProperty> properties, int stdVariableId) {
		for (ProjectProperty property : properties) {
			if (stdVariableId == property.getTypeId()) {
				return property.getValue();
			}
		}
		return null;
	}
	
	
	
	

	
}
