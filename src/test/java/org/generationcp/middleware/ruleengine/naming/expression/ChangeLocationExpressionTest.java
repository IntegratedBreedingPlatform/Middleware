package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.domain.germplasm.BasicGermplasmDTO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by Daniel Villafuerte on 6/16/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ChangeLocationExpressionTest {

	public static final int ORIGINAL_LOCATION_ID = 2;
	public static final int TEST_GID = 3;
	public static final String NEW_LOCATION_ABBR = "ABC";

	@InjectMocks
	private ChangeLocationExpression dut;

	@Test
	public void testChangeLocationExpressionNoChange() throws MiddlewareException {
		final AdvancingSource source = mock(AdvancingSource.class);
		Mockito.when(source.getHarvestLocationId()).thenReturn(ORIGINAL_LOCATION_ID);
		Mockito.when(source.getOriginGermplasm()).thenReturn(Mockito.mock(BasicGermplasmDTO.class));

		final List<StringBuilder> input = constructExpressionInput();

		dut.apply(input, source, null);
		assertEquals("", input.get(0).toString());
	}

	@Test
	public void testChangeLocationExpressionChanged() throws MiddlewareException {
		final AdvancingSource source = mock(AdvancingSource.class);
		Mockito.when(source.getHarvestLocationId()).thenReturn(ORIGINAL_LOCATION_ID + 1);
		Mockito.when(source.getLocationAbbreviation()).thenReturn(NEW_LOCATION_ABBR);
		Mockito.when(source.getOriginGermplasm()).thenReturn(Mockito.mock(BasicGermplasmDTO.class));

		final List<StringBuilder> input = constructExpressionInput();

		dut.apply(input, source, null);
		assertEquals(NEW_LOCATION_ABBR, input.get(0).toString());
	}

	protected List<StringBuilder> constructExpressionInput() {
		final List<StringBuilder> list = new ArrayList<>();
		list.add(new StringBuilder(ChangeLocationExpression.KEY));

		return list;
	}

}
