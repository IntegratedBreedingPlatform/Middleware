package org.generationcp.middleware.pojos.dms;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class DmsProjectTest {

	@Test
	public void getNextPropertyRank() {
		final DmsProject project = Mockito.mock(DmsProject.class, Mockito.CALLS_REAL_METHODS);
		final List<ProjectProperty> projectProperties = Arrays.asList(
			createMockProjectProperty(2),
			createMockProjectProperty(3),
			createMockProjectProperty(1),
			createMockProjectProperty(4));
		Mockito.when(project.getProperties()).thenReturn(projectProperties);

		assertThat(project.getNextPropertyRank(), is(5));
	}

	@Test
	public void getNextPropertyRank_NoProperties() {
		final DmsProject project = Mockito.mock(DmsProject.class, Mockito.CALLS_REAL_METHODS);
		Mockito.when(project.getProperties()).thenReturn(new ArrayList<>());
		assertThat(project.getNextPropertyRank(), is(1));
	}

	private ProjectProperty createMockProjectProperty(final int rank) {
		final ProjectProperty property = Mockito.mock(ProjectProperty.class);
		Mockito.when(property.getRank()).thenReturn(rank);
		return property;
	}

}
