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
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ExperimentTransactionDAO extends GenericDAO<ExperimentTransaction, Integer> {

	private static final Logger LOG = LoggerFactory.getLogger(ExperimentTransactionDAO.class);

	public ExperimentTransactionDAO(final Session session) {
		super(session);
	}

	public Long countTransactionsByNdExperimentIds(final List<Integer> ndExperimentIds, final TransactionStatus transactionStatus,
		final ExperimentTransactionType experimentTransactionType) {
		if (ndExperimentIds == null || ndExperimentIds.isEmpty()) {
			return 0L;
		}
		try {
			final Query query = this.getSession().createQuery("select count(distinct t.id) from Transaction t "
				+ "inner join t.experimentTransactions et where et.type = :expTransactionType and et.experiment.id in (:ndExperimentIdsList) and t.status = :trnStatus")
				.setParameter("expTransactionType", experimentTransactionType.getId())
				.setParameterList("ndExperimentIdsList", ndExperimentIds)
				.setParameter("trnStatus", transactionStatus.getIntValue());
			return (Long) query.uniqueResult();
		} catch (final HibernateException e) {
			final String message =
				"Error at countPlantingTransactionsByStatus ndExperimentIds = " + ndExperimentIds + ", transactionType = " + transactionStatus
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
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
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

	public List<Transaction> getTransactionsByStudyId(final Integer studyId,
		final TransactionStatus transactionStatus, final ExperimentTransactionType experimentTransactionType) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class, "transaction");
			criteria.createAlias("transaction.experimentTransactions", "experimentTransaction", Criteria.INNER_JOIN);
			criteria.createAlias("experimentTransaction.experiment.project", "project", Criteria.INNER_JOIN);
			criteria.add(Restrictions.eq("status", transactionStatus.getIntValue()));
			criteria.add(Restrictions.eq("project.study.projectId", studyId));
			criteria.add(Restrictions.eq("experimentTransaction.type", experimentTransactionType.getId()));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			return criteria.list();
		} catch (final HibernateException e) {
			final String message =
				"Error at getTransactionsByStudyId studyId = " + studyId + ", transactionType + "
					+ transactionStatus
					.getValue() + ", experimentTransactionStatus=" + experimentTransactionType.getValue();
			ExperimentTransactionDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<Transaction> getTransactionsByStudyAndEntryId(final Integer studyId, final Integer entryId,
													  final TransactionStatus transactionStatus, final ExperimentTransactionType experimentTransactionType) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class, "transaction");
			criteria.createAlias("transaction.experimentTransactions", "experimentTransaction", Criteria.INNER_JOIN);
			criteria.createAlias("experimentTransaction.experiment.project", "project", Criteria.INNER_JOIN);
			criteria.createAlias("experimentTransaction.experiment.stock", "stock", Criteria.INNER_JOIN);
			criteria.add(Restrictions.eq("status", transactionStatus.getIntValue()));
			criteria.add(Restrictions.eq("project.study.projectId", studyId));
			criteria.add(Restrictions.eq("stock.stockId", entryId));
			criteria.add(Restrictions.eq("experimentTransaction.type", experimentTransactionType.getId()));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			return criteria.list();
		} catch (final HibernateException e) {
			final String message =
					"Error at getTransactionsByStudyId studyId = " + studyId + ", transactionType + "
							+ transactionStatus
							.getValue() + ", experimentTransactionStatus=" + experimentTransactionType.getValue();
			ExperimentTransactionDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public List<Transaction> getTransactionsByInstanceIds(final List<Integer> instanceIds,
		final TransactionStatus transactionStatus, final ExperimentTransactionType experimentTransactionType) {
		try {
			final Criteria criteria = this.getSession().createCriteria(Transaction.class, "transaction");
			criteria.createAlias("transaction.experimentTransactions", "experimentTransaction", Criteria.INNER_JOIN);
			criteria.createAlias("experimentTransaction.experiment.geoLocation", "geolocation", Criteria.INNER_JOIN);
			criteria.add(Restrictions.eq("status", transactionStatus.getIntValue()));
			criteria.add(Restrictions.in("geolocation.locationId", instanceIds));
			criteria.add(Restrictions.eq("experimentTransaction.type", experimentTransactionType.getId()));
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			return criteria.list();
		} catch (final HibernateException e) {
			final String message =
				"Error at getTransactionsByInstanceIds instanceIds = " + instanceIds + ", transactionType + "
					+ transactionStatus
					.getValue() + ", experimentTransactionStatus=" + experimentTransactionType.getValue();
			ExperimentTransactionDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

	public void deleteExperimentTransactionsByStudyId(final Integer studyId, final ExperimentTransactionType experimentTransactionType) {
		try {
			final String sqlDelete =
				"delete et from ims_experiment_transaction et"
					+ " inner join ims_transaction t on (t.trnid = et.trnid) "
					+ " inner join nd_experiment e on (e.nd_experiment_id = et.nd_experiment_id) "
					+ " inner join project p on (p.project_id = e.project_id) "
					+ " where et.type = :type and p.study_id = :studyId "
					+ " and t.trnstat = :transactionStatus";
			this.getSession().createSQLQuery(sqlDelete)
				.setParameter("studyId", studyId)
				.setParameter("type", experimentTransactionType.getId())
				.setParameter("transactionStatus", TransactionStatus.CANCELLED.getIntValue())
				.executeUpdate();
		} catch (final HibernateException e) {
			final String message =
				"Error at deleteExperimentTransactionsByStudyId studyId = " + studyId + ", experimentTransactionType + "
					+ experimentTransactionType.getValue();
			ExperimentTransactionDAO.LOG.error(message, e);
			throw new MiddlewareQueryException(message, e);
		}
	}

}
