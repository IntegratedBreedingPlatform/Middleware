package org.generationcp.middleware.service.api.ontology;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.api.location.LocationService;
import org.generationcp.middleware.api.location.search.LocationSearchRequest;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.sqlfilter.SqlTextFilter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

public class LocationValidator implements VariableValueValidator {

	public static final Integer SCALE_LOC_ID = 1903;
	public static final Integer SCALE_LOC_NAME = 1904;
	public static final Integer SCALE_LOC_ABBR = 6016;

	@Autowired
	private LocationService locationService;

	@Override
	public boolean isValid(final MeasurementVariable variable) {
		this.ensureLocationDataType(variable.getDataTypeId());
		final String value = variable.getValue();
		if (!StringUtils.isEmpty(value)) {
			final LocationSearchRequest request = new LocationSearchRequest();
			final Integer scaleId = variable.getScaleId();
			if (SCALE_LOC_ABBR.equals(scaleId)) {
				request.setLocationAbbreviations(Collections.singletonList(value));

			} else if (SCALE_LOC_ID.equals(scaleId)) {
				if (NumberUtils.isDigits(value)) {
					request.setLocationIds(Collections.singletonList(Integer.parseInt(value)));
				} else {
					return false;
				}
			} else if (SCALE_LOC_NAME.equals(scaleId)) {
				final SqlTextFilter locationNameFilter = new SqlTextFilter(value, SqlTextFilter.Type.STARTSWITH);
				request.setLocationNameFilter(locationNameFilter);
			}
			return this.locationService.countFilteredLocations(request, null) > 0;
		}
		return true;
	}

	private void ensureLocationDataType(final Integer dataTypeId) {
		if (!DataType.LOCATION.getId().equals(dataTypeId)) {
			throw new IllegalStateException("The ensureLocationDataType method must never be called for non Location variables. "
				+ "Please report this error to your administrator.");
		}
	}
}
