package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.file.FileMetadata;
import org.hibernate.criterion.Restrictions;

public class FileMetadataDAO extends GenericDAO<FileMetadata, Integer> {

	public FileMetadata getByFileUUID(final String fileUUID) {
		return (FileMetadata) this.getSession().createCriteria(this.getPersistentClass())
			.add(Restrictions.eq("fileUUID", fileUUID))
			.uniqueResult();
	}
}
