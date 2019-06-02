import book.SSLClient;
import book.SSLServer;
import util.PropertiesReader;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PropertiesReader reader = new PropertiesReader();
		if(args[0].equalsIgnoreCase("server")) {
			new SSLServer();
		}
		else if(args[0].equalsIgnoreCase("client")) {
			new SSLClient();
		}
	}	
}
