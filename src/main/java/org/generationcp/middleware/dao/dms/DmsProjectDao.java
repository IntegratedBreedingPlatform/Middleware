/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.dao.dms;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.DataType;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.derived_variables.Formula;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.service.api.study.StudyFilters;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.generationcp.middleware.util.FormulaUtils;
import org.generationcp.middleware.util.Util;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * DAO class for {@link DmsProject}.
 *
 * @author Darla Ani, Joyce Avestro
 */
@SuppressWarnings("unchecked")
public class DmsProjectDao extends GenericDAO<DmsProject, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(DmsProjectDao.class);

	private static final Integer LOCATION_ID = TermId.LOCATION_ID.getId();

	private static final String PROGRAM_UUID = "program_uuid";
	private static final String PROJECT_ID = "projectId";
	public static final String DELETED = "deleted";
	private static final int DELETED_STUDY = 1;

	// getObservationSetVariables scalars
	private static final String OBS_SET_VARIABLE_ID = "variableId";
	private static final String OBS_SET_VARIABLE_NAME = "variableName";
	private static final String OBS_SET_DESCRIPTION = "description";
	private static final String OBS_SET_ALIAS = "alias";
	private static final String OBS_SET_VALUE = "value";
	private static final String OBS_SET_VARIABLE_TYPE_ID = "variableTypeId";
	private static final String OBS_SET_SCALE = "scale";
	private static final String OBS_SET_METHOD = "method";
	private static final String OBS_SET_PROPERTY = "property";
	private static final String OBS_SET_DATA_TYPE_ID = "dataTypeId";
	private static final String OBS_SET_CATEGORY_ID = "categoryId";
	private static final String OBS_SET_CATEGORY_NAME = "categoryName";
	private static final String OBS_SET_CATEGORY_DESCRIPTION = "categoryDescription";
	private static final String OBS_SET_FORMULA_ID = "formulaId";
	private static final String OBS_SET_SCALE_MIN_RANGE = "scaleMinRange";
	private static final String OBS_SET_SCALE_MAX_RANGE = "scaleMaxRange";
	private static final String OBS_SET_EXPECTED_MIN = "expectedMin";
	private static final String OBS_SET_EXPECTED_MAX = "expectedMax";
	private static final String OBS_SET_CROP_ONTOLOGY_ID = "cropOntologyId";
	private static final String OBS_SET_VARIABLE_VALUE = "variableValue";

	/**
	 * Type of study is stored in projectprops table.
	 * Which folder the study is in, is defined in the project_relationship table.
	 */
	static final String GET_CHILDREN_OF_FOLDER =
		"SELECT subject.project_id AS project_id, "
			+ "subject.name AS name,  subject.description AS description, "
			+ "	(CASE WHEN (pr.type_id = " + TermId.IS_STUDY.getId() + ") THEN 1 ELSE 0 END) AS is_study, "
			+ "    subject.program_uuid AS program_uuid, "
			+ "    st.study_type_id AS studyType, st.label as label, st.name as studyTypeName, "
			+ "st.visible as visible, st.cvterm_id as cvtermId, subject.locked as isLocked, "
			+ "u.userId as ownerId, CONCAT(fname, ' ', lname) as ownerName "
			+ "  FROM project subject "
			+ "	INNER JOIN project_relationship pr on subject.project_id = pr.subject_project_id "
			+ "  LEFT JOIN study_type st ON subject.study_type_id = st.study_type_id "
			+ "  LEFT JOIN users u ON u.userid = subject.created_by "
			+ "  LEFT JOIN persons p ON p.personid = u.personid "
			+ " WHERE (pr.type_id = " + TermId.HAS_PARENT_FOLDER.getId() + " or pr.type_id = " + TermId.IS_STUDY.getId() + ")"
			+ "   AND pr.object_project_id = :folderId "
			+ "   AND subject.deleted != " + DELETED_STUDY
			+ "   AND (subject.program_uuid = :program_uuid OR subject.program_uuid IS NULL) "
			+ "   AND (:studyTypeId is null or subject.study_type_id = :studyTypeId or subject.study_type_id is null)"
			// the OR here for value = null is required for folders.
			+ "	ORDER BY name";

	private static final String STUDY_REFERENCE_SQL =
		"SELECT pr.project_id AS project_id, "
			+ "pr.name AS name,  pr.description AS description, pr.program_uuid AS program_uuid, "
			+ "st.study_type_id AS studyType, st.label as label, st.name as studyTypeName, "
			+ "st.visible as visible, st.cvterm_id as cvtermId, pr.locked as isLocked, "
			+ "u.userId as ownerId, CONCAT(fname, ' ', lname) as ownerName "
			+ "  FROM project pr "
			+ "  LEFT JOIN study_type st ON pr.study_type_id = st.study_type_id "
			+ "  LEFT JOIN users u ON u.userid = pr.created_by "
			+ "  LEFT JOIN persons p ON p.personid = u.personid "
			+ " WHERE pr.project_id = :studyId and pr.deleted != " + DELETED_STUDY;

	private static final String GET_STUDIES_OF_FOLDER =
		"SELECT  DISTINCT pr.subject_project_id " + "FROM    project_relationship pr, project p " + "WHERE   pr.type_id = "
			+ TermId.IS_STUDY.getId() + " " + "        AND pr.subject_project_id = p.project_id "
			+ "        AND pr.object_project_id = :folderId " + "		AND p.deleted != " + DELETED_STUDY + " "
			+ "ORDER BY p.name ";

	private static final String GET_ALL_FOLDERS =
		"SELECT pr.object_project_id, pr.subject_project_id, p.name, p.description " + " FROM project_relationship pr "
			+ " INNER JOIN project p ON p.project_id = pr.subject_project_id " + " WHERE pr.type_id = " + TermId.HAS_PARENT_FOLDER
			.getId();

	private static final String GET_ALL_PROGRAM_STUDIES_AND_FOLDERS =
		"SELECT pr.subject_project_id " + "FROM project_relationship pr, project p " + "WHERE pr.type_id = " + TermId.IS_STUDY.getId()
			+ " " + "AND pr.subject_project_id = p.project_id " + "AND p.program_uuid = :program_uuid " + "AND p.deleted != "
			+ DELETED_STUDY + "  " + "UNION SELECT pr.subject_project_id " + "FROM project_relationship pr, project p "
			+ "WHERE pr.type_id = " + TermId.HAS_PARENT_FOLDER.getId() + " " + "AND pr.subject_project_id = p.project_id "
			+ "AND p.program_uuid = :program_uuid ";

	static final String GET_STUDY_METADATA_BY_ID = " SELECT  "
		+ "     geoloc.nd_geolocation_id AS studyDbId, "
		+ "     pmain.project_id AS trialOrNurseryId, "
		+ "		CONCAT(pmain.name, ' Environment Number ', geoloc.description) AS studyName, "
		+ "     pmain.study_type_id AS studyType, "
		+ "     MAX(IF(geoprop.type_id = " + TermId.SEASON_VAR.getId() + ", "
		+ "                 geoprop.value, "
		+ "                 NULL)) AS seasonId, "
		+ "     pmain.project_id AS trialDbId, "
		+ " 	pmain.name AS trialName, "
		+ "     MAX(pmain.start_date) AS startDate, "
		+ "     MAX(pmain.end_date) AS endDate, "
		+ "     pmain.deleted, "
		+ "     MAX(IF(geoprop.type_id = " + TermId.LOCATION_ID.getId() + ", "
		+ "                 geoprop.value, "
		+ "                 NULL)) "
		+ "     AS locationId "
		+ " FROM "
		+ "     nd_geolocation geoloc "
		+ "         INNER JOIN "
		+ "     nd_experiment nde ON nde.nd_geolocation_id = geoloc.nd_geolocation_id "
		+ "         INNER JOIN "
		+ "     project proj ON proj.project_id = nde.project_id "
		+ "         INNER JOIN "
		+ "     project_relationship pr ON proj.project_id = pr.subject_project_id "
		+ "         INNER JOIN "
		+ "     project pmain ON pmain.project_id = pr.object_project_id "
		+ "         AND pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
		+ "         LEFT OUTER JOIN "
		+ "     nd_geolocationprop geoprop ON geoprop.nd_geolocation_id = geoloc.nd_geolocation_id "
		+ "         LEFT OUTER JOIN "
		+ "     projectprop pProp ON pmain.project_id = pProp.project_id "
		+ " WHERE "
		+ "     nde.type_id = " + TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()
		+ "         AND geoloc.nd_geolocation_id = :studyId "
		+ " GROUP BY geoloc.nd_geolocation_id ";

	private static final String GET_PROJECTID_BY_STUDYDBID =
		"SELECT DISTINCT pr.object_project_id"
			+ " FROM project_relationship pr"
			+ " INNER JOIN project p ON p.project_id = pr.subject_project_id"
			+ " INNER JOIN nd_experiment nde ON nde.project_id = pr.subject_project_id"
			+ " INNER JOIN nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id"
			+ " WHERE gl.nd_geolocation_id = :studyDbId"
			+ " AND pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
			+ " AND p.dataset_type_id = " + DatasetTypeEnum.SUMMARY_DATA.getId();

	private static final String STUDY_DETAILS_SQL = " SELECT DISTINCT \n"
		+ "   p.name                     AS name, \n"
		+ "   p.description              AS title, \n"
		+ "   p.objective                AS objective, \n"
		+ "   p.start_date      		 AS startDate, \n"
		+ "   p.end_date		         AS endDate, \n"
		+ "   stype.study_type_id        AS studyTypeId, \n"
		+ "   stype.label                AS studyTypeLabel, \n"
		+ "   stype.name                 AS studyTypeName, \n"
		+ "   ppPI.value                 AS piName, \n"
		+ "   gpSiteName.value           AS siteName, \n"
		+ "   p.project_id               AS id, \n"
		+ "   ppPIid.value               AS piId, \n"
		+ "   gpSiteId.value             AS siteId, \n"
		+ "   ppFolder.object_project_id AS folderId, \n"
		+ "   p.program_uuid             AS programUUID, \n"
		+ "	  p.study_update 			 AS studyUpdate, \n"
		+ "	  p.created_by               AS createdBy, \n"
		+ "   p.locked                   AS isLocked "
		+ " FROM \n"
		+ "   project p \n"
		+ "   INNER JOIN study_type stype on stype.study_type_id = p.study_type_id"
		+ "   INNER JOIN project_relationship ppFolder ON p.project_id = ppFolder.subject_project_id \n"
		+ "   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id AND ppPI.variable_id = " + TermId.PI_NAME.getId() + " \n"
		+ "   LEFT JOIN projectprop ppPIid ON p.project_id = ppPIid.project_id AND ppPIid.variable_id = " + TermId.PI_ID.getId() + " \n"
		+ "   LEFT JOIN nd_experiment e ON e.project_id = p.project_id \n"
		+ "   LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id AND gpSiteName.type_id = "
		+ TermId.TRIAL_LOCATION.getId() + " \n"
		+ "   LEFT JOIN nd_geolocationprop gpSiteId ON e.nd_geolocation_id = gpSiteId.nd_geolocation_id AND gpSiteId.type_id = "
		+ TermId.LOCATION_ID.getId() + " \n"
		+ " WHERE p.project_id = :studyId \n";

	private static final String COUNT_PROJECTS_WITH_VARIABLE =
		"SELECT count(pp.project_id)  FROM projectprop pp inner join project p on (p.project_id = pp.project_id)\n"
			+ "WHERE pp.variable_id = :variableId and p.deleted = 0";
	static final String COUNT_CALCULATED_VARIABLES_IN_DATASETS = "SELECT COUNT(1) FROM projectprop pp\n"
		+ "INNER JOIN formula f ON pp.variable_id = f.target_variable_id\n"
		+ "where project_id in (:projectIds) and type_id = " + VariableType.TRAIT.getId();

	private List<Reference> getChildrenNodesList(final List<Object[]> list) {
		final List<Reference> childrenNodes = new ArrayList<>();
		for (final Object[] row : list) {
			// project.id
			final Integer id = (Integer) row[0];
			// project.name
			final String name = (String) row[1];
			// project.description
			final String description = (String) row[2];
			// non-zero if a study, else a folder
			final Integer isStudy = (Integer) row[3];
			// project.program_uuid
			final String projectUUID = (String) row[4];

			if (isStudy.equals(1)) {
				final Integer studyTypeId = (Integer) row[5];
				final String label = (String) row[6];
				final String studyTypeName = (String) row[7];
				final boolean visible = ((Byte) row[8]) == 1;
				final Integer cvtermId = (Integer) row[9];
				final Boolean isLocked = (Boolean) row[10];
				final StudyTypeDto studyTypeDto = new StudyTypeDto(studyTypeId, label, studyTypeName, cvtermId, visible);
				final Integer ownerId = (Integer) row[11];
				final String ownerName = (String) row[12];
				childrenNodes.add(new StudyReference(id, name, description, projectUUID, studyTypeDto, isLocked, ownerId, ownerName));
			} else {
				childrenNodes.add(new FolderReference(id, name, description, projectUUID));
			}
		}

		return childrenNodes;
	}

	public List<DatasetReference> getDatasetNodesByStudyId(final Integer studyId) {

		final List<DatasetReference> datasetReferences = new ArrayList<>();

		try {

			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.objectProject.projectId", studyId));

			final ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property(DmsProjectDao.PROJECT_ID));
			projectionList.add(Projections.property("name"));
			projectionList.add(Projections.property("description"));
			projectionList.add(Projections.property("pr.objectProject.projectId"));
			criteria.setProjection(projectionList);

			criteria.addOrder(Order.asc("name"));

			final List<Object[]> list = criteria.list();

			for (final Object[] row : list) {
				final Integer id = (Integer) row[0]; // project.id
				final String name = (String) row[1]; // project.name
				final String description = (String) row[2]; // project.description
				datasetReferences.add(new DatasetReference(id, name, description));
			}

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getDatasetNodesByStudyId query from Project: " + e.getMessage(), e);
		}

		return datasetReferences;

	}

	public List<DmsProject> getDatasetsByParent(final Integer studyId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.objectProject.projectId", studyId));
			criteria.setProjection(Projections.property("pr.subjectProject"));
			return criteria.list();

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error in getDatasetsByParent= " + studyId + " query in DmsProjectDao: " + e.getMessage(), e);
		}
	}

	public List<DmsProject> getByIds(final Collection<Integer> projectIds) {
		final List<DmsProject> studyNodes = new ArrayList<>();
		try {
			if (projectIds != null && !projectIds.isEmpty()) {
				final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.in(DmsProjectDao.PROJECT_ID, projectIds));
				criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

				return criteria.list();
			}
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error in getByIds= " + projectIds + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return studyNodes;
	}

	public List<DmsProject> getProjectsByFolder(final Integer folderId, final int start, final int numOfRows) {
		List<DmsProject> projects = new ArrayList<>();
		if (folderId == null) {
			return projects;
		}

		try {
			// Get projects by folder
			final Query query = this.getSession().createSQLQuery(DmsProjectDao.GET_STUDIES_OF_FOLDER);
			query.setParameter("folderId", folderId);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			final List<Integer> projectIds = query.list();
			projects = this.getByIds(projectIds);

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getProjectsByFolder query from Project: " + e.getMessage(), e);
		}

		return projects;
	}

	public long countProjectsByFolder(final Integer folderId) {
		long count = 0;
		if (folderId == null) {
			return count;
		}

		try {
			final Query query = this.getSession().createSQLQuery(DmsProjectDao.GET_STUDIES_OF_FOLDER);
			query.setParameter("folderId", folderId);
			final List<Object[]> list = query.list();
			count = list.size();
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error in countProjectsByFolder(" + folderId + ") query in DmsProjectDao: " + e.getMessage(),
				e);
		}

		return count;

	}

	public Integer getProjectIdByNameAndProgramUUID(final String name, final String programUUID, final TermId relationship) {
		try {
			final String sql = "SELECT s.project_id FROM project s " + " WHERE name = :name AND program_uuid = :program_uuid"
				+ " AND EXISTS (SELECT 1 FROM project_relationship pr WHERE pr.subject_project_id = s.project_id "
				+ "   AND pr.type_id = " + relationship.getId() + ") " + "	AND s.deleted !=  " + DELETED_STUDY + " LIMIT 1";

			final Query query =
				this.getSession().createSQLQuery(sql).setParameter("name", name).setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			return (Integer) query.uniqueResult();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getStudyIdByName=" + name + " query in DmsProjectDao: " + e.getMessage(), e);
		}
	}

	public List<StudyDetails> getAllStudyDetails(final StudyTypeDto studyType, final String programUUID) {
		return this.getAllStudyDetails(studyType, programUUID, -1, -1);
	}

	public List<StudyDetails> getAllStudyDetails(
		final StudyTypeDto studyType, final String programUUID, final int start,
		final int numOfRows) {
		final List<StudyDetails> studyDetails = new ArrayList<>();

		final StringBuilder sqlString = new StringBuilder().append(
			"SELECT DISTINCT p.name AS name, p.description AS title, p.objective AS objective, p.start_date AS startDate, ")
			.append("p.end_date AS endDate, ppPI.value AS piName, gpSiteName.value AS siteName, p.project_id AS id ")
			.append(", ppPIid.value AS piId, gpSiteId.value AS siteId, p.created_by as createdBy, p.locked as isLocked ")
			.append("FROM project p ")
			.append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
			.append("                   AND ppPI.variable_id =  ").append(TermId.PI_NAME.getId()).append(" ")
			.append("   LEFT JOIN projectprop ppPIid ON p.project_id = ppPIid.project_id ")
			.append("                   AND ppPIid.variable_id =  ").append(TermId.PI_ID.getId()).append(" ")
			.append("       LEFT JOIN nd_experiment e ON p.project_id = e.project_id ")
			.append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
			.append("           AND gpSiteName.type_id =  ").append(TermId.TRIAL_LOCATION.getId()).append(" ")
			.append("       LEFT JOIN nd_geolocationprop gpSiteId ON e.nd_geolocation_id = gpSiteId.nd_geolocation_id ")
			.append("           AND gpSiteId.type_id =  ").append(TermId.LOCATION_ID.getId()).append(" ")
			.append("       LEFT JOIN project_relationship pr ON pr.object_project_id = p.project_id and pr.type_id = ")
			.append(TermId.BELONGS_TO_STUDY.getId()).append(" WHERE p.deleted != " + DELETED_STUDY + " ")
			.append(" AND p.study_type_id = '" + studyType.getId() + "'")
			.append(" AND (p.program_uuid = :" + DmsProjectDao.PROGRAM_UUID + " ").append("OR p.program_uuid IS NULL) ")
			.append(" ORDER BY p.name ");
		if (start > 0 && numOfRows > 0) {
			sqlString.append(" LIMIT " + start + "," + numOfRows);
		}

		final List<Object[]> list;

		try {
			final Query query =
				this.getSession().createSQLQuery(sqlString.toString()).addScalar("name").addScalar("title").addScalar("objective")
					.addScalar("startDate").addScalar("endDate").addScalar("piName").addScalar("siteName").addScalar("id")
					.addScalar("piId").addScalar("siteId").addScalar("createdBy").addScalar("isLocked").setParameter(
					DmsProjectDao.PROGRAM_UUID,
					programUUID);
			list = query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in getAllStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
		}

		if (list == null || list.isEmpty()) {
			return studyDetails;
		}

		for (final Object[] row : list) {
			final String name = (String) row[0];
			final String title = (String) row[1];
			final String objective = (String) row[2];
			final String startDate = (String) row[3];
			final String endDate = (String) row[4];
			final String piName = (String) row[5];
			final String siteName = (String) row[6];
			final Integer id = (Integer) row[7];
			final String piId = (String) row[8];
			final String siteId = (String) row[9];
			final String createdBy = (String) row[10];
			final Boolean isLocked = (Boolean) row[11];

			final StudyDetails study =
				new StudyDetails(id, name, title, objective, startDate, endDate, studyType, piName, siteName, piId, siteId, Util
					.getCurrentDateAsStringValue(), createdBy, isLocked);
			studyDetails.add(study);
		}
		return studyDetails;
	}

	public StudyDetails getStudyDetails(final int studyId) {
		StudyDetails studyDetails = null;
		try {

			final Query query =
				this.getSession().createSQLQuery(STUDY_DETAILS_SQL).addScalar("name").addScalar("title").addScalar("objective")
					.addScalar("startDate").addScalar("endDate").addScalar("studyTypeId").addScalar("studyTypeLabel").addScalar(
					"studyTypeName").addScalar("piName").addScalar("siteName").addScalar("id").addScalar("piId").addScalar("siteId")
					.addScalar("folderId").addScalar("programUUID").addScalar("studyUpdate")
					.addScalar("createdBy").addScalar("isLocked");

			query.setParameter("studyId", studyId);

			final List<Object[]> list = query.list();

			if (list != null && !list.isEmpty()) {
				for (final Object[] row : list) {
					final String name = (String) row[0];
					final String title = (String) row[1];
					final String objective = (String) row[2];
					final String startDate = (String) row[3];
					final String endDate = (String) row[4];
					final Integer studyTypeId = (Integer) row[5];
					final String studyTypeLabel = (String) row[6];
					final String studyTypeName = (String) row[7];
					final String piName = (String) row[8];
					final String siteName = (String) row[9];
					final Integer id = (Integer) row[10];
					final String piId = (String) row[11];
					final String siteId = (String) row[12];
					final Integer folderId = (Integer) row[13];
					final String programUUID = (String) row[14];
					final String studyUpdate = (String) row[15];
					final String createdBy = (String) row[16];
					final Boolean isLocked = (Boolean) row[17];

					final StudyTypeDto studyTypeDto = new StudyTypeDto(studyTypeId, studyTypeLabel, studyTypeName);

					studyDetails =
						new StudyDetails(id, name, title, objective, startDate, endDate, studyTypeDto, piName, siteName, piId, siteId,
							studyUpdate, createdBy, isLocked);
					studyDetails.setParentFolderId(folderId.longValue());
					studyDetails.setProgramUUID(programUUID);
				}
			}

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error in getTrialObservationTable() query in DmsProjectDao: " + e.getMessage(), e);
		}
		return studyDetails;
	}

	public long countAllStudyDetails(final StudyTypeDto studyType, final String programUUID) {
		try {
			final StringBuilder sqlString =
				new StringBuilder()
					.append("SELECT COUNT(1) ").append("FROM project p ")
					.append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
					.append("                   AND ppPI.variable_id =  ")
					.append(TermId.PI_NAME.getId())
					.append(" ")
					.append("       LEFT JOIN nd_experiment e ON p.project_id = e.project_id ")
					.append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
					.append("           AND gpSiteName.type_id =  ").append(TermId.TRIAL_LOCATION.getId()).append(" ")
					.append("WHERE p.deleted != " + DELETED_STUDY + " ")
					.append(" AND p.study_type_id = '" + studyType.getId())
					.append("'   AND (p.").append(DmsProjectDao.PROGRAM_UUID)
					.append(" = :").append(DmsProjectDao.PROGRAM_UUID).append(" ").append("   OR p.")
					.append(DmsProjectDao.PROGRAM_UUID).append(" IS NULL) ");

			final Query query =
				this.getSession().createSQLQuery(sqlString.toString()).setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error in countAllStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
		}

	}

	public List<StudyDetails> getAllStudyDetails(final String programUUID) {
		return this.getAllStudyDetails(programUUID, 0, -1);
	}

	public List<StudyDetails> getAllStudyDetails(final String programUUID, final int start, final int numOfRows) {
		final List<StudyDetails> studyDetails = new ArrayList<>();
		try {

			final StringBuilder sqlString = new StringBuilder()
				.append("SELECT DISTINCT p.name AS name, p.description AS title, p.objective AS objective, p.start_date AS startDate, ")
				.append("p.end_date AS endDate, ppPI.value AS piName, gpSiteName.value AS siteName, p.project_id AS id, st"
					+ ".study_type_id AS "
					+ "studyType , st.label as label, st.name as studyTypeName, st.visible as visible, st.cvterm_id as cvtermId ")
				.append(", ppPIid.value AS piId, gpSiteId.value AS siteId, p.created_by as createdBy, p.locked as isLocked ")
				.append("FROM project p ")
				.append(" LEFT JOIN projectprop  ppPI ON p.project_id = ppPI.project_id ").append(" AND ppPI.variable_id =  ")
				.append(TermId.PI_NAME.getId()).append(" ")
				// 8100
				.append(" LEFT JOIN projectprop ppPIid ON p.project_id = ppPIid.project_id ")
				.append(" AND ppPIid.variable_id =  ").append(TermId.PI_ID.getId()).append(" ")
				.append(" INNER JOIN nd_experiment e ON e.project_id = p.project_id ")
				.append(" LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
				.append(" AND gpSiteName.type_id =  ").append(TermId.TRIAL_LOCATION.getId()).append(" ")
				// 8180
				.append(" LEFT JOIN nd_geolocationprop gpSiteId ON e.nd_geolocation_id = gpSiteId.nd_geolocation_id ")
				.append(" AND gpSiteId.type_id =  ").append(TermId.LOCATION_ID.getId()).append(" ")
				.append(" LEFT JOIN study_type st ON p.study_type_id = st.study_type_id ")
				.append(" WHERE p.deleted != " + DELETED_STUDY + " ")
				.append(" AND p.study_type_id IS NOT NULL ")
				.append(" AND (p.program_uuid = :" + DmsProjectDao.PROGRAM_UUID + " ").append("OR p.program_uuid IS NULL) ")
				.append(" ORDER BY p.name ");
			if (start > 0 && numOfRows > 0) {
				sqlString.append(" LIMIT " + start + "," + numOfRows);
			}

			final Query query =
				this.getSession().createSQLQuery(sqlString.toString()).addScalar("name").addScalar("title").addScalar("objective")
					.addScalar("startDate").addScalar("endDate").addScalar("piName").addScalar("siteName").addScalar("id")
					.addScalar("studyType").addScalar("label").addScalar("studyTypeName").addScalar("visible").addScalar("cvTermId")
					.addScalar("piId").addScalar("siteId").addScalar("createdBy").addScalar("isLocked")
					.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);

			final List<Object[]> list = query.list();

			if (list != null && !list.isEmpty()) {
				for (final Object[] row : list) {
					final String name = (String) row[0];
					final String title = (String) row[1];
					final String objective = (String) row[2];
					final String startDate = (String) row[3];
					final String endDate = (String) row[4];
					final String piName = (String) row[5];
					final String siteName = (String) row[6];
					final Integer id = (Integer) row[7];
					final Integer studyTypeId = (Integer) row[8];
					final String label = (String) row[9];
					final String studyTypeName = (String) row[10];
					final boolean visible = ((Byte) row[11]) == 1;
					final Integer cvTermId = (Integer) row[12];
					final String piId = (String) row[13];
					final String siteId = (String) row[14];
					final String createdBy = (String) row[15];
					final Boolean isLocked = (Boolean) row[16];

					final StudyTypeDto studyTypeDto = new StudyTypeDto(studyTypeId, label, studyTypeName, cvTermId, visible);
					studyDetails.add(
						new StudyDetails(id, name, title, objective, startDate, endDate, studyTypeDto, piName, siteName, piId, siteId,
							Util.getCurrentDateAsStringValue(), createdBy, isLocked));
				}
			}

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error in getAllStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
		}
		return studyDetails;

	}

	public long countAllStudyDetails(final String programUUID) {
		try {

			final StringBuilder sqlString =
				new StringBuilder()
					.append("SELECT COUNT(1) ").append("FROM project p ")
					.append(" ")
					.append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
					.append("                   AND ppPI.variable_id =  ")
					.append(TermId.PI_NAME.getId())
					.append(" ")
					// 8100
					.append("       INNER JOIN nd_experiment e ON e.project_id = p.project_id ")
					.append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
					.append("           AND gpSiteName.type_id =  ")
					.append(TermId.TRIAL_LOCATION.getId())
					.append(" ")
					// 8180
					.append("WHERE p.deleted != " + DELETED_STUDY + " ")
					.append(" AND p.study_type_id IS NOT NULL ")
					.append("   AND (p.").append(DmsProjectDao.PROGRAM_UUID).append(" = :").append(DmsProjectDao.PROGRAM_UUID)
					.append(" ").append("   OR p.").append(DmsProjectDao.PROGRAM_UUID).append(" IS NULL) ");

			final Query query =
				this.getSession().createSQLQuery(sqlString.toString()).setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error in countAllStudyDetails() query in DmsProjectDao: " + e.getMessage(),
				e);
		}

	}

	@SuppressWarnings("rawtypes")
	public boolean checkIfProjectNameIsExistingInProgram(final String name, final String programUUID) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("name", name));
			criteria.add(Restrictions.eq("programUUID", programUUID));

			final List list = criteria.list();
			if (list != null && !list.isEmpty()) {
				return true;
			}

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error in checkIfProjectNameIsExisting=" + name + " query on DmsProjectDao: " + e.getMessage(), e);
		}

		return false;
	}

	public List<FolderReference> getAllFolders() {
		final List<FolderReference> folders = new ArrayList<>();
		try {
			final SQLQuery query = this.getSession().createSQLQuery(DmsProjectDao.GET_ALL_FOLDERS);
			final List<Object[]> result = query.list();
			if (result != null && !result.isEmpty()) {
				for (final Object[] row : result) {
					folders.add(new FolderReference((Integer) row[0], (Integer) row[1], (String) row[2], (String) row[3]));
				}
			}

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error at getAllFolders, query at DmsProjectDao: " + e.getMessage(), e);
		}
		return folders;
	}

	public List<Integer> getAllProgramStudiesAndFolders(final String programUUID) {
		final List<Integer> projectIds;
		try {
			final SQLQuery query = this.getSession().createSQLQuery(DmsProjectDao.GET_ALL_PROGRAM_STUDIES_AND_FOLDERS);
			query.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			projectIds = query.list();
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error at getAllProgramStudiesAndFolders, query at DmsProjectDao: " + e.getMessage(), e);
		}
		return projectIds;
	}

	public Integer getProjectIdByStudyDbId(final int studyDbId) {
		try {
			final Query query = this.getSession().createSQLQuery(GET_PROJECTID_BY_STUDYDBID);
			query.setParameter("studyDbId", studyDbId);
			return (Integer) query.uniqueResult();
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}

	public Integer getProjectIdByNameAndProgramUUID(final String name, final String programUUID) {
		try {
			final String sql =
				"SELECT project_id FROM project WHERE name = :name AND program_uuid = :program_uuid and study_type_id is not null";
			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("name", name);
			query.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			final List<Integer> list = query.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getDistinctProjectDescription() query from Project " + e.getMessage(), e);
		}
		return null;
	}

	public List<String> getAllSharedProjectNames() {
		try {
			final String sql = "SELECT name FROM project WHERE program_uuid is null";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			return query.list();
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getAllSharedProjectNames()" + e.getMessage(), e);
		}
	}

	public List<DmsProject> findPagedProjects(final Map<StudyFilters, String> filters, final Integer pageSize, final Integer pageNumber) {

		final Criteria criteria = this.buildCoreCriteria(filters, this.getOrderBy(filters));
		if (pageNumber != null && pageSize != null) {
			criteria.setFirstResult(pageSize * (pageNumber - 1));
			criteria.setMaxResults(pageSize);
		}
		return criteria.list();
	}

	private Order getOrderBy(final Map<StudyFilters, String> filters) {
		if (filters.containsKey(StudyFilters.SORT_BY_FIELD)) {
			if ("asc".equals(filters.get(StudyFilters.ORDER))) {
				return Order.asc(filters.get(StudyFilters.SORT_BY_FIELD));
			} else {
				return Order.desc(filters.get(StudyFilters.SORT_BY_FIELD));
			}
		}
		return Order.asc(DmsProjectDao.PROJECT_ID);
	}

	public long countStudies(final Map<StudyFilters, String> filters) {
		final Criteria criteria = this.buildCoreCriteria(filters, this.getOrderBy(filters));
		criteria.setProjection(Projections.rowCount());
		return (long) criteria.uniqueResult();
	}

	private Criteria buildCoreCriteria(final Map<StudyFilters, String> parameters, final Order orderBy) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.isNotNull("studyType"));

		criteria.add(Restrictions.ne(DmsProjectDao.DELETED, true));

		if (parameters.containsKey(StudyFilters.PROGRAM_ID)) {
			criteria.add(Restrictions.eq(StudyFilters.PROGRAM_ID.getParameter(), parameters.get(StudyFilters.PROGRAM_ID)));
		} else {
			criteria.add(Restrictions.isNotNull(StudyFilters.PROGRAM_ID.getParameter()));
		}

		if (parameters.containsKey(StudyFilters.LOCATION_ID)) {
			// Find environments with specified location (saved in GeolocationProperty)
			final DetachedCriteria locationCriteria = DetachedCriteria.forClass(ExperimentModel.class);
			locationCriteria.add(Restrictions.eq("typeId", TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()));
			locationCriteria.createAlias("geoLocation", "g");
			locationCriteria.createAlias("g.properties", "gp");
			locationCriteria.add(Restrictions.and(
				Restrictions.eq("gp.typeId", DmsProjectDao.LOCATION_ID),
				Restrictions.eq("gp.value", parameters.get(StudyFilters.LOCATION_ID))));
			locationCriteria.setProjection(Projections.property("project.projectId"));

			criteria.createAlias("relatedBys", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Property.forName("pr.subjectProject.projectId").in(locationCriteria));
		}

		criteria.addOrder(orderBy);
		return criteria;
	}

	public StudyMetadata getStudyMetadata(final Integer studyId) {
		Preconditions.checkNotNull(studyId);
		try {
			final SQLQuery query = this.getSession().createSQLQuery(DmsProjectDao.GET_STUDY_METADATA_BY_ID);
			query.addScalar("studyDbId");
			query.addScalar("trialOrNurseryId");
			query.addScalar("studyName");
			query.addScalar("studyType");
			query.addScalar("seasonId");
			query.addScalar("trialDbId");
			query.addScalar("trialName");
			query.addScalar("startDate");
			query.addScalar("endDate");
			query.addScalar("deleted");
			query.addScalar("locationID");
			query.setParameter("studyId", studyId);
			final Object result = query.uniqueResult();
			if (result != null) {
				final Object[] row = (Object[]) result;
				final StudyMetadata studyMetadata = new StudyMetadata();
				studyMetadata.setStudyDbId(studyId);
				studyMetadata.setNurseryOrTrialId((row[1] instanceof Integer) ? (Integer) row[1] : null);
				studyMetadata.setStudyName((row[2] instanceof String) ? (String) row[2] : null);
				studyMetadata.setStudyType((row[3] instanceof String) ? (String) row[3] : null);
				if (row[4] instanceof String && !StringUtils.isBlank((String) row[4])) {
					studyMetadata.addSeason(TermId.getById(Integer.parseInt((String) row[4])).toString());
				}
				studyMetadata.setTrialDbId(
					(row[5] instanceof String && StringUtils.isNumeric((String) row[5])) ? Integer.parseInt((String) row[5]) : null);
				studyMetadata.setTrialName((row[6] instanceof String) ? (String) row[6] : null);
				studyMetadata.setStartDate((row[7] instanceof String) ? (String) row[7] : null);
				studyMetadata.setEndDate((row[8] instanceof String) ? (String) row[8] : null);
				studyMetadata.setActive(Boolean.FALSE.equals(row[9]));
				studyMetadata.setLocationId((row[10] instanceof String) ? Integer.parseInt((String) row[10]) : null);
				return studyMetadata;
			} else {
				return null;
			}
		} catch (final HibernateException e) {
			final String message = "Error with getStudyMetadata() query from study: " + studyId;
			DmsProjectDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	/**
	 * Detect the usage of the specified variable in any programs except for the specified programUUID.
	 *
	 * @param variableId    - The term id of the variable (e.g. 8190 to look for variable LOCATION_NAME_ID)
	 * @param variableValue - The value of the variable (e.g. 101 which is the location name id of the location "India")
	 * @param programUUID
	 * @return
	 */
	public boolean isVariableUsedInOtherPrograms(final String variableId, final String variableValue, final String programUUID) {
		Preconditions.checkNotNull(variableId);
		Preconditions.checkNotNull(variableValue);

		// Check if the variable is used in trial level and/or environment level of studies except for the specified programUUID.
		final SQLQuery query = this.getSession().createSQLQuery(
			"SELECT CASE WHEN\n" + "            (EXISTS( SELECT project.* FROM\n" + "                    projectprop INNER JOIN\n"
				+ "                    project ON project.project_id = projectprop.project_id WHERE\n"
				+ "                    projectprop.variable_id = :variableId AND projectprop.value = :variableValue\n"
				+ "                        AND project.program_uuid <> :programUUID AND project.deleted = 0)) = 1 " + "						OR "
				+ "				(EXISTS( SELECT \n" + "                    project.* FROM project\n"
				+ "                        INNER JOIN\n"
				+ "                    nd_experiment ON nd_experiment.project_id = project.project_id\n"
				+ "                        INNER JOIN\n"
				+ "                    nd_geolocationprop ON nd_experiment.nd_geolocation_id = nd_geolocationprop.nd_geolocation_id"
				+ "                WHERE nd_geolocationprop.type_id = :variableId\n"
				+ "                        AND nd_geolocationprop.value = :variableValue\n"
				+ "                        AND project.program_uuid <> :programUUID AND project.deleted = 0)) = 1 THEN 1 ELSE 0\n"
				+ "    END;");
		query.setParameter("variableId", variableId);
		query.setParameter("variableValue", variableValue);
		query.setParameter("programUUID", programUUID);

		return ((BigInteger) query.uniqueResult()).intValue() != 0;

	}

	/***
	 * Count calculated traits in the speficified datasets.
	 * @param projectIds
	 * @return
	 */
	public int countCalculatedVariablesInDatasets(final Set<Integer> projectIds) {
		// Check if the variable is used in trial level and/or environment level of studies except for the specified programUUID.
		final SQLQuery query = this.getSession().createSQLQuery(COUNT_CALCULATED_VARIABLES_IN_DATASETS);
		query.setParameterList("projectIds", projectIds);
		return ((BigInteger) query.uniqueResult()).intValue();

	}

	public String getProjectStartDateByProjectId(final int projectId) {
		try {
			final String sql = "SELECT start_date FROM project WHERE project_id = :projectId";
			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("projectId", projectId);
			final List<String> list = query.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException("Error with getProjectStartDateByProjectId() query from Project " + e.getMessage(), e);
		}
		return null;
	}

	public List<Reference> getRootFolders(final String programUUID, final Integer studyType) {
		return this.getChildrenOfFolder(DmsProject.SYSTEM_FOLDER_ID, programUUID, studyType);
	}

	public List<Reference> getChildrenOfFolder(final Integer folderId, final String programUUID, final Integer studyType) {

		final List<Reference> childrenNodes;

		try {
			final Query query =
				this.getSession().createSQLQuery(DmsProjectDao.GET_CHILDREN_OF_FOLDER).addScalar("project_id").addScalar("name")
					.addScalar("description").addScalar("is_study", new IntegerType()).addScalar("program_uuid").addScalar("studyType")
					.addScalar("label")
					.addScalar("studyTypeName").addScalar("visible").addScalar("cvtermId").addScalar("isLocked").addScalar("ownerId")
					.addScalar("ownerName");
			query.setParameter("folderId", folderId);
			query.setParameter("studyTypeId", studyType);
			query.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);

			final List<Object[]> list = query.list();
			childrenNodes = this.getChildrenNodesList(list);

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error retrieving study folder tree, folderId=" + folderId + " programUUID=" + programUUID + ":" + e.getMessage(), e);
		}

		return childrenNodes;
	}

	public StudyReference getStudyReference(final Integer studyId) {
		StudyReference studyReference = null;

		try {
			final Query query =
				this.getSession().createSQLQuery(DmsProjectDao.STUDY_REFERENCE_SQL).addScalar("project_id").addScalar("name")
					.addScalar("description").addScalar("program_uuid").addScalar("studyType").addScalar("label")
					.addScalar("studyTypeName").addScalar("visible").addScalar("cvtermId").addScalar("isLocked").addScalar("ownerId")
					.addScalar("ownerName");
			query.setParameter("studyId", studyId);

			final List<Object[]> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (final Object[] row : list) {
					final Integer id = (Integer) row[0];
					final String name = (String) row[1];
					final String description = (String) row[2];
					final String projectUUID = (String) row[3];
					final Integer studyTypeId = (Integer) row[4];
					final String label = (String) row[5];
					final String studyTypeName = (String) row[6];
					final boolean visible = ((Byte) row[7]) == 1;
					final Integer cvtermId = (Integer) row[8];
					final Boolean isLocked = (Boolean) row[9];
					final StudyTypeDto studyTypeDto = new StudyTypeDto(studyTypeId, label, studyTypeName, cvtermId, visible);
					final Integer ownerId = (Integer) row[10];
					final String ownerName = (String) row[11];
					studyReference = new StudyReference(id, name, description, projectUUID, studyTypeDto, isLocked, ownerId, ownerName);
				}
			}

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error getting StudyReference for studyId=" + studyId + ":" + e.getMessage(), e);
		}

		return studyReference;
	}

	public List<MeasurementVariable> getObservationSetVariables(final Integer observationSetId, final List<Integer> variableTypes) {

		try {
			final String query = " SELECT distinct "  //
				+ "   pp.variable_id AS " + OBS_SET_VARIABLE_ID + ", "  //
				+ "   variable.name AS " + OBS_SET_VARIABLE_NAME + ", "  //
				+ "   variable.definition AS " + OBS_SET_DESCRIPTION + ", "  //
				+ "   pp.alias AS " + OBS_SET_ALIAS + ", "  //
				+ "   pp.value as " + OBS_SET_VALUE + ", "
				+ "   variableType.cvterm_id AS " + OBS_SET_VARIABLE_TYPE_ID + ", "  //
				+ "   scale.name AS " + OBS_SET_SCALE + ", "  //
				+ "   method.name AS " + OBS_SET_METHOD + ", "  //
				+ "   property.name AS " + OBS_SET_PROPERTY + ", "  //
				+ "   dataType.cvterm_id AS " + OBS_SET_DATA_TYPE_ID + ", "  //
				+ "   category.cvterm_id AS " + OBS_SET_CATEGORY_ID + ", "  //
				+ "   category.name AS " + OBS_SET_CATEGORY_NAME + ", "  //
				+ "   category.definition AS " + OBS_SET_CATEGORY_DESCRIPTION + ", "  //
				+ "   (SELECT formula_id FROM formula WHERE target_variable_id = pp.variable_id and active = 1 LIMIT 1) AS "
				+ OBS_SET_FORMULA_ID + ", "
				+ "   scaleMinRange.value AS " + OBS_SET_SCALE_MIN_RANGE + ", "  //
				+ "   scaleMaxRange.value AS " + OBS_SET_SCALE_MAX_RANGE + ", "  //
				+ "   vo.expected_min AS " + OBS_SET_EXPECTED_MIN + ", "  //
				+ "   vo.expected_max AS " + OBS_SET_EXPECTED_MAX + ", "  //
				+ "   cropOntology.value AS " + OBS_SET_CROP_ONTOLOGY_ID + ","
				+ "   pp.value as " + OBS_SET_VARIABLE_VALUE
				+ " FROM project dataset "  //
				+ "   INNER JOIN projectprop pp ON dataset.project_id = pp.project_id "  //
				+ "   INNER JOIN cvterm variable ON pp.variable_id = variable.cvterm_id "  //
				+ "   INNER JOIN cvterm variableType ON pp.type_id = variableType.cvterm_id "  //
				+ "   INNER JOIN cvterm_relationship cvtrscale ON variable.cvterm_id = cvtrscale.subject_id " //
				+ "                                            AND cvtrscale.type_id = " + TermId.HAS_SCALE.getId()  //
				+ "   INNER JOIN cvterm scale ON cvtrscale.object_id = scale.cvterm_id "  //
				+ "   INNER JOIN cvterm_relationship cvtrmethod ON variable.cvterm_id = cvtrmethod.subject_id " //
				+ "                                             AND cvtrmethod.type_id = " + TermId.HAS_METHOD.getId() //
				+ "   INNER JOIN cvterm method ON cvtrmethod.object_id = method.cvterm_id "  //
				+ "   INNER JOIN cvterm_relationship cvtrproperty ON variable.cvterm_id = cvtrproperty.subject_id " //
				+ "                                               AND cvtrproperty.type_id = " + TermId.HAS_PROPERTY.getId() //
				+ "   INNER JOIN cvterm property ON cvtrproperty.object_id = property.cvterm_id "  //
				+ "   INNER JOIN cvterm_relationship cvtrdataType ON scale.cvterm_id = cvtrdataType.subject_id " //
				+ "                                               AND cvtrdataType.type_id = " + TermId.HAS_TYPE.getId() //
				+ "   INNER JOIN cvterm dataType ON cvtrdataType.object_id = dataType.cvterm_id "  //
				+ "   LEFT JOIN cvterm_relationship cvtrcategory ON scale.cvterm_id = cvtrcategory.subject_id "
				+ "                                              AND cvtrcategory.type_id = " + TermId.HAS_VALUE.getId() //
				+ "   LEFT JOIN cvterm category ON cvtrcategory.object_id = category.cvterm_id "  //
				+ "   LEFT JOIN cvtermprop scaleMaxRange on scale.cvterm_id = scaleMaxRange.cvterm_id " //
				+ "                                         AND scaleMaxRange.type_id = " + TermId.MAX_VALUE.getId() //
				+ "   LEFT JOIN cvtermprop scaleMinRange on scale.cvterm_id = scaleMinRange.cvterm_id " //
				+ "                                         AND scaleMinRange.type_id = " + TermId.MIN_VALUE.getId() //
				+ "   LEFT JOIN variable_overrides vo ON variable.cvterm_id = vo.cvterm_id "  //
				+ "                                      AND dataset.program_uuid = vo.program_uuid " //
				+ "   LEFT JOIN cvtermprop cropOntology ON cropOntology.cvterm_id = variable.cvterm_id" //
				+ "        AND cropOntology.type_id = " + TermId.CROP_ONTOLOGY_ID.getId()
				+ " WHERE " //
				+ "   dataset.project_id = :observationSetId " //
				+ "   AND pp.type_id in (:variableTypes) "
				+ " ORDER BY pp.rank ";

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(query);
			sqlQuery.setParameter("observationSetId", observationSetId);
			sqlQuery.setParameterList("variableTypes", variableTypes);
			sqlQuery
				.addScalar(OBS_SET_VARIABLE_ID)
				.addScalar(OBS_SET_VARIABLE_NAME)
				.addScalar(OBS_SET_DESCRIPTION)
				.addScalar(OBS_SET_ALIAS)
				.addScalar(OBS_SET_VALUE)
				.addScalar(OBS_SET_VARIABLE_TYPE_ID)
				.addScalar(OBS_SET_SCALE)
				.addScalar(OBS_SET_METHOD)
				.addScalar(OBS_SET_PROPERTY)
				.addScalar(OBS_SET_DATA_TYPE_ID)
				.addScalar(OBS_SET_CATEGORY_ID)
				.addScalar(OBS_SET_CATEGORY_NAME)
				.addScalar(OBS_SET_CATEGORY_DESCRIPTION)
				.addScalar(OBS_SET_SCALE_MIN_RANGE, new DoubleType())
				.addScalar(OBS_SET_SCALE_MAX_RANGE, new DoubleType())
				.addScalar(OBS_SET_EXPECTED_MIN, new DoubleType())
				.addScalar(OBS_SET_EXPECTED_MAX, new DoubleType())
				.addScalar(OBS_SET_FORMULA_ID, new IntegerType())
				.addScalar(OBS_SET_CROP_ONTOLOGY_ID)
				.addScalar(OBS_SET_VARIABLE_VALUE);

			sqlQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			final List<Map<String, Object>> results = sqlQuery.list();

			final Map<Integer, MeasurementVariable> variables = new LinkedHashMap<>();

			for (final Map<String, Object> result : results) {
				final Integer variableId = (Integer) result.get("variableId");

				if (!variables.containsKey(variableId)) {
					variables.put(variableId, new MeasurementVariable());

					final MeasurementVariable measurementVariable = variables.get(variableId);

					measurementVariable.setTermId(variableId);
					measurementVariable.setName((String) result.get(OBS_SET_VARIABLE_NAME));
					measurementVariable.setAlias((String) result.get(OBS_SET_ALIAS));
					measurementVariable.setValue((String) result.get(OBS_SET_VALUE));
					measurementVariable.setDescription((String) result.get(OBS_SET_DESCRIPTION));
					measurementVariable.setScale((String) result.get(OBS_SET_SCALE));
					measurementVariable.setMethod((String) result.get(OBS_SET_METHOD));
					measurementVariable.setProperty((String) result.get(OBS_SET_PROPERTY));
					final VariableType variableType = VariableType.getById((Integer) result.get(OBS_SET_VARIABLE_TYPE_ID));
					measurementVariable.setVariableType(variableType);
					//TODO: fix the saving of Treatment Factor Variables in the projectprop table.
					// Right now, the saved typeid is 1100. It should be 1809(VariableType.TREATMENT_FACTOR.getid())
					if (variableType != null) {
						measurementVariable.setFactor(!variableType.getRole().equals(PhenotypicType.VARIATE));
					}
					final DataType dataType = DataType.getById((Integer) result.get(OBS_SET_DATA_TYPE_ID));
					measurementVariable.setDataType(dataType.getName());
					measurementVariable.setDataTypeId(dataType.getId());

					final Integer formulaId = (Integer) result.get(OBS_SET_FORMULA_ID);
					if (formulaId != null) {
						final Formula formula = (Formula) this.getSession()
							.createCriteria(Formula.class)
							.add(Restrictions.eq("formulaId", formulaId))
							.add(Restrictions.eq("active", true))
							.uniqueResult();
						if (formula != null) {
							measurementVariable.setFormula(FormulaUtils.convertToFormulaDto(formula));
						}
					}

					final Double scaleMinRange = (Double) result.get(OBS_SET_SCALE_MIN_RANGE);
					final Double scaleMaxRange = (Double) result.get(OBS_SET_SCALE_MAX_RANGE);
					final Double expectedMin = (Double) result.get(OBS_SET_EXPECTED_MIN);
					final Double expectedMax = (Double) result.get(OBS_SET_EXPECTED_MAX);

					measurementVariable.setMinRange(expectedMin != null ? expectedMin : scaleMinRange);
					measurementVariable.setMaxRange(expectedMax != null ? expectedMax : scaleMaxRange);
					measurementVariable.setScaleMinRange(scaleMinRange);
					measurementVariable.setScaleMaxRange(scaleMaxRange);
					measurementVariable.setVariableMinRange(expectedMin);
					measurementVariable.setVariableMaxRange(expectedMax);
					measurementVariable.setCropOntology((String) result.get(OBS_SET_CROP_ONTOLOGY_ID));
				}

				final MeasurementVariable measurementVariable = variables.get(variableId);

				if (measurementVariable.getValue() == null || measurementVariable.getValue().isEmpty()) {
					measurementVariable.setValue((String) result.get(OBS_SET_VARIABLE_VALUE));
				}

				final Object categoryId = result.get(OBS_SET_CATEGORY_ID);
				if (categoryId != null) {
					if (measurementVariable.getPossibleValues() == null || measurementVariable.getPossibleValues().isEmpty()) {
						measurementVariable.setPossibleValues(new ArrayList<ValueReference>());
					}
					final ValueReference valueReference = //
						new ValueReference(
							(Integer) categoryId, //
							Objects.toString(result.get(OBS_SET_CATEGORY_NAME)), //
							Objects.toString(result.get(OBS_SET_CATEGORY_DESCRIPTION)));
					if (!measurementVariable.getPossibleValues().contains(valueReference)) {
						measurementVariable.getPossibleValues().add(valueReference);
					}
				}
			}

			return new ArrayList<>(variables.values());
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error getting datasets variables for dataset=" + observationSetId + ": " + e.getMessage(), e);
		}
	}

	public void lockUnlockStudy(final Integer studyId, final Boolean isLocked) {
		Preconditions.checkNotNull(studyId);

		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq(PROJECT_ID, studyId));
		final DmsProject study = (DmsProject) criteria.uniqueResult();
		if (study != null) {
			study.setLocked(isLocked);
			this.save(study);
		}
	}

	public List<DatasetDTO> getDatasets(final Integer parentId) {
		final List<DatasetDTO> datasetDTOS;
		try {

			final ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("project.projectId"), "datasetId");
			projectionList.add(Projections.property("dt.datasetTypeId"), "datasetTypeId");
			projectionList.add(Projections.property("project.name"), "name");
			projectionList.add(Projections.property("pr.objectProject.projectId"), "parentDatasetId");
			final Criteria criteria = this.getSession().createCriteria(DmsProject.class, "project");
			criteria.createAlias("project.relatedTos", "pr");
			criteria.createAlias("project.datasetType", "dt");
			criteria.add(Restrictions.eq("pr.objectProject.projectId", parentId));
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.setProjection(projectionList);
			criteria.setResultTransformer(Transformers.aliasToBean(DatasetDTO.class));
			datasetDTOS = criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error getting getDatasets for studyId=" + parentId + ":" + e.getMessage(), e);
		}
		return datasetDTOS;

	}

	public List<StudyInstance> getDatasetInstances(final int datasetId) {

		try {
			final String sql = "select \n" + "	geoloc.nd_geolocation_id as INSTANCE_DBID, \n"
				+ "	max(if(geoprop.type_id = 8190, loc.lname, null)) as LOCATION_NAME, \n" + // 8180 = cvterm for LOCATION_NAME
				"	max(if(geoprop.type_id = 8190, loc.labbr, null)) as LOCATION_ABBR, \n" + // 8189 = cvterm for LOCATION_ABBR
				"	max(if(geoprop.type_id = 8189, geoprop.value, null)) as CUSTOM_LOCATION_ABBR, \n" +
				// 8189 = cvterm for CUSTOM_LOCATION_ABBR
				"	max(if(geoprop.type_id = 8583, geoprop.value, null)) as FIELDMAP_BLOCK, \n" +
				// 8583 = cvterm for BLOCK_ID (meaning instance has fieldmap)
				"   geoloc.description as INSTANCE_NUMBER \n" + " from \n" + "	nd_geolocation geoloc \n"
				+ "    inner join nd_experiment nde on nde.nd_geolocation_id = geoloc.nd_geolocation_id \n"
				+ "    inner join project proj on proj.project_id = nde.project_id \n"
				+ "    left outer join nd_geolocationprop geoprop on geoprop.nd_geolocation_id = geoloc.nd_geolocation_id \n"
				+ "	   left outer join location loc on geoprop.value = loc.locid and geoprop.type_id = 8190 \n"
				+ " where proj.project_id = :datasetId \n"
				+ "    group by geoloc.nd_geolocation_id \n" + "    order by (1 * geoloc.description) asc ";

			final SQLQuery query = this.getSession().createSQLQuery(sql);
			query.setParameter("datasetId", datasetId);
			query.addScalar("INSTANCE_DBID", new IntegerType());
			query.addScalar("LOCATION_NAME", new StringType());
			query.addScalar("LOCATION_ABBR", new StringType());
			query.addScalar("CUSTOM_LOCATION_ABBR", new StringType());
			query.addScalar("FIELDMAP_BLOCK", new StringType());
			query.addScalar("INSTANCE_NUMBER", new IntegerType());

			final List queryResults = query.list();
			final List<StudyInstance> instances = new ArrayList<>();
			for (final Object result : queryResults) {
				final Object[] row = (Object[]) result;
				final boolean hasFieldmap = !StringUtils.isEmpty((String) row[4]);
				final StudyInstance instance =
					new StudyInstance((Integer) row[0], (String) row[1], (String) row[2], (Integer) row[5], (String) row[3], hasFieldmap);
				instances.add(instance);
			}
			return instances;
		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
				"Unexpected error in executing getDatasetInstances(datasetId = " + datasetId + ") query: " + he.getMessage(), he);
		}
	}

	public void deleteDataset(final int datasetId) {
		try {
			// Please note we are manually flushing because non hibernate based deletes and updates causes the Hibernate session to get out of synch with
			// underlying database. Thus flushing to force Hibernate to synchronize with the underlying database before the delete
			// statement
			this.getSession().flush();

			// Delete from project relationship
			SQLQuery statement =
				this.getSession().createSQLQuery(
					"delete pr " + "from project_relationship pr " + "where pr.subject_project_id = " + datasetId);
			statement.executeUpdate();

			// Delete experiments
			statement =
				this.getSession().createSQLQuery(
					"delete e, pheno, eprop " + "from nd_experiment e, "
						+ "phenotype pheno, nd_experimentprop eprop "
						+ "where e.project_id = " + datasetId
						+ "  and e.nd_experiment_id = pheno.nd_experiment_id "
						+ "  and e.nd_experiment_id = eprop.nd_experiment_id");
			statement.executeUpdate();

			// Delete project stuff
			statement =
				this.getSession().createSQLQuery(
					"delete p, pp " + "from project p, projectprop pp " + "where p.project_id = " + datasetId
						+ "  and p.project_id = pp.project_id");
			statement.executeUpdate();

		} catch (final HibernateException e) {
			final String errorMessage = "Error in delete=" + datasetId + " in DmsProjectDao: " + e.getMessage();
			DmsProjectDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public long countByVariable(final int variableId) {
		try {
			final SQLQuery query = this.getSession().createSQLQuery(DmsProjectDao.COUNT_PROJECTS_WITH_VARIABLE);
			query.setParameter("variableId", variableId);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			final String errorMessage = "Error at countByVariable=" + variableId + " in DmsProjectDao: " + e.getMessage();
			DmsProjectDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public DatasetDTO getDataset(final Integer datasetId) {
		final DatasetDTO datasetDTO;
		try {

			final ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("project.projectId"), "datasetId");
			projectionList.add(Projections.property("dt.datasetTypeId"), "datasetTypeId");
			projectionList.add(Projections.property("project.name"), "name");
			projectionList.add(Projections.property("pr.objectProject.projectId"), "parentDatasetId");
			final Criteria criteria = this.getSession().createCriteria(DmsProject.class, "project");
			criteria.createAlias("project.relatedTos", "pr");
			criteria.createAlias("project.datasetType", "dt");
			criteria.add(Restrictions.eq("project.projectId", datasetId));
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.setProjection(projectionList);
			criteria.setResultTransformer(Transformers.aliasToBean(DatasetDTO.class));
			datasetDTO = (DatasetDTO) criteria.uniqueResult();

		} catch (final HibernateException e) {
			final String errorMessage = "Error getting getDataset for datasetId =" + datasetId + ":" + e.getMessage();
			DmsProjectDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
		return datasetDTO;

	}

	public DatasetDTO getDatasetOfSampleList(final Integer sampleListId) {

		final DatasetDTO datasetDTO;
		try {

			final ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("project.projectId"), "datasetId");
			projectionList.add(Projections.property("dt.datasetTypeId"), "datasetTypeId");
			projectionList.add(Projections.property("project.name"), "name");
			projectionList.add(Projections.property("pr.objectProject.projectId"), "parentDatasetId");

			final Criteria criteria = this.getSession().createCriteria(SampleList.class);
			criteria.createAlias("samples", "sample")
				.createAlias("samples.experiment", "experiment")
				.createAlias("experiment.project", "project")
				.createAlias("project.relatedTos", "pr")
				.createAlias("project.datasetType", "dt")
				.add(Restrictions.eq("id", sampleListId));
			criteria.setProjection(Projections.distinct(projectionList));
			criteria.setResultTransformer(Transformers.aliasToBean(DatasetDTO.class));
			datasetDTO = (DatasetDTO) criteria.uniqueResult();

		} catch (final HibernateException e) {
			final String errorMessage = "Error getting getDatasetOfSampleList for sampleListId =" + sampleListId + ":" + e.getMessage();
			DmsProjectDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}

		return datasetDTO;

	}

	public List<DmsProject> getByStudyAndDatasetType(final int studyId, final int datasetTypeId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(DmsProject.class, "project");
			criteria.createAlias("relatedTos", "pr");
			criteria.createAlias("project.datasetType", "dt");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.objectProject.projectId", studyId));
			criteria.add(Restrictions.eq("dt.datasetTypeId", datasetTypeId));
			return criteria.list();
		} catch (final HibernateException e) {
			final String errorMessage = "Error getting getByStudyAndDatasetType for datasetTypeId =" + datasetTypeId + ":" + e.getMessage();
			DmsProjectDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}
}
