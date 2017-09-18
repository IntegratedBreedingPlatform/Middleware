
package org.generationcp.middleware.operation.transformer.etl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class StandardVariableTransformerTest {
	
	private static StandardVariableTransformer standardVariableTransformer;
	
	@BeforeClass
	public static void setUp() {
		standardVariableTransformer = new StandardVariableTransformer(Mockito.mock(HibernateSessionProvider.class));
	}

	@Test
	public void testTransformVariable()
			throws MiddlewareQueryException {
		Variable variable = createVariableTestData();
		StandardVariable standardVariable = standardVariableTransformer.transformVariable(variable);
		assertNotNull(standardVariable);
		assertEquals(variable.getId(),standardVariable.getId());
		assertEquals(variable.getName(),standardVariable.getName());
		assertEquals(variable.getDefinition(),standardVariable.getDescription());
		assertEquals(variable.getProperty(),standardVariable.getProperty());
		assertEquals(variable.getScale(),standardVariable.getScale());
		assertEquals(variable.getMethod(),standardVariable.getMethod());
		DataType vDataType = variable.getScale().getDataType();
		Term svDataType = standardVariable.getDataType();
		assertEquals(vDataType.getId(),new Integer(svDataType.getId()));
		assertEquals(vDataType.getName(),svDataType.getName());
		VariableConstraints svConstraints = standardVariable.getConstraints();
		assertEquals(new Double(variable.getMinValue()),svConstraints.getMinValue());
		assertEquals(new Double(variable.getMaxValue()),svConstraints.getMaxValue());
		List<Enumeration> validValues = standardVariable.getEnumerations();
		List<TermSummary> categories = variable.getScale().getCategories();
		assertEquals(categories.size(),categories.size());

		Map<String, String> categoryMap = new HashMap<>();
		for (TermSummary category : categories) {
			categoryMap.put(category.getName(), category.getDefinition());
		}

		for (Enumeration enumeration : validValues) {
			assertTrue(categoryMap.keySet().contains(enumeration.getName()));
			assertEquals(categoryMap.get(enumeration.getName()),
					enumeration.getDescription());
		}
		assertEquals(variable.getProperty().getCropOntologyId(),
				standardVariable.getCropOntologyId());
		assertTrue(standardVariable.isObsolete());
	}

	private Variable createVariableTestData() {
		Variable variable = new Variable();
		variable.setId(1);
		variable.setName("VARIABLE NAME");
		variable.setDefinition("VARIABLE DEF");
		variable.setProperty(createPropertyTestData());
		variable.setMethod(createMethodTestData());
		variable.setScale(createScaleTestData());
		variable.setMinValue("1");
		variable.setMaxValue("4");
		variable.setObsolete(true);
		return variable;
	}

	private Property createPropertyTestData() {
		Term term = new Term();
		term.setId(2);
		term.setName("PROPERTY NAME");
		term.setDefinition("PROPERTY DEF");
		return new Property(term);	
	}
	
	private Method createMethodTestData() {
		Term term = new Term();
		term.setId(2);
		term.setName("METHOD NAME");
		term.setDefinition("METHOD DEF");
		return new Method(term);
	}
	
	private Scale createScaleTestData() {
		Term term = new Term();
		term.setId(2);
		term.setName("SCALE NAME");
		term.setDefinition("SCALE DEF");
		Scale scale = new Scale(term);
		scale.setDataType(DataType.CATEGORICAL_VARIABLE);
		scale.addCategory(new TermSummary(null, "CAT NAME 1", "CAT DESC 1"));
		scale.addCategory(new TermSummary(null, "CAT NAME 2", "CAT DESC 2"));
		return scale;
	}
}
