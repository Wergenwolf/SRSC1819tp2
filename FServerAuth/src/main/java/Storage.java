import Resources.Account;
import Utils.dbConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Storage {

    public static boolean checkUser(String user) {
        return getAccount(user) != null;
    }

    static Account getAccount(String username) {
        try {
            Connection connection = dbConnection.getConnection();

            //Execute query
            PreparedStatement ps = connection.prepareStatement("select * from Accounts where Username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            //Get user
            if (rs.next()) {
                Account acc = new Account(rs.getString("Username"), rs.getString("Email"), rs.getString("Name"), rs.getString("Password"), "false", String.valueOf(rs.getBoolean("Locked")), rs.getString("Salt"));

                ps.close();
                return acc;
            }
            connection.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * @param acc The account to add to persistent storage
     */
    static void addAccount(Account acc) throws IOException {
        try {
            Connection connection = dbConnection.getConnection();
            //Execute query
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Accounts VALUES(?,?,?,?,?,?)");

            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getEmail());
            ps.setString(3, acc.getName());
            ps.setString(4, acc.getPassword());
            ps.setBoolean(5, acc.isLocked());
            ps.setString(6, acc.getSalt());

            int i = ps.executeUpdate();
            ps.close();
            connection.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    static void updateAccount(Account acc) {
        String sql = "UPDATE Accounts SET Password = ?,Locked = ?,Salt = ? WHERE Username = ?";
        try {
            Connection connection = dbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            // set the corresponding param
            ps.setString(1, acc.getPassword());
            ps.setBoolean(2, acc.isLocked());
            ps.setString(3, acc.getSalt());
            ps.setString(4, acc.getUsername());
            // execute the update statement
            ps.executeUpdate();
            ps.close();
            connection.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    static int removeAccount(String user) {
        String sql = "DELETE FROM Accounts WHERE Username = ?";
        int nAffectedRows = 0;
        try {
            Connection connection = dbConnection.getConnection();
            assert connection != null;
            PreparedStatement ps = connection.prepareStatement(sql);
            // set the corresponding param
            ps.setString(1, user);
            // execute the delete statement
            if (ps.executeUpdate() > 0)
                System.out.println("Deleted user: " + user);
            ps.close();
            connection.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }

    static int removeAccount(Account acc) {
        return removeAccount(acc.getUsername());
    }


    private static Date getDateTime(String timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        return date;
    }
}
