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

import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.Country;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGermplasmPedigreeQueries{

    private static ManagerFactory factory;
    private static GermplasmDataManager manager;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getGermplasmDataManager();
    }

    @Test
    public void testGetMethodById() throws Exception {
        Integer id = Integer.valueOf(1);
        Method method = manager.getMethodByID(id);
        System.out.println(method);
        System.out.println("testGetMethodById(" + id + ") RESULTS: " + method);
    }

    @Test
    public void testGetAllMethods() throws Exception {
        List<Method> methods = manager.getAllMethods();

        List<Method> sublist = methods.subList(0, 4);
        System.out.println("testGetAllMethods() RESULTS: " + sublist);
        for (Method method : sublist) {
            System.out.println("  " + method);
        }
    }

    @Test
    public void testGetUserDefinedFieldById() throws Exception {
        Integer id = Integer.valueOf(1);
        UserDefinedField udfld = manager.getUserDefinedFieldByID(id);
        System.out.println("testGetUserDefinedFieldById(" + id + ") RESULTS: " + udfld);
    }

    @Test
    public void testGetCountryById() throws Exception {
        Integer id = Integer.valueOf(229);
        Country country = manager.getCountryById(id);
        System.out.println("testGetCountryById(" + id + ") RESULTS: " + country);
    }

    @Test
    public void testGetLocationById() throws Exception {
        Integer id = Integer.valueOf(1);
        Location location = manager.getLocationByID(id);
        System.out.println("testGetLocationById(" + id + ") RESULTS: " + location);
    }

    @Test
    public void testGetBibliographicalReferenceById() throws Exception {
        Integer id = Integer.valueOf(1);
        Bibref bibref = manager.getBibliographicReferenceByID(id);
        System.out.println("testGetBibliographicalReferenceById(" + id + ") RESULTS: " + bibref);
    }

    @Test
    public void testGetGermplasmDescendants() throws Exception {
        Integer gid = Integer.valueOf(47888);
        List<Object[]> germplsmList = manager.getDescendants(gid, 0, 20);

        System.out.println("testGetGermplasmDescendants(" + gid + ") RESULTS: ");
        for (Object[] object : germplsmList) {
            System.out.println("  progenitor number: " + object[0]);
            System.out.println("   " + object[1]);
        }
    }

    @Test
    public void testCountGermplasmDescendants() throws Exception {
        Integer gid = Integer.valueOf(47888);
        long count = manager.countDescendants(gid);
        System.out.println("testCountGermplasmDescendants(" + gid + ") RESULTS: " + count);
    }

    @Test
    public void testGetProgenitorByGID() throws Exception {
        Integer gid = Integer.valueOf(779745);
        Integer pNo = Integer.valueOf(10);
        Germplasm germplasm = manager.getParentByGIDAndProgenitorNumber(gid, pNo);
        System.out.println("testGetProgenitorByGID(" + gid + ", " + pNo + ") RESULTS: " + germplasm);
    }

    @Test
    public void testGeneratePedigreeTree() throws Exception {
        Integer gid = Integer.valueOf(306436);
        int level = 4;
        System.out.println("testGeneratePedigreeTree(" + gid + "," + level + ") RESULTS: ");
        GermplasmPedigreeTree tree = manager.generatePedigreeTree(gid, level);
        if (tree != null) {
            printNode(tree.getRoot(), 1);
        }
    }

    @Test
    public void testGetManagementNeighbors() throws Exception {
        Integer gid = Integer.valueOf(625);
        List<Germplasm> neighbors = manager.getManagementNeighbors(gid);

        System.out.println("testGetManagementNeighbors(" + gid + ") RESULTS:");
        for (Germplasm g : neighbors) {
            String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
            System.out.println("  " + g.getGid() + " : " + name);
        }
    }

    @Test
    public void testGetGroupRelatives() throws Exception {
        Integer gid = Integer.valueOf(1);
        List<Germplasm> neighbors = manager.getGroupRelatives(gid);

        System.out.println("testGetGroupRelatives(" + gid + ") RESULTS:");
        for (Germplasm g : neighbors) {
            String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
            System.out.println("  " + g.getGid() + " : " + name);
        }
    }

    @Test
    public void testGetGenerationHistory() throws Exception {
        Integer gid = new Integer(50533);
        List<Germplasm> results = manager.getGenerationHistory(gid);

        System.out.println("testGetGenerationHistory(" + gid + ") RESULTS:");
        for (Germplasm g : results) {
            String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
            System.out.println("  " + g.getGid() + " : " + name);
        }
    }

    @Test
    public void testGetDerivativeNeighborhood() throws Exception {
        Integer gid= new Integer(1);
        int stepsBack=3;
        int stepsForward=3;
        GermplasmPedigreeTree tree = manager.getDerivativeNeighborhood(gid, stepsBack, stepsForward);
        System.out.println("testGetDerivativeNeighborhood(" + gid+", " +stepsBack+", "+ stepsForward + ") RESULTS: ");
        if (tree != null) {
            printNode(tree.getRoot(), 1);
        }
    }

    /**
     * @Test public void testGetGermplasmByExample() throws Exception {
     *       Germplasm sample = new Germplasm(); Location location = new
     *       Location(); location.setLname("International");
     *       sample.setLocation(location);
     * 
     *       List<Germplasm> results = manager.getGermplasmByExample(sample, 0,
     *       5); Assert.assertTrue(results != null);
     *       Assert.assertTrue(!results.isEmpty()); for(Germplasm result :
     *       results) { System.out.println(result); } }
     * @Test public void testCountGermplasmByExample() throws Exception {
     *       Germplasm sample = new Germplasm(); Location location = new
     *       Location(); location.setLname("International");
     *       sample.setLocation(location);
     * 
     *       System.out.println(manager.countGermplasmByExample(sample)); }
     * @Test public void testGetGermplasmByExampleWithAttribute() throws
     *       Exception { Germplasm sample = new Germplasm(); Attribute attribute
     *       = new Attribute(1); attribute.setAval("FOR DISTRIBUTION");
     *       sample.getAttributes().add(attribute);
     * 
     *       List<Germplasm> results = manager.getGermplasmByExample(sample, 0,
     *       5); Assert.assertTrue(results != null);
     *       Assert.assertTrue(!results.isEmpty()); for(Germplasm result :
     *       results) { System.out.println(result); } }
     * @Test public void testCountGermplasmByExampleWithAttribute() throws
     *       Exception { Germplasm sample = new Germplasm(); Attribute attribute
     *       = new Attribute(1); attribute.setAval("FOR DISTRIBUTION");
     *       sample.getAttributes().add(attribute);
     * 
     *       System.out.println(manager.countGermplasmByExample(sample)); }
     **/

    private void printNode(GermplasmPedigreeTreeNode node, int level) {
        StringBuffer tabs = new StringBuffer();

        for (int ctr = 1; ctr < level; ctr++) {
            tabs.append("\t");
        }

        String name = node.getGermplasm().getPreferredName() != null ? node.getGermplasm().getPreferredName().getNval() : null;
        System.out.println(tabs.toString() + node.getGermplasm().getGid() + " : " + name);

        for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
            printNode(parent, level + 1);
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
