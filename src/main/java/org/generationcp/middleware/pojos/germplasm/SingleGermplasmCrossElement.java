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

import org.generationcp.middleware.pojos.Germplasm;

public class SingleGermplasmCrossElement implements GermplasmCrossElement {

	private static final long serialVersionUID = 1575136137040870760L;

	private Germplasm germplasm;

	public Germplasm getGermplasm() {
		return this.germplasm;
	}

	public void setGermplasm(Germplasm germplasm) {
		this.germplasm = germplasm;
	}

	@Override
	public String toString() {
		if (this.germplasm != null) {
			String crossName = this.germplasm.getCrossName();
			if(crossName != null){
				return crossName;
			}else {
				return "";
			}
		} else {
			return "Unknown";
		}
	}
}
