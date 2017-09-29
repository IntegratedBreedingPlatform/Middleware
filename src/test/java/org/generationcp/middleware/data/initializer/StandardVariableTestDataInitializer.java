
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.Random;

import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
import org.generationcp.middleware.domain.oms.Term;

public class StandardVariableTestDataInitializer {

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
		stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
		stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization nursery", 2));
		stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree nursery", 3));
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

	public static StandardVariable createStandardVariable(final Term property, final Term scale, final Term method,
			final Term dataType, final Term storedIn, final Term isA, final PhenotypicType phenotypicType,
			final int termId, final String name) {
		final StandardVariable stdVar = new StandardVariable(property, scale, method, dataType, isA, phenotypicType);
		stdVar.setId(termId);
		stdVar.setName(name);

		return stdVar;
	}
}
