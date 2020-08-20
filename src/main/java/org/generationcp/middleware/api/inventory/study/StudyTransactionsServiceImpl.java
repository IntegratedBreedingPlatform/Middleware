package org.generationcp.middleware.api.inventory.study;

import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.manager.DaoFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class StudyTransactionsServiceImpl implements StudyTransactionsService {

	private DaoFactory daoFactory;

	public StudyTransactionsServiceImpl() {
	}

	public StudyTransactionsServiceImpl(final HibernateSessionProvider sessionProvider) {
		this.daoFactory = new DaoFactory(sessionProvider);
	}

	@Override
	public long countStudyTransactions(final Integer studyId, final StudyTransactionsRequest studyTransactionsRequest) {
		return this.daoFactory.getTransactionDAO().countStudyTransactions(studyId, studyTransactionsRequest, false);
	}

	@Override
	public List<StudyTransactionsDto> searchStudyTransactions(final Integer studyId,
		final StudyTransactionsRequest studyTransactionsRequest) {

		return this.daoFactory.getTransactionDAO().searchStudyTransactions(studyId, studyTransactionsRequest, false);
	}

	@Override
	public List<StudyTransactionsDto> searchStudyTransactionsWithLotAggregatedData(final Integer studyId,
		final StudyTransactionsRequest studyTransactionsRequest) {
		return this.daoFactory.getTransactionDAO().searchStudyTransactions(studyId, studyTransactionsRequest, true);
	}
}
