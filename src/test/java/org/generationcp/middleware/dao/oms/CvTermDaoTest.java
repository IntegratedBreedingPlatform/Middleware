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

import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.PropertyReference;
import org.generationcp.middleware.domain.oms.StandardVariableReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TraitClassReference;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.StudyType;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.service.api.study.VariableDTO;
import org.generationcp.middleware.util.Debug;
import org.generationcp.middleware.utils.test.IntegrationTestDataInitializer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CvTermDaoTest extends IntegrationTestBase {

	private static final int METHOD_APPLIED = 4020;
	private static final int METHOD_ASSIGNED = 4030;
	private static final int METHOD_ENUMERATED = 4040;
	private static final int STUDY_TYPE_ID = 6;
	public static final String MAIZE = "maize";

	private static CVTermDao dao;
	private DaoFactory daoFactory;
	private IntegrationTestDataInitializer testDataInitializer;

	@Before
	public void setUp() throws Exception {
		this.daoFactory = new DaoFactory(this.sessionProvder);
		this.testDataInitializer = new IntegrationTestDataInitializer(this.sessionProvder, this.workbenchSessionProvider);
		CvTermDaoTest.dao = this.daoFactory.getCvTermDao();
	}

	@Test
	public void testGetTermIdsWithTypeByNameOrSynonyms() throws Exception {
		final Map<String, VariableType> expectedStdVarWithTypeMap = this.createVarNameWithTypeMapTestData();

		final List<String> nameOrSynonyms = new ArrayList<String>();
		nameOrSynonyms.addAll(expectedStdVarWithTypeMap.keySet());

		final Map<String, Map<Integer, VariableType>> results =
			CvTermDaoTest.dao.getTermIdsWithTypeByNameOrSynonyms(nameOrSynonyms, CvId.VARIABLES.getId());

		Debug.println(0, "testGetTermIdsWithTypeByNameOrSynonyms(nameOrSynonyms=" + nameOrSynonyms + ") RESULTS:");
		for (final String name : nameOrSynonyms) {
			final Map<Integer, VariableType> actualStdVarIdWithTypeMap = results.get(name);
			Debug.println(0, "    Name/Synonym = " + name + ", Terms = " + actualStdVarIdWithTypeMap);
			if (actualStdVarIdWithTypeMap != null) {
				final VariableType variableType = expectedStdVarWithTypeMap.get(name);
				Assert.assertTrue(actualStdVarIdWithTypeMap.containsValue(variableType));
			}
		}

	}

	private Map<String, VariableType> createVarNameWithTypeMapTestData() {
		final Map<String, VariableType> varNameWithTypeMap = new HashMap<String, VariableType>();
		varNameWithTypeMap.put("TRIAL_INSTANCE", VariableType.ENVIRONMENT_DETAIL);
		varNameWithTypeMap.put("ENTRY_NO", VariableType.GERMPLASM_DESCRIPTOR);
		varNameWithTypeMap.put("DESIGNATION", VariableType.GERMPLASM_DESCRIPTOR);
		varNameWithTypeMap.put("GID", VariableType.GERMPLASM_DESCRIPTOR);
		varNameWithTypeMap.put("CROSS", VariableType.GERMPLASM_DESCRIPTOR);
		varNameWithTypeMap.put("PLOT_NO", VariableType.EXPERIMENTAL_DESIGN);
		varNameWithTypeMap.put("REP_NO", VariableType.EXPERIMENTAL_DESIGN);
		varNameWithTypeMap.put("SITE_SOIL_PH", VariableType.ENVIRONMENT_CONDITION);
		return varNameWithTypeMap;
	}

	@Test
	public void testFilterByColumnValue() throws Exception {
		final List<CVTerm> cvTerms = CvTermDaoTest.dao.filterByColumnValue("name", "Collaborator");
		Assert.assertEquals(cvTerms.size(), 1);
	}

	@Test
	public void testFilterByColumnValues() throws Exception {
		final List<Integer> ids = Arrays.asList(2020, 2030);
		final List<CVTerm> cvTerms = CvTermDaoTest.dao.filterByColumnValues("cvTermId", ids);
		Assert.assertEquals(cvTerms.size(), 2);
	}

	@Test
	public void testGetByNameAndCvId() throws Exception {
		CVTerm cvterm = CvTermDaoTest.dao.getByNameAndCvId("User", CvId.PROPERTIES.getId());
		Assert.assertEquals(2002, (int) cvterm.getCvTermId());
		Debug.println(0, "testGetByNameAndCvId(\"User\", " + CvId.PROPERTIES.getId() + "): " + cvterm);

		cvterm = CvTermDaoTest.dao.getByNameAndCvId("DBCV", CvId.SCALES.getId());
		Debug.println(0, "testGetByNameAndCvId(\"DBCV\", " + CvId.SCALES.getId() + "): " + cvterm);
		Assert.assertEquals(6000, (int) cvterm.getCvTermId());

		cvterm = CvTermDaoTest.dao.getByNameAndCvId("Assigned", CvId.METHODS.getId());
		Debug.println(0, "testGetByNameAndCvId(\"Assigned\", " + CvId.METHODS.getId() + "): " + cvterm);
		Assert.assertEquals(4030, (int) cvterm.getCvTermId());

	}

	@Test
	public void testGetTermsByNameOrSynonym() throws Exception {
		final List<Integer> termIds = CvTermDaoTest.dao.getTermsByNameOrSynonym("Cooperator", 1010);
		Debug.println(0, "testGetTermsByNameOrSynonym(): " + termIds);
	}

	@Test
	public void testGetStandardVariableIdsWithTypeByProperties() throws Exception {
		final Map<String, VariableType> expectedVarWithTypeMap = this.createVarNameWithTypeMapTestData();

		final List<String> propertyNames = new ArrayList<String>();
		propertyNames.addAll(expectedVarWithTypeMap.keySet());

		final Map<String, Map<Integer, VariableType>> results = CvTermDaoTest.dao.getStandardVariableIdsWithTypeByProperties(propertyNames);

		Debug.println(0, "testGetStandardVariableIdsByProperties(nameOrSynonyms=" + propertyNames + ") RESULTS:");
		for (final String name : propertyNames) {
			final Map<Integer, VariableType> actualStdVarIdWithTypeMap = results.get(name);
			Debug.println(0, "    Name/Synonym = " + name + ", Terms = " + actualStdVarIdWithTypeMap);
			if (actualStdVarIdWithTypeMap != null) {
				Assert.assertTrue(actualStdVarIdWithTypeMap.containsValue(expectedVarWithTypeMap.get(name)));
			}
		}
	}

	@Test
	public void testGetOntologyTraitClasses() throws Exception {
		final List<TraitClassReference> traitClasses = CvTermDaoTest.dao.getTraitClasses(TermId.ONTOLOGY_TRAIT_CLASS);
		Assert.assertTrue(traitClasses.size() > 0);
		Debug.println(4, "testGetOntologyTraitClasses(): ");
		for (final TraitClassReference trait : traitClasses) {
			Debug.println(8, trait.toString());
		}
	}

	@Test
	public void testGetOntologyResearchClasses() throws Exception {
		final List<TraitClassReference> traitClasses = CvTermDaoTest.dao.getTraitClasses(TermId.ONTOLOGY_RESEARCH_CLASS);
		Assert.assertTrue(traitClasses.size() > 0);
		Debug.println(4, "testGetTraitClasses(): ");
		for (final TraitClassReference trait : traitClasses) {
			Debug.println(8, trait.toString());
		}
	}

	@Test
	public void testGetAllTraitClasses() throws Exception {
		final List<TraitClassReference> traitClasses = CvTermDaoTest.dao.getAllTraitClasses();
		Assert.assertTrue(traitClasses.size() > 0);
		Debug.println(4, "testGetAllTraitClasses(): ");
		for (final TraitClassReference trait : traitClasses) {
			Debug.println(8, trait.toString());
		}
	}

	@Test
	public void testGetPropertiesOfTraitClasses() throws Exception {

		final List<Integer> traitClassIds = Arrays.asList(1340, 1345, 1350, 1360, 1370, 1380, 1410, 22568);

		final Map<Integer, List<PropertyReference>> traitClassProperties = CvTermDaoTest.dao.getPropertiesOfTraitClasses(traitClassIds);
		Assert.assertTrue(traitClassProperties.size() > 0);
		Debug.println(4, "testGetPropertiesOfTraitClasses(): ");
		for (final Integer traitClassId : traitClassIds) {
			final List<PropertyReference> properties = traitClassProperties.get(traitClassId);
			if (properties != null) {
				Debug.println(4, traitClassId + " (size = " + properties.size() + ") : " + properties);
				for (final PropertyReference property : properties) {
					property.print(4);
				}
			} else {
				Debug.println(4, traitClassId + " (size = 0) : " + null);
			}
		}
	}

	@Test
	public void testGetStandardVariablesOfProperties() throws Exception {

		final List<Integer> propertyIds = Arrays.asList(1340, 2000, 2002, 2010, 2012, 2015, 2270);

		final Map<Integer, List<StandardVariableReference>> propertyVariables =
			CvTermDaoTest.dao.getStandardVariablesOfProperties(propertyIds);
		Assert.assertTrue(propertyVariables.size() > 0);
		Debug.println(4, "testGetStandardVariablesOfProperties(): ");
		for (final Integer id : propertyIds) {
			final List<StandardVariableReference> properties = propertyVariables.get(id);
			if (properties != null) {
				Debug.println(4, id + " (size = " + properties.size() + ") : " + properties);
			} else {
				Debug.println(4, id + " (size = 0) : " + null);
			}
		}
	}

	@Test
	public void testGetAllByCvId_CvIdAsInt() {
		final List<CVTerm> nonObsoleteMethods = dao.getAllByCvId(CvId.METHODS.getId(), true);
		Assert.assertNotNull(nonObsoleteMethods);
		for (final CVTerm cvTerm : nonObsoleteMethods) {
			Assert.assertEquals("All methods should have cv id " + CvId.METHODS.getId(), cvTerm.getCv().intValue(), CvId.METHODS.getId());
			Assert.assertFalse("Method " + cvTerm.getCvTermId() + " should be non-obsolete", cvTerm.isObsolete());
		}
	}

	@Test
	public void testGetAllByCvId_FilterObsolete() {
		final List<CVTerm> nonObsoleteProperties = dao.getAllByCvId(CvId.PROPERTIES, true);
		Assert.assertNotNull(nonObsoleteProperties);
		for (final CVTerm cvTerm : nonObsoleteProperties) {
			Assert.assertEquals("All properties should have cv id " + CvId.PROPERTIES.getId(), cvTerm.getCv().intValue(),
				CvId.PROPERTIES.getId());
			Assert.assertFalse("Property " + cvTerm.getCvTermId() + " should be non-obsolete", cvTerm.isObsolete());
		}
	}

	@Test
	public void testGetAllByCvId_DontFilterObsolete() {
		final List<CVTerm> nonObsoleteProperties = dao.getAllByCvId(CvId.PROPERTIES, true);
		final List<CVTerm> allProperties = dao.getAllByCvId(CvId.PROPERTIES, false);
		Assert.assertNotNull(allProperties);
		int numberOfObsoleteProperties = 0;
		for (final CVTerm cvTerm : allProperties) {
			Assert.assertEquals("All properties should have cv id " + CvId.PROPERTIES.getId(), cvTerm.getCv().intValue(),
				CvId.PROPERTIES.getId());
			if (cvTerm.isObsolete()) {
				numberOfObsoleteProperties++;
			}
		}
		final int expectedNumberOfNonObsoleteProperties = nonObsoleteProperties.size();
		final int actualNumberOfNonObsoleteProperties = allProperties.size() - numberOfObsoleteProperties;
		Assert.assertEquals("Non-obsolete properties should be " + expectedNumberOfNonObsoleteProperties,
			expectedNumberOfNonObsoleteProperties, actualNumberOfNonObsoleteProperties);
	}

	@Test
	public void testGetAllByCvId_ListOfCvTermIds_FilterObsolete() {
		final List<Integer> termIds = Arrays.asList(METHOD_APPLIED, METHOD_ASSIGNED, METHOD_ENUMERATED);
		final List<CVTerm> nonObsoleteMethods = dao.getAllByCvId(termIds, CvId.METHODS, true);
		Assert.assertNotNull(nonObsoleteMethods);
		Assert.assertEquals("Methods " + termIds + " should all be non-obsolete", 3, nonObsoleteMethods.size());
		for (final CVTerm cvTerm : nonObsoleteMethods) {
			Assert.assertEquals("All methods should have cv id " + CvId.METHODS.getId(), cvTerm.getCv().intValue(), CvId.METHODS.getId());
			Assert.assertFalse("Method " + cvTerm.getCvTermId() + " should be non-obsolete", cvTerm.isObsolete());
		}
	}

	@Test
	public void testGetAllByCvId_ListOfCvTermIds_DontFilterObsolete() {
		final boolean filterObsolete = false;
		final List<CVTerm> allProperties = dao.getAllByCvId(CvId.PROPERTIES, false);
		final List<Integer> obsoletePropertyIds = new ArrayList<>();
		for (final CVTerm cvTerm : allProperties) {
			if (cvTerm.isObsolete()) {
				obsoletePropertyIds.add(cvTerm.getCvTermId());
			}
		}
		if (!obsoletePropertyIds.isEmpty()) {
			final List<CVTerm> obsoleteProperties = dao.getAllByCvId(obsoletePropertyIds, CvId.PROPERTIES, false);
			Assert.assertNotNull(obsoleteProperties);
			Assert.assertEquals("Obsolete properties should be " + obsoletePropertyIds.size(), obsoletePropertyIds.size(),
				obsoleteProperties.size());
			for (final CVTerm cvTerm : obsoleteProperties) {
				Assert.assertEquals("All properties should have cv id " + CvId.PROPERTIES.getId(), cvTerm.getCv().intValue(),
					CvId.PROPERTIES.getId());
				Assert.assertTrue("Property " + cvTerm.getCvTermId() + " should be obsolete", cvTerm.isObsolete());
			}
		}
	}

	@Test
	public void testConvertToVariableDTO() {
		final Map<String, Object> map = new HashMap<>();
		map.put(CVTermDao.VARIABLE_ID, "123");
		map.put(CVTermDao.VARIABLE_NAME, "variableName");
		map.put(CVTermDao.VARIABLE_ALIAS, "alias");
		map.put(CVTermDao.VARIABLE_SCALE, "scale");
		map.put(CVTermDao.VARIABLE_SCALE_ID, "scaleId");
		map.put(CVTermDao.VARIABLE_METHOD, "method");
		map.put(CVTermDao.VARIABLE_METHOD_ID, "methodId");
		map.put(CVTermDao.VARIABLE_METHOD_DESCRIPTION, "methodDescription");
		map.put(CVTermDao.VARIABLE_PROPERTY, "property");
		map.put(CVTermDao.VARIABLE_PROPERTY_ID, "propertyId");
		map.put(CVTermDao.VARIABLE_PROPERTY_DESCRIPTION, "propertyDescription");
		map.put(CVTermDao.VARIABLE_PROPERTY_ONTOLOGY_ID, "propertyOntology");
		map.put(CVTermDao.VARIABLE_DATA_TYPE_ID, DataType.NUMERIC_VARIABLE.getId());
		map.put(CVTermDao.VARIABLE_SCALE_CATEGORIES, "a,1|b,2|c,3|d,4|e,5");
		map.put(CVTermDao.VARIABLE_SCALE_MIN_RANGE, new Double(100));
		map.put(CVTermDao.VARIABLE_SCALE_MAX_RANGE, new Double(1000));
		map.put(CVTermDao.VARIABLE_EXPECTED_MIN, new Double(1));
		map.put(CVTermDao.VARIABLE_EXPECTED_MAX, new Double(10));
		map.put(CVTermDao.VARIABLE_CREATION_DATE, "variableCreationDate");
		map.put(CVTermDao.VARIABLE_TRAIT_CLASS, "traitClass");
		map.put(CVTermDao.VARIABLE_FORMULA_DEFINITION, "formulaDefinition");

		final List<VariableDTO> variableDTOs = dao.convertToVariableDTO(Lists.newArrayList(map));
		final VariableDTO variableDTO = variableDTOs.get(0);

		Assert.assertEquals(map.get(CVTermDao.VARIABLE_ID), variableDTO.getObservationVariableDbId());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_ALIAS), variableDTO.getObservationVariableName());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_ALIAS), variableDTO.getName());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_CREATION_DATE), variableDTO.getDate());

		Assert.assertEquals(map.get(CVTermDao.VARIABLE_PROPERTY), variableDTO.getTrait().getName());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_PROPERTY), variableDTO.getTrait().getTraitName());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_PROPERTY_ID), variableDTO.getTrait().getTraitDbId());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_PROPERTY_DESCRIPTION), variableDTO.getTrait().getDescription());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_TRAIT_CLASS), variableDTO.getTrait().getTraitClassAttribute());
		Assert.assertEquals("Active", variableDTO.getTrait().getStatus());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_PROPERTY_ONTOLOGY_ID), variableDTO.getTrait().getXref());

		Assert.assertEquals(map.get(CVTermDao.VARIABLE_PROPERTY_ONTOLOGY_ID),
			variableDTO.getTrait().getOntologyReference().getOntologyDbId());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_PROPERTY), variableDTO.getTrait().getOntologyReference().getOntologyName());

		Assert.assertEquals(map.get(CVTermDao.VARIABLE_SCALE), variableDTO.getScale().getName());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_SCALE), variableDTO.getScale().getScaleName());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_SCALE_ID), variableDTO.getScale().getScaleDbId());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_EXPECTED_MIN), variableDTO.getScale().getValidValues().getMin());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_EXPECTED_MAX), variableDTO.getScale().getValidValues().getMax());
		Assert.assertEquals(DataType.NUMERIC_VARIABLE.getBrapiName(), variableDTO.getScale().getDataType());
		Assert.assertEquals(4, variableDTO.getScale().getDecimalPlaces().intValue());
		Assert.assertEquals(5, variableDTO.getScale().getValidValues().getCategories().size());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_SCALE), variableDTO.getScale().getOntologyReference().getOntologyName());

		Assert.assertEquals(map.get(CVTermDao.VARIABLE_METHOD), variableDTO.getMethod().getName());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_METHOD), variableDTO.getMethod().getMethodName());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_METHOD_ID), variableDTO.getMethod().getMethodDbId());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_METHOD_DESCRIPTION), variableDTO.getMethod().getDescription());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_FORMULA_DEFINITION), variableDTO.getMethod().getFormula());
		Assert.assertEquals(map.get(CVTermDao.VARIABLE_METHOD), variableDTO.getMethod().getOntologyReference().getOntologyName());

	}

	@Test
	public void testGetDataTypeBrapiName() {

		Assert.assertEquals(DataType.NUMERIC_VARIABLE.getBrapiName(), dao.getDataTypeBrapiName(DataType.NUMERIC_VARIABLE.getId()));
		Assert.assertEquals(DataType.DATE_TIME_VARIABLE.getBrapiName(), dao.getDataTypeBrapiName(DataType.DATE_TIME_VARIABLE.getId()));
		Assert.assertEquals(DataType.CATEGORICAL_VARIABLE.getBrapiName(), dao.getDataTypeBrapiName(DataType.CATEGORICAL_VARIABLE.getId()));
		Assert.assertEquals(DataType.CHARACTER_VARIABLE.getBrapiName(), dao.getDataTypeBrapiName(DataType.CHARACTER_VARIABLE.getId()));
		Assert.assertEquals("", dao.getDataTypeBrapiName(DataType.DATASET.getId()));

	}

	private DmsProject createProject(final String name, final String programUUID) {
		return this.createProject(name, programUUID, true);
	}

	private DmsProject createProject(final String name, final String programUUID, final boolean isStudy) {
		final DmsProject project = new DmsProject();
		project.setName(name);
		project.setDescription(name + RandomStringUtils.randomAlphabetic(20));
		project.setProgramUUID(programUUID);
		if (isStudy) {
			final StudyType studyType = new StudyType();
			studyType.setStudyTypeId(STUDY_TYPE_ID);
			project.setStudyType(studyType);

			final DmsProject parent = new DmsProject();
			parent.setProjectId(DmsProject.SYSTEM_FOLDER_ID);
			project.setParent(parent);

			project.setObjective(RandomStringUtils.randomAlphabetic(20));
			project.setStartDate("20190101");
			project.setEndDate("20190630");
		}
		this.daoFactory.getDmsProjectDAO().save(project);
		return project;
	}

}
