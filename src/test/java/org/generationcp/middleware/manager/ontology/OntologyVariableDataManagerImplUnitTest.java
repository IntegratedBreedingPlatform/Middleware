package org.generationcp.middleware.manager.ontology;

import org.generationcp.middleware.UnitTestBase;
import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.dao.dms.ProgramFavoriteDAO;
import org.generationcp.middleware.dao.oms.CVDao;
import org.generationcp.middleware.dao.oms.CVTermDao;
import org.generationcp.middleware.dao.oms.CVTermRelationshipDao;
import org.generationcp.middleware.dao.oms.CvTermPropertyDao;
import org.generationcp.middleware.dao.oms.CvTermSynonymDao;
import org.generationcp.middleware.dao.oms.VariableOverridesDao;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.oms.TermSummary;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.Method;
import org.generationcp.middleware.domain.ontology.Property;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.TermRelationshipId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.ontology.api.OntologyCommonDAO;
import org.generationcp.middleware.manager.ontology.daoElements.OntologyVariableInfo;
import org.generationcp.middleware.manager.ontology.daoElements.VariableFilter;
import org.generationcp.middleware.manager.ontology.daoElements.VariableInfoDaoElements;
import org.generationcp.middleware.pojos.dms.ProgramFavorite;
import org.generationcp.middleware.pojos.oms.CVTerm;
import org.generationcp.middleware.pojos.oms.CVTermProperty;
import org.generationcp.middleware.pojos.oms.CVTermRelationship;
import org.generationcp.middleware.pojos.oms.CVTermSynonym;
import org.generationcp.middleware.pojos.oms.VariableOverrides;
import org.generationcp.middleware.util.ISO8601DateParser;
import org.generationcp.middleware.utils.test.UnitTestDaoIDGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OntologyVariableDataManagerImplUnitTest extends UnitTestBase {

    private final String DUMMY_PROGRAM_UUID = UUID.randomUUID().toString();

    @InjectMocks
    private OntologyVariableDataManagerImpl variableDataManager = new OntologyVariableDataManagerImpl();

    @Mock
    private CVDao cvDao;

    @Mock
    private CVTermDao termDao;

    @Mock
    private CvTermPropertyDao cvTermPropertyDao;

    @Mock
    private CVTermRelationshipDao cvTermRelationshipDao;

    @Mock
    private VariableOverridesDao variableOverridesDao;

    @Mock
    private OntologyDaoFactory daoFactory;

    @Mock
    private ProgramFavoriteDAO programFavoriteDAO;

    @Mock
    private OntologyCommonDAO ontologyCommonDAO;

    @Mock
    private CvTermSynonymDao cvTermSynonymDao;

    @Mock
    private DmsProjectDao dmsProjectDao;

    @Mock
    private ExperimentDao experimentDao;

    @Mock
    private OntologyScaleDataManagerImpl scaleDataManager;

    @Mock
    private OntologyPropertyDataManagerImpl propertyDataManager;

    @Mock
    private OntologyMethodDataManagerImpl methodDataManager;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(this.daoFactory.getCvDao()).thenReturn(cvDao);
        Mockito.when(this.daoFactory.getCvTermDao()).thenReturn(termDao);
        Mockito.when(this.daoFactory.getCvTermPropertyDao()).thenReturn(cvTermPropertyDao);
        Mockito.when(this.daoFactory.getCvTermRelationshipDao()).thenReturn(cvTermRelationshipDao);
        Mockito.when(this.daoFactory.getVariableProgramOverridesDao()).thenReturn(variableOverridesDao);
        Mockito.when(this.daoFactory.getProgramFavoriteDao()).thenReturn(programFavoriteDAO);
        Mockito.when(this.daoFactory.getCvTermSynonymDao()).thenReturn(cvTermSynonymDao);
        Mockito.when(this.daoFactory.getDmsProjectDao()).thenReturn(dmsProjectDao);
        Mockito.when(this.daoFactory.getExperimentDao()).thenReturn(experimentDao);
    }

    /**
     * Test to verify get variable method fetch all variables
     * Step 1:Create 3 variable terms
     * Step 2:Set Created Date & Updated Date properties for variables
     * Step 4:Combine both the properties and store them in the list
     * Step 5:Mock termDao data and daoFactory data
     * Step 6:Fetch the variable and assert them
     *
     * @throws Exception
     */

    @Test
    public void testGetAllVariablesShouldFetchAndVerifyWithScale() throws Exception {

        //Variables
        List<CVTerm> variableTerms = new ArrayList<>();
        //Fill 1 variables using helper
        TestDataHelper.fillTestVariableCvTerms(variableTerms, 1);


        CVTerm variableTerm = variableTerms.get(0);

        // Fill Test created date property
        Date testCreatedDateForVariable = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        List<CVTermProperty> variableCreatedDateProperty = new ArrayList<>();
        TestDataHelper.fillTestCreatedDateProperties(variableTerms, variableCreatedDateProperty, testCreatedDateForVariable);

        // Fill Test update date property
        Date testUpdatedDateForVariable = TestDataHelper.constructDate(2015, Calendar.JUNE, 30);
        List<CVTermProperty> variableUpdatedDateProperty = new ArrayList<>();
        TestDataHelper.fillTestUpdatedDateProperties(variableTerms, variableUpdatedDateProperty, testUpdatedDateForVariable); //date variable name argument


        //Combining Properties
        List<CVTermProperty> combinedPropertiesForVariables = new ArrayList<>(variableCreatedDateProperty);
        combinedPropertiesForVariables.addAll(variableUpdatedDateProperty);

        CVTerm scaleTerm = TestDataHelper.getTestCvTerm(CvId.SCALES);

        //Adding cvTerm relationship
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        cvTermRelationship.setCvTermRelationshipId(CvId.SCALES.getId());
        cvTermRelationship.setSubjectId(scaleTerm.getCvTermId());
        cvTermRelationship.setTypeId(TermRelationshipId.HAS_SCALE.getId());
        cvTermRelationship.setObjectId(TermId.HAS_SCALE.getId());

        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        Mockito.doReturn(TestDataHelper.generateScale()).when(this.scaleDataManager).getScale(TestDataHelper.generateScale().getId(), true);

        //Creating cvTerm list
        List<CVTerm> cvTermList = new ArrayList<>();
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCv(CvId.SCALES.getId());
        cvTerm.setCvTermId(TermId.HAS_SCALE.getId());
        cvTerm.setName("Name");
        cvTerm.setDefinition("Definition");
        cvTerm.setIsObsolete(false);
        cvTermList.add(cvTerm);

        Mockito.when(this.termDao.getAllByCvId(CvId.VARIABLES, false)).thenReturn(variableTerms);

        VariableOverrides overrides = new VariableOverrides();
        overrides.setAlias("Alias");
        overrides.setExpectedMin("1");
        overrides.setExpectedMax("10");

        ProgramFavorite programFavorite = new ProgramFavorite();

        Mockito.when(this.termDao.getById(variableTerm.getCvTermId())).thenReturn(variableTerm);
        Mockito.when(this.cvTermPropertyDao.getByCvTermId(variableTerm.getCvTermId())).thenReturn(combinedPropertiesForVariables);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(variableTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.variableOverridesDao.getByVariableAndProgram(variableTerm.getCvTermId(), this.DUMMY_PROGRAM_UUID)).thenReturn(overrides);
        Mockito.when(this.programFavoriteDAO.getProgramFavorite(this.DUMMY_PROGRAM_UUID, ProgramFavorite.FavoriteType.VARIABLE, variableTerm.getCvTermId())).thenReturn(programFavorite);

        Mockito.when(this.daoFactory.getCvTermDao().getAllByCvId(CvId.SCALES, true)).thenReturn(cvTermList);
        Mockito.when(this.dmsProjectDao.countByVariable(CvId.VARIABLES.getId())).thenReturn(1000L);

        Variable variable = this.variableDataManager.getVariable(this.DUMMY_PROGRAM_UUID, variableTerm.getCvTermId(), true, true);

        //assert variable not null
        //assert variable properties and other relations
        Assert.assertNotNull(variable);
        Assert.assertNotNull(combinedPropertiesForVariables);
        Assert.assertNotNull(relationships);

    }

    @Test
    public void testGetAllVariablesShouldFetchAndVerifyWithProperty() throws Exception {

        //Variables
        List<CVTerm> variableTerms = new ArrayList<>();
        //Fill 1 variables using helper
        TestDataHelper.fillTestVariableCvTerms(variableTerms, 1);

        CVTerm variableTerm = variableTerms.get(0);

        // Fill Test created date property
        Date testCreatedDateForVariable = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        List<CVTermProperty> variableCreatedDateProperty = new ArrayList<>();
        TestDataHelper.fillTestCreatedDateProperties(variableTerms, variableCreatedDateProperty, testCreatedDateForVariable);

        // Fill Test update date property
        Date testUpdatedDateForVariable = TestDataHelper.constructDate(2015, Calendar.JUNE, 30);
        List<CVTermProperty> variableUpdatedDateProperty = new ArrayList<>();
        TestDataHelper.fillTestUpdatedDateProperties(variableTerms, variableUpdatedDateProperty, testUpdatedDateForVariable); //date variable name argument

        //Combining Properties
        List<CVTermProperty> combinedPropertiesForVariables = new ArrayList<>(variableCreatedDateProperty);
        combinedPropertiesForVariables.addAll(variableUpdatedDateProperty);

        CVTerm propertyTerm = TestDataHelper.getTestCvTerm(CvId.PROPERTIES);

        //Adding cvterm relationship
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        cvTermRelationship.setCvTermRelationshipId(CvId.SCALES.getId());
        cvTermRelationship.setSubjectId(propertyTerm.getCvTermId());
        cvTermRelationship.setTypeId(TermRelationshipId.HAS_PROPERTY.getId());
        cvTermRelationship.setObjectId(TermId.HAS_PROPERTY.getId());

        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        Mockito.doReturn(TestDataHelper.generateProperty()).when(this.propertyDataManager).getProperty(TestDataHelper.generateProperty().getId(), true);

        //Creating cvterm list
        List<CVTerm> cvTermList = new ArrayList<>();
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCv(CvId.PROPERTIES.getId());
        cvTerm.setCvTermId(TermId.HAS_PROPERTY.getId());
        cvTerm.setName("Name");
        cvTerm.setDefinition("Definition");
        cvTerm.setIsObsolete(false);
        cvTermList.add(cvTerm);

        Mockito.when(this.termDao.getAllByCvId(CvId.VARIABLES, false)).thenReturn(variableTerms);

        VariableOverrides overrides = new VariableOverrides();
        overrides.setAlias("Alias");
        overrides.setExpectedMin("1");
        overrides.setExpectedMax("10");

        ProgramFavorite programFavorite = new ProgramFavorite();

        Mockito.when(this.termDao.getById(variableTerm.getCvTermId())).thenReturn(variableTerm);
        Mockito.when(this.cvTermPropertyDao.getByCvTermId(variableTerm.getCvTermId())).thenReturn(combinedPropertiesForVariables);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(variableTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.variableOverridesDao.getByVariableAndProgram(variableTerm.getCvTermId(), this.DUMMY_PROGRAM_UUID)).thenReturn(overrides);
        Mockito.when(this.programFavoriteDAO.getProgramFavorite(this.DUMMY_PROGRAM_UUID, ProgramFavorite.FavoriteType.VARIABLE, variableTerm.getCvTermId())).thenReturn(programFavorite);
        Mockito.when(this.daoFactory.getCvTermDao().getAllByCvId(CvId.SCALES, true)).thenReturn(cvTermList);
        Mockito.when(this.dmsProjectDao.countByVariable(CvId.VARIABLES.getId())).thenReturn(1000L);

        Variable variable = this.variableDataManager.getVariable(this.DUMMY_PROGRAM_UUID, variableTerm.getCvTermId(), true, true);

        //assert variable not null
        //assert variable properties and other relations
        Assert.assertNotNull(variable);
        Assert.assertNotNull(combinedPropertiesForVariables);
        Assert.assertNotNull(relationships);

    }

    @Test
    public void testGetAllVariablesShouldFetchAndVerifyWithMethod() throws Exception {

        //Variables
        List<CVTerm> variableTerms = new ArrayList<>();
        //Fill 1 variables using helper
        TestDataHelper.fillTestVariableCvTerms(variableTerms, 1);

        CVTerm variableTerm = variableTerms.get(0);

        // Fill Test created date property
        Date testCreatedDateForVariable = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        List<CVTermProperty> variableCreatedDateProperty = new ArrayList<>();
        TestDataHelper.fillTestCreatedDateProperties(variableTerms, variableCreatedDateProperty, testCreatedDateForVariable);

        // Fill Test update date property
        Date testUpdatedDateForVariable = TestDataHelper.constructDate(2015, Calendar.JUNE, 30);
        List<CVTermProperty> variableUpdatedDateProperty = new ArrayList<>();
        TestDataHelper.fillTestUpdatedDateProperties(variableTerms, variableUpdatedDateProperty, testUpdatedDateForVariable); //date variable name argument

        CVTermProperty cvTermProperty = new CVTermProperty();
        cvTermProperty.setTypeId(VariableType.ANALYSIS.getId());

        //Combining Properties
        List<CVTermProperty> combinedPropertiesForVariables = new ArrayList<>(variableCreatedDateProperty);
        combinedPropertiesForVariables.add(cvTermProperty);
        combinedPropertiesForVariables.addAll(variableUpdatedDateProperty);

        CVTerm methodTerm = TestDataHelper.getTestCvTerm(CvId.METHODS);

        //Adding cvterm relationship
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        cvTermRelationship.setCvTermRelationshipId(CvId.METHODS.getId());
        cvTermRelationship.setSubjectId(methodTerm.getCvTermId());
        cvTermRelationship.setTypeId(TermRelationshipId.HAS_METHOD.getId());
        cvTermRelationship.setObjectId(TermId.HAS_METHOD.getId());

        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        Method method = new Method();
        method.setId(1);

        Mockito.doReturn(method).when(this.methodDataManager).getMethod(method.getId(), true);

        //Creating cvterm list
        List<CVTerm> cvTermList = new ArrayList<>();
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCv(CvId.METHODS.getId());
        cvTerm.setCvTermId(TermId.HAS_METHOD.getId());
        cvTerm.setName("Name");
        cvTerm.setDefinition("Definition");
        cvTerm.setIsObsolete(false);
        cvTermList.add(cvTerm);

        Mockito.when(this.termDao.getAllByCvId(CvId.VARIABLES, false)).thenReturn(variableTerms);

        VariableOverrides overrides = new VariableOverrides();
        overrides.setAlias("Alias");
        overrides.setExpectedMin("1");
        overrides.setExpectedMax("10");

        ProgramFavorite programFavorite = new ProgramFavorite();

        Mockito.when(this.termDao.getById(variableTerm.getCvTermId())).thenReturn(variableTerm);
        Mockito.when(this.cvTermPropertyDao.getByCvTermId(variableTerm.getCvTermId())).thenReturn(combinedPropertiesForVariables);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(variableTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.variableOverridesDao.getByVariableAndProgram(variableTerm.getCvTermId(), this.DUMMY_PROGRAM_UUID)).thenReturn(overrides);
        Mockito.when(this.programFavoriteDAO.getProgramFavorite(this.DUMMY_PROGRAM_UUID, ProgramFavorite.FavoriteType.VARIABLE, variableTerm.getCvTermId())).thenReturn(programFavorite);
        Mockito.when(this.daoFactory.getCvTermDao().getAllByCvId(CvId.SCALES, true)).thenReturn(cvTermList);
        Mockito.when(this.dmsProjectDao.countByVariable(CvId.VARIABLES.getId())).thenReturn(1000L);
        Mockito.when(this.experimentDao.countByObservedVariable(CvId.VARIABLES.getId(), TermId.VARIABLE_TYPE.getId())).thenReturn(0L);

        Variable variable = this.variableDataManager.getVariable(this.DUMMY_PROGRAM_UUID, variableTerm.getCvTermId(), true, true);

        //assert variable not null
        //assert variable properties and other relations
        Assert.assertNotNull(variable);
        Assert.assertNotNull(combinedPropertiesForVariables);
        Assert.assertNotNull(relationships);
    }


    /**
     * The test to get variables with filter.
     *
     * @throws Exception
     */
    @Test
    public void testGetVariableWithFilter() throws Exception{

        VariableFilter variableFilter = new VariableFilter();
        variableFilter.setFetchAll(true);
        variableFilter.setProgramUuid(this.DUMMY_PROGRAM_UUID);
        variableFilter.setFetchAll(true);
        variableFilter.setFavoritesOnly(false);
        Mockito.doNothing().when(this.ontologyCommonDAO).getVariableRelationships
                (Matchers.anyMapOf(Integer.class, Method.class), Matchers.anyMapOf(Integer.class, Property.class), Matchers.anyMapOf(Integer.class , Scale.class));


        this.variableDataManager.getWithFilter(variableFilter);
    }

    /**
     * The test to get variable filter having property class
     *
     * @throws Exception
     */
    @Test
    public void testGetVariableWithFilterWithPropertyClass() throws Exception{
        Mockito.doNothing().when(this.ontologyCommonDAO).getVariableRelationships
                (Matchers.anyMapOf(Integer.class, Method.class), Matchers.anyMapOf(Integer.class, Property.class), Matchers.anyMapOf(Integer.class , Scale.class));

        VariableFilter variableFilter = new VariableFilter();
        variableFilter.addPropertyClass("Property Class");

        this.variableDataManager.getWithFilter(variableFilter);
    }

    /**
     * The test to get variable filter having datatype
     *
     * @throws Exception
     */
    @Test
    public void testGetVariableWithFilterWithDataType() throws Exception{
        //Was done like this because in return the argumemt was void
        Mockito.doNothing().when(this.ontologyCommonDAO).getVariableRelationships
                (Matchers.anyMapOf(Integer.class, Method.class), Matchers.anyMapOf(Integer.class, Property.class), Matchers.anyMapOf(Integer.class , Scale.class));

        VariableFilter variableFilter = new VariableFilter();
        variableFilter.addDataType(DataType.CATEGORICAL_VARIABLE);

        this.variableDataManager.getWithFilter(variableFilter);
    }

    /**
     * The test to get variable filter having variable type
     *
     * @throws Exception
     */
    @Test
    public void testGetVariableWithFilterWithVariableType() throws Exception{
        Mockito.doNothing().when(this.ontologyCommonDAO).getVariableRelationships
                (Matchers.anyMapOf(Integer.class, Method.class), Matchers.anyMapOf(Integer.class, Property.class), Matchers.anyMapOf(Integer.class , Scale.class));

        VariableFilter variableFilter = new VariableFilter();
        variableFilter.addVariableType(VariableType.ENVIRONMENT_DETAIL);

        this.variableDataManager.getWithFilter(variableFilter);
    }

    /**
     * Test to get variable studies
     *
     * @throws Exception
     */
    //Question:Purpose of this method
    @Test
    public void testGetVariableStudies() throws Exception{
        Variable variable = new Variable();

        this.variableDataManager.getVariableStudies(variable.getId());
    }


    /**
     * Test to add new variable
     *
     * @throws Exception
     */
    @Test
    public void testAddVariableShouldAddNewVariable() throws Exception {

        //Variables
        List<CVTerm> variableTerms = new ArrayList<>();
        //Fill 1 variables using helper
        TestDataHelper.fillTestVariableCvTerms(variableTerms, 1);

        Method method = new Method();
        Property property = new Property();
        Scale scale = new Scale();
        Variable variable = new Variable();
        Term term = new Term();
        term.setId(1);

        String expectedMin = "1";
        String expectedMax = "50";

        //Adding data to ontologyVariableInfo for variable associated ids
        OntologyVariableInfo ontologyVariableInfo = new OntologyVariableInfo();

        ontologyVariableInfo.setTerm(term);
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

        //Creating new cvterm
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
        cvTerm.setCv(CvId.VARIABLES.getId());
        cvTerm.setName(variable.getName());
        cvTerm.setDefinition(variable.getDefinition());
        cvTerm.setIsObsolete(false);
        cvTerm.setIsRelationshipType(false);

        Date createdDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        this.stubCurrentDate(createdDate);

        Mockito.when(this.termDao.getByNameAndCvId(variable.getName(), CvId.VARIABLES.getId())).thenReturn(null);
        Mockito.when(termDao.save(ontologyVariableInfo.getName(), ontologyVariableInfo.getDescription(), CvId.VARIABLES)).thenReturn(cvTerm);

        this.variableDataManager.addVariable(ontologyVariableInfo);

        Mockito.when(this.termDao.getById(ontologyVariableInfo.getId())).thenReturn(cvTerm);

        String message = "The %s for Variable '" + variable.getId() + "' was not added correctly.";
        Assert.assertNotNull(String.format(message, "methodId"), method.getId());
        Assert.assertNotNull(String.format(message, "propertyId"), property.getId());
        Assert.assertNotNull(String.format(message, "scaleId"), scale.getId());

        Assert.assertEquals(String.format(message, "Name"), cvTerm.getName(), variable.getName());
        Assert.assertEquals(String.format(message, "IsFavorite"), true, ontologyVariableInfo.isFavorite());
        Assert.assertEquals(String.format(message, "MethodId"), method.getId(), ontologyVariableInfo.getMethodId().intValue());
        Assert.assertEquals(String.format(message, "PropertyId"), property.getId(), ontologyVariableInfo.getPropertyId().intValue());
        Assert.assertEquals(String.format(message, "ScaleId"), scale.getId(), ontologyVariableInfo.getScaleId().intValue());
        Assert.assertTrue(String.format(message, "Variable Type"), ontologyVariableInfo.getVariableTypes().contains(VariableType.ANALYSIS));
        Assert.assertEquals(String.format(message, "Expected Min"), expectedMin, ontologyVariableInfo.getExpectedMin());
        Assert.assertEquals(String.format(message, "Expected Max"), expectedMax, ontologyVariableInfo.getExpectedMax());
    }

    /**
     * Test to verify that duplicate values are not allowed to add
     *
     * @throws Exception
     */
    @Test(expected = MiddlewareException.class)
    public void testAddNewVariableShouldNotAddDuplicateVariable() throws Exception{

        Variable variable = new Variable();
        variable.setName(TestDataHelper.getNewRandomName("Name"));

        OntologyVariableInfo ontologyVariableInfo = new OntologyVariableInfo();

        ontologyVariableInfo.setName(variable.getName());
        ontologyVariableInfo.setDescription("Description");

        //Adding cvterm data
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
        cvTerm.setCv(CvId.VARIABLES.getId());
        cvTerm.setName(variable.getName());

        Date createdDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        this.stubCurrentDate(createdDate);

        Mockito.when(this.termDao.getByNameAndCvId(variable.getName(), CvId.VARIABLES.getId())).thenReturn(cvTerm);
        Mockito.when(termDao.save(ontologyVariableInfo.getName(), ontologyVariableInfo.getDescription(), CvId.VARIABLES)).thenReturn(cvTerm);

        this.variableDataManager.addVariable(ontologyVariableInfo);
    }

    /**
     * Test to verify that two variable types cannot be added in one variable
     *
     * @throws Exception
     */

    @Test(expected = MiddlewareException.class)
    public void testAddVariableShouldNotAllowAnalysisVariableType() throws Exception{
        OntologyVariableInfo ontologyVariableInfo = new OntologyVariableInfo();

        ontologyVariableInfo.setName(TestDataHelper.getNewRandomName("Name"));
        ontologyVariableInfo.setDescription("Description");
        ontologyVariableInfo.addVariableType(VariableType.ANALYSIS);
        ontologyVariableInfo.addVariableType(VariableType.ENVIRONMENT_DETAIL);

        this.variableDataManager.addVariable(ontologyVariableInfo);
    }



    /**
     * Test to delete existing variable
     *
     * @throws Exception
     */
    @Test
    public void testDeleteVariableShouldDeleteExistingVariable() throws Exception{
        CVTerm variableTerm = TestDataHelper.getTestCvTerm(CvId.VARIABLES);

        // Fill Test Created Date Property
        Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        List<CVTermProperty> variableCreatedDateProperties = new ArrayList<>();
        TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(variableTerm), variableCreatedDateProperties, testCreatedDate);

        // Fill Test Updated Date Property
        Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.MAY, 20);
        List<CVTermProperty> variableUpdatedDateProperties = new ArrayList<>();
        TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(variableTerm), variableUpdatedDateProperties, testUpdatedDate);

        //Combine the create date and modified date properties
        List<CVTermProperty> combinedProperties = new ArrayList<>(variableCreatedDateProperties);
        combinedProperties.addAll(variableUpdatedDateProperties);

        //Adding cvterm synonym
        CVTermSynonym cvTermSynonym = new CVTermSynonym();
        List<CVTermSynonym> cvTermSynonyms = new ArrayList<>();
        cvTermSynonyms.add(cvTermSynonym);

        //Adding cvterm relationship
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        VariableOverrides variableOverrides = new VariableOverrides();
        List<VariableOverrides> variableOverridesList = new ArrayList<>();
        variableOverridesList.add(variableOverrides);

        Mockito.when(this.termDao.getById(variableTerm.getCvTermId())).thenReturn(variableTerm);
        Mockito.when(this.cvTermPropertyDao.getByCvTermId(variableTerm.getCvTermId())).thenReturn(combinedProperties);
        Mockito.when(this.cvTermSynonymDao.getByCvTermId(variableTerm.getCvTermId())).thenReturn(cvTermSynonyms);
        Mockito.doNothing().when(this.termDao).makeTransient(variableTerm);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(variableTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.variableOverridesDao.getByVariableId(variableTerm.getCvTermId())).thenReturn(variableOverridesList);
        Mockito.when(this.ontologyCommonDAO.getVariableObservations(variableTerm.getCvTermId())).thenReturn(0);

        this.variableDataManager.deleteVariable(variableTerm.getCvTermId());

        Mockito.verify(this.termDao, Mockito.times(1)).makeTransient(variableTerm);
        Mockito.verify(this.cvTermRelationshipDao, Mockito.times(1)).makeTransient(Mockito.any(CVTermRelationship.class));

    }

    /**
     * Test to verify that used variable cannot be deleted.
     *
     * @throws Exception
     */
    @Test(expected = MiddlewareException.class)
    public void testDeleteVariableShouldNotDeleteUsedVariable() throws Exception{
        CVTerm variableTerm = TestDataHelper.getTestCvTerm(CvId.VARIABLES);

        // Fill Test Created Date Property
        Date testCreatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        List<CVTermProperty> variableCreatedDateProperties = new ArrayList<>();
        TestDataHelper.fillTestCreatedDateProperties(Collections.singletonList(variableTerm), variableCreatedDateProperties, testCreatedDate);

        // Fill Test Updated Date Property
        Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.MAY, 20);
        List<CVTermProperty> variableUpdatedDateProperties = new ArrayList<>();
        TestDataHelper.fillTestUpdatedDateProperties(Collections.singletonList(variableTerm), variableUpdatedDateProperties, testUpdatedDate);

        //Combine the create date and modified date properties
        List<CVTermProperty> combinedProperties = new ArrayList<>(variableCreatedDateProperties);
        combinedProperties.addAll(variableUpdatedDateProperties);

        //Creating cvterm synonym
        CVTermSynonym cvTermSynonym = new CVTermSynonym();
        List<CVTermSynonym> cvTermSynonyms = new ArrayList<>();
        cvTermSynonyms.add(cvTermSynonym);

        Mockito.when(this.termDao.getById(variableTerm.getCvTermId())).thenReturn(variableTerm);
        Mockito.when(this.cvTermPropertyDao.getByCvTermId(variableTerm.getCvTermId())).thenReturn(combinedProperties);
        Mockito.when(this.cvTermSynonymDao.getByCvTermId(variableTerm.getCvTermId())).thenReturn(cvTermSynonyms);
        Mockito.doNothing().when(this.termDao).makeTransient(variableTerm);
        Mockito.when(this.ontologyCommonDAO.getVariableObservations(variableTerm.getCvTermId())).thenReturn(1);

        this.variableDataManager.deleteVariable(variableTerm.getCvTermId());
}

    /**
     * Test to update existing variable
     *
     * @throws Exception
     */
    @Test
    public void testUpdateVariableShouldUpdateExistingVariable() throws Exception{
        CVTerm variableTerm = TestDataHelper.getTestCvTerm(CvId.VARIABLES);

        Method method = new Method();
        Property property = new Property();
        Scale scale = new Scale();

        String expectedMin = "1";
        String expectedMax = "50";

        CVTerm cvTerm = TestDataHelper.getTestCvTerm(CvId.VARIABLES);

        //Creating cvterm relationship
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        VariableOverrides variableOverrides = new VariableOverrides();
        method.setId(1);
        method.setName("New Method Name");
        method.setDefinition("New Method Definition");

        //Creating cvterm synonym
        CVTermSynonym cvTermSynonym = new CVTermSynonym();
        cvTermSynonym.setCvTermId(cvTerm.getCvTermId());
        cvTermSynonym.setTypeId(TermRelationshipId.HAS_METHOD.getId());
        cvTermSynonym.setSynonym(CvId.METHODS.name());
        List<CVTermSynonym> cvTermSynonyms = new ArrayList<>();
        cvTermSynonyms.add(cvTermSynonym);

        Mockito.when(this.cvTermSynonymDao.getByCvTermId(cvTermSynonym.getCvTermId())).thenReturn(cvTermSynonyms);
        Mockito.when(this.termDao.save(method.getName() , method.getDefinition() , CvId.METHODS)).thenReturn(cvTerm);

        String cropOntologyId = "CO:888";

        OntologyVariableInfo updateVariable = new OntologyVariableInfo();

        updateVariable.setId(variableTerm.getCvTermId());
        updateVariable.setName(TestDataHelper.getNewRandomName("Name"));
        updateVariable.setDescription("updated description");
        updateVariable.setIsFavorite(false);
        updateVariable.setMethodId(method.getId());
        updateVariable.setExpectedMin(expectedMin);
        updateVariable.setExpectedMax(expectedMax);
        property.setCropOntologyId(cropOntologyId);
        updateVariable.setPropertyId(property.getId());
        updateVariable.setScaleId(scale.getId());
        updateVariable.setAlias("");
        updateVariable.setIsFavorite(true);

        //dao elements used for variableInfo to reduce functional complexity
        VariableInfoDaoElements variableInfoDaoElements = new VariableInfoDaoElements();
        variableInfoDaoElements.setVariableId(updateVariable.getId());
        variableInfoDaoElements.setProgramUuid(updateVariable.getProgramUuid());

        Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        this.stubCurrentDate(testUpdatedDate);

        CVTermProperty cvTermProperty = new CVTermProperty();
        cvTermProperty.setCvTermPropertyId(1);

        Mockito.when(this.variableOverridesDao.getByVariableAndProgram(variableInfoDaoElements.getVariableId(), variableInfoDaoElements.getProgramUuid())).thenReturn(variableOverrides);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(cvTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.termDao.getById(variableInfoDaoElements.getVariableId())).thenReturn(cvTerm);
        Mockito.when(this.cvTermPropertyDao.save(updateVariable.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(testUpdatedDate), 0)).thenReturn(cvTermProperty);

        this.variableDataManager.updateVariable(updateVariable);

        // Make sure the inserted data should come as they are inserted and Display proper message if the data doesn't come as expected
        String message = "The %s for variable '" + updateVariable.getId() + "' was not updated correctly.";
//        Assert.assertEquals(String.format(message, "Name"), cvTerm.getName(), updateVariable.getName()); //Can we set hardcoded name?
        Assert.assertEquals(String.format(message, "Description"), updateVariable.getDescription(), cvTerm.getDefinition());
        Assert.assertEquals(String.format(message, "MethodId"), updateVariable.getMethodId().intValue(), method.getId());
        Assert.assertEquals(String.format(message, "ScaleId"), updateVariable.getScaleId().intValue(), scale.getId());
        Assert.assertEquals(String.format(message, "PropertyId"), updateVariable.getPropertyId().intValue(), property.getId());
        Assert.assertEquals(String.format(message, "Expected Min"), updateVariable.getExpectedMin(), expectedMin);
        Assert.assertEquals(String.format(message, "Expected Max"), updateVariable.getExpectedMax(), expectedMax);
        Assert.assertEquals(String.format(message, "CropOntologyId"), property.getCropOntologyId(), cropOntologyId);
        Assert.assertEquals(String.format(message, "IsObsolete"), false, cvTerm.isObsolete());
        Assert.assertEquals(String.format(message, "IsFavorite"), true, updateVariable.isFavorite());

}


    /**
     * Test to update variable without values of min, max and alias
     *
     * @throws Exception
     */
    @Test
    public void testUpdateVariableWithoutMinMaxAlias() throws Exception{
        Method method = new Method();
        Property property = new Property();
        Scale scale = new Scale();
        Variable variable = new Variable();

        //Adding cvterms
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCvTermId(CvId.METHODS.getId());
        cvTerm.setCv(CvId.VARIABLES.getId());
        cvTerm.setName(CvId.VARIABLES.name());
        cvTerm.setDefinition(variable.getDefinition());
        cvTerm.setIsObsolete(false);
        cvTerm.setIsRelationshipType(false);

        //Adding cvterm relationship data
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        VariableOverrides variableOverrides = new VariableOverrides();
        CVTerm methodTerm = TestDataHelper.getTestCvTerm(CvId.METHODS);
        Method updateMethod = new Method();
        method.setId(methodTerm.getCvTermId());
        method.setName("New Method Name");
        method.setDefinition("New Method Definition");

        //Adding cvterm synonym data
        CVTermSynonym cvTermSynonym = new CVTermSynonym();
        cvTermSynonym.setCvTermId(cvTerm.getCvTermId());
        cvTermSynonym.setSynonym(CvId.VARIABLES.name());
        List<CVTermSynonym> cvTermSynonyms = new ArrayList<>();
        cvTermSynonyms.add(cvTermSynonym);

        Mockito.when(this.cvTermSynonymDao.getByCvTermId(cvTermSynonym.getCvTermId())).thenReturn(cvTermSynonyms);
        Mockito.when(this.termDao.save(method.getName() , method.getDefinition() , CvId.METHODS)).thenReturn(cvTerm);

        String cropOntologyId = "CO:888";

        OntologyVariableInfo updateVariable = new OntologyVariableInfo();
        updateVariable.setName("Variable Name");
        updateVariable.setIsFavorite(false);
        updateVariable.setMethodId(updateMethod.getId());
        property.setCropOntologyId(cropOntologyId);
        updateVariable.setPropertyId(property.getId());
        updateVariable.setScaleId(scale.getId());
        updateVariable.setAlias("");

        VariableInfoDaoElements variableInfoDaoElements = new VariableInfoDaoElements();
        variableInfoDaoElements.setVariableId(updateVariable.getId());
        variableInfoDaoElements.setProgramUuid(updateVariable.getProgramUuid());

        Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        this.stubCurrentDate(testUpdatedDate);

        CVTermProperty cvTermProperty = new CVTermProperty();

        Mockito.when(this.variableOverridesDao.getByVariableAndProgram(variableInfoDaoElements.getVariableId(), variableInfoDaoElements.getProgramUuid())).thenReturn(variableOverrides);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(cvTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.termDao.getById(variableInfoDaoElements.getVariableId())).thenReturn(cvTerm);
        Mockito.when(this.cvTermPropertyDao.save(updateVariable.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(testUpdatedDate), 0)).thenReturn(cvTermProperty);

        this.variableDataManager.updateVariable(updateVariable);
    }


    /**
     * Test to update variable without synonym
     *
     * @throws Exception
     */
    @Test
    public void testUpdateVariableShouldUpdateExistingVariableWithoutSynonym() throws Exception{
        CVTerm variableTerm = TestDataHelper.getTestCvTerm(CvId.VARIABLES);

        Method method = new Method();
        Property property = new Property();
        Scale scale = new Scale();
        Variable variable = new Variable();

        String expectedMin = "1";
        String expectedMax = "50";

        //Adding cvterms
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCvTermId(CvId.METHODS.getId());
        cvTerm.setCv(CvId.VARIABLES.getId());
        cvTerm.setName(CvId.VARIABLES.name());
        cvTerm.setDefinition(variable.getDefinition());
        cvTerm.setIsObsolete(false);
        cvTerm.setIsRelationshipType(false);

        //Adding cvterm property
        CVTermProperty cvTermProperty = new CVTermProperty();
        cvTermProperty.setCvTermPropertyId(1111);
        cvTermProperty.setTypeId(TermId.VARIABLE_TYPE.getId());
        List<CVTermProperty> cvTermProperties = new ArrayList<>();
        cvTermProperties.add(cvTermProperty);

        //Adding cvterm relationship
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        VariableOverrides variableOverrides = new VariableOverrides();

        //Adding cvterm synonym
        CVTermSynonym cvTermSynonym = new CVTermSynonym();
        cvTermSynonym.setCvTermId(cvTerm.getCvTermId());
        cvTermSynonym.setSynonym(CvId.METHODS.name());
        List<CVTermSynonym> cvTermSynonyms = new ArrayList<>();
        cvTermSynonyms.add(cvTermSynonym);


        Mockito.when(this.cvTermSynonymDao.getByCvTermId(cvTermSynonym.getCvTermId())).thenReturn(cvTermSynonyms);
        Mockito.when(this.termDao.save(method.getName() , method.getDefinition() , CvId.METHODS)).thenReturn(cvTerm);

        String cropOntologyId = "CO:888";

        OntologyVariableInfo updateVariable = new OntologyVariableInfo();
        updateVariable.setId(variableTerm.getCvTermId());
        updateVariable.setName("Variable Name");
        updateVariable.setIsFavorite(false);
        updateVariable.setExpectedMin(expectedMin);
        updateVariable.setExpectedMax(expectedMax);
        updateVariable.setMethodId(method.getId());
        property.setCropOntologyId(cropOntologyId);
        updateVariable.setPropertyId(property.getId());
        updateVariable.setScaleId(scale.getId());
        updateVariable.setAlias("");

        VariableInfoDaoElements variableInfoDaoElements = new VariableInfoDaoElements();
        variableInfoDaoElements.setVariableId(updateVariable.getId());
        variableInfoDaoElements.setProgramUuid(updateVariable.getProgramUuid());
        variableInfoDaoElements.setTermProperties(cvTermProperties);

        Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        this.stubCurrentDate(testUpdatedDate);

        Mockito.when(this.variableOverridesDao.getByVariableAndProgram(variableInfoDaoElements.getVariableId(), variableInfoDaoElements.getProgramUuid())).thenReturn(variableOverrides);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(cvTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.termDao.getById(variableInfoDaoElements.getVariableId())).thenReturn(cvTerm);
        Mockito.when(this.cvTermPropertyDao.getByCvTermId(variableInfoDaoElements.getVariableId())).thenReturn(cvTermProperties);
        Mockito.when(this.cvTermPropertyDao.save(updateVariable.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(testUpdatedDate), 0)).thenReturn(cvTermProperty);

        this.variableDataManager.updateVariable(updateVariable);
    }


    /**
     * Test to update variable with method data.
     *
     * @throws Exception
     */
    @Test
    public void testUpdateVariableShouldUpdateExistingVariableHavingMethod() throws Exception{
        CVTerm variableTerm = TestDataHelper.getTestCvTerm(CvId.VARIABLES);

        Method method = new Method();
        Property property = new Property();
        Scale scale = new Scale();
        Variable variable = new Variable();

        String expectedMin = "1";
        String expectedMax = "50";

        //Creating cvterms
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
        cvTerm.setCv(CvId.VARIABLES.getId());
        cvTerm.setName(CvId.VARIABLES.name());
        cvTerm.setDefinition(variable.getDefinition());
        cvTerm.setIsObsolete(false);
        cvTerm.setIsRelationshipType(false);

        //Adding cvterm relationship data
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        cvTermRelationship.setObjectId(CvId.METHODS.getId());
        cvTermRelationship.setTypeId(TermRelationshipId.HAS_METHOD.getId());
        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        VariableOverrides variableOverrides = new VariableOverrides();

        Mockito.when(this.termDao.save(method.getName() , method.getDefinition() , CvId.METHODS)).thenReturn(cvTerm);

        String cropOntologyId = "CO:888";

        OntologyVariableInfo updateVariable = new OntologyVariableInfo();
        updateVariable.setId(variableTerm.getCvTermId());
        updateVariable.setName("Variable Name");
        updateVariable.setIsFavorite(false);
        updateVariable.setMethodId(method.getId());
        updateVariable.setExpectedMin(expectedMin);
        updateVariable.setExpectedMax(expectedMax);
        property.setCropOntologyId(cropOntologyId);
        updateVariable.setPropertyId(property.getId());
        updateVariable.setScaleId(scale.getId());
        updateVariable.setAlias("");

        VariableInfoDaoElements variableInfoDaoElements = new VariableInfoDaoElements();
        variableInfoDaoElements.setVariableId(updateVariable.getId());
        variableInfoDaoElements.setProgramUuid(updateVariable.getProgramUuid());

        Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        this.stubCurrentDate(testUpdatedDate);

        CVTermProperty cvTermProperty = new CVTermProperty();

        Mockito.when(this.variableOverridesDao.getByVariableAndProgram(variableInfoDaoElements.getVariableId(), variableInfoDaoElements.getProgramUuid())).thenReturn(variableOverrides);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(cvTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.termDao.getById(variableInfoDaoElements.getVariableId())).thenReturn(cvTerm);
        Mockito.when(this.cvTermPropertyDao.save(updateVariable.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(testUpdatedDate), 0)).thenReturn(cvTermProperty);

        this.variableDataManager.updateVariable(updateVariable);
    }

    /**
     * Test to update variable with property data
     *
     * @throws Exception
     */
    @Test
    public void testUpdateVariableShouldUpdateExistingVariableHavingProperty() throws Exception{
        String cropOntologyId = "CO:888";
        Method method = new Method();
        Property property = new Property();
        property.setCropOntologyId(cropOntologyId);
        Scale scale = new Scale();
        Variable variable = new Variable();

        String expectedMin = "1";
        String expectedMax = "50";

        //Adding cvterms
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
        cvTerm.setCv(CvId.VARIABLES.getId());
        cvTerm.setName(CvId.VARIABLES.name());
        cvTerm.setDefinition(variable.getDefinition());
        cvTerm.setIsObsolete(false);
        cvTerm.setIsRelationshipType(false);

        //Adding cvterm relationship data
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        cvTermRelationship.setObjectId(CvId.PROPERTIES.getId());
        cvTermRelationship.setTypeId(TermRelationshipId.HAS_PROPERTY.getId());
        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        VariableOverrides variableOverrides = new VariableOverrides();

        Mockito.when(this.termDao.save(method.getName() , method.getDefinition() , CvId.METHODS)).thenReturn(cvTerm);


        OntologyVariableInfo updateVariable = new OntologyVariableInfo();
        updateVariable.setName("Variable Name");
        updateVariable.setIsFavorite(false);
        updateVariable.setMethodId(method.getId());
        updateVariable.setExpectedMin(expectedMin);
        updateVariable.setExpectedMax(expectedMax);
        updateVariable.setPropertyId(property.getId());
        updateVariable.setScaleId(scale.getId());
        updateVariable.setAlias("");

        VariableInfoDaoElements variableInfoDaoElements = new VariableInfoDaoElements();
        variableInfoDaoElements.setVariableId(updateVariable.getId());
        variableInfoDaoElements.setProgramUuid(updateVariable.getProgramUuid());

        Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        this.stubCurrentDate(testUpdatedDate);

        CVTermProperty cvTermProperty = new CVTermProperty();

        Mockito.when(this.variableOverridesDao.getByVariableAndProgram(variableInfoDaoElements.getVariableId(), variableInfoDaoElements.getProgramUuid())).thenReturn(variableOverrides);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(cvTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.termDao.getById(variableInfoDaoElements.getVariableId())).thenReturn(cvTerm);
        Mockito.when(this.cvTermPropertyDao.save(updateVariable.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(testUpdatedDate), 0)).thenReturn(cvTermProperty);

        this.variableDataManager.updateVariable(updateVariable);
    }

    /**
     * Test to update variable with scale data
     *
     * @throws Exception
     */
    @Test
    public void testUpdateVariableShouldUpdateExistingVariableHavingScale() throws Exception{
        Method method = new Method();
        Property property = new Property();
        Scale scale = new Scale();
        Variable variable = new Variable();

        String expectedMin = "1";
        String expectedMax = "50";

        //Adding cvterms
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
        cvTerm.setCv(CvId.VARIABLES.getId());
        cvTerm.setName(CvId.VARIABLES.name());
        cvTerm.setDefinition(variable.getDefinition());
        cvTerm.setIsObsolete(false);
        cvTerm.setIsRelationshipType(false);

        //Adding cvterm relationship data
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        cvTermRelationship.setObjectId(CvId.SCALES.getId());
        cvTermRelationship.setTypeId(TermId.HAS_SCALE.getId());
        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        VariableOverrides variableOverrides = new VariableOverrides();

        Mockito.when(this.termDao.save(method.getName() , method.getDefinition() , CvId.METHODS)).thenReturn(cvTerm);

        String cropOntologyId = "CO:888";
        property.setCropOntologyId(cropOntologyId);

        OntologyVariableInfo updateVariable = new OntologyVariableInfo();
        updateVariable.setName("Variable Name");
        updateVariable.setIsFavorite(false);
        updateVariable.setMethodId(method.getId());
        updateVariable.setExpectedMin(expectedMin);
        updateVariable.setExpectedMax(expectedMax);
        updateVariable.setPropertyId(property.getId());
        updateVariable.setScaleId(scale.getId());
        updateVariable.setAlias("");
        updateVariable.addVariableType(VariableType.ANALYSIS);

        VariableInfoDaoElements variableInfoDaoElements = new VariableInfoDaoElements();
        variableInfoDaoElements.setVariableId(updateVariable.getId());
        variableInfoDaoElements.setProgramUuid(updateVariable.getProgramUuid());

        Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        this.stubCurrentDate(testUpdatedDate);

        CVTermProperty cvTermProperty = new CVTermProperty();

        Mockito.when(this.variableOverridesDao.getByVariableAndProgram(variableInfoDaoElements.getVariableId(), variableInfoDaoElements.getProgramUuid())).thenReturn(variableOverrides);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(cvTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.termDao.getById(variableInfoDaoElements.getVariableId())).thenReturn(cvTerm);
        Mockito.when(this.cvTermPropertyDao.save(updateVariable.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(testUpdatedDate), 0)).thenReturn(cvTermProperty);

        this.variableDataManager.updateVariable(updateVariable);
    }

    /**
     * Test to verify that if the variable does not exist then it should not allow
     * for update
     *
     * @throws Exception
     */

    @Test(expected = MiddlewareException.class)
    public void testUpdateVariableShouldNotUpdateIfNotExist() throws Exception{
        OntologyVariableInfo ontologyVariableInfo = new OntologyVariableInfo();

        ontologyVariableInfo.setName(TestDataHelper.getNewRandomName("Name"));
        ontologyVariableInfo.setDescription("Description");
        ontologyVariableInfo.addVariableType(VariableType.ANALYSIS);

        this.variableDataManager.updateVariable(ontologyVariableInfo);
    }

    /**
     * Test to verify that two variable types cannot be update in one variable
     *
     * @throws Exception
     */

    @Test(expected = MiddlewareException.class)
    public void testUpdateVariableShouldNotAllowAnalysisVariableType() throws Exception{
        Method method = new Method();
        Property property = new Property();
        Scale scale = new Scale();
        Variable variable = new Variable();

        String expectedMin = "1";
        String expectedMax = "50";

        //Adding cvterms
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
        cvTerm.setCv(CvId.VARIABLES.getId());
        cvTerm.setName(CvId.VARIABLES.name());
        cvTerm.setDefinition(variable.getDefinition());
        cvTerm.setIsObsolete(false);
        cvTerm.setIsRelationshipType(false);

        //Adding cvterm relationship data
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        cvTermRelationship.setObjectId(CvId.SCALES.getId());
        cvTermRelationship.setTypeId(TermRelationshipId.HAS_SCALE.getId());
        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        VariableOverrides variableOverrides = new VariableOverrides();

        Mockito.when(this.termDao.save(method.getName() , method.getDefinition() , CvId.METHODS)).thenReturn(cvTerm);

        String cropOntologyId = "CO:888";
        property.setCropOntologyId(cropOntologyId);

        OntologyVariableInfo updateVariable = new OntologyVariableInfo();
        updateVariable.setName("Variable Name");
        updateVariable.setIsFavorite(false);
        updateVariable.setMethodId(method.getId());
        updateVariable.setExpectedMin(expectedMin);
        updateVariable.setExpectedMax(expectedMax);
        updateVariable.setPropertyId(property.getId());
        updateVariable.setScaleId(scale.getId());
        updateVariable.setAlias("");
        updateVariable.addVariableType(VariableType.ANALYSIS);
        updateVariable.addVariableType(VariableType.ENVIRONMENT_DETAIL);

        VariableInfoDaoElements variableInfoDaoElements = new VariableInfoDaoElements();
        variableInfoDaoElements.setVariableId(updateVariable.getId());
        variableInfoDaoElements.setProgramUuid(updateVariable.getProgramUuid());

        Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        this.stubCurrentDate(testUpdatedDate);

        CVTermProperty cvTermProperty = new CVTermProperty();

        Mockito.when(this.variableOverridesDao.getByVariableAndProgram(variableInfoDaoElements.getVariableId(), variableInfoDaoElements.getProgramUuid())).thenReturn(variableOverrides);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(cvTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.termDao.getById(variableInfoDaoElements.getVariableId())).thenReturn(cvTerm);
        Mockito.when(this.cvTermPropertyDao.save(updateVariable.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(testUpdatedDate), 0)).thenReturn(cvTermProperty);

        this.variableDataManager.updateVariable(updateVariable);
    }

    /**
     * Test to verify if the term is not variable then it should not proceed
     *
     * @throws Exception
     */
    @Test(expected = MiddlewareException.class)
    public void testUpdateVariableShouldNotAddIfTermIsNotVariable() throws Exception{
        CVTerm variableTerm = TestDataHelper.getTestCvTerm(CvId.VARIABLES);

        //Adding cvterms
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCvTermId(UnitTestDaoIDGenerator.generateId(CVTerm.class));
        cvTerm.setCv(CvId.SCALES.getId());
        cvTerm.setName(CvId.SCALES.name());
        cvTerm.setIsObsolete(false);
        cvTerm.setIsRelationshipType(false);

        //Adding cvterm relationship data
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        VariableOverrides variableOverrides = new VariableOverrides();

        OntologyVariableInfo updateVariable = new OntologyVariableInfo();
        updateVariable.setId(variableTerm.getCvTermId());
        updateVariable.setName("Variable Name");

        VariableInfoDaoElements variableInfoDaoElements = new VariableInfoDaoElements();
        variableInfoDaoElements.setVariableId(updateVariable.getId());
        variableInfoDaoElements.setProgramUuid(updateVariable.getProgramUuid());

        Date testUpdatedDate = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        this.stubCurrentDate(testUpdatedDate);

        CVTermProperty cvTermProperty = new CVTermProperty();

        Mockito.when(this.variableOverridesDao.getByVariableAndProgram(variableInfoDaoElements.getVariableId(), variableInfoDaoElements.getProgramUuid())).thenReturn(variableOverrides);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(cvTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.termDao.getById(variableInfoDaoElements.getVariableId())).thenReturn(cvTerm);
        Mockito.when(this.cvTermPropertyDao.save(updateVariable.getId(), TermId.LAST_UPDATE_DATE.getId(), ISO8601DateParser.toString(testUpdatedDate), 0)).thenReturn(cvTermProperty);

        this.variableDataManager.updateVariable(updateVariable);

    }

    /**
     * Test to retrieve categorical value for null
     *
     * @throws Exception
     */
    //Question: Use of this method
    @Test
    public void testRetrieveVariableCategoricalValueForNullValue() throws Exception{

        this.variableDataManager.retrieveVariableCategoricalValue(this.DUMMY_PROGRAM_UUID, null , null);
    }

    /**
     * Test to retrieve variable categorical value
     *
     * @throws Exception
     */
    @Test
    public void testRetrieveVariableCategoricalValue() throws Exception{

        //Variables
        List<CVTerm> variableTerms = new ArrayList<>();
        //Fill 1 variables using helper
        TestDataHelper.fillTestVariableCvTerms(variableTerms, 1);

        CVTerm variableTerm = variableTerms.get(0);

        // Fill Test created date property
        Date testCreatedDateForVariable = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        List<CVTermProperty> variableCreatedDateProperty = new ArrayList<>();
        TestDataHelper.fillTestCreatedDateProperties(variableTerms, variableCreatedDateProperty, testCreatedDateForVariable);

        // Fill Test update date property
        Date testUpdatedDateForVariable = TestDataHelper.constructDate(2015, Calendar.JUNE, 30);
        List<CVTermProperty> variableUpdatedDateProperty = new ArrayList<>();
        TestDataHelper.fillTestUpdatedDateProperties(variableTerms, variableUpdatedDateProperty, testUpdatedDateForVariable);

        //Combining Properties
        List<CVTermProperty> combinedPropertiesForVariables = new ArrayList<>(variableCreatedDateProperty);
        combinedPropertiesForVariables.addAll(variableUpdatedDateProperty);

        CVTerm scaleTerm = TestDataHelper.getTestCvTerm(CvId.SCALES);

        //Adding cvterm relationship
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        cvTermRelationship.setCvTermRelationshipId(UnitTestDaoIDGenerator.generateId(CVTermRelationship.class));
        cvTermRelationship.setSubjectId(scaleTerm.getCvTermId());
        cvTermRelationship.setTypeId(TermId.HAS_TYPE.getId());
        cvTermRelationship.setObjectId(CvId.SCALES.getId());

        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        Mockito.when(this.termDao.getAllByCvId(CvId.VARIABLES, false)).thenReturn(variableTerms);

        //Creating cvterm list
        List<CVTerm> cvTermList = new ArrayList<>();
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCv(CvId.SCALES.getId());
        cvTerm.setCvTermId(1);
        cvTerm.setName("Name");
        cvTerm.setDefinition("Definition");
        cvTerm.setIsObsolete(false);
        cvTermList.add(cvTerm);

        VariableOverrides overrides = new VariableOverrides();
        ProgramFavorite programFavorite = new ProgramFavorite();

        Mockito.when(this.termDao.getById(variableTerm.getCvTermId())).thenReturn(variableTerm);
        Mockito.when(this.cvTermPropertyDao.getByCvTermId(variableTerm.getCvTermId())).thenReturn(combinedPropertiesForVariables);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(variableTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.variableOverridesDao.getByVariableAndProgram(variableTerm.getCvTermId(), this.DUMMY_PROGRAM_UUID)).thenReturn(overrides);
        Mockito.when(this.programFavoriteDAO.getProgramFavorite(this.DUMMY_PROGRAM_UUID, ProgramFavorite.FavoriteType.VARIABLE, variableTerm.getCvTermId())).thenReturn(programFavorite);
        Mockito.when(this.daoFactory.getCvTermDao().getAllByCvId(CvId.SCALES, true)).thenReturn(cvTermList);

        Variable variable = this.variableDataManager.getVariable(this.DUMMY_PROGRAM_UUID, variableTerm.getCvTermId(), true, false);

        TermSummary termSummary = new TermSummary(DataType.CATEGORICAL_VARIABLE.getId(), "Category1", "Definition");

        Scale scale = new Scale();
        scale.setId(CvId.SCALES.getId());
        scale.setDataType(DataType.CATEGORICAL_VARIABLE);
        scale.addCategory(termSummary);

        Mockito.when(this.programFavoriteDAO.getProgramFavorite(this.DUMMY_PROGRAM_UUID, ProgramFavorite.FavoriteType.VARIABLE, variableTerm.getCvTermId())).thenReturn(programFavorite);
        Mockito.when(this.termDao.getById(variableTerm.getCvTermId())).thenReturn(variableTerm);

        variable.setScale(scale);

        this.variableDataManager.retrieveVariableCategoricalValue(this.DUMMY_PROGRAM_UUID, variableTerm.getCvTermId() , DataType.CATEGORICAL_VARIABLE.getId());
    }

    /**
     * Tes to retrieve variable categorical value with same term id
     *
     * @throws Exception
     */
    @Test
    public void testRetrieveVariableCategoricalValueWithSameTermId() throws Exception {

        //Variables
        List<CVTerm> variableTerms = new ArrayList<>();
        //Fill 1 variables using helper
        TestDataHelper.fillTestVariableCvTerms(variableTerms, 1);

        CVTerm variableTerm = variableTerms.get(0);

        // Fill Test created date property
        Date testCreatedDateForVariable = TestDataHelper.constructDate(2015, Calendar.JANUARY, 1);
        List<CVTermProperty> variableCreatedDateProperty = new ArrayList<>();
        TestDataHelper.fillTestCreatedDateProperties(variableTerms, variableCreatedDateProperty, testCreatedDateForVariable);

        // Fill Test update date property
        Date testUpdatedDateForVariable = TestDataHelper.constructDate(2015, Calendar.JUNE, 30);
        List<CVTermProperty> variableUpdatedDateProperty = new ArrayList<>();
        TestDataHelper.fillTestUpdatedDateProperties(variableTerms, variableUpdatedDateProperty, testUpdatedDateForVariable);

        //Combining Properties
        List<CVTermProperty> combinedPropertiesForVariables = new ArrayList<>(variableCreatedDateProperty);
        combinedPropertiesForVariables.addAll(variableUpdatedDateProperty);

        CVTerm scaleTerm = TestDataHelper.getTestCvTerm(CvId.SCALES);

        //Adding cvterm relationship data
        CVTermRelationship cvTermRelationship = new CVTermRelationship();
        cvTermRelationship.setCvTermRelationshipId(UnitTestDaoIDGenerator.generateId(CVTermRelationship.class));
        cvTermRelationship.setSubjectId(scaleTerm.getCvTermId());
        cvTermRelationship.setTypeId(TermId.HAS_TYPE.getId());
        cvTermRelationship.setObjectId(CvId.SCALES.getId());

        List<CVTermRelationship> relationships = new ArrayList<>();
        relationships.add(cvTermRelationship);

        Mockito.when(this.termDao.getAllByCvId(CvId.VARIABLES, false)).thenReturn(variableTerms);

        //Creating cvterm list
        List<CVTerm> cvTermList = new ArrayList<>();
        CVTerm cvTerm = new CVTerm();
        cvTerm.setCv(CvId.SCALES.getId());
        cvTerm.setCvTermId(1);
        cvTerm.setName("Name");
        cvTerm.setDefinition("Definition");
        cvTerm.setIsObsolete(false);
        cvTermList.add(cvTerm);

        VariableOverrides overrides = new VariableOverrides();
        ProgramFavorite programFavorite = new ProgramFavorite();

        Mockito.when(this.termDao.getById(variableTerm.getCvTermId())).thenReturn(variableTerm);
        Mockito.when(this.cvTermPropertyDao.getByCvTermId(variableTerm.getCvTermId())).thenReturn(combinedPropertiesForVariables);
        Mockito.when(this.cvTermRelationshipDao.getBySubject(variableTerm.getCvTermId())).thenReturn(relationships);
        Mockito.when(this.variableOverridesDao.getByVariableAndProgram(variableTerm.getCvTermId(), this.DUMMY_PROGRAM_UUID)).thenReturn(overrides);
        Mockito.when(this.programFavoriteDAO.getProgramFavorite(this.DUMMY_PROGRAM_UUID, ProgramFavorite.FavoriteType.VARIABLE, variableTerm.getCvTermId())).thenReturn(programFavorite);
        Mockito.when(this.daoFactory.getCvTermDao().getAllByCvId(CvId.SCALES, true)).thenReturn(cvTermList);

        Variable variable = this.variableDataManager.getVariable(this.DUMMY_PROGRAM_UUID, variableTerm.getCvTermId(), true, false);

        TermSummary termSummary = new TermSummary(1, "Category1", "Definition");

        Scale scale = new Scale();
        scale.setId(CvId.SCALES.getId());
        scale.setDataType(DataType.CATEGORICAL_VARIABLE);
        scale.addCategory(termSummary);

        Mockito.when(this.programFavoriteDAO.getProgramFavorite(this.DUMMY_PROGRAM_UUID, ProgramFavorite.FavoriteType.VARIABLE, variableTerm.getCvTermId())).thenReturn(programFavorite);
        Mockito.when(this.termDao.getById(variableTerm.getCvTermId())).thenReturn(variableTerm);

        variable.setScale(scale);

        this.variableDataManager.retrieveVariableCategoricalValue(this.DUMMY_PROGRAM_UUID, variableTerm.getCvTermId() , DataType.CATEGORICAL_VARIABLE.getId());

        Assert.assertNotNull(variable);
    }

    /**
     * Test to retrieve null categorical value
     *
     * @throws Exception
     */
    @Test
    public void testRetrieveNullVariableCategoricalValue() throws Exception {
        //Variables
        List<CVTerm> variableTerms = new ArrayList<>();
        //Fill 1 variables using helper
        TestDataHelper.fillTestVariableCvTerms(variableTerms, 1);

        CVTerm variableTerm = variableTerms.get(0);

        Mockito.when(this.termDao.getAllByCvId(CvId.VARIABLES, false)).thenReturn(variableTerms);
        Mockito.when(this.daoFactory.getCvTermDao().getAllByCvId(CvId.VARIABLES, true)).thenReturn(variableTerms);
        Mockito.when(this.termDao.getById(variableTerm.getCvTermId())).thenReturn(variableTerm);
        Variable variable = this.variableDataManager.getVariable(this.DUMMY_PROGRAM_UUID , variableTerm.getCvTermId() , true , false);

        Scale scale = new Scale();
        scale.setId(CvId.SCALES.getId());
        scale.setDataType(DataType.CATEGORICAL_VARIABLE);

        variable.setScale(scale);

        this.variableDataManager.retrieveVariableCategoricalValue(this.DUMMY_PROGRAM_UUID, variableTerm.getCvTermId() , DataType.CATEGORICAL_VARIABLE.getId());
    }

    /**
     * Test to check process treatment factor has pair value.
     *
     * @throws Exception
     */
    @Test
    public void testProcessTreatmentFactorHasPairValue() throws Exception{

        OntologyVariableInfo variableInfo = new OntologyVariableInfo();
        variableInfo.setId(1);
        variableInfo.setPropertyId(CvId.VARIABLES.getId());

        Property property = new Property();
        property.setId(1);

        Variable variable = new Variable();
        variable.setId(CvId.METHODS.getId());
        variable.toCVTerm().setCvTermId(variableInfo.getId());
        variable.setProperty(property);

        List<Variable> variables = new ArrayList<>();
        variables.add(variable);

        List<Integer> hiddenFields = new ArrayList<>();
        hiddenFields.add(variable.getId());

        Mockito.when(this.termDao.hasPossibleTreatmentPairs(variable.getId(), variable.getProperty().getId(), hiddenFields)).thenReturn(false);

        this.variableDataManager.processTreatmentFactorHasPairValue(variables , hiddenFields);
    }
}