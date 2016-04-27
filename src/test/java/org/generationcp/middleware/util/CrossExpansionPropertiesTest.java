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
		Mockito.when(mockProperties.getProperty("maize.nametype.order")).thenReturn("CODE3,CODE2,CODE1");
		Mockito.when(mockProperties.getProperty("wheat.nametype.order")).thenReturn("WHEAT_CODE3,WHEAT_CODE4,WHEAT_CODE5");

		final CrossExpansionProperties crossExpansionProperties = new CrossExpansionProperties(mockProperties);

		final List<String> nameTypeOrderMaize = crossExpansionProperties.getNameTypeOrder("maize");

		assertTrue("The Name type order must contain number CODE3", nameTypeOrderMaize.contains("CODE3"));
		assertTrue("The Name type order must contain number CODE2", nameTypeOrderMaize.contains("CODE2"));
		assertTrue("The Name type order must contain number CODE1", nameTypeOrderMaize.contains("CODE1"));

		// Order is relevant and must be in the same order provided in the property file
		assertTrue("The Name type order item 0", nameTypeOrderMaize.get(0).equals("CODE3"));
		assertTrue("The Name type order item 1", nameTypeOrderMaize.get(1).equals("CODE2"));
		assertTrue("The Name type order item 2", nameTypeOrderMaize.get(2).equals("CODE1"));

		final List<String> nameTypeOrderWheat = crossExpansionProperties.getNameTypeOrder("wheat");

		assertTrue("The Name type order must contain number 5", nameTypeOrderWheat.contains("WHEAT_CODE3"));
		assertTrue("The Name type order must contain number 4", nameTypeOrderWheat.contains("WHEAT_CODE4"));
		assertTrue("The Name type order must contain number 1", nameTypeOrderWheat.contains("WHEAT_CODE5"));

		// Order is relevant and must be in the same order provided in the property file

		assertTrue("The Name type order item 0", nameTypeOrderWheat.get(0).equals("WHEAT_CODE3"));
		assertTrue("The Name type order item 1", nameTypeOrderWheat.get(1).equals("WHEAT_CODE4"));
		assertTrue("The Name type order item 2", nameTypeOrderWheat.get(2).equals("WHEAT_CODE5"));

		final List<String> nameTypeOrderRice = crossExpansionProperties.getNameTypeOrder("rice");

		assertTrue("For unspecified crops this must be an empty list", nameTypeOrderRice.isEmpty());


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
