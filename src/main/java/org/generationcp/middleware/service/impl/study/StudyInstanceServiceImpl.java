package org.generationcp.middleware.service.impl.study;

import com.google.common.base.Optional;
import org.generationcp.middleware.domain.dms.ExperimentType;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.Operation;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.dms.DatasetType;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.ExperimentProperty;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.api.study.StudyService;
import org.generationcp.middleware.service.impl.study.generation.ExperimentModelGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Transactional
public class StudyInstanceServiceImpl implements StudyInstanceService {

	@Autowired
	private StudyService studyService;

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
	public StudyInstance createStudyInstance(final CropType crop, final int studyId, final int datasetId) {

		// Get the existing environment dataset variables.
		// Since we are creating a new study instance, the values of these variables are just blank.
		final List<MeasurementVariable> measurementVariables = this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId,
			Arrays.asList(VariableType.ENVIRONMENT_DETAIL.getId(), VariableType.STUDY_CONDITION.getId()));

		final int instanceNumber = this.daoFactory.getInstanceDao().getNextInstanceNumber(datasetId);

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

		return studyInstance;
	}

	@Override
	public List<StudyInstance> getStudyInstances(final int studyId) {
		return this.getStudyInstances(studyId, Collections.emptyList());
	}

	private List<StudyInstance> getStudyInstances(final int studyId, final List<Integer> instanceIds) {
		final int environmentDatasetId =
			this.daoFactory.getDmsProjectDAO().getDatasetsByTypeForStudy(studyId, DatasetTypeEnum.SUMMARY_DATA.getId()).get(0)
				.getProjectId();
		final DatasetType datasetType = this.daoFactory.getDatasetTypeDao().getById(DatasetTypeEnum.SUMMARY_DATA.getId());
		final List<StudyInstance> instances = this.daoFactory.getDmsProjectDAO().getDatasetInstances(environmentDatasetId, instanceIds, datasetType);
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
	public void deleteStudyInstance(final Integer studyId, final Integer instanceId) {
		final ExperimentModel geolocation = this.daoFactory.getInstanceDao().getById(instanceId);
		final Integer instanceNumber = geolocation.getObservationUnitNo();

		// Delete plot and environment experiments
		final Integer environmentDatasetId = this.studyService.getEnvironmentDatasetId(studyId);
		final Integer plotDatasetId = this.studyService.getPlotDatasetId(studyId);
		this.daoFactory.getExperimentDao()
			.deleteExperimentsForDatasets(Arrays.asList(plotDatasetId, environmentDatasetId), Collections.singletonList(instanceNumber));
	}

	@Override
	public Optional<StudyInstance> getStudyInstance(final int studyId, final Integer instanceId) {
		final List<StudyInstance> studyInstances = this.getStudyInstances(studyId, Collections.singletonList(instanceId));
		if (!CollectionUtils.isEmpty(studyInstances)) {
			return Optional.of(studyInstances.get(0));
		}
		return Optional.absent();
	}

	protected Optional<Location> getUnspecifiedLocation() {
		final List<Location> locations = this.daoFactory.getLocationDAO().getByName(Location.UNSPECIFIED_LOCATION, Operation.EQUAL);
		if (!locations.isEmpty()) {
			return Optional.of(locations.get(0));
		}
		return Optional.absent();
	}

}
