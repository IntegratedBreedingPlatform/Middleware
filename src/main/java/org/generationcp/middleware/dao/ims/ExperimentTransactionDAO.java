package org.generationcp.middleware.dao.ims;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ims.ExperimentTransaction;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ExperimentTransactionDAO extends GenericDAO<ExperimentTransaction, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(ExperimentTransactionDAO.class);

	public Long countPlantingTransactionsByStatus(final List<Integer> observationUnitIds, final TransactionStatus transactionStatus) {
		if (observationUnitIds == null || observationUnitIds.isEmpty()) {
			return 0L;
		}
		try {
			final Query query = this.getSession().createQuery("select count(distinct t.id) from Transaction t "
				+ "inner join t.experimentTransactions et where et.type = :expTransactionType and et.experiment.id in (:obsUnitList) and t.status = :trnStatus")
				.setParameter("expTransactionType", ExperimentTransactionType.PLANTING.getId())
				.setParameterList("obsUnitList", observationUnitIds)
				.setParameter("trnStatus", transactionStatus.getIntValue());
			return (Long) query.uniqueResult();
		} catch (final HibernateException e) {
			final String message =
				"Error at countTransactionsByType observationUnitIds = " + observationUnitIds + ", transactionType + " + transactionStatus
					.getValue();
			ExperimentTransactionDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

}
