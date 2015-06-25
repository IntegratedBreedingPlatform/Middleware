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

public class ScaleDataManagerImplTest extends DataManagerIntegrationTest {

	private static OntologyScaleDataManager manager;

	private static Scale testScale;

	@Before
	public void setUp() throws Exception {
		ScaleDataManagerImplTest.manager = DataManagerIntegrationTest.managerFactory.getOntologyScaleDataManager();
		String name = MiddlewareIntegrationTest.getNewRandomName();
		String definition = "Test Definition";
		ScaleDataManagerImplTest.testScale = new Scale();
		ScaleDataManagerImplTest.testScale.setName(name);
		ScaleDataManagerImplTest.testScale.setDefinition(definition);
		ScaleDataManagerImplTest.testScale.setDataType(DataType.NUMERIC_VARIABLE);
		ScaleDataManagerImplTest.testScale.setMinValue("0");
		ScaleDataManagerImplTest.testScale.setMaxValue("100");
		ScaleDataManagerImplTest.manager.addScale(ScaleDataManagerImplTest.testScale);
	}

	@Test
	public void testGetAllScales() throws Exception {
		List<Scale> scales = ScaleDataManagerImplTest.manager.getAllScales();
		Assert.assertTrue(scales.size() > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "From Total Scales:  " + scales.size());
	}

	@Test
	public void testGetScaleById() throws Exception {
		Scale scale = ScaleDataManagerImplTest.manager.getScaleById(6025);
		Assert.assertNotNull(scale);
		Assert.assertEquals("CSSI", scale.getName());
	}

	@Test
	public void testAddScale() throws Exception {
		Assert.assertNotNull(ScaleDataManagerImplTest.testScale.getId());
		Assert.assertTrue(ScaleDataManagerImplTest.testScale.getId() > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + ScaleDataManagerImplTest.testScale);
		Scale scaleFromDb = ScaleDataManagerImplTest.manager.getScaleById(ScaleDataManagerImplTest.testScale.getId());
		Assert.assertEquals(ScaleDataManagerImplTest.testScale.getName(), scaleFromDb.getName());
		Assert.assertEquals(ScaleDataManagerImplTest.testScale.getDataType(), scaleFromDb.getDataType());
		Assert.assertEquals(ScaleDataManagerImplTest.testScale.getMinValue(), scaleFromDb.getMinValue());
		Assert.assertEquals(ScaleDataManagerImplTest.testScale.getMaxValue(), scaleFromDb.getMaxValue());
	}

	@Test
	public void testAddCategoricalScale() throws Exception {
		Scale scale = new Scale();
		scale.setName(MiddlewareIntegrationTest.getNewRandomName());
		scale.setDefinition("");
		scale.setDataType(DataType.CATEGORICAL_VARIABLE);
		scale.addCategory("1", "First");
		scale.addCategory("2", "Second");
		ScaleDataManagerImplTest.manager.addScale(scale);
		Assert.assertNotNull(scale.getId());
		Assert.assertTrue(scale.getId() > 0);
		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + scale);
		Scale scaleFromDb = ScaleDataManagerImplTest.manager.getScaleById(scale.getId());
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
		scale.addCategory("1", "First");
		scale.addCategory("2", "Second");
		ScaleDataManagerImplTest.manager.addScale(scale);
		Assert.assertNotNull(scale.getId());
		Assert.assertTrue(scale.getId() > 0);

		// Updating same scale with one more category
		scale.addCategory("3", "Third");
		ScaleDataManagerImplTest.manager.updateScale(scale);

		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + scale);
		Scale scaleFromDb = ScaleDataManagerImplTest.manager.getScaleById(scale.getId());
		Assert.assertEquals(scale.getName(), scaleFromDb.getName());
		Assert.assertEquals(scale.getDataType(), scaleFromDb.getDataType());
		Assert.assertEquals(scale.getMinValue(), scaleFromDb.getMinValue());
		Assert.assertEquals(scale.getMaxValue(), scaleFromDb.getMaxValue());
	}

	@Test
	public void testUpdateScale() throws Exception {
		ScaleDataManagerImplTest.testScale.setDefinition("new definition");
		ScaleDataManagerImplTest.testScale.setDataType(DataType.CATEGORICAL_VARIABLE);
		ScaleDataManagerImplTest.testScale.addCategory("1", "First");
		ScaleDataManagerImplTest.testScale.addCategory("2", "Second");
		ScaleDataManagerImplTest.testScale.setMinValue(null);
		ScaleDataManagerImplTest.testScale.setMaxValue(null);
		ScaleDataManagerImplTest.manager.updateScale(ScaleDataManagerImplTest.testScale);
		Scale updatedScale = ScaleDataManagerImplTest.manager.getScaleById(ScaleDataManagerImplTest.testScale.getId());
		Assert.assertEquals(updatedScale.getDefinition(), ScaleDataManagerImplTest.testScale.getDefinition());
		Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + ScaleDataManagerImplTest.testScale);
	}

	@After
	public void tearDown() throws Exception {
		ScaleDataManagerImplTest.manager.deleteScale(ScaleDataManagerImplTest.testScale.getId());
	}
}
