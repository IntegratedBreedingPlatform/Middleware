package org.generationcp.middleware.service.impl.audit;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.audit.ObservationAuditService;
import org.generationcp.middleware.service.api.dataset.ObservationAuditDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ObservationAuditServiceImpl implements ObservationAuditService {

	private DaoFactory daoFactory;

	public ObservationAuditServiceImpl() {
	}

	public ObservationAuditServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<ObservationAuditDTO> getObservationAuditList(final String observationUnitId, final Integer variableId,
		final Pageable pageable) {
		return this.daoFactory.getPhenotypeAuditDao()
			.getObservationAuditByObservationUnitIdAndObservableId(observationUnitId, variableId, pageable);
	}

	@Override
	public long countObservationAudit(final String observationUnitId, final Integer variableId) {
		return this.daoFactory.getPhenotypeAuditDao().countObservationAudit(observationUnitId, variableId);
	}
}
