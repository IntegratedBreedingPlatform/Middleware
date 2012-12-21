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

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.TraitDataManager;
import org.generationcp.middleware.pojos.Scale;
import org.generationcp.middleware.pojos.ScaleContinuous;
import org.generationcp.middleware.pojos.ScaleDiscrete;
import org.generationcp.middleware.pojos.Trait;
import org.generationcp.middleware.pojos.TraitMethod;
import org.junit.AfterClass;
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
        System.out.println("testGetAllScales() RESULTS:");
        for (Scale scale : scales) {
            System.out.println("  " + scale);
        }
    }

    @Test
    public void testCountAllScales() throws Exception {
        System.out.println("testCountAllScales(): " + manager.countAllScales());
    }

    @Test
    public void testGetScaleDiscreteDescription() throws Exception {
        Integer scaleId = Integer.valueOf(1);
        String value = "1";
        String desc = manager.getScaleDiscreteDescription(scaleId, value);
        System.out.println("testGetScaleDiscreteDescription(scaleId=" + scaleId + ", value=" + value + ") DESCRIPTION: " + desc);
    }

    @Test
    public void testGetDiscreteValuesOfScale() throws Exception {
        Integer scaleId = Integer.valueOf(1);
        List<ScaleDiscrete> results = manager.getDiscreteValuesOfScale(scaleId);

        System.out.println("testGetDiscreteValuesOfScale(scaleId=" + scaleId + ") RESULTS: ");
        for (ScaleDiscrete sd : results) {
            System.out.println("  " + sd);
        }
    }

    @Test
    public void testGetRangeOfContinuousScale() throws Exception {
        Integer scaleId = Integer.valueOf(68);
        ScaleContinuous range = manager.getRangeOfContinuousScale(scaleId);
        System.out.println("testGetRangeOfContinuousScale(scaleId=" + scaleId + ") RANGE: " + range);
    }

    @Test
    public void testGetTraitById() throws Exception {
        Integer traitId = Integer.valueOf(305);
        Trait trait = manager.getTraitById(traitId);
        System.out.println("testGetTraitById(traitId=" + traitId + ") TRAIT: " + trait);
    }

    @Test
    public void testGetAllTraits() throws Exception {
        List<Trait> traits = manager.getAllTraits(0, 5, Database.CENTRAL);

        System.out.println("testGetAllTraits() RESULTS: ");
        for (Trait trait : traits) {
            System.out.println("  " + trait);
        }
    }

    @Test
    public void testCountAllTraits() throws Exception {
        System.out.println("testCountAllTraits(): " + manager.countAllTraits());
    }

    @Test
    public void testGetTraitMethodById() throws Exception {
        Integer id = Integer.valueOf(1);
        TraitMethod method = manager.getTraitMethodById(id);
        System.out.println("testGetTraitMethodById(id=" + id + ")" + method);
    }

    @Test
    public void testGetAllTraitMethods() throws Exception {
        List<TraitMethod> traits = manager.getAllTraitMethods(0, 5, Database.CENTRAL);

        System.out.println("testGetAllTraitMethods() RESULTS: ");
        for (TraitMethod trait : traits) {
            System.out.println("  " + trait);
        }
    }

    @Test
    public void testCountAllTraitMethods() throws Exception {
        System.out.println("testCountAllTraitMethods(): " + manager.countAllTraitMethods());
    }

    @Test
    public void testGetTraitMethodsByTraitId() throws Exception {
        Integer traitId = Integer.valueOf(1215);
        List<TraitMethod> traits = manager.getTraitMethodsByTraitId(traitId);

        System.out.println("testGetTraitMethodsByTraitId(" + traitId + ") RESULTS: ");
        for (TraitMethod trait : traits) {
            System.out.println("  " + trait);
        }
    }

    @Test
    public void testGetScalesByTraitId() throws Exception {
        Integer traitId = Integer.valueOf(1215);
        List<Scale> scales = manager.getScalesByTraitId(traitId);

        System.out.println("testGetScalesByTraitId(" + traitId + ") RESULTS: ");
        for (Scale scale : scales) {
            System.out.println(scale);
        }
    }

    @Test
    public void testAddTraitMethod() throws MiddlewareQueryException {
        TraitMethod traitMethod = new TraitMethod();
        traitMethod.setId(-1);
        traitMethod.setAbbreviation("abrev");
        traitMethod.setDescription("desc");
        traitMethod.setName("Hoarding Method");
        
        // add the person
        manager.addTraitMethod(traitMethod);

        traitMethod = manager.getTraitMethodById(-1);

        System.out.println("testAddTraitMethod() ADDED: " + traitMethod);

        // cleanup: delete the trait method
        manager.deleteTraitMethod(traitMethod);
    }
   
    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
