package Exceptions;

public class UsernameInUseException extends MyException {
    public UsernameInUseException(String msg) {
        super(msg);
    }

    public UsernameInUseException() {
        super("Username already in use");
    }

}
