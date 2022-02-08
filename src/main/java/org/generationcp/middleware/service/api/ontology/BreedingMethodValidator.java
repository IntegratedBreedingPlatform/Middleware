package org.generationcp.middleware.service.api.ontology;

import liquibase.util.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.api.breedingmethod.BreedingMethodSearchRequest;
import org.generationcp.middleware.api.breedingmethod.BreedingMethodService;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Collections;

@Configurable
public class BreedingMethodValidator implements VariableValueValidator {

	public static final Integer SCALE_BM_ID = 1909;
	public static final Integer SCALE_BM_NAME = 1910;

	@Autowired
	private BreedingMethodService breedingMethodService;

	@Override
	public boolean isValid(final MeasurementVariable variable) {
		this.ensureBreedingMethodDataType(variable.getDataTypeId());
		final String value = variable.getValue();
		if (!StringUtils.isEmpty(value)) {
			final BreedingMethodSearchRequest request = new BreedingMethodSearchRequest();

			final Integer scaleId = variable.getScaleId();
			if (TermId.BREEDING_METHOD_SCALE.getId() == scaleId) {
				request.setMethodAbbreviations(Collections.singletonList(value));
			} else if (SCALE_BM_ID.equals(scaleId)) {
				if (NumberUtils.isDigits(value)) {
					request.setMethodIds(Collections.singletonList(Integer.parseInt(value)));
				} else {
					return false;
				}
			} else if (SCALE_BM_NAME.equals(scaleId)) {
				final SqlTextFilter nameFilter = new SqlTextFilter(value, SqlTextFilter.Type.EXACTMATCH);
				request.setNameFilter(nameFilter);
			}
			return this.breedingMethodService.countSearchBreedingMethods(request, null) > 0;
		}
		return true;
	}

	private void ensureBreedingMethodDataType(final Integer dataTypeId) {
		if (!DataType.BREEDING_METHOD.getId().equals(dataTypeId)) {
			throw new IllegalStateException("The ensureBreedingMethodDataType method must never be called for non Breeding Method variables. "
				+ "Please report this error to your administrator.");
		}
	}
}
