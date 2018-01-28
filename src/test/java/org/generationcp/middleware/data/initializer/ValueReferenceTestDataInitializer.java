
package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.ValueReference;

public class ValueReferenceTestDataInitializer {

	private static final String DUMMY_PROGRAM_UUID = "2486a5d7-43ad-4a17-be4a-52572ff4b2f3";

	public ValueReferenceTestDataInitializer() {
		// do nothing
	}

	public ValueReference createValueReference(final int id, final String name) {
		final ValueReference valueReference = new ValueReference(id, name);
		valueReference.setProgramUUID(DUMMY_PROGRAM_UUID);
		valueReference.setDescription(name);
		return valueReference;
	}

	public ValueReference createValueReference(final int id, final String name, final String description) {
		final ValueReference valueReference = new ValueReference(id, name);
		valueReference.setProgramUUID(DUMMY_PROGRAM_UUID);
		valueReference.setDescription(description);
		return valueReference;
	}

	public List<ValueReference> createValueReferenceList(final int noOfEntries) {
		final List<ValueReference> valueReferenceList = new ArrayList<ValueReference>();
		for (int i = 1; i <= noOfEntries; i++) {
			valueReferenceList.add(this.createValueReference(i, "Name " + i));
		}
		return valueReferenceList;
	}
	
	public static List<ValueReference> createPossibleValues() {
		final List<ValueReference> possibleValues = new ArrayList<>();
		possibleValues.add(new ValueReference(1, "1", ""));
		possibleValues.add(new ValueReference(2, "2", ""));
		possibleValues.add(new ValueReference(3, "3", ""));
		possibleValues.add(new ValueReference(4, "4", ""));
		possibleValues.add(new ValueReference(5, "5", ""));
		return possibleValues;
	}
}
