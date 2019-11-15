package org.generationcp.middleware.service.api.rpackage;

import org.generationcp.middleware.pojos.workbench.RCall;

import java.util.List;

public interface RPackageService {

	List<RCall> getAllRCalls();

	List<RCall> getRCallsByPackageId(final Integer packageId);

}
