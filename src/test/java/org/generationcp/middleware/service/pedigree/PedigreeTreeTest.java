
package org.generationcp.middleware.service.pedigree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Method;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class PedigreeTreeTest {

	private static final String MAIZE = "maize";

	private FunctionBasedGuavaCacheLoader<CropGermplasmKey, Germplasm> germplasmCache;
	private FunctionBasedGuavaCacheLoader<CropMethodKey, Method> methodCache;

	private Map<CropGermplasmKey, Germplasm> germplasmMap;
	private Map<CropMethodKey, Method> methodsMap;

	private Set<Integer> randomNumbers;

	@Before
	public void setUp() throws Exception {

		// Keep our generated germplasm in this map
		this.germplasmMap = new HashMap<CropGermplasmKey, Germplasm>();

		// Keep our generated methos in this map
		this.methodsMap = new HashMap<CropMethodKey, Method>();

		this.randomNumbers = new LinkedHashSet<Integer>();

		setUpGermplasmCache();

		setUpMethodCache();

		generateRandomGermplasm();

	}

	private void setUpMethodCache() {
		this.methodCache =
				new FunctionBasedGuavaCacheLoader<CropMethodKey, Method>(CacheBuilder.newBuilder().maximumSize(100000)
						.expireAfterWrite(100, TimeUnit.MINUTES).<CropMethodKey, Method>build(), new Function<CropMethodKey, Method>() {
					@Override
					public Method apply(CropMethodKey key) {
						return PedigreeTreeTest.this.methodsMap.get(key);
					}
				});
	}

	private void setUpGermplasmCache() {
		this.germplasmCache =
				new FunctionBasedGuavaCacheLoader<CropGermplasmKey, Germplasm>(CacheBuilder.newBuilder().maximumSize(100000)
						.expireAfterWrite(100, TimeUnit.MINUTES).<CropGermplasmKey, Germplasm>build(), new Function<CropGermplasmKey, Germplasm>() {
					@Override
					public Germplasm apply(CropGermplasmKey key) {
						return PedigreeTreeTest.this.germplasmMap.get(key);
					}
				});
	}

	private void generateRandomGermplasm() {
		final Random randomNumGenerator = new Random();
		while (this.randomNumbers.size() < 10000) {
			final Integer next = randomNumGenerator.nextInt(10000) + 1;
			// Duplicates are ignored since it is a set
			this.randomNumbers.add(next);
		}
	}

	@Test
	public void testPedigreeTreeGeneration() throws Exception {
		final Germplasm generateRandomGermplasm = this.generateRandomGermplasm(2);

		final PedigreeTree pedigreeTree = new PedigreeTree(this.germplasmCache, this.methodCache, PedigreeTreeTest.MAIZE);
		final GermplasmNode resultNode = pedigreeTree.buildPedigreeTree(generateRandomGermplasm.getGid());
		resultNode.printTree();
		this.compareGeneratedNodes(generateRandomGermplasm, resultNode);
	}

	private void compareGeneratedNodes(final Germplasm generateRandomGermplasm, final GermplasmNode resultNode) {
		if (generateRandomGermplasm == null || resultNode == null) {
			Assert.assertTrue("Both must be null otherwise we have an issue.", generateRandomGermplasm == null && resultNode == null);
			return;
		}
		Assert.assertEquals(generateRandomGermplasm.getGid(), resultNode.getGermplasm().getGid());
		final CropGermplasmKey femaleGermplasmKey = new CropGermplasmKey(PedigreeTreeTest.MAIZE, generateRandomGermplasm.getGpid1());
		this.compareGeneratedNodes(this.germplasmMap.get(femaleGermplasmKey), resultNode.getFemaleParent());

		final CropGermplasmKey maleGermplasmKey = new CropGermplasmKey(PedigreeTreeTest.MAIZE, generateRandomGermplasm.getGpid2());

		this.compareGeneratedNodes(this.germplasmMap.get(maleGermplasmKey), resultNode.getMaleParent());

	}

	private Germplasm generateRandomGermplasm(final int maxNumberOfNodes) {
		final Random randomGenerator = new Random();
		final int femaleSideNodes = randomGenerator.nextInt(maxNumberOfNodes) + 1;
		final int maleSideNodes = randomGenerator.nextInt(maxNumberOfNodes) + 1;

		final Germplasm rootGermplasm = this.generateTestGermplasm(1, 1);
		this.generateTree(rootGermplasm, femaleSideNodes, maleSideNodes, this.randomNumbers.iterator());
		return rootGermplasm;
	}

	private void generateTree(final Germplasm germplasm, final int femaleSideNodes, final int maleSideNodes,
			final Iterator<Integer> iterator) {

		if (femaleSideNodes != 0) {
			final Integer femaleGid = iterator.next();
			final Germplasm generateTestGermplasm = this.generateTestGermplasm(femaleGid, 1);
			generateTree(generateTestGermplasm, femaleSideNodes - 1, maleSideNodes, iterator);
			germplasm.setGpid1(femaleGid);
		}

		if (maleSideNodes != 0) {
			final Integer maleGid = iterator.next();
			final Germplasm generateTestGermplasm = this.generateTestGermplasm(maleGid, 1);
			generateTree(generateTestGermplasm, femaleSideNodes, maleSideNodes - 1, iterator);

			germplasm.setGpid2(maleGid);
		}

	}

	private Germplasm generateTestGermplasm(final int gid, final int methodId) {
		final Germplasm germplasm = new Germplasm();
		germplasm.setGid(gid);
		final Method method = new Method(methodId);
		germplasm.setMethod(method);
		germplasm.setMethodId(methodId);
		this.germplasmMap.put(new CropGermplasmKey(PedigreeTreeTest.MAIZE, gid), germplasm);
		this.methodsMap.put(new CropMethodKey(PedigreeTreeTest.MAIZE, methodId), method);
		return germplasm;
	}

}
