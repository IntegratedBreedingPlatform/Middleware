package org.generationcp.middleware.manager.ontology;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
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
		term.setIsSystem(false);
		return term;
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

	/**
	 * Fill properties for last update date for any term (Method, Scale, Property, Variable)
	 * Supplied Current date to control modified date.
	 */
	public static void fillTestUpdatedDateProperties(List<CVTerm> terms, List<CVTermProperty> properties, Date currentDate) {
		for (CVTerm term : terms) {
			properties.add(getTestProperty(term.getCvTermId(), TermId.LAST_UPDATE_DATE, ISO8601DateParser.toString(currentDate), 0));
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

	/**
	 * Appends random number to string to make name unique
	 */
	public static String getNewRandomName(String name) {
		return name + "_" + new Random().nextInt(100000);
	}
}
