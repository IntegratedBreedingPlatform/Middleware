
package org.generationcp.middleware.manager;

import java.util.List;
import java.util.Map;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.data.initializer.GermplasmDataManagerDataInitializer;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.pojos.Name;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class GermplasmDataManagerTest {

	private static final Integer TEST_STUDY_ID = 1;

	@Mock
	private GermplasmDAO germplasmDAO;

	@Test
	public void testGetDirectParentsForStudyOneParentOnly() {

		// we make use of partial mocking techniques so as to be able to inject the GermplasmDAO mock
		final GermplasmDataManagerImpl germplasmDataManager = Mockito.mock(GermplasmDataManagerImpl.class);

		Mockito.doReturn(germplasmDAO).when(germplasmDataManager).getGermplasmDao();
		Mockito.doReturn(GermplasmDataManagerDataInitializer.createGermplasmParentNameMap()).when(this.germplasmDAO)
				.getGermplasmParentNamesForStudy(TEST_STUDY_ID);

		final List<Germplasm> germplasmList = GermplasmDataManagerDataInitializer.createGermplasmList();
		// for testing purposes, we set the gpid1 of the germplasm to simulate a germplasm with only one parent
		final Germplasm testGermplasm = germplasmList.get(0);
		testGermplasm.setGpid1(0);

		Mockito.doReturn(germplasmList).when(this.germplasmDAO).getGermplasmParentsForStudy(TEST_STUDY_ID);

		// the doCallRealMethod allows us to execute the actual implementation instead of a test stub
		Mockito.doCallRealMethod().when(germplasmDataManager).getDirectParentsForStudy(TEST_STUDY_ID);
		Mockito.doCallRealMethod().when(germplasmDataManager)
				.createGermplasmPedigreeTreeNode(Mockito.anyInt(), Mockito.anyMapOf(GermplasmNameType.class, Name.class));

		final Map<Integer, GermplasmPedigreeTreeNode> output = germplasmDataManager.getDirectParentsForStudy(TEST_STUDY_ID);

		Assert.assertTrue(
				"Data manager should still be able to create a structure containing parent information even with only one parent",
				output.containsKey(GermplasmDataManagerDataInitializer.TEST_GID));

		Assert.assertNotNull(
				"Data manager should still be able to create a structure containing parent information even with only one parent",
				output.get(GermplasmDataManagerDataInitializer.TEST_GID));

		final GermplasmPedigreeTreeNode node = output.get(GermplasmDataManagerDataInitializer.TEST_GID);

		// we use 0 as the index for retrieving the expected null object here because previously, we set the germplasm's gpid1 to 0
		// gpid1 represents the first parent, hence the first parent info should be null
		Assert.assertNull("Non present parental information should be represented by a null in the linked nodes list", node
				.getLinkedNodes().get(0));

		// since gpid2 (2nd parent) information is available, we expect the 2nd entry in the list to have actual info
		Assert.assertNotNull("Data manager unable to properly represent parent info when only one is present", node.getLinkedNodes().get(1));
		Assert.assertEquals("Data manager unable to properly represent parent info when only one is present", testGermplasm.getGpid2(),
				node.getLinkedNodes().get(1).getGermplasm().getGid());

	}

}
