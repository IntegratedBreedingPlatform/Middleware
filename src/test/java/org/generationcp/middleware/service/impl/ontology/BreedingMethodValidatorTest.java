package org.generationcp.middleware.service.impl.ontology;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.api.breedingmethod.BreedingMethodSearchRequest;
import org.generationcp.middleware.api.breedingmethod.BreedingMethodService;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.generationcp.middleware.service.api.ontology.BreedingMethodValidator;
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
public class BreedingMethodValidatorTest {

	@Mock
	private BreedingMethodService breedingMethodService;

	@InjectMocks
	private BreedingMethodValidator breedingMethodValidator;

	@Test(expected = IllegalStateException.class)
	public void test_isValid_NonBreedingMethodDataType_ShouldThrowException(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.CHARACTER_VARIABLE.getId());
		this.breedingMethodValidator.isValid(variable);
	}

	@Test
	public void test_isValid_EmptyValue(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.BREEDING_METHOD.getId());
		Assert.assertTrue(this.breedingMethodValidator.isValid(variable));
	}

	@Test
	public void test_isValid_BreedingMethodIDScale_ValidValue(){
		final Integer id = new Random().nextInt(100);
		final BreedingMethodSearchRequest request = new BreedingMethodSearchRequest();
		request.setMethodIds(Collections.singletonList(id));
		Mockito.doReturn(1L).when(this.breedingMethodService).countSearchBreedingMethods(request, null);

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.BREEDING_METHOD.getId());
		variable.setScaleId(BreedingMethodValidator.SCALE_BM_ID);
		variable.setValue(id.toString());
		Assert.assertTrue(this.breedingMethodValidator.isValid(variable));
	}

	@Test
	public void test_isValid_BreedingMethodIDScale_NonExistingValue(){
		final Integer id = new Random().nextInt(100);
		final BreedingMethodSearchRequest request = new BreedingMethodSearchRequest();
		request.setMethodIds(Collections.singletonList(id));
		Mockito.doReturn(0L).when(this.breedingMethodService).countSearchBreedingMethods(request, null);

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.BREEDING_METHOD.getId());
		variable.setScaleId(BreedingMethodValidator.SCALE_BM_ID);
		variable.setValue(id.toString());
		Assert.assertFalse(this.breedingMethodValidator.isValid(variable));
	}

	@Test
	public void test_isValid_BreedingMethodIDScale_NonDigitsValue(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.BREEDING_METHOD.getId());
		variable.setScaleId(BreedingMethodValidator.SCALE_BM_ID);
		variable.setValue("1.5");
		Assert.assertFalse(this.breedingMethodValidator.isValid(variable));
	}

	@Test
	public void test_isValid_BreedingMethodAbbrScale_ValidValue(){
		final String abbr = RandomStringUtils.randomAlphabetic(20);
		final BreedingMethodSearchRequest request = new BreedingMethodSearchRequest();
		request.setMethodAbbreviations(Collections.singletonList(abbr));
		Mockito.doReturn(1L).when(this.breedingMethodService).countSearchBreedingMethods(request, null);

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.BREEDING_METHOD.getId());
		variable.setScaleId(TermId.BREEDING_METHOD_SCALE.getId());
		variable.setValue(abbr);
		Assert.assertTrue(this.breedingMethodValidator.isValid(variable));
	}

	@Test
	public void test_isValid_BreedingMethodAbbrScale_NonExistingAbbr(){
		final String abbr = RandomStringUtils.randomAlphabetic(20);
		final BreedingMethodSearchRequest request = new BreedingMethodSearchRequest();
		request.setMethodAbbreviations(Collections.singletonList(abbr));
		Mockito.doReturn(0L).when(this.breedingMethodService).countSearchBreedingMethods(request, null);

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.BREEDING_METHOD.getId());
		variable.setScaleId(TermId.BREEDING_METHOD_SCALE.getId());
		variable.setValue(abbr);
		Assert.assertFalse(this.breedingMethodValidator.isValid(variable));
	}

	@Test
	public void test_isValid_BreedingMethodNameScale_ValidValue(){
		final String name = RandomStringUtils.randomAlphabetic(20);
		Mockito.doReturn(1L).when(this.breedingMethodService).countSearchBreedingMethods(ArgumentMatchers.any(BreedingMethodSearchRequest.class), ArgumentMatchers.isNull());

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.BREEDING_METHOD.getId());
		variable.setScaleId(BreedingMethodValidator.SCALE_BM_NAME);
		variable.setValue(name);
		Assert.assertTrue(this.breedingMethodValidator.isValid(variable));

		final ArgumentCaptor<BreedingMethodSearchRequest> breedingMethodSearchRequestArgumentCaptor =
				ArgumentCaptor.forClass(BreedingMethodSearchRequest.class);
		Mockito.verify(this.breedingMethodService).countSearchBreedingMethods(breedingMethodSearchRequestArgumentCaptor.capture(), ArgumentMatchers.isNull());
		final BreedingMethodSearchRequest actualBreedingMethodSearchRequest = breedingMethodSearchRequestArgumentCaptor.getValue();
		final SqlTextFilter nameFilter = actualBreedingMethodSearchRequest.getNameFilter();
		assertNotNull(nameFilter);
		assertThat(nameFilter.getValue(), is(name));
		assertThat(nameFilter.getType(), is(SqlTextFilter.Type.EXACTMATCH));
	}

	@Test
	public void test_isValid_BreedingMethodNameScale_NonExistingName(){
		final String name = RandomStringUtils.randomAlphabetic(20);
		Mockito.doReturn(0L).when(this.breedingMethodService).countSearchBreedingMethods(ArgumentMatchers.any(BreedingMethodSearchRequest.class), ArgumentMatchers.isNull());

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.BREEDING_METHOD.getId());
		variable.setScaleId(BreedingMethodValidator.SCALE_BM_NAME);
		variable.setValue(name);
		Assert.assertFalse(this.breedingMethodValidator.isValid(variable));

		final ArgumentCaptor<BreedingMethodSearchRequest> breedingMethodSearchRequestArgumentCaptor =
				ArgumentCaptor.forClass(BreedingMethodSearchRequest.class);
		Mockito.verify(this.breedingMethodService).countSearchBreedingMethods(breedingMethodSearchRequestArgumentCaptor.capture(), ArgumentMatchers.isNull());
		final BreedingMethodSearchRequest actualBreedingMethodSearchRequest = breedingMethodSearchRequestArgumentCaptor.getValue();
		final SqlTextFilter nameFilter = actualBreedingMethodSearchRequest.getNameFilter();
		assertNotNull(nameFilter);
		assertThat(nameFilter.getValue(), is(name));
		assertThat(nameFilter.getType(), is(SqlTextFilter.Type.EXACTMATCH));
	}

}
