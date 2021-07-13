package org.generationcp.middleware.pojos;

import org.generationcp.middleware.ContextHolder;
import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AbstractEntityTest {

	private static final int USER_ID = new Random().nextInt();

	@Test
	public void shouldHasModifiedFields() {
		assertTrue(this.getField(AbstractEntity.MODIFIED_BY_FIELD_NAME));
		assertTrue(this.getField(AbstractEntity.MODIFIED_DATE_FIELD_NAME));
	}

	@Test
	public void shouldCreate() {
		ContextHolder.setLoggedInUserId(USER_ID);

		final Dummy dummy = new Dummy();
		assertNotNull(dummy.getCreatedDate());
		assertThat(dummy.getCreatedBy(), is(USER_ID));
	}

	private boolean getField(final String fieldName) {
		return Arrays.stream(AbstractEntity.class.getDeclaredFields())
			.filter(field -> field.getName().equals(fieldName))
			.findFirst()
			.isPresent();
	}

	private static class Dummy extends AbstractEntity {

	}

}
