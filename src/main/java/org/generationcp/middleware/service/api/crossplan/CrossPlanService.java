package org.generationcp.middleware.service.api.crossplan;

import org.generationcp.middleware.api.crossplan.CrossPlanSearchRequest;
import org.generationcp.middleware.api.crossplan.CrossPlanSearchResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CrossPlanService {

    Long countSearchCrossPlans(String programUUID, CrossPlanSearchRequest crossPlanSearchRequest);

    List<CrossPlanSearchResponse> searchCrossPlans(String programUUID, CrossPlanSearchRequest crossPlanSearchRequest, Pageable pageable);
}
