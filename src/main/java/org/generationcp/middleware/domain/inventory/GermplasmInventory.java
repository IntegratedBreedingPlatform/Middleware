package org.generationcp.middleware.domain.inventory;

import java.io.Serializable;
import java.util.List;

/**
 * POJO for storing aggregate inventory information and list of
 * lots associated with germplasm
 * 
 * @author Darla Ani
 *
 */
public class GermplasmInventory implements Serializable {

	private static final long serialVersionUID = -5519155457123841685L;
	
	private Integer gid;
	
	//number of lots with actual inventory available for given germplasm
	private Integer actualInventoryLotCount;
	
	//number of lots with reserved amount for given germplasm
	private Integer reservedLotCount;
	
	//list of lots for germplasm
	private List<? extends LotDetails> lotRows;
	
	
	public GermplasmInventory(Integer gid) {
		super();
		this.gid = gid;
	}

	public Integer getGid() {
		return gid;
	}

	public void setGid(Integer gid) {
		this.gid = gid;
	}

	public Integer getActualInventoryLotCount() {
		return actualInventoryLotCount;
	}

	public void setActualInventoryLotCount(Integer actualInventoryLotCount) {
		this.actualInventoryLotCount = actualInventoryLotCount;
	}

	public Integer getReservedLotCount() {
		return reservedLotCount;
	}

	public void setReservedLotCount(Integer reservedLotCount) {
		this.reservedLotCount = reservedLotCount;
	}

	public List<? extends LotDetails> getLotRows() {
		return lotRows;
	}

	public void setLotRows(List<? extends LotDetails> lotRows) {
		this.lotRows = lotRows;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GermplasmInventory [");
		builder.append("gid=");
		builder.append(gid);
		builder.append(", actualInventoryLotCount=");
		builder.append(actualInventoryLotCount);
		builder.append(", reservedLotCount=");
		builder.append(reservedLotCount);
		if (lotRows != null){
			builder.append(", lotCount = ");
			builder.append(lotRows.size());
			builder.append(", lots={");
			for (LotDetails lot : lotRows) {
				builder.append(lot);
			}
			builder.append("}");
		}
		builder.append("]");
		return builder.toString();
	}

}
