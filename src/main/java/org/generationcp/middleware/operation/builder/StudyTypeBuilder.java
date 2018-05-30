package org.generationcp.middleware.operation.builder;

import org.generationcp.middleware.domain.study.StudyTypeDto;
import org.generationcp.middleware.pojos.dms.StudyType;

import java.util.ArrayList;
import java.util.List;

public class StudyTypeBuilder {

	public StudyTypeBuilder() {
		super();
	}

	public StudyTypeDto createStudyTypeDto(final StudyType studyType) {
		if (studyType != null) {
			return new StudyTypeDto(studyType.getStudyTypeId(), studyType.getLabel(), studyType.getName(), studyType.getCvTermId(),
				studyType.isVisible());
		}
		return null;
	}

	public List<StudyTypeDto> createStudyTypeDto(final List<StudyType> all) {
		final List<StudyTypeDto> list = new ArrayList<>();
		for (final StudyType st: all) {
			list.add(createStudyTypeDto(st));
		}
		return list;
	}
}
