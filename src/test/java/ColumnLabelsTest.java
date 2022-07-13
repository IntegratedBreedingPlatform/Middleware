import java.util.Arrays;
import java.util.List;

import org.generationcp.middleware.constant.ColumnLabels;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ColumnLabelsTest {

	private static final String SAMPLE_HEADER = "SAMPLE HEADER";

	@Mock
	private OntologyDataManager ontologyDataManager;

	private static final List<String> ADDABLE_COLUMNS = Arrays.asList(ColumnLabels.PREFERRED_ID.getName(),
			ColumnLabels.PREFERRED_NAME.getName(), ColumnLabels.GERMPLASM_DATE.getName(), ColumnLabels.GERMPLASM_LOCATION.getName(),
			ColumnLabels.BREEDING_METHOD_NAME.getName(), ColumnLabels.BREEDING_METHOD_ABBREVIATION.getName(),
			ColumnLabels.BREEDING_METHOD_NUMBER.getName(), ColumnLabels.BREEDING_METHOD_GROUP.getName(),
			ColumnLabels.FGID.getName(), ColumnLabels.CROSS_FEMALE_PREFERRED_NAME.getName(),
			ColumnLabels.MGID.getName(), ColumnLabels.CROSS_MALE_PREFERRED_NAME.getName(),
			ColumnLabels.GROUP_SOURCE_GID.getName(),ColumnLabels.GROUP_SOURCE_PREFERRED_NAME.getName(),
			ColumnLabels.IMMEDIATE_SOURCE_GID.getName(),ColumnLabels.IMMEDIATE_SOURCE_NAME.getName());

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn(new Term(1, ColumnLabelsTest.SAMPLE_HEADER, "Some Definition")).when(this.ontologyDataManager)
				.getTermById(Matchers.anyInt());
	}

	@Test
	public void testGetAddableGermplasmColumns() {
		Assert.assertEquals(ColumnLabelsTest.ADDABLE_COLUMNS, ColumnLabels.getAddableGermplasmColumns());
	}

	@Test
	public void testGetTermNameFromOntology() {
		final String name = ColumnLabels.GERMPLASM_LOCATION.getTermNameFromOntology(this.ontologyDataManager);
		Mockito.verify(this.ontologyDataManager).getTermById(ColumnLabels.GERMPLASM_LOCATION.getTermId().getId());
		Assert.assertEquals(ColumnLabelsTest.SAMPLE_HEADER, name);
	}

}
