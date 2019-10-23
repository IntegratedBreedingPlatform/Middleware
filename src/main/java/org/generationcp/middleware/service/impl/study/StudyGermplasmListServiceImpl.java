
package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmListService;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;

public class StudyGermplasmListServiceImpl implements StudyGermplasmListService {

	private final Session currentSession;

	public StudyGermplasmListServiceImpl(final Session currentSession) {
		this.currentSession = currentSession;
	}

	@Override
	public List<StudyGermplasmDto> getGermplasmList(final int studyBusinessIdentifier) {

		final Criteria listDataCriteria =
			this.currentSession.createCriteria(ListDataProject.class).createAlias("list", "l")
				.add(Restrictions.eq("l.projectId", studyBusinessIdentifier));
		final List<ListDataProject> list = listDataCriteria.list();
		final List<StudyGermplasmDto> studyGermplasmDtos = new ArrayList<>();
		Integer index = 0;
		for (final ListDataProject listDataProject : list) {
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

}
