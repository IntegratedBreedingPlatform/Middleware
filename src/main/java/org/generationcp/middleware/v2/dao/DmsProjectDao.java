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
package org.generationcp.middleware.v2.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.v2.domain.AbstractNode;
import org.generationcp.middleware.v2.domain.CVTermId;
import org.generationcp.middleware.v2.domain.DatasetNode;
import org.generationcp.middleware.v2.domain.FolderNode;
import org.generationcp.middleware.v2.domain.StudyNode;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 * 
 * @author Darla Ani, Joyce Avestro
 *
 */

public class DmsProjectDao extends GenericDAO<DmsProject, Integer> {

	public static final String GET_CHILDREN_OF_FOLDER =		
			"SELECT  DISTINCT subject.project_id, subject.name, IFNULL(is_study.type_id, 0) AS is_study "
			+ "FROM    project subject "
			+ "        INNER JOIN project_relationship pr ON subject.project_id = pr.subject_project_id "
			+ "        LEFT JOIN project_relationship is_study ON subject.project_id = is_study.subject_project_id "
			+ "        					AND is_study.type_id = " + CVTermId.IS_STUDY.getId() + " "
			+ "WHERE   pr.type_id = " + CVTermId.HAS_PARENT_FOLDER.getId() + " "
			+ "        and pr.object_project_id = :folderId "
			+ "ORDER BY name "
			;

	@SuppressWarnings("unchecked")
	public List<FolderNode> getRootFolders() throws MiddlewareQueryException{
		
		List<FolderNode> folderList = new ArrayList<FolderNode>();
		
		/* SELECT DISTINCT p.projectId, p.name
		 * 	FROM DmsProject p 
		 * 		JOIN p.relatedTos pr 
		 * WHERE pr.typeId = CVTermId.HAS_PARENT_FOLDER.getId() 
		 * 		 AND pr.objectProject.projectId = " + DmsProject.SYSTEM_FOLDER_ID  
		 * ORDER BY name 
		 */
		
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", CVTermId.HAS_PARENT_FOLDER.getId()));
			criteria.add(Restrictions.eq("pr.objectProject.projectId", DmsProject.SYSTEM_FOLDER_ID));
			
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("projectId"));
			projectionList.add(Projections.property("name"));
			criteria.setProjection(projectionList);
			
			criteria.addOrder(Order.asc("projectId"));
			
			List<Object[]> list = criteria.list();
			if (list != null && list.size() > 0) {
				for (Object[] row : list){
					Integer id = (Integer)row[0]; //project.id
					String name = (String) row [1]; //project.name
					folderList.add(new FolderNode(id, name));
				}
			}
		} catch (HibernateException e) {
			logAndThrowException("Error with getRootFolders query from Project: " + e.getMessage(), e);
		}	
		
		return folderList;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<AbstractNode> getChildrenOfFolder(Integer folderId) throws MiddlewareQueryException{
		
		List<AbstractNode> childrenNodes = new ArrayList<AbstractNode>();
		
		try {
			Query query = getSession().createSQLQuery(GET_CHILDREN_OF_FOLDER);
			query.setParameter("folderId", folderId);
			List<Object[]> list =  query.list();
			
			for (Object[] row : list){
				Integer id = (Integer) row[0]; //project.id
				String name = (String) row [1]; //project.name
				Integer isStudy = ((BigInteger) row[2]).intValue(); //non-zero if a study, else a folder
				
				if (isStudy > 0){
					childrenNodes.add(new StudyNode(id, name));
				} else {
					childrenNodes.add(new FolderNode(id, name));
				}
			}
			
		} catch (HibernateException e) {
			logAndThrowException("Error with getChildrenOfFolder query from Project: " + e.getMessage(), e);
		}
		
		return childrenNodes;
		
	}
	
	

	@SuppressWarnings("unchecked")
	public List<DatasetNode> getDatasetNodesByStudyId(Integer studyId) throws MiddlewareQueryException{
		
		List<DatasetNode> datasetNodes = new ArrayList<DatasetNode>();
		
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
			criteria.add(Restrictions.eq("pr.typeId", CVTermId.BELONGS_TO_STUDY.getId()));
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
				datasetNodes.add(new DatasetNode(id, name));
			}
			
		} catch (HibernateException e) {
			logAndThrowException("Error with getDatasetNodesByStudyId query from Project: " + e.getMessage(), e);
		}
		
		return datasetNodes;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<StudyNode> getStudiesByName(String name) throws MiddlewareQueryException {
		List<StudyNode> studyNodes = new ArrayList<StudyNode>();
		
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.add(Restrictions.eq("name", name));
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", CVTermId.IS_STUDY.getId()));
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("projectId"));
			projectionList.add(Projections.property("name"));
			criteria.setProjection(projectionList);
			
			List<Object[]> nodes = criteria.list();
			if (nodes != null && nodes.size() > 0) {
				for (Object[] node : nodes) {
					studyNodes.add(new StudyNode((Integer) node[0], (String) node[1]));
				}
			}
		} catch (HibernateException e) {
			logAndThrowException("Error in getStudiesByName=" + name + " query on DmsProjectDao: " + e.getMessage(), e);
		}
		
		return studyNodes;
	}
	
	public List<StudyNode> getStudiesByUserIds(Collection<Integer> userIds) throws MiddlewareQueryException {
		List<Object> userIdStrings = new ArrayList<Object>();
		if (userIds != null && userIds.size() > 0) {
			for (Integer userId : userIds) {
				userIdStrings.add(userId.toString());
			}
		}
		return getStudiesByStudyProperty(CVTermId.STUDY_UID.getId(), Restrictions.in("p.value", userIdStrings));
	}
	
	public List<StudyNode> getStudiesByStartDate(Integer startDate) throws MiddlewareQueryException {
		return getStudiesByStudyProperty(CVTermId.START_DATE.getId(), Restrictions.eq("p.value", startDate.toString()));
	}

	@SuppressWarnings("unchecked")
	private List<StudyNode> getStudiesByStudyProperty(Integer studyPropertyId, Criterion valueExpression) throws MiddlewareQueryException {
		List<StudyNode> studyNodes = new ArrayList<StudyNode>();
		
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.createAlias("properties", "p");
			criteria.add(Restrictions.eq("p.typeId", studyPropertyId));
			criteria.add(valueExpression);
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", CVTermId.IS_STUDY.getId()));
			ProjectionList projectionList = Projections.projectionList();
			projectionList.add(Projections.property("projectId"));
			projectionList.add(Projections.property("name"));
			criteria.setProjection(projectionList);
			
			List<Object[]> nodes = criteria.list();
			if (nodes != null && nodes.size() > 0) {
				for (Object[] node : nodes) {
					studyNodes.add(new StudyNode((Integer) node[0], (String) node[1]));
				}
			}
		} catch (HibernateException e) {
			e.printStackTrace();
			logAndThrowException("Error in getStudiesByStudyProperty with " + valueExpression + " for property " + studyPropertyId 
					+ " in DmsProjectDao: " + e.getMessage(), e);
		}
		return studyNodes;
	}
	
	@SuppressWarnings("unchecked")
	public List<StudyNode> getStudiesByIds(Collection<Integer> projectIds) throws MiddlewareQueryException {
		List<StudyNode> studyNodes = new ArrayList<StudyNode>();
		try {
			if (projectIds != null && projectIds.size() > 0) {
				Criteria criteria = getSession().createCriteria(getPersistentClass());
				criteria.add(Restrictions.in("projectId", projectIds));
				criteria.createAlias("relatedTos", "pr");
				criteria.add(Restrictions.eq("pr.typeId", CVTermId.IS_STUDY.getId()));
				ProjectionList projectionList = Projections.projectionList();
				projectionList.add(Projections.property("projectId"));
				projectionList.add(Projections.property("name"));
				criteria.setProjection(projectionList);
				
				List<Object[]> nodes = criteria.list();
				if (nodes != null && nodes.size() > 0) {
					for (Object[] node : nodes) {
						studyNodes.add(new StudyNode((Integer) node[0], (String) node[1]));
					}
				}
			}
		} catch (HibernateException e) {
			logAndThrowException("Error in getStudiesByIds= " + projectIds + " query in DmsProjectDao: " + e.getMessage(), e);
		}
		return studyNodes;
	}
	
	@SuppressWarnings("unchecked")
	public List<DmsProject> getDatasetsByStudy(Integer studyId) throws MiddlewareQueryException {
		try {
			Criteria criteria = getSession().createCriteria(getPersistentClass());
			criteria.createAlias("relatedTos", "pr");
			criteria.add(Restrictions.eq("pr.typeId", CVTermId.BELONGS_TO_STUDY.getId()));
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
			criteria.add(Restrictions.eq("pr.typeId", CVTermId.BELONGS_TO_STUDY.getId()));
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
}
