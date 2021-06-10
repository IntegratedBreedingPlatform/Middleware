package org.generationcp.middleware.domain.job;

import org.generationcp.middleware.pojos.workbench.job.JobStatus;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

@AutoProperty
public class JobDTO {

	private Integer jobId;
	private String quartzJobId;
	private JobStatus jobStatus;
	private Date startedDate;
	private Date completedDate;

	public JobDTO(final Integer jobId, final String quartzJobId, final JobStatus jobStatus, final Date startedDate,
		final Date completedDate) {
		this.jobId = jobId;
		this.quartzJobId = quartzJobId;
		this.jobStatus = jobStatus;
		this.startedDate = startedDate;
		this.completedDate = completedDate;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(final Integer jobId) {
		this.jobId = jobId;
	}

	public String getQuartzJobId() {
		return quartzJobId;
	}

	public void setQuartzJobId(final String quartzJobId) {
		this.quartzJobId = quartzJobId;
	}

	public JobStatus getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(final JobStatus jobStatus) {
		this.jobStatus = jobStatus;
	}

	public Date getStartedDate() {
		return startedDate;
	}

	public void setStartedDate(final Date startedDate) {
		this.startedDate = startedDate;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void setCompletedDate(final Date completedDate) {
		this.completedDate = completedDate;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(final Object o) {
		return Pojomatic.equals(this, o);
	}

}
