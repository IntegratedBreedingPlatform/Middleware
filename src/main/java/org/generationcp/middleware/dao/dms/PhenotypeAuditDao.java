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

package org.generationcp.middleware.dao.dms;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.dao.audit.RevisionTypeResolver;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.service.api.dataset.ObservationAuditDTO;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;

/**
 * DAO class for Phenotype Audit
 */
@SuppressWarnings("unchecked")
public class PhenotypeAuditDao extends GenericDAO<ObservationAuditDTO, Integer> {

	private final static String COUNT_PHENOTYPE_AUD_QUERY =
		" SELECT count(1) "
			+ "FROM phenotype_aud p "
			+ "INNER JOIN nd_experiment e ON p.nd_experiment_id = e.nd_experiment_id AND e.obs_unit_id = :obsUnitId "
			+ "WHERE p.observable_id = :observableId";

	private final static String PHENOTYPE_AUD_QUERY =
		" SELECT p.phenotype_id as phenotypeId, p.value as value, "
			+ "IF((p.value is null AND prev_p_aud.value is null) OR p.value = coalesce(prev_p_aud.value, p.value), false, true) as valueChanged, "
			+ "p.draft_value as draftValue, "
			+ "IF((p.draft_value is null AND prev_p_aud.draft_value is null) OR p.draft_value = coalesce(prev_p_aud.draft_value, p.draft_value), false, true) as draftValueChanged, "
			+ "p.updated_by as updatedByUserId, p.updated_date  as updatedDate, p.rev_type as revisionType, "
			+ "(SELECT uname FROM workbench.users WHERE users.userid = p.updated_by) as updatedBy "
			+ "FROM phenotype_aud p "
			+ "  INNER JOIN nd_experiment e ON p.nd_experiment_id = e.nd_experiment_id AND e.obs_unit_id = :obsUnitId "
			+ "  LEFT JOIN phenotype_aud prev_p_aud ON prev_p_aud.aud_id = "
			+ "		(SELECT inn.aud_id "
			+ "		FROM phenotype_aud inn "
			+ "			 WHERE inn.phenotype_id = p.phenotype_id AND inn.aud_id < p.aud_id "
			+ " 	ORDER BY inn.aud_id DESC LIMIT 1) "
			+ "WHERE p.observable_id = :observableId "
			+ "ORDER BY p.aud_id DESC ";

	public long countObservationAudit(final String observationUnitId, final int observableId) {
		final SQLQuery query = this.getSession().createSQLQuery(COUNT_PHENOTYPE_AUD_QUERY);
		query.setParameter("observableId", observableId);
		query.setParameter("obsUnitId", observationUnitId);

		return ((BigInteger) query.uniqueResult()).longValue();
	}

	public List<ObservationAuditDTO> getObservationAuditByObservationUnitIdAndObservableId(final String observationUnitId,
		final int observableId, final Pageable pageable) {

		try {
			this.getSession().flush();

			final SQLQuery query = this.getSession().createSQLQuery(PHENOTYPE_AUD_QUERY);
			query.setParameter("observableId", observableId);
			query.setParameter("obsUnitId", observationUnitId);
			query.addScalar("phenotypeId").addScalar("value").addScalar("valueChanged", BooleanType.INSTANCE)
				.addScalar("draftValue").addScalar("draftValueChanged", BooleanType.INSTANCE).addScalar("updatedByUserId")
				.addScalar("updatedDate").addScalar("revisionType", RevisionTypeResolver.INSTANCE).addScalar("updatedBy");

			query.setResultTransformer(Transformers.aliasToBean(ObservationAuditDTO.class));

			GenericDAO.addPaginationToSQLQuery(query, pageable);

			return query.list();
		} catch (final HibernateException e) {
			throw new MiddlewareQueryException(
				"Error in getPhenotypeAuditByObservationUnitIdAndObservableId(" + observationUnitId + ", " + observableId
					+ ") in PhenotypeAuditDao: " + e
					.getMessage(),
				e);
		}
	}

}
