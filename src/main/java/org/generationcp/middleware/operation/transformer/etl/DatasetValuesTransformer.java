package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

import java.util.List;

public class DatasetValuesTransformer extends Transformer {
	public DatasetValuesTransformer(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public DatasetValues transform(String name, String description, DataSetType type, List<MeasurementVariable> mvList, VariableTypeList varTypeList) throws MiddlewareQueryException {
		DatasetValues dataValues = new DatasetValues();
		
		dataValues.setName(name);
		dataValues.setDescription(description);
		dataValues.setType(type);
		
		VariableList variables = new VariableList();
		
		List<VariableType> varTypes = varTypeList.getVariableTypes();
		for (VariableType varType : varTypes) {
		    String value = null;
		    for (MeasurementVariable var : mvList) {
		        if (var.getTermId() == varType.getId()) {
		            value = var.getValue();
		        }
		    }
		    variables.add(new Variable(varType, value));
		}
		
		dataValues.setVariables(variables);
		
		return dataValues;
	}
}
