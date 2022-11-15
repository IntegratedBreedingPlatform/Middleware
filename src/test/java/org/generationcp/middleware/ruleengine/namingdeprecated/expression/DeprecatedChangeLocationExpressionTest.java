package org.generationcp.middleware.ruleengine.namingdeprecated.expression;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.ruleengine.pojo.DeprecatedAdvancingSource;
import org.generationcp.middleware.ruleengine.pojo.ImportedGermplasm;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
public class DeprecatedChangeLocationExpressionTest {

    public static final int ORIGINAL_LOCATION_ID = 2;
    public static final int TEST_GID = 3;
    public static final String NEW_LOCATION_ABBR = "ABC";

    @Mock
    private GermplasmDataManager germplasmDataManager;

    @InjectMocks
    private DeprecatedChangeLocationExpression dut;

    @Test
    public void testChangeLocationExpressionNoChange() throws MiddlewareException {
        Germplasm germplasm = mock(Germplasm.class);
        when(germplasmDataManager.getGermplasmByGID(anyInt())).thenReturn(germplasm);

        when(germplasm.getLocationId()).thenReturn(ORIGINAL_LOCATION_ID);
        List<StringBuilder> input = constructExpressionInput();

        DeprecatedAdvancingSource source = new DeprecatedAdvancingSource();
        source.setHarvestLocationId(ORIGINAL_LOCATION_ID);
        ImportedGermplasm importedGermplasm = mock(ImportedGermplasm.class);
        when(importedGermplasm.getGid()).thenReturn(Integer.toString(TEST_GID));
        source.setGermplasm(importedGermplasm);

        dut.apply(input, source, null);
        assertEquals("", input.get(0).toString());
    }

    @Test
    public void testChangeLocationExpressionChanged() throws MiddlewareException {
        Germplasm germplasm = mock(Germplasm.class);
        when(germplasmDataManager.getGermplasmByGID(anyInt())).thenReturn(germplasm);

        when(germplasm.getLocationId()).thenReturn(ORIGINAL_LOCATION_ID);
        List<StringBuilder> input = constructExpressionInput();

        DeprecatedAdvancingSource source = new DeprecatedAdvancingSource();
        source.setHarvestLocationId(ORIGINAL_LOCATION_ID + 1);
        ImportedGermplasm importedGermplasm = mock(ImportedGermplasm.class);
        when(importedGermplasm.getGid()).thenReturn(Integer.toString(TEST_GID));
        source.setGermplasm(importedGermplasm);
        source.setLocationAbbreviation(NEW_LOCATION_ABBR);

        dut.apply(input, source, null);
        assertEquals(NEW_LOCATION_ABBR, input.get(0).toString());
    }

    protected List<StringBuilder> constructExpressionInput() {
        List<StringBuilder> list = new ArrayList<>();
        list.add(new StringBuilder(DeprecatedChangeLocationExpression.KEY));

        return list;
    }

}
