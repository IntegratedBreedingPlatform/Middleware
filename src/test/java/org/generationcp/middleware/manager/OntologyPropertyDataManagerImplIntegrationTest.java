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
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.OntologyDataCreationUtil;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OntologyPropertyDataManagerImplIntegrationTest extends IntegrationTestBase {

	@Autowired
	private OntologyPropertyDataManager propertyDataManager;
	private Property testProperty;

	private static final String CLASS_NAME = "newClass";

	@Before
	public void setUp() throws Exception {
		this.testProperty = new Property();
		this.testProperty.setName(OntologyDataCreationUtil.getNewRandomName());
		this.testProperty.setDefinition("definition");
		this.testProperty.setCropOntologyId("CO_322:0000046");
		this.testProperty.addClass(CLASS_NAME);
		this.testProperty.addClass(OntologyDataCreationUtil.getNewRandomName());
		Debug.println("adding test property " + this.testProperty);
		this.propertyDataManager.addProperty(this.testProperty);
	}

	@Test
	public void testGetPropertyById() throws Exception {
		Property property = this.propertyDataManager.getProperty(this.testProperty.getId(), true);
		Assert.assertNotNull(property);
		property.print(2);
		Assert.assertTrue("Property should have id " + this.testProperty.getId(), property.getId() == this.testProperty.getId());
		Assert.assertTrue("Property " + property.getId() + " should have cv id " + CvId.PROPERTIES.getId(),
				property.getVocabularyId() == CvId.PROPERTIES.getId());
		Assert.assertFalse("Property " + property.getId() + " should not be obsolete", property.isObsolete());

	}

	@Test
	public void testGetPropertyById_DontFilterObsolete() throws Exception {
		CVTermDao cvtermDao = new CVTermDao(this.sessionProvder.getSession());

		// set testProperty to obsolete
		int id = this.testProperty.getId();
		CVTerm testPropertyCvTerm = cvtermDao.getById(id);
		testPropertyCvTerm.setIsObsolete(true);
		cvtermDao.update(testPropertyCvTerm);

		boolean filterObsolete = false;
		Property property = this.propertyDataManager.getProperty(id, filterObsolete);
		Assert.assertNotNull(property);
		Assert.assertTrue("Property should have id " + id, property.getId() == id);
		Assert.assertTrue("Property " + property.getId() + " should have cv id " + CvId.PROPERTIES.getId(),
				property.getVocabularyId() == CvId.PROPERTIES.getId());
		Assert.assertFalse("Property " + property.getId() + " should not be obsolete", property.isObsolete());

		// revert changes
		testPropertyCvTerm.setIsObsolete(false);
		cvtermDao.update(testPropertyCvTerm);

	}

	@Test
	public void testGetAllPropertiesByClassName() throws Exception {
		List<Property> properties = this.propertyDataManager.getAllPropertiesWithClass("agronomic");
		for (Property p : properties) {
			p.print(2);
		}
		Debug.println("Properties: " + properties.size());
		Assert.assertTrue(properties.size() >= 10);
	}

	@Test
	public void testGetAllProperties() throws Exception {
		List<Property> properties = this.propertyDataManager.getAllProperties();
		for (Property p : properties) {
			p.print(2);
			Assert.assertTrue("Property " + p.getId() + " should have cv id " + CvId.PROPERTIES.getId(),
					p.getVocabularyId() == CvId.PROPERTIES.getId());
			Assert.assertFalse("Property " + p.getId() + " should not be obsolete", p.isObsolete());
		}
		Debug.println("Properties: " + properties.size());
		Assert.assertTrue(properties.size() > 0);
	}

	@Test
	public void testUpdateProperty() throws Exception {
		this.testProperty.setDefinition("new definition");
		this.testProperty.setCropOntologyId("CO_322:0000047");
		this.testProperty.getClasses().clear();
		this.testProperty.addClass(OntologyDataCreationUtil.getNewRandomName());
		this.propertyDataManager.updateProperty(this.testProperty);
		Property updatedProperty = this.propertyDataManager.getProperty(this.testProperty.getId(), true);
		Assert.assertEquals(updatedProperty.getDefinition(), "new definition");
		Assert.assertEquals(updatedProperty.getCropOntologyId(), "CO_322:0000047");
		Assert.assertEquals(updatedProperty.getClasses().size(), 1);
		Assert.assertTrue(updatedProperty.getClasses().containsAll(this.testProperty.getClasses()));

		// className = newClass should delete on-fly when class is no longer used by any property.
		Criteria criteria = this.sessionProvder.getSession().createCriteria(CVTerm.class)
				.add(Restrictions.eq("name",CLASS_NAME))
				.add(Restrictions.eq("cvId", CvId.TRAIT_CLASS.getId()));
		Assert.assertTrue("Expected cvterm for the class that is no longer in use to be deleted as part of update.", criteria.list()
				.isEmpty());
	}
}
