/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.pojos.germplasm;

import java.util.List;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.Name;
import org.generationcp.middleware.service.pedigree.PedigreeDataManagerFactory;
import org.generationcp.middleware.util.CrossExpansionProperties;

public class NewSingleGermplasmCrossElement implements BackCrossOrNormalCross {

	private static final long serialVersionUID = 1575136137040870760L;

	private Germplasm germplasm;

	private boolean rootNode;

	public Germplasm getGermplasm() {
		return this.germplasm;
	}

	@Override
	public void setGermplasm(Germplasm germplasm) {
		this.germplasm = germplasm;
	}

	@Override
	public String getCrossExpansionString(String cropName, CrossExpansionProperties crossExpansionProperties, final PedigreeDataManagerFactory pedigreeDataManagerFactory) {


		final StringBuilder toreturn = new StringBuilder();
		if(germplasm != null) {
			final List<Integer> nameTypeOrder = crossExpansionProperties.getNameTypeOrder(cropName);
			final List<Name> namesByGID = pedigreeDataManagerFactory.getGermplasmDataManager().getByGIDWithListTypeFilters(germplasm.getGid(), null, nameTypeOrder);
			if(!rootNode) {
				if(CrossBuilderUtil.nameTypeBasedResolution(toreturn, nameTypeOrder, namesByGID)){
					return toreturn.toString();
				}
			}
		}

		if (this.germplasm != null) {
			Name nameObject = this.germplasm.getPreferredName();
			if (nameObject == null) {
				return this.germplasm.getGid().toString();
			} else {
				return nameObject.getNval();
			}
		} else {
			return "Unknown";
		}
	}

	@Override
	public boolean isRootNode() {
		return rootNode;
	}

	@Override
	public void setRootNode(boolean rootNode) {
		this.rootNode = rootNode;
	}


}
