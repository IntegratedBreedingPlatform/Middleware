package org.generationcp.middleware.pojos.workbench;

import org.generationcp.middleware.pojos.AbstractEntity;
import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

@Entity
@Table(name = "crop_genotyping_parameter")
@AutoProperty
public class CropGenotypingParameter implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "crop_genotyping_parameter_id")
	private int cropGenotypingParameterId;

	@Column(name = "crop_name")
	private String cropName;

	@Column(name = "endpoint")
	private String endpoint;

	@Column(name = "token_endpoint")
	private String tokenEndpoint;

	@Column(name = "username")
	private String userName;

	@Column(name = "program_id")
	private String programId;

	@Transient
	private String password;

	public int getCropGenotypingParameterId() {
		return this.cropGenotypingParameterId;
	}

	public void setCropGenotypingParameterId(final int cropGenotypingParameterId) {
		this.cropGenotypingParameterId = cropGenotypingParameterId;
	}

	public String getCropName() {
		return this.cropName;
	}

	public void setCropName(final String cropName) {
		this.cropName = cropName;
	}

	public String getEndpoint() {
		return this.endpoint;
	}

	public void setEndpoint(final String endpoint) {
		this.endpoint = endpoint;
	}

	public String getTokenEndpoint() {
		return this.tokenEndpoint;
	}

	public void setTokenEndpoint(final String tokenEndpoint) {
		this.tokenEndpoint = tokenEndpoint;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public String getProgramId() {
		return this.programId;
	}

	public void setProgramId(final String programId) {
		this.programId = programId;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
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
