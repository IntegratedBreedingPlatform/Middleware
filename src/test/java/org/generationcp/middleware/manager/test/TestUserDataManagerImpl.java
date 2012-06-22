package org.generationcp.middleware.manager.test;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.User;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUserDataManagerImpl {
private final static Logger log = LoggerFactory.getLogger(TestUserDataManagerImpl.class);
    
    private static ManagerFactory factory;
    private static UserDataManager manager;
    
    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getUserDataManager();
    }
    
    @Test
    public void testGetAllUsers() {
        List<User> users = manager.getAllUsers();
        
        log.info("Retrieved {} users:", users.size());
        for (User user : users) {
            log.info("{}", new Object[] { user.getName() });
        }
    }
    
    @Test
    public void testAddUser() throws QueryException {
        User user = new User();
        user.setUserid(-1);
        user.setInstalid(-1);
        user.setStatus(-1);
        user.setAccess(-1);
        user.setUserid(-1);
        user.setType(-1);
        user.setName("user_test");
        user.setPassword("user_password");
        user.setPersonid(-1);
        user.setAdate(20120101);
        user.setCdate(20120101);
        
        manager.addUser(user);
        
        user = manager.getUserById(-1);
        
        manager.deleteUser(user);
    }
}
