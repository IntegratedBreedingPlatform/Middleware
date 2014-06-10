package org.generationcp.middleware.operation.transformer.etl;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class MeasurementVariableTransformer extends Transformer {
	
    public MeasurementVariableTransformer(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public List<MeasurementVariable> transform(VariableTypeList variableTypeList, boolean isFactor) {
		
	    List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();
	    
	    if (variableTypeList != null && variableTypeList.size() > 0) {
	        for (VariableType variableType : variableTypeList.getVariableTypes()) {
	            StandardVariable stdVariable = variableType.getStandardVariable();
	            String label = getLabelOfStoredIn(stdVariable.getStoredIn().getId());
	            
	            MeasurementVariable measurementVariable = new MeasurementVariable(stdVariable.getId(), variableType.getLocalName(), 
	                    stdVariable.getDescription(), stdVariable.getScale().getName(), stdVariable.getMethod().getName(),
	                    stdVariable.getProperty().getName(), stdVariable.getDataType().getName(), "", 
	                    label);
	            measurementVariable.setStoredIn(stdVariable.getStoredIn().getId());
	            measurementVariable.setFactor(isFactor);
	            measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
	            measurementVariable.setPossibleValues(transformPossibleValues(stdVariable.getEnumerations()));
	            if (stdVariable.getConstraints() != null) {
	            	measurementVariable.setMinRange(stdVariable.getConstraints().getMinValue());
	            	measurementVariable.setMaxRange(stdVariable.getConstraints().getMaxValue());
	            }
	            if (variableType.getTreatmentLabel() != null && !"".equals(variableType.getTreatmentLabel())) {
	            	measurementVariable.setTreatmentLabel(variableType.getTreatmentLabel());
	            }
	            measurementVariables.add(measurementVariable);
	        }
	    }
	    
	    return measurementVariables;
	}
	
	public List<MeasurementVariable> transform(VariableList variableList, boolean isFactor) {
		
	    List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();
	    
	    if (variableList != null && variableList.size() > 0) {
	        for (Variable variable : variableList.getVariables()) {
	        	VariableType variableType = variable.getVariableType();
	            StandardVariable stdVariable = variableType.getStandardVariable();
	            String label = getLabelOfStoredIn(stdVariable.getStoredIn().getId());
	            
	            MeasurementVariable measurementVariable = new MeasurementVariable(stdVariable.getId(), variableType.getLocalName(), 
	                    stdVariable.getDescription(), stdVariable.getScale().getName(), stdVariable.getMethod().getName(),
	                    stdVariable.getProperty().getName(), stdVariable.getDataType().getName(), "", 
	                    label);
	            measurementVariable.setStoredIn(stdVariable.getStoredIn().getId());
	            measurementVariable.setFactor(isFactor);
	            measurementVariable.setValue(variable.getDisplayValue());
	            measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
	            measurementVariable.setPossibleValues(transformPossibleValues(stdVariable.getEnumerations()));
	            if (stdVariable.getConstraints() != null) {
	            	measurementVariable.setMinRange(stdVariable.getConstraints().getMinValue());
	            	measurementVariable.setMaxRange(stdVariable.getConstraints().getMaxValue());
	            }
	            if (variableType.getTreatmentLabel() != null && !"".equals(variableType.getTreatmentLabel())) {
	            	measurementVariable.setTreatmentLabel(variableType.getTreatmentLabel());
	            }
	            measurementVariables.add(measurementVariable);
	        }
	    }
	    
	    return measurementVariables;
	}

	private String getLabelOfStoredIn(int storedIn) {
            return PhenotypicType.getPhenotypicTypeById(storedIn).getLabelList().get(0);
        }
	
	private List<ValueReference> transformPossibleValues(List<Enumeration> enumerations) {
		List<ValueReference> list = new ArrayList<ValueReference>();
		
		if (enumerations != null) {
			for (Enumeration enumeration : enumerations) {
				list.add(new ValueReference(enumeration.getId(), enumeration.getName(), enumeration.getDescription()));
			}
		}
		
		return list;
	}

	public MeasurementVariable transform(StandardVariable stdVariable, boolean isFactor) {
		MeasurementVariable measurementVariable = null;
		
	    if (stdVariable != null) {
            String label = getLabelOfStoredIn(stdVariable.getStoredIn().getId());
            
            measurementVariable = new MeasurementVariable(stdVariable.getId(), stdVariable.getName(), 
                    stdVariable.getDescription(), stdVariable.getScale().getName(), stdVariable.getMethod().getName(),
                    stdVariable.getProperty().getName(), stdVariable.getDataType().getName(), "", 
                    label);
            measurementVariable.setStoredIn(stdVariable.getStoredIn().getId());
            measurementVariable.setFactor(isFactor);
            measurementVariable.setDataTypeId(stdVariable.getDataType().getId());
            measurementVariable.setPossibleValues(transformPossibleValues(stdVariable.getEnumerations()));
            if (stdVariable.getConstraints() != null) {
            	measurementVariable.setMinRange(stdVariable.getConstraints().getMinValue());
            	measurementVariable.setMaxRange(stdVariable.getConstraints().getMaxValue());
            }
//            if (variableType.getTreatmentLabel() != null && !"".equals(variableType.getTreatmentLabel())) {
//            	measurementVariable.setTreatmentLabel(variableType.getTreatmentLabel());
//            }
	    }
	    
	    return measurementVariable;
	}
}
