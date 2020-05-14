package org.generationcp.middleware.service.impl.inventory;

import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.inventory.PlantingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class PlantingServiceImpl implements PlantingService {

	private DaoFactory daoFactory;

	public PlantingServiceImpl() {
	}

	public PlantingServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	public PlantingPreparationDTO searchPlantingPreparation(final SearchCompositeDto<ObservationUnitsSearchDTO, Integer> searchDTO) {
		return null; // TODO
	}

}
