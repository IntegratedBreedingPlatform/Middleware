package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.junit.Assert;
import org.junit.Test;

public class OntologyDataHelperTest {

	public static String propertyName = "propertyName";

	@Test
	public void testMapFromPhenotypeWithoutNoneVariate(){
		PhenotypicType phenotypicType = PhenotypicType.STUDY;

		VariableType variableType = OntologyDataHelper.mapFromPhenotype(phenotypicType, propertyName);
		Assert.assertEquals(variableType, VariableType.STUDY_DETAIL);
	}

	@Test
	public void testMapFromPhenotypeWithVariateAndValidPropertyName(){
		PhenotypicType phenotypicType = PhenotypicType.VARIATE;

		VariableType variableType = OntologyDataHelper.mapFromPhenotype(phenotypicType, "Selections");
		Assert.assertEquals(variableType, VariableType.SELECTION_METHOD);
	}

	@Test
	public void testMapFromPhenotypeWithVariateAndInValidPropertyName(){
		PhenotypicType phenotypicType = PhenotypicType.VARIATE;

		VariableType variableType = OntologyDataHelper.mapFromPhenotype(phenotypicType, propertyName);
		Assert.assertEquals(variableType, VariableType.TRAIT);
	}
}
