
package org.generationcp.middleware.service.pedigree;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.pedigree.cache.keys.CropGermplasmKey;
import org.hibernate.SQLQuery;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Function;
import com.google.common.cache.Cache;

import org.junit.Assert;

/**
 * Integration test to make sure the germplasm cache is working.
 *
 */
public class GermplasmCacheTest extends IntegrationTestBase {

	private static final String TEST_CROP = "TestCrop";
	@Autowired
	private GermplasmDataManager germplasmManager;

	@Test
	public void testInitialisesCache() {
		final GermplasmCache germplasmCache = new GermplasmCache(germplasmManager, 5);

		runGermplasmTest(germplasmCache, new Function<Set<Integer>, Void>(){

			@Override
			public Void apply(Set<Integer> input) {
				germplasmCache.initialisesCache(TEST_CROP, input, 5);
				return null;
			}});

	}
	
	@Test
	public void testGetGermplasm() {
		final GermplasmCache germplasmCache = new GermplasmCache(germplasmManager, 5);

		runGermplasmTest(germplasmCache, new Function<Set<Integer>, Void>(){

			@Override
			public Void apply(Set<Integer> gids) {
				for (Integer gid : gids) {
					germplasmCache.getGermplasm(new CropGermplasmKey(TEST_CROP, gid));
				}
				return null;
			}});

	}

	private void runGermplasmTest(final GermplasmCache germplasmCache, final Function<Set<Integer>, Void> function) {
		List<Germplasm> germplasmWithOneJustOneParent = getGermplasmWithOneJustOneLevelAncestry();

		Set<Integer> testSet = getTestSet(germplasmWithOneJustOneParent);
		// Underlying database crop name does not matter for the test.
		
		function.apply(testSet);
		
		final Cache<CropGermplasmKey, Germplasm> germplasmGoogleGauvaCache = germplasmCache.getGermplasmCache();

		Set<Integer> expectedNumberOfGermplasmCached = getExpectedNumberOfGermplasmCached(germplasmWithOneJustOneParent);
		Assert.assertEquals(expectedNumberOfGermplasmCached.size(), germplasmGoogleGauvaCache.size());
	}

	private Set<Integer> getExpectedNumberOfGermplasmCached(final List<Germplasm> germplasmWithOneJustOneParent) {
		final Set<Integer> cachedGids = new HashSet<>();

		for (Germplasm germplasm : germplasmWithOneJustOneParent) {
			cachedGids.add(germplasm.getGid());
			cachedGids.add(germplasm.getGpid1());
			cachedGids.add(germplasm.getGpid2());
		}

		return cachedGids;
	}

	private Set<Integer> getTestSet(List<Germplasm> germplasmWithOneJustOneParent) {
		final Set<Integer> testGids = new HashSet<>();
		for (Germplasm germplasm : germplasmWithOneJustOneParent) {
			testGids.add(germplasm.getGid());
		}
		return testGids;
	}

	private List<Germplasm> getGermplasmWithOneJustOneLevelAncestry() {
		SQLQuery createSQLQuery = this.sessionProvder.getSession()
				.createSQLQuery("Select parent.* FROM germplsm parent "
						+ "INNER JOIN germplsm kid on parent.gpid1 = kid.gid or parent.gpid2 = kid.gid "
						+ "WHERE parent.gpid1 !=0 AND parent.gpid2 !=0 AND parent.grplce = 0 and kid.gpid1 = 0 "
						+ "AND kid.gpid2 = 0 AND kid.grplce = 0 LIMIT 5");
		createSQLQuery.addEntity(Germplasm.class);
		return createSQLQuery.list();
	}
}
