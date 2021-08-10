package org.generationcp.middleware.service.impl.ontology;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.service.api.ontology.PersonValidator;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Random;

@RunWith(MockitoJUnitRunner.class)
public class PersonValidatorTest {

	@Mock
	private UserService userService;

	@InjectMocks
	private PersonValidator personValidator;

	@Test(expected = IllegalStateException.class)
	public void test_isValid_NonPersonDataType_ShouldThrowException(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.CHARACTER_VARIABLE.getId());
		this.personValidator.isValid(variable);
	}

	@Test
	public void test_isValid_EmptyValue(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.PERSON.getId());
		Assert.assertTrue(this.personValidator.isValid(variable));
	}

	@Test
	public void test_isValid_PersonIDScale_ValidValue(){
		final int id = new Random().nextInt(100);
		Mockito.doReturn(new Person()).when(this.userService).getPersonById(id);

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.PERSON.getId());
		variable.setScaleId(TermId.PERSON_ID.getId());
		variable.setValue(String.valueOf(id));
		Assert.assertTrue(this.personValidator.isValid(variable));
	}

	@Test
	public void test_isValid_PersonIDScale_NonExistingId(){
		final int id = new Random().nextInt(100);
		Mockito.doReturn(null).when(this.userService).getPersonById(id);

		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.PERSON.getId());
		variable.setScaleId(TermId.PERSON_ID.getId());
		variable.setValue(String.valueOf(id));
		Assert.assertFalse(this.personValidator.isValid(variable));
	}

	@Test
	public void test_isValid_PersonIDScale_NonDigitsID(){
		final MeasurementVariable variable = new MeasurementVariable();
		variable.setDataTypeId(DataType.PERSON.getId());
		variable.setScaleId(TermId.PERSON_ID.getId());
		variable.setValue("1.5");
		Assert.assertFalse(this.personValidator.isValid(variable));
	}



}
