/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.manager;

import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Implements {@link DataManagerIntegrationTest}
 */

public class OntologyScaleDataManagerImplIntegrationTest extends DataManagerIntegrationTest {

	private static OntologyScaleDataManager manager;

	private static Scale testScale;

	@Before
	public void setUp() throws Exception {
		OntologyScaleDataManagerImplIntegrationTest.manager = DataManagerIntegrationTest.managerFactory.getOntologyScaleDataManager();
		String name = MiddlewareIntegrationTest.getNewRandomName();
		String definition = "Test Definition";
		OntologyScaleDataManagerImplIntegrationTest.testScale = new Scale();
		OntologyScaleDataManagerImplIntegrationTest.testScale.setName(name);
		OntologyScaleDataManagerImplIntegrationTest.testScale.setDefinition(definition);
		OntologyScaleDataManagerImplIntegrationTest.testScale.setDataType(DataType.NUMERIC_VARIABLE);
		OntologyScaleDataManagerImplIntegrationTest.testScale.setMinValue("0");
		OntologyScaleDataManagerImplIntegrationTest.testScale.setMaxValue("100");
		OntologyScaleDataManagerImplIntegrationTest.manager.addScale(OntologyScaleDataManagerImplIntegrationTest.testScale);
	}

	@Test
	public void testGetAllScales() throws Exception {
		List<Scale> scales = OntologyScaleDataManagerImplIntegrationTest.manager.getAllScales();
		Assert.assertTrue(scales.size() > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "From Total Scales:  " + scales.size());
	}

	@Test
	public void testGetScaleById() throws Exception {
		Scale scale = OntologyScaleDataManagerImplIntegrationTest.manager.getScaleById(6025);
		Assert.assertNotNull(scale);
		Assert.assertEquals("CSSI", scale.getName());
	}

	@Test
	public void testAddScale() throws Exception {
		Assert.assertNotNull(OntologyScaleDataManagerImplIntegrationTest.testScale.getId());
		Assert.assertTrue(OntologyScaleDataManagerImplIntegrationTest.testScale.getId() > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + OntologyScaleDataManagerImplIntegrationTest.testScale);
		Scale scaleFromDb = OntologyScaleDataManagerImplIntegrationTest.manager.getScaleById(OntologyScaleDataManagerImplIntegrationTest.testScale.getId());
		Assert.assertEquals(OntologyScaleDataManagerImplIntegrationTest.testScale.getName(), scaleFromDb.getName());
		Assert.assertEquals(OntologyScaleDataManagerImplIntegrationTest.testScale.getDataType(), scaleFromDb.getDataType());
		Assert.assertEquals(OntologyScaleDataManagerImplIntegrationTest.testScale.getMinValue(), scaleFromDb.getMinValue());
		Assert.assertEquals(OntologyScaleDataManagerImplIntegrationTest.testScale.getMaxValue(), scaleFromDb.getMaxValue());
	}

	@Test
	public void testAddCategoricalScale() throws Exception {
		Scale scale = new Scale();
		scale.setName(MiddlewareIntegrationTest.getNewRandomName());
		scale.setDefinition("");
		scale.setDataType(DataType.CATEGORICAL_VARIABLE);
		scale.addCategory(new TermSummary(null, "1", "First"));
		scale.addCategory(new TermSummary(null, "2", "Second"));
		OntologyScaleDataManagerImplIntegrationTest.manager.addScale(scale);
		Assert.assertNotNull(scale.getId());
		Assert.assertTrue(scale.getId() > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + scale);
		Scale scaleFromDb = OntologyScaleDataManagerImplIntegrationTest.manager.getScaleById(scale.getId());
		Assert.assertEquals(scale.getName(), scaleFromDb.getName());
		Assert.assertEquals(scale.getDataType(), scaleFromDb.getDataType());
		Assert.assertEquals(scale.getMinValue(), scaleFromDb.getMinValue());
		Assert.assertEquals(scale.getMaxValue(), scaleFromDb.getMaxValue());
	}

	@Test
	public void testUpdateCategoricalScale() throws Exception {
		Scale scale = new Scale();
		scale.setName(MiddlewareIntegrationTest.getNewRandomName());
		scale.setDefinition("");
		scale.setDataType(DataType.CATEGORICAL_VARIABLE);
		scale.addCategory(new TermSummary(null, "1", "First"));
		scale.addCategory(new TermSummary(null, "2", "Second"));
		OntologyScaleDataManagerImplIntegrationTest.manager.addScale(scale);
		Assert.assertNotNull(scale.getId());
		Assert.assertTrue(scale.getId() > 0);

		// Updating same scale with one more category
		scale.addCategory(new TermSummary(3,"3", "Third"));
		OntologyScaleDataManagerImplIntegrationTest.manager.updateScale(scale);

		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + scale);
		Scale scaleFromDb = OntologyScaleDataManagerImplIntegrationTest.manager.getScaleById(scale.getId());
		Assert.assertEquals(scale.getName(), scaleFromDb.getName());
		Assert.assertEquals(scale.getDataType(), scaleFromDb.getDataType());
		Assert.assertEquals(scale.getMinValue(), scaleFromDb.getMinValue());
		Assert.assertEquals(scale.getMaxValue(), scaleFromDb.getMaxValue());
	}

	@Test
	public void testUpdateScale() throws Exception {
		OntologyScaleDataManagerImplIntegrationTest.testScale.setDefinition("new definition");
		OntologyScaleDataManagerImplIntegrationTest.testScale.setDataType(DataType.CATEGORICAL_VARIABLE);
		OntologyScaleDataManagerImplIntegrationTest.testScale.addCategory(new TermSummary(1, "1", "First"));
		OntologyScaleDataManagerImplIntegrationTest.testScale.addCategory(new TermSummary(2, "2", "Second"));
		OntologyScaleDataManagerImplIntegrationTest.testScale.setMinValue(null);
		OntologyScaleDataManagerImplIntegrationTest.testScale.setMaxValue(null);
		OntologyScaleDataManagerImplIntegrationTest.manager.updateScale(OntologyScaleDataManagerImplIntegrationTest.testScale);
		Scale updatedScale = OntologyScaleDataManagerImplIntegrationTest.manager.getScaleById(OntologyScaleDataManagerImplIntegrationTest.testScale.getId());
		Assert.assertEquals(updatedScale.getDefinition(), OntologyScaleDataManagerImplIntegrationTest.testScale.getDefinition());
		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + OntologyScaleDataManagerImplIntegrationTest.testScale);
	}

	@After
	public void tearDown() throws Exception {
		OntologyScaleDataManagerImplIntegrationTest.manager.deleteScale(OntologyScaleDataManagerImplIntegrationTest.testScale.getId());
	}
}
