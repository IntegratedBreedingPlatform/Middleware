/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 * <p/>
 * Generation Challenge Programme (GCP)
 * <p/>
 * <p/>
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *******************************************************************************/

package org.generationcp.middleware.operation.builder;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.VariableOverridesDao;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.ontology.OntologyDataHelper;
import org.generationcp.middleware.operation.saver.ProjectPropertySaver;
import org.generationcp.middleware.operation.saver.StandardVariableSaver;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StandardVariableBuilderTest extends IntegrationTestBase {

	private static final int TRIAL_INSTANCE_PROPERTY_ID = 2100;
	private static final int TRIAL_INSTANCE_SCALE_ID = 6040;
	private static final int TRIAL_INSTANCE_METHOD_ID = 4040;

	private StandardVariableBuilder standardVariableBuilder;
	private ProjectPropertySaver projectPropertySaver;
	private StandardVariableSaver standardVariableSaver;
	private CVTermDao cvTermDao;
	private VariableOverridesDao variableOverridesDao;

	@Before
	public void setUp() {
		this.standardVariableBuilder = new StandardVariableBuilder(this.sessionProvder);
		this.projectPropertySaver = new ProjectPropertySaver(this.sessionProvder);
		this.standardVariableSaver = new StandardVariableSaver(this.sessionProvder);
		this.cvTermDao = new CVTermDao(this.sessionProvder.getSession());
		this.variableOverridesDao = new VariableOverridesDao(this.sessionProvder.getSession());
	}

	@Test
	public void testCreate() {
		final StandardVariable standardVariable = standardVariableBuilder.create(TermId.TRIAL_INSTANCE_FACTOR.getId(), null);
		assertNotNull(standardVariable);
		assertEquals(TermId.TRIAL_INSTANCE_FACTOR.getId(), standardVariable.getId());
		assertEquals(TRIAL_INSTANCE_PROPERTY_ID, standardVariable.getProperty().getId());
		assertEquals(TRIAL_INSTANCE_SCALE_ID, standardVariable.getScale().getId());
		assertEquals(TRIAL_INSTANCE_METHOD_ID, standardVariable.getMethod().getId());
	}

	@Test
	public void testCreateObsoleteVariable() {
		final CVTermDao cvtermDao = new CVTermDao(this.sessionProvder.getSession());

		// set variable to obsolete
		final int id = TermId.TRIAL_INSTANCE_FACTOR.getId();
		final CVTerm variable = cvtermDao.getById(id);
		variable.setIsObsolete(true);
		cvtermDao.update(variable);

		final StandardVariable standardVariable = standardVariableBuilder.create(id, null);
		assertNotNull(standardVariable);
		assertEquals(TermId.TRIAL_INSTANCE_FACTOR.getId(), standardVariable.getId());
		assertEquals(TRIAL_INSTANCE_PROPERTY_ID, standardVariable.getProperty().getId());
		assertEquals(TRIAL_INSTANCE_SCALE_ID, standardVariable.getScale().getId());
		assertEquals(TRIAL_INSTANCE_METHOD_ID, standardVariable.getMethod().getId());

		// revert changes
		variable.setIsObsolete(false);
		cvtermDao.update(variable);
	}

	@Test
	public void testCreateList() {

		final List<Integer> standardVariableIds = new ArrayList<Integer>();
		standardVariableIds.add(TermId.ENTRY_NO.getId());
		standardVariableIds.add(TermId.GID.getId());

		final List<StandardVariable> standardVariables = standardVariableBuilder.create(standardVariableIds, null);
		assertNotNull(standardVariables);
		assertEquals(standardVariableIds.size(), standardVariables.size());
		for (final StandardVariable standardVariable : standardVariables) {
			assertTrue(standardVariable.getId() == TermId.ENTRY_NO.getId() || standardVariable.getId() == TermId.GID.getId());
			if (standardVariable.getId() == TermId.ENTRY_NO.getId()) {
				assertEquals(2200, standardVariable.getProperty().getId());
				assertEquals(6040, standardVariable.getScale().getId());
				assertEquals(4040, standardVariable.getMethod().getId());
			} else if (standardVariable.getId() == TermId.GID.getId()) {
				assertEquals(2205, standardVariable.getProperty().getId());
				assertEquals(1907, standardVariable.getScale().getId());
				assertEquals(4030, standardVariable.getMethod().getId());
			}
		}
	}

	@Test
	public void testCreateEmptyList() {
		final List<Integer> standardVariableIds = new ArrayList<Integer>();

		final List<StandardVariable> standardVariables = standardVariableBuilder.create(standardVariableIds, null);
		assertNotNull(standardVariables);
		assertEquals(0, standardVariables.size());
	}

	@Test
	public void testCreateNullList() {
		final List<Integer> standardVariableIds = null;

		final List<StandardVariable> standardVariables = standardVariableBuilder.create(standardVariableIds, null);
		assertNotNull(standardVariables);
		assertEquals(0, standardVariables.size());
	}

	@Test
	public void testFindOrSaveFindExisting() {
		final String name = "TRIAL_INSTANCE";
		final String description = "Trial instance - enumerated (number)";
		final String property = "Trial instance";
		final String scale = "Number";
		final String method = "Enumerated";
		final String datatype = "N";
		final PhenotypicType role = PhenotypicType.TRIAL_ENVIRONMENT;
		final StandardVariable standardVariable =
			standardVariableBuilder.findOrSave(name, description, property, scale, method, role, null, datatype, null);
		assertNotNull(standardVariable);
		assertEquals(TermId.TRIAL_INSTANCE_FACTOR.getId(), standardVariable.getId());
		assertEquals(TRIAL_INSTANCE_PROPERTY_ID, standardVariable.getProperty().getId());
		assertEquals(TRIAL_INSTANCE_SCALE_ID, standardVariable.getScale().getId());
		assertEquals(TRIAL_INSTANCE_METHOD_ID, standardVariable.getMethod().getId());
		assertEquals(name, standardVariable.getName());
		assertEquals(description, standardVariable.getDescription());
		assertEquals(property, standardVariable.getProperty().getName());
		assertEquals(scale, standardVariable.getScale().getName());
		assertEquals(method, standardVariable.getMethod().getName());
		assertEquals(role, standardVariable.getPhenotypicType());
		final VariableType variableType = OntologyDataHelper.mapFromPhenotype(role, property);
		assertEquals(variableType, standardVariable.getVariableTypes().iterator().next());
		assertEquals(TermId.NUMERIC_VARIABLE.getId(), standardVariable.getDataType().getId());
	}

	@Test
	public void testFindOrSaveSaveNewStandardVariable() {
		final String name = "Test Variable Name";
		final String description = "Test Variable Name Description";
		final String property = "Test Property";
		final String scale = "Test Scale";
		final String method = "Test Method";
		final String datatype = "N";
		final PhenotypicType role = PhenotypicType.TRIAL_ENVIRONMENT;
		final StandardVariable standardVariable =
			standardVariableBuilder.findOrSave(name, description, property, scale, method, role, null, datatype, null);
		assertNotNull(standardVariable);
		assertEquals(name, standardVariable.getName());
		assertEquals(description, standardVariable.getDescription());
		assertEquals(property, standardVariable.getProperty().getName());
		assertEquals(scale, standardVariable.getScale().getName());
		assertEquals(method, standardVariable.getMethod().getName());
		assertEquals(role, standardVariable.getPhenotypicType());
		final VariableType variableType = OntologyDataHelper.mapFromPhenotype(role, property);
		assertEquals(variableType, standardVariable.getVariableTypes().iterator().next());
		assertEquals(TermId.NUMERIC_VARIABLE.getId(), standardVariable.getDataType().getId());
	}

	@Test
	public void testGetByName() {
		final String name = "TRIAL_INSTANCE";
		final StandardVariable standardVariable = standardVariableBuilder.getByName(name, null);
		assertNotNull(standardVariable);
		assertEquals(TermId.TRIAL_INSTANCE_FACTOR.getId(), standardVariable.getId());
		assertEquals(name, standardVariable.getName());
		assertEquals(TRIAL_INSTANCE_PROPERTY_ID, standardVariable.getProperty().getId());
		assertEquals(TRIAL_INSTANCE_SCALE_ID, standardVariable.getScale().getId());
		assertEquals(TRIAL_INSTANCE_METHOD_ID, standardVariable.getMethod().getId());
	}

	@Test
	public void testGetByNameNotFound() {
		final String name = "VAR_123456";
		final StandardVariable standardVariable = standardVariableBuilder.getByName(name, null);
		assertNull(standardVariable);
	}

	@Test
	public void testGetByPropertyScaleMethod() {
		final int propertyId = TRIAL_INSTANCE_PROPERTY_ID;
		final int scaleId = TRIAL_INSTANCE_SCALE_ID;
		final int methodId = TRIAL_INSTANCE_METHOD_ID;
		final StandardVariable standardVariable = standardVariableBuilder.getByPropertyScaleMethod(propertyId, scaleId, methodId, null);
		assertNotNull(standardVariable);
		assertEquals(TRIAL_INSTANCE_PROPERTY_ID, standardVariable.getProperty().getId());
		assertEquals(TRIAL_INSTANCE_SCALE_ID, standardVariable.getScale().getId());
		assertEquals(TRIAL_INSTANCE_METHOD_ID, standardVariable.getMethod().getId());
	}

	@Test
	public void testGetByPropertyScaleMethodNotFound() {
		final int propertyId = 1;
		final int scaleId = 2;
		final int methodId = 3;
		final StandardVariable standardVariable = standardVariableBuilder.getByPropertyScaleMethod(propertyId, scaleId, methodId, null);
		assertNull(standardVariable);
	}

	@Test
	public void testGetByPropertyScaleMethodRole() {
		final int propertyId = TRIAL_INSTANCE_PROPERTY_ID;
		final int scaleId = TRIAL_INSTANCE_SCALE_ID;
		final int methodId = TRIAL_INSTANCE_METHOD_ID;
		final PhenotypicType role = PhenotypicType.TRIAL_ENVIRONMENT;
		final StandardVariable standardVariable =
			standardVariableBuilder.getByPropertyScaleMethodRole(propertyId, scaleId, methodId, role, null);
		assertNotNull(standardVariable);
		assertEquals(TRIAL_INSTANCE_PROPERTY_ID, standardVariable.getProperty().getId());
		assertEquals(TRIAL_INSTANCE_SCALE_ID, standardVariable.getScale().getId());
		assertEquals(TRIAL_INSTANCE_METHOD_ID, standardVariable.getMethod().getId());
	}

	@Test
	public void testGetByPropertyScaleMethodRoleNotFound() {
		final int propertyId = 1;
		final int scaleId = 2;
		final int methodId = 3;
		final PhenotypicType role = PhenotypicType.TRIAL_ENVIRONMENT;
		final StandardVariable standardVariable =
			standardVariableBuilder.getByPropertyScaleMethodRole(propertyId, scaleId, methodId, role, null);
		assertNull(standardVariable);
	}

	@Test
	public void testGetStandardVariablesInProjects_HeaderHasNoMatch() {

		final String headerNameToMatch = RandomStringUtils.randomAlphabetic(10);
		final List<String> headers = Arrays.asList(headerNameToMatch);

		final DmsProject dmsProject = this.createDMSProject();

		final Map<String, List<StandardVariable>> result =
			this.standardVariableBuilder.getStandardVariablesInProjects(headers, dmsProject.getProgramUUID());

		Assert.assertTrue(result.get(headerNameToMatch.toUpperCase()).isEmpty());

	}

	@Test
	public void testGetStandardVariablesInProjects_HeaderHasMatchInProjectProperty() {

		final String headerNameToMatch = RandomStringUtils.randomAlphabetic(10);
		final List<String> headers = Arrays.asList(headerNameToMatch);

		final DmsProject dmsProject = this.createDMSProject();
		final VariableTypeList variableTypeList = new VariableTypeList();
		variableTypeList.add(
			this.createDMSVariableType(headerNameToMatch, RandomStringUtils.randomAlphabetic(10), 1, PhenotypicType.VARIATE,
				VariableType.TRAIT));
		this.projectPropertySaver.saveProjectProperties(dmsProject, variableTypeList, null);

		final Map<String, List<StandardVariable>> result =
			this.standardVariableBuilder.getStandardVariablesInProjects(headers, dmsProject.getProgramUUID());

		Assert.assertEquals(headerNameToMatch, result.get(headerNameToMatch.toUpperCase()).iterator().next().getName());

	}

	@Test
	public void testGetStandardVariablesInProjects_HeaderHasMatchWithOntologyVariableAlias() {

		final String headerNameToMatch = RandomStringUtils.randomAlphabetic(10);
		final List<String> headers = Arrays.asList(headerNameToMatch);

		final DmsProject dmsProject = this.createDMSProject();

		// Create a standard variable with name different from header name, but has alias same as header name
		final StandardVariable standardVariable =
			this.createStandardVariable(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10),
				RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10));
		this.variableOverridesDao.save(standardVariable.getId(), dmsProject.getProgramUUID(), headerNameToMatch, null, null);

		final Map<String, List<StandardVariable>> result =
			this.standardVariableBuilder.getStandardVariablesInProjects(headers, dmsProject.getProgramUUID());

		Assert.assertEquals(standardVariable.getName(), result.get(headerNameToMatch.toUpperCase()).iterator().next().getName());
		Assert.assertEquals(headerNameToMatch, result.get(headerNameToMatch.toUpperCase()).iterator().next().getAlias());

	}

	@Test
	public void testGetStandardVariablesInProjects_HeaderHasMatchWithOntologyVariableName() {

		final String headerNameToMatch = RandomStringUtils.randomAlphabetic(10);
		final List<String> headers = Arrays.asList(headerNameToMatch);

		final DmsProject dmsProject = this.createDMSProject();

		// Create a standard variable with name same as as the header name.
		this.createStandardVariable(
			headerNameToMatch, RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10),
			RandomStringUtils.randomAlphabetic(10));

		final Map<String, List<StandardVariable>> result =
			this.standardVariableBuilder.getStandardVariablesInProjects(headers, dmsProject.getProgramUUID());

		Assert.assertEquals(headerNameToMatch, result.get(headerNameToMatch.toUpperCase()).iterator().next().getName());

	}

	@Test
	public void testGetStandardVariablesInProjects_HeaderHasMatchWithOntologyVariableProperty() {

		final String headerNameToMatch = RandomStringUtils.randomAlphabetic(10);
		final List<String> headers = Arrays.asList(headerNameToMatch);

		final DmsProject dmsProject = this.createDMSProject();

		// Create a standard variable with property name same as as the header name.
		final StandardVariable standardVariable = this.createStandardVariable(
			RandomStringUtils.randomAlphabetic(10), headerNameToMatch, RandomStringUtils.randomAlphabetic(10),
			RandomStringUtils.randomAlphabetic(10));

		final Map<String, List<StandardVariable>> result =
			this.standardVariableBuilder.getStandardVariablesInProjects(headers, dmsProject.getProgramUUID());

		Assert.assertEquals(standardVariable.getName(), result.get(headerNameToMatch.toUpperCase()).iterator().next().getName());

	}

	@Test
	public void testSetRoleOfVariablesVariableTypeIsUnassigned() {

		final StandardVariable trialInstanceFactor = standardVariableBuilder.create(TermId.TRIAL_INSTANCE_FACTOR.getId(), null);
		final List<StandardVariable> standardVariables = new ArrayList<>();
		standardVariables.add(trialInstanceFactor);

		// Assign a null VariableType for trialInstanceFactor
		final Map<Integer, VariableType> variableTypeMap = new HashMap<>();
		variableTypeMap.put(TermId.TRIAL_INSTANCE_FACTOR.getId(), null);

		standardVariableBuilder.setRoleOfVariables(standardVariables, variableTypeMap);

		// The trialInstanceFactor has no VariableType assigned to it, so the phenotypicType (role) is null
		Assert.assertNull(trialInstanceFactor.getPhenotypicType());

	}

	@Test
	public void testSetRoleOfVariablesVariableTypeIsAssigned() {

		final StandardVariable trialInstanceFactor = standardVariableBuilder.create(TermId.TRIAL_INSTANCE_FACTOR.getId(), null);
		final List<StandardVariable> standardVariables = new ArrayList<>();
		standardVariables.add(trialInstanceFactor);

		// Assign the trialInstanceFactor to VariableType.ENVIRONMENT_DETAIL
		final Map<Integer, VariableType> variableTypeMap = new HashMap<>();
		variableTypeMap.put(TermId.TRIAL_INSTANCE_FACTOR.getId(), VariableType.ENVIRONMENT_DETAIL);

		standardVariableBuilder.setRoleOfVariables(standardVariables, variableTypeMap);

		// The phenotypicType (role) of trialInstanceFactor should be from VariableType.ENVIRONMENT_DETAIL
		Assert.assertEquals(VariableType.ENVIRONMENT_DETAIL.getRole(), trialInstanceFactor.getPhenotypicType());

	}

	private DmsProject createDMSProject() {

		final DmsProject dmsProject = new DmsProject();
		dmsProject.setProjectId(1);
		dmsProject.setName("ProjectName");
		dmsProject.setDescription("ProjectDescription");
		dmsProject.setProgramUUID(UUID.randomUUID().toString());

		return dmsProject;
	}

	private DMSVariableType createDMSVariableType(
		final String localName, final String localDescription, final int rank,
		final PhenotypicType role, final VariableType variableType) {
		final DMSVariableType dmsVariableType = new DMSVariableType();
		dmsVariableType.setLocalName(localName);
		dmsVariableType.setLocalDescription(localDescription);
		dmsVariableType.setRank(rank);
		dmsVariableType.setRole(role);
		dmsVariableType.setVariableType(variableType);
		if (variableType != null && variableType.getId().intValue() == VariableType.TREATMENT_FACTOR.getId()) {
			dmsVariableType.setTreatmentLabel("TEST TREATMENT LABEL");
		}
		dmsVariableType.setStandardVariable(
			this.createStandardVariable(localName, RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10),
				RandomStringUtils.randomAlphanumeric(10)));
		return dmsVariableType;
	}

	private StandardVariable createStandardVariable(
		final String name, final String propertyName, final String scaleName, final String methodName) {

		final CVTerm property = this.cvTermDao.save(propertyName, "", CvId.PROPERTIES);
		final CVTerm scale = this.cvTermDao.save(scaleName, "", CvId.SCALES);
		final CVTerm method = this.cvTermDao.save(methodName, "", CvId.METHODS);

		final StandardVariable standardVariable = new StandardVariable();
		standardVariable.setName(name);
		standardVariable.setProperty(new Term(property.getCvTermId(), property.getName(), property.getDefinition()));
		standardVariable.setScale(new Term(scale.getCvTermId(), scale.getName(), scale.getDefinition()));
		standardVariable.setMethod(new Term(method.getCvTermId(), method.getName(), method.getDefinition()));
		standardVariable.setDataType(new Term(DataType.CHARACTER_VARIABLE.getId(), "Character variable", "variable with char values"));
		standardVariable.setIsA(new Term(1050, "Study condition", "Study condition class"));

		this.standardVariableSaver.save(standardVariable);

		return standardVariable;
	}

}
