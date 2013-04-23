package org.generationcp.middleware.v2.manager;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.v2.dao.DmsProjectDao;
import org.generationcp.middleware.v2.dao.ProjectPropertyDao;
import org.generationcp.middleware.v2.domain.AbstractNode;
import org.generationcp.middleware.v2.domain.DataSet;
import org.generationcp.middleware.v2.domain.DatasetNode;
import org.generationcp.middleware.v2.domain.FactorDetails;
import org.generationcp.middleware.v2.domain.FolderNode;
import org.generationcp.middleware.v2.domain.ObservationDetails;
import org.generationcp.middleware.v2.domain.StudyDetails;
import org.generationcp.middleware.v2.domain.StudyNode;
import org.generationcp.middleware.v2.domain.StudyQueryFilter;
import org.generationcp.middleware.v2.factory.ProjectFactory;
import org.generationcp.middleware.v2.factory.ProjectPropertyFactory;
import org.generationcp.middleware.v2.factory.StudyFactory;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.generationcp.middleware.v2.pojos.DmsDataset;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ProjectProperty;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyDataManagerImpl extends DataManager implements StudyDataManager {
	
    private static final Logger LOG = LoggerFactory.getLogger(StudyDataManagerImpl.class);

	private DmsProjectDao dmsProjectDao;

	private ProjectPropertyDao projectPropertyDao;
	
	
	public StudyDataManagerImpl() { 		
	}

	public StudyDataManagerImpl(HibernateSessionProvider sessionProviderForLocal,
			                    HibernateSessionProvider sessionProviderForCentral) {
		super(sessionProviderForLocal, sessionProviderForCentral);
	}

	public StudyDataManagerImpl(Session sessionForLocal, Session sessionForCentral) {
		super(sessionForLocal, sessionForCentral);
	}

	private DmsProjectDao getDmsProjectDao() {
		if (dmsProjectDao == null) {
			dmsProjectDao = new DmsProjectDao();
		}
		dmsProjectDao.setSession(getActiveSession());
		return dmsProjectDao;
	}

	private ProjectPropertyDao getProjectPropertyDao() {
		if (projectPropertyDao == null) {
			projectPropertyDao = new ProjectPropertyDao();
		}
		projectPropertyDao.setSession(getActiveSession());
		return projectPropertyDao;
	}
	
	
	private StudyFactory getStudyFactory() {
		return StudyFactory.getInstance();
	}
	
	@Override
	public StudyDetails getStudyDetails(Integer studyId) throws MiddlewareQueryException {
		if (setWorkingDatabase(studyId)) {
			DmsProject project = getDmsProjectDao().getById(studyId);
			if (project != null) {
				return getStudyFactory().createStudyDetails(project);
			}
		}
		return null;
	}
	
	@Override
	public List<FolderNode> getRootFolders(Database instance) throws MiddlewareQueryException{
		if (setWorkingDatabase(instance, getDmsProjectDao())){
			return getDmsProjectDao().getRootFolders();
		}
		return null;
	}
	
	@Override
	public List<AbstractNode> getChildrenOfFolder(Integer folderId, Database instance) throws MiddlewareQueryException{
		if (setWorkingDatabase(instance, getDmsProjectDao())){
			return getDmsProjectDao().getChildrenOfFolder(folderId);
		}
		return null;
	}
	
	@Override
	public List<DatasetNode> getDatasetNodesByStudyId(Integer studyId, Database instance) throws MiddlewareQueryException{
		if (setWorkingDatabase(instance, getDmsProjectDao())){
			return getDmsProjectDao().getDatasetNodesByStudyId(studyId);
		}
		return null;
	}

	@Override
	public DataSet getDataSet(int dataSetId) throws MiddlewareQueryException {
		return getDataSetBuilder().build(dataSetId);
	}

	@Override
	public List<FactorDetails> getFactors(Integer projectId) throws MiddlewareQueryException {
		return getFactorDetailsBuilder().build(projectId);
	}
	
	@Override
	public List<ObservationDetails> getObservations(Integer projectId) throws MiddlewareQueryException {
		return getObservationDetailsBuilder().build(projectId);
	}
	
	@Override
	public List<StudyNode> searchStudies(StudyQueryFilter filter) throws MiddlewareQueryException {
		return getStudyNodeBuilder().build(filter);
	}

	@Override
    public Study addStudy(Study study) throws MiddlewareQueryException{
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
            trans = session.beginTransaction();
            
            // Save project
            DmsProjectDao projectDao = getDmsProjectDao();
            DmsProject project = ProjectFactory.getInstance().createProject(study);

            Integer generatedId = projectDao.getNegativeId("projectId");
            project.setProjectId(generatedId);
            
            // Save project properties
            List<ProjectProperty> properties = ProjectPropertyFactory.getInstance().
            										createProjectProperties(study, project);
            ProjectPropertyDao projectPropertyDao = getProjectPropertyDao();
            
            generatedId = projectPropertyDao.getNegativeId("projectPropertyId") +1;
            for (ProjectProperty property : properties){
                 property.setProjectPropertyId(generatedId--);
 //               projectPropertyDao.save(property);
            }
            project.setProperties(properties);
            DmsProject savedProject = projectDao.save(project);
           
            // Save project relationships
            
            // Get the parent from the study
 /*           DmsProject parent = projectDao.getById(study.getHierarchy()); 
            List<ProjectRelationship> relationships = 
            		ProjectRelationshipFactory.getInstance().
            			createProjectRelationship(study, parent);
            ProjectRelationshipDao projectRelationshipDao = getProjectRelationshipDao();
            
            for (ProjectRelationship relationship : relationships){
                generatedId = projectRelationshipDao.getNegativeId("projectRelationshipId");
                relationship.setProjectRelationshipId(generatedId);
                projectRelationshipDao.save(relationship);
            }
 */           
            study.setId( savedProject.getProjectId());

            trans.commit();
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with addStudy(study=" + study + "): " + e.getMessage(), e, LOG);
        }
        return study;
    	
    }

	@Override
    public DmsDataset addDmsDataset(DmsDataset dataset) throws MiddlewareQueryException{
        requireLocalDatabaseInstance();
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;

        try {
        	
        	// Save Project Entry
            trans = session.beginTransaction();
            DmsProjectDao dao = getDmsProjectDao();
            DmsProject project = new DmsProject(dataset);

            Integer generatedId = dao.getNegativeId("projectId");

            project.setProjectId(generatedId);
            DmsProject savedProject = dao.save(project);
            
            //TODO Save factors
            
            //TODO Save variates
            
            //TODO Save row data
            
            dataset.setProjectId(savedProject.getProjectId());

            trans.commit();
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            logAndThrowException("Error encountered with addDmsDataset(dataset=" + dataset + "): " + e.getMessage(), e, LOG);
        }
        return dataset;
    }

}
