
package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class StandardVariableTestDataInitializer {

	private static final int DUMMY_PROPERTY_ID = 10;
	private static final String DUMMY_PROPERTY_NAME = "PROPERTY";
	private static final String DUMMY_PROPERTY_DEF = "PROPERT-DEF";

	private static final int DUMMY_SCALE_ID = 20;
	private static final String DUMMY_SCALE_NAME = "SCALE";
	private static final String DUMMY_SCALE_DEF = "SCALE-DEF";

	private static final int DUMMY_METHOD_ID = 30;
	private static final String DUMMY_METHOD_NAME = "METHOD";
	private static final String DUMMY_METHOD_DEF = "METHOD-DEF";

	private static final int DUMMY_DATATYPE_ID = 40;
	private static final String DUMMY_DATATYPE_NAME = "DATATYPE";
	private static final String DUMMY_DATATYPE_DEF = "DATATYPE-DEF";

	public static StandardVariable createStandardVariable() {
		final StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("variable name " + new Random().nextInt(10000));
		stdVariable.setDescription("variable description");
		stdVariable.setProperty(new Term(2002, "User", "Database user"));
		stdVariable.setMethod(new Term(4030, "Assigned", "Term, name or id assigned"));
		stdVariable.setScale(new Term(61220, "DBCV", "Controlled vocabulary from a database"));
		stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
		stdVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));
		stdVariable.setEnumerations(new ArrayList<Enumeration>());
		stdVariable.getEnumerations()
			.add(new Enumeration(10000, "N", "Nursery", 1));
		stdVariable.getEnumerations()
			.add(new Enumeration(10001, "HB", "Hybridization Nursery", 2));
		stdVariable.getEnumerations()
			.add(new Enumeration(10002, "PN", "Pedigree Nursery", 3));
		stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));
		stdVariable.setCropOntologyId("CROP-TEST");

		return stdVariable;
	}

	public static StandardVariable createStandardVariable(final Integer id, final String name) {
		final StandardVariable stdVariable = StandardVariableTestDataInitializer.createStandardVariable();
		stdVariable.setId(id);
		stdVariable.setName(name);
		stdVariable.setDescription(name + " Description");
		return stdVariable;
	}

	public static StandardVariable createStandardVariable(final TermId termId) {
		final StandardVariable standardVariable = new StandardVariable();
		Term term = new Term();
		term.setId(termId.getId());
		standardVariable.setDataType(term);
		standardVariable.setVariableTypes(new HashSet<>(Arrays.asList(VariableType.TRAIT)));
		return standardVariable;
	}

	public static StandardVariable createStandardVariable(final Term property, final Term scale, final Term method,
		final Term dataType, final Term storedIn, final Term isA, final PhenotypicType phenotypicType,
		final int termId, final String name) {
		final StandardVariable stdVar = new StandardVariable(property, scale, method, dataType, isA, phenotypicType);
		stdVar.setId(termId);
		stdVar.setName(name);

		return stdVar;
	}

	public static StandardVariable createStandardVariableTestData(final String name,
		final PhenotypicType phenotypicType) {
		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setName(name);
		standardVariable.setPhenotypicType(phenotypicType);
		// PSM combination should be unique but for testing this class, it is
		// not important
		standardVariable.setProperty(new Term(StandardVariableTestDataInitializer.DUMMY_PROPERTY_ID,
			StandardVariableTestDataInitializer.DUMMY_PROPERTY_NAME,
			StandardVariableTestDataInitializer.DUMMY_PROPERTY_DEF));
		standardVariable.setScale(new Term(StandardVariableTestDataInitializer.DUMMY_SCALE_ID,
			StandardVariableTestDataInitializer.DUMMY_SCALE_NAME,
			StandardVariableTestDataInitializer.DUMMY_SCALE_DEF));
		standardVariable.setMethod(new Term(StandardVariableTestDataInitializer.DUMMY_METHOD_ID,
			StandardVariableTestDataInitializer.DUMMY_METHOD_NAME,
			StandardVariableTestDataInitializer.DUMMY_METHOD_DEF));
		standardVariable.setDataType(new Term(StandardVariableTestDataInitializer.DUMMY_DATATYPE_ID,
			StandardVariableTestDataInitializer.DUMMY_DATATYPE_NAME,
			StandardVariableTestDataInitializer.DUMMY_DATATYPE_DEF));
		return standardVariable;
	}

	public static StandardVariable createStandardVariable(final Term property, final Term scale, final Term method,
		final Term dataType) throws Exception {

		final StandardVariable standardVariable = new StandardVariable();

		standardVariable.setName("TestVariable" + new Random().nextLong());
		standardVariable.setDescription("For unit testing purposes");
		standardVariable.setProperty(property);
		standardVariable.setScale(scale);
		standardVariable.setMethod(method);
		standardVariable.setDataType(dataType);
		standardVariable.setPhenotypicType(PhenotypicType.VARIATE);

		return standardVariable;
	}

	public static StandardVariable createStandardVariable(final String name, final String propertyName, final String scaleName,
		final String methodName) {

		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setName(name);
		standardVariable.setDescription("For unit testing purposes");

		final Term property = new Term(1, propertyName, propertyName);
		standardVariable.setProperty(property);

		final Term scale = new Term(2, scaleName, scaleName);
		standardVariable.setScale(scale);

		final Term method = new Term(3, methodName, methodName);
		standardVariable.setMethod(method);

		standardVariable.setPhenotypicType(PhenotypicType.VARIATE);

		return standardVariable;
	}

	public static StandardVariable createStandardVariableWithCategoricalDataType(final int id, final String name) {

		final StandardVariable stdVariable = StandardVariableTestDataInitializer.createStandardVariable();
		stdVariable.setId(id);
		stdVariable.setName(name);
		stdVariable.setDescription(name + " Description");
		stdVariable.setDataType(new Term(TermId.CATEGORICAL_VARIABLE.getId(), "Categorical variable",
			"variable with categorical values"));
		return stdVariable;
	}
}
