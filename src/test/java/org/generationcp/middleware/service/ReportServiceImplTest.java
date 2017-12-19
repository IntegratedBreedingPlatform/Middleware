
package org.generationcp.middleware.service;

import org.generationcp.middleware.dao.CountryDAO;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.data.initializer.GermplasmDataManagerDataInitializer;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.reports.AbstractReporter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceImplTest {

	@Mock
	private GermplasmDataManager germplasmDataManager;

	private final ReportServiceImpl reportService = new ReportServiceImpl();

	public static final int TEST_GERMPLASM_LIST_ID = 2;
    public static final Integer TEST_LOCATION_ID = 3;
    public static final int TEST_COUNTRY_ID = 4;
    public static final String TEST_COUNTRY_ISO_NAME = "Afghanistan";
    public static final String TEST_LOCATION_ABBREVIATION = "ICWS";
	public static final int TEST_STUDY_ID = 1;

	@Test
	public void testRetrieveLocationIdFromConditionReturnsNullForEmptyString() {
		List<MeasurementVariable> conditionList = WorkbookTestDataInitializer.createConditions(false, 1, TEST_LOCATION_ID);
        this.setBlankLocationValue(conditionList);

		Assert.assertNull("Expecting a null for location id when the value of LOCATION ID variable is empty string.",
				this.reportService.retrieveLocationIdFromCondition(conditionList));
	}

	@Test
	public void testRetrieveLocationIdFromConditionReturnsAnIntegerForValidNumber() {
        List<MeasurementVariable> conditionList = WorkbookTestDataInitializer.createConditions(false, 1, TEST_LOCATION_ID);

		Assert.assertEquals("Expecting an integer for location id when the value of LOCATION ID variable is a valid number > 0.",
				TEST_LOCATION_ID, this.reportService.retrieveLocationIdFromCondition(conditionList));
	}

	@Test
	public void testExtractGermplasmListData() {
		final Map<String, Object> data = reportService.extractGermplasmListData();
		Assert.assertNotNull("Data extracter does not provide non null value for required report generation parameter",
				data.get(AbstractReporter.STUDY_CONDITIONS_KEY));
		Assert.assertNotNull("Data extracter does not provide non null value for required report generation parameter",
				data.get(AbstractReporter.DATA_SOURCE_KEY));
	}

	@Test
	public void testExtractParentDataSingleParent() {
		// we use partial mocking on the ReportServiceImplementation so as to be able to mock the GermplasmDataManager object
		final ReportServiceImpl unitUnderTest = Mockito.mock(ReportServiceImpl.class);
		Mockito.doReturn(this.germplasmDataManager).when(unitUnderTest).getGermplasmDataManager();

		final List<MeasurementRow> measurementRowList = createMeasurementList();
		final Map<Integer, GermplasmPedigreeTreeNode> pedigreeTreeNodeMap = GermplasmDataManagerDataInitializer.createTreeNodeMap(true);

		Mockito.doReturn(pedigreeTreeNodeMap).when(this.germplasmDataManager).getDirectParentsForStudy(TEST_STUDY_ID);
		Mockito.doCallRealMethod().when(unitUnderTest).appendParentsInformation(TEST_STUDY_ID, measurementRowList);
        Mockito.doCallRealMethod().when(unitUnderTest).provideParentInformation(Mockito.any(GermplasmPedigreeTreeNode.class), Mockito.anyList());

		unitUnderTest.appendParentsInformation(TEST_STUDY_ID, measurementRowList);

		final MeasurementRow row = measurementRowList.get(0);
		Assert.assertEquals("Female selection history should be blank if female parent information is not provided in database", ReportServiceImpl.BLANK_STRING_VALUE, row
				.getMeasurementData(AbstractReporter.FEMALE_SELECTION_HISTORY_KEY).getValue());
		Assert.assertEquals("Male selection history not properly filled in from provided information",
				GermplasmDataManagerDataInitializer.MALE_SELECTION_HISTORY,
				row.getMeasurementData(AbstractReporter.MALE_SELECTION_HISTORY_KEY).getValue());

	}

    @Test
    public void testAppendParentInfoNoParentsAvailable() {
        // we use partial mocking on the ReportServiceImplementation so as to be able to mock the GermplasmDataManager object
        final ReportServiceImpl unitUnderTest = Mockito.mock(ReportServiceImpl.class);
        Mockito.doReturn(this.germplasmDataManager).when(unitUnderTest).getGermplasmDataManager();

        final List<MeasurementRow> measurementRowList = createMeasurementList();

        // here we create a new empty map to simulate a scenario when no parental information is available for the given input
        Mockito.doReturn(new HashMap<Integer, GermplasmPedigreeTreeNode>()).when(this.germplasmDataManager).getDirectParentsForStudy(TEST_STUDY_ID);
        Mockito.doCallRealMethod().when(unitUnderTest).appendParentsInformation(TEST_STUDY_ID, measurementRowList);
        Mockito.doCallRealMethod().when(unitUnderTest).provideBlankParentInformationValues(Mockito.anyList());

        unitUnderTest.appendParentsInformation(TEST_STUDY_ID, measurementRowList);

        final MeasurementRow row = measurementRowList.get(0);
        Assert.assertEquals("Female selection history should be blank if female parent information is not provided in database", ReportServiceImpl.BLANK_STRING_VALUE, row
                .getMeasurementData(AbstractReporter.FEMALE_SELECTION_HISTORY_KEY).getValue());
        Assert.assertEquals("Male selection history should be blank if female parent information is not provided in database",
                ReportServiceImpl.BLANK_STRING_VALUE,
                row.getMeasurementData(AbstractReporter.MALE_SELECTION_HISTORY_KEY).getValue());

    }

	protected List<MeasurementRow> createMeasurementList() {
		final List<MeasurementRow> rowList = new ArrayList<>();
		final MeasurementRow row = new MeasurementRow();
		final List<MeasurementData> dataList = new ArrayList<>();
		final MeasurementData data = new MeasurementData("GID", GermplasmDataManagerDataInitializer.TEST_GID.toString());
		dataList.add(data);

		row.setDataList(dataList);
		rowList.add(row);

		return rowList;
	}

    @Test
    public void testAppendCountryInformationLocationProvided() {
        // we use partial mocking on the ReportServiceImplementation so as to be able to mock the GermplasmDataManager object
        final ReportServiceImpl mocked = Mockito.mock(ReportServiceImpl.class);
        final LocationDataManager locationDataManager = Mockito.mock(LocationDataManager.class);
        final CountryDAO countryDAO = Mockito.mock(CountryDAO.class);

        final Location location = new Location(TEST_LOCATION_ID);
        location.setCntryid(TEST_COUNTRY_ID);
        location.setLabbr(TEST_LOCATION_ABBREVIATION);

        final Country country = new Country(TEST_COUNTRY_ID);
        country.setIsofull(TEST_COUNTRY_ISO_NAME);


        final List<MeasurementVariable> variableList = WorkbookTestDataInitializer.createConditions(false, 1, TEST_LOCATION_ID);
        final int originalConditionSize = variableList.size();

        Mockito.doCallRealMethod().when(mocked).appendCountryInformationFromCondition(variableList);
        Mockito.doCallRealMethod().when(mocked).retrieveLocationIdFromCondition(variableList);
        Mockito.doCallRealMethod().when(mocked).createPlaceholderCountryMeasurementVariable(Mockito.anyString());
        Mockito.doReturn(locationDataManager).when(mocked).getLocationDataManager();
        Mockito.doReturn(countryDAO).when(mocked).getCountryDao();

        Mockito.doReturn(location).when(locationDataManager).getLocationByID(TEST_LOCATION_ID);
        Mockito.doReturn(country).when(countryDAO).getById(TEST_COUNTRY_ID);
        final List<MeasurementVariable> processed = mocked.appendCountryInformationFromCondition(variableList);

        Assert.assertTrue("Additional conditions must have been added to the condition list after processing the current location information", processed.size() > originalConditionSize);
        boolean countryVariableFound = false;
        boolean locationAbbrVariableFound = false;
        for (final MeasurementVariable variable : processed) {
            switch (variable.getName()) {
                case AbstractReporter.COUNTRY_VARIABLE_NAME:
                    countryVariableFound = true;
                    Assert.assertEquals("Expected value for country name is not found", TEST_COUNTRY_ISO_NAME, variable.getValue());
                    break;
                case AbstractReporter.LOCATION_ABBREV_VARIABLE_NAME:
                    locationAbbrVariableFound = true;
                    Assert.assertEquals("Expected value for location abbreviation is not found", TEST_LOCATION_ABBREVIATION, variable.getValue());
                    break;
                default:
                    // we do not care about the other measurement variables, so we do no extra processing here
                    break;
            }
        }

        Assert.assertTrue("Expected measurement variables were not found", countryVariableFound && locationAbbrVariableFound);
    }

    @Test
    public void testAppendCountryInformationLocationNotProvided() {
        final List<MeasurementVariable> variableList = WorkbookTestDataInitializer.createConditions(false, 1, TEST_LOCATION_ID);
        setBlankLocationValue(variableList);

        final int originalConditionListSize = variableList.size();
        final List<MeasurementVariable> processed = reportService.appendCountryInformationFromCondition(variableList);

        Assert.assertEquals("No additional processing should be done with blank location information", originalConditionListSize, processed.size());
    }

    protected void setBlankLocationValue(List<MeasurementVariable> conditionsList) {
        for (final MeasurementVariable variable : conditionsList) {
            if (TermId.LOCATION_ID.getId() == variable.getTermId() ) {
                // we simulate a blank location value here
                variable.setValue(null);
            }
        }
    }

}
