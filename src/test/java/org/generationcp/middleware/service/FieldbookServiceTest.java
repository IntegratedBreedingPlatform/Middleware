package org.generationcp.middleware.service;


import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class FieldbookServiceTest {
	@Test
    public void testSetOrderVariableByRankIfWorkbookIsNull() throws MiddlewareQueryException{
    	FieldbookServiceImpl impl = Mockito.mock(FieldbookServiceImpl.class);
    	Assert.assertFalse("Should return false since the workbook is null", impl.setOrderVariableByRank(null));    	
    }	
}
