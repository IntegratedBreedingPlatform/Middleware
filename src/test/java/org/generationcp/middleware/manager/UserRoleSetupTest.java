package org.generationcp.middleware.manager;

import java.util.ArrayList;
import java.util.List;

import org.generationcp.middleware.DataManagerIntegrationTest;
import org.generationcp.middleware.exceptions.MiddlewareQueryException;
import org.generationcp.middleware.hibernate.HibernateSessionPerThreadProvider;
import org.generationcp.middleware.hibernate.HibernateSessionProvider;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;
import org.generationcp.middleware.pojos.workbench.UserRole;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Creates users with different roles. Temporary while the new user creation screens are under development.
 *
 */
public class UserRoleSetupTest extends DataManagerIntegrationTest {

	private static WorkbenchDataManagerImpl manager;
    
    @BeforeClass
    public static void setUp() throws MiddlewareQueryException  {
        HibernateSessionProvider sessionProvider = new HibernateSessionPerThreadProvider(workbenchSessionUtil.getSessionFactory());
        manager = new WorkbenchDataManagerImpl(sessionProvider);   
    }
    
    @Test
    public void testAddUsersWithRoles() throws MiddlewareQueryException {
    	//Admin
    	Person adminPerson = new Person();
        adminPerson.setFirstName("Naymesh");
        adminPerson.setMiddleName("-");
        adminPerson.setLastName("Mistry");
        adminPerson.setEmail("naymesh@leafnode.io");
        adminPerson.setInstituteId(0);
        adminPerson.setPositionName("-");
        adminPerson.setTitle("-");
        adminPerson.setExtension("-");
        adminPerson.setFax("-");
        adminPerson.setNotes("-");
        adminPerson.setContact("-");
        adminPerson.setLanguage(0);
        adminPerson.setPhone("-");        
        Integer adminPersonId = manager.addPerson(adminPerson);

        User adminUser = new User();
        adminUser.setName("admin");
        adminUser.setPassword("b");
        adminUser.setPersonid(adminPersonId);
        adminUser.setInstalid(0);
        adminUser.setStatus(0);
        adminUser.setAccess(0);
        adminUser.setType(0);
        adminUser.setAdate(20140101);
        adminUser.setCdate(20140101);
        
        List<UserRole> adminRoles = new ArrayList<UserRole>();
        adminRoles.add(new UserRole(adminUser, "ADMIN"));
        adminUser.setRoles(adminRoles);        
        manager.addUser(adminUser);
        
        //Breeder        
        Person breederPerson = new Person();
        breederPerson.setFirstName("Rebecca");
        breederPerson.setMiddleName("-");
        breederPerson.setLastName("Berrigan");
        breederPerson.setEmail("rebecca@leafnode.io");
        breederPerson.setInstituteId(0);
        breederPerson.setPositionName("-");
        breederPerson.setTitle("-");
        breederPerson.setExtension("-");
        breederPerson.setFax("-");
        breederPerson.setNotes("-");
        breederPerson.setContact("-");
        breederPerson.setLanguage(0);
        breederPerson.setPhone("-");
        Integer breederPersonId = manager.addPerson(breederPerson);

        User breederUser = new User();
        breederUser.setName("breeder");
        breederUser.setPassword("b");
        breederUser.setPersonid(breederPersonId);
        breederUser.setInstalid(0);
        breederUser.setStatus(0);
        breederUser.setAccess(0);
        breederUser.setType(0);
        breederUser.setAdate(20140101);
        breederUser.setCdate(20140101);
        
        List<UserRole> breederRoles = new ArrayList<UserRole>();
        breederRoles.add(new UserRole(breederUser, "BREEDER"));
        breederUser.setRoles(breederRoles);        
        manager.addUser(breederUser);
        
        // Technician
        Person technicianPerson = new Person();
        technicianPerson.setFirstName("Lisa");
        technicianPerson.setMiddleName("-");
        technicianPerson.setLastName("Quayle");
        technicianPerson.setEmail("lisa@leafnode.io");
        technicianPerson.setInstituteId(0);
        technicianPerson.setPositionName("-");
        technicianPerson.setTitle("-");
        technicianPerson.setExtension("-");
        technicianPerson.setFax("-");
        technicianPerson.setNotes("-");
        technicianPerson.setContact("-");
        technicianPerson.setLanguage(0);
        technicianPerson.setPhone("-");
        Integer technicianPersonId = manager.addPerson(technicianPerson);

        User technicianUser = new User();
        technicianUser.setName("technician");
        technicianUser.setPassword("b");
        technicianUser.setPersonid(technicianPersonId);
        technicianUser.setInstalid(0);
        technicianUser.setStatus(0);
        technicianUser.setAccess(0);
        technicianUser.setType(0);
        technicianUser.setAdate(20140101);
        technicianUser.setCdate(20140101);
        
        List<UserRole> technicianRoles = new ArrayList<UserRole>();
        technicianRoles.add(new UserRole(technicianUser, "TECHNICIAN"));
        technicianUser.setRoles(technicianRoles);        
        manager.addUser(technicianUser);
    }

}
