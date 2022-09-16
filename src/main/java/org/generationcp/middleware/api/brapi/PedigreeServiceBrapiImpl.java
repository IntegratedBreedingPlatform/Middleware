package org.generationcp.middleware.api.brapi;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.api.brapi.v1.attribute.AttributeDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeReferenceDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeSearchRequest;
import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.domain.germplasm.ParentType;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.api.PedigreeService;
import org.generationcp.middleware.util.CrossExpansionProperties;
import org.generationcp.middleware.util.PedigreeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional
public class PedigreeServiceBrapiImpl implements PedigreeServiceBrapi {

	public static final int UNKNOWN = 0;
	private final DaoFactory daoFactory;

	@Autowired
	private PedigreeService pedigreeService;

	@Autowired
	private CrossExpansionProperties crossExpansionProperties;

	public PedigreeServiceBrapiImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public long countPedigreeNodes(final PedigreeNodeSearchRequest pedigreeNodeSearchRequest) {
		return this.daoFactory.getGermplasmDao().countPedigreeNodes(pedigreeNodeSearchRequest);
	}

	@Override
	public List<PedigreeNodeDTO> searchPedigreeNodes(final PedigreeNodeSearchRequest pedigreeNodeSearchRequest, final Pageable pageable) {
		final List<PedigreeNodeDTO> result = this.daoFactory.getGermplasmDao().searchPedigreeNodes(pedigreeNodeSearchRequest, pageable);

		// Extract the gids of the germplasm
		final List<Integer> gids = result.stream().map(PedigreeNodeDTO::getGid).filter(Objects::nonNull).collect(Collectors.toList());
		// Extract the gids of the germplasm's parents
		final List<Integer> gidsOfParents =
			result.stream().filter(p -> !CollectionUtils.isEmpty(p.getParents())).flatMap(p -> p.getParents().stream())
				.map(PedigreeNodeReferenceDTO::getGid).filter(Objects::nonNull)
				.collect(Collectors.toList());

		if (!CollectionUtils.isEmpty(result)) {

			// Retrieve the progeny and siblings if explicitly specified
			final Map<Integer, List<PedigreeNodeReferenceDTO>> progenyMapByGids =
				pedigreeNodeSearchRequest.isIncludeProgeny() ? this.daoFactory.getGermplasmDao().getProgenyByGids(gids) : new HashMap<>();
			final Map<Integer, List<PedigreeNodeReferenceDTO>> siblingsMapByGids =
				pedigreeNodeSearchRequest.isIncludeSiblings() ? this.daoFactory.getGermplasmDao().getSiblingsByGids(gids) : new HashMap<>();

			/** Populate the preferred names, PUIs, external references, pedigree string, additionalInfo (attributes),
			 * progeny (optional) and siblings (optional)
			 **/
			final Map<Integer, String> preferredNamesMap =
				this.daoFactory.getNameDao().getPreferredNamesByGIDs(ListUtils.union(gids, gidsOfParents));
			final Map<Integer, String> germplasmPUIsMap = this.daoFactory.getNameDao().getPUIsByGIDs(gids);
			final Map<Integer, String> pedigreeStringMap =
				this.pedigreeService.getCrossExpansions(new HashSet<>(gids), null, this.crossExpansionProperties);
			final Map<String, List<ExternalReferenceDTO>> referencesByGidMap =
				this.daoFactory.getGermplasmExternalReferenceDAO().getExternalReferences(gids).stream()
					.collect(groupingBy(ExternalReferenceDTO::getEntityId));
			final Map<Integer, Map<String, String>> attributesByGidsMap =
				this.daoFactory.getAttributeDAO().getAttributesByGidsMap(gids).entrySet().stream().collect(Collectors.toMap(
					Map.Entry::getKey,
					e -> e.getValue().stream()
						.collect(Collectors.toMap(AttributeDTO::getAttributeCode, AttributeDTO::getValue, (a1, a2) -> a1))));

			for (final PedigreeNodeDTO pedigreeNodeDTO : result) {
				pedigreeNodeDTO.setPedigreeString(pedigreeStringMap.getOrDefault(pedigreeNodeDTO.getGid(), null));
				pedigreeNodeDTO.setDefaultDisplayName(preferredNamesMap.getOrDefault(pedigreeNodeDTO.getGid(), null));
				pedigreeNodeDTO.setGermplasmName(preferredNamesMap.getOrDefault(pedigreeNodeDTO.getGid(), null));
				pedigreeNodeDTO.setGermplasmPUI(germplasmPUIsMap.getOrDefault(pedigreeNodeDTO.getGid(), null));
				pedigreeNodeDTO.setExternalReferences(
					referencesByGidMap.getOrDefault(String.valueOf(pedigreeNodeDTO.getGid()), new ArrayList<>()));
				pedigreeNodeDTO.setProgeny(progenyMapByGids.getOrDefault(pedigreeNodeDTO.getGid(), null));
				pedigreeNodeDTO.setSiblings(siblingsMapByGids.getOrDefault(pedigreeNodeDTO.getGid(), null));
				pedigreeNodeDTO.setAdditionalInfo(attributesByGidsMap.getOrDefault(pedigreeNodeDTO.getGid(), null));
				if (!CollectionUtils.isEmpty(pedigreeNodeDTO.getParents())) {
					pedigreeNodeDTO.getParents().forEach(p -> p.setGermplasmName(preferredNamesMap.getOrDefault(p.getGid(), null)));
				}
			}
		}
		return result;
	}

	@Override
	public Set<String> updatePedigreeNodes(final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap,
		final Multimap<String, Object[]> conflictErrors) {

		final Map<String, Germplasm> germplasmForUpdateMap =
			this.daoFactory.getGermplasmDao().getByGIDsOrUUIDListWithMethodAndBibref(Collections.emptySet(),
				pedigreeNodeDTOMap.keySet()).stream().collect(Collectors.toMap(Germplasm::getGermplasmUUID, Function.identity()));

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

		final Set<String> updatedGermplasmDbIds = new HashSet<>();
		final Iterator<Map.Entry<String, Germplasm>> iterator = germplasmForUpdateMap.entrySet().iterator();
		while (iterator.hasNext()) {
			final Map.Entry<String, Germplasm> entry = iterator.next();
			final Germplasm germplasm = entry.getValue();
			this.updateGermplasm(germplasm, germplasmForUpdateMap, pedigreeNodeDTOMap, conflictErrors, updatedGermplasmDbIds,
				germplasmProgenitorsMapByGIDs, germplasmProgenitorsMapByUUIDs);
		}
		return updatedGermplasmDbIds;
	}

	private void updateGermplasm(final Germplasm germplasm,
		final Map<String, Germplasm> germplasmForUpdateMap,
		final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap,
		final Multimap<String, Object[]> conflictErrors,
		final Set<String> updatedGermplasmDbIds,
		final Map<String, Germplasm> germplasmProgenitorsMapByGIDs,
		final Map<String, Germplasm> germplasmProgenitorsMapByUUIDs) {

		// Do not update germplasm if it is already updated.
		if (germplasm != null && pedigreeNodeDTOMap.containsKey(germplasm.getGermplasmUUID()) && !updatedGermplasmDbIds.contains(
			germplasm.getGermplasmUUID())) {

			final PedigreeNodeDTO pedigreeNodeDTO = pedigreeNodeDTOMap.get(germplasm.getGermplasmUUID());
			final Optional<PedigreeNodeReferenceDTO> parent1 = this.getParent1(pedigreeNodeDTO);
			final Optional<PedigreeNodeReferenceDTO> parent2 = this.getParent2(pedigreeNodeDTO);
			final List<PedigreeNodeReferenceDTO> otherParents = this.getOtherParents(pedigreeNodeDTO);

			// If the parents of germplasm are to be updated, we should update them first before
			// assigning to the germplasm.
			if (parent1.isPresent() && StringUtils.isNotEmpty(parent1.get().getGermplasmDbId())) {
				this.updateGermplasm(germplasmForUpdateMap.get(parent1.get().getGermplasmDbId()), germplasmForUpdateMap,
					pedigreeNodeDTOMap,
					conflictErrors, updatedGermplasmDbIds, germplasmProgenitorsMapByGIDs, germplasmProgenitorsMapByUUIDs);
			}
			if (parent2.isPresent() && StringUtils.isNotEmpty(parent2.get().getGermplasmDbId())) {
				this.updateGermplasm(germplasmForUpdateMap.get(parent2.get().getGermplasmDbId()), germplasmForUpdateMap,
					pedigreeNodeDTOMap,
					conflictErrors, updatedGermplasmDbIds, germplasmProgenitorsMapByGIDs, germplasmProgenitorsMapByUUIDs);
			}
			otherParents.forEach(parent -> {
				if (StringUtils.isNotEmpty(parent.getGermplasmDbId())) {
					this.updateGermplasm(germplasmForUpdateMap.get(parent.getGermplasmDbId()), germplasmForUpdateMap,
						pedigreeNodeDTOMap, conflictErrors, updatedGermplasmDbIds,
						germplasmProgenitorsMapByGIDs, germplasmProgenitorsMapByUUIDs);
				}
			});

			final Multimap<String, Object[]> conflictErrorsPerGermplasm = ArrayListMultimap.create();
			PedigreeUtil.assignProgenitors(germplasm, germplasmProgenitorsMapByGIDs, conflictErrorsPerGermplasm,
				this.resolveParentGid(parent1, germplasmProgenitorsMapByUUIDs),
				this.resolveParentGid(parent2, germplasmProgenitorsMapByUUIDs),
				germplasm.getMethod(),
				otherParents.stream().map(o -> this.resolveParentGid(Optional.of(o), germplasmProgenitorsMapByUUIDs))
					.collect(Collectors.toList()));

			final GermplasmDAO germplasmDAO = this.daoFactory.getGermplasmDao();
			if (conflictErrorsPerGermplasm.isEmpty()) {
				germplasmDAO.update(germplasm);
				updatedGermplasmDbIds.add(germplasm.getGermplasmUUID());
			} else {
				// Detach the germplasm from session so that any changes to it will not be automatically persisted by the transaction.
				germplasmDAO.evict(germplasm);
				conflictErrors.putAll(conflictErrorsPerGermplasm);
			}

		}
	}

	private Integer resolveParentGid(final Optional<PedigreeNodeReferenceDTO> parentReference,
		final Map<String, Germplasm> germplasmProgenitorsMapByUUIDs) {
		if (parentReference.isPresent() && StringUtils.isNotEmpty(parentReference.get().getGermplasmDbId())) {
			return germplasmProgenitorsMapByUUIDs.get(parentReference.get().getGermplasmDbId()).getGid();
		}
		return UNKNOWN;
	}

	private Optional<PedigreeNodeReferenceDTO> getParent1(final PedigreeNodeDTO pedigreeNodeDTO) {
		return pedigreeNodeDTO.getParents().stream().filter(
				pedigreeNodeReferenceDTO -> ParentType.FEMALE.name().equals(pedigreeNodeReferenceDTO.getParentType())
					|| ParentType.POPULATION.name().equals(pedigreeNodeReferenceDTO.getParentType()))
			.findAny();
	}

	private Optional<PedigreeNodeReferenceDTO> getParent2(final PedigreeNodeDTO pedigreeNodeDTO) {
		return pedigreeNodeDTO.getParents().stream().filter(
				pedigreeNodeReferenceDTO -> ParentType.MALE.name().equals(pedigreeNodeReferenceDTO.getParentType())
					|| ParentType.SELF.name().equals(pedigreeNodeReferenceDTO.getParentType()))
			.findFirst();
	}

	private List<PedigreeNodeReferenceDTO> getOtherParents(final PedigreeNodeDTO pedigreeNodeDTO) {
		final List<PedigreeNodeReferenceDTO> otherParentPedigreeNodeReferences = pedigreeNodeDTO.getParents().stream().filter(
			pedigreeNodeReferenceDTO -> ParentType.MALE.name().equals(pedigreeNodeReferenceDTO.getParentType())).collect(
			Collectors.toList());
		if (!CollectionUtils.isEmpty(otherParentPedigreeNodeReferences)) {
			final List<PedigreeNodeReferenceDTO> otherParents = new ArrayList<>();
			final Iterator<PedigreeNodeReferenceDTO> iterator = otherParentPedigreeNodeReferences.iterator();
			// Skip the first male parent
			iterator.next();
			while (iterator.hasNext()) {
				final PedigreeNodeReferenceDTO pedigreeNodeReferenceDTO = iterator.next();
				otherParents.add(pedigreeNodeReferenceDTO);
			}
			return otherParents;
		}
		return new ArrayList<>();
	}
}
