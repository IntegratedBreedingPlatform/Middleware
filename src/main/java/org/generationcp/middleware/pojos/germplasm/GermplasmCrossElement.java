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
import java.util.List;

import org.generationcp.middleware.pojos.Name;

public interface GermplasmCrossElement extends Serializable {

		void setLevel(Integer level);

		void setNames(List<Name> name);

		void setRootLevel(Integer rootLevel);

		Integer getRootLevel();

}
