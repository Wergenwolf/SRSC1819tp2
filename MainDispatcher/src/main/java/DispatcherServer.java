import book.SSLServer;

import java.net.Socket;

public class DispatcherServer {

    public static void main(String[] args) {
        SSLServer server = new SSLServer("localhost",
                8123,
                new String[]{"MainDispatcher/server/servertls.conf", "MainDispatcher/server/server.properties"},
                "123456".toCharArray());
        System.out.println("Dispatcher accepting...");

        Socket s = null;
        while (true) {
            s = server.accept();
            System.out.println("New connection accepted");
            new DispatcherThread(s).start();
        }
    }
}
