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

import org.generationcp.middleware.domain.common.IdName;
import org.generationcp.middleware.domain.oms.DataType;
import org.junit.Assert;
import org.junit.Test;


public class TestEnumerations {
    
    @Test
    public void testDataTypeEnumeration() throws Exception {
        DataType dataType = DataType.CATEGORICAL_VARIABLE;
        IdName idName = dataType.toIdName();
        Assert.assertEquals(dataType.getName(), idName.getName());
        Assert.assertEquals(dataType.getId(), idName.getId());
    }

}
