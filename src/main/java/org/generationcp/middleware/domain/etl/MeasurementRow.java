/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
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

	public MeasurementRow(final MeasurementRow row) {
		this.stockId = row.stockId;
		this.locationId = row.locationId;
		this.experimentId = row.experimentId;
		this.dataList = new ArrayList<MeasurementData>();
		for (final MeasurementData data : row.dataList) {
			this.dataList.add(new MeasurementData(data));
		}
	}

	public MeasurementRow(final List<MeasurementData> dataList) {
		this.dataList = dataList;
	}

	public MeasurementRow(final long stockId, final long locationId, final List<MeasurementData> dataList) {
		this.stockId = stockId;
		this.locationId = locationId;
		this.dataList = dataList;
	}

	public long getStockId() {
		return this.stockId;
	}

	public void setStockId(final long stockId) {
		this.stockId = stockId;
	}

	public long getLocationId() {
		return this.locationId;
	}

	public void setLocationId(final long locationId) {
		this.locationId = locationId;
	}

	public int getExperimentId() {
		return this.experimentId;
	}

	public void setExperimentId(final int experimentId) {
		this.experimentId = experimentId;
	}

	public List<MeasurementData> getTrialDataList(final List<String> trialHeaders) {
		final List<MeasurementData> list = new ArrayList<MeasurementData>();

		if (this.dataList != null && !this.dataList.isEmpty()) {
			for (final MeasurementData data : this.dataList) {
				if (trialHeaders != null && trialHeaders.contains(data.getLabel())) {
					list.add(data);
				}
			}
			return list;
		}
		return list;
	}

	public List<MeasurementData> getNonTrialDataList(final List<String> trialHeaders) {
		final List<MeasurementData> list = new ArrayList<>();
		if (this.dataList != null && !this.dataList.isEmpty()) {
			for (final MeasurementData data : this.dataList) {
				//Remove SAMPLES with TermId = -2 because is nonexistent term Id
				final boolean isSampleVariable = data.getMeasurementVariable().getTermId() == TermId.SAMPLES.getId();
				if ( (trialHeaders == null || !trialHeaders.contains(data.getLabel()) ) && !isSampleVariable) {
					list.add(data);
				}
			}
			return list;
		}
		return list;
	}

	public String getMeasurementDataValue(final String label) {
		if (label != null && this.dataList != null && !this.dataList.isEmpty()) {
			for (final MeasurementData data : this.dataList) {
				if (label != null && data.getLabel() != null && label.equals(data.getLabel())) {
					if (data.getcValueId() != null) {
						return data.getcValueId().toString();
					} else {
						return data.getValue();
					}
				}
			}
		}
		return null;
	}

	public MeasurementData getMeasurementData(final String label) {
		if (label != null && this.dataList != null && !this.dataList.isEmpty()) {
			for (final MeasurementData data : this.dataList) {
				if (label != null && data.getLabel() != null && label.equals(data.getLabel())) {
					return data;
				}
			}
		}
		return null;
	}

	public MeasurementData getMeasurementData(final Integer termId) {
		if (termId != null && this.dataList != null && !this.dataList.isEmpty()) {
			for (final MeasurementData data : this.dataList) {
				if (data.getMeasurementVariable() != null && data.getMeasurementVariable().getTermId() == termId) {
					return data;
				}
			}
		}
		return null;
	}

	public List<MeasurementData> getDataList() {
		return this.dataList;
	}

	public void setDataList(final List<MeasurementData> dataList) {
		this.dataList = dataList;
	}

	public List<MeasurementVariable> getMeasurementVariables() {
		final List<MeasurementVariable> variables = new ArrayList<MeasurementVariable>();
		if (this.dataList != null) {
			for (final MeasurementData data : this.dataList) {
				if (data.getMeasurementVariable() != null) {
					variables.add(data.getMeasurementVariable());
				}
			}
		}
		return variables;
	}

	public String getMeasurementDataValue(final Integer id) {
		if (this.dataList != null && !this.dataList.isEmpty()) {
			for (final MeasurementData data : this.dataList) {
				if (data.getMeasurementVariable().getTermId() == id.intValue()) {
					if (data.getcValueId() != null) {
						return data.getcValueId().toString();
					} else {
						return data.getValue();
					}
				}
			}
		}

		return null;
	}

	public Integer getRange() {
		final String strRange = this.getMeasurementDataValue(TermId.RANGE_NO.getId());
		return strRange != null && NumberUtils.isNumber(strRange) ? Double.valueOf(strRange).intValue() : null;
	}

	public Integer getColumn() {
		final String strRange = this.getMeasurementDataValue(TermId.COLUMN_NO.getId());
		return strRange != null && NumberUtils.isNumber(strRange) ? Double.valueOf(strRange).intValue() : null;
	}

	public String getKeyIdentifier() {
		return this.getMeasurementDataValue(TermId.OBS_UNIT_ID.getId());
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("MeasurementRow [stockId=");
		builder.append(this.stockId);
		builder.append(", locationId=");
		builder.append(this.locationId);
		builder.append(", experimentId=");
		builder.append(this.experimentId);
		builder.append(", dataList=");
		builder.append(this.dataList);
		builder.append("]");
		return builder.toString();
	}

	public void print(final int indent) {
		Debug.println(indent, "MeasurementRow: ");
		Debug.println(indent + 3, "Stock Id: " + this.stockId);
		Debug.println(indent + 3, "Location Id: " + this.locationId);
		Debug.println(indent + 3, "Experiment Id: " + this.experimentId);
		Debug.println(indent + 3, "DataList: ");
		for (final MeasurementData data : this.dataList) {
			data.print(indent + 6);
		}
	}

	// FIXME consolidate logic in the copy constructor
	public MeasurementRow copy() {
		List<MeasurementData> newDataList = null;
		if (this.dataList != null && !this.dataList.isEmpty()) {
			newDataList = new ArrayList<MeasurementData>();
			for (final MeasurementData data : this.dataList) {
				newDataList.add(data.copy());
			}
		}
		final MeasurementRow row = new MeasurementRow(this.stockId, this.locationId, newDataList);
		row.setExperimentId(this.experimentId);
		return row;
	}

	// FIXME consolidate logic in a copy constructor
	public MeasurementRow copy(final List<MeasurementVariable> variableList) {
		List<MeasurementData> newDataList = null;
		if (this.dataList != null && !this.dataList.isEmpty()) {
			newDataList = new ArrayList<MeasurementData>();
			for (final MeasurementData data : this.dataList) {
				final MeasurementVariable var = this.getMatchingMeasurementVariable(variableList, data.getMeasurementVariable());
				newDataList.add(data.copy(var));
			}
		}
		final MeasurementRow row = new MeasurementRow(this.stockId, this.locationId, newDataList);
		row.setExperimentId(this.experimentId);
		return row;
	}

	private MeasurementVariable getMatchingMeasurementVariable(final List<MeasurementVariable> variableList, final MeasurementVariable originalVariable) {
		if (variableList != null && originalVariable != null) {
			for (final MeasurementVariable var : variableList) {
				if (var.getTermId() == originalVariable.getTermId()) {
					return var;
				}
			}
		}
		return originalVariable;
	}
}
