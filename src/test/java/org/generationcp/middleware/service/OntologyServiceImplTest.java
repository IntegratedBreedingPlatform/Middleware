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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.dms.Enumeration;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableConstraints;
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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class OntologyServiceImplTest extends DataManagerIntegrationTest {

	private static final String NUMBER_OF_RECORDS = " #RECORDS: ";
	private static final int AGRONOMIC_TRAIT_CLASS = 1340;

	private static final int NUMERIC_VARIABLE = 1;
	private static final int CHARACTER_VARIABLE = 2;
	private static final int CATEGORICAL_VARIABLE = 3;

	private static OntologyService ontologyService;

	@BeforeClass
	public static void setUp() throws Exception {
		OntologyServiceImplTest.ontologyService = DataManagerIntegrationTest.managerFactory.getOntologyService();
	}

	@Test
	public void testGetStandardVariableById() throws MiddlewareQueryException {
		StandardVariable var = OntologyServiceImplTest.ontologyService.getStandardVariable(8005);
		Assert.assertNotNull(var);
		var.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetStandardVariables() throws MiddlewareQueryException {
		List<StandardVariable> vars = OntologyServiceImplTest.ontologyService.getStandardVariables("CUAN_75DAG");

		// TODO : fix hardcoded data assertion
		/* assertFalse(vars.isEmpty()); */
		/*
		 * for (StandardVariable var : vars){ var.print(INDENT); }
		 */
	}

	@Test
	public void testGetNumericStandardVariableWithMinMax() throws MiddlewareQueryException {
		StandardVariable var = OntologyServiceImplTest.ontologyService.getStandardVariable(8270);
		Assert.assertNotNull(var);
		Assert.assertNotNull(var.getConstraints());
		Assert.assertNotNull(var.getConstraints().getMinValueId());
		var.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetStandardVariablesByTraitClass() throws MiddlewareQueryException {
		List<StandardVariable> vars = OntologyServiceImplTest.ontologyService.getStandardVariablesByTraitClass(Integer.valueOf(1410));
		Assert.assertFalse(vars.isEmpty());
		for (StandardVariable var : vars) {
			Debug.println(MiddlewareIntegrationTest.INDENT, var.toString());
		}
	}

	@Test
	public void testGetStandardVariablesByProperty() throws MiddlewareQueryException {
		List<StandardVariable> vars = OntologyServiceImplTest.ontologyService.getStandardVariablesByProperty(Integer.valueOf(20109));

		// TODO fix hardcoded data assertion
		/*
		 * assertFalse(vars.isEmpty()); // stdvarid = 20961 for (StandardVariable var : vars){ Debug.println(INDENT, var.toString()); }
		 */
	}

	@Test
	public void testGetStandardVariablesByMethod() throws MiddlewareQueryException {
		List<StandardVariable> vars = OntologyServiceImplTest.ontologyService.getStandardVariablesByMethod(Integer.valueOf(20643));

		// TODO fix hardcoded data assertion
		/*
		 * assertFalse(vars.isEmpty()); for (StandardVariable var : vars){ Debug.println(INDENT, var.toString()); }
		 */
	}

	@Test
	public void testGetStandardVariablesByScale() throws MiddlewareQueryException {
		List<StandardVariable> vars = OntologyServiceImplTest.ontologyService.getStandardVariablesByScale(Integer.valueOf(20392));

		// TODO fix hardcoded data assertion
		/*
		 * assertFalse(vars.isEmpty()); for (StandardVariable var : vars){ Debug.println(INDENT, var.toString()); }
		 */
	}

	@Test
	public void testAddStandardVariable() throws MiddlewareQueryException {
		StandardVariable stdVar = this.createNewStandardVariable(OntologyServiceImplTest.CHARACTER_VARIABLE);
	}

	@Test
	public void testDeleteStandardVariable() throws MiddlewareQueryException {
		StandardVariable stdVar = this.createNewStandardVariable(OntologyServiceImplTest.CHARACTER_VARIABLE);

		OntologyServiceImplTest.ontologyService.deleteStandardVariable(stdVar.getId());
		Term term = OntologyServiceImplTest.ontologyService.getTermById(stdVar.getId());

		Assert.assertNull(term);
	}

	@Test
	public void testAddOrUpdateStandardVariableMinMaxConstraintsInCentral() throws Exception {
		int standardVariableId = 8270; // numeric variable
		VariableConstraints constraints = new VariableConstraints(-100.0, 100.0);
		try {
			OntologyServiceImplTest.ontologyService.addOrUpdateStandardVariableMinMaxConstraints(standardVariableId, constraints);
		} catch (MiddlewareException e) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "MiddlewareException expected: \n\t" + e.getMessage());
			Assert.assertTrue(e.getMessage().contains("Cannot update the constraints of standard variables from Central database."));
		} catch (Exception e) {
			if (!e.getMessage().contains("Error in getNegativeId")) {
				throw e;
			} // else, ignore. entry already exists
		}
	}

	@Test
	public void testAddOrUpdateStandardVariableMinMaxConstraintsInLocal() throws Exception {
		try {
			StandardVariable variable = this.createNewStandardVariable(OntologyServiceImplTest.NUMERIC_VARIABLE);
			int standardVariableId = variable.getId();
			VariableConstraints constraints = new VariableConstraints(-100.0, 100.0);
			OntologyServiceImplTest.ontologyService.addOrUpdateStandardVariableMinMaxConstraints(standardVariableId, constraints);
			constraints.print(MiddlewareIntegrationTest.INDENT);
			Assert.assertNotNull(constraints);
			Assert.assertNotNull(constraints.getMinValueId());
			Assert.assertNotNull(constraints.getMaxValueId());

			// cleanup
			OntologyServiceImplTest.ontologyService.deleteStandardVariable(standardVariableId);
		} catch (Exception e) {
			if (!e.getMessage().contains("Error in getNegativeId")) {
				throw e;
			} // else, ignore. entry already exists
		}
	}

	@Test
	public void testDeleteStandardVariableMinMaxConstraints() throws MiddlewareQueryException {
		StandardVariable variable = this.createNewStandardVariable(OntologyServiceImplTest.NUMERIC_VARIABLE);
		int standardVariableId = variable.getId();
		Assert.assertNotNull(variable.getConstraints().getMinValueId());
		OntologyServiceImplTest.ontologyService.deleteStandardVariableMinMaxConstraints(standardVariableId);
		Assert.assertNull(OntologyServiceImplTest.ontologyService.getStandardVariable(standardVariableId).getConstraints());

		// cleanup
		OntologyServiceImplTest.ontologyService.deleteStandardVariable(standardVariableId);
	}

	@Test
	public void testAddGetDeleteStandardVariableValidValue() throws MiddlewareQueryException, MiddlewareException {
		int standardVariableId = 22550; // categorical variable
		String validValueLabel = "8";
		String validValueDescription = "Majority of plants severely stunted";

		StandardVariable standardVariable = OntologyServiceImplTest.ontologyService.getStandardVariable(standardVariableId);
		Enumeration validValue = new Enumeration(null, validValueLabel, validValueDescription, 8);

		Debug.println(MiddlewareIntegrationTest.INDENT, "BEFORE ADD: ");
		standardVariable.print(MiddlewareIntegrationTest.INDENT * 2);

		OntologyServiceImplTest.ontologyService.addStandardVariableValidValue(standardVariable, validValue);

		Debug.println(MiddlewareIntegrationTest.INDENT, "AFTER ADD: ");
		standardVariable = OntologyServiceImplTest.ontologyService.getStandardVariable(standardVariableId);
		standardVariable.print(MiddlewareIntegrationTest.INDENT * 2);

		// Assertion for successful add
		Assert.assertNotNull(standardVariable.getEnumeration(validValueLabel, validValueDescription));

		// Test delete
		validValue = standardVariable.getEnumeration(validValueLabel, validValueDescription);
		Assert.assertTrue(validValue != null && validValue.getId() != null);

		OntologyServiceImplTest.ontologyService.deleteStandardVariableValidValue(standardVariableId, validValue.getId());

		Debug.println(MiddlewareIntegrationTest.INDENT, "AFTER DELETE: ");
		standardVariable = OntologyServiceImplTest.ontologyService.getStandardVariable(standardVariableId);
		standardVariable.print(MiddlewareIntegrationTest.INDENT * 2);
		Assert.assertNull(standardVariable.getEnumeration(validValueLabel, validValueDescription));

	}

	@Test
	public void testGetAllTermsByCvId() throws MiddlewareQueryException {
		List<Term> terms = OntologyServiceImplTest.ontologyService.getAllTermsByCvId(CvId.VARIABLES);
		for (Term term : terms) {
			term.print(MiddlewareIntegrationTest.INDENT);
		}
	}

	/* ======================= PROPERTY ================================== */

	@Test
	public void testGetPropertyById() throws MiddlewareQueryException {
		Property property = OntologyServiceImplTest.ontologyService.getProperty(2000);
		Assert.assertNotNull(property);
		property.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetPropertyByName() throws MiddlewareQueryException {
		Property property = OntologyServiceImplTest.ontologyService.getProperty("Dataset");
		Assert.assertNotNull(property);
		property.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetAllProperties() throws MiddlewareQueryException {
		List<Property> properties = OntologyServiceImplTest.ontologyService.getAllProperties();
		Assert.assertFalse(properties.isEmpty());
		for (Property property : properties) {
			property.print(MiddlewareIntegrationTest.INDENT);
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, OntologyServiceImplTest.NUMBER_OF_RECORDS + properties.size());
	}

	@Test
	public void testAddProperty() throws MiddlewareQueryException {
		Property property =
				OntologyServiceImplTest.ontologyService.addProperty("NEW property", "New property description",
						OntologyServiceImplTest.AGRONOMIC_TRAIT_CLASS);
		property.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testAddOrUpdateProperty() throws Exception {
		String name = "NEW property";
		String definition = "New property description " + (int) (Math.random() * 100);
		Property origProperty = OntologyServiceImplTest.ontologyService.getProperty(name);
		Property newProperty =
				OntologyServiceImplTest.ontologyService.addOrUpdateProperty(name, definition,
						OntologyServiceImplTest.AGRONOMIC_TRAIT_CLASS, null);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Original:  " + origProperty);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Updated :  " + newProperty);

		if (origProperty != null) { // if the operation is update, the ids must be same
			Assert.assertSame(origProperty.getId(), newProperty.getId());
		}
	}

	@Test
	public void testUpdateProperty() throws Exception {
		String name = "UPDATE property";
		String definition = "Update property description " + (int) (Math.random() * 100);
		Property origProperty = OntologyServiceImplTest.ontologyService.getProperty(name);
		if (origProperty == null || origProperty.getTerm() == null) { // first run, add before update
			origProperty =
					OntologyServiceImplTest.ontologyService.addProperty(name, definition, OntologyServiceImplTest.AGRONOMIC_TRAIT_CLASS);
		}
		OntologyServiceImplTest.ontologyService.updateProperty(new Property(new Term(origProperty.getId(), name, definition), origProperty
				.getIsA()));
		Property newProperty = OntologyServiceImplTest.ontologyService.getProperty(name);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Original:  " + origProperty);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Updated :  " + newProperty);
		Assert.assertTrue(newProperty.getDefinition().equals(definition));
	}

	/* ======================= SCALE ================================== */

	@Test
	public void testGetScaleById() throws MiddlewareQueryException {
		Scale scale = OntologyServiceImplTest.ontologyService.getScale(6030);
		Assert.assertNotNull(scale);
		scale.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetScaleByName() throws MiddlewareQueryException {
		Scale scale = OntologyServiceImplTest.ontologyService.getScale("Calculated");
		Assert.assertNotNull(scale);
		scale.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetAllScales() throws MiddlewareQueryException {
		List<Scale> scales = OntologyServiceImplTest.ontologyService.getAllScales();
		Assert.assertFalse(scales.isEmpty());
		for (Scale scale : scales) {
			scale.print(MiddlewareIntegrationTest.INDENT);
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, OntologyServiceImplTest.NUMBER_OF_RECORDS + scales.size());
	}

	@Test
	public void testAddScale() throws MiddlewareQueryException {
		Scale scale = OntologyServiceImplTest.ontologyService.addScale("NEW scale", "New scale description");
		scale.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testAddOrUpdateScale() throws Exception {
		String name = "NEW scale";
		String definition = "New scale description " + (int) (Math.random() * 100);
		Scale origScale = OntologyServiceImplTest.ontologyService.getScale(name);
		Scale newScale = OntologyServiceImplTest.ontologyService.addOrUpdateScale(name, definition);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Original:  " + origScale);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Updated :  " + newScale);

		if (origScale != null) { // if the operation is update, the ids must be same
			Assert.assertSame(origScale.getId(), newScale.getId());
		}
	}

	@Test
	public void testUpdateScale() throws Exception {
		String name = "UPDATE scale";
		String definition = "Update scale description " + (int) (Math.random() * 100);
		Scale origScale = OntologyServiceImplTest.ontologyService.getScale(name);
		if (origScale == null || origScale.getTerm() == null) { // first run, add before update
			origScale = OntologyServiceImplTest.ontologyService.addScale(name, definition);
		}
		OntologyServiceImplTest.ontologyService.updateScale(new Scale(new Term(origScale.getId(), name, definition)));
		Scale newScale = OntologyServiceImplTest.ontologyService.getScale(name);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Original:  " + origScale);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Updated :  " + newScale);
		Assert.assertTrue(newScale.getDefinition().equals(definition));
	}

	/* ======================= METHOD ================================== */

	@Test
	public void testGetMethodById() throws MiddlewareQueryException {
		Method method = OntologyServiceImplTest.ontologyService.getMethod(4030);
		Assert.assertNotNull(method);
		method.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetMethodByName() throws MiddlewareQueryException {
		Method method = OntologyServiceImplTest.ontologyService.getMethod("Enumerated");
		Assert.assertNotNull(method);
		method.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testGetAllMethods() throws MiddlewareQueryException {
		List<Method> methods = OntologyServiceImplTest.ontologyService.getAllMethods();
		Assert.assertFalse(methods.isEmpty());
		for (Method method : methods) {
			method.print(MiddlewareIntegrationTest.INDENT);
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, OntologyServiceImplTest.NUMBER_OF_RECORDS + methods.size());
	}

	@Test
	public void testAddMethod() throws MiddlewareQueryException {
		Method method = OntologyServiceImplTest.ontologyService.addMethod("NEW method", "New method description");
		method.print(MiddlewareIntegrationTest.INDENT);
	}

	@Test
	public void testAddOrUpdateMethod() throws Exception {
		String name = "NEW method";
		String definition = "New method description " + (int) (Math.random() * 100);
		Method origMethod = OntologyServiceImplTest.ontologyService.getMethod(name);
		Method newMethod = OntologyServiceImplTest.ontologyService.addOrUpdateMethod(name, definition);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Original:  " + origMethod);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Updated :  " + newMethod);

		if (origMethod != null) { // if the operation is update, the ids must be same
			Assert.assertSame(origMethod.getId(), newMethod.getId());
		}
	}

	@Test
	public void testUpdateMethod() throws Exception {
		String name = "UPDATE method";
		String definition = "Update method description " + (int) (Math.random() * 100);
		Method origMethod = OntologyServiceImplTest.ontologyService.getMethod(name);
		if (origMethod == null || origMethod.getTerm() == null) { // first run, add before update
			origMethod = OntologyServiceImplTest.ontologyService.addMethod(name, definition);
		}
		OntologyServiceImplTest.ontologyService.updateMethod(new Method(new Term(origMethod.getId(), name, definition)));
		Method newMethod = OntologyServiceImplTest.ontologyService.getMethod(name);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Original:  " + origMethod);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Updated :  " + newMethod);
		Assert.assertTrue(newMethod.getDefinition().equals(definition));
	}

	/* ======================= OTHERS ================================== */

	@Test
	public void testGetDataTypes() throws MiddlewareQueryException {
		List<Term> dataTypes = OntologyServiceImplTest.ontologyService.getAllDataTypes();
		Assert.assertFalse(dataTypes.isEmpty());
		for (Term dataType : dataTypes) {
			dataType.print(MiddlewareIntegrationTest.INDENT);
		}
	}

	@Test
	public void testGetAllTraitGroupsHierarchy() throws MiddlewareQueryException {
		List<TraitClassReference> traitGroups = OntologyServiceImplTest.ontologyService.getAllTraitGroupsHierarchy(true);
		Assert.assertFalse(traitGroups.isEmpty());
		for (TraitClassReference traitGroup : traitGroups) {
			traitGroup.print(MiddlewareIntegrationTest.INDENT);
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, OntologyServiceImplTest.NUMBER_OF_RECORDS + traitGroups.size());
	}

	@Test
	public void testGetAllRoles() throws MiddlewareQueryException {
		List<Term> roles = OntologyServiceImplTest.ontologyService.getAllRoles();
		Assert.assertFalse(roles.isEmpty());
		for (Term role : roles) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "---");
			role.print(MiddlewareIntegrationTest.INDENT);
		}
		Debug.println(MiddlewareIntegrationTest.INDENT, OntologyServiceImplTest.NUMBER_OF_RECORDS + roles.size());
	}

	@Test
	public void testAddTraitClass() throws MiddlewareQueryException {
		String name = "Test Trait Class " + new Random().nextInt(10000);
		String definition = "Test Definition";

		TraitClass traitClass =
				OntologyServiceImplTest.ontologyService.addTraitClass(name, definition, TermId.ONTOLOGY_TRAIT_CLASS.getId());
		Assert.assertNotNull(traitClass);
		Assert.assertTrue(traitClass.getId() < 0);
		traitClass.print(MiddlewareIntegrationTest.INDENT);

		OntologyServiceImplTest.ontologyService.deleteTraitClass(traitClass.getId());
	}

	@Test
	public void testAddOrUpdateTraitClass() throws Exception {
		String name = "NEW trait class";
		String definition = "New trait class description " + (int) (Math.random() * 100);
		TraitClass newTraitClass =
				OntologyServiceImplTest.ontologyService.addOrUpdateTraitClass(name, definition,
						OntologyServiceImplTest.AGRONOMIC_TRAIT_CLASS);
		Debug.println(MiddlewareIntegrationTest.INDENT, "Updated :  " + newTraitClass);
		Assert.assertTrue(newTraitClass.getDefinition().equals(definition));
	}

	@Test
	public void testUpdateTraitClassInCentral() throws Exception {
		String newValue = "Agronomic New";
		try {
			TraitClass origTraitClass =
					new TraitClass(OntologyServiceImplTest.ontologyService.getTermById(OntologyServiceImplTest.AGRONOMIC_TRAIT_CLASS),
							OntologyServiceImplTest.ontologyService.getTermById(TermId.ONTOLOGY_TRAIT_CLASS.getId()));
			OntologyServiceImplTest.ontologyService.updateTraitClass(new TraitClass(new Term(origTraitClass.getId(), newValue, newValue),
					origTraitClass.getIsA()));
		} catch (MiddlewareException e) {
			Debug.println(MiddlewareIntegrationTest.INDENT, "MiddlewareException expected: \"" + e.getMessage() + "\"");
			Assert.assertTrue(e.getMessage().contains("Cannot update terms in central"));
		}
	}

	@Test
	public void testUpdateTraitClassNotInCentral() throws Exception {
		String name = "UPDATE trait class";
		String definition = "UPDATE trait class description " + (int) (Math.random() * 100);
		TraitClass origTraitClass =
				OntologyServiceImplTest.ontologyService.addTraitClass(name, definition, OntologyServiceImplTest.AGRONOMIC_TRAIT_CLASS);

		if (origTraitClass != null) {
			TraitClass newTraitClass =
					OntologyServiceImplTest.ontologyService.updateTraitClass(new TraitClass(new Term(origTraitClass.getId(), name,
							definition), origTraitClass.getIsA()));
			Debug.println(MiddlewareIntegrationTest.INDENT, "Original:  " + origTraitClass);
			Debug.println(MiddlewareIntegrationTest.INDENT, "Updated :  " + newTraitClass);
			Assert.assertTrue(newTraitClass.getDefinition().equals(definition));
		}
	}

	@Test
	public void testDeleteTraitClass() throws Exception {
		String name = "Test Trait Class " + new Random().nextInt(10000);
		String definition = "Test Definition";

		Term term = OntologyServiceImplTest.ontologyService.addTraitClass(name, definition, TermId.ONTOLOGY_TRAIT_CLASS.getId()).getTerm();
		OntologyServiceImplTest.ontologyService.deleteTraitClass(term.getId());

		term = OntologyServiceImplTest.ontologyService.getTermById(term.getId());
		Assert.assertNull(term);
	}

	@Test
	public void testDeleteProperty() throws Exception {
		String name = "Test Property" + new Random().nextInt(10000);
		String definition = "Property Definition";
		int isA = 1087;

		Property property = OntologyServiceImplTest.ontologyService.addProperty(name, definition, isA);
		OntologyServiceImplTest.ontologyService.deleteProperty(property.getId(), isA);

		Term term = OntologyServiceImplTest.ontologyService.getTermById(property.getId());
		Assert.assertNull(term);
	}

	@Test
	public void testDeleteMethod() throws Exception {
		String name = "Test Method " + new Random().nextInt(10000);
		String definition = "Test Definition";

		// add a method, should allow insert
		Method method = OntologyServiceImplTest.ontologyService.addMethod(name, definition);

		OntologyServiceImplTest.ontologyService.deleteMethod(method.getId());

		// check if value does not exist anymore
		Term term = OntologyServiceImplTest.ontologyService.getTermById(method.getId());
		Assert.assertNull(term);
	}

	@Test
	public void testDeleteScale() throws Exception {
		String name = "Test Scale " + new Random().nextInt(10000);
		String definition = "Test Definition";

		Scale scale = OntologyServiceImplTest.ontologyService.addScale(name, definition);

		OntologyServiceImplTest.ontologyService.deleteScale(scale.getId());

		// check if value does not exist anymore
		Term term = OntologyServiceImplTest.ontologyService.getTermById(scale.getId());
		Assert.assertNull(term);
	}

	@Test
	public void testValidateDeleteStandardVariableEnumeration() throws Exception {

		int standardVariableId = TermId.CHECK.getId();
		int enumerationId = -2;

		boolean found =
				OntologyServiceImplTest.ontologyService.validateDeleteStandardVariableEnumeration(standardVariableId, enumerationId);
		Debug.println(MiddlewareIntegrationTest.INDENT, "testValidateDeleteStandardVariableEnumeration " + found);
	}

	private StandardVariable createNewStandardVariable(int dataType) throws MiddlewareQueryException {
		String propertyName = "property name " + new Random().nextInt(10000);
		OntologyServiceImplTest.ontologyService.addProperty(propertyName, "test property", 1087);

		StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("variable name " + new Random().nextInt(10000));
		stdVariable.setDescription("variable description");
		stdVariable.setProperty(OntologyServiceImplTest.ontologyService.findTermByName(propertyName, CvId.PROPERTIES));
		stdVariable.setMethod(new Term(4030, "Assigned", "Term, name or id assigned"));
		stdVariable.setScale(new Term(6000, "DBCV", "Controlled vocabulary from a database"));
		stdVariable.setStoredIn(new Term(1010, "Study information", "Study element"));

		switch (dataType) {
			case NUMERIC_VARIABLE:
				stdVariable.setDataType(new Term(1110, "Numeric variable", "Variable with numeric values either continuous or integer"));
				stdVariable.setConstraints(new VariableConstraints(100.0, 999.0));
				break;
			case CHARACTER_VARIABLE:
				stdVariable.setDataType(new Term(1120, "Character variable", "variable with char values"));
				break;
			case CATEGORICAL_VARIABLE:
				stdVariable.setDataType(new Term(1130, "Categorical variable",
						"Variable with discrete class values (numeric or character all treated as character)"));
				stdVariable.setEnumerations(new ArrayList<Enumeration>());
				stdVariable.getEnumerations().add(new Enumeration(10000, "N", "Nursery", 1));
				stdVariable.getEnumerations().add(new Enumeration(10001, "HB", "Hybridization nursery", 2));
				stdVariable.getEnumerations().add(new Enumeration(10002, "PN", "Pedigree nursery", 3));
				break;
		}

		stdVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));
		stdVariable.setCropOntologyId("CROP-TEST");

		OntologyServiceImplTest.ontologyService.addStandardVariable(stdVariable);

		Debug.println(MiddlewareIntegrationTest.INDENT, "Standard variable saved: " + stdVariable.getId());

		return stdVariable;
	}

	@Test
	public void testGetAllInventoryScales() throws Exception {
		List<Scale> scales = OntologyServiceImplTest.ontologyService.getAllInventoryScales();
		if (scales != null) {
			System.out.println("INVENTORY SCALES = ");
			for (Scale scale : scales) {
				System.out.println(scale);
			}
		}
	}
}
