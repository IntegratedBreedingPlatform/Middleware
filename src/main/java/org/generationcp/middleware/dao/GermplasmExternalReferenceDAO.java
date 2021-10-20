package org.generationcp.middleware.dao;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.GermplasmExternalReference;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GermplasmExternalReferenceDAO extends GenericExternalReferenceDAO<GermplasmExternalReference> {

	@Override
	String getIdField() {
		return "gid";
	}

	@Override
	String getReferenceTable() {
		return "external_reference_germplasm";
	}

}
