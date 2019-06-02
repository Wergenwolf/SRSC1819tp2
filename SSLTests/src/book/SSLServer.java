package book;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import util.PropertiesReader;

public class SSLServer {

	SSLServerSocketFactory factory;
	SSLServerSocket socket;
	SSLSocket sslSocket;
	SSLContext sslContext;
	KeyStore ks;
	KeyManagerFactory keyManagerFactory;
	PropertiesReader prop;

	Boolean mutualAuth;
	String trustStorePath;
	String keyStorePath;
	String[] protocols;
	String[] ciphersuites;
	
	String keystore;
	char[] keystorePassword;
	char[] entryPassword;

	public SSLServer() {

		// TODO Testing only
		keystorePassword = new char[] { '1', '2', '3', '4', '5', '6' };

		
		startConfig();
		try {
			doProtocol(sslSocket);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doProtocol(Socket socket) throws Exception {
		System.out.println("[SSLServer] Server created");

		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();

		int ch = 0;
		while ((ch = in.read()) != '!') {
			out.write(ch);
		}
		out.write('!');

		socket.close();

		System.out.println("[SSLServer] Server end");
	}

	private void readProperties() {

		try {
			Properties prop = new Properties();
			String filename = "server/servertls.conf";
			InputStream in = new FileInputStream(filename);
			if(in != null) {
				prop.load(in);
			}
			in.close();
			protocols = prop.getProperty("TLS-PROT-ENF").split("\\s");
			ciphersuites = prop.getProperty("CIPHERSUITES").split("\\s");
			mutualAuth = false;
			if(prop.getProperty("TLS-AUTH").equalsIgnoreCase("mutual"))
				mutualAuth = true;
			
			filename = "server/server.properties";
			in = new FileInputStream(filename);
			prop.load(in);
			keyStorePath = prop.getProperty("keyStorePath");
			trustStorePath = prop.getProperty("trustStorePath");
			System.setProperty("javax.net.ssl.trustStore", trustStorePath);

		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[SSLServer] Failed to set truststore");
		}

	}
	
	private void configureSocket() {
		try {
			
			ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(keyStorePath), keystorePassword);
			keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyManagerFactory.init(ks, keystorePassword);
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

			factory = sslContext.getServerSocketFactory();
			socket = (SSLServerSocket) factory.createServerSocket(8123);
			socket.setNeedClientAuth(mutualAuth);
			socket.setEnabledProtocols(protocols);
			socket.setEnabledCipherSuites(ciphersuites);
			sslSocket = (SSLSocket) socket.accept();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startConfig() {
		readProperties();
		configureSocket();
	}

	public static void main(String[] args) {
		new SSLServer();
	}

}
