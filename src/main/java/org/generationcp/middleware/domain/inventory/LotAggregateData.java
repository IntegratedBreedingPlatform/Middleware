package org.generationcp.middleware.domain.inventory;

import java.io.Serializable;
import java.util.Map;

/**
 * This POJO stores aggregate information about lot such as
 * actual balance, available balance, total reserved etc
 * @author Darla Ani
 *
 */
public class LotAggregateData implements Serializable {
	
	private static final long serialVersionUID = 438412738380543621L;

	private Integer lotId;
	
	private Double actualBalance;
	
	private Double availableBalance;
	
	private Double reservedTotal;

	// key = record id and value = reserved quantity
	private Map<Integer, Double> reservationMap;

	public LotAggregateData(Integer lotId) {
		super();
		this.lotId = lotId;
	}

	public Integer getLotId() {
		return lotId;
	}

	public void setLotId(Integer lotId) {
		this.lotId = lotId;
	}

	public Double getActualBalance() {
		return actualBalance;
	}

	public void setActualBalance(Double actualBalance) {
		this.actualBalance = actualBalance;
	}

	public Double getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(Double availableBalance) {
		this.availableBalance = availableBalance;
	}

	public Double getReservedTotal() {
		return reservedTotal;
	}

	public void setReservedTotal(Double reservedTotal) {
		this.reservedTotal = reservedTotal;
	}
	
	public Map<Integer, Double> getReservationMap() {
		return reservationMap;
	}

	public void setReservationMap(Map<Integer, Double> reservationMap) {
		this.reservationMap = reservationMap;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LotAggregateData [lotId=");
		builder.append(lotId);
		builder.append(", actualBalance=");
		builder.append(actualBalance);
		builder.append(", availableBalance=");
		builder.append(availableBalance);
		builder.append(", reservedTotal=");
		builder.append(reservedTotal);
		if (reservationMap != null && reservationMap.size() > 0){
        	builder.append(", reservationMap={");
        	for (Integer id : reservationMap.keySet()){
        		builder.append("Id:");
        		builder.append(id);
        		builder.append("->");
        		builder.append("Qty:");
        		builder.append(reservationMap.get(id));
        	}
        }
		builder.append("]");
		return builder.toString();
	}
	
	

}
