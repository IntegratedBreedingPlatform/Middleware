/*******************************************************************************
 * Copyright (c) 2013, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/
package org.generationcp.middleware.operation.builder;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.ErrorCode;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkbookBuilderTest extends TestOutputFormatter {
    
    private static final Logger LOG = LoggerFactory.getLogger(WorkbookBuilderTest.class);
    
    private static WorkbookBuilder workbookBuilder;
    
    @BeforeClass
	public static void setUp() throws Exception {
    	workbookBuilder = new WorkbookBuilder(Mockito.mock(HibernateSessionProvider.class));
    }
 
    @Test
    public void testCheckingOfMeasurementDatasetWithError() {
    	try {
    		workbookBuilder.checkMeasurementDataset(null);
    	} catch (MiddlewareQueryException e) {
    		LOG.error(e.getMessage(), e);
    		Assert.assertEquals("Expected code ", ErrorCode.STUDY_FORMAT_INVALID.getCode(), e.getCode());
    	}
    }
    
    @Test
    public void testCheckingOfMeasurementDatasetWithoutError() {
    	boolean hasError = false;
    	try {
    		workbookBuilder.checkMeasurementDataset(1);
    	} catch (MiddlewareQueryException e) {
    		LOG.error(e.getMessage(), e);
    		Assert.fail("Expected no error but got one");
    		hasError = true;
    	}
    	Assert.assertFalse("Expected no error but got one", hasError);
    }
	
}
