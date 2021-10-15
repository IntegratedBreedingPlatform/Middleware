package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.pojos.GermplasmListColumnCategory;

import java.util.List;

public class GermplasmListMeasurementVariableDTO extends MeasurementVariable {

	private GermplasmListColumnCategory columnCategory;

	public GermplasmListMeasurementVariableDTO(final int termId, final String name, final String alias,
		final GermplasmListColumnCategory category) {
		this.setTermId(termId);
		this.setName(name);
		this.setAlias(alias);
		this.columnCategory = category;
	}

	public GermplasmListMeasurementVariableDTO(final int termId, final String name, final String alias,
		final GermplasmListColumnCategory category, final Integer datatypeId, final List<ValueReference> possibleValues) {
		this(termId, name, alias, category);
		this.setDataTypeId(datatypeId);
		this.setPossibleValues(possibleValues);
	}

	public GermplasmListColumnCategory getColumnCategory() {
		return columnCategory;
	}

	public void setColumnCategory(final GermplasmListColumnCategory columnCategory) {
		this.columnCategory = columnCategory;
	}

}
