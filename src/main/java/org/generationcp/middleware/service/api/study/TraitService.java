
package org.generationcp.middleware.service.api.study;

import java.util.List;

public interface TraitService {

	List<TraitDto> getTraits(final int studyBusinessIdentifier);

	List<TraitDto> getSelectionMethods(final int studyIdentifier);
}
