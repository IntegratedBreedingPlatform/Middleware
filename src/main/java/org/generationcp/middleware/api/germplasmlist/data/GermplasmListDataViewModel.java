package org.generationcp.middleware.api.germplasmlist.data;

import org.generationcp.middleware.pojos.GermplasmListDataView;

public class GermplasmListDataViewModel {

	private final Integer columnId;
	private final boolean isStaticColumn;
	private final boolean isNameColumn;
	private final boolean isDescriptorColumn;
	private final boolean isEntryDetailColumn;

	public GermplasmListDataViewModel(final GermplasmListDataView column) {
		this.columnId = column.getColumnId();
		this.isStaticColumn = column.isStaticColumn();
		this.isNameColumn = column.isNameColumn();
		this.isDescriptorColumn = column.isDescriptorColumn();
		this.isEntryDetailColumn = column.isEntryDetailColumn();
	}

	private GermplasmListDataViewModel(final Integer columnId) {
		this.columnId = columnId;
		this.isStaticColumn = true;
		this.isNameColumn = false;
		this.isDescriptorColumn = false;
		this.isEntryDetailColumn = false;
	}

	public static GermplasmListDataViewModel buildStaticGermplasmListDataViewModel(final Integer staticId) {
		return new GermplasmListDataViewModel(staticId);
	}

	public Integer getColumnId() {
		return columnId;
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
