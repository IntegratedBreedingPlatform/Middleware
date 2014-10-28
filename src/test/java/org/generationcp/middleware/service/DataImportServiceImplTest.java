package org.generationcp.middleware.service;

import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.Workbook;
import org.generationcp.middleware.exceptions.WorkbookParserException;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.operation.parser.WorkbookParser;
import org.generationcp.middleware.util.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataImportServiceImplTest {

    public static final int INVALID_VARIABLES_COUNT = 5;
    public static final int VALID_VARIABLES_COUNT = 5;
    @Mock
    private WorkbookParser parser;

    @Mock
    private Workbook workbook;

    @Mock
    private OntologyDataManager ontology;

    @Mock
    private File file;

    @InjectMocks
    private DataImportServiceImpl dataImportService;

    @Test
    public void testStrictParseWorkbookWithGreaterThan32VarNames() throws Exception {
        DataImportServiceImpl moleDataImportService = spy(dataImportService);

        // we just need to test if isTrialInstanceNumberExists works, so lets mock out other dataImportService calls for the moment
        when(workbook.isNursery()).thenReturn(true);

        // tip! do note that spy-ed object still calls the real method, may cause changing internal state as side effect
        when(moleDataImportService.isEntryExists(ontology, workbook.getFactors())).thenReturn(true);
        when(moleDataImportService.isPlotExists(ontology, workbook.getFactors())).thenReturn(true);
        when(moleDataImportService.isTrialInstanceNumberExists(ontology, workbook.getTrialVariables())).thenReturn(true);

        when(workbook.getAllVariables()).thenReturn(initializeTestMeasurementVariables());

        try {
            moleDataImportService.strictParseWorkbook(file, parser, workbook, ontology);
            fail("We expects workbookParserException to be thrown");
        } catch (WorkbookParserException e) {

            verify(moleDataImportService).validateMeasurmentVariableNameLengths(workbook.getAllVariables());

            for (Message error : e.getErrorMessages()) {
                assertEquals("All errors should contain a error.trim.measurement.variable error", error.getMessageKey(), "error.trim.measurement.variable");
            }
        }
    }

    @Test
    public void testValidateMeasurementVariableNameLengths() throws Exception {
        List<MeasurementVariable> measurementVariables = initializeTestMeasurementVariables();

        List<Message> messages = dataImportService.validateMeasurmentVariableNameLengths(measurementVariables);

        assertEquals("we should only have 5 variables with > 32 char length", INVALID_VARIABLES_COUNT,messages.size());

        for (Message message : messages) {
            assertTrue("returned messages should only contain the variables with names > 32",message.getMessageParams()[0].length() > 32);
        }
    }

    @Test
    public void testValidateMeasurementVariableNameLengthsAllShortNames() throws Exception {
        List<MeasurementVariable> measurementVariables = getShortNamedMeasurementVariables();

        List<Message> messages = dataImportService.validateMeasurmentVariableNameLengths(measurementVariables);

        assertEquals("messages should be empty",0,messages.size());
    }

    protected List<MeasurementVariable> initializeTestMeasurementVariables() {
        List<MeasurementVariable> measurementVariables = getShortNamedMeasurementVariables();

        // 5 long names
        for (int i = 0; i < INVALID_VARIABLES_COUNT; i++) {
            MeasurementVariable mv = new MeasurementVariable();

            mv.setName("[" + i + "]_MEASUREMENT_VARIABLE_WITH_NAME_UP_TO_THIRTY_TWO_CHARACTERS");
            measurementVariables.add(mv);
        }
        return measurementVariables;
    }

    private List<MeasurementVariable> getShortNamedMeasurementVariables() {
        List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

        // 5 short names
        for (int i = 0; i < VALID_VARIABLES_COUNT; i++) {
            MeasurementVariable mv = new MeasurementVariable();
            mv.setName("["+ i +"]_SHORT");
            measurementVariables.add(mv);
        }
        return measurementVariables;
    }
}