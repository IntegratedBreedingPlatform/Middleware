
package org.generationcp.middleware;

import org.generationcp.middleware.api.crop.CropService;
import org.generationcp.middleware.api.program.ProgramService;
import org.generationcp.middleware.domain.gms.GermplasmListType;
import org.generationcp.middleware.exceptions.MiddlewareException;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.DaoFactory;
import org.generationcp.middleware.manager.WorkbenchDaoFactory;
import org.generationcp.middleware.manager.api.GermplasmListManager;
import org.generationcp.middleware.pojos.GermplasmList;
import org.generationcp.middleware.pojos.GermplasmListData;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.workbench.CropType;
import org.generationcp.middleware.pojos.workbench.Project;
import org.generationcp.middleware.pojos.workbench.ProjectUserInfo;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.generationcp.middleware.pojos.workbench.WorkbenchUser;
import org.generationcp.middleware.service.api.user.UserService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Ignore("This is just for setting up some performance level data volumes. Not intended to run regularly on CI.")
@TransactionConfiguration(defaultRollback = false)
public class PerfDataSetupTest extends IntegrationTestBase {

	@Autowired
	private CropService cropService;

	@Autowired
	private UserService userService;

	@Autowired
	private ProgramService programService;

	@Autowired
	private GermplasmListManager germplasmListManager;
	private GermplasmTestDataGenerator germplasmTestDataGenerator;

	private static final int NUMBER_OF_GERMPLASM = 500;
	private static final int NUMBER_OF_LISTS = 500;
	private static final int MAX_NUMBER_OF_ENTRIES_PER_LIST = 20;

	private static final String GERMPLSM_PREFIX = "GP-VARIETY-";

	private static final Logger LOG = LoggerFactory.getLogger(PerfDataSetupTest.class);

	private DaoFactory daoFactory;
	private WorkbenchDaoFactory workbenchDaoFactory;

	@Before
	public void setUp() {
		if (this.daoFactory == null) {
			this.daoFactory = new DaoFactory(this.sessionProvder);
		}

		if (this.workbenchDaoFactory == null) {
			this.workbenchDaoFactory = new WorkbenchDaoFactory(this.workbenchSessionProvider);
		}

		if (this.germplasmTestDataGenerator == null) {
			this.germplasmTestDataGenerator = new GermplasmTestDataGenerator(this.sessionProvder, this.daoFactory);
		}
	}

	@Test
	public void setUpPerfTestData() throws MiddlewareException {
		final String programUUID = this.createWorkbenchProgram();
		this.createGermplasmListPerfData(programUUID);
	}

	private String createWorkbenchProgram() throws MiddlewareQueryException {

		final Person person = new Person();
		person.setInstituteId(1);
		person.setFirstName("Joe");
		person.setMiddleName("The");
		person.setLastName("Breeder");
		person.setPositionName("Plant Breeder");
		person.setTitle("Mr.");
		person.setExtension("123");
		person.setFax("No Fax");
		person.setEmail("joe.breeder@ibp.org");
		person.setNotes("No Notes");
		person.setContact("No Contact");
		person.setLanguage(1);
		person.setPhone("02121212121");
		this.workbenchDaoFactory.getPersonDAO().save(person);

		final WorkbenchUser workbenchUser = new WorkbenchUser();
		workbenchUser.setInstalid(1);
		workbenchUser.setStatus(1);
		workbenchUser.setAccess(1);
		workbenchUser.setType(1);
		workbenchUser.setName("joe");
		// Bcrypt string for password "b" generated at https://www.bcrypt-generator.com/
		workbenchUser.setPassword("$2a$08$sfZD1PpIrk3KHcqvUarui.eWRir4OXWYEaVSNcvyVK6EtkB5RzYl.");
		workbenchUser.setPerson(person);
		workbenchUser.setAssignDate(20150101);
		workbenchUser.setCloseDate(20150101);
		// Role ID 1 = ADMIN
		workbenchUser.setRoles(Arrays.asList(new UserRole(workbenchUser, 1)));

		this.workbenchDaoFactory.getWorkbenchUserDAO().save(workbenchUser);

		CropType cropType = this.cropService.getCropTypeByName("maize");
		if (cropType == null) {
			cropType = new CropType("maize");
			cropType.setDbName("ibdbv2_maize_merged");
			cropType.setVersion("4.0.0");
			this.daoFactory.getCropTypeDAO().saveOrUpdate(cropType);
		}

		final Project program = new Project();
		program.setProjectName("Draught Resistance in Maize" + new Random().nextInt(100));
		program.setUserId(workbenchUser.getUserid());
		program.setStartDate(new Date(System.currentTimeMillis()));
		program.setCropType(cropType);
		program.setLastOpenDate(new Date(System.currentTimeMillis()));
		this.programService.addProgram(program);

		// FIXME (BMS-4631) replace this with adding to workbench_project_user_info
		// this.workbenchDataManager.addProjectUserRole(projectUserRoles);

		final ProjectUserInfo pUserInfo = new ProjectUserInfo(program, workbenchUser);
		this.workbenchDaoFactory.getProjectUserInfoDAO().saveOrUpdate(pUserInfo);

		LOG.info("Workbench program and users created.");
		return program.getUniqueID();
	}

	private void createGermplasmListPerfData(final String programUUID) throws MiddlewareException {
		// Create germplasm
		final Integer[] gids =
				this.germplasmTestDataGenerator.createGermplasmRecords(PerfDataSetupTest.NUMBER_OF_GERMPLASM,
						PerfDataSetupTest.GERMPLSM_PREFIX);
		
		LOG.info(gids.length + " germplasm records created.");

		for (int listNumber = 1; listNumber <= PerfDataSetupTest.NUMBER_OF_LISTS; listNumber++) {

			// Create Germplasm list
			final GermplasmList germplasmList =
					new GermplasmList(null, "Test Germplasm List " + listNumber, Long.valueOf(20150101), GermplasmListType.LST.name(),
							Integer.valueOf(1), "Test Germplasm List" + listNumber, null, 1);
			germplasmList.setProgramUUID(programUUID);

			this.germplasmListManager.addGermplasmList(germplasmList);

			// Add random number of list data entries by selecting a random number of gid's
			final List<GermplasmListData> germplasmListData = new ArrayList<GermplasmListData>();
			final int numberOfEntries = new Random().nextInt(PerfDataSetupTest.MAX_NUMBER_OF_ENTRIES_PER_LIST);

			for (int entryNumber = 1; entryNumber <= numberOfEntries; entryNumber++) {
				final int randomGidIndex = new Random().nextInt(gids.length);
				germplasmListData.add(new GermplasmListData(null, germplasmList, gids[randomGidIndex], entryNumber,
					PerfDataSetupTest.GERMPLSM_PREFIX + entryNumber + " Source",
					PerfDataSetupTest.GERMPLSM_PREFIX + "Group A", 0, 0));
			}
			this.germplasmListManager.addGermplasmListData(germplasmListData);
			
			LOG.info(germplasmList.getName() + " created with " + numberOfEntries + " entries.");
		}		
		LOG.info("All done!");
	}
}
