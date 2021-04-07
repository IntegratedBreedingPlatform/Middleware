package org.generationcp.middleware.dao.releasenote;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNotePerson;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Optional;

public class ReleaseNotePersonDAO extends GenericDAO<ReleaseNotePerson, Integer> {

	public ReleaseNotePersonDAO(final Session session) {
		super(session);
	}

	public Optional<ReleaseNotePerson> getByReleaseNoteIdAndPersonId(final Integer releaseNoteId, final Integer personId) {

		final Criteria criteria = this.getSession().createCriteria(ReleaseNotePerson.class);
		criteria.add(Restrictions.eq("releaseNote.id", releaseNoteId));
		criteria.add(Restrictions.eq("person.id", personId));

		final ReleaseNotePerson releaseNotePersonDAO = (ReleaseNotePerson) criteria.uniqueResult();
		return Optional.ofNullable(releaseNotePersonDAO);
	}

}
