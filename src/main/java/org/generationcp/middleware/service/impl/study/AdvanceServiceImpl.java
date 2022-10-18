package org.generationcp.middleware.service.impl.study;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.math.NumberUtils;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Location;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.ruleengine.naming.expression.resolver.LocationDataResolver;
import org.generationcp.middleware.ruleengine.naming.expression.resolver.SeasonDataResolver;
import org.generationcp.middleware.ruleengine.naming.expression.resolver.SelectionTraitResolver;
import org.generationcp.middleware.ruleengine.pojo.NewAdvancingSource;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitData;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.AdvanceService;
import org.generationcp.middleware.service.api.study.MeasurementVariableDto;
import org.generationcp.middleware.service.api.study.StudyInstanceService;
import org.generationcp.middleware.service.impl.dataset.DatasetServiceImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class AdvanceServiceImpl implements AdvanceService {

	@Resource
	private DatasetService datasetService;

	//	@Resource
	//	private ExpressionDataProcessorFactory dataProcessorFactory;

	@Resource
	private StudyInstanceService studyInstanceService;

	private final DaoFactory daoFactory;
	private final SeasonDataResolver seasonDataResolver;
	private final SelectionTraitResolver selectionTraitResolver;
	private final LocationDataResolver locationDataResolver;

	public AdvanceServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.seasonDataResolver = new SeasonDataResolver();
		this.selectionTraitResolver = new SelectionTraitResolver();
		this.locationDataResolver = new LocationDataResolver();
	}

	@Override
	public List<Integer> advanceStudy(final Integer studyId, final AdvanceStudyRequest request) {

		final List<Integer> datasetTypeIds = Arrays.asList(
			DatasetTypeEnum.SUMMARY_DATA.getId(),
			DatasetTypeEnum.PLOT_DATA.getId());
		final Map<Integer, DatasetDTO> datasetsByType =
			this.datasetService.getDatasets(studyId, new HashSet<>(datasetTypeIds)).stream().collect(Collectors.toMap(
				DatasetDTO::getDatasetTypeId, datasetDTO -> datasetDTO));

		final DatasetDTO plotDataset = datasetsByType.get(DatasetTypeEnum.PLOT_DATA.getId());
		final List<MeasurementVariable> plotDatasetVariables = this.daoFactory.getDmsProjectDAO()
			.getObservationSetVariables(plotDataset.getDatasetId(), DatasetServiceImpl.OBSERVATION_DATASET_VARIABLE_TYPES);
		plotDataset.setVariables(plotDatasetVariables);

		final ObservationUnitsSearchDTO plotDataObservationsSearchDTO = new ObservationUnitsSearchDTO();
		plotDataObservationsSearchDTO.setInstanceIds(request.getInstanceIds());
		final List<ObservationUnitRow> observationUnitRows = this.datasetService
			.getObservationUnitRows(studyId, plotDataset.getDatasetId(), plotDataObservationsSearchDTO,
				new PageRequest(0, Integer.MAX_VALUE));
		if (CollectionUtils.isEmpty(observationUnitRows)) {
			return new ArrayList<>();
		}

		final DatasetDTO environmentDataset = datasetsByType.get(DatasetTypeEnum.SUMMARY_DATA.getId());
		final List<MeasurementVariable> environmentDatasetVariables = this.daoFactory.getDmsProjectDAO()
			.getObservationSetVariables(environmentDataset.getDatasetId(), DatasetTypeEnum.SUMMARY_DATA.getVariableTypes());
		environmentDataset.setVariables(environmentDatasetVariables);

		final ObservationUnitsSearchDTO trialObservationUnitsSearchDTO = new ObservationUnitsSearchDTO();
		trialObservationUnitsSearchDTO.setInstanceIds(request.getInstanceIds());
		trialObservationUnitsSearchDTO.setEnvironmentDatasetId(environmentDataset.getDatasetId());
		final List<MeasurementVariableDto> environmentConditions = this.getObservationSetVariables(environmentDataset.getDatasetId(),
			Lists.newArrayList(VariableType.ENVIRONMENT_CONDITION.getId()));
		trialObservationUnitsSearchDTO.setEnvironmentConditions(environmentConditions);

		final List<MeasurementVariableDto> environmentDetailsVariables =
			this.getObservationSetVariables(environmentDataset.getDatasetId(), Lists.newArrayList(VariableType.ENVIRONMENT_DETAIL.getId()));
		trialObservationUnitsSearchDTO.setEnvironmentDetails(environmentDetailsVariables);

		// TODO: check why is returning 0 for study 42756
		// TODO: this is not returning values for Selection_trait variable -> it should be fixed now!
		final List<ObservationUnitRow> trialObservationUnitRows = this.datasetService
			.getObservationUnitRows(studyId, environmentDataset.getDatasetId(), trialObservationUnitsSearchDTO,
				new PageRequest(0, Integer.MAX_VALUE));

		final List<MeasurementVariable> studyVariables =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(studyId, Arrays.asList(VariableType.STUDY_DETAIL.getId()));
		// TODO: review a better way to obtain this variables
		final List<MeasurementVariable> studyEnvironmentVariables =
			this.getStudyEnvironmentVariables(studyVariables, environmentDataset.getVariables());
		//		// TODO: review a better way to obtain this variables
		final List<MeasurementVariable> studyVariates = this.getStudyVariates(environmentDataset.getVariables());

		// Getting data related at study level
		final String seasonStudyLevel = this.seasonDataResolver.resolveStudyLevelData(studyEnvironmentVariables);
		final String selectionTraitStudyLevel = this.selectionTraitResolver
			.resolveStudyLevelData(Stream.concat(studyEnvironmentVariables.stream(), studyVariates.stream()).collect(
				Collectors.toList()));

		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId =
			plotDatasetVariables.stream().collect(Collectors.toMap(MeasurementVariable::getTermId, variable -> variable));

		final Map<Integer, StudyInstance> studyInstanceMap =
			this.studyInstanceService.getStudyInstances(studyId).stream()
				.collect(Collectors.toMap(StudyInstance::getInstanceNumber, i -> i));

		final Map<String, Method> breedingMethodsByCodes =
			this.daoFactory.getMethodDAO().getAllMethod().stream()
				.collect(Collectors.toMap(Method::getMcode, method -> method, (method1, method2) -> method1));

		final Map<Integer, Location> locationsByLocationId =
			this.getLocationsByLocationIdFromTrialObservationUnits(trialObservationUnitRows);

		final Set<Integer> gids = observationUnitRows.stream()
			.map(ObservationUnitRow::getGid)
			.collect(Collectors.toSet());
		final Map<Integer, Germplasm> originGermplasmsByGid = this.daoFactory.getGermplasmDao().getByGIDList(new ArrayList<>(gids)).stream()
			.collect(Collectors.toMap(Germplasm::getGid, germplasm -> germplasm));

		observationUnitRows.forEach(row -> {
			final NewAdvancingSource advancingSourceCandidate =
				new NewAdvancingSource(seasonStudyLevel, selectionTraitStudyLevel,
					originGermplasmsByGid.get(row.getGid()));
			advancingSourceCandidate.setTrialInstanceNumber(String.valueOf(row.getTrialInstance()));

			// If study is Trial, then setting data if trial instance is not null
			if (advancingSourceCandidate.getTrialInstanceNumber() != null) {
				final Integer trialInstanceNumber = row.getTrialInstance();
				final ObservationUnitRow trialInstanceObservations = trialObservationUnitRows.stream()
					.filter(observationUnitRow -> Integer.valueOf(advancingSourceCandidate.getTrialInstanceNumber())
						.equals(observationUnitRow.getTrialInstance()))
					.findFirst()
					.get();
				if (studyInstanceMap.containsKey(trialInstanceNumber) && trialInstanceObservations != null) {
					this.getVariableByVariableId(trialInstanceObservations.getEnvironmentVariables().values(), TermId.LOCATION_ID.getId())
						.ifPresent(
							observationUnitData -> observationUnitData
								.setValue(String.valueOf(studyInstanceMap.get(trialInstanceNumber).getLocationId()))
						);
				}

				advancingSourceCandidate.setTrailInstanceObservation(trialInstanceObservations);
			}

			// TODO: resolve plot data level
			this.resolveEnvironmentAndPlotLevelData(advancingSourceCandidate, row, locationsByLocationId, plotDataVariablesByTermId);

			// Setting conditions for Breeders Cross ID
			// TODO: we only set conditions for breeders cross id. Find another way to pass conditions to breeders cross id
			advancingSourceCandidate.setConditions(studyEnvironmentVariables);
			// TODO: NPE! fix it. (why there are observationUnitDatas without variableId value?? ) -----> It should be FIXED now -> I added 		trialObservationUnitsSearchDTO.setEnvironmentDatasetId(environmentDataset.getDatasetId());
			//			advancingSourceCandidate
			//				.setReplicationNumber(this.getObservationValueByVariableId(row.getVariables().values(), TermId.REP_NO.getId()));

			final Integer methodId = request.getMethodVariateId() == null ? request.getBreedingMethodId() :
				this.getBreedingMethodId(request.getMethodVariateId(), row, breedingMethodsByCodes);
			if (methodId == null) {
				return;
			}

			//			dataProcessor
			//				.processPlotLevelData(advancingSourceCandidate, row, null, locationsByLocationId, plotDataVariablesByTermId);

			// TODO: create naming ->  names
			// TODO: create the advanced germplasm and save it

		});

		// TODO: returns the recently created gerplasm::gids
		return null;
	}

	// TODO: review a better way to obtain this variables
	private List<MeasurementVariable> getStudyEnvironmentVariables(final List<MeasurementVariable> studyVariables,
		final List<MeasurementVariable> environmentVariables) {

		final List<MeasurementVariable> conditions = studyVariables.stream()
			.filter(variable -> variable.getVariableType().getRole() != PhenotypicType.VARIATE)
			.collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(environmentVariables)) {
			final List<MeasurementVariable> trialEnvironmentVariables = environmentVariables.stream()
				.filter(variable -> variable.getVariableType().getRole() == PhenotypicType.TRIAL_ENVIRONMENT)
				.collect(Collectors.toList());
			conditions.addAll(trialEnvironmentVariables);
		}
		return conditions;
	}

	// TODO: review a better way to obtain this
	private List<MeasurementVariable> getStudyVariates(final List<MeasurementVariable> studyVariables) {
		return studyVariables.stream()
			.filter(variable -> variable.getVariableType().getRole() == PhenotypicType.VARIATE)
			.collect(Collectors.toList());
	}

	private Optional<ObservationUnitData> getVariableByVariableId(final Collection<ObservationUnitData> variables, final int variableId) {
		return variables.stream()
			.filter(variable -> variable.getVariableId() == variableId)
			.findFirst();
	}

	private String getObservationValueByVariableId(final Collection<ObservationUnitData> variables, final int variableId) {
		return this.getVariableByVariableId(variables, variableId)
			.map(ObservationUnitData::getValue)
			.orElse(null);
	}

	private List<MeasurementVariableDto> getObservationSetVariables(final Integer datasetId, final List<Integer> variableTypes) {
		return this.daoFactory.getDmsProjectDAO().getObservationSetVariables(datasetId, variableTypes)
			.stream()
			.map(variable -> new MeasurementVariableDto(variable.getTermId(), variable.getName()))
			.collect(Collectors.toList());
	}

	private Integer getBreedingMethodId(final Integer methodVariateId, final ObservationUnitRow row,
		final Map<String, Method> breedingMethodsByCode) {
		// TODO: the user can select BREEDING_METHOD_VARIATE as variate variable ???
		if (TermId.BREEDING_METHOD_VARIATE.getId() == methodVariateId) {
			return this.getIntegerValue(this.getObservationValueByVariableId(row.getVariables().values(), methodVariateId));
		} else if (TermId.BREEDING_METHOD_VARIATE_TEXT.getId() == methodVariateId
			|| TermId.BREEDING_METHOD_VARIATE_CODE.getId() == methodVariateId) {
			final String methodName = this.getObservationValueByVariableId(row.getVariables().values(), methodVariateId);
			if (StringUtils.isEmpty(methodName)) {
				return null;
			}
			if (NumberUtils.isNumber(methodName)) {
				return Double.valueOf(methodName).intValue();
			}

			// coming from old fb or other sources
			final Method method = breedingMethodsByCode.get(methodName);
			if (method != null && (
				(methodVariateId == TermId.BREEDING_METHOD_VARIATE_TEXT.getId() && methodName.equalsIgnoreCase(method.getMname())) ||
					(methodVariateId == TermId.BREEDING_METHOD_VARIATE_CODE.getId() && methodName
						.equalsIgnoreCase(method.getMcode())))) {
				return method.getMid();
			}
		} else {
			// on load of study, this has been converted to id and not the code.
			return this.getIntegerValue(this.getObservationValueByVariableId(row.getVariables().values(), methodVariateId));
		}
		return null;
	}

	private Integer getIntegerValue(final String value) {
		Integer integerValue = null;

		if (NumberUtils.isNumber(value)) {
			integerValue = Double.valueOf(value).intValue();
		}

		return integerValue;
	}

	private Map<Integer, Location> getLocationsByLocationIdFromTrialObservationUnits(
		final List<ObservationUnitRow> trialObservationUnitRows) {
		final Set<Integer> locationIds = this.getVariableValuesFromObservations(trialObservationUnitRows, TermId.LOCATION_ID.getId());
		return this.daoFactory.getLocationDAO().getByIds(new ArrayList<>(locationIds)).stream()
			.collect(Collectors.toMap(Location::getLocid, location -> location));
	}

	private Set<Integer> getVariableValuesFromObservations(final List<ObservationUnitRow> observations, final Integer variableId) {
		return observations.stream()
			.map(row -> row.getEnvironmentVariables().values())
			.flatMap(Collection::stream)
			.filter(observationUnitData -> observationUnitData.getVariableId().equals(variableId))
			.map(observationUnitData -> this.getIntegerValue(observationUnitData.getValue()))
			.collect(Collectors.toSet());
	}

	private void resolveEnvironmentAndPlotLevelData(final NewAdvancingSource source, final ObservationUnitRow row,
		final Map<Integer, Location> locationsByLocationId,
		final Map<Integer, MeasurementVariable> plotDataVariablesByTermId) {
		this.locationDataResolver.resolveEnvironmentLevelData(source, locationsByLocationId);
		this.seasonDataResolver.resolveEnvironmentLevelData(source, plotDataVariablesByTermId);

		// TODO: check if it's right to first process level plot data and then environment data. Currently it works in this way.
		this.selectionTraitResolver.resolvePlotLevelData(source, row, plotDataVariablesByTermId);
		this.selectionTraitResolver.resolveEnvironmentLevelData(source, plotDataVariablesByTermId);
	}

}
