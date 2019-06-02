package book;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Properties;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLClient {

	SSLSocketFactory factory;
	SSLSocket socket;
	SSLContext sslContext;
	KeyStore ks;
	KeyManagerFactory keyManagerFactory;
	
	Boolean mutualAuth;
	String keystorePath;
	String truststorePath;
	
	char[] keystorePassword;

	public SSLClient() {
		
		// TODO Testing only
		keystorePassword = new char[] { '9', '8', '7', '6', '5', '4' };
		
		startConfig();
		try {
			doProtocol(socket);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void doProtocol(Socket socket) throws Exception {
		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();

		out.write("World".getBytes());
		out.write('!');

		int ch = 0;
		while ((ch = in.read()) != '!') {
			System.out.println((char) ch);
		}

		System.out.println((char) ch);
	}

	private void readProperties() {

		try {
			Properties prop = new Properties();
			String filename = "client/client.properties";
			InputStream in = new FileInputStream(filename);
			prop.load(in);
			keystorePath = prop.getProperty("keyStorePath");
			truststorePath = prop.getProperty("trustStorePath");
			System.setProperty("javax.net.ssl.trustStore", truststorePath);
			mutualAuth = false;
			if(prop.getProperty("TLS-AUTH").equalsIgnoreCase("MUTUAL"))
				mutualAuth = true;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[SSLClient] Failed to set truststore");
		}

	}

	private void configureSocket() {
		try {
			
			if(!mutualAuth) {
				factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			}
			else {
				ks = KeyStore.getInstance("JKS");
				ks.load(new FileInputStream(keystorePath), keystorePassword);
				keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
				keyManagerFactory.init(ks, keystorePassword);
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
				factory = sslContext.getSocketFactory();
			}
			
			socket = (SSLSocket) factory.createSocket("localhost", 8123);
//			socket.setEnabledCipherSuites(new String[] {"TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384"});
//			socket.setEnabledProtocols(new String[]{"TLSv1.2"});
			socket.startHandshake();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startConfig() {
		readProperties();
		configureSocket();
	}
	
	public static void main(String[] args) {
		new SSLClient();
	}

}
