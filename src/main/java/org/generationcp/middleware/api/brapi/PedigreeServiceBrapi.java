package org.generationcp.middleware.api.brapi;

import com.google.common.collect.Multimap;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeSearchRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PedigreeServiceBrapi {

	long countPedigreeNodes(PedigreeNodeSearchRequest pedigreeNodeSearchRequest);

	List<PedigreeNodeDTO> searchPedigreeNodes(PedigreeNodeSearchRequest pedigreeNodeSearchRequest, Pageable pageable);

	Set<String> updatePedigreeNodes(Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap, Multimap<String, Object[]> conflictErrors);

}
