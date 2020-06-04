
package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.dao.dms.StockDao;
import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.InventoryDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Transactional
public class StudyGermplasmServiceImpl implements StudyGermplasmService {

	@Resource
	private InventoryDataManager inventoryDataManager;

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
			studyGermplasmDto.setCross(this.findStockPropValue(TermId.CROSS.getId(), stockModel.getProperties()));
			studyGermplasmDto.setDesignation(stockModel.getName());
			studyGermplasmDto.setEntryCode(stockModel.getValue());
			studyGermplasmDto.setEntryNumber(Integer.valueOf(stockModel.getUniqueName()));
			studyGermplasmDto.setGermplasmId(stockModel.getGermplasm().getGid());
			studyGermplasmDto.setPosition(String.valueOf(++index));
			studyGermplasmDto.setSeedSource(this.findStockPropValue(TermId.SEED_SOURCE.getId(), stockModel.getProperties()));
			studyGermplasmDto.setCheckType(Integer.valueOf(this.findStockPropValue(TermId.ENTRY_TYPE.getId(), stockModel.getProperties())));
			studyGermplasmDto.setStockIds(stockIdsMap.getOrDefault(stockModel.getGermplasm().getGid(), ""));
			studyGermplasmDto.setGroupId(stockModel.getGermplasm().getMgid());
			studyGermplasmDtos.add(studyGermplasmDto);
		}
		return studyGermplasmDtos;
	}

	@Override
	public List<StudyGermplasmDto> getGermplasmFromPlots(final int studyBusinessIdentifier, final Set<Integer> plotNos) {
		return this.daoFactory.getStockDao().getStudyGermplasmDtoList(studyBusinessIdentifier, plotNos);
	}

	@Override
	public long countStudyGermplasm(final int studyId) {
		return this.daoFactory.getStockDao().countStocksForStudy(studyId);
	}

	@Override
	public void deleteStudyGermplasm(final int studyId) {
		this.daoFactory.getStockDao().deleteStocksForStudy(studyId);
	}

	@Override
	public List<StudyGermplasmDto> saveStudyGermplasm(final Integer studyId, final List<StudyGermplasmDto> studyGermplasmDtoList) {
		final List<StudyGermplasmDto> list = new ArrayList<>();
		for (final StudyGermplasmDto studyGermplasm : studyGermplasmDtoList) {
			final StockModel stockModel = this.daoFactory.getStockDao().saveOrUpdate(new StockModel(studyId, studyGermplasm));
			list.add(new StudyGermplasmDto(stockModel));
		}
		return  list;
	}

	@Override
	public long countStudyGermplasmByEntryTypeIds(final int studyId, final List<String> systemDefinedEntryTypeIds) {
		return this.daoFactory.getStockDao().countStocksByStudyAndEntryTypeIds(studyId, systemDefinedEntryTypeIds);
	}

	@Override
	public Map<Integer, String> getInventoryStockIdMap(final List<StudyGermplasmDto> studyGermplasmDtoList) {

		final List<Integer> gids = new ArrayList<>();
		if (studyGermplasmDtoList != null && !studyGermplasmDtoList.isEmpty()) {
			for (final StudyGermplasmDto studyGermplasmDto : studyGermplasmDtoList) {
				gids.add(studyGermplasmDto.getGermplasmId());
			}
		}

		return this.inventoryDataManager.retrieveStockIds(gids);

	}

	@Override
	public boolean isValidStudyGermplasm(final int studyId, final int entryId) {
		final StockModel stock = this.daoFactory.getStockDao().getById(entryId);
		if (stock != null) {
			return studyId == stock.getProject().getProjectId();
		}
		return false;
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
