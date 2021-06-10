package org.generationcp.middleware.dao.job;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.pojos.workbench.job.Job;
import org.generationcp.middleware.pojos.workbench.releasenote.ReleaseNote;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.Optional;

public class JobDAO extends GenericDAO<Job, Integer> {

	public JobDAO(final Session session) {
		super(session);
	}

	public Job getByQuartzJobId(final String quartzJobId) {
		final Criteria criteria = this.getSession().createCriteria(Job.class);
		criteria.add(Restrictions.eq("quartzJobId", quartzJobId));
		return (Job) criteria.uniqueResult();
	}

	public Optional<ReleaseNote> getLatestByMajorVersion(final String majorVersion) {
		final Criteria criteria = this.getSession().createCriteria(ReleaseNote.class);
		criteria.add(Restrictions.like("version", majorVersion, MatchMode.START));
		criteria.add(Restrictions.eq("enabled", true));
		criteria.add(Restrictions.le("releaseDate", new Date()));
		criteria.addOrder(Order.desc("releaseDate"));
		criteria.setMaxResults(1);

		final ReleaseNote releaseNote = (ReleaseNote) criteria.uniqueResult();
		return Optional.ofNullable(releaseNote);
	}

}
