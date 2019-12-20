
package org.generationcp.middleware.service.pedigree.string.processors;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.pedigree.GermplasmNode;
import org.generationcp.middleware.service.pedigree.string.processors.BackcrossProcessor;
import org.generationcp.middleware.service.pedigree.string.processors.BreedingMethodFactory;
import org.generationcp.middleware.service.pedigree.string.processors.BreedingMethodProcessor;
import org.generationcp.middleware.service.pedigree.string.processors.SimpleCrossProcessor;
import org.generationcp.middleware.service.pedigree.string.processors.DoubleCrossProcessor;
import org.generationcp.middleware.service.pedigree.string.processors.InbredProcessor;
import org.generationcp.middleware.service.pedigree.string.processors.SingleCrossHybridProcessor;
import org.generationcp.middleware.service.pedigree.string.processors.ThreeWayHybridProcessor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BreedingMethodFactoryTest {

	@Test
	public void testFactoryReturnsSingleCrossHybridProcessor() {
		GermplasmNode germplasmNode = getGermplasmNodeWithMethodName("Single cross");
		BreedingMethodProcessor methodProcessor = BreedingMethodFactory.getMethodProcessor(germplasmNode);
		Assert.assertTrue(methodProcessor instanceof SingleCrossHybridProcessor);
		Assert.assertEquals("A single cross hybrid must reduce the level value by 1", 1,
				((SingleCrossHybridProcessor) methodProcessor).getLevelSubtractor());
	}

	@Test
	public void testFactoryReturnsComplexCrossHybridProcessor() {
		GermplasmNode germplasmNode = getGermplasmNodeWithMethodName("complex cross");
		BreedingMethodProcessor methodProcessor = BreedingMethodFactory.getMethodProcessor(germplasmNode);
		Assert.assertTrue("Complex cross is processed by a single cross processor.", methodProcessor instanceof SingleCrossHybridProcessor);
		Assert.assertEquals("A cross hybrid must reduce the level value by 0", 0,
				((SingleCrossHybridProcessor) methodProcessor).getLevelSubtractor());
	}

	@Test
	public void testFactoryReturnsDoubleCrossProcessor(){
		GermplasmNode germplasmNode = getGermplasmNodeWithMethodName("double cross");
		BreedingMethodProcessor methodProcessor = BreedingMethodFactory.getMethodProcessor(germplasmNode);
		Assert.assertTrue( methodProcessor instanceof DoubleCrossProcessor);
	}

	@Test
	public void testFactoryReturnsBackcrossProcessor() {
		GermplasmNode germplasmNode = getGermplasmNodeWithMethodName("three-way cross");
		BreedingMethodProcessor methodProcessor = BreedingMethodFactory.getMethodProcessor(germplasmNode);
		Assert.assertTrue( methodProcessor instanceof ThreeWayHybridProcessor);
	}

	@Test
	public void testFactoryReturnsThreewayCrossProcessor() {
		GermplasmNode germplasmNode = getGermplasmNodeWithMethodName("backcross");
		BreedingMethodProcessor methodProcessor = BreedingMethodFactory.getMethodProcessor(germplasmNode);
		Assert.assertTrue( methodProcessor instanceof BackcrossProcessor);
	}

	@Test
	public void testFactoryReturnsNormalCrossProcessor() {
		GermplasmNode germplasmNode = getGermplasmNodeWithMethodName("cross");
		BreedingMethodProcessor methodProcessor = BreedingMethodFactory.getMethodProcessor(germplasmNode);
		Assert.assertTrue( methodProcessor instanceof SimpleCrossProcessor);
	}

	@Test
	public void testFactoryReturnsDefaultProcessorWhenGermplasmNotNull(){
		GermplasmNode germplasmNode = getGermplasmNodeWithMethodName("");
		BreedingMethodProcessor methodProcessor = BreedingMethodFactory.getMethodProcessor(germplasmNode);
		Assert.assertTrue( methodProcessor instanceof SingleCrossHybridProcessor);
	}

	@Test
	public void testFactoryReturnsDefaultProcessorWhenGermplasmIsNull(){
		GermplasmNode germplasmNode = getGermplasmNodeWithMethodName("");
		germplasmNode.setGermplasm(null);
		BreedingMethodProcessor methodProcessor = BreedingMethodFactory.getMethodProcessor(germplasmNode);
		Assert.assertTrue( methodProcessor instanceof InbredProcessor);
	}
	private GermplasmNode getGermplasmNodeWithMethodName(final String methodName) {
		GermplasmNode germplasmNode = new GermplasmNode(new Germplasm(1));
		Method method = new Method();
		method.setMname(methodName);
		germplasmNode.setMethod(method);
		return germplasmNode;
	}

}
