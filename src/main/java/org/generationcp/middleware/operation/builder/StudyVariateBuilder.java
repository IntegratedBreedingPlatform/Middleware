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

import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;

import java.util.List;

public class StudyVariateBuilder extends Builder {

	private DaoFactory daoFactory;

	public StudyVariateBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		daoFactory = new DaoFactory(sessionProvider);
	}

	public VariableTypeList build(int studyId) throws MiddlewareException {
		VariableTypeList variates = new VariableTypeList();
		this.addVariates(studyId, variates);

		List<DatasetReference> dataSetReferences = this.daoFactory.getDmsProjectDAO().getDirectChildDatasetsOfStudy(studyId);
		for (DatasetReference dataSetReference : dataSetReferences) {
			this.addVariates(dataSetReference.getId(), variates);
		}
		return variates.sort();
	}

	private void addVariates(int projectId, VariableTypeList variates) throws MiddlewareException {
		DmsProject project = this.daoFactory.getDmsProjectDAO().getById(projectId);
		VariableTypeList variableTypes = this.getVariableTypeBuilder().create(
				project.getProperties(),project.getProgramUUID());
		variates.addAll(variableTypes.getVariates());
	}
}
