
package org.generationcp.middleware.domain.etl;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.DesignTypeItem;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.junit.Assert;
import org.junit.Test;

public class ExperimentalDesignVariableTest {

	private static final int RCBD_ID = TermId.RANDOMIZED_COMPLETE_BLOCK.getId();
	private static final String RCBD_NAME = "RCBD";
	private static final String RCBD_DESC = "Randomized complete block design";
	private static final int RIBD_ID = TermId.RESOLVABLE_INCOMPLETE_BLOCK.getId();
	private static final String RIBD_NAME = "RIBD";
	private static final String RIBD_DESC = "Resolvable incomplete block design";
	private static final int RRCD_ID = TermId.RESOLVABLE_INCOMPLETE_ROW_COL.getId();
	private static final String RRCD_NAME = "RRCD";
	private static final String RRCD_DESC = "Resolvable row-column design";
	private static final int RIBDL_ID = TermId.RESOLVABLE_INCOMPLETE_BLOCK_LATIN.getId();
	private static final String RIBDL_NAME = "RIBDL";
	private static final String RIBDL_DESC = "Resolvable Incomplete Block Design (Latinized)";
	private static final int RRCDL_ID = TermId.RESOLVABLE_INCOMPLETE_ROW_COL_LATIN.getId();
	private static final String RRCDL_NAME = "RRCDL";
	private static final String RRCDL_DESC = "Resolvable Row-and-Column Design (Latinized)";
	private static final String ALPHA_LATTICE = "Alpha Lattice";
	private static final int OTHER_DESIGN_ID = TermId.OTHER_DESIGN.getId();
	private static final String OTHER_DESIGN_DESC = DesignTypeItem.CUSTOM_IMPORT.getName();

	@Test
	public void testGetExperimentalDesignDisplay_RCBD() {
		final List<MeasurementVariable> variables = new ArrayList<>();
		variables.add(this.createMeasurementVariableTestData(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), new Integer(
				ExperimentalDesignVariableTest.RCBD_ID).toString(), this.createPossibleValuesOfExptDesign()));
		final ExperimentalDesignVariable experimentalDesignVariable = new ExperimentalDesignVariable(variables);
		Assert.assertEquals("Experimental design must be " + ExperimentalDesignVariableTest.RCBD_DESC,
				ExperimentalDesignVariableTest.RCBD_DESC, experimentalDesignVariable.getExperimentalDesignDisplay());
	}

	private MeasurementVariable createMeasurementVariableTestData(final int termId, final String value,
			final List<ValueReference> possibleValues) {
		final MeasurementVariable measurementVariable = new MeasurementVariable();
		measurementVariable.setTermId(termId);
		measurementVariable.setValue(value);
		measurementVariable.setPossibleValues(possibleValues);
		return measurementVariable;
	}

	private List<ValueReference> createPossibleValuesOfExptDesign() {
		final List<ValueReference> possibleValues = new ArrayList<>();
		possibleValues.add(new ValueReference(ExperimentalDesignVariableTest.RCBD_ID, ExperimentalDesignVariableTest.RCBD_NAME,
				ExperimentalDesignVariableTest.RCBD_DESC));
		possibleValues.add(new ValueReference(ExperimentalDesignVariableTest.RIBD_ID, ExperimentalDesignVariableTest.RIBD_NAME,
				ExperimentalDesignVariableTest.RIBD_DESC));
		possibleValues.add(new ValueReference(ExperimentalDesignVariableTest.RRCD_ID, ExperimentalDesignVariableTest.RRCD_NAME,
				ExperimentalDesignVariableTest.RRCD_DESC));
		possibleValues.add(new ValueReference(ExperimentalDesignVariableTest.RIBDL_ID, ExperimentalDesignVariableTest.RIBDL_NAME,
				ExperimentalDesignVariableTest.RIBDL_DESC));
		possibleValues.add(new ValueReference(ExperimentalDesignVariableTest.RRCDL_ID, ExperimentalDesignVariableTest.RRCDL_NAME,
				ExperimentalDesignVariableTest.RRCDL_DESC));
		return possibleValues;
	}

	@Test
	public void testGetExperimentalDesignDisplay_RIBD() {
		final List<MeasurementVariable> variables = new ArrayList<>();
		variables.add(this.createMeasurementVariableTestData(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), new Integer(
				ExperimentalDesignVariableTest.RIBD_ID).toString(), this.createPossibleValuesOfExptDesign()));
		final ExperimentalDesignVariable experimentalDesignVariable = new ExperimentalDesignVariable(variables);
		Assert.assertEquals("Experimental design must be " + ExperimentalDesignVariableTest.RIBD_DESC,
				ExperimentalDesignVariableTest.RIBD_DESC, experimentalDesignVariable.getExperimentalDesignDisplay());
	}

	@Test
	public void testGetExperimentalDesignDisplay_Alpha_Lattice() {
		final List<MeasurementVariable> variables = new ArrayList<>();
		variables.add(this.createMeasurementVariableTestData(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), new Integer(
				ExperimentalDesignVariableTest.RIBD_ID).toString(), this.createPossibleValuesOfExptDesign()));
		variables.add(this.createMeasurementVariableTestData(TermId.EXPT_DESIGN_SOURCE.getId(), "E30-Rep2-Block6-5Ind.csv", null));
		final ExperimentalDesignVariable experimentalDesignVariable = new ExperimentalDesignVariable(variables);
		Assert.assertEquals("Experimental design must be " + ExperimentalDesignVariableTest.ALPHA_LATTICE,
				ExperimentalDesignVariableTest.ALPHA_LATTICE, experimentalDesignVariable.getExperimentalDesignDisplay());
	}

	@Test
	public void testGetExperimentalDesignDisplay_Other_Design() {
		final List<MeasurementVariable> variables = new ArrayList<>();
		variables.add(this.createMeasurementVariableTestData(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), new Integer(
				ExperimentalDesignVariableTest.OTHER_DESIGN_ID).toString(), this.createPossibleValuesOfExptDesign()));
		variables.add(this.createMeasurementVariableTestData(TermId.EXPT_DESIGN_SOURCE.getId(), "Other Design.csv", null));
		final ExperimentalDesignVariable experimentalDesignVariable = new ExperimentalDesignVariable(variables);
		Assert.assertEquals("Experimental design must be " + ExperimentalDesignVariableTest.OTHER_DESIGN_DESC,
				ExperimentalDesignVariableTest.OTHER_DESIGN_DESC, experimentalDesignVariable.getExperimentalDesignDisplay());
	}

	@Test
	public void testGetExperimentalDesignDisplay_Unknown_Design() {
		final List<MeasurementVariable> variables = new ArrayList<>();
		variables.add(this.createMeasurementVariableTestData(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), "12345",
				this.createPossibleValuesOfExptDesign()));
		final ExperimentalDesignVariable experimentalDesignVariable = new ExperimentalDesignVariable(variables);
		Assert.assertEquals("Experimental design must be empty ", "", experimentalDesignVariable.getExperimentalDesignDisplay());
	}

	@Test
	public void testGetExperimentalDesignDisplayWithNullVariableValue() {
		final List<MeasurementVariable> variables = new ArrayList<>();
		variables.add(this.createMeasurementVariableTestData(TermId.EXPERIMENT_DESIGN_FACTOR.getId(), null,
				this.createPossibleValuesOfExptDesign()));
		final ExperimentalDesignVariable experimentalDesignVariable = new ExperimentalDesignVariable(variables);
		Assert.assertEquals("Experimental design must be empty ", "", experimentalDesignVariable.getExperimentalDesignDisplay());
	}
}
