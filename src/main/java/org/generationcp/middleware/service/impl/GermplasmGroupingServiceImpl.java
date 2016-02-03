package org.generationcp.middleware.service.impl;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.api.GermplasmGroupingService;


public class GermplasmGroupingServiceImpl implements GermplasmGroupingService {

	@Override
	public void markFixed(Germplasm germplasmToFix) {
		if (germplasmToFix.getMgid() == null) {
			germplasmToFix.setMgid(germplasmToFix.getGid());
		}
	}

}
