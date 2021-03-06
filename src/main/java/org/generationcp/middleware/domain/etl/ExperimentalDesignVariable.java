
package org.generationcp.middleware.domain.etl;

import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.dms.ExperimentDesignType;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;

import java.util.List;

public class ExperimentalDesignVariable {

	private List<MeasurementVariable> variables;

	public ExperimentalDesignVariable(final List<MeasurementVariable> variables) {
		this.variables = variables;
	}

	private MeasurementVariable getByTermId(final TermId termId) {
		if (this.variables != null) {
			for (final MeasurementVariable variable : this.variables) {
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
		final MeasurementVariable variable = this.getExperimentalDesign();
		final MeasurementVariable exptDesignSource = this.getExperimentalDesignSource();
		if (NumberUtils.toInt(variable.getValue()) == TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId() && exptDesignSource != null) {
			return ExperimentDesignType.ALPHA_LATTICE;
		} else if (variable != null && variable.getPossibleValues() != null && !variable.getPossibleValues().isEmpty()
			&& NumberUtils.isNumber(variable.getValue())) {
			for (final ValueReference ref : variable.getPossibleValues()) {
				if (ref.getId().equals(Integer.valueOf(variable.getValue()))) {
					return ref.getDescription();
				}
			}
			if (exptDesignSource != null) {
				return ExperimentDesignType.CUSTOM_IMPORT.getName();
			}
		}
		return "";
	}

	public MeasurementVariable getNumberOfBlocks() {
		return this.getByTermId(TermId.NBLKS);
	}

	public MeasurementVariable getNumberOfReplicates() {
		return this.getByTermId(TermId.NUMBER_OF_REPLICATES);
	}

	public MeasurementVariable getBlockSize() {
		return this.getByTermId(TermId.BLOCK_SIZE);
	}

	public MeasurementVariable getReplicationPercentage() {
		return this.getByTermId(TermId.PERCENTAGE_OF_REPLICATION);
	}

	public MeasurementVariable getReplicationsMap() {
		return this.getByTermId(TermId.REPLICATIONS_MAP);
	}

	public String getReplicationsMapDisplay() {
		final MeasurementVariable variable = this.getReplicationsMap();
		if (variable != null && variable.getPossibleValues() != null && !variable.getPossibleValues().isEmpty()
			&& NumberUtils.isNumber(variable.getValue())) {
			for (final ValueReference ref : variable.getPossibleValues()) {
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

	public MeasurementVariable getChecksStartingPosition() {
		return this.getByTermId(TermId.CHECK_START);
	}

	public MeasurementVariable getChecksSpacing() {
		return this.getByTermId(TermId.CHECK_INTERVAL);
	}

	public MeasurementVariable getChecksMannerOfInsertion() {
		return this.getByTermId(TermId.CHECK_PLAN);
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
	public void setVariables(final List<MeasurementVariable> variables) {
		this.variables = variables;
	}

}
