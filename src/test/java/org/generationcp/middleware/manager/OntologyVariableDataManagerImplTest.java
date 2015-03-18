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
import org.generationcp.middleware.domain.oms.OntologyVariableSummary;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.utils.test.Debug;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import org.junit.Assert;

public class OntologyVariableDataManagerImplTest extends DataManagerIntegrationTest {

	private static OntologyVariableDataManager manager;

	@Before
	public void setUp() throws Exception {
		manager = DataManagerIntegrationTest.managerFactory.getOntologyVariableDataManager();
    }

    @Test
    public void testGetAllScales() throws Exception {
        List<OntologyVariableSummary> variables = manager.getAllVariables();
        Assert.assertTrue(variables.size() > 0);
        Debug.println(MiddlewareIntegrationTest.INDENT, "From Total Scales:  " + variables.size());
    }

    @After
    public void tearDown() throws Exception {

    }
}
