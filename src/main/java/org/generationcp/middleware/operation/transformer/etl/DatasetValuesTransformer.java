
package org.generationcp.middleware.operation.transformer.etl;

import java.util.List;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class DatasetValuesTransformer extends Transformer {

	public DatasetValuesTransformer(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public DatasetValues transform(String name, String description, List<MeasurementVariable> mvList,
			VariableTypeList varTypeList) throws MiddlewareQueryException {
		DatasetValues dataValues = new DatasetValues();

		dataValues.setName(name);
		dataValues.setDescription(description);

		VariableList variables = new VariableList();

		List<DMSVariableType> varTypes = varTypeList.getVariableTypes();
		for (DMSVariableType varType : varTypes) {
			String value = null;
			for (MeasurementVariable var : mvList) {
				if (var.getTermId() == varType.getId()) {
					varType.setVariableType(var.getVariableType());
					value = var.getValue();
				}
			}
			variables.add(new Variable(varType, value));
		}

		dataValues.setVariables(variables);

		return dataValues;
	}
}
