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
import org.generationcp.middleware.manager.ontology.api.OntologyScaleDataManager;
import org.junit.After;
import org.junit.Before;

public class OntologyScaleDataManagerImplTest extends DataManagerIntegrationTest {

	private static OntologyScaleDataManager manager;

	@Before
	public void setUp() throws Exception {
		manager = DataManagerIntegrationTest.managerFactory.getOntologyScaleDataManager();
    }

    @After
    public void tearDown() throws Exception {
    }
}
