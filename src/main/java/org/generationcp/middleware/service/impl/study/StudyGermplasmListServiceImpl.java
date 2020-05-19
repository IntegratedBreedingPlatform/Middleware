
package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.oms.TermId;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.dms.StockModel;
import org.generationcp.middleware.pojos.dms.StockProperty;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmListService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Transactional
// FIXME: Merge this class to StockModelService.
public class StudyGermplasmListServiceImpl implements StudyGermplasmListService {

	private final DaoFactory daoFactory;

	public StudyGermplasmListServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<StudyGermplasmDto> getGermplasmList(final int studyBusinessIdentifier) {

		final List<StockModel> stockModelList = this.daoFactory.getStockDao().getStocksForStudy(studyBusinessIdentifier);

		final List<StudyGermplasmDto> studyGermplasmDtos = new ArrayList<>();
		int index = 0;
		for (final StockModel stockModel : stockModelList) {
			final StudyGermplasmDto studyGermplasmDto = new StudyGermplasmDto();
			studyGermplasmDto.setCross(stockModel.getGermplasm().getCrossName()); // TODO: IBP-3697 Verify mapping
			studyGermplasmDto.setDesignation(stockModel.getName());
			studyGermplasmDto.setEntryCode(stockModel.getValue());
			studyGermplasmDto.setEntryNumber(Integer.valueOf(stockModel.getUniqueName()));
			studyGermplasmDto.setGermplasmId(stockModel.getGermplasm().getGid());
			++index;
			studyGermplasmDto.setPosition(String.valueOf(index));
			studyGermplasmDto.setSeedSource(stockModel.getGermplasm().getCrossName()); // TODO: IBP-3697 Verify mapping
			studyGermplasmDto.setEntryType(""); // TODO: IBP-3697 Verify mapping
			studyGermplasmDto.setCheckType(Integer.valueOf(this.findStockPropValue(TermId.ENTRY_TYPE.getId(), stockModel.getProperties())));
			studyGermplasmDto.setStockIds(""); // TODO: IBP-3697 Verify mapping
			studyGermplasmDto.setGroupId(0); // TODO: IBP-3697 Verify mapping
			studyGermplasmDtos.add(studyGermplasmDto);
		}
		return studyGermplasmDtos;
	}

	@Override
	public List<StudyGermplasmDto> getGermplasmListFromPlots(final int studyBusinessIdentifier, final Set<Integer> plotNos) {
		return this.daoFactory.getStockDao().getStudyGermplasmDtoList(studyBusinessIdentifier, plotNos);
	}

	private String findStockPropValue(final int termId, final Set<StockProperty> properties) {
		if (properties != null) {
			for (final StockProperty property : properties) {
				if (termId == property.getTypeId()) {
					return property.getValue();
				}
			}
		}
		return null;
	}

}
