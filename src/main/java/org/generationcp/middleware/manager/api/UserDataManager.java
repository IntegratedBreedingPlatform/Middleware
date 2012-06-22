package org.generationcp.middleware.manager.api;

import java.util.List;

import org.generationcp.middleware.exceptions.QueryException;
import org.generationcp.middleware.pojos.User;

public interface UserDataManager {
    
    List<User> getAllUsers();
    
    void addUser(User user) throws QueryException;
    
    User getUserById(int id);
    
    void deleteUser(User user) throws QueryException;
    
}
