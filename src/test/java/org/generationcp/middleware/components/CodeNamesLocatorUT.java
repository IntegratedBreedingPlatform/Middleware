package org.generationcp.middleware.components;

import java.util.List;
import java.util.Properties;

import org.generationcp.middleware.manager.api.UserDefinedFieldsDataManager;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class CodeNamesLocatorUT {

	public static final String CODED_NAMES_IDS = "1,2,3,4";
	public static final String EMPTY_STRING = "";
	public static final int EXPECTED_CODEDNAMES_SIZE = 4;
	public static final int VALUE_10 = 10;
	private static final int VALUE_12 = 12;
	public static final int EXPECTED_VALUE_2 = 2;
	public static final String GERMPLASM_CODE_NAMES_IDS = "germplasm.code.names.ids";
	CodeNamesLocator target;

	@Mock
	Properties properties;

	@Mock
	UserDefinedFieldsDataManager manager;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		target = new CodeNamesLocator(properties,manager);

	}

	@Test
	public void locateNonCodeNamesReturnsAListOfQueriedUDFLDWithoutCodeNameIds() throws Exception {

		when(properties.getProperty(GERMPLASM_CODE_NAMES_IDS)).thenReturn(CODED_NAMES_IDS);
		UserDefinedField expectedItem1 = new UserDefinedField(new Integer(VALUE_10));
		UserDefinedField expectedItem2 = new UserDefinedField(new Integer(VALUE_12));
		List<UserDefinedField> expectedList = Lists.newArrayList(expectedItem1,expectedItem2);
		when(manager.getNotCodeNamesFactor(anyList())).thenReturn(expectedList);

		List<UserDefinedField> list = target.locateNonCodeNames();

		assertThat(list).contains(expectedItem1,expectedItem2);
		assertThat(list).hasSize(EXPECTED_VALUE_2);

		verify(manager).getNotCodeNamesFactor(anyList());

	}



	@Test
	public void locateCodedNamesIdsReturnAListOfCodedIdsWhenPropertiesFileExist() throws Exception {
		when(properties.getProperty(GERMPLASM_CODE_NAMES_IDS)).thenReturn(CODED_NAMES_IDS);

		List<Integer> codedNamesIds = target.locateCodedNamesIds();

		assertThat(codedNamesIds).contains(new Integer(1),new Integer(2),new Integer(3),new Integer(4));
		assertThat(codedNamesIds).hasSize(EXPECTED_CODEDNAMES_SIZE);
	}

	@Test
	public void getCodedNamesIdsReturnEmptyListWhenPropertyIsEmpty() throws Exception {
		when(properties.getProperty(GERMPLASM_CODE_NAMES_IDS)).thenReturn(EMPTY_STRING);

		List<Integer> codedNamesIds = target.locateCodedNamesIds();

		assertThat(codedNamesIds).isEmpty();
	}
}
