package org.generationcp.middleware.manager.ontology;

import java.util.ArrayList;
import java.util.Calendar;
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
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
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



}
