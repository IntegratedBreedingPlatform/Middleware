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

	private static final String PROGRAM_UUID = "program_uuid";
	private static final String VALUE = "value";
	private static final String TYPE_ID = "typeId";
	private static final String PROJECT_ID = "projectId";
	private static final String VARIABLE_ID = "variableId";
	public static final String DELETED = "deleted";
	private static final int DELETED_STUDY = 1;

	private static final int START_DATE = TermId.START_DATE.getId();

	/**
	 * Type of study is stored in projectprops table.
	 * Which folder the study is in, is defined in the project_relationship table.
	 */
	static final String GET_CHILDREN_OF_FOLDER =
			"SELECT subject.project_id, subject.name,  subject.description, "
					+ "	(CASE WHEN (pr.type_id = " + TermId.IS_STUDY.getId() + ") THEN 1 ELSE 0 END) AS is_study, "
					+ "    subject.program_uuid, "
					+ "    subject.study_type "
					+ " FROM project subject "
					+ "	INNER JOIN project_relationship pr on subject.project_id = pr.subject_project_id "
					+ "    WHERE (pr.type_id = " + TermId.HAS_PARENT_FOLDER.getId() + " or pr.type_id = " + TermId.IS_STUDY.getId() + ")"
					+ "		AND pr.object_project_id = :folderId "
					+ "     AND NOT EXISTS (SELECT 1 FROM project p WHERE p.project_id = subject.project_id AND p.deleted = " + DELETED_STUDY + ")"
					+ "     AND (subject.program_uuid = :program_uuid OR subject.program_uuid IS NULL) "
					+ "     AND (subject.study_type in (:studyTypeIds) or subject.study_type IS NULL)"  // the OR here for value = null is required for folders.
					+ "	ORDER BY name";

	private static final String GET_STUDIES_OF_FOLDER = "SELECT  DISTINCT pr.subject_project_id "
			+ "FROM    project_relationship pr, project p " + "WHERE   pr.type_id = " + TermId.IS_STUDY.getId() + " "
			+ "        AND pr.subject_project_id = p.project_id " + "        AND pr.object_project_id = :folderId "
			+ "		AND p.deleted != " + DELETED_STUDY + " "
			+ "ORDER BY p.name ";

	private static final String GET_ALL_FOLDERS = "SELECT pr.object_project_id, pr.subject_project_id, p.name, p.description "
			+ " FROM project_relationship pr " + " INNER JOIN project p ON p.project_id = pr.subject_project_id " + " WHERE pr.type_id = "
			+ TermId.HAS_PARENT_FOLDER.getId();

	private static final String GET_ALL_PROGRAM_STUDIES_AND_FOLDERS = "SELECT pr.subject_project_id "
			+ "FROM project_relationship pr, project p " + "WHERE pr.type_id = " + TermId.IS_STUDY.getId() + " "
			+ "AND pr.subject_project_id = p.project_id " + "AND p.program_uuid = :program_uuid "
			+ "AND p.deleted != " + DELETED_STUDY + "  "
			+ "UNION SELECT pr.subject_project_id " + "FROM project_relationship pr, project p " + "WHERE pr.type_id = "
			+ TermId.HAS_PARENT_FOLDER.getId() + " " + "AND pr.subject_project_id = p.project_id " + "AND p.program_uuid = :program_uuid ";

	static final String GET_STUDY_METADATA_BY_ID = " SELECT  "
		+ "     geoloc.nd_geolocation_id AS studyDbId, "
		+ "     pmain.project_id AS trialOrNurseryId, "
		+ "     CASE pmain.study_type "
		+ "         WHEN      '" + StudyType.N.getName() + "'  THEN pmain.name "
		+ "         WHEN '" + StudyType.T.getName() + "'   THEN CONCAT(pmain.name, '-', geoloc.description) "
		+ "         ELSE '' "
		+ "     END AS studyName, "
		+ "     pmain.study_type AS studyType, "
		+ "     CASE pmain.study_type "
		+ "         WHEN '" + StudyType.N.getName() + "'         THEN "
		+ "             MAX(IF(pProp.variable_id = " + TermId.SEASON_VAR.getId() + ", "
		+ "                 pProp.value, "
		+ "                 NULL)) "
		+ "         WHEN '" + StudyType.T.getName()	+ "'         THEN "
		+ "             MAX(IF(geoprop.type_id = " + TermId.SEASON_VAR.getId() + ", "
		+ "                 geoprop.value, "
		+ "                 NULL)) "
		+ "     END AS seasonId, "
		+ "     CASE pmain.study_type "
		+ "         WHEN '" + StudyType.N.getName() + "' THEN NULL "
		+ "         WHEN '" + StudyType.T.getName() + "'   THEN pmain.project_id "
		+ "         ELSE '' "
		+ "     END AS trialDbId, "
		+ "     CASE pmain.study_type "
		+ "         WHEN '" + StudyType.N.getName() + "' THEN NULL "
		+ "         WHEN '" + StudyType.T.getName() + "'   THEN pmain.name "
		+ "         ELSE '' "
		+ "     END AS trialName, "
		+ "     MAX(IF(pProp.variable_id = " + TermId.START_DATE.getId() + ", "
		+ "         pProp.value, "
		+ "         NULL)) AS startDate, "
		+ "     MAX(IF(pProp.variable_id = " + TermId.END_DATE.getId() + ", "
		+ "         pProp.value, "
		+ "         NULL)) AS endDate, "
		+ "     pmain.deleted, "
		+ "     CASE pmain.study_type "
		+ "         WHEN '" + StudyType.N.getName()	+ "'         THEN "
		+ "             MAX(IF(pProp.variable_id = " + TermId.LOCATION_ID.getId() + ", "
		+ "                 pProp.value, "
		+ "                 NULL)) "
		+ "         WHEN '" + StudyType.T.getName() + "'         THEN "
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

	public List<Reference> getRootFolders(final String programUUID, final List<StudyType> studyTypes) {
		return getChildrenOfFolder(DmsProject.SYSTEM_FOLDER_ID, programUUID, studyTypes);
	}

	public List<Reference> getChildrenOfFolder(final Integer folderId, final String programUUID, final List<StudyType> studyTypes) {

		final List<Reference> childrenNodes = new ArrayList<>();

		if (studyTypes == null || studyTypes.isEmpty()) {
			throw new MiddlewareQueryException("Missing required parameter. At least one study type must be specified.");
		}

		try {
			final Query query = this.getSession().createSQLQuery(DmsProjectDao.GET_CHILDREN_OF_FOLDER);
			query.setParameter("folderId", folderId);
			query.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);

			final List<String> stydyTypeIds = new ArrayList<>();
			for (final StudyType studyType : studyTypes) {
				stydyTypeIds.add(studyType.getName());
			}
			query.setParameterList("studyTypeIds", stydyTypeIds);

			final List<Object[]> list = query.list();

			for (final Object[] row : list) {
				// project.id
				final Integer id = (Integer) row[0];
				// project.name
				final String name = (String) row[1];
				// project.description
				final String description = (String) row[2];
				// non-zero if a study, else a folder
				final Integer isStudy = ((Integer) row[3]);
				// project.program_uuid
				final String projectUUID = (String) row[4];

				if (isStudy == 1) {
					final String studyTypeRaw = (String) row[5];
					final StudyType studyType = studyTypeRaw != null ? StudyType.getStudyTypeByName(studyTypeRaw) : null;
					childrenNodes.add(new StudyReference(id, name, description, projectUUID, studyType));
				} else {
					childrenNodes.add(new FolderReference(id, name, description, projectUUID));
				}
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error retrieving study folder tree, folderId=" + folderId + " programUUID=" + programUUID + ":" + e.getMessage(), e);
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
			this.logAndThrowException("Error with getDatasetNodesByStudyId query from Project: " + e.getMessage(), e);
		}

		return datasetReferences;

	}

	public List<DmsProject> getStudiesByName(final String name) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.add(Restrictions.eq("name", name));
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.IS_STUDY.getId()));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

			return criteria.list();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getStudiesByName=" + name + " query on DmsProjectDao: " + e.getMessage(), e);
		}

		return new ArrayList<>();
	}

	public List<DmsProject> getStudiesByUserIds(final Collection<Integer> userIds) {
		final List<Object> userIdStrings = new ArrayList<>();
		if (userIds != null && !userIds.isEmpty()) {
			for (final Integer userId : userIds) {
				userIdStrings.add(userId.toString());
			}
		}
		return this.getStudiesByStudyProperty(TermId.STUDY_UID.getId(), Restrictions.in("p.value", userIdStrings));
	}

	public List<DmsProject> getStudiesByStartDate(final Integer startDate) {
		return this.getStudiesByStudyProperty(TermId.START_DATE.getId(), Restrictions.eq("p.value", startDate.toString()));
	}

	private List<DmsProject> getStudiesByStudyProperty(final Integer studyPropertyId, final Criterion valueExpression) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("properties", "p");
			criteria.add(Restrictions.eq("p.typeId", studyPropertyId));
			criteria.add(valueExpression);
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.IS_STUDY.getId()));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

			return criteria.list();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getStudiesByStudyProperty with " + valueExpression + " for property " + studyPropertyId
					+ " in DmsProjectDao: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public List<DmsProject> getStudiesByIds(final Collection<Integer> projectIds) {
		try {
			if (projectIds != null && !projectIds.isEmpty()) {
				final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
				criteria.add(Restrictions.in(DmsProjectDao.PROJECT_ID, projectIds));
				criteria.createAlias("relatedTos", "pr");
				criteria.add(Restrictions.eq("pr.typeId", TermId.IS_STUDY.getId()));
				criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

				return criteria.list();
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getStudiesByIds= " + projectIds + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public List<DmsProject> getDatasetsByStudy(final Integer studyId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.objectProject.projectId", studyId));
			criteria.setProjection(Projections.property("pr.subjectProject"));
			return criteria.list();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getDatasetsByStudy= " + studyId + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public DmsProject getParentStudyByDataset(final Integer datasetId) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.subjectProject.projectId", datasetId));

			criteria.setProjection(Projections.property("pr.objectProject"));

			return (DmsProject) criteria.uniqueResult();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getParentStudyByDataset= " + datasetId + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return null;
	}

	public List<DmsProject> getStudyAndDatasetsById(final Integer projectId) {
		final Set<DmsProject> projects = new HashSet<>();

		final DmsProject project = this.getById(projectId);
		if (project != null) {
			projects.add(project);

			final DmsProject parent = this.getParentStudyByDataset(projectId);
			if (parent != null) {
				projects.add(parent);

			} else {
				final List<DmsProject> datasets = this.getDatasetsByStudy(projectId);
				if (datasets != null && !datasets.isEmpty()) {
					projects.addAll(datasets);
				}
			}
		}

		return new ArrayList<>(projects);
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
			this.logAndThrowException("Error in getByIds= " + projectIds + " query in DmsProjectDao: " + e.getMessage(), e);
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
			this.logAndThrowException("Error with getProjectsByFolder query from Project: " + e.getMessage(), e);
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
			this.logAndThrowException("Error in countProjectsByFolder(" + folderId + ") query in DmsProjectDao: " + e.getMessage(), e);
		}

		return count;

	}

	public List<DmsProject> getDataSetsByStudyAndProjectProperty(final int studyId, final int variable, final String value) {
		try {
			final Criteria criteria = this.getSession().createCriteria(this.getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.objectProject.projectId", studyId));
			criteria.createAlias("properties", "prop");
			criteria.add(Restrictions.eq("prop.variableId", variable));
			criteria.add(Restrictions.eq("prop.value", value));
			criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
			criteria.addOrder(Order.asc("prop.rank"));

			return criteria.list();

		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error in getDataSetsByProjectProperty(" + variable + ", " + value + ") query in DmsProjectDao: " + e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public List<StudyReference> getStudiesByTrialEnvironments(final List<Integer> environmentIds) {
		final List<StudyReference> studies = new ArrayList<>();
		try {
			final String sql =
					"SELECT p.project_id, p.name, p.description, count(DISTINCT e.nd_geolocation_id)" + " FROM project p"
							+ " INNER JOIN project_relationship pr ON pr.object_project_id = p.project_id AND pr.type_id = "
							+ TermId.BELONGS_TO_STUDY.getId() + " INNER JOIN nd_experiment_project ep"
							+ " INNER JOIN nd_experiment e ON e.nd_experiment_id = ep.nd_experiment_id"
							+ " INNER JOIN nd_geolocation g on g.nd_geolocation_id = e.nd_geolocation_id"
							+ " WHERE (ep.project_id = p.project_id OR ep.project_id = pr.subject_project_id)"
							+ " AND e.nd_geolocation_id IN (:environmentIds)" + " GROUP BY p.project_id, p.name, p.description";
			final Query query = this.getSession().createSQLQuery(sql).setParameterList("environmentIds", environmentIds);
			final List<Object[]> result = query.list();
			for (final Object[] row : result) {
				studies.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2], ((BigInteger) row[3]).intValue()));
			}

		} catch (final HibernateException e) {
			this.logAndThrowException(
					"Error in getStudiesByTrialEnvironments=" + environmentIds + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return studies;
	}

	public Integer getProjectIdByNameAndProgramUUID(final String name, final String programUUID, final TermId relationship) {
		try {
			final String sql =
					"SELECT s.project_id FROM project s " + " WHERE name = :name AND program_uuid = :program_uuid"
							+ " AND EXISTS (SELECT 1 FROM project_relationship pr WHERE pr.subject_project_id = s.project_id "
							+ "   AND pr.type_id = " + relationship.getId() + ") "
							+ "	AND s.deleted !=  " + DELETED_STUDY
							+ " LIMIT 1";

			final Query query =
					this.getSession().createSQLQuery(sql).setParameter("name", name).setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			return (Integer) query.uniqueResult();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getStudyIdByName=" + name + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return null;
	}

	public List<StudyDetails> getAllStudyDetails(final StudyType studyType, final String programUUID) {
		return this.getAllStudyDetails(studyType, programUUID, -1, -1);
	}

	public List<StudyDetails> getAllStudyDetails(final StudyType studyType, final String programUUID, final int start, final int numOfRows) {
		final List<StudyDetails> studyDetails = new ArrayList<>();

		final StringBuilder sqlString = new StringBuilder().append(
			"SELECT DISTINCT p.name AS name, p.description AS title, ppObjective.value AS objective, ppStartDate.value AS startDate, ")
			.append("ppEndDate.value AS endDate, ppPI.value AS piName, gpSiteName.value AS siteName, p.project_id AS id ")
			.append(", ppPIid.value AS piId, gpSiteId.value AS siteId ").append("FROM project p ")
			.append("   LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id ")
			.append("                   AND ppObjective.variable_id =  ").append(TermId.STUDY_OBJECTIVE.getId()).append(" ")
			.append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
			.append("                   AND ppStartDate.variable_id =  ").append(TermId.START_DATE.getId()).append(" ")
			.append("   LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id ")
			.append("                   AND ppEndDate.variable_id =  ").append(TermId.END_DATE.getId()).append(" ")
			.append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
			.append("                   AND ppPI.variable_id =  ").append(TermId.PI_NAME.getId()).append(" ")
			.append("   LEFT JOIN projectprop ppPIid ON p.project_id = ppPIid.project_id ")
			.append("                   AND ppPIid.variable_id =  ").append(TermId.PI_ID.getId()).append(" ")
			.append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
			.append("       LEFT JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
			.append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
			.append("           AND gpSiteName.type_id =  ").append(TermId.TRIAL_LOCATION.getId()).append(" ")
			.append("       LEFT JOIN nd_geolocationprop gpSiteId ON e.nd_geolocation_id = gpSiteId.nd_geolocation_id ")
			.append("           AND gpSiteId.type_id =  ").append(TermId.LOCATION_ID.getId()).append(" ")
			.append("       LEFT JOIN project_relationship pr ON pr.object_project_id = p.project_id and pr.type_id = ")
			.append(TermId.BELONGS_TO_STUDY.getId()).append(" WHERE p.deleted != " + DELETED_STUDY + " ")
			.append(" AND p.study_type = '" + studyType.getName() + "'")
			.append(" AND (p.program_uuid = :" + DmsProjectDao.PROGRAM_UUID + " ").append("OR p.program_uuid IS NULL) ")
			.append(" ORDER BY p.name ");
		if (start > 0 && numOfRows > 0) {
			sqlString.append(" LIMIT " + start + "," + numOfRows);
		}

		List<Object[]> list = null;

		try {
			final Query query = this.getSession().createSQLQuery(sqlString.toString()).addScalar("name").addScalar("title").addScalar("objective")
				.addScalar("startDate").addScalar("endDate").addScalar("piName").addScalar("siteName").addScalar("id").addScalar("piId")
				.addScalar("siteId").setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			list = query.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getAllStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
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

			final StudyDetails study =
				new StudyDetails(id, name, title, objective, startDate, endDate, studyType, piName, siteName, piId, siteId);
			studyDetails.add(study);
		}
		return studyDetails;
	}

	public StudyType getStudyType(final int studyId) {
		try {
			final SQLQuery query = this.getSession().createSQLQuery("SELECT p.study_type FROM project p " + " WHERE p.project_id = :projectId ");
			query.setParameter(DmsProjectDao.PROJECT_ID, studyId);

			final Object queryResult = query.uniqueResult();
			if (queryResult != null) {
				return StudyType.getStudyTypeByName((String) queryResult);
			}
			return null;
		} catch (final HibernateException he) {
			throw new MiddlewareQueryException(
				String.format("Hibernate error in getting study type for a studyId %s. Cause: %s", studyId, he.getCause().getMessage()),
				he);
		}
	}

	public StudyDetails getStudyDetails(final StudyType studyType, final int studyId) {
		StudyDetails studyDetails = null;
		try {

			final Query query =
					this.getSession().createSQLQuery(STUDY_DETAILS_SQL).addScalar("name").addScalar("title").addScalar("objective")
					.addScalar("startDate").addScalar("endDate").addScalar("piName").addScalar("siteName").addScalar("id")
					.addScalar("piId").addScalar("siteId").addScalar("folderId").addScalar("programUUID");

			query.setParameter("studyId", studyId);

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
					final String piId = (String) row[8];
					final String siteId = (String) row[9];
					final Integer folderId = (Integer) row[10];
					final String programUUID = (String) row[11];

					studyDetails =
							new StudyDetails(id, name, title, objective, startDate, endDate, studyType, piName, siteName, piId, siteId);
					studyDetails.setParentFolderId(folderId.longValue());
					studyDetails.setProgramUUID(programUUID);
				}
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getTrialObservationTable() query in DmsProjectDao: " + e.getMessage(), e);
		}
		return studyDetails;
	}

	public long countAllStudyDetails(final StudyType studyType, final String programUUID) {
		try {
			final StringBuilder sqlString =
					new StringBuilder()
			.append("SELECT COUNT(1) ")
			.append("FROM project p ")
			.append("   LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id ")
			.append("                   AND ppObjective.variable_id =  ")
			.append(TermId.STUDY_OBJECTIVE.getId())
			.append(" ")
			.append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
			.append("                   AND ppStartDate.variable_id =  ")
			.append(TermId.START_DATE.getId())
			.append(" ")
			.append("   LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id ")
			.append("                   AND ppEndDate.variable_id =  ")
			.append(TermId.END_DATE.getId())
			.append(" ")
			.append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
			.append("                   AND ppPI.variable_id =  ")
			.append(TermId.PI_NAME.getId())
			.append(" ")
			.append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
			.append("       LEFT JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
			.append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
			.append("           AND gpSiteName.type_id =  ").append(TermId.TRIAL_LOCATION.getId()).append(" ")
			.append("WHERE p.deleted != " + DELETED_STUDY + " ")
			.append(" AND p.study_type = '" + studyType.getName() )
			.append("'   AND (p.").append(DmsProjectDao.PROGRAM_UUID)
			.append(" = :").append(DmsProjectDao.PROGRAM_UUID).append(" ").append("   OR p.")
			.append(DmsProjectDao.PROGRAM_UUID).append(" IS NULL) ");

			final Query query = this.getSession().createSQLQuery(sqlString.toString()).setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in countAllStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
		}
		return 0;

	}

	public List<StudyDetails> getAllNurseryAndTrialStudyDetails(final String programUUID) {
		return this.getAllNurseryAndTrialStudyDetails(programUUID, 0, -1);
	}

	public List<StudyDetails> getAllNurseryAndTrialStudyDetails(final String programUUID, final int start, final int numOfRows) {
		final List<StudyDetails> studyDetails = new ArrayList<>();
		try {

			final StringBuilder sqlString = new StringBuilder().append(
				"SELECT DISTINCT p.name AS name, p.description AS title, ppObjective.value AS objective, ppStartDate.value AS startDate, ")
				.append(
					"ppEndDate.value AS endDate, ppPI.value AS piName, gpSiteName.value AS siteName, p.project_id AS id, p.study_type AS "
						+ "studyType ")
				.append(", ppPIid.value AS piId, gpSiteId.value AS siteId ").append("FROM project p ")
				.append(" LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id ")
				.append(" AND ppObjective.variable_id =  ").append(TermId.STUDY_OBJECTIVE.getId()).append(" ")
				// 8030
				.append(" LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
				.append(" AND ppStartDate.variable_id =  ").append(TermId.START_DATE.getId()).append(" ")
				// 8050
				.append(" LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id ")
				.append(" AND ppEndDate.variable_id =  ").append(TermId.END_DATE.getId()).append(" ")
				// 8060
				.append(" LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
				.append(" AND ppPI.variable_id =  ").append(TermId.PI_NAME.getId()).append(" ")
				// 8100
				.append(" LEFT JOIN projectprop ppPIid ON p.project_id = ppPIid.project_id ")
				.append(" AND ppPIid.variable_id =  ").append(TermId.PI_ID.getId()).append(" ")
				.append(" LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
				.append(" INNER JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
				.append(" LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
				.append(" AND gpSiteName.type_id =  ").append(TermId.TRIAL_LOCATION.getId()).append(" ")
				// 8180
				.append(" LEFT JOIN nd_geolocationprop gpSiteId ON e.nd_geolocation_id = gpSiteId.nd_geolocation_id ")
				.append(" AND gpSiteId.type_id =  ").append(TermId.LOCATION_ID.getId()).append(" ")
				.append(" WHERE p.deleted != " + DELETED_STUDY + " ")
				.append(" AND p.study_type IN ( '" + StudyType.N.getName() + "', '" + StudyType.T.getName() + "')")
				.append(" AND (p.program_uuid = :" + DmsProjectDao.PROGRAM_UUID + " ").append("OR p.program_uuid IS NULL) ")
				.append(" ORDER BY p.name ");
			if (start > 0 && numOfRows > 0) {
				sqlString.append(" LIMIT " + start + "," + numOfRows);
			}

			final Query query = this.getSession().createSQLQuery(sqlString.toString()).addScalar("name").addScalar("title").addScalar("objective")
				.addScalar("startDate").addScalar("endDate").addScalar("piName").addScalar("siteName").addScalar("id")
				.addScalar("studyType").addScalar("piId").addScalar("siteId").setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);

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
					final String studyTypeId = (String) row[8];
					final String piId = (String) row[9];
					final String siteId = (String) row[10];

					studyDetails.add(
						new StudyDetails(id, name, title, objective, startDate, endDate, StudyType.getStudyTypeByName(studyTypeId), piName,
							siteName, piId, siteId));
				}
			}

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getAllNurseryAndTrialStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
		}
		return studyDetails;

	}

	public long countAllNurseryAndTrialStudyDetails(final String programUUID) {
		try {

			final StringBuilder sqlString =
					new StringBuilder()
			.append("SELECT COUNT(1) ")
			.append("FROM project p ")
			.append("   LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id ")
			.append("                   AND ppObjective.variable_id =  ")
			.append(TermId.STUDY_OBJECTIVE.getId())
			.append(" ")
			// 8030
			.append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
			.append("                   AND ppStartDate.variable_id =  ")
			.append(TermId.START_DATE.getId())
			.append(" ")
			// 8050
			.append("   LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id ")
			.append("                   AND ppEndDate.variable_id =  ")
			.append(TermId.END_DATE.getId())
			.append(" ")
			// 8060
			.append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
			.append("                   AND ppPI.variable_id =  ")
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
			.append("WHERE p.deleted != " + DELETED_STUDY + " ")
			.append( " AND p.study_type in ('" + StudyType.N.getName() + "', '" + StudyType.T.getName() + "' ) ")
			.append("   AND (p.").append(DmsProjectDao.PROGRAM_UUID).append(" = :").append(DmsProjectDao.PROGRAM_UUID)
			.append(" ").append("   OR p.").append(DmsProjectDao.PROGRAM_UUID).append(" IS NULL) ");

			final Query query = this.getSession().createSQLQuery(sqlString.toString()).setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);

			return ((BigInteger) query.uniqueResult()).longValue();

		} catch (final HibernateException e) {
			this.logAndThrowException("Error in countAllNurseryAndTrialStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
		}
		return 0;

	}

	/**
	 * Retrieves all the study details
	 *
	 * @return List of all nursery and trial study nodes
	 * @
	 */

	public List<StudyNode> getAllNurseryAndTrialStudyNodes(final String programUUID) {
		final List<StudyNode> studyNodes = new ArrayList<>();

		final StringBuilder sqlString =
				new StringBuilder().append("SELECT DISTINCT p.project_id AS id ").append("        , p.name AS name ")
				.append("        , p.description AS description ").append("        , ppStartDate.value AS startDate ")
				.append("        , p.study_type AS studyType ").append("        , gpSeason.value AS season ")
				.append("FROM project p  ")
				.append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
				.append("                   AND ppStartDate.variable_id =  ").append(TermId.START_DATE.getId()).append(" ")
				.append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
				.append("   INNER JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
				.append("   LEFT JOIN nd_geolocationprop gpSeason ON e.nd_geolocation_id = gpSeason.nd_geolocation_id ")
				.append("           AND gpSeason.type_id =  ").append(TermId.SEASON_VAR.getId()).append(" ")
				.append("WHERE p.deleted != " + DELETED_STUDY + " ")
				.append( " AND p.study_type in ('" + StudyType.N.getName() + "', '" + StudyType.T.getName() + "' ) ")
				.append("   AND (p.").append(DmsProjectDao.PROGRAM_UUID)
				.append(" = :").append(DmsProjectDao.PROGRAM_UUID).append(" ").append("   OR p.")
				.append(DmsProjectDao.PROGRAM_UUID).append(" IS NULL) ");
		List<Object[]> list = null;

		try {
			final Query query =
					this.getSession().createSQLQuery(sqlString.toString()).addScalar("id").addScalar("name").addScalar("description")
					.addScalar("startDate").addScalar("studyType").addScalar("season")
					.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			list = query.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error in getAllStudyNodes() query in DmsProjectDao: " + e.getMessage(), e);
		}

		if (list == null || list.isEmpty()) {
			return studyNodes;
		}

		for (final Object[] row : list) {
			final Integer id = (Integer) row[0];
			final String name = (String) row[1];
			final String description = (String) row[2];
			final String startDate = (String) row[3];
			final String studyTypeStr = (String) row[4];
			final String seasonStr = (String) row[5];

			final Season season = Season.getSeason(seasonStr);
			studyNodes.add(new StudyNode(id, name, description, startDate, StudyType.getStudyTypeByName(studyTypeStr), season));

		}
		Collections.sort(studyNodes);
		return studyNodes;
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
			this.logAndThrowException("Error in checkIfProjectNameIsExisting=" + name + " query on DmsProjectDao: " + e.getMessage(), e);
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
			this.logAndThrowException("Error at getAllFolders, query at DmsProjectDao: " + e.getMessage(), e);
		}
		return folders;
	}

	public List<Integer> getAllProgramStudiesAndFolders(final String programUUID) {
		List<Integer> projectIds = null;
		try {
			final SQLQuery query = this.getSession().createSQLQuery(DmsProjectDao.GET_ALL_PROGRAM_STUDIES_AND_FOLDERS);
			query.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			projectIds = query.list();
		} catch (final HibernateException e) {
			this.logAndThrowException("Error at getAllProgramStudiesAndFolders, query at DmsProjectDao: " + e.getMessage(), e);
		}
		return projectIds;
	}

	public List<ValueReference> getDistinctProjectNames() {
		final List<ValueReference> results = new ArrayList<>();
		try {
			final String sql = "SELECT DISTINCT name FROM project ";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			final List<String> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (final String row : list) {
					results.add(new ValueReference(row, row));
				}
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getDistinctProjectNames() query from Project " + e.getMessage(), e);
		}
		return results;
	}

	public List<ValueReference> getDistinctProjectDescriptions() {
		final List<ValueReference> results = new ArrayList<>();
		try {
			final String sql = "SELECT DISTINCT description FROM project ";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			final List<String> list = query.list();
			if (list != null && !list.isEmpty()) {
				for (final String row : list) {
					results.add(new ValueReference(row, row));
				}
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getDistinctProjectDescription() query from Project " + e.getMessage(), e);
		}
		return results;
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
			final String sql = "SELECT project_id FROM project WHERE name = :name AND program_uuid = :program_uuid";
			final Query query = this.getSession().createSQLQuery(sql);
			query.setParameter("name", name);
			query.setParameter(DmsProjectDao.PROGRAM_UUID, programUUID);
			final List<Integer> list = query.list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		} catch (final HibernateException e) {
			this.logAndThrowException("Error with getDistinctProjectDescription() query from Project " + e.getMessage(), e);
		}
		return null;
	}

	public List<String> getAllSharedProjectNames() {
		final List<String> results = new ArrayList<>();
		try {
			final String sql = "SELECT name FROM project WHERE program_uuid is null";
			final SQLQuery query = this.getSession().createSQLQuery(sql);
			return query.list();
		} catch (final HibernateException e) {
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
		criteria.add(Restrictions.isNotNull("studyType"));

		criteria.add(Restrictions.ne(DmsProjectDao.DELETED, true));

		if (parameters.containsKey(StudyFilters.PROGRAM_ID)) {
			criteria.add(Restrictions.eq(StudyFilters.PROGRAM_ID.getParameter(), parameters.get(StudyFilters.PROGRAM_ID)));
		} else {
			criteria.add(Restrictions.isNotNull(StudyFilters.PROGRAM_ID.getParameter()));
		}

		if (parameters.containsKey(StudyFilters.LOCATION_ID)) {
			final DetachedCriteria ppLocation = DetachedCriteria.forClass(ProjectProperty.class);

			ppLocation.add(Restrictions.eq(DmsProjectDao.VARIABLE_ID, DmsProjectDao.LOCATION_ID));
			ppLocation.add(Restrictions.eq(DmsProjectDao.VALUE, parameters.get(StudyFilters.LOCATION_ID)));
			ppLocation.setProjection(Projections.property("project.projectId"));

			criteria.add(Property.forName(DmsProjectDao.PROJECT_ID).in(ppLocation));
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
				studyMetadata.setTrialDbId((row[5] instanceof String && StringUtils.isNumeric((String) row[5])) ? Integer.parseInt((String) row[5]) : null);
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
}
