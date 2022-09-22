package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;

import java.util.ArrayList;
import java.util.List;

public class StandardVariableTransformer extends Transformer {

	public StandardVariableTransformer() {
	}

	public StandardVariable transformVariable(final Variable variable) {
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(variable.getId());
		standardVariable.setName(variable.getName());
		standardVariable.setAlias(variable.getAlias());
		standardVariable.setDescription(variable.getDefinition());
		standardVariable.setProperty(variable.getProperty());
		standardVariable.setScale(variable.getScale());
		standardVariable.setMethod(variable.getMethod());
		standardVariable.setObsolete(variable.isObsolete());
		standardVariable.setFormula(variable.getFormula());
		final DataType dataType = variable.getScale().getDataType();
		if (dataType != null) {
			standardVariable.setDataType(new Term(dataType.getId(), dataType.getName(), dataType.getName()));
		}
		final VariableConstraints variableConstraints = new VariableConstraints();
		variableConstraints.setMinValueId(0);
		variableConstraints.setMaxValueId(0);
		//setting min value
		if(variable.getMinValue() != null ) {
			variableConstraints.setMinValue(Double.parseDouble(variable.getMinValue()));
		}
		else if(variable.getScale().getMinValue() != null) {
			variableConstraints.setMinValue(Double.parseDouble(variable.getScale().getMinValue()));
		}
		//setting max value
		if(variable.getMaxValue() != null ) {
			variableConstraints.setMaxValue(Double.parseDouble(variable.getMaxValue()));
		}
		else if(variable.getScale().getMaxValue() != null) {
			variableConstraints.setMaxValue(Double.parseDouble(variable.getScale().getMaxValue()));
		}

		standardVariable.setConstraints(variableConstraints);
		standardVariable.setEnumerations(this.getValidValues(variable));
		standardVariable.setCropOntologyId(variable.getProperty().getCropOntologyId());
		standardVariable.setVariableTypes(variable.getVariableTypes());
		standardVariable.setSystem(variable.getIsSystem());
		return standardVariable;
	}

	private List<Enumeration> getValidValues(final Variable variable) {
		final List<Enumeration> validValues = new ArrayList<>();
		final List<TermSummary> categories = variable.getScale().getCategories();
		int rank = 1;
		for (final TermSummary category : categories) {
			validValues.add(new Enumeration(category.getId(), category.getName(), category.getDefinition(), rank++));
		}
		return validValues;
	}
}
