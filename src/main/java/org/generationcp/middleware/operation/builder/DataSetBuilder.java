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

import java.util.Set;

import org.generationcp.middleware.domain.DataSet;
import org.generationcp.middleware.domain.DataSetType;
import org.generationcp.middleware.domain.TermId;
import org.generationcp.middleware.domain.VariableTypeList;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.helper.VariableInfo;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;

public class DataSetBuilder extends Builder {

	public DataSetBuilder(HibernateSessionProvider sessionProviderForLocal,
			                 HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public DataSet build(int dataSetId) throws MiddlewareQueryException {
		DataSet dataSet = null;
		if (setWorkingDatabase(dataSetId)) {
			DmsProject project = getDmsProjectDao().getById(dataSetId);
			if (project != null) {
				dataSet = createDataSet(project);
			}
		}
		return dataSet;
	}
	
	public VariableTypeList getVariableTypes(int dataSetId) throws MiddlewareQueryException {
		VariableTypeList variableTypeList = new VariableTypeList();
		if (setWorkingDatabase(dataSetId)) {
			DmsProject project = getDmsProjectDao().getById(dataSetId);
			if (project != null) {
				Set<VariableInfo> variableInfoList = getVariableInfoBuilder().create(project.getProperties());
				for (VariableInfo variableInfo : variableInfoList) {
					variableTypeList.add(getVariableTypeBuilder().create(variableInfo));
				}
			}
		}
		return variableTypeList.sort();
	}

	private DataSet createDataSet(DmsProject project) throws MiddlewareQueryException {
		DataSet dataSet = new DataSet();
		dataSet.setId(project.getProjectId());
		dataSet.setName(project.getName());
		dataSet.setDescription(project.getDescription());
		dataSet.setStudyId(getStudyId(project));
		dataSet.setDataSetType(getDataSetType(project));
		dataSet.setVariableTypes(getVariableTypes(project));
		dataSet.setLocationIds(getLocationIds(project.getProjectId()));
		return dataSet;
	}

	private Set<Integer> getLocationIds(Integer projectId) throws MiddlewareQueryException {
		return this.getGeolocationDao().getLocationIds(projectId);
	}

	private VariableTypeList getVariableTypes(DmsProject project) throws MiddlewareQueryException {
		VariableTypeList variableTypes = new VariableTypeList();
		
		Set<VariableInfo> variableInfoList = getVariableInfoBuilder().create(project.getProperties());
		for (VariableInfo variableInfo : variableInfoList) {
			variableTypes.add(getVariableTypeBuilder().create(variableInfo));
		}
		return variableTypes.sort();
	}

	private int getStudyId(DmsProject project) {
		DmsProject study = project.getRelatedTos().get(0).getObjectProject();
		return study.getProjectId();
	}
	
	private DataSetType getDataSetType(DmsProject project) throws MiddlewareQueryException {
		for (ProjectProperty property : project.getProperties()) {
			if (TermId.DATASET_TYPE.getId() == property.getTypeId()) {
				return DataSetType.findById(Integer.valueOf(property.getValue()));
			}
		}
		return null;
	}
	
}
