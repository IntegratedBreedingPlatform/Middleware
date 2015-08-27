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

package org.generationcp.middleware.operation.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.StandardVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.junit.Before;
import org.junit.Test;

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
		StandardVariable standardVariable = standardVariableBuilder.create(TermId.TRIAL_INSTANCE_FACTOR.getId(), null);
		assertNotNull(standardVariable);
		assertEquals(TermId.TRIAL_INSTANCE_FACTOR.getId(), standardVariable.getId());
		assertEquals(TRIAL_INSTANCE_PROPERTY_ID, standardVariable.getProperty().getId());
		assertEquals(TRIAL_INSTANCE_SCALE_ID, standardVariable.getScale().getId());
		assertEquals(TRIAL_INSTANCE_METHOD_ID, standardVariable.getMethod().getId());
	}

	@Test
	public void testCreate_List() throws MiddlewareException {
		List<Integer> standardVariableIds = new ArrayList<Integer>();
		standardVariableIds.add(TermId.ENTRY_NO.getId());
		standardVariableIds.add(TermId.GID.getId());

		List<StandardVariable> standardVariables = standardVariableBuilder.create(standardVariableIds, null);
		assertNotNull(standardVariables);
		assertEquals(standardVariableIds.size(), standardVariables.size());
		for (StandardVariable standardVariable : standardVariables) {
			assertTrue(standardVariable.getId() == TermId.ENTRY_NO.getId() || standardVariable.getId() == TermId.GID.getId());
			if (standardVariable.getId() == TermId.ENTRY_NO.getId()) {
				assertEquals(2200, standardVariable.getProperty().getId());
				assertEquals(6040, standardVariable.getScale().getId());
				assertEquals(4040, standardVariable.getMethod().getId());
			} else if (standardVariable.getId() == TermId.GID.getId()) {
				assertEquals(2205, standardVariable.getProperty().getId());
				assertEquals(6010, standardVariable.getScale().getId());
				assertEquals(4030, standardVariable.getMethod().getId());
			}
		}
	}

	@Test
	public void testCreate_EmptyList() throws MiddlewareException {
		List<Integer> standardVariableIds = new ArrayList<Integer>();

		List<StandardVariable> standardVariables = standardVariableBuilder.create(standardVariableIds, null);
		assertNotNull(standardVariables);
		assertEquals(0, standardVariables.size());
	}

	@Test
	public void testCreate_NullList() throws MiddlewareException {
		List<Integer> standardVariableIds = null;

		List<StandardVariable> standardVariables = standardVariableBuilder.create(standardVariableIds, null);
		assertNotNull(standardVariables);
		assertEquals(0, standardVariables.size());
	}

	@Test
	public void testFindOrSave_Find() throws MiddlewareException {
		String name = "TRIAL_INSTANCE";
		String description = "Trial instance - enumerated (number)";
		String property = "Trial instance";
		String scale = "Number";
		String method = "Enumerated";
		String datatype = "N";
		PhenotypicType role = PhenotypicType.TRIAL_ENVIRONMENT;
		StandardVariable standardVariable =
				standardVariableBuilder.findOrSave(name, description, property, scale, method, role, datatype, null);
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
		assertEquals(TermId.NUMERIC_VARIABLE.getId(), standardVariable.getDataType().getId());
	}

	@Test
	public void testFindOrSave_Save() throws MiddlewareException {
		String name = "Test Variable Name";
		String description = "Test Variable Name Description";
		String property = "Test Property";
		String scale = "Test Scale";
		String method = "Test Method";
		String datatype = "N";
		PhenotypicType role = PhenotypicType.TRIAL_ENVIRONMENT;
		StandardVariable standardVariable =
				standardVariableBuilder.findOrSave(name, description, property, scale, method, role, datatype, null);
		assertNotNull(standardVariable);
		assertEquals(name, standardVariable.getName());
		assertEquals(description, standardVariable.getDescription());
		assertEquals(property, standardVariable.getProperty().getName());
		assertEquals(scale, standardVariable.getScale().getName());
		assertEquals(method, standardVariable.getMethod().getName());
		assertEquals(role, standardVariable.getPhenotypicType());
		assertEquals(TermId.NUMERIC_VARIABLE.getId(), standardVariable.getDataType().getId());
	}

	@Test
	public void testGetByName() throws MiddlewareException {
		String name = "TRIAL_INSTANCE";
		StandardVariable standardVariable = standardVariableBuilder.getByName(name, null);
		assertNotNull(standardVariable);
		assertEquals(TermId.TRIAL_INSTANCE_FACTOR.getId(), standardVariable.getId());
		assertEquals(name, standardVariable.getName());
		assertEquals(TRIAL_INSTANCE_PROPERTY_ID, standardVariable.getProperty().getId());
		assertEquals(TRIAL_INSTANCE_SCALE_ID, standardVariable.getScale().getId());
		assertEquals(TRIAL_INSTANCE_METHOD_ID, standardVariable.getMethod().getId());
	}

	@Test
	public void testGetByName_NotFound() throws MiddlewareException {
		String name = "VAR_123456";
		StandardVariable standardVariable = standardVariableBuilder.getByName(name, null);
		assertNull(standardVariable);
	}

	@Test
	public void testGetByPropertyScaleMethod() throws MiddlewareException {
		int propertyId = TRIAL_INSTANCE_PROPERTY_ID;
		int scaleId = TRIAL_INSTANCE_SCALE_ID;
		int methodId = TRIAL_INSTANCE_METHOD_ID;
		StandardVariable standardVariable = standardVariableBuilder.getByPropertyScaleMethod(propertyId, scaleId, methodId, null);
		assertNotNull(standardVariable);
		assertEquals(TRIAL_INSTANCE_PROPERTY_ID, standardVariable.getProperty().getId());
		assertEquals(TRIAL_INSTANCE_SCALE_ID, standardVariable.getScale().getId());
		assertEquals(TRIAL_INSTANCE_METHOD_ID, standardVariable.getMethod().getId());
	}

	@Test
	public void testGetByPropertyScaleMethod_NotFound() throws MiddlewareException {
		int propertyId = 1;
		int scaleId = 2;
		int methodId = 3;
		StandardVariable standardVariable = standardVariableBuilder.getByPropertyScaleMethod(propertyId, scaleId, methodId, null);
		assertNull(standardVariable);
	}

	@Test
	public void testGetByPropertyScaleMethodRole() throws MiddlewareException {
		int propertyId = TRIAL_INSTANCE_PROPERTY_ID;
		int scaleId = TRIAL_INSTANCE_SCALE_ID;
		int methodId = TRIAL_INSTANCE_METHOD_ID;
		PhenotypicType role = PhenotypicType.TRIAL_ENVIRONMENT;
		StandardVariable standardVariable = standardVariableBuilder.getByPropertyScaleMethodRole(propertyId, scaleId, methodId, role, null);
		assertNotNull(standardVariable);
		assertEquals(TRIAL_INSTANCE_PROPERTY_ID, standardVariable.getProperty().getId());
		assertEquals(TRIAL_INSTANCE_SCALE_ID, standardVariable.getScale().getId());
		assertEquals(TRIAL_INSTANCE_METHOD_ID, standardVariable.getMethod().getId());
	}

	@Test
	public void testGetByPropertyScaleMethodRole_NotFound() throws MiddlewareException {
		int propertyId = 1;
		int scaleId = 2;
		int methodId = 3;
		PhenotypicType role = PhenotypicType.TRIAL_ENVIRONMENT;
		StandardVariable standardVariable = standardVariableBuilder.getByPropertyScaleMethodRole(propertyId, scaleId, methodId, role, null);
		assertNull(standardVariable);
	}

	@Test
	public void testGetStandardVariablesInProjects() throws MiddlewareException {
		List<String> headers = this.createLocalVariableNamesOfProjectTestData();
		Map<String, List<StandardVariable>> stdVars = standardVariableBuilder.getStandardVariablesInProjects(headers, null);
		assertNotNull(stdVars);
		assertEquals(headers.size(), stdVars.size());
		for (String header : stdVars.keySet()) {
			assertTrue(headers.contains(header));
			List<StandardVariable> headerStandardVariables = stdVars.get(header);
			assertNotNull(headerStandardVariables);
			for (StandardVariable standardVariable : headerStandardVariables) {
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

	private List<String> createLocalVariableNamesOfProjectTestData() {
		List<String> headers = new ArrayList<String>();
		headers.add("TRIAL_INSTANCE");
		headers.add("ENTRY_NO");
		headers.add("PLOT_NO");
		return headers;
	}
}
