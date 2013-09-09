package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class ExperimentValuesTransformer extends Transformer {
	public ExperimentValuesTransformer(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public VariableList transform(MeasurementRow mRow, VariableTypeList varTypeList) throws MiddlewareQueryException {
		VariableList variableList = null;
		
		return variableList;
	}
}
