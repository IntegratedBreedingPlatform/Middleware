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

public class StudyVariateBuilder extends Builder {

	public StudyVariateBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public VariableTypeList build(int studyId) throws MiddlewareException {
		VariableTypeList variates = new VariableTypeList();
		this.addVariates(studyId, variates);

		List<DatasetReference> dataSetReferences = this.getDmsProjectDao().getDirectChildDatasetsOfStudy(studyId);
		for (DatasetReference dataSetReference : dataSetReferences) {
			this.addVariates(dataSetReference.getId(), variates);
		}
		return variates.sort();
	}

	private void addVariates(int projectId, VariableTypeList variates) throws MiddlewareException {
		DmsProject project = this.getDmsProjectDao().getById(projectId);
		VariableTypeList variableTypes = this.getVariableTypeBuilder().create(
				project.getProperties(),project.getProgramUUID());
		variates.addAll(variableTypes.getVariates());
	}
}
