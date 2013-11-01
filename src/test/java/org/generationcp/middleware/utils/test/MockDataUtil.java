package org.generationcp.middleware.utils.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.dao.MethodDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTree;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.util.Debug;
import org.mockito.Mockito;

public class MockDataUtil {

	//Germplasm Test Data
	private static Germplasm g1 = createTestGermplasm(-1, 1, -1, 0);
	private static Germplasm g2 = createTestGermplasm(-2, 1, -1, -1);
	private static Germplasm g3 = createTestGermplasm(-3, 1, -1, -1);
	private static Germplasm g4 = createTestGermplasm(-4, 3, 2, -2);
	private static Germplasm g5 = createTestGermplasm(-5, 2, -1, -2);
	private static Germplasm g6 = createTestGermplasm(-6, 1, -1, -3);
	private static Germplasm g7 = createTestGermplasm(-7, 1, -1, -3);
	private static Germplasm g8 = createTestGermplasm(-8, 2, -1, -3);
	private static Germplasm g9 = createTestGermplasm(-9, 1, -1, -4);
	private static Germplasm g10 = createTestGermplasm(-10, 1, -1, -4);
	private static Germplasm g11 = createTestGermplasm(-11, 1, -1, -7);
	private static Germplasm g12 = createTestGermplasm(-12, 1, -1, -11);
	private static Germplasm g13 = createTestGermplasm(-13, 1, -1, -11);
	private static Germplasm g14 = createTestGermplasm(-14, 1, -1, -13);
	private static Germplasm g15 = createTestGermplasm(-15, 1, -1, -8);
	private static Germplasm g16 = createTestGermplasm(-16, 1, -1, -8);
	private static Germplasm g17 = createTestGermplasm(-17, 1, -1, -15);
	private static Germplasm g18 = createTestGermplasm(-18, 2, -1, -15);
	private static Germplasm g19 = createTestGermplasm(-19, 1, -1, -16);
	private static Germplasm g20 = createTestGermplasm(-20, 1, -1, -18);
	
	private static Germplasm[] germplasmArr = {g1, g2, g3, g4, g5, g6, g7, g8, g9, g10, g11, g12, g13, g14, g15, g16, g17, g18, g19, g20};

	/**
	 * Create the mock test data for the Manager
	 * 
	 * @param manager
	 * @throws MiddlewareQueryException
	 */
	public static <M> void mockNeighborhoodTestData(M manager, char methodType) throws MiddlewareQueryException {
    	
    	GermplasmDAO gDao = createMockAndInjectDao(manager, GermplasmDAO.class, "germplasmDao");
    	MethodDAO mDao = createMockAndInjectDao(manager, MethodDAO.class, "methodDao");

    	mockGermplasmTestData(gDao, methodType);
    	mockMethodTestData(mDao);
    }
	
	/**
	 * Cleanup and remove the mock objects from the Manager.
	 * 
	 * @param manager
	 * @throws MiddlewareQueryException
	 */
	public static <M> void cleanupMockMaintenanceTestData(M manager) throws MiddlewareQueryException {
		removeMockFromManager(manager, "germplasmDao");
		removeMockFromManager(manager, "methodDao");
	}
		
	/**
	 * Creates and returns the mock DAO class, and inject this class to the Manager.
	 */
	private static <T, M> T createMockAndInjectDao(M manager, Class<T> daoClass, String fieldName) throws MiddlewareQueryException {

		T dao = Mockito.mock(daoClass);
    	setToManager(manager, fieldName, dao);
    	
    	return dao;
	}
	
	/**
	 * Remove mock objects from the Manager
	 */
	private static <M> void removeMockFromManager(M manager, String fieldName) throws MiddlewareQueryException {
		setToManager(manager, fieldName, null);
	}
	
	private static <M> void setToManager(M manager, String fieldName, Object value) throws MiddlewareQueryException {
    	try {
    		//TODO: when using spring, change this directly to inject mock class instead of reflection
    		Field field = manager.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
    		field.setAccessible(true);
    		field.set(manager, value);
    		//TODO: hard coded while not using spring injection
    		Field gMgrField = manager.getClass().getDeclaredField("germplasmDataManager");
    		gMgrField.setAccessible(true);
    		GermplasmDataManager gManager = (GermplasmDataManager) gMgrField.get(manager);
    		field = gManager.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
    		field.setAccessible(true);
    		field.set(gManager, value);
    	
    	} catch (Exception e) {
    		throw new MiddlewareQueryException("Failed to inject DAOs to the Manager for Testing: " + e.getMessage(), e);
    	}
		
	}
    
	/**
	 * Create Germplasm Test Data
	 */
	private static void mockGermplasmTestData(GermplasmDAO gDao, char methodType) throws MiddlewareQueryException {
    	for (int i = 1; i <= germplasmArr.length; i++) {
    		Mockito.when(gDao.getByGIDWithPrefName(i*-1)).thenReturn(germplasmArr[i-1]);
    	}
    	
    	for (int i = 1; i <= germplasmArr.length; i++) {
    		Mockito.when(gDao.getChildren(i*-1, methodType)).thenReturn(getChildren(i*-1, germplasmArr, methodType));
    	}
	}
	
	/**
	 * Create Method Test Data
	 */
	private static void mockMethodTestData(MethodDAO mDao) throws MiddlewareQueryException {
    	Mockito.when(mDao.getById(1, false)).thenReturn(new Method(1, "MAN", "S", "C2W", "Maintenance", "Maintenance", 0, 0, 0, 0, 0, 0, 0, 0));
    	Mockito.when(mDao.getById(2, false)).thenReturn(new Method(2, "DER", "S", "C2W", "Derivative", "Derivative", 0, 0, 0, 0, 0, 0, 0, 0));
    	Mockito.when(mDao.getById(3, false)).thenReturn(new Method(3, "GEN", "S", "C2W", "Generative", "Generative", 0, 0, 0, 0, 0, 0, 0, 0));
	}
	
	/**
	 * Get the children germplasm from the mock test data.
	 */
	private static List<Germplasm> getChildren(int gid, Germplasm[] germplasmArr, char methodType) {
    	List<Germplasm> list = new ArrayList<Germplasm>();
    	for (int i = 0; i < germplasmArr.length; i++) {
    		if (germplasmArr[i].getGpid2() == gid && (methodType == 'D' || methodType == 'M' && germplasmArr[i].getMethodId() == 1)) {
    			list.add(germplasmArr[i]);
    		}
    	}
    	return list;
    }
    
	/**
	 * Create a test Germplasm class
	 */
    private static Germplasm createTestGermplasm(int gid, int methodId, int gnpgs, int gpid2) {
    	Germplasm germplasm = new Germplasm(gid);
    	germplasm.setMethodId(methodId);
    	germplasm.setGnpgs(gnpgs);
    	germplasm.setGpid2(gpid2);
    	return germplasm;
    }
    
    /**
     * Prints the GermplasmPedigreeTree content.
     * 
     * @param tree
     * @param depth
     */
    public static String printTree(GermplasmPedigreeTree tree, String tabChar, String endChar) {
    	if (tree != null) {
    		StringBuffer printOut = new StringBuffer();
    		printNode(printOut, tree.getRoot(), 0, tabChar, endChar);
    		return printOut.toString();
    	} else {
    		return "NO TREE CREATED";
    	}
    }

    /**
     * Default printing format for the console
     */
    public static void printTree(GermplasmPedigreeTree tree) {
    	Debug.println(0, printTree(tree, "  ", "\n"));
    }

    /**
     * Recursively prints the GermplasmPedigreeTreeNodes in the tree.
     */
    private static void printNode(StringBuffer printOut, GermplasmPedigreeTreeNode node, int depth, String tabChar, String endChar) {
    	printOut.append(getTabsByDepth(depth, tabChar) + node.getGermplasm().getGid() + endChar);
    	if (node.getLinkedNodes() != null) {
    		for (GermplasmPedigreeTreeNode aNode : node.getLinkedNodes()) {
    			printNode(printOut, aNode, depth + 1, tabChar, endChar);
    		}
    	} else {
    		printOut.append(getTabsByDepth(depth, tabChar) + "no linked nodes" + endChar);
    	}
    }

    /**
     * Defines the indention in the tree
     */
    private static String getTabsByDepth(int depth, String tabChar) {
    	StringBuffer tabs = new StringBuffer();
    	for (int i = 0; i < depth; i++) {
    		tabs.append(tabChar);
    	}
    	return tabs.toString();
    }
    
}
