package org.generationcp.middleware.api.brapi.v2.observationunit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.generationcp.middleware.dao.dms.ExperimentDao;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.ExperimentModel;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitDto;
import org.generationcp.middleware.service.api.phenotype.ObservationUnitSearchRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
public class ObservationUnitServiceImpl implements ObservationUnitService {

	private static final Logger LOG = LoggerFactory.getLogger(ObservationUnitServiceImpl.class);

	private final HibernateSessionProvider sessionProvider;
	private final DaoFactory daoFactory;

	public ObservationUnitServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
		this.daoFactory = new DaoFactory(this.sessionProvider);
	}

	@Override
	public void update(final String observationUnitDbId, final ObservationUnitPatchRequestDTO requestDTO) {

		final ExperimentDao experimentDao = this.daoFactory.getExperimentDao();
		final ExperimentModel experimentModel = experimentDao.getByObsUnitId(observationUnitDbId);

		if (experimentModel == null) {
			throw new MiddlewareRequestException("", "invalid.observation.unit.id");
		}

		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String props = experimentModel.getJsonProps() != null ? experimentModel.getJsonProps() : "{}";
			final Map<String, Object> propsMap = mapper.readValue(props, HashMap.class);
			propsMap.put("geoCoordinates", requestDTO.getObservationUnitPosition().getGeoCoordinates());
			experimentModel.setJsonProps(mapper.writeValueAsString(propsMap));
			experimentDao.save(experimentModel);
		} catch (final Exception e) {
			final String message = "couldn't parse prop column for observationUnitDbId=" + observationUnitDbId;
			LOG.error(message, e);
			throw new MiddlewareException(message);
		}
	}

	@Override
	public List<ObservationUnitDto> searchObservationUnits(final Integer pageSize, final Integer pageNumber,
		final ObservationUnitSearchRequestDTO requestDTO) {
		return this.daoFactory.getPhenotypeDAO().searchObservationUnits(pageSize, pageNumber, requestDTO);
	}

	@Override
	public long countObservationUnits(final ObservationUnitSearchRequestDTO requestDTO) {
		return this.daoFactory.getPhenotypeDAO().countObservationUnits(requestDTO);
	}
}
