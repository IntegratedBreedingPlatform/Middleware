package org.generationcp.middleware.dao.oms;


import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.pojos.oms.VariableProgramOverrides;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class VariableProgramOverridesDaoTest extends MiddlewareIntegrationTest {

    private static VariableProgramOverridesDao dao;

    private static Integer variableId = 18000; //Grain_yield from test db
    private static String programId = UUID.randomUUID().toString();

    @BeforeClass
    public static void setUp() throws Exception {
        dao = new VariableProgramOverridesDao();
        dao.setSession(sessionUtil.getCurrentSession());
    }

    @Test
    public void testSaveTermProgramProperty() throws Exception {
        VariableProgramOverrides overrides = dao.save(variableId, programId, "My Alias", "0", "100");
        dao.flush();
        VariableProgramOverrides dbOverrides = dao.getByVariableAndProgram(variableId, programId);
        assertEquals(overrides.getAlias(), dbOverrides.getAlias());
        assertEquals(overrides.getMinValue(), dbOverrides.getMinValue());
        assertEquals(overrides.getMaxValue(), dbOverrides.getMaxValue());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
    }
}
