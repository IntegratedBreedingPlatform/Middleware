
package org.generationcp.middleware.operation.saver;

import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.manager.ontology.VariableCache;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PhenotypeSaverTest extends IntegrationTestBase {

	private PhenotypeSaver phenotypeSaver;

	@Before
	public void setUp() {
		this.phenotypeSaver = new PhenotypeSaver(super.sessionProvder);
		ContextHolder.setCurrentCrop("maize");
	}

	@Test
	public void testCreatePhenotypeReturningNullNewPhenotype() {
		String value = null;
		Phenotype oldPhenotype = null;
		Assert.assertNull(this.phenotypeSaver.createPhenotype(1, value, oldPhenotype, TermId.NUMERIC_VARIABLE.getId()));
		value = "";
		Assert.assertNull(this.phenotypeSaver.createPhenotype(1, value, oldPhenotype, TermId.NUMERIC_VARIABLE.getId()));
		oldPhenotype = new Phenotype();
		oldPhenotype.setPhenotypeId(null);
		Assert.assertNull(this.phenotypeSaver.createPhenotype(1, value, oldPhenotype, TermId.NUMERIC_VARIABLE.getId()));
		value = null;
		Assert.assertNull(this.phenotypeSaver.createPhenotype(1, value, oldPhenotype, TermId.NUMERIC_VARIABLE.getId()));
	}

	@Test
	public void testCreatePhenotypeFromBreedingMethodVariableWithMcodeValue() {
		final Integer variableId = TermId.BREEDING_METHOD_VARIATE_CODE.getId();
		final String value = "AGB1";
		final Phenotype newPhenotype = this.phenotypeSaver.createPhenotype(variableId, value, null, TermId.NUMERIC_VARIABLE.getId());
		Assert.assertNotNull(newPhenotype);
		Assert.assertEquals(value, newPhenotype.getValue());
		Assert.assertEquals(null, newPhenotype.getcValueId());
		Assert.assertEquals(variableId, newPhenotype.getObservableId());
		Assert.assertEquals(String.valueOf(variableId), newPhenotype.getName());
	}

	@Test
	public void testCreatePhenotypeFromBreedingMethodVarWithMidValue() {
		final Integer variableId = TermId.BREEDING_METHOD_VARIATE_CODE.getId();
		final String value = "70";
		final String mCode = "AGB1";
		final Phenotype newPhenotype = this.phenotypeSaver.createPhenotype(variableId, value, null, TermId.NUMERIC_VARIABLE.getId());
		Assert.assertNotNull(newPhenotype);
		Assert.assertEquals(mCode, newPhenotype.getValue());
		Assert.assertEquals(null, newPhenotype.getcValueId());
		Assert.assertEquals(variableId, newPhenotype.getObservableId());
		Assert.assertEquals(String.valueOf(variableId), newPhenotype.getName());
	}

	@Test
	public void testCreatePhenotypeFromBreedingMethodVarWithMidValueUsingVariableCache() {
		final Integer variableId = TermId.BREEDING_METHOD_VARIATE_CODE.getId();
		final String value = "70";
		final String mCode = "AGB1";
		VariableCache.addToCache(variableId, this.createBreedingMethodVariable(variableId));
		final Phenotype newPhenotype = this.phenotypeSaver.createPhenotype(variableId, value, null, TermId.NUMERIC_VARIABLE.getId());
		Assert.assertNotNull(newPhenotype);
		Assert.assertEquals(mCode, newPhenotype.getValue());
		Assert.assertEquals(null, newPhenotype.getcValueId());
		Assert.assertEquals(variableId, newPhenotype.getObservableId());
		Assert.assertEquals(String.valueOf(variableId), newPhenotype.getName());
	}

	private Variable createBreedingMethodVariable(final Integer variableId) {
		final Variable variable = new Variable();
		variable.setId(variableId);
		variable.setProperty(this.createBreedingMethodProperty());
		return variable;
	}

	private Property createBreedingMethodProperty() {
		final Property property = new Property();
		property.setName("Breeding Method");
		property.setId(TermId.BREEDING_METHOD_PROP.getId());
		return property;
	}

	@Test
	public void testCreatePhenotypeFromCategoricalVariableWithCvtermNameAsValue() {
		final Integer crustVariableId = 20310;
		final String crustValue = "1";
		final Phenotype newPhenotype =
				this.phenotypeSaver.createPhenotype(crustVariableId, crustValue, null, TermId.CATEGORICAL_VARIABLE.getId());
		Assert.assertNotNull(newPhenotype);
		Assert.assertEquals(crustValue, newPhenotype.getValue());
		Assert.assertEquals(null, newPhenotype.getcValueId());
		Assert.assertEquals(crustVariableId, newPhenotype.getObservableId());
		Assert.assertEquals(String.valueOf(crustVariableId), newPhenotype.getName());
	}

	@Test
	public void testCreatePhenotypeFromCategoricalVariableWithCvtermIdAsValue() {
		final Integer crustVariableId = 20310;
		final String crustValue = "1";
		final Integer crustCvalueId = 50723;
		final Phenotype newPhenotype =
				this.phenotypeSaver.createPhenotype(crustVariableId, String.valueOf(crustCvalueId), null,
						TermId.CATEGORICAL_VARIABLE.getId());
		Assert.assertNotNull(newPhenotype);
		Assert.assertEquals(crustValue, newPhenotype.getValue());
		Assert.assertEquals(crustCvalueId, newPhenotype.getcValueId());
		Assert.assertEquals(crustVariableId, newPhenotype.getObservableId());
		Assert.assertEquals(String.valueOf(crustVariableId), newPhenotype.getName());
	}

	@Test
	public void testCreatePhenotypeFromCategoricalVariableWithAcceptedValue() {
		final Integer crustVariableId = 20310;
		final String crustValue = "abc";
		final Phenotype newPhenotype =
				this.phenotypeSaver.createPhenotype(crustVariableId, crustValue, null, TermId.CATEGORICAL_VARIABLE.getId());
		Assert.assertNotNull(newPhenotype);
		Assert.assertEquals(crustValue, newPhenotype.getValue());
		Assert.assertEquals(null, newPhenotype.getcValueId());
		Assert.assertEquals(crustVariableId, newPhenotype.getObservableId());
		Assert.assertEquals(String.valueOf(crustVariableId), newPhenotype.getName());
	}

	@Test
	public void testCreatePhenotypeFromCategoricalVariableWithNullValueAndExistingPhenotype() {
		final Phenotype oldPhenotype = new Phenotype();
		oldPhenotype.setPhenotypeId(1234567);
		final Integer crustVariableId = 20310;
		final String crustValue = null;
		final Phenotype newPhenotype =
				this.phenotypeSaver.createPhenotype(crustVariableId, crustValue, oldPhenotype, TermId.CATEGORICAL_VARIABLE.getId());
		Assert.assertNotNull(newPhenotype);
		Assert.assertEquals(oldPhenotype.getPhenotypeId(), newPhenotype.getPhenotypeId());
		Assert.assertEquals(crustValue, newPhenotype.getValue());
		Assert.assertEquals(null, newPhenotype.getcValueId());
		Assert.assertEquals(crustVariableId, newPhenotype.getObservableId());
		Assert.assertEquals(String.valueOf(crustVariableId), newPhenotype.getName());
	}

	@Test
	public void testCreatePhenotypeFromNonCategoricalAndNonBreedingMethodVar() {
		final Integer siteSoilPhVariableId = 8270;
		final String siteSoilPhValue = "7";
		final Phenotype newPhenotype =
				this.phenotypeSaver.createPhenotype(siteSoilPhVariableId, siteSoilPhValue, null, TermId.NUMERIC_VARIABLE.getId());
		Assert.assertNotNull(newPhenotype);
		Assert.assertEquals(siteSoilPhValue, newPhenotype.getValue());
		Assert.assertEquals(null, newPhenotype.getcValueId());
		Assert.assertEquals(siteSoilPhVariableId, newPhenotype.getObservableId());
		Assert.assertEquals(String.valueOf(siteSoilPhVariableId), newPhenotype.getName());
	}

}
