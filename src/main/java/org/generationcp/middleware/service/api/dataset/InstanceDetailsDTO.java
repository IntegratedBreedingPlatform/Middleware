package org.generationcp.middleware.service.api.dataset;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class InstanceDetailsDTO {

	private Long environment;
	private Long nOfObservations;
	private Long nOfEntries;
	private Long nOfReps;

	public void InstanceDetailsDTO(){

	}

	public void InstanceDetailsDTO(final Long environment, final Long nOfObservations, final Long nOfEntries, final Long nOfReps) {
		this.environment = environment;
		this.nOfObservations = nOfObservations;
		this.nOfEntries = nOfEntries;
		this.nOfReps = nOfReps;
	}

	public Long getEnvironment() {
		return environment;
	}

	public void setEnvironment(final Long environment) {
		this.environment = environment;
	}

	public Long getnOfObservations() {
		return nOfObservations;
	}

	public void setnOfObservations(final Long nOfObservations) {
		this.nOfObservations = nOfObservations;
	}

	public Long getnOfEntries() {
		return nOfEntries;
	}

	public void setnOfEntries(final Long nOfEntries) {
		this.nOfEntries = nOfEntries;
	}

	public Long getnOfReps() {
		return nOfReps;
	}

	public void setnOfReps(final Long nOfReps) {
		this.nOfReps = nOfReps;
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
