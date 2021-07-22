package org.generationcp.middleware.dao;

import org.generationcp.middleware.pojos.file.FileMetadata;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class FileMetadataDAO extends GenericDAO<FileMetadata, Integer> {

	public FileMetadata getByFileUUID(final String fileUUID) {
		return (FileMetadata) this.getSession().createCriteria(this.getPersistentClass())
			.add(Restrictions.eq("fileUUID", fileUUID))
			.uniqueResult();
	}

	public List<FileMetadata> findByObservationId(final Integer observationId) {
		return this.getSession().createCriteria(this.getPersistentClass())
			.add(Restrictions.eq("phenotype.phenotypeId", observationId))
			.list();
	}
}
