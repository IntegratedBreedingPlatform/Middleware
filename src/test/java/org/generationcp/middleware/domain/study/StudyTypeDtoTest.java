package org.generationcp.middleware.domain.study;

import org.junit.Assert;
import org.junit.Test;

public class StudyTypeDtoTest {
	
	private static final StudyTypeDto TRIAL_FILTER = new StudyTypeDto(1, "Trial", "T");
	private static final StudyTypeDto NURSERY_FILTER = new StudyTypeDto(2, "Nursery", "N");
	
	@Test
	public void testGenerateStudyTypeFilterLabel(){
		Assert.assertEquals("Trials", TRIAL_FILTER.getPluralFormOfLabel());
		Assert.assertEquals("Nurseries", NURSERY_FILTER.getPluralFormOfLabel());
	}

}
