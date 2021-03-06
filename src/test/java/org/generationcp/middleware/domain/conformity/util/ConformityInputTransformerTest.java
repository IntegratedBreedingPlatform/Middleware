
package org.generationcp.middleware.domain.conformity.util;

import java.util.HashMap;
import java.util.Map;

import org.generationcp.middleware.domain.conformity.ConformityGermplasmInput;
import org.generationcp.middleware.domain.conformity.UploadInput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConformityInputTransformerTest {

	private Map<String, String>[] parentMapArray;
	private Map<String, String>[] populationMapArray;

	@Test
	public void testInputTransformation() throws Exception {
		UploadInput input = ConformityInputTransformer.transformInput(this.parentMapArray, this.populationMapArray);

		Assert.assertNotNull(input);
		Assert.assertTrue(input.isParentInputAvailable());
		Assert.assertTrue(input.getParentAGID() == 31591);
		Assert.assertTrue(input.getParentBGID() == 37101);
		Assert.assertTrue(input.getEntries().size() == this.parentMapArray.length + this.populationMapArray.length);

		ConformityGermplasmInput parentAInput = input.getEntries().get(31591);
		Assert.assertEquals("SUVITA-2", parentAInput.getLine());
		Assert.assertEquals("Parent1", parentAInput.getAlias());
		Assert.assertTrue(parentAInput.getMarkerValues().size() == 3);

		Assert.assertEquals("G", parentAInput.getMarkerValues().get("1_0583"));
		Assert.assertEquals("A", parentAInput.getMarkerValues().get("1_0878"));
		Assert.assertEquals("G", parentAInput.getMarkerValues().get("1_0690"));

	}

	@Before
	public void setUp() {
		this.parentMapArray = new Map[2];
		this.populationMapArray = new Map[3];

		Map<String, String> entryMap = new HashMap<String, String>();

		// simulate a hashmap entry for the first parent, based on the provided input
		entryMap.put("Line", "SUVITA-2");
		entryMap.put("GID", "31591");
		entryMap.put("Alias", "Parent1");
		entryMap.put("SNo", "1");
		entryMap.put("1_0583", "G");
		entryMap.put("1_0878", "A");
		entryMap.put("1_0690", "G");
		this.parentMapArray[0] = entryMap;

		entryMap = new HashMap<String, String>();
		entryMap.put("Line", "IT 97 K-499-35");
		entryMap.put("GID", "37101");
		entryMap.put("Alias", "Parent2");
		entryMap.put("SNo", "2");
		entryMap.put("1_0583", "A");
		entryMap.put("1_0878", "G");
		entryMap.put("1_0690", "A");
		this.parentMapArray[1] = entryMap;

		entryMap = new HashMap<String, String>();
		entryMap.put("Line", "2010-057");
		entryMap.put("GID", "-255");
		entryMap.put("Alias", "F1");
		entryMap.put("SNo", "3");
		entryMap.put("1_0583", "G/A");
		entryMap.put("1_0878", "G/A");
		entryMap.put("1_0690", "G/A");
		this.populationMapArray[0] = entryMap;

		entryMap = new HashMap<String, String>();
		entryMap.put("Line", "2010-057-1");
		entryMap.put("GID", "-256");
		entryMap.put("Alias", "F2");
		entryMap.put("SNo", "4");
		entryMap.put("1_0583", "A");
		entryMap.put("1_0878", "A");
		entryMap.put("1_0690", "G");
		this.populationMapArray[1] = entryMap;

		entryMap = new HashMap<String, String>();
		entryMap.put("Line", "2010-057-2");
		entryMap.put("GID", "-257");
		entryMap.put("Alias", "F2");
		entryMap.put("SNo", "5");
		entryMap.put("1_0583", "A");
		entryMap.put("1_0878", "A");
		entryMap.put("1_0690", "A");
		this.populationMapArray[2] = entryMap;

	}
}
