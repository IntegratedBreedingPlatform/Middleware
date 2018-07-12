package org.generationcp.middleware.pojos.workbench;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;


@SuppressWarnings("UnqualifiedFieldAccess")
@Entity
@Table(name = "brapi_calls", schema = "workbench")
public class BrapiCall {

	@Id
	@Column(name = "call_name")
	private String call;

	@Column(name = "data_types")
	private String datatypes;

	@Column(name = "methods")
	private String methods;

	@Column(name = "versions")
	private String versions;

	public BrapiCall(final String call, final String datatypes, final String methods, final String versions) {
		this.call = call;
		this.datatypes = datatypes;
		this.methods = methods;
		this.versions = versions;
	}

	public BrapiCall() {

	}

	public String getCall() {
		return this.call;
	}

	public void setCall(final String call) {
		this.call = call;
	}

	public String getDatatypes() {
		return this.datatypes;
	}

	public void setDatatypes(final String datatypes) {
		this.datatypes = datatypes;
	}

	public String getMethods() {
		return this.methods;
	}

	public void setMethods(final String methods) {
		this.methods = methods;
	}

	public String getVersions() {
		return this.versions;
	}

	public void setVersions(final String versions) {
		this.versions = versions;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o)
			return true;
		if (o == null || this.getClass() != o.getClass())
			return false;
		final BrapiCall calls = (BrapiCall) o;
		return Objects.equals(this.call, calls.call) &&
			Objects.equals(this.datatypes, calls.datatypes) &&
			Objects.equals(this.methods, calls.methods) &&
			Objects.equals(this.versions, calls.versions);
	}

	@Override
	public int hashCode() {

		return Objects.hash(this.call, this.datatypes, this.methods, this.versions);
	}

	@Override
	public String toString() {
		return "BrapiCall{" +
			"call='" + this.call + '\'' +
			", datatypes='" + this.datatypes + '\'' +
			", methods='" + this.methods + '\'' +
			", versions='" + this.versions + '\'' +
			'}';
	}
}
