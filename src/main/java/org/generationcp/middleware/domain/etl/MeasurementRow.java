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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.util.Debug;

public class MeasurementRow {

	private long stockId;
	
	private long locationId;
	
	private int experimentId;
	
	private List<MeasurementData> dataList;
	
	public MeasurementRow() {
	}

    public MeasurementRow(List<MeasurementData> dataList) {
        this.dataList = dataList;
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
	
	public int getExperimentId() {
            return experimentId;
        }
    
        public void setExperimentId(int experimentId) {
                this.experimentId = experimentId;
        }
	
	public List<MeasurementData> getTrialDataList(List<String> trialHeaders) {
		List<MeasurementData> list = new ArrayList<MeasurementData>();
		if(dataList!=null && !dataList.isEmpty()) {
			for (MeasurementData data : dataList) {
				if(trialHeaders!=null && trialHeaders.contains(data.getLabel())) {
					list.add(data);
				}
			}
			return list;
		}
		return list;
	}
	
	public List<MeasurementData> getNonTrialDataList(List<String> trialHeaders) {
		List<MeasurementData> list = new ArrayList<MeasurementData>();
		if(dataList!=null && !dataList.isEmpty()) {
			for (MeasurementData data : dataList) {
				if(trialHeaders==null || !trialHeaders.contains(data.getLabel())) {
					list.add(data);
				}
			}
			return list;
		}
		return list;
	}
	
	public String getMeasurementDataValue(String label) {
		if(label!=null && dataList!=null && !dataList.isEmpty()) {
			for (MeasurementData data : dataList) {
				if(label!=null && data.getLabel()!=null && label.equals(data.getLabel())) {
					return data.getValue();
				}
			}
		}
		return null;
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
        builder.append(", experimentId=");
        builder.append(experimentId);
        builder.append(", dataList=");
        builder.append(dataList);
        builder.append("]");
        return builder.toString();
    }
	
	public void print(int indent) {
		Debug.println(indent, "MeasurementRow: ");
		Debug.println(indent + 3, "Stock Id: " + stockId);
        Debug.println(indent + 3, "Location Id: " + locationId);
        Debug.println(indent + 3, "Experiment Id: " + experimentId);
		Debug.println(indent + 3, "DataList: ");
		for (MeasurementData data : dataList){
			data.print(indent + 6);
		}
	}

}
