package org.generationcp.middleware;

import java.util.Date;

import org.generationcp.middleware.manager.ontology.TestDataHelper;
import org.generationcp.middleware.util.Clock;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;

public abstract class UnitTestBase extends TestBase {

    @Before
    public void setup(){
        String test_database = "test_ibdbv2_maize_merged";
        ContextHolder.setCurrentCrop(test_database);
    }

	@Mock
	private Clock systemClock;

	protected void stubCurrentDate(int year, int month, int day) {
		stubCurrentDate(TestDataHelper.constructDate(year, month, day));
	}

	protected void stubCurrentDate(Date date) {
		Mockito.doReturn(date).when(systemClock).now();
	}

}
