
package org.generationcp.middleware;

import org.generationcp.middleware.manager.CrossStudyDataManagerImplTest;
import org.generationcp.middleware.manager.GermplasmDataManagerImplTest;
import org.generationcp.middleware.manager.GermplasmDataManagerUtilTest;
import org.generationcp.middleware.manager.OntologyDataManagerImplIntegrationTest;
import org.generationcp.middleware.manager.UserDataManagerImplTest;
import org.generationcp.middleware.manager.UserRoleSetupTest;
import org.generationcp.middleware.manager.WorkbenchDataManagerImplTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({UserRoleSetupTest.class, UserDataManagerImplTest.class, CrossStudyDataManagerImplTest.class,
		GermplasmDataManagerUtilTest.class, GermplasmDataManagerImplTest.class, OntologyDataManagerImplIntegrationTest.class,
		WorkbenchDataManagerImplTest.class})
public class AllMWTests {

}
