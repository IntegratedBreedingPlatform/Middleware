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
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.manager.ontology.api.OntologyConstantDataManager;
import org.junit.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class OntologyConstantDataManagerImplTest extends DataManagerIntegrationTest {

	private static OntologyConstantDataManager manager;

	@BeforeClass
	public static void setUp() throws Exception {
		manager = DataManagerIntegrationTest.managerFactory.getOntologyConstantDataManager();
	}

    @Test
    public void testGetAllTraitClass() throws Exception {

        List<Term> classes = manager.getAllTraitClass();
        Assert.assertTrue(classes.size() > 0);
        for(Term c : classes){
            c.print(MiddlewareIntegrationTest.INDENT);
        }
    }

    @Test
    public void testGetAllScaleDataTypes() throws Exception {

        List<Term> dataTypes = manager.getDataTypes();
        Assert.assertTrue(dataTypes.size() > 0);
        for(Term d : dataTypes){
            d.print(MiddlewareIntegrationTest.INDENT);
        }
    }
}
