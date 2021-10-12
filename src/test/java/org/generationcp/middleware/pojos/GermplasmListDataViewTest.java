package org.generationcp.middleware.pojos;

import org.generationcp.middleware.domain.ontology.VariableType;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class GermplasmListDataViewTest {

	private static final GermplasmList GERMPLASM_LIST = Mockito.mock(GermplasmList.class);
	private static final Integer COLUMN_ID = new Random().nextInt();

	@Test
	public void shouldCreateStaticView() {
		final GermplasmListDataView view = new GermplasmListDataView.GermplasmListDataStaticViewBuilder(GERMPLASM_LIST, COLUMN_ID).build();
		this.assertView(view, COLUMN_ID, null, null, null, COLUMN_ID, true, false, false, false, false);
	}

	@Test
	public void shouldCreateNameView() {
		final GermplasmListDataView view = new GermplasmListDataView.GermplasmListDataNameViewBuilder(GERMPLASM_LIST, COLUMN_ID).build();
		this.assertView(view, null, COLUMN_ID, null, null, COLUMN_ID, false, true, false, false, false);
	}

	@Test
	public void shouldGermplasmDescriptorView() {
		final GermplasmListDataView view1 =
			new GermplasmListDataView.GermplasmListDataVariableViewBuilder(GERMPLASM_LIST, COLUMN_ID,
				VariableType.GERMPLASM_PASSPORT.getId()).build();
		this.assertView(view1, null, null, COLUMN_ID, VariableType.GERMPLASM_PASSPORT.getId(), COLUMN_ID, false, false, true, true, false);

		final GermplasmListDataView view2 =
			new GermplasmListDataView.GermplasmListDataVariableViewBuilder(GERMPLASM_LIST, COLUMN_ID,
				VariableType.GERMPLASM_ATTRIBUTE.getId()).build();
		this.assertView(view2, null, null, COLUMN_ID, VariableType.GERMPLASM_ATTRIBUTE.getId(), COLUMN_ID, false, false, true, true, false);
	}

	@Test
	public void shouldCreateEntryDetailsView() {
		final GermplasmListDataView view =
			new GermplasmListDataView.GermplasmListDataVariableViewBuilder(GERMPLASM_LIST, COLUMN_ID, 1234).build();
		this.assertView(view, null, null, COLUMN_ID, 1234, COLUMN_ID, false, false, true, false, true);
	}

	private void assertView(final GermplasmListDataView view, final Integer staticId, final Integer nameFldno, final Integer cvTermId,
		final Integer typeId, final Integer columnId, final boolean isStaticColumn, final boolean isNameColumn,
		final boolean isVariableColumn, final boolean isDescriptorColumn, final boolean isEntryDetailColumn) {
		assertNotNull(view);
		assertThat(view.getList(), is(GERMPLASM_LIST));
		assertThat(view.getStaticId(), is(staticId));
		assertThat(view.getNameFldno(), is(nameFldno));
		assertThat(view.getCvtermId(), is(cvTermId));
		assertThat(view.getTypeId(), is(typeId));
		assertThat(view.getColumnId(), is(columnId));
		assertThat(view.isStaticColumn(), is(isStaticColumn));
		assertThat(view.isNameColumn(), is(isNameColumn));
		assertThat(view.isVariableColumn(), is(isVariableColumn));
		assertThat(view.isDescriptorColumn(), is(isDescriptorColumn));
		assertThat(view.isEntryDetailColumn(), is(isEntryDetailColumn));
	}

}
