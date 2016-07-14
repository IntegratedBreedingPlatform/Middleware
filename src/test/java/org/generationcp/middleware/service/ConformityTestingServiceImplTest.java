
package org.generationcp.middleware.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import javax.sql.DataSource;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.domain.conformity.ConformityGermplasmInput;
import org.generationcp.middleware.domain.conformity.UploadInput;
import org.generationcp.middleware.exceptions.ConformityException;
import org.generationcp.middleware.service.api.ConformityTestingService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class ConformityTestingServiceImplTest extends IntegrationTestBase {

	@Autowired
	private ConformityTestingService conformityTestingService;

	// TODO will require a DataSource bean setup in testContext.xml
	@Autowired
	private DataSource dataSource;

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

	@After
	public void cleanup() throws Exception {

	}

	@Test
	public void testConformityViaAncestry() throws Exception {
		UploadInput input = new UploadInput();
		input.setParentAGID(177);
		input.setParentBGID(-2);

		ConformityGermplasmInput entry = new ConformityGermplasmInput("C1_001-01", "", -3);
		entry.getMarkerValues().put("GKAM0022", "A");
		entry.getMarkerValues().put("GKAM0035", "C");
		entry.getMarkerValues().put("GKAM0090", "A/G");
		entry.setsNumber(3);
		input.addEntry(entry);

		// entry = new ConformityGermplasmInput("C1_001-02", "", -2);
		// entry.getMarkerValues().put("GKAM0001", "T");
		// entry.getMarkerValues().put("GKAM0004", "G");
		// entry.getMarkerValues().put("GKAM0005", "T");
		// entry.setsNumber(4);
		// input.addEntry(entry);

		// entry = new ConformityGermplasmInput("C1_001-03", "", -5);
		// entry.getMarkerValues().put("GKAM0001", "C");
		// entry.getMarkerValues().put("GKAM0004", "-");
		// entry.getMarkerValues().put("GKAM0005", "A/T");
		// entry.setsNumber(5);
		// input.addEntry(entry);
		//
		// entry = new ConformityGermplasmInput("C1_001-04", "", -6);
		// entry.getMarkerValues().put("GKAM0001", "A/T");
		// entry.getMarkerValues().put("GKAM0004", "G");
		// entry.getMarkerValues().put("GKAM0005", "T");
		// entry.setsNumber(6);
		// input.addEntry(entry);

		try {
			Map<Integer, Map<String, String>> output = this.conformityTestingService.testConformity(input);
			System.out.println(output);

			// verify that problematic entry is present
			// assertTrue(output.containsKey(-5));

			// verify that the correct count of problematic markers are noted
			// assertTrue(output.get(-5).size() == 2);

			// verify that passed entries are not included
			// assertTrue(output.size() == 1);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testConformityNoParentOrAncestor() throws Exception {
		UploadInput input = new UploadInput();
		input.setParentAGID(-1);
		input.setParentBGID(2);

		ConformityGermplasmInput entry = new ConformityGermplasmInput("C1_001-01", "", -2);
		entry.getMarkerValues().put("GKAM0001", "A");
		entry.getMarkerValues().put("GKAM0004", "G");
		entry.getMarkerValues().put("GKAM0005", "T");
		entry.setsNumber(3);
		input.addEntry(entry);

		entry = new ConformityGermplasmInput("C1_001-02", "", -4);
		entry.getMarkerValues().put("GKAM0001", "T");
		entry.getMarkerValues().put("GKAM0004", "G");
		entry.getMarkerValues().put("GKAM0005", "T");
		entry.setsNumber(4);
		input.addEntry(entry);

		entry = new ConformityGermplasmInput("C1_001-03", "", -5);
		entry.getMarkerValues().put("GKAM0001", "C");
		entry.getMarkerValues().put("GKAM0004", "-");
		entry.getMarkerValues().put("GKAM0005", "A/T");
		entry.setsNumber(5);
		input.addEntry(entry);

		entry = new ConformityGermplasmInput("C1_001-04", "", -6);
		entry.getMarkerValues().put("GKAM0001", "A/T");
		entry.getMarkerValues().put("GKAM0004", "G");
		entry.getMarkerValues().put("GKAM0005", "T");
		entry.setsNumber(6);
		input.addEntry(entry);

		try {
			this.conformityTestingService.testConformity(input);

			Assert.fail("Unable to warn regarding no parent or ancestor information");
		} catch (ConformityException e) {

		}
	}

	@Test
	public void testAll() {
		UploadInput input = new UploadInput();
		input.setParentAGID(1);
		input.setParentBGID(2);

		ConformityGermplasmInput entry = new ConformityGermplasmInput("005_24", "", 1);
		entry.getMarkerValues().put("SB_01_112", "G");
		entry.getMarkerValues().put("SB_01_161", "G");
		entry.getMarkerValues().put("SB_01_122", "G");
		entry.setsNumber(1);
		input.addEntry(entry);

		entry = new ConformityGermplasmInput("093_09", "", 2);
		entry.getMarkerValues().put("SB_01_112", "A");
		entry.getMarkerValues().put("SB_01_161", "A/G");
		entry.getMarkerValues().put("SB_01_122", "A/G");
		entry.setsNumber(2);
		input.addEntry(entry);

		entry = new ConformityGermplasmInput("C1_001-01", "", 3);
		entry.getMarkerValues().put("SB_01_112", "-");
		entry.getMarkerValues().put("SB_01_161", "-");
		entry.getMarkerValues().put("SB_01_122", "G");
		entry.setsNumber(3);
		input.addEntry(entry);

		entry = new ConformityGermplasmInput("C1_001-02", "", 4);
		entry.getMarkerValues().put("SB_01_112", "A/G");
		entry.getMarkerValues().put("SB_01_161", "G");
		entry.getMarkerValues().put("SB_01_122", "A/G");
		entry.setsNumber(4);
		input.addEntry(entry);

		entry = new ConformityGermplasmInput("C1_001-03", "", 5);
		entry.getMarkerValues().put("SB_01_112", "A");
		entry.getMarkerValues().put("SB_01_161", "A");
		entry.getMarkerValues().put("SB_01_122", "G");
		entry.setsNumber(5);
		input.addEntry(entry);

		entry = new ConformityGermplasmInput("C1_001-04", "", 6);
		entry.getMarkerValues().put("SB_01_112", "-");
		entry.getMarkerValues().put("SB_01_161", "G");
		entry.getMarkerValues().put("SB_01_122", "G");
		entry.setsNumber(6);
		input.addEntry(entry);

		try {
			Map<Integer, Map<String, String>> output = this.conformityTestingService.testConformity(input);

			// verify that problematic entry is present
			Assert.assertTrue(output.containsKey(5));

			// verify that the correct count of problematic markers are noted
			Assert.assertTrue(output.get(5).size() == 2);

			// verify that passed entries are not included
			Assert.assertTrue(output.size() == 1);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
