package org.generationcp.middleware.service.impl.job;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.job.Job;
import org.generationcp.middleware.service.api.job.JobService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class JobServiceImpl implements JobService {

	private final WorkbenchDaoFactory workbenchDaoFactory;

	public JobServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public Job create(final String quartzJobId) {
		final Job job = new Job(quartzJobId);
		return this.workbenchDaoFactory.getJobDAO().save(job);
	}

	@Override
	public Job getByQuartzJobId(final String quartzJobId) {
		return this.workbenchDaoFactory.getJobDAO().getByQuartzJobId(quartzJobId);
	}

	@Override
	public void markAsExecuting(final String quartzJobId) {
		final Job job = this.getByQuartzJobId(quartzJobId);
		job.markAsExecuting();
		this.workbenchDaoFactory.getJobDAO().save(job);
	}

	@Override
	public void markAsCompleted(final String quartzJobId) {
		final Job job = this.getByQuartzJobId(quartzJobId);
		job.markAsCompleted();
		this.workbenchDaoFactory.getJobDAO().save(job);
	}

}
