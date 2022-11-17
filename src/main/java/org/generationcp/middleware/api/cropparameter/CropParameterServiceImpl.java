package org.generationcp.middleware.api.cropparameter;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.exceptions.MiddlewareRequestException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.CropParameter;
import org.generationcp.middleware.service.impl.crop.CropGenotypingParameterDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service
@Transactional
public class CropParameterServiceImpl implements CropParameterService {

	private final DaoFactory daoFactory;

	@Value("${db.encryption.secret.passphrase}")
	private String secretPassphrase;

	private static final String ENDPOINT = "_endpoint";
	private static final String TOKEN_ENDPOINT = "_token_endpoint";
	private static final String PROGRAM_ID = "_program_id";
	private static final String USERNAME = "_username";
	private static final String PASSWORD = "_password";
	private static final String BASE_URL = "_base_url";

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
		return ofNullable(this.daoFactory.getCropParameterDAO().getCropParameterById(cropParameterEnum.getKey(), this.secretPassphrase));
	}

	@Override
	public Optional<CropGenotypingParameterDTO> getCropGenotypingParameter(final String keyFilter) {
		final CropGenotypingParameterDTO cropGenotypingParameter = new CropGenotypingParameterDTO();
		final Map<String, CropParameter> cropParameterMap = this.daoFactory.getCropParameterDAO()
			.getCropParameterMapByKeyFilter(null, this.secretPassphrase, keyFilter);
		final List<String> requiredParameters = Arrays.asList(keyFilter + ENDPOINT, keyFilter + TOKEN_ENDPOINT,
			keyFilter + PROGRAM_ID, keyFilter + USERNAME, keyFilter + PASSWORD, keyFilter + BASE_URL);

		if (cropParameterMap.keySet().containsAll(requiredParameters)) {
			final String endpoint = cropParameterMap.get(keyFilter + ENDPOINT).getValue();
			final String tokenEndpoint = cropParameterMap.get(keyFilter + TOKEN_ENDPOINT).getValue();
			final String programId = cropParameterMap.get(keyFilter + PROGRAM_ID).getValue();
			final String username = cropParameterMap.get(keyFilter + USERNAME).getValue();
			final String password = cropParameterMap.get(keyFilter + PASSWORD).getValue();
			final String baseUrl = cropParameterMap.get(keyFilter + BASE_URL).getValue();

			if (!StringUtils.isEmpty(endpoint) && !StringUtils.isEmpty(programId) && !StringUtils.isEmpty(username)
				&& !StringUtils.isEmpty(tokenEndpoint) && !StringUtils.isEmpty(password)) {
				cropGenotypingParameter.setEndpoint(endpoint);
				cropGenotypingParameter.setTokenEndpoint(tokenEndpoint);
				cropGenotypingParameter.setProgramId(programId);
				cropGenotypingParameter.setUserName(username);
				cropGenotypingParameter.setPassword(password);
				cropGenotypingParameter.setBaseUrl(baseUrl);

				return Optional.of(cropGenotypingParameter);
			}
		}

		// return an empty object, will notify the user that required parameters have missing values
		return Optional.empty();
	}
}
