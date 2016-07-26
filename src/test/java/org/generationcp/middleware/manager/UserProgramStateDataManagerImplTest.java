
package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class UserProgramStateDataManagerImplTest extends IntegrationTestBase {

	@Autowired
	private UserProgramStateDataManager manager;

	@Test
	public void testSavingUserProgramStateAndRetrieval() throws MiddlewareQueryException {
		Integer userId = 99;
		String programUuid = "xx221212";
		String type = "GERMPLASM_LIST";
		List<String> treeState = new ArrayList<String>();
		treeState.add("45");
		treeState.add("5");
		treeState.add("6");

		this.manager.saveOrUpdateUserProgramTreeState(userId, programUuid, type, treeState);
		List<String> states = this.manager.getUserProgramTreeStateByUserIdProgramUuidAndType(userId, programUuid, type);
		for (int index = 0; index < treeState.size(); index++) {
			Assert.assertEquals("Should be the same list after saving and retrieving", treeState.get(index), states.get(index));
		}
	}

}
