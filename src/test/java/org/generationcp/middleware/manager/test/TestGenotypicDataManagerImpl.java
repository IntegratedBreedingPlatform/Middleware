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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.generationcp.middleware.pojos.CharacterDataElement;
import org.generationcp.middleware.pojos.CharacterLevelElement;
import org.generationcp.middleware.pojos.Factor;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.NumericDataElement;
import org.generationcp.middleware.pojos.NumericLevelElement;
import org.generationcp.middleware.pojos.NumericRange;
import org.generationcp.middleware.pojos.Representation;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.pojos.StudyEffect;
import org.generationcp.middleware.pojos.TraitCombinationFilter;
import org.generationcp.middleware.pojos.Variate;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGenotypicDataManagerImpl{

    private static ManagerFactory factory;
    private static GenotypicDataManager manager;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local"); 
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getGenotypicDataManager();
    }

    @Test
    public void testGetNameIdsByGermplasmIds() throws Exception {
        List<Integer> germplasmIds = new ArrayList<Integer>();
        germplasmIds.add(Integer.valueOf(-3787));
        germplasmIds.add(Integer.valueOf(-6785));
        germplasmIds.add(Integer.valueOf(-4070));
        
        List<Integer> results = manager.getNameIdsByGermplasmIds(germplasmIds);
        
        System.out.println("RESULTS (testGetNameIdsByGermplasmIds):");
        if (results == null || results.isEmpty()) {
            System.out.println(" No records found.");
        } else {
            for (Integer nId : results){
                System.out.println(nId);
            }
        }
    }

    @Test
    public void testGetNamesByNameIds() throws Exception {
        List<Integer> nameIds = new ArrayList<Integer>();
        nameIds.add(Integer.valueOf(-1));
        nameIds.add(Integer.valueOf(-2));
        nameIds.add(Integer.valueOf(-3));
        
        List<Name> results = manager.getNamesByNameIds(nameIds);
        System.out.println("RESULTS (testGetNamesByNameIds):");
        if (results == null || results.isEmpty()) {
            System.out.println(" No records found.");
        } else {
            for (Name name : results){
                System.out.println(name);
            }
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
