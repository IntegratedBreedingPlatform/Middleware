package org.generationcp.middleware.api.brapi;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeSearchRequest;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.domain.germplasm.ParentType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.util.PedigreeUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class PedigreeServiceBrapiImpl implements PedigreeServiceBrapi {

	private final DaoFactory daoFactory;

	public PedigreeServiceBrapiImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<PedigreeNodeDTO> searchPedigreeNodes(final PedigreeNodeSearchRequest pedigreeNodeSearchRequest, final Pageable pageable) {
		return this.daoFactory.getGermplasmDao().searchPedigreeNodes(pedigreeNodeSearchRequest, pageable);
	}

	@Override
	public List<PedigreeNodeDTO> updatePedigreeNodes(final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap) {

		final GermplasmDAO germplasmDAO = this.daoFactory.getGermplasmDao();

		final List<Germplasm> germplasmForUpdate =
			this.daoFactory.getGermplasmDao().getByGIDsOrUUIDListWithMethodAndBibref(Collections.emptySet(),
				pedigreeNodeDTOMap.keySet());

		// Extract the germplasmDbIds of the parents
		final Set<String>
			progenitorGermplasmDbIds =
			pedigreeNodeDTOMap.values().stream().map(PedigreeNodeDTO::getParents).collect(Collectors.toList()).stream()
				.flatMap(Collection::stream).map(PedigreeNodeReferenceDTO::getGermplasmDbId).collect(
					Collectors.toSet());
		final List<Germplasm> progenitors = this.daoFactory.getGermplasmDao().getByGIDsOrUUIDListWithMethodAndBibref(Collections.emptySet(),
			progenitorGermplasmDbIds);

		final Map<String, Germplasm> germplasmProgenitorsMapByGIDs =
			progenitors.stream().collect(Collectors.toMap(g -> String.valueOf(g.getGid()), Function.identity()));
		final Map<String, Germplasm> germplasmProgenitorsMapByUUIDs =
			progenitors.stream().collect(Collectors.toMap(g -> String.valueOf(g.getGermplasmUUID()), Function.identity()));

		germplasmForUpdate.stream().forEach(germplasm -> {

			if (pedigreeNodeDTOMap.containsKey(germplasm.getGermplasmUUID())) {

				final Multimap<String, Object[]> conflictErrors = ArrayListMultimap.create();
				final PedigreeNodeDTO pedigreeNodeDTO = pedigreeNodeDTOMap.get(germplasm.getGermplasmUUID());
				final Integer femaleParentGid = this.resolveFemaleGid(pedigreeNodeDTO, germplasmProgenitorsMapByUUIDs);
				final Integer maleParentGid = this.resolveMaleGid(pedigreeNodeDTO, germplasmProgenitorsMapByUUIDs);
				final List<Integer> otherParentGids = this.resolveOtherParentGids(pedigreeNodeDTO, germplasmProgenitorsMapByUUIDs);

				PedigreeUtil.assignProgenitors(germplasm, germplasmProgenitorsMapByGIDs, conflictErrors, femaleParentGid,
					maleParentGid, germplasm.getMethod(), otherParentGids);

				if (conflictErrors.isEmpty()) {
					germplasmDAO.update(germplasm);
				}
			}
		});

		final PedigreeNodeSearchRequest pedigreeNodeSearchRequest = new PedigreeNodeSearchRequest();
		pedigreeNodeSearchRequest.setGermplasmDbIds(new ArrayList<>(pedigreeNodeDTOMap.keySet()));
		return this.searchPedigreeNodes(pedigreeNodeSearchRequest, null);
	}

	private Integer resolveFemaleGid(final PedigreeNodeDTO pedigreeNodeDTO, final Map<String, Germplasm> germplasmProgenitorsMapByUUIDs) {
		final Optional<PedigreeNodeReferenceDTO> femalePedigreeNodeReference = pedigreeNodeDTO.getParents().stream().filter(
				pedigreeNodeReferenceDTO -> pedigreeNodeReferenceDTO.getParentType() == ParentType.FEMALE
					|| pedigreeNodeReferenceDTO.getParentType() == ParentType.POPULATION)
			.findAny();
		if (femalePedigreeNodeReference.isPresent()) {
			return germplasmProgenitorsMapByUUIDs.get(femalePedigreeNodeReference.get().getGermplasmDbId()).getGid();
		}
		return null;
	}

	private Integer resolveMaleGid(final PedigreeNodeDTO pedigreeNodeDTO, final Map<String, Germplasm> germplasmProgenitorsMapByUUIDs) {
		final Optional<PedigreeNodeReferenceDTO> malePedigreeNodeReference = pedigreeNodeDTO.getParents().stream().filter(
				pedigreeNodeReferenceDTO -> pedigreeNodeReferenceDTO.getParentType() == ParentType.MALE
					|| pedigreeNodeReferenceDTO.getParentType() == ParentType.SELF)
			.findFirst();
		if (malePedigreeNodeReference.isPresent()) {
			return germplasmProgenitorsMapByUUIDs.get(malePedigreeNodeReference.get().getGermplasmDbId()).getGid();
		}
		return null;
	}

	private List<Integer> resolveOtherParentGids(final PedigreeNodeDTO pedigreeNodeDTO,
		final Map<String, Germplasm> germplasmProgenitorsMapByUUIDs) {
		final List<PedigreeNodeReferenceDTO> otherParentPedigreeNodeReferences = pedigreeNodeDTO.getParents().stream().filter(
			pedigreeNodeReferenceDTO -> pedigreeNodeReferenceDTO.getParentType() == ParentType.MALE).collect(
			Collectors.toList());
		if (!CollectionUtils.isEmpty(otherParentPedigreeNodeReferences)) {
			final List<Integer> otherParentGids = new ArrayList<>();
			final Iterator<PedigreeNodeReferenceDTO> iterator = otherParentPedigreeNodeReferences.iterator();
			// Skip the first male parent
			iterator.next();
			while (iterator.hasNext()) {
				final PedigreeNodeReferenceDTO pedigreeNodeReferenceDTO = iterator.next();
				otherParentGids.add(germplasmProgenitorsMapByUUIDs.get(pedigreeNodeReferenceDTO.getGermplasmDbId()).getGid());
			}
			return otherParentGids;
		}
		return new ArrayList<>();
	}
}
