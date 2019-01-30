package org.generationcp.middleware.domain.sample;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Date;

/**
 * Created by clarysabel on 1/19/18.
 */
@AutoProperty
public class SampleObservationUnitDTO {

	private Integer id;

	private String plantNo;

	private Date createdDate;

	private String businessKey;

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getPlantNo() {
		return plantNo;
	}

	public void setPlantNo(final String plantNo) {
		this.plantNo = plantNo;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(final Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(final String businessKey) {
		this.businessKey = businessKey;
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
	public boolean equals(Object o) {
		return Pojomatic.equals(this, o);
	}

}
