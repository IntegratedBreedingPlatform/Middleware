package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.*;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;

import java.util.ArrayList;
import java.util.List;

public class DMSVariableTestDataInitializer {

	public static Variable createVariable() {
		final Variable variable = new Variable();
		variable.setVariableType(DMSVariableTypeTestDataInitializer.createDMSVariableType());
		return variable;
	}
	
	public static Variable createVariableWithStandardVariable(final TermId termId) {
		final Variable variable = new Variable();
		variable.setVariableType(DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithStandardVariable(termId));
		variable.setValue("Cross/1");
		return variable;
	}
	
	public static Variable createVariableWithStandardVariable(final TermId termId, final String value) {
		final Variable variable = DMSVariableTestDataInitializer.createVariableWithStandardVariable(termId);
		variable.setValue(value);
		return variable;
	}
	
	public static Variable createVariableWithCategoricalStandardVariable() {
		final Variable variable = new Variable();
		variable.setVariableType(DMSVariableTypeTestDataInitializer.createDMSVariableTypeWithCategoricalStandardVariable());
		variable.setValue("1");
		return variable;
	}

	public static Variable createVariable(final Integer variableId, final String variableValue, final Integer dataTypeId, final VariableType variableType) {

		final Variable variable = new Variable();
		variable.setValue(variableValue);

		final StandardVariable standardVariable = new StandardVariable();
		final List<Enumeration> enumerations = new ArrayList<>();
		enumerations.add(new Enumeration(1234, "Categorical Name 1", "Categorical Name Description 1", 1));
		enumerations.add(new Enumeration(1235, "Categorical Name 2", "Categorical Name Description 2", 2));
		enumerations.add(new Enumeration(1236, "Categorical Name 3", "Categorical Name Description 3", 3));
		standardVariable.setEnumerations(enumerations);
		standardVariable.setDataType(new Term(dataTypeId, "",""));
		standardVariable.setId(variableId);
		standardVariable.setPhenotypicType(PhenotypicType.VARIATE);

		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setVariableType(variableType);
		dmsVariableType.setStandardVariable(standardVariable);
		dmsVariableType.setRole(variableType.getRole());
		dmsVariableType.setRank(99);

		variable.setVariableType(dmsVariableType);

		return variable;

	}
}
