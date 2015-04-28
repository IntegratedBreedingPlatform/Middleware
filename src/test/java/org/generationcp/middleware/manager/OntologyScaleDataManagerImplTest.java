/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.oms.DataType;
import org.generationcp.middleware.domain.oms.Scale;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class OntologyScaleDataManagerImplTest extends DataManagerIntegrationTest {

	private static OntologyScaleDataManager manager;

    private static Scale testScale;

	@Before
	public void setUp() throws Exception {
		manager = DataManagerIntegrationTest.managerFactory.getOntologyScaleDataManager();
        String name = getNewRandomName();
        String definition = "Test Definition";
        testScale = new Scale();
        testScale.setName(name);
        testScale.setDefinition(definition);
        testScale.setDataType(DataType.NUMERIC_VARIABLE);
        testScale.setMinValue("0");
        testScale.setMaxValue("100");
        manager.addScale(testScale);
    }

    @Test
    public void testGetAllScales() throws Exception {
        List<Scale> scales = manager.getAllScales();
        Assert.assertTrue(scales.size() > 0);
        Debug.println(MiddlewareIntegrationTest.INDENT, "From Total Scales:  " + scales.size());
    }

    @Test
    public void testGetScaleById() throws Exception {
        Scale scale = manager.getScaleById(6025);
        Assert.assertNotNull(scale);
        Assert.assertEquals("CSSI", scale.getName());
    }

    @Test
    public void testAddScale() throws Exception {
        Assert.assertNotNull(testScale.getId());
        Assert.assertTrue(testScale.getId() > 0);
        Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + testScale);
        Scale scaleFromDb = manager.getScaleById(testScale.getId());
        Assert.assertEquals(testScale.getName(), scaleFromDb.getName());
        Assert.assertEquals(testScale.getDataType(), scaleFromDb.getDataType());
        Assert.assertEquals(testScale.getMinValue(), scaleFromDb.getMinValue());
        Assert.assertEquals(testScale.getMaxValue(), scaleFromDb.getMaxValue());
    }

    @Test
    public void testAddCategoricalScale() throws Exception {
        Scale scale = new Scale();
        scale.setName(getNewRandomName());
        scale.setDefinition("");
        scale.setDataType(DataType.CATEGORICAL_VARIABLE);
        scale.addCategory("1", "First");
        scale.addCategory("2", "Second");
        manager.addScale(scale);
        Assert.assertNotNull(scale.getId());
        Assert.assertTrue(scale.getId() > 0);
        Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + scale);
        Scale scaleFromDb = manager.getScaleById(scale.getId());
        Assert.assertEquals(scale.getName(), scaleFromDb.getName());
        Assert.assertEquals(scale.getDataType(), scaleFromDb.getDataType());
        Assert.assertEquals(scale.getMinValue(), scaleFromDb.getMinValue());
        Assert.assertEquals(scale.getMaxValue(), scaleFromDb.getMaxValue());
    }

    @Test
    public void testUpdateCategoricalScale() throws Exception {
        Scale scale = new Scale();
        scale.setName(getNewRandomName());
        scale.setDefinition("");
        scale.setDataType(DataType.CATEGORICAL_VARIABLE);
        scale.addCategory("1", "First");
        scale.addCategory("2", "Second");
        manager.addScale(scale);
        Assert.assertNotNull(scale.getId());
        Assert.assertTrue(scale.getId() > 0);

        //Updating same scale with one more category
        scale.addCategory("3", "Third");
        manager.updateScale(scale);

        Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + scale);
        Scale scaleFromDb = manager.getScaleById(scale.getId());
        Assert.assertEquals(scale.getName(), scaleFromDb.getName());
        Assert.assertEquals(scale.getDataType(), scaleFromDb.getDataType());
        Assert.assertEquals(scale.getMinValue(), scaleFromDb.getMinValue());
        Assert.assertEquals(scale.getMaxValue(), scaleFromDb.getMaxValue());
    }

    @Test
    public void testUpdateScale() throws Exception {
        testScale.setDefinition("new definition");
        testScale.setDataType(DataType.CATEGORICAL_VARIABLE);
        testScale.addCategory("1", "First");
        testScale.addCategory("2", "Second");
        testScale.setMinValue(null);
        testScale.setMaxValue(null);
        manager.updateScale(testScale);
        Scale updatedScale = manager.getScaleById(testScale.getId());
        Assert.assertEquals(updatedScale.getDefinition(), testScale.getDefinition());
        Debug.println(MiddlewareIntegrationTest.INDENT, "From db:  " + testScale);
    }

    @After
    public void tearDown() throws Exception {
        manager.deleteScale(testScale.getId());
    }
}
