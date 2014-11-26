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

import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;

import java.util.List;

public class StudyVariateBuilder extends Builder {

	public StudyVariateBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
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
