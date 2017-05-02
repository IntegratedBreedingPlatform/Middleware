/*******************************************************************************
 *
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.oms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.PropertyReference;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.util.Debug;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CvTermDaoTest extends IntegrationTestBase {

	private static final int METHOD_APPLIED = 4020;
	private static final int METHOD_ASSIGNED = 4030;
	private static final int METHOD_ENUMERATED = 4040;

	private static CVTermDao dao;

	@Before
	public void setUp() throws Exception {
		CvTermDaoTest.dao = new CVTermDao();
		CvTermDaoTest.dao.setSession(this.sessionProvder.getSession());
	}

	@Test
	public void testGetTermIdsWithTypeByNameOrSynonyms() throws Exception {
		Map<String, VariableType> expectedStdVarWithTypeMap = this.createVarNameWithTypeMapTestData();

		List<String> nameOrSynonyms = new ArrayList<String>();
		nameOrSynonyms.addAll(expectedStdVarWithTypeMap.keySet());

		Map<String, Map<Integer, VariableType>> results =
				CvTermDaoTest.dao.getTermIdsWithTypeByNameOrSynonyms(nameOrSynonyms, CvId.VARIABLES.getId());

		Debug.println(0, "testGetTermIdsWithTypeByNameOrSynonyms(nameOrSynonyms=" + nameOrSynonyms + ") RESULTS:");
		for (String name : nameOrSynonyms) {
			Map<Integer, VariableType> actualStdVarIdWithTypeMap = results.get(name);
			Debug.println(0, "    Name/Synonym = " + name + ", Terms = " + actualStdVarIdWithTypeMap);
			if (actualStdVarIdWithTypeMap != null) {
				final VariableType variableType = expectedStdVarWithTypeMap.get(name);
				Assert.assertTrue(actualStdVarIdWithTypeMap.containsValue(variableType));
			}
		}

	}

	private Map<String, VariableType> createVarNameWithTypeMapTestData() {
		Map<String, VariableType> varNameWithTypeMap = new HashMap<String, VariableType>();
		varNameWithTypeMap.put("TRIAL_INSTANCE", VariableType.ENVIRONMENT_DETAIL);
		varNameWithTypeMap.put("ENTRY_NO", VariableType.GERMPLASM_DESCRIPTOR);
		varNameWithTypeMap.put("DESIGNATION", VariableType.GERMPLASM_DESCRIPTOR);
		varNameWithTypeMap.put("GID", VariableType.GERMPLASM_DESCRIPTOR);
		varNameWithTypeMap.put("CROSS", VariableType.GERMPLASM_DESCRIPTOR);
		varNameWithTypeMap.put("PLOT_NO", VariableType.EXPERIMENTAL_DESIGN);
		varNameWithTypeMap.put("REP_NO", VariableType.EXPERIMENTAL_DESIGN);
		varNameWithTypeMap.put("SITE_SOIL_PH", VariableType.TRIAL_CONDITION);
		return varNameWithTypeMap;
	}

	@Test
	public void testFilterByColumnValue() throws Exception {
		List<CVTerm> cvTerms = CvTermDaoTest.dao.filterByColumnValue("name", "Collaborator");
		Assert.assertEquals(cvTerms.size(), 1);
	}

	@Test
	public void testFilterByColumnValues() throws Exception {
		List<Integer> ids = Arrays.asList(2020, 2030);
		List<CVTerm> cvTerms = CvTermDaoTest.dao.filterByColumnValues("cvTermId", ids);
		Assert.assertEquals(cvTerms.size(), 2);
	}

	@Test
	public void testGetByNameAndCvId() throws Exception {
		CVTerm cvterm = CvTermDaoTest.dao.getByNameAndCvId("User", CvId.PROPERTIES.getId());
		Assert.assertTrue(cvterm.getCvTermId() == 2002);
		Debug.println(0, "testGetByNameAndCvId(\"User\", " + CvId.PROPERTIES.getId() + "): " + cvterm);

		cvterm = CvTermDaoTest.dao.getByNameAndCvId("DBCV", CvId.SCALES.getId());
		Debug.println(0, "testGetByNameAndCvId(\"DBCV\", " + CvId.SCALES.getId() + "): " + cvterm);
		Assert.assertTrue(cvterm.getCvTermId() == 6000);

		cvterm = CvTermDaoTest.dao.getByNameAndCvId("Assigned", CvId.METHODS.getId());
		Debug.println(0, "testGetByNameAndCvId(\"Assigned\", " + CvId.METHODS.getId() + "): " + cvterm);
		Assert.assertTrue(cvterm.getCvTermId() == 4030);

	}

	@Test
	public void testGetTermsByNameOrSynonym() throws Exception {
		List<Integer> termIds = CvTermDaoTest.dao.getTermsByNameOrSynonym("Cooperator", 1010);
		Debug.println(0, "testGetTermsByNameOrSynonym(): " + termIds);
	}

	@Test
	public void testGetStandardVariableIdsWithTypeByProperties() throws Exception {
		Map<String, VariableType> expectedVarWithTypeMap = this.createVarNameWithTypeMapTestData();

		List<String> propertyNames = new ArrayList<String>();
		propertyNames.addAll(expectedVarWithTypeMap.keySet());

		Map<String, Map<Integer, VariableType>> results = CvTermDaoTest.dao.getStandardVariableIdsWithTypeByProperties(propertyNames);

		Debug.println(0, "testGetStandardVariableIdsByProperties(nameOrSynonyms=" + propertyNames + ") RESULTS:");
		for (String name : propertyNames) {
			Map<Integer, VariableType> actualStdVarIdWithTypeMap = results.get(name);
			Debug.println(0, "    Name/Synonym = " + name + ", Terms = " + actualStdVarIdWithTypeMap);
			if (actualStdVarIdWithTypeMap != null) {
				Assert.assertTrue(actualStdVarIdWithTypeMap.containsValue(expectedVarWithTypeMap.get(name)));
			}
		}
	}

	@Ignore("See BMS-3721")
	@Test
	public void testGetOntologyTraitClasses() throws Exception {
		List<TraitClassReference> traitClasses = CvTermDaoTest.dao.getTraitClasses(TermId.ONTOLOGY_TRAIT_CLASS);
		Assert.assertTrue(traitClasses.size() > 0);
		Debug.println(4, "testGetOntologyTraitClasses(): ");
		for (TraitClassReference trait : traitClasses) {
			Debug.println(8, trait.toString());
		}
	}

	@Ignore("See BMS-3721")
	@Test
	public void testGetOntologyResearchClasses() throws Exception {
		List<TraitClassReference> traitClasses = CvTermDaoTest.dao.getTraitClasses(TermId.ONTOLOGY_RESEARCH_CLASS);
		Assert.assertTrue(traitClasses.size() > 0);
		Debug.println(4, "testGetTraitClasses(): ");
		for (TraitClassReference trait : traitClasses) {
			Debug.println(8, trait.toString());
		}
	}

	@Ignore("See BMS-3721")
	@Test
	public void testGetAllTraitClasses() throws Exception {
		List<TraitClassReference> traitClasses = CvTermDaoTest.dao.getAllTraitClasses();
		Assert.assertTrue(traitClasses.size() > 0);
		Debug.println(4, "testGetAllTraitClasses(): ");
		for (TraitClassReference trait : traitClasses) {
			Debug.println(8, trait.toString());
		}
	}

	@Test
	public void testGetPropertiesOfTraitClasses() throws Exception {

		List<Integer> traitClassIds = Arrays.asList(1340, 1345, 1350, 1360, 1370, 1380, 1410, 22568);

		Map<Integer, List<PropertyReference>> traitClassProperties = CvTermDaoTest.dao.getPropertiesOfTraitClasses(traitClassIds);
		Assert.assertTrue(traitClassProperties.size() > 0);
		Debug.println(4, "testGetPropertiesOfTraitClasses(): ");
		for (Integer traitClassId : traitClassIds) {
			List<PropertyReference> properties = traitClassProperties.get(traitClassId);
			if (properties != null) {
				Debug.println(4, traitClassId + " (size = " + properties.size() + ") : " + properties);
				for (PropertyReference property : properties) {
					property.print(4);
				}
			} else {
				Debug.println(4, traitClassId + " (size = 0) : " + null);
			}
		}
	}

	@Test
	public void testGetStandardVariablesOfProperties() throws Exception {

		List<Integer> propertyIds = Arrays.asList(1340, 2000, 2002, 2010, 2012, 2015, 2270);

		Map<Integer, List<StandardVariableReference>> propertyVariables = CvTermDaoTest.dao.getStandardVariablesOfProperties(propertyIds);
		Assert.assertTrue(propertyVariables.size() > 0);
		Debug.println(4, "testGetStandardVariablesOfProperties(): ");
		for (Integer id : propertyIds) {
			List<StandardVariableReference> properties = propertyVariables.get(id);
			if (properties != null) {
				Debug.println(4, id + " (size = " + properties.size() + ") : " + properties);
			} else {
				Debug.println(4, id + " (size = 0) : " + null);
			}
		}
	}

	@Test
	public void testGetAllByCvId_CvIdAsInt() {
		List<CVTerm> nonObsoleteMethods = dao.getAllByCvId(CvId.METHODS.getId(), true);
		Assert.assertNotNull(nonObsoleteMethods);
		for (CVTerm cvTerm : nonObsoleteMethods) {
			Assert.assertTrue("All methods should have cv id " + CvId.METHODS.getId(), cvTerm.getCv().intValue() == CvId.METHODS.getId());
			Assert.assertFalse("Method " + cvTerm.getCvTermId() + " should be non-obsolete", cvTerm.isObsolete());
		}
	}

	@Test
	public void testGetAllByCvId_FilterObsolete() {
		List<CVTerm> nonObsoleteProperties = dao.getAllByCvId(CvId.PROPERTIES, true);
		Assert.assertNotNull(nonObsoleteProperties);
		for (CVTerm cvTerm : nonObsoleteProperties) {
			Assert.assertTrue("All properties should have cv id " + CvId.PROPERTIES.getId(),
					cvTerm.getCv().intValue() == CvId.PROPERTIES.getId());
			Assert.assertFalse("Property " + cvTerm.getCvTermId() + " should be non-obsolete", cvTerm.isObsolete());
		}
	}

	@Test
	public void testGetAllByCvId_DontFilterObsolete() {
		List<CVTerm> nonObsoleteProperties = dao.getAllByCvId(CvId.PROPERTIES, true);
		List<CVTerm> allProperties = dao.getAllByCvId(CvId.PROPERTIES, false);
		Assert.assertNotNull(allProperties);
		int numberOfObsoleteProperties = 0;
		for (CVTerm cvTerm : allProperties) {
			Assert.assertTrue("All properties should have cv id " + CvId.PROPERTIES.getId(),
					cvTerm.getCv().intValue() == CvId.PROPERTIES.getId());
			if(cvTerm.isObsolete()) {
				numberOfObsoleteProperties++;
			}
		}
		int expectedNumberOfNonObsoleteProperties = nonObsoleteProperties.size();
		int actualNumberOfNonObsoleteProperties = allProperties.size() - numberOfObsoleteProperties;
		Assert.assertEquals("Non-obsolete properties should be " + expectedNumberOfNonObsoleteProperties,
				expectedNumberOfNonObsoleteProperties, actualNumberOfNonObsoleteProperties);
	}

	@Test
	public void testGetAllByCvId_ListOfCvTermIds_FilterObsolete() {
		List<Integer> termIds = Arrays.asList(METHOD_APPLIED, METHOD_ASSIGNED, METHOD_ENUMERATED);
		List<CVTerm> nonObsoleteMethods = dao.getAllByCvId(termIds, CvId.METHODS, true);
		Assert.assertNotNull(nonObsoleteMethods);
		Assert.assertEquals("Methods " + termIds.toString() + " should all be non-obsolete", 3, nonObsoleteMethods.size());
		for (CVTerm cvTerm : nonObsoleteMethods) {
			Assert.assertTrue("All methods should have cv id " + CvId.METHODS.getId(), cvTerm.getCv().intValue() == CvId.METHODS.getId());
			Assert.assertFalse("Method " + cvTerm.getCvTermId() + " should be non-obsolete", cvTerm.isObsolete());
		}
	}

	@Test
	public void testGetAllByCvId_ListOfCvTermIds_DontFilterObsolete() {
		boolean filterObsolete = false;
		List<CVTerm> allProperties = dao.getAllByCvId(CvId.PROPERTIES, filterObsolete);
		List<Integer> obsoletePropertyIds = new ArrayList<>();
		for (CVTerm cvTerm : allProperties) {
			if (cvTerm.isObsolete()) {
				obsoletePropertyIds.add(cvTerm.getCvTermId());
			}
		}
		if (!obsoletePropertyIds.isEmpty()) {
			List<CVTerm> obsoleteProperties = dao.getAllByCvId(obsoletePropertyIds, CvId.PROPERTIES, filterObsolete);
			Assert.assertNotNull(obsoleteProperties);
			Assert.assertEquals("Obsolete properties should be " + obsoletePropertyIds.size(), obsoletePropertyIds.size(),
					obsoleteProperties.size());
			for (CVTerm cvTerm : obsoleteProperties) {
				Assert.assertTrue("All properties should have cv id " + CvId.PROPERTIES.getId(),
						cvTerm.getCv().intValue() == CvId.PROPERTIES.getId());
				Assert.assertTrue("Property " + cvTerm.getCvTermId() + " should be obsolete", cvTerm.isObsolete());
			}
		}
	}

}
