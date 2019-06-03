package Exceptions;

public class AuthenticationErrorException extends MyException {
    public AuthenticationErrorException(String msg) {
        super(msg);
    }

    public AuthenticationErrorException() {
        super("User not authenticated");
    }

}
