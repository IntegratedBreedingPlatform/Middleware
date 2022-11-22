package org.generationcp.middleware.api.cropparameter;

import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.CropParameter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
@Transactional
public class CropParameterServiceImpl implements CropParameterService {

	private final DaoFactory daoFactory;

	@Value("${db.encryption.secret.passphrase}")
	private String secretPassphrase;

	public CropParameterServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public List<CropParameter> getCropParameters(final Pageable pageable) {
		return this.daoFactory.getCropParameterDAO().getAllCropParameters(pageable, this.secretPassphrase);
	}

	@Override
	public void modifyCropParameter(final String key, final CropParameterPatchRequestDTO request) {
		final CropParameter cropParameter = this.daoFactory.getCropParameterDAO().getById(key);
		if (cropParameter == null) {
			throw new MiddlewareRequestException("", "error.record.not.found", "key=" + key);
		}
		if (Boolean.TRUE.equals(request.isEncrypted())) {
			this.daoFactory.getCropParameterDAO().updateEncryptedValue(key, request.getValue(), this.secretPassphrase);
		} else {
			cropParameter.setValue(request.getValue());
			this.daoFactory.getCropParameterDAO().saveOrUpdate(cropParameter);
		}
	}

	@Override
	public Optional<CropParameter> getCropParameter(final CropParameterEnum cropParameterEnum) {
		return ofNullable(this.daoFactory.getCropParameterDAO().getCropParameterByKey(cropParameterEnum.getKey(), this.secretPassphrase));
	}

	@Override
	public List<CropParameter> getCropParametersByGroupName(final String groupName) {
		return this.daoFactory.getCropParameterDAO()
			.getCropParametersByGroupName(null, this.secretPassphrase, groupName);
	}
}
