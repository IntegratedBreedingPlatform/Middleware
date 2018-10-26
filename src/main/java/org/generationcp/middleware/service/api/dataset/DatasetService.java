package org.generationcp.middleware.service.api.dataset;

import org.generationcp.middleware.domain.dms.DatasetDTO;

import java.util.List;
import java.util.Set;

/**
 * Created by clarysabel on 10/22/18.
 */
public interface DatasetService {

	List<DatasetDTO> getDatasetByStudyId(final Integer studyId, final Set<Integer> filterByTypeIds);

}
