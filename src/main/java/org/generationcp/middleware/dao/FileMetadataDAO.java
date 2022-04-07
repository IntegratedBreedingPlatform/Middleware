package org.generationcp.middleware.dao;

import org.generationcp.middleware.api.file.FileMetadataFilterRequest;
import org.generationcp.middleware.pojos.file.FileMetadata;
import org.generationcp.middleware.util.SqlQueryParamBuilder;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

public class FileMetadataDAO extends GenericDAO<FileMetadata, Integer> {

	private static final String SEARCH_BASE_QUERY = " select distinct f.* from file_metadata f " //
		+ " left join nd_experiment nde on f.nd_experiment_id = nde.nd_experiment_id " //
		+ " left join germplsm g on f.gid = g.gid " //
		+ " left join nd_geolocation env on env.nd_geolocation_id = f.nd_geolocation_id " //
		+ " left join file_metadata_cvterm fmc on f.file_id = fmc.file_metadata_id " //
		+ "    left join cvterm variable on fmc.cvterm_id = variable.cvterm_id " //
		+ "    left join variable_overrides vo on vo.cvterm_id = variable.cvterm_id " //
		+ "              and (:programUUID is null || vo.program_uuid = :programUUID) " //
		+ " where 1 = 1 ";

	public FileMetadataDAO(final Session session) {
		super(session);
	}

	public FileMetadata getByFileUUID(final String fileUUID) {
		return (FileMetadata) this.getSession().createCriteria(this.getPersistentClass())
			.add(Restrictions.eq("fileUUID", fileUUID))
			.uniqueResult();
	}

	public List<FileMetadata> getAll(final List<Integer> variableIds, final Integer datasetId, final String germplasmUUID,
		final Integer instanceId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass())
			.createAlias("variables", "variables", JoinType.LEFT_OUTER_JOIN)
			.createAlias("germplasm", "germplasm", JoinType.LEFT_OUTER_JOIN)
			.createAlias("experimentModel", "experimentModel", JoinType.LEFT_OUTER_JOIN)
			.createAlias("geolocation", "geolocation", JoinType.LEFT_OUTER_JOIN);
		if (!isEmpty(variableIds)) {
			criteria.add(Restrictions.in("variables.cvTermId", variableIds));
		}
		if (datasetId != null) {
			criteria.add(Restrictions.eq("experimentModel.project.projectId", datasetId));
		}
		if (!isBlank(germplasmUUID)) {
			criteria.add(Restrictions.eq("germplasm.germplasmUUID", germplasmUUID));
		}

		if (instanceId != null) {
			criteria.add(Restrictions.eq("geolocation.locationId", instanceId));
		}
		return criteria.list();

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


	public long countSearch(final FileMetadataFilterRequest filterRequest, final String programUUID) {
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

		final Integer instanceId = filterRequest.getInstanceId();
		if(instanceId != null) {
			paramBuilder.append(" and env.nd_geolocation_id = :instanceId ");
			paramBuilder.setParameter("instanceId", instanceId);
		}
	}

	public FileMetadata getByPath(final String path) {
		return (FileMetadata) this.getSession().createCriteria(this.getPersistentClass())
			.add(Restrictions.eq("path", path))
			.uniqueResult();
	}

	public void detachFiles(final List<Integer> variableIds, final Integer datasetId, final String germplasmUUID,
		final Integer instanceId) {
		final SQLQuery sqlQuery = this.getSession().createSQLQuery("delete fmc " //
			+ " from file_metadata_cvterm fmc " //
			+ " inner join ( " //
			+ "         select fmc.file_metadata_id, fmc.cvterm_id " //
			+ "         from file_metadata fm " //
			+ "                  left join nd_experiment ne on fm.nd_experiment_id = ne.nd_experiment_id " //
			+ "                  left join germplsm g on fm.gid = g.gid " //
			+ "					 left join nd_geolocation env on env.nd_geolocation_id = f.nd_geolocation_id " //
			+ "                  inner join file_metadata_cvterm fmc on fm.file_id = fmc.file_metadata_id " //
			+ "         where fmc.cvterm_id in (:variableIds)"  //
			+ " 			  and (:datasetId is null or ne.project_id = :datasetId) " //
			+ " 			  and (:germplasmUUID is null or g.germplsm_uuid = :germplasmUUID) "
			+ " 			  and (:instanceId is null or env.nd_geolocation = :instanceId) " //
			+ " ) T on T.file_metadata_id = fmc.file_metadata_id and T.cvterm_id = fmc.cvterm_id ");

		sqlQuery.setParameter("datasetId", datasetId);
		sqlQuery.setParameter("germplasmUUID", germplasmUUID);
		sqlQuery.setParameter("instanceId", instanceId);
		sqlQuery.setParameterList("variableIds", variableIds);
		sqlQuery.executeUpdate();
	}

	/**
	 * Important Note:
	 * file_metadata_cvterm is prepared to link a file to (possibly) many variables,
	 * something that is not currently possible to achieve through the BMS interface,
	 * but it is consistent with the brapi schema (See {@link org.generationcp.middleware.api.brapi.v1.image.Image#descriptiveOntologyTerms}
	 * <br>
	 * If the multiple-variable scenario becomes a reality in the future, this query will need to raise an exception for those cases,
	 * prompting the user to execute a detach variables instead ({@link #detachFiles(List, Integer, String)})
	 */
	public void removeFiles(final List<Integer> variableIds, final Integer datasetId, final String germplasmUUID,
		final Integer instanceId) {
		final List<Integer> fileMetadataIds = this.getSession().createSQLQuery("select fm.file_id " //
			+ "         from file_metadata fm " //
			+ "                  left join nd_experiment ne on fm.nd_experiment_id = ne.nd_experiment_id " //
			+ "                  left join germplsm g on fm.gid = g.gid " //
			+ "					 left join nd_geolocation env on env.nd_geolocation_id = f.nd_geolocation_id " //
			+ "                  inner join file_metadata_cvterm fmc on fm.file_id = fmc.file_metadata_id " //
			+ "         where fmc.cvterm_id in (:variableIds) " //
			+ " 			  and (:datasetId is null or ne.project_id = :datasetId) " //
			+ " 			  and (:germplasmUUID is null or g.germplsm_uuid = :germplasmUUID) "
			+ " 			  and (:instanceId is null or env.nd_geolocation = :instanceId) ")
			.setParameter("datasetId", datasetId)
			.setParameter("germplasmUUID", germplasmUUID)
			.setParameter("instanceId", instanceId)
			.setParameterList("variableIds", variableIds)
			.list();

		/*
		 * We do multiple deletes instead of single delete join (delete fmc, fm from ...) because
		 * "the MySQL optimizer might process tables in an order that differs from that of their parent/child relationship"
		 */

		this.getSession().createSQLQuery("delete from file_metadata_cvterm "
			+ " where file_metadata_id in (:fileMetadataIds)")
			.setParameterList("fileMetadataIds", fileMetadataIds)
			.executeUpdate();

		this.getSession().createSQLQuery("delete from file_metadata "
			+ " where file_metadata.file_id in (:fileMetadataIds)")
			.setParameterList("fileMetadataIds", fileMetadataIds)
			.executeUpdate();
	}

	public void updateGid(final Integer newGid, final List<String> targetFileUUIDs) {
		final String sql = "UPDATE file_metadata SET gid = :gid WHERE file_uuid IN (:targetFileUUIDs)";
		this.getSession().createSQLQuery(sql)
				.setParameter("gid", newGid)
				.setParameterList("targetFileUUIDs", targetFileUUIDs)
				.executeUpdate();
	}

	public List<FileMetadata> getByGids(final List<Integer> gids) {
		return this.getSession().createCriteria(this.getPersistentClass())
				.add(Restrictions.in("germplasm.gid", gids))
				.list();
	}

}
