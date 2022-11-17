package org.generationcp.middleware.service.impl.crop;

import org.apache.commons.lang3.StringUtils;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.CropParameter;
import org.generationcp.middleware.service.api.crop.CropGenotypingParameterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CropGenotypingParameterServiceImpl implements CropGenotypingParameterService {

	private final DaoFactory daoFactory;

	@Value("${db.encryption.secret.passphrase}")
	private String secretPassphrase;

	private static final String ENDPOINT = "_endpoint";
	private static final String TOKEN_ENDPOINT = "_token_endpoint";
	private static final String PROGRAM_ID = "_program_id";
	private static final String USERNAME = "_username";
	private static final String PASSWORD = "_password";
	private static final String BASE_URL = "_base_url";

	public CropGenotypingParameterServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
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
