package org.generationcp.middleware.api.germplasmlist;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.pojos.GermplasmListColumnCategory;

public class GermplasmListMeasurementVariableDTO extends MeasurementVariable {

	private GermplasmListColumnCategory columnCategory;

	public GermplasmListColumnCategory getColumnCategory() {
		return columnCategory;
	}

	public void setColumnCategory(final GermplasmListColumnCategory columnCategory) {
		this.columnCategory = columnCategory;
	}

}
