package org.generationcp.middleware.api.brapi;

import com.google.common.collect.Multimap;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeDTO;
import org.generationcp.middleware.api.brapi.v2.germplasm.PedigreeNodeSearchRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PedigreeServiceBrapi {

	List<PedigreeNodeDTO> searchPedigreeNodes(PedigreeNodeSearchRequest pedigreeNodeSearchRequest, Pageable pageable);

	List<PedigreeNodeDTO> updatePedigreeNodes(Map<String, PedigreeNodeDTO> pedigreeNodeDTOMap, Multimap<String, Object[]> conflictErrors);

}
