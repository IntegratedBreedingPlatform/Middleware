package org.generationcp.middleware.api.study;

import java.util.List;

public interface AdvanceRequest {

	List<Integer> getInstanceIds();

	List<Integer> getSelectedReplications();

	List<String> getExcludedAdvancedRows();

	AbstractAdvanceRequest.SelectionTraitRequest getSelectionTraitRequest();

	<T> T accept(AdvanceRequestVisitor<T> visitor);

}
