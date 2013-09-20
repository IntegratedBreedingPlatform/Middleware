package org.generationcp.middleware.operation.transformer.etl;

import java.util.List;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class VariableListTransformer extends Transformer {
	
	public VariableListTransformer(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public VariableList transformStock(MeasurementRow mRow, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		VariableList variableList = new VariableList();

		if (mRow != null && mRow.getDataList() != null && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			if (mRow.getDataList().size() == variableTypeList.getVariableTypes().size()) {
				int i = 0;
				for (VariableType variableType : variableTypeList.getVariableTypes()) {
					if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.GERMPLASM) {
						variableList.add(new Variable(variableType, mRow.getDataList().get(i).getValue()));
					}
					i++;
				}
				
			} else {//else invalid data
				throw new MiddlewareQueryException("Variables did not match the Measurements Row.");
			}
		}
		
		return variableList;
	}
	
	public VariableList transformTrialEnvironment(MeasurementRow mRow, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		VariableList variableList = new VariableList() ;
		
		if (mRow != null && mRow.getDataList() != null && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			if (mRow.getDataList().size() == variableTypeList.getVariableTypes().size()) {
				List<VariableType> varTypes = variableTypeList.getVariableTypes();
				
				for(int i = 0, l = varTypes.size(); i < l; i++ ){
					VariableType varType = varTypes.get(i);
					String value = mRow.getDataList().get(i).getValue();
										
					if (varType.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT) {
						Variable variable = new Variable(varType, value);
						variableList.add(variable);
					}
				}
			}
			else{
				throw new MiddlewareQueryException("Variables did not match the Measurements Row.");
			}
		}
		
		return variableList;
	}
	
	public VariableList transformTrialEnvironment(List<MeasurementVariable> mVarList, VariableTypeList variableTypeList) throws MiddlewareQueryException {
		VariableList variableList = new VariableList() ;
		
		if (mVarList != null  && variableTypeList != null && variableTypeList.getVariableTypes() != null) {
			if (mVarList.size() == variableTypeList.getVariableTypes().size()) {
				
				List<VariableType> varTypes = variableTypeList.getVariableTypes();
				for(int i = 0, l = mVarList.size(); i < l ; i++ ){
					VariableType varType = varTypes.get(i);
					String value = mVarList.get(i).getValue();
					
					if (varType.getStandardVariable().getPhenotypicType() == PhenotypicType.TRIAL_ENVIRONMENT) {
						variableList.add(new Variable(varType,value));
					}
				}
				
			} else {//else invalid data
				throw new MiddlewareQueryException("Variables did not match the Measurement Variable List.");
			}
		}
		
		return variableList;
	}
	
	public VariableList transformStudyDetails(StudyDetails studyDetails) throws MiddlewareQueryException {
		
		VariableList variables = new VariableList();
		String localName = null;
		String localDescription = null;
		String value = null;
		int stdVariableId = 0;
		if (studyDetails != null) {
			for (int rank=1;rank<=7;rank++) {
				//variables to be used later
				//only get STUDY_NAME, STUDY_TITLE, PM_KEY, STUDY_OBJECTIVE, STUDY_TYPE, START_DATE, END_DATE
				switch(rank) {
					case 1: stdVariableId = TermId.STUDY_NAME.getId();
							localName = "STUDY_NAME";
							localDescription = "Study name";
						    value = studyDetails.getStudyName();					     
						    break;					    
					case 2: stdVariableId = TermId.STUDY_TITLE.getId();
							localName = "STUDY_TITLE";
							localDescription = "Study title";
						    value = studyDetails.getTitle();					     
				    		break;
					case 3: stdVariableId = TermId.PM_KEY.getId();
							localName = "PM_KEY";
							localDescription = "Project Management Key";
				    		value = studyDetails.getPmKey();					     
				    		break; 	
					case 4: stdVariableId = TermId.STUDY_OBJECTIVE.getId();
							localName = "STUDY_OBJECTIVE";
							localDescription = "Study objective";
		    				value = studyDetails.getObjective();					     
		    				break; 	
					case 5: stdVariableId = TermId.STUDY_TYPE.getId();
							localName = "TYPE";
							localDescription = "Study type";
							value = studyDetails.getStudyType()!=null?
									Integer.toString(studyDetails.getStudyType().getId()):null;					     
							break; 	 	
					case 6: stdVariableId = TermId.START_DATE.getId();
							localName = "START";
							localDescription = "Start date";
							value = studyDetails.getStartDate();					     
							break; 	 	
					case 7: stdVariableId = TermId.END_DATE.getId();
							localName = "END";
							localDescription = "End date";
							value = studyDetails.getEndDate();					     
							break; 	 	
				}
				//for standardVariable
				StandardVariable standardVariable = getStandardVariableBuilder().create(stdVariableId);
				
				//for variableType
				VariableType variableType = new VariableType();
				variableType.setLocalName(localName);
				variableType.setLocalDescription(localDescription);
				variableType.setRank(rank);
				variableType.setStandardVariable(standardVariable);
				
				//for variable
				Variable variable = new Variable();
				variable.setVariableType(variableType);
				variable.setValue(value);
			    
				//add variable
				variables.add(variable);
			}
		}
		return variables.sort();
	}
}
