package org.generationcp.middleware.api.cropparameter;

import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.CropParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CropParameterImpl implements CropParameterService {

	private final DaoFactory daoFactory;

	public CropParameterImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<CropParameter> getCropParameters(final Pageable pageable) {
		return this.daoFactory.getCropParameterDAO().getAll(pageable.getPageNumber(), pageable.getPageSize());
	}

	@Override
	public void modifyCropParameter(final String key, final CropParameterPatchRequestDTO request) {
		final CropParameter cropParameter = this.daoFactory.getCropParameterDAO().getById(key);
		if (cropParameter == null) {
			throw new MiddlewareRequestException("", "error.record.not.found", "key=" + key);
		}
		cropParameter.setValue(request.getValue());
		this.daoFactory.getCropParameterDAO().saveOrUpdate(cropParameter);
	}
}
