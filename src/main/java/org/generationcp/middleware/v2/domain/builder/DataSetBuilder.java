package org.generationcp.middleware.v2.domain.builder;

import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.VariableTypeList;
import org.generationcp.middleware.v2.helper.VariableInfo;
import org.generationcp.middleware.v2.pojos.DmsProject;

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
		dataSet.setVariableTypes(getVariableTypes(project));
		return dataSet;
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
	
}
