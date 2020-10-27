package org.generationcp.middleware.api.germplasmlist;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.util.Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;

@Transactional
@Service
public class GermplasmListServiceImpl implements GermplasmListService {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);

	private final DaoFactory daoFactory;

	public GermplasmListServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public GermplasmListGeneratorDTO create(final GermplasmListGeneratorDTO request, final int status, final String programUUID,
		final WorkbenchUser loggedInUser) {

		final Integer currentUserId = loggedInUser.getUserid();
		final GermplasmList parent = request.getParentFolderId() != null ?
			this.daoFactory.getGermplasmListDAO().getById(Integer.valueOf(request.getParentFolderId()), false) : null;
		final String description = request.getDescription() != null ? request.getDescription() : StringUtils.EMPTY;

		// save list
		GermplasmList germplasmList = new GermplasmList(null, request.getName(), Long.valueOf(DATE_FORMAT.format(request.getDate())),
			request.getType(), currentUserId, description, parent, status, request.getNotes());
		germplasmList.setProgramUUID(programUUID);
		germplasmList = this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);
		request.setId(germplasmList.getId());

		// save germplasm list data
		for (final GermplasmListGeneratorDTO.GermplasmEntryDTO entry : request.getEntries()) {
			final GermplasmListData germplasmListData = new GermplasmListData(null, germplasmList, entry.getGid(), entry.getEntryNo(),
				entry.getEntryCode(), entry.getSeedSource(), entry.getDesignation(), entry.getGroupName(),
				GermplasmListDataDAO.STATUS_ACTIVE, null);
			this.daoFactory.getGermplasmListDataDAO().save(germplasmListData);
		}

		return request;
	}
}
