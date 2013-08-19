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

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.utils.test.MockDataUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPedigreeDataManager{

    private static ManagerFactory factory;
    private static GermplasmDataManager germplasmManager;
    private static PedigreeDataManager pedigreeManager;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        germplasmManager = factory.getGermplasmDataManager();
        pedigreeManager = factory.getPedigreeDataManager();
    }


    @Test
    public void testGetGermplasmDescendants() throws Exception {
        Integer gid = Integer.valueOf(47888);
        List<Object[]> germplsmList = pedigreeManager.getDescendants(gid, 0, 20);

        System.out.println("testGetGermplasmDescendants(" + gid + ") RESULTS: ");
        for (Object[] object : germplsmList) {
            System.out.println("  progenitor number: " + object[0]);
            System.out.println("   " + object[1]);
        }
    }

    @Test
    public void testCountGermplasmDescendants() throws Exception {
        Integer gid = Integer.valueOf(47888);
        long count = pedigreeManager.countDescendants(gid);
        System.out.println("testCountGermplasmDescendants(" + gid + ") RESULTS: " + count);
    }

    @Test
    public void testGetProgenitorByGID() throws Exception {
        Integer gid = Integer.valueOf(779745);
        Integer pNo = Integer.valueOf(10);
        Germplasm germplasm = pedigreeManager.getParentByGIDAndProgenitorNumber(gid, pNo);
        System.out.println("testGetProgenitorByGID(" + gid + ", " + pNo + ") RESULTS: " + germplasm);
    }

    @Test
    public void testGeneratePedigreeTree() throws Exception {
        Integer gid = Integer.valueOf(306436);
        int level = 4;
        System.out.println("testGeneratePedigreeTree(" + gid + "," + level + ") RESULTS: ");
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
        System.out.println("generatePedigreeTree(" + gid + ", " + levels + ", " + includeDerivativeLines +")");
        System.out.println(renderNode(germplasmPedigreeTree.getRoot(),""));
    }
    
    private String renderNode(GermplasmPedigreeTreeNode node, String prefix){
        String outputString = "";
        if(node != null){
             outputString = "-" + prefix + " " + node.getGermplasm().getGid();
             for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
                 outputString = outputString + "\n"
                         + renderNode(parent, prefix + "-");
             }
        }

        return outputString;
     }


    @Test
    public void testGetManagementNeighbors() throws Exception {
        Integer gid = Integer.valueOf(2);
        int count = (int) pedigreeManager.countManagementNeighbors(gid);
        List<Germplasm> neighbors = pedigreeManager.getManagementNeighbors(gid, 0, count);
        Assert.assertNotNull(neighbors);
        Assert.assertTrue(!neighbors.isEmpty());
        System.out.println("testGetManagementNeighbors(" + gid + ") RESULTS: " + count);
        for (Germplasm g : neighbors) {
            String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
            System.out.println("  " + g.getGid() + " : " + name);
        }
    }

    @Test
    public void testGetGroupRelatives() throws Exception {
        Integer gid = Integer.valueOf(1);
        
        long count = pedigreeManager.countGroupRelatives(gid);
        List<Germplasm> neighbors = pedigreeManager.getGroupRelatives(gid, 0, (int) count);
        Assert.assertNotNull(neighbors);
        Assert.assertTrue(!neighbors.isEmpty());

        System.out.println("testGetGroupRelatives(" + gid + ") RESULTS: " + neighbors.size());
        for (Germplasm g : neighbors) {
            String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
            System.out.println("  " + g.getGid() + " : " + name);
        }
    }

    @Test
    public void testGetGenerationHistory() throws Exception {
        Integer gid = new Integer(50533);
        List<Germplasm> results = pedigreeManager.getGenerationHistory(gid);
        Assert.assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());

        System.out.println("testGetGenerationHistory(" + gid + ") RESULTS: " + results.size());
        for (Germplasm g : results) {
            String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
            System.out.println("  " + g.getGid() + " : " + name);
        }
    }
    
    @Test
    public void testGetDescendants() throws Exception {
        Integer id = Integer.valueOf(2);
        List<Object[]> results = pedigreeManager.getDescendants(id, 0, 100);
        assertNotNull(results);
        Assert.assertTrue(!results.isEmpty());
        System.out.println("testGetDescendants Results: ");
        for (Object[] result : results) {
            System.out.println("  " + result);
        }
        System.out.println("Number of record/s: " + results.size() );
    }
    

    @Test
    public void testGetDerivativeNeighborhood() throws Exception {
        Integer gid= new Integer(1);
        int stepsBack=3;
        int stepsForward=3;
        GermplasmPedigreeTree tree = pedigreeManager.getDerivativeNeighborhood(gid, stepsBack, stepsForward);
        System.out.println("testGetDerivativeNeighborhood(" + gid+", " +stepsBack+", "+ stepsForward + ") RESULTS: ");
        if (tree != null) {
            printNode(tree.getRoot(), 1);
        }
    }
    
    @Test
    public void testGetDerivativeNeighborhood2() throws Exception {
    
        System.out.println("TestDerivativeNeighborhood");
        MockDataUtil.mockNeighborhoodTestData(pedigreeManager, 'D');
        GermplasmPedigreeTree tree;
        
        System.out.println("TestCase #1: GID = TOP node (GID, Backward, Forward = -1, 0, 6)");
        tree = pedigreeManager.getDerivativeNeighborhood(-1, 0, 6);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19", MockDataUtil.printTree(tree, "*", ""));
        
        System.out.println("TestCase #2: GID = LEAF node (GID, Backward, Forward = -5, 2, 10)");
        tree = pedigreeManager.getDerivativeNeighborhood(-5, 2, 10);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19", MockDataUtil.printTree(tree, "*", ""));
        
        System.out.println("TestCase #3: GID = MID node AND Backward < Depth of LEAF (GID, Backward, Forward = -11, 1, 10)");
        tree = pedigreeManager.getDerivativeNeighborhood(-11, 1, 10);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-7*-11**-12**-13***-14", MockDataUtil.printTree(tree, "*", ""));
        
        System.out.println("TestCase #4: GID = MID node AND Backward = Dept of LEAF (GID, Backward, Forward = -11, 3, 10)");
        tree = pedigreeManager.getDerivativeNeighborhood(-11, 3, 10);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19", MockDataUtil.printTree(tree, "*", ""));

        System.out.println("TestCase #5: GID = MID node AND Backward > Dept of LEAF (GID, Backward, Forward = -11, 5, 10)");
        tree = pedigreeManager.getDerivativeNeighborhood(-11, 5, 10);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19", MockDataUtil.printTree(tree, "*", ""));

        System.out.println("TestCase #6: GID = MID node AND Forward < Tree Depth - MID depth (GID, Backward, Forward = -3, 1, 2)");
        tree = pedigreeManager.getDerivativeNeighborhood(-3, 1, 2);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11**-8***-15***-16", MockDataUtil.printTree(tree, "*", ""));
        
        System.out.println("TestCase #7: GID is MAN, but Ancestors and Descendants have non-MAN members (GID, Backward, Forward = -15, 2, 1)");
        tree = pedigreeManager.getDerivativeNeighborhood(-15, 2, 1);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-3*-6*-7**-11***-12***-13*-8**-15***-17***-18**-16***-19", MockDataUtil.printTree(tree, "*", ""));

        System.out.println("TestCase #8: Should stop at GEN even if Backward count is not exhausted (GID, Backward, Forward = -9, 4, 1)");
        tree = pedigreeManager.getDerivativeNeighborhood(-9, 4, 1);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-4*-9*-10", MockDataUtil.printTree(tree, "*", ""));
        
        //cleanup
        MockDataUtil.cleanupMockMaintenanceTestData(pedigreeManager);
    }


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
    


    @Test
    public void testGetMaintenanceNeighborhood() throws Exception {
    
        System.out.println("TestMaintenanceNeighborhood");
        MockDataUtil.mockNeighborhoodTestData(pedigreeManager, 'M');
        GermplasmPedigreeTree tree;
        
        System.out.println("TestCase #1: GID = TOP node (GID, Backward, Forward = -1, 0, 6)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-1, 0, 6);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));
        
        System.out.println("TestCase #2: GID = LEAF node (GID, Backward, Forward = -5, 2, 10)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-5, 2, 10);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));
        
        System.out.println("TestCase #3: GID = MID node AND Backward < Depth of LEAF (GID, Backward, Forward = -11, 1, 10)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-11, 1, 10);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-7*-11**-12**-13***-14", MockDataUtil.printTree(tree, "*", ""));
        
        System.out.println("TestCase #4: GID = MID node AND Backward = Dept of LEAF (GID, Backward, Forward = -11, 3, 10)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-11, 3, 10);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));

        System.out.println("TestCase #5: GID = MID node AND Backward > Dept of LEAF (GID, Backward, Forward = -11, 5, 10)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-11, 5, 10);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));

        System.out.println("TestCase #6: GID = MID node AND Forward < Tree Depth - MID depth (GID, Backward, Forward = -3, 1, 2)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-3, 1, 2);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-1*-2*-3**-6**-7***-11", MockDataUtil.printTree(tree, "*", ""));
        
        System.out.println("TestCase #7: GID is MAN, but Ancestors and Descendants have non-MAN members (GID, Backward, Forward = -15, 2, 1)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-15, 2, 1);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-1*-2*-3**-6**-7***-11", MockDataUtil.printTree(tree, "*", ""));

        System.out.println("TestCase #8: Should stop at GEN even if Backward count is not exhausted (GID, Backward, Forward = -9, 4, 1)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-9, 4, 1);
        MockDataUtil.printTree(tree);
        Assert.assertEquals("-4*-9*-10", MockDataUtil.printTree(tree, "*", ""));
        
        //cleanup
        MockDataUtil.cleanupMockMaintenanceTestData(pedigreeManager);
    }
    

        
    @Test
    public void testGetParentByGIDAndProgenitorNumber() throws Exception {
        Integer gid = Integer.valueOf(2);
        Integer progenitorNumber = Integer.valueOf(2);
        Germplasm result = pedigreeManager.getParentByGIDAndProgenitorNumber(gid, progenitorNumber);
        assertNotNull(result);
        System.out.println("testGetParentByGIDAndProgenitorNumber Results: ");
        System.out.println("  " + result);
    }
    

    @Test
    public void testCountDescendants() throws Exception {
        Integer gid = 10; //change gid value
        long count = pedigreeManager.countDescendants(gid);
        Assert.assertNotNull(count);
        System.out.println("testCountDescendants("+gid+") Results: " + count);
    }
    
    @Test
    public void testCountGroupRelatives() throws Exception {
        Integer gid = 1; //change gid value
        long count = pedigreeManager.countGroupRelatives(gid);
        Assert.assertNotNull(count);
        System.out.println("testCountGroupRelatives("+gid+") Results: " + count);
    }
    
    @Test
    public void testCountManagementNeighbors() throws Exception {
        Integer gid = 1; //change gid value
        long count = pedigreeManager.countManagementNeighbors(gid);
        Assert.assertNotNull(count);
        System.out.println("testCountManagementNeighbors("+gid+") Results: " + count);
    }
    
    

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }

}
