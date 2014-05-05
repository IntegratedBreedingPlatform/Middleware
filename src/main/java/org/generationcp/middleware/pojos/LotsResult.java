package org.generationcp.middleware.pojos;

import java.util.List;

public class LotsResult {

	private List<Integer> lotIdsAdded;
	private List<Integer> gidsSkipped;
	private List<Integer> gidsProcessed;
	
	public List<Integer> getLotIdsAdded() {
		return lotIdsAdded;
	}
	public void setLotIdsAdded(List<Integer> lotIdsAdded) {
		this.lotIdsAdded = lotIdsAdded;
	}
	public List<Integer> getGidsSkipped() {
		return gidsSkipped;
	}
	public void setGidsSkipped(List<Integer> gidsSkipped) {
		this.gidsSkipped = gidsSkipped;
	}
	public List<Integer> getGidsProcessed() {
		return gidsProcessed;
	}
	public void setGidsProcessed(List<Integer> gidsProcessed) {
		this.gidsProcessed = gidsProcessed;
	}
	
}
