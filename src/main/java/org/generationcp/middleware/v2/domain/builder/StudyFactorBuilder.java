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
package org.generationcp.middleware.v2.domain.builder;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.DatasetReference;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.pojos.DmsProject;

public class StudyFactorBuilder extends Builder {

	public StudyFactorBuilder(
			HibernateSessionProvider sessionProviderForLocal,
			HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public VariableTypeList build(int studyId) throws MiddlewareQueryException {
		VariableTypeList factors = new VariableTypeList();
		if (setWorkingDatabase(studyId)) {
			addFactors(studyId, factors);
		
			List<DatasetReference> dataSetReferences = getDmsProjectDao().getDatasetNodesByStudyId(studyId);
			for (DatasetReference dataSetReference : dataSetReferences) {
				addFactors(dataSetReference.getId(), factors);
			}
		}
		return factors.sort();
	}
	
	private void addFactors(int projectId, VariableTypeList factors) throws MiddlewareQueryException {
		DmsProject project = getDmsProjectDao().getById(projectId);
		VariableTypeList variableTypes = getVariableTypeBuilder().create(project.getProperties());
		factors.addAll(variableTypes.getFactors());
	}
}
