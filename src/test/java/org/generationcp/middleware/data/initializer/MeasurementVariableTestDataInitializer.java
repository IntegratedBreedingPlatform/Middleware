
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;

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

	public List<MeasurementVariable> createMeasurementVariableList() {
		final List<MeasurementVariable> measurementVarList = new ArrayList<MeasurementVariable>();
		measurementVarList.add(this.createMeasurementVariable());
		return measurementVarList;
	}
	
	public MeasurementVariable createMeasurementVariable(final int termId, final int dataTypeId) {
		final MeasurementVariable var = new MeasurementVariable();
		var.setProperty("Property");
		var.setScale("Scale");
		var.setMethod("Method");
		var.setLabel("Label");
		var.setName("Name");
		var.setDescription("Description");
		var.setTermId(termId);
		var.setDataTypeId(dataTypeId);

		if (TermId.CATEGORICAL_VARIABLE.getId() == dataTypeId) {
			final List<ValueReference> possibleValues = new ArrayList<ValueReference>();
			possibleValues.add(new ValueReference(1, "Possible Value Name"));
			var.setPossibleValues(possibleValues);
		}

		return var;
	}
}
