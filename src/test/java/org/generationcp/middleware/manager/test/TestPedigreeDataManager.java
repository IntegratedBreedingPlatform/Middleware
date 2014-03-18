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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.util.Debug;
import org.generationcp.middleware.utils.test.MockDataUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestPedigreeDataManager{

    private static ManagerFactory factory;
    private static PedigreeDataManager pedigreeManager;


    private long startTime;

    @Rule
    public TestName name = new TestName();


    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        pedigreeManager = factory.getPedigreeDataManager();
    }


    @Before
    public void beforeEachTest() {
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
    }

    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        Debug.println(0, "#####" + name.getMethodName() + " End: Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime / 1000000000) + " s");
    }

    
    @Test
    public void testGetGermplasmDescendants() throws Exception {
        Integer gid = Integer.valueOf(47888);
        List<Object[]> germplsmList = pedigreeManager.getDescendants(gid, 0, 20);

        Debug.println(0, "testGetGermplasmDescendants(" + gid + "): ");
        for (Object[] object : germplsmList) {
            Debug.println(0, "  progenitor number: " + object[0]);
            Debug.println(0, "   " + object[1]);
        }
    }

    @Test
    public void testCountGermplasmDescendants() throws Exception {
        Integer gid = Integer.valueOf(47888);
        long count = pedigreeManager.countDescendants(gid);
        Debug.println(0, "testCountGermplasmDescendants(" + gid + ") RESULT: " + count);
    }

    @Test
    public void testGetProgenitorByGID() throws Exception {
        Integer gid = Integer.valueOf(779745);
        Integer pNo = Integer.valueOf(10);
        Germplasm germplasm = pedigreeManager.getParentByGIDAndProgenitorNumber(gid, pNo);
        Debug.println(0, "testGetProgenitorByGID(" + gid + ", " + pNo + ") RESULT: " + germplasm);
    }

    @Test
    public void testGeneratePedigreeTree() throws Exception {
        Integer gid = Integer.valueOf(306436);
        int level = 4;
        Debug.println(3, "GID = " + gid + ", level = " + level +  ":");
        GermplasmPedigreeTree tree = pedigreeManager.generatePedigreeTree(gid, level);
        if (tree != null) {
            printNode(tree.getRoot(), 1);
        }
    }
    

    @Test
    public void testGeneratePedigreeTree2() throws MiddlewareQueryException {
        // Using the sample on the story description (rice)
        int gid = 50533;
        int levels = 3;
        Boolean includeDerivativeLines = true;
        
        GermplasmPedigreeTree germplasmPedigreeTree = pedigreeManager.generatePedigreeTree(gid, levels, includeDerivativeLines);
        Debug.println(0, "generatePedigreeTree(" + gid + ", " + levels + ", " + includeDerivativeLines +")");
        Debug.println(0, renderNode(germplasmPedigreeTree.getRoot(),""));
    }
    
    private String renderNode(GermplasmPedigreeTreeNode node, String prefix){
        StringBuffer outputString = new StringBuffer();
        if(node != null){
             outputString.append("-").append(prefix).append(" ").append(node.getGermplasm().getGid());
             for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
                 outputString.append("\n").append(renderNode(parent, prefix + "-"));
             }
        }
        return outputString.toString();
     }


    @Test
    public void testGetManagementNeighbors() throws Exception {
        Integer gid = Integer.valueOf(2);
        int count = (int) pedigreeManager.countManagementNeighbors(gid);
        List<Germplasm> neighbors = pedigreeManager.getManagementNeighbors(gid, 0, count);
        assertNotNull(neighbors);
        assertFalse(neighbors.isEmpty());
        Debug.println(0, "testGetManagementNeighbors(" + gid + ") RESULT: " + count);
        for (Germplasm g : neighbors) {
            String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
            Debug.println(0, "  " + g.getGid() + " : " + name);
        }
    }

    @Test
    public void testGetGroupRelatives() throws Exception {
        Integer gid = Integer.valueOf(1);
        
        long count = pedigreeManager.countGroupRelatives(gid);
        List<Germplasm> neighbors = pedigreeManager.getGroupRelatives(gid, 0, (int) count);
        assertNotNull(neighbors);
        assertFalse(neighbors.isEmpty());

        Debug.println(0, "testGetGroupRelatives(" + gid + ") RESULT: " + neighbors.size());
        for (Germplasm g : neighbors) {
            String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
            Debug.println(0, "  " + g.getGid() + " : " + name);
        }
    }

    @Test
    public void testGetGenerationHistory() throws Exception {
        Integer gid = Integer.valueOf(50533);
        List<Germplasm> results = pedigreeManager.getGenerationHistory(gid);
        assertNotNull(results);
        assertFalse(results.isEmpty());

        Debug.println(0, "testGetGenerationHistory(" + gid + ") RESULT: " + results.size());
        for (Germplasm g : results) {
            String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
            Debug.println(0, "  " + g.getGid() + " : " + name);
        }
    }
    
    @Test
    public void testGetDescendants() throws Exception {
        Integer id = Integer.valueOf(2);
        List<Object[]> results = pedigreeManager.getDescendants(id, 0, 100);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        for (Object[] result : results) {
            Debug.println(0, "  " + result);
        }
        Debug.println(0, "Number of record/s: " + results.size() );
    }
    

    @Test
    public void testGetDerivativeNeighborhood() throws Exception {
        Integer gid = Integer.valueOf(1);
        int stepsBack = 3;
        int stepsForward = 3;
        GermplasmPedigreeTree tree = pedigreeManager.getDerivativeNeighborhood(gid, stepsBack, stepsForward);
        Debug.println(0, "testGetDerivativeNeighborhood(" + gid + ", " + stepsBack + ", " + stepsForward + "): ");
        if (tree != null) {
            printNode(tree.getRoot(), 1);
        }
    }
    
    @Test
    public void testGetDerivativeNeighborhood2() throws Exception {
    
        Debug.println(0, "TestDerivativeNeighborhood");
        MockDataUtil.mockNeighborhoodTestData(pedigreeManager, 'D');
        GermplasmPedigreeTree tree;
        
        Debug.println(0, "TestCase #1: GID = TOP node (GID, Backward, Forward = -1, 0, 6)");
        tree = pedigreeManager.getDerivativeNeighborhood(-1, 0, 6);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(0, "TestCase #2: GID = LEAF node (GID, Backward, Forward = -5, 2, 10)");
        tree = pedigreeManager.getDerivativeNeighborhood(-5, 2, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(0, "TestCase #3: GID = MID node AND Backward < Depth of LEAF (GID, Backward, Forward = -11, 1, 10)");
        tree = pedigreeManager.getDerivativeNeighborhood(-11, 1, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-7*-11**-12**-13***-14", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(0, "TestCase #4: GID = MID node AND Backward = Dept of LEAF (GID, Backward, Forward = -11, 3, 10)");
        tree = pedigreeManager.getDerivativeNeighborhood(-11, 3, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19", MockDataUtil.printTree(tree, "*", ""));

        Debug.println(0, "TestCase #5: GID = MID node AND Backward > Dept of LEAF (GID, Backward, Forward = -11, 5, 10)");
        tree = pedigreeManager.getDerivativeNeighborhood(-11, 5, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19", MockDataUtil.printTree(tree, "*", ""));

        Debug.println(0, "TestCase #6: GID = MID node AND Forward < Tree Depth - MID depth (GID, Backward, Forward = -3, 1, 2)");
        tree = pedigreeManager.getDerivativeNeighborhood(-3, 1, 2);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11**-8***-15***-16", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(0, "TestCase #7: GID is MAN, but Ancestors and Descendants have non-MAN members (GID, Backward, Forward = -15, 2, 1)");
        tree = pedigreeManager.getDerivativeNeighborhood(-15, 2, 1);
        MockDataUtil.printTree(tree);
        assertEquals("-3*-6*-7**-11***-12***-13*-8**-15***-17***-18**-16***-19", MockDataUtil.printTree(tree, "*", ""));

        Debug.println(0, "TestCase #8: Should stop at GEN even if Backward count is not exhausted (GID, Backward, Forward = -9, 4, 1)");
        tree = pedigreeManager.getDerivativeNeighborhood(-9, 4, 1);
        MockDataUtil.printTree(tree);
        assertEquals("-4*-9*-10", MockDataUtil.printTree(tree, "*", ""));
        
        //cleanup
        MockDataUtil.cleanupMockMaintenanceTestData(pedigreeManager);
    }


    private void printNode(GermplasmPedigreeTreeNode node, int level) {
        StringBuffer tabs = new StringBuffer();

        for (int ctr = 1; ctr < level; ctr++) {
            tabs.append("\t");
        }

        String name = node.getGermplasm().getPreferredName() != null ? node.getGermplasm().getPreferredName().getNval() : null;
        Debug.println(0, tabs.toString() + node.getGermplasm().getGid() + " : " + name);

        for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
            printNode(parent, level + 1);
        }
    }
    


    @Test
    public void testGetMaintenanceNeighborhood() throws Exception {
    
        MockDataUtil.mockNeighborhoodTestData(pedigreeManager, 'M');
        GermplasmPedigreeTree tree;
        
        Debug.println(0, "TestCase #1: GID = TOP node (GID, Backward, Forward = -1, 0, 6)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-1, 0, 6);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(0, "TestCase #2: GID = LEAF node (GID, Backward, Forward = -5, 2, 10)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-5, 2, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(0, "TestCase #3: GID = MID node AND Backward < Depth of LEAF (GID, Backward, Forward = -11, 1, 10)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-11, 1, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-7*-11**-12**-13***-14", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(0, "TestCase #4: GID = MID node AND Backward = Dept of LEAF (GID, Backward, Forward = -11, 3, 10)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-11, 3, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));

        Debug.println(0, "TestCase #5: GID = MID node AND Backward > Dept of LEAF (GID, Backward, Forward = -11, 5, 10)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-11, 5, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));

        Debug.println(0, "TestCase #6: GID = MID node AND Forward < Tree Depth - MID depth (GID, Backward, Forward = -3, 1, 2)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-3, 1, 2);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2*-3**-6**-7***-11", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(0, "TestCase #7: GID is MAN, but Ancestors and Descendants have non-MAN members (GID, Backward, Forward = -15, 2, 1)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-15, 2, 1);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2*-3**-6**-7***-11", MockDataUtil.printTree(tree, "*", ""));

        Debug.println(0, "TestCase #8: Should stop at GEN even if Backward count is not exhausted (GID, Backward, Forward = -9, 4, 1)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-9, 4, 1);
        MockDataUtil.printTree(tree);
        assertEquals("-4*-9*-10", MockDataUtil.printTree(tree, "*", ""));
        
        //cleanup
        MockDataUtil.cleanupMockMaintenanceTestData(pedigreeManager);
    }
    

        
    @Test
    public void testGetParentByGIDAndProgenitorNumber() throws Exception {
        Integer gid = Integer.valueOf(2);
        Integer progenitorNumber = Integer.valueOf(2);
        Germplasm result = pedigreeManager.getParentByGIDAndProgenitorNumber(gid, progenitorNumber);
        assertNotNull(result);
        Debug.println(0, " RESULT: " + result);
    }
    

    @Test
    public void testCountDescendants() throws Exception {
        Integer gid = 10; //change gid value
        long count = pedigreeManager.countDescendants(gid);
        assertNotNull(count);
        Debug.println(0, "testCountDescendants("+gid+") Result: " + count);
    }
    
    @Test
    public void testCountGroupRelatives() throws Exception {
        Integer gid = 1; //change gid value
        long count = pedigreeManager.countGroupRelatives(gid);
        assertNotNull(count);
        Debug.println(0, "testCountGroupRelatives("+gid+") Result: " + count);
    }
    
    @Test
    public void testCountManagementNeighbors() throws Exception {
        Integer gid = 1; //change gid value
        long count = pedigreeManager.countManagementNeighbors(gid);
        assertNotNull(count);
        Debug.println(0, "testCountManagementNeighbors("+gid+") Result: " + count);
    }
    
    @Test
    public void testGetPedigreeLine() throws Exception {
        Integer gid = Integer.valueOf(306436);
        int locationID = 187;
        List<Germplasm> results = pedigreeManager.getPedigreeLine(gid, locationID);

        Debug.println(0, "testGetPedigreeLine(" + gid + ", " + locationID + ") RESULT: " + results.size());
        for (Germplasm g : results) {
        	Debug.println(0, "  " + g);
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
