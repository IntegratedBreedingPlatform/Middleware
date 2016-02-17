
package org.generationcp.middleware.service;

import org.generationcp.middleware.data.initializer.GermplasmDataManagerDataInitializer;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.GermplasmPedigreeTreeNode;
import org.generationcp.middleware.reports.AbstractReporter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceImplTest {

	@Mock
	private GermplasmDataManager germplasmDataManager;

	private final ReportServiceImpl reportService = new ReportServiceImpl();

	public static final int TEST_GERMPLASM_LIST_ID = 2;
	public static final int TEST_STUDY_ID = 1;

	@Test
	public void testRetrieveLocationIdFromConditionReturnsNullForEmptyString() {
		final MeasurementVariable condition = new MeasurementVariable();
		condition.setTermId(TermId.LOCATION_ID.getId());
		condition.setValue("");
		Assert.assertNull("Expecting a null for location id when the value of LOCATION ID variable is empty string.",
				this.reportService.retrieveLocationIdFromCondition(condition, TermId.LOCATION_ID));
	}

	@Test
	public void testRetrieveLocationIdFromConditionReturnsAnIntegerForValidNumber() {
		final MeasurementVariable condition = new MeasurementVariable();
		condition.setTermId(TermId.LOCATION_ID.getId());
		final int locationId = 1;
		condition.setValue(String.valueOf(locationId));
		Assert.assertEquals("Expecting an integer for location id when the value of LOCATION ID variable is a valid number > 0.",
				locationId, this.reportService.retrieveLocationIdFromCondition(condition, TermId.LOCATION_ID).intValue());
	}

	@Test
	public void testExtractGermplasmListData() {
		final Map<String, Object> data = reportService.extractGermplasmListData(TEST_GERMPLASM_LIST_ID);
		Assert.assertNotNull("Data extracter does not provide non null value for required report generation parameter",
				data.get(AbstractReporter.STUDY_CONDITIONS_KEY));
		Assert.assertNotNull("Data extracter does not provide non null value for required report generation parameter",
				data.get(AbstractReporter.DATA_SOURCE_KEY));
	}

	@Test
	public void testExtractParentDataSingleParent() {
		// we use partial mocking on the ReportServiceImplementation so as to be able to mock the GermplasmDataManager object
		final ReportServiceImpl unitUnderTest = Mockito.mock(ReportServiceImpl.class);
		Mockito.doReturn(this.germplasmDataManager).when(unitUnderTest).getGermplasmDataManager();

		final List<MeasurementRow> measurementRowList = createMeasurementList();
		final Map<Integer, GermplasmPedigreeTreeNode> pedigreeTreeNodeMap = GermplasmDataManagerDataInitializer.createTreeNodeMap(true);

		Mockito.doReturn(pedigreeTreeNodeMap).when(this.germplasmDataManager).getDirectParentsForStudy(TEST_STUDY_ID);
		Mockito.doCallRealMethod().when(unitUnderTest).appendParentsInformation(TEST_STUDY_ID, measurementRowList);

		unitUnderTest.appendParentsInformation(TEST_STUDY_ID, measurementRowList);

		final MeasurementRow row = measurementRowList.get(0);
		Assert.assertEquals("Female selection history should be blank if female parent information is not provided in database", "", row
				.getMeasurementData(AbstractReporter.FEMALE_SELECTION_HISTORY_KEY).getValue());
		Assert.assertEquals("Male selection history not properly filled in from provided information",
				GermplasmDataManagerDataInitializer.MALE_SELECTION_HISTORY,
				row.getMeasurementData(AbstractReporter.MALE_SELECTION_HISTORY_KEY).getValue());

	}

	protected List<MeasurementRow> createMeasurementList() {
		final List<MeasurementRow> rowList = new ArrayList<>();
		final MeasurementRow row = new MeasurementRow();
		final List<MeasurementData> dataList = new ArrayList<>();
		final MeasurementData data = new MeasurementData("GID", GermplasmDataManagerDataInitializer.TEST_GID.toString());
		dataList.add(data);

		row.setDataList(dataList);
		rowList.add(row);

		return rowList;
	}
}
