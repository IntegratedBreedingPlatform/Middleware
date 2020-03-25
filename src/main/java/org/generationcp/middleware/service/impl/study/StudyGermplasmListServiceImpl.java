
package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.generationcp.middleware.service.api.study.StudyGermplasmListService;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StudyGermplasmListServiceImpl implements StudyGermplasmListService {

	private final Session currentSession;
	private final DaoFactory daoFactory;

	public StudyGermplasmListServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
		this.currentSession = sessionProvider.getSession();
	}

	@Override
	public List<StudyGermplasmDto> getGermplasmList(final int studyBusinessIdentifier) {

		final Criteria listDataCriteria =
			this.currentSession.createCriteria(ListDataProject.class).createAlias("list", "l")
				.add(Restrictions.eq("l.projectId", studyBusinessIdentifier));
		listDataCriteria.add(Restrictions.eq("l.type", GermplasmListType.STUDY.name()));
		listDataCriteria.addOrder(Order.asc("entryId"));
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

	@Override
	public List<StudyGermplasmDto> getGermplasmListFromPlots(final int studyBusinessIdentifier, final Set<Integer> plotNos) {
		return daoFactory.getStockDao().getStudyGermplasmDtoList(studyBusinessIdentifier, plotNos);
	}

}
