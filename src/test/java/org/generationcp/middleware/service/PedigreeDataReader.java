package org.generationcp.middleware.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class PedigreeDataReader {

	private static final String CSV_FILE_PARENT_FOLDER_LOCATION = "/org/generationcp/middleware/service/test/wheat/pedigree/";

	Map<String, String> getAllTestDataFromFolder(final String folderName) {
		final SortedMap<String, String> map = new TreeMap<String, String>();

		final List<String> listFilesForFolder;
		try {
			listFilesForFolder = lestAllCSVFilesInFolder(Paths.get(
					getClass().getResource(
							CSV_FILE_PARENT_FOLDER_LOCATION + folderName)
							.toURI()).toFile());
			// TODO: Use CSV Reader Library. Error handling
			for (final String string : listFilesForFolder) {
				final BufferedReader br = new BufferedReader(new FileReader(string));
				String line = null;
				while ((line = br.readLine()) != null) {
					String str[] = line.split(",");
					map.put(str[0], str[1]);
				}
				br.close();
			}

		} catch (URISyntaxException | IOException e) {
			throw new RuntimeException(String.format("Unable to get a list for files from %s", folderName), e);
		}

		return map;
	}

	/**
	 * @param folder
	 *            folder to search for csv files
	 * @return list of all csv files found in a folder
	 */
	private List<String> lestAllCSVFilesInFolder(final File folder) {
		final FilenameFilter csvFilter = new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.toLowerCase().endsWith(".csv")) {
					return true;
				}
				return false;
			}

		};
		
		final List<String> list = new ArrayList<>();
		for (final File fileEntry : folder.listFiles(csvFilter)) {
			if (fileEntry.isDirectory()) {
				list.addAll(lestAllCSVFilesInFolder(fileEntry));
			} else {
				list.add(fileEntry.getAbsolutePath());
			}
		}
		return list;
	}
}
