package org.generationcp.middleware.v2.manager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DataManager;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.pojos.Study;
import org.generationcp.middleware.v2.dao.DmsProjectDao;
import org.generationcp.middleware.v2.dao.ProjectPropertyDao;
import org.generationcp.middleware.v2.dao.ProjectRelationshipDao;
import org.generationcp.middleware.v2.domain.AbstractNode;
import org.generationcp.middleware.v2.domain.CVTermId;
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
import org.generationcp.middleware.v2.factory.ProjectRelationshipFactory;
import org.generationcp.middleware.v2.factory.StudyFactory;
import org.generationcp.middleware.v2.manager.api.StudyDataManager;
import org.generationcp.middleware.v2.pojos.DmsProject;
import org.generationcp.middleware.v2.pojos.ProjectProperty;
import org.generationcp.middleware.v2.pojos.ProjectRelationship;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StudyDataManagerImpl extends DataManager implements StudyDataManager {
	
    private static final Logger LOG = LoggerFactory.getLogger(StudyDataManagerImpl.class);

	private DmsProjectDao dmsProjectDao;

	private ProjectPropertyDao projectPropertyDao;
	
	private ProjectRelationshipDao projectRelationshipDao;
	
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
	
	private ProjectRelationshipDao getProjectRelationshipDao() {
		if (projectRelationshipDao == null) {
			projectRelationshipDao = new ProjectRelationshipDao();
		}
		projectRelationshipDao.setSession(getActiveSession());
		return projectRelationshipDao;
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
		List<DmsProject> projects = getProjectSearcher().searchByFilter(filter);
		return getStudyNodeBuilder().build(projects);
	}
	
	@Override
	public Set<StudyDetails> searchStudiesByGid(Integer gid) throws MiddlewareQueryException {
		Set<StudyDetails> studies = new HashSet<StudyDetails>();
		List<DmsProject> projects = getProjectSearcher().searchStudiesByFactor(CVTermId.GID.getId(), gid.toString());
		for (DmsProject project : projects)	 {
			studies.add(getStudyFactory().createStudyDetails(project));
		}
		return studies;
	}

	@Override
    public Study addStudy(Study study) throws MiddlewareQueryException{
        requireLocalDatabaseInstance();
        DmsProject parent = getDmsProjectDao().getById(study.getHierarchy()); 
		try {
			study.setId(saveStudy(study, parent));
        } catch (Exception e) {
            logAndThrowException("Error encountered with addStudy(study=" + study + "): " + e.getMessage(), e, LOG);
		}
		return study;
    }
	
	@Override
    public StudyDetails addStudyDetails(StudyDetails studyDetails) throws MiddlewareQueryException{
        requireLocalDatabaseInstance();
		try {
			studyDetails.setId(saveStudy(new Study(studyDetails), null));
        } catch (Exception e) {
          logAndThrowException("Error encountered with addStudyDetails(studyDetails=" + studyDetails + "): " + e.getMessage(), e, LOG);
		}
		return studyDetails;	
    }
	
	private Integer saveStudy(Study study, DmsProject parent) throws Exception{
        Session session = getCurrentSessionForLocal();
        Transaction trans = null;
        Integer id = null;

        try {
            trans = session.beginTransaction();
            
            // Save project
            DmsProjectDao projectDao = getDmsProjectDao();
            DmsProject project = ProjectFactory.getInstance().createProject(study);
            Integer generatedId = projectDao.getNegativeId("projectId");
            project.setProjectId(generatedId);
            DmsProject savedProject = projectDao.save(project);
            
            // Save project properties
            List<ProjectProperty> properties = ProjectPropertyFactory.getInstance().
            										createProjectProperties(study, project);
            ProjectPropertyDao projectPropertyDao = getProjectPropertyDao();
            
            for (ProjectProperty property : properties){
                generatedId = projectPropertyDao.getNegativeId("projectPropertyId");
                property.setProjectPropertyId(generatedId);
                 property.setProject(savedProject);
                 projectPropertyDao.save(property);
            }
            savedProject.setProperties(properties);
           
            // Save the relationship to parent and add relationship is_study
            if (parent == null){
            	parent = projectDao.getById(DmsProject.SYSTEM_FOLDER_ID); // Make the new study a root study 
            }
            List<ProjectRelationship> relationships = ProjectRelationshipFactory.getInstance().
            											createProjectRelationship(study, parent);
            ProjectRelationshipDao projectRelationshipDao = getProjectRelationshipDao();
            
            for (ProjectRelationship relationship : relationships){
                generatedId = projectRelationshipDao.getNegativeId("projectRelationshipId");
                relationship.setProjectRelationshipId(generatedId);
                relationship.setObjectProject(savedProject);
                relationship.setSubjectProject(parent);
                projectRelationshipDao.save(relationship);
            }
            savedProject.setRelatedTos(relationships);
            
            id = savedProject.getProjectId();

            trans.commit();
            
        } catch (Exception e) {
            rollbackTransaction(trans);
            throw e;
        }
        return id;
		
	}

}
