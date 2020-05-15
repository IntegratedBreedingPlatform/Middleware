package org.generationcp.middleware.service.impl.inventory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.generationcp.middleware.domain.inventory.common.SearchCompositeDto;
import org.generationcp.middleware.domain.inventory.manager.ExtendedLotDto;
import org.generationcp.middleware.domain.inventory.manager.LotsSearchDto;
import org.generationcp.middleware.domain.inventory.planting.PlantingMetadata;
import org.generationcp.middleware.domain.inventory.planting.PlantingRequestDto;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.generationcp.middleware.service.api.dataset.ObservationUnitsSearchDTO;
import org.generationcp.middleware.service.api.inventory.PlantingService;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class PlantingServiceImpl implements PlantingService {

	private DaoFactory daoFactory;

	@Autowired
	private DatasetService datasetService;

	public PlantingServiceImpl() {
	}

	public PlantingServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public PlantingPreparationDTO searchPlantingPreparation(
		final Integer studyId,
		final Integer datasetId,
		final SearchCompositeDto<ObservationUnitsSearchDTO, Integer> searchDTO) {

		this.processSearchComposite(searchDTO);

		// observation units
		final List<ObservationUnitRow> observationUnitRows =
			this.datasetService.getObservationUnitRows(studyId, datasetId, searchDTO.getSearchRequest());
		Preconditions.checkArgument(!Util.isEmpty(observationUnitRows), "No results for observation units");

		// process observation units and build entries
		final Map<Integer, PlantingPreparationDTO.PlantingPreparationEntryDTO> entriesByEntryNo = new LinkedHashMap<>();
		// there might be some entries with the same gid
		final Map<Integer, List<PlantingPreparationDTO.PlantingPreparationEntryDTO>> entriesByGid = new HashMap<>();

		for (final ObservationUnitRow observationUnitRow : observationUnitRows) {
			if (entriesByEntryNo.get(observationUnitRow.getEntryNumber()) == null) {
				final PlantingPreparationDTO.PlantingPreparationEntryDTO entry = new PlantingPreparationDTO.PlantingPreparationEntryDTO();
				final Integer gid = observationUnitRow.getGid();

				entry.setEntryNo(observationUnitRow.getEntryNumber());
				entry.setGid(gid);
				entry.setDesignation(observationUnitRow.getDesignation());
				entry.setEntryType(observationUnitRow.getVariables().get("ENTRY_TYPE").getValue());

				entriesByEntryNo.put(observationUnitRow.getEntryNumber(), entry);

				if (entriesByGid.get(gid) == null) {
					entriesByGid.put(gid, new ArrayList<>());
				}
				entriesByGid.get(gid).add(entry);
			}

			final PlantingPreparationDTO.PlantingPreparationEntryDTO.ObservationUnitDTO observationUnit =
				new PlantingPreparationDTO.PlantingPreparationEntryDTO.ObservationUnitDTO();
			observationUnit.setNdExperimentId(observationUnitRow.getObservationUnitId());
			observationUnit.setObservationUnitId(observationUnitRow.getObsUnitId());
			observationUnit.setInstanceId(observationUnitRow.getTrialInstance());
			entriesByEntryNo.get(observationUnitRow.getEntryNumber()).getObservationUnits().add(observationUnit);
		}

		// stock
		final LotsSearchDto lotsSearchDto = new LotsSearchDto();
		lotsSearchDto.setGids(new ArrayList<>(entriesByGid.keySet()));
		lotsSearchDto.setStatus(0); // active
		lotsSearchDto.setMinAvailableBalance(Double.MIN_VALUE);
		final List<ExtendedLotDto> extendedLotDtos = this.daoFactory.getLotDao().searchLots(lotsSearchDto, null);
		if (!Util.isEmpty(extendedLotDtos)) {
			for (final ExtendedLotDto lot : extendedLotDtos) {
				final List<PlantingPreparationDTO.PlantingPreparationEntryDTO> entries = entriesByGid.get(lot.getGid());
				if (Util.isEmpty(entries)) {
					continue;
				}
				final PlantingPreparationDTO.PlantingPreparationEntryDTO.StockDTO stock =
					new PlantingPreparationDTO.PlantingPreparationEntryDTO.StockDTO();
				stock.setStockId(lot.getStockId());
				stock.setAvailableBalance(lot.getAvailableBalance());
				stock.setStorageLocation(lot.getLocationName());
				stock.setUnitId(lot.getUnitId());
				for (final PlantingPreparationDTO.PlantingPreparationEntryDTO entry : entries) {
					entry.getStockByStockId().put(lot.getStockId(), stock);
				}
			}
		}

		// transform final list
		final PlantingPreparationDTO plantingPreparationDTO = new PlantingPreparationDTO();
		plantingPreparationDTO.setEntries(new ArrayList<PlantingPreparationDTO.PlantingPreparationEntryDTO>(entriesByEntryNo.values()));

		return plantingPreparationDTO;
	}

	private void processSearchComposite(final SearchCompositeDto<ObservationUnitsSearchDTO, Integer> searchDTO) {
		if (searchDTO.getItemIds() != null && !searchDTO.getItemIds().isEmpty()) {
			final ObservationUnitsSearchDTO searchRequest = new ObservationUnitsSearchDTO();
			final ObservationUnitsSearchDTO.Filter filter = searchRequest.new Filter();
			filter.setFilteredNdExperimentIds(searchDTO.getItemIds());
			searchRequest.setFilter(filter);
			searchDTO.setSearchRequest(searchRequest);
		}
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
