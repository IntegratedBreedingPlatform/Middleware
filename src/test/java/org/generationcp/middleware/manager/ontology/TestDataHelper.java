package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.generationcp.middleware.utils.test.UnitTestDaoIDGenerator;

/**
 * Helper class for ontology test to build test data
 */
public class TestDataHelper {

	private static final Boolean isObsolete = false;
	private static final Boolean isRelationshipType = false;

	public static void fillTestMethodsCvTerms(List<CVTerm> terms, int count) {
		for (int i = 0; i < count; i++) {
			terms.add(getTestCvTerm(CvId.METHODS));
		}
	}

	/**
	 * Function to fill properties as per the count
	 * @param properties put property Id and property
	 * @param count generate no. of count properties
	 */
	public static void fillTestProperties(Map<Integer, Property> properties, int count) {
		for (int i = 0; i < count; i++) {
			Property property = TestDataHelper.generateProperty();
			properties.put(property.getId(), property);
		}
	}

	/**
	 * Function to build CVTerm with supplied CvId. The name will be auto generated with CvId
	 */
	public static CVTerm getTestCvTerm(CvId cvId) {
		CVTerm term = new CVTerm();
		term.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
		term.setName(getNewRandomName(cvId.toString()));
		term.setDefinition("description");
		term.setCv(cvId.getId());
		term.setIsObsolete(isObsolete);
		term.setIsRelationshipType(isRelationshipType);
		return term;
	}

	/**
	 * Function to generate new property
	 * @return generated property
	 */
	private static Property generateProperty() {
		Property property = new Property();
		property.setId(UnitTestDaoIDGenerator.generateId(Property.class));
		property.setName(getNewRandomName("Name"));
		property.setDefinition("description");
		property.setCropOntologyId("CO:1234");
		property.addClass("Class1");
		property.addClass("Class2");
		return property;
	}

	/**
	 * Fill properties for created date for any term (Method, Scale, Property, Variable)
	 * Supplied Current date to control created date.
	 */
	public static void fillTestCreatedDateProperties(List<CVTerm> terms, List<CVTermProperty> properties, Date currentDate) {
		for (CVTerm term : terms) {
			properties.add(getTestProperty(term.getCvTermId(), TermId.CREATION_DATE, ISO8601DateParser.toString(currentDate), 0));
		}
	}


	public static void fillTestCreatedDatePropertyProps(List<Property> propertyList, List<CVTermProperty> properties, Date currentDate) {
		for (Property property : propertyList) {
			properties.add(getTestProperty(property.getId(), TermId.CREATION_DATE, ISO8601DateParser.toString(currentDate), 0));
		}
	}

	/**
	 * Fill properties for last update date for any term (Method, Scale, Property, Variable)
	 * Supplied Current date to control modified date.
	 */
	public static void fillTestUpdatedDateProperties(List<CVTerm> terms, List<CVTermProperty> properties, Date currentDate) {
		for (CVTerm term : terms) {
			properties.add(getTestProperty(term.getCvTermId(), TermId.LAST_UPDATE_DATE, ISO8601DateParser.toString(currentDate), 0));
		}
	}

	public static void fillTestUpdatedDatePropertyProps(List<Property> propertyList, List<CVTermProperty> properties, Date currentDate) {
		for (Property property : propertyList) {
			properties.add(getTestProperty(property.getId(), TermId.LAST_UPDATE_DATE, ISO8601DateParser.toString(currentDate), 0));
		}
	}

	/**
	 * Build CVTermProperty for supplied cvterm, type, value and rank
	 */
	public static CVTermProperty getTestProperty(Integer cvTermId, TermId typeId, String value, int rank) {
		CVTermProperty property = new CVTermProperty();
		property.setCvTermPropertyId(UnitTestDaoIDGenerator.generateId(CVTermProperty.class));
		property.setCvTermId(cvTermId);
		property.setTypeId(typeId.getId());
		property.setValue(value);
		property.setRank(rank);
		return property;
	}

	public static void fillTestPropertiesCvTerms(List<CVTerm> terms, int count) {
		for (int i = 0; i < count; i++) {
			terms.add(getTestCvTerm(CvId.PROPERTIES));
		}
	}

	public static void fillTestVariableCvTerms(List<CVTerm> terms, int count) {
		for (int i = 0; i < count; i++) {
			terms.add(getTestCvTerm(CvId.VARIABLES));
		}
	}

	/**
	 * Appends random number to string to make name unique
	 */
	public static String getNewRandomName(String name) {
		return name + "_" + new Random().nextInt(100000);
	}

	/**
	 * Generate new random classes that are not persisted in database
	 * @param count Total number of classes (is_a) to be generated
	 * @param dao CVTermDao for persistence support
	 * @return List<CVTerm> newly generated is_a term that are not available in database
	 */
	public static List<CVTerm> generateNewIsATerms(int count, CVTermDao dao){

		List<CVTerm> isATerms = new ArrayList<>();

		// Get all classes to check weather the randomly generated classes are exists or not
        List<Term> allClasses = dao.getTermByCvId(CvId.TRAIT_CLASS.getId());

        Set<String> restrictedClasses = new HashSet<>();

        // Add all classes to set
        for (Term term : allClasses) {
            restrictedClasses.add(term.getName());
        }

        //Get class names that are not present in existing database.
        int iCount = 0;
        while (iCount < count) {
            CVTerm classTerm = TestDataHelper.getTestCvTerm(CvId.TRAIT_CLASS);
            if (restrictedClasses.contains(classTerm.getName())) {
                continue;
            }
            iCount++;
            isATerms.add(classTerm);
            dao.save(classTerm);
        }

		return isATerms;
	}

	/**
	 * Random logic applied to select random class (is_a) from list of terms
	 * @param propertyTerms fill two classes (isA) per property term in map
	 * @param isATerms Choosing two is_a from isATerms randomly
	 * @param propertyClassesMap Fill property classes and persist relationship in database
	 */
	public static void fillIsARelationshipsForProperty(List<CVTerm> propertyTerms, List<CVTerm> isATerms, Map<Integer, Set<String>> propertyClassesMap, CVTermRelationshipDao cvTermRelationshipDao){

		final Integer assignedIsACountPerProperty = 2;
		final Integer totalIsACount = isATerms.size();

		for (CVTerm property : propertyTerms) {
			//Getting first random is_a
			Set<String> addedIsA = new HashSet<>();

			int isACount = 0;
			while(isACount < assignedIsACountPerProperty){
				CVTerm isA = isATerms.get(new Random().nextInt(totalIsACount));
				if(addedIsA.contains(isA.getName())){
					continue;
				}
				addedIsA.add(isA.getName());
				cvTermRelationshipDao.save(property.getCvTermId(), TermId.IS_A.getId(), isA.getCvTermId());
				isACount ++;
			}

			propertyClassesMap.put(property.getCvTermId(), addedIsA);
		}
	}

	/**
	 * Fill crop ontology id to property term and add to property map.
	 * @param propertyTerms fill two classes (isA) per property term in map
	 * @param cropOntologyIdMap crop ontology map to hold property id and crop ontology id data.
	 * @param cvTermPropertyDao Fill crop ontology for property and persist
	 */
	public static void fillCropOntologyForProperty(List<CVTerm> propertyTerms, Map<Integer, String> cropOntologyIdMap, CvTermPropertyDao cvTermPropertyDao){

		for (CVTerm property : propertyTerms) {
			String cropOntologyId = TestDataHelper.getNewRandomName("CO:");
			cvTermPropertyDao.updateOrDeleteProperty(property.getCvTermId(), TermId.CROP_ONTOLOGY_ID.getId(), cropOntologyId, 0);
			cropOntologyIdMap.put(property.getCvTermId(), cropOntologyId);
		}
	}
}
