
package org.generationcp.middleware.service.impl.study;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmListService;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class StudyGermplasmListServiceImpl implements StudyGermplasmListService {

	private final Session currentSession;

	public StudyGermplasmListServiceImpl(final Session currentSession) {
		this.currentSession = currentSession;
	}

	@Override
	public List<StudyGermplasmDto> getGermplasmList(int studyBusinessIdentifier) {

		final Criteria listDataCriteria =
				this.currentSession.createCriteria(ListDataProject.class).createAlias("list", "l")
						.add(Restrictions.eq("l.projectId", studyBusinessIdentifier));
		final List<ListDataProject> list = listDataCriteria.list();
		final List<StudyGermplasmDto> studyGermplasmDtos = new ArrayList<StudyGermplasmDto>();
		Integer index = 0;
		for (final ListDataProject listDataProject : list) {
			final StudyGermplasmDto studyGermplasmDto = new StudyGermplasmDto();
			studyGermplasmDto.setCross(listDataProject.getGroupName());
			studyGermplasmDto.setDesignation(listDataProject.getDesignation());
			studyGermplasmDto.setEntryCode(listDataProject.getEntryCode());
			studyGermplasmDto.setEntryNo(listDataProject.getEntryId().toString());
			studyGermplasmDto.setGermplasmId(listDataProject.getGermplasmId());
			++index;
			studyGermplasmDto.setPosition(index.toString());
			studyGermplasmDto.setSeedSource(listDataProject.getSeedSource());
			studyGermplasmDto.setEntryType(listDataProject.getList().getType());
			studyGermplasmDtos.add(studyGermplasmDto);
		}
		return studyGermplasmDtos;
	}

}
