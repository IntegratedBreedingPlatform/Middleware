
package org.generationcp.middleware.manager.test;

import java.util.List;

import org.generationcp.middleware.exceptions.MiddlewareQueryException;
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

public class TestUserDataManagerImpl{

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

        System.out.println("testGetAllUsers() RESULTS: " + users.size());
        for (User user : users) {
            System.out.println("  " + user);
        }
    }

    @Test
    public void testCountAllUsers() {
        long count = manager.countAllUsers();
        System.out.println("testCountAllUsers(): " + count);
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

        user = manager.getUserById(-1);
        System.out.println("testAddUser() ADDED: " + user);

        // cleanup
        manager.deleteUser(user);
    }

    @Test
    public void testGetAllPersons() throws Exception{
        List<Person> persons = manager.getAllPersons();

        System.out.println("testGetAllPersons() RESULTS: " + persons.size());
        for (Person person : persons) {
            System.out.println("  " + person);
        }
    }

    @Test
    public void testCountAllPersons() throws Exception{
        long count = manager.countAllPersons();
        System.out.println("testCountAllPersons(): " + count);
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

        person = manager.getPersonById(-1);
        System.out.println("testAddPerson() ADDED: " + person);

        // delete the person
        manager.deletePerson(person);
    }

    @Test
    public void testIsPersonExists() throws MiddlewareQueryException {
        String firstName = "PATERNO";
        String lastName = "BORLAGDAN";
        System.out.println("testIsPersonExists(firstName=" + firstName + ", lastName=" + lastName + "): "
                + manager.isPersonExists(firstName, lastName));

        firstName = "PATTY";
        lastName = "Borly".toUpperCase();
        System.out.println("testIsPersonExists(firstName=" + firstName + ", lastName=" + lastName + "): "
                + manager.isPersonExists(firstName, lastName));
    }

    @Test
    public void testIsUsernameExists() throws MiddlewareQueryException {
        String userName = "GMCLAREN";
        System.out.println("testIsUsernameExists(" + userName + "): " + manager.isUsernameExists(userName));
        userName = "GUESTret";
        System.out.println("testIsUsernameExists(" + userName + "): " + manager.isUsernameExists(userName));
    }

    @Test
    public void testGetAllInstallationRecords() throws Exception {
        List<Installation> results = manager.getAllInstallationRecords(0, 5, Database.CENTRAL);
        System.out.println("testGetAllInstallationRecords() RESULTS: ");
        for (Installation holder : results) {
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testGetInstallationRecordById() throws Exception {
        Long id = Long.valueOf(1);
        System.out.println("testGetInstallationRecordById(" + id + ")" + manager.getInstallationRecordById(id));
    }

    @Test
    public void testGetInstallationRecordsByAdminId() throws Exception {
        Long id = Long.valueOf(1);
        List<Installation> results = manager.getInstallationRecordsByAdminId(id);
        System.out.println("testGetInstallationRecordsByAdminId(" + id + ") RESULTS: ");
        for (Installation holder : results) {
            System.out.println("  " + holder);
        }
    }

    @Test
    public void testGetLatestInstallationRecord() throws Exception {
        Installation result = manager.getLatestInstallationRecord(Database.CENTRAL);
        System.out.println("testGetLatestInstallationRecord() :" + result);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        factory.close();
    }
}
