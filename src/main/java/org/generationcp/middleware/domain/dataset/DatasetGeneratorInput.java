package org.generationcp.middleware.domain.dataset;

import org.pojomatic.Pojomatic;
import org.pojomatic.annotations.AutoProperty;

/**
 * Created by clarysabel on 10/24/18.
 */
@AutoProperty
public class DatasetGeneratorInput {

	private Integer datasetTypeId;
	private String datasetName;
	private Integer[] instanceIds;
	private Integer sequenceVariableId;
	private Integer numberOfSubObservationUnits;

	public Integer getDatasetTypeId() {
		return datasetTypeId;
	}

	public void setDatasetTypeId(final Integer datasetTypeId) {
		this.datasetTypeId = datasetTypeId;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(final String datasetName) {
		this.datasetName = datasetName;
	}

	public Integer[] getInstanceIds() {
		return instanceIds;
	}

	public void setInstanceIds(final Integer[] instanceIds) {
		this.instanceIds = instanceIds;
	}

	public Integer getSequenceVariableId() {
		return sequenceVariableId;
	}

	public void setSequenceVariableId(final Integer sequenceVariableId) {
		this.sequenceVariableId = sequenceVariableId;
	}

	public Integer getNumberOfSubObservationUnits() {
		return numberOfSubObservationUnits;
	}

	public void setNumberOfSubObservationUnits(final Integer numberOfSubObservationUnits) {
		this.numberOfSubObservationUnits = numberOfSubObservationUnits;
	}

	@Override
	public int hashCode() {
		return Pojomatic.hashCode(this);
	}

	@Override
	public String toString() {
		return Pojomatic.toString(this);
	}

	@Override
	public boolean equals(Object o) {
		return Pojomatic.equals(this, o);
	}

}
