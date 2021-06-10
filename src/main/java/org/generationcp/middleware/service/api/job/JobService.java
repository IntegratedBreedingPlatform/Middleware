package org.generationcp.middleware.service.api.job;

import org.generationcp.middleware.pojos.workbench.job.Job;

public interface JobService {

	Job create(String quartzJobId);

	Job getByQuartzJobId(String quartzJobId);

	void markAsExecuting(String quartzJobId);

	void markAsCompleted(String quartzJobId);

}
