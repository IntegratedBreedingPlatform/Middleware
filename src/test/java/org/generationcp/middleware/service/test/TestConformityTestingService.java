package org.generationcp.middleware.service.test;


import org.generationcp.middleware.domain.conformity.ConformityGermplasmInput;
import org.generationcp.middleware.domain.conformity.UploadInput;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.GenotypicDataManager;
import org.generationcp.middleware.manager.api.PedigreeDataManager;
import org.generationcp.middleware.service.ConformityTestingServiceImpl;
import org.generationcp.middleware.service.api.ConformityTestingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */
@RunWith(JUnit4.class)
public class TestConformityTestingService {


    /**
     * Created by IntelliJ IDEA.
     * User: Daniel Villafuerte
     */


    private ConformityTestingService conformityTestingService;

    @Before
    public void setup() {
        try {
            DatabaseConnectionParameters localParameters = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
            DatabaseConnectionParameters centralParameters = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
            ManagerFactory factory = new ManagerFactory(localParameters, centralParameters);
            PedigreeDataManager pedigreeDataManager = factory.getPedigreeDataManager();
            GenotypicDataManager genotypicDataManager = factory.getGenotypicDataManager();

            conformityTestingService = new ConformityTestingServiceImpl(genotypicDataManager, pedigreeDataManager);
        } catch (Exception e) {
            fail(e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testAll() {
        UploadInput input = new UploadInput();
        input.setParentAGID(1);
        input.setParentBGID(2);

        ConformityGermplasmInput entry = new ConformityGermplasmInput("005_24", 1);
        entry.getMarkerValues().put("SB_01_112", "G");
        entry.getMarkerValues().put("SB_01_161", "G");
        entry.getMarkerValues().put("SB_01_122", "G");

        input.addEntry(entry);

        entry = new ConformityGermplasmInput("093_09", 2);
        entry.getMarkerValues().put("SB_01_112", "A");
        entry.getMarkerValues().put("SB_01_161", "A/G");
        entry.getMarkerValues().put("SB_01_122", "A/G");
        input.addEntry(entry);

        entry = new ConformityGermplasmInput("C1_001-01", 3);
        entry.getMarkerValues().put("SB_01_112", "-");
        entry.getMarkerValues().put("SB_01_161", "-");
        entry.getMarkerValues().put("SB_01_122", "G");
        input.addEntry(entry);

        entry = new ConformityGermplasmInput("C1_001-02", 4);
        entry.getMarkerValues().put("SB_01_112", "A/G");
        entry.getMarkerValues().put("SB_01_161", "G");
        entry.getMarkerValues().put("SB_01_122", "A/G");
        input.addEntry(entry);

        entry = new ConformityGermplasmInput("C1_001-03", 5);
        entry.getMarkerValues().put("SB_01_112", "A");
        entry.getMarkerValues().put("SB_01_161", "A");
        entry.getMarkerValues().put("SB_01_122", "G");
        input.addEntry(entry);

        entry = new ConformityGermplasmInput("C1_001-04", 6);
        entry.getMarkerValues().put("SB_01_112", "-");
        entry.getMarkerValues().put("SB_01_161", "G");
        entry.getMarkerValues().put("SB_01_122", "G");
        input.addEntry(entry);

        try {
            Map<Integer, Map<String, String>> output = conformityTestingService.testConformity(input);

            // verify that problematic entry is present
            assertTrue(output.containsKey(5));

            // verify that the correct count of problematic markers are noted
            assertTrue(output.get(5).size() == 2);

            // verify that passed entries are not included
            assertTrue(output.size() == 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
