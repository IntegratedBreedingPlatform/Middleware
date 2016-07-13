package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.collections.MultiHashMap;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.dao.oms.VariableOverridesDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.manager.ontology.api.OntologyMethodDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyPropertyDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.olap4j.impl.ArrayMap;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Integration Test Class to handle Ontology Variable Data Manager
 */
public class OntologyVariableDataManagerImplIntegrationTest extends IntegrationTestBase {

    @Autowired
    private OntologyMethodDataManager methodDataManager;

	@Autowired
	private OntologyScaleDataManager scaleDataManager;

	@Autowired
	private OntologyPropertyDataManager propertyDataManager;

	@Autowired
	private OntologyVariableDataManager variableDataManager;

    @Autowired
    private OntologyDaoFactory daoFactory;

	private CVDao cvDao;
    private CVTermDao termDao;
    private CvTermPropertyDao propertyDao;
    private CVTermRelationshipDao relationshipDao;
	private VariableOverridesDao variableOverridesDao;

	private Method method;
	private Property property;
	private Scale scale;
	private Variable variable;

	private final String DUMMY_PROGRAM_UUID = UUID.randomUUID().toString();
	private final String test_database = "test_ibdbv2_maize_merged";


    @Before
    public void setUp() throws Exception {
		this.cvDao = this.daoFactory.getCvDao();
        this.termDao = this.daoFactory.getCvTermDao();
        this.propertyDao = this.daoFactory.getCvTermPropertyDao();
        this.relationshipDao = this.daoFactory.getCvTermRelationshipDao();
		this.variableOverridesDao = this.daoFactory.getVariableProgramOverridesDao();
		ContextHolder.setCurrentCrop(test_database);
    }

	/**
	 * Test to verify get variable method fetch all variables
	 * Step 1. Create 3 variable terms
	 * Step 2. Create Method and its Created Date and Updated Date properties
	 * Step 3. Create Property and Is A Term, Crop Ontology Id, Created & Updated Date properties
	 * Step 4. Create Scale and its Data Type and values, Created & Updated Date properties
	 * Step 5. Set Relationships of Method, Property and Scale to variable
	 * Step 6. Set Expected Range for Variable using variable overrides dao
	 * Step 7. Set Min and Max values of Variable using variable overrides dao
	 * Step 8. Set Variable Type and Created & Updated Date for variable
	 * Step 9. Save Variable
	 * Step 10. Fetch variable and assert its values and relationships
	 * @throws Exception
	 */
    @Test
    public void getVariablesShouldGetFullVariables() throws Exception {
		List<CVTerm> variableTerms = new ArrayList<>();
		// Fill 3 variables using TestDataHelper
		TestDataHelper.fillTestVariableCvTerms(variableTerms, 3);

		Map<Integer, CVTerm> termMap = new HashMap<>();
		Map<Integer, CVTerm> relationshipMap = new MultiHashMap();

		Map<Integer, String> methodCreatedDatePropertyMap = new HashMap<>();
		Map<Integer, String> methodUpdatedDatePropertyMap = new HashMap<>();
		Map<Integer, String> propertyCreatedDatePropsMap = new HashMap<>();
		Map<Integer, String> propertyUpdatedDatePropsMap = new HashMap<>();
		Map<Integer, String> scaleCreatedDatePropertyMap = new HashMap<>();
		Map<Integer, String> scaleUpdatedDatePropertyMap = new HashMap<>();
		Map<Integer, CVTerm> isATermMap = new HashMap<>();
		Map<Integer, String> cropOntologyIdMap = new HashMap<>();
		Map<Integer, DataType> dataTypeMap = new HashMap<>();
		Map<Integer, String> maxValuesMap = new HashMap<>();
		Map<Integer, String> minValuesMap = new ArrayMap<>();
		Map<Integer, VariableType> variableTypeMap = new HashMap<>();
		Map<Integer, Integer> expectedMaxValuesMap = new HashMap<>();
		Map<Integer, Integer> expectedMinValuesMap = new ArrayMap<>();

		for (CVTerm term : variableTerms) {

			// Create a Method and fill its properties
			method = TestDataHelper.createMethod(this.termDao, methodDataManager);
			TestDataHelper.fillTestDateProperties(this.propertyDao, method.toCVTerm(), true, methodCreatedDatePropertyMap);
			TestDataHelper.fillTestDateProperties(this.propertyDao, method.toCVTerm(), false, methodUpdatedDatePropertyMap);

			// Create a Property and fill is a term relationship and crop ontology id
			property = TestDataHelper.createProperty(this.termDao, propertyDataManager);
			TestDataHelper.fillIsAPropertyMap(this.relationshipDao, termDao, property.toCVTerm(), isATermMap);
			TestDataHelper.fillCropOntologyIdMap(this.propertyDao, property.toCVTerm(), cropOntologyIdMap);

			TestDataHelper.fillTestDateProperties(this.propertyDao, property.toCVTerm(), true, propertyCreatedDatePropsMap);
			TestDataHelper.fillTestDateProperties(this.propertyDao, property.toCVTerm(), false, propertyUpdatedDatePropsMap);

			// Create a Scale and set data type and its values
			scale = TestDataHelper.createScale(this.termDao, scaleDataManager);
			TestDataHelper.fillDataTypeMap(this.relationshipDao, scale.toCVTerm(), dataTypeMap);
			TestDataHelper.fillMaxValuesMap(this.propertyDao, scale.toCVTerm(), maxValuesMap);
			TestDataHelper.fillMinValuesMap(this.propertyDao, scale.toCVTerm(), minValuesMap);

			TestDataHelper.fillTestDateProperties(this.propertyDao, scale.toCVTerm(), true, scaleCreatedDatePropertyMap);
			TestDataHelper.fillTestDateProperties(this.propertyDao, scale.toCVTerm(), false, scaleUpdatedDatePropertyMap);

			this.termDao.save(term);
			termMap.put(term.getCvTermId(), term);

			this.relationshipDao.save(term.getCvTermId(), TermId.HAS_METHOD.getId(), method.getId());
			this.relationshipDao.save(term.getCvTermId(), TermId.HAS_PROPERTY.getId(), property.getId());
			this.relationshipDao.save(term.getCvTermId(), TermId.HAS_SCALE.getId(), scale.getId());

			relationshipMap.put(term.getCvTermId(), method.toCVTerm());
			relationshipMap.put(term.getCvTermId(), property.toCVTerm());
			relationshipMap.put(term.getCvTermId(), scale.toCVTerm());

			// Get Min and Max value of scale and generate expected Min and Max value for variable
			String maxValue = maxValuesMap.get(scale.getId());
			String minValue = minValuesMap.get(scale.getId());

			Integer range = (Integer.parseInt(maxValue) - Integer.parseInt(minValue)) + Integer.parseInt(minValue);

			Integer variableMinValue = TestDataHelper.randomWithRange(Integer.parseInt(minValue), range);
			Integer variableMaxValue = TestDataHelper.randomWithRange(range+1, Integer.parseInt(maxValue));

			this.variableOverridesDao.save(term.getCvTermId(), this.DUMMY_PROGRAM_UUID, "", variableMinValue.toString(), variableMaxValue.toString());

			expectedMaxValuesMap.put(term.getCvTermId(), variableMaxValue);
			expectedMinValuesMap.put(term.getCvTermId(), variableMinValue);
		}

		this.propertyDao.updateOrDeleteProperty(variableTerms.get(0).getCvTermId(), TermId.VARIABLE_TYPE.getId(), VariableType.ANALYSIS.getName(), 0);
		this.propertyDao.updateOrDeleteProperty(variableTerms.get(1).getCvTermId(), TermId.VARIABLE_TYPE.getId(), VariableType.EXPERIMENTAL_DESIGN.getName(), 0);
		this.propertyDao.updateOrDeleteProperty(variableTerms.get(2).getCvTermId(), TermId.VARIABLE_TYPE.getId(), VariableType.SELECTION_METHOD.getName(), 0);

		variableTypeMap.put(variableTerms.get(0).getCvTermId(), VariableType.ANALYSIS);
		variableTypeMap.put(variableTerms.get(1).getCvTermId(), VariableType.EXPERIMENTAL_DESIGN);
		variableTypeMap.put(variableTerms.get(2).getCvTermId(), VariableType.SELECTION_METHOD);

		Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> variableCreatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(variableTerms, variableCreatedDateProperties, testCreatedDate);

		Map<Integer, String> variableCreatedDateMap = new HashMap<>();

		// Fetch Created Date Properties and save it using propertyDao
		for (CVTermProperty createdDateProperty : variableCreatedDateProperties) {
			this.propertyDao.save(createdDateProperty);
			variableCreatedDateMap.put(createdDateProperty.getCvTermId(), createdDateProperty.getValue());
		}

		Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.MAY, 20);
		List<CVTermProperty> variableUpdatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(variableTerms, variableUpdatedDateProperties, testUpdatedDate);

		Map<Integer, String> variableUpdatedDateMap = new HashMap<>();

		// Fetch Updated Date Property and save it using propertyDao
		for (CVTermProperty updatedDateProperty : variableUpdatedDateProperties) {
			this.propertyDao.save(updatedDateProperty);
			variableUpdatedDateMap.put(updatedDateProperty.getCvTermId(), updatedDateProperty.getValue());
		}

		for (CVTerm variableTerm : variableTerms) {
			variable = this.variableDataManager.getVariable(this.DUMMY_PROGRAM_UUID, variableTerm.getCvTermId(), true, false);

			// Make sure our variable exists and is inserted properly and display proper message if it is not inserted properly
			String message = "The %s for variable '" + variableTerm.getCvTermId() + "' was not added correctly.";

			if (termMap.containsKey(variable.getId())) {
				CVTerm term = termMap.get(variableTerm.getCvTermId());

				Assert.assertEquals(String.format(message, "Name"), term.getName(), variableTerm.getName());
				Assert.assertEquals(String.format(message, "Definition"), term.getDefinition(), variableTerm.getDefinition());
				Assert.assertEquals(String.format(message, "IsObsolete"), term.isObsolete(), variableTerm.isObsolete());

				if (variableTypeMap.containsKey(variable.getId())) {
					// Check for Variable Type
					VariableType variableType = variableTypeMap.get(variable.getId());
					Assert.assertTrue(String.format(message, "Variable Type"), variable.getVariableTypes().contains(variableType));
				}

				String expectedMin = null, expectedMax = null;
				if(expectedMinValuesMap.containsKey(variable.getId())) {
					// Check for Expected Min value
					expectedMin = expectedMinValuesMap.get(variable.getId()).toString();
					Assert.assertEquals(String.format(message, "Expected Min"), expectedMin, variable.getMinValue());
				}

				if(expectedMaxValuesMap.containsKey(variable.getId())) {
					// Check for Expected Max value
					expectedMax = expectedMaxValuesMap.get(variable.getId()).toString();
					Assert.assertEquals(String.format(message, "Expected Max"), expectedMax, variable.getMaxValue());
				}

				Assert.assertNotNull(String.format(message, "Expected Max"), expectedMax);
				Assert.assertNotNull(String.format(message, "Expected Min"), expectedMin);

				Integer max = Integer.parseInt(expectedMax);
				Integer min = Integer.parseInt(expectedMin);

				Assert.assertTrue(String.format(message, "Expected Range"), max > min);

				// Check for Created Date & Updated Date Property for Variable
				String createdDateForVariable = variableCreatedDateMap.get(variable.getId());
				String updatedDateForVariable = variableUpdatedDateMap.get(variable.getId());

				Assert.assertEquals(String.format(message, "CreatedDateForVariable"), createdDateForVariable, ISO8601DateParser.toString(variable.getDateCreated()));
				Assert.assertEquals(String.format(message, "UpdatedDateForVariable"), updatedDateForVariable, ISO8601DateParser.toString(variable.getDateLastModified()));

				for(CVTerm key: relationshipMap.values()) {

					if (Objects.equals(key.getCvTermId(), variable.getMethod().getId())) {
						// Check for Method
						Assert.assertEquals(String.format(message, "Method Name"), key.getName(), variable.getMethod().getName());
						Assert.assertEquals(String.format(message, "Method Definition"), key.getDefinition(), variable.getMethod().getDefinition());

						if(methodCreatedDatePropertyMap.containsKey(key.getCvTermId())) {
							String createdDateProperty = methodCreatedDatePropertyMap.get(key.getCvTermId());

							Assert.assertEquals(String.format(message, "CreatedDateForMethod"), createdDateProperty,
									ISO8601DateParser.toString(variable.getMethod().getDateCreated()));
						}

						if(methodUpdatedDatePropertyMap.containsKey(key.getCvTermId())) {
							String updatedDateProperty = methodUpdatedDatePropertyMap.get(key.getCvTermId());

							Assert.assertEquals(String.format(message, "UpdatedDateForMethod"), updatedDateProperty,
									ISO8601DateParser.toString(variable.getMethod().getDateLastModified()));
						}

					} else if (Objects.equals(key.getCvTermId(), variable.getProperty().getId())) {
						// Check for Property
						Assert.assertEquals(String.format(message, "Property Name"), key.getName(), variable.getProperty().getName());
						Assert.assertEquals(String.format(message, "Property Definition"), key.getDefinition(), variable.getProperty().getDefinition());

						if (isATermMap.containsKey(key.getCvTermId())) {
							CVTerm cvTerm = isATermMap.get(key.getCvTermId());
							Assert.assertTrue(String.format(message, "Is A Term"), variable.getProperty().getClasses().contains(cvTerm.getName()));
						}

						if (cropOntologyIdMap.containsKey(key.getCvTermId())) {
							String cropOntologyId = cropOntologyIdMap.get(key.getCvTermId());
							Assert.assertEquals(String.format(message, "CropOntologyId"), cropOntologyId, variable.getProperty().getCropOntologyId());
						}

						if(propertyCreatedDatePropsMap.containsKey(key.getCvTermId())) {
							String createdDateProperty = propertyCreatedDatePropsMap.get(key.getCvTermId());

							Assert.assertEquals(String.format(message, "CreatedDateForProperty"), createdDateProperty,
									ISO8601DateParser.toString(variable.getProperty().getDateCreated()));
						}

						if(propertyUpdatedDatePropsMap.containsKey(key.getCvTermId())) {
							String updatedDateProperty = propertyUpdatedDatePropsMap.get(key.getCvTermId());

							Assert.assertEquals(String.format(message, "UpdatedDateForProperty"), updatedDateProperty,
									ISO8601DateParser.toString(variable.getProperty().getDateLastModified()));
						}

					} else if (Objects.equals(key.getCvTermId(), variable.getScale().getId())) {
						// Check for Scale
						Assert.assertEquals(String.format(message, "Scale Name"), key.getName(), variable.getScale().getName());
						Assert.assertEquals(String.format(message, "Scale Definition"), key.getDefinition(), variable.getScale().getDefinition());

						if(dataTypeMap.containsKey(key.getCvTermId())) {
							DataType dataType = dataTypeMap.get(key.getCvTermId());

							Assert.assertEquals(String.format(message, "Data Type"), dataType, variable.getScale().getDataType());
						}

						if(minValuesMap.containsKey(key.getCvTermId())) {
							String minValue = minValuesMap.get(key.getCvTermId());

							Assert.assertEquals(String.format(message, "Min Value"), minValue, variable.getScale().getMinValue());
						}

						if(maxValuesMap.containsKey(key.getCvTermId())) {
							String maxValue = maxValuesMap.get(key.getCvTermId());

							Assert.assertEquals(String.format(message, "Max Value"), maxValue, variable.getScale().getMaxValue());
						}

						if(scaleCreatedDatePropertyMap.containsKey(key.getCvTermId())) {
							String createdDateProperty = scaleCreatedDatePropertyMap.get(key.getCvTermId());

							Assert.assertEquals(String.format(message, "CreatedDateForScale"), createdDateProperty,
									ISO8601DateParser.toString(variable.getScale().getDateCreated()));
						}

						if(scaleUpdatedDatePropertyMap.containsKey(key.getCvTermId())) {
							String updatedDateProperty = scaleUpdatedDatePropertyMap.get(key.getCvTermId());

							Assert.assertEquals(String.format(message, "UpdatedDateForScale"), updatedDateProperty,
									ISO8601DateParser.toString(variable.getScale().getDateLastModified()));
						}

					}
				}
			}
		}
	}

	/**
	 * Test to verify Add Variable should add a new Variable properly
	 * @throws Exception
	 */
	@Test
	public void testAddVariableShouldAddNewVariable() throws Exception {
		// Create a Method
		method = TestDataHelper.createMethod(termDao, methodDataManager);

		// Create a Property and fill Is-A term relationship and crop ontology id
		property = TestDataHelper.createProperty(termDao, propertyDataManager);

		// Create a Scale and set data type and its values
		scale = TestDataHelper.createScale(termDao, scaleDataManager);

		String expectedMin = "1";
		String expectedMax = "50";

		OntologyVariableInfo ontologyVariableInfo = new OntologyVariableInfo();
		ontologyVariableInfo.setName(TestDataHelper.getNewRandomName("Name"));
		ontologyVariableInfo.setDescription("Description");
		ontologyVariableInfo.setIsFavorite(true);
		ontologyVariableInfo.setMethodId(method.getId());
		ontologyVariableInfo.setPropertyId(property.getId());
		ontologyVariableInfo.setScaleId(scale.getId());
		ontologyVariableInfo.setExpectedMin(expectedMin);
		ontologyVariableInfo.setExpectedMax(expectedMax);
		ontologyVariableInfo.setProgramUuid(this.DUMMY_PROGRAM_UUID);
		ontologyVariableInfo.addVariableType(VariableType.ANALYSIS);

		Date date = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		this.stubCurrentDate(date);

		this.variableDataManager.addVariable(ontologyVariableInfo);

		CVTerm variable = this.termDao.getById(ontologyVariableInfo.getId());

		// Make sure each variable data inserted properly, assert them and display proper message if not inserted properly
		String message = "The %s for Variable '" + variable.getCvTermId() + "' was not added correctly.";
		Assert.assertEquals(String.format(message, "Name"), variable.getName(), ontologyVariableInfo.getName());
		Assert.assertEquals(String.format(message, "Definition"), variable.getDefinition(), ontologyVariableInfo.getDescription());
		Assert.assertEquals(String.format(message, "IsFavorite"), true, ontologyVariableInfo.isFavorite());
		Assert.assertEquals(String.format(message, "MethodId"), method.getId(), ontologyVariableInfo.getMethodId().intValue());
		Assert.assertEquals(String.format(message, "PropertyId"), property.getId(), ontologyVariableInfo.getPropertyId().intValue());
		Assert.assertEquals(String.format(message, "ScaleId"), scale.getId(), ontologyVariableInfo.getScaleId().intValue());
		Assert.assertTrue(String.format(message, "Variable Type"), ontologyVariableInfo.getVariableTypes().contains(VariableType.ANALYSIS));
		Assert.assertEquals(String.format(message, "Expected Min"), expectedMin, ontologyVariableInfo.getExpectedMin());
		Assert.assertEquals(String.format(message, "Expected Max"), expectedMax, ontologyVariableInfo.getExpectedMax());

		// Fetch Created date property and assert it
		List<CVTermProperty> addedProperties = this.propertyDao.getByCvTermId(variable.getCvTermId());
		// Added 2 properties: Variable Type and Created Date
		Assert.assertTrue(String.format(message, "Property Size"), addedProperties.size() == 2);

		for(CVTermProperty property : addedProperties) {
			if (Objects.equals(property.getTypeId(), TermId.CREATION_DATE.getId())) {
				Assert.assertEquals(String.format(message, "CreatedDate"), property.getValue(), ISO8601DateParser.toString(date));
			} else if (Objects.equals(property.getTypeId(), TermId.VARIABLE_TYPE.getId())) {
				Assert.assertEquals(String.format(message, "Variable Type"), property.getValue(), VariableType.ANALYSIS.getName());
			}
		}
	}

	/**
	 * Test to verify update variable should update variable properly
	 * @throws Exception
	 */
	@Test
	public void testUpdateVariableShouldUpdateExistingVariable() throws Exception {
		CVTerm variableTerm = TestDataHelper.getTestCvTerm(CvId.VARIABLES);
		this.termDao.save(variableTerm);

		Map<Integer, CVTerm> isATermMap = new HashMap<>();
		Map<Integer, String> cropOntologyIdMap = new HashMap<>();
		Map<Integer, String> maxValuesMap = new HashMap<>();
		Map<Integer, String> minValuesMap = new ArrayMap<>();

		// Create a Method and fill its properties
		method = TestDataHelper.createMethod(this.termDao, methodDataManager);

		// Create a Property and fill is a term relationship and crop ontology id
		property = TestDataHelper.createProperty(this.termDao, propertyDataManager);
		TestDataHelper.fillIsAPropertyMap(this.relationshipDao, termDao, property.toCVTerm(), isATermMap);
		TestDataHelper.fillCropOntologyIdMap(this.propertyDao, property.toCVTerm(), cropOntologyIdMap);

		// Create a Scale and set data type and its values
		scale = TestDataHelper.createScale(this.termDao, scaleDataManager);
		relationshipDao.save(scale.getId(), TermId.HAS_TYPE.getId(), DataType.NUMERIC_VARIABLE.getId());

		propertyDao.updateOrDeleteProperty(scale.getId(), TermId.MIN_VALUE.getId(), "10", 0);
		propertyDao.updateOrDeleteProperty(scale.getId(), TermId.MAX_VALUE.getId(), "100", 0);

		TestDataHelper.fillMaxValuesMap(this.propertyDao, scale.toCVTerm(), maxValuesMap);
		TestDataHelper.fillMinValuesMap(this.propertyDao, scale.toCVTerm(), minValuesMap);

		this.termDao.save(variableTerm);

		this.relationshipDao.save(variableTerm.getCvTermId(), TermId.HAS_METHOD.getId(), method.getId());
		this.relationshipDao.save(variableTerm.getCvTermId(), TermId.HAS_PROPERTY.getId(), property.getId());
		this.relationshipDao.save(variableTerm.getCvTermId(), TermId.HAS_SCALE.getId(), scale.getId());

		// Get Min and Max value of scale and generate expected Min and Max value for variable
		String maxValue = maxValuesMap.get(scale.getId());
		String minValue = minValuesMap.get(scale.getId());

		Integer range = (Integer.parseInt(maxValue) - Integer.parseInt(minValue)) + Integer.parseInt(minValue);

		Integer variableMinValue = TestDataHelper.randomWithRange(Integer.parseInt(minValue), range);
		Integer variableMaxValue = TestDataHelper.randomWithRange(range+1, Integer.parseInt(maxValue));

		this.variableOverridesDao.save(variableTerm.getCvTermId(), this.DUMMY_PROGRAM_UUID, "", variableMinValue.toString(), variableMaxValue.toString());

		// Set Variable Type
		this.propertyDao.updateOrDeleteProperty(variableTerm.getCvTermId(), TermId.VARIABLE_TYPE.getId(), VariableType.ANALYSIS.getName(), 0);

		// Adding Created Date Property
		Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(variableTerm), createdDateProperties, testCreatedDate);

		CVTermProperty createProperty = createdDateProperties.get(0);
		this.propertyDao.save(createProperty);

		String expectedMin = "10";
		String expectedMax = "20";
		String cropOntologyId = "CO:888";

		// Update Variable Info
		OntologyVariableInfo variable = new OntologyVariableInfo();
		variable.setId(variableTerm.getCvTermId());
		variable.setName("Variable Name");
		variable.setIsFavorite(false);
		Method updatedMethod = TestDataHelper.createMethod(this.termDao, methodDataManager);
		variable.setMethodId(updatedMethod.getId());
		variable.setExpectedMin(expectedMin);
		variable.setExpectedMax(expectedMax);
		property.setCropOntologyId(cropOntologyId);
		variable.setPropertyId(property.getId());
		variable.setScaleId(scale.getId());

		Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		this.stubCurrentDate(testUpdatedDate);

		this.variableDataManager.updateVariable(variable);

		CVTerm cvterm = this.termDao.getById(variable.getId());

		// Make sure the inserted data should come as they are inserted and Display proper message if the data doesn't come as expected
		String message = "The %s for variable '" + variable.getId() + "' was not updated correctly.";
		Assert.assertEquals(String.format(message, "Name"), variable.getName(), cvterm.getName());
		Assert.assertEquals(String.format(message, "Definition"), variable.getDescription(), cvterm.getDefinition());
		Assert.assertEquals(String.format(message, "MethodId"), variable.getMethodId().intValue(), updatedMethod.getId());
		Assert.assertEquals(String.format(message, "ScaleId"), variable.getScaleId().intValue(), scale.getId());
		Assert.assertEquals(String.format(message, "PropertyId"), variable.getPropertyId().intValue(), property.getId());
		Assert.assertEquals(String.format(message, "Expected Min"), variable.getExpectedMin(), expectedMin);
		Assert.assertEquals(String.format(message, "Expected Max"), variable.getExpectedMax(), expectedMax);
		Assert.assertEquals(String.format(message, "CropOntologyId"), property.getCropOntologyId(), cropOntologyId);
		Assert.assertEquals(String.format(message, "IsObsolete"), false, cvterm.isObsolete());
		Assert.assertEquals(String.format(message, "IsFavorite"), false, variable.isFavorite());

		// Fetch Created and Updated Date property and assert it
		List<CVTermProperty> updatedProperties = this.propertyDao.getByCvTermId(cvterm.getCvTermId());

		Assert.assertTrue(String.format(message, "Property Size"), updatedProperties.size() == 2);

		for (CVTermProperty cvTermProperty : updatedProperties) {
			if (Objects.equals(cvTermProperty.getTypeId(), TermId.CREATION_DATE.getId())) {
				Assert.assertEquals(String.format(message, "Created Date"), cvTermProperty.getValue(), ISO8601DateParser.toString(testCreatedDate));
			} else if (Objects.equals(cvTermProperty.getTypeId(), TermId.LAST_UPDATE_DATE.getId())) {
				Assert.assertEquals(String.format(message, "Updated Date"), cvTermProperty.getValue(), ISO8601DateParser.toString(testUpdatedDate));
			}
		}
	}

	/**
	 * Test to verify Delete Variable should delete variable and its relationships with method, property and scale properly
	 * @throws Exception
	 */
	@Test
	public void testDeleteVariableShouldDeleteExistingVariable() throws Exception {
		// Add Variable Term
		CVTerm variableTerm = TestDataHelper.getTestCvTerm(CvId.VARIABLES);
		this.termDao.save(variableTerm);

		Map<Integer, CVTerm> isATermMap = new HashMap<>();
		Map<Integer, String> cropOntologyIdMap = new HashMap<>();
		Map<Integer, String> maxValuesMap = new HashMap<>();
		Map<Integer, String> minValuesMap = new ArrayMap<>();

		// Create a Method
		method = TestDataHelper.createMethod(this.termDao, methodDataManager);

		// Create a Property and fill is a term relationship and crop ontology id
		property = TestDataHelper.createProperty(this.termDao, propertyDataManager);
		TestDataHelper.fillIsAPropertyMap(this.relationshipDao, termDao, property.toCVTerm(), isATermMap);
		TestDataHelper.fillCropOntologyIdMap(this.propertyDao, property.toCVTerm(), cropOntologyIdMap);

		// Create a Scale and set data type and its values
		scale = TestDataHelper.createScale(this.termDao, scaleDataManager);
		relationshipDao.save(scale.getId(), TermId.HAS_TYPE.getId(), DataType.NUMERIC_VARIABLE.getId());

		propertyDao.updateOrDeleteProperty(scale.getId(), TermId.MIN_VALUE.getId(), "10", 0);
		propertyDao.updateOrDeleteProperty(scale.getId(), TermId.MAX_VALUE.getId(), "100", 0);

		TestDataHelper.fillMaxValuesMap(this.propertyDao, scale.toCVTerm(), maxValuesMap);
		TestDataHelper.fillMinValuesMap(this.propertyDao, scale.toCVTerm(), minValuesMap);

		this.termDao.save(variableTerm);

		this.relationshipDao.save(variableTerm.getCvTermId(), TermId.HAS_METHOD.getId(), method.getId());
		this.relationshipDao.save(variableTerm.getCvTermId(), TermId.HAS_PROPERTY.getId(), property.getId());
		this.relationshipDao.save(variableTerm.getCvTermId(), TermId.HAS_SCALE.getId(), scale.getId());

		// Get Min and Max value of scale and generate expected Min and Max value for variable
		String maxValue = maxValuesMap.get(scale.getId());
		String minValue = minValuesMap.get(scale.getId());

		Integer range = (Integer.parseInt(maxValue) - Integer.parseInt(minValue)) + Integer.parseInt(minValue);

		Integer variableMinValue = TestDataHelper.randomWithRange(Integer.parseInt(minValue), range);
		Integer variableMaxValue = TestDataHelper.randomWithRange(range+1, Integer.parseInt(maxValue));

		this.variableOverridesDao.save(variableTerm.getCvTermId(), this.DUMMY_PROGRAM_UUID, "", variableMinValue.toString(), variableMaxValue.toString());

		// Set Variable Type
		this.propertyDao.updateOrDeleteProperty(variableTerm.getCvTermId(), TermId.VARIABLE_TYPE.getId(), VariableType.ANALYSIS.getName(), 0);

		// Adding Created Date Property
		Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
		List<CVTermProperty> createdDateProperties = new ArrayList<>();
		TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(variableTerm), createdDateProperties, testCreatedDate);

		CVTermProperty createProperty = createdDateProperties.get(0);
		this.propertyDao.save(createProperty);

		// Fill Test Updated Date Property using TestDataHelper
		Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.MAY, 20);
		List<CVTermProperty> scaleUpdatedDateProperties = new ArrayList<>();
		TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(variableTerm), scaleUpdatedDateProperties, testUpdatedDate);

		CVTermProperty scaleUpdatedDateProperty = scaleUpdatedDateProperties.get(0);
		this.propertyDao.save(scaleUpdatedDateProperty);

		this.variableDataManager.deleteVariable(variableTerm.getCvTermId());

		CVTerm cvTerm = this.termDao.getById(variableTerm.getCvTermId());

		// Make sure the variable must be deleted and it asserts null
		String message = "The %s for Variable '" + variableTerm.getCvTermId() + "' was not deleted correctly.";
		Assert.assertNull(String.format(message, "Term"), cvTerm);

		// Make sure the properties must be deleted
		List<CVTermProperty> deletedProperties = this.propertyDao.getByCvTermId(variableTerm.getCvTermId());
		Assert.assertTrue(String.format(message, "Properties"), deletedProperties.size() == 0);

		// Make sure the relationships of Variable with Method, Scale and Property must be deleted
		List<CVTermRelationship> deletedRelationships = this.relationshipDao.getBySubject(variableTerm.getCvTermId());
		Assert.assertTrue(String.format(message, "Variable Relationships"), Objects.equals(deletedRelationships.size(), 0));
	}
}
