
package org.generationcp.middleware.operation.transformer.etl;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;

import java.util.ArrayList;
import java.util.List;

public class StandardVariableTransformer extends Transformer {

	public StandardVariableTransformer(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public StandardVariable transformVariable(Variable variable)
			throws MiddlewareQueryException {
		StandardVariable standardVariable = new StandardVariable();
		standardVariable.setId(variable.getId());
		standardVariable.setName(variable.getName());
		standardVariable.setDescription(variable.getDefinition());
		standardVariable.setProperty(variable.getProperty());
		standardVariable.setScale(variable.getScale());
		standardVariable.setMethod(variable.getMethod());
		DataType dataType = variable.getScale().getDataType();
		if(dataType!=null) {
			standardVariable.setDataType(new Term(dataType.getId(),dataType.getName(),dataType.getName()));
		}
		if(variable.getMinValue()!=null && variable.getMaxValue()!=null) {
			standardVariable.setConstraints(new VariableConstraints(
				0, 0, Double.parseDouble(variable.getMinValue()), 
				Double.parseDouble(variable.getMaxValue())));
		} else if(variable.getScale().getMinValue()!=null && variable.getScale().getMaxValue()!=null) {
			standardVariable.setConstraints(new VariableConstraints(
					0, 0, Double.parseDouble(variable.getScale().getMinValue()), 
					Double.parseDouble(variable.getScale().getMaxValue())));
		}
		standardVariable.setEnumerations(getValidValues(variable));
		standardVariable.setCropOntologyId(variable.getProperty().getCropOntologyId());
		standardVariable.setVariableTypes(variable.getVariableTypes());
		return standardVariable;
	}

	private List<Enumeration> getValidValues(Variable variable) {
		List<Enumeration> validValues = new ArrayList<>();
		List<TermSummary> categories = variable.getScale().getCategories();
		int rank = 1;
		for (TermSummary category : categories) {
			validValues.add(new Enumeration(category.getId(), category.getName(), category.getDefinition(), rank++));
		}
		return validValues;
	}
}
