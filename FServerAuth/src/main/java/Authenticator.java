import Exceptions.*;
import Resources.Account;
import Utils.CryptoUtil;

import java.io.IOException;


public class Authenticator {

    public static void create_account(String username, String email, String name, String pwd1, String pwd2) throws PasswordMismatchException, UsernameInUseException, PasswordTooWeakException, PasswordDoesNotMeetRequirementsException, UsernameDoesNotMeetRequirementsException, IOException {
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

    public static void delete_account(String name) throws UndefinedAccountException {
        if (Storage.removeAccount(name.toLowerCase()) == 0)
            throw new UndefinedAccountException("No account was found");
    }

    public static void change_pwd(String name, String pwd1, String pwd2) throws UndefinedAccountException, PasswordMismatchException, PasswordTooWeakException, PasswordDoesNotMeetRequirementsException {
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

//    public static Account login(HttpServletRequest req, HttpServletResponse resp) throws AuthenticationErrorException {
//        Account acc;
//        try {
//            acc = new Account(req.getSession().getAttribute("USER").toString(), req.getSession().getAttribute("PASS").toString(), req.getSession().getAttribute("ROLE").toString(), req.getSession().getAttribute("LOCKED").toString(), req.getSession().getAttribute("LOGGED_IN").toString());
//            Account acc2 = get_account(acc.getUsername());
//            if (!acc.equals(acc2)) throw new AuthenticationErrorException();
//        } catch (Exception e) {
//            throw new AuthenticationErrorException();
//        }
//        acc.getCapabilities().addAll((ArrayList<String>) req.getSession().getAttribute("CAPABILITIES"));
//        return acc;
//    }

    public static boolean checkPassword(String username, String password) throws UndefinedAccountException {
        Account acc = get_account(username);

        return CryptoUtil.doesPasswordMatch(password, acc.getPassword());
    }
}
