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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.utils.test.Debug;
import org.generationcp.middleware.utils.test.MockDataUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PedigreeDataManagerTest extends DataManagerIntegrationTest {

    private static PedigreeDataManager pedigreeManager;

    @BeforeClass
    public static void setUp() throws Exception {
        pedigreeManager = managerFactory.getPedigreeDataManager();
    }
    
    @Test
    public void testGetGermplasmDescendants() throws Exception {
        Integer gid = Integer.valueOf(1);
        List<Object[]> germplsmList = pedigreeManager.getDescendants(gid, 0, 20);

        Debug.println(INDENT, "testGetGermplasmDescendants(" + gid + "): ");
        for (Object[] object : germplsmList) {
            Debug.println(INDENT, "  progenitor number: " + object[0]);
            Debug.println(INDENT, "   " + object[1]);
        }
    }

    @Test
    public void testCountGermplasmDescendants() throws Exception {
        Integer gid = Integer.valueOf(1);
        long count = pedigreeManager.countDescendants(gid);
        Debug.println(INDENT, "testCountGermplasmDescendants(" + gid + "):" + count);
    }

    @Test
    public void testGetProgenitorByGID() throws Exception {
        Integer gid = Integer.valueOf(779745);
        Integer pNo = Integer.valueOf(10);
        Germplasm germplasm = pedigreeManager.getParentByGIDAndProgenitorNumber(gid, pNo);
        Debug.println(INDENT, "testGetProgenitorByGID(" + gid + ", " + pNo + "):" + germplasm);
    }

    @Test
    public void testGeneratePedigreeTree() throws Exception {
        Integer gid = Integer.valueOf(306436);
        int level = 4;
        Debug.println(INDENT, "GID = " + gid + ", level = " + level +  ":");
        GermplasmPedigreeTree tree = pedigreeManager.generatePedigreeTree(gid, level);
        if (tree != null) {
            printNode(tree.getRoot(), 1);
        }
    }
    
    @Test
    public void testGetPedigreeLevelCount() throws Exception {
    	Integer gid = 1;
    	boolean includeDerivativeLine = false;
    	Integer pedigreeLevelCount = pedigreeManager.countPedigreeLevel(gid, includeDerivativeLine);
    	Debug.println(Integer.toString(pedigreeLevelCount));
    	Assert.assertNotNull("It should not be null",pedigreeLevelCount);
    	Assert.assertEquals("It should be equal to 1",new Integer(1),pedigreeLevelCount);
    	
    }

    @Test
    public void testGetPedigreeLevelCount_IncludeDerivative() throws Exception {
    	Integer gid = 1;
    	boolean includeDerivativeLine = true;
    	Integer pedigreeLevelCount = pedigreeManager.countPedigreeLevel(gid, includeDerivativeLine);
    	Debug.println(Integer.toString(pedigreeLevelCount));
    	Assert.assertNotNull("It should not be null",pedigreeLevelCount);
    	Assert.assertEquals("It should be equal to 1",new Integer(1),pedigreeLevelCount);
    	
    }

    @Test
    public void testGeneratePedigreeTree2() throws MiddlewareQueryException {
        int gid = 1;
        int levels = 3;
        Boolean includeDerivativeLines = true;
        
        GermplasmPedigreeTree germplasmPedigreeTree = 
                pedigreeManager.generatePedigreeTree(gid, levels, includeDerivativeLines);
        Debug.println(INDENT, "generatePedigreeTree(" + gid + ", " + levels + ", " + includeDerivativeLines +")");
        Debug.println(renderNode(germplasmPedigreeTree.getRoot(),""));
    }
    
    private String renderNode(GermplasmPedigreeTreeNode node, String prefix){
        StringBuffer outputString = new StringBuffer();
        if(node != null){
             outputString.append("   -").append(prefix).append(" ").append(node.getGermplasm().getGid());
             for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
                 outputString.append("\n").append(renderNode(parent, prefix + "-"));
             }
        }
        return outputString.toString();
     }


    @Test
    public void testGetManagementNeighbors() throws Exception {
        Integer gid = Integer.valueOf(1);
        int count = (int) pedigreeManager.countManagementNeighbors(gid);
        List<Germplasm> neighbors = pedigreeManager.getManagementNeighbors(gid, 0, count);
        assertNotNull(neighbors);
        assertFalse(neighbors.isEmpty());
        Debug.println(INDENT, "testGetManagementNeighbors(" + gid + "):" + count);
        for (Germplasm g : neighbors) {
            String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
            Debug.println(INDENT,  g.getGid() + " : " + name);
        }
    }

    @Test
    public void testGetGroupRelatives() throws Exception {
        Integer gid = Integer.valueOf(1);
        
        long count = pedigreeManager.countGroupRelatives(gid);
        List<Germplasm> neighbors = pedigreeManager.getGroupRelatives(gid, 0, (int) count);
        assertNotNull(neighbors);

        Debug.println(INDENT, "testGetGroupRelatives(" + gid + "):" + neighbors.size());
        for (Germplasm g : neighbors) {
            String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
            Debug.println(INDENT,  g.getGid() + " : " + name);
        }
    }

    @Test
    public void testGetGenerationHistory() throws Exception {
        Integer gid = Integer.valueOf(50533);
        List<Germplasm> results = pedigreeManager.getGenerationHistory(gid);
        assertNotNull(results);
        assertFalse(results.isEmpty());

        Debug.println(INDENT, "testGetGenerationHistory(" + gid + "):" + results.size());
        for (Germplasm g : results) {
            String name = g.getPreferredName() != null ? g.getPreferredName().getNval() : null;
            Debug.println(INDENT,  g.getGid() + " : " + name);
        }
    }
    
    @Test
    public void testGetDescendants() throws Exception {
        Integer id = Integer.valueOf(2);
        List<Object[]> results = pedigreeManager.getDescendants(id, 0, 100);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        for (Object[] result : results){
            Debug.println(INDENT, result[0]);
            Debug.println(INDENT, result[1]);
        }
    }

    @Test
    public void testGetDerivativeNeighborhood() throws Exception {
        Integer gid = Integer.valueOf(1);
        int stepsBack = 3;
        int stepsForward = 3;
        GermplasmPedigreeTree tree = pedigreeManager.getDerivativeNeighborhood(gid, stepsBack, stepsForward);
        Debug.println(INDENT, "testGetDerivativeNeighborhood(" + gid + ", " + stepsBack + ", " + stepsForward + "): ");
        if (tree != null) {
            printNode(tree.getRoot(), 1);
        }
    }
    
    @Test
    public void testGetDerivativeNeighborhood2() throws Exception {
    
        MockDataUtil.mockNeighborhoodTestData(pedigreeManager, 'D');
        GermplasmPedigreeTree tree;
        
        Debug.println(INDENT, "TestCase #1: GID = TOP node (GID, Backward, Forward = -1, 0, 6)");
        tree = pedigreeManager.getDerivativeNeighborhood(-1, 0, 6);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(INDENT, "TestCase #2: GID = LEAF node (GID, Backward, Forward = -5, 2, 10)");
        tree = pedigreeManager.getDerivativeNeighborhood(-5, 2, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(INDENT, "TestCase #3: GID = MID node AND Backward < Depth of LEAF (GID, Backward, Forward = -11, 1, 10)");
        tree = pedigreeManager.getDerivativeNeighborhood(-11, 1, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-7*-11**-12**-13***-14", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(INDENT, "TestCase #4: GID = MID node AND Backward = Dept of LEAF (GID, Backward, Forward = -11, 3, 10)");
        tree = pedigreeManager.getDerivativeNeighborhood(-11, 3, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19", MockDataUtil.printTree(tree, "*", ""));

        Debug.println(INDENT, "TestCase #5: GID = MID node AND Backward > Dept of LEAF (GID, Backward, Forward = -11, 5, 10)");
        tree = pedigreeManager.getDerivativeNeighborhood(-11, 5, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11****-12****-13*****-14**-8***-15****-17****-18*****-20***-16****-19", MockDataUtil.printTree(tree, "*", ""));

        Debug.println(INDENT, "TestCase #6: GID = MID node AND Forward < Tree Depth - MID depth (GID, Backward, Forward = -3, 1, 2)");
        tree = pedigreeManager.getDerivativeNeighborhood(-3, 1, 2);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2**-4***-9***-10**-5*-3**-6**-7***-11**-8***-15***-16", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(INDENT, "TestCase #7: GID is MAN, but Ancestors and Descendants have non-MAN members (GID, Backward, Forward = -15, 2, 1)");
        tree = pedigreeManager.getDerivativeNeighborhood(-15, 2, 1);
        MockDataUtil.printTree(tree);
        assertEquals("-3*-6*-7**-11***-12***-13*-8**-15***-17***-18**-16***-19", MockDataUtil.printTree(tree, "*", ""));

        Debug.println(INDENT, "TestCase #8: Should stop at GEN even if Backward count is not exhausted (GID, Backward, Forward = -9, 4, 1)");
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

        String name = node.getGermplasm().getPreferredName() != null ? 
                            node.getGermplasm().getPreferredName().getNval() : null;
        Debug.println(INDENT, tabs.toString() + node.getGermplasm().getGid() + " : " + name);

        for (GermplasmPedigreeTreeNode parent : node.getLinkedNodes()) {
            printNode(parent, level + 1);
        }
    }

    @Test
    public void testGetMaintenanceNeighborhood() throws Exception {
    
        MockDataUtil.mockNeighborhoodTestData(pedigreeManager, 'M');
        GermplasmPedigreeTree tree;
        
        Debug.println(INDENT, "TestCase #1: GID = TOP node (GID, Backward, Forward = -1, 0, 6)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-1, 0, 6);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(INDENT, "TestCase #2: GID = LEAF node (GID, Backward, Forward = -5, 2, 10)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-5, 2, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(INDENT, "TestCase #3: GID = MID node AND Backward < Depth of LEAF (GID, Backward, Forward = -11, 1, 10)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-11, 1, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-7*-11**-12**-13***-14", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(INDENT, "TestCase #4: GID = MID node AND Backward = Dept of LEAF (GID, Backward, Forward = -11, 3, 10)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-11, 3, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));

        Debug.println(INDENT, "TestCase #5: GID = MID node AND Backward > Dept of LEAF (GID, Backward, Forward = -11, 5, 10)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-11, 5, 10);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2*-3**-6**-7***-11****-12****-13*****-14", MockDataUtil.printTree(tree, "*", ""));

        Debug.println(INDENT, "TestCase #6: GID = MID node AND Forward < Tree Depth - MID depth (GID, Backward, Forward = -3, 1, 2)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-3, 1, 2);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2*-3**-6**-7***-11", MockDataUtil.printTree(tree, "*", ""));
        
        Debug.println(INDENT, "TestCase #7: GID is MAN, but Ancestors and Descendants have non-MAN members (GID, Backward, Forward = -15, 2, 1)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-15, 2, 1);
        MockDataUtil.printTree(tree);
        assertEquals("-1*-2*-3**-6**-7***-11", MockDataUtil.printTree(tree, "*", ""));

        Debug.println(INDENT, "TestCase #8: Should stop at GEN even if Backward count is not exhausted (GID, Backward, Forward = -9, 4, 1)");
        tree = pedigreeManager.getMaintenanceNeighborhood(-9, 4, 1);
        MockDataUtil.printTree(tree);
        assertEquals("-4*-9*-10", MockDataUtil.printTree(tree, "*", ""));
        
        //cleanup
        MockDataUtil.cleanupMockMaintenanceTestData(pedigreeManager);
    }
            
    @Test
    public void testGetParentByGIDAndProgenitorNumber() throws Exception {
        Integer gid = Integer.valueOf(104);
        Integer progenitorNumber = Integer.valueOf(2);
        Germplasm result = pedigreeManager.getParentByGIDAndProgenitorNumber(gid, progenitorNumber);
        assertNotNull(result);
        Debug.println(INDENT, "testGetParentByGIDAndProgenitorNumber(): " + result);
    }
    
    @Test
    public void testCountDescendants() throws Exception {
        Integer gid = 10; //change gid value
        long count = pedigreeManager.countDescendants(gid);
        assertNotNull(count);
        Debug.println(INDENT, "testCountDescendants(" + gid + "):" + count);
    }
    
    @Test
    public void testCountGroupRelatives() throws Exception {
        Integer gid = 1; //change gid value
        long count = pedigreeManager.countGroupRelatives(gid);
        assertNotNull(count);
        Debug.println(INDENT, "testCountGroupRelatives(" + gid + "):" + count);
    }
    
    @Test
    public void testCountManagementNeighbors() throws Exception {
        Integer gid = 1; //change gid value
        long count = pedigreeManager.countManagementNeighbors(gid);
        assertNotNull(count);
        Debug.println(INDENT, "testCountManagementNeighbors(" + gid + "):" + count);
    }
    
    @Test
    public void testGetPedigreeLine() throws Exception {
        Integer gid = Integer.valueOf(306436);
        int locationID = 187;
        List<Germplasm> results = pedigreeManager.getPedigreeLine(gid, locationID);

        Debug.println(INDENT, "testGetPedigreeLine(" + gid + ", " + locationID + "):" + results.size());
        Debug.printObjects(INDENT, results);
    }
}
