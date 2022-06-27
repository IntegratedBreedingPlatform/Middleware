package org.generationcp.middleware.api.brapi;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;
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

		// Extract the gids of the germplasm and its parents
		final List<Integer> gids = result.stream().map(PedigreeNodeDTO::getGid).filter(Objects::nonNull).collect(Collectors.toList());
		gids.addAll(result.stream().filter(p -> !CollectionUtils.isEmpty(p.getParents())).flatMap(p -> p.getParents().stream())
			.map(PedigreeNodeReferenceDTO::getGid).filter(Objects::nonNull)
			.collect(Collectors.toList()));

		if (!CollectionUtils.isEmpty(result)) {
			// Populate the preferred name, PUIs, external references and pedigree string
			final Map<Integer, String> preferredNamesMap = this.daoFactory.getNameDao().getPreferredNamesByGIDs(gids);
			final Map<Integer, String> germplasmPUIsMap = this.daoFactory.getNameDao().getPUIsByGIDs(gids);
			final Map<Integer, String> pedigreeStringMap =
				this.pedigreeService.getCrossExpansions(new HashSet<>(gids), null, this.crossExpansionProperties);
			final Map<String, List<ExternalReferenceDTO>> referencesByGidMap =
				this.daoFactory.getGermplasmExternalReferenceDAO().getExternalReferences(gids).stream()
					.collect(groupingBy(ExternalReferenceDTO::getEntityId));
			for (final PedigreeNodeDTO pedigreeNodeDTO : result) {
				pedigreeNodeDTO.setPedigreeString(pedigreeStringMap.getOrDefault(pedigreeNodeDTO.getGid(), null));
				pedigreeNodeDTO.setDefaultDisplayName(preferredNamesMap.getOrDefault(pedigreeNodeDTO.getGid(), null));
				pedigreeNodeDTO.setGermplasmName(preferredNamesMap.getOrDefault(pedigreeNodeDTO.getGid(), null));
				pedigreeNodeDTO.setGermplasmPUI(germplasmPUIsMap.getOrDefault(pedigreeNodeDTO.getGid(), null));
				pedigreeNodeDTO.setExternalReferences(
					referencesByGidMap.getOrDefault(String.valueOf(pedigreeNodeDTO.getGid()), new ArrayList<>()));
			}
		}
		return result;
	}

	@Override
	public Set<String> updatePedigreeNodes(final Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap,
		final Multimap<String, Object[]> conflictErrors) {

		final Set<String> updatedGermplasmDbIds = new HashSet<>();
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
				final PedigreeNodeDTO pedigreeNodeDTO = pedigreeNodeDTOMap.get(germplasm.getGermplasmUUID());
				final Integer femaleParentGid = this.resolveFemaleGid(pedigreeNodeDTO, germplasmProgenitorsMapByUUIDs);
				final Integer maleParentGid = this.resolveMaleGid(pedigreeNodeDTO, germplasmProgenitorsMapByUUIDs);
				final List<Integer> otherParentGids = this.resolveOtherParentGids(pedigreeNodeDTO, germplasmProgenitorsMapByUUIDs);

				final Multimap<String, Object[]> conflictErrorsPerGermplasm = ArrayListMultimap.create();
				PedigreeUtil.assignProgenitors(germplasm, germplasmProgenitorsMapByGIDs, conflictErrorsPerGermplasm, femaleParentGid,
					maleParentGid, germplasm.getMethod(), otherParentGids);

				if (conflictErrorsPerGermplasm.isEmpty()) {
					germplasmDAO.update(germplasm);
					updatedGermplasmDbIds.add(germplasm.getGermplasmUUID());
				} else {
					// Detach the germplasm from session so that any changes to it will not be automatically persisted by the transaction.
					germplasmDAO.evict(germplasm);
					conflictErrors.putAll(conflictErrorsPerGermplasm);
				}
			}
		});

		return updatedGermplasmDbIds;
	}

	private Integer resolveFemaleGid(final PedigreeNodeDTO pedigreeNodeDTO, final Map<String, Germplasm> germplasmProgenitorsMapByUUIDs) {
		final Optional<PedigreeNodeReferenceDTO> femalePedigreeNodeReference = pedigreeNodeDTO.getParents().stream().filter(
				pedigreeNodeReferenceDTO -> ParentType.FEMALE.name().equals(pedigreeNodeReferenceDTO.getParentType())
					|| ParentType.POPULATION.name().equals(pedigreeNodeReferenceDTO.getParentType()))
			.findAny();
		if (femalePedigreeNodeReference.isPresent() && StringUtils.isNotEmpty(femalePedigreeNodeReference.get().getGermplasmDbId())) {
			return germplasmProgenitorsMapByUUIDs.get(femalePedigreeNodeReference.get().getGermplasmDbId()).getGid();
		}
		return UNKNOWN;
	}

	private Integer resolveMaleGid(final PedigreeNodeDTO pedigreeNodeDTO, final Map<String, Germplasm> germplasmProgenitorsMapByUUIDs) {
		final Optional<PedigreeNodeReferenceDTO> malePedigreeNodeReference = pedigreeNodeDTO.getParents().stream().filter(
				pedigreeNodeReferenceDTO -> ParentType.MALE.name().equals(pedigreeNodeReferenceDTO.getParentType())
					|| ParentType.SELF.name().equals(pedigreeNodeReferenceDTO.getParentType()))
			.findFirst();
		if (malePedigreeNodeReference.isPresent() && StringUtils.isNotEmpty(malePedigreeNodeReference.get().getGermplasmDbId())) {
			return germplasmProgenitorsMapByUUIDs.get(malePedigreeNodeReference.get().getGermplasmDbId()).getGid();
		}
		return UNKNOWN;
	}

	private List<Integer> resolveOtherParentGids(final PedigreeNodeDTO pedigreeNodeDTO,
		final Map<String, Germplasm> germplasmProgenitorsMapByUUIDs) {
		final List<PedigreeNodeReferenceDTO> otherParentPedigreeNodeReferences = pedigreeNodeDTO.getParents().stream().filter(
			pedigreeNodeReferenceDTO -> ParentType.MALE.name().equals(pedigreeNodeReferenceDTO.getParentType())).collect(
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
