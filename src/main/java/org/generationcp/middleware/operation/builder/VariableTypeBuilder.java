/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.operation.builder;

import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

import java.util.List;
import java.util.Set;

public class VariableTypeBuilder extends Builder {

	public VariableTypeBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}
	
	public VariableTypeList create(List<ProjectProperty> properties) throws MiddlewareQueryException {
		Set<VariableInfo> variableInfoList = getVariableInfoBuilder().create(properties);
		
		VariableTypeList variableTypes = new VariableTypeList();
		for (VariableInfo variableInfo : variableInfoList) {
			variableTypes.add(create(variableInfo));
		}
		return variableTypes.sort();
	}

	public VariableType create(VariableInfo variableInfo) throws MiddlewareQueryException {
		VariableType variableType = new VariableType();
		
		variableType.setLocalName(variableInfo.getLocalName());
		variableType.setLocalDescription(variableInfo.getLocalDescription());
		variableType.setRank(variableInfo.getRank());
		variableType.setStandardVariable(getStandardVariableBuilder().create(variableInfo.getStdVariableId()));
		variableType.setTreatmentLabel(variableInfo.getTreatmentLabel());
		
		return variableType;
	}
}
