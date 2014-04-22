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
package org.generationcp.middleware.utils.test;

import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;


/**
 * @author Joyce Avestro
 *
 */
public class TestOutputFormatter{
    
    protected long startTime;
    protected static final int INDENT = 3;

    @Rule
    public TestName name = new TestName();

    @Before
    public void beforeEachTest() {
        Debug.println("#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
    }

    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        Debug.println("#####" + name.getMethodName() + " End: Elapsed Time = " 
                + elapsedTime + " ns = " + ((double) elapsedTime / 1000000000) + " s");
    }

}
