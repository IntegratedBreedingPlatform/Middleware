
package org.generationcp.middleware.manager.test;

import static org.junit.Assert.assertNotNull;
import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.manager.Database;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.UserDataManager;
import org.generationcp.middleware.pojos.Installation;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.util.Debug;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TestUserDataManagerImpl{

    private static ManagerFactory factory;
    private static UserDataManager manager;

    private long startTime;

    @Rule
    public TestName name = new TestName();


    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getUserDataManager();
    }


    @Before
    public void beforeEachTest() {
        Debug.println(0, "#####" + name.getMethodName() + " Start: ");
        startTime = System.nanoTime();
    }

    @After
    public void afterEachTest() {
        long elapsedTime = System.nanoTime() - startTime;
        Debug.println(0, "#####" + name.getMethodName() + " End: Elapsed Time = " + elapsedTime + " ns = " + ((double) elapsedTime / 1000000000) + " s");
    }

    @Test
    public void testGetAllUsers() throws Exception {
        List<User> users = manager.getAllUsers();
        assertNotNull(users);

        Debug.println(0, "testGetAllUsers(): " + users.size());
        for (User user : users) {
            Debug.println(0, "  " + user);
        }
    }

    @Test
    public void testCountAllUsers() throws Exception {
        long count = manager.countAllUsers();
        Debug.println(0, "testCountAllUsers(): " + count);
    }

    @Test
    public void testAddUser() throws MiddlewareQueryException {
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

        user = manager.getUserById(user.getUserid());
        assertNotNull(user);
        Debug.println(0, "testAddUser() ADDED: " + user);

        // cleanup
        manager.deleteUser(user);
    }

    @Test
    public void testGetAllPersons() throws Exception{
        List<Person> persons = manager.getAllPersons();
        assertNotNull(persons);

        Debug.println(0, "testGetAllPersons(): " + persons.size());
        for (Person person : persons) {
            Debug.println(0, "  " + person);
        }
    }

    @Test
    public void testCountAllPersons() throws Exception{
        long count = manager.countAllPersons();
        Debug.println(0, "testCountAllPersons(): " + count);
    }

    @Test
    public void testAddPerson() throws MiddlewareQueryException {
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

        person = manager.getPersonById(person.getId());
        assertNotNull(person);
        Debug.println(0, "testAddPerson() ADDED: " + person);

        // delete the person
        manager.deletePerson(person);
    }

    @Test
    public void testIsPersonExists() throws MiddlewareQueryException {
        String firstName = "PATERNO";
        String lastName = "BORLAGDAN";
        Debug.println(0, "testIsPersonExists(firstName=" + firstName + ", lastName=" + lastName + "): "
                + manager.isPersonExists(firstName, lastName));

        firstName = "PATTY";
        lastName = "Borly".toUpperCase();
        Debug.println(0, "testIsPersonExists(firstName=" + firstName + ", lastName=" + lastName + "): "
                + manager.isPersonExists(firstName, lastName));
    }

    @Test
    public void testIsUsernameExists() throws MiddlewareQueryException {
    	Debug.println(0, "testIsUsernameExists() ");
        String userName = "GMCLAREN";
        Debug.println(0, "Existing Username (" + userName + "): " + manager.isUsernameExists(userName));
        String userName2 = "CLAREN";
        Debug.println(0, "Non-existing Username (" + userName2 + "): " + manager.isUsernameExists(userName2));
    }

    @Test
    public void testGetAllInstallationRecords() throws Exception {
        List<Installation> results = manager.getAllInstallationRecords(0, 150, Database.CENTRAL);
        List<Installation> results2 = manager.getAllInstallationRecords(0, 150, Database.LOCAL);
        Debug.println(0, "testGetAllInstallationRecords()");
        Debug.println(0, "Central Database: " + results.size());
        for (Installation holder : results) {
            Debug.println(0, "  " + holder);
        }

        Debug.println(0, "Local Database: " + results2.size());
        for (Installation holder : results2) {
            Debug.println(0, "  " + holder);
        }
    }

    @Test
    public void testGetInstallationRecordById() throws Exception {
        Long id = Long.valueOf(1);
        Debug.println(0, "testGetInstallationRecordById(" + id + ")" + manager.getInstallationRecordById(id));
    }

    @Test
    public void testGetInstallationRecordsByAdminId() throws Exception {
        Long id = Long.valueOf(1);
        List<Installation> results = manager.getInstallationRecordsByAdminId(id);
        Debug.println(0, "testGetInstallationRecordsByAdminId(" + id + ") RESULTS: ");
        for (Installation holder : results) {
            Debug.println(0, "  " + holder);
        }
    }

    @Test
    public void testGetLatestInstallationRecord() throws Exception {
        Installation result = manager.getLatestInstallationRecord(Database.CENTRAL);
        Installation result2 = manager.getLatestInstallationRecord(Database.LOCAL);
        Debug.println(0, "Central Database: " + result);
        Debug.println(0, "Local Database: " + result2);
    }


    @Test
	public void testGetPersonById() throws Exception {

    	//central database
		int id = 1;
		Person personid = manager.getPersonById(id);
		assertNotNull(personid);
		Debug.println(0, "Central database: " + personid);

		//local database
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

        manager.addPerson(person);

		Person personid2 = manager.getPersonById(person.getId());
		assertNotNull(personid2);
		Debug.println(0, "Local Database: " + personid2);

        manager.deletePerson(person);
	}

    @Test
	public void testGetUserById() throws Exception {
    	//central database
		int id = 1;
		User userid = manager.getUserById(id);
		assertNotNull(userid);
		Debug.println(0, "Central Database: " + userid);

    	//local database
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

        User userid2 = manager.getUserById(user.getUserid());
        assertNotNull(userid2);
        Debug.println(0, "Local Database: " + userid2);

        // cleanup
        manager.deleteUser(user);
	}

    @Test
	public void testGetUserByUserName() throws Exception {
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

		String name = "user_test";
		User userName = manager.getUserByUserName(name);
		assertNotNull(userName);
		Debug.println(0, "testGetUserByUserName: " + userName);

	    manager.deleteUser(user);

	}

    @Test
    public void testIsValidUserLogin() throws MiddlewareQueryException {
    	String validuser = "GMCLAREN"; //enter valid username
    	String validpass = "IR123"; // enter valid password
        Debug.println(0, "testIsValidUserLogin (using valid username & password): " + manager.isValidUserLogin(validuser, validpass));
        String invaliduser = "username"; //enter invalid username
    	String invalidpass = "password"; // enter invalid password
        Debug.println(0, "testIsValidUserLogin (using invalid username & password): " + manager.isValidUserLogin(invaliduser, invalidpass));
    }


    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }
}
