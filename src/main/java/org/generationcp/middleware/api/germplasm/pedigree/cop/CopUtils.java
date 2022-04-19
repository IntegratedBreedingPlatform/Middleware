package org.generationcp.middleware.api.germplasm.pedigree.cop;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public abstract class CopUtils {

	/**
	 * AWS S3 uses forward slash to identify folders
	 */
	public static final String FILE_STORAGE_PATH_SLASH = "/";
	public static final String FILE_STORAGE_PATH_ROOT = "cop";

	public static File generateFile(final CopResponse results, final String fileNameFullPath) throws IOException {
		try (final CSVWriter csvWriter = new CSVWriter(
			new OutputStreamWriter(new FileOutputStream(fileNameFullPath), StandardCharsets.UTF_8), ',')
		) {
			final File newFile = new File(fileNameFullPath);
			csvWriter.writeAll(results.getUpperTriangularMatrix());
			return newFile;
		}

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
