package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.domain.search_request.brapi.v2.GermplasmListSearchRequestDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.GermplasmListDTO;
import org.generationcp.middleware.service.api.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class GermplasmListServiceBrapiImpl implements GermplasmListServiceBrapi {

	@Autowired
	private UserService userService;

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

}
