package org.generationcp.middleware.api.germplasm;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.workbench.CropType;

import java.util.List;

public interface GermplasmGuidGenerator {

	void generateObservationUnitIds(final CropType crop, final List<Germplasm> germplasmsList);

}
