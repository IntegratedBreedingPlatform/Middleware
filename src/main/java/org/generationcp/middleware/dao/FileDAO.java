package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.file.FileMetadata;
import org.hibernate.criterion.Restrictions;

public class FileDAO extends GenericDAO<FileMetadata, Integer> {

	public FileMetadata getByFileUUID(final String fileUuid) {
		return (FileMetadata) this.getSession().createCriteria(this.getPersistentClass())
			.add(Restrictions.eq("fileUUID", fileUuid))
			.uniqueResult();
	}
}
