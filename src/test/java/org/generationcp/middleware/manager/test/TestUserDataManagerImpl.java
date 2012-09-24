package org.generationcp.middleware.manager.test;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Installation;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.junit.AfterClass;
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
    /**
    @Test
    public void testGetAllUsers() {
        List<User> users = manager.getAllUsers();
        
        log.info("Retrieved {} users:", users.size());
        for (User user : users) {
            log.info("{}", new Object[] { user.getName() });
        }
    }
    
    @Test
    public void testCountAllUsers() {
    	
        int count = manager.countAllUsers();
        
        log.info("Retrieved users count:" + count);
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
 
    @Test
    public void testGetAllPersons() {
        List<Person> persons = manager.getAllPersons();
        
        log.info("Retrieved {} persons:", persons.size());
        for (Person person : persons) {
        	log.info("{} {} {}", new Object[] { person.getFirstName(), person.getMiddleName(), person.getLastName() });
        }
    }
    
    @Test
    public void testCountAllPersons() {
    	
        int count = manager.countAllPersons();
        
        log.info("Retrieved persons count:" + count);
    }   
    
    @Test
    public void testAddPerson() throws QueryException {
        Person person = new Person();
        person.setId(-1);
        person.setInstituteId(1);
        person.setFirstName("Lich");
        person.setMiddleName("Frozen");
        person.setLastName("King");
        person.setPositionName("King of Icewind Dale");
        person.setTitle("His Highness");
        person.setExtension("1");
        person.setFax("2");
        person.setEmail("lichking@blizzard.com");
        person.setNotes("notes");
        person.setContact("3");
        person.setLanguage(-1);
        person.setPhone("4");
        
        // add the person
        manager.addPerson(person);
        
        person = manager.getPersonById(-1);
        
        // delete the person
        manager.deletePerson(person);
    }
    
    @Test
    public void testIsPersonExists() throws QueryException {
        Boolean result = manager.isPersonExists("PATERNO", "BORLAGDAN");
        log.info(result.toString());
        result = manager.isPersonExists("PATTY", "Borly".toUpperCase());
        log.info(result.toString());
    }
    
    @Test
    public void testIsUsernameExists() throws QueryException {
        Boolean result = manager.isUsernameExists("GMCLAREN");
        log.info(result.toString());
        result = manager.isUsernameExists("GUESTret");
        log.info(result.toString());
    }
    **/
    @Test
    public void testGetAllInstallationRecords() throws Exception {
        List<Installation> results = manager.getAllInstallationRecords(0, 5, Database.CENTRAL);
        for(Installation holder : results){
            System.out.println(holder);
        }
    }
    
    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }
}
