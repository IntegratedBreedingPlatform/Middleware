package org.generationcp.middleware.api.brapi;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.brapi.v1.germplasm.GermplasmDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.list.GermplasmListImportRequestDTO;
import org.generationcp.middleware.api.germplasm.GermplasmService;
import org.generationcp.middleware.dao.germplasmlist.GermplasmListDataDAO;
import org.generationcp.middleware.domain.search_request.brapi.v2.GermplasmListSearchRequestDTO;
import org.generationcp.middleware.domain.search_request.brapi.v2.GermplasmSearchRequest;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.GermplasmListExternalReference;
import org.generationcp.middleware.pojos.Name;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class GermplasmListServiceBrapiImpl implements GermplasmListServiceBrapi {

	@Autowired
	private UserService userService;

	@Autowired
	private GermplasmServiceBrapi germplasmServiceBrapi;

	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private CrossExpansionProperties crossExpansionProperties;

	@Autowired
	private GermplasmService germplasmService;

	@Autowired
	private GermplasmDataManager germplasmDataManager;

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
	public List<GermplasmListDTO> saveGermplasmListDTOs(List<GermplasmListImportRequestDTO> importRequestDTOS) {
		final List<String> savedListIds = new ArrayList<>();

		final List<String> germplasmUUIDs = importRequestDTOS.stream()
			.filter(importRequestDTO -> !CollectionUtils.isEmpty(importRequestDTO.getData()))
			.map(importRequestDTO -> importRequestDTO.getData()).flatMap(Collection::stream).collect(Collectors.toList());

		final GermplasmSearchRequest germplasmSearchRequest = new GermplasmSearchRequest();
		germplasmSearchRequest.setGermplasmDbIds(germplasmUUIDs);
		final List<GermplasmDTO> germplasmDTOS = this.germplasmServiceBrapi.searchGermplasmDTO(germplasmSearchRequest, null);
		final Map<String, GermplasmDTO> germplasmDTOMap = germplasmDTOS.stream()
			.collect(Collectors.toMap(germplasmDTO -> germplasmDTO.getGermplasmDbId().toUpperCase(), Function.identity()));

		final List<Integer> gids = germplasmDTOS.stream().map(germplasmDTO -> Integer.valueOf(germplasmDTO.getGid()))
			.collect(Collectors.toList());
		final Map<Integer, String> crossExpansions =
			this.pedigreeService.getCrossExpansionsBulk(new HashSet<>(gids), null, this.crossExpansionProperties);
		final Map<Integer, String> plotCodeValuesByGIDs = this.germplasmService.getPlotCodeValues(new HashSet<>(gids));
		final Map<Integer, String> preferredNamesMap = this.germplasmDataManager.getPreferredNamesByGids(gids);
		final Map<Integer, List<Name>> namesByGid = this.daoFactory.getNameDao().getNamesByGids(gids)
			.stream().collect(groupingBy(n -> n.getGermplasm().getGid()));

		for(GermplasmListImportRequestDTO importRequestDTO: importRequestDTOS) {
			final GermplasmList germplasmList = this.saveGermplasmList(importRequestDTO);
			this.saveGermplasmListData(germplasmList, importRequestDTO.getData(), crossExpansions, plotCodeValuesByGIDs, germplasmDTOMap,
				preferredNamesMap, namesByGid);
			savedListIds.add(germplasmList.getId().toString());
		}
		final GermplasmListSearchRequestDTO germplasmListSearchRequestDTO = new GermplasmListSearchRequestDTO();
		germplasmListSearchRequestDTO.setListDbIds(savedListIds);
		return this.searchGermplasmListDTOs(germplasmListSearchRequestDTO, null);
	}

	private void saveGermplasmListData(final GermplasmList germplasmList, final List<String> guids,
		final Map<Integer, String> crossExpansions,	final Map<Integer, String> plotCodeValuesByGIDs,
		final Map<String, GermplasmDTO> germplasmDTOMap, final Map<Integer, String> preferredNamesMap,
		final Map<Integer, List<Name>> namesByGid) {
		if(!CollectionUtils.isEmpty(guids)) {
			int entryNo = 1;
			for (final String guid : guids) {
				if (germplasmDTOMap.containsKey(guid.toUpperCase())) {
					final Integer gid = Integer.valueOf(germplasmDTOMap.get(guid.toUpperCase()).getGid());
					final String preferredName = preferredNamesMap.get(gid);
					final List<Name> names = namesByGid.get(gid);
					Preconditions.checkArgument(preferredName != null || names != null, "No name found for gid=" + gid);
					final String designation = preferredName != null ? preferredName : names.get(0).getNval();
					final Integer currentEntryNo = entryNo++;
					GermplasmListData germplasmListData = new GermplasmListData(null, germplasmList, gid, currentEntryNo,
						String.valueOf(currentEntryNo), plotCodeValuesByGIDs.get(gid), designation, crossExpansions.get(gid),
						GermplasmListDataDAO.STATUS_ACTIVE, null);
					this.daoFactory.getGermplasmListDataDAO().save(germplasmListData);
				}
			}
		}
	}

	private GermplasmList saveGermplasmList(final GermplasmListImportRequestDTO request) {
		final String description = request.getListDescription() != null ? request.getListDescription() : StringUtils.EMPTY;
		final Long date = Long.valueOf(Util.getSimpleDateFormat(Util.DATE_AS_NUMBER_FORMAT).format(Util.tryParseDate(request.getDateCreated(), Util.FRONTEND_DATE_FORMAT)));
		GermplasmList germplasmList = new GermplasmList(null, request.getListName(), date,	GermplasmList.LIST_TYPE,
			Integer.valueOf(request.getListOwnerPersonDbId()), description, null, GermplasmList.Status.LOCKED_LIST.getCode(),
			null, null);
		setGermplasmListExternalReferences(request, germplasmList);

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
