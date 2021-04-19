package org.generationcp.middleware.api.study;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public class MyStudiesServiceImpl implements MyStudiesService {

	private DaoFactory daoFactory;

	public MyStudiesServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public long countMyStudies(final String programUUID, final Integer userId) {
		return daoFactory.getDmsProjectDAO().countMyStudies(programUUID, userId);
	}

	@Override
	public List<MyStudiesDTO> getMyStudies(final String programUUID, final Pageable pageable, final Integer userId) {
		return this.daoFactory.getDmsProjectDAO().getMyStudies(programUUID, pageable, userId);
	}
}
