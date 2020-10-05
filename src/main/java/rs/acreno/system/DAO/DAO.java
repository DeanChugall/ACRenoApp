package rs.acreno.system.DAO;

import rs.acreno.system.exeption.AcrenoException;

import java.sql.Connection;
import java.sql.SQLException;

public interface DAO {

    //void setup() throws AcrenoException, SQLException;

    Connection connect() throws AcrenoException, SQLException;

    void close() throws AcrenoException, SQLException;
}
