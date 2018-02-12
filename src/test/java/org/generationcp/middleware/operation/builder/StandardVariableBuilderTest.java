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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.ontology.OntologyDataHelper;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class StandardVariableBuilderTest extends IntegrationTestBase {

	private static final int TRIAL_INSTANCE_PROPERTY_ID = 2100;
	private static final int TRIAL_INSTANCE_SCALE_ID = 6040;
	private static final int TRIAL_INSTANCE_METHOD_ID = 4040;

	private static StandardVariableBuilder standardVariableBuilder;

	@Before
	public void setUp() throws Exception {
		standardVariableBuilder = new StandardVariableBuilder(this.sessionProvder);
	}

	@Test
	public void testCreate() throws MiddlewareException {
		final StandardVariable standardVariable = standardVariableBuilder.create(TermId.TRIAL_INSTANCE_FACTOR.getId(), null);
		assertNotNull(standardVariable);
		assertEquals(TermId.TRIAL_INSTANCE_FACTOR.getId(), standardVariable.getId());
		assertEquals(TRIAL_INSTANCE_PROPERTY_ID, standardVariable.getProperty().getId());
		assertEquals(TRIAL_INSTANCE_SCALE_ID, standardVariable.getScale().getId());
		assertEquals(TRIAL_INSTANCE_METHOD_ID, standardVariable.getMethod().getId());
	}

	@Test
	public void testCreateObsoleteVariable() throws MiddlewareException {
		final CVTermDao cvtermDao = new CVTermDao();
		cvtermDao.setSession(this.sessionProvder.getSession());

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
	public void testCreateList() throws MiddlewareException {

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
	public void testCreateEmptyList() throws MiddlewareException {
		final List<Integer> standardVariableIds = new ArrayList<Integer>();

		final List<StandardVariable> standardVariables = standardVariableBuilder.create(standardVariableIds, null);
		assertNotNull(standardVariables);
		assertEquals(0, standardVariables.size());
	}

	@Test
	public void testCreateNullList() throws MiddlewareException {
		final List<Integer> standardVariableIds = null;

		final List<StandardVariable> standardVariables = standardVariableBuilder.create(standardVariableIds, null);
		assertNotNull(standardVariables);
		assertEquals(0, standardVariables.size());
	}

	@Test
	public void testFindOrSaveFindExisting() throws MiddlewareException {
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
	public void testFindOrSaveSaveNewStandardVariable() throws MiddlewareException {
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
	public void testGetByName() throws MiddlewareException {
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
	public void testGetByNameNotFound() throws MiddlewareException {
		final String name = "VAR_123456";
		final StandardVariable standardVariable = standardVariableBuilder.getByName(name, null);
		assertNull(standardVariable);
	}

	@Test
	public void testGetByPropertyScaleMethod() throws MiddlewareException {
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
	public void testGetByPropertyScaleMethodNotFound() throws MiddlewareException {
		final int propertyId = 1;
		final int scaleId = 2;
		final int methodId = 3;
		final StandardVariable standardVariable = standardVariableBuilder.getByPropertyScaleMethod(propertyId, scaleId, methodId, null);
		assertNull(standardVariable);
	}

	@Test
	public void testGetByPropertyScaleMethodRole() throws MiddlewareException {
		final int propertyId = TRIAL_INSTANCE_PROPERTY_ID;
		final int scaleId = TRIAL_INSTANCE_SCALE_ID;
		final int methodId = TRIAL_INSTANCE_METHOD_ID;
		final PhenotypicType role = PhenotypicType.TRIAL_ENVIRONMENT;
		final StandardVariable standardVariable = standardVariableBuilder.getByPropertyScaleMethodRole(propertyId, scaleId, methodId, role, null);
		assertNotNull(standardVariable);
		assertEquals(TRIAL_INSTANCE_PROPERTY_ID, standardVariable.getProperty().getId());
		assertEquals(TRIAL_INSTANCE_SCALE_ID, standardVariable.getScale().getId());
		assertEquals(TRIAL_INSTANCE_METHOD_ID, standardVariable.getMethod().getId());
	}

	@Test
	public void testGetByPropertyScaleMethodRoleNotFound() throws MiddlewareException {
		final int propertyId = 1;
		final int scaleId = 2;
		final int methodId = 3;
		final PhenotypicType role = PhenotypicType.TRIAL_ENVIRONMENT;
		final StandardVariable standardVariable = standardVariableBuilder.getByPropertyScaleMethodRole(propertyId, scaleId, methodId, role, null);
		assertNull(standardVariable);
	}

	@Test
	public void testGetStandardVariablesInProjects() {
		final List<String> headers = this.createLocalVariableNamesOfProjectTestData();
		final Map<String, List<StandardVariable>> stdVars = standardVariableBuilder.getStandardVariablesInProjects(headers, null);
		assertNotNull(stdVars);
		
		final List<String> headerNamesTrimmed = Lists.transform(headers, new Function<String, String>() {
			public String apply(String s) {
				return s.trim();
			}
		});
		assertEquals(headerNamesTrimmed.size(), stdVars.size());
		for (final String header : stdVars.keySet()) {
			assertTrue(headerNamesTrimmed.contains(header));
			final List<StandardVariable> headerStandardVariables = stdVars.get(header);
			assertNotNull(headerStandardVariables);
			assertTrue(!headerStandardVariables.isEmpty());
			for (final StandardVariable standardVariable : headerStandardVariables) {
				if ("TRIAL_INSTANCE".equals(header)) {
					assertEquals(TermId.TRIAL_INSTANCE_FACTOR.getId(), standardVariable.getId());
					assertEquals(PhenotypicType.TRIAL_ENVIRONMENT, standardVariable.getPhenotypicType());
				} else if ("ENTRY_NO".equals(header)) {
					assertEquals(TermId.ENTRY_NO.getId(), standardVariable.getId());
					assertEquals(PhenotypicType.GERMPLASM, standardVariable.getPhenotypicType());
				} else if ("PLOT_NO".equals(header)) {
					assertEquals(TermId.PLOT_NO.getId(), standardVariable.getId());
					assertEquals(PhenotypicType.TRIAL_DESIGN, standardVariable.getPhenotypicType());
				}
			}
		}
	}

	@Test
	public void testGetStandardVariablesInProjectsHeaderNoMatchFromTheOntology() {
		final List<String> headers = new ArrayList<>();
		headers.add("UNKNOWN_TRAIT_NAME1");

		final Map<String, List<StandardVariable>> stdVars = standardVariableBuilder.getStandardVariablesInProjects(headers, null);
		assertNotNull(stdVars);
		assertEquals(headers.size(), stdVars.size());
		for (final String header : stdVars.keySet()) {

			assertTrue(headers.contains(header));

			final List<StandardVariable> headerStandardVariables = stdVars.get(header);
			assertTrue("If the header name doesn't match any Standard Variables, the variable list must be empty",
					headerStandardVariables.isEmpty());
		}
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

	private List<String> createLocalVariableNamesOfProjectTestData() {
		final List<String> headers = new ArrayList<>();
		// Put a trailing space at the end. Should still be retrieved
		headers.add("TRIAL_INSTANCE ");
		headers.add("ENTRY_NO");
		headers.add("PLOT_NO");
		return headers;
	}
}
