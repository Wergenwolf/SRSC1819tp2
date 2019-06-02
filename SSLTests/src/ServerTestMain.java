import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import book.SSLServer;

public class ServerTestMain {

	public static void main(String[] args) throws IOException {

		SSLServer server = new SSLServer("localhost",
				8123,
				new String[]{"server/servertls.conf","server/server.properties"},
				"123456".toCharArray());
		Socket s = server.accept();
        BufferedReader r = new BufferedReader(new InputStreamReader(
			     s.getInputStream()));
		String b;
		while( (b = r.readLine() )!= null) {
			System.out.println(b);
		}
		
	}

}
