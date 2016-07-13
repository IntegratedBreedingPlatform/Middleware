package org.generationcp.middleware.manager;

import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.manager.api.AuditorDataManager;
import org.generationcp.middleware.pojos.Bibref;
import org.generationcp.middleware.pojos.UserDefinedField;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.fest.assertions.api.Assertions.assertThat;

public class AuditorDataManagerImplIT extends IntegrationTestBase {

	public static final String DUMMY_STRING = " ";
	public static final int DUMMY_PUBDATE = 123455;
	@Autowired
	private AuditorDataManager manager;

	@Test
	public void bibrefTypeIsRetrievedFromTheDatabase() throws Exception {
		UserDefinedField bibrefType = manager.getBibrefType();

		assertThat(bibrefType).isNotNull();
	}

	@Test
	public void auditoryIsSavedInTheDatabase() throws Exception {
		Bibref ref = new Bibref(0);
		ref.setType(manager.getBibrefType());
		ref.setPucntry(DUMMY_STRING);
		ref.setVolume(DUMMY_STRING);
		ref.setMonogr(DUMMY_STRING);
		ref.setAuthors(DUMMY_STRING);
		ref.setPucity(DUMMY_STRING);
		ref.setAnalyt(DUMMY_STRING);
		ref.setEditors(DUMMY_STRING);
		ref.setIssue(DUMMY_STRING);
		ref.setPublish(DUMMY_STRING);
		ref.setSeries(DUMMY_STRING);
		ref.setPagecol(DUMMY_STRING);
		ref.setPubdate(DUMMY_PUBDATE);
		Bibref save = manager.save(ref);

		assertThat(save).isNotNull();
		assertThat(save.getId()).isGreaterThan(0);
		assertThat(manager.getAuditory(save.getId())).isEqualTo(save);
	}
}
