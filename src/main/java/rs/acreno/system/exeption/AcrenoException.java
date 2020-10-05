package rs.acreno.system.exeption;

import java.sql.SQLException;

public class AcrenoException extends Exception{

    public AcrenoException(String message) {
        super(message);
    }

    public AcrenoException(String message, Throwable cause) {
        super(message, cause);
    }


}
