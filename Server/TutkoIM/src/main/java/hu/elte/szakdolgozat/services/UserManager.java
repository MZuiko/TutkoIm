package hu.elte.szakdolgozat.services;

import hu.elte.szakdolgozat.model.User;
import java.util.List;

public interface UserManager {

    void createUser(User user) throws ValidationException, InfrastructureException, DuplicateException;

    List<User> getUsers() throws InfrastructureException;

    String getUser(String userName) throws InfrastructureException;

    void deleteUser(Integer id) throws InfrastructureException;

}
