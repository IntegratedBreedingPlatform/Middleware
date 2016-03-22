package org.generationcp.middleware.service.api;

import org.generationcp.middleware.pojos.Configuration;

public interface ConfigurationService {

	Configuration getConfiguration(String key);
}
