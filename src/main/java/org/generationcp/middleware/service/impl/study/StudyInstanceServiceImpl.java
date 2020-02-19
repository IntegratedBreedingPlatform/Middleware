package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Preconditions;
import org.generationcp.middleware.dao.dms.EnvironmentDao;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.api.study.generation.ExperimentDesignService;
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
public class StudyInstanceServiceImpl implements StudyInstanceService {

	@Resource
	private StudyService studyService;

	@Resource
	private ExperimentDesignService experimentDesignService;

	private ExperimentModelGenerator experimentModelGenerator;
	private DaoFactory daoFactory;


	public StudyInstanceServiceImpl() {
		// no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
	}

	public StudyInstanceServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.experimentModelGenerator = new ExperimentModelGenerator();
		this.studyService = new StudyServiceImpl(sessionProvider);
	}

	@Override
	public List<StudyInstance> createStudyInstances(final CropType crop, final int studyId, final int datasetId, final Integer numberOfInstancesToGenerate) {
		Preconditions.checkArgument(numberOfInstancesToGenerate > 0);

		// Retrieve existing study instances
		final List<ExperimentModel> environments = this.daoFactory.getEnvironmentDao().getEnvironmentsByDataset(datasetId);
		final List<Integer> instanceNumbers = environments.stream().map(ExperimentModel::getObservationUnitNo).collect(Collectors.toList());

		final List<StudyInstance> studyInstances = new ArrayList<>();
		final boolean hasExperimentalDesign = this.experimentDesignService.getStudyExperimentDesignTypeTermId(studyId).isPresent();
		int instancesGenerated = 0;
		while (instancesGenerated < numberOfInstancesToGenerate) {
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
	public List<StudyInstance> getStudyInstances(final int studyId) {
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
	public void deleteStudyInstances(final Integer studyId, final List<Integer> instanceIds) {
		final Integer environmentDatasetId = this.studyService.getEnvironmentDatasetId(studyId);
		final EnvironmentDao environmentDao = this.daoFactory.getEnvironmentDao();
		final List<ExperimentModel> allEnvironments = environmentDao.getEnvironmentsByDataset(environmentDatasetId);
		final List<ExperimentModel> environmentsToDelete = allEnvironments.stream()
			.filter(instance -> instanceIds.contains(instance.getNdExperimentId())).collect(
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
	public Optional<StudyInstance> getStudyInstance(final int studyId, final Integer instanceId) {
		final List<StudyInstance> studyInstances = this.getStudyInstances(studyId, Collections.singletonList(instanceId));
		if (!CollectionUtils.isEmpty(studyInstances)) {
			return Optional.of(studyInstances.get(0));
		}
		return Optional.empty();
	}

	protected Optional<Location> getUnspecifiedLocation() {
		final List<Location> locations = this.daoFactory.getLocationDAO().getByName(Location.UNSPECIFIED_LOCATION, Operation.EQUAL);
		if (!locations.isEmpty()) {
			return Optional.of(locations.get(0));
		}
		return Optional.empty();
	}

}
