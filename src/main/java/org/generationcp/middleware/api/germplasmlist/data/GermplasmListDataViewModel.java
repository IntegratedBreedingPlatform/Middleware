package org.generationcp.middleware.api.germplasmlist.data;

import org.generationcp.middleware.pojos.GermplasmListColumnCategory;
import org.generationcp.middleware.pojos.GermplasmListDataView;

public class GermplasmListDataViewModel {

	private final GermplasmListColumnCategory category;
	private final Integer typeId;
	private final Integer variableId;
	private final boolean isStaticColumn;
	private final boolean isNameColumn;
	private final boolean isDescriptorColumn;
	private final boolean isEntryDetailColumn;

	public GermplasmListDataViewModel(final GermplasmListDataView column) {
		this.category = column.getCategory();
		this.typeId = column.getTypeId();
		this.variableId = column.getVariableId();
		this.isStaticColumn = column.isStaticColumn();
		this.isNameColumn = column.isNameColumn();
		this.isDescriptorColumn = column.isDescriptorColumn();
		this.isEntryDetailColumn = column.isEntryDetailColumn();
	}

	private GermplasmListDataViewModel(final Integer variableId) {
		this.category = GermplasmListColumnCategory.STATIC;
		this.typeId = null;
		this.variableId = variableId;
		this.isStaticColumn = true;
		this.isNameColumn = false;
		this.isDescriptorColumn = false;
		this.isEntryDetailColumn = false;
	}

	public static GermplasmListDataViewModel buildStaticGermplasmListDataViewModel(final Integer termId) {
		return new GermplasmListDataViewModel(termId);
	}

	public GermplasmListColumnCategory getCategory() {
		return category;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public Integer getVariableId() {
		return variableId;
	}

	public boolean isStaticColumn() {
		return isStaticColumn;
	}

	public boolean isNameColumn() {
		return isNameColumn;
	}

	public boolean isDescriptorColumn() {
		return isDescriptorColumn;
	}

	public boolean isEntryDetailColumn() {
		return isEntryDetailColumn;
	}

}
