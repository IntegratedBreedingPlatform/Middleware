package org.generationcp.middleware.pojos.ims;

import org.generationcp.middleware.exceptions.MiddlewareException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class LotStatusTest {

	@Test
	public void shouldGetIntValueByName() {
		assertThat(LotStatus.getIntValueByName(LotStatus.ACTIVE.name()), is(LotStatus.ACTIVE.getIntValue()));
		assertThat(LotStatus.getIntValueByName(LotStatus.CLOSED.name()), is(LotStatus.CLOSED.getIntValue()));
	}

	@Test
	public void shouldGetValueByName() {
		assertThat(LotStatus.getValueByName(LotStatus.ACTIVE.name()), is(LotStatus.ACTIVE));
		assertThat(LotStatus.getValueByName(LotStatus.CLOSED.name()), is(LotStatus.CLOSED));
	}

	@Test(expected = MiddlewareException.class)
	public void shouldFailGetValueByNameWithInvalidName() {
		LotStatus.getValueByName("invalidName");
	}

}
