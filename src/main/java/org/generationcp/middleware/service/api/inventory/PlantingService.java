package org.generationcp.middleware.service.api.inventory;

import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.domain.inventory.planting.PlantingMetadata;
import org.generationcp.middleware.domain.inventory.planting.PlantingRequestDto;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.impl.inventory.PlantingPreparationDTO;

import java.util.List;

public interface PlantingService {

	PlantingPreparationDTO searchPlantingPreparation(final Integer studyId,
		final Integer datasetId, SearchCompositeDto<ObservationUnitsSearchDTO, Integer> searchCompositeDto);

	PlantingMetadata getPlantingMetadata(Integer studyId, Integer datasetId, PlantingRequestDto plantingRequestDto);

	void savePlanting(Integer userId, Integer studyId, Integer datasetId, PlantingRequestDto plantingRequestDto,
		TransactionStatus transactionStatus);

	List<Transaction> getPlantingTransactionsByStudyId(Integer studyId, TransactionStatus transactionStatus);

	List<Transaction> getPlantingTransactionsByStudyAndEntryId(Integer studyId, Integer entryId, TransactionStatus transactionStatus);

	List<Transaction> getPlantingTransactionsByInstanceId(Integer instance, TransactionStatus transactionStatus);
}
