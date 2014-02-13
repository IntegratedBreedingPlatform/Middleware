/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 * 
 * Generation Challenge Programme (GCP)
 * 
 * 
 * This software is licensed for use under the terms of the GNU General Public
 * License (http://bit.ly/8Ztv8M) and the provisions of Part F of the Generation
 * Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 * 
 *******************************************************************************/
package org.generationcp.middleware.dao.dms;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * DAO class for {@link DmsProject}.
 * 
 * @author Darla Ani, Joyce Avestro
 *
 */
public class DmsProjectDao extends GenericDAO<DmsProject, Integer> {
	    
	private static final String GET_CHILDREN_OF_FOLDER =		
			"SELECT DISTINCT subject.project_id, subject.name,  subject.description " 
			+ "		, (CASE WHEN (type_id = " + TermId.IS_STUDY.getId() + ") THEN 1 ELSE 0 END) AS is_study  "
			+ "FROM project subject "
			+ "		INNER JOIN project_relationship pr on subject.project_id = pr.subject_project_id  "
			+ "WHERE (pr.type_id = " + TermId.HAS_PARENT_FOLDER.getId() + " or pr.type_id = " + TermId.IS_STUDY.getId() + ") " 
			+ "		AND pr.object_project_id = :folderId "
			+ "		AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = "+ TermId.STUDY_STATUS.getId()
			+ "     	AND pp.project_id = subject.project_id AND pp.value = " 
			+ "         "+TermId.DELETED_STUDY.getId()+") "
			+ "ORDER BY name "
			;

	
	private static final String GET_STUDIES_OF_FOLDER =
			"SELECT  DISTINCT pr.subject_project_id "
			+ "FROM    project_relationship pr, project p "
			+ "WHERE   pr.type_id = "  + TermId.IS_STUDY.getId() + " "
			+ "        AND pr.subject_project_id = p.project_id "
			+ "        AND pr.object_project_id = :folderId "
			+ "		AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = "+ TermId.STUDY_STATUS.getId()
			+ "     	AND pp.project_id = p.project_id AND pp.value = " 
			+ "         "+TermId.DELETED_STUDY.getId()+") "
			+ "ORDER BY p.name "
			;
	
	private static final String GET_ROOT_FOLDERS =
			"SELECT DISTINCT p.project_id, p.name, p.description " 
			+ " FROM project p "
		  	+ " INNER JOIN project_relationship pr ON pr.subject_project_id = p.project_id " 
		    + " WHERE pr.object_project_id = " + DmsProject.SYSTEM_FOLDER_ID  
		    + " AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = "+ TermId.STUDY_STATUS.getId()
			+ "     	AND pp.project_id = p.project_id AND pp.value = " 
			+ "         "+TermId.DELETED_STUDY.getId()+") "
		    + " ORDER BY p.project_id ";
	
	private static final String COUNT_PROJECTS_WITH_VARIABLE =
	        "SELECT count(pp.project_id) " 
	        + " FROM projectprop pp "
	        + " WHERE NOT EXISTS( "
	            + " SELECT 1 FROM projectprop stat "
	            + " WHERE stat.project_id = pp.project_id "
	            + " AND stat.type_id = " + TermId.STUDY_STATUS.getId()
	            + " AND value = " + TermId.DELETED_STUDY.getId() + ") "
	        + " AND pp.type_id = " + TermId.STANDARD_VARIABLE.getId()
	        + " AND pp.value = :variableId";
	
	private static final String GET_ALL_FOLDERS = 
	        "SELECT pr.object_project_id, pr.subject_project_id, p.name, p.description "
	        + " FROM project_relationship pr "
	        + " INNER JOIN project p ON p.project_id = pr.subject_project_id "
	        + " WHERE pr.type_id = " + TermId.HAS_PARENT_FOLDER.getId() 
	        ;
	
	@SuppressWarnings("unchecked")
	public List<FolderReference> getRootFolders() throws MiddlewareQueryException{
		
		List<FolderReference> folderList = new ArrayList<FolderReference>();
		
		/* SELECT DISTINCT p.projectId, p.name, p.description
		 * 	FROM DmsProject p 
		 * 		JOIN p.relatedTos pr 
		 * WHERE pr.typeId = CVTermId.HAS_PARENT_FOLDER.getId() 
		 * 		 AND pr.objectProject.projectId = " + DmsProject.SYSTEM_FOLDER_ID  
		 * 		 AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = "+ TermId.STUDY_STATUS.getId()
		 *    	 AND pp.project_id = p.project_id AND pp.value = " 
	     *         "+TermId.DELETED_STUDY.getId()+") "	
		 * ORDER BY p.project_id 
		 */
		
		try {
			
			Query query = getSession().createSQLQuery(GET_ROOT_FOLDERS);
			List<Object[]> list =  query.list();
			
			if (list != null && list.size() > 0) {
				for (Object[] row : list){
					Integer id = (Integer)row[0]; //project.id
					String name = (String) row [1]; //project.name
					String description = (String) row [2]; //project.description
					folderList.add(new FolderReference(id, name, description));
				}
			}
		} catch (HibernateException e) {
			logAndThrowException("Error with getRootFolders query from Project: " + e.getMessage(), e);
		}	
		
		return folderList;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<Reference> getChildrenOfFolder(Integer folderId) throws MiddlewareQueryException{
		
		List<Reference> childrenNodes = new ArrayList<Reference>();
		
		try {
			Query query = getSession().createSQLQuery(GET_CHILDREN_OF_FOLDER);
			query.setParameter("folderId", folderId);
			List<Object[]> list =  query.list();
			
			for (Object[] row : list){
				Integer id = (Integer) row[0]; //project.id
				String name = (String) row [1]; //project.name
				String description = (String) row[2]; //project.description
				Integer isStudy = ((Integer) row[3]).intValue(); //non-zero if a study, else a folder
				
				if (isStudy > 0){
					childrenNodes.add(new StudyReference(id, name, description));
				} else {
					childrenNodes.add(new FolderReference(id, name, description));
				}
			}
			
		} catch (HibernateException e) {
			logAndThrowException("Error with getChildrenOfFolder query from Project: " + e.getMessage(), e);
		}
		
		return childrenNodes;
		
	}
	
	

	@SuppressWarnings("unchecked")
	public List<DatasetReference> getDatasetNodesByStudyId(Integer studyId) throws MiddlewareQueryException{
		
		List<DatasetReference> datasetReferences = new ArrayList<DatasetReference>();
		
		try {
			/*
			SELECT DISTINCT p.projectId, p.name, p.description, pr.objectProject.projectId 
			FROM DmsProject p JOIN p.relatedTos pr 
			WHERE pr.typeId = CVTermId.BELONGS_TO_STUDY.getId()
			      AND pr.objectProject.projectId = :studyId 
			ORDER BY name
			*/ 

			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.objectProject.projectId", studyId));
			
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("projectId"));
			projectionList.add(Projections.property("name"));
			projectionList.add(Projections.property("description"));
			projectionList.add(Projections.property("pr.objectProject.projectId"));
			criteria.setProjection(projectionList);
			
			criteria.addOrder(Order.asc("name"));

			List<Object[]> list =  criteria.list();
			
			for (Object[] row : list){
				Integer id = (Integer) row[0]; //project.id
				String name = (String) row [1]; //project.name
				String description = (String) row [2]; //project.description
				datasetReferences.add(new DatasetReference(id, name, description));
			}
			
		} catch (HibernateException e) {
			logAndThrowException("Error with getDatasetNodesByStudyId query from Project: " + e.getMessage(), e);
		}
		
		return datasetReferences;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<DmsProject> getStudiesByName(String name) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("name", name));
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.IS_STUDY.getId()));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			
			return criteria.list();
			
		} catch (HibernateException e) {
			logAndThrowException("Error in getStudiesByName=" + name + " query on DmsProjectDao: " + e.getMessage(), e);
		}
		
		return new ArrayList<DmsProject>();
	}
	
	public List<DmsProject> getStudiesByUserIds(Collection<Integer> userIds) throws MiddlewareQueryException {
		List<Object> userIdStrings = new ArrayList<Object>();
		if (userIds != null && userIds.size() > 0) {
			for (Integer userId : userIds) {
				userIdStrings.add(userId.toString());
			}
		}
		return getStudiesByStudyProperty(TermId.STUDY_UID.getId(), Restrictions.in("p.value", userIdStrings));
	}
	
	public List<DmsProject> getStudiesByStartDate(Integer startDate) throws MiddlewareQueryException {
		return getStudiesByStudyProperty(TermId.START_DATE.getId(), Restrictions.eq("p.value", startDate.toString()));
	}

	@SuppressWarnings("unchecked")
	private List<DmsProject> getStudiesByStudyProperty(Integer studyPropertyId, Criterion valueExpression) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.createAlias("properties", "p");
			criteria.add(Restrictions.eq("p.typeId", studyPropertyId));
			criteria.add(valueExpression);
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.IS_STUDY.getId()));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			
			return criteria.list();
			
		} catch (HibernateException e) {
			logAndThrowException("Error in getStudiesByStudyProperty with " + valueExpression + " for property " + studyPropertyId 
					+ " in DmsProjectDao: " + e.getMessage(), e);
		}
		return new ArrayList<DmsProject>();
	}
	
	@SuppressWarnings("unchecked")
	public List<DmsProject> getStudiesByIds(Collection<Integer> projectIds) throws MiddlewareQueryException {
		try {
			if (projectIds != null && projectIds.size() > 0) {
				Criteria criteria = getSession().createCriteria(getPersistentClass());
				criteria.add(Restrictions.in("projectId", projectIds));
				criteria.createAlias("relatedTos", "pr");
				criteria.add(Restrictions.eq("pr.typeId", TermId.IS_STUDY.getId()));
				criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
				
				return criteria.list();
			}
		} catch (HibernateException e) {
			logAndThrowException("Error in getStudiesByIds= " + projectIds + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return new ArrayList<DmsProject>();
	}
	
	@SuppressWarnings("unchecked")
	public List<DmsProject> getDatasetsByStudy(Integer studyId) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.objectProject.projectId", studyId));
			criteria.setProjection(Projections.property("pr.subjectProject"));
			return criteria.list();
			
		} catch (HibernateException e) {
			logAndThrowException("Error in getDatasetsByStudy= " + studyId + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return new ArrayList<DmsProject>();
	}
	
	public DmsProject getParentStudyByDataset(Integer datasetId) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.subjectProject.projectId", datasetId));
			
			criteria.setProjection(Projections.property("pr.objectProject"));

			return (DmsProject) criteria.uniqueResult();
			
		} catch (HibernateException e) {
			logAndThrowException("Error in getParentStudyByDataset= " + datasetId + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return null;
	}
	
	public List<DmsProject> getStudyAndDatasetsById(Integer projectId) throws MiddlewareQueryException {
		Set<DmsProject> projects = new HashSet<DmsProject>();
		
		DmsProject project = getById(projectId);
		if (project != null) {
			projects.add(project);
			
			DmsProject parent = getParentStudyByDataset(projectId);
			if (parent != null) {
				projects.add(parent);
			
			} else {
				List<DmsProject> datasets = getDatasetsByStudy(projectId);
				if (datasets != null && datasets.size() > 0) {
					projects.addAll(datasets);
				}
			}
		}
		
		return new ArrayList<DmsProject>(projects);
	}
	
	@SuppressWarnings("unchecked")
	public List<DmsProject> getByFactor(Integer factorId) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.createAlias("properties", "p");
			criteria.add(Restrictions.eq("p.typeId", TermId.STANDARD_VARIABLE.getId()));
			criteria.add(Restrictions.eq("p.value", factorId.toString()));

			List<DmsProject> results = criteria.list();
			
			return results;
		
		} catch(HibernateException e) {
			logAndThrowException("Error getByFactor=" + factorId + " at DmsProjectDao: " + e.getMessage(), new Throwable());
		}
		return new ArrayList<DmsProject>();
	}
	
	@SuppressWarnings("unchecked")
	public List<DmsProject> getByIds(Collection<Integer> projectIds) throws MiddlewareQueryException {
		List<DmsProject> studyNodes = new ArrayList<DmsProject>();
		try {
			if (projectIds != null && projectIds.size() > 0) {
				Criteria criteria = getSession().createCriteria(getPersistentClass());
				criteria.add(Restrictions.in("projectId", projectIds));
				criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
				
				return criteria.list();
			}
		} catch (HibernateException e) {
			logAndThrowException("Error in getByIds= " + projectIds + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return studyNodes;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<DmsProject> getProjectsByFolder(Integer folderId, int start, int numOfRows) throws MiddlewareQueryException{
		List<DmsProject> projects = new ArrayList<DmsProject>();
		if (folderId == null){
			return projects;
		}
		
		try {			
			// Get projects by folder
			Query query = getSession().createSQLQuery(DmsProjectDao.GET_STUDIES_OF_FOLDER);
			query.setParameter("folderId", folderId);
			query.setFirstResult(start);
			query.setMaxResults(numOfRows);
			List<Integer> projectIds =  (List<Integer>) query.list();
			projects = getByIds(projectIds);
			
		} catch (HibernateException e) {
			logAndThrowException("Error with getProjectsByFolder query from Project: " + e.getMessage(), e);
		}
		
		return projects;
	}

	@SuppressWarnings("unchecked")
	public long countProjectsByFolder(Integer folderId) throws MiddlewareQueryException{
		long count = 0;
		if (folderId == null) {
			return count;
		}
		
		try {
			Query query = getSession().createSQLQuery(DmsProjectDao.GET_STUDIES_OF_FOLDER);
			query.setParameter("folderId", folderId);
			List<Object[]> list =  query.list();
			count = list.size();
		} catch (HibernateException e) {
			logAndThrowException("Error in countProjectsByFolder(" + folderId + ") query in DmsProjectDao: " + e.getMessage(), e);
		}
		
		
		return count;

	}
	
	@SuppressWarnings("unchecked")
	public List<DmsProject> getDataSetsByStudyAndProjectProperty(int studyId, int type, String value) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.BELONGS_TO_STUDY.getId()));
			criteria.add(Restrictions.eq("pr.objectProject.projectId", studyId));
			criteria.createAlias("properties", "prop");
			criteria.add(Restrictions.eq("prop.typeId", type));
			criteria.add(Restrictions.eq("prop.value", value));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			
			return criteria.list();
			
		} catch(HibernateException e) {
			logAndThrowException("Error in getDataSetsByProjectProperty(" + type + ", " + value + ") query in DmsProjectDao: " + e.getMessage(), e);
		}
		return new ArrayList<DmsProject>();
	}
	
	@SuppressWarnings("unchecked")
	public List<StudyReference> getStudiesByTrialEnvironments(List<Integer> environmentIds) throws MiddlewareQueryException {
		List<StudyReference> studies = new ArrayList<StudyReference>();
		try {
			String sql = "SELECT p.project_id, p.name, p.description, count(DISTINCT e.nd_geolocation_id)"
							+ " FROM project p"
							+ " INNER JOIN project_relationship pr ON pr.object_project_id = p.project_id AND pr.type_id = " + TermId.BELONGS_TO_STUDY.getId()
							+ " INNER JOIN nd_experiment_project ep"
							+ " INNER JOIN nd_experiment e ON e.nd_experiment_id = ep.nd_experiment_id"
							+ " INNER JOIN nd_geolocation g on g.nd_geolocation_id = e.nd_geolocation_id"
							+ " WHERE (ep.project_id = p.project_id OR ep.project_id = pr.subject_project_id)"
							+ " AND e.nd_geolocation_id IN (:environmentIds)"
							+ " GROUP BY p.project_id, p.name, p.description";
			Query query = getSession().createSQLQuery(sql)
							.setParameterList("environmentIds", environmentIds);
			List<Object[]> result = query.list();
			for (Object[] row : result) {
				studies.add(new StudyReference((Integer) row[0], (String) row[1], (String) row[2], ((BigInteger) row[3]).intValue()));
			}
			
		} catch(HibernateException e) {
			logAndThrowException("Error in getStudiesByTrialEnvironments=" + environmentIds + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return studies;
	}
	
	public Integer getProjectIdByName(String name, TermId relationship) throws MiddlewareQueryException {
		try {
			String sql = "SELECT s.project_id FROM project s "
					+ " WHERE name = :name "
					+ " AND EXISTS (SELECT 1 FROM project_relationship pr WHERE pr.subject_project_id = s.project_id "
					+ "   AND pr.type_id = " + relationship.getId() + ") " 
	                + "	AND NOT EXISTS (SELECT 1 FROM projectprop pp WHERE pp.type_id = "+ TermId.STUDY_STATUS.getId() 
	                + "   AND pp.project_id = s.project_id AND pp.value = "
	    			+ "   (SELECT cvterm_id FROM cvterm WHERE name = 9 AND cv_id = "+CvId.STUDY_STATUS.getId()+")) "
	    			+ " LIMIT 1";
			
			Query query = getSession().createSQLQuery(sql)
							.setParameter("name", name);
			return (Integer) query.uniqueResult();
			
		} catch(HibernateException e) {
			logAndThrowException("Error in getStudyIdByName=" + name + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return null;
	}
	
    public List<StudyDetails> getAllStudyDetails(StudyType studyType) throws MiddlewareQueryException {
		return getAllStudyDetails(studyType, -1, -1);
	}
	@SuppressWarnings("unchecked")
    public List<StudyDetails> getAllStudyDetails(StudyType studyType, int start, int numOfRows) throws MiddlewareQueryException {
	    List<StudyDetails> studyDetails = new ArrayList<StudyDetails>();
        try {
            
            StringBuilder sqlString = new StringBuilder()
            .append("SELECT DISTINCT p.name AS name, p.description AS title, ppObjective.value AS objective, ppStartDate.value AS startDate, ")
            .append(                        "ppEndDate.value AS endDate, ppPI.value AS piName, gpSiteName.value AS siteName, p.project_id AS id ")
            .append("FROM project p ")
            .append("   INNER JOIN projectprop ppNursery ON p.project_id = ppNursery.project_id ")
            .append("                   AND ppNursery.type_id = ").append(TermId.STUDY_TYPE.getId()).append(" ")
            .append("                   AND ppNursery.value = ").append(studyType.getId()).append(" ") // 10000 for Nursery
            .append("   LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id ")
            .append("                   AND ppObjective.type_id =  ").append(TermId.STUDY_OBJECTIVE.getId()).append(" ") // 8030
            .append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
            .append("                   AND ppStartDate.type_id =  ").append(TermId.START_DATE.getId()).append(" ") // 8050 
            .append("   LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id ")
            .append("                   AND ppEndDate.type_id =  ").append(TermId.END_DATE.getId()).append(" ") // 8060 
            .append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
            .append("                   AND ppPI.type_id =  ").append(TermId.PI_NAME.getId()).append(" ") // 8100 
            .append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
            .append("       LEFT JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
            .append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
            .append("           AND gpSiteName.type_id =  ").append(TermId.TRIAL_LOCATION.getId()).append(" ") // 8180 
            .append("WHERE NOT EXISTS (SELECT 1 FROM projectprop ppDeleted WHERE ppDeleted.type_id =  ").append(TermId.STUDY_STATUS.getId()).append(" ") // 8006
            .append("               AND ppDeleted.project_id = p.project_id AND ppDeleted.value =  ").append(TermId.DELETED_STUDY.getId()).append(") ") // 12990
            .append("               ORDER BY p.name ") // 12990 
            ;
        
            Query query = getSession().createSQLQuery(sqlString.toString())
                        .addScalar("name")
                        .addScalar("title")
                        .addScalar("objective")
                        .addScalar("startDate")
                        .addScalar("endDate")
                        .addScalar("piName")
                        .addScalar("siteName")
                        .addScalar("id")
                        ;
            setStartAndNumOfRows(query, start, numOfRows);

            List<Object[]> list =  query.list();
            
            if (list != null && list.size() > 0) {
                for (Object[] row : list){
                    String name = (String) row [0]; 
                    String title = (String) row [1]; 
                    String objective = (String) row [2]; 
                    String startDate = (String) row [3]; 
                    String endDate = (String) row [4]; 
                    String piName = (String) row [5]; 
                    String siteName = (String) row [6];
                    Integer id = (Integer) row[7];
                    
                    studyDetails.add(new StudyDetails( id, name, title, objective, startDate, endDate, studyType, piName, siteName));
                }
            }

        } catch(HibernateException e) {
            logAndThrowException("Error in getAllStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
        }
        return studyDetails;
	    
	}
	
	public StudyDetails getStudyDetails(StudyType studyType, int studyId) throws MiddlewareQueryException {
	    StudyDetails studyDetails = null;
	    try {
	            
	            StringBuilder sqlString = new StringBuilder()
	            .append("SELECT DISTINCT p.name AS name, p.description AS title, ppObjective.value AS objective, ppStartDate.value AS startDate, ")
	            .append(                        "ppEndDate.value AS endDate, ppPI.value AS piName, gpSiteName.value AS siteName, p.project_id AS id ")
	            .append("FROM project p ")
	            .append("   INNER JOIN projectprop ppNursery ON p.project_id = ppNursery.project_id ")
	            .append("                   AND ppNursery.type_id = ").append(TermId.STUDY_TYPE.getId()).append(" ")
	            .append("                   AND ppNursery.value = ").append(studyType.getId()).append(" ") // 10000 for Nursery
	            .append("   LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id ")
	            .append("                   AND ppObjective.type_id =  ").append(TermId.STUDY_OBJECTIVE.getId()).append(" ") // 8030
	            .append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
	            .append("                   AND ppStartDate.type_id =  ").append(TermId.START_DATE.getId()).append(" ") // 8050 
	            .append("   LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id ")
	            .append("                   AND ppEndDate.type_id =  ").append(TermId.END_DATE.getId()).append(" ") // 8060 
	            .append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
	            .append("                   AND ppPI.type_id =  ").append(TermId.PI_NAME.getId()).append(" ") // 8100 
	            .append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
	            .append("       LEFT JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
	            .append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
	            .append("           AND gpSiteName.type_id =  ").append(TermId.TRIAL_LOCATION.getId()).append(" ") // 8180 
	            .append("  WHERE p.project_id = ").append(studyId); 
	            ;
	        
	            Query query = getSession().createSQLQuery(sqlString.toString())
	                        .addScalar("name")
	                        .addScalar("title")
	                        .addScalar("objective")
	                        .addScalar("startDate")
	                        .addScalar("endDate")
	                        .addScalar("piName")
	                        .addScalar("siteName")
	                        .addScalar("id")
	                        ;
	            
	            List<Object[]> list =  query.list();
	            
	            if (list != null && list.size() > 0) {
	                for (Object[] row : list){
	                    String name = (String) row [0]; 
	                    String title = (String) row [1]; 
	                    String objective = (String) row [2]; 
	                    String startDate = (String) row [3]; 
	                    String endDate = (String) row [4]; 
	                    String piName = (String) row [5]; 
	                    String siteName = (String) row [6];
	                    Integer id = (Integer) row[7];
	                    
	                    studyDetails = new StudyDetails( id, name, title, objective, startDate, endDate, studyType, piName, siteName);
	                }
	            }

	    } catch(HibernateException e) {
	        logAndThrowException("Error in getStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
	    }
	    return studyDetails;
	}
	
	public long countAllStudyDetails(StudyType studyType) throws MiddlewareQueryException {
	    try {
            StringBuilder sqlString = new StringBuilder()
            .append("SELECT COUNT(1) ")
            .append("FROM project p ")
            .append("   INNER JOIN projectprop ppNursery ON p.project_id = ppNursery.project_id ")
            .append("                   AND ppNursery.type_id = ").append(TermId.STUDY_TYPE.getId()).append(" ")
            .append("                   AND ppNursery.value = ").append(studyType.getId()).append(" ") // 10000 for Nursery
            .append("   LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id ")
            .append("                   AND ppObjective.type_id =  ").append(TermId.STUDY_OBJECTIVE.getId()).append(" ") // 8030
            .append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
            .append("                   AND ppStartDate.type_id =  ").append(TermId.START_DATE.getId()).append(" ") // 8050 
            .append("   LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id ")
            .append("                   AND ppEndDate.type_id =  ").append(TermId.END_DATE.getId()).append(" ") // 8060 
            .append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
            .append("                   AND ppPI.type_id =  ").append(TermId.PI_NAME.getId()).append(" ") // 8100 
            .append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
            .append("       LEFT JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
            .append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
            .append("           AND gpSiteName.type_id =  ").append(TermId.TRIAL_LOCATION.getId()).append(" ") // 8180 
            .append("WHERE NOT EXISTS (SELECT 1 FROM projectprop ppDeleted WHERE ppDeleted.type_id =  ").append(TermId.STUDY_STATUS.getId()).append(" ") // 8006
            .append("               AND ppDeleted.project_id = p.project_id AND ppDeleted.value =  ").append(TermId.DELETED_STUDY.getId()).append(") ") // 12990     
            ;
            
            Query query = getSession().createSQLQuery(sqlString.toString());

            return ((BigInteger) query.uniqueResult()).longValue();

        } catch(HibernateException e) {
            logAndThrowException("Error in countAllStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
        }
        return 0;
	    
	}
	
	public List<StudyDetails> getAllNurseryAndTrialStudyDetails() throws MiddlewareQueryException {
		return getAllNurseryAndTrialStudyDetails(0, -1);
	}
	
	@SuppressWarnings("unchecked")
    public List<StudyDetails> getAllNurseryAndTrialStudyDetails(int start, int numOfRows) throws MiddlewareQueryException {
	    List<StudyDetails> studyDetails = new ArrayList<StudyDetails>();
        try {
            
            StringBuilder sqlString = new StringBuilder()
            .append("SELECT DISTINCT p.name AS name, p.description AS title, ppObjective.value AS objective, ppStartDate.value AS startDate, ")
            .append(                        "ppEndDate.value AS endDate, ppPI.value AS piName, gpSiteName.value AS siteName, p.project_id AS id, ppStudy.value AS studyType ")
            .append("FROM project p ")
            .append("   INNER JOIN projectprop ppStudy ON p.project_id = ppStudy.project_id ")
            .append("                   AND ppStudy.type_id = ").append(TermId.STUDY_TYPE.getId()).append(" ")
            .append("                   AND ppStudy.value in (").append(TermId.NURSERY.getId()).append(",").append(TermId.TRIAL.getId()).append(") ") 
            .append("   LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id ")
            .append("                   AND ppObjective.type_id =  ").append(TermId.STUDY_OBJECTIVE.getId()).append(" ") // 8030
            .append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
            .append("                   AND ppStartDate.type_id =  ").append(TermId.START_DATE.getId()).append(" ") // 8050 
            .append("   LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id ")
            .append("                   AND ppEndDate.type_id =  ").append(TermId.END_DATE.getId()).append(" ") // 8060 
            .append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
            .append("                   AND ppPI.type_id =  ").append(TermId.PI_NAME.getId()).append(" ") // 8100 
            .append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
            .append("       INNER JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
            .append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
            .append("           AND gpSiteName.type_id =  ").append(TermId.TRIAL_LOCATION.getId()).append(" ") // 8180 
            .append("WHERE NOT EXISTS (SELECT 1 FROM projectprop ppDeleted WHERE ppDeleted.type_id =  ").append(TermId.STUDY_STATUS.getId()).append(" ") // 8006
            .append("               AND ppDeleted.project_id = p.project_id AND ppDeleted.value =  ").append(TermId.DELETED_STUDY.getId()).append(") ") // 12990
            .append("               ORDER BY p.name ") 
            ;
        
            Query query = getSession().createSQLQuery(sqlString.toString())
                        .addScalar("name")
                        .addScalar("title")
                        .addScalar("objective")
                        .addScalar("startDate")
                        .addScalar("endDate")
                        .addScalar("piName")
                        .addScalar("siteName")
                        .addScalar("id")
                        .addScalar("studyType")
                        ;
            setStartAndNumOfRows(query, start, numOfRows);

            List<Object[]> list =  query.list();
            
            if (list != null && list.size() > 0) {
                for (Object[] row : list){
                    String name = (String) row [0]; 
                    String title = (String) row [1]; 
                    String objective = (String) row [2]; 
                    String startDate = (String) row [3]; 
                    String endDate = (String) row [4]; 
                    String piName = (String) row [5]; 
                    String siteName = (String) row [6];
                    Integer id = (Integer) row[7];
                    String studyTypeId = (String) row[8];
                    
                    studyDetails.add(new StudyDetails( id, name, title, objective, startDate, endDate, 
                    		TermId.NURSERY.getId()==Integer.parseInt(studyTypeId)?StudyType.N:StudyType.T, piName, siteName));
                }
            }

        } catch(HibernateException e) {
            logAndThrowException("Error in getAllNurseryAndTrialStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
        }
        return studyDetails;
	    
	}
	
	public long countAllNurseryAndTrialStudyDetails() throws MiddlewareQueryException {
	    try {
            
            StringBuilder sqlString = new StringBuilder()
            .append("SELECT COUNT(1) ")
            .append("FROM project p ")
            .append("   INNER JOIN projectprop ppStudy ON p.project_id = ppStudy.project_id ")
            .append("                   AND ppStudy.type_id = ").append(TermId.STUDY_TYPE.getId()).append(" ")
            .append("                   AND ppStudy.value in (").append(TermId.NURSERY.getId()).append(",").append(TermId.TRIAL.getId()).append(") ") 
            .append("   LEFT JOIN projectprop ppObjective ON p.project_id = ppObjective.project_id ")
            .append("                   AND ppObjective.type_id =  ").append(TermId.STUDY_OBJECTIVE.getId()).append(" ") // 8030
            .append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
            .append("                   AND ppStartDate.type_id =  ").append(TermId.START_DATE.getId()).append(" ") // 8050 
            .append("   LEFT JOIN projectprop ppEndDate ON p.project_id = ppEndDate.project_id ")
            .append("                   AND ppEndDate.type_id =  ").append(TermId.END_DATE.getId()).append(" ") // 8060 
            .append("   LEFT JOIN projectprop ppPI ON p.project_id = ppPI.project_id ")
            .append("                   AND ppPI.type_id =  ").append(TermId.PI_NAME.getId()).append(" ") // 8100 
            .append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
            .append("       INNER JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
            .append("       LEFT JOIN nd_geolocationprop gpSiteName ON e.nd_geolocation_id = gpSiteName.nd_geolocation_id ")
            .append("           AND gpSiteName.type_id =  ").append(TermId.TRIAL_LOCATION.getId()).append(" ") // 8180 
            .append("WHERE NOT EXISTS (SELECT 1 FROM projectprop ppDeleted WHERE ppDeleted.type_id =  ").append(TermId.STUDY_STATUS.getId()).append(" ") // 8006
            .append("               AND ppDeleted.project_id = p.project_id AND ppDeleted.value =  ").append(TermId.DELETED_STUDY.getId()).append(") ") // 12990
            ;
            
            Query query = getSession().createSQLQuery(sqlString.toString());

            return ((BigInteger) query.uniqueResult()).longValue();

        } catch(HibernateException e) {
            logAndThrowException("Error in countAllNurseryAndTrialStudyDetails() query in DmsProjectDao: " + e.getMessage(), e);
        }
        return 0;
	    
	}
	
	/**
	 * Retrieves all the study details 
	 * @return
	 * @throws MiddlewareQueryException
	 */
	@SuppressWarnings("unchecked")
	public List<StudyNode> getAllNurseryAndTrialStudyNodes() throws MiddlewareQueryException {
	    List<StudyNode> studyNodes = new ArrayList<StudyNode>();
        try {
            /*
                SELECT DISTINCT p.project_id AS id
                        , p.name AS name
                        , p.description AS description
                        , ppStartDate.value AS startDate
                        , ppStudyType.value AS studyType
                        , gpSeason.value AS season
                FROM project p 
                INNER JOIN projectprop ppStudyType ON p.project_id = ppStudyType.project_id 
                               AND ppStudyType.type_id = 8070 -- TermId.STUDY_TYPE.getIdc 
                               AND (ppStudyType.value = 10000 -- TermId.NURSERY.getId()  --  10000 for Nursery
                               OR ppStudyType.value = 10010) -- TermId.TRIAL.getId()  --  10010 for Trial
                LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id 
                               AND ppStartDate.type_id =  8050 -- TermId.START_DATE.getId()  --  8050 
                LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id 
                INNER JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id 
                LEFT JOIN nd_geolocationprop gpSeason ON e.nd_geolocation_id = gpSeason.nd_geolocation_id 
                       AND gpSeason.type_id =  8371  --  2452 
                WHERE NOT EXISTS (SELECT 1 FROM projectprop ppDeleted WHERE ppDeleted.type_id = 8006 --  TermId.STUDY_STATUS.getId(  --  8006
                           AND ppDeleted.project_id = p.project_id AND ppDeleted.value =  12990 ) -- TermId.DELETED_STUDY.getId(  --  12990     

             */
            StringBuilder sqlString = new StringBuilder()
            .append("SELECT DISTINCT p.project_id AS id ")
            .append("        , p.name AS name ")
            .append("        , p.description AS description ")
            .append("        , ppStartDate.value AS startDate ")
            .append("        , ppStudyType.value AS studyType ")
            .append("        , gpSeason.value AS season ")
            .append("FROM project p  ")
            .append("   INNER JOIN projectprop ppStudyType ON p.project_id = ppStudyType.project_id ")
            .append("                   AND ppStudyType.type_id = ").append(TermId.STUDY_TYPE.getId()).append(" ") // 8070
            .append("                   AND (ppStudyType.value = ").append(TermId.NURSERY.getId()).append(" ") // 10000 for Nursery
            .append("                   OR ppStudyType.value = ").append(TermId.TRIAL.getId()).append(") ") // 10010 for Trial
            .append("   LEFT JOIN projectprop ppStartDate ON p.project_id = ppStartDate.project_id ")
            .append("                   AND ppStartDate.type_id =  ").append(TermId.START_DATE.getId()).append(" ") // 8050 
            .append("   LEFT JOIN nd_experiment_project ep ON p.project_id = ep.project_id ")
            .append("   INNER JOIN nd_experiment e ON ep.nd_experiment_id = e.nd_experiment_id ")
            .append("   LEFT JOIN nd_geolocationprop gpSeason ON e.nd_geolocation_id = gpSeason.nd_geolocation_id ")
            .append("           AND gpSeason.type_id =  ").append(TermId.SEASON_VAR.getId()).append(" ") // 8371 
            .append("WHERE NOT EXISTS (SELECT 1 FROM projectprop ppDeleted WHERE ppDeleted.type_id =  ").append(TermId.STUDY_STATUS.getId()).append(" ") // 8006
            .append("               AND ppDeleted.project_id = p.project_id AND ppDeleted.value =  ").append(TermId.DELETED_STUDY.getId()).append(") ") // 12990     
            ;
        
            Query query = getSession().createSQLQuery(sqlString.toString())
                        .addScalar("id")
                        .addScalar("name")
                        .addScalar("description")
                        .addScalar("startDate")
                        .addScalar("studyType")
                        .addScalar("season")
                        ;

            List<Object[]> list =  query.list();
            
            if (list != null && list.size() > 0) {
                for (Object[] row : list){
                    Integer id = (Integer) row[0];
                    String name = (String) row [1]; 
                    String description = (String) row [2]; 
                    String startDate = (String) row [3]; 
                    String studyTypeStr = (String) row [4];
                    String seasonStr = (String) row[5];
                    
                    StudyType studyType = StudyType.N; 
                    if (Integer.parseInt(studyTypeStr) != TermId.NURSERY.getId()){
                    	studyType = StudyType.T;
                    }
                    
                    Season season = Season.getSeason(seasonStr); 
                    studyNodes.add(new StudyNode(id, name, description, startDate, studyType, season));
                    
                }
            }

        } catch(HibernateException e) {
            logAndThrowException("Error in getAllStudyNodes() query in DmsProjectDao: " + e.getMessage(), e);
        }
        Collections.sort(studyNodes);
        
        return studyNodes;
	    
	}
	
	@SuppressWarnings("rawtypes")
	public boolean checkIfProjectNameIsExisting(String name) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("name", name));
			
			List list = criteria.list();
			if(list!=null && !list.isEmpty()) {
				return true;
			}
			
		} catch (HibernateException e) {
			logAndThrowException("Error in checkIfProjectNameIsExisting=" + name + " query on DmsProjectDao: " + e.getMessage(), e);
		}
		
		return false;
	}
	
    public long countByVariable(int variableId) throws MiddlewareQueryException {
        try {
            SQLQuery query = getSession().createSQLQuery(COUNT_PROJECTS_WITH_VARIABLE);
            query.setParameter("variableId", variableId);
            
            return ((BigInteger) query.uniqueResult()).longValue();
            
        } catch(HibernateException e) {
            logAndThrowException("Error at countByVariable=" + variableId + ", " + variableId + " query at DmsProjectDao: " + e.getMessage(), e);
        }
        return 0;
    }

    public List<FolderReference> getAllFolders() throws MiddlewareQueryException {
        List<FolderReference> folders = new ArrayList<FolderReference>();
        try {
            SQLQuery query = getSession().createSQLQuery(GET_ALL_FOLDERS);
            List<Object[]> result = query.list();
            if (result != null && !result.isEmpty()) {
                for (Object[] row : result) {
                    folders.add(new FolderReference((Integer) row[0], (Integer) row[1], (String) row[2], (String) row[3]));
                }
            }
            
        } catch(HibernateException e) {
            logAndThrowException("Error at getAllFolders, query at DmsProjectDao: " + e.getMessage(), e);
        }
        return folders;
    }

	
	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctProjectNames() throws MiddlewareQueryException {
		List<ValueReference> results = new ArrayList<ValueReference>();
		try {
			String sql = "SELECT DISTINCT name FROM project ";
			SQLQuery query = getSession().createSQLQuery(sql);
			List<String> list = query.list();
			int index = 1;
			if (list != null && !list.isEmpty()) {
				for (String row : list) {
					results.add(new ValueReference(index++, row));
				}
			}
		} catch(HibernateException e) {
			logAndThrowException("Error with getDistinctProjectNames() query from Project " + e.getMessage(), e);
		}
		return results;
	}

	@SuppressWarnings("unchecked")
	public List<ValueReference> getDistinctProjectDescriptions() throws MiddlewareQueryException {
		List<ValueReference> results = new ArrayList<ValueReference>();
		try {
			String sql = "SELECT DISTINCT description FROM project ";
			SQLQuery query = getSession().createSQLQuery(sql);
			List<String> list = query.list();
			int index = 1;
			if (list != null && !list.isEmpty()) {
				for (String row : list) {
					results.add(new ValueReference(index++, row));
				}
			}
		} catch(HibernateException e) {
			logAndThrowException("Error with getDistinctProjectDescription() query from Project " + e.getMessage(), e);
		}
		return results;
	}
}
