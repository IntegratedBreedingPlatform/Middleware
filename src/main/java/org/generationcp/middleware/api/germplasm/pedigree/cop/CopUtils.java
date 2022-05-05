package org.generationcp.middleware.api.germplasm.pedigree.cop;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public abstract class CopUtils {

	/**
	 * AWS S3 uses forward slash to identify folders
	 */
	public static final String FILE_STORAGE_PATH_SLASH = "/";
	public static final String FILE_STORAGE_PATH_ROOT = "cop";

	public static File generateFile(
		final CopResponse results,
		final String nameKeySelected,
		final String fileNameFullPath
	) throws IOException {
		try (final CSVWriter csvWriter = new CSVWriter(
			new OutputStreamWriter(new FileOutputStream(fileNameFullPath), StandardCharsets.UTF_8), ',')
		) {
			final File newFile = new File(fileNameFullPath);
			final List<String[]> upperTriangularMatrix = replaceCommonNamesIfNeeded(results, nameKeySelected);
			csvWriter.writeAll(upperTriangularMatrix);
			return newFile;
		}

	}

	/**
	 * TODO a more generic method not tied to upperTriangularMatrix format,
	 *  that works for any internal representation in CopResponse, e.g array2d)
	 */
	private static List<String[]> replaceCommonNamesIfNeeded(final CopResponse results, final String nameKeySelected) {
		final List<String[]> upperTriangularMatrix = results.getUpperTriangularMatrix();

		final Map<Integer, String> gidToName = results.getGermplasmCommonNamesMap().get(nameKeySelected);
		if (gidToName == null) {
			return upperTriangularMatrix;
		}

		final List<String[]> matrixWithNames = new ArrayList<>();
		final String[] header = upperTriangularMatrix.get(0);
		final List<String> newHeader = new ArrayList<>();
		for (final String col : header) {
			try {
				newHeader.add(gidToName.get(Integer.valueOf(col)));
			} catch (final NumberFormatException exception) {
				newHeader.add(col);
			}
		}
		matrixWithNames.add(newHeader.toArray(new String[] {}));

		//noinspection SimplifyStreamApiCallChains
		matrixWithNames.addAll(upperTriangularMatrix.stream().skip(0).map(array -> {
			final Integer gid;
			try {
				gid = Integer.valueOf(array[0]);
				if (gidToName.get(gid) != null) {
					// replace first column in matrix which has gids
					array[0] = gidToName.get(gid);
				}
			} catch (final NumberFormatException exception) {
				// no-op
			}
			return array;
		}).collect(toList()));
		return matrixWithNames;
	}

	public static String getStorageFilePath(final Integer listId) {
		return FILE_STORAGE_PATH_ROOT + FILE_STORAGE_PATH_SLASH + getFileName(listId);
	}

	public static String getFileName(final Integer listId) {
		return "COP-listId-" + listId + ".csv";
	}

	public static byte[] matrixToCsvBytes(final List<String[]> csvLines) {
		final StringBuilder sb = new StringBuilder();
		for (final String[] csvLine : csvLines) {
			sb.append(String.join(";", csvLine));
			sb.append("\n");
		}
		return sb.toString().getBytes(StandardCharsets.UTF_8);
	}
}
