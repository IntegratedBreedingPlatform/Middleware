
package org.generationcp.middleware.domain.etl;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;

public class ExperimentalDesignVariable {

	private List<MeasurementVariable> variables;

	public ExperimentalDesignVariable(List<MeasurementVariable> variables) {
		this.variables = variables;
	}

	private MeasurementVariable getByTermId(TermId termId) {
		if (this.variables != null) {
			for (MeasurementVariable variable : this.variables) {
				if (variable.getTermId() == termId.getId()) {
					return variable;
				}
			}
		}
		return null;
	}

	public MeasurementVariable getExperimentalDesign() {
		return this.getByTermId(TermId.EXPERIMENT_DESIGN_FACTOR);
	}

	public String getExperimentalDesignDisplay() {
		MeasurementVariable variable = this.getByTermId(TermId.EXPERIMENT_DESIGN_FACTOR);
		if (variable != null && variable.getPossibleValues() != null && !variable.getPossibleValues().isEmpty()
				&& NumberUtils.isNumber(variable.getValue())) {
			for (ValueReference ref : variable.getPossibleValues()) {
				if (ref.getId().equals(Integer.valueOf(variable.getValue()))) {
					return ref.getDescription();
				}
			}
		}
		return "";
	}

	public MeasurementVariable getNumberOfReplicates() {
		return this.getByTermId(TermId.NUMBER_OF_REPLICATES);
	}

	public MeasurementVariable getBlockSize() {
		return this.getByTermId(TermId.BLOCK_SIZE);
	}

	public MeasurementVariable getReplicationsMap() {
		return this.getByTermId(TermId.REPLICATIONS_MAP);
	}

	public String getReplicationsMapDisplay() {
		MeasurementVariable variable = this.getReplicationsMap();
		if (variable != null && variable.getPossibleValues() != null && !variable.getPossibleValues().isEmpty()
				&& NumberUtils.isNumber(variable.getValue())) {
			for (ValueReference ref : variable.getPossibleValues()) {
				if (ref.getId().equals(Integer.valueOf(variable.getValue()))) {
					return ref.getDescription();
				}
			}
		}
		return "";
	}

	public MeasurementVariable getNumberOfRepsInCols() {
		return this.getByTermId(TermId.NO_OF_REPS_IN_COLS);
	}

	public MeasurementVariable getNumberOfRowsInReps() {
		return this.getByTermId(TermId.NO_OF_ROWS_IN_REPS);
	}

	public MeasurementVariable getNumberOfColsInReps() {
		return this.getByTermId(TermId.NO_OF_COLS_IN_REPS);
	}

	public MeasurementVariable getNumberOfContiguousRowsLatinize() {
		return this.getByTermId(TermId.NO_OF_CROWS_LATINIZE);
	}

	public MeasurementVariable getNumberOfContiguousColsLatinize() {
		return this.getByTermId(TermId.NO_OF_CCOLS_LATINIZE);
	}

	public MeasurementVariable getNumberOfContiguousBlocksLatinize() {
		return this.getByTermId(TermId.NO_OF_CBLKS_LATINIZE);
	}

	public MeasurementVariable getExperimentalDesignSource() {
		return this.getByTermId(TermId.EXPT_DESIGN_SOURCE);
	}

	/**
	 * @return the variables
	 */
	public List<MeasurementVariable> getVariables() {
		return this.variables;
	}

	/**
	 * @param variables the variables to set
	 */
	public void setVariables(List<MeasurementVariable> variables) {
		this.variables = variables;
	}

}
