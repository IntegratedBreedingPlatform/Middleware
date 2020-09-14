
package org.generationcp.middleware.service.impl.study;

import com.google.common.collect.Lists;
import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.domain.etl.MeasurementVariable;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.domain.ontology.VariableType;
import org.generationcp.middleware.domain.study.StudyEntrySearchDto;
import org.generationcp.middleware.enumeration.DatasetTypeEnum;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.service.api.dataset.DatasetService;
import org.generationcp.middleware.service.api.study.StudyEntryDto;
import org.generationcp.middleware.service.api.study.StudyEntryPropertyData;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmService;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Transactional
public class StudyGermplasmServiceImpl implements StudyGermplasmService {

	@Resource
	private InventoryDataManager inventoryDataManager;

	@Resource
	private DatasetService datasetService;

	private final DaoFactory daoFactory;

	public StudyGermplasmServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<StudyGermplasmDto> getGermplasm(final int studyBusinessIdentifier) {

		final List<StockModel> stockModelList = this.daoFactory.getStockDao().getStocksForStudy(studyBusinessIdentifier);
		final List<Integer> gids = new ArrayList<>();
		for (final StockModel stockModel : stockModelList) {
			gids.add(stockModel.getGermplasm().getGid());
		}
		final Map<Integer, String> stockIdsMap = this.daoFactory.getTransactionDAO().retrieveStockIds(gids);
		final List<StudyGermplasmDto> studyGermplasmDtos = new ArrayList<>();
		int index = 0;
		for (final StockModel stockModel : stockModelList) {
			final StudyGermplasmDto studyGermplasmDto = new StudyGermplasmDto();
			studyGermplasmDto.setEntryId(stockModel.getStockId());
			studyGermplasmDto.setCross(this.findStockPropValue(TermId.CROSS.getId(), stockModel.getProperties()));
			studyGermplasmDto.setDesignation(stockModel.getName());
			studyGermplasmDto.setEntryCode(stockModel.getValue());
			studyGermplasmDto.setEntryNumber(Integer.valueOf(stockModel.getUniqueName()));
			studyGermplasmDto.setGermplasmId(stockModel.getGermplasm().getGid());
			studyGermplasmDto.setPosition(String.valueOf(++index));
			studyGermplasmDto.setSeedSource(this.findStockPropValue(TermId.SEED_SOURCE.getId(), stockModel.getProperties()));
			studyGermplasmDto.setCheckType(Integer.valueOf(this.findStockPropValue(TermId.ENTRY_TYPE.getId(), stockModel.getProperties())));
			studyGermplasmDto.setStockIds(stockIdsMap.getOrDefault(stockModel.getGermplasm().getGid(), ""));
			final String groupGid = this.findStockPropValue(TermId.GROUPGID.getId(), stockModel.getProperties());
			studyGermplasmDto.setGroupId(groupGid != null ? Integer.valueOf(groupGid) : 0);
			studyGermplasmDtos.add(studyGermplasmDto);
		}
		return studyGermplasmDtos;
	}

	@Override
	public List<StudyEntryDto> getStudyEntries(final int studyId, final StudyEntrySearchDto entrySearchDto, final Pageable pageable) {

		final Integer plotDatasetId =
			datasetService.getDatasets(studyId, new HashSet<>(Arrays.asList(DatasetTypeEnum.PLOT_DATA.getId()))).get(0).getDatasetId();

		final List<MeasurementVariable> entryDescriptors =
			this.datasetService.getObservationSetVariables(plotDatasetId, Lists
				.newArrayList(VariableType.GERMPLASM_DESCRIPTOR.getId()));

		//Remove the ones that are stored in stock and that in the future will not be descriptors
		final List<Integer> termsToRemove = Lists
			.newArrayList(TermId.ENTRY_CODE.getId(), TermId.DESIG.getId(), TermId.ENTRY_NO.getId(), TermId.GID.getId(),
				TermId.OBS_UNIT_ID.getId());
		for (Iterator<MeasurementVariable> i = entryDescriptors.iterator(); i.hasNext(); ) {
			final MeasurementVariable measurementVariable = i.next();
			if (termsToRemove.contains(measurementVariable.getTermId())) {
				i.remove();
			}
		}

		final List<StudyEntryDto> studyEntryDtos =
			this.daoFactory.getStockDao().getStudyEntries(studyId, entryDescriptors, entrySearchDto, pageable);
		return studyEntryDtos;
	}

	@Override
	public List<StudyGermplasmDto> getGermplasmFromPlots(final int studyBusinessIdentifier, final Set<Integer> plotNos) {
		return this.daoFactory.getStockDao().getStudyGermplasmDtoList(studyBusinessIdentifier, plotNos);
	}

	@Override
	public long countStudyEntries(final int studyId) {
		return this.daoFactory.getStockDao().countStocksForStudy(studyId);
	}

	@Override
	public void deleteStudyEntries(final int studyId) {
		this.daoFactory.getStockDao().deleteStocksForStudy(studyId);
	}

	@Override
	public List<StudyGermplasmDto> saveStudyGermplasm(final Integer studyId, final List<StudyGermplasmDto> studyGermplasmDtoList) {
		final List<StudyGermplasmDto> list = new ArrayList<>();
		for (final StudyGermplasmDto studyGermplasm : studyGermplasmDtoList) {
			final StockModel stockModel = this.daoFactory.getStockDao().saveOrUpdate(new StockModel(studyId, studyGermplasm));
			list.add(new StudyGermplasmDto(stockModel));
		}
		return list;
	}

	@Override
	public long countStudyGermplasmByEntryTypeIds(final int studyId, final List<String> systemDefinedEntryTypeIds) {
		return this.daoFactory.getStockDao().countStocksByStudyAndEntryTypeIds(studyId, systemDefinedEntryTypeIds);
	}

	@Override
	public Optional<StudyGermplasmDto> getStudyGermplasm(final int studyId, final int entryId) {
		final StockModel stock = this.daoFactory.getStockDao().getById(entryId);
		if (stock != null) {
			if (studyId != stock.getProject().getProjectId()) {
				throw new MiddlewareRequestException("", "invalid.entryid.for.study");
			}
			return Optional.of(new StudyGermplasmDto(stock));
		}
		return Optional.empty();
	}

	@Override
	public StudyGermplasmDto replaceStudyGermplasm(final int studyId, final int entryId, final int gid, final String crossExpansion) {
		// Check first that new GID is valid
		final Germplasm newGermplasm = this.daoFactory.getGermplasmDao().getById(gid);
		if (newGermplasm == null) {
			throw new MiddlewareException("Invalid GID: " + gid);
		}
		final StockDao stockDao = this.daoFactory.getStockDao();
		// Copy from old entry: entry type, entry #, entry code
		final StockModel stock = stockDao.getById(entryId);
		if (stock == null) {
			throw new MiddlewareRequestException("", "invalid.entryid");
		} else if (studyId != stock.getProject().getProjectId()) {
			throw new MiddlewareRequestException("", "invalid.entryid.for.study");
		} else if (stock.getGermplasm().getGid() == gid) {
			throw new MiddlewareRequestException("", "new.gid.matches.old.gid");
		}
		final StudyGermplasmDto newStudyGermplasm = new StudyGermplasmDto();
		newStudyGermplasm.setEntryNumber(Integer.valueOf(stock.getUniqueName()));
		Optional<StockProperty> entryType =
			stock.getProperties().stream().filter(prop -> TermId.ENTRY_TYPE.getId() == (prop.getTypeId())).findFirst();
		entryType.ifPresent(entry -> newStudyGermplasm.setEntryType(entry.getValue()));
		newStudyGermplasm.setGermplasmId(gid);
		newStudyGermplasm.setEntryCode(stock.getValue());
		final Name preferredName = newGermplasm.getPreferredName();
		newStudyGermplasm.setDesignation(preferredName != null ? preferredName.getNval() : "");
		newStudyGermplasm.setCross(crossExpansion);
		// Save also Group GID
		newStudyGermplasm.setGroupId(newGermplasm.getMgid());

		final StudyGermplasmDto savedStudyGermplasm = this.saveStudyGermplasm(studyId, Collections.singletonList(newStudyGermplasm)).get(0);
		stockDao.replaceExperimentStocks(entryId, savedStudyGermplasm.getEntryId());
		// Finally delete old stock
		stockDao.makeTransient(stock);
		return savedStudyGermplasm;
	}

	@Override
	public void updateStudyEntryProperty(final int studyId, final StudyEntryPropertyData studyEntryPropertyData) {
		final StockProperty stockProperty = this.daoFactory.getStockPropertyDao().getById(studyEntryPropertyData.getStudyEntryPropertyId());
		if (stockProperty != null) {
			stockProperty.setValue(studyEntryPropertyData.getValue());
			this.daoFactory.getStockPropertyDao().saveOrUpdate(stockProperty);
		}
	}

	@Override
	public Optional<StudyEntryPropertyData> getStudyEntryPropertyData(final int studyEntryPropertyId) {
		final StockProperty stockProperty = this.daoFactory.getStockPropertyDao().getById(studyEntryPropertyId);
		if (stockProperty != null) {
			final StudyEntryPropertyData studyEntryPropertyData = new StudyEntryPropertyData();
			studyEntryPropertyData.setVariableId(stockProperty.getTypeId());
			studyEntryPropertyData.setStudyEntryPropertyId(stockProperty.getStockPropId());
			studyEntryPropertyData.setValue(stockProperty.getValue());
			return Optional.of(studyEntryPropertyData);
		}
		return Optional.empty();
	}

	private String findStockPropValue(final Integer termId, final Set<StockProperty> properties) {
		if (properties != null) {
			for (final StockProperty property : properties) {
				if (termId.equals(property.getTypeId())) {
					return property.getValue();
				}
			}
		}
		return null;
	}

}
