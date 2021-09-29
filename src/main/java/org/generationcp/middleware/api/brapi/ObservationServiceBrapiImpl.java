package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.observation.ObservationDto;
import org.generationcp.middleware.api.brapi.v2.observation.ObservationSearchRequestDto;
import org.generationcp.middleware.dao.dms.PhenotypeDao;
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

		final PhenotypeDao dao = this.daoFactory.getPhenotypeDAO();
		for (final ObservationDto observation : observations) {
			final Phenotype phenotype = new Phenotype();
			phenotype.setCreatedDate(new Date());
			phenotype.setUpdatedDate(new Date());
			phenotype.setObservableId(Integer.valueOf(observation.getObservationVariableDbId()));
			phenotype.setValue(observation.getValue());
			phenotype.setcValue(0); // TODO:
			phenotype.setExperiment(experimentModelMap.get(observation.getObservationUnitDbId()));
			final Phenotype saved = dao.save(phenotype);
			observationDbIds.add(saved.getPhenotypeId());
		}
		return observationDbIds;
	}
}
