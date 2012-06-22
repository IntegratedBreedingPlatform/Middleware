package org.generationcp.middleware.manager.test;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.manager.DatabaseConnectionParameters;
import org.generationcp.middleware.manager.ManagerFactory;
import org.generationcp.middleware.manager.api.PersonDataManager;
import org.generationcp.middleware.pojos.Person;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPersonDataManagerImpl {
    private final static Logger log = LoggerFactory.getLogger(TestPersonDataManagerImpl.class);
    
    private static ManagerFactory factory;
    private static PersonDataManager manager;
    
    @BeforeClass
    public static void setUp() throws Exception {
        DatabaseConnectionParameters local = new DatabaseConnectionParameters("testDatabaseConfig.properties", "local");
        DatabaseConnectionParameters central = new DatabaseConnectionParameters("testDatabaseConfig.properties", "central");
        factory = new ManagerFactory(local, central);
        manager = factory.getPersonDataManager();
    }
    
    @Test
    public void testGetAllPersons() {
        List<Person> persons = manager.getAllPersons();
        
        log.debug("Retrieved {} persons:", persons.size());
        for (Person person : persons) {
            log.debug("{} {} {}", new Object[] { person.getFirstName(), person.getMiddleName(), person.getLastName() });
        }
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
        
        // delete the person
        manager.deletePerson(person);
    }
}
