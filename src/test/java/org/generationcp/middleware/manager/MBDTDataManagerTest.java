
package org.generationcp.middleware.manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.mbdt.SelectedGenotypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.api.MBDTDataManager;
import org.generationcp.middleware.pojos.mbdt.MBDTGeneration;
import org.generationcp.middleware.pojos.mbdt.MBDTProjectData;
import org.generationcp.middleware.pojos.mbdt.SelectedGenotype;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class MBDTDataManagerTest extends IntegrationTestBase {

	@Autowired
	private MBDTDataManager dut;

	// TODO setup in testContext.xml and inject
	private DataSource dataSource;

	public static final Integer SAMPLE_PROJECT_ID = -1;
	public static final String SAMPLE_PROJECT_NAME = "SAMPLE_PROJECT";
	public static final String SAMPLE_GENERATION_NAME = "SAMPLE GENERATION";
	public static final int SAMPLE_DATASET_ID = -1;
	public static final int SAMPLE_GENERATION_ID = -1;
	public static final int[] SAMPLE_SELECTED_MARKER_IDS = new int[] {-1, -2, -3};
	public static final int[] SAMPLE_SELECTED_ACCESSION_GIDS = new int[] {1, 2, 3, 4, 5, 6};
	public static final List<Integer> SAMPLE_PARENT_GIDS = new ArrayList<Integer>();

	@BeforeClass
	public static void setUp() throws Exception {
		MBDTDataManagerTest.SAMPLE_PARENT_GIDS.add(4);
		MBDTDataManagerTest.SAMPLE_PARENT_GIDS.add(5);
		MBDTDataManagerTest.SAMPLE_PARENT_GIDS.add(6);
	}

	protected void executeUpdate(String sql) throws Exception {
		Connection conn = null;
		Statement stmt = null;

		try {
			conn = this.dataSource.getConnection();
			stmt = conn.createStatement();

			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			Assert.fail(e.getMessage());
		} finally {
			this.closeDatabaseResources(conn, stmt, null);
		}
	}

	protected void insertSampleProjectData() throws Exception {
		this.executeUpdate("INSERT INTO MBDT_PROJECT VALUES (" + MBDTDataManagerTest.SAMPLE_PROJECT_ID + ", '"
				+ MBDTDataManagerTest.SAMPLE_PROJECT_NAME + "', 0, null, null, null, null, null, null)");
	}

	protected void insertSampleGenerationData() throws Exception {
		this.executeUpdate("INSERT INTO mbdt_generations VALUES (" + MBDTDataManagerTest.SAMPLE_GENERATION_ID + ", '"
				+ MBDTDataManagerTest.SAMPLE_GENERATION_NAME + "', " + MBDTDataManagerTest.SAMPLE_PROJECT_ID + ", "
				+ MBDTDataManagerTest.SAMPLE_DATASET_ID + ")");
	}

	protected void deleteSampleGenerationData() throws Exception {
		this.executeUpdate("DELETE FROM mbdt_generations WHERE generation_id = " + MBDTDataManagerTest.SAMPLE_GENERATION_ID);
	}

	protected void deleteSampleProjectData() throws Exception {
		this.executeUpdate("DELETE FROM mbdt_generations WHERE project_id IN (SELECT project_id FROM mbdt_project WHERE pname = '"
				+ MBDTDataManagerTest.SAMPLE_PROJECT_NAME + "')");
		this.executeUpdate("DELETE FROM mbdt_project WHERE pname = '" + MBDTDataManagerTest.SAMPLE_PROJECT_NAME + "'");
	}

	protected void insertSampleMarkerData() throws Exception {
		int i = 1;
		for (int markerId : MBDTDataManagerTest.SAMPLE_SELECTED_MARKER_IDS) {
			this.executeUpdate("INSERT INTO mbdt_selected_markers VALUES(" + i + ", " + MBDTDataManagerTest.SAMPLE_GENERATION_ID + ", "
					+ markerId + ")");
			i++;
		}
	}

	protected void insertSampleAccessionData() throws Exception {
		int i = 1;
		for (int sampleSelectedAccessionGid : MBDTDataManagerTest.SAMPLE_SELECTED_ACCESSION_GIDS) {
			this.executeUpdate("INSERT INTO mbdt_selected_genotypes VALUES(" + i + ", " + MBDTDataManagerTest.SAMPLE_GENERATION_ID + ", "
					+ sampleSelectedAccessionGid + ", 'SR')");
			i++;
		}
	}

	protected void insertSampleParentData() throws Exception {
		int i = 1;
		for (int sampleSelectedAccessionGid : MBDTDataManagerTest.SAMPLE_PARENT_GIDS) {
			this.executeUpdate("INSERT INTO mbdt_selected_genotypes VALUES(" + i + ", " + MBDTDataManagerTest.SAMPLE_GENERATION_ID + ", "
					+ sampleSelectedAccessionGid + ", 'D')");
			i++;
		}
	}

	protected void deleteSampleMarkerData() throws Exception {
		for (int markerId : MBDTDataManagerTest.SAMPLE_SELECTED_MARKER_IDS) {
			this.executeUpdate("DELETE FROM mbdt_selected_markers WHERE marker_id = " + markerId);
		}
	}

	protected void deleteSampleAccessionData() throws Exception {
		for (int gid : MBDTDataManagerTest.SAMPLE_SELECTED_ACCESSION_GIDS) {
			this.executeUpdate("DELETE FROM mbdt_selected_genotypes WHERE gid = " + gid);
		}
	}

	@Test
	public void testRetrieveProject() throws Exception {
		// set up dummy data in database
		Connection conn = null;
		Statement stmt = null;

		try {
			this.insertSampleProjectData();

			MBDTProjectData retrieved = this.dut.getProjectData(MBDTDataManagerTest.SAMPLE_PROJECT_ID);

			Assert.assertNotNull(retrieved);

			Assert.assertEquals(MBDTDataManagerTest.SAMPLE_PROJECT_NAME, retrieved.getProjectName());

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleProjectData();
			this.closeDatabaseResources(conn, stmt, null);
		}
	}

	@Test
	public void testSetProjectPositive() throws Exception {

		MBDTProjectData newProject = new MBDTProjectData(null, MBDTDataManagerTest.SAMPLE_PROJECT_NAME, 0, null, null, null);

		this.dut.clear();
		Integer generatedId = this.dut.setProjectData(newProject);
		Assert.assertNotNull(generatedId);

		// clean up operation
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connection = this.dataSource.getConnection();
			stmt = connection.createStatement();

			// check on the db side if the generated project really exists
			rs = stmt.executeQuery("SELECT pname FROM mbdt_project WHERE project_id = " + generatedId);

			if (rs.next()) {
				String retrievedName = rs.getString("pname");
				Assert.assertNotNull(retrievedName);
				Assert.assertEquals(retrievedName, MBDTDataManagerTest.SAMPLE_PROJECT_NAME);
			} else {
				Assert.fail("Unable to properly retrieve 'generated' project");
			}

			// perform clean up
			stmt.execute("DELETE FROM mbdt_project WHERE project_id = " + generatedId);

		} catch (SQLException e) {
			Assert.fail(e.getMessage());
			e.printStackTrace();
		} finally {
			this.closeDatabaseResources(connection, stmt, rs);
		}
	}

	@Test
	public void testSetProjectNegative() {

		MBDTProjectData newProject = new MBDTProjectData(null, null, 0, null, null, null);

		try {
			this.dut.setProjectData(newProject);

			Assert.fail("Should not allow saving of null project name");
		} catch (MiddlewareQueryException e) {
			// an exception is actually the expected flow
		}

	}

	@Test
	public void testGetAllProjects() throws Exception {
		// set up dummy data in database
		Connection conn = null;
		Statement stmt = null;

		try {
			this.insertSampleProjectData();

			List<MBDTProjectData> dataList = this.dut.getAllProjects();

			Assert.assertNotNull(dataList);

			Assert.assertTrue(dataList.size() == 1);

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleProjectData();
			this.closeDatabaseResources(conn, stmt, null);
		}
	}

	@Test
	public void testGetProjectByNamePositive() throws Exception {
		// set up dummy data in database
		Connection conn = null;
		Statement stmt = null;

		try {
			this.insertSampleProjectData();

			Integer projectID = this.dut.getProjectIDByProjectName(MBDTDataManagerTest.SAMPLE_PROJECT_NAME);

			Assert.assertNotNull(projectID);

			Assert.assertEquals(MBDTDataManagerTest.SAMPLE_PROJECT_ID, projectID);

		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleProjectData();
			this.closeDatabaseResources(conn, stmt, null);
		}
	}

	@Test
	public void testGetProjectByNameNegative() throws Exception {
		// set up dummy data in database
		Connection conn = null;
		Statement stmt = null;

		try {
			this.insertSampleProjectData();

			Integer projectID = this.dut.getProjectIDByProjectName("non existent project name");

			Assert.assertNull(projectID);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleProjectData();
			this.closeDatabaseResources(conn, stmt, null);
		}
	}

	@Test
	public void testAddGeneration() throws Exception {

		// check the database for correct retrieval

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			this.insertSampleProjectData();
			MBDTGeneration generation = new MBDTGeneration();
			generation.setGenotypeDatasetID(MBDTDataManagerTest.SAMPLE_DATASET_ID);
			generation.setGenerationName(MBDTDataManagerTest.SAMPLE_GENERATION_NAME);
			generation = this.dut.setGeneration(MBDTDataManagerTest.SAMPLE_PROJECT_ID, generation);

			Assert.assertNotNull(generation);
			Assert.assertEquals(MBDTDataManagerTest.SAMPLE_GENERATION_NAME, generation.getGenerationName());
			Assert.assertNotNull(generation.getGenerationID());

			conn = this.dataSource.getConnection();
			stmt = conn.createStatement();

			rs = stmt.executeQuery("SELECT gname from mbdt_generations WHERE generation_id = " + generation.getGenerationID());

			if (rs.next()) {
				String generationName = rs.getString("gname");
				Assert.assertNotNull(generationName);
				Assert.assertEquals(MBDTDataManagerTest.SAMPLE_GENERATION_NAME, generationName);
			} else {
				Assert.fail("Unable to properly create generation entry");
			}

			stmt.executeUpdate("DELETE FROM mbdt_generations where generation_id = " + generation.getGenerationID());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleProjectData();
			this.closeDatabaseResources(conn, stmt, rs);
		}
	}

	@Test
	public void testRetrieveGeneration() throws Exception {
		try {
			this.insertSampleProjectData();
			this.insertSampleGenerationData();
			MBDTGeneration generation = this.dut.getGeneration(MBDTDataManagerTest.SAMPLE_GENERATION_ID);

			Assert.assertNotNull(generation);
			Assert.assertEquals(MBDTDataManagerTest.SAMPLE_GENERATION_NAME, generation.getGenerationName());
			Assert.assertTrue(MBDTDataManagerTest.SAMPLE_DATASET_ID == generation.getGenotypeDatasetID());
		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
			e.printStackTrace();
		} finally {
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
		}
	}

	@Test
	public void testGetGenerationByNameAndProjectIDPositive() throws Exception {
		try {
			this.insertSampleProjectData();
			this.insertSampleGenerationData();
			Integer generationID =
					this.dut.getGenerationIDByGenerationName(MBDTDataManagerTest.SAMPLE_GENERATION_NAME,
							MBDTDataManagerTest.SAMPLE_PROJECT_ID);

			Assert.assertNotNull(generationID);
			Assert.assertTrue(MBDTDataManagerTest.SAMPLE_GENERATION_ID == generationID);
		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
			e.printStackTrace();
		} finally {
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
		}
	}

	@Test
	public void testGetGenerationByNameAndProjectIDNegative() throws Exception {
		try {
			this.insertSampleProjectData();
			this.insertSampleGenerationData();
			Integer generationID =
					this.dut.getGenerationIDByGenerationName("non existent generation name", MBDTDataManagerTest.SAMPLE_PROJECT_ID);

			Assert.assertNull(generationID);
		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
			e.printStackTrace();
		} finally {
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
		}
	}

	// added a test case for retrieving multiple generations
	@Test
	public void testRetrieveGenerations() throws Exception {
		try {
			this.insertSampleProjectData();
			this.insertSampleGenerationData();

			List<MBDTGeneration> generations = this.dut.getAllGenerations(MBDTDataManagerTest.SAMPLE_PROJECT_ID);

			Assert.assertNotNull(generations);
			Assert.assertTrue(generations.size() == 1);

			MBDTGeneration generation = generations.get(0);

			Assert.assertEquals(MBDTDataManagerTest.SAMPLE_GENERATION_NAME, generation.getGenerationName());
			Assert.assertTrue(MBDTDataManagerTest.SAMPLE_DATASET_ID == generation.getGenotypeDatasetID());
		} catch (MiddlewareQueryException e) {
			Assert.fail(e.getMessage());
			e.printStackTrace();
		} finally {
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
		}
	}

	@Test
	public void testSetSelectedMarker() throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		List<Integer> testMarkerIDs = new ArrayList<Integer>();

		StringBuffer sqlString =
				new StringBuffer("SELECT marker_id, sm_id FROM mbdt_selected_markers mark INNER JOIN mbdt_generations")
						.append(" gen ON (mark.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
						.append(" WHERE marker_id in (");

		for (int i = 0; i < MBDTDataManagerTest.SAMPLE_SELECTED_MARKER_IDS.length; i++) {
			testMarkerIDs.add(MBDTDataManagerTest.SAMPLE_SELECTED_MARKER_IDS[i]);

			if (i != 0) {
				sqlString.append(",");
			}

			sqlString.append(MBDTDataManagerTest.SAMPLE_SELECTED_MARKER_IDS[i]);
		}

		sqlString.append(")");

		try {
			this.insertSampleProjectData();
			this.insertSampleGenerationData();

			// workaround for Hibernate
			MBDTProjectData proj = this.dut.getProjectData(MBDTDataManagerTest.SAMPLE_PROJECT_ID);
			this.dut.setProjectData(proj);
			MBDTGeneration generation = this.dut.getGeneration(MBDTDataManagerTest.SAMPLE_GENERATION_ID);
			this.dut.setGeneration(MBDTDataManagerTest.SAMPLE_PROJECT_ID, generation);

			this.dut.setMarkerStatus(MBDTDataManagerTest.SAMPLE_GENERATION_ID, testMarkerIDs);

			conn = this.dataSource.getConnection();
			stmt = conn.createStatement();

			rs = stmt.executeQuery(sqlString.toString());

			int recordCount = 0;

			// clear list to make way for storage of ids
			testMarkerIDs.clear();

			while (rs.next()) {
				testMarkerIDs.add(rs.getInt("sm_id"));
				recordCount++;
			}

			Assert.assertTrue(recordCount == MBDTDataManagerTest.SAMPLE_SELECTED_MARKER_IDS.length);

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
			Assert.fail(e.getMessage());
			e.printStackTrace();
		} finally {
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
			this.closeDatabaseResources(conn, stmt, rs);
		}
	}

	@Test
	public void testGetSelectedMarker() throws Exception {
		try {
			this.insertSampleProjectData();
			this.insertSampleGenerationData();
			this.insertSampleMarkerData();
			List<Integer> selectedMarkerIDs = this.dut.getMarkerStatus(MBDTDataManagerTest.SAMPLE_GENERATION_ID);

			Assert.assertNotNull(selectedMarkerIDs);
			Assert.assertTrue(MBDTDataManagerTest.SAMPLE_SELECTED_MARKER_IDS.length == selectedMarkerIDs.size());

		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleMarkerData();
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
		}
	}

	@Test
	public void testGetSelectedAccessionNonExistingGenerationID() throws Exception {
		try {
			this.insertSampleProjectData();
			this.insertSampleGenerationData();
			this.insertSampleAccessionData();

			this.dut.getSelectedAccession(Integer.MAX_VALUE);
			Assert.fail("Unable to recognize non existing generation ID");
		} catch (MiddlewareQueryException e) {

		} finally {
			this.deleteSampleAccessionData();
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
		}
	}

	@Test
	public void testGetSelectedAccessions() throws Exception {
		try {
			this.insertSampleProjectData();
			this.insertSampleGenerationData();
			this.insertSampleAccessionData();
			List<SelectedGenotype> accessions = this.dut.getSelectedAccession(MBDTDataManagerTest.SAMPLE_GENERATION_ID);

			Assert.assertNotNull(accessions);
			Assert.assertTrue(MBDTDataManagerTest.SAMPLE_SELECTED_ACCESSION_GIDS.length == accessions.size());

			for (SelectedGenotype accession : accessions) {
				/* assertEquals(SelectedGenotypeEnum.SR, accession.getType()); */

				// selected accessions are now entries that have the S prefix to them
				Assert.assertTrue(accession.getType().equals(SelectedGenotypeEnum.SR)
						|| accession.getType().equals(SelectedGenotypeEnum.SD));
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleAccessionData();
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
		}
	}

	@Test
	public void testGetParents() throws Exception {
		try {
			this.insertSampleProjectData();
			this.insertSampleGenerationData();
			this.insertSampleParentData();
			List<SelectedGenotype> parents = this.dut.getParentData(MBDTDataManagerTest.SAMPLE_GENERATION_ID);

			Assert.assertNotNull(parents);
			Assert.assertTrue(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.size() == parents.size());

			for (SelectedGenotype accession : parents) {
				Assert.assertTrue(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.contains(accession.getGid()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleAccessionData();
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
		}
	}

	@Test
	public void testSetSelectedAccessionNonExisting() throws Exception {

		List<Integer> gidList = new ArrayList<Integer>();

		StringBuffer sqlString =
				new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
						.append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
						.append(" WHERE gid in(");

		for (int i = 0; i < MBDTDataManagerTest.SAMPLE_SELECTED_ACCESSION_GIDS.length; i++) {
			gidList.add(MBDTDataManagerTest.SAMPLE_SELECTED_ACCESSION_GIDS[i]);

			if (i != 0) {
				sqlString.append(",");
			}

			sqlString.append(MBDTDataManagerTest.SAMPLE_SELECTED_ACCESSION_GIDS[i]);
		}

		sqlString.append(")");

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			// workaround for Hibernate : associate inserted data to Hibernate session to avoid problems later on
			this.dut.clear();
			MBDTProjectData newProject =
					new MBDTProjectData(MBDTDataManagerTest.SAMPLE_PROJECT_ID, MBDTDataManagerTest.SAMPLE_PROJECT_NAME, 0, null, null, null);
			this.dut.setProjectData(newProject);

			MBDTGeneration generation =
					new MBDTGeneration(MBDTDataManagerTest.SAMPLE_GENERATION_NAME, newProject, MBDTDataManagerTest.SAMPLE_DATASET_ID);
			this.dut.setGeneration(MBDTDataManagerTest.SAMPLE_PROJECT_ID, generation);

			this.dut.setSelectedAccessions(generation, gidList);

			conn = this.dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlString.toString());

			int recordCount = 0;
			gidList.clear();

			while (rs.next()) {
				recordCount++;
				gidList.add(rs.getInt("sg_id"));

				Assert.assertEquals("SR", rs.getString("sg_type"));
			}

			Assert.assertTrue(recordCount == MBDTDataManagerTest.SAMPLE_SELECTED_ACCESSION_GIDS.length);

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
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
			this.closeDatabaseResources(conn, stmt, rs);
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
			this.dut.clear();
			MBDTProjectData newProject =
					new MBDTProjectData(MBDTDataManagerTest.SAMPLE_PROJECT_ID, MBDTDataManagerTest.SAMPLE_PROJECT_NAME, 0, null, null, null);
			this.dut.setProjectData(newProject);

			MBDTGeneration generation =
					new MBDTGeneration(MBDTDataManagerTest.SAMPLE_GENERATION_NAME, newProject, MBDTDataManagerTest.SAMPLE_DATASET_ID);
			this.dut.setGeneration(MBDTDataManagerTest.SAMPLE_PROJECT_ID, generation);

			conn = this.dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT count(*) as genotypeCount from mbdt_selected_genotypes");

			assert rs.next();
			int genotypeCount = rs.getInt("genotypeCount");

			this.dut.setSelectedAccessions(generation, gidList);

			rs = stmt.executeQuery("SELECT count(*) as genotypeCount from mbdt_selected_genotypes");
			assert rs.next();

			Assert.assertEquals(genotypeCount, rs.getInt("genotypeCount"));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
			this.closeDatabaseResources(conn, stmt, rs);
		}
	}

	@Test
	public void testSetParentDataNonExistingGeneration() throws Exception {
		try {
			this.dut.setParentData(null, SelectedGenotypeEnum.R, MBDTDataManagerTest.SAMPLE_PARENT_GIDS);
			Assert.fail("Unable to catch non existing generation ID");
		} catch (MiddlewareQueryException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSetSelectedAccessionCurrentlyExistingNonSelected() throws Exception {

		List<Integer> gidList = new ArrayList<Integer>();

		StringBuffer sqlString =
				new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
						.append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
						.append(" WHERE gid in(");

		for (int i = 0; i < MBDTDataManagerTest.SAMPLE_PARENT_GIDS.size(); i++) {
			gidList.add(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.get(i));

			if (i != 0) {
				sqlString.append(",");
			}

			sqlString.append(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.get(i));
		}

		sqlString.append(")");

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			// workaround for Hibernate : associate inserted data to Hibernate session to avoid problems later on
			this.dut.clear();
			MBDTProjectData newProject =
					new MBDTProjectData(MBDTDataManagerTest.SAMPLE_PROJECT_ID, MBDTDataManagerTest.SAMPLE_PROJECT_NAME, 0, null, null, null);
			this.dut.setProjectData(newProject);

			MBDTGeneration generation =
					new MBDTGeneration(MBDTDataManagerTest.SAMPLE_GENERATION_NAME, newProject, MBDTDataManagerTest.SAMPLE_DATASET_ID);
			this.dut.setGeneration(MBDTDataManagerTest.SAMPLE_PROJECT_ID, generation);

			// insert parent data into mbdt_selected_genotypes table
			this.dut.setParentData(generation, SelectedGenotypeEnum.D, new ArrayList<Integer>(gidList));

			// using the same gid list, pass them into the setSelectedAccessions method
			this.dut.setSelectedAccessions(generation, new ArrayList<Integer>(gidList));

			conn = this.dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlString.toString());

			int recordCount = 0;
			gidList.clear();

			while (rs.next()) {
				recordCount++;
				gidList.add(rs.getInt("sg_id"));

				// the set selected accessions method must have changed the sg type of the provided gids from the previous D to something
				// with the selected prefix 'S'
				Assert.assertEquals("SD", rs.getString("sg_type"));

				Integer gid = rs.getInt("gid");
				Assert.assertTrue(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.contains(gid));
			}

			Assert.assertTrue(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.size() == recordCount);

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
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
			this.closeDatabaseResources(conn, stmt, rs);
		}
	}

	@Test
	public void testSetSelectedAccessionCurrentlyExistingSelected() throws Exception {

		List<Integer> gidList = new ArrayList<Integer>();

		StringBuffer sqlString =
				new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
						.append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
						.append(" WHERE gid in(");

		for (int i = 0; i < MBDTDataManagerTest.SAMPLE_PARENT_GIDS.size(); i++) {
			gidList.add(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.get(i));

			if (i != 0) {
				sqlString.append(",");
			}

			sqlString.append(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.get(i));
		}

		sqlString.append(")");

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			// workaround for Hibernate : associate inserted data to Hibernate session to avoid problems later on
			this.dut.clear();
			MBDTProjectData newProject =
					new MBDTProjectData(MBDTDataManagerTest.SAMPLE_PROJECT_ID, MBDTDataManagerTest.SAMPLE_PROJECT_NAME, 0, null, null, null);
			this.dut.setProjectData(newProject);

			MBDTGeneration generation =
					new MBDTGeneration(MBDTDataManagerTest.SAMPLE_GENERATION_NAME, newProject, MBDTDataManagerTest.SAMPLE_DATASET_ID);
			this.dut.setGeneration(MBDTDataManagerTest.SAMPLE_PROJECT_ID, generation);

			// insert parent data into mbdt_selected_genotypes table
			this.dut.setParentData(generation, SelectedGenotypeEnum.D, gidList);

			// using the same gid list, pass them into the setSelectedAccessions method
			this.dut.setSelectedAccessions(generation, new ArrayList<Integer>(gidList));

			// make a call to selected accessions a second time. the items that were marked as selected should go back to being selected
			this.dut.setSelectedAccessions(generation, new ArrayList<Integer>(gidList));

			conn = this.dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlString.toString());

			int recordCount = 0;
			gidList.clear();

			while (rs.next()) {
				recordCount++;
				gidList.add(rs.getInt("sg_id"));

				// the set selected accessions method must have changed the sg type of the provided gids from the previous D to something
				// with the selected prefix 'S'
				Assert.assertEquals("D", rs.getString("sg_type"));

				Integer gid = rs.getInt("gid");
				Assert.assertTrue(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.contains(gid));
			}

			Assert.assertTrue(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.size() == recordCount);

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
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
			this.closeDatabaseResources(conn, stmt, rs);
		}
	}

	@Test
	public void testSetParentNonExisting() throws Exception {

		StringBuffer sqlString =
				new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
						.append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
						.append(" WHERE gid in(");

		List<Integer> gidList = new ArrayList<Integer>();
		for (int i = 0; i < MBDTDataManagerTest.SAMPLE_PARENT_GIDS.size(); i++) {
			gidList.add(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.get(i));

			if (i != 0) {
				sqlString.append(",");
			}

			sqlString.append(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.get(i));
		}

		sqlString.append(")");

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {

			this.dut.clear();
			MBDTProjectData newProject =
					new MBDTProjectData(MBDTDataManagerTest.SAMPLE_PROJECT_ID, MBDTDataManagerTest.SAMPLE_PROJECT_NAME, 0, null, null, null);
			this.dut.setProjectData(newProject);

			MBDTGeneration generation =
					new MBDTGeneration(MBDTDataManagerTest.SAMPLE_GENERATION_NAME, newProject, MBDTDataManagerTest.SAMPLE_DATASET_ID);
			this.dut.setGeneration(MBDTDataManagerTest.SAMPLE_PROJECT_ID, generation);

			this.dut.setParentData(generation, SelectedGenotypeEnum.R, gidList);

			conn = this.dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlString.toString());

			int recordCount = 0;
			gidList.clear();

			while (rs.next()) {
				recordCount++;
				gidList.add(rs.getInt("sg_id"));
				Assert.assertEquals("R", rs.getString("sg_type"));
			}

			Assert.assertTrue(recordCount == MBDTDataManagerTest.SAMPLE_PARENT_GIDS.size());

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
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
			this.closeDatabaseResources(conn, stmt, rs);
		}

	}

	/*
	 * No longer applicable with the change to the selected genotype
	 * 
	 * @Test public void testSetParentNegativeNonParentEnumType() throws Exception {
	 * 
	 * List<Integer> gidList = new ArrayList<Integer>(); for (int i = 0; i < SAMPLE_PARENT_GIDS.size(); i++) {
	 * gidList.add(SAMPLE_PARENT_GIDS.get(i));
	 * 
	 * }
	 * 
	 * try { dut.clear(); MBDTProjectData newProject = new MBDTProjectData(SAMPLE_PROJECT_ID, SAMPLE_PROJECT_NAME, 0, null, null, null);
	 * dut.setProjectData(newProject);
	 * 
	 * MBDTGeneration generation = new MBDTGeneration(SAMPLE_GENERATION_NAME, newProject, SAMPLE_DATASET_ID);
	 * dut.setGeneration(SAMPLE_PROJECT_ID, generation);
	 * 
	 * dut.setParentData(SAMPLE_PROJECT_ID, SAMPLE_DATASET_ID, SelectedGenotypeEnum.SA, gidList);
	 * fail("Not able to catch error, setting parent with non parent genotype type"); } catch (MiddlewareQueryException e) {
	 * 
	 * } finally { deleteSampleGenerationData(); deleteSampleProjectData(); } }
	 */

	@Test
	public void testSetParentAlreadyExistingNonSelected() throws Exception {
		StringBuffer sqlString =
				new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
						.append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
						.append(" WHERE gid in(");
		List<Integer> gidList = new ArrayList<Integer>();
		for (int i = 0; i < MBDTDataManagerTest.SAMPLE_PARENT_GIDS.size(); i++) {
			gidList.add(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.get(i));

			if (i != 0) {
				sqlString.append(",");
			}

			sqlString.append(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.get(i));

		}

		sqlString.append(")");

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			this.dut.clear();
			MBDTProjectData newProject =
					new MBDTProjectData(MBDTDataManagerTest.SAMPLE_PROJECT_ID, MBDTDataManagerTest.SAMPLE_PROJECT_NAME, 0, null, null, null);
			this.dut.setProjectData(newProject);

			MBDTGeneration generation =
					new MBDTGeneration(MBDTDataManagerTest.SAMPLE_GENERATION_NAME, newProject, MBDTDataManagerTest.SAMPLE_DATASET_ID);
			this.dut.setGeneration(MBDTDataManagerTest.SAMPLE_PROJECT_ID, generation);

			this.dut.setParentData(generation, SelectedGenotypeEnum.R, new ArrayList<Integer>(MBDTDataManagerTest.SAMPLE_PARENT_GIDS));

			// make another call to setParentData. since the gid entries should already be existing, it should just modify those items
			this.dut.setParentData(generation, SelectedGenotypeEnum.D, new ArrayList<Integer>(MBDTDataManagerTest.SAMPLE_PARENT_GIDS));

			conn = this.dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlString.toString());

			while (rs.next()) {
				String type = rs.getString("sg_type");

				Assert.assertEquals("D", type);
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
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleAccessionData();
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
		}
	}

	@Test
	public void testSetParentDuplicateGIDInParameterList() throws Exception {
		StringBuffer sqlString =
				new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
						.append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
						.append(" WHERE gid in(");

		List<Integer> gidList = new ArrayList<Integer>();
		for (int i = 0; i < MBDTDataManagerTest.SAMPLE_PARENT_GIDS.size(); i++) {
			gidList.add(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.get(i));

			if (i != 0) {
				sqlString.append(",");
			}

			sqlString.append(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.get(i));
		}

		// add a duplicate gid to the list
		gidList.add(gidList.get(0));

		sqlString.append(")");

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {

			this.dut.clear();
			MBDTProjectData newProject = this.dut.getProjectData(MBDTDataManagerTest.SAMPLE_PROJECT_ID);
			if (newProject == null) {
				newProject =
						new MBDTProjectData(MBDTDataManagerTest.SAMPLE_PROJECT_ID, MBDTDataManagerTest.SAMPLE_PROJECT_NAME, 0, null, null,
								null);
				this.dut.setProjectData(newProject);
			}

			MBDTGeneration generation = this.dut.getGeneration(MBDTDataManagerTest.SAMPLE_GENERATION_ID);
			if (generation == null) {
				generation =
						new MBDTGeneration(MBDTDataManagerTest.SAMPLE_GENERATION_NAME, newProject, MBDTDataManagerTest.SAMPLE_DATASET_ID);
				this.dut.setGeneration(MBDTDataManagerTest.SAMPLE_PROJECT_ID, generation);
			}

			this.dut.setParentData(generation, SelectedGenotypeEnum.R, gidList);

			conn = this.dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlString.toString());

			int recordCount = 0;
			gidList.clear();

			while (rs.next()) {
				recordCount++;
				gidList.add(rs.getInt("sg_id"));
				Assert.assertEquals("R", rs.getString("sg_type"));
			}

			Assert.assertTrue(recordCount == MBDTDataManagerTest.SAMPLE_PARENT_GIDS.size());

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
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
			this.closeDatabaseResources(conn, stmt, rs);
		}
	}

	@Test
	public void testSetParentAlreadyExistingSelected() throws Exception {
		StringBuffer sqlString =
				new StringBuffer("SELECT sg_id, gid, sg_type FROM mbdt_selected_genotypes geno INNER JOIN mbdt_generations")
						.append(" gen ON (geno.generation_id = gen.generation_id) INNER JOIN mbdt_project proj ON (gen.project_id = proj.project_id)")
						.append(" WHERE gid in(");
		List<Integer> gidList = new ArrayList<Integer>();
		for (int i = 0; i < MBDTDataManagerTest.SAMPLE_PARENT_GIDS.size(); i++) {
			gidList.add(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.get(i));

			if (i != 0) {
				sqlString.append(",");
			}

			sqlString.append(MBDTDataManagerTest.SAMPLE_PARENT_GIDS.get(i));

		}

		sqlString.append(")");

		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {

			this.dut.clear();

			MBDTProjectData newProject =
					new MBDTProjectData(MBDTDataManagerTest.SAMPLE_PROJECT_ID, MBDTDataManagerTest.SAMPLE_PROJECT_NAME, 0, null, null, null);
			this.dut.setProjectData(newProject);

			MBDTGeneration generation =
					new MBDTGeneration(MBDTDataManagerTest.SAMPLE_GENERATION_NAME, newProject, MBDTDataManagerTest.SAMPLE_DATASET_ID);
			this.dut.setGeneration(MBDTDataManagerTest.SAMPLE_PROJECT_ID, generation);

			// insert entries into the system whose type is recurrent, with the selected prefix
			this.dut.setSelectedAccessions(generation, new ArrayList<Integer>(MBDTDataManagerTest.SAMPLE_PARENT_GIDS));

			// since the entries should already be existing in the system, it should change the parent type of the entries, without removing
			// the selected prefix
			this.dut.setParentData(generation, SelectedGenotypeEnum.D, new ArrayList<Integer>(MBDTDataManagerTest.SAMPLE_PARENT_GIDS));

			conn = this.dataSource.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlString.toString());

			while (rs.next()) {
				String type = rs.getString("sg_type");

				Assert.assertEquals("SD", type);
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
			Assert.fail(e.getMessage());
		} finally {
			this.deleteSampleAccessionData();
			this.deleteSampleGenerationData();
			this.deleteSampleProjectData();
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
