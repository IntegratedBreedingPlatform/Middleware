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

	public List<FileMetadata> findByObservationUnitUUID(final String observationUnitUUID) {
		return this.getSession().createCriteria(this.getPersistentClass())
			.createAlias("experimentModel", "experimentModel")
			.add(Restrictions.eq("experimentModel.obsUnitId", observationUnitUUID))
			.list();
	}
}
