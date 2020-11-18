package org.generationcp.middleware.api.germplasmlist;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Transactional
@Service
public class GermplasmListServiceImpl implements GermplasmListService {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT);

	private final DaoFactory daoFactory;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

	public GermplasmListServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public GermplasmListGeneratorDTO create(final GermplasmListGeneratorDTO request, final int status, final String programUUID,
		final WorkbenchUser loggedInUser) {

		final List<Integer> gids = request.getEntries()
			.stream().map(GermplasmListGeneratorDTO.GermplasmEntryDTO::getGid).collect(Collectors.toList());
		final Map<Integer, String> preferredNamesMap = this.germplasmDataManager.getPreferredNamesByGids(gids);
		final Map<Integer, List<Name>> namesByGid = this.daoFactory.getNameDao().getNamesByGids(gids)
			.stream().collect(groupingBy(Name::getGermplasmId));

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
			final Integer gid = entry.getGid();
			final String preferredName = preferredNamesMap.get(gid);
			final List<Name> names = namesByGid.get(gid);
			Preconditions.checkArgument(preferredName != null || names != null, "No name found for gid=" + gid);
			final String designation = preferredName != null ? preferredName : names.get(0).getNval();
			final GermplasmListData germplasmListData = new GermplasmListData(null, germplasmList, gid, entry.getEntryNo(),
				entry.getEntryCode(), entry.getSeedSource(), designation, entry.getGroupName(),
				GermplasmListDataDAO.STATUS_ACTIVE, null);
			this.daoFactory.getGermplasmListDataDAO().save(germplasmListData);
		}

		return request;
	}

	@Override
	public Optional<GermplasmList> getGermplasmListById(final Integer id) {
		return Optional.ofNullable(this.daoFactory.getGermplasmListDAO().getById(id));
	}

	@Override
	public Optional<GermplasmList> getGermplasmListByIdAndProgramUUID(final Integer id, final String programUUID) {
		return Optional.ofNullable(this.daoFactory.getGermplasmListDAO().getByIdAndProgramUUID(id, programUUID));
	}

	@Override
	public Optional<GermplasmList> getGermplasmListByParentAndName(final String germplasmListName, final Integer parentId,
		final String programUUID) {
		return Optional.ofNullable(this.daoFactory.getGermplasmListDAO().getGermplasmListByParentAndName(germplasmListName, parentId, programUUID));
	}

	@Override
	public Integer createGermplasmListFolder(final Integer userId, final String folderName, final Integer parentId,
		final String programUUID) {

		final GermplasmList parentFolder = (Objects.isNull(parentId)) ? null :
			this.getGermplasmListById(parentId).orElseThrow(() -> new MiddlewareException("Parent Folder does not exist"));

		final GermplasmList folder = new GermplasmList();
		folder.setDate(Util.getCurrentDateAsLongValue());
		folder.setUserId(userId);
		folder.setDescription(folderName);
		folder.setName(folderName);
		folder.setNotes(null);
		folder.setParent(parentFolder);
		folder.setType(GermplasmList.FOLDER_TYPE);
		folder.setProgramUUID(programUUID);
		folder.setStatus(GermplasmList.Status.FOLDER.getCode());
		return this.daoFactory.getGermplasmListDAO().save(folder).getId();
	}

	@Override
	public Integer updateGermplasmListFolder(final Integer userId, final String folderName, final Integer folderId,
		final String programUUID) {

		final GermplasmList folder =
			this.getGermplasmListById(folderId).orElseThrow(() -> new MiddlewareException("Folder does not exist"));

		folder.setName(folderName);
		folder.setDescription(folderName);

		return this.daoFactory.getGermplasmListDAO().save(folder).getId();
	}

	@Override
	public Integer moveGermplasmListFolder(final Integer germplasmListId, final Integer newParentFolderId,
		final String programUUID) {

		final GermplasmList listToMove = this.getGermplasmListById(germplasmListId)
			.orElseThrow(() -> new MiddlewareRequestException("", "list.folder.not.found"));

		final GermplasmList newParentFolder = (Objects.isNull(newParentFolderId)) ? null :
			this.getGermplasmListById(newParentFolderId).orElseThrow(() -> new MiddlewareRequestException("", "list.parent.folder.not.found"));

		listToMove.setProgramUUID(programUUID);
		listToMove.setParent(newParentFolder);

		try {
			return this.daoFactory.getGermplasmListDAO().saveOrUpdate(listToMove).getId();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in moveGermplasmList in GermplasmListServiceImpl: " + e.getMessage(), e);
		}
	}

	@Override
	public void deleteGermplasmListFolder(final Integer folderId) {
		final GermplasmList folder = this.getGermplasmListById(folderId)
			.orElseThrow(() -> new MiddlewareRequestException("", "list.folder.not.found"));

		this.daoFactory.getGermplasmListDAO().makeTransient(folder);
	}

}
