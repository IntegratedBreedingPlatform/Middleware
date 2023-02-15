package org.generationcp.middleware.service.api.audit;

import org.generationcp.middleware.service.api.dataset.ObservationAuditDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ObservationAuditService {

	List<ObservationAuditDTO> getObservationAuditList(String observationUnitId, Integer variableId, Pageable pageable);

	long countObservationAudit(String observationUnitId, Integer variableId);
}
