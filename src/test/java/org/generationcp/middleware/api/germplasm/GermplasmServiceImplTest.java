package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Attribute;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

public class GermplasmServiceImplTest {

	@Test
	public void testGetPlotCodeValue() {
		final GermplasmServiceImpl unitToTest = new GermplasmServiceImpl(Mockito.mock(HibernateSessionProvider.class));

		// We want to mock away calls to other methods in same unit.
		final GermplasmServiceImpl partiallyMockedUnit = Mockito.spy(unitToTest);
		final Integer testGid = 1;

		// First set up data such that no plot code attribute is associated.
		Mockito.doReturn(null).when(partiallyMockedUnit).getPlotCodeField();
		final List<Attribute> attributes = new ArrayList<Attribute>();
		Mockito.doReturn(attributes).when(partiallyMockedUnit).getAttributesByGID(Matchers.anyInt());

		final String plotCode1 = partiallyMockedUnit.getPlotCodeValue(testGid);
		assertThat("getPlotCodeValue() should never return null.", plotCode1, is(notNullValue()));
		assertThat("Expected `Unknown` returned when there is no plot code attribute present.", "Unknown", is(equalTo(plotCode1)));
		// Now setup data so that gid has plot code attribute associated with it.
		final UserDefinedField udfld = new UserDefinedField();
		udfld.setFldno(1152);
		udfld.setFtable("ATRIBUTS");
		udfld.setFtype("PASSPORT");
		udfld.setFcode("PLOTCODE");

		Mockito.when(partiallyMockedUnit.getPlotCodeField()).thenReturn(udfld);
		final Attribute plotCodeAttr = new Attribute();
		plotCodeAttr.setTypeId(udfld.getFldno());
		plotCodeAttr.setAval("The PlotCode Value");
		attributes.add(plotCodeAttr);
		Mockito.when(partiallyMockedUnit.getAttributesByGID(testGid)).thenReturn(attributes);

		final String plotCode2 = partiallyMockedUnit.getPlotCodeValue(testGid);
		assertThat("getPlotCodeValue() should never return null.", plotCode2, is(notNullValue()));
		assertThat("Expected value of plot code attribute returned when plot code attribute is present.", plotCodeAttr.getAval(),
			is(equalTo(plotCode2)));
	}

}
