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

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.oms.TermProperty;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.pojos.oms.CVTermProperty;

public class TermPropertyBuilder extends Builder {

	private DaoFactory daoFactory;


	public TermPropertyBuilder(HibernateSessionProvider sessionProviderForLocal) {
		super(sessionProviderForLocal);
		daoFactory = new DaoFactory(sessionProviderForLocal);
	}

	public TermProperty get(int termPropertyId) throws MiddlewareQueryException {
		TermProperty term = null;
		term = this.create(daoFactory.getCvTermPropertyDao().getById(termPropertyId));
		return term;
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

	public List<CVTermProperty> findProperties(int cvTermId) throws MiddlewareQueryException {
		return daoFactory.getCvTermPropertyDao().getByCvTermId(cvTermId);
	}

	public List<CVTermProperty> findPropertiesByType(int cvTermId, int typeId) throws MiddlewareQueryException {
		return daoFactory.getCvTermPropertyDao().getByCvTermAndType(cvTermId, typeId);
	}

	public CVTermProperty findPropertyByType(int cvTermId, int typeId) throws MiddlewareQueryException {
		return daoFactory.getCvTermPropertyDao().getOneByCvTermAndType(cvTermId, typeId);
	}
}
