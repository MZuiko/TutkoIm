package hu.elte.szakdolgozat.db;

import hu.elte.szakdolgozat.services.DuplicateException;
import hu.elte.szakdolgozat.services.InfrastructureException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MysqlService implements Mysql {

    private static MysqlService INSTANCE;
    private final String DB_URL;
    private final String USER;
    private final String PASSWORD;
    private final Integer DUPLICATE_ERROR_CODE;
    private Connection connection;

    private MysqlService() {
        this.DB_URL = "jdbc:mysql://localhost:3306/tutkoim";
        this.USER = "admin";
        this.PASSWORD = "asd";
        this.DUPLICATE_ERROR_CODE = 1062;
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
    public ResultSet query(PreparedStatement stmt) throws InfrastructureException {
        try {
            return stmt.executeQuery();
        } catch (SQLException se) {
            throw new InfrastructureException("unable to execute query", se);
        }
    }

    @Override
    public void insertRow(PreparedStatement stmt) throws InfrastructureException, DuplicateException {
        try {
            stmt.executeUpdate();
        } catch (SQLException se) {
            if (se.getErrorCode() == DUPLICATE_ERROR_CODE) {
                throw new DuplicateException("unable to insert", se);
            } else {
                throw new InfrastructureException("unable to insert", se);
            }
        }
    }

    @Override
    public void updateRow(PreparedStatement stmt) throws InfrastructureException {
        try {
            stmt.executeUpdate();
        } catch (SQLException se) {
            throw new InfrastructureException("unable to update", se);
        }
    }

    @Override
    public void deleteRow(PreparedStatement stmt) throws InfrastructureException {
        try {
            stmt.executeUpdate();
        } catch (SQLException se) {
            throw new InfrastructureException("unable to delete", se);
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
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
