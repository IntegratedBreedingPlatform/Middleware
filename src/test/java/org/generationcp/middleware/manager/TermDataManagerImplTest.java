/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/

package org.generationcp.middleware.manager;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.MiddlewareIntegrationTest;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.manager.ontology.api.TermDataManager;
import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class TermDataManagerImplTest extends DataManagerIntegrationTest {

	private static TermDataManager manager;

	@BeforeClass
	public static void setUp() throws Exception {
		manager = DataManagerIntegrationTest.managerFactory.getTermDataManager();
	}

    @Test
    public void testGetAllTraitClass() throws Exception {

        List<Term> classes = manager.getTermByCvId(CvId.TRAIT_CLASS.getId());
        Assert.assertTrue(classes.size() > 0);
        for(Term c : classes){
            c.print(MiddlewareIntegrationTest.INDENT);
        }
    }

    @Test
    public void testGetTermByNameAndCvId() throws Exception {
        Term term = manager.getTermByNameAndCvId("Project", CvId.PROPERTIES.getId());
        Assert.assertNotNull(term);
    }
}
