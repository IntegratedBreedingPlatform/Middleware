package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.domain.dms.DatasetDTO;
import org.generationcp.middleware.domain.dms.PhenotypicType;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.ruleengine.naming.expression.dataprocessor.ExpressionDataProcessor;
import org.generationcp.middleware.ruleengine.naming.expression.dataprocessor.ExpressionDataProcessorFactory;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.study.AdvanceService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdvanceServiceImpl implements AdvanceService {

	@Resource
	private DatasetService studyDatasetService;

	@Resource
	private ExpressionDataProcessorFactory dataProcessorFactory;

	private DaoFactory daoFactory;

	public AdvanceServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<Integer> advanceStudy(final Integer studyId, final AdvanceStudyRequest request) {
		final List<Integer> datasetTypeIds = Arrays.asList(
			DatasetTypeEnum.SUMMARY_DATA.getId(),
			DatasetTypeEnum.PLOT_DATA.getId());
		final List<DatasetDTO> datasets = this.studyDatasetService.getDatasets(studyId, new HashSet<>(datasetTypeIds));

		final List<MeasurementVariable> studyVariables =
			this.daoFactory.getDmsProjectDAO().getObservationSetVariables(studyId, Arrays.asList(VariableType.STUDY_DETAIL.getId()));
		DatasetDTO environmentDataset = this.filterDatasetByType(datasets, DatasetTypeEnum.SUMMARY_DATA);
		DatasetDTO plotDataset = this.filterDatasetByType(datasets, DatasetTypeEnum.PLOT_DATA);

		final ObservationUnitsSearchDTO searchDTO = new ObservationUnitsSearchDTO();
		searchDTO.setInstanceIds(searchDTO.getInstanceIds());
		final List<ObservationUnitRow> observationUnitRows = this.studyDatasetService
			.getObservationUnitRows(studyId, plotDataset.getDatasetId(), searchDTO, new PageRequest(0, Integer.MAX_VALUE));
		if (CollectionUtils.isEmpty(observationUnitRows)) {
			return new ArrayList<>();
		}

		final AdvancingSource environmentLevel = new AdvancingSource();
		final List<MeasurementVariable> conditions =
			this.getStudyConditions(studyVariables, environmentDataset.getVariables());
		final List<MeasurementVariable> constants = this.getStudyConstants(studyVariables);

		// TODO: fix dataProcessor list
		final ExpressionDataProcessor dataProcessor = this.dataProcessorFactory.retrieveExecutorProcessor();
		dataProcessor.processEnvironmentLevelData(environmentLevel, request, conditions, constants);

		// TODO: get the source germplasm and set to advancingSource::SetoriginGermplasm
		observationUnitRows.forEach(observationUnitRow -> {
			final AdvancingSource advancingSourceCandidate = environmentLevel.copy();

			// TODO: setTrailInstanceObservation!!
			// advancingSourceCandidate.setTrailInstanceObservation(trialInstanceObservations);

			// TODO: create naming ->  names
			// TODO: create the advanced germplasm and save it
		});

		// TODO: returns the recently created gerplasm::gids
		return null;
	}

	private DatasetDTO filterDatasetByType(final List<DatasetDTO> datasets, final DatasetTypeEnum type) {
		return datasets.stream()
			.filter(datasetDTO -> type.getId() == datasetDTO.getDatasetTypeId())
			.findFirst()
			//TODO: add properly exception
			.orElseThrow(() -> new RuntimeException());
	}

	private List<MeasurementVariable> getStudyConditions(final List<MeasurementVariable> studyVariables,
		final List<MeasurementVariable> environmentVariables) {

		final List<MeasurementVariable> conditions = studyVariables.stream()
			.filter(variable -> variable.getRole() != PhenotypicType.VARIATE)
			.collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(environmentVariables)) {
			final List<MeasurementVariable> trialEnvironmentVariables = environmentVariables.stream()
				.filter(variable -> variable.getRole() == PhenotypicType.TRIAL_ENVIRONMENT)
				.collect(Collectors.toList());
			conditions.addAll(trialEnvironmentVariables);
		}
		return conditions;
	}

	private List<MeasurementVariable> getStudyConstants(final List<MeasurementVariable> studyVariables) {
		return studyVariables.stream()
			.filter(variable -> variable.getRole() == PhenotypicType.VARIATE)
			.collect(Collectors.toList());
	}

}
