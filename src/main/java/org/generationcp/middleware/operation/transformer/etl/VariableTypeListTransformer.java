package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableType;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

import java.util.List;

public class VariableTypeListTransformer extends Transformer {
	
    public VariableTypeListTransformer(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public VariableTypeList transform(List<MeasurementVariable> measurementVariables, boolean isVariate) 
			throws MiddlewareQueryException, MiddlewareException {
		
		return transform(measurementVariables, isVariate, 1);
	}
	
	public VariableTypeList transform(List<MeasurementVariable> measurementVariables, boolean isVariate, int rank) 
			throws MiddlewareQueryException, MiddlewareException {
		
		VariableTypeList variableTypeList = new VariableTypeList();
		
		if (measurementVariables != null && measurementVariables.size() > 0) {
    		for (MeasurementVariable measurementVariable : measurementVariables) {
    			StandardVariable standardVariable = null;
    			if(measurementVariable.getTermId()!=0) {//in etl v2, standard variables are already created before saving the study
    				standardVariable = getStandardVariableBuilder().create(measurementVariable.getTermId());
    			} else {
    				standardVariable = getStandardVariableBuilder().findOrSave(
    					measurementVariable.getName(), 
    					measurementVariable.getDescription(), 
    					measurementVariable.getProperty(),
    					measurementVariable.getScale(),
    					measurementVariable.getMethod(), 
    					isVariate ? PhenotypicType.VARIATE : PhenotypicType.getPhenotypicTypeForLabel(measurementVariable.getLabel()),
    					measurementVariable.getDataType());
    			}

                measurementVariable.setTermId(standardVariable.getId());

    			VariableType variableType = new VariableType(
    						measurementVariable.getName(), 
    						measurementVariable.getDescription(), 
    						standardVariable, rank++);
    			variableType.setTreatmentLabel(measurementVariable.getTreatmentLabel());
    			
    			variableTypeList.add(variableType);
				
    		}
		}
		
		return variableTypeList;
	}
}
