package org.generationcp.middleware.reports;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.data.initializer.WorkbookTestDataInitializer;
import org.generationcp.middleware.domain.etl.MeasurementData;
import org.generationcp.middleware.domain.etl.MeasurementRow;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaizeStudyManifestTest {
    public static final String TEST_SEASON_VALUE = "Wet season";
    public static final String TEST_COLLABORATOR_NAME = "LeafNode";

    private static final MeasurementVariable SEASON_MEASUREMENT_VARIABLE = new MeasurementVariable("Crop_season_code", "", "test", "test", "Season", "test", TEST_SEASON_VALUE, "");
    private static final MeasurementVariable COLLABORATOR_MEASUREMENT_VARIABLE = new MeasurementVariable("Collaborator", "", "", "", "", "", TEST_COLLABORATOR_NAME, "");
    public static final String TEST_STUDY_NAME = "testStudyName";
    public static final String TEST_PROGRAM_NAME = "testProgram";

    private final MaizeStudyManifest unitUnderTest = new MaizeStudyManifest();


    @Test
    public void testProvideBlankValues() {
        final Map<String, Object> paramMap = new HashMap<>();
        unitUnderTest.provideBlankValues(paramMap);

        Assert.assertFalse("Unable to provide empty entries for known fields in the report", paramMap.isEmpty());

        final Object collaborator =  paramMap.get(MaizeStudyManifest.COLLABORATOR_REPORT_KEY);
        Assert.assertNotNull("Unable to provide empty entries for known field in the report", collaborator);
        Assert.assertTrue("Value for unknown field is not empty", StringUtils.isEmpty((String) collaborator));

    }

    @Test
    public void testBuildBasicReportValues() {
        final Workbook workbook = WorkbookTestDataInitializer.createTestWorkbook(1, new StudyTypeDto("T"), TEST_STUDY_NAME, 1, false);
        final List<MeasurementVariable> conditions = workbook.getStudyConditions();
        final List<MeasurementRow> observations = workbook.getObservations();

        final Map<String, Object> reportParams = new HashMap<>();
        reportParams.put(AbstractReporter.STUDY_CONDITIONS_KEY, conditions);
        reportParams.put(AbstractReporter.DATA_SOURCE_KEY, observations);
        reportParams.put(AbstractReporter.STUDY_OBSERVATIONS_KEY, workbook.getTrialObservations());
        reportParams.put(AbstractReporter.PROGRAM_NAME_ARG_KEY, TEST_PROGRAM_NAME);


        final Map<String, Object> reportValues = this.unitUnderTest.buildJRParams(reportParams, TEST_STUDY_NAME);
        Assert.assertTrue("Report should be able to provide the study name", reportValues.containsKey(AbstractReporter.STUDY_NAME_REPORT_KEY));
        Assert.assertEquals("Unable to provide the expected value for study name", TEST_STUDY_NAME, reportValues.get(AbstractReporter.STUDY_NAME_REPORT_KEY));

        Assert.assertTrue("Report should be able to provide the program name", reportValues.containsKey(MaizeStudyManifest.MAIZE_MANIFEST_PROGRAM_KEY));
        Assert.assertEquals("Unable to provide the expected value for program name", TEST_PROGRAM_NAME, reportValues.get(MaizeStudyManifest.MAIZE_MANIFEST_PROGRAM_KEY));
    }

    @Test
    public void testBuildReportValuesFromEnvironmentSettings() {
        final Workbook workbook = WorkbookTestDataInitializer.createTestWorkbook(1, new StudyTypeDto("T"), TEST_STUDY_NAME, 1, false);
        final List<MeasurementVariable> conditions = workbook.getStudyConditions();
        final List<MeasurementRow> observations = workbook.getObservations();
        final List<MeasurementRow> studyObservations = createTestRowForReport();


        final Map<String, Object> reportParams = new HashMap<>();
        reportParams.put(AbstractReporter.STUDY_CONDITIONS_KEY, conditions);
        reportParams.put(AbstractReporter.DATA_SOURCE_KEY, observations);
        reportParams.put(AbstractReporter.STUDY_OBSERVATIONS_KEY, studyObservations);

        final Map<String, Object> reportValues = this.unitUnderTest.buildJRParams(reportParams, TEST_STUDY_NAME);
        Assert.assertEquals("Unable to provide report with collaborator value", TEST_COLLABORATOR_NAME, reportValues.get(MaizeStudyManifest.COLLABORATOR_REPORT_KEY));
        Assert.assertEquals("Unable to provide report with season value", TEST_SEASON_VALUE, reportValues.get("season"));
    }

    @Test
    public void testBuildReportValuesFromTrialSettings() {
        final Workbook workbook = WorkbookTestDataInitializer.createTestWorkbook(1, new StudyTypeDto("T"), "testStudyName", 1, false);
        final List<MeasurementVariable> conditions = workbook.getStudyConditions();
        final List<MeasurementRow> observations = workbook.getObservations();
        final List<MeasurementRow> trialObservations = workbook.getTrialObservations();

        conditions.add(SEASON_MEASUREMENT_VARIABLE);
        conditions.add(COLLABORATOR_MEASUREMENT_VARIABLE);


        final Map<String, Object> reportParams = new HashMap<>();
        reportParams.put(AbstractReporter.STUDY_CONDITIONS_KEY, conditions);
        reportParams.put(AbstractReporter.DATA_SOURCE_KEY, observations);
        reportParams.put(AbstractReporter.STUDY_OBSERVATIONS_KEY, trialObservations);

        final Map<String, Object> reportValues = this.unitUnderTest.buildJRParams(reportParams, TEST_STUDY_NAME);
        Assert.assertEquals("Unable to provide report with collaborator value", TEST_COLLABORATOR_NAME, reportValues.get(MaizeStudyManifest.COLLABORATOR_REPORT_KEY));
        Assert.assertEquals("Unable to provide report with season value", TEST_SEASON_VALUE, reportValues.get("season"));
    }

    protected List<MeasurementRow> createTestRowForReport() {
        final List<MeasurementRow> rowList = new ArrayList<>();
        final MeasurementRow row = new MeasurementRow();
        final List<MeasurementData> dataList = new ArrayList<>();


        dataList.add(new MeasurementData("COOPERATOR", TEST_COLLABORATOR_NAME, true, "", COLLABORATOR_MEASUREMENT_VARIABLE));

        dataList.add(new MeasurementData("Crop_season_code", TEST_SEASON_VALUE, true, "", SEASON_MEASUREMENT_VARIABLE));

        row.setDataList(dataList);

        rowList.add(row);

        return rowList;
    }

}
