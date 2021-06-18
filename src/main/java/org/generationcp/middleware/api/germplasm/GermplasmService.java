package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.api.brapi.v1.attribute.AttributeDTO;
import org.generationcp.middleware.api.brapi.v1.germplasm.GermplasmDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.GermplasmImportRequest;
import org.generationcp.middleware.api.brapi.v2.germplasm.GermplasmUpdateRequest;
import org.generationcp.middleware.api.nametype.GermplasmNameTypeDTO;
import org.generationcp.middleware.domain.germplasm.GermplasmBasicDetailsDto;
import org.generationcp.middleware.domain.germplasm.GermplasmDto;
import org.generationcp.middleware.domain.germplasm.GermplasmUpdateDTO;
import org.generationcp.middleware.domain.germplasm.PedigreeDTO;
import org.generationcp.middleware.domain.germplasm.ProgenitorsDetailsDto;
import org.generationcp.middleware.domain.germplasm.ProgenitorsUpdateRequestDto;
import org.generationcp.middleware.domain.germplasm.ProgenyDTO;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportRequestDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmImportResponseDto;
import org.generationcp.middleware.domain.germplasm.importation.GermplasmMatchRequestDto;
import org.generationcp.middleware.domain.oms.Term;
import org.generationcp.middleware.domain.ontology.Variable;
import org.generationcp.middleware.domain.search_request.brapi.v1.GermplasmSearchRequestDto;
import org.generationcp.middleware.pojos.Attribute;
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
	 * Returns value of the plot code (seed source) where the germplasm was created, identified by the given gid. Returns "Unknown" if plot
	 * code attribute is not present. Never returns null.
	 */
	String getPlotCodeValue(Integer gid);

	/**
	 * Returns a map of plot codes (seed source) where the germplasm was created, indexed by the given gids. Returns "Unknown" if plot
	 * code attribute is not present. Never returns null.
	 *
	 * @param gids
	 * @return Map<gids, plotCodeValue>
	 */
	Map<Integer, String> getPlotCodeValues(Set<Integer> gids);

	/**
	 * Returns all the attributes of the Germplasm identified by the given id.
	 *
	 * @param gid - id of the Germplasm
	 * @return a {@link List} of {@link Attribute}
	 */
	List<Attribute> getAttributesByGID(Integer gid);

	/**
	 * @return the term that represents "plot code"
	 */
	Term getPlotCodeField();

	Map<Integer, GermplasmImportResponseDto> importGermplasm(String cropName, String programUUID,
		GermplasmImportRequestDto germplasmImportRequestDto);

	long countGermplasmMatches(GermplasmMatchRequestDto germplasmMatchRequestDto);

	List<GermplasmDto> findGermplasmMatches(GermplasmMatchRequestDto germplasmMatchRequestDto, Pageable pageable);

	Set<Integer> importGermplasmUpdates(String programUUID, List<GermplasmUpdateDTO> germplasmUpdateDTOList);

	List<GermplasmDTO> createGermplasm(String cropname, List<GermplasmImportRequest> germplasmImportRequestList);

	GermplasmDTO updateGermplasm(String germplasmDbId, GermplasmUpdateRequest germplasmUpdateRequest);

	long countFilteredGermplasm(GermplasmSearchRequestDto germplasmSearchRequestDTO);

	List<GermplasmDTO> searchFilteredGermplasm(GermplasmSearchRequestDto germplasmSearchRequestDTO, Pageable pageable);

	Optional<GermplasmDTO> getGermplasmDTOByGUID(String germplasmUUID);

	long countGermplasmByStudy(Integer studyDbId);

	List<GermplasmDTO> getGermplasmByStudy(Integer studyDbId, Pageable pageable);

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

	PedigreeDTO getPedigree(Integer gid, String notation, Boolean includeSiblings);

	ProgenyDTO getProgeny(final Integer gid);

	List<AttributeDTO> getAttributesByGUID(String germplasmUUID, List<String> attributeDbIds, Pageable pageable);

	long countAttributesByGUID(String germplasmUUID, List<String> attributeDbIds);

	List<GermplasmNameTypeDTO> filterGermplasmNameTypes(Set<String> codes);

	GermplasmDto getGermplasmDtoById(Integer gid);

	ProgenitorsDetailsDto getGermplasmProgenitorDetails(Integer gid);

	void updateGermplasmBasicDetails(Integer gid, GermplasmBasicDetailsDto germplasmBasicDetailsDto);

	void updateGermplasmPedigree(Integer gid, ProgenitorsUpdateRequestDto progenitorsUpdateRequstDto);

	public List<Variable> getGermplasmAttributeVariables(final List<Integer> gids, final String programUUID);

}
