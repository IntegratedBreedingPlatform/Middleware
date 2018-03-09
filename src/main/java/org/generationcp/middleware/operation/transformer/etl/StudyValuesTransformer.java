
package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StudyValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

import java.util.List;

public class StudyValuesTransformer extends Transformer {

	public StudyValuesTransformer(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public StudyValues transform(Integer germplasmId, Integer locationId, List<MeasurementVariable> measurementVariables,
		VariableTypeList variableTypeList) throws MiddlewareException {

		StudyValues studyValues = new StudyValues();
		VariableList variableList = new VariableList();
		if (variableTypeList != null) {
			for (DMSVariableType variableType : variableTypeList.getVariableTypes()) {
				if (variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.STUDY
						|| variableType.getStandardVariable().getPhenotypicType() == PhenotypicType.VARIATE) {
					String value = null;
					for (MeasurementVariable var : measurementVariables) {
						if (var.getTermId() == variableType.getId()) {
							value = var.getValue();

						}
					}
					variableList.add(new Variable(variableType, value));
				}
			}
		}

		studyValues.setVariableList(variableList);
		studyValues.setGermplasmId(germplasmId);
		studyValues.setLocationId(locationId);

		return studyValues;
	}

}
