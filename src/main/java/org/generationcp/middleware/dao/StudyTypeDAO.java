package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.dms.StudyType;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class StudyTypeDAO extends GenericDAO<StudyType, Integer> {

	public List<StudyType> getAllVisibleStudyTypes() {
		final Criteria criteria = this.getSession().createCriteria(StudyType.class);
		criteria.add(Restrictions.eq("visible", true));
		return criteria.list();
	}

	public StudyType getStudyTypeByName(final String name) {
		final Criteria criteria = this.getSession().createCriteria(StudyType.class);
		criteria.add(Restrictions.eq("name", name));
		return (StudyType) criteria.list().get(0);
	}

	public StudyType getStudyTypeByLabel(final String label) {
		final Criteria criteria = this.getSession().createCriteria(StudyType.class);
		criteria.add(Restrictions.eq("label", label));
		return (StudyType) criteria.list().get(0);
	}
}
