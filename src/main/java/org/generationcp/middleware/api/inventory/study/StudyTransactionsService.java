package org.generationcp.middleware.api.inventory.study;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface StudyTransactionsService {

	long countStudyTransactions(Integer studyId, StudyTransactionsRequest studyTransactionsRequest);

	List<StudyTransactionsDto> searchStudyTransactions(Integer studyId, StudyTransactionsRequest studyTransactionsRequest, PageRequest pageRequest);
}
