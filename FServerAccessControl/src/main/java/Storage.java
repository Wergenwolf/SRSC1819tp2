import Resources.Account;
import Utils.dbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Storage {

    static Account getAccount(String username) {
        try {
            Connection connection = dbConnection.getConnection();

            //Execute query
            PreparedStatement ps = connection.prepareStatement("select * from Permissions where Username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            //Get user
            if (rs.next()) {
                Account acc = new Account(rs.getString("Username"), rs.getBoolean("Read"), rs.getBoolean("Write"));

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
    static void addAccount(Account acc) {
        try {
            Connection connection = dbConnection.getConnection();
            //Execute query
            PreparedStatement ps = connection.prepareStatement("INSERT INTO Permissions VALUES(?,?,?)");

            ps.setString(1, acc.getUsername());
            ps.setBoolean(2, acc.isRead());
            ps.setBoolean(3, acc.isWrite());
            int i = ps.executeUpdate();
            ps.close();
            connection.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    static void updateAccount(Account acc) {
        String sql = "UPDATE Permissions SET Read = ?,Write = ? WHERE Username = ?";
        try {
            Connection connection = dbConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            // set the corresponding param
            ps.setBoolean(1, acc.isRead());
            ps.setBoolean(2, acc.isWrite());
            ps.setString(3, acc.getUsername());
            // execute the update statement
            ps.executeUpdate();
            ps.close();
            connection.close();

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    static int removeAccount(String user) {
        String sql = "DELETE FROM Permissions WHERE Username = ?";
        int nAffectedRows = 0;
        try {
            Connection connection = dbConnection.getConnection();
            assert connection != null;
            PreparedStatement ps = connection.prepareStatement(sql);
            // set the corresponding param
            ps.setString(1, user);
            // execute the delete statement
            int nDel = ps.executeUpdate();
            if (nDel > 0) {
                System.out.println("Deleted user: " + user);
            }
            ps.close();
            connection.close();
            return nDel;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }

    static int removeAccount(Account acc) {
        return removeAccount(acc.getUsername());
    }

}
