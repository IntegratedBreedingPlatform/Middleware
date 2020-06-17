package org.generationcp.middleware.api.inventory.study;

import java.util.List;

public interface StudyTransactionsService {

	long countStudyTransactions(Integer studyId, StudyTransactionsRequest studyTransactionsRequest);

	List<StudyTransactionsDto> searchStudyTransactions(final Integer studyId, StudyTransactionsRequest studyTransactionsRequest);
}
