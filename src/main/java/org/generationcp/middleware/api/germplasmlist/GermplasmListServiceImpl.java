package org.generationcp.middleware.api.germplasmlist;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.dao.GermplasmListDataDAO;
import org.generationcp.middleware.enumeration.SampleListType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.SampleList;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.util.Util;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
		final Map<Integer, String> preferrredNamesMap = this.germplasmDataManager.getPreferredNamesByGids(gids);

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
			final String preferredName = preferrredNamesMap.get(gid);
			final GermplasmListData germplasmListData = new GermplasmListData(null, germplasmList, gid, entry.getEntryNo(),
				entry.getEntryCode(), entry.getSeedSource(), preferredName, entry.getGroupName(),
				GermplasmListDataDAO.STATUS_ACTIVE, null);
			this.daoFactory.getGermplasmListDataDAO().save(germplasmListData);
		}

		return request;
	}

	@Override
	public Integer createSampleListFolder(final String folderName, final Integer parentId, final String username,
		final String programUUID) {

//		Preconditions.checkNotNull(folderName);
//		Preconditions.checkNotNull(parentId);
//		Preconditions.checkNotNull(username, "username can not be empty");
//		Preconditions.checkNotNull(programUUID);
//		Preconditions.checkArgument(!folderName.isEmpty(), "folderName can not be empty");
//		Preconditions.checkArgument(!programUUID.isEmpty(), "programUUID can not be empty");

		final GermplasmList parentList = Optional.of(this.daoFactory.getGermplasmListDAO().getById(parentId))
			.orElseThrow(() -> new MiddlewareException("Parent Folder does not exist"));

//		Preconditions.checkArgument(parentList.isFolder(), "Specified parentID is not a folder");

		final SampleList uniqueSampleListName =
			this.daoFactory.getGermplasmListDAO().getSampleListByParentAndName(folderName, parentList.getId(), programUUID);

		Preconditions.checkArgument(uniqueSampleListName == null, "Folder name should be unique within the same directory");

		try {

			final WorkbenchUser workbenchUser = this.userService.getUserByUsername(username);
			final SampleList sampleFolder = new SampleList();
			sampleFolder.setCreatedDate(new Date());
			sampleFolder.setCreatedByUserId(workbenchUser.getUserid());
			sampleFolder.setDescription(null);
			sampleFolder.setListName(folderName);
			sampleFolder.setNotes(null);
			sampleFolder.setHierarchy(parentList);
			sampleFolder.setType(SampleListType.FOLDER);
			sampleFolder.setProgramUUID(programUUID);
			return this.daoFactory.getSampleListDao().save(sampleFolder).getId();

		} catch (final HibernateException e) {
			throw new MiddlewareQueryException("Error in createSampleListFolder in SampleListServiceImpl: " + e.getMessage(), e);
		}

		return null;
	}

}
