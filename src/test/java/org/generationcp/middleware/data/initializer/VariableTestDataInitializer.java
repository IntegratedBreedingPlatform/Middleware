package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;

public class VariableTestDataInitializer {

	public Variable createVariable(final DataType datatype){
		Variable variable = new Variable();
		Scale scale =  new Scale();
		scale.setDataType(datatype);
		variable.setScale(scale);
		return variable;
	}
}
