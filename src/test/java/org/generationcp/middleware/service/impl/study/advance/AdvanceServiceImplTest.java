package org.generationcp.middleware.service.impl.study.advance;

import org.apache.commons.lang3.RandomStringUtils;
import org.generationcp.middleware.api.study.AdvanceStudyRequest;
import org.generationcp.middleware.pojos.Method;
import org.generationcp.middleware.service.api.dataset.ObservationUnitRow;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class AdvanceServiceImplTest {

	@InjectMocks
	private AdvanceServiceImpl advanceService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

}
