package org.generationcp.middleware.service.api.rpackage;

import org.generationcp.middleware.domain.rpackage.RCallDTO;
import org.generationcp.middleware.domain.rpackage.RPackageDTO;

import java.util.List;
import java.util.Optional;

public interface RPackageService {

	List<RCallDTO> getRCallsByPackageId(final Integer packageId);

	Optional<RPackageDTO> getRPackageById(final Integer packageId);
}
