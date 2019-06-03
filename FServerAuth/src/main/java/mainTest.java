import Exceptions.*;

import java.io.IOException;

public class mainTest {

    public static void main(String[] args) {
        try {
            Authenticator.create_account("asd", "asd", "asd", "asd", "asd");
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


    }
}
