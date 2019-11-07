
package org.generationcp.middleware.service.api;

import org.generationcp.middleware.pojos.ims.Lot;
import org.generationcp.middleware.pojos.workbench.CropType;

import java.util.List;

public interface LotIDGenerator {

	void generateLotIds(final CropType crop, final List<Lot> lots);

}
