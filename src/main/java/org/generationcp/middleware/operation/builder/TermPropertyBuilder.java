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

package org.generationcp.middleware.operation.builder;

import org.generationcp.middleware.domain.oms.TermProperty;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.oms.CVTermProperty;

import java.util.ArrayList;
import java.util.List;

public class TermPropertyBuilder extends Builder {

	public TermPropertyBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
	}

	public TermProperty create(CVTermProperty cVTermProperty) {
		TermProperty termProperty = null;
		if (cVTermProperty != null) {
			termProperty =
					new TermProperty(cVTermProperty.getCvTermPropertyId(), cVTermProperty.getTypeId(), cVTermProperty.getValue(),
							cVTermProperty.getRank());
		}
		return termProperty;
	}

	public List<TermProperty> create(List<CVTermProperty> cvTermProperties) {
		List<TermProperty> properties = new ArrayList<TermProperty>();

		if (cvTermProperties != null && !cvTermProperties.isEmpty()) {
			for (CVTermProperty cvTermProperty : cvTermProperties) {
				properties.add(this.create(cvTermProperty));
			}
		}

		return properties;
	}

}
