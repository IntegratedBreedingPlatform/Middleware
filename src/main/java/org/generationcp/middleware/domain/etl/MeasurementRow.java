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

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.oms.TermId;
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
					if (data.getcValueId() != null) {
						return data.getcValueId().toString();
					}
					else {
						return data.getValue();
					}
				}
			}
		}
		return null;
	}

	public MeasurementData getMeasurementData(String label) {
		if(label!=null && dataList!=null && !dataList.isEmpty()) {
			for (MeasurementData data : dataList) {
				if(label!=null && data.getLabel()!=null && label.equals(data.getLabel())) {
					return data;
				}
			}
		}
		return null;
	}

	public MeasurementData getMeasurementData(Integer termId) {
		if (termId != null && dataList != null && !dataList.isEmpty()) {
			for (MeasurementData data : dataList) {
				if (data.getMeasurementVariable() != null && data.getMeasurementVariable().getTermId() == termId) {
					return data;
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
	
	public List<MeasurementVariable> getMeasurementVariables() {
		List<MeasurementVariable> variables = new ArrayList<MeasurementVariable>();
		if (this.dataList != null) {
			for (MeasurementData data : dataList) {
				if (data.getMeasurementVariable() != null) {
					variables.add(data.getMeasurementVariable());
				}
			}
		}
		return variables;
	}
	
	public String getMeasurementDataValue(Integer id) {
		List<MeasurementVariable> variables = getMeasurementVariables();
		String label = null;
		for (MeasurementVariable variable : variables) {
			if (variable.getTermId() == id) {
				label = variable.getName();
			}
		}
		
		return label != null ? getMeasurementDataValue(label) : null;
	}
	
	public Integer getRange() {
		String strRange = getMeasurementDataValue(TermId.RANGE_NO.getId());
		return strRange != null && NumberUtils.isNumber(strRange) ? Double.valueOf(strRange).intValue() : null;
	}
	
	public Integer getColumn() {
		String strRange = getMeasurementDataValue(TermId.COLUMN_NO.getId());
		return strRange != null && NumberUtils.isNumber(strRange) ? Double.valueOf(strRange).intValue() : null;
	}
	
	public String getKeyIdentifier() {
		String trialInstanceNumber = getMeasurementDataValue(TermId.TRIAL_INSTANCE_FACTOR.getId());
		if (trialInstanceNumber == null) {
			trialInstanceNumber = "1";
		}
		String plotNumber = getMeasurementDataValue(TermId.PLOT_NO.getId());
		if (plotNumber == null || plotNumber.isEmpty()) {
			plotNumber = getMeasurementDataValue(TermId.PLOT_NNO.getId());
		}
		String entryNumber = getMeasurementDataValue(TermId.ENTRY_NO.getId());
		return trialInstanceNumber + "-" + plotNumber + "-" + entryNumber;
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
	
	public MeasurementRow copy() {
		List<MeasurementData> newDataList = null;
		if (this.dataList != null && !this.dataList.isEmpty()) {
			newDataList = new ArrayList<MeasurementData>();
			for (MeasurementData data : this.dataList) {
				newDataList.add(data.copy());
			}
		}
		MeasurementRow row = new MeasurementRow(this.stockId, this.locationId, newDataList);
		row.setExperimentId(this.experimentId);
		return row;
	}
}
