package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class VariableListTransformer extends Transformer {
	public VariableListTransformer(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public VariableList transformStock(MeasurementRow mRow) throws MiddlewareQueryException {
		VariableList variableList = null;
		
		return variableList;
	}
	
	public VariableList transformTrialEnvironment(MeasurementRow mRow) throws MiddlewareQueryException {
		VariableList variableList = null;
		
		return variableList;
	}
}
