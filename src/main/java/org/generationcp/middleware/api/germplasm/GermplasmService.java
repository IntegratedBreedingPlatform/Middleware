package org.generationcp.middleware.api.germplasm;

import org.apache.commons.lang3.tuple.Pair;
import org.generationcp.middleware.domain.germplasm.BasicGermplasmDTO;
import org.generationcp.middleware.domain.germplasm.GermplasmBasicDetailsDto;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.domain.germplasm.GermplasmMergedDto;
import org.generationcp.middleware.domain.germplasm.GermplasmMergeRequestDto;
import org.generationcp.middleware.domain.germplasm.GermplasmProgenyDto;
import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.domain.germplasm.ProgenitorsDetailsDto;
import org.generationcp.middleware.domain.germplasm.ProgenitorsUpdateRequestDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportRequestDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportResponseDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmMatchRequestDto;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.pojos.Germplasm;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GermplasmService {

	List<Germplasm> getGermplasmByGUIDs(List<String> guids);

	/**
	 * Returns all germplasm for the given germplasm ids
	 *
	 * @param gids
	 * @return a {@link List} of {@link Germplasm}
	 */
	List<Germplasm> getGermplasmByGIDs(List<Integer> gids);

	/**
	 * Returns a map of plot codes (seed source) where the germplasm was created, indexed by the given gids. Returns "Unknown" if plot
	 * code attribute is not present. Never returns null.
	 *
	 * @param gids
	 * @return Map<gids, plotCodeValue>
	 */
	Map<Integer, String> getPlotCodeValues(Set<Integer> gids);

	/**
	 * @return the term that represents "plot code"
	 */
	Term getPlotCodeField();

	Map<Integer, GermplasmImportResponseDto> importGermplasm(String cropName, String programUUID,
		GermplasmImportRequestDto germplasmImportRequestDto);

	long countGermplasmMatches(GermplasmMatchRequestDto germplasmMatchRequestDto);

	List<GermplasmDto> findGermplasmMatches(GermplasmMatchRequestDto germplasmMatchRequestDto, Pageable pageable);

	Set<Integer> importGermplasmUpdates(String programUUID, List<GermplasmUpdateDTO> germplasmUpdateDTOList);

	/**
	 * Delete the specified germplasm
	 *
	 * @param gids
	 */
	void deleteGermplasm(List<Integer> gids);

	Set<Integer> getCodeFixedGidsByGidList(List<Integer> gids);

	Set<Integer> getGidsWithOpenLots(List<Integer> gids);

	Set<Integer> getGidsOfGermplasmWithDescendants(List<Integer> gids);

	Set<Integer> getGermplasmUsedInLockedList(List<Integer> gids);

	Set<Integer> getGermplasmUsedInStudies(List<Integer> gids);

	Set<Integer> getGermplasmUsedInLockedStudies(List<Integer> gids);

	GermplasmDto getGermplasmDtoById(Integer gid);

	ProgenitorsDetailsDto getGermplasmProgenitorDetails(Integer gid);

	void updateGermplasmBasicDetails(Integer gid, GermplasmBasicDetailsDto germplasmBasicDetailsDto);

	void updateGermplasmPedigree(Integer gid, ProgenitorsUpdateRequestDto progenitorsUpdateRequestDto);

	void mergeGermplasm(GermplasmMergeRequestDto germplasmMergeRequestDto, String crossExpansion);

	List<GermplasmMergedDto> getGermplasmMerged(Integer gid);

	List<GermplasmProgenyDto> getGermplasmProgenies(Integer gid);

	Optional<Germplasm> findOneByMethodId(Integer breedingMethodDbId);

	boolean isLocationUsedInGermplasm(Integer locationId);

	/**
	 * Returns the group and immediate source name for a given germplasms
	 *
	 * @param gids
	 * @return a {@link Map} of {@link Pair} where the left value corresponds to the group source name and the right value to the immediate source name
	 */
	Map<Integer, Pair<String, String>> getDerivativeParentsMapByGids(Set<Integer> gids);

	Map<Integer, Integer> countGermplasmDerivativeProgeny(Set<Integer> gids);

	List<BasicGermplasmDTO> getBasicGermplasmByGids(Set<Integer> gids);

}
