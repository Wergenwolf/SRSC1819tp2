package Exceptions;

public class PasswordTooWeakException extends MyException {
    public PasswordTooWeakException(String msg) {
        super(msg);
    }

    public PasswordTooWeakException() {
        super("Password is too weak");
    }
}