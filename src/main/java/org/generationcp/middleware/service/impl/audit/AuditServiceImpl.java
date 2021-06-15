package org.generationcp.middleware.service.impl.audit;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.audit.AuditService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuditServiceImpl implements AuditService {

	private DaoFactory daoFactory;
	
	public AuditServiceImpl() {
	}

	public AuditServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<GermplasmNameChangeDTO> getNameChangesByNameId(final Integer nameId, final Pageable pageable) {
		return this.daoFactory.getAuditDAO().getNameChangesByNameId(nameId, pageable);
	}

	@Override
	public long countNameChangesByNameId(final Integer nameId) {
		return this.daoFactory.getAuditDAO().countNameChangesByNameId(nameId);
	}

}
