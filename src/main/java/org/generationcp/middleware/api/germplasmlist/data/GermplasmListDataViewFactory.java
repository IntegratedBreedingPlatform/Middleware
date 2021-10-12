package org.generationcp.middleware.api.germplasmlist.data;

import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListColumnCategory;
import org.generationcp.middleware.pojos.GermplasmListDataView;

public class GermplasmListDataViewFactory {

	public static GermplasmListDataView create(final GermplasmList germplasmList, final GermplasmListDataUpdateViewDTO dto) {

		if (dto.getCategory() == GermplasmListColumnCategory.STATIC) {
			return new GermplasmListDataView.GermplasmListDataStaticViewBuilder(germplasmList, dto.getId()).build();
		}

		if (dto.getCategory() == GermplasmListColumnCategory.NAMES) {
			return new GermplasmListDataView.GermplasmListDataNameViewBuilder(germplasmList, dto.getId()).build();
		}

		if (dto.getCategory() == GermplasmListColumnCategory.VARIABLE) {
			return new GermplasmListDataView.GermplasmListDataVariableViewBuilder(germplasmList, dto.getId(), dto.getTypeId()).build();
		}

		throw new UnsupportedOperationException();
	}

}
