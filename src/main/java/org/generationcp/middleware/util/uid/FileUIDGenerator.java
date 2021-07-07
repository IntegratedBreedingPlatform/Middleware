package org.generationcp.middleware.util.uid;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.file.FileMetadata;
import org.generationcp.middleware.pojos.workbench.CropType;

import java.util.List;

public final class FileUIDGenerator {

	private static final int SUFFIX_LENGTH = 8;
	private static final UIDGenerator.UID_ROOT ROOT = UIDGenerator.UID_ROOT.FILE;

	public static void generate(final CropType crop, final List<FileMetadata> fileMetadataList) {
		UIDGenerator.<FileMetadata>generate(crop, fileMetadataList, ROOT, SUFFIX_LENGTH,
			new UIDGenerator.UIDAdapter<FileMetadata>() {

				@Override
				public String getUID(final FileMetadata entry) {
					return entry.getFileUUID();
				}

				@Override
				public void setUID(final FileMetadata entry, final String uid) {
					entry.setFileUUID(uid);
				}
			});
	}
}
