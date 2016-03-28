
package org.generationcp.middleware.service;

import org.generationcp.middleware.dao.CountryDAO;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.reports.AbstractReporter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

public class ReportServiceImplTest {

	private final ReportServiceImpl reportService = new ReportServiceImpl();

	public static final int TEST_GERMPLASM_LIST_ID = 2;
    public static final Integer TEST_LOCATION_ID = 3;
    public static final int TEST_COUNTRY_ID = 4;
    public static final String TEST_COUNTRY_ISO_NAME = "Afghanistan";
    public static final String TEST_LOCATION_ABBREVIATION = "ICWS";

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
		final Map<String, Object> data = reportService.extractGermplasmListData(TEST_GERMPLASM_LIST_ID);
		Assert.assertNotNull("Data extracter does not provide non null value for required report generation parameter",
				data.get(AbstractReporter.STUDY_CONDITIONS_KEY));
        Assert.assertNotNull("Data extracter does not provide non null value for required report generation parameter",
                data.get(AbstractReporter.DATA_SOURCE_KEY));
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

        Mockito.doCallRealMethod().when(mocked).appendCountryInformation(variableList);
        Mockito.doCallRealMethod().when(mocked).retrieveLocationIdFromCondition(variableList);
        Mockito.doReturn(locationDataManager).when(mocked).getLocationDataManager();
        Mockito.doReturn(countryDAO).when(mocked).getCountryDao();

        Mockito.doReturn(location).when(locationDataManager).getLocationByID(TEST_LOCATION_ID);
        Mockito.doReturn(country).when(countryDAO).getById(TEST_COUNTRY_ID);
        final List<MeasurementVariable> processed = mocked.appendCountryInformation(variableList);

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
        final List<MeasurementVariable> processed = reportService.appendCountryInformation(variableList);

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
