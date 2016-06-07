/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.service;

import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Random;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Method;
import org.generationcp.middleware.domain.oms.Property;
import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitClass;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.service.api.OntologyService;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OntologyServiceImplTest extends IntegrationTestBase {

	private static final String SEED_AMOUNT_G = "SEED_AMOUNT_g";
		private static final String NUMBER_OF_RECORDS = " #RECORDS: ";
	private static final int AGRONOMIC_TRAIT_CLASS = 1340;

	@Autowired
	private OntologyService ontologyService;

	private static final String PROGRAM_UUID = "1234567";

	@Test
	public void testGetStandardVariableById() throws MiddlewareException {
		StandardVariable var = this.ontologyService.getStandardVariable(8005, PROGRAM_UUID);
		Assert.assertNotNull(var);
		var.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testGetStandardVariables() throws MiddlewareException {
		this.ontologyService.getStandardVariables("CUAN_75DAG", PROGRAM_UUID);

		// TODO : fix hardcoded data assertion
		/* assertFalse(vars.isEmpty()); */
		/*
		 * for (StandardVariable var : vars){ var.print(INDENT); }
		 */
	}

	@Test
	public void testGetNumericStandardVariableWithMinMax() throws MiddlewareException {
		StandardVariable var = this.ontologyService.getStandardVariable(8270, PROGRAM_UUID);
		Assert.assertNotNull(var);
		if (var.getConstraints() != null) {
			Assert.assertNotNull(var.getConstraints().getMinValueId());
			Assert.assertNotNull(var.getConstraints().getMaxValueId());
		}
		var.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testGetStandardVariablesByProperty() throws MiddlewareException {
		List<StandardVariable> vars = this.ontologyService.getStandardVariablesByProperty(Integer.valueOf(2100), PROGRAM_UUID);
		assertFalse(vars.isEmpty());
	}

	@Test
	public void testGetStandardVariablesByMethod() throws MiddlewareException {
		List<StandardVariable> vars = this.ontologyService.getStandardVariablesByMethod(Integer.valueOf(4040), PROGRAM_UUID);
		assertFalse(vars.isEmpty());
	}

	@Test
	public void testGetStandardVariablesByScale() throws MiddlewareException {
		List<StandardVariable> vars = this.ontologyService.getStandardVariablesByScale(Integer.valueOf(6040), PROGRAM_UUID);
		assertFalse(vars.isEmpty());
	}

	@Test
	public void testGetAllTermsByCvId() throws MiddlewareQueryException {
		List<Term> terms = this.ontologyService.getAllTermsByCvId(CvId.VARIABLES);
		for (Term term : terms) {
			term.print(IntegrationTestBase.INDENT);
		}
	}

	/* ======================= PROPERTY ================================== */

	@Test
	public void testGetPropertyById() throws MiddlewareQueryException {
		Property property = this.ontologyService.getProperty(2000);
		Assert.assertNotNull(property);
		property.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testGetPropertyByName() throws MiddlewareQueryException {
		Property property = this.ontologyService.getProperty("Dataset");
		Assert.assertNotNull(property);
		property.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testGetAllProperties() throws MiddlewareQueryException {
		List<Property> properties = this.ontologyService.getAllProperties();
		Assert.assertFalse(properties.isEmpty());
		for (Property property : properties) {
			property.print(IntegrationTestBase.INDENT);
		}
		Debug.println(IntegrationTestBase.INDENT, OntologyServiceImplTest.NUMBER_OF_RECORDS + properties.size());
	}

	@Test
	public void testAddProperty() throws MiddlewareQueryException {
		Property property =
				this.ontologyService.addProperty("NEW property", "New property description", OntologyServiceImplTest.AGRONOMIC_TRAIT_CLASS);
		property.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testUpdateProperty() throws Exception {
		String name = "UPDATE property";
		String definition = "Update property description " + (int) (Math.random() * 100);
		Property origProperty = this.ontologyService.getProperty(name);
		if (origProperty == null || origProperty.getTerm() == null) { // first run, add before update
			origProperty = this.ontologyService.addProperty(name, definition, OntologyServiceImplTest.AGRONOMIC_TRAIT_CLASS);
		}
		this.ontologyService.updateProperty(new Property(new Term(origProperty.getId(), name, definition), origProperty.getIsA()));
		Property newProperty = this.ontologyService.getProperty(name);

		Debug.println(IntegrationTestBase.INDENT, "Original:  " + origProperty);
		Debug.println(IntegrationTestBase.INDENT, "Updated :  " + newProperty);
		Assert.assertTrue(newProperty.getDefinition().equals(definition));
	}

	/* ======================= SCALE ================================== */

	@Test
	public void testGetScaleById() throws MiddlewareQueryException {
		Scale scale = this.ontologyService.getScale(6030);
		Assert.assertNotNull(scale);
		scale.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testGetScaleByName() throws MiddlewareQueryException {
		Scale scale = this.ontologyService.getScale("Calculated");
		Assert.assertNotNull(scale);
		scale.print(IntegrationTestBase.INDENT);
	}
	
	@Test
	public void testGetInventoryScaleByName() throws MiddlewareQueryException {
		Scale scale = this.ontologyService.getInventoryScaleByName(SEED_AMOUNT_G);
		Assert.assertNotNull("Scale should not be null", scale);
		Assert.assertEquals("Scale name should be " + SEED_AMOUNT_G, SEED_AMOUNT_G, scale.getName());
	}

	@Test
	public void testGetAllScales() throws MiddlewareQueryException {
		List<Scale> scales = this.ontologyService.getAllScales();
		Assert.assertFalse(scales.isEmpty());
		for (Scale scale : scales) {
			scale.print(IntegrationTestBase.INDENT);
		}
		Debug.println(IntegrationTestBase.INDENT, OntologyServiceImplTest.NUMBER_OF_RECORDS + scales.size());
	}

	@Test
	public void testAddScale() throws MiddlewareQueryException {
		Scale scale = this.ontologyService.addScale("NEW scale", "New scale description");
		scale.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testUpdateScale() throws Exception {
		String name = "UPDATE scale";
		String definition = "Update scale description " + (int) (Math.random() * 100);
		Scale origScale = this.ontologyService.getScale(name);
		if (origScale == null || origScale.getTerm() == null) { // first run, add before update
			origScale = this.ontologyService.addScale(name, definition);
		}
		this.ontologyService.updateScale(new Scale(new Term(origScale.getId(), name, definition)));
		Scale newScale = this.ontologyService.getScale(name);

		Debug.println(IntegrationTestBase.INDENT, "Original:  " + origScale);
		Debug.println(IntegrationTestBase.INDENT, "Updated :  " + newScale);
		Assert.assertTrue(newScale.getDefinition().equals(definition));
	}

	/* ======================= METHOD ================================== */

	@Test
	public void testGetMethodById() throws MiddlewareQueryException {
		Method method = this.ontologyService.getMethod(4030);
		Assert.assertNotNull(method);
		method.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testGetMethodByName() throws MiddlewareQueryException {
		Method method = this.ontologyService.getMethod("Enumerated");
		Assert.assertNotNull(method);
		method.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testGetAllMethods() throws MiddlewareQueryException {
		List<Method> methods = this.ontologyService.getAllMethods();
		Assert.assertFalse(methods.isEmpty());
		for (Method method : methods) {
			method.print(IntegrationTestBase.INDENT);
		}
		Debug.println(IntegrationTestBase.INDENT, OntologyServiceImplTest.NUMBER_OF_RECORDS + methods.size());
	}

	@Test
	public void testAddMethod() throws MiddlewareQueryException {
		Method method = this.ontologyService.addMethod("NEW method", "New method description");
		method.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testUpdateMethod() throws Exception {
		String name = "UPDATE method";
		String definition = "Update method description " + (int) (Math.random() * 100);
		Method origMethod = this.ontologyService.getMethod(name);
		if (origMethod == null || origMethod.getTerm() == null) { // first run, add before update
			origMethod = this.ontologyService.addMethod(name, definition);
		}
		this.ontologyService.updateMethod(new Method(new Term(origMethod.getId(), name, definition)));
		Method newMethod = this.ontologyService.getMethod(name);

		Debug.println(IntegrationTestBase.INDENT, "Original:  " + origMethod);
		Debug.println(IntegrationTestBase.INDENT, "Updated :  " + newMethod);
		Assert.assertTrue(newMethod.getDefinition().equals(definition));
	}

	/* ======================= OTHERS ================================== */

	@Test
	public void testGetDataTypes() throws MiddlewareQueryException {
		List<Term> dataTypes = this.ontologyService.getAllDataTypes();
		Assert.assertFalse(dataTypes.isEmpty());
		for (Term dataType : dataTypes) {
			dataType.print(IntegrationTestBase.INDENT);
		}
	}

	@Test
	public void testGetAllTraitGroupsHierarchy() throws MiddlewareQueryException {
		List<TraitClassReference> traitGroups = this.ontologyService.getAllTraitGroupsHierarchy(true);
		Assert.assertFalse(traitGroups.isEmpty());
		for (TraitClassReference traitGroup : traitGroups) {
			traitGroup.print(IntegrationTestBase.INDENT);
		}
		Debug.println(IntegrationTestBase.INDENT, OntologyServiceImplTest.NUMBER_OF_RECORDS + traitGroups.size());
	}

	@Test
	public void testGetAllRoles() throws MiddlewareQueryException {
		List<Term> roles = this.ontologyService.getAllRoles();
		Assert.assertFalse(roles.isEmpty());
		for (Term role : roles) {
			Debug.println(IntegrationTestBase.INDENT, "---");
			role.print(IntegrationTestBase.INDENT);
		}
		Debug.println(IntegrationTestBase.INDENT, OntologyServiceImplTest.NUMBER_OF_RECORDS + roles.size());
	}

	@Test
	public void testAddOrUpdateTraitClass() throws Exception {
		String name = "NEW trait class";
		String definition = "New trait class description " + (int) (Math.random() * 100);
		TraitClass newTraitClass =
				this.ontologyService.addOrUpdateTraitClass(name, definition, OntologyServiceImplTest.AGRONOMIC_TRAIT_CLASS);
		Debug.println(IntegrationTestBase.INDENT, "Updated :  " + newTraitClass);
		Assert.assertTrue(newTraitClass.getDefinition().equals(definition));
	}

	@Test
	public void testUpdateTraitClassInCentral() throws Exception {
		String newValue = "Agronomic New";
		try {
			TraitClass origTraitClass =
					new TraitClass(this.ontologyService.getTermById(OntologyServiceImplTest.AGRONOMIC_TRAIT_CLASS),
							this.ontologyService.getTermById(TermId.ONTOLOGY_TRAIT_CLASS.getId()));
			this.ontologyService.updateTraitClass(new TraitClass(new Term(origTraitClass.getId(), newValue, newValue), origTraitClass
					.getIsA()));
		} catch (MiddlewareException e) {
			Debug.println(IntegrationTestBase.INDENT, "MiddlewareException expected: \"" + e.getMessage() + "\"");
			Assert.assertTrue(e.getMessage().contains("Cannot update terms in central"));
		}
	}

	@Test
	public void testUpdateTraitClassNotInCentral() throws Exception {
		String name = "UPDATE trait class";
		String definition = "UPDATE trait class description " + (int) (Math.random() * 100);
		TraitClass origTraitClass = this.ontologyService.addTraitClass(name, definition, OntologyServiceImplTest.AGRONOMIC_TRAIT_CLASS);

		if (origTraitClass != null) {
			TraitClass newTraitClass =
					this.ontologyService.updateTraitClass(new TraitClass(new Term(origTraitClass.getId(), name, definition), origTraitClass
							.getIsA()));
			Debug.println(IntegrationTestBase.INDENT, "Original:  " + origTraitClass);
			Debug.println(IntegrationTestBase.INDENT, "Updated :  " + newTraitClass);
			Assert.assertTrue(newTraitClass.getDefinition().equals(definition));
		}
	}

	@Test
	public void testDeleteTraitClass() throws Exception {
		String name = "Test Trait Class " + new Random().nextInt(10000);
		String definition = "Test Definition";

		Term term = this.ontologyService.addTraitClass(name, definition, TermId.ONTOLOGY_TRAIT_CLASS.getId()).getTerm();
		this.ontologyService.deleteTraitClass(term.getId());

		term = this.ontologyService.getTermById(term.getId());
		Assert.assertNull(term);
	}

	@Test
	public void testDeleteProperty() throws Exception {
		String name = "Test Property" + new Random().nextInt(10000);
		String definition = "Property Definition";
		int isA = 1087;

		Property property = this.ontologyService.addProperty(name, definition, isA);
		this.ontologyService.deleteProperty(property.getId(), isA);

		Term term = this.ontologyService.getTermById(property.getId());
		Assert.assertNull(term);
	}

	@Test
	public void testDeleteMethod() throws Exception {
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";

		// add a method, should allow insert
		Method method = this.ontologyService.addMethod(name, definition);

		this.ontologyService.deleteMethod(method.getId());

		// check if value does not exist anymore
		Term term = this.ontologyService.getTermById(method.getId());
		Assert.assertNull(term);
	}

	@Test
	public void testDeleteScale() throws Exception {
		String name = "Test Scale " + new Random().nextInt(10000);
		String definition = "Test Definition";

		Scale scale = this.ontologyService.addScale(name, definition);

		this.ontologyService.deleteScale(scale.getId());

		// check if value does not exist anymore
		Term term = this.ontologyService.getTermById(scale.getId());
		Assert.assertNull(term);
	}

	@Test
	public void testValidateDeleteStandardVariableEnumeration() throws Exception {

		int standardVariableId = TermId.CHECK.getId();
		int enumerationId = -2;

		boolean found = this.ontologyService.validateDeleteStandardVariableEnumeration(standardVariableId, enumerationId);
		Debug.println(IntegrationTestBase.INDENT, "testValidateDeleteStandardVariableEnumeration " + found);
	}

	@Test
	public void testGetAllInventoryScales() throws Exception {
		List<Scale> scales = this.ontologyService.getAllInventoryScales();
		if (scales != null) {
			System.out.println("INVENTORY SCALES = ");
			for (Scale scale : scales) {
				System.out.println(scale);
			}
		}
	}
}
