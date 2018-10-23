package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.dao.dms.DmsProjectDao;
import org.generationcp.middleware.domain.dms.DMSVariableType;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.dms.Study;
import org.generationcp.middleware.domain.dms.VariableTypeList;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.OntologyDataManagerImpl;
import org.generationcp.middleware.manager.api.OntologyDataManager;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clarysabel on 10/22/18.
 */
@Transactional
public class DatasetServiceImpl implements DatasetService {

	private static final Logger LOG = LoggerFactory.getLogger(DatasetServiceImpl.class);

	private DaoFactory daoFactory;

	private DmsProjectDao dmsProjectDao;

	private OntologyVariableDataManager ontologyVariableDataManager;

	DatasetServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public DatasetServiceImpl(HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		dmsProjectDao = daoFactory.getDmsProjectDao();
		ontologyVariableDataManager = new OntologyVariableDataManagerImpl(sessionProvider);
	}

	public Integer generateSubObservationDataset (final Integer studyId, final String datasetName, final Integer datasetTypeId, final Integer[] instanceIds,
			final Integer observationUnitVariableId, final Integer numberOfSubObservationUnits) {

		final DmsProject study = dmsProjectDao.getById(studyId);

		// Study exists

		// Study is not locked

		// Instances in the list belongs to the study

		// observationUnitVariableId has type = ObservationUnit

		// datasetTypeId matches sub-observation unit dataset types

		// numberOfSubObservationUnits <= max allowed

		final DmsProject datasetProject = new DmsProject();
		datasetProject.setName(datasetName);
		datasetProject.setDescription(datasetName);
		datasetProject.setProgramUUID(study.getProgramUUID());
		datasetProject.setDeleted(false);
		datasetProject.setLocked(false);

		datasetProject.setProperties(buildDefaultDatasetProperties(study, datasetProject, datasetName, datasetTypeId));
		datasetProject.addProperty(buildDatasetProperty(datasetProject, study.getProgramUUID(), VariableType.OBSERVATION_UNIT.getId(), observationUnitVariableId, null, null, 4));
		datasetProject.setRelatedTos(this.buildProjectRelationship(study, datasetProject));
		dmsProjectDao.save(datasetProject);

		// find plot dataset nd_experiments
		// iterate and create new sub-observation units

		// save

		return null;
	}


	private List<ProjectProperty> buildDefaultDatasetProperties (final DmsProject study, final DmsProject dmsProject, final String datasetName, final Integer datasetTypeId) {
		final List<ProjectProperty> projectProperties = new ArrayList<>();
		final ProjectProperty datasetProperty = buildDatasetProperty(dmsProject, study.getProgramUUID(), VariableType.STUDY_DETAIL.getId(), TermId.DATASET_NAME.getId(), datasetName, null, 1);
		final ProjectProperty datasetTitleProperty = buildDatasetProperty(dmsProject, study.getProgramUUID(), VariableType.STUDY_DETAIL.getId(), TermId.DATASET_TITLE.getId(), null, null, 2);
		final ProjectProperty datasetTypeProperty = buildDatasetProperty(dmsProject, study.getProgramUUID(), VariableType.STUDY_DETAIL.getId(), TermId.DATASET_TYPE.getId(), String.valueOf(datasetTypeId), null, 3);
		projectProperties.add(datasetProperty);
		projectProperties.add(datasetTitleProperty);
		projectProperties.add(datasetTypeProperty);
		return projectProperties;
	}

	private ProjectProperty buildDatasetProperty (final DmsProject dmsProject, final String programUUID, final Integer typeId, final Integer variableId, final String value, final String alias, final Integer rank) {
		// Validate that typeId is in set of types supported by the variable
		final Variable variable = ontologyVariableDataManager.getVariable(programUUID, variableId, false, false);
	    final ProjectProperty projectProperty = new ProjectProperty(dmsProject, typeId, value, rank, variableId, (alias == null) ? variable.getName() : alias);
		return projectProperty;
	}


	private List<ProjectRelationship> buildProjectRelationship(DmsProject studyProject, DmsProject datasetProject)
			throws MiddlewareQueryException {
		ProjectRelationship relationship = new ProjectRelationship();
		relationship.setSubjectProject(datasetProject);
		relationship.setObjectProject(studyProject);
		relationship.setTypeId(TermId.BELONGS_TO_STUDY.getId());

		List<ProjectRelationship> relationships = new ArrayList<ProjectRelationship>();
		relationships.add(relationship);

		return relationships;
	}

}
