package org.generationcp.middleware.api.brapi;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.domain.search_request.brapi.v2.VariableSearchRequestDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.sample.SampleObservationDto;
import org.generationcp.middleware.service.api.study.VariableDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

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

    public List<VariableDTO> getObservationVariables(final String crop, VariableSearchRequestDTO requestDTO, Pageable pageable) {
        final List<VariableDTO> variableDTOS = this.daoFactory.getCvTermDao().getObservationVariables(requestDTO, pageable);
        if(!CollectionUtils.isEmpty(variableDTOS)) {
            final List<String> variableIds = new ArrayList<>(variableDTOS.stream().map(VariableDTO::getObservationVariableDbId)
                    .collect(Collectors.toSet()));
            variableIds.addAll(variableDTOS.stream().map(variableDTO -> variableDTO.getTrait().getTraitDbId())
                    .collect(Collectors.toSet()));
            variableIds.addAll(variableDTOS.stream().map(variableDTO -> variableDTO.getScale().getScaleDbId())
                    .collect(Collectors.toSet()));
            variableIds.addAll(variableDTOS.stream().map(variableDTO -> variableDTO.getMethod().getMethodDbId())
                    .collect(Collectors.toSet()));

            final Map<String, List<ExternalReferenceDTO>> externalReferencesMap =
                    this.daoFactory.getVariableExternalReferenceDAO().getExternalReferences(variableIds).stream()
                            .collect(groupingBy(
                                    ExternalReferenceDTO::getEntityId));
            for(final VariableDTO dto: variableDTOS) {
                dto.setCommonCropName(crop);
                dto.setCrop(crop);
                dto.setExternalReferences(externalReferencesMap.get(dto.getObservationVariableDbId()));
                dto.getScale().setExternalReferences(externalReferencesMap.get(dto.getScale().getScaleDbId()));
                dto.getMethod().setExternalReferences(externalReferencesMap.get(dto.getMethod().getMethodDbId()));
                dto.getTrait().setExternalReferences(externalReferencesMap.get(dto.getTrait().getTraitDbId()));
            }
        }
        return variableDTOS;
    }

    public long countObservationVariables(VariableSearchRequestDTO requestDTO){
        return this.daoFactory.getCvTermDao().countObservationVariables(requestDTO);
    }
}
