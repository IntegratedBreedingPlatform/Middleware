package org.generationcp.middleware.service.api.study;

import java.util.List;

public interface TraitService {
	
	List<String> getTraits(final int studyBusinessIdentifier);
}