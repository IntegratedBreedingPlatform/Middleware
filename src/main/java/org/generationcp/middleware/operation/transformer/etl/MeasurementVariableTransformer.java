package org.generationcp.middleware.operation.transformer.etl;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.StandardVariable;
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
	            MeasurementVariable measurementVariable = new MeasurementVariable(stdVariable.getId(), stdVariable.getName(), 
	                    stdVariable.getDescription(), stdVariable.getScale().getName(), stdVariable.getMethod().getName(),
	                    stdVariable.getProperty().getName(), stdVariable.getDataType().getName(), "", 
	                    variableType.getLocalName());
	            measurementVariable.setStoredIn(stdVariable.getStoredIn().getName());
	            measurementVariable.setFactor(isFactor);
	            measurementVariables.add(measurementVariable);
	        }
	    }
	    
	    return measurementVariables;
	}
}
