
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;

public class MeasurementVariableTestDataInitializer {

	public static MeasurementVariable createMeasurementVariable() {
		final MeasurementVariable measurementVar = new MeasurementVariable();
		measurementVar.setTermId(TermId.PI_ID.getId());
		measurementVar.setValue("1");
		return measurementVar;
	}

	public static MeasurementVariable createMeasurementVariable(final int termId, final String value) {
		final MeasurementVariable measurementVar = new MeasurementVariable();
		measurementVar.setTermId(termId);
		measurementVar.setValue(value);
		return measurementVar;
	}

	public static MeasurementVariable createMeasurementVariable(final int termId, final String name, final String value) {
		final MeasurementVariable measurementVar = new MeasurementVariable();
		measurementVar.setTermId(termId);
		measurementVar.setName(name);
		measurementVar.setValue(value);
		return measurementVar;
	}

	public static List<MeasurementVariable> createMeasurementVariableList() {
		final List<MeasurementVariable> measurementVarList = new ArrayList<MeasurementVariable>();
		measurementVarList.add(MeasurementVariableTestDataInitializer.createMeasurementVariable());
		return measurementVarList;
	}

	public static MeasurementVariable createMeasurementVariableWithName(final int termId, final String name) {
		final MeasurementVariable measurementVar = new MeasurementVariable();
		measurementVar.setTermId(termId);
		measurementVar.setName(name);
		return measurementVar;
	}

	public static MeasurementVariable createMeasurementVariable(final int termId, final String name, final String description,
			final String scale, final String method, final String property, final String dataType, final String value,
			final String label, final int dataTypeId, final PhenotypicType role) {
		final MeasurementVariable variable = new MeasurementVariable(termId, name, description, scale, method, property,
				dataType, value, label);
		variable.setRole(role);
		variable.setDataTypeId(dataTypeId);
		variable.setVariableType(VariableType.TRAIT);
		return variable;
	}
}
