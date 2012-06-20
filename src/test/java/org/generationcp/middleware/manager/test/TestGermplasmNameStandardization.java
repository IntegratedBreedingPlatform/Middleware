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

package org.generationcp.middleware.manager.test;

import org.generationcp.middleware.manager.GermplasmDataManagerImpl;
import org.junit.Assert;
import org.junit.Test;

public class TestGermplasmNameStandardization{

    @Test
    public void testNameStandardization() throws Exception {
        String parameter = "I-1RT  /  P 001 A-23 / ";
        String expectedResult = "I-1RT/P 1 A-23/";

        String result = GermplasmDataManagerImpl.standardaizeName(parameter);
        System.out.println(result);
        Assert.assertTrue(result.equals(expectedResult));
    }
}
