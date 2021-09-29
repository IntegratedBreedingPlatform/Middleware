package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.observation.ObservationDto;
import org.generationcp.middleware.api.brapi.v2.observation.ObservationSearchRequestDto;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
import org.generationcp.middleware.domain.dms.ValueReference;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.pojos.dms.Phenotype;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ObservationServiceBrapiImpl implements ObservationServiceBrapi {

	private final DaoFactory daoFactory;

	public ObservationServiceBrapiImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<ObservationDto> searchObservations(final ObservationSearchRequestDto observationSearchRequestDto, final Integer pageSize,
		final Integer pageNumber) {
		return this.daoFactory.getPhenotypeDAO().searchObservations(observationSearchRequestDto, pageSize, pageNumber);
	}

	@Override
	public long countObservations(final ObservationSearchRequestDto observationSearchRequestDto) {
		return this.daoFactory.getPhenotypeDAO().countObservations(observationSearchRequestDto);
	}

	@Override
	public List<Integer> createObservations(final List<ObservationDto> observations) {

		final List<Integer> observationDbIds = new ArrayList<>();
		final Map<String, ExperimentModel> experimentModelMap = this.daoFactory.getExperimentDao()
			.getByObsUnitIds(observations.stream().map(ObservationDto::getObservationUnitDbId).collect(Collectors.toList())).stream()
			.collect(Collectors.toMap(ExperimentModel::getObsUnitId, Function.identity()));
		final Map<Integer, List<ValueReference>> validValuesForCategoricalVariables =
			this.getCategoriesForCategoricalVariables(observations);

		final PhenotypeDao dao = this.daoFactory.getPhenotypeDAO();
		for (final ObservationDto observation : observations) {
			final Phenotype phenotype = new Phenotype();
			phenotype.setCreatedDate(new Date());
			phenotype.setUpdatedDate(new Date());
			phenotype.setObservableId(Integer.valueOf(observation.getObservationVariableDbId()));
			phenotype.setValue(observation.getValue());
			phenotype.setcValue(
				this.resolveCategoricalIdValue(Integer.valueOf(observation.getObservationVariableDbId()), observation.getValue(),
					validValuesForCategoricalVariables));
			phenotype.setExperiment(experimentModelMap.get(observation.getObservationUnitDbId()));
			final Phenotype saved = dao.save(phenotype);
			observationDbIds.add(saved.getPhenotypeId());
		}
		return observationDbIds;
	}

	private Map<Integer, List<ValueReference>> getCategoriesForCategoricalVariables(final List<ObservationDto> observations) {
		final Set<Integer> variableIds =
			observations.stream().map((o) -> Integer.valueOf(o.getObservationVariableDbId())).collect(Collectors.toSet());
		return this.daoFactory.getCvTermRelationshipDao().getCategoriesForCategoricalVariables(new ArrayList<>(variableIds));

	}

	private Integer resolveCategoricalIdValue(final Integer variableId, final String value,
		final Map<Integer, List<ValueReference>> validValuesForCategoricalVariables) {
		if (validValuesForCategoricalVariables.containsKey(variableId)) {
			final Optional<ValueReference> match =
				validValuesForCategoricalVariables.get(variableId).stream().filter(v -> v.getName().equals(value)).findAny();
			return match.isPresent() ? Integer.valueOf(match.get().getKey()) : null;
		}
		return null;
	}
}
