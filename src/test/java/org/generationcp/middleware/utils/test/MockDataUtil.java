
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

	private static final int INDENT = 3;

	// Germplasm Test Data
	private static final Germplasm g1 = MockDataUtil.createTestGermplasm(-1, 1, -1, 0);
	private static final Germplasm g2 = MockDataUtil.createTestGermplasm(-2, 1, -1, -1);
	private static final Germplasm g3 = MockDataUtil.createTestGermplasm(-3, 1, -1, -1);
	private static final Germplasm g4 = MockDataUtil.createTestGermplasm(-4, 3, 2, -2);
	private static final Germplasm g5 = MockDataUtil.createTestGermplasm(-5, 2, -1, -2);
	private static final Germplasm g6 = MockDataUtil.createTestGermplasm(-6, 1, -1, -3);
	private static final Germplasm g7 = MockDataUtil.createTestGermplasm(-7, 1, -1, -3);
	private static final Germplasm g8 = MockDataUtil.createTestGermplasm(-8, 2, -1, -3);
	private static final Germplasm g9 = MockDataUtil.createTestGermplasm(-9, 1, -1, -4);
	private static final Germplasm g10 = MockDataUtil.createTestGermplasm(-10, 1, -1, -4);
	private static final Germplasm g11 = MockDataUtil.createTestGermplasm(-11, 1, -1, -7);
	private static final Germplasm g12 = MockDataUtil.createTestGermplasm(-12, 1, -1, -11);
	private static final Germplasm g13 = MockDataUtil.createTestGermplasm(-13, 1, -1, -11);
	private static final Germplasm g14 = MockDataUtil.createTestGermplasm(-14, 1, -1, -13);
	private static final Germplasm g15 = MockDataUtil.createTestGermplasm(-15, 1, -1, -8);
	private static final Germplasm g16 = MockDataUtil.createTestGermplasm(-16, 1, -1, -8);
	private static final Germplasm g17 = MockDataUtil.createTestGermplasm(-17, 1, -1, -15);
	private static final Germplasm g18 = MockDataUtil.createTestGermplasm(-18, 2, -1, -15);
	private static final Germplasm g19 = MockDataUtil.createTestGermplasm(-19, 1, -1, -16);
	private static final Germplasm g20 = MockDataUtil.createTestGermplasm(-20, 1, -1, -18);

	private static final Germplasm[] germplasmArr = {MockDataUtil.g1, MockDataUtil.g2, MockDataUtil.g3, MockDataUtil.g4, MockDataUtil.g5,
			MockDataUtil.g6, MockDataUtil.g7, MockDataUtil.g8, MockDataUtil.g9, MockDataUtil.g10, MockDataUtil.g11, MockDataUtil.g12,
			MockDataUtil.g13, MockDataUtil.g14, MockDataUtil.g15, MockDataUtil.g16, MockDataUtil.g17, MockDataUtil.g18, MockDataUtil.g19,
			MockDataUtil.g20};

	/**
	 * Create the mock test data for the Manager
	 *
	 * @param manager
	 * @throws MiddlewareQueryException
	 */
	public static <M> void mockNeighborhoodTestData(final M manager, final char methodType) throws MiddlewareQueryException {

		final GermplasmDAO gDao = MockDataUtil.createMockAndInjectDao(manager, GermplasmDAO.class, "germplasmDao");
		final MethodDAO mDao = MockDataUtil.createMockAndInjectDao(manager, MethodDAO.class, "methodDao");

		MockDataUtil.mockGermplasmTestData(gDao, methodType);
		MockDataUtil.mockMethodTestData(mDao);
	}

	/**
	 * Cleanup and remove the mock objects from the Manager.
	 *
	 * @param manager
	 * @throws MiddlewareQueryException
	 */
	public static <M> void cleanupMockMaintenanceTestData(final M manager) throws MiddlewareQueryException {
		MockDataUtil.removeMockFromManager(manager, "germplasmDao");
		MockDataUtil.removeMockFromManager(manager, "methodDao");
	}

	/**
	 * Creates and returns the mock DAO class, and inject this class to the Manager.
	 */
	private static <T, M> T createMockAndInjectDao(final M manager, final Class<T> daoClass, final String fieldName) throws MiddlewareQueryException {

		final T dao = Mockito.mock(daoClass);
		MockDataUtil.setToManager(manager, fieldName, dao);

		return dao;
	}

	/**
	 * Remove mock objects from the Manager
	 */
	private static <M> void removeMockFromManager(final M manager, final String fieldName) throws MiddlewareQueryException {
		MockDataUtil.setToManager(manager, fieldName, null);
	}

	private static <M> void setToManager(final M manager, final String fieldName, final Object value) throws MiddlewareQueryException {
		try {
			// TODO: when using spring, change this directly to inject mock class instead of reflection
			Field field = manager.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(manager, value);
			// TODO: hard coded while not using spring injection
			final Field gMgrField = manager.getClass().getDeclaredField("germplasmDataManager");
			gMgrField.setAccessible(true);
			final GermplasmDataManager gManager = (GermplasmDataManager) gMgrField.get(manager);
			field = gManager.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(gManager, value);

		} catch (final Exception e) {
			throw new MiddlewareQueryException("Failed to inject DAOs to the Manager for Testing: " + e.getMessage(), e);
		}

	}

	/**
	 * Create Germplasm Test Data
	 */
	private static void mockGermplasmTestData(final GermplasmDAO gDao, final char methodType) throws MiddlewareQueryException {
		for (int i = 1; i <= MockDataUtil.germplasmArr.length; i++) {
			Mockito.when(gDao.getByGIDWithPrefName(i * -1)).thenReturn(MockDataUtil.germplasmArr[i - 1]);
		}

		for (int i = 1; i <= MockDataUtil.germplasmArr.length; i++) {
			Mockito.when(gDao.getDescendants(i * -1, methodType)).thenReturn(
					MockDataUtil.getChildren(i * -1, MockDataUtil.germplasmArr, methodType));
		}
	}

	/**
	 * Create Method Test Data
	 */
	private static void mockMethodTestData(final MethodDAO mDao) throws MiddlewareQueryException {
		Mockito.when(mDao.getById(1, false)).thenReturn(
				new Method(1, "MAN", "S", "C2W", "Maintenance", "Maintenance", 0, 0, 0, 0, 0, 0, 0, 0));
		Mockito.when(mDao.getById(2, false)).thenReturn(
				new Method(2, "DER", "S", "C2W", "Derivative", "Derivative", 0, 0, 0, 0, 0, 0, 0, 0));
		Mockito.when(mDao.getById(3, false)).thenReturn(
				new Method(3, "GEN", "S", "C2W", "Generative", "Generative", 0, 0, 0, 0, 0, 0, 0, 0));
	}

	/**
	 * Get the children germplasm from the mock test data.
	 */
	private static List<Germplasm> getChildren(final int gid, final Germplasm[] germplasmArr, final char methodType) {
		final List<Germplasm> list = new ArrayList<Germplasm>();
		for (int i = 0; i < germplasmArr.length; i++) {
			if (germplasmArr[i].getGpid2() == gid && (methodType == 'D' || methodType == 'M' && germplasmArr[i].getMethod().getMid() == 1)) {
				list.add(germplasmArr[i]);
			}
		}
		return list;
	}

	/**
	 * Create a test Germplasm class
	 */
	private static Germplasm createTestGermplasm(final int gid, final int methodId, final int gnpgs, final int gpid2) {
		final Germplasm germplasm = new Germplasm(gid);
		germplasm.setMethod(new Method(methodId));
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
	public static String printTree(final GermplasmPedigreeTree tree, final String tabChar, final String endChar) {
		if (tree != null) {
			final StringBuffer printOut = new StringBuffer();
			MockDataUtil.printNode(printOut, tree.getRoot(), 0, tabChar, endChar);
			return printOut.toString();
		} else {
			return "NO TREE CREATED";
		}
	}

	/**
	 * Default printing format for the console
	 */
	public static void printTree(final GermplasmPedigreeTree tree) {
		Debug.println(MockDataUtil.INDENT, MockDataUtil.printTree(tree, "   ", "\n"));
	}

	/**
	 * Recursively prints the GermplasmPedigreeTreeNodes in the tree.
	 */
	private static void printNode(final StringBuffer printOut, final GermplasmPedigreeTreeNode node, final int depth, final String tabChar, final String endChar) {
		printOut.append(MockDataUtil.getTabsByDepth(depth, tabChar) + node.getGermplasm().getGid() + endChar);
		if (node.getLinkedNodes() != null) {
			for (final GermplasmPedigreeTreeNode aNode : node.getLinkedNodes()) {
				MockDataUtil.printNode(printOut, aNode, depth + 1, tabChar, endChar);
			}
		} else {
			printOut.append(MockDataUtil.getTabsByDepth(depth, tabChar) + "no linked nodes" + endChar);
		}
	}

	/**
	 * Defines the indention in the tree
	 */
	private static String getTabsByDepth(final int depth, final String tabChar) {
		final StringBuffer tabs = new StringBuffer();
		for (int i = 0; i < depth; i++) {
			tabs.append(tabChar);
		}
		return tabs.toString();
	}

}
