package org.generationcp.middleware.domain.dms;

public abstract class InstanceVariableData {

	protected Integer instanceId;
	protected Integer variableId;
	protected String value;
	protected Integer categoricalValueId;

	public Integer getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(final Integer instanceId) {
		this.instanceId = instanceId;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public void setVariableId(final Integer variableId) {
		this.variableId = variableId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getCategoricalValueId() {
		return categoricalValueId;
	}

	public void setCategoricalValueId(final Integer categoricalValueId) {
		this.categoricalValueId = categoricalValueId;
	}

}
