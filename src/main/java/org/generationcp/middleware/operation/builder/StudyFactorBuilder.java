/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.operation.builder;

import java.util.List;

import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;

public class StudyFactorBuilder extends Builder {

	public StudyFactorBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public VariableTypeList build(int studyId) throws MiddlewareException {
		VariableTypeList factors = new VariableTypeList();
		this.addFactors(studyId, factors);

		List<DatasetReference> dataSetReferences = this.getDmsProjectDao().getDirectChildDatasetsOfStudy(studyId);
		for (DatasetReference dataSetReference : dataSetReferences) {
			this.addFactors(dataSetReference.getId(), factors);
		}
		return factors.sort();
	}

	private void addFactors(int projectId, VariableTypeList factors) throws MiddlewareException {
		DmsProject project = this.getDmsProjectDao().getById(projectId);
		VariableTypeList variableTypes = this.getVariableTypeBuilder().create(
				project.getProperties(),project.getProgramUUID());
		factors.addAll(variableTypes.getFactors());
	}
}
