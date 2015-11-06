package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.pojos.oms.CV;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration Test Class to handle Ontology Scale Data Manager
 */
public class OntologyScaleDataManagerImplIntegrationTest extends IntegrationTestBase {

    @Autowired
    private OntologyScaleDataManager manager;

    @Autowired
    private OntologyDaoFactory daoFactory;

	private CVDao cvDao;
    private CVTermDao termDao;
    private CvTermPropertyDao propertyDao;
    private CVTermRelationshipDao relationshipDao;

    @Before
    public void setUp() throws Exception {
		this.cvDao = this.daoFactory.getCvDao();
        this.termDao = this.daoFactory.getCvTermDao();
        this.propertyDao = this.daoFactory.getCvTermPropertyDao();
        this.relationshipDao = this.daoFactory.getCvTermRelationshipDao();
    }

	/**
	 * Test to verify get scales method fetch all scales
	 * @throws Exception
	 */
    @Test
    public void getScalesShouldGetFullScales() throws Exception {
        List<CVTerm> scaleTerms = new ArrayList<>();
        TestDataHelper.fillTestScaleCvTerms(scaleTerms, 10);

        Map<Integer, CVTerm> termMap = new HashMap<>();
        // save 10 scales using termDao
        for (CVTerm term : scaleTerms) {
			this.termDao.save(term);
            termMap.put(term.getCvTermId(), term);
		}

		// Set 4 scales with Numeric Data Type
		TestDataHelper.fillRelationshipsForScales(scaleTerms, DataType.NUMERIC_VARIABLE, this.relationshipDao, 0, 3);

		Map<Integer, String> minValueMap = new HashMap<>();
		Map<Integer, String> maxValueMap = new HashMap<>();

		String min1 = "5";
		String min2 = "0";

		// Set Min value for element 1 and 3
		this.propertyDao.updateOrDeleteProperty(scaleTerms.get(1).getCvTermId(), TermId.MIN_VALUE.getId(), min1, 0);
		this.propertyDao.updateOrDeleteProperty(scaleTerms.get(3).getCvTermId(), TermId.MIN_VALUE.getId(), min2, 0);

		// set Minimum values in map using scale id and its minimum value
		minValueMap.put(scaleTerms.get(1).getCvTermId(), min1);
		minValueMap.put(scaleTerms.get(3).getCvTermId(), min2);

		String max1 = "15";
		String max2 = "100";

		// Set Max value for element 2 and 3
		this.propertyDao.updateOrDeleteProperty(scaleTerms.get(2).getCvTermId(), TermId.MAX_VALUE.getId(), max1, 0);
		this.propertyDao.updateOrDeleteProperty(scaleTerms.get(3).getCvTermId(), TermId.MAX_VALUE.getId(), max2, 0);

		// set Maximum values in map using scale id and its maximum value
		maxValueMap.put(scaleTerms.get(2).getCvTermId(), max1);
		maxValueMap.put(scaleTerms.get(3).getCvTermId(), max2);

		// Set 2 Scales with Categorical Data Type
		TestDataHelper.fillRelationshipsForScales(scaleTerms, DataType.CATEGORICAL_VARIABLE, this.relationshipDao, 4, 5);

		// Make categories
		List<CVTerm> categoryTerms = new ArrayList<>();

		CV cv = new CV();
		TestDataHelper.fillCvForCategories(scaleTerms.get(4), cv, cvDao);

		TestDataHelper.fillTestScaleCategories(categoryTerms, cv.getCvId());

		// Save Categories to CVTerm
		for (CVTerm term : categoryTerms) {
			this.termDao.save(term);
		}

		Map<Integer, Set<String>> categoryMap = new HashMap<>();
		TestDataHelper.fillRelationshipsForScaleCategories(scaleTerms, categoryTerms, categoryMap, this.relationshipDao, 4, 5);

		// Set Scales with Date Data Type
		TestDataHelper.fillRelationshipsForScales(scaleTerms, DataType.DATE_TIME_VARIABLE, this.relationshipDao, 6, 7);

		// Set Scales with Character Data Type
		TestDataHelper.fillRelationshipsForScales(scaleTerms, DataType.CHARACTER_VARIABLE, this.relationshipDao, 8, 9);

		//Save created date
		Map<Integer, String> createdDateMap = new HashMap<>();
		Date testCreatedDate = this.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(scaleTerms, createdDateProperties, testCreatedDate);

		for (CVTermProperty property : createdDateProperties) {
			propertyDao.save(property);
			createdDateMap.put(property.getCvTermId(), property.getValue());
		}

		//Save last modification date
		Map<Integer, String> updateDateMap = new HashMap<>();
		Date testUpdatedDate = this.constructDate(2015, Calendar.MAY, 20);
		List<CVTermProperty> updatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(scaleTerms, updatedDateProperties, testUpdatedDate);

		// Fetch Updated Date Property and save it using propertyDao
		for (CVTermProperty property : updatedDateProperties) {
			propertyDao.save(property);
			updateDateMap.put(property.getCvTermId(), property.getValue());
		}

		// Fetch all scales and check our inserted scales exists or not
		List<Scale> scales = this.manager.getAllScales();

		// Iterate all scales and find our inserted scales and assert it
		for (Scale scale : scales) {
			String message = "The %s for scale '" + scale.getId() + "' was not added correctly.";

			if (termMap.containsKey(scale.getId())) {
				CVTerm scaleTerm = termMap.get(scale.getId());
				Assert.assertEquals(String.format(message, "Name"), scaleTerm.getName(), scale.getName());
				Assert.assertEquals(String.format(message, "Definition"), scaleTerm.getDefinition(), scale.getDefinition());
				Assert.assertEquals(String.format(message, "IsObsolete"), scaleTerm.isObsolete(), scale.isObsolete());

				if (Objects.equals(scale.getDataType().getId(), DataType.NUMERIC_VARIABLE.getId())) {
					Assert.assertEquals(String.format(message, "Minimum Value"), scale.getMinValue(), minValueMap.get(scale.getId()));
					Assert.assertEquals(String.format(message, "Maximum Value"), scale.getMaxValue(), maxValueMap.get(scale.getId()));

				} else if (Objects.equals(scale.getDataType().getId(), DataType.CATEGORICAL_VARIABLE.getId())) {

					final Set<String> categories = categoryMap.get(scale.getId());

					if (!Objects.isNull(categories)) {
						Assert.assertEquals(String.format(message, "Categories"), scale.getCategories().size(), categories.size());
					}

					for (int i = 0; i < scale.getCategories().size(); i++) {
						Assert.assertEquals(String.format(message, "Category Id"), scale.getCategories().get(i).getId(), categoryTerms.get(i).getCvTermId());
						Assert.assertEquals(String.format(message, "Category Name"), scale.getCategories().get(i).getName(), categoryTerms.get(i).getName());
						Assert.assertEquals(String.format(message, "Category Description"), scale.getCategories().get(i).getDefinition(), categoryTerms.get(i).getDefinition());
					}
				}

				String createdDateProperty = createdDateMap.get(scale.getId());
				String updatedDateProperty = updateDateMap.get(scale.getId());

				Assert.assertEquals(String.format(message, "Created Date"), createdDateProperty, ISO8601DateParser.toString(scale.getDateCreated()));
				Assert.assertEquals(String.format(message, "Updated Date"), updatedDateProperty, ISO8601DateParser.toString(scale.getDateLastModified()));

			}
		}
	}
}
