package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Person;
import org.generationcp.middleware.pojos.User;

public interface UserDataManager {
    
    /**
     * Returns all Persons
     * 
     * @return gets all Persons
     */   	
    List<User> getAllUsers();
    
    /**
     * Returns number of all Users
     * 
     * @return the number of all Users
     */   
    int countAllUsers();
    
    void addUser(User user) throws QueryException;
    
    User getUserById(int id);
    
    void deleteUser(User user) throws QueryException;
    
    /**
     * Returns all Persons
     * 
     * @return gets all Persons
     */   
    List<Person> getAllPersons();
    
    /**
     * Returns number of all Persons
     * 
     * @return the number of all Persons
     */   
    int countAllPersons();
    
    
    void addPerson(Person person) throws QueryException;
    
    Person getPersonById(int id);
    
    void deletePerson(Person person) throws QueryException; 
    
    boolean isValidUserLogin(String username, String password) throws QueryException;
}
