package Resources;

import Utils.CryptoUtil;

import java.util.ArrayList;
import java.util.List;

import static Utils.Constants.SALT_STRENGTH;

public class Account {
    private final String username;
    private final String email;
    private final String name;
    private String password;
    private boolean loggedIn;
    private boolean locked;
    private String salt;
    private String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Creates an account
     *
     * @param username The user identifier, must be unique
     * @param password The user password in clear text
     */
    public Account(String username, String email, String name, String password) {
        this.username = username;
        this.email = email;
        this.name = name;
        try {
            this.salt = generateSalt();
            this.password = CryptoUtil.bCryptEncrypt(password, this.salt);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
        this.loggedIn = false;
        this.locked = false;
    }

    public Account(String username, String email, String name, String password, String loggedIn, String locked, String salt) {
        this.username = username.toLowerCase();
        this.email = email;
        this.name = name;
        this.password = password;
        this.loggedIn = loggedIn.equals("true");
        this.locked = locked.equals("true");
        this.salt = salt;
    }

    private String generateSalt() {
        return CryptoUtil.genSalt(SALT_STRENGTH);
    }

    /**
     * Checks if an account is locked
     *
     * @return TRUE if locked in FALSE otherwise
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Sets the account locked status
     *
     * @param locked Set TRUE for locked, set FALSE for unlocked
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Checks if an account is logged in
     *
     * @return TRUE if logged in FALSE otherwise
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Sets the account login status
     *
     * @param loggedIn Set TRUE for logged in, set FALSE for logged out
     */
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getCapabilities() {
        List<String> emptyCap = new ArrayList<>();
        return emptyCap;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        try {
            this.salt = generateSalt();
            this.password = CryptoUtil.bCryptEncrypt(password, this.salt);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSalt() {
        return salt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return username.equals(account.username) &&
                email.equals(account.email) &&
                name.equals(account.name);
    }
}
