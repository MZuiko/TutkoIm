package hu.elte.szakdolgozat.services;

import hu.elte.szakdolgozat.db.MysqlService;
import hu.elte.szakdolgozat.model.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserManagerService implements UserManager {

    private static UserManagerService INSTANCE;
    private final MysqlService mysqlService = MysqlService.getInstance();

    private UserManagerService() {
    }

    public static synchronized UserManagerService getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new UserManagerService();
        }
        return INSTANCE;
    }

    @Override
    public void createUser(User user) throws ValidationException, InfrastructureException {
        if (null == user.getUserName() || null == user.getPassword()
                || null == user.getFirstName() || null == user.getLastName()
                || "".equals(user.getUserName()) || "".equals(user.getPassword())
                || "".equals(user.getFirstName()) || "".equals(user.getLastName())) {
            throw new ValidationException("username, password and name are required");
        } else if (3 > user.getUserName().length()) {
            throw new ValidationException("username is too short");
        } else if (3 > user.getPassword().length()) {
            throw new ValidationException("password is too short");
        } else if (2 > user.getFirstName().length()) {
            throw new ValidationException("first name is too short");
        } else if (2 > user.getLastName().length()) {
            throw new ValidationException("last name is too short");
        } else if (25 < user.getUserName().length()) {
            throw new ValidationException("username is too long");
        } else if (25 < user.getPassword().length()) {
            throw new ValidationException("password is too long");
        } else if (25 < user.getFirstName().length()) {
            throw new ValidationException("first name is too long");
        } else if (25 < user.getLastName().length()) {
            throw new ValidationException("last name is too long");
        } else {
            try {
                String generatedPassword = getHash(user.getPassword());
                String insert = "insert into users (username, password, firstname,"
                        + "lastname) values (?,?,?,?);";
                PreparedStatement stmt;
                stmt = mysqlService.getConnection().prepareStatement(insert);
                stmt.setString(1, user.getUserName());
                stmt.setString(2, generatedPassword);
                stmt.setString(3, user.getFirstName());
                stmt.setString(4, user.getLastName());
                mysqlService.insertRow(stmt);
            } catch (NoSuchAlgorithmException nsae) {
                throw new RuntimeException("password cannot be created, "
                        + "please select an other one", nsae);
            } catch (DuplicateException de) {
                throw new ValidationException("username already exists", de);
            } catch (SQLException se) {
                throw new InfrastructureException(se.getMessage(), se);
            }
        }
    }

    @Override
    public User getUser(String userName) throws InfrastructureException {
        User user = null;
        try {
            String select = "select * from users where username = ?;";
            PreparedStatement stmt;
            stmt = mysqlService.getConnection().prepareStatement(select);
            stmt.setString(1, userName);
            ResultSet rs = mysqlService.query(stmt);
            while (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUserName(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
                user.setStatus(rs.getString("status"));
                user.setIp(rs.getString("ip"));
                user.setPort(rs.getInt("port"));
            }
            return user;
        } catch (SQLException se) {
            throw new InfrastructureException(se.getMessage(), se);
        }
    }

    @Override
    public void deleteUser(String userName) throws InfrastructureException {
        try {
            String delete = "delete from friends where user1 = ? or user2 = ?;";
            PreparedStatement stmt;
            stmt = mysqlService.getConnection().prepareStatement(delete);
            stmt.setString(1, userName);
            stmt.setString(2, userName);
            mysqlService.deleteRow(stmt);
            delete = "delete from users where username = ?;";
            stmt = mysqlService.getConnection().prepareStatement(delete);
            stmt.setString(1, userName);
            mysqlService.deleteRow(stmt);
        } catch (SQLException se) {
            throw new InfrastructureException(se.getMessage(), se);
        }
    }

    @Override
    public void logIn(User user) throws InfrastructureException, ValidationException {
        try {
            User u = getUser(user.getUserName());
            if (null == u || !getHash(user.getPassword()).equals(u.getPassword())) {
                throw new ValidationException("wrong username/password");
            } else {
                String update = "update users set status = 'online', ip = ?, "
                        + "port = ? where username = ?;";
                PreparedStatement stmt;
                stmt = mysqlService.getConnection().prepareStatement(update);
                stmt.setString(1, user.getIp());
                stmt.setInt(2, user.getPort());
                stmt.setString(3, user.getUserName());
                mysqlService.updateRow(stmt);
            }
        } catch (NoSuchAlgorithmException | SQLException ex) {
            throw new InfrastructureException(ex.getMessage(), ex);
        }
    }

    @Override
    public void logOut(String userName) throws InfrastructureException {
        try {
            String update = "update users set status = 'offline' where username = ?;";
            PreparedStatement stmt;
            stmt = mysqlService.getConnection().prepareStatement(update);
            stmt.setString(1, userName);
            mysqlService.updateRow(stmt);
        } catch (SQLException se) {
            throw new InfrastructureException(se.getMessage(), se);
        }
    }

    @Override
    public void addFriend(String user1, String user2) throws InfrastructureException {
        try {
            String insert = "insert into friends (user1,user2) values(?,?);";
            PreparedStatement stmt;
            stmt = mysqlService.getConnection().prepareStatement(insert);
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            mysqlService.updateRow(stmt);
        } catch (SQLException se) {
            throw new InfrastructureException(se.getMessage(), se);
        }
    }

    @Override
    public List<User> getFriends(String userName) throws InfrastructureException {
        List<User> userList = new ArrayList();
        try {
            String select = "select username, firstname, lastname, status "
                    + "from users where username = (select user1 from friends "
                    + "where user2 = ? and accepted = 'yes') UNION "
                    + "select username, firstname, lastname, status "
                    + "from users where username = (select user2 from friends "
                    + "where user1 = ? and accepted = 'yes');";
            PreparedStatement stmt;
            stmt = mysqlService.getConnection().prepareStatement(select);
            stmt.setString(1, userName);
            stmt.setString(2, userName);
            ResultSet rs = mysqlService.query(stmt);
            while (rs.next()) {
                User user = new User();
                user.setUserName(rs.getString("username"));
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
                user.setStatus(rs.getString("status"));
                userList.add(user);
            }
            return userList;
        } catch (SQLException se) {
            throw new InfrastructureException(se.getMessage(), se);
        }
    }

    @Override
    public void acceptFriend(String user1, String user2) throws InfrastructureException {
        try {
            String update = "update friends set accepted = 'yes' where user1 = ?"
                    + "and user2 = ?;";
            PreparedStatement stmt;
            stmt = mysqlService.getConnection().prepareStatement(update);
            stmt.setString(1, user2);
            stmt.setString(2, user1);
            mysqlService.updateRow(stmt);
        } catch (SQLException se) {
            throw new InfrastructureException(se.getMessage(), se);
        }
    }

    @Override
    public List<User> getAccept(String userName) throws InfrastructureException {
        List<User> userList = new ArrayList();
        try {
            String select = "select username, firstname, lastname "
                    + "from users where username = (select user1 from friends "
                    + "where user2 = ? and accepted = 'no');";
            PreparedStatement stmt;
            stmt = mysqlService.getConnection().prepareStatement(select);
            stmt.setString(1, userName);
            ResultSet rs = mysqlService.query(stmt);
            while (rs.next()) {
                User user = new User();
                user.setUserName(rs.getString("username"));
                user.setFirstName(rs.getString("firstname"));
                user.setLastName(rs.getString("lastname"));
                userList.add(user);
            }
            return userList;
        } catch (SQLException se) {
            throw new InfrastructureException(se.getMessage(), se);
        }
    }

    @Override
    public void removeFriend(String user1, String user2) throws InfrastructureException {
        try {
            String delete = "delete from friends where user1=? and user2=?"
                    + " or  user1=? and user2=?;";
            PreparedStatement stmt;
            stmt = mysqlService.getConnection().prepareStatement(delete);
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            stmt.setString(3, user2);
            stmt.setString(4, user1);
            mysqlService.updateRow(stmt);
        } catch (SQLException se) {
            throw new InfrastructureException(se.getMessage(), se);
        }
    }

    private String getHash(String password) throws NoSuchAlgorithmException {
        String generatedPassword;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        generatedPassword = sb.toString();
        return generatedPassword;
    }
}
