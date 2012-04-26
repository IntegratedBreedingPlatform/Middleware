package org.generationcp.middleware.manager.test;

import org.generationcp.middleware.manager.GermplasmDataManagerImpl;
import org.junit.Assert;
import org.junit.Test;


public class TestGermplasmNameStandardization
{

	@Test
	public void testNameStandardization() throws Exception
	{
		String parameter = "I-1RT  /  P 001 A-23 / ";
		String expectedResult = "I-1RT/P 1 A-23/";
		
		String result = GermplasmDataManagerImpl.standardaizeName(parameter);
		System.out.println(result);
		Assert.assertTrue(result.equals(expectedResult));
	}
}
