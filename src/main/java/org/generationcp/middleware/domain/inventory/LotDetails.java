package org.generationcp.middleware.domain.inventory;

import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.pojos.Location;

import java.io.Serializable;

public class LotDetails implements Serializable{
	
	private static final long serialVersionUID = 2572260467983831666L;

	// ims_lot fields
    private Integer lotId;
    private Integer entityIdOfLot;
    private Integer scaleId;
    private Integer locId;
    private String commentOfLot;
    
    // computed or looked up values
    private Double actualLotBalance; 
    private Double availableLotBalance;
    private Location locationOfLot;
    private Double reservedTotal;
    private Term scaleOfLot;

	public Integer getLotId() {
        return lotId;
    }

    public void setLotId(Integer lotId) {
        this.lotId = lotId;
    }

    public Integer getEntityIdOfLot() {
        return entityIdOfLot;
    }

    public void setEntityIdOfLot(Integer entityIdOfLot) {
        this.entityIdOfLot = entityIdOfLot;
    }

    public Double getActualLotBalance() {
        return actualLotBalance;
    }

    public void setActualLotBalance(Double actualLotBalance) {
        this.actualLotBalance = actualLotBalance;
    }

    public Location getLocationOfLot() {
        return locationOfLot;
    }

    public void setLocationOfLot(Location locationOfLot) {
        this.locationOfLot = locationOfLot;
    }

    public Term getScaleOfLot() {
        return scaleOfLot;
    }

    public void setScaleOfLot(Term scaleOfLot) {
        this.scaleOfLot = scaleOfLot;
    }
    
    public String getCommentOfLot() {
        return commentOfLot;
    }
    
    public void setCommentOfLot(String commentOfLot) {
        this.commentOfLot = commentOfLot;
    }

    public Double getAvailableLotBalance() {
		return availableLotBalance;
	}

	public void setAvailableLotBalance(Double availableLotBalance) {
		this.availableLotBalance = availableLotBalance;
	}

	public Double getReservedTotal() {
		return reservedTotal;
	}

	public void setReservedTotal(Double reservedTotal) {
		this.reservedTotal = reservedTotal;
	}

	public Integer getScaleId() {
		return scaleId;
	}

	public void setScaleId(Integer scaleId) {
		this.scaleId = scaleId;
	}

	public Integer getLocId() {
		return locId;
	}

	public void setLocId(Integer locId) {
		this.locId = locId;
	}

	@Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LotReportRow [lotId=");
        builder.append(lotId);
        builder.append(", entityIdOfLot=");
        builder.append(entityIdOfLot);
        builder.append(", actualLotBalance=");
        builder.append(actualLotBalance);
        builder.append(", availableLotBalance=");
        builder.append(availableLotBalance);
        builder.append(", reservedTotal=");
        builder.append(reservedTotal);
        builder.append(", locationId=");
        builder.append(locId);
        builder.append(", locationOfLot=");
        builder.append(locationOfLot);
        builder.append(", scaleId=");
        builder.append(scaleId);
        builder.append(", scaleOfLot=");
        builder.append(scaleOfLot);
        builder.append(", commentOfLot=");
        builder.append(commentOfLot);
        builder.append("]");
        return builder.toString();
    }

}
