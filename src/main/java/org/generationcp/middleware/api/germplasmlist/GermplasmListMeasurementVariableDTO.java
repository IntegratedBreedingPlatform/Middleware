package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.Scale;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.pojos.GermplasmListColumnCategory;

import java.util.List;

import static org.apache.commons.lang3.math.NumberUtils.createDouble;

public class GermplasmListMeasurementVariableDTO extends MeasurementVariable {

	private GermplasmListColumnCategory columnCategory;

	public GermplasmListMeasurementVariableDTO(final int termId, final String name, final String alias,
		final GermplasmListColumnCategory category) {
		this.setTermId(termId);
		this.setName(name);
		this.setAlias(alias);
		this.columnCategory = category;
	}

	public GermplasmListMeasurementVariableDTO(
		final Variable variable, final VariableType variableType,
		final GermplasmListColumnCategory category, final List<ValueReference> possibleValues) {

		this(variable.getId(), variable.getName(), variable.getAlias(), category);
		this.setPossibleValues(possibleValues);
		this.setVariableType(variableType);

		final Scale scale = variable.getScale();
		this.setDataTypeId(scale.getDataType().getId());
		this.setScaleMinRange(createDouble(scale.getMinValue()));
		this.setScaleMaxRange(createDouble(scale.getMaxValue()));
		this.setVariableMinRange(createDouble(variable.getMinValue()));
		this.setVariableMaxRange(createDouble(variable.getMaxValue()));

		// TODO continue mapping
	}

	public GermplasmListColumnCategory getColumnCategory() {
		return columnCategory;
	}

	public void setColumnCategory(final GermplasmListColumnCategory columnCategory) {
		this.columnCategory = columnCategory;
	}

}
