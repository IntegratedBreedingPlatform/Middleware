package org.generationcp.middleware.dao.util;

import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.exceptions.InvalidBrapiDatatypeException;

import java.util.ArrayList;
import java.util.List;

public class VariableUtils {

	public static List<String> convertBrapiDataTypeToDataTypeIds(final List<String> brapiDataTypeNames)
		throws InvalidBrapiDatatypeException {
		final List<String> dataTypeIds = new ArrayList<>();
		for (final String brapiDataTypeName : brapiDataTypeNames) {
			final DataType dataType = DataType.getByBrapiName(brapiDataTypeName);
			if (dataType != null) {
				dataTypeIds.add(dataType.getId().toString());
			} else {
				throw new InvalidBrapiDatatypeException(brapiDataTypeName + " is not a valid brapi datatype name.");
			}
		}
		return dataTypeIds;
	}
}
