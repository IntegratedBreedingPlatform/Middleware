package org.generationcp.middleware.manager.test;

import com.mchange.v2.c3p0.DriverManagerDataSourceFactory;
import org.generationcp.middleware.domain.mbdt.SelectedGenotypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.MBDTDataManager;
import org.generationcp.middleware.pojos.mbdt.MBDTGeneration;
import org.generationcp.middleware.pojos.mbdt.MBDTProjectData;
import org.generationcp.middleware.pojos.mbdt.SelectedGenotype;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */


public class TestMBDTDataManager
        extends TestOutputFormatter {

    private MBDTDataManager dut;
    private DataSource dataSource;

    public static final Integer SAMPLE_PROJECT_ID = -1;
    public static final String SAMPLE_PROJECT_NAME = "SAMPLE_PROJECT";
    public static final String SAMPLE_GENERATION_NAME = "SAMPLE GENERATION";
    public static final int SAMPLE_DATASET_ID = -1;
    public static final int SAMPLE_GENERATION_ID = -1;
    public static final int[] SAMPLE_SELECTED_MARKER_IDS = new int[]{-1, -2, -3};
    public static final int[] SAMPLE_SELECTED_ACCESSION_GIDS = new int[]{1, 2, 3, 4, 5, 6};
    public static final List<Integer> SAMPLE_PARENT_GIDS = new ArrayList<Integer>();
    private static ManagerFactory managerFactory;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "local");

        DatabaseConnectionParameters central = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "central");
        managerFactory = new ManagerFactory(local, central);

        SAMPLE_PARENT_GIDS.add(4);
        SAMPLE_PARENT_GIDS.add(5);
        SAMPLE_PARENT_GIDS.add(6);
    }

    @Before
    public void prepareDatabaseItems() throws Exception {
        dut = managerFactory.getMbdtDataManager();
        DatabaseConnectionParameters local = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "local");

        dataSource = DriverManagerDataSourceFactory.create(local.getDriverName(), local.getUrl(), local.getUsername(), local.getPassword());
    }

    protected void executeUpdate(String sql) throws Exception {
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();

            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            fail(e.getMessage());
        } finally {
            closeDatabaseResources(conn, stmt, null);
        }
    }

    protected void insertSampleProjectData() throws Exception {
        executeUpdate("INSERT INTO MBDT_PROJECT VALUES (" + SAMPLE_PROJECT_ID + ", '" + SAMPLE_PROJECT_NAME + "', 0, null, null, null, null, null, null)");
    }

    protected void insertSampleGenerationData() throws Exception {
        executeUpdate("INSERT INTO mbdt_generations VALUES (" + SAMPLE_GENERATION_ID + ", '" + SAMPLE_GENERATION_NAME + "', " + SAMPLE_PROJECT_ID + ", " + SAMPLE_DATASET_ID + ")");
    }

    protected void deleteSampleGenerationData() throws Exception {
        executeUpdate("DELETE FROM mbdt_generations WHERE generation_id = " + SAMPLE_GENERATION_ID);
    }

    protected void deleteSampleProjectData() throws Exception {
        executeUpdate("DELETE FROM mbdt_project WHERE project_id = " + SAMPLE_PROJECT_ID);
    }

    protected void insertSampleMarkerData() throws Exception {
        int i = -1;
        for (int markerId : SAMPLE_SELECTED_MARKER_IDS) {
            executeUpdate("INSERT INTO mbdt_selected_markers VALUES(" + i + ", " + SAMPLE_GENERATION_ID + ", " + markerId + ")");
            i--;
        }
    }

    protected void insertSampleAccessionData() throws Exception {
        int i = -1;
        for (int sampleSelectedAccessionGid : SAMPLE_SELECTED_ACCESSION_GIDS) {
            executeUpdate("INSERT INTO mbdt_selected_genotypes VALUES(" + i + ", " + SAMPLE_GENERATION_ID + ", " + sampleSelectedAccessionGid + ", 'SR')");
            i--;
        }
    }

    protected void insertSampleParentData() throws Exception {
        int i = -1;
        for (int sampleSelectedAccessionGid : SAMPLE_PARENT_GIDS) {
            executeUpdate("INSERT INTO mbdt_selected_genotypes VALUES(" + i + ", " + SAMPLE_GENERATION_ID + ", " + sampleSelectedAccessionGid + ", 'D')");
            i--;
        }
    }

    protected void deleteSampleMarkerData() throws Exception {
        for (int markerId : SAMPLE_SELECTED_MARKER_IDS) {
            executeUpdate("DELETE FROM mbdt_selected_markers WHERE marker_id = " + markerId);
        }
    }

    protected void deleteSampleAccessionData() throws Exception {
        for (int gid : SAMPLE_SELECTED_ACCESSION_GIDS) {
            executeUpdate("DELETE FROM mbdt_selected_genotypes WHERE gid = " + gid);
        }
    }

    @Test
    public void testRetrieveProject() throws Exception {
        // set up dummy data in database
        Connection conn = null;
        Statement stmt = null;

        try {
            insertSampleProjectData();

            MBDTProjectData retrieved = dut.getProjectData(SAMPLE_PROJECT_ID);

            assertNotNull(retrieved);

            assertEquals(SAMPLE_PROJECT_NAME, retrieved.getProjectName());


        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            deleteSampleProjectData();
            closeDatabaseResources(conn, stmt, null);
        }
    }

    @Test
    public void testSetProjectPositive() throws Exception {

        MBDTProjectData newProject = new MBDTProjectData(null, SAMPLE_PROJECT_NAME, 0, null, null, null);

        dut.clear();
        Integer generatedId = dut.setProjectData(newProject);
        assertNotNull(generatedId);
        assertTrue(generatedId < 0);

        // clean up operation
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = dataSource.getConnection();
            stmt = connection.createStatement();

            // check on the db side if the generated project really exists
            rs = stmt.executeQuery("SELECT pname FROM mbdt_project WHERE project_id = " + generatedId);

            if (rs.next()) {
                String retrievedName = rs.getString("pname");
                assertNotNull(retrievedName);
                assertEquals(retrievedName, SAMPLE_PROJECT_NAME);
            } else {
                fail("Unable to properly retrieve 'generated' project");
            }

            // perform clean up
            stmt.execute("DELETE FROM mbdt_project WHERE project_id = " + generatedId);

        } catch (SQLException e) {
            fail(e.getMessage());
            e.printStackTrace();
        } finally {
            closeDatabaseResources(connection, stmt, rs);
        }
    }

    @Test
    public void testSetProjectNegative() {

        MBDTProjectData newProject = new MBDTProjectData(null, null, 0, null, null, null);

        Integer generatedId = null;
        try {
            generatedId = dut.setProjectData(newProject);

            fail("Should not allow saving of null project name");
        } catch (MiddlewareQueryException e) {
            // an exception is actually the expected flow
        }

    }

    @Test
    public void testAddGeneration() throws Exception {

        // check the database for correct retrieval

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            insertSampleProjectData();
            MBDTGeneration generation = new MBDTGeneration();
            generation.setGenotypeDatasetID(SAMPLE_DATASET_ID);
            generation.setGenerationName(SAMPLE_GENERATION_NAME);
            generation = dut.setGeneration(SAMPLE_PROJECT_ID, generation);

            assertNotNull(generation);
            assertEquals(SAMPLE_GENERATION_NAME, generation.getGenerationName());
            assertNotNull(generation.getGenerationID());
            assertTrue(generation.getGenerationID() < 0);

            conn = dataSource.getConnection();
            stmt = conn.createStatement();

            rs = stmt.executeQuery("SELECT gname from mbdt_generations WHERE generation_id = " + generation.getGenerationID());

            if (rs.next()) {
                String generationName = rs.getString("gname");
                assertNotNull(generationName);
                assertEquals(SAMPLE_GENERATION_NAME, generationName);
            } else {
                fail("Unable to properly create generation entry");
            }


            stmt.executeUpdate("DELETE FROM mbdt_generations where generation_id = " + generation.getGenerationID());


        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            deleteSampleProjectData();
            closeDatabaseResources(conn, stmt, rs);
        }
    }

    @Test
    public void testRetrieveGeneration() throws Exception {
        try {
            insertSampleProjectData();
            insertSampleGenerationData();
            MBDTGeneration generation = dut.getGeneration(SAMPLE_GENERATION_ID);

            assertNotNull(generation);
            assertEquals(SAMPLE_GENERATION_NAME, generation.getGenerationName());
            assertTrue(SAMPLE_DATASET_ID == generation.getGenotypeDatasetID());
        } catch (MiddlewareQueryException e) {
            fail(e.getMessage());
            e.printStackTrace();
        } finally {
            deleteSampleGenerationData();
            deleteSampleProjectData();
        }
    }


    // added a test case for retrieving multiple generations
    @Test
    public void testRetrieveGenerations() throws Exception {
        try {
            insertSampleProjectData();
            insertSampleGenerationData();

            List<MBDTGeneration> generations = dut.getGenerations(SAMPLE_PROJECT_ID);

            assertNotNull(generations);
            assertTrue(generations.size() == 1);

            MBDTGeneration generation = generations.get(0);

            assertEquals(SAMPLE_GENERATION_NAME, generation.getGenerationName());
            assertTrue(SAMPLE_DATASET_ID == generation.getGenotypeDatasetID());
        } catch (MiddlewareQueryException e) {
            fail(e.getMessage());
            e.printStackTrace();
        } finally {
            deleteSampleGenerationData();
            deleteSampleProjectData();
        }
    }

    @Test
    public void testSetSelectedMarker() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        List<Integer> testMarkerIDs = new ArrayList<Integer>();

        StringBuffer sqlString = new StringBuffer("SELECT marker_id, sm_id FROM mbdt_selected_markers mark INNER JOIN mbdt_generations")
                .append(" gen ON (mark.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
                .append(" WHERE marker_id in (");

        for (int i = 0; i < SAMPLE_SELECTED_MARKER_IDS.length; i++) {
            testMarkerIDs.add(SAMPLE_SELECTED_MARKER_IDS[i]);

            if (i != 0) {
                sqlString.append(",");
            }

            sqlString.append(SAMPLE_SELECTED_MARKER_IDS[i]);
        }

        sqlString.append(")");


        try {
            insertSampleProjectData();
            insertSampleGenerationData();

            // workaround for Hibernate
            MBDTProjectData proj = dut.getProjectData(SAMPLE_PROJECT_ID);
            dut.setProjectData(proj);
            MBDTGeneration generation = dut.getGeneration(SAMPLE_GENERATION_ID);
            dut.setGeneration(SAMPLE_PROJECT_ID, generation);

            dut.setMarkerStatus(SAMPLE_GENERATION_ID, testMarkerIDs);

            conn = dataSource.getConnection();
            stmt = conn.createStatement();

            rs = stmt.executeQuery(sqlString.toString());

            int recordCount = 0;

            // clear list to make way for storage of ids
            testMarkerIDs.clear();

            while (rs.next()) {
                testMarkerIDs.add(rs.getInt("sm_id"));
                recordCount++;
            }

            assertTrue(recordCount == SAMPLE_SELECTED_MARKER_IDS.length);

            sqlString = new StringBuffer("DELETE FROM mbdt_selected_markers where sm_id IN(");

            for (int i = 0; i < testMarkerIDs.size(); i++) {
                if (i != 0) {
                    sqlString.append(",");
                }

                sqlString.append(testMarkerIDs.get(i));
            }

            sqlString.append(")");

            stmt.executeUpdate(sqlString.toString());
        } catch (MiddlewareQueryException e) {
            fail(e.getMessage());
            e.printStackTrace();
        } finally {
            deleteSampleGenerationData();
            deleteSampleProjectData();
            closeDatabaseResources(conn, stmt, rs);
        }
    }

    @Test
    public void testGetSelectedMarker() throws Exception {
        try {
            insertSampleProjectData();
            insertSampleGenerationData();
            insertSampleMarkerData();
            List<Integer> selectedMarkerIDs = dut.getMarkerStatus(SAMPLE_GENERATION_ID);


            assertNotNull(selectedMarkerIDs);
            assertTrue(SAMPLE_SELECTED_MARKER_IDS.length == selectedMarkerIDs.size());

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            deleteSampleMarkerData();
            deleteSampleGenerationData();
            deleteSampleProjectData();
        }
    }

    @Test
    public void testGetSelectedAccessionNonExistingGenerationID() throws Exception {
        try {
            insertSampleProjectData();
            insertSampleGenerationData();
            insertSampleAccessionData();

            // attempt to retrieve accesions for non existing generation
            List<SelectedGenotype> accessions = dut.getSelectedAccession(Integer.MAX_VALUE);
            fail("Unable to recognize non existing generation ID");
        } catch (MiddlewareQueryException e) {

        } finally {
            deleteSampleAccessionData();
            deleteSampleGenerationData();
            deleteSampleProjectData();
        }
    }

    @Test
    public void testGetSelectedAccessions() throws Exception {
        try {
            insertSampleProjectData();
            insertSampleGenerationData();
            insertSampleAccessionData();
            List<SelectedGenotype> accessions = dut.getSelectedAccession(SAMPLE_GENERATION_ID);

            assertNotNull(accessions);
            assertTrue(SAMPLE_SELECTED_ACCESSION_GIDS.length == accessions.size());

            for (SelectedGenotype accession : accessions) {
                /*assertEquals(SelectedGenotypeEnum.SR, accession.getType());*/

                // selected accessions are now entries that have the S prefix to them
                assertTrue(accession.getType().equals(SelectedGenotypeEnum.SR) || accession.getType().equals(SelectedGenotypeEnum.SD));
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            deleteSampleAccessionData();
            deleteSampleGenerationData();
            deleteSampleProjectData();
        }
    }

    @Test
    public void testGetParents() throws Exception {
        try {
            insertSampleProjectData();
            insertSampleGenerationData();
            insertSampleParentData();
            List<SelectedGenotype> parents = dut.getParentData(SAMPLE_GENERATION_ID);

            assertNotNull(parents);
            assertTrue(SAMPLE_PARENT_GIDS.size() == parents.size());

            for (SelectedGenotype accession : parents) {
                assertTrue(SAMPLE_PARENT_GIDS.contains(accession.getGid()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            deleteSampleAccessionData();
            deleteSampleGenerationData();
            deleteSampleProjectData();
        }
    }

    @Test
    public void testSetSelectedAccessionNonExisting() throws Exception {

        List<Integer> gidList = new ArrayList<Integer>();

        StringBuffer sqlString = new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
                .append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
                .append(" WHERE gid in(");

        for (int i = 0; i < SAMPLE_SELECTED_ACCESSION_GIDS.length; i++) {
            gidList.add(SAMPLE_SELECTED_ACCESSION_GIDS[i]);

            if (i != 0) {
                sqlString.append(",");
            }

            sqlString.append(SAMPLE_SELECTED_ACCESSION_GIDS[i]);
        }

        sqlString.append(")");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // workaround for Hibernate : associate inserted data to Hibernate session to avoid problems later on
            dut.clear();
            MBDTProjectData newProject = new MBDTProjectData(SAMPLE_PROJECT_ID, SAMPLE_PROJECT_NAME, 0, null, null, null);
            dut.setProjectData(newProject);

            MBDTGeneration generation = new MBDTGeneration(SAMPLE_GENERATION_NAME, newProject, SAMPLE_DATASET_ID);
            generation.setGenerationID(SAMPLE_GENERATION_ID);
            dut.setGeneration(SAMPLE_PROJECT_ID, generation);


            dut.setSelectedAccessions(SAMPLE_GENERATION_ID, gidList);

            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString.toString());

            int recordCount = 0;
            gidList.clear();

            while (rs.next()) {
                recordCount++;
                gidList.add(rs.getInt("sg_id"));

                assertEquals("SR", rs.getString("sg_type"));
            }

            assertTrue(recordCount == SAMPLE_SELECTED_ACCESSION_GIDS.length);

            // clean up
            sqlString = new StringBuffer("DELETE FROM mbdt_selected_genotypes WHERE sg_id IN (");

            for (int i = 0; i < gidList.size(); i++) {
                if (i != 0) {
                    sqlString.append(",");
                }

                sqlString.append(gidList.get(i));
            }

            sqlString.append(")");
            stmt.executeUpdate(sqlString.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            deleteSampleGenerationData();
            deleteSampleProjectData();
            closeDatabaseResources(conn, stmt, rs);
        }
    }

    @Test
    public void testSetParentDataEmptyList() throws Exception {
        List<Integer> gidList = new ArrayList<Integer>();

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // workaround for Hibernate : associate inserted data to Hibernate session to avoid problems later on
            dut.clear();
            MBDTProjectData newProject = new MBDTProjectData(SAMPLE_PROJECT_ID, SAMPLE_PROJECT_NAME, 0, null, null, null);
            dut.setProjectData(newProject);

            MBDTGeneration generation = new MBDTGeneration(SAMPLE_GENERATION_NAME, newProject, SAMPLE_DATASET_ID);
            generation.setGenerationID(SAMPLE_GENERATION_ID);
            dut.setGeneration(SAMPLE_PROJECT_ID, generation);

            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT count(*) as genotypeCount from mbdt_selected_genotypes");

            assert(rs.next());
            int genotypeCount = rs.getInt("genotypeCount");

            dut.setSelectedAccessions(SAMPLE_GENERATION_ID, gidList);

            rs = stmt.executeQuery("SELECT count(*) as genotypeCount from mbdt_selected_genotypes");
            assert(rs.next());

            assertEquals(genotypeCount, rs.getInt("genotypeCount"));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            deleteSampleGenerationData();
            deleteSampleProjectData();
            closeDatabaseResources(conn, stmt, rs);
        }
    }

    @Test
    public void testSetParentDataNonExistingGenerationID() throws Exception {
        try {
            dut.setParentData(Integer.MAX_VALUE, SelectedGenotypeEnum.R, SAMPLE_PARENT_GIDS);
            fail("Unable to catch non existing generation ID");
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSetSelectedAccessionCurrentlyExistingNonSelected() throws Exception {

        List<Integer> gidList = new ArrayList<Integer>();

        StringBuffer sqlString = new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
                .append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
                .append(" WHERE gid in(");

        for (int i = 0; i < SAMPLE_PARENT_GIDS.size(); i++) {
            gidList.add(SAMPLE_PARENT_GIDS.get(i));

            if (i != 0) {
                sqlString.append(",");
            }

            sqlString.append(SAMPLE_PARENT_GIDS.get(i));
        }

        sqlString.append(")");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;


        try {
            // workaround for Hibernate : associate inserted data to Hibernate session to avoid problems later on
            dut.clear();
            MBDTProjectData newProject = new MBDTProjectData(SAMPLE_PROJECT_ID, SAMPLE_PROJECT_NAME, 0, null, null, null);
            dut.setProjectData(newProject);

            MBDTGeneration generation = new MBDTGeneration(SAMPLE_GENERATION_NAME, newProject, SAMPLE_DATASET_ID);
            generation.setGenerationID(SAMPLE_GENERATION_ID);
            dut.setGeneration(SAMPLE_PROJECT_ID, generation);

            // insert parent data into mbdt_selected_genotypes table
            dut.setParentData(SAMPLE_GENERATION_ID, SelectedGenotypeEnum.D, new ArrayList<Integer>(gidList));

            // using the same gid list, pass them into the setSelectedAccessions method
            dut.setSelectedAccessions(SAMPLE_GENERATION_ID, new ArrayList<Integer>(gidList));

            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString.toString());

            int recordCount = 0;
            gidList.clear();

            while (rs.next()) {
                recordCount++;
                gidList.add(rs.getInt("sg_id"));

                // the set selected accessions method must have changed the sg type of the provided gids from the previous D to something with the selected prefix 'S'
                assertEquals("SD", rs.getString("sg_type"));

                Integer gid = rs.getInt("gid");
                assertTrue(SAMPLE_PARENT_GIDS.contains(gid));
            }

            assertTrue(SAMPLE_PARENT_GIDS.size() == recordCount);

            // clean up
            sqlString = new StringBuffer("DELETE FROM mbdt_selected_genotypes WHERE sg_id IN (");

            for (int i = 0; i < gidList.size(); i++) {
                if (i != 0) {
                    sqlString.append(",");
                }

                sqlString.append(gidList.get(i));
            }

            sqlString.append(")");
            stmt.executeUpdate(sqlString.toString());

        } catch (MiddlewareQueryException e) {
            fail(e.getMessage());
        } finally {
            deleteSampleGenerationData();
            deleteSampleProjectData();
            closeDatabaseResources(conn, stmt, rs);
        }
    }

    @Test
    public void testSetSelectedAccessionCurrentlyExistingSelected() throws Exception {

        List<Integer> gidList = new ArrayList<Integer>();

        StringBuffer sqlString = new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
                .append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
                .append(" WHERE gid in(");

        for (int i = 0; i < SAMPLE_PARENT_GIDS.size(); i++) {
            gidList.add(SAMPLE_PARENT_GIDS.get(i));

            if (i != 0) {
                sqlString.append(",");
            }

            sqlString.append(SAMPLE_PARENT_GIDS.get(i));
        }

        sqlString.append(")");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;


        try {
            // workaround for Hibernate : associate inserted data to Hibernate session to avoid problems later on
            dut.clear();
            MBDTProjectData newProject = new MBDTProjectData(SAMPLE_PROJECT_ID, SAMPLE_PROJECT_NAME, 0, null, null, null);
            dut.setProjectData(newProject);

            MBDTGeneration generation = new MBDTGeneration(SAMPLE_GENERATION_NAME, newProject, SAMPLE_DATASET_ID);
            generation.setGenerationID(SAMPLE_GENERATION_ID);
            dut.setGeneration(SAMPLE_PROJECT_ID, generation);

            // insert parent data into mbdt_selected_genotypes table
            dut.setParentData(SAMPLE_GENERATION_ID, SelectedGenotypeEnum.D, gidList);

            // using the same gid list, pass them into the setSelectedAccessions method
            dut.setSelectedAccessions(SAMPLE_GENERATION_ID, new ArrayList<Integer>(gidList));

            // make a call to selected accessions a second time. the items that were marked as selected should go back to being selected
            dut.setSelectedAccessions(SAMPLE_GENERATION_ID, new ArrayList<Integer>(gidList));

            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString.toString());

            int recordCount = 0;
            gidList.clear();

            while (rs.next()) {
                recordCount++;
                gidList.add(rs.getInt("sg_id"));

                // the set selected accessions method must have changed the sg type of the provided gids from the previous D to something with the selected prefix 'S'
                assertEquals("D", rs.getString("sg_type"));

                Integer gid = rs.getInt("gid");
                assertTrue(SAMPLE_PARENT_GIDS.contains(gid));
            }

            assertTrue(SAMPLE_PARENT_GIDS.size() == recordCount);

            // clean up
            sqlString = new StringBuffer("DELETE FROM mbdt_selected_genotypes WHERE sg_id IN (");

            for (int i = 0; i < gidList.size(); i++) {
                if (i != 0) {
                    sqlString.append(",");
                }

                sqlString.append(gidList.get(i));
            }

            sqlString.append(")");
            stmt.executeUpdate(sqlString.toString());

        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            deleteSampleGenerationData();
            deleteSampleProjectData();
            closeDatabaseResources(conn, stmt, rs);
        }
    }


    @Test
    public void testSetParentNonExisting() throws Exception {


        StringBuffer sqlString = new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
                .append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
                .append(" WHERE gid in(");

        List<Integer> gidList = new ArrayList<Integer>();
        for (int i = 0; i < SAMPLE_PARENT_GIDS.size(); i++) {
            gidList.add(SAMPLE_PARENT_GIDS.get(i));

            if (i != 0) {
                sqlString.append(",");
            }

            sqlString.append(SAMPLE_PARENT_GIDS.get(i));
        }

        sqlString.append(")");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            dut.clear();
            MBDTProjectData newProject = new MBDTProjectData(SAMPLE_PROJECT_ID, SAMPLE_PROJECT_NAME, 0, null, null, null);
            dut.setProjectData(newProject);

            MBDTGeneration generation = new MBDTGeneration(SAMPLE_GENERATION_NAME, newProject, SAMPLE_DATASET_ID);
            generation.setGenerationID(SAMPLE_GENERATION_ID);
            dut.setGeneration(SAMPLE_PROJECT_ID, generation);


            dut.setParentData(SAMPLE_GENERATION_ID, SelectedGenotypeEnum.R, gidList);

            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString.toString());

            int recordCount = 0;
            gidList.clear();

            while (rs.next()) {
                recordCount++;
                gidList.add(rs.getInt("sg_id"));
                assertEquals("R", rs.getString("sg_type"));
            }

            assertTrue(recordCount == SAMPLE_PARENT_GIDS.size());

            // clean up
            sqlString = new StringBuffer("DELETE FROM mbdt_selected_genotypes WHERE sg_id IN (");

            for (int i = 0; i < gidList.size(); i++) {
                if (i != 0) {
                    sqlString.append(",");
                }

                sqlString.append(gidList.get(i));
            }

            sqlString.append(")");
            stmt.executeUpdate(sqlString.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            deleteSampleGenerationData();
            deleteSampleProjectData();
            closeDatabaseResources(conn, stmt, rs);
        }

    }

    /*
    No longer applicable with the change to the selected genotype
    @Test
    public void testSetParentNegativeNonParentEnumType() throws Exception {

        List<Integer> gidList = new ArrayList<Integer>();
        for (int i = 0; i < SAMPLE_PARENT_GIDS.size(); i++) {
            gidList.add(SAMPLE_PARENT_GIDS.get(i));

        }

        try {
            dut.clear();
            MBDTProjectData newProject = new MBDTProjectData(SAMPLE_PROJECT_ID, SAMPLE_PROJECT_NAME, 0, null, null, null);
            dut.setProjectData(newProject);

            MBDTGeneration generation = new MBDTGeneration(SAMPLE_GENERATION_NAME, newProject, SAMPLE_DATASET_ID);
            dut.setGeneration(SAMPLE_PROJECT_ID, generation);

            dut.setParentData(SAMPLE_PROJECT_ID, SAMPLE_DATASET_ID, SelectedGenotypeEnum.SA, gidList);
            fail("Not able to catch error, setting parent with non parent genotype type");
        } catch (MiddlewareQueryException e) {

        } finally {
            deleteSampleGenerationData();
            deleteSampleProjectData();
        }
    }*/

    @Test
    public void testSetParentAlreadyExistingNonSelected() throws Exception {
        StringBuffer sqlString = new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
                .append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
                .append(" WHERE gid in(");
        List<Integer> gidList = new ArrayList<Integer>();
        for (int i = 0; i < SAMPLE_PARENT_GIDS.size(); i++) {
            gidList.add(SAMPLE_PARENT_GIDS.get(i));

            if (i != 0) {
                sqlString.append(",");
            }

            sqlString.append(SAMPLE_PARENT_GIDS.get(i));

        }

        sqlString.append(")");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            dut.clear();
            MBDTProjectData newProject = new MBDTProjectData(SAMPLE_PROJECT_ID, SAMPLE_PROJECT_NAME, 0, null, null, null);
            dut.setProjectData(newProject);

            MBDTGeneration generation = new MBDTGeneration(SAMPLE_GENERATION_NAME, newProject, SAMPLE_DATASET_ID);
            dut.setGeneration(SAMPLE_PROJECT_ID, generation);


            dut.setParentData(SAMPLE_GENERATION_ID, SelectedGenotypeEnum.R, new ArrayList<Integer>(SAMPLE_PARENT_GIDS));

            // make another call to setParentData. since the gid entries should already be existing, it should just modify those items
            dut.setParentData(SAMPLE_GENERATION_ID, SelectedGenotypeEnum.D, new ArrayList<Integer>(SAMPLE_PARENT_GIDS));

            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString.toString());

            int recordCount = 0;

            while (rs.next()) {
                recordCount++;
                String type = rs.getString("sg_type");

                assertEquals("D", type);
            }

            // clean up
            sqlString = new StringBuffer("DELETE FROM mbdt_selected_genotypes WHERE sg_id IN (");

            for (int i = 0; i < gidList.size(); i++) {
                if (i != 0) {
                    sqlString.append(",");
                }

                sqlString.append(gidList.get(i));
            }

            sqlString.append(")");
            stmt.executeUpdate(sqlString.toString());
        } catch (MiddlewareQueryException e) {
            fail(e.getMessage());
        } finally {
            deleteSampleAccessionData();
            deleteSampleGenerationData();
            deleteSampleProjectData();
        }
    }

    @Test
    public void testSetParentDuplicateGIDInParameterList() throws Exception {
        StringBuffer sqlString = new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
                .append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
                .append(" WHERE gid in(");

        List<Integer> gidList = new ArrayList<Integer>();
        for (int i = 0; i < SAMPLE_PARENT_GIDS.size(); i++) {
            gidList.add(SAMPLE_PARENT_GIDS.get(i));

            if (i != 0) {
                sqlString.append(",");
            }

            sqlString.append(SAMPLE_PARENT_GIDS.get(i));
        }

        // add a duplicate gid to the list
        gidList.add(gidList.get(0));

        sqlString.append(")");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            dut.clear();
            MBDTProjectData newProject = dut.getProjectData(SAMPLE_PROJECT_ID);
            if (newProject == null) {
                newProject = new MBDTProjectData(SAMPLE_PROJECT_ID, SAMPLE_PROJECT_NAME, 0, null, null, null);
                dut.setProjectData(newProject);
            }

            MBDTGeneration generation = dut.getGeneration(SAMPLE_GENERATION_ID);
            if (generation == null) {
                generation = new MBDTGeneration(SAMPLE_GENERATION_NAME, newProject, SAMPLE_DATASET_ID);
                generation.setGenerationID(SAMPLE_GENERATION_ID);
                dut.setGeneration(SAMPLE_PROJECT_ID, generation);
            }

            dut.setParentData(SAMPLE_GENERATION_ID, SelectedGenotypeEnum.R, gidList);

            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString.toString());

            int recordCount = 0;
            gidList.clear();

            while (rs.next()) {
                recordCount++;
                gidList.add(rs.getInt("sg_id"));
                assertEquals("R", rs.getString("sg_type"));
            }

            assertTrue(recordCount == SAMPLE_PARENT_GIDS.size());

            // clean up
            sqlString = new StringBuffer("DELETE FROM mbdt_selected_genotypes WHERE sg_id IN (");

            for (int i = 0; i < gidList.size(); i++) {
                if (i != 0) {
                    sqlString.append(",");
                }

                sqlString.append(gidList.get(i));
            }

            sqlString.append(")");
            stmt.executeUpdate(sqlString.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            deleteSampleGenerationData();
            deleteSampleProjectData();
            closeDatabaseResources(conn, stmt, rs);
        }
    }

    @Test
    public void testSetParentAlreadyExistingSelected() throws Exception {
        StringBuffer sqlString = new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
                .append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
                .append(" WHERE gid in(");
        List<Integer> gidList = new ArrayList<Integer>();
        for (int i = 0; i < SAMPLE_PARENT_GIDS.size(); i++) {
            gidList.add(SAMPLE_PARENT_GIDS.get(i));

            if (i != 0) {
                sqlString.append(",");
            }

            sqlString.append(SAMPLE_PARENT_GIDS.get(i));

        }

        sqlString.append(")");

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            dut.clear();

            MBDTProjectData newProject = new MBDTProjectData(SAMPLE_PROJECT_ID, SAMPLE_PROJECT_NAME, 0, null, null, null);
            dut.setProjectData(newProject);

            MBDTGeneration generation = new MBDTGeneration(SAMPLE_GENERATION_NAME, newProject, SAMPLE_DATASET_ID);
            dut.setGeneration(SAMPLE_PROJECT_ID, generation);


            // insert entries into the system whose type is recurrent, with the selected prefix
            dut.setSelectedAccessions(SAMPLE_GENERATION_ID, new ArrayList<Integer>(SAMPLE_PARENT_GIDS));

            // since the entries should already be existing in the system, it should change the parent type of the entries, without removing the selected prefix
            dut.setParentData(SAMPLE_GENERATION_ID, SelectedGenotypeEnum.D, new ArrayList<Integer>(SAMPLE_PARENT_GIDS));

            conn = dataSource.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlString.toString());

            int recordCount = 0;

            while (rs.next()) {
                recordCount++;
                String type = rs.getString("sg_type");

                assertEquals("SD", type);
            }

            // clean up
            sqlString = new StringBuffer("DELETE FROM mbdt_selected_genotypes WHERE sg_id IN (");

            for (int i = 0; i < gidList.size(); i++) {
                if (i != 0) {
                    sqlString.append(",");
                }

                sqlString.append(gidList.get(i));
            }

            sqlString.append(")");
            stmt.executeUpdate(sqlString.toString());
        } catch (MiddlewareQueryException e) {
            fail(e.getMessage());
        } finally {
            deleteSampleAccessionData();
            deleteSampleGenerationData();
            deleteSampleProjectData();
        }
    }


    protected void closeDatabaseResources(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
        if (conn != null) {
            conn.close();
        }

        if (stmt != null) {
            stmt.close();
        }

        if (rs != null) {
            rs.close();
        }
    }
}