package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Preconditions;
import org.generationcp.middleware.dao.dms.EnvironmentDao;
import org.generationcp.middleware.dao.dms.ExperimentPropertyDao;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.domain.dms.EnvironmentData;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.study.StudyEnvironmentService;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class StudyEnvironmentServiceImpl implements StudyEnvironmentService {

	@Resource
	private StudyService studyService;

	@Resource
	private ExperimentDesignService experimentDesignService;

	private ExperimentModelGenerator experimentModelGenerator;
	private DaoFactory daoFactory;


	public StudyEnvironmentServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public StudyEnvironmentServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.experimentModelGenerator = new ExperimentModelGenerator();
		this.studyService = new StudyServiceImpl(sessionProvider);
	}

	@Override
	public List<StudyInstance> createStudyEnvironments(final CropType crop, final int studyId, final int datasetId, final Integer numberOfEnvironmentsToGenerate) {
		Preconditions.checkArgument(numberOfEnvironmentsToGenerate > 0);

		// Retrieve existing study instances
		final List<ExperimentModel> environments = this.daoFactory.getEnvironmentDao().getEnvironmentsByDataset(datasetId);
		final List<Integer> instanceNumbers = environments.stream().map(ExperimentModel::getObservationUnitNo).collect(Collectors.toList());

		final List<StudyInstance> studyInstances = new ArrayList<>();
		final boolean hasExperimentalDesign = this.experimentDesignService.getStudyExperimentDesignTypeTermId(studyId).isPresent();
		int instancesGenerated = 0;
		while (instancesGenerated < numberOfEnvironmentsToGenerate) {
			// If design is generated, increment last instance number. Otherwise, attempt to find  "gap" instance number first (if any)
			Integer instanceNumber = Collections.max(instanceNumbers) + 1;
			if (!hasExperimentalDesign) {
				instanceNumber = 1;
				while (instanceNumbers.contains(instanceNumber)) {
					instanceNumber++;
				}
			}

			// The default value of an instance's location name is "Unspecified Location"
			final Optional<Location> location = this.getUnspecifiedLocation();

			final ExperimentModel experimentModel =
				this.experimentModelGenerator.generate(crop, datasetId, ExperimentType.TRIAL_ENVIRONMENT);
			experimentModel.setObservationUnitNo(instanceNumber);
			final boolean locationPresent = location.isPresent();
			if (locationPresent) {
				experimentModel.setProperties(Collections.singletonList(
					new ExperimentProperty(experimentModel, String.valueOf(location.get().getLocid()), 1, TermId.LOCATION_ID.getId())));
			}
			this.daoFactory.getExperimentDao().save(experimentModel);

			final StudyInstance studyInstance =
				new StudyInstance(experimentModel.getNdExperimentId(), instanceNumber, false, false, false, true);
			if (locationPresent) {
				studyInstance.setLocationId(location.get().getLocid());
				studyInstance.setLocationName(location.get().getLname());
				studyInstance.setLocationAbbreviation(location.get().getLabbr());
			}

			instanceNumbers.add(instanceNumber);
			studyInstances.add(studyInstance);
			instancesGenerated++;
		}



		return studyInstances;
	}

	@Override
	public List<StudyInstance> getStudyEnvironments(final int studyId) {
		return this.getStudyInstances(studyId, Collections.emptyList());
	}

	private List<StudyInstance> getStudyInstances(final int studyId, final List<Integer> instanceIds) {
		final int environmentDatasetId =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0)
				.getProjectId();
		final List<StudyInstance> instances = this.daoFactory.getDmsProjectDAO().getDatasetInstances(environmentDatasetId, instanceIds);
		// If study has advance or cross list and instance has experiment design, mark instance as cannot be deleted
		final boolean hasAdvancedOrCrossesList = this.daoFactory.getGermplasmListDAO().hasAdvancedOrCrossesList(studyId);
		if (hasAdvancedOrCrossesList) {
			for (final StudyInstance instance : instances) {
				if (instance.isHasExperimentalDesign()) {
					instance.setCanBeDeleted(false);
				}
			}
		}
		return instances;
	}

	@Override
	public void deleteStudyEnvironments(final Integer studyId, final List<Integer> environmentIds) {
		final Integer environmentDatasetId = this.studyService.getEnvironmentDatasetId(studyId);
		final EnvironmentDao environmentDao = this.daoFactory.getEnvironmentDao();
		final List<ExperimentModel> allEnvironments = environmentDao.getEnvironmentsByDataset(environmentDatasetId);
		final List<ExperimentModel> environmentsToDelete = allEnvironments.stream()
			.filter(instance -> environmentIds.contains(instance.getNdExperimentId())).collect(
				Collectors.toList());
		final List<Integer> instanceNumbersToDelete = environmentsToDelete.stream().map(ExperimentModel::getObservationUnitNo).collect(Collectors.toList());

		// Delete plot and environment experiments
		final Integer plotDatasetId = this.studyService.getPlotDatasetId(studyId);
		this.daoFactory.getExperimentDao()
			.deleteExperimentsForDatasets(Arrays.asList(plotDatasetId, environmentDatasetId), instanceNumbersToDelete);

		// IF experimental design is not yet generated, re-number succeeding trial instances
		final boolean hasExperimentalDesign = this.experimentDesignService.getStudyExperimentDesignTypeTermId(studyId).isPresent();
		if (!hasExperimentalDesign) {
			final Integer startingInstanceNumber = Collections.min(instanceNumbersToDelete);
			final List<ExperimentModel> instancesToUpdate =
				allEnvironments.stream().filter(instance -> !instanceNumbersToDelete.contains(instance.getObservationUnitNo())
					&& instance.getObservationUnitNo() > startingInstanceNumber).collect(
					Collectors.toList());
			// Unfortunately, not possible in MySQL 5 to do batch update as row_number function is only available in MySQL 8
			// Also tried using MySQL variable assignment like @instance_number:=@instance_number + 1 but it causes Hibernate error
			// as it's being treated as named parameter. Hopefully can be optimized when we upgrade Hibernate and/or MySQL version
			Integer instanceNumber = startingInstanceNumber;
			for (final ExperimentModel instance : instancesToUpdate) {
				instance.setObservationUnitNo(instanceNumber++);
				this.daoFactory.getExperimentDao().saveOrUpdate(instance);
			}
		}
	}

	@Override
	public Optional<StudyInstance> getStudyEnvironment(final int studyId, final Integer environmentId) {
		final List<StudyInstance> studyInstances = this.getStudyInstances(studyId, Collections.singletonList(environmentId));
		if (!CollectionUtils.isEmpty(studyInstances)) {
			return Optional.of(studyInstances.get(0));
		}
		return Optional.empty();
	}

	@Override
	public EnvironmentData addEnvironmentData(final EnvironmentData environmentData) {
		Preconditions.checkNotNull(environmentData.getEnvironmentId());
		final Integer variableId = environmentData.getVariableId();
		Preconditions.checkNotNull(variableId);
		Preconditions.checkNotNull(environmentData.getValue());
		final Boolean isEnvironmentalCondition = environmentData.getVariableIsEnvironmentalCondition();
		// Environment oonditions are stored in phenotype. Other environment details are saved in nd_experimentprop
		if (isEnvironmentalCondition) {
			final Phenotype phenotype = new Phenotype(variableId, environmentData.getValue(), new ExperimentModel(environmentData.getEnvironmentId()));
			phenotype.setCreatedDate(new Date());
			phenotype.setUpdatedDate(new Date());
			phenotype.setcValue(environmentData.getCategoricalValueId());
			phenotype.setName(String.valueOf(variableId));

			this.daoFactory.getPhenotypeDAO().save(phenotype);
			environmentData.setEnvironmentDataId(phenotype.getPhenotypeId());
		} else {
			final String value = this.getEnvironmentDataValue(environmentData);
			final ExperimentProperty property =
				new ExperimentProperty(new ExperimentModel(environmentData.getEnvironmentId()), value, 1, environmentData.getVariableId());
			this.daoFactory.getExperimentPropertyDao().save(property);
			environmentData.setEnvironmentDataId(property.getNdExperimentpropId());
		}

		return environmentData;
	}

	private String getEnvironmentDataValue(final EnvironmentData environmentData) {
		return (environmentData.getCategoricalValueId() != null && environmentData.getCategoricalValueId() > 0) ? String.valueOf(environmentData.getCategoricalValueId()) :
			environmentData.getValue();
	}

	@Override
	public EnvironmentData updateEnvironmentData(final EnvironmentData environmentData) {
		Preconditions.checkNotNull(environmentData.getEnvironmentDataId());
		Preconditions.checkNotNull(environmentData.getEnvironmentId());
		Preconditions.checkNotNull(environmentData.getVariableId());
		Preconditions.checkNotNull(environmentData.getValue());
		final Boolean isEnvironmentalCondition = environmentData.getVariableIsEnvironmentalCondition();
		// Environment oonditions are stored in phenotype. Other environment details are saved in nd_experimentprop
		if (isEnvironmentalCondition) {
			final PhenotypeDao phenotypeDAO = this.daoFactory.getPhenotypeDAO();
			final Phenotype phenotype = phenotypeDAO.getById(environmentData.getEnvironmentDataId());
			Preconditions.checkNotNull(phenotype);
			phenotype.setValue(environmentData.getValue());
			phenotype.setcValue(environmentData.getCategoricalValueId());
			phenotype.setUpdatedDate(new Date());
			phenotypeDAO.update(phenotype);
		} else {
			final ExperimentPropertyDao propertyDao = this.daoFactory.getExperimentPropertyDao();
			final ExperimentProperty property = propertyDao.getById(environmentData.getEnvironmentDataId());
			Preconditions.checkNotNull(property);
			property.setValue(this.getEnvironmentDataValue(environmentData));
			propertyDao.update(property);
		}
		return environmentData;
	}

	protected Optional<Location> getUnspecifiedLocation() {
		final List<Location> locations = this.daoFactory.getLocationDAO().getByName(Location.UNSPECIFIED_LOCATION, Operation.EQUAL);
		if (!locations.isEmpty()) {
			return Optional.of(locations.get(0));
		}
		return Optional.empty();
	}

}
