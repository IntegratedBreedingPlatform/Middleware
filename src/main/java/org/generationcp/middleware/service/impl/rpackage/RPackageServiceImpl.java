package org.generationcp.middleware.service.impl.rpackage;

import com.google.common.base.Optional;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.RCall;
import org.generationcp.middleware.pojos.workbench.RPackage;
import org.generationcp.middleware.service.api.rpackage.RPackageService;

import java.util.List;

public class RPackageServiceImpl implements RPackageService {

	private final WorkbenchDaoFactory workbenchDaoFactory;

	public RPackageServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public List<RCall> getAllRCalls() {
		return this.workbenchDaoFactory.getRCallDao().getAll();
	}

	@Override
	public List<RCall> getRCallsByPackageId(final Integer packageId) {
		return this.workbenchDaoFactory.getRCallDao().getRCallsByPackageId(packageId);
	}

	@Override
	public Optional<RPackage> getRPackageById(final Integer packageId) {
		return Optional.fromNullable(this.workbenchDaoFactory.getRPackageDao().getById(packageId));
	}

}
