/*******************************************************************************
 * Copyright (c) 2012, All Rights Reserved.
 *
 * Generation Challenge Programme (GCP)
 *
 *
 * This software is licensed for use under the terms of the GNU General Public License (http://bit.ly/8Ztv8M) and the provisions of Part F
 * of the Generation Challenge Programme Amended Consortium Agreement (http://bit.ly/KQX1nL)
 *
 *******************************************************************************/

package org.generationcp.middleware.service;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.UUID;

import org.generationcp.middleware.domain.etl.StudyDetails;
import org.generationcp.middleware.domain.oms.StudyType;
import org.generationcp.middleware.manager.api.StudyDataManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FieldbookServiceImplTest {

	private Service fieldbookService;
	private StudyDataManager mockStudyDataManager;

	@Before
	public void before() {
		this.fieldbookService = spy(new FieldbookServiceImpl());
		this.mockStudyDataManager = Mockito.mock(StudyDataManager.class);

	}

	@Test
	public void getAllLocalNurseryDetails() throws Exception {
		doReturn(this.mockStudyDataManager).when(this.fieldbookService).getStudyDataManager();
		final String randomProgramUUID = UUID.randomUUID().toString();
		when(this.mockStudyDataManager.getAllStudyDetails(StudyType.N, randomProgramUUID)).thenReturn(new ArrayList<StudyDetails>());
		((FieldbookServiceImpl) this.fieldbookService).getAllLocalNurseryDetails(randomProgramUUID);

	}

	@Test
	public void getAllLocalTrialStudyDetails() throws Exception {
		doReturn(this.mockStudyDataManager).when(this.fieldbookService).getStudyDataManager();
		final String randomProgramUUID = UUID.randomUUID().toString();
		when(this.mockStudyDataManager.getAllStudyDetails(StudyType.T, randomProgramUUID)).thenReturn(new ArrayList<StudyDetails>());
		((FieldbookServiceImpl) this.fieldbookService).getAllLocalTrialStudyDetails(randomProgramUUID);

	}

}
