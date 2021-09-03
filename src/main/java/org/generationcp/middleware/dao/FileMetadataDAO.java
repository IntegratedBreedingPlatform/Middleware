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
		+ " left join nd_experiment nde on f.nd_experiment_id = nde.nd_experiment_id " //
		+ " left join germplsm g on f.gid = g.gid " //
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

		final String germplasmUUID = filterRequest.getGermplasmUUID();
		if (!isBlank(germplasmUUID)) {
			paramBuilder.append(" and g.germplsm_uuid = :germplasmUUID ");
			paramBuilder.setParameter("germplasmUUID", germplasmUUID);
		}
	}

	public FileMetadata getByPath(final String path) {
		return (FileMetadata) this.getSession().createCriteria(this.getPersistentClass())
			.add(Restrictions.eq("path", path))
			.uniqueResult();
	}

	public void detachVariables(final Integer datasetId, final List<Integer> variableIds) {
		this.getSession().createSQLQuery("delete " //
			+ " from file_metadata_cvterm " //
			+ " where file_metadata_id in ( " //
			+ "     select T.file_id " //
			+ "     from ( " //
			+ "         select fm.file_id " //
			+ "         from file_metadata fm " //
			+ "                  inner join nd_experiment ne on fm.nd_experiment_id = ne.nd_experiment_id " //
			+ "                  inner join project dataset on ne.project_id = dataset.project_id " //
			+ "                  inner join file_metadata_cvterm fmc on fm.file_id = fmc.file_metadata_id " //
			+ "         where dataset.project_id = :datasetId and fmc.cvterm_id in (:variableIds) " //
			+ "     ) T " //
			+ " ) ")
			.setParameter("datasetId", datasetId)
			.setParameterList("variableIds", variableIds)
			.executeUpdate();
	}
}
