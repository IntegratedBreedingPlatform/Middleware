package org.generationcp.middleware.service.impl.ontology;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.api.location.LocationService;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.service.api.ontology.LocationValidator;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LocationValidatorTest {

	@Mock
	private LocationService locationService;

	@InjectMocks
	private LocationValidator locationValidator;

	@Test(expected = IllegalStateException.class)
	public void test_isValid_NonLocationDataType_ShouldThrowException(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.CHARACTER_VARIABLE.getId());
		this.locationValidator.isValid(variable);
	}

	@Test
	public void test_isValid_EmptyValue(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.LOCATION.getId());
		Assert.assertTrue(this.locationValidator.isValid(variable));
	}

	@Test
	public void test_isValid_LocationIDScale_ValidValue(){
		final Integer locId = new Random().nextInt(100);
		final LocationSearchRequest request = new LocationSearchRequest();
		request.setLocationIds(Collections.singletonList(locId));
		Mockito.doReturn(1L).when(this.locationService).countFilteredLocations(request, null);

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.LOCATION.getId());
		variable.setScaleId(LocationValidator.SCALE_LOC_ID);
		variable.setValue(locId.toString());
		Assert.assertTrue(this.locationValidator.isValid(variable));
	}

	@Test
	public void test_isValid_LocationIDScale_NonExistingID(){
		final Integer locId = new Random().nextInt(100);
		final LocationSearchRequest request = new LocationSearchRequest();
		request.setLocationIds(Collections.singletonList(locId));
		Mockito.doReturn(0L).when(this.locationService).countFilteredLocations(request, null);

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.LOCATION.getId());
		variable.setScaleId(LocationValidator.SCALE_LOC_ID);
		variable.setValue(locId.toString());
		Assert.assertFalse(this.locationValidator.isValid(variable));
	}

	@Test
	public void test_isValid_LocationIDScale_NonDigitsValue(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.LOCATION.getId());
		variable.setScaleId(LocationValidator.SCALE_LOC_ID);
		variable.setValue("1.5");
		Assert.assertFalse(this.locationValidator.isValid(variable));
	}

	@Test
	public void test_isValid_LocationAbbrScale_ValidValue(){
		final String locAbbr = RandomStringUtils.randomAlphabetic(20);
		final LocationSearchRequest request = new LocationSearchRequest();
		request.setLocationAbbreviations(Collections.singletonList(locAbbr));
		Mockito.doReturn(1L).when(this.locationService).countFilteredLocations(request, null);

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.LOCATION.getId());
		variable.setScaleId(LocationValidator.SCALE_LOC_ABBR);
		variable.setValue(locAbbr);
		Assert.assertTrue(this.locationValidator.isValid(variable));
	}

	@Test
	public void test_isValid_LocationAbbrScale_NonExistingAbbr(){
		final String locAbbr = RandomStringUtils.randomAlphabetic(20);
		final LocationSearchRequest request = new LocationSearchRequest();
		request.setLocationAbbreviations(Collections.singletonList(locAbbr));
		Mockito.doReturn(0L).when(this.locationService).countFilteredLocations(request, null);

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.LOCATION.getId());
		variable.setScaleId(LocationValidator.SCALE_LOC_ABBR);
		variable.setValue(locAbbr);
		Assert.assertFalse(this.locationValidator.isValid(variable));
	}

	@Test
	public void test_isValid_LocationNameScale_ValidValue(){
		final String locName = RandomStringUtils.randomAlphabetic(20);

		Mockito.doReturn(1L).when(this.locationService).countFilteredLocations(ArgumentMatchers.any(), ArgumentMatchers.isNull());

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.LOCATION.getId());
		variable.setScaleId(LocationValidator.SCALE_LOC_NAME);
		variable.setValue(locName);
		Assert.assertTrue(this.locationValidator.isValid(variable));

		final ArgumentCaptor<LocationSearchRequest> locationSearchRequestArgumentCaptor =
				ArgumentCaptor.forClass(LocationSearchRequest.class);

		Mockito.verify(this.locationService).countFilteredLocations(locationSearchRequestArgumentCaptor.capture(), ArgumentMatchers.isNull());
		final LocationSearchRequest actualLocationSearchRequest = locationSearchRequestArgumentCaptor.getValue();
		final SqlTextFilter locationNameFilter = actualLocationSearchRequest.getLocationNameFilter();
		assertNotNull(locationNameFilter);
		assertThat(locationNameFilter.getType(), is(SqlTextFilter.Type.STARTSWITH));
		assertNotNull(locationNameFilter.getValue(), is(locName));
	}

	@Test
	public void test_isValid_LocationNameScale_NonExistingName(){
		final String locName = RandomStringUtils.randomAlphabetic(20);

		Mockito.doReturn(0L).when(this.locationService).countFilteredLocations(ArgumentMatchers.any(), ArgumentMatchers.isNull());

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.LOCATION.getId());
		variable.setScaleId(LocationValidator.SCALE_LOC_NAME);
		variable.setValue(locName);
		Assert.assertFalse(this.locationValidator.isValid(variable));

		final ArgumentCaptor<LocationSearchRequest> locationSearchRequestArgumentCaptor =
				ArgumentCaptor.forClass(LocationSearchRequest.class);

		Mockito.verify(this.locationService).countFilteredLocations(locationSearchRequestArgumentCaptor.capture(), ArgumentMatchers.isNull());
		final LocationSearchRequest actualLocationSearchRequest = locationSearchRequestArgumentCaptor.getValue();
		final SqlTextFilter locationNameFilter = actualLocationSearchRequest.getLocationNameFilter();
		assertNotNull(locationNameFilter);
		assertThat(locationNameFilter.getType(), is(SqlTextFilter.Type.STARTSWITH));
		assertNotNull(locationNameFilter.getValue(), is(locName));
	}

}
