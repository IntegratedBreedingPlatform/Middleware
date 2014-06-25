package org.generationcp.middleware.domain.inventory;

public class ListEntryLotDetails extends LotDetails implements Cloneable {
private static final long serialVersionUID = -4418453030620877249L;
	
	private Integer id;
	
	private Double reservedTotalForEntry;
	
	private Double reservedTotalForOtherEntries;
	
	public ListEntryLotDetails(){
	}

	public ListEntryLotDetails(Integer id) {
		super();
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getReservedTotalForEntry() {
		return reservedTotalForEntry;
	}

	public void setReservedTotalForEntry(Double reservedTotalForEntry) {
		this.reservedTotalForEntry = reservedTotalForEntry;
	}

	public Double getReservedTotalForOtherEntries() {
		return reservedTotalForOtherEntries;
	}

	public void setReservedTotalForOtherEntries(Double reservedTotalForOtherEntries) {
		this.reservedTotalForOtherEntries = reservedTotalForOtherEntries;
	}
	
	@Override
	public String toString() {
		 StringBuilder builder = new StringBuilder();
	        builder.append("ListEntryLotReportRow [id=");
	        builder.append(getId());
	        builder.append(", lotId=");
	        builder.append(getLotId());
	        builder.append(", entityIdOfLot=");
	        builder.append(getEntityIdOfLot());
	        builder.append(", actualLotBalance=");
	        builder.append(getActualLotBalance());
	        builder.append(", availableLotBalance=");
	        builder.append(getAvailableLotBalance());
	        builder.append(", reservedTotal=");
	        builder.append(getReservedTotal());
	        builder.append(", locationId=");
	        builder.append(getLocId());
	        builder.append(", locationOfLot=");
	        builder.append(getLocationOfLot());
	        builder.append(", scaleId=");
	        builder.append(getScaleId());
	        builder.append(", scaleOfLot=");
	        builder.append(getScaleOfLot());
	        builder.append(", commentOfLot=");
	        builder.append(getCommentOfLot());
	        builder.append(", reservedTotalForEntry=");
	        builder.append(reservedTotalForEntry);
	        builder.append(", reservedTotalForOtherEntries=");
	        builder.append(reservedTotalForOtherEntries);
	        builder.append("]");
	        return builder.toString();
	}

	public ListEntryLotDetails makeClone(){
        try{
            return (ListEntryLotDetails) super.clone();
        }catch( CloneNotSupportedException e ){
            return null;
        }
    } 
	
}
