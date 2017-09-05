
package org.generationcp.middleware.service.api;

import org.generationcp.middleware.pojos.Plant;

public interface PlantService {

	Plant buildPlant(final String cropPrefix, final Integer plantNumber, final Integer experimentId);

}
