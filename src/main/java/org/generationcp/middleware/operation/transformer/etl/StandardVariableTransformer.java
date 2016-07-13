
package org.generationcp.middleware.operation.transformer.etl;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

public class StandardVariableTransformer extends Transformer {

	public StandardVariableTransformer() {
		super(null);
	}

	public StandardVariableTransformer(final HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public StandardVariable transformVariable(final Variable variable) {
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(variable.getId());
		standardVariable.setName(variable.getName());
		standardVariable.setDescription(variable.getDefinition());
		standardVariable.setProperty(variable.getProperty());
		standardVariable.setScale(variable.getScale());
		standardVariable.setMethod(variable.getMethod());
		final DataType dataType = variable.getScale().getDataType();
		if (dataType != null) {
			standardVariable.setDataType(new Term(dataType.getId(), dataType.getName(), dataType.getName()));
		}
		if (variable.getMinValue() != null || variable.getMaxValue() != null) {
			standardVariable.setConstraints(new VariableConstraints(0, 0,
					variable.getMinValue() != null ? Double.parseDouble(variable.getMinValue()) : null,
					variable.getMaxValue() != null ? Double.parseDouble(variable.getMaxValue()) : null));
		} else if (variable.getScale().getMinValue() != null || variable.getScale().getMaxValue() != null) {
			standardVariable.setConstraints(new VariableConstraints(0, 0,
					variable.getScale().getMinValue() != null ? Double.parseDouble(variable.getScale().getMinValue()) : null,
					variable.getScale().getMaxValue() != null ? Double.parseDouble(variable.getScale().getMaxValue()) : null));
		}
		standardVariable.setEnumerations(this.getValidValues(variable));
		standardVariable.setCropOntologyId(variable.getProperty().getCropOntologyId());
		standardVariable.setVariableTypes(variable.getVariableTypes());
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
