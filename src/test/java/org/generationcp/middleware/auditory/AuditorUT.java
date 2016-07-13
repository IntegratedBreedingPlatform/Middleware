package org.generationcp.middleware.auditory;

import org.generationcp.middleware.dao.BibrefDAO;
import org.generationcp.middleware.manager.api.AuditorDataManager;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.UserDefinedField;
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
	Bibref expectedAuditory;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		auditor = new Auditor(managerMock);
		expectedAuditory = new Bibref();
		expectedAuditory.setRefid(DUMMY_ID);

	}

	@Test
	public void startAuditoryCreatesAuditoryElementWithId() throws Exception {
		//Given
		when(managerMock.save(any(Bibref.class))).thenReturn(expectedAuditory);

		//When
		Auditory auditory = auditor.startAuditory(DUMMY_USERNAME, DUMMY_FILENAME);

		//Then
		assertThat(auditory.getId()).isGreaterThan(0);
		verify(managerMock).save(any(Bibref.class));
	}

	@Test
	public void startAuditoryCreatesAuditoryElementWithFilename() throws Exception {
		//Given
		UserDefinedField userDefinedField = new UserDefinedField();
		when(managerMock.getBibrefType()).thenReturn(userDefinedField);
		ArgumentCaptor<Bibref> auditoryCaptor = ArgumentCaptor.forClass(Bibref.class);
		when(managerMock.save(any(Bibref.class))).thenReturn(expectedAuditory);


		//When
		auditor.startAuditory(DUMMY_USERNAME, DUMMY_FILENAME);

		//Then
		verify(managerMock).save(auditoryCaptor.capture());
		Bibref capturedAuditory = auditoryCaptor.getValue();
		assertThat(capturedAuditory.getFilename()).isSameAs(DUMMY_FILENAME);
	}

	@Test
	public void startAuditoryCreatesAuditoryElementWithUsername() throws Exception {
		//Given
		ArgumentCaptor<Bibref> auditoryCaptor = ArgumentCaptor.forClass(Bibref.class);
		when(managerMock.save(any(Bibref.class))).thenReturn(expectedAuditory);
		UserDefinedField userDefinedField = new UserDefinedField();
		when(managerMock.getBibrefType()).thenReturn(userDefinedField);

		//When
		auditor.startAuditory(DUMMY_USERNAME, DUMMY_FILENAME);

		//Then
		verify(managerMock).save(auditoryCaptor.capture());
		Bibref capturedAuditory = auditoryCaptor.getValue();
		assertThat(capturedAuditory.getUsername()).isSameAs(DUMMY_USERNAME);
	}

	@Test
	public void failStartAuditoryWhenManagerFails() throws AuditoryException {
		//Given
		UserDefinedField userDefinedField = new UserDefinedField();
		when(managerMock.getBibrefType()).thenReturn(userDefinedField);
		when(managerMock.save(any(Bibref.class))).thenThrow(Exception.class);

		//When
		try {
			auditor.startAuditory(DUMMY_USERNAME, DUMMY_FILENAME);
			fail("Should have failed");
		} catch (AuditoryException e) {
			assertThat(e).hasMessage("Could not start auditory");
		}

	}

	@Test
	public void failStartAuditoryWhenFilenameInputdataIsInvalid() throws AuditoryException {
		//When
		try {
			auditor.startAuditory(DUMMY_USERNAME, null);
			fail("Should have failed");
		} catch (AuditoryException e) {
			assertThat(e).hasMessage("Invalid input data");
		}

		//Then
		verify(managerMock,never()).save(any(Bibref.class));
	}
	@Test
	public void failStartAuditoryWhenUsernameInputdataIsInvalid() throws AuditoryException {
		//When
		try {
			auditor.startAuditory(null, DUMMY_FILENAME);
			fail("Should have failed");
		} catch (AuditoryException e) {
			assertThat(e).hasMessage("Invalid input data");
		}

		//Then
		verify(managerMock,never()).save(any(Bibref.class));
	}

	@Test
	public void auditableElementIsAttachedToAuditory() throws AuditoryException {
		Bibref expectedAuditory = new Bibref();
		expectedAuditory.setRefid(DUMMY_ID);
		when(managerMock.save(any(Bibref.class))).thenReturn(expectedAuditory);
		Auditable auditableMock = mock(Auditable.class);

		//When
		auditor.startAuditory(DUMMY_USERNAME, DUMMY_FILENAME);
		auditor.audit(auditableMock);

		//Then
		verify(auditableMock).audit(any(Bibref.class));
	}
}
