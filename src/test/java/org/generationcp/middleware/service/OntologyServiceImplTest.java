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

import org.generationcp.middleware.IntegrationTestBase;
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
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OntologyServiceImplTest extends IntegrationTestBase {

	private static final String NUMBER_OF_RECORDS = " #RECORDS: ";
	private static final int AGRONOMIC_TRAIT_CLASS = 1340;

	private static final int NUMERIC_VARIABLE = 1;
	private static final int CHARACTER_VARIABLE = 2;
	private static final int CATEGORICAL_VARIABLE = 3;

	@Autowired
	private OntologyService ontologyService;

	@Test
	public void testGetStandardVariableById() throws MiddlewareQueryException {
		StandardVariable var = this.ontologyService.getStandardVariable(8005);
		Assert.assertNotNull(var);
		var.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testGetStandardVariables() throws MiddlewareQueryException {
		this.ontologyService.getStandardVariables("CUAN_75DAG");

		// TODO : fix hardcoded data assertion
		/* assertFalse(vars.isEmpty()); */
		/*
		 * for (StandardVariable var : vars){ var.print(INDENT); }
		 */
	}

	@Test
	public void testGetNumericStandardVariableWithMinMax() throws MiddlewareQueryException {
		StandardVariable var = this.ontologyService.getStandardVariable(8270);
		Assert.assertNotNull(var);
		Assert.assertNotNull(var.getConstraints());
		Assert.assertNotNull(var.getConstraints().getMinValueId());
		var.print(IntegrationTestBase.INDENT);
	}

	@Test
	public void testGetStandardVariablesByTraitClass() throws MiddlewareQueryException {
		List<StandardVariable> vars = this.ontologyService.getStandardVariablesByTraitClass(Integer.valueOf(1410));
		Assert.assertFalse(vars.isEmpty());
		for (StandardVariable var : vars) {
			Debug.println(IntegrationTestBase.INDENT, var.toString());
		}
	}

	@Test
	public void testGetStandardVariablesByProperty() throws MiddlewareQueryException {
		this.ontologyService.getStandardVariablesByProperty(Integer.valueOf(20109));

		// TODO fix hardcoded data assertion
		/*
		 * assertFalse(vars.isEmpty()); // stdvarid = 20961 for (StandardVariable var : vars){ Debug.println(INDENT, var.toString()); }
		 */
	}

	@Test
	public void testGetStandardVariablesByMethod() throws MiddlewareQueryException {
		this.ontologyService.getStandardVariablesByMethod(Integer.valueOf(20643));

		// TODO fix hardcoded data assertion
		/*
		 * assertFalse(vars.isEmpty()); for (StandardVariable var : vars){ Debug.println(INDENT, var.toString()); }
		 */
	}

	@Test
	public void testGetStandardVariablesByScale() throws MiddlewareQueryException {
		this.ontologyService.getStandardVariablesByScale(Integer.valueOf(20392));

		// TODO fix hardcoded data assertion
		/*
		 * assertFalse(vars.isEmpty()); for (StandardVariable var : vars){ Debug.println(INDENT, var.toString()); }
		 */
	}

	@Test
	public void testAddStandardVariable() throws MiddlewareQueryException {
		this.createNewStandardVariable(OntologyServiceImplTest.CHARACTER_VARIABLE);
	}

	@Test
	public void testDeleteStandardVariable() throws MiddlewareQueryException {
		StandardVariable stdVar = this.createNewStandardVariable(OntologyServiceImplTest.CHARACTER_VARIABLE);

		this.ontologyService.deleteStandardVariable(stdVar.getId());
		Term term = this.ontologyService.getTermById(stdVar.getId());

		Assert.assertNull(term);
	}

	@Test
	public void testAddOrUpdateStandardVariableMinMaxConstraintsInCentral() throws Exception {
		int standardVariableId = 8270; // numeric variable
		VariableConstraints constraints = new VariableConstraints(-100.0, 100.0);
		try {
			this.ontologyService.addOrUpdateStandardVariableMinMaxConstraints(standardVariableId, constraints);
		} catch (MiddlewareException e) {
			Debug.println(IntegrationTestBase.INDENT, "MiddlewareException expected: \n\t" + e.getMessage());
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
			this.ontologyService.addOrUpdateStandardVariableMinMaxConstraints(standardVariableId, constraints);
			constraints.print(IntegrationTestBase.INDENT);
			Assert.assertNotNull(constraints);
			Assert.assertNotNull(constraints.getMinValueId());
			Assert.assertNotNull(constraints.getMaxValueId());

			// cleanup
			this.ontologyService.deleteStandardVariable(standardVariableId);
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
		this.ontologyService.deleteStandardVariableMinMaxConstraints(standardVariableId);
		Assert.assertNull(this.ontologyService.getStandardVariable(standardVariableId).getConstraints());

		// cleanup
		this.ontologyService.deleteStandardVariable(standardVariableId);
	}

	@Test
	public void testAddGetDeleteStandardVariableValidValue() throws MiddlewareQueryException, MiddlewareException {
		int standardVariableId = 22550; // categorical variable
		String validValueLabel = "8";
		String validValueDescription = "Majority of plants severely stunted";

		StandardVariable standardVariable = this.ontologyService.getStandardVariable(standardVariableId);
		Enumeration validValue = new Enumeration(null, validValueLabel, validValueDescription, 8);

		Debug.println(IntegrationTestBase.INDENT, "BEFORE ADD: ");
		standardVariable.print(IntegrationTestBase.INDENT * 2);

		this.ontologyService.addStandardVariableValidValue(standardVariable, validValue);

		Debug.println(IntegrationTestBase.INDENT, "AFTER ADD: ");
		standardVariable = this.ontologyService.getStandardVariable(standardVariableId);
		standardVariable.print(IntegrationTestBase.INDENT * 2);

		// Assertion for successful add
		Assert.assertNotNull(standardVariable.getEnumeration(validValueLabel, validValueDescription));

		// Test delete
		validValue = standardVariable.getEnumeration(validValueLabel, validValueDescription);
		Assert.assertTrue(validValue != null && validValue.getId() != null);

		this.ontologyService.deleteStandardVariableValidValue(standardVariableId, validValue.getId());

		Debug.println(IntegrationTestBase.INDENT, "AFTER DELETE: ");
		standardVariable = this.ontologyService.getStandardVariable(standardVariableId);
		standardVariable.print(IntegrationTestBase.INDENT * 2);
		Assert.assertNull(standardVariable.getEnumeration(validValueLabel, validValueDescription));

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
	public void testAddOrUpdateProperty() throws Exception {
		String name = "NEW property";
		String definition = "New property description " + (int) (Math.random() * 100);
		Property origProperty = this.ontologyService.getProperty(name);
		Property newProperty =
				this.ontologyService.addOrUpdateProperty(name, definition, OntologyServiceImplTest.AGRONOMIC_TRAIT_CLASS, null);

		Debug.println(IntegrationTestBase.INDENT, "Original:  " + origProperty);
		Debug.println(IntegrationTestBase.INDENT, "Updated :  " + newProperty);

		if (origProperty != null) { // if the operation is update, the ids must be same
			Assert.assertSame(origProperty.getId(), newProperty.getId());
		}
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
	public void testAddOrUpdateScale() throws Exception {
		String name = "NEW scale";
		String definition = "New scale description " + (int) (Math.random() * 100);
		Scale origScale = this.ontologyService.getScale(name);
		Scale newScale = this.ontologyService.addOrUpdateScale(name, definition);

		Debug.println(IntegrationTestBase.INDENT, "Original:  " + origScale);
		Debug.println(IntegrationTestBase.INDENT, "Updated :  " + newScale);

		if (origScale != null) { // if the operation is update, the ids must be same
			Assert.assertSame(origScale.getId(), newScale.getId());
		}
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
	public void testAddOrUpdateMethod() throws Exception {
		String name = "NEW method";
		String definition = "New method description " + (int) (Math.random() * 100);
		Method origMethod = this.ontologyService.getMethod(name);
		Method newMethod = this.ontologyService.addOrUpdateMethod(name, definition);

		Debug.println(IntegrationTestBase.INDENT, "Original:  " + origMethod);
		Debug.println(IntegrationTestBase.INDENT, "Updated :  " + newMethod);

		if (origMethod != null) { // if the operation is update, the ids must be same
			Assert.assertSame(origMethod.getId(), newMethod.getId());
		}
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
	public void testAddTraitClass() throws MiddlewareQueryException {
		String name = "Test Trait Class " + new Random().nextInt(10000);
		String definition = "Test Definition";

		TraitClass traitClass = this.ontologyService.addTraitClass(name, definition, TermId.ONTOLOGY_TRAIT_CLASS.getId());
		Assert.assertNotNull(traitClass);
		Assert.assertTrue(traitClass.getId() < 0);
		traitClass.print(IntegrationTestBase.INDENT);

		this.ontologyService.deleteTraitClass(traitClass.getId());
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

	private StandardVariable createNewStandardVariable(int dataType) throws MiddlewareQueryException {
		String propertyName = "property name " + new Random().nextInt(10000);
		this.ontologyService.addProperty(propertyName, "test property", 1087);

		StandardVariable stdVariable = new StandardVariable();
		stdVariable.setName("variable name " + new Random().nextInt(10000));
		stdVariable.setDescription("variable description");
		stdVariable.setProperty(this.ontologyService.findTermByName(propertyName, CvId.PROPERTIES));
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

		this.ontologyService.addStandardVariable(stdVariable);

		Debug.println(IntegrationTestBase.INDENT, "Standard variable saved: " + stdVariable.getId());

		return stdVariable;
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
