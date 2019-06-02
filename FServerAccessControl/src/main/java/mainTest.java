import Resources.Permission;

public class mainTest {

    public static void main(String[] args) {
        Permission p = AccessControl.getPermission("ola");
        System.out.println(p);

        //AccessControl.setPermission("olaaa",Permission.NONE);

        p = AccessControl.getPermission("ola");
        System.out.println(p);

        AccessControl.removeAccPermission("olaaa");


    }
}
