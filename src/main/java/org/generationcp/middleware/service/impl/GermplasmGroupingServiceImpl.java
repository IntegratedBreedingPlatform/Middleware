package org.generationcp.middleware.service.impl;

import java.util.List;

import org.generationcp.middleware.dao.GermplasmDAO;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.api.GermplasmGroupingService;


public class GermplasmGroupingServiceImpl implements GermplasmGroupingService {

	private GermplasmDAO germplasmDAO;

	public GermplasmGroupingServiceImpl(GermplasmDAO germplasmDAO) {
		this.germplasmDAO = germplasmDAO;
	}

	@Override
	public void markFixed(Germplasm germplasmToFix, boolean preserveExistingGroup) {
		assignMGID(germplasmToFix, germplasmToFix.getGid(), preserveExistingGroup);

		// TODO germplasmDAO.getAllChildren() just gets the immediate descendants at a given level. We need to recurse ang get whole
		// tree(?).
		final List<Germplasm> allChildren = this.germplasmDAO.getAllChildren(germplasmToFix.getGid());
		for (Germplasm child : allChildren) {
			assignMGID(child, germplasmToFix.getGid(), preserveExistingGroup);
		}
	}

	private void assignMGID(Germplasm germplasm, Integer mgidToAssign, boolean preserveExistingGroup) {
		if ((germplasm.getMgid() == null || new Integer(0).equals(germplasm.getMgid())) && !preserveExistingGroup) {
			germplasm.setMgid(mgidToAssign);
		}
	}
}
