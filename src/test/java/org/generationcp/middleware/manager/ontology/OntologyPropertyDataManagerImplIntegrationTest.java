package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OntologyPropertyDataManagerImplIntegrationTest extends IntegrationTestBase {

    @Autowired
    private OntologyPropertyDataManager manager;

    @Autowired
    private OntologyDaoFactory daoFactory;

    private CVTermDao termDao;
    private CvTermPropertyDao propertyDao;
    private CVTermRelationshipDao relationshipDao;

    @Before
    public void setUp() throws Exception {
        this.termDao = this.daoFactory.getCvTermDao();
        this.propertyDao = this.daoFactory.getCvTermPropertyDao();
        this.relationshipDao = this.daoFactory.getCvTermRelationshipDao();
    }

    @Test
    public void getPropertiesShouldGetFullProperties() throws Exception {
        List<CVTerm> propertyTerms = new ArrayList<>();
        TestDataHelper.fillTestPropertiesCvTerms(propertyTerms, 3);

        Map<Integer, CVTerm> termMap = new HashMap<>();
        // save 3 properties using termDao
        for (CVTerm term : propertyTerms) {
            termDao.save(term);
            termMap.put(term.getCvTermId(), term);
        }

        // Get all classes to check weather the randomly generated classes are exists or not
        List<Term> allClasses = this.termDao.getTermByCvId(CvId.TRAIT_CLASS.getId());

        Set<String> restrictedClasses = new HashSet<>();

        // Add all classes to set
        for (Term term : allClasses) {
            restrictedClasses.add(term.getName());
        }

        List<CVTerm> isATerms = new ArrayList<>();

        //Get 5 class names and attach two classes randomly
        int iCount = 0;
        while (iCount < 5) {
            CVTerm classTerm = TestDataHelper.getTestCvTerm(CvId.TRAIT_CLASS);
            if (restrictedClasses.contains(classTerm.getName())) {
                continue;
            }
            iCount++;
            isATerms.add(classTerm);
            this.termDao.save(classTerm);
        }

        Map<Integer, Set<String>> propertyClassesMap = new HashMap<>();
        Map<Integer, String> cropOntologyIdMap = new HashMap<>();

        for (CVTerm property : propertyTerms) {
            //Getting first random is_a
            Set<String> addedIsA = new HashSet<>();

            int isACount = 0;
            while(isACount < 2){
                CVTerm isA = isATerms.get(new Random().nextInt(5));
                if(addedIsA.contains(isA.getName())){
                    continue;
                }
                addedIsA.add(isA.getName());
                this.relationshipDao.save(property.getCvTermId(), TermId.IS_A.getId(), isA.getCvTermId());
                isACount ++;
            }

            propertyClassesMap.put(property.getCvTermId(), addedIsA);

            String cropOntologyId = TestDataHelper.getNewRandomName("CO:");

            this.propertyDao.updateOrDeleteProperty(property.getCvTermId(), TermId.CROP_ONTOLOGY_ID.getId(), cropOntologyId, 0);

            cropOntologyIdMap.put(property.getCvTermId(), cropOntologyId);
        }

        //Save created date
        Map<Integer, String> createdDateMap = new HashMap<>();
        Date testCreatedDate = this.constructDate(2015, Calendar.JANUARY, 1);
        List<CVTermProperty> createdDateProperties = new ArrayList<>();
        TestDataHelper.fillTestCreatedDateProperties(propertyTerms, createdDateProperties, testCreatedDate);

        for (CVTermProperty property : createdDateProperties) {
            propertyDao.save(property);
            createdDateMap.put(property.getCvTermId(), property.getValue());
        }

        //Save last modification date
        Map<Integer, String> updateDateMap = new HashMap<>();
        Date testUpdatedDate = this.constructDate(2015, Calendar.MAY, 20);
        List<CVTermProperty> updatedDateProperties = new ArrayList<>();
        TestDataHelper.fillTestUpdatedDateProperties(propertyTerms, updatedDateProperties, testUpdatedDate);


        // Fetch Updated Date Property and save it using propertyDao
        for (CVTermProperty property : updatedDateProperties) {
            propertyDao.save(property);
            updateDateMap.put(property.getCvTermId(), property.getValue());
        }

        // Fetch all methods and check our last inserted method exists or not
        List<Property> properties = this.manager.getAllProperties();

        // Iterate all methods and find our inserted method and assert it
        for (Property property : properties) {
            // Make sure our method exists and is inserted properly and display proper message if it is not inserted properly
            String message = "The %s for property '" + property.getId() + "' was not added correctly.";
            if (termMap.containsKey(property.getId())) {
                CVTerm propertyTerm = termMap.get(property.getId());
                String createdDateProperty = createdDateMap.get(property.getId());
                String updatedDateProperty = updateDateMap.get(property.getId());

                Assert.assertEquals(String.format(message, "Name"), propertyTerm.getName(), property.getName());
                Assert.assertEquals(String.format(message, "Definition"), propertyTerm.getDefinition(), property.getDefinition());
                Assert.assertEquals(String.format(message, "IsObsolete"), propertyTerm.isObsolete(), property.isObsolete());
                Assert.assertEquals(String.format(message, "CreatedDate"), createdDateProperty,
                        ISO8601DateParser.toString(property.getDateCreated()));
                Assert.assertEquals(String.format(message, "UpdatedDate"), updatedDateProperty,
                        ISO8601DateParser.toString(property.getDateLastModified()));

                Assert.assertEquals(String.format(message, "CropOntologyId"), cropOntologyIdMap.get(property.getId()), property.getCropOntologyId());
                Assert.assertArrayEquals(String.format(message, "TraitClasses"), propertyClassesMap.get(property.getId()).toArray(), property.getClasses().toArray());
            }
        }

    }

    @Test
    public void testGetPropertyByIdShouldGetFullPropertyWithIdSupplied() throws Exception {
        CVTerm propertyTerm = TestDataHelper.getTestCvTerm(CvId.PROPERTIES);
        this.termDao.save(propertyTerm);

        // Get all classes to check weather the randomly generated classes are exists or not
        List<Term> allClasses = this.termDao.getTermByCvId(CvId.TRAIT_CLASS.getId());
        Set<String> restrictedClasses = new HashSet<>();

        // Add all classes to set
        for (Term term : allClasses) {
            restrictedClasses.add(term.getName());
        }

        List<CVTerm> isATerms = new ArrayList<>();
        List<String> isATermNames = new ArrayList<>();

        //Get 5 class names and attach two classes randomly
        int iCount = 0;
        while (iCount < 2) {
            CVTerm classTerm = TestDataHelper.getTestCvTerm(CvId.TRAIT_CLASS);
            if (restrictedClasses.contains(classTerm.getName())) {
                continue;
            }
            iCount++;
            isATerms.add(classTerm);
            isATermNames.add(classTerm.getName());
            this.termDao.save(classTerm);
        }

        this.relationshipDao.save(propertyTerm.getCvTermId(), TermId.IS_A.getId(), isATerms.get(0).getCvTermId());
        this.relationshipDao.save(propertyTerm.getCvTermId(), TermId.IS_A.getId(), isATerms.get(1).getCvTermId());

        String cropOntologyId = TestDataHelper.getNewRandomName("CO:");

        this.propertyDao.updateOrDeleteProperty(propertyTerm.getCvTermId(), TermId.CROP_ONTOLOGY_ID.getId(), cropOntologyId, 0);

        Date testCreatedDate = this.constructDate(2015, Calendar.JANUARY, 1);
        List<CVTermProperty> createdDateProperties = new ArrayList<>();
        TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(propertyTerm), createdDateProperties, testCreatedDate);

        CVTermProperty createProperty = createdDateProperties.get(0);
        this.propertyDao.save(createProperty);

        //Save last modification date
        Date testUpdatedDate = this.constructDate(2015, Calendar.MAY, 20);
        List<CVTermProperty> updatedDateProperties = new ArrayList<>();
        TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(propertyTerm), updatedDateProperties, testUpdatedDate);

        CVTermProperty updateProperty = updatedDateProperties.get(0);
        this.propertyDao.save(updateProperty);


       Property property=this.manager.getProperty(propertyTerm.getCvTermId());

        // Make sure our method exists and is inserted properly and display proper message if it is not inserted properly
        String message = "The %s for property '" + property.getId() + "' was not added correctly.";
        Assert.assertEquals(String.format(message, "Name"), propertyTerm.getName(), property.getName());
        Assert.assertEquals(String.format(message, "Definition"), propertyTerm.getDefinition(), property.getDefinition());
        Assert.assertEquals(String.format(message, "IsObsolete"), propertyTerm.isObsolete(), property.isObsolete());
        Assert.assertEquals(String.format(message, "CreatedDate"), testCreatedDate, property.getDateCreated());
        Assert.assertEquals(String.format(message, "UpdatedDate"), testUpdatedDate, property.getDateLastModified());

        Assert.assertEquals(String.format(message, "CropOntologyId"), cropOntologyId, property.getCropOntologyId());
        Assert.assertArrayEquals(String.format(message, "TraitClasses"), isATermNames.toArray(), property.getClasses().toArray());
    }
}
