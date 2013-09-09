package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

import java.util.List;

public class DatasetValuesTransformer extends Transformer {
	public DatasetValuesTransformer(HibernateSessionProvider sessionProviderForLocal,
            HibernateSessionProvider sessionProviderForCentral) {
				super(sessionProviderForLocal, sessionProviderForCentral);
	}
	
	public DatasetValues transform(List<MeasurementVariable> mvList) throws MiddlewareQueryException {
		DatasetValues dataValues = null;
		
		return dataValues;
	}
}
