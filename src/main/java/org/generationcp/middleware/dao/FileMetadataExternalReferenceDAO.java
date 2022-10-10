package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.FileMetadataExternalReference;

public class FileMetadataExternalReferenceDAO extends GenericExternalReferenceDAO<FileMetadataExternalReference> {

	@Override
	String getIdField() {
		return "file_id";
	}

	@Override
	String getReferenceTable() {
		return "external_reference_file_metadata";
	}
}
