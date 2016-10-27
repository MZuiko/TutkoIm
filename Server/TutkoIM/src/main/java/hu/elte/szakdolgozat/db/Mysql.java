package hu.elte.szakdolgozat.db;

import hu.elte.szakdolgozat.services.DuplicateException;
import hu.elte.szakdolgozat.services.InfrastructureException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface Mysql {

    ResultSet query(PreparedStatement stmt) throws InfrastructureException;

    void insertRow(PreparedStatement stmt) throws InfrastructureException, DuplicateException;

    void updateRow(PreparedStatement stmt) throws InfrastructureException;

    void deleteRow(PreparedStatement stmt) throws InfrastructureException;

    Connection getConnection();

    void close() throws InfrastructureException;
}
