package Exceptions;

public class PasswordDoesNotMeetRequirementsException extends MyException {
    public PasswordDoesNotMeetRequirementsException(String msg) {
        super(msg);
    }

    public PasswordDoesNotMeetRequirementsException() {
        super("Password does not meet requirements");
    }

}
