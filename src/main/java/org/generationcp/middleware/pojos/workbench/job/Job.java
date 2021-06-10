package org.generationcp.middleware.pojos.workbench.job;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "job")
public class Job implements Serializable {

	private static final long serialVersionUID = -3896953440301323065L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private Integer id;

	@Column(name = "qrtz_job_id", nullable = false, updatable = false)
	private String quartzJobId;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private JobStatus status;

	@Column(name = "started_date")
	private Date startedDate;

	@Column(name = "completed_date")
	private Date completedDate;

	private Job() {
	}

	public Job(final String quartzJobId) {
		this.quartzJobId = quartzJobId;
		this.status = JobStatus.PENDING;
	}

	public Integer getId() {
		return id;
	}

	public String getQuartzJobId() {
		return quartzJobId;
	}

	public JobStatus getStatus() {
		return status;
	}

	public Date getStartedDate() {
		return startedDate;
	}

	public Date getCompletedDate() {
		return completedDate;
	}

	public void markAsExecuting() {
		this.startedDate = new Date();
		this.status = JobStatus.EXECUTING;
	}

	public void markAsCompleted() {
		this.completedDate = new Date();
		this.status = JobStatus.COMPLETED;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.id).hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof WorkbenchUser)) {
			return false;
		}

		final Job otherObj = (Job) obj;

		return new EqualsBuilder().append(this.id, otherObj.id).isEquals();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ReleaseNote [id=");
		builder.append(this.id);
		builder.append(", quartzJobId=");
		builder.append(this.quartzJobId);
		builder.append(", status=");
		builder.append(this.status);
		builder.append("]");
		return builder.toString();
	}

}
