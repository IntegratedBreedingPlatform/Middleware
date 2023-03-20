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
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.brapi.v2.observationlevel.ObservationLevel;
import org.generationcp.middleware.api.study.MyStudiesDTO;
import org.generationcp.middleware.api.study.StudySearchRequest;
import org.generationcp.middleware.api.study.StudySearchResponse;
import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.util.CommonQueryConstants;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.StudySummary;
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
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.service.api.study.ExperimentalDesign;
import org.generationcp.middleware.service.api.study.SeasonDto;
import org.generationcp.middleware.service.api.study.StudyInstanceDto;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.generationcp.middleware.service.api.study.StudySearchFilter;
import org.generationcp.middleware.service.impl.study.StudyInstance;
import org.generationcp.middleware.util.FormulaUtils;
import org.generationcp.middleware.util.SQLQueryBuilder;
import org.generationcp.middleware.util.Util;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * DAO class for {@link DmsProject}.
 *
 * @author Darla Ani, Joyce Avestro
 */
@SuppressWarnings("unchecked")
public class DmsProjectDao extends GenericDAO<DmsProject, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(DmsProjectDao.class);

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
	private static final String OBS_SET_SCALE_ID = "scaleId";
	private static final String OBS_SET_IS_SYSTEM_VARIABLE = "isSystem";

	public static final String STUDY_NAME_BRAPI = "CONCAT(pmain.name, ' Environment Number ', geoloc.description)";

	/**
	 * Type of study is stored in project.study_type_id
	 * Which folder the study is in is defined in project.parent_project_id
	 */
	static final String GET_CHILDREN_OF_FOLDER =
		"SELECT subject.project_id AS project_id, "
			+ "subject.name AS name,  subject.description AS description, "
			+ "	(CASE WHEN subject.study_type_id IS NOT NULL THEN 1 ELSE 0 END) AS is_study, "
			+ "    subject.program_uuid AS program_uuid, "
			+ "    st.study_type_id AS studyType, st.label as label, st.name as studyTypeName, "
			+ "st.visible as visible, st.cvterm_id as cvtermId, subject.locked as isLocked, "
			+ "subject.created_by "
			+ "  FROM project subject "
			+ "  LEFT JOIN study_type st ON subject.study_type_id = st.study_type_id "
			+ " LEFT JOIN project parent ON subject.parent_project_id = parent.project_id "
			+ " WHERE subject.parent_project_id = :folderId "
			+ "   AND parent.study_type_id IS NULL "
			+ "   AND subject.deleted != " + DELETED_STUDY
			+ "   AND (:program_uuid IS NULL OR subject.program_uuid = :program_uuid OR subject.program_uuid IS NULL) "
			+ "   AND (:studyTypeId is null or subject.study_type_id = :studyTypeId or subject.study_type_id is null)"
			// the OR here for value = null is required for folders.
			+ "	ORDER BY name";

	private static final String STUDY_REFERENCE_SQL =
		"SELECT pr.project_id AS project_id, "
			+ "pr.name AS name,  pr.description AS description, pr.program_uuid AS program_uuid, "
			+ "st.study_type_id AS studyType, st.label as label, st.name as studyTypeName, "
			+ "st.visible as visible, st.cvterm_id as cvtermId, pr.locked as isLocked, "
			+ "pr.created_by "
			+ "  FROM project pr "
			+ "  LEFT JOIN study_type st ON pr.study_type_id = st.study_type_id "
			+ " WHERE pr.project_id = :studyId and pr.deleted != " + DELETED_STUDY;

	private static final String GET_ALL_PROGRAM_STUDIES_AND_FOLDERS =
		" SELECT p.project_id FROM project p "
			+ " WHERE study_id IS NULL AND p.project_id != " + DmsProject.SYSTEM_FOLDER_ID
			+ " AND p.deleted != " + DELETED_STUDY
			+ " AND p.program_uuid = :program_uuid ";

	private static final String GET_STUDY_METADATA_BY_GEOLOCATION_ID = " SELECT  "
		+ "     geoloc.nd_geolocation_id AS studyDbId, "
		+ "     pmain.project_id AS trialOrNurseryId, "
		+ "     " + STUDY_NAME_BRAPI + " AS studyName, "
		+ "     study_type.study_type_id AS studyType, "
		+ "     study_type.label AS studyTypeName, "
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
		+ "     AS locationId,"
		+ "		pmain.description AS studyDescription, "
		+ "     pmain.objective AS studyObjective, "
		+ "     (Select definition from cvterm where cvterm_id = (MAX(IF(geoprop.type_id = " + TermId.EXPERIMENT_DESIGN_FACTOR.getId()
		+ ", "
		+ "			geoprop.value, "
		+ "			NULL)))) "
		+ "     AS experimentalDesign,"
		+ "		pmain.study_update AS lastUpdate"
		+ " FROM "
		+ "     nd_geolocation geoloc "
		+ "         INNER JOIN "
		+ "     nd_experiment nde ON nde.nd_geolocation_id = geoloc.nd_geolocation_id "
		+ "         INNER JOIN "
		+ "     project proj ON proj.project_id = nde.project_id "
		+ "         INNER JOIN "
		+ "     project pmain ON pmain.project_id = proj.study_id "
		+ "         INNER JOIN "
		+ "     study_type ON study_type.study_type_id = pmain.study_type_id "
		+ "         LEFT OUTER JOIN "
		+ "     nd_geolocationprop geoprop ON geoprop.nd_geolocation_id = geoloc.nd_geolocation_id "
		+ "         LEFT OUTER JOIN "
		+ "     projectprop pProp ON pmain.project_id = pProp.project_id "
		+ " WHERE "
		+ "     nde.type_id = " + TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()
		+ "         AND geoloc.nd_geolocation_id = :instanceId "
		+ "         AND proj.deleted != " + DELETED_STUDY
		+ " GROUP BY geoloc.nd_geolocation_id ";

	private static final String GET_PROJECTID_BY_STUDYDBID =
		"SELECT DISTINCT p.study_id"
			+ " FROM project p "
			+ " INNER JOIN nd_experiment nde ON nde.project_id = p.project_id"
			+ " WHERE nde.nd_geolocation_id = :studyDbId"
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
		+ "   p.parent_project_id 		 AS folderId, \n"
		+ "   p.program_uuid             AS programUUID, \n"
		+ "	  p.study_update 			 AS studyUpdate, \n"
		+ "	  p.created_by               AS createdBy, \n"
		+ "   p.locked                   AS isLocked "
		+ " FROM \n"
		+ "   project p \n"
		+ "   INNER JOIN study_type stype on stype.study_type_id = p.study_type_id"
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

	private static final List<String> BRAPI_INSTANCES_SORTABLE_FIELDS = Arrays
		.asList("programDbId", "programName", "studyDbId", "studyName", "trialDbId", "trialName", "studyTypeDbId", "studyTypeName",
			"seasonDbId", "season", "startDate", "endDate", "locationDbId", "locationName");

	private static final List<String> BRAPI_STUDIES_SORTABLE_FIELDS = Arrays
		.asList("programDbId", "programName", "trialDbId", "trialName", "startDate", "endDate", "locationDbId");

	public DmsProjectDao(final Session session) {
		super(session);
	}

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
				childrenNodes.add(new StudyReference(id, name, description, projectUUID, studyTypeDto, isLocked, ownerId));
			} else {
				childrenNodes.add(new FolderReference(id, name, description, projectUUID));
			}
		}

		return childrenNodes;
	}

	public List<DatasetReference> getDirectChildDatasetsOfStudy(final Integer studyId) {

		final List<DatasetReference> datasetReferences = new ArrayList<>();

		try {

			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("study.projectId", studyId));
			// Exclude sub-observation datasets of study
			criteria.add(Restrictions.eq("parent.projectId", studyId));

			final ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property(DmsProjectDao.PROJECT_ID));
			projectionList.add(Projections.property("name"));
			projectionList.add(Projections.property("description"));
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
			throw new MiddlewareQueryException("Error with getDirectChildDatasetsOfStudy query from Project: " + e.getMessage(), e);
		}

		return datasetReferences;

	}

	public List<DmsProject> getDatasetsByParent(final Integer parentId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("parent.projectId", parentId));
			return criteria.list();

		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(
				"Error in getDatasetsByParent= " + parentId + " query in DmsProjectDao: " + e.getMessage(), e);
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
			.append(" WHERE p.deleted != " + DELETED_STUDY + " ")
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
					.addScalar("piId").addScalar("siteId").addScalar("createdBy").addScalar("isLocked")
					.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
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

	public Long countMyStudies(final String programUUID, final int userId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass())
				.add(Restrictions.eq("createdBy", String.valueOf(userId)))
				.add(Restrictions.isNotNull("studyType"))
				.add(Restrictions.eq("deleted", false));
			if (!StringUtils.isBlank(programUUID)) {
				criteria.add(Restrictions.eq("programUUID", programUUID));
			}
			criteria.setProjection(Projections.rowCount());
			return (Long) criteria.uniqueResult();
		} catch (final HibernateException e) {
			final String errorMessage = "Error with countMyStudies(programUUID=" + programUUID + ", userId= " + userId + " ): "
				+ e.getMessage();
			LOG.error(errorMessage);
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}

	public List<MyStudiesDTO> getMyStudies(final String programUUID, final Pageable pageable, final int userId) {
		try {
			final Map<Integer, MyStudiesDTO> myStudies = new LinkedHashMap<>();

			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass())
				.createAlias("studyType", "studyType")
				.createAlias("parent", "parent")
				.add(Restrictions.eq("createdBy", String.valueOf(userId)))
				.add(Restrictions.isNotNull("studyType"))
				.add(Restrictions.eq("deleted", false));
			if (!StringUtils.isBlank(programUUID)) {
				criteria.add(Restrictions.eq("programUUID", programUUID));
			}
			addPagination(criteria, pageable);
			addOrder(criteria, pageable);
			final List<DmsProject> projects = criteria.list();

			if (projects.isEmpty()) {
				return Collections.emptyList();
			}

			for (final DmsProject project : projects) {
				final MyStudiesDTO myStudy = new MyStudiesDTO();
				myStudy.setStudyId(project.getProjectId());
				myStudy.setName(project.getName());
				Preconditions.checkNotNull(project.getParent(), "folder is null");
				myStudy.setFolder(project.getParent().getName());
				myStudy.setDate(Util.tryConvertDate(project.getStartDate(), Util.DATE_AS_NUMBER_FORMAT, Util.FRONTEND_DATE_FORMAT));
				myStudy.setType(project.getStudyType().getLabel());
				myStudies.put(project.getProjectId(), myStudy);
			}

			/**
			 * (count(ph.phenotype_id) / count(DISTINCT ph.phenotype_id)
			 *   -> divide by how many times the count is duplicated because of joins
			 */
			final SQLQuery sqlQuery = this.getSession().createSQLQuery("select p.project_id as studyId, " //
				+ "     dataset.name as datasetName, " //
				+ "     concat(gl.description, ' - ', l.lname) as instanceName, " //
				+ "     ifnull( " //
				+ "       floor(count(ph.value) / (count(ph.phenotype_id) / count(DISTINCT ph.phenotype_id))) " //
				+ "         , 0) as confirmedCount, " //
				+ "     ifnull( " //
				+ "       floor(count(ph.draft_value) / (count(ph.phenotype_id) / count(DISTINCT ph.phenotype_id))) " //
				+ "         , 0) as pendingCount, " //
				+ "     ifnull( " //
				+ "       floor(count(distinct nde.nd_experiment_id) * count(distinct pp.variable_id) " //
				+ "           - count(ph.phenotype_id) / (count(ph.phenotype_id) / count(DISTINCT ph.phenotype_id))) " //
				+ "         , 0) as unobservedCount " //
				+ " from project p " //
				+ "          left join project dataset on dataset.study_id = p.project_id " //
				+ "          inner join projectprop pp on dataset.project_id = pp.project_id and pp.type_id in (" //
				+ "               " + VariableType.TRAIT.getId() + ", " //
				+ "               " + VariableType.SELECTION_METHOD.getId() //
				+ "             ) and dataset.dataset_type_id in (" //
				+ "               " + DatasetTypeEnum.PLOT_DATA.getId() + ", " //
				+ "               " + DatasetTypeEnum.PLANT_SUBOBSERVATIONS.getId() + ", " //
				+ "               " + DatasetTypeEnum.QUADRAT_SUBOBSERVATIONS.getId() + ", " //
				+ "               " + DatasetTypeEnum.TIME_SERIES_SUBOBSERVATIONS.getId() + ", " //
				+ "               " + DatasetTypeEnum.CUSTOM_SUBOBSERVATIONS.getId() //
				+ "             ) " //
				+ "          left join nd_experiment nde on nde.project_id = dataset.project_id " //
				+ "          inner join nd_geolocation gl on nde.nd_geolocation_id = gl.nd_geolocation_id " //
				+ "          left join nd_geolocationprop gp on gl.nd_geolocation_id = gp.nd_geolocation_id "  //
				+ "             and gp.type_id = " + TermId.LOCATION_ID.getId() //
				+ "             and gp.nd_geolocation_id = gl.nd_geolocation_id " //
				+ "          left join location l on l.locid = gp.value " //
				+ "          left join phenotype ph on nde.nd_experiment_id = ph.nd_experiment_id " //
				+ "     where p.project_id in (:projectIds) " //
				+ " group by dataset.project_id, nde.nd_geolocation_id");

			sqlQuery.addScalar("studyId")
				.addScalar("datasetName")
				.addScalar("instanceName")
				.addScalar("confirmedCount", new IntegerType())
				.addScalar("pendingCount", new IntegerType())
				.addScalar("unobservedCount", new IntegerType());
			sqlQuery.setParameterList("projectIds", myStudies.keySet());
			sqlQuery.setResultTransformer(Transformers.aliasToBean(MyStudiesDTO.MyStudyMetadata.ObservationMetadata.class));

			final List<MyStudiesDTO.MyStudyMetadata.ObservationMetadata> metadataList = sqlQuery.list();

			for (final MyStudiesDTO.MyStudyMetadata.ObservationMetadata metadata : metadataList) {
				final MyStudiesDTO myStudiesDTO = myStudies.get(metadata.getStudyId());
				myStudiesDTO.getMetadata().getObservations().add(metadata);
			}

			return new ArrayList<>(myStudies.values());
		} catch (final HibernateException e) {
			final String errorMessage = "Error with getMyStudies(programUUID=" + programUUID + ", userId= " + userId + " ): "
				+ e.getMessage();
			LOG.error(errorMessage);
			throw new MiddlewareQueryException(e.getMessage(), e);
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

	public StudyMetadata getStudyMetadataForInstanceId(final Integer instanceId) {
		Preconditions.checkNotNull(instanceId);
		try {
			final SQLQuery query = this.getSession().createSQLQuery(DmsProjectDao.GET_STUDY_METADATA_BY_GEOLOCATION_ID);
			query.addScalar("studyDbId");
			query.addScalar("trialOrNurseryId");
			query.addScalar("studyName");
			query.addScalar("studyType");
			query.addScalar("studyTypeName");
			query.addScalar("seasonId");
			query.addScalar("trialDbId");
			query.addScalar("trialName");
			query.addScalar("startDate");
			query.addScalar("endDate");
			query.addScalar("deleted");
			query.addScalar("locationID");
			query.addScalar("studyDescription");
			query.addScalar("studyObjective");
			query.addScalar("experimentalDesign");
			query.addScalar("lastUpdate");
			query.setParameter("instanceId", instanceId);
			final Object result = query.uniqueResult();
			if (result != null) {
				final Object[] row = (Object[]) result;
				final StudyMetadata studyMetadata = new StudyMetadata();
				studyMetadata.setStudyDbId(instanceId);
				studyMetadata.setNurseryOrTrialId((row[1] instanceof Integer) ? (Integer) row[1] : null);
				studyMetadata.setStudyName((row[2] instanceof String) ? (String) row[2] : null);
				studyMetadata.setStudyType((row[3] instanceof Integer) ? ((Integer) row[3]).toString() : null);
				studyMetadata.setStudyTypeName((row[4] instanceof String) ? (String) row[4] : null);
				if (row[5] instanceof String && !StringUtils.isBlank((String) row[5])) {
					studyMetadata.addSeason(TermId.getById(Integer.parseInt((String) row[5])).toString());
				}
				studyMetadata.setTrialDbId(
					(row[6] instanceof Integer) ? (Integer) row[6] : null);
				studyMetadata.setTrialName((row[7] instanceof String) ? (String) row[7] : null);
				studyMetadata.setStartDate(Util.tryParseDate((String) row[8]));
				studyMetadata.setEndDate(Util.tryParseDate((String) row[9]));
				studyMetadata.setActive(Boolean.FALSE.equals(row[10]));
				studyMetadata.setLocationId((row[11] instanceof String) ? Integer.parseInt((String) row[11]) : null);
				studyMetadata.setStudyDescription((row[12] instanceof String) ? (String) row[12] : null);
				studyMetadata.setStudyObjective((row[13] instanceof String) ? (String) row[13] : null);
				studyMetadata.setExperimentalDesign((row[14] instanceof String) ? (String) row[14] : null);
				studyMetadata.setLastUpdate(Util.tryParseDate((String) row[15]));
				return studyMetadata;
			} else {
				return null;
			}
		} catch (final HibernateException e) {
			final String message = "Error with getStudyMetadataForInstanceId() query from study with instanceId: " + instanceId;
			DmsProjectDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
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
					.addScalar("studyTypeName").addScalar("visible").addScalar("cvtermId").addScalar("isLocked")
					.addScalar("created_by", new IntegerType());
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
					.addScalar("studyTypeName").addScalar("visible").addScalar("cvtermId").addScalar("isLocked").addScalar("created_by");
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
					final String ownerId = (String) row[10];
					studyReference =
						new StudyReference(id, name, description, projectUUID, studyTypeDto, isLocked, Integer.valueOf(ownerId));
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
		return this.getObservationSetVariables(Collections.singletonList(observationSetId), variableTypes);
	}

	public List<MeasurementVariable> getObservationSetVariables(final List<Integer> observationSetIds, final List<Integer> variableTypes) {

		try {
			final String query = " SELECT distinct "  //
				+ "   pp.variable_id AS " + OBS_SET_VARIABLE_ID + ", "  //
				+ "   variable.name AS " + OBS_SET_VARIABLE_NAME + ", "  //
				+ "   variable.definition AS " + OBS_SET_DESCRIPTION + ", "  //
				+ "	  variable.is_system as " + OBS_SET_IS_SYSTEM_VARIABLE + ", "  //
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
				+ "   pp.value as " + OBS_SET_VARIABLE_VALUE + ","
				+ "	  scale.cvterm_id as " + OBS_SET_SCALE_ID
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
				+ "   dataset.project_id in (:observationSetIds) " //
				+ "   AND pp.type_id in (:variableTypes) "
				+ " ORDER BY pp.rank ";

			final SQLQuery sqlQuery = this.getSession().createSQLQuery(query);
			sqlQuery.setParameterList("observationSetIds", observationSetIds);
			sqlQuery.setParameterList("variableTypes", variableTypes);
			sqlQuery
				.addScalar(OBS_SET_VARIABLE_ID)
				.addScalar(OBS_SET_VARIABLE_NAME)
				.addScalar(OBS_SET_DESCRIPTION)
				.addScalar(OBS_SET_IS_SYSTEM_VARIABLE, new BooleanType())
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
				.addScalar(OBS_SET_VARIABLE_VALUE)
				.addScalar(OBS_SET_SCALE_ID);

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
					measurementVariable.setIsSystemVariable(Boolean.TRUE.equals(result.get(OBS_SET_IS_SYSTEM_VARIABLE)));
					measurementVariable.setScale((String) result.get(OBS_SET_SCALE));
					measurementVariable.setMethod((String) result.get(OBS_SET_METHOD));
					measurementVariable.setProperty((String) result.get(OBS_SET_PROPERTY));
					measurementVariable.setScaleId((Integer) result.get(OBS_SET_SCALE_ID));
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
						measurementVariable.setPossibleValues(new ArrayList<>());
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
				"Error getting datasets variables for dataset=" + observationSetIds + ": " + e.getMessage(), e);
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

	public List<DatasetDTO> getDatasets(final Integer studyId) {
		final List<DatasetDTO> datasetDTOS;
		try {

			final ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("project.projectId"), "datasetId");
			projectionList.add(Projections.property("dt.datasetTypeId"), "datasetTypeId");
			projectionList.add(Projections.property("project.name"), "name");
			projectionList.add(Projections.property("parent.projectId"), "parentDatasetId");

			final Criteria criteria = this.getSession().createCriteria(DmsProject.class, "project");
			criteria.createAlias("project.datasetType", "dt");
			criteria.createAlias("project.study", "study");
			criteria.add(Restrictions.eq("study.projectId", studyId));
			criteria.setProjection(projectionList);
			criteria.setResultTransformer(Transformers.aliasToBean(DatasetDTO.class));
			datasetDTOS = criteria.list();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error getting getDatasets for studyId=" + studyId + ":" + e.getMessage(), e);
		}
		return datasetDTOS;

	}

	public Map<Integer, Integer> getStudyIdEnvironmentDatasetIdMap(final List<Integer> studyIds) {
		try {
			final Map<Integer, Integer> studyIdEnvironmentDatasetIdMap = new HashMap<>();

			final String sqlString = "SELECT study_id AS studyId, project_id AS projectId FROM project "
				+ " where dataset_type_id = " + DatasetTypeEnum.SUMMARY_DATA.getId()
				+ " AND study_id IN (:studyIds)";
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(sqlString);

			sqlQuery.addScalar("studyId", new IntegerType());
			sqlQuery.addScalar("projectId", new IntegerType());
			sqlQuery.setParameterList("studyIds", studyIds);

			sqlQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			final List<Map<String, Object>> results = sqlQuery.list();
			for (final Map<String, Object> result : results) {
				studyIdEnvironmentDatasetIdMap.put((Integer) result.get("studyId"), (Integer) result.get("projectId"));
			}

			return studyIdEnvironmentDatasetIdMap;

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error getting getStudyIdEnvironmentDatasetIdMap for studyIds=" + studyIds + ":" + e.getMessage(), e);
		}
	}

	public Map<Integer, List<ObservationLevel>> getObservationLevelsMap(final List<Integer> studyIds) {
		try {
			final Map<Integer, List<ObservationLevel>> observationLevelsMap = new HashMap<>();

			final String sqlString = "SELECT study_id AS studyId, dataset_type_id AS datasetTypeId, name AS datasetName FROM project "
				+ " where dataset_type_id != " + DatasetTypeEnum.SUMMARY_DATA.getId()
				+ " AND study_id IN (:studyIds)";
			final SQLQuery sqlQuery = this.getSession().createSQLQuery(sqlString);

			sqlQuery.addScalar("studyId", new IntegerType());
			sqlQuery.addScalar("datasetTypeId", new IntegerType());
			sqlQuery.addScalar("datasetName", new StringType());
			sqlQuery.setParameterList("studyIds", studyIds);

			sqlQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			final List<Map<String, Object>> results = sqlQuery.list();

			for (final Map<String, Object> result : results) {
				final ObservationLevel observationLevel = new ObservationLevel((Integer) result.get("datasetTypeId"), "study");
				final Integer studyId = (Integer) result.get("studyId");

				observationLevelsMap.putIfAbsent(studyId, new ArrayList<>());
				observationLevelsMap.get(studyId).add(observationLevel);
			}

			return observationLevelsMap;

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error getting getObservationLevelsMap for studyIds=" + studyIds + ":" + e.getMessage(), e);
		}
	}

	public List<StudyInstance> getDatasetInstances(final int datasetId) {
		return this.getDatasetInstances(datasetId, Collections.emptyList());
	}

	public List<StudyInstance> getDatasetInstances(final int datasetId, final List<Integer> instanceIds) {

		try {
			final StringBuilder sql = new StringBuilder("select geoloc.nd_geolocation_id as instanceId, ")
				.append(" geoloc.description as instanceNumber, ")
				.append(" max(if(geoprop.type_id = " + TermId.LOCATION_ID.getId() + ", loc.locid, null)) as locationId, ")
				// 8190 = cvterm for LOCATION_ID
				.append(" max(if(geoprop.type_id = " + TermId.LOCATION_ID.getId() + ", loc.lname, null)) as locationName, ")
				.append(" max(if(geoprop.type_id = " + TermId.LOCATION_ID.getId() + ", loc.labbr, null)) as locationAbbreviation, ")
				// 8189 = cvterm for LOCATION_ABBR
				.append(" max(if(geoprop.type_id = " + TermId.LOCATION_ABBR.getId()
					+ ", geoprop.value, null)) as customLocationAbbreviation, ")

				// 8189 = cvterm for CUSTOM_LOCATION_ABBR
				// FIXME rewrite to be valid for the whole instance, not just the datasetId
				// if instance has X/Y coordinates (fieldmap or row/col design)
				.append(CommonQueryConstants.HAS_FIELD_LAYOUT_EXPRESSION + ", ")

				// FIXME rewrite to be valid for the whole instance, not just the datasetId
				// if instance has been georeferenced using the geojson editor
				.append("  max(case when json_props like '%geoCoordinates%' then 1 else 0 end) as hasGeoJSON, ")

				// if instance obs units are associated to transactions
				.append("  case when ")
				.append("    EXISTS(select ne.nd_geolocation_id  ")
				.append("    from ims_experiment_transaction iet  ")
				.append("             inner join nd_experiment ne on ne.nd_experiment_id = iet.nd_experiment_id  ")
				.append("             inner join ims_transaction it on it.trnid = iet.trnid  ")
				.append(
					"    where it.trnstat <> 9 and ne.nd_geolocation_id = geoloc.nd_geolocation_id) then 1 else 0 end as hasInventory,  ")

				// If study has any plot experiments, hasExperimentalDesign flag = true
				.append("  case when EXISTS(select exp.nd_experiment_id FROM nd_experiment exp WHERE exp.type_id = 1155 ")
				.append("  AND exp.nd_geolocation_id = geoloc.nd_geolocation_id) then 1 else 0 end as hasExperimentalDesign, ")

				// If study samples
				.append("  case when EXISTS(select s.sample_id ")
				.append("               from sample s ")
				.append(
					"                        inner join nd_experiment exp on exp.nd_experiment_id = s.nd_experiment_id and exp.type_id = 1155 ")
				.append("               where exp.nd_geolocation_id = geoloc.nd_geolocation_id) ")
				// or has sub-observations or
				.append("        or EXISTS(select exp.nd_experiment_id ")
				.append("            from nd_experiment exp ")
				.append("                     INNER JOIN project pr ON pr.project_id = exp.project_id AND exp.type_id = 1155 ")
				.append(
					"                     INNER JOIN dataset_type dt on dt.dataset_type_id = pr.dataset_type_id and is_subobs_type = 1 ")
				.append("            where exp.nd_geolocation_id = geoloc.nd_geolocation_id) ")
				// or inventory
				.append("        or EXISTS(select ne.nd_geolocation_id  ")
				.append("             from ims_experiment_transaction iet  ")
				.append("             inner join nd_experiment ne on ne.nd_experiment_id = iet.nd_experiment_id  ")
				.append("             inner join ims_transaction it on it.trnid = iet.trnid  ")
				.append("             where it.trnstat <> 9 and ne.nd_geolocation_id = geoloc.nd_geolocation_id) ")
				// or has files
				.append("        or EXISTS(select f.file_id ")
				.append("            from file_metadata f ")
				.append("              inner join nd_experiment exp on f.nd_experiment_id = exp.nd_experiment_id ")
				.append("              inner join project pr on pr.project_id = exp.project_id ")
				.append("                         and exp.type_id = " + TermId.PLOT_EXPERIMENT.getId())
				.append("            where exp.nd_geolocation_id = geoloc.nd_geolocation_id ) ")
				// then canBeDeleted = false
				.append("             then 0 ")
				.append("         else 1 end as canBeDeleted, ")

				// if study has any pending or accepted plot observations, hasMeasurements = true
				.append("  case when EXISTS(select ph.phenotype_id from phenotype ph ")
				.append("  inner join nd_experiment exp on exp.nd_experiment_id = ph.nd_experiment_id and exp.type_id = 1155 ")
				.append("  where exp.nd_geolocation_id = geoloc.nd_geolocation_id and ")
				.append(
					"  (ph.value is not null or ph.cvalue_id is not null or draft_value is not null or draft_cvalue_id is not null)) then 1 else 0 end as hasMeasurements ")

				.append(" from nd_geolocation geoloc ")
				.append(" inner join nd_experiment nde on nde.nd_geolocation_id = geoloc.nd_geolocation_id ")
				.append(" inner join project proj on proj.project_id = nde.project_id ")
				.append(" left outer join nd_geolocationprop geoprop on geoprop.nd_geolocation_id = geoloc.nd_geolocation_id ")
				.append(" left outer join location loc on geoprop.value = loc.locid and geoprop.type_id = " + TermId.LOCATION_ID.getId())
				.append(" ")
				.append(" left join nd_experimentprop ndep on nde.nd_experiment_id = ndep.nd_experiment_id ")
				.append(" where proj.project_id = :datasetId ");

			if (!CollectionUtils.isEmpty(instanceIds)) {
				sql.append(" AND geoloc.nd_geolocation_id IN (:instanceIds) ");
			}
			sql.append("    group by geoloc.nd_geolocation_id ");
			sql.append("    order by (1 * geoloc.description) asc ");

			final SQLQuery query = this.getSession().createSQLQuery(sql.toString());
			query.setParameter("datasetId", datasetId);
			if (!CollectionUtils.isEmpty(instanceIds)) {
				query.setParameterList("instanceIds", instanceIds);
			}
			query.addScalar("instanceId", new IntegerType());
			query.addScalar("locationId", new IntegerType());
			query.addScalar("locationName", new StringType());
			query.addScalar("locationAbbreviation", new StringType());
			query.addScalar("customLocationAbbreviation", new StringType());
			query.addScalar("hasGeoJSON", new BooleanType());
			query.addScalar("hasFieldLayout", new BooleanType());
			query.addScalar("hasInventory", new BooleanType());
			query.addScalar("instanceNumber", new IntegerType());
			query.addScalar("hasExperimentalDesign", new BooleanType());
			query.addScalar("canBeDeleted", new BooleanType());
			query.addScalar("hasMeasurements", new BooleanType());
			query.setResultTransformer(Transformers.aliasToBean(StudyInstance.class));
			return query.list();
		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
				"Unexpected error in executing getDatasetInstances(datasetId = " + datasetId + ") query: " + he.getMessage(), he);
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

	public long countByVariableIdAndValue(final Integer variableId, final String value) {
		return (long) this.getSession().createCriteria(this.getPersistentClass())
			.createAlias("properties", "property")
			.add(Restrictions.eq("property.variableId", variableId))
			.add(Restrictions.eq("property.value", value))
			.setProjection(Projections.rowCount())
			.uniqueResult();
	}

	public DatasetDTO getDataset(final Integer datasetId) {
		final DatasetDTO datasetDTO;
		try {

			final ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("project.projectId"), "datasetId");
			projectionList.add(Projections.property("dt.datasetTypeId"), "datasetTypeId");
			projectionList.add(Projections.property("project.name"), "name");
			projectionList.add(Projections.property("parent.projectId"), "parentDatasetId");
			final Criteria criteria = this.getSession().createCriteria(DmsProject.class, "project");
			criteria.createAlias("project.parent", "parent");
			criteria.createAlias("project.datasetType", "dt");
			criteria.add(Restrictions.eq("project.projectId", datasetId));
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

	public DatasetDTO getDatasetByObsUnitDbId(final String observationUnitDbId) {
		return (DatasetDTO) this.getSession().createQuery("select p.projectId as datasetId, "
				+ " p.datasetType.datasetTypeId as datasetTypeId,"
				+ " p.name as name,"
				+ " p.parent.projectId as parentDatasetId"
				+ " from DmsProject p "
				+ "where exists ("
				+ "   select 1 from ExperimentModel e "
				+ "   where e.project.projectId = p.projectId"
				+ "   and e.obsUnitId = :observationUnitDbId)")
			.setParameter("observationUnitDbId", observationUnitDbId)
			.setResultTransformer(Transformers.aliasToBean(DatasetDTO.class))
			.uniqueResult();
	}

	public DatasetDTO getDatasetOfSampleList(final Integer sampleListId) {

		final DatasetDTO datasetDTO;
		try {

			final ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("project.projectId"), "datasetId");
			projectionList.add(Projections.property("dt.datasetTypeId"), "datasetTypeId");
			projectionList.add(Projections.property("project.name"), "name");
			projectionList.add(Projections.property("parent.projectId"), "parentDatasetId");

			final Criteria criteria = this.getSession().createCriteria(SampleList.class);
			criteria.createAlias("samples", "sample")
				.createAlias("samples.experiment", "experiment")
				.createAlias("experiment.project", "project")
				.createAlias("project.parent", "parent")
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

	public List<DmsProject> getDatasetsByTypeForStudy(final List<Integer> studyIds, final int datasetTypeId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(DmsProject.class, "project");
			criteria.createAlias("project.study", "study");
			criteria.createAlias("project.datasetType", "dt");
			criteria.add(Restrictions.in("study.projectId", studyIds));
			criteria.add(Restrictions.eq("dt.datasetTypeId", datasetTypeId));
			return criteria.list();
		} catch (final HibernateException e) {
			final String errorMessage =
				"Error getting getDatasetsByTypeForStudy for datasetTypeId =" + datasetTypeId + ":" + e.getMessage();
			DmsProjectDao.LOG.error(errorMessage, e);
			throw new MiddlewareQueryException(errorMessage, e);
		}
	}

	public List<DmsProject> getDatasetsByTypeForStudy(final int studyId, final int datasetTypeId) {
		return this.getDatasetsByTypeForStudy(Collections.singletonList(studyId), datasetTypeId);
	}

	public List<Integer> getPersonIdsAssociatedToEnvironment(final Integer instanceId) {
		Preconditions.checkNotNull(instanceId);
		try {
			final Query query =
				this.getSession().createSQLQuery("SELECT DISTINCT pp.value AS personId \n"
					+ "FROM   cvterm scale \n"
					+ "       INNER JOIN cvterm_relationship r \n"
					+ "               ON ( r.object_id = scale.cvterm_id ) \n"
					+ "       INNER JOIN cvterm variable \n"
					+ "               ON ( r.subject_id = variable.cvterm_id ) \n"
					+ "       INNER JOIN nd_geolocationprop pp \n"
					+ "               ON ( pp.type_id = variable.cvterm_id ) \n"
					+ "WHERE  pp.nd_geolocation_id = :instanceId \n"
					+ "       AND r.object_id = 1901; ").addScalar("personId", new IntegerType());
			query.setParameter("instanceId", instanceId);
			return query.list();
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with getPersonIdsAssociatedToEnvironment() query from instanceId: " + instanceId;
			DmsProjectDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public long countStudyInstances(final StudySearchFilter studySearchFilter) {
		final SQLQuery sqlQuery =
			this.getSession().createSQLQuery(this.createCountStudyInstanceQueryString(studySearchFilter));
		this.addStudySearchFilterParameters(sqlQuery, studySearchFilter);
		return ((BigInteger) sqlQuery.uniqueResult()).longValue();
	}

	public long countStudies(final StudySearchFilter studySearchFilter) {
		final SQLQuery sqlQuery =
			this.getSession().createSQLQuery(this.createCountStudyQueryString(studySearchFilter));
		this.addStudySearchFilterParameters(sqlQuery, studySearchFilter);
		return ((BigInteger) sqlQuery.uniqueResult()).longValue();
	}

	public List<StudyInstanceDto> getStudyInstances(final StudySearchFilter studySearchFilter, final Pageable pageable) {

		final SQLQuery sqlQuery =
			this.getSession().createSQLQuery(this.createStudyInstanceQueryString(studySearchFilter, pageable));

		sqlQuery.addScalar("studyDbId");
		sqlQuery.addScalar("studyName");
		sqlQuery.addScalar("trialDbId");
		sqlQuery.addScalar("trialName");
		sqlQuery.addScalar("studyTypeDbId");
		sqlQuery.addScalar("studyTypeName");
		sqlQuery.addScalar("seasonDbId");
		sqlQuery.addScalar("season");
		sqlQuery.addScalar("startDate");
		sqlQuery.addScalar("endDate");
		sqlQuery.addScalar("active", new IntegerType());
		sqlQuery.addScalar("locationDbId");
		sqlQuery.addScalar("locationName");
		sqlQuery.addScalar("programDbId");
		sqlQuery.addScalar("programName");
		sqlQuery.addScalar("experimentalDesign");
		sqlQuery.addScalar("experimentalDesignId");
		sqlQuery.addScalar("lastUpdate");
		sqlQuery.addScalar("studyDescription");
		sqlQuery.addScalar("studyPUI");
		sqlQuery.addScalar("studyObjective");

		this.addStudySearchFilterParameters(sqlQuery, studySearchFilter);

		addPaginationToSQLQuery(sqlQuery, pageable);

		sqlQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		final List<Map<String, Object>> results = sqlQuery.list();

		final List<StudyInstanceDto> studyInstanceDtoList = new ArrayList<>();
		for (final Map<String, Object> result : results) {
			final StudyInstanceDto studyInstanceDto = new StudyInstanceDto();
			studyInstanceDto.setCommonCropName(studySearchFilter.getCommonCropName());
			studyInstanceDto.setStudyDbId(String.valueOf(result.get("studyDbId")));
			studyInstanceDto.setStudyName(String.valueOf(result.get("studyName")));
			studyInstanceDto.setTrialDbId(String.valueOf(result.get("trialDbId")));
			studyInstanceDto.setTrialName(String.valueOf(result.get("trialName")));
			studyInstanceDto.setStudyTypeDbId(String.valueOf(result.get("studyTypeDbId")));
			studyInstanceDto.setStudyTypeName(String.valueOf(result.get("studyTypeName")));
			studyInstanceDto.setStartDate(Util.tryParseDate((String) result.get("startDate")));
			studyInstanceDto.setEndDate(Util.tryParseDate((String) result.get("endDate")));
			studyInstanceDto.setLocationDbId(String.valueOf(result.get("locationDbId")));
			studyInstanceDto.setLocationName(String.valueOf(result.get("locationName")));
			studyInstanceDto.setProgramDbId(String.valueOf(result.get("programDbId")));
			studyInstanceDto.setProgramName(String.valueOf(result.get("programName")));

			if (result.get("experimentalDesignId") != null) {
				studyInstanceDto.setExperimentalDesign(new ExperimentalDesign(
					String.valueOf(result.get("experimentalDesignId")), String.valueOf(result.get("experimentalDesign"))));
			}

			final Map<String, String> lastUpdate = new HashMap<>();
			lastUpdate.put("timeStamp", String.valueOf(result.get("lastUpdate")));
			lastUpdate.put("version", "1.0");
			studyInstanceDto.setLastUpdate(lastUpdate);

			studyInstanceDto.setStudyDescription(String.valueOf(result.get("studyDescription")));
			studyInstanceDto.setStudyPUI(String.valueOf(result.get("studyPUI")));

			final String seasonDbId = (String) result.get("seasonDbId");
			studyInstanceDto.setSeasons(!StringUtils.isEmpty(seasonDbId) ?
				Collections.singletonList(new SeasonDto(String.valueOf(result.get("season")), seasonDbId)) : Collections.emptyList());

			studyInstanceDto
				.setActive(((Integer) result.get("active")) == 1 ? Boolean.TRUE.toString() : Boolean.FALSE.toString());

			final Map<String, String> properties = new HashMap<>();
			properties.put("studyObjective", result.get("studyObjective") == null ? "" : String.valueOf(result.get("studyObjective")));
			studyInstanceDto.setAdditionalInfo(properties);

			studyInstanceDtoList.add(studyInstanceDto);
		}
		return studyInstanceDtoList;
	}

	public List<StudySummary> getStudies(final StudySearchFilter studySearchFilter, final Pageable pageable) {
		final SQLQuery sqlQuery =
			this.getSession().createSQLQuery(this.createStudyQueryString(studySearchFilter, pageable));

		sqlQuery.addScalar("trialDbId");
		sqlQuery.addScalar("trialName");
		sqlQuery.addScalar("trialDescription");
		sqlQuery.addScalar("trialPUI");
		sqlQuery.addScalar("startDate");
		sqlQuery.addScalar("endDate");
		sqlQuery.addScalar("active", new IntegerType());
		sqlQuery.addScalar("programDbId");
		sqlQuery.addScalar("programName");
		sqlQuery.addScalar("locationDbId");

		this.addStudySearchFilterParameters(sqlQuery, studySearchFilter);

		addPaginationToSQLQuery(sqlQuery, pageable);

		sqlQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
		final List<Map<String, Object>> results = sqlQuery.list();

		final List<StudySummary> studyList = new ArrayList<>();
		for (final Map<String, Object> result : results) {
			final StudySummary studySummary = new StudySummary();
			studySummary.setTrialDbId((Integer) result.get("trialDbId"));
			studySummary.setName(String.valueOf(result.get("trialName")));
			studySummary.setDescription(String.valueOf(result.get("trialDescription")));
			studySummary.setObservationUnitId(String.valueOf(result.get("trialPUI")));
			studySummary.setStartDate(Util.tryParseDate((String) result.get("startDate")));
			studySummary.setEndDate(Util.tryParseDate((String) result.get("endDate")));
			studySummary.setProgramDbId(String.valueOf(result.get("programDbId")));
			studySummary.setProgramName(String.valueOf(result.get("programName")));
			studySummary.setLocationId(String.valueOf(result.get("locationDbId")));
			studySummary.setActive(((Integer) result.get("active")) == 1);
			studyList.add(studySummary);
		}
		return studyList;
	}

	private void addStudySearchFilterParameters(final SQLQuery sqlQuery, final StudySearchFilter studySearchFilter) {

		if (!CollectionUtils.isEmpty(studySearchFilter.getStudyDbIds())) {
			sqlQuery.setParameterList("studyDbIds", studySearchFilter.getStudyDbIds());
		}
		if (!StringUtils.isEmpty(studySearchFilter.getLocationDbId())) {
			sqlQuery.setParameter("locationDbId", studySearchFilter.getLocationDbId());
		}
		if (!StringUtils.isEmpty(studySearchFilter.getProgramDbId())) {
			sqlQuery.setParameter("programDbId", studySearchFilter.getProgramDbId());
		}
		if (!StringUtils.isEmpty(studySearchFilter.getSeasonDbId())) {
			sqlQuery.setParameter("seasonDbId", studySearchFilter.getSeasonDbId());
		}
		if (!StringUtils.isEmpty(studySearchFilter.getStudyTypeDbId())) {
			sqlQuery.setParameter("studyTypeDbId", studySearchFilter.getStudyTypeDbId());
		}
		if (!CollectionUtils.isEmpty(studySearchFilter.getTrialDbIds())) {
			sqlQuery.setParameterList("trialDbIds", studySearchFilter.getTrialDbIds());
		}
		if (!StringUtils.isEmpty(studySearchFilter.getTrialName())) {
			sqlQuery.setParameter("trialName", studySearchFilter.getTrialName());
		}
		if (!StringUtils.isEmpty(studySearchFilter.getTrialPUI())) {
			sqlQuery.setParameter("trialPUI", studySearchFilter.getTrialPUI());
		}
		if (!StringUtils.isEmpty(studySearchFilter.getStudyPUI())) {
			sqlQuery.setParameter("studyPUI", studySearchFilter.getStudyPUI());
		}
		if (studySearchFilter.getGermplasmDbId() != null) {
			sqlQuery.setParameter("germplasmDbId", studySearchFilter.getGermplasmDbId());
		}
		if (studySearchFilter.getObservationVariableDbId() != null) {
			sqlQuery.setParameter("observationVariableDbId", studySearchFilter.getObservationVariableDbId());
		}
		// Search Date Range
		if (studySearchFilter.getSearchDateRangeStart() != null) {
			sqlQuery.setParameter(
				"searchTrialDateStart",
				Util.formatDateAsStringValue(studySearchFilter.getSearchDateRangeStart(), Util.DATE_AS_NUMBER_FORMAT));

		} else if (studySearchFilter.getSearchDateRangeEnd() != null) {
			sqlQuery.setParameter(
				"searchTrialDateEnd",
				Util.formatDateAsStringValue(studySearchFilter.getSearchDateRangeEnd(), Util.DATE_AS_NUMBER_FORMAT));
		}

		if (!StringUtils.isEmpty(studySearchFilter.getExternalReferenceSource())) {
			sqlQuery.setParameter("referenceSource", studySearchFilter.getExternalReferenceSource());
		}
		if (!StringUtils.isEmpty(studySearchFilter.getExternalReferenceID())) {
			sqlQuery.setParameter("referenceId", studySearchFilter.getExternalReferenceID());
		}

	}

	private String createCountStudyInstanceQueryString(final StudySearchFilter studySearchFilter) {
		final StringBuilder sql = new StringBuilder(" SELECT COUNT(DISTINCT geoloc.nd_geolocation_id) ");
		this.appendStudyInstanceFromQuery(sql);
		this.appendStudySearchFilter(sql, studySearchFilter);
		return sql.toString();
	}

	private String createStudyInstanceQueryString(final StudySearchFilter studySearchFilter, final Pageable pageable) {
		final StringBuilder sql = new StringBuilder(" SELECT  ");
		sql.append("     geoloc.nd_geolocation_id AS studyDbId, ");
		sql.append("     " + STUDY_NAME_BRAPI + " AS studyName, ");
		sql.append("     pmain.study_type_id AS studyTypeDbId, ");
		sql.append("     studyType.label AS studyTypeName, ");
		sql.append("     geopropSeason.value AS seasonDbId, ");
		sql.append("     cvtermSeason.definition AS season, ");
		sql.append("     pmain.project_id AS trialDbId, ");
		sql.append(" 	 pmain.name AS trialName, ");
		sql.append("     MAX(pmain.start_date) AS startDate, ");
		sql.append("     MAX(pmain.end_date) AS endDate, ");
		sql.append(
			"     CASE WHEN pmain.end_date IS NOT NULL AND LENGTH(pmain.end_date) > 0 AND CONVERT(pmain.end_date, UNSIGNED) < CONVERT(date_format(now(), '%Y%m%d'), UNSIGNED) "
				+ "THEN 0 ELSE 1 END AS active, ");
		sql.append("     location.locid AS locationDbId, ");
		sql.append("     location.lname AS locationName, ");
		sql.append("     wp.project_name AS programName, ");
		sql.append("     wp.project_uuid AS programDbId, ");
		sql.append("     cvtermExptDesign.definition AS experimentalDesign, ");
		sql.append("     geopropExperimentalDesign.value AS experimentalDesignId, ");
		sql.append("     pmain.study_update AS lastUpdate, ");
		sql.append("     pmain.description AS studyDescription, ");
		sql.append("     nde.obs_unit_id AS studyPUI, ");
		sql.append("     pmain.objective AS studyObjective ");
		this.appendStudyInstanceFromQuery(sql);
		this.appendStudySearchFilter(sql, studySearchFilter);
		sql.append(" GROUP BY geoloc.nd_geolocation_id ");

		addPageRequestOrderBy(sql, pageable, DmsProjectDao.BRAPI_INSTANCES_SORTABLE_FIELDS);

		return sql.toString();
	}

	private String createCountStudyQueryString(final StudySearchFilter studySearchFilter) {
		final StringBuilder sql = new StringBuilder(" SELECT COUNT(DISTINCT pmain.project_id) ");
		this.appendStudySummaryFromQuery(sql);
		this.appendStudySearchFilter(sql, studySearchFilter);
		return sql.toString();
	}

	private String createStudyQueryString(final StudySearchFilter studySearchFilter, final Pageable pageable) {
		final StringBuilder sql = new StringBuilder(" SELECT  ");
		sql.append("     pmain.project_id AS trialDbId, ");
		sql.append(" 	 pmain.name AS trialName, ");
		sql.append(" 	 pmain.description AS trialDescription, ");
		sql.append(" 	 study_exp.obs_unit_id AS trialPUI, ");
		sql.append("     pmain.start_date AS startDate, ");
		sql.append("     pmain.end_date AS endDate, ");
		sql.append(
			"     CASE WHEN pmain.end_date IS NOT NULL AND LENGTH(pmain.end_date) > 0 AND CONVERT(pmain.end_date, UNSIGNED) < CONVERT(date_format(now(), '%Y%m%d'), UNSIGNED) "
				+ "THEN 0 ELSE 1 END AS active, ");
		sql.append("     wp.project_name AS programName, ");
		sql.append("     pmain.program_uuid AS programDbId, ");
		// locationDbId is not unique to study but can have different value per environment.
		// Get the MIN or MAX depending on sort parameter and direction
		if (pageable != null && pageable.getSort() != null && pageable.getSort().getOrderFor("locationDbId") != null
			&& Sort.Direction.DESC.equals(pageable.getSort().getOrderFor("locationDbId").getDirection())) {
			sql.append("     MAX(geopropLocation.value) as locationDbId ");
		} else {
			sql.append("     MIN(geopropLocation.value) as locationDbId ");
		}

		this.appendStudySummaryFromQuery(sql);
		this.appendStudySearchFilter(sql, studySearchFilter);
		sql.append(" GROUP BY pmain.project_id ");

		addPageRequestOrderBy(sql, pageable, DmsProjectDao.BRAPI_STUDIES_SORTABLE_FIELDS);

		return sql.toString();
	}

	private void appendStudyInstanceFromQuery(final StringBuilder sql) {
		sql.append(" FROM ");
		sql.append("     nd_geolocation geoloc ");
		sql.append("         INNER JOIN ");
		sql.append("     nd_experiment nde ON nde.nd_geolocation_id = geoloc.nd_geolocation_id ");
		sql.append("         INNER JOIN ");
		sql.append("     project proj ON proj.project_id = nde.project_id ");
		sql.append("         INNER JOIN ");
		sql.append("     project pmain ON pmain.project_id = proj.study_id ");
		sql.append("         INNER JOIN ");
		sql.append("     project plot ON plot.study_id = pmain.project_id AND plot.dataset_type_id = " + DatasetTypeEnum.PLOT_DATA.getId());
		sql.append("         LEFT OUTER JOIN ");
		sql.append("     study_type studyType ON studyType.study_type_id = pmain.study_type_id ");
		sql.append("         LEFT OUTER JOIN ");
		sql.append(
			"     nd_geolocationprop geopropSeason ON geopropSeason.nd_geolocation_id = geoloc.nd_geolocation_id AND geopropSeason.type_id = "
				+ TermId.SEASON_VAR.getId());
		sql.append("         LEFT OUTER JOIN ");
		sql.append(
			"     nd_geolocationprop geopropLocation ON geopropLocation.nd_geolocation_id = geoloc.nd_geolocation_id AND geopropLocation.type_id = "
				+ TermId.LOCATION_ID.getId());
		sql.append("         LEFT OUTER JOIN ");
		sql.append("     location ON geopropLocation.value = location.locid");
		sql.append("         LEFT OUTER JOIN ");
		sql.append(
			"     nd_geolocationprop geopropExperimentalDesign ON geopropExperimentalDesign.nd_geolocation_id = geoloc.nd_geolocation_id"
				+ " AND geopropExperimentalDesign.type_id = " + TermId.EXPERIMENT_DESIGN_FACTOR.getId());
		sql.append("         LEFT OUTER JOIN ");
		sql.append("     cvterm cvtermExptDesign ON cvtermExptDesign.cvterm_id = geopropExperimentalDesign.value");
		sql.append("         LEFT OUTER JOIN ");
		sql.append("     workbench.workbench_project wp ON wp.project_uuid = pmain.program_uuid");
		sql.append("         LEFT JOIN workbench.users wu ON wu.userid = pmain.created_by");
		sql.append("         LEFT JOIN workbench.persons wper ON wper.personid = wu.personid");
		sql.append("         LEFT OUTER JOIN ");
		sql.append("     cvterm cvtermSeason ON cvtermSeason.cvterm_id = geopropSeason.value");
		sql.append("         LEFT JOIN external_reference_instance er ON er.nd_geolocation_id = geoloc.nd_geolocation_id ");
		sql.append(" WHERE ");
		sql.append("     nde.type_id = " + TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId() + " ");
		sql.append("     AND pmain.deleted = 0 ");//Exclude Deleted Studies
	}

	private void appendStudySummaryFromQuery(final StringBuilder sql) {
		sql.append(" FROM ");
		sql.append("     nd_geolocation geoloc ");
		sql.append("         INNER JOIN ");
		sql.append("     nd_experiment study_exp ON study_exp.nd_geolocation_id = geoloc.nd_geolocation_id AND study_exp.type_id = "
			+ TermId.STUDY_EXPERIMENT.getId());
		sql.append("         LEFT JOIN ");
		sql.append("     nd_experiment nde ON nde.nd_geolocation_id = geoloc.nd_geolocation_id AND nde.type_id = "
			+ TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId());
		sql.append("         INNER JOIN ");
		sql.append("     project pmain ON pmain.project_id = study_exp.project_id ");
		sql.append("         LEFT OUTER JOIN ");
		sql.append(
			"     nd_geolocationprop geopropLocation ON geopropLocation.nd_geolocation_id = geoloc.nd_geolocation_id AND geopropLocation.type_id = "
				+ TermId.LOCATION_ID.getId());
		sql.append("         LEFT OUTER JOIN workbench.workbench_project wp ON wp.project_uuid = pmain.program_uuid");
		sql.append("         LEFT JOIN external_reference_study er ON er.study_id = pmain.project_id ");
		sql.append(" WHERE pmain.deleted = 0 ");//Exclude Deleted Studies
	}

	private void appendStudySearchFilter(final StringBuilder sql, final StudySearchFilter studySearchFilter) {
		if (!CollectionUtils.isEmpty(studySearchFilter.getStudyDbIds())) {
			sql.append(" AND geoloc.nd_geolocation_id IN (:studyDbIds) ");
		}
		if (!StringUtils.isEmpty(studySearchFilter.getLocationDbId())) {
			sql.append(" AND geopropLocation.value = :locationDbId ");
		}
		if (!StringUtils.isEmpty(studySearchFilter.getProgramDbId())) {
			sql.append(" AND pmain.program_uuid = :programDbId ");
		}
		if (!StringUtils.isEmpty(studySearchFilter.getSeasonDbId())) {
			sql.append(" AND cvtermSeason.cvterm_id = :seasonDbId ");
		}
		if (!StringUtils.isEmpty(studySearchFilter.getStudyTypeDbId())) {
			sql.append(" AND pmain.study_type_id = :studyTypeDbId ");
		}
		if (!CollectionUtils.isEmpty(studySearchFilter.getTrialDbIds())) {
			sql.append(" AND pmain.project_id IN (:trialDbIds) ");
		}
		if (!StringUtils.isEmpty(studySearchFilter.getTrialName())) {
			sql.append(" AND pmain.name = :trialName ");
		}
		if (!StringUtils.isEmpty(studySearchFilter.getTrialPUI())) {
			sql.append(" AND study_exp.obs_unit_id = :trialPUI ");
		}
		if (!StringUtils.isEmpty(studySearchFilter.getStudyPUI())) {
			sql.append(" AND nde.obs_unit_id = :studyPUI ");
		}
		if (studySearchFilter.getGermplasmDbId() != null) {
			sql.append(" AND exists (SELECT 1 from germplsm g "
				+ " INNER JOIN stock s ON s.dbxref_id = g.gid"
				+ " where g.germplsm_uuid = :germplasmDbId AND s.project_id = pmain.project_id) ");
		}
		if (studySearchFilter.getObservationVariableDbId() != null) {
			sql.append(" AND exists (SELECT 1 from projectprop where variable_id = :observationVariableDbId");
			sql.append(" AND project_id = plot.project_id");
			sql.append(" AND type_id in (" + VariableType.TRAIT.getId() + ", " + VariableType.SELECTION_METHOD.getId() + "))");
		}

		if (studySearchFilter.getActive() != null) {
			if (BooleanUtils.isTrue(studySearchFilter.getActive())) {
				sql.append(
					" AND (pmain.end_date IS NULL or LENGTH(pmain.end_date) = 0 OR CONVERT(pmain.end_date, UNSIGNED) >= CONVERT(date_format(now(), '%Y%m%d'), UNSIGNED) ) ");
			} else {
				sql.append(
					" AND pmain.end_date IS NOT NULL AND LENGTH(pmain.end_date) > 0 AND CONVERT(pmain.end_date, UNSIGNED) < CONVERT(date_format(now(), '%Y%m%d'), UNSIGNED) ");
			}

		}
		// Search Date Range
		if (studySearchFilter.getSearchDateRangeStart() != null) {
			sql.append(" AND :searchTrialDateStart <= pmain.end_date");

		} else if (studySearchFilter.getSearchDateRangeEnd() != null) {
			sql.append(" AND :searchTrialDateEnd >= pmain.start_date");
		}

		if (!StringUtils.isEmpty(studySearchFilter.getExternalReferenceID())) {
			sql.append(" AND er.reference_id = :referenceId ");
		}
		if (!StringUtils.isEmpty(studySearchFilter.getExternalReferenceSource())) {
			sql.append(" AND er.reference_source = :referenceSource ");
		}
	}

	public Integer getDatasetIdByEnvironmentIdAndDatasetType(final Integer environmentId, final DatasetTypeEnum datasetType) {
		try {
			final Query query = this.getSession().createSQLQuery("SELECT DISTINCT dataset.project_id"
				+ " FROM project p "
				+ " INNER JOIN nd_experiment nde ON nde.project_id = p.project_id"
				+ " INNER JOIN project dataset ON dataset.study_id = p.study_id"
				+ " INNER JOIN project study ON p.study_id = study.project_id AND study.deleted != " + DELETED_STUDY
				+ " WHERE nde.nd_geolocation_id = :environmentId AND nde.type_id = " + ExperimentType.TRIAL_ENVIRONMENT.getTermId()
				+ " AND dataset.dataset_type_id = :datasetTypeId");
			query.setParameter("environmentId", environmentId);
			query.setParameter("datasetTypeId", datasetType.getId());
			return (Integer) query.uniqueResult();
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}

	public List<Integer> getDatasetTypeIdsOfEnvironment(final Integer environmentId) {
		try {
			final Query query = this.getSession().createSQLQuery("SELECT DISTINCT dataset.dataset_type_id"
				+ " FROM project p "
				+ " INNER JOIN nd_experiment nde ON nde.project_id = p.project_id"
				+ " INNER JOIN project dataset ON dataset.study_id = p.study_id"
				+ " INNER JOIN project study ON p.study_id = study.project_id AND study.deleted != " + DELETED_STUDY
				+ " WHERE nde.nd_geolocation_id = :environmentId AND nde.type_id = " + ExperimentType.TRIAL_ENVIRONMENT.getTermId());
			query.setParameter("environmentId", environmentId);
			return query.list();
		} catch (final HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}

	public boolean isValidDatasetId(final Integer datasetId) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("projectId", datasetId));
		criteria.add(Restrictions.isNotNull("datasetType"));
		criteria.setProjection(Projections.rowCount());
		final Long count = (Long) criteria.uniqueResult();
		return count > 0;
	}

	public boolean allDatasetIdsBelongToStudy(final Integer studyId, final List<Integer> datasetIds) {

		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.in("projectId", datasetIds));
		criteria.add(Restrictions.eq("study.projectId", studyId));
		criteria.setProjection(Projections.rowCount());
		final Long count = (Long) criteria.uniqueResult();
		return count.intValue() == datasetIds.size();
	}

	public void markProjectsAndChildrenAsDeleted(final List<Integer> projectIds) {
		try {
			if (!CollectionUtils.isEmpty(projectIds)) {
				final String timestamp = Util.getCurrentDateAsStringValue("yyyyMMddHHmmssSSS");
				final String sql =
					"UPDATE project p SET p.deleted = 1, p.name = CONCAT(p.name, '#', :timestamp) WHERE "
						+ "p.project_id IN (:projectIds) OR p.study_id IN (:projectIds)";
				final SQLQuery sqlQuery = this.getSession().createSQLQuery(sql);
				sqlQuery.setParameter("timestamp", timestamp);
				sqlQuery.setParameterList("projectIds", projectIds);
				sqlQuery.executeUpdate();
			}
		} catch (final MiddlewareQueryException e) {
			final String message = "Error with markProjectsAndChildrenAsDeleted() query from projectIds: " + projectIds;
			DmsProjectDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<StudySearchResponse> searchStudies(final String programUUID, final StudySearchRequest request,
		final Map<Integer, List<Integer>> categoricalValueReferenceIdsByVariablesIds, final List<Integer> locationIds,
		final List<Integer> userIds, final Pageable pageable) {
		final SQLQueryBuilder queryBuilder =
			StudySearchDAOQuery.getSelectQuery(request, categoricalValueReferenceIdsByVariablesIds, locationIds, userIds, pageable);
		queryBuilder.setParameter("programUUID", programUUID);

		final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
		queryBuilder.addParamsToQuery(query);

		query.addScalar(StudySearchDAOQuery.STUDY_ID_ALIAS);
		query.addScalar(StudySearchDAOQuery.STUDY_NAME_ALIAS);
		query.addScalar(StudySearchDAOQuery.STUDY_DESCRIPTION_ALIAS);
		query.addScalar(StudySearchDAOQuery.STUDY_TYPE_NAME_ALIAS);
		query.addScalar(StudySearchDAOQuery.LOCKED_ALIAS, BooleanType.INSTANCE);
		query.addScalar(StudySearchDAOQuery.STUDY_OWNER_NAME_ALIAS);
		query.addScalar(StudySearchDAOQuery.STUDY_OWNER_ID_ALIAS);
		query.addScalar(StudySearchDAOQuery.START_DATE_ALIAS);
		query.addScalar(StudySearchDAOQuery.END_DATE_ALIAS);
		query.addScalar(StudySearchDAOQuery.UPDATE_DATE_ALIAS);
		query.addScalar(StudySearchDAOQuery.PARENT_FOLDER_NAME_ALIAS);
		query.addScalar(StudySearchDAOQuery.OBJECTIVE_ALIAS);
		query.setResultTransformer(Transformers.aliasToBean(StudySearchResponse.class));

		GenericDAO.addPaginationToSQLQuery(query, pageable);

		return (List<StudySearchResponse>) query.list();
	}

	public long countSearchStudies(final String programUUID, final StudySearchRequest request,
		final Map<Integer, List<Integer>> categoricalValueReferenceIdsByVariablesIds, final List<Integer> locationIds,
		final List<Integer> userIds) {
		final SQLQueryBuilder queryBuilder =
			StudySearchDAOQuery.getCountQuery(request, categoricalValueReferenceIdsByVariablesIds, locationIds, userIds);
		queryBuilder.setParameter("programUUID", programUUID);

		final SQLQuery query = this.getSession().createSQLQuery(queryBuilder.build());
		queryBuilder.addParamsToQuery(query);

		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public Optional<FolderReference> getFolderByParentAndName(final Integer parentId, final String folderName, final String programUUID) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.add(Restrictions.eq("parent.projectId", parentId));
		criteria.add(Restrictions.eq("name", folderName));
		criteria.add(Restrictions.eq("programUUID", programUUID));
		criteria.add(Restrictions.isNull("studyType"));
		criteria.add(Restrictions.eq("deleted", false));

		final ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("projectId"), "id");
		projectionList.add(Projections.property("name"), "name");
		projectionList.add(Projections.property("description"), "description");
		projectionList.add(Projections.property("programUUID"), "programUUID");
		projectionList.add(Projections.property("parent.projectId"), "parentFolderId");
		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(FolderReference.class));

		return Optional.ofNullable((FolderReference) criteria.uniqueResult());
	}

}
