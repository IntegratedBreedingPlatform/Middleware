/*******************************************************************************
 * Copyright (c) 2014, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.pojos.ims;

import java.util.List;

/**
 * The Class LotsResult.
 */
public class LotsResult {

	private List<Integer> lotIdsAdded;
	
	private List<Integer> lotIdsUpdated;
	
	private List<Integer> gidsUpdated;
	
	private List<Integer> gidsAdded;
	
	public List<Integer> getLotIdsAdded() {
		return lotIdsAdded;
	}

	public void setLotIdsAdded(List<Integer> lotIdsAdded) {
		this.lotIdsAdded = lotIdsAdded;
	}

	public List<Integer> getLotIdsUpdated() {
		return lotIdsUpdated;
	}

	public void setLotIdsUpdated(List<Integer> lotIdsUpdated) {
		this.lotIdsUpdated = lotIdsUpdated;
	}

	public List<Integer> getGidsUpdated() {
		return gidsUpdated;
	}

	public void setGidsUpdated(List<Integer> gidsUpdated) {
		this.gidsUpdated = gidsUpdated;
	}

	public List<Integer> getGidsAdded() {
		return gidsAdded;
	}

	public void setGidsAdded(List<Integer> gidsAdded) {
		this.gidsAdded = gidsAdded;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LotsResult [lotIdsAdded=");
		builder.append(lotIdsAdded);
		builder.append(", lotIdsUpdated=");
		builder.append(lotIdsUpdated);
		builder.append(", gidsUpdated=");
		builder.append(gidsUpdated);
		builder.append(", gidsAdded=");
		builder.append(gidsAdded);
		builder.append("]");
		return builder.toString();
	}
	
}
