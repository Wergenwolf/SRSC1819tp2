package Exceptions;

public class UndefinedAccountException extends MyException {
    public UndefinedAccountException(String msg) {
        super(msg);
    }

    public UndefinedAccountException() {
        super("No account was found");
    }
}
