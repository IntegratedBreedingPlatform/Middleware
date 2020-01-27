
package org.generationcp.middleware.service.impl.study;

import org.generationcp.middleware.DataSetupTest;
import org.generationcp.middleware.GermplasmTestDataGenerator;
import org.generationcp.middleware.IntegrationTestBase;
import org.generationcp.middleware.dao.GermplasmListDAO;
import org.generationcp.middleware.dao.ListDataProjectDAO;
import org.generationcp.middleware.data.initializer.GermplasmListTestDataInitializer;
import org.generationcp.middleware.data.initializer.ListDataProjectTestDataInitializer;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.manager.api.GermplasmDataManager;
import org.generationcp.middleware.pojos.Germplasm;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.ListDataProject;
import org.generationcp.middleware.service.api.study.StudyGermplasmDto;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * The class <code>StudyGermplasmListServiceImplTest</code> contains tests for the class <code>{@link StudyGermplasmListServiceImpl}</code>.
 */
public class StudyGermplasmListServiceImplTest extends IntegrationTestBase {

	private static final String GERMPLASM_PREFERRED_NAME_PREFIX = DataSetupTest.GERMPLSM_PREFIX + "PR-";
	private static final Integer STUDY_ID = 54321;


	@Autowired
	private GermplasmDataManager germplasmManager;

	private StudyGermplasmListServiceImpl service;
	private GermplasmListDAO listDao;
	private ListDataProjectDAO listDataProjectDAO;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;
	private List<Integer> gids;


	@Before
	public void setup() {
		this.service = new StudyGermplasmListServiceImpl(this.sessionProvder.getSession());
		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.germplasmManager);
		}
		this.listDao = new GermplasmListDAO();
		this.listDao.setSession(this.sessionProvder.getSession());
		this.listDataProjectDAO = new ListDataProjectDAO();
		this.listDataProjectDAO.setSession(this.sessionProvder.getSession());
		this.setupData();
	}

	@Test
	public void test() {
		final List<StudyGermplasmDto> germplasmList = this.service.getGermplasmList(STUDY_ID);
		Assert.assertEquals(DataSetupTest.NUMBER_OF_GERMPLASM, germplasmList.size());
		int index = 1;
		for (final StudyGermplasmDto dto : germplasmList) {
			Assert.assertEquals(index++, dto.getEntryNumber().intValue());
		}
	}

	private void setupData() {
		final GermplasmList list = new GermplasmListTestDataInitializer().createGermplasmList("TESTLIST", 1, "TESTLIST-DESC", null, 1, "");
		list.setType(GermplasmListType.STUDY.name());
		list.setProjectId(STUDY_ID);
		this.listDao.save(list);

		// Save a second advance list
		final GermplasmList list2 = new GermplasmListTestDataInitializer().createGermplasmList("ADVLIST", 1, "ADVLIST-DESC", null, 1, "");
		list2.setType(GermplasmListType.ADVANCED.name());
		list2.setProjectId(STUDY_ID);
		this.listDao.save(list2);

		// Save entries for study list
		final Germplasm parentGermplasm = this.germplasmTestDataGenerator.createGermplasmWithPreferredAndNonpreferredNames();
		final Integer[] gids = this.germplasmTestDataGenerator
			.createChildrenGermplasm(DataSetupTest.NUMBER_OF_GERMPLASM, StudyGermplasmListServiceImplTest.GERMPLASM_PREFERRED_NAME_PREFIX,
				parentGermplasm);
		this.gids = Arrays.asList(gids);
		for (int i=1; i<=DataSetupTest.NUMBER_OF_GERMPLASM; i++) {
			final Integer gid = this.gids.get(DataSetupTest.NUMBER_OF_GERMPLASM - i);
			final ListDataProject listDataProject = ListDataProjectTestDataInitializer
				.createListDataProject(list, gid, 0, i, "entryCode", "seedSource", "DESIG-" +gid, "groupName",
					"duplicate", "notes", 20170125);
			this.listDataProjectDAO.save(listDataProject);
		}

	}

}
