package org.generationcp.middleware.dao.releasenote;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNote;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.Optional;

public class ReleaseNoteDAO extends GenericDAO<ReleaseNote, Integer> {

	public ReleaseNoteDAO(final Session session) {
		super(session);
	}

	public Optional<ReleaseNote> getLatestReleaseNote() {
		final Criteria criteria = this.getSession().createCriteria(ReleaseNote.class);
		criteria.add(Restrictions.le("releaseDate", new Date()));
		criteria.addOrder(Order.desc("releaseDate"));
		criteria.setMaxResults(1);

		final ReleaseNote releaseNote = (ReleaseNote) criteria.uniqueResult();
		return Optional.ofNullable(releaseNote);
	}

}
