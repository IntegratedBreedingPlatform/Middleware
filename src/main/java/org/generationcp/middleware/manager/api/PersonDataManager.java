package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.Person;

public interface PersonDataManager {
    
    List<Person> getAllPersons();
    
    void addPerson(Person person) throws QueryException;
    
    void deletePerson(Person person) throws QueryException;
}
