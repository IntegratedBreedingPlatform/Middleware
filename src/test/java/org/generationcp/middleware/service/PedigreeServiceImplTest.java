package org.generationcp.middleware.service;

import static org.junit.Assert.assertEquals;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.pedigree.PedigreeFactory;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.junit.Before;
import org.junit.Test;

/**
 * Please note that this test requires a wheat database with a gid dump to
 * actually run.
 * 
 * @author Akhil
 *
 */
public class PedigreeServiceImplTest extends DataManagerIntegrationTest {

	private PedigreeService pedigreeCimmytWheatService;
	private CrossExpansionProperties crossExpansionProperties;

	private PedigreeDataReader pedigreeDataReader = new PedigreeDataReader();

	@Before
	public void setup() {
		pedigreeCimmytWheatService = managerFactory.getPedigreeService(
				PedigreeFactory.PROFILE_CIMMYT, CropType.CropEnum.WHEAT.toString());
		crossExpansionProperties = new CrossExpansionProperties();
		crossExpansionProperties.setDefaultLevel(1);
		crossExpansionProperties.setWheatLevel(0);
	}

	@Test
	public void wheatPedigreeMaleBackCross() throws Exception {

		testCSVFiles("F1-F5MaleBackCross");

	}

	@Test
	public void wheatPedigreeDoubleCross() throws Exception {

		testCSVFiles("F1-F6DoubleCross");

	}

	@Test
	public void wheatPedigreeFemaleBackCross() throws Exception {

		testCSVFiles("F1-F6FemaleBackCross");

	}

	@Test
	public void wheatPedigreeSingleCross() throws Exception {

		testCSVFiles("F1-F6SingleCross");

	}

	@Test
	public void wheatPedigreeTopCross() throws Exception {

		testCSVFiles("F1-F6TopCross");

	}

	private void testCSVFiles(final String folderName) throws IOException, FileNotFoundException,
			UnsupportedEncodingException, MiddlewareQueryException {

		System.out
				.println("Please make sure that you have a wheat database with a historic dump of GID's");
		final List<Results> failed = Collections.synchronizedList(new ArrayList<Results>());
		final List<Results> passed = Collections.synchronizedList(new ArrayList<Results>());

		final long timestampForTheFile = System.currentTimeMillis();
		final File passedFilePath = File.createTempFile(folderName + "Passed-"
				+ timestampForTheFile + "-", ".csv");
		final File failedFilePath = File.createTempFile(folderName + "Failed-"
				+ timestampForTheFile + "-", ".csv");

		System.out.println("Passed files will be recorded here:\n"
				+ passedFilePath.getAbsolutePath());
		System.out.println("Falied files will be recorded here:\n"
				+ failedFilePath.getAbsolutePath());

		final PrintWriter passWrite = new PrintWriter(passedFilePath, "UTF-8");
		final PrintWriter failWriter = new PrintWriter(failedFilePath, "UTF-8");

		final Map<String, String> testCases = pedigreeDataReader
				.getAllTestDataFromFolder(folderName);

		for (final Entry<String, String> gidAndExpectedResultEntrySet : testCases.entrySet()) {
			testOneEntry(failed, passed, passWrite, failWriter, gidAndExpectedResultEntrySet);
		}

		passWrite.close();
		failWriter.close();
		final String assertMessage = String
				.format("Passed entries %s. Failed entries %s. There must be no failed entries. Please review %s for failed entries",
						passed.size(), failed.size(), failedFilePath);
		assertEquals(assertMessage, 0, failed.size());

	}

	/**
	 * Test one entry to see if it works.
	 */
	private void testOneEntry(final List<Results> failed, final List<Results> passed,
			final PrintWriter passWrite, final PrintWriter failWriter, Entry<String, String> es)
			throws MiddlewareQueryException {
		final String calculatedPedigreeString = pedigreeCimmytWheatService.getCrossExpansion(
				Integer.parseInt(es.getKey()), crossExpansionProperties);
		final Results comparisonResult = new Results(es.getKey(), es.getValue(),
				calculatedPedigreeString);
		if (calculatedPedigreeString == null) {
			System.out.println("pause");
		}
		if (calculatedPedigreeString.equals(es.getValue())) {
			addToListAndFile(passed, passWrite, comparisonResult);
		} else {
			addToListAndFile(failed, failWriter, comparisonResult);
		}

	}

	private void addToListAndFile(final List<Results> listToModify, final PrintWriter writer,
			final Results comparisonResult) {
		listToModify.add(comparisonResult);
		writer.println("\"" + comparisonResult.getGid() + "\"" + "," + "\""
				+ comparisonResult.getExpectedPedigree() + "\"" + "," + "\""
				+ comparisonResult.getActualPedigree() + "\"");
	}

	/**
	 * Class to hold the results of the wheat pedigree comparison.
	 * 
	 * 
	 *
	 */
	private class Results {
		private String gid;
		private String expectedPedigree;
		private String actualPedigree;

		public Results(String gid, String expectedPedigree, String actualPedigree) {
			super();
			this.gid = gid;
			this.expectedPedigree = expectedPedigree;
			this.actualPedigree = actualPedigree;
		}

		public String getGid() {
			return gid;
		}

		public String getExpectedPedigree() {
			return expectedPedigree;
		}

		public String getActualPedigree() {
			return actualPedigree;
		}

	}

}
