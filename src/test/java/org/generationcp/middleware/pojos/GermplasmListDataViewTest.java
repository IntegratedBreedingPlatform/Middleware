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
	private static final Integer VARIABLE_ID = new Random().nextInt();

	@Test
	public void shouldCreateStaticView() {
		final GermplasmListDataView view =
			new GermplasmListDataView(GERMPLASM_LIST, GermplasmListColumnCategory.STATIC, null, VARIABLE_ID);
		this.assertView(view, GermplasmListColumnCategory.STATIC, null, true, false, false, false);
	}

	@Test
	public void shouldCreateNameView() {
		final GermplasmListDataView view =
			new GermplasmListDataView(GERMPLASM_LIST, GermplasmListColumnCategory.NAMES, null, VARIABLE_ID);
		this.assertView(view, GermplasmListColumnCategory.NAMES, null, false, true, false, false);
	}

	@Test
	public void shouldGermplasmDescriptorView() {
		final GermplasmListDataView view1 =
			new GermplasmListDataView(GERMPLASM_LIST, GermplasmListColumnCategory.VARIABLE, VariableType.GERMPLASM_PASSPORT.getId(), VARIABLE_ID);
		this.assertView(view1, GermplasmListColumnCategory.VARIABLE, VariableType.GERMPLASM_PASSPORT.getId(), false, false, true, false);

		final GermplasmListDataView view2 =
			new GermplasmListDataView(GERMPLASM_LIST, GermplasmListColumnCategory.VARIABLE, VariableType.GERMPLASM_ATTRIBUTE.getId(), VARIABLE_ID);
		this.assertView(view2, GermplasmListColumnCategory.VARIABLE, VariableType.GERMPLASM_ATTRIBUTE.getId(), false, false, true, false);
	}

	@Test
	public void shouldCreateEntryDetailsView() {
		final GermplasmListDataView view =
			new GermplasmListDataView(GERMPLASM_LIST, GermplasmListColumnCategory.VARIABLE, 1234, VARIABLE_ID);
		this.assertView(view, GermplasmListColumnCategory.VARIABLE, 1234, false, false, false, true);
	}

	private void assertView(final GermplasmListDataView view, final GermplasmListColumnCategory category,
		final Integer typeId, final boolean isStaticColumn, final boolean isNameColumn, final boolean isDescriptorColumn,
		final boolean isEntryDetailColumn) {
		assertNotNull(view);
		assertThat(view.getList(), is(GERMPLASM_LIST));
		assertThat(view.getCategory(), is(category));
		assertThat(view.getTypeId(), is(typeId));
		assertThat(view.getVariableId(), is(VARIABLE_ID));
		assertThat(view.isStaticColumn(), is(isStaticColumn));
		assertThat(view.isNameColumn(), is(isNameColumn));
		assertThat(view.isDescriptorColumn(), is(isDescriptorColumn));
		assertThat(view.isEntryDetailColumn(), is(isEntryDetailColumn));
	}

}
