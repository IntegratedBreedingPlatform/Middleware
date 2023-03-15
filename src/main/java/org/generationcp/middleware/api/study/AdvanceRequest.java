package org.generationcp.middleware.api.study;

import java.util.List;

public interface AdvanceRequest {

	List<Integer> getInstanceIds();

	List<Integer> getSelectedReplications();

	List<Integer> getExcludedObservations();

	AbstractAdvanceRequest.SelectionTraitRequest getSelectionTraitRequest();

	<T> T accept(AdvanceRequestVisitor<T> visitor);

}
