package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;

public class VariableTestDataInitializer {

	public static Variable createVariable(final DataType datatype) {
		final Variable variable = new Variable();
		final Scale scale = new Scale();
		scale.setDataType(datatype);
		variable.setScale(scale);
		return variable;
	}

	public static Variable createVariable() {
		final Variable variable = new Variable();
		final Scale scale = new Scale();
		scale.setDataType(DataType.CATEGORICAL_VARIABLE);
		scale.setName("Scale Name");
		variable.setScale(scale);
		final Property property = new Property();
		property.setName("Property name");
		variable.setProperty(property);
		final Method method = new Method();
		method.setName("Method name");
		variable.setMethod(method);
		return variable;
	}
}
