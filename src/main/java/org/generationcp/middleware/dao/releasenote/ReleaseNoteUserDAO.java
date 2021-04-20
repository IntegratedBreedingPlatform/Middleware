package org.generationcp.middleware.dao.releasenote;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNoteUser;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Optional;

public class ReleaseNoteUserDAO extends GenericDAO<ReleaseNoteUser, Integer> {

	public ReleaseNoteUserDAO(final Session session) {
		super(session);
	}

	public Optional<ReleaseNoteUser> getByReleaseNoteIdAndUserId(final Integer releaseNoteId, final Integer userId) {

		final Criteria criteria = this.getSession().createCriteria(ReleaseNoteUser.class);
		criteria.add(Restrictions.eq("releaseNote.id", releaseNoteId));
		criteria.add(Restrictions.eq("user.userid", userId));

		final ReleaseNoteUser releaseNoteUser = (ReleaseNoteUser) criteria.uniqueResult();
		return Optional.ofNullable(releaseNoteUser);
	}

}
