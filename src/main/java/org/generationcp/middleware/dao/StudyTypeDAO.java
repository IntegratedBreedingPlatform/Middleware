package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.dms.StudyType;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class StudyTypeDAO extends GenericDAO<StudyType, Integer> {

	public StudyTypeDAO(final Session session) {
		super(session);
	}

	public List<StudyType> getAllVisibleStudyTypes() {
		final Criteria criteria = this.getSession().createCriteria(StudyType.class);
		criteria.add(Restrictions.eq("visible", true));
		return criteria.list();
	}

	public StudyType getStudyTypeByName(final String name) {
		final Criteria criteria = this.getSession().createCriteria(StudyType.class);
		criteria.add(Restrictions.eq("name", name));
		if (!criteria.list().isEmpty()) {
			return (StudyType) criteria.list().get(0);
		}
		return null;
	}

	public StudyType getStudyTypeByLabel(final String label) {
		final Criteria criteria = this.getSession().createCriteria(StudyType.class);
		criteria.add(Restrictions.eq("label", label));
		if (!criteria.list().isEmpty()) {
			return (StudyType) criteria.list().get(0);
		}
		return null;
	}
}
