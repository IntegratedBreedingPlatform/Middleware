package org.generationcp.middleware.dao;

import liquibase.util.StringUtils;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.UserProgramTreeState;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class UserProgramTreeStateDAO extends GenericDAO<UserProgramTreeState,Integer> {

	public UserProgramTreeState getUserProgramTreeState(final int userId, final String programUuid, final String type) throws MiddlewareQueryException {
		try {
			final Criteria criteria = this.getSession().createCriteria(UserProgramTreeState.class);

			criteria.add(Restrictions.eq( "userId", userId));
			if (!StringUtils.isEmpty(programUuid)) {
				criteria.add(Restrictions.eq("programUuid", programUuid));
			} else {
				criteria.add(Restrictions.isNull("programUuid"));
			}
			criteria.add(Restrictions.eq("treeType", type));

			@SuppressWarnings("unchecked") final List<UserProgramTreeState> userProgramTreeStates = criteria.list();
			if (!userProgramTreeStates.isEmpty()) {
				return userProgramTreeStates.get(0);
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("error in: WorkbenchDataManager.getUserProgramTreeStateByUserIdProgramUuidAndType(programId="
				+ programUuid + "): " + e.getMessage(), e);
		}

		return null;
	}

}
