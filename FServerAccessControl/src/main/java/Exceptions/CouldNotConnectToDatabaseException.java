package Exceptions;

public class CouldNotConnectToDatabaseException extends MyException {
    public CouldNotConnectToDatabaseException(String msg) {
        super(msg);
    }

    public CouldNotConnectToDatabaseException() {
        super("Could not connect to the database");
    }

}
