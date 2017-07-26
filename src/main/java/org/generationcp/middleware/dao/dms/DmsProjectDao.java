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
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.CvId;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.workbench.StudyNode;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Season;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.service.api.study.StudyFilters;
import org.generationcp.middleware.service.api.study.StudyMetadata;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DAO class for {@link DmsProject}.
 *
 * @author Darla Ani, Joyce Avestro
 *
 */
@SuppressWarnings("unchecked")
public class DmsProjectDao extends GenericDAO<DmsProject, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(DmsProjectDao.class);

	private static final Integer LOCATION_ID = Integer.valueOf(TermId.LOCATION_ID.getId());

	private static final int DELETED_STUDY = TermId.DELETED_STUDY.getId();

	private static final int STUDY_STATUS = TermId.STUDY_STATUS.getId();

	private static final String PROGRAM_UUID = "program_uuid";

	private static final String VALUE = "value";

	private static final String TYPE_ID = "typeId";

	private static final String PROJECT_ID = "projectId";
	
	private static final int START_DATE = TermId.START_DATE.getId();

	/**
	 * Type of study and whether study is deleted are stored in projectprops table.
	 * Which folder the study is in, is defined in the project_relationship table.
	 */
	static final String GET_CHILDREN_OF_FOLDER =
			"SELECT subject.project_id, subject.name,  subject.description, "
					+ "	(CASE WHEN (pr.type_id = " + TermId.IS_STUDY.getId() + ") THEN 1 ELSE 0 END) AS is_study, "
					+ "    subject.program_uuid, "
					+ "    prop.value "
					+ " FROM project subject "
					+ "	INNER JOIN project_relationship pr on subject.project_id = pr.subject_project_id "
					+ "	LEFT OUTER JOIN projectprop prop ON prop.project_id = subject.project_id AND prop.type_id = " + TermId.STUDY_TYPE.getId()
					+ "    WHERE (pr.type_id = " + TermId.HAS_PARENT_FOLDER.getId() + " or pr.type_id = " + TermId.IS_STUDY.getId() + ")"
					+ "		AND pr.object_project_id = :folderId "
					+ "     AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = " + TermId.STUDY_STATUS.getId() + " AND pp.project_id = subject.project_id AND pp.value = " + TermId.DELETED_STUDY.getId() + ") "
					+ "     AND (subject.program_uuid = :program_uuid OR subject.program_uuid IS NULL) "
					+ "     AND (prop.value in (:studyTypeIds) or prop.value IS NULL)"  // the OR here for value = null is required for folders.
					+ "	ORDER BY name";

	private static final String GET_STUDIES_OF_FOLDER = "SELECT  DISTINCT pr.subject_project_id "
			+ "FROM    project_relationship pr, project p " + "WHERE   pr.type_id = " + TermId.IS_STUDY.getId() + " "
			+ "        AND pr.subject_project_id = p.project_id " + "        AND pr.object_project_id = :folderId "
			+ "		AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = " + TermId.STUDY_STATUS.getId()
			+ "     	AND pp.project_id = p.project_id AND pp.value = " + "         " + TermId.DELETED_STUDY.getId() + ") "
			+ "ORDER BY p.name ";

	private static final String GET_ALL_FOLDERS = "SELECT pr.object_project_id, pr.subject_project_id, p.name, p.description "
			+ " FROM project_relationship pr " + " INNER JOIN project p ON p.project_id = pr.subject_project_id " + " WHERE pr.type_id = "
			+ TermId.HAS_PARENT_FOLDER.getId();

	private static final String GET_ALL_PROGRAM_STUDIES_AND_FOLDERS = "SELECT pr.subject_project_id "
			+ "FROM project_relationship pr, project p " + "WHERE pr.type_id = " + TermId.IS_STUDY.getId() + " "
			+ "AND pr.subject_project_id = p.project_id " + "AND p.program_uuid = :program_uuid "
			+ "AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = " + TermId.STUDY_STATUS.getId() + " "
			+ "AND pp.project_id = p.project_id AND pp.value = " + TermId.DELETED_STUDY.getId() + ") "
			+ "UNION SELECT pr.subject_project_id " + "FROM project_relationship pr, project p " + "WHERE pr.type_id = "
			+ TermId.HAS_PARENT_FOLDER.getId() + " " + "AND pr.subject_project_id = p.project_id " + "AND p.program_uuid = :program_uuid ";

	static final String GET_STUDY_METADATA_BY_ID = " SELECT  "
		+ "     geoloc.nd_geolocation_id AS studyDbId, "
		+ "     pmain.project_id AS trialOrNurseryId, "
		+ "     CASE ppStudy.value "
		+ "         WHEN '10000' THEN pmain.name "
		+ "         WHEN '10010' THEN CONCAT(pmain.name, '-', geoloc.description) "
		+ "         ELSE '' "
		+ "     END AS studyName, "
		+ "     CASE ppStudy.value "
		+ "         WHEN '10000' THEN 'N' "
		+ "         WHEN '10010' THEN 'T' "
		+ "         ELSE '' "
		+ "     END AS studyType, "
		+ "     CASE ppStudy.value "
		+ "         WHEN "
		+ "             '10000' "
		+ "         THEN "
		+ "             MAX(IF(pProp.type_id = " + TermId.SEASON_VAR.getId() + ", "
		+ "                 pProp.value, "
		+ "                 NULL)) "
		+ "         WHEN "
		+ "             '10010' "
		+ "         THEN "
		+ "             MAX(IF(geoprop.type_id = " + TermId.SEASON_VAR.getId() + ", "
		+ "                 geoprop.value, "
		+ "                 NULL)) "
		+ "     END AS seasonId, "
		+ "     CASE ppStudy.value "
		+ "         WHEN '10000' THEN NULL "
		+ "         WHEN '10010' THEN pmain.project_id "
		+ "         ELSE '' "
		+ "     END AS trialDbId, "
		+ "     CASE ppStudy.value "
		+ "         WHEN '10000' THEN NULL "
		+ "         WHEN '10010' THEN pmain.name "
		+ "         ELSE '' "
		+ "     END AS trialName, "
		+ "     MAX(IF(pProp.type_id = " + TermId.START_DATE.getId() + ", "
		+ "         pProp.value, "
		+ "         NULL)) AS startDate, "
		+ "     MAX(IF(pProp.type_id = " + TermId.END_DATE.getId() + ", "
		+ "         pProp.value, "
		+ "         NULL)) AS endDate, "
		+ "     MAX(IF(pProp.type_id = " + TermId.STUDY_STATUS.getId() + ", "
		+ "         pProp.value, "
		+ "         NULL)) AS active, "
		+ "     CASE ppStudy.value "
		+ "         WHEN "
		+ "             '10000' "
		+ "         THEN "
		+ "             MAX(IF(pProp.type_id = " + TermId.LOCATION_ID.getId() + ", "
		+ "                 pProp.value, "
		+ "                 NULL)) "
		+ "         WHEN "
		+ "             '10010' "
		+ "         THEN "
		+ "             MAX(IF(geoprop.type_id = " + TermId.LOCATION_ID.getId() + ", "
		+ "                 geoprop.value, "
		+ "                 NULL)) "
		+ "     END AS locationId "
		+ " FROM "
		+ "     nd_geolocation geoloc "
		+ "         INNER JOIN "
		+ "     nd_experiment nde ON nde.nd_geolocation_id = geoloc.nd_geolocation_id "
		+ "         INNER JOIN "
		+ "     nd_experiment_project ndep ON ndep.nd_experiment_id = nde.nd_experiment_id "
		+ "         INNER JOIN "
		+ "     project proj ON proj.project_id = ndep.project_id "
		+ "         INNER JOIN "
		+ "     project_relationship pr ON proj.project_id = pr.subject_project_id "
		+ "         INNER JOIN "
		+ "     project pmain ON pmain.project_id = pr.object_project_id "
		+ "         AND pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
		+ "         LEFT OUTER JOIN "
		+ "     nd_geolocationprop geoprop ON geoprop.nd_geolocation_id = geoloc.nd_geolocation_id "
		+ "         INNER JOIN "
		+ "     projectprop ppStudy ON pmain.project_id = ppStudy.project_id "
		+ "         AND ppStudy.type_id = " + TermId.STUDY_TYPE.getId()
		+ "         LEFT OUTER JOIN "
		+ "     projectprop pProp ON pmain.project_id = pProp.project_id "
		+ " WHERE "
		+ "     nde.type_id = " + TermId.TRIAL_ENVIRONMENT_EXPERIMENT.getId()
		+ "         AND geoloc.nd_geolocation_id = :studyId "
		+ " GROUP BY geoloc.nd_geolocation_id ";

	static final String GET_PROJECTID_BY_STUDYDBID = "SELECT DISTINCT"
		+ "      pr.object_project_id"
		+ " FROM"
		+ "     project_relationship pr"
		+ "         INNER JOIN"
		+ "     project p ON p.project_id = pr.subject_project_id"
		+ "         INNER JOIN"
		+ "     nd_experiment_project ep ON pr.subject_project_id = ep.project_id"
		+ "         INNER JOIN"
		+ "     nd_experiment nde ON nde.nd_experiment_id = ep.nd_experiment_id"
		+ "         INNER JOIN"
		+ "     nd_geolocation gl ON nde.nd_geolocation_id = gl.nd_geolocation_id"
		+ " WHERE"
		+ "     gl.nd_geolocation_id = :studyDbId"
		+ "     AND pr.type_id = " + TermId.BELONGS_TO_STUDY.getId();

	private static final String STUDY_DETAILS_SQL = " SELECT DISTINCT \n"
		+ "   p.name                     AS name, \n"
		+ "   p.description              AS title, \n"
		+ "   ppObjective.value          AS objective, \n"
		+ "   ppStartDate.value          AS startDate, \n"
		+ "   ppEndDate.value            AS endDate, \n"
		+ "   ppPI.value                 AS piName, \n"
		+ "   gpSiteName.value           AS siteName, \n"
		+ "   p.project_id               AS id, \n"
		+ "   ppPIid.value               AS piId, \n"
		+ "   gpSiteId.value             AS siteId, \n"
		+ "   ppFolder.object_project_id AS folderId, \n"
		+ "   p.program_uuid             AS programUUID \n"
		+ " FROM \n"
		+ "   project p \n"
		+ "   INNER JOIN project_relationship ppFolder ON p.project_id = ppFolder.subject_project_id \n"
		+ "   LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id "
		+ "AND ppObjective.variable_id = " + TermId.STUDY_OBJECTIVE.getId() + " \n"
		+ "   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id AND ppStartDate.variable_id = " + TermId.START_DATE.getId() + " \n"
		+ "   LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id AND ppEndDate.variable_id = " + TermId.END_DATE.getId() + " \n"
		+ "   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id AND ppPI.variable_id = " + TermId.PI_NAME.getId() + " \n"
		+ "   LEFT JOIN projectprop ppPIid ON p.project_id = ppPIid.project_id AND ppPIid.variable_id = " + TermId.PI_ID.getId() + " \n"
		+ "   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id \n"
		+ "   LEFT JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id \n"
		+ "   LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id AND gpSiteName.type_id = " + TermId.TRIAL_LOCATION.getId()+ " \n"
		+ "   LEFT JOIN nd_geolocationprop gpSiteId ON e.nd_geolocation_id = gpSiteId.nd_geolocation_id AND gpSiteId.type_id = " + TermId.LOCATION_ID.getId() + " \n"
		+ " WHERE p.project_id = :studyId \n";

	public List<Reference> getRootFolders(String programUUID, List<StudyType> studyTypes) {
		return getChildrenOfFolder(DmsProject.SYSTEM_FOLDER_ID, programUUID, studyTypes);
	}

	public List<Reference> getChildrenOfFolder(Integer folderId, String programUUID, List<StudyType> studyTypes) {

		List<Reference> childrenNodes = new ArrayList<>();
		
		if (studyTypes == null || studyTypes.isEmpty()) {
			throw new MiddlewareQueryException("Missing required parameter. At least one study type must be specified.");
		}

		try {
			Query query = this.getSession().createSQLQuery(DmsProjectDao.GET_CHILDREN_OF_FOLDER);
			query.setParameter("folderId", folderId);
			query.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			
			List<Integer> stydyTypeIds = new ArrayList<>();
			for (StudyType studyType : studyTypes) {
				stydyTypeIds.add(studyType.getId());
			}
			query.setParameterList("studyTypeIds", stydyTypeIds);

			List<Object[]> list = query.list();

			for (Object[] row : list) {
				// project.id
				Integer id = (Integer) row[0];
				// project.name
				String name = (String) row[1];
				// project.description
				String description = (String) row[2];
				// non-zero if a study, else a folder
				Integer isStudy = ((Integer) row[3]);
				// project.program_uuid
				String projectUUID = (String) row[4];
				
				if (isStudy == 1) {
					String studyTypeRaw = (String) row[5];				
					StudyType studyType = studyTypeRaw != null ? StudyType.getStudyTypeById(Integer.valueOf(studyTypeRaw)) : null;
					childrenNodes.add(new StudyReference(id, name, description, projectUUID, studyType));
				} else {
					childrenNodes.add(new FolderReference(id, name, description, projectUUID));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error retrieving study folder tree, folderId=" + folderId + " programUUID=" + programUUID + ":" + e.getMessage(), e);
		}

		return childrenNodes;

	}

	public List<DatasetReference> getDatasetNodesByStudyId(Integer studyId) throws MiddlewareQueryException {

		List<DatasetReference> datasetReferences = new ArrayList<>();

		try {

			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.objectProject.projectId", studyId));

			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property(DmsProjectDao.PROJECT_ID));
			projectionList.add(Projections.property("name"));
			projectionList.add(Projections.property("description"));
			projectionList.add(Projections.property("pr.objectProject.projectId"));
			criteria.setProjection(projectionList);

			criteria.addOrder(Order.asc("name"));

			List<Object[]> list = criteria.list();

			for (Object[] row : list) {
				Integer id = (Integer) row[0]; // project.id
				String name = (String) row[1]; // project.name
				String description = (String) row[2]; // project.description
				datasetReferences.add(new DatasetReference(id, name, description));
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getDatasetNodesByStudyId query from Project: " + e.getMessage(), e);
		}

		return datasetReferences;

	}

	public List<DmsProject> getStudiesByName(String name) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("name", name));
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.IS_STUDY.getId()));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getStudiesByName=" + name + " query on DmsProjectDao: " + e.getMessage(), e);
		}

		return new ArrayList<>();
	}

	public List<DmsProject> getStudiesByUserIds(Collection<Integer> userIds) throws MiddlewareQueryException {
		List<Object> userIdStrings = new ArrayList<>();
		if (userIds != null && !userIds.isEmpty()) {
			for (Integer userId : userIds) {
				userIdStrings.add(userId.toString());
			}
		}
		return this.getStudiesByStudyProperty(TermId.STUDY_UID.getId(), Restrictions.in("p.value", userIdStrings));
	}

	public List<DmsProject> getStudiesByStartDate(Integer startDate) throws MiddlewareQueryException {
		return this.getStudiesByStudyProperty(TermId.START_DATE.getId(), Restrictions.eq("p.value", startDate.toString()));
	}

	private List<DmsProject> getStudiesByStudyProperty(Integer studyPropertyId, Criterion valueExpression) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("properties", "p");
			criteria.add(Restrictions.eq("p.typeId", studyPropertyId));
			criteria.add(valueExpression);
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.IS_STUDY.getId()));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getStudiesByStudyProperty with " + valueExpression + " for property " + studyPropertyId
					+ " in DmsProjectDao: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public List<DmsProject> getStudiesByIds(Collection<Integer> projectIds) throws MiddlewareQueryException {
		try {
			if (projectIds != null && !projectIds.isEmpty()) {
				Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.in(DmsProjectDao.PROJECT_ID, projectIds));
				criteria.createAlias("relatedTos", "pr");
				criteria.add(Restrictions.eq("pr.typeId", TermId.IS_STUDY.getId()));
				criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error in getStudiesByIds= " + projectIds + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public List<DmsProject> getDatasetsByStudy(Integer studyId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.objectProject.projectId", studyId));
			criteria.setProjection(Projections.property("pr.subjectProject"));
			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getDatasetsByStudy= " + studyId + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public DmsProject getParentStudyByDataset(Integer datasetId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.subjectProject.projectId", datasetId));

			criteria.setProjection(Projections.property("pr.objectProject"));

			return (DmsProject) criteria.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getParentStudyByDataset= " + datasetId + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return null;
	}

	public List<DmsProject> getStudyAndDatasetsById(Integer projectId) throws MiddlewareQueryException {
		Set<DmsProject> projects = new HashSet<>();

		DmsProject project = this.getById(projectId);
		if (project != null) {
			projects.add(project);

			DmsProject parent = this.getParentStudyByDataset(projectId);
			if (parent != null) {
				projects.add(parent);

			} else {
				List<DmsProject> datasets = this.getDatasetsByStudy(projectId);
				if (datasets != null && !datasets.isEmpty()) {
					projects.addAll(datasets);
				}
			}
		}

		return new ArrayList<>(projects);
	}

	public List<DmsProject> getByFactor(Integer factorId) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("properties", "p");
			criteria.add(Restrictions.eq("p.typeId", TermId.STANDARD_VARIABLE.getId()));
			criteria.add(Restrictions.eq("p.value", factorId.toString()));

			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException("Error getByFactor=" + factorId + " at DmsProjectDao: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public List<DmsProject> getByIds(Collection<Integer> projectIds) throws MiddlewareQueryException {
		List<DmsProject> studyNodes = new ArrayList<>();
		try {
			if (projectIds != null && !projectIds.isEmpty()) {
				Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.in(DmsProjectDao.PROJECT_ID, projectIds));
				criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

				return criteria.list();
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error in getByIds= " + projectIds + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return studyNodes;
	}

	public List<DmsProject> getProjectsByFolder(Integer folderId, int start, int numOfRows) throws MiddlewareQueryException {
		List<DmsProject> projects = new ArrayList<>();
		if (folderId == null) {
			return projects;
		}

		try {
			// Get projects by folder
			Query query = this.getSession().createSQLQuery(DmsProjectDao.GET_STUDIES_OF_FOLDER);
			query.setParameter("folderId", folderId);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			List<Integer> projectIds = query.list();
			projects = this.getByIds(projectIds);

		} catch (HibernateException e) {
			this.logAndThrowException("Error with getProjectsByFolder query from Project: " + e.getMessage(), e);
		}

		return projects;
	}

	public long countProjectsByFolder(Integer folderId) throws MiddlewareQueryException {
		long count = 0;
		if (folderId == null) {
			return count;
		}

		try {
			Query query = this.getSession().createSQLQuery(DmsProjectDao.GET_STUDIES_OF_FOLDER);
			query.setParameter("folderId", folderId);
			List<Object[]> list = query.list();
			count = list.size();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in countProjectsByFolder(" + folderId + ") query in DmsProjectDao: " + e.getMessage(), e);
		}

		return count;

	}

	public List<DmsProject> getDataSetsByStudyAndProjectProperty(int studyId, int type, String value) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.objectProject.projectId", studyId));
			criteria.createAlias("properties", "prop");
			criteria.add(Restrictions.eq("prop.typeId", type));
			criteria.add(Restrictions.eq("prop.value", value));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			criteria.addOrder(Order.asc("prop.rank"));

			return criteria.list();

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error in getDataSetsByProjectProperty(" + type + ", " + value + ") query in DmsProjectDao: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public List<StudyReference> getStudiesByTrialEnvironments(List<Integer> environmentIds) throws MiddlewareQueryException {
		List<StudyReference> studies = new ArrayList<>();
		try {
			String sql =
					"SELECT p.project_id, p.name, p.description, count(DISTINCT e.nd_geolocation_id)" + " FROM project p"
							+ " INNER JOIN project_relationship pr ON pr.object_project_id = p.project_id AND pr.type_id = "
							+ TermId.BELONGS_TO_STUDY.getId() + " INNER JOIN nd_experiment_project ep"
							+ " INNER JOIN nd_experiment e ON e.nd_experiment_id = ep.nd_experiment_id"
							+ " INNER JOIN nd_geolocation g on g.nd_geolocation_id = e.nd_geolocation_id"
							+ " WHERE (ep.project_id = p.project_id OR ep.project_id = pr.subject_project_id)"
							+ " AND e.nd_geolocation_id IN (:environmentIds)" + " GROUP BY p.project_id, p.name, p.description";
			Query query = this.getSession().createSQLQuery(sql).setParameterList("environmentIds", environmentIds);
			List<Object[]> result = query.list();
			for (Object[] row : result) {
				studies.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2], ((BigInteger) row[3]).intValue()));
			}

		} catch (HibernateException e) {
			this.logAndThrowException(
					"Error in getStudiesByTrialEnvironments=" + environmentIds + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return studies;
	}

	public Integer getProjectIdByNameAndProgramUUID(String name, String programUUID, TermId relationship) throws MiddlewareQueryException {
		try {
			String sql =
					"SELECT s.project_id FROM project s " + " WHERE name = :name AND program_uuid = :program_uuid"
							+ " AND EXISTS (SELECT 1 FROM project_relationship pr WHERE pr.subject_project_id = s.project_id "
							+ "   AND pr.type_id = " + relationship.getId() + ") "
							+ "	AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = " + TermId.STUDY_STATUS.getId()
							+ "   AND pp.project_id = s.project_id AND pp.value = "
							+ "   (SELECT cvterm_id FROM cvterm WHERE name = 9 AND cv_id = " + CvId.STUDY_STATUS.getId() + ")) "
							+ " LIMIT 1";

			Query query =
					this.getSession().createSQLQuery(sql).setParameter("name", name).setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			return (Integer) query.uniqueResult();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getStudyIdByName=" + name + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return null;
	}

	public List<StudyDetails> getAllStudyDetails(StudyType studyType, String programUUID) throws MiddlewareQueryException {
		return this.getAllStudyDetails(studyType, programUUID, -1, -1);
	}

	public List<StudyDetails> getAllStudyDetails(StudyType studyType, String programUUID, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<StudyDetails> studyDetails = new ArrayList<>();

		StringBuilder sqlString =
				new StringBuilder()
		.append("SELECT DISTINCT p.name AS name, p.description AS title, ppObjective.value AS objective, ppStartDate.value AS startDate, ")
		.append("ppEndDate.value AS endDate, ppPI.value AS piName, gpSiteName.value AS siteName, p.project_id AS id ")
		.append(", ppPIid.value AS piId, gpSiteId.value AS siteId ")
		.append("FROM (Select ip.project_id, ip.name, ip.description, ip.program_uuid FROM project ip ")
		.append("INNER JOIN  projectprop ppNursery ON ip.project_id = ppNursery.project_id ")
		.append("AND ppNursery.type_id = " + TermId.STUDY_TYPE.getId() + " ")
		.append( "AND ppNursery.value = " + studyType.getId() + " ")
		.append("WHERE ip.project_id NOT IN (SELECT project_id FROM projectprop ppDeleted WHERE ") 
		.append("ppDeleted.type_id = " + TermId.STUDY_STATUS.getId() + " ")
		.append("AND ppDeleted.value = " + TermId.DELETED_STUDY.getId() + " )")
		.append("AND ip.program_uuid = :" + DmsProjectDao.PROGRAM_UUID + " ")
		.append("OR ip.program_uuid IS NULL ")
		.append("ORDER BY ip.name ");
				
		if(start > 0 && numOfRows > 0) {
			sqlString.append("LIMIT " + start + "," + numOfRows);
		}

		sqlString.append(" ) p ")
		.append("   LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id ")
		.append("                   AND ppObjective.type_id =  ")
		.append(TermId.STUDY_OBJECTIVE.getId())
		.append(" ")
		.append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
		.append("                   AND ppStartDate.type_id =  ")
		.append(TermId.START_DATE.getId())
		.append(" ")
		.append("   LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id ")
		.append("                   AND ppEndDate.type_id =  ")
		.append(TermId.END_DATE.getId())
		.append(" ")
		.append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
		.append("                   AND ppPI.type_id =  ")
		.append(TermId.PI_NAME.getId())
		.append(" ")
		.append("   LEFT JOIN projectprop ppPIid ON p.project_id = ppPIid.project_id ")
		.append("                   AND ppPIid.type_id =  ")
		.append(TermId.PI_ID.getId())
		.append(" ")
		.append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
		.append("       LEFT JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
		.append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
		.append("           AND gpSiteName.type_id =  ")
		.append(TermId.TRIAL_LOCATION.getId())
		.append(" ")
		.append("       LEFT JOIN nd_geolocationprop gpSiteId ON e.nd_geolocation_id = gpSiteId.nd_geolocation_id ")
		.append("           AND gpSiteId.type_id =  ")
		.append(TermId.LOCATION_ID.getId())
		.append(" ")
		.append("       LEFT JOIN project_relationship pr ON pr.object_project_id = p.project_id and pr.type_id = ")
		.append(TermId.BELONGS_TO_STUDY.getId());

		List<Object[]> list = null;

		try {
			Query query =
					this.getSession().createSQLQuery(sqlString.toString()).addScalar("name").addScalar("title").addScalar("objective")
					.addScalar("startDate").addScalar("endDate").addScalar("piName").addScalar("siteName").addScalar("id")
					.addScalar("piId").addScalar("siteId")
					.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			list = query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in getAllStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
		}

		if (list == null || list.isEmpty()) {
			return studyDetails;
		}

		for (Object[] row : list) {
			String name = (String) row[0];
			String title = (String) row[1];
			String objective = (String) row[2];
			String startDate = (String) row[3];
			String endDate = (String) row[4];
			String piName = (String) row[5];
			String siteName = (String) row[6];
			Integer id = (Integer) row[7];
			String piId = (String) row[8];
			String siteId = (String) row[9];

			StudyDetails study =
					new StudyDetails(id, name, title, objective, startDate, endDate, studyType, piName, siteName, piId, siteId);
			studyDetails.add(study);
		}
		return studyDetails;
	}

	public StudyType getStudyType(int studyId) throws MiddlewareQueryException {
		try {
			SQLQuery query =
					this.getSession().createSQLQuery(
							"SELECT pp.value FROM project p " + " INNER JOIN projectprop pp ON p.project_id = pp.project_id "
									+ " WHERE p.project_id = :projectId AND pp.type_id = :typeId");
			query.setParameter(DmsProjectDao.PROJECT_ID, studyId);
			query.setParameter(DmsProjectDao.TYPE_ID, TermId.STUDY_TYPE.getId());
			Object queryResult = query.uniqueResult();
			if (queryResult != null) {
				return StudyType.getStudyTypeById(Integer.valueOf((String) queryResult));
			}
			return null;
		} catch (HibernateException he) {
			throw new MiddlewareQueryException(String.format("Hibernate error in getting study type for a studyId %s. Cause: %s", studyId,
					he.getCause().getMessage()), he);
		}
	}

	public StudyDetails getStudyDetails(StudyType studyType, int studyId) throws MiddlewareQueryException {
		StudyDetails studyDetails = null;
		try {

			Query query =
					this.getSession().createSQLQuery(STUDY_DETAILS_SQL).addScalar("name").addScalar("title").addScalar("objective")
					.addScalar("startDate").addScalar("endDate").addScalar("piName").addScalar("siteName").addScalar("id")
					.addScalar("piId").addScalar("siteId").addScalar("folderId").addScalar("programUUID");

			query.setParameter("studyId", studyId);

			List<Object[]> list = query.list();

			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					String name = (String) row[0];
					String title = (String) row[1];
					String objective = (String) row[2];
					String startDate = (String) row[3];
					String endDate = (String) row[4];
					String piName = (String) row[5];
					String siteName = (String) row[6];
					Integer id = (Integer) row[7];
					String piId = (String) row[8];
					String siteId = (String) row[9];
					Integer folderId = (Integer) row[10];
					String programUUID = (String) row[11];

					studyDetails =
							new StudyDetails(id, name, title, objective, startDate, endDate, studyType, piName, siteName, piId, siteId);
					studyDetails.setParentFolderId(folderId.longValue());
					studyDetails.setProgramUUID(programUUID);
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getTrialObservationTable() query in DmsProjectDao: " + e.getMessage(), e);
		}
		return studyDetails;
	}

	public long countAllStudyDetails(StudyType studyType, String programUUID) throws MiddlewareQueryException {
		try {
			StringBuilder sqlString =
					new StringBuilder()
			.append("SELECT COUNT(1) ")
			.append("FROM project p ")
			.append("   INNER JOIN projectprop ppNursery ON p.project_id = ppNursery.project_id ")
			.append("                   AND ppNursery.type_id = ")
			.append(TermId.STUDY_TYPE.getId())
			.append(" ")
			.append("                   AND ppNursery.value = ")
			.append(studyType.getId())
			.append(" ")
			.append("   LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id ")
			.append("                   AND ppObjective.type_id =  ")
			.append(TermId.STUDY_OBJECTIVE.getId())
			.append(" ")
			.append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
			.append("                   AND ppStartDate.type_id =  ")
			.append(TermId.START_DATE.getId())
			.append(" ")
			.append("   LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id ")
			.append("                   AND ppEndDate.type_id =  ")
			.append(TermId.END_DATE.getId())
			.append(" ")
			.append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
			.append("                   AND ppPI.type_id =  ")
			.append(TermId.PI_NAME.getId())
			.append(" ")
			.append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
			.append("       LEFT JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
			.append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
			.append("           AND gpSiteName.type_id =  ").append(TermId.TRIAL_LOCATION.getId()).append(" ")
			.append("WHERE p.project_id NOT IN (SELECT project_id FROM projectprop ppDeleted WHERE ppDeleted.type_id =  ")
			.append(TermId.STUDY_STATUS.getId()).append(" ")
			.append("               AND ppDeleted.value =  ")
			.append(TermId.DELETED_STUDY.getId()).append(") ").append("   AND (p.").append(DmsProjectDao.PROGRAM_UUID)
			.append(" = :").append(DmsProjectDao.PROGRAM_UUID).append(" ").append("   OR p.")
			.append(DmsProjectDao.PROGRAM_UUID).append(" IS NULL) ");

			Query query = this.getSession().createSQLQuery(sqlString.toString()).setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in countAllStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
		}
		return 0;

	}

	public List<StudyDetails> getAllNurseryAndTrialStudyDetails(String programUUID) throws MiddlewareQueryException {
		return this.getAllNurseryAndTrialStudyDetails(programUUID, 0, -1);
	}

	public List<StudyDetails> getAllNurseryAndTrialStudyDetails(String programUUID, int start, int numOfRows)
			throws MiddlewareQueryException {
		List<StudyDetails> studyDetails = new ArrayList<>();
		try {

			StringBuilder sqlString =
					new StringBuilder()
			.append("SELECT DISTINCT p.name AS name, p.description AS title, ppObjective.value AS objective, ppStartDate.value AS startDate, ")
			.append("ppEndDate.value AS endDate, ppPI.value AS piName, gpSiteName.value AS siteName, p.project_id AS id, p.studyType AS studyType ")
			.append(", ppPIid.value AS piId, gpSiteId.value AS siteId ")
			.append("FROM (Select ip.project_id, ip.name, ip.description, ip.program_uuid, ppStudy.value AS studyType FROM project ip ")
			.append("INNER JOIN  projectprop ppStudy ON ip.project_id = ppStudy.project_id ")
			.append("AND ppStudy.type_id = " + TermId.STUDY_TYPE.getId() + " ")
			.append( "AND ppStudy.value IN (" + TermId.NURSERY.getId() + "," + TermId.TRIAL.getId() + ")")
			.append("WHERE ip.project_id NOT IN (SELECT project_id FROM projectprop ppDeleted WHERE ") 
			.append("ppDeleted.type_id = " + TermId.STUDY_STATUS.getId() + " ")
			.append("AND ppDeleted.value = " + TermId.DELETED_STUDY.getId() + " )")
			.append("AND ip.program_uuid = :" + DmsProjectDao.PROGRAM_UUID + " ")
			.append("OR ip.program_uuid IS NULL ")
			.append("ORDER BY ip.name ");
					
			if(start > 0 && numOfRows > 0) {
				sqlString.append("LIMIT " + start + "," + numOfRows);
			}			
			
			 sqlString.append(") p  LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id ")
			.append("                   AND ppObjective.type_id =  ")
			.append(TermId.STUDY_OBJECTIVE.getId())
			.append(" ")
			// 8030
			.append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
			.append("                   AND ppStartDate.type_id =  ")
			.append(TermId.START_DATE.getId())
			.append(" ")
			// 8050
			.append("   LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id ")
			.append("                   AND ppEndDate.type_id =  ")
			.append(TermId.END_DATE.getId())
			.append(" ")
			// 8060
			.append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
			.append("                   AND ppPI.type_id =  ")
			.append(TermId.PI_NAME.getId())
			.append(" ")
			// 8100
			.append("   LEFT JOIN projectprop ppPIid ON p.project_id = ppPIid.project_id ")
			.append("                   AND ppPIid.type_id =  ")
			.append(TermId.PI_ID.getId())
			.append(" ")
			.append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
			.append("       INNER JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
			.append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
			.append("           AND gpSiteName.type_id =  ")
			.append(TermId.TRIAL_LOCATION.getId())
			.append(" ")
			// 8180
			.append("       LEFT JOIN nd_geolocationprop gpSiteId ON e.nd_geolocation_id = gpSiteId.nd_geolocation_id ")
			.append("           AND gpSiteId.type_id =  ").append(TermId.LOCATION_ID.getId())
			.append(" ");

			Query query =
					this.getSession().createSQLQuery(sqlString.toString()).addScalar("name").addScalar("title").addScalar("objective")
					.addScalar("startDate").addScalar("endDate").addScalar("piName").addScalar("siteName").addScalar("id")
					.addScalar("studyType").addScalar("piId").addScalar("siteId")
					.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);

			List<Object[]> list = query.list();

			if (list != null && !list.isEmpty()) {
				for (Object[] row : list) {
					String name = (String) row[0];
					String title = (String) row[1];
					String objective = (String) row[2];
					String startDate = (String) row[3];
					String endDate = (String) row[4];
					String piName = (String) row[5];
					String siteName = (String) row[6];
					Integer id = (Integer) row[7];
					String studyTypeId = (String) row[8];
					String piId = (String) row[9];
					String siteId = (String) row[10];

					studyDetails.add(new StudyDetails(id, name, title, objective, startDate, endDate, TermId.NURSERY.getId() == Integer
							.parseInt(studyTypeId) ? StudyType.N : StudyType.T, piName, siteName, piId, siteId));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error in getAllNurseryAndTrialStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
		}
		return studyDetails;

	}

	public long countAllNurseryAndTrialStudyDetails(String programUUID) throws MiddlewareQueryException {
		try {

			StringBuilder sqlString =
					new StringBuilder()
			.append("SELECT COUNT(1) ")
			.append("FROM project p ")
			.append("   INNER JOIN projectprop ppStudy ON p.project_id = ppStudy.project_id ")
			.append("                   AND ppStudy.type_id = ")
			.append(TermId.STUDY_TYPE.getId())
			.append(" ")
			.append("                   AND ppStudy.value in (")
			.append(TermId.NURSERY.getId())
			.append(",")
			.append(TermId.TRIAL.getId())
			.append(") ")
			.append("   LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id ")
			.append("                   AND ppObjective.type_id =  ")
			.append(TermId.STUDY_OBJECTIVE.getId())
			.append(" ")
			// 8030
			.append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
			.append("                   AND ppStartDate.type_id =  ")
			.append(TermId.START_DATE.getId())
			.append(" ")
			// 8050
			.append("   LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id ")
			.append("                   AND ppEndDate.type_id =  ")
			.append(TermId.END_DATE.getId())
			.append(" ")
			// 8060
			.append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
			.append("                   AND ppPI.type_id =  ")
			.append(TermId.PI_NAME.getId())
			.append(" ")
			// 8100
			.append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
			.append("       INNER JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
			.append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
			.append("           AND gpSiteName.type_id =  ")
			.append(TermId.TRIAL_LOCATION.getId())
			.append(" ")
			// 8180
			.append("WHERE NOT EXISTS (SELECT 1 FROM projectprop ppDeleted WHERE ppDeleted.type_id =  ")
			.append(TermId.STUDY_STATUS.getId())
			.append(" ")
			// 8006
			.append("               AND ppDeleted.project_id = p.project_id AND ppDeleted.value =  ")
			.append(TermId.DELETED_STUDY.getId()).append(") ")
			// 12990
			.append("   AND (p.").append(DmsProjectDao.PROGRAM_UUID).append(" = :").append(DmsProjectDao.PROGRAM_UUID)
			.append(" ").append("   OR p.").append(DmsProjectDao.PROGRAM_UUID).append(" IS NULL) ");

			Query query = this.getSession().createSQLQuery(sqlString.toString()).setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (HibernateException e) {
			this.logAndThrowException("Error in countAllNurseryAndTrialStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
		}
		return 0;

	}

	/**
	 * Retrieves all the study details
	 *
	 * @return List of all nursery and trial study nodes
	 * @throws MiddlewareQueryException
	 */

	public List<StudyNode> getAllNurseryAndTrialStudyNodes(String programUUID) throws MiddlewareQueryException {
		List<StudyNode> studyNodes = new ArrayList<>();

		StringBuilder sqlString =
				new StringBuilder().append("SELECT DISTINCT p.project_id AS id ").append("        , p.name AS name ")
				.append("        , p.description AS description ").append("        , ppStartDate.value AS startDate ")
				.append("        , ppStudyType.value AS studyType ").append("        , gpSeason.value AS season ")
				.append("FROM project p  ")
				.append("   INNER JOIN projectprop ppStudyType ON p.project_id = ppStudyType.project_id ")
				.append("                   AND ppStudyType.type_id = ").append(TermId.STUDY_TYPE.getId()).append(" ")
				.append("                   AND (ppStudyType.value = ").append(TermId.NURSERY.getId()).append(" ")
				.append("                   OR ppStudyType.value = ").append(TermId.TRIAL.getId()).append(") ")
				.append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
				.append("                   AND ppStartDate.type_id =  ").append(TermId.START_DATE.getId()).append(" ")
				.append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
				.append("   INNER JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
				.append("   LEFT JOIN nd_geolocationprop gpSeason ON e.nd_geolocation_id = gpSeason.nd_geolocation_id ")
				.append("           AND gpSeason.type_id =  ").append(TermId.SEASON_VAR.getId()).append(" ")
				.append("WHERE NOT EXISTS (SELECT 1 FROM projectprop ppDeleted WHERE ppDeleted.type_id =  ")
				.append(TermId.STUDY_STATUS.getId()).append(" ")
				.append("               AND ppDeleted.project_id = p.project_id AND ppDeleted.value =  ")
				.append(TermId.DELETED_STUDY.getId()).append(") ").append("   AND (p.").append(DmsProjectDao.PROGRAM_UUID)
				.append(" = :").append(DmsProjectDao.PROGRAM_UUID).append(" ").append("   OR p.")
				.append(DmsProjectDao.PROGRAM_UUID).append(" IS NULL) ");
		List<Object[]> list = null;

		try {
			Query query =
					this.getSession().createSQLQuery(sqlString.toString()).addScalar("id").addScalar("name").addScalar("description")
					.addScalar("startDate").addScalar("studyType").addScalar("season")
					.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			list = query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error in getAllStudyNodes() query in DmsProjectDao: " + e.getMessage(), e);
		}

		if (list == null || list.isEmpty()) {
			return studyNodes;
		}

		for (Object[] row : list) {
			Integer id = (Integer) row[0];
			String name = (String) row[1];
			String description = (String) row[2];
			String startDate = (String) row[3];
			String studyTypeStr = (String) row[4];
			String seasonStr = (String) row[5];

			StudyType studyType = StudyType.N;
			if (Integer.parseInt(studyTypeStr) != TermId.NURSERY.getId()) {
				studyType = StudyType.T;
			}

			Season season = Season.getSeason(seasonStr);
			studyNodes.add(new StudyNode(id, name, description, startDate, studyType, season));

		}
		Collections.sort(studyNodes);
		return studyNodes;
	}

	@SuppressWarnings("rawtypes")
	public boolean checkIfProjectNameIsExistingInProgram(String name, String programUUID) throws MiddlewareQueryException {
		try {
			Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("name", name));
			criteria.add(Restrictions.eq("programUUID", programUUID));

			List list = criteria.list();
			if (list != null && !list.isEmpty()) {
				return true;
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error in checkIfProjectNameIsExisting=" + name + " query on DmsProjectDao: " + e.getMessage(), e);
		}

		return false;
	}

	public List<FolderReference> getAllFolders() throws MiddlewareQueryException {
		List<FolderReference> folders = new ArrayList<>();
		try {
			SQLQuery query = this.getSession().createSQLQuery(DmsProjectDao.GET_ALL_FOLDERS);
			List<Object[]> result = query.list();
			if (result != null && !result.isEmpty()) {
				for (Object[] row : result) {
					folders.add(new FolderReference((Integer) row[0], (Integer) row[1], (String) row[2], (String) row[3]));
				}
			}

		} catch (HibernateException e) {
			this.logAndThrowException("Error at getAllFolders, query at DmsProjectDao: " + e.getMessage(), e);
		}
		return folders;
	}

	public List<Integer> getAllProgramStudiesAndFolders(String programUUID) throws MiddlewareQueryException {
		List<Integer> projectIds = null;
		try {
			SQLQuery query = this.getSession().createSQLQuery(DmsProjectDao.GET_ALL_PROGRAM_STUDIES_AND_FOLDERS);
			query.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			projectIds = query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error at getAllProgramStudiesAndFolders, query at DmsProjectDao: " + e.getMessage(), e);
		}
		return projectIds;
	}

	public List<ValueReference> getDistinctProjectNames() throws MiddlewareQueryException {
		List<ValueReference> results = new ArrayList<>();
		try {
			String sql = "SELECT DISTINCT name FROM project ";
			SQLQuery query = this.getSession().createSQLQuery(sql);
			List<String> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (String row : list) {
					results.add(new ValueReference(row, row));
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getDistinctProjectNames() query from Project " + e.getMessage(), e);
		}
		return results;
	}

	public List<ValueReference> getDistinctProjectDescriptions() throws MiddlewareQueryException {
		List<ValueReference> results = new ArrayList<>();
		try {
			String sql = "SELECT DISTINCT description FROM project ";
			SQLQuery query = this.getSession().createSQLQuery(sql);
			List<String> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (String row : list) {
					results.add(new ValueReference(row, row));
				}
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getDistinctProjectDescription() query from Project " + e.getMessage(), e);
		}
		return results;
	}

	public Integer getProjectIdByStudyDbId(int studyDbId) throws MiddlewareQueryException {
		try {
			Query query = this.getSession().createSQLQuery(GET_PROJECTID_BY_STUDYDBID);
			query.setParameter("studyDbId", studyDbId);
			return (Integer) query.uniqueResult();
		} catch (HibernateException e) {
			LOG.error(e.getMessage(), e);
			throw new MiddlewareQueryException(e.getMessage(), e);
		}
	}

	public Integer getProjectIdByNameAndProgramUUID(String name, String programUUID) throws MiddlewareQueryException {
		try {
			String sql = "SELECT project_id FROM project WHERE name = :name AND program_uuid = :program_uuid";
			Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("name", name);
			query.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			List<Integer> list = query.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getDistinctProjectDescription() query from Project " + e.getMessage(), e);
		}
		return null;
	}

	public List<String> getAllSharedProjectNames() throws MiddlewareQueryException {
		List<String> results = new ArrayList<>();
		try {
			String sql = "SELECT name FROM project WHERE program_uuid is null";
			SQLQuery query = this.getSession().createSQLQuery(sql);
			return query.list();
		} catch (HibernateException e) {
			this.logAndThrowException("Error with getAllSharedProjectNames()" + e.getMessage(), e);
		}
		return results;
	}

	public List<DmsProject> findPagedProjects(final Map<StudyFilters, String> filters,
			final Integer pageSize, final Integer pageNumber) {

		final Criteria criteria = buildCoreCriteria(filters, getOrderBy(filters));
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
		final Criteria criteria = buildCoreCriteria(filters, getOrderBy(filters));
		criteria.setProjection(Projections.rowCount());
		return (long) criteria.uniqueResult();
	}

	private Criteria buildCoreCriteria(final Map<StudyFilters, String> parameters, final Order orderBy) {
		final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
		criteria.createAlias("properties", "pr");
		criteria.add(Restrictions.eq("pr.typeId", TermId.STUDY_TYPE.getId()));

		final DetachedCriteria inactive = DetachedCriteria.forClass(ProjectProperty.class);
		inactive.add(Restrictions.eq(DmsProjectDao.TYPE_ID, Integer.valueOf(DmsProjectDao.STUDY_STATUS)));
		inactive.add(Restrictions.eq(DmsProjectDao.VALUE, String.valueOf(DmsProjectDao.DELETED_STUDY)));
		inactive.setProjection(Projections.property("project.projectId"));
		criteria.add(Property.forName(DmsProjectDao.PROJECT_ID).notIn(inactive));

		if (parameters.containsKey(StudyFilters.PROGRAM_ID)) {
			criteria.add(Restrictions.eq(StudyFilters.PROGRAM_ID.getParameter(), parameters.get(StudyFilters.PROGRAM_ID)));
		} else {
			criteria.add(Restrictions.isNotNull(StudyFilters.PROGRAM_ID.getParameter()));
		}

		if (parameters.containsKey(StudyFilters.LOCATION_ID)) {
			final DetachedCriteria ppLocation = DetachedCriteria.forClass(ProjectProperty.class);

			ppLocation.add(Restrictions.eq(DmsProjectDao.TYPE_ID, DmsProjectDao.LOCATION_ID));
			ppLocation.add(Restrictions.eq(DmsProjectDao.VALUE, parameters.get(StudyFilters.LOCATION_ID)));
			ppLocation.setProjection(Projections.property("project.projectId"));

			criteria.add(Property.forName(DmsProjectDao.PROJECT_ID).in(ppLocation));
		}

		criteria.addOrder(orderBy);
		return criteria;
	}

	public StudyMetadata getStudyMetadata(Integer studyId) throws MiddlewareQueryException {
		Preconditions.checkNotNull(studyId);
		try {
			SQLQuery query = this.getSession().createSQLQuery(DmsProjectDao.GET_STUDY_METADATA_BY_ID);
			query.addScalar("studyDbId");
			query.addScalar("trialOrNurseryId");
			query.addScalar("studyName");
			query.addScalar("studyType");
			query.addScalar("seasonId");
			query.addScalar("trialDbId");
			query.addScalar("trialName");
			query.addScalar("startDate");
			query.addScalar("endDate");
			query.addScalar("active");
			query.addScalar("locationID");
			query.setParameter("studyId", studyId);
			Object result = query.uniqueResult();
			if (result != null) {
				Object[] row = (Object[]) result;
				StudyMetadata studyMetadata = new StudyMetadata();
				studyMetadata.setStudyDbId(studyId);
				studyMetadata.setNurseryOrTrialId((row[1] instanceof Integer) ? (Integer) row[1] : null);
				studyMetadata.setStudyName((row[2] instanceof String) ? (String) row[2] : null);
				studyMetadata.setStudyType((row[3] instanceof String) ? (String) row[3] : null);
				if (row[4] instanceof String && !StringUtils.isBlank((String) row[4])) {
					studyMetadata.addSeason(TermId.getById(Integer.parseInt((String) row[4])).toString());
				}
				studyMetadata.setTrialDbId((row[5] instanceof String && StringUtils.isNumeric((String) row[5])) ? Integer.parseInt((String) row[5]) : null);
				studyMetadata.setTrialName((row[6] instanceof String) ? (String) row[6] : null);
				studyMetadata.setStartDate((row[7] instanceof String) ? (String) row[7] : null);
				studyMetadata.setEndDate((row[8] instanceof String) ? (String) row[8] : null);
				studyMetadata.setActive((row[9] != null) ? false : true);
				studyMetadata.setLocationId((row[10] instanceof String) ? Integer.parseInt((String) row[10]) : null);
				return studyMetadata;
			} else {
				return null;
			}
		} catch (HibernateException e) {
			final String message = "Error with getStudyMetadata() query from study: " + studyId;
			DmsProjectDao.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}
}
