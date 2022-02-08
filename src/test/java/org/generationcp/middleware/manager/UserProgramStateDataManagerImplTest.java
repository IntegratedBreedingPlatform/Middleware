
package org.generationcp.middleware.manager;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class UserProgramStateDataManagerImplTest extends IntegrationTestBase {

	@Autowired
	private UserProgramStateDataManager manager;

	@Test
	public void testSavingUserProgramStateAndRetrieval() {
		final Integer userId = 99;
		final String programUuid = "xx221212";
		final String type = "GERMPLASM_LIST";
		final List<String> treeState = new ArrayList<>();
		treeState.add("45");
		treeState.add("5");
		treeState.add("6");

		this.manager.saveOrUpdateUserProgramTreeState(userId, programUuid, type, treeState);
		final List<String> states = this.manager.getUserProgramTreeState(userId, programUuid, type);
		for (int index = 0; index < treeState.size(); index++) {
			Assert
				.assertEquals("Should be the same list after saving and retrieving", treeState.get(index).trim(), states.get(index).trim());
		}
	}

	@Test
	public void testSavingUserProgramStateAndRetrieval_NullProgramUUID() {
		final Integer userId = 99;
		final String type = "GERMPLASM_LIST";
		final List<String> treeState = new ArrayList<>();
		treeState.add("CROP");
		treeState.add("55");
		treeState.add("66");

		this.manager.saveOrUpdateUserProgramTreeState(userId, null, type, treeState);
		final List<String> states = this.manager.getUserProgramTreeState(userId, null, type);
		for (int index = 0; index < treeState.size(); index++) {
			Assert
				.assertEquals("Should be the same list after saving and retrieving", treeState.get(index).trim(), states.get(index).trim());
		}
	}

}
