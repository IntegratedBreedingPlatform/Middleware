package org.generationcp.middleware.api.germplasm.pedigree.cop;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public abstract class CopUtils {

	public static File generateFile(final CopResponse results, final String fileNameFullPath) throws IOException {
		try (final CSVWriter csvWriter = new CSVWriter(
			new OutputStreamWriter(new FileOutputStream(fileNameFullPath), StandardCharsets.UTF_8), ',')
		) {
			final File newFile = new File(fileNameFullPath);
			csvWriter.writeAll(results.getArray());
			return newFile;
		}

	}

	public static String getFileFullPath(final Integer listId) {
		final String tmpdir = System.getProperty("java.io.tmpdir");
		return tmpdir + File.separator + "COP-listId-" + listId + ".csv";
	}
}
