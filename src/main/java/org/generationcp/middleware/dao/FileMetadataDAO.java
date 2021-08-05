package org.generationcp.middleware.dao;

import org.apache.commons.lang.StringUtils;
import org.generationcp.middleware.pojos.file.FileMetadata;
import org.generationcp.middleware.util.StringUtil;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

public class FileMetadataDAO extends GenericDAO<FileMetadata, Integer> {

	public FileMetadata getByFileUUID(final String fileUUID) {
		return (FileMetadata) this.getSession().createCriteria(this.getPersistentClass())
			.add(Restrictions.eq("fileUUID", fileUUID))
			.uniqueResult();
	}

	public List<FileMetadata> list(
		final String programUUID,
		final String observationUnitUUID,
		final String variableName,
		final String fileName
	) {
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(" select distinct f.* from file_metadata f " //
			+ " inner join nd_experiment nde on f.nd_experiment_id = nde.nd_experiment_id "
			+ " left join file_metadata_cvterm fmc on f.file_id = fmc.file_metadata_id " //
			+ "    left join cvterm variable on fmc.cvterm_id = variable.cvterm_id " //
			+ "    left join variable_overrides vo on vo.cvterm_id = variable.cvterm_id " //
			+ "  and (:programUUID is null || vo.program_uuid = :programUUID) " //
			+ " where (:variableName is null or (variable.name like :variableName or vo.alias like :variableName))" //
			+ "  and (:fileName is null or f.name like :fileName)"
			+ "  and nde.obs_unit_id = :observationUnitUUID ");
		sqlQuery.addEntity(FileMetadata.class);
		sqlQuery.setParameter("fileName", !isBlank(fileName) ? ("%" + fileName + "%") : null);
		sqlQuery.setParameter("programUUID", programUUID);
		sqlQuery.setParameter("variableName", !isBlank(variableName) ? ("%" + variableName + "%") : null);
		sqlQuery.setParameter("observationUnitUUID", observationUnitUUID);

		return sqlQuery.list();
	}
}
