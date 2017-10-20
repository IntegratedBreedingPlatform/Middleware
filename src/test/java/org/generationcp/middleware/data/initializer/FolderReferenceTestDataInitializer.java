package org.generationcp.middleware.data.initializer;

import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;

public class FolderReferenceTestDataInitializer {
	public static Reference createReference(final int id) {
		final Reference reference = new FolderReference(id, "FOLDER");
		return reference;
	}
}
