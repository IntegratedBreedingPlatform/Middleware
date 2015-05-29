package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.junit.BeforeClass;
import org.junit.Test;

public class UserProgramStateDataManagerImplTest  extends DataManagerIntegrationTest {

    private static UserProgramStateDataManager manager;

    @BeforeClass
    public static void setUp() throws Exception {
        manager = managerFactory.getUserProgramStateDataManager();
    }
    
    @Test
    public void testSavingUserProgramStateAndRetrieval() throws MiddlewareQueryException{
    	Integer userId = 99;
    	String programUuid = "xx221212";
    	String type = "GERMPLASM_LIST";
    	List<String> treeState = new ArrayList<String>();
    	treeState.add("45");
    	treeState.add("5");
    	treeState.add("6");
    	manager.saveOrUpdateProgramPreset(userId, programUuid, type, treeState);
    	List<String> states = manager.getUserProgramTreeStateByUserIdProgramUuidAndType(userId, programUuid, type);
    	for(int index = 0 ; index < treeState.size() ; index++){
    		Assert.assertEquals("Should be the same list after savign and retrieving", treeState.get(index), states.get(index));
    	}
    }

}
