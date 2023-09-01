package org.generationcp.middleware.api.study;

import java.util.List;

public interface AdvanceRequest {

	List<Integer> getInstanceIds();

	List<Integer> getSelectedReplications();

	AbstractAdvanceRequest.SelectionTraitRequest getSelectionTraitRequest();

	<T> T accept(AdvanceRequestVisitor<T> visitor);

	boolean isPropagateDescriptors();

	List<Integer> getDescriptorIds();

	boolean isOverrideDescriptorsLocation();

	Integer getLocationOverrideId();
}
