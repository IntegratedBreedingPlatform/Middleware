package org.generationcp.middleware.service.impl.crossplan;

import org.generationcp.middleware.api.crossplan.CrossPlanSearchRequest;
import org.generationcp.middleware.api.crossplan.CrossPlanSearchResponse;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.crossplan.CrossPlanService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class CrossPlanServiceImpl implements CrossPlanService {

    private final DaoFactory daoFactory;

    public CrossPlanServiceImpl(final HibernateSessionProvider sessionProvider) {
        this.daoFactory = new DaoFactory(sessionProvider);
    }
    @Override
    public Long countSearchCrossPlans(String programUUID, CrossPlanSearchRequest crossPlanSearchRequest) {
        return this.daoFactory.getCrossPlanDAO().countSearchCrossPlans(programUUID, crossPlanSearchRequest);
    }

    @Override
    public List<CrossPlanSearchResponse> searchCrossPlans(String programUUID, CrossPlanSearchRequest crossPlanSearchRequest, Pageable pageable) {
        return this.daoFactory.getCrossPlanDAO().searchCrossPlans(programUUID, crossPlanSearchRequest,pageable);
    }
}
