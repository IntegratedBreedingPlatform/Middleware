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

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.OntologyDataCreationUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OntologyScaleDataManagerImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private OntologyScaleDataManager manager;

	private Scale testScale;

	@Before
	public void setUp() throws Exception {
		String name = OntologyDataCreationUtil.getNewRandomName();
		String definition = "Test Definition";
		this.testScale = new Scale();
		this.testScale.setName(name);
		this.testScale.setDefinition(definition);
		this.testScale.setDataType(DataType.NUMERIC_VARIABLE);
		this.testScale.setMinValue("0");
		this.testScale.setMaxValue("100");
		this.manager.addScale(this.testScale);
	}

	@Test
	public void testGetAllScales() throws Exception {
		List<Scale> scales = this.manager.getAllScales();
		Assert.assertTrue(scales.size() > 0);
		Debug.println(IntegrationTestBase.INDENT, "From Total Scales:  " + scales.size());
	}

	@Test
	public void testGetScaleById() throws Exception {
		Scale scale = this.manager.getScaleById(6025);
		Assert.assertNotNull(scale);
		Assert.assertEquals("CSSI", scale.getName());
	}

	@Test
	public void testAddScale() throws Exception {
		Assert.assertNotNull(this.testScale.getId());
		Assert.assertTrue(this.testScale.getId() > 0);
		Debug.println(IntegrationTestBase.INDENT, "From db:  " + this.testScale);
		Scale scaleFromDb = this.manager.getScaleById(this.testScale.getId());
		Assert.assertEquals(this.testScale.getName(), scaleFromDb.getName());
		Assert.assertEquals(this.testScale.getDataType(), scaleFromDb.getDataType());
		Assert.assertEquals(this.testScale.getMinValue(), scaleFromDb.getMinValue());
		Assert.assertEquals(this.testScale.getMaxValue(), scaleFromDb.getMaxValue());
	}

	@Test
	public void testAddCategoricalScale() throws Exception {
		Scale scale = new Scale();
		scale.setName(OntologyDataCreationUtil.getNewRandomName());
		scale.setDefinition("");
		scale.setDataType(DataType.CATEGORICAL_VARIABLE);
		scale.addCategory(new TermSummary(null, "1", "First"));
		scale.addCategory(new TermSummary(null, "2", "Second"));
		this.manager.addScale(scale);
		Assert.assertNotNull(scale.getId());
		Assert.assertTrue(scale.getId() > 0);
		Debug.println(IntegrationTestBase.INDENT, "From db:  " + scale);
		Scale scaleFromDb = this.manager.getScaleById(scale.getId());
		Assert.assertEquals(scale.getName(), scaleFromDb.getName());
		Assert.assertEquals(scale.getDataType(), scaleFromDb.getDataType());
		Assert.assertEquals(scale.getMinValue(), scaleFromDb.getMinValue());
		Assert.assertEquals(scale.getMaxValue(), scaleFromDb.getMaxValue());
	}

	@Test
	public void testUpdateCategoricalScale() throws Exception {
		Scale scale = new Scale();
		scale.setName(OntologyDataCreationUtil.getNewRandomName());
		scale.setDefinition("");
		scale.setDataType(DataType.CATEGORICAL_VARIABLE);
		scale.addCategory(new TermSummary(null, "1", "First"));
		scale.addCategory(new TermSummary(null, "2", "Second"));
		this.manager.addScale(scale);
		Assert.assertNotNull(scale.getId());
		Assert.assertTrue(scale.getId() > 0);

		// Updating same scale with one more category
		scale.addCategory(new TermSummary(3, "3", "Third"));
		this.manager.updateScale(scale);

		Debug.println(IntegrationTestBase.INDENT, "From db:  " + scale);
		Scale scaleFromDb = this.manager.getScaleById(scale.getId());
		Assert.assertEquals(scale.getName(), scaleFromDb.getName());
		Assert.assertEquals(scale.getDataType(), scaleFromDb.getDataType());
		Assert.assertEquals(scale.getMinValue(), scaleFromDb.getMinValue());
		Assert.assertEquals(scale.getMaxValue(), scaleFromDb.getMaxValue());
	}

	@Test
	public void testUpdateScale() throws Exception {
		this.testScale.setDefinition("new definition");
		this.testScale.setDataType(DataType.CATEGORICAL_VARIABLE);
		this.testScale.addCategory(new TermSummary(1, "1", "First"));
		this.testScale.addCategory(new TermSummary(2, "2", "Second"));
		this.testScale.setMinValue(null);
		this.testScale.setMaxValue(null);
		this.manager.updateScale(this.testScale);
		Scale updatedScale = this.manager.getScaleById(this.testScale.getId());
		Assert.assertEquals(updatedScale.getDefinition(), this.testScale.getDefinition());
		Debug.println(IntegrationTestBase.INDENT, "From db:  " + this.testScale);
	}

	@After
	public void tearDown() throws Exception {
		this.manager.deleteScale(this.testScale.getId());
	}
}
