package org.generationcp.middleware.pojos.workbench;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "r_call_parameter")
public class RCallParameter {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "call_parameter_id", nullable = false)
	private Integer id;

	@Column(name = "parameter_key")
	private String key;

	@Column(name = "value")
	private String value;

	@Column(name = "call_id")
	private Integer callId;

	public RCallParameter() {

	}

	public RCallParameter(final int id, final String key, final String value) {
		this.id = id;
		this.key = key;
		this.value = value;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(final String key) {
		this.key = key;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public Integer getCallId() {
		return this.callId;
	}

	public void setCallId(final Integer callId) {
		this.callId = callId;
	}

}
