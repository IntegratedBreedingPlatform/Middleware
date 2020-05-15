package org.generationcp.middleware.service.impl.inventory;

import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.domain.inventory.planting.PlantingMetadata;
import org.generationcp.middleware.domain.inventory.planting.PlantingRequestDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.inventory.PlantingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

	@Override
	public PlantingMetadata getPlantingMetadata(final PlantingRequestDto plantingRequestDto) {
		final PlantingMetadata plantingMetadata = new PlantingMetadata();
		//TODO Get ObervationUnitIds
		final List<Integer> obsUnitIds = Lists.newArrayList(16500, 16502, 16503, 16501, 16504);
		plantingMetadata.setConfirmedTransactionsCount(
			daoFactory.getExperimentTransactionDao().countPlantingTransactionsByStatus(obsUnitIds, TransactionStatus.CONFIRMED));
		plantingMetadata.setPendingTransactionsCount(
			daoFactory.getExperimentTransactionDao().countPlantingTransactionsByStatus(obsUnitIds, TransactionStatus.PENDING));
		return plantingMetadata;
	}

}
