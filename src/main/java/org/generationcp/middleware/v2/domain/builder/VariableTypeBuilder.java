package org.generationcp.middleware.v2.domain.builder;

import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.VariableType;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.helper.VariableInfo;
import org.generationcp.middleware.v2.pojos.ProjectProperty;

public class VariableTypeBuilder extends Builder {

	public VariableTypeBuilder(HibernateSessionProvider sessionProviderForLocal,
			                   HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public VariableTypeList create(List<ProjectProperty> properties) throws MiddlewareQueryException {
		Set<VariableInfo> variableInfoList = getVariableInfoBuilder().create(properties);
		
		VariableTypeList variableTypes = new VariableTypeList();
		for (VariableInfo variableInfo : variableInfoList) {
			variableTypes.add(create(variableInfo));
		}
		return variableTypes;
	}

	public VariableType create(VariableInfo variableInfo) throws MiddlewareQueryException {
		VariableType variableType = new VariableType();
		
		variableType.setLocalName(variableInfo.getLocalName());
		variableType.setLocalDescription(variableInfo.getLocalDescription());
		variableType.setRank(variableInfo.getRank());
		variableType.setStandardVariable(getStandardVariableBuilder().create(variableInfo.getStdVariableId()));
		return variableType;
	}
}
