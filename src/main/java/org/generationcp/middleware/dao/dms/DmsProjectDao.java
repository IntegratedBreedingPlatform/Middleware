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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.domain.dms.DatasetReference;
import org.generationcp.middleware.domain.dms.FolderReference;
import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
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
			+ "ORDER BY name "
			;

	
	private static final String GET_STUDIES_OF_FOLDER =
			"SELECT  DISTINCT pr.subject_project_id "
			+ "FROM    project_relationship pr, project p "
			+ "WHERE   pr.type_id = "  + TermId.IS_STUDY.getId() + " "
			+ "        AND pr.subject_project_id = p.project_id "
			+ "        AND pr.object_project_id = :folderId "
			+ "ORDER BY p.name "
			;

	@SuppressWarnings("unchecked")
	public List<FolderReference> getRootFolders() throws MiddlewareQueryException{
		
		List<FolderReference> folderList = new ArrayList<FolderReference>();
		
		/* SELECT DISTINCT p.projectId, p.name, p.description
		 * 	FROM DmsProject p 
		 * 		JOIN p.relatedTos pr 
		 * WHERE pr.typeId = CVTermId.HAS_PARENT_FOLDER.getId() 
		 * 		 AND pr.objectProject.projectId = " + DmsProject.SYSTEM_FOLDER_ID  
		 * ORDER BY name 
		 */
		
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", TermId.HAS_PARENT_FOLDER.getId()));
			criteria.add(Restrictions.eq("pr.objectProject.projectId", DmsProject.SYSTEM_FOLDER_ID));
			
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("projectId"));
			projectionList.add(Projections.property("name"));
			projectionList.add(Projections.property("description"));
			criteria.setProjection(projectionList);
			
			criteria.addOrder(Order.asc("projectId"));
			
			List<Object[]> list = criteria.list();
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
			SELECT DISTINCT p.projectId, p.name, pr.objectProject.projectId 
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
			projectionList.add(Projections.property("pr.objectProject.projectId"));
			criteria.setProjection(projectionList);
			
			criteria.addOrder(Order.asc("name"));

			List<Object[]> list =  criteria.list();
			
			for (Object[] row : list){
				Integer id = (Integer) row[0]; //project.id
				String name = (String) row [1]; //project.name
				datasetReferences.add(new DatasetReference(id, name));
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
			e.printStackTrace();
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
			logAndThrowException("Error getByFactor=" + factorId + " at DmsProjectDao: " + e.getMessage());
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

}
