import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import book.SSLClient;

public class ClientTestMain {

	public static void main(String[] args) throws IOException {

		SSLClient client = new SSLClient("localhost",
				8123,
				"client/client.properties",
				"987654".toCharArray());
		
		BufferedWriter w = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		w.write("Hello from clientMain!",0,"Hello from clientMain!".length());
		w.close();
		client.close();
		
		
	}

}
