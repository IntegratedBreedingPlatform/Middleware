
package org.generationcp.middleware.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Please note that this test requires a wheat database with a gid dump to actually run.
 *
 * @author Akhil
 *
 */
@Ignore("Historic failing test. Disabled temporarily. Developers working in this area please spend some time to fix and remove @Ignore.")
public class PedigreeServiceImplTest extends IntegrationTestBase {

	// TODO pedigree service parameters need to be set in testContext.xml
	@Autowired
	private PedigreeService pedigreeCimmytWheatService;

	private CrossExpansionProperties crossExpansionProperties;

	private final PedigreeDataReader pedigreeDataReader = new PedigreeDataReader();

	@Before
	public void setup() {
		final Properties mockProperties = Mockito.mock(Properties.class);
		Mockito.when(mockProperties.getProperty("wheat.generation.level")).thenReturn("0");
		this.crossExpansionProperties = new CrossExpansionProperties(mockProperties);
		this.crossExpansionProperties.setDefaultLevel(1);
	}

	@Test
	public void wheatPedigreeMaleBackCross() throws Exception {

		this.testCSVFiles("F1-F5MaleBackCross");

	}

	@Test
	public void wheatPedigreeDoubleCross() throws Exception {

		this.testCSVFiles("F1-F6DoubleCross");

	}

	@Test
	public void wheatPedigreeFemaleBackCross() throws Exception {

		this.testCSVFiles("F1-F6FemaleBackCross");

	}

	@Test
	public void wheatPedigreeSingleCross() throws Exception {

		this.testCSVFiles("F1-F6SingleCross");

	}

	@Test
	public void wheatPedigreeTopCross() throws Exception {

		this.testCSVFiles("F1-F6TopCross");

	}

	private void testCSVFiles(final String folderName) throws IOException, FileNotFoundException, UnsupportedEncodingException,
			MiddlewareQueryException {

		System.out.println("Please make sure that you have a wheat database with a historic dump of GID's");
		final List<Results> failed = Collections.synchronizedList(new ArrayList<Results>());
		final List<Results> passed = Collections.synchronizedList(new ArrayList<Results>());

		final long timestampForTheFile = System.currentTimeMillis();
		final File passedFilePath = File.createTempFile(folderName + "Passed-" + timestampForTheFile + "-", ".csv");
		final File failedFilePath = File.createTempFile(folderName + "Failed-" + timestampForTheFile + "-", ".csv");

		System.out.println("Passed files will be recorded here:\n" + passedFilePath.getAbsolutePath());
		System.out.println("Falied files will be recorded here:\n" + failedFilePath.getAbsolutePath());

		final PrintWriter passWrite = new PrintWriter(passedFilePath, "UTF-8");
		final PrintWriter failWriter = new PrintWriter(failedFilePath, "UTF-8");

		final Map<String, String> testCases = this.pedigreeDataReader.getAllTestDataFromFolder(folderName);

		for (final Entry<String, String> gidAndExpectedResultEntrySet : testCases.entrySet()) {
			this.testOneEntry(failed, passed, passWrite, failWriter, gidAndExpectedResultEntrySet);
		}

		passWrite.close();
		failWriter.close();
		final String assertMessage =
				String.format("Passed entries %s. Failed entries %s. There must be no failed entries. Please review %s for failed entries",
						passed.size(), failed.size(), failedFilePath);
		Assert.assertEquals(assertMessage, 0, failed.size());

	}

	/**
	 * Test one entry to see if it works.
	 */
	private void testOneEntry(final List<Results> failed, final List<Results> passed, final PrintWriter passWrite,
			final PrintWriter failWriter, Entry<String, String> es) throws MiddlewareQueryException {
		final String calculatedPedigreeString =
				this.pedigreeCimmytWheatService.getCrossExpansion(Integer.parseInt(es.getKey()), this.crossExpansionProperties);
		final Results comparisonResult = new Results(es.getKey(), es.getValue(), calculatedPedigreeString);
		if (calculatedPedigreeString == null) {
			System.out.println("pause");
		}
		if (calculatedPedigreeString.equals(es.getValue())) {
			this.addToListAndFile(passed, passWrite, comparisonResult);
		} else {
			this.addToListAndFile(failed, failWriter, comparisonResult);
		}

	}

	private void addToListAndFile(final List<Results> listToModify, final PrintWriter writer, final Results comparisonResult) {
		listToModify.add(comparisonResult);
		writer.println("\"" + comparisonResult.getGid() + "\"" + "," + "\"" + comparisonResult.getExpectedPedigree() + "\"" + "," + "\""
				+ comparisonResult.getActualPedigree() + "\"");
	}

	/**
	 * Class to hold the results of the wheat pedigree comparison.
	 *
	 *
	 *
	 */
	private class Results {

		private final String gid;
		private final String expectedPedigree;
		private final String actualPedigree;

		public Results(String gid, String expectedPedigree, String actualPedigree) {
			super();
			this.gid = gid;
			this.expectedPedigree = expectedPedigree;
			this.actualPedigree = actualPedigree;
		}

		public String getGid() {
			return this.gid;
		}

		public String getExpectedPedigree() {
			return this.expectedPedigree;
		}

		public String getActualPedigree() {
			return this.actualPedigree;
		}

	}

}
