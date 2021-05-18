package org.generationcp.middleware.api.germplasm.pedigree;

import org.generationcp.middleware.domain.germplasm.GermplasmDto;

import java.util.List;

public interface GermplasmPedigreeService {

	GermplasmTreeNode getGermplasmPedigreeTree(Integer gid, Integer level, boolean includeDerivativeLines);

	List<GermplasmDto> getGenerationHistory(Integer gid);

	List<GermplasmDto> getManagementNeighbors(Integer gid);

	List<GermplasmDto> getGroupRelatives(Integer gid);

	GermplasmNeighborhoodNode getGermplasmMaintenanceNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward);

	GermplasmNeighborhoodNode getGermplasmDerivativeNeighborhood(Integer gid, int numberOfStepsBackward, int numberOfStepsForward);

	Integer countGenerations(Integer gid, boolean includeDerivativeLines, boolean calculateFull);
}
