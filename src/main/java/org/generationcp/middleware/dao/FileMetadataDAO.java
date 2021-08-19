package org.generationcp.middleware.dao;

import org.generationcp.middleware.api.file.FileMetadataFilterRequest;
import org.generationcp.middleware.pojos.file.FileMetadata;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

public class FileMetadataDAO extends GenericDAO<FileMetadata, Integer> {

	private static final String SEARCH_BASE_QUERY = " select distinct f.* from file_metadata f " //
		+ " inner join nd_experiment nde on f.nd_experiment_id = nde.nd_experiment_id " //
		+ " left join file_metadata_cvterm fmc on f.file_id = fmc.file_metadata_id " //
		+ "    left join cvterm variable on fmc.cvterm_id = variable.cvterm_id " //
		+ "    left join variable_overrides vo on vo.cvterm_id = variable.cvterm_id " //
		+ "              and (:programUUID is null || vo.program_uuid = :programUUID) " //
		+ " where 1 = 1 ";

	public FileMetadata getByFileUUID(final String fileUUID) {
		return (FileMetadata) this.getSession().createCriteria(this.getPersistentClass())
			.add(Restrictions.eq("fileUUID", fileUUID))
			.uniqueResult();
	}

	public List<FileMetadata> search(final FileMetadataFilterRequest filterRequest, final String programUUID, final Pageable pageable) {

		final StringBuilder queryBuilder = new StringBuilder(SEARCH_BASE_QUERY);
		addSearchParams(new SqlQueryParamBuilder(queryBuilder), filterRequest, programUUID);
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(queryBuilder.toString());
		sqlQuery.addEntity(FileMetadata.class);
		addSearchParams(new SqlQueryParamBuilder(sqlQuery), filterRequest, programUUID);
		addPaginationToSQLQuery(sqlQuery, pageable);
		return sqlQuery.list();
	}


	public long countSearch(final FileMetadataFilterRequest filterRequest, final String programUUID, final Pageable pageable) {
		final StringBuilder queryBuilder = new StringBuilder(SEARCH_BASE_QUERY);
		addSearchParams(new SqlQueryParamBuilder(queryBuilder), filterRequest, programUUID);
		final SQLQuery sqlQuery = this.getSession().createSQLQuery("select count(1) from (" + queryBuilder.toString() + ") as T");
		addSearchParams(new SqlQueryParamBuilder(sqlQuery), filterRequest, programUUID);
		return ((BigInteger) sqlQuery.uniqueResult()).longValue();
	}

	private static void addSearchParams(final SqlQueryParamBuilder paramBuilder, final FileMetadataFilterRequest filterRequest,
		final String programUUID) {

		paramBuilder.setParameter("programUUID", programUUID);

		final String fileName = filterRequest.getFileName();
		if (!isBlank(fileName)) {
			paramBuilder.append(" and f.name like :fileName ");
			paramBuilder.setParameter("fileName",  "%" + fileName + "%");
		}

		final String variableName = filterRequest.getVariableName();
		if (!isBlank(variableName)) {
			paramBuilder.append(" and (variable.name like :variableName or vo.alias like :variableName) ");
			paramBuilder.setParameter("variableName",  "%" + variableName + "%");
		}

		final String observationUnitUUID = filterRequest.getObservationUnitUUID();
		if (!isBlank(observationUnitUUID)) {
			paramBuilder.append(" and nde.obs_unit_id = :observationUnitUUID ");
			paramBuilder.setParameter("observationUnitUUID", observationUnitUUID);
		}
	}
}
