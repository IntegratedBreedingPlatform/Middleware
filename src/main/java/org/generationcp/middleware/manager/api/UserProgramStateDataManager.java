package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.UserProgramTreeState;

public interface UserProgramStateDataManager {

	List<String> getUserProgramTreeStateByUserIdProgramUuidAndType(int userId, String programUuid, String type)
			throws MiddlewareQueryException;

	UserProgramTreeState saveOrUpdateUserProgramTreeState(int userId, String programUuid, String type, List<String> treeState)
			throws MiddlewareQueryException;

}
