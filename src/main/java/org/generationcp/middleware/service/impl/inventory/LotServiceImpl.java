package org.generationcp.middleware.service.impl.inventory;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.service.api.inventory.LotService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class LotServiceImpl implements LotService {

	public LotServiceImpl() {

	}

	public LotServiceImpl(final HibernateSessionProvider sessionProvider) {
	}

}
