package hu.elte.szakdolgozat.db;

import hu.elte.szakdolgozat.services.DuplicateException;
import hu.elte.szakdolgozat.services.InfrastructureException;
import java.sql.ResultSet;

public interface Mysql {

    ResultSet query(String sql) throws InfrastructureException;

    void insertRow(String sql) throws InfrastructureException, DuplicateException;

    void deleteRow(String sql) throws InfrastructureException;

    void close() throws InfrastructureException;
}
