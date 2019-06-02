import Resources.Account;
import Resources.Permission;

public class AccessControl {
    public static void setPermission(String username, Permission permission) {
        Account acc = Storage.getAccount(username);
        if (acc == null) {
            acc = new Account(username, false, false);

            boolean read = false, write = false;
            if (permission == Permission.RW || permission == Permission.READ)
                read = true;
            if (permission == Permission.RW || permission == Permission.WRITE)
                write = true;

            acc.setRead(read);
            acc.setWrite(write);

            Storage.addAccount(acc);
        } else {

            boolean read = false, write = false;
            if (permission == Permission.RW || permission == Permission.READ)
                read = true;
            if (permission == Permission.RW || permission == Permission.WRITE)
                write = true;

            acc.setRead(read);
            acc.setWrite(write);

            Storage.updateAccount(acc);
        }


    }

    public static Permission getPermission(String username) {
        Account acc = Storage.getAccount(username);
        if (acc == null)
            return Permission.NONE;
        boolean read = false, write = false;
        if (acc.isRead() && acc.isWrite())
            return Permission.RW;
        else if (acc.isRead())
            return Permission.READ;
        else if (acc.isWrite())
            return Permission.WRITE;
        else
            return Permission.NONE;
    }

    static void removeAccPermission(String username) {
        Storage.removeAccount(username);
    }
}
