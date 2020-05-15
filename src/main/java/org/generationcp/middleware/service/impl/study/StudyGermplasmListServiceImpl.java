
package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmListService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Transactional
public class StudyGermplasmListServiceImpl implements StudyGermplasmListService {

	private final DaoFactory daoFactory;

	public StudyGermplasmListServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<StudyGermplasmDto> getGermplasmList(final int studyBusinessIdentifier) {

		final List<ListDataProject> listEntries = this.daoFactory.getListDataProjectDAO().getGermplasmList(studyBusinessIdentifier);

		final List<StudyGermplasmDto> studyGermplasmDtos = new ArrayList<>();
		Integer index = 0;
		for (final ListDataProject listDataProject : listEntries) {
			final StudyGermplasmDto studyGermplasmDto = new StudyGermplasmDto();
			studyGermplasmDto.setCross(listDataProject.getGroupName());
			studyGermplasmDto.setDesignation(listDataProject.getDesignation());
			studyGermplasmDto.setEntryCode(listDataProject.getEntryCode());
			studyGermplasmDto.setEntryNumber(listDataProject.getEntryId());
			studyGermplasmDto.setGermplasmId(listDataProject.getGermplasmId());
			++index;
			studyGermplasmDto.setPosition(index.toString());
			studyGermplasmDto.setSeedSource(listDataProject.getSeedSource());
			studyGermplasmDto.setEntryType(listDataProject.getList().getType());
			studyGermplasmDto.setCheckType(listDataProject.getCheckType());
			studyGermplasmDto.setStockIds(listDataProject.getStockIDs());
			studyGermplasmDto.setGroupId(listDataProject.getGroupId());
			studyGermplasmDtos.add(studyGermplasmDto);
		}
		return studyGermplasmDtos;
	}

	@Override
	public List<StudyGermplasmDto> getGermplasmListFromPlots(final int studyBusinessIdentifier, final Set<Integer> plotNos) {
		return daoFactory.getStockDao().getStudyGermplasmDtoList(studyBusinessIdentifier, plotNos);
	}

}
