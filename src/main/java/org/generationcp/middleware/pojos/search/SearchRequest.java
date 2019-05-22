
package org.generationcp.middleware.pojos.search;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@AutoProperty
@Entity
@Table(name = "search_request")
public class SearchRequest {

	private int requestId;
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
	@Column(name = "parameters")
	public String getParameters() {
		return this.parameters;
	}

	public void setParameters(final String parameters) {
		this.parameters = parameters;
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
