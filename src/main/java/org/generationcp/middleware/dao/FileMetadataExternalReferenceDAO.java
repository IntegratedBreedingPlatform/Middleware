package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.FileMetadataExternalReference;
import org.hibernate.Session;

public class FileMetadataExternalReferenceDAO extends GenericExternalReferenceDAO<FileMetadataExternalReference> {

	public FileMetadataExternalReferenceDAO(final Session session) {
		super(session);
	}

	@Override
	String getIdField() {
		return "file_id";
	}

	@Override
	String getReferenceTable() {
		return "external_reference_file_metadata";
	}
}
