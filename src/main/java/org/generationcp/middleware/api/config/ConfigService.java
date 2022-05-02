package org.generationcp.middleware.api.config;

import org.generationcp.middleware.pojos.Config;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ConfigService {

	List<Config> getConfig(Pageable pageable);

	void modifyConfig(String key, ConfigPatchRequestDTO request);
}
