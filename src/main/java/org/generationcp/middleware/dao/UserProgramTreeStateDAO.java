package org.generationcp.middleware.dao;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.UserProgramTreeState;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class UserProgramTreeStateDAO extends GenericDAO<UserProgramTreeState,Integer> {

	public UserProgramTreeState getUserProgramTreeState(int userId, String programUuid, String type) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(UserProgramTreeState.class);

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

}
