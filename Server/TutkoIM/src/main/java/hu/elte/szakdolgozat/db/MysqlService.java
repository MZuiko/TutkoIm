package hu.elte.szakdolgozat.db;

import hu.elte.szakdolgozat.services.DuplicateException;
import hu.elte.szakdolgozat.services.InfrastructureException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlService implements Mysql {

    private static MysqlService INSTANCE;
    private final String DB_URL;
    private final String USER;
    private final String PASSWORD;
    private final Integer DUPLICATE_ERROR_CODE = 1062;
    private static Connection connection;

    private MysqlService() {
        DB_URL = "jdbc:mysql://localhost:3306/personal";
        USER = "jano";
        PASSWORD = "asd";
        try {
            driverRegister();
            connection = connect();
        } catch (InfrastructureException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static synchronized MysqlService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MysqlService();
        }
        return INSTANCE;
    }

    private void driverRegister() throws InfrastructureException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException cnfe) {
            throw new InfrastructureException("unable to connect to database", cnfe);
        }
    }

    private Connection connect() throws InfrastructureException {
        try {
            Connection conn;
            String url = DB_URL + "?characterEncoding=UTF-8&useSSL=false";
            conn = DriverManager.getConnection(url, USER, PASSWORD);
            return conn;
        } catch (SQLException se) {
            throw new InfrastructureException("unable to connect to database", se);
        }
    }

    @Override
    public ResultSet query(String sql) throws InfrastructureException {
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException se) {
            throw new InfrastructureException("unable to execute query", se);
        }
    }

    @Override
    public void insertRow(String sql) throws InfrastructureException, DuplicateException {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException se) {
            if (se.getErrorCode() == DUPLICATE_ERROR_CODE) {
                throw new DuplicateException("username already exists", se);
            } else {
                throw new InfrastructureException("unable to insert", se);
            }
        }
    }

    @Override
    public void deleteRow(String sql) throws InfrastructureException {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException se) {
            throw new InfrastructureException("unable to delete", se);
        }
    }

    @Override
    public void close() throws InfrastructureException {
        try {
            connection.close();
        } catch (SQLException se) {
            throw new InfrastructureException("unable to close", se);
        }
    }
}
