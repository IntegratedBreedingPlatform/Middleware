package org.generationcp.middleware.domain.inventory;

public class ListEntryLotDetails extends LotDetails implements Cloneable {

	private static final long serialVersionUID = -4418453030620877249L;

	private Integer id;

	private Double reservedTotalForEntry;

	private Double reservedTotalForOtherEntries;

	private Double committedTotalForEntry;

	private Double committedTotalForOtherEntries;

	public ListEntryLotDetails() {
	}

	public ListEntryLotDetails(Integer id) {
		super();
		this.id = id;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getReservedTotalForEntry() {
		return this.reservedTotalForEntry;
	}

	public void setReservedTotalForEntry(Double reservedTotalForEntry) {
		this.reservedTotalForEntry = reservedTotalForEntry;
	}

	public Double getReservedTotalForOtherEntries() {
		return this.reservedTotalForOtherEntries;
	}

	public void setReservedTotalForOtherEntries(Double reservedTotalForOtherEntries) {
		this.reservedTotalForOtherEntries = reservedTotalForOtherEntries;
	}

	public Double getCommittedTotalForEntry() {
		return committedTotalForEntry;
	}

	public void setCommittedTotalForEntry(Double committedTotalForEntry) {
		this.committedTotalForEntry = committedTotalForEntry;
	}

	public Double getCommittedTotalForOtherEntries() {
		return committedTotalForOtherEntries;
	}

	public void setCommittedTotalForOtherEntries(Double committedTotalForOtherEntries) {
		this.committedTotalForOtherEntries = committedTotalForOtherEntries;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ListEntryLotReportRow [id=");
		builder.append(this.getId());
		builder.append(", lotId=");
		builder.append(this.getLotId());
		builder.append(", entityIdOfLot=");
		builder.append(this.getEntityIdOfLot());
		builder.append(", actualLotBalance=");
		builder.append(this.getActualLotBalance());
		builder.append(", availableLotBalance=");
		builder.append(this.getAvailableLotBalance());
		builder.append(", reservedTotal=");
		builder.append(this.getReservedTotal());
		builder.append(", locationId=");
		builder.append(this.getLocId());
		builder.append(", locationOfLot=");
		builder.append(this.getLocationOfLot());
		builder.append(", scaleId=");
		builder.append(this.getScaleId());
		builder.append(", scaleOfLot=");
		builder.append(this.getScaleOfLot());
		builder.append(", commentOfLot=");
		builder.append(this.getCommentOfLot());
		builder.append(", reservedTotalForEntry=");
		builder.append(this.reservedTotalForEntry);
		builder.append(", reservedTotalForOtherEntries=");
		builder.append(this.reservedTotalForOtherEntries);
		builder.append("]");
		return builder.toString();
	}

	public ListEntryLotDetails makeClone() {
		try {
			return (ListEntryLotDetails) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
