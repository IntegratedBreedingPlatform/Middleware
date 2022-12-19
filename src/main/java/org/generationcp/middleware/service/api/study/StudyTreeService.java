package org.generationcp.middleware.service.api.study;

public interface StudyTreeService {

	Integer createStudyTreeFolder(final int parentId, final String name, final String programUUID);

}
