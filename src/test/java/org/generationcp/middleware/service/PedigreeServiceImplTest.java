package org.generationcp.middleware.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.service.api.PedigreeService;
import org.junit.Before;
import org.junit.Test;

import com.hazelcast.util.SortedHashMap;

public class PedigreeServiceImplTest extends DataManagerIntegrationTest {

	private PedigreeService pedigreeService;

	@Before
	public void setUp() {
		pedigreeService = managerFactory.getPedigreeService();
	}

	/**
	 * Temporary test harness to use for invoking the actual service and
	 * generate a pedigree strings with CIMMYT algorithm.
	 */
	@Test
	public void getCrossExpansionCimmytWheat() throws Exception {

		// String crossString1 =
		// pedigreeService.getCrossExpansionCimmytWheat(69218, 1, 1);
		// String crossString7 =
		// pedigreeService.getCrossExpansionCimmytWheat(69218, 1, 7);
		// String crossString17 =
		// pedigreeService.getCrossExpansionCimmytWheat(69218, 1, 17);

		Map<String, String> readTestFile = readTestFile();
		List<Three> failed = new ArrayList<Three>();
		List<Three> passed = new ArrayList<Three>();
		List<String> nfeList = new ArrayList<String>();
		PrintWriter passWrite = new PrintWriter("/Users/Akhil/Downloads/passed.txt", "UTF-8");
		PrintWriter failWriter = new PrintWriter("/Users/Akhil/Downloads/failed.txt", "UTF-8");
		int counter = 0;
		for (Entry<String, String> es : readTestFile.entrySet()) {
			try {
				String crossExpansionCimmytWheat = pedigreeService.getCrossExpansionCimmytWheat(
						Integer.parseInt(es.getKey()), 1, 1);
				System.out.println("*********************************" + es.getKey() + " - "
						+ es.getValue());
				// assertEquals(String.format("GID %s failed",
				// es.getKey()),crossExpansionCimmytWheat,es.getValue());
				Three three = new Three(es.getKey(), es.getValue().replace("\"", ""),
						crossExpansionCimmytWheat);
				if (crossExpansionCimmytWheat.equals(es.getValue().replace("\"", ""))) {
					passWrite.println("\"" + three.getGid() + "\"" + "," + "\""
							+ three.getExpectedString() + "\"" + "," + "\""
							+ three.getActualOutCome() + "\"");
					passed.add(three);

				} else {
					failed.add(three);
					failWriter.println("\"" + three.getGid() + "\"" + "," + "\""
							+ three.getExpectedString() + "\"" + "," + "\""
							+ three.getActualOutCome() + "\"");

				}
				System.out.println(String.format("GID %s passed", es.getKey()));
			} catch (NumberFormatException nfe) {
				nfeList.add(es.getKey());
			}
			counter++;
			if (counter == 10000) {
				break;
			}

		}
		System.out.println(failed.size());
		System.out.println(passed.size());
		passWrite.close();
		failWriter.close();

		// Debug.println(INDENT, "Cross string ntype = 1 [" +crossString1 + "]")
		// ;
		// Debug.println(INDENT, "Cross string ntype = 7 [" +crossString7 + "]")
		// ;
		// Debug.println(INDENT, "Cross string ntype = 17 [" +crossString17 +
		// "]") ;
	}

	private class Three {
		private String gid;
		private String expectedString;
		private String actualOutCome;

		public Three(String gid, String expectedString, String actualOutCome) {
			super();
			this.gid = gid;
			this.expectedString = expectedString;
			this.actualOutCome = actualOutCome;
		}

		public String getGid() {
			return gid;
		}

		public void setGid(String gid) {
			this.gid = gid;
		}

		public String getExpectedString() {
			return expectedString;
		}

		public void setExpectedString(String expectedString) {
			this.expectedString = expectedString;
		}

		public String getActualOutCome() {
			return actualOutCome;
		}

		public void setActualOutCome(String actualOutCome) {
			this.actualOutCome = actualOutCome;
		}

	}

	private Map<String, String> readTestFile() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(
				"/Users/Akhil/Downloads/CIMMYT_Wheat_Pedigrees_Sorted.csv"));
		String line = null;
		SortedMap<String, String> map = new TreeMap<String, String>();

		while ((line = br.readLine()) != null) {
			String str[] = line.split(",");
			map.put(str[0], str[1]);
		}
		br.close();
		return map;
	}

}
