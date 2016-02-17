
package org.generationcp.middleware;

import org.generationcp.middleware.manager.CrossStudyDataManagerImplTest;
import org.generationcp.middleware.manager.GermplasmDataManagerIntegrationTest;
import org.generationcp.middleware.manager.GermplasmDataManagerUtilTest;
import org.generationcp.middleware.manager.OntologyDataManagerImplIntegrationTest;
import org.generationcp.middleware.manager.UserDataManagerImplTest;
import org.generationcp.middleware.manager.WorkbenchDataManagerImplTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({UserDataManagerImplTest.class, CrossStudyDataManagerImplTest.class, GermplasmDataManagerUtilTest.class,
	GermplasmDataManagerIntegrationTest.class, OntologyDataManagerImplIntegrationTest.class, WorkbenchDataManagerImplTest.class})
public class AllMWTests {

}
