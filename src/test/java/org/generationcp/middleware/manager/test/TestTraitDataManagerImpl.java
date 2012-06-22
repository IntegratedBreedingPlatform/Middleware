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

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.Scale;
import org.generationcp.middleware.pojos.ScaleContinuous;
import org.generationcp.middleware.pojos.ScaleDiscrete;
import org.generationcp.middleware.pojos.Trait;
import org.generationcp.middleware.pojos.TraitMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestTraitDataManagerImpl{

    private static ManagerFactory factory;
    private static TraitDataManager manager;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getTraitDataManager();
    }

    @Test
    public void testGetScaleById() throws Exception {
        Scale scale = manager.getScaleByID(new Integer(1));
        System.out.println(scale);
    }

    @Test
    public void testGetAllScales() throws Exception {
        List<Scale> scales = manager.getAllScales(0, 5, Database.CENTRAL);

        System.out.println("RESULTS:");
        for (Scale scale : scales) {
            System.out.println(scale);
        }
    }

    @Test
    public void testCountAllScales() throws Exception {
        System.out.println(manager.countAllScales());
    }

    @Test
    public void testGetScaleDiscreteDescription() throws Exception {
        String desc = manager.getScaleDiscreteDescription(new Integer(1), "1");
        System.out.println(desc);
    }

    @Test
    public void testGetDiscreteValuesOfScale() throws Exception {
        List<ScaleDiscrete> results = manager.getDiscreteValuesOfScale(new Integer(1));

        System.out.println("RESULTS:");
        for (ScaleDiscrete sd : results) {
            System.out.println(sd);
        }
    }

    @Test
    public void testGetRangeOfContinuousScale() throws Exception {
        ScaleContinuous range = manager.getRangeOfContinuousScale(new Integer(68));
        System.out.println(range);
    }

    @Test
    public void testGetTraitById() throws Exception {
        Trait trait = manager.getTraitById(new Integer(305));
        System.out.println(trait);
    }

    @Test
    public void testGetAllTraits() throws Exception {
        List<Trait> traits = manager.getAllTraits(0, 5, Database.CENTRAL);

        System.out.println("RESULTS:");
        for (Trait trait : traits) {
            System.out.println(trait);
        }
    }

    @Test
    public void testCountAllTraits() throws Exception {
        System.out.println(manager.countAllTraits());
    }

    @Test
    public void testGetTraitMethodById() throws Exception {
        TraitMethod method = manager.getTraitMethodById(new Integer(1));
        System.out.println(method);
    }

    @Test
    public void testGetAllTraitMethods() throws Exception {
        List<TraitMethod> traits = manager.getAllTraitMethods(0, 5, Database.CENTRAL);

        System.out.println("RESULTS:");
        for (TraitMethod trait : traits) {
            System.out.println(trait);
        }
    }

    @Test
    public void testCountAllTraitMethods() throws Exception {
        System.out.println(manager.countAllTraitMethods());
    }

    @Test
    public void testGetTraitMethodsByTraitId() throws Exception {
        List<TraitMethod> traits = manager.getTraitMethodsByTraitId(new Integer(1215));

        System.out.println("RESULTS:");
        for (TraitMethod trait : traits) {
            System.out.println(trait);
        }
    }

    @Test
    public void testGetScalesByTraitId() throws Exception {
        List<Scale> scales = manager.getScalesByTraitId(new Integer(1215));

        System.out.println("RESULTS:");
        for (Scale scale : scales) {
            System.out.println(scale);
        }
    }

    @Test
    public void testAddTraitMethod() throws QueryException {
        TraitMethod traitMethod = new TraitMethod();
        traitMethod.setId(-1);
        traitMethod.setAbbreviation("abrev");
        traitMethod.setDescription("desc");
        traitMethod.setName("Hoarding Method");
        traitMethod.setTraitId(-1);

        
        // add the person
        manager.addTraitMethod(traitMethod);
        
        traitMethod = manager.getTraitMethodById(-1);
        
        // delete the person
        //manager.deleteTraitMethod(traitMethod);
    }    
    
    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
