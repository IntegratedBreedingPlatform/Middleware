package org.generationcp.middleware.service.impl.rpackage;

import org.generationcp.middleware.domain.rpackage.RCallDTO;
import org.generationcp.middleware.domain.rpackage.RPackageDTO;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.pojos.workbench.RCall;
import org.generationcp.middleware.pojos.workbench.RCallParameter;
import org.generationcp.middleware.pojos.workbench.RPackage;
import org.generationcp.middleware.service.api.rpackage.RPackageService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RPackageServiceImpl implements RPackageService {

	private final WorkbenchDaoFactory workbenchDaoFactory;

	public RPackageServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.workbenchDaoFactory = new WorkbenchDaoFactory(sessionProvider);
	}

	@Override
	public List<RCallDTO> getRCallsByPackageId(final Integer packageId) {
		return this.convert(this.workbenchDaoFactory.getRCallDao().getRCallsByPackageId(packageId));
	}

	@Override
	public Optional<RPackageDTO> getRPackageById(final Integer packageId) {
		final RPackage rPackage = this.workbenchDaoFactory.getRPackageDao().getById(packageId);
		if (rPackage != null) {
			return Optional.of(new RPackageDTO(rPackage.getEndpoint(), rPackage.getDescription()));
		}
		return Optional.empty();
	}

	private List<RCallDTO> convert(final List<RCall> rCalls) {
		final List<RCallDTO> rCallDTOS = new ArrayList<>();
		for (final RCall rCall : rCalls) {
			rCallDTOS.add(this.mapToDTO(rCall));
		}
		return rCallDTOS;
	}

	private RCallDTO mapToDTO(final RCall rCall) {
		final RCallDTO rCallDTO = new RCallDTO();
		rCallDTO.setrCallId(rCall.getId());
		rCallDTO.setDescription(rCall.getDescription());
		rCallDTO.setEndpoint(rCall.getrPackage().getEndpoint());
		rCallDTO.setAggregate(rCall.isAggregate());
		final Map<String, String> parameters = new HashMap<>();
		for (final RCallParameter rCallParameter : rCall.getrCallParameters()) {
			parameters.put(rCallParameter.getKey(), rCallParameter.getValue());
		}
		rCallDTO.setParameters(parameters);
		return rCallDTO;
	}

}
