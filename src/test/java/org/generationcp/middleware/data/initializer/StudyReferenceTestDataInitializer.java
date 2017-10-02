package org.generationcp.middleware.data.initializer;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.domain.dms.Reference;
import org.generationcp.middleware.domain.dms.StudyReference;

public class StudyReferenceTestDataInitializer {
	public static StudyReference createStudyReference(final Integer id, final String name, final String description) {
		StudyReference reference = new StudyReference(id, name, description);
		return reference;
	}
	
	public static List<Reference> createStudyReferenceList(final int numberOfEntries) {
		List<Reference> studyReferences = new ArrayList<>(); 
		for(int i=1; i<=numberOfEntries; i++) {
			studyReferences.add(createStudyReference(i, "Study Name " + i, "Study Description " + i));
		}
		return studyReferences;
	}
}
