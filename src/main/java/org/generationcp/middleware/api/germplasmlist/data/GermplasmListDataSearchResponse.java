package org.generationcp.middleware.api.germplasmlist.data;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

import java.util.Map;

@AutoProperty
public class GermplasmListDataSearchResponse {

	private Integer listDataId;

	private Map<String, Object> data;

	public Integer getListDataId() {
		return listDataId;
	}

	public void setListDataId(final Integer listDataId) {
		this.listDataId = listDataId;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(final Map<String, Object> data) {
		this.data = data;
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
