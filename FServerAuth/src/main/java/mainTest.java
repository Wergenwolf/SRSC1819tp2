import Exceptions.*;
import Resources.Account;

import java.io.IOException;

public class mainTest {

    public static void main(String[] args) {
        try {
            Authenticator.create_account("userxxx124", "asf@hotmail124", "tomigr124", "qwe", "qwe");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PasswordDoesNotMeetRequirementsException e) {
            e.printStackTrace();
        } catch (PasswordMismatchException e) {
            e.printStackTrace();
        } catch (UsernameDoesNotMeetRequirementsException e) {
            e.printStackTrace();
        } catch (PasswordTooWeakException e) {
            e.printStackTrace();
        } catch (UsernameInUseException e) {
            e.printStackTrace();
        }

        try {
            Account acc = Authenticator.login("userxxx124", "qwe");
            System.out.println(acc.getUsername());
        } catch (AuthenticationErrorException e) {
            e.printStackTrace();
        } catch (UndefinedAccountException e) {
            e.printStackTrace();
        }

    }
}
