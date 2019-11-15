package org.generationcp.middleware.service.api.rpackage;

import com.google.common.base.Optional;
import org.generationcp.middleware.pojos.workbench.RCall;
import org.generationcp.middleware.pojos.workbench.RPackage;

import java.util.List;

public interface RPackageService {

	List<RCall> getAllRCalls();

	List<RCall> getRCallsByPackageId(final Integer packageId);

	Optional<RPackage> getRPackageById(final Integer packageId);
}
