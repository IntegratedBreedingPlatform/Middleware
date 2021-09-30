package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.domain.search_request.brapi.v2.VariableSearchRequestDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.study.VariableDTO;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class VariableServiceBrapiImpl implements VariableServiceBrapi{

    private HibernateSessionProvider sessionProvider;
    private DaoFactory daoFactory;

    public VariableServiceBrapiImpl() {
        // no-arg constuctor is required by CGLIB proxying used by Spring 3x and older.
    }
    public VariableServiceBrapiImpl(final HibernateSessionProvider sessionProvider) {
        this.daoFactory = new DaoFactory(sessionProvider);
        this.sessionProvider = sessionProvider;
    }

    public List<VariableDTO> getObservationVariables(VariableSearchRequestDTO requestDTO, Pageable pageable) {
        final List<VariableDTO> variableDTOS = this.daoFactory.getCvTermDao().getObservationVariables(requestDTO, pageable);

        return variableDTOS;
    }

    public long countObservationVariables(VariableSearchRequestDTO requestDTO){
        return this.daoFactory.getCvTermDao().countObservationVariables(requestDTO);
    }
}
