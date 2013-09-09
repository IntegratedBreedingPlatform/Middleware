/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.domain.etl;

import java.util.List;

import org.generationcp.middleware.util.Debug;

public class MeasurementRow {

	private long stockId;
	
	private long locationId;
	
	private List<MeasurementData> dataList;
	
	public MeasurementRow() {
	}

	public MeasurementRow(long stockId, long locationId, List<MeasurementData> dataList) {
		this.stockId = stockId;
		this.locationId = locationId;
		this.dataList = dataList;
	}

	public long getStockId() {
		return stockId;
	}

	public void setStockId(long stockId) {
		this.stockId = stockId;
	}

	public long getLocationId() {
		return locationId;
	}

	public void setLocationId(long locationId) {
		this.locationId = locationId;
	}

	public List<MeasurementData> getDataList() {
		return dataList;
	}

	public void setDataList(List<MeasurementData> dataList) {
		this.dataList = dataList;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MeasurementRow [stockId=");
		builder.append(stockId);
		builder.append(", locationId=");
		builder.append(locationId);
		builder.append(", dataList=");
		builder.append(dataList);
		builder.append("]");
		return builder.toString();
	}
	
	public void print(int indent) {
		Debug.println(indent, "MeasurementRow: ");
		Debug.println(indent + 3, "Stock Id: " + stockId);
	    Debug.println(indent + 3, "Location Id: " + locationId);
		Debug.println(indent + 3, "DataList: ");
		for (MeasurementData data : dataList){
			data.print(indent + 6);
		}
	}

}
