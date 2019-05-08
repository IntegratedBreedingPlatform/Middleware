
package org.generationcp.middleware.pojos.search;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by cyrus on 12/19/14.
 */
@Entity
@Table(name = "search_request")
public class SearchRequest {

	private int requestId;
	private String requestType;
	private String parameters;

	@GeneratedValue
	@Id
	@Column(name = "request_id")
	public int getRequestId() {
		return this.requestId;
	}

	public void setRequestId(final int programPresetsId) {
		this.requestId = programPresetsId;
	}

	@Basic
	@Column(name = "request_type")
	public String getRequestType() {
		return this.requestType;
	}

	public void setRequestType(final String requestType) {
		this.requestType = requestType;
	}

	@Basic
	@Column(name = "parameters")
	public String getParameters() {
		return this.parameters;
	}

	public void setParameters(final String parameters) {
		this.parameters = parameters;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || this.getClass() != o.getClass()) {
			return false;
		}

		final SearchRequest that = (SearchRequest) o;

		if (this.requestId != that.requestId) {
			return false;
		}
		if (this.parameters != null ? !this.parameters.equals(that.parameters) : that.parameters != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = this.requestId;
		result = 31 * result + (this.requestType != null ? this.requestType.hashCode() : 0);
		result = 31 * result + (this.parameters != null ? this.parameters.hashCode() : 0);
		return result;
	}
}
