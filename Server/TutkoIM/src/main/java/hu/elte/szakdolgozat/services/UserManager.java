package hu.elte.szakdolgozat.services;

import hu.elte.szakdolgozat.model.User;
import java.util.List;

public interface UserManager {

    void createUser(User user) throws ValidationException, InfrastructureException;

    List<User> getUsers() throws InfrastructureException;

    User getUser(String userName) throws InfrastructureException;

    void deleteUser(String userName) throws InfrastructureException;

    void logIn(User user) throws InfrastructureException, ValidationException;

    void logOut(String userName) throws InfrastructureException;

    void addFriend(String myName, String friendName) throws InfrastructureException, DuplicateException;
}
