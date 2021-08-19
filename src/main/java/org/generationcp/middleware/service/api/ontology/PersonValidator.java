package org.generationcp.middleware.service.api.ontology;

import liquibase.util.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Configurable
public class PersonValidator implements VariableValueValidator {

	@Autowired
	private UserService userService;

	@Override
	public boolean isValid(final MeasurementVariable variable) {
		this.ensurePersonDataType(variable.getDataTypeId());
		// For now, only validate Person ID scale
		if (variable.getScaleId() != null && TermId.PERSON_ID.getId() == variable.getScaleId() && !StringUtils.isEmpty(variable.getValue())) {
			if (NumberUtils.isDigits(variable.getValue())) {
				final Person person = this.userService.getPersonById(Integer.parseInt(variable.getValue()));
				return person != null;
			}
			return false;
		}
		return true;
	}

	private void ensurePersonDataType(final Integer dataTypeId) {
		if (!DataType.PERSON.getId().equals(dataTypeId)) {
			throw new IllegalStateException("The ensurePersonDataType method must never be called for non Person variables. "
				+ "Please report this error to your administrator.");
		}
	}
}
