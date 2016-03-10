package org.generationcp.middleware.auditory;

import org.generationcp.middleware.dao.BibrefDAO;
import org.generationcp.middleware.pojos.Bibref;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuditorUT {

	public static final String DUMMY_FILENAME = "DUMMY_FILENAME";
	public static final String DUMMY_USERNAME = "DUMMY_USERNAME";
	public static final int DUMMY_ID = 10;
	@Mock
	AuditorDataManager managerMock;
	@Mock
	private BibrefDAO bibrefDaoMock;

	Auditor auditor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		auditor = new Auditor(managerMock);

	}

	@Test
	public void startAuditoryCreatesAuditoryElementWithId() throws Exception {
		//Given
		Bibref expectedAuditory = new Bibref();
		expectedAuditory.setRefid(DUMMY_ID);
		when(managerMock.getBibrefDao()).thenReturn(bibrefDaoMock);
		when(bibrefDaoMock.save(any(Bibref.class))).thenReturn(expectedAuditory);

		//When
		Auditory auditory = auditor.startAuditory(DUMMY_USERNAME, DUMMY_FILENAME);

		//Then
		assertThat(auditory.getId()).isGreaterThan(0);
		verify(bibrefDaoMock).save(any(Bibref.class));
	}

	@Test
	public void startAuditoryCreatesAuditoryElementWithFilename() throws Exception {
		//Given
		ArgumentCaptor<Bibref> auditoryCaptor = ArgumentCaptor.forClass(Bibref.class);
		when(managerMock.getBibrefDao()).thenReturn(bibrefDaoMock);

		//When
		auditor.startAuditory(DUMMY_USERNAME, DUMMY_FILENAME);

		//Then
		verify(bibrefDaoMock).save(auditoryCaptor.capture());
		Bibref capturedAuditory = auditoryCaptor.getValue();
		assertThat(capturedAuditory.getFilename()).isSameAs(DUMMY_FILENAME);
	}

	@Test
	public void startAuditoryCreatesAuditoryElementWithUsername() throws Exception {
		//Given
		ArgumentCaptor<Bibref> auditoryCaptor = ArgumentCaptor.forClass(Bibref.class);
		when(managerMock.getBibrefDao()).thenReturn(bibrefDaoMock);

		//When
		auditor.startAuditory(DUMMY_USERNAME, DUMMY_FILENAME);

		//Then
		verify(bibrefDaoMock).save(auditoryCaptor.capture());
		Bibref capturedAuditory = auditoryCaptor.getValue();
		assertThat(capturedAuditory.getUsername()).isSameAs(DUMMY_USERNAME);
	}

	@Test
	public void failStartAuditoryWhenManagerFails() {
		//Given
		when(managerMock.getBibrefDao()).thenThrow(Exception.class);

		//When
		try {
			auditor.startAuditory(DUMMY_USERNAME, DUMMY_FILENAME);
			fail("Should have failed");
		} catch (AuditoryException e) {
			assertThat(e).hasMessage("Could not start auditory");
		}

		//Then
		verify(bibrefDaoMock,never()).save(any(Bibref.class));

	}

	@Test
	public void failStartAuditoryWhenFilenameInputdataIsInvalid() {
		//When
		try {
			auditor.startAuditory(DUMMY_USERNAME, null);
			fail("Should have failed");
		} catch (AuditoryException e) {
			assertThat(e).hasMessage("Invalid input data");
		}

		//Then
		verify(bibrefDaoMock,never()).save(any(Bibref.class));
	}
	@Test
	public void failStartAuditoryWhenUsernameInputdataIsInvalid() {
		//When
		try {
			auditor.startAuditory(null, DUMMY_FILENAME);
			fail("Should have failed");
		} catch (AuditoryException e) {
			assertThat(e).hasMessage("Invalid input data");
		}

		//Then
		verify(bibrefDaoMock,never()).save(any(Bibref.class));
	}


}
