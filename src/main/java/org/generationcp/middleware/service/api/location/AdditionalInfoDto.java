
package org.generationcp.middleware.service.api.location;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AdditionalInfoDto implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4902812852565213456L;
	
	private Integer locationId;
	
	private HashMap<String, String> ladditionalInfo;

	public AdditionalInfoDto(final String field, final String value) {
		ladditionalInfo.put(field, value);
	}

//	public AdditionalInfoDto() {
//		ladditionalInfo = new HashMap<String, String>();
//	}

	public AdditionalInfoDto(Integer locationId) {
		ladditionalInfo = new HashMap<String, String>();
		this.setId(locationId);
	}

	public void addInfo(final String field, final String value) {
		ladditionalInfo.put(field, value);
	}

	public String getInfoValue(final String field) {
		return ladditionalInfo.get(field);
	}

	public Map<String, String> getToMap() {
		return this.ladditionalInfo;
	}

	public void setId(Integer locationId) {
		this.locationId = locationId;
	}

	public Integer getId() {
		return this.locationId;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.ladditionalInfo).hashCode();
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof AdditionalInfoDto)) {
			return false;
		}
		final AdditionalInfoDto castOther = (AdditionalInfoDto) other;
		return new EqualsBuilder().append(this.getId(), castOther.getId()).isEquals();
	}

}
