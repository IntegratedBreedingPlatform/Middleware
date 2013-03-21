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

import java.util.ArrayList;
import java.util.List;


/**
 * Utility methods for the GenotypicDataManager class
 * 
 * @author Joyce Avestro
 *
 */
public class GenotypicDataManagerUtil{

    public static List<Integer> getPositiveIds(List<Integer> ids){
        List<Integer> positiveIds = new ArrayList<Integer>();
        for (Integer id : ids) {
            if (id >= 0) {
                positiveIds.add(id);
            }
        }
        return positiveIds;
    }
    

    public static List<Integer> getNegativeIds(List<Integer> ids){
        List<Integer> negativeIds = new ArrayList<Integer>();
        for (Integer id : ids) {
            if (id < 0) {
                negativeIds.add(id);
            }
        }
        return negativeIds;
    }
    
    
    
}
