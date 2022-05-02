package org.generationcp.middleware.api.config;

import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.Config;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ConfigServiceImpl implements ConfigService {

	private final DaoFactory daoFactory;

	public ConfigServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<Config> getConfig(final Pageable pageable) {
		return this.daoFactory.getConfigDAO().getAll(pageable.getPageNumber(), pageable.getPageSize());
	}

	@Override
	public void modifyConfig(final String key, final ConfigPatchRequestDTO request) {
		final Config config = this.daoFactory.getConfigDAO().getById(key);
		if (config == null) {
			throw new MiddlewareRequestException("", "error.record.not.found", "key=" + key);
		}
		config.setValue(request.getValue());
		this.daoFactory.getConfigDAO().saveOrUpdate(config);
	}
}
