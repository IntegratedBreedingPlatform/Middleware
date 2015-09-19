package org.generationcp.middleware;

import java.util.Date;

import org.generationcp.middleware.util.Clock;
import org.mockito.Mock;
import org.mockito.Mockito;

public abstract class UnitTestBase extends TestBase {

	@Mock
	private Clock systemClock;

	protected void stubCurrentDate(int year, int month, int day) {
		stubCurrentDate(this.constructDate(year, month, day));
	}

	protected void stubCurrentDate(Date date) {
		Mockito.doReturn(date).when(systemClock).now();
	}

}
