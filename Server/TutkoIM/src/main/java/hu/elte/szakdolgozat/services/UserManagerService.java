package hu.elte.szakdolgozat.services;

import hu.elte.szakdolgozat.db.MysqlService;
import hu.elte.szakdolgozat.model.User;
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
        if (INSTANCE == null) {
            INSTANCE = new UserManagerService();
        }
        return INSTANCE;
    }

    @Override
    public void createUser(User user)
            throws ValidationException, InfrastructureException, DuplicateException {
        if (null == user.getUserName() || null == user.getPassword() || null == user.getRealName()) {
            throw new ValidationException("Username, password and realname are required");
        }
        if (3 > user.getUserName().length()) {
            throw new ValidationException("Username is too short");
        }
        if (3 > user.getPassword().length()) {
            throw new ValidationException("Password is too short");
        }
        if (3 > user.getRealName().length()) {
            throw new ValidationException("Realame is too short");
        }
        if (25 < user.getUserName().length()) {
            throw new ValidationException("Username is too long");
        }
        if (25 < user.getPassword().length()) {
            throw new ValidationException("Password is too long");
        }
        if (25 < user.getRealName().length()) {
            throw new ValidationException("Realname is too long");
        }

        String insert = "insert into users (Username, Password, Realname) values ";
        String sql = insert + "('" + user.getUserName() + "', '"
                + user.getPassword() + "', '" + user.getRealName() + "');";
        mysqlService.insertRow(sql);
    }

    @Override
    public List<User> getUsers() throws InfrastructureException {
        List<User> userList = new ArrayList();
        try {
            ResultSet rs = mysqlService.query("select * from users;");
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("Id"));
                user.setUserName(rs.getString("Username"));
                user.setPassword(rs.getString("Password"));
                user.setRealName(rs.getString("Realname"));
                userList.add(user);
            }
            return userList;
        } catch (SQLException se) {
            throw new InfrastructureException(se.getMessage(), se);
        }
    }

    @Override
    public String getUser(String userName) throws InfrastructureException {
        String realName = null;
        try {
            String select = "select Realname from users where Username = ";
            ResultSet rs = mysqlService.query(select + "'" + userName + "'" + ";");
            while (rs.next()) {
                realName = rs.getString("Realname");
            }
            return realName;
        } catch (SQLException se) {
            throw new InfrastructureException(se.getMessage(), se);
        }
    }

    @Override
    public void deleteUser(Integer id) throws InfrastructureException {
        String delete = "delete from users where id = ";
        String sql = delete + id + ";";
        mysqlService.deleteRow(sql);
    }
}
