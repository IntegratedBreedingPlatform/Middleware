package org.generationcp.middleware;

import org.generationcp.middleware.manager.UserDataManagerImplTest;
import org.generationcp.middleware.manager.UserRoleSetupTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({UserRoleSetupTest.class,UserDataManagerImplTest.class})
public class AllMWTests {

}
