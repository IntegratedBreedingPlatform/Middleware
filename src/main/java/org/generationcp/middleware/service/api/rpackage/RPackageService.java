package org.generationcp.middleware.service.api.rpackage;

import com.google.common.base.Optional;
import org.generationcp.middleware.domain.rpackage.RCallDTO;
import org.generationcp.middleware.domain.rpackage.RPackageDTO;
import org.generationcp.middleware.pojos.workbench.RPackage;

import java.util.List;

public interface RPackageService {

	List<RCallDTO> getRCallsByPackageId(final Integer packageId);

	Optional<RPackageDTO> getRPackageById(final Integer packageId);
}
