package org.generationcp.middleware.manager.test;

import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.DriverManagerDataSource;
import com.mchange.v2.c3p0.DriverManagerDataSourceFactory;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.MBDTDataManager;
import org.generationcp.middleware.pojos.mbdt.MBDTProjectData;
import org.generationcp.middleware.utils.test.TestOutputFormatter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: Daniel Villafuerte
 */


public class MBDTDataManagerTest extends TestOutputFormatter {

    private static ManagerFactory managerFactory;
    private static MBDTDataManager manager;
    private DataSource dataSource;

    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "local");

        DatabaseConnectionParameters central = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "central");
        managerFactory = new ManagerFactory(local, central);
        manager = managerFactory.getMbdtDataManager();
    }

    @Before
    public void prepareDatabaseItems() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters(
                "testDatabaseConfig.properties", "local");

        dataSource = DriverManagerDataSourceFactory.create(local.getDriverName(), local.getUrl(), local.getUsername(), local.getPassword());
    }

    @Test
    public void testRetrieveProject() throws Exception {
        Integer projectId = -1;
        String testProjectName = "GET PROJECT";

        // set up dummy data in database
        Connection conn = null;
        Statement stmt = null;

        try {
            conn = dataSource.getConnection();
            stmt = conn.createStatement();

            stmt.executeUpdate("INSERT INTO MBDT_PROJECT VALUES (" + projectId + ", '" + testProjectName + "', 0, null, null, null, null, null, null)");

            MBDTProjectData retrieved = manager.getProjectData(projectId);

            assertNotNull(retrieved);

            assertEquals(testProjectName, retrieved.getProjectName());

            stmt.executeUpdate("DELETE FROM MBDT_PROJECT WHERE project_id  " + projectId);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            if (conn != null) {
                conn.close();
            }

            if (stmt != null) {
                stmt.close();
            }
        }
    }

    @Test
    public void testSetProjectPositive() throws Exception {

        String testProjectName = "NEW TRIAL";


        MBDTProjectData newProject = new MBDTProjectData(null, testProjectName, 0, null, null, null);

        Integer generatedId = manager.setProjectData(newProject);
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
            rs = stmt.executeQuery("SELECT pname from MBDT_PROJECT where project_id = " + generatedId);

            if (rs.next()) {
                String retrievedName = rs.getString("pname");
                assertNotNull(retrievedName);
                assertEquals(retrievedName, testProjectName);
            } else {
                fail("Unable to properly retrieve 'generated' project");
            }


            // perform clean up
            stmt.execute("DELETE FROM MBDT_PROJECT where project_id = " + generatedId);

        } catch (SQLException e) {
            fail(e.getMessage());
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }

            if (stmt != null) {
                stmt.close();
            }
        }
    }

    @Test
    public void testSetProjectNegative() {

        MBDTProjectData newProject = new MBDTProjectData(null, null, 0, null, null, null);

        Integer generatedId = null;
        try {
            generatedId = manager.setProjectData(newProject);

            fail("Should not allow saving of null project name");
        } catch (MiddlewareQueryException e) {
            e.printStackTrace();
        }

    }
}