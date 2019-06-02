package util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

	String[] tlsProtocols;
	String auth;
	String[] ciphersuites;

	public PropertiesReader() {
		readProperties();
	}

	private void readProperties() {
		Properties prop = new Properties();
		String filename = "server/servertls.conf";

		try {
			InputStream in;
			in = new FileInputStream(filename);
			prop.load(in);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		tlsProtocols = prop.getProperty("TLS-PROT-ENF").split("\\s");
		auth = prop.getProperty("TLS-AUTH");
		ciphersuites = prop.getProperty("CIPHERSUITES").split("\\s");
	}

	public String[] getTlsProtocols() {
		return tlsProtocols;
	}

	public String getAuth() {
		return auth;
	}

	public String[] getCiphersuites() {
		return ciphersuites;
	}

	public static void main(String[] args) {
		PropertiesReader reader = new PropertiesReader();
		reader.readProperties();
	}

}
