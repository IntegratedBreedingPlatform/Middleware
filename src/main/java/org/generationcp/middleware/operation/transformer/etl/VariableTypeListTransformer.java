package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

import java.util.List;

public class VariableTypeListTransformer extends Transformer {
	public VariableTypeListTransformer(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public VariableTypeList transform(List<MeasurementVariable> mvList) throws MiddlewareQueryException {
		VariableTypeList variableTypeList = null;
		
		return variableTypeList ;
	}
}
