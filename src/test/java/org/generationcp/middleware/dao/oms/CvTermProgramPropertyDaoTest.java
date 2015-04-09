package org.generationcp.middleware.dao.oms;


import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.pojos.oms.CVTermProgramProperty;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class CvTermProgramPropertyDaoTest extends MiddlewareIntegrationTest {

    private static CvTermProgramPropertyDao dao;

    private static Integer variableId = 18000; //Grain_yield from test db
    private static String programId = UUID.randomUUID().toString();

    @BeforeClass
    public static void setUp() throws Exception {
        dao = new CvTermProgramPropertyDao();
        dao.setSession(sessionUtil.getCurrentSession());
    }

    @Test
    public void testSaveTermProgramProperty() throws Exception {
        CVTermProgramProperty property = dao.save(variableId, TermId.ALIAS.getId(), programId, "My Alias");
        dao.flush();
        List<CVTermProgramProperty> properties = dao.getByCvTermTypeProgram(variableId, TermId.ALIAS.getId(), programId);
        assertEquals(properties.size(), 1);
        assertEquals(property.getValue(), "My Alias");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        dao.setSession(null);
        dao = null;
    }

}
