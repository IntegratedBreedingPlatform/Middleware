package org.generationcp.middleware.dao.ims;

import org.generationcp.middleware.dao.GenericDAO;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.pojos.ims.ExperimentTransaction;
import org.generationcp.middleware.pojos.ims.ExperimentTransactionType;
import org.generationcp.middleware.pojos.ims.Transaction;
import org.generationcp.middleware.pojos.ims.TransactionStatus;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ExperimentTransactionDAO extends GenericDAO<ExperimentTransaction, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(ExperimentTransactionDAO.class);

	public Long countPlantingTransactionsByStatus(final List<Integer> ndExperimentIds, final TransactionStatus transactionStatus) {
		if (ndExperimentIds == null || ndExperimentIds.isEmpty()) {
			return 0L;
		}
		try {
			final Query query = this.getSession().createQuery("select count(distinct t.id) from Transaction t "
				+ "inner join t.experimentTransactions et where et.type = :expTransactionType and et.experiment.id in (:ndExperimentIdsList) and t.status = :trnStatus")
				.setParameter("expTransactionType", ExperimentTransactionType.PLANTING.getId())
				.setParameterList("ndExperimentIdsList", ndExperimentIds)
				.setParameter("trnStatus", transactionStatus.getIntValue());
			return (Long) query.uniqueResult();
		} catch (final HibernateException e) {
			final String message =
				"Error at countTransactionsByType ndExperimentIds = " + ndExperimentIds + ", transactionType = " + transactionStatus
					.getValue();
			ExperimentTransactionDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<Transaction> getTransactionsByNdExperimentIds(final List<Integer> ndExperimentIds,
		final TransactionStatus transactionStatus, final ExperimentTransactionType experimentTransactionType) {
		if (ndExperimentIds == null || ndExperimentIds.isEmpty()) {
			return new ArrayList<>();
		}
		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class, "transaction");
			criteria.createAlias("transaction.experimentTransactions", "experimentTransaction", Criteria.INNER_JOIN);
			criteria.add(Restrictions.eq("status", transactionStatus.getIntValue()));
			criteria.add(Restrictions.in("experimentTransaction.experiment.id", ndExperimentIds));
			criteria.add(Restrictions.eq("experimentTransaction.type", experimentTransactionType.getId()));
			return criteria.list();
		} catch (final HibernateException e) {
			final String message =
				"Error at getTransactionsByNdExperimentIds ndExperimentIds = " + ndExperimentIds + ", transactionType + "
					+ transactionStatus
					.getValue() + ", experimentTransactionStatus=" + experimentTransactionType.getValue();
			ExperimentTransactionDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

}
