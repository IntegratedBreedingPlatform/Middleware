package org.generationcp.middleware.api.germplasm;

import com.google.common.collect.ImmutableSet;
import org.generationcp.middleware.dao.AttributeDAO;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.dao.UserDefinedFieldDAO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.UDTableType;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GermplasmServiceImplTest {

	private static final Integer GID = new Random().nextInt(Integer.MAX_VALUE);
	private static final Integer FIELD_NUMBER = ThreadLocalRandom.current().nextInt();

	@InjectMocks
	private GermplasmServiceImpl germplasmService;

	@Mock
	private DaoFactory daoFactory;

	@Mock
	private AttributeDAO attributeDAO;

	@Mock
	private UserDefinedFieldDAO userDefinedFieldDAO;

	@Captor
	private ArgumentCaptor<List<Integer>> integerListArgumentCaptor;

	@Captor
	private ArgumentCaptor<Set<String>> stringSetArgumentCaptor;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		//Mock DaoFactory
		ReflectionTestUtils.setField(this.germplasmService, "daoFactory", daoFactory);
		Mockito.when(this.daoFactory.getAttributeDAO()).thenReturn(this.attributeDAO);
		Mockito.when(this.daoFactory.getUserDefinedFieldDAO()).thenReturn(this.userDefinedFieldDAO);
	}

	@Test
	public void testGetPlotCodeValue() {
		final GermplasmServiceImpl unitToTest = new GermplasmServiceImpl(Mockito.mock(HibernateSessionProvider.class));

		// We want to mock away calls to other methods in same unit.
		final GermplasmServiceImpl partiallyMockedUnit = Mockito.spy(unitToTest);

		// First set up data such that no plot code attribute is associated.
		Mockito.doReturn(null).when(partiallyMockedUnit).getPlotCodeField();
		final List<Attribute> attributes = new ArrayList<>();
		Mockito.doReturn(attributes).when(partiallyMockedUnit).getAttributesByGID(ArgumentMatchers.anyInt());

		final String plotCode1 = partiallyMockedUnit.getPlotCodeValue(GID);
		assertThat("getPlotCodeValue() should never return null.", plotCode1, is(notNullValue()));
		assertThat("Expected `Unknown` returned when there is no plot code attribute present.", "Unknown", is(plotCode1));
		// Now setup data so that gid has plot code attribute associated with it.
		final UserDefinedField udfld = Mockito.mock(UserDefinedField.class);
		Mockito.when(udfld.getFcode()).thenReturn(GermplasmServiceImpl.PLOT_CODE);

		Mockito.when(partiallyMockedUnit.getPlotCodeField()).thenReturn(udfld);
		final Attribute plotCodeAttr = new Attribute();
		plotCodeAttr.setTypeId(udfld.getFldno());
		plotCodeAttr.setAval("The PlotCode Value");
		attributes.add(plotCodeAttr);
		Mockito.when(partiallyMockedUnit.getAttributesByGID(GID)).thenReturn(attributes);

		final String plotCode2 = partiallyMockedUnit.getPlotCodeValue(GID);
		assertThat("getPlotCodeValue() should never return null.", plotCode2, is(notNullValue()));
		assertThat("Expected value of plot code attribute returned when plot code attribute is present.", plotCodeAttr.getAval(),
			is(equalTo(plotCode2)));
	}

	@Test
	public void test_getPlotCodeValues_OK() {
		final int unknownPlotCodeGid = new Random().nextInt();
		final String plotCodeValue = UUID.randomUUID().toString();

		//Mock attribute
		final Attribute attribute = Mockito.mock(Attribute.class);
		Mockito.when(attribute.getGermplasmId()).thenReturn(GID);
		Mockito.when(attribute.getAval()).thenReturn(plotCodeValue);
		final List<Attribute> attributes = Arrays.asList(attribute);
		Mockito.when(this.attributeDAO.getAttributeValuesByTypeAndGIDList(ArgumentMatchers.eq(FIELD_NUMBER), ArgumentMatchers.anyList()))
			.thenReturn(attributes);

		//Create partial mock for the unit to be tested
		final GermplasmServiceImpl partiallyMockedUnit = Mockito.spy(new GermplasmServiceImpl(Mockito.mock(HibernateSessionProvider.class)));
		ReflectionTestUtils.setField(partiallyMockedUnit, "daoFactory", daoFactory);

		//Mock GermplasmServiceImpl#getPlotCodeField);
		final UserDefinedField userDefinedField = Mockito.mock(UserDefinedField.class);
		Mockito.when(userDefinedField.getFldno()).thenReturn(FIELD_NUMBER);
		Mockito.doReturn(userDefinedField).when(partiallyMockedUnit).getPlotCodeField();

		final Map<Integer, String> plotCodeValues = partiallyMockedUnit.getPlotCodeValues(ImmutableSet.of(GID, unknownPlotCodeGid));
		assertNotNull(plotCodeValues);
		assertThat(plotCodeValues.size(), is(2));
		assertTrue(plotCodeValues.containsKey(GID));
		assertThat(plotCodeValues.get(GID), is(plotCodeValue));

		assertTrue(plotCodeValues.containsKey(unknownPlotCodeGid));
		assertThat(plotCodeValues.get(unknownPlotCodeGid), is(GermplasmListDataDAO.SOURCE_UNKNOWN));

		Mockito.verify(this.attributeDAO).getAttributeValuesByTypeAndGIDList(ArgumentMatchers.eq(FIELD_NUMBER), this.integerListArgumentCaptor
			.capture());
		final List<Integer> actualGIDs = this.integerListArgumentCaptor.getValue();
		assertNotNull(actualGIDs);
		assertThat(actualGIDs.size(), is(2));
		assertThat(actualGIDs, hasItems(GID, unknownPlotCodeGid));
	}

	@Test
	public void test_getPlotCodeField_OK() {
		final UserDefinedField userDefinedField = Mockito.mock(UserDefinedField.class);
		Mockito.when(userDefinedField.getFcode()).thenReturn(GermplasmServiceImpl.PLOT_CODE);
		this.mockUserDefinedFieldDAOGetByFieldTableNameAndType(userDefinedField);

		final UserDefinedField actualPlotCodeField = this.germplasmService.getPlotCodeField();
		assertThat(actualPlotCodeField, is(userDefinedField));

		this.verifyUserDefinedFieldDAOGetByFieldTableNameAndType();
	}

	@Test
	public void test_getPlotCodeField_NoPlotCodeNotAttrAssociated_OK() {
		this.mockUserDefinedFieldDAOGetByFieldTableNameAndType(Mockito.mock(UserDefinedField.class));

		final UserDefinedField actualPlotCodeField = this.germplasmService.getPlotCodeField();
		assertNotNull(actualPlotCodeField);
		assertThat(actualPlotCodeField.getFldno(), is(0));

		this.verifyUserDefinedFieldDAOGetByFieldTableNameAndType();
	}

	private void mockUserDefinedFieldDAOGetByFieldTableNameAndType(UserDefinedField userDefinedField) {
		Mockito.when(this.userDefinedFieldDAO.getByFieldTableNameAndType(ArgumentMatchers.eq(UDTableType.ATRIBUTS_PASSPORT.getTable()),
			ArgumentMatchers.anySet())).thenReturn(Arrays.asList(userDefinedField));
	}

	private void verifyUserDefinedFieldDAOGetByFieldTableNameAndType() {
		Mockito.verify(this.userDefinedFieldDAO).getByFieldTableNameAndType(ArgumentMatchers.eq(UDTableType.ATRIBUTS_PASSPORT.getTable()),
			this.stringSetArgumentCaptor.capture());
		final Set<String> actualFieldTypes = this.stringSetArgumentCaptor.getValue();
		assertThat(actualFieldTypes.size(), is(1));
		assertThat(actualFieldTypes, hasItem(UDTableType.ATRIBUTS_PASSPORT.getType()));
	}

}
