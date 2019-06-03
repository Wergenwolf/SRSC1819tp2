import book.SSLServer;

import java.net.Socket;

public class mainStorage {
    public static void main(String[] args) {


        SSLServer server = new SSLServer("localhost",
                8122,
                new String[]{"FServerStorage/server/servertls.conf", "FServerStorage/server/server.properties"},
                "123456".toCharArray());
        System.out.println("Storage accepting...");

        Socket s = null;
        while (true) {
            s = server.accept();
            System.out.println("New connection accepted");
            new StorageThread(s).start();
        }


    }
}
