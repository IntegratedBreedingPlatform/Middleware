package org.generationcp.middleware.service.impl.audit;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.service.api.audit.GermplasmAuditService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class GermplasmAuditServiceImpl implements GermplasmAuditService {

	private DaoFactory daoFactory;
	
	public GermplasmAuditServiceImpl() {
	}

	public GermplasmAuditServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<GermplasmNameAuditDTO> getNameChangesByNameId(final Integer nameId, final Pageable pageable) {
		return this.daoFactory.getGermplasmAuditDAO().getNameChangesByNameId(nameId, pageable);
	}

	@Override
	public long countNameChangesByNameId(final Integer nameId) {
		return this.daoFactory.getGermplasmAuditDAO().countNameChangesByNameId(nameId);
	}

	@Override
	public List<GermplasmAttributeAuditDTO> getAttributeChangesByAttributeId(final Integer attributeId, final Pageable pageable) {
		return this.daoFactory.getGermplasmAuditDAO().getAttributeChangesByAttributeId(attributeId, pageable);
	}

	@Override
	public long countAttributeChangesByAttributeId(final Integer attributeId) {
		return this.daoFactory.getGermplasmAuditDAO().countAttributeChangesByAttributeId(attributeId);
	}

	@Override
	public List<GermplasmBasicDetailsAuditDTO> getBasicDetailsChangesByGid(final Integer gid, final Pageable pageable) {
		return this.daoFactory.getGermplasmAuditDAO().getBasicDetailsChangesByGid(gid, pageable);
	}

	@Override
	public long countBasicDetailsChangesByGid(final Integer gid) {
		return this.daoFactory.getGermplasmAuditDAO().countBasicDetailsChangesByGid(gid);
	}

}
