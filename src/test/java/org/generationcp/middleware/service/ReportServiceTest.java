
package org.generationcp.middleware.service;

import org.generationcp.middleware.dao.CountryDAO;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.LocationDataManager;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.reports.AbstractReporter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest {

	private static final Integer TEST_COUNTRY_ID = 1;
	private static final Integer TEST_LOCATION_ID = 1;
	public static final String TEST_ABBR = "TestAbbr";
	public static final String TEST_COUNTRY_FULL_NAME = "TEST_FULL_NAME";

	// Mocking the object for now,
	// until we can de-entangle the Middleware dependencies that it needs
	private ReportServiceImpl unitUnderTest = Mockito.spy(new ReportServiceImpl());
	private LocationDataManager locationDataManager;
	private CountryDAO countryDAO;

	@Mock
	private DaoFactory daoFactory;

	@Before
	public void setUp() throws Exception {
		unitUnderTest.setDaoFactory(daoFactory);
		locationDataManager = Mockito.mock(LocationDataManager.class);
		countryDAO = Mockito.mock(CountryDAO.class);
		Mockito.doReturn(locationDataManager).when(unitUnderTest).getLocationDataManager();
		Mockito.doReturn(countryDAO).when(daoFactory).getCountryDao();
	}

	@Test
	public void testPopulateLocationInformation() {
		List<MeasurementVariable> sampleCondition = new ArrayList<>();
		MeasurementVariable location = new MeasurementVariable();
		location.setName("LOCATION_ID");
		location.setTermId(TermId.LOCATION_ID.getId());
		location.setValue(TEST_LOCATION_ID.toString());

		sampleCondition.add(location);
		Location location1 = new Location(1);
		location1.setLabbr(TEST_ABBR);
		location1.setCntryid(TEST_COUNTRY_ID);
		Mockito.when(locationDataManager.getLocationByID(TEST_LOCATION_ID)).thenReturn(location1);

		Country testCountry = new Country(TEST_COUNTRY_ID);
		testCountry.setIsofull(TEST_COUNTRY_FULL_NAME);

		Mockito.when(countryDAO.getById(TEST_COUNTRY_ID)).thenReturn(testCountry);

		List<MeasurementVariable> populated = unitUnderTest.appendCountryInformationFromCondition(sampleCondition);
		Assert.assertNotEquals("Measurement variables should now contain location abbreviation and country information",
				sampleCondition.size(), populated.size());

		boolean countryInfoFound = false;
		boolean abbreInfoFound = false;
		for (MeasurementVariable measurementVariable : populated) {
			if (measurementVariable.getName().equals(AbstractReporter.LOCATION_ABBREV_VARIABLE_NAME)) {
				abbreInfoFound = true;
			}

			if (measurementVariable.getName().equals(AbstractReporter.COUNTRY_VARIABLE_NAME)) {
				countryInfoFound = true;
			}
		}

		Assert.assertTrue("Country information not properly appended", countryInfoFound);
		Assert.assertTrue("Abbreviation not properly appended", abbreInfoFound);
	}
}
