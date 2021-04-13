
package org.generationcp.middleware.manager;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.UserProgramTreeState;
import org.generationcp.middleware.util.Util;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cyrus on 12/16/14.
 */
@Transactional
public class UserProgramStateDataManagerImpl implements UserProgramStateDataManager {

	private DaoFactory daoFactory;

	public UserProgramStateDataManagerImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public UserProgramStateDataManagerImpl() {
		super();
	}

	@Override
	public List<String> getUserProgramTreeState(final int userId, final String programUuid, final String type) {
		List<String> treeState = new ArrayList<>();
		final UserProgramTreeState userProgramTreeState = this.daoFactory.getUserProgramTreeStateDAO().getUserProgramTreeState(userId, programUuid, type);
		if (userProgramTreeState != null) {
			treeState = new ArrayList<>(Arrays.asList(userProgramTreeState.getTreeState().split(",")));
		}

		return treeState;
	}

	@Override
	public UserProgramTreeState saveOrUpdateUserProgramTreeState(final int userId, final String programUuid, final String type, final List<String> treeState) {
		UserProgramTreeState userProgramTreeState = this.daoFactory.getUserProgramTreeStateDAO().getUserProgramTreeState(userId, programUuid, type);
		if (userProgramTreeState == null) {
			userProgramTreeState = new UserProgramTreeState();
			userProgramTreeState.setUserId(userId);
			userProgramTreeState.setProgramUuid(programUuid);
			userProgramTreeState.setTreeType(type);
		}
		final String text = Util.convertCollectionToCSV(treeState);
		userProgramTreeState.setTreeState(text);
		this.daoFactory.getUserProgramTreeStateDAO().saveOrUpdate(userProgramTreeState);

		return userProgramTreeState;
	}

}
