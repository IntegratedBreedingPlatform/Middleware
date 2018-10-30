package org.generationcp.middleware.service.impl.dataset;

import org.apache.commons.lang.RandomStringUtils;
import org.generationcp.middleware.domain.dms.DataSetType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.WorkbenchDataManager;
import org.generationcp.middleware.manager.ontology.OntologyVariableDataManagerImpl;
import org.generationcp.middleware.manager.ontology.api.OntologyVariableDataManager;
import org.generationcp.middleware.pojos.dms.DmsProject;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ProjectProperty;
import org.generationcp.middleware.pojos.dms.ProjectRelationship;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	private OntologyVariableDataManager ontologyVariableDataManager;

	@Autowired
	private WorkbenchDataManager workbenchDataManager;

	DatasetServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public DatasetServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		ontologyVariableDataManager = new OntologyVariableDataManagerImpl(sessionProvider);
	}

	@Override
	public long countPhenotypes(final Integer datasetId, final List<Integer> traitIds) {
		return this.daoFactory.getPhenotypeDAO().countPhenotypesForDataset(datasetId, traitIds);
	}

	@Override
	public Integer generateSubObservationDataset (final Integer studyId, final String datasetName, final Integer datasetTypeId, final List<Integer> instanceIds,
			final Integer observationUnitVariableId, final Integer numberOfSubObservationUnits) {

		final DmsProject study = daoFactory.getDmsProjectDAO().getById(studyId);

		final String cropPrefix = workbenchDataManager.getProjectByUuid(study.getProgramUUID()).getCropType().getPlotCodePrefix();

		final List<DmsProject> plotDatasets = daoFactory.getDmsProjectDAO()
				.getDataSetsByStudyAndProjectProperty(studyId, TermId.DATASET_TYPE.getId(), String.valueOf(DataSetType.PLOT_DATA.getId()));

		if (plotDatasets == null || plotDatasets.isEmpty()) {
			throw new MiddlewareException("Study does not have a plot dataset associated to it");
		}

		final DmsProject plotDataset = plotDatasets.get(0);

		final DmsProject subObservationDataset = new DmsProject();

		final List<ProjectProperty> projectProperties = buildDefaultDatasetProperties(study, subObservationDataset, datasetName, datasetTypeId);
		projectProperties.add(buildDatasetProperty(subObservationDataset, study.getProgramUUID(), VariableType.OBSERVATION_UNIT.getId(),
				observationUnitVariableId, null, null, 4));

		subObservationDataset.setName(datasetName);
		subObservationDataset.setDescription(datasetName);
		subObservationDataset.setProgramUUID(study.getProgramUUID());
		subObservationDataset.setDeleted(false);
		subObservationDataset.setLocked(false);
		subObservationDataset.setProperties(projectProperties);
		subObservationDataset.setRelatedTos(this.buildProjectRelationships(plotDataset, subObservationDataset));

		daoFactory.getDmsProjectDAO().save(subObservationDataset);

		// iterate and create new sub-observation units
		final List<ExperimentModel> plotObservationUnits =
				daoFactory.getExperimentDao().getObservationUnits(plotDataset.getProjectId(), instanceIds);
		for (final ExperimentModel plotObservationUnit : plotObservationUnits) {
			for (int i = 1; i <= numberOfSubObservationUnits; i++) {
				ExperimentModel experimentModel = new ExperimentModel(plotObservationUnit.getGeoLocation(), plotObservationUnit.getTypeId(),
						cropPrefix + "P" + RandomStringUtils.randomAlphanumeric(8), subObservationDataset, plotObservationUnit.getStock(),
						plotObservationUnit, i);
				daoFactory.getExperimentDao().save(experimentModel);
			}
		}

		return null;
	}

	@Override
	public Boolean isDatasetNameAvailable(final String name, final String projectUUID) {
		final Integer dmsProjectId = daoFactory.getDmsProjectDAO().getProjectIdByNameAndProgramUUID(name, projectUUID);
		return (dmsProjectId == null);
	}

	@Override
	public Integer getNumberOfChildren(final Integer parentId) {
		return this.daoFactory.getDmsProjectDAO().getDatasetsByStudy(parentId).size();
	}

	private List<ProjectProperty> buildDefaultDatasetProperties(final DmsProject study, final DmsProject dmsProject,
			final String datasetName, final Integer datasetTypeId) {
		final List<ProjectProperty> projectProperties = new ArrayList<>();
		final ProjectProperty datasetProperty =
				buildDatasetProperty(dmsProject, study.getProgramUUID(), VariableType.STUDY_DETAIL.getId(), TermId.DATASET_NAME.getId(),
						datasetName, null, 1);
		final ProjectProperty datasetTitleProperty =
				buildDatasetProperty(dmsProject, study.getProgramUUID(), VariableType.STUDY_DETAIL.getId(), TermId.DATASET_TITLE.getId(),
						null, null, 2);
		final ProjectProperty datasetTypeProperty =
				buildDatasetProperty(dmsProject, study.getProgramUUID(), VariableType.STUDY_DETAIL.getId(), TermId.DATASET_TYPE.getId(),
						String.valueOf(datasetTypeId), null, 3);
		projectProperties.add(datasetProperty);
		projectProperties.add(datasetTitleProperty);
		projectProperties.add(datasetTypeProperty);
		return projectProperties;
	}

	private ProjectProperty buildDatasetProperty(final DmsProject dmsProject, final String programUUID, final Integer typeId,
			final Integer variableId, final String value, final String alias, final Integer rank) {
		final Variable variable = ontologyVariableDataManager.getVariable(programUUID, variableId, false, false);
		if (!variable.getVariableTypes().contains(VariableType.getById(typeId))) {
			throw new MiddlewareException("Specified type does not match with the list of types associated to the variable");
		}
		final ProjectProperty projectProperty =
				new ProjectProperty(dmsProject, typeId, value, rank, variableId, (alias == null) ? variable.getName() : alias);
		return projectProperty;
	}

	private List<ProjectRelationship> buildProjectRelationships(DmsProject parentDataset, DmsProject childDataset)
			throws MiddlewareQueryException {
		ProjectRelationship relationship = new ProjectRelationship();
		relationship.setSubjectProject(childDataset);
		relationship.setObjectProject(parentDataset);
		relationship.setTypeId(TermId.BELONGS_TO_STUDY.getId());

		List<ProjectRelationship> relationships = new ArrayList<ProjectRelationship>();
		relationships.add(relationship);

		return relationships;
	}

	protected void setDaoFactory(DaoFactory daoFactory) {
		this.daoFactory = daoFactory;
	}

}
