package org.generationcp.middleware.api.brapi;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.ContextHolder;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.list.GermplasmListImportRequestDTO;
import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.api.germplasmlist.data.GermplasmListDataService;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDataDAO;
import org.generationcp.middleware.domain.search_request.brapi.v2.GermplasmListSearchRequestDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.GermplasmListExternalReference;
import org.generationcp.middleware.service.api.GermplasmListDTO;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.service.api.user.UserService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class GermplasmListServiceBrapiImpl implements GermplasmListServiceBrapi {

	@Autowired
	private UserService userService;

	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private CrossExpansionProperties crossExpansionProperties;

	@Autowired
	private GermplasmService germplasmService;

	@Autowired
	private GermplasmListDataService germplasmListDataService;

	private final DaoFactory daoFactory;

	public GermplasmListServiceBrapiImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<GermplasmListDTO> searchGermplasmListDTOs(final GermplasmListSearchRequestDTO searchRequestDTO, final Pageable pageable) {
		final List<GermplasmListDTO> lists = this.daoFactory.getGermplasmListDAO().searchGermplasmListDTOs(searchRequestDTO, pageable);
		if (!CollectionUtils.isEmpty(lists)) {
			final List<Integer> userIds =
				lists.stream().map(list -> Integer.valueOf(list.getListOwnerPersonDbId())).collect(Collectors.toList());
			final Map<Integer, String> userIDFullNameMap = this.userService.getUserIDFullNameMap(userIds);
			final List<Integer> listIds = new ArrayList<>(lists.stream().map(list -> Integer.valueOf(list.getListDbId()))
				.collect(Collectors.toSet()));
			final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
				this.daoFactory.getGermplasmListExternalReferenceDAO().getExternalReferences(listIds).stream()
					.collect(groupingBy(ExternalReferenceDTO::getEntityId));
			for (final GermplasmListDTO listDTO : lists) {
				listDTO.setListOwnerName(userIDFullNameMap.get(Integer.valueOf(listDTO.getListOwnerPersonDbId())));
				listDTO.setExternalReferences(externalReferencesMap.get(listDTO.getListDbId()));
			}
		}
		return lists;
	}

	@Override
	public long countGermplasmListDTOs(final GermplasmListSearchRequestDTO searchRequestDTO) {
		return this.daoFactory.getGermplasmListDAO().countGermplasmListDTOs(searchRequestDTO);
	}

	@Override
	public List<GermplasmListDTO> saveGermplasmListDTOs(final List<GermplasmListImportRequestDTO> importRequestDTOS) {
		final List<String> savedListIds = new ArrayList<>();

		final List<String> germplasmUUIDs = importRequestDTOS.stream()
			.map(GermplasmListImportRequestDTO::getData).filter(data -> !CollectionUtils.isEmpty(data))
			.flatMap(Collection::stream).collect(Collectors.toList());
		final List<Germplasm> data = this.daoFactory.getGermplasmDao().getGermplasmByGUIDs(germplasmUUIDs);
		final Map<String, Integer> guuidGidMap = data.stream()
			.collect(Collectors.toMap(germplasm -> germplasm.getGermplasmUUID().toUpperCase(), Germplasm::getGid));

		final List<Integer> gids = data.stream().map(Germplasm::getGid).collect(Collectors.toList());
		final Map<Integer, String> crossExpansions =
			this.pedigreeService.getCrossExpansionsBulk(new HashSet<>(gids), null, this.crossExpansionProperties);
		final Map<Integer, String> plotCodeValuesByGIDs = this.germplasmService.getPlotCodeValues(new HashSet<>(gids));

		for(final GermplasmListImportRequestDTO importRequestDTO: importRequestDTOS) {
			final GermplasmList germplasmList = this.saveGermplasmList(importRequestDTO);
			this.saveGermplasmListData(germplasmList, importRequestDTO.getData(), crossExpansions, plotCodeValuesByGIDs, guuidGidMap);

			// Add default columns
			this.germplasmListDataService.saveDefaultView(germplasmList);

			savedListIds.add(germplasmList.getId().toString());
		}
		final GermplasmListSearchRequestDTO germplasmListSearchRequestDTO = new GermplasmListSearchRequestDTO();
		germplasmListSearchRequestDTO.setListDbIds(savedListIds);
		return this.searchGermplasmListDTOs(germplasmListSearchRequestDTO, null);
	}

	private void saveGermplasmListData(final GermplasmList germplasmList, final List<String> guuids,
		final Map<Integer, String> crossExpansions,	final Map<Integer, String> plotCodeValuesByGIDs,
		final Map<String, Integer> guuidGidMap) {
		if(!CollectionUtils.isEmpty(guuids)) {
			int entryNo = 1;
			for (final String guid : guuids) {
				if (guuidGidMap.containsKey(guid.toUpperCase())) {
					final Integer gid = guuidGidMap.get(guid.toUpperCase());
					final Integer currentEntryNo = entryNo++;
					final GermplasmListData germplasmListData = new GermplasmListData(null, germplasmList, gid, currentEntryNo,
						plotCodeValuesByGIDs.get(gid), crossExpansions.get(gid), GermplasmListDataDAO.STATUS_ACTIVE, null);
					this.daoFactory.getGermplasmListDataDAO().save(germplasmListData);
				}
			}
		}
	}

	private GermplasmList saveGermplasmList(final GermplasmListImportRequestDTO request) {
		final String description = request.getListDescription() != null ? request.getListDescription() : StringUtils.EMPTY;
		final Date currentDate = request.getDateCreated() == null ? new Date() : Util.tryParseDate(request.getDateCreated(), Util.FRONTEND_DATE_FORMAT);
		final Long date = Long.valueOf(Util.getSimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT).format(currentDate));
		final Integer userId = request.getListOwnerPersonDbId() == null ? ContextHolder.getLoggedInUserId() : Integer.valueOf(request.getListOwnerPersonDbId());
		GermplasmList germplasmList = new GermplasmList(null, request.getListName(), date,	GermplasmList.LIST_TYPE,
			userId, description, null, GermplasmList.Status.LOCKED_LIST.getCode(),
			null, null);
		this.setGermplasmListExternalReferences(request, germplasmList);

		germplasmList = this.daoFactory.getGermplasmListDAO().saveOrUpdate(germplasmList);
		return germplasmList;
	}

	private void setGermplasmListExternalReferences(final GermplasmListImportRequestDTO request, final GermplasmList germplasmList) {
		if (request.getExternalReferences() != null) {
			final List<GermplasmListExternalReference> references = new ArrayList<>();
			request.getExternalReferences().forEach(reference -> {
				final GermplasmListExternalReference externalReference =
					new GermplasmListExternalReference(germplasmList, reference.getReferenceID(), reference.getReferenceSource());
				references.add(externalReference);
			});
			germplasmList.setExternalReferences(references);
		}
	}
}
