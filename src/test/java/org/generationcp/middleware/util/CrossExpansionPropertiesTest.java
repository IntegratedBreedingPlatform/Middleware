package org.generationcp.middleware.util;

import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

import org.generationcp.middleware.manager.StudyDataManagerImplTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;


public class CrossExpansionPropertiesTest {




	@Test
	public void testGetNameTypeOrder() throws Exception {
		final Properties mockProperties = Mockito.mock(Properties.class);
		Mockito.when(mockProperties.getProperty("maize.nametype.order")).thenReturn("43,42,41");
		Mockito.when(mockProperties.getProperty("wheat.nametype.order")).thenReturn("1,4,5");

		final CrossExpansionProperties crossExpansionProperties = new CrossExpansionProperties(mockProperties);

		final List<Integer> nameTypeOrderMaize = crossExpansionProperties.getNameTypeOrder("maize");

		assertTrue("The Name type order must contain number 43", nameTypeOrderMaize.contains(new Integer(43)));
		assertTrue("The Name type order must contain number 42", nameTypeOrderMaize.contains(new Integer(42)));
		assertTrue("The Name type order must contain number 41", nameTypeOrderMaize.contains(new Integer(41)));

		// Order is relevant and must be in the same order provided in the property file
		assertTrue("The Name type order item 0", nameTypeOrderMaize.get(0).equals(new Integer(43)));
		assertTrue("The Name type order item 1", nameTypeOrderMaize.get(1).equals(new Integer(42)));
		assertTrue("The Name type order item 2", nameTypeOrderMaize.get(2).equals(new Integer(41)));

		final List<Integer> nameTypeOrderWheat = crossExpansionProperties.getNameTypeOrder("wheat");

		assertTrue("The Name type order must contain number 5", nameTypeOrderWheat.contains(new Integer(5)));
		assertTrue("The Name type order must contain number 4", nameTypeOrderWheat.contains(new Integer(4)));
		assertTrue("The Name type order must contain number 1", nameTypeOrderWheat.contains(new Integer(1)));

		// Order is relevant and must be in the same order provided in the property file

		assertTrue("The Name type order item 0", nameTypeOrderWheat.get(0).equals(new Integer(1)));
		assertTrue("The Name type order item 1", nameTypeOrderWheat.get(1).equals(new Integer(4)));
		assertTrue("The Name type order item 2", nameTypeOrderWheat.get(2).equals(new Integer(5)));

		final List<Integer> nameTypeOrderRice = crossExpansionProperties.getNameTypeOrder("rice");

		assertTrue("For unspecified crops this must be an empty list", nameTypeOrderRice.isEmpty());


	}

	@Test
	public void testExceptionalNameTypeOrder() throws Exception {
		final Properties mockProperties = Mockito.mock(Properties.class);
		Mockito.when(mockProperties.getProperty("maize.nametype.order")).thenReturn("A,B,C");

		final CrossExpansionProperties crossExpansionProperties = new CrossExpansionProperties(mockProperties);

		final List<Integer> nameTypeOrderMaize = crossExpansionProperties.getNameTypeOrder("maize");


	}

	@Test
	public void testGetCropGenerationLevel() throws Exception {
		final Properties mockProperties = Mockito.mock(Properties.class);
		final CrossExpansionProperties crossExpansionProperties = new CrossExpansionProperties(mockProperties);
		crossExpansionProperties.setDefaultLevel(100);

		Mockito.when(mockProperties.getProperty("wheat.generation.level")).thenReturn("2");
		Mockito.when(mockProperties.getProperty("maize.generation.level")).thenReturn("3");
		assertEquals(3, crossExpansionProperties.getCropGenerationLevel("maize"));
		assertEquals(2, crossExpansionProperties.getCropGenerationLevel("wheat"));

		// For crops which do not have a generation level specified drop to the default level.
		assertEquals(100, crossExpansionProperties.getCropGenerationLevel("rice"));

	}

}
