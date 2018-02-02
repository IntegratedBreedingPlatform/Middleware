package org.generationcp.middleware.service.api.phenotype;

import java.io.Serializable;
import java.util.List;

public class PhenotypeSearchRequestDTO implements Serializable {

	private List<Integer> cvTermIds;

	public List<Integer> getCvTermIds() {
		return cvTermIds;
	}

	public void setCvTermIds(List<Integer> cvTermIds) {
		this.cvTermIds = cvTermIds;
	}
}
