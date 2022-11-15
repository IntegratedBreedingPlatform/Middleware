package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Daniel Villafuerte on 6/16/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ChangeLocationExpressionTest {

	public static final int ORIGINAL_LOCATION_ID = 2;
	public static final int TEST_GID = 3;
	public static final String NEW_LOCATION_ABBR = "ABC";

	@Mock
	private GermplasmDataManager germplasmDataManager;

	@InjectMocks
	private ChangeLocationExpression dut;

	@Test
	public void testChangeLocationExpressionNoChange() throws MiddlewareException {
		final Germplasm germplasm = mock(Germplasm.class);
		when(germplasmDataManager.getGermplasmByGID(anyInt())).thenReturn(germplasm);

		when(germplasm.getLocationId()).thenReturn(ORIGINAL_LOCATION_ID);
		final List<StringBuilder> input = constructExpressionInput();

		final AdvancingSource source = mock(AdvancingSource.class);
		Mockito.when(source.getHarvestLocationId()).thenReturn(ORIGINAL_LOCATION_ID);

		dut.apply(input, source, null);
		assertEquals("", input.get(0).toString());
	}

	@Test
	public void testChangeLocationExpressionChanged() throws MiddlewareException {
		final Germplasm germplasm = mock(Germplasm.class);
		when(germplasmDataManager.getGermplasmByGID(anyInt())).thenReturn(germplasm);

		when(germplasm.getLocationId()).thenReturn(ORIGINAL_LOCATION_ID);
		final List<StringBuilder> input = constructExpressionInput();

		final AdvancingSource source = mock(AdvancingSource.class);
		Mockito.when(source.getHarvestLocationId()).thenReturn(ORIGINAL_LOCATION_ID + 1);

		Mockito.when(source.getLocationAbbreviation()).thenReturn(NEW_LOCATION_ABBR);

		dut.apply(input, source, null);
		assertEquals(NEW_LOCATION_ABBR, input.get(0).toString());
	}

	protected List<StringBuilder> constructExpressionInput() {
		final List<StringBuilder> list = new ArrayList<>();
		list.add(new StringBuilder(ChangeLocationExpression.KEY));

		return list;
	}

}
