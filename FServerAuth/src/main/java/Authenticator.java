import Exceptions.*;
import Resources.AccessOperation;
import Resources.Account;
import Resources.Token;
import Utils.CryptoUtil;
import Utils.TokenUtil;

import java.io.IOException;
import java.util.Date;


public class Authenticator {

    public final static long EXPIRE_TIME = 60 * 60 * 3; //3 Horas

    public static synchronized void create_account(String username, String email, String name, String pwd1, String pwd2) throws PasswordMismatchException, UsernameInUseException, PasswordTooWeakException, PasswordDoesNotMeetRequirementsException, UsernameDoesNotMeetRequirementsException, IOException {
        if (!pwd1.equals(pwd2)) throw new PasswordMismatchException();
        if (Storage.getAccount(name.toLowerCase()) != null) throw new UsernameInUseException();

        runUsernameCheck(name);
        runTest(pwd1);

        Account acc = new Account(username.toLowerCase(), email.toLowerCase(), name.toLowerCase(), pwd1);
        Storage.addAccount(acc);
    }

    private static void runUsernameCheck(String username) throws UsernameDoesNotMeetRequirementsException {
        if (username.equals(""))
            throw new UsernameDoesNotMeetRequirementsException("Username does not meet requirements (Username can't be empty)");
        if (username.length() > 128)
            throw new UsernameDoesNotMeetRequirementsException("Username does not meet requirements (Username can't be over 128 characters)");
    }

    private static void runTest(String pwd1) throws PasswordTooWeakException, PasswordDoesNotMeetRequirementsException {
        if (pwd1.length() > 50)
            throw new PasswordDoesNotMeetRequirementsException("Password max length is 50 characters");

    }

    public static synchronized void delete_account(String name) throws UndefinedAccountException {
        if (Storage.removeAccount(name.toLowerCase()) == 0)
            throw new UndefinedAccountException("No account was found");
    }

    public static synchronized void change_pwd(String name, String pwd1, String pwd2) throws UndefinedAccountException, PasswordMismatchException, PasswordTooWeakException, PasswordDoesNotMeetRequirementsException {
        if (!pwd1.equals(pwd2)) throw new PasswordMismatchException();

        Account acc = Storage.getAccount(name.toLowerCase());
        if (acc == null) throw new UndefinedAccountException();

        runTest(pwd1);

        acc.setPassword(pwd1);
        Storage.updateAccount(acc);
    }

    public static Account login(String name, String pwd) throws AuthenticationErrorException, UndefinedAccountException {
        Account acc = get_account(name.toLowerCase());

        if (CryptoUtil.doesPasswordMatch(pwd, acc.getPassword())) {
            acc.setLoggedIn(true);
            return acc;
        } else {
            throw new AuthenticationErrorException("Invalid password");
        }
    }

    public static void logout(Account acc) throws UndefinedAccountException {
        acc.setLoggedIn(false);
    }

    public static Account get_account(String name) throws UndefinedAccountException {
        Account acc = Storage.getAccount(name.toLowerCase());
        if (acc == null) throw new UndefinedAccountException();
        return acc;
    }

    public static boolean checkPassword(String username, String password) throws UndefinedAccountException {
        Account acc = get_account(username);

        return CryptoUtil.doesPasswordMatch(password, acc.getPassword());
    }

    public static boolean checkPasswordHash(String username, String pwDstr) throws UndefinedAccountException {
        Account acc = get_account(username);

        return acc.getPassword().equals(pwDstr);
    }

    public static String generateToken(String username) throws Exception {
        Date now = new Date();
        Date end = new Date(now.getTime() + 1000 * EXPIRE_TIME);
        return TokenUtil.genToken(new Token("Fserver", AccessOperation.READ, end.getTime()));
    }
}
