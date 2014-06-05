package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

import java.util.List;


public class StudyValuesTransformer extends Transformer {
	public StudyValuesTransformer(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public StudyValues transform(Integer germplasmId, Integer locationId, StudyDetails studyDetails, 
			List<MeasurementVariable> measurementVariables, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		
		StudyValues studyValues = new StudyValues();
		VariableList variableList = new VariableList();
		VariableList variableListFromStudy = getVariableListTransformer().transformStudyDetails(studyDetails, variableTypeList);
		variableTypeList.allocateRoom(variableListFromStudy.size());
		
		if (variableTypeList != null) {
			for (VariableType variableType : variableTypeList.getVariableTypes()) {
				if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.STUDY 
				        || variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.VARIATE) {
				    String value = null;
				    for (MeasurementVariable var : measurementVariables) {
				        if (var.getTermId() == variableType.getId()) {
				            value = var.getValue();
				            
				        }
				    }
				    variableList.add(new Variable(variableType, value));
				}
			}
		}
		
		if (variableListFromStudy != null) {
			for(Variable variable : variableListFromStudy.getVariables()) {
				variableList.add(variable);
				variableTypeList.add(variable.getVariableType());
			}
		}
				
		studyValues.setVariableList(variableList);
		studyValues.setGermplasmId(germplasmId);
		studyValues.setLocationId(locationId);
		
		return studyValues;
	}
	
}
