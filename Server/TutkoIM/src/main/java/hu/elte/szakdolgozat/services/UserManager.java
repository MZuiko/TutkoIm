package hu.elte.szakdolgozat.services;

import hu.elte.szakdolgozat.model.User;
import java.util.List;

public interface UserManager {

    void createUser(User user) throws ValidationException, InfrastructureException;

    User getUser(String userName) throws InfrastructureException;

    void deleteUser(String userName) throws InfrastructureException;

    void logIn(User user) throws InfrastructureException, ValidationException;

    void logOut(String userName) throws InfrastructureException;

    void addFriend(String user1, String user2) throws InfrastructureException;

    List<User> getFriends(String userName) throws InfrastructureException;

    void acceptFriend(String user1, String user2) throws InfrastructureException;

    public List<User> getAccept(String userName) throws InfrastructureException;

    void removeFriend(String user1, String user2) throws InfrastructureException;
}
