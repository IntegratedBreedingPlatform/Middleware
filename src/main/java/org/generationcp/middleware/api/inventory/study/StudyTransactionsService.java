package org.generationcp.middleware.api.inventory.study;

import java.util.List;

public interface StudyTransactionsService {

	long countAllStudyTransactions(Integer studyId, StudyTransactionsRequest studyTransactionsRequest);

	long countFilteredStudyTransactions(Integer studyId, StudyTransactionsRequest studyTransactionsRequest);

	List<StudyTransactionsDto> searchStudyTransactions(final Integer studyId, StudyTransactionsRequest studyTransactionsRequest);
}
