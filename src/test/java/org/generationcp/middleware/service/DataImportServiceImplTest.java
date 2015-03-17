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
import java.util.Arrays;
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
    
    public static final String[] STRINGS_WITH_INVALID_CHARACTERS = new String[]{"1234", "word@", "_+world=", "!!world!!", "&&&"};
    public static final String[] STRINGS_WITH_VALID_CHARACTERS = new String[]{"i_am_groot", "hello123world", "%%bangbang", "something_something", "zawaruldoisbig"};
    private static final String PROGRAM_UUID = "123456789";

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
            moleDataImportService.strictParseWorkbook(file, parser, workbook, ontology, PROGRAM_UUID);
            fail("We expects workbookParserException to be thrown");
        } catch (WorkbookParserException e) {

            verify(moleDataImportService).validateMeasurementVariableName(workbook.getAllVariables());

            final String[] errorTypes = {DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_LENGTH,DataImportServiceImpl.ERROR_INVALID_VARIABLE_NAME_CHARACTERS};
            for (Message error : e.getErrorMessages()) {
                assertTrue("All errors should contain either ERROR_INVALID_VARIABLE_NAME_CHARACTERS or ERROR_INVALID_VARIABLE_NAME_LENGTH", Arrays.asList(errorTypes).contains(error.getMessageKey()));
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

    @Test
    public void testValidateMeasurmentVariableNameCharacters() throws Exception {
        List<MeasurementVariable> measurementVariables = getValidNamedMeasurementVariables();
        measurementVariables.addAll(getInvalidNamedMeasurementVariables());

        List<Message> messages = dataImportService.validateMeasurmentVariableNameCharacters(measurementVariables);

        assertEquals("we should only have messages same size with the STRINGS_WITH_INVALID_CHARACTERS count", STRINGS_WITH_INVALID_CHARACTERS.length,messages.size());

        for (Message message : messages) {
            assertTrue("returned messages should contain the names from the set of invalid strings list", Arrays.asList(STRINGS_WITH_INVALID_CHARACTERS).contains(message.getMessageParams()[0]));
        }
    }

    protected List<MeasurementVariable> initializeTestMeasurementVariables() {
        List<MeasurementVariable> measurementVariables = getShortNamedMeasurementVariables();

        // 5 long names
        for (int i = 0; i < INVALID_VARIABLES_COUNT; i++) {
            MeasurementVariable mv = new MeasurementVariable();

            mv.setName("NUM_" + i + "_MEASUREMENT_VARIABLE_WITH_NAME_UP_TO_THIRTY_TWO_CHARACTERS");
            measurementVariables.add(mv);
        }

        // also add those invalid variables to add to the main test
        measurementVariables.addAll(getInvalidNamedMeasurementVariables());

        return measurementVariables;
    }

    private List<MeasurementVariable> getShortNamedMeasurementVariables() {
        List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

        // 5 short names
        for (int i = 0; i < VALID_VARIABLES_COUNT; i++) {
            MeasurementVariable mv = new MeasurementVariable();
            mv.setName("NUM_"+ i +"_SHORT");
            measurementVariables.add(mv);
        }
        return measurementVariables;
    }

    private List<MeasurementVariable> getInvalidNamedMeasurementVariables() {
        List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

        for (int i = 0; i < STRINGS_WITH_INVALID_CHARACTERS.length; i++) {
            MeasurementVariable mv = new MeasurementVariable();
            mv.setName(STRINGS_WITH_INVALID_CHARACTERS[i]);
            measurementVariables.add(mv);
        }
        return measurementVariables;
    }

    private List<MeasurementVariable> getValidNamedMeasurementVariables() {
        List<MeasurementVariable> measurementVariables = new ArrayList<MeasurementVariable>();

        for (int i = 0; i < STRINGS_WITH_VALID_CHARACTERS.length; i++) {
            MeasurementVariable mv = new MeasurementVariable();
            mv.setName(STRINGS_WITH_VALID_CHARACTERS[i]);
            measurementVariables.add(mv);
        }
        return measurementVariables;
    }


}