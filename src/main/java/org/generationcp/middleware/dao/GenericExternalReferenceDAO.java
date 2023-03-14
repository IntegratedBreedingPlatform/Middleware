package org.generationcp.middleware.dao;

import org.generationcp.middleware.api.brapi.v2.germplasm.ExternalReferenceDTO;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;

import java.util.List;

public abstract class GenericExternalReferenceDAO<T> extends GenericDAO<T, Integer> {

	public GenericExternalReferenceDAO(final Session session) {
		super(session);
	}

	public List<ExternalReferenceDTO> getExternalReferences(final List<Integer> ids) {
		final SQLQuery sqlQuery = this.getSession().createSQLQuery(
			"SELECT CAST(" + this.getIdField() + " AS CHAR(255)) as entityId, reference_id as referenceID, reference_source as referenceSource "
				+ "FROM " + this.getReferenceTable() + " WHERE " + this.getIdField() + " IN (:ids)");

		sqlQuery.addScalar("entityId").addScalar("referenceID").addScalar("referenceSource")
			.setResultTransformer(new AliasToBeanResultTransformer(ExternalReferenceDTO.class));

		sqlQuery.setParameterList("ids", ids);

		return sqlQuery.list();
	}


	abstract String getIdField();

	abstract String getReferenceTable();


}
