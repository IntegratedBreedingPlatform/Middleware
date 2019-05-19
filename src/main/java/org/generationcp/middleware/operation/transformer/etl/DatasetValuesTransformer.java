
package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.DatasetValues;
import org.generationcp.middleware.domain.dms.Variable;
import org.generationcp.middleware.domain.dms.VariableList;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

import java.util.List;

public class DatasetValuesTransformer extends Transformer {

	public DatasetValuesTransformer(final HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public DatasetValues transform(
		final String name, final String description, final List<MeasurementVariable> mvList,
		final VariableTypeList varTypeList) {
		final DatasetValues dataValues = new DatasetValues();

		dataValues.setName(name);
		dataValues.setDescription(description);

		final VariableList variables = new VariableList();

		final List<DMSVariableType> varTypes = varTypeList.getVariableTypes();
		for (final DMSVariableType varType : varTypes) {
			String value = null;
			for (final MeasurementVariable var : mvList) {
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
