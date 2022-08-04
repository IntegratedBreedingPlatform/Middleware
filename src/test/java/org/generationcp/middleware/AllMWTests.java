
package org.generationcp.middleware;

import org.generationcp.middleware.manager.CrossStudyDataManagerImplTest;
import org.generationcp.middleware.manager.GermplasmDataManagerIntegrationTest;
import org.generationcp.middleware.manager.GermplasmDataManagerUtilTest;
import org.generationcp.middleware.manager.OntologyDataManagerImplIntegrationTest;
import org.generationcp.middleware.manager.RoleServiceImplTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({CrossStudyDataManagerImplTest.class, GermplasmDataManagerUtilTest.class,
	GermplasmDataManagerIntegrationTest.class, OntologyDataManagerImplIntegrationTest.class, RoleServiceImplTest.class})
public class AllMWTests {

}
