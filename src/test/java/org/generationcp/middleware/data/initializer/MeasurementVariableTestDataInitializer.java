
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;

//TODO convert the methods here to static methods
public class MeasurementVariableTestDataInitializer {

	public MeasurementVariable createMeasurementVariable() {
		final MeasurementVariable measurementVar = new MeasurementVariable();
		measurementVar.setTermId(TermId.PI_ID.getId());
		measurementVar.setValue("1");
		return measurementVar;
	}

	public MeasurementVariable createMeasurementVariable(final int termId, final String value) {
		final MeasurementVariable measurementVar = new MeasurementVariable();
		measurementVar.setTermId(termId);
		measurementVar.setValue(value);
		return measurementVar;
	}

	public MeasurementVariable createMeasurementVariable(final int termId, final String name, final String value) {
		final MeasurementVariable measurementVar = new MeasurementVariable();
		measurementVar.setTermId(termId);
		measurementVar.setName(name);
		measurementVar.setValue(value);
		return measurementVar;
	}

	public List<MeasurementVariable> createMeasurementVariableList() {
		final List<MeasurementVariable> measurementVarList = new ArrayList<MeasurementVariable>();
		measurementVarList.add(this.createMeasurementVariable());
		return measurementVarList;
	}

	public MeasurementVariable createMeasurementVariableWithName(final int termId, final String name) {
		final MeasurementVariable measurementVar = new MeasurementVariable();
		measurementVar.setTermId(termId);
		measurementVar.setName(name);
		return measurementVar;
	}
}
