
package org.generationcp.middleware.pojos.ims;

public class ReservedInventoryKey {

	Integer id; // this is just the count of the reservedInventoryEntry
	Integer lrecId; // lrecId in List
	Integer lotId;

	public ReservedInventoryKey(Integer id, Integer lrecId, Integer lotId) {
		super();
		this.id = id;
		this.lrecId = lrecId;
		this.lotId = lotId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getLrecId() {
		return this.lrecId;
	}

	public void setLrecId(Integer lrecId) {
		this.lrecId = lrecId;
	}

	public Integer getLotId() {
		return this.lotId;
	}

	public void setLotId(Integer lotId) {
		this.lotId = lotId;
	}

	@Override
	public boolean equals(Object obj) {
		ReservedInventoryKey toCompare = (ReservedInventoryKey) obj;
		if (this.getLotId() == toCompare.getLotId() && this.getLrecId() == toCompare.getLrecId()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.lotId == null ? 0 : this.lotId.hashCode());
		result = prime * result + (this.lrecId == null ? 0 : this.lrecId.hashCode());
		return result;
	}

}
