package org.generationcp.middleware.service.api.dataset;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

@AutoProperty
public class InstanceInfomationDTO {

	private Long environment;
	private Long nOfObservations;
	private Long nOfEntries;
	private Long nOfReps;


	public void InstanceInfomationDTO(){

	}

	public void InstanceInfomationDTO(final Long environment, final Long nOfObservations, final Long nOfEntries, final Long nOfReps) {
		this.environment = environment;
		this.nOfObservations = nOfObservations;
		this.nOfEntries = nOfEntries;
		this.nOfReps = nOfReps;
	}

	public Long getEnvironment() {
		return environment;
	}

	public InstanceInfomationDTO setEnvironment(final Long environment) {
		this.environment = environment;
		return this;
	}

	public Long getnOfObservations() {
		return nOfObservations;
	}

	public InstanceInfomationDTO setnOfObservations(final Long nOfObservations) {
		this.nOfObservations = nOfObservations;
		return this;
	}

	public Long getnOfEntries() {
		return nOfEntries;
	}

	public InstanceInfomationDTO setnOfEntries(final Long nOfEntries) {
		this.nOfEntries = nOfEntries;
		return this;
	}

	public Long getnOfReps() {
		return nOfReps;
	}

	public InstanceInfomationDTO setnOfReps(final Long nOfReps) {
		this.nOfReps = nOfReps;
		return this;
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
