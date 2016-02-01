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

import java.io.Serializable;

import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.service.pedigree.PedigreeDataManagerFactory;
import org.generationcp.middleware.util.CrossExpansionProperties;

public interface NewGermplasmCrossElement extends Serializable {

		String getCrossExpansionString(final String cropName, final CrossExpansionProperties crossExpansionProperties, PedigreeDataManagerFactory pedigreeDataManagerFactory);

		void setGermplasm(Germplasm germplasm);

		Germplasm getGermplasm();

}
