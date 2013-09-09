package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.StudyValues;
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
	
	public StudyValues transform(StudyDetails studyDetails, List<MeasurementVariable> mvList) throws MiddlewareQueryException {
		StudyValues studyValues = null;
		
		return studyValues;
	}
}
