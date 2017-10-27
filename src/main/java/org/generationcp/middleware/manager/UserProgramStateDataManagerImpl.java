
package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.api.UserProgramStateDataManager;
import org.generationcp.middleware.pojos.UserProgramTreeState;
import org.generationcp.middleware.util.Util;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by cyrus on 12/16/14.
 */
@Transactional
public class UserProgramStateDataManagerImpl extends DataManager implements UserProgramStateDataManager {

	public UserProgramStateDataManagerImpl(HibernateSessionProvider sessionProvider) {
		super(sessionProvider);
	}

	public UserProgramStateDataManagerImpl() {
		super();
	}

	@Override
	public List<String> getUserProgramTreeStateByUserIdProgramUuidAndType(int userId, String programUuid, String type)
			throws MiddlewareQueryException {
		List<String> treeState = new ArrayList<>();
		try {
			UserProgramTreeState userProgramTreeState = this.getUserProgramTreeState(userId, programUuid, type);
			if (userProgramTreeState != null) {
				treeState = new ArrayList<String>(Arrays.asList(userProgramTreeState.getTreeState().split(",")));
			}
		} catch (HibernateException e) {
			this.logAndThrowException("error in: WorkbenchDataManager.getUserProgramTreeStateByUserIdProgramUuidAndType(programId="
					+ programUuid + "): " + e.getMessage(), e);
		}

		return treeState;
	}

	private UserProgramTreeState getUserProgramTreeState(int userId, String programUuid, String type) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getCurrentSession().createCriteria(UserProgramTreeState.class);

			criteria.add(Restrictions.eq("userId", userId));
			criteria.add(Restrictions.eq("programUuid", programUuid));
			criteria.add(Restrictions.eq("treeType", type));

			@SuppressWarnings("unchecked")
			List<UserProgramTreeState> userProgramTreeStates = criteria.list();
			if (!userProgramTreeStates.isEmpty()) {
				return userProgramTreeStates.get(0);
			}
		} catch (HibernateException e) {
			this.logAndThrowException("error in: WorkbenchDataManager.getUserProgramTreeStateByUserIdProgramUuidAndType(programId="
					+ programUuid + "): " + e.getMessage(), e);
		}

		return null;
	}

	@Override
	public UserProgramTreeState saveOrUpdateUserProgramTreeState(int userId, String programUuid, String type, List<String> treeState)
			throws MiddlewareQueryException {
		UserProgramTreeState userProgramTreeState = null;
		
		
		try {

			userProgramTreeState = this.getUserProgramTreeState(userId, programUuid, type);
			if (userProgramTreeState == null) {
				userProgramTreeState = new UserProgramTreeState();
				userProgramTreeState.setUserId(userId);
				userProgramTreeState.setProgramUuid(programUuid);
				userProgramTreeState.setTreeType(type);
			}
			String text = Util.convertCollectionToCSV(treeState);
			userProgramTreeState.setTreeState(text);
			this.getUserProgramTreeStateDAO().saveOrUpdate(userProgramTreeState);

		} catch (HibernateException e) {

			this.logAndThrowException("error in: WorkbenchDataManager.saveOrUpdateUserProgramTreeState(programId=" + programUuid + "): "
					+ e.getMessage(), e);
		}

		return userProgramTreeState;
	}

}
