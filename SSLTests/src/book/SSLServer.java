package finalSSL;

import java.io.FileInputStream;
import java.io.IOException;
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

public class SSLServer extends Socket {

	private SSLServerSocketFactory serverSocketFactory;
	private SSLServerSocket serverSocket;
	private SSLSocket sslSocket;
	private SSLContext sslContext;
	private KeyStore ks;
	private KeyManagerFactory keyManagerFactory;
	
	private Boolean mutualAuth;
	private String trustStorePath;
	private String keyStorePath;
	private String[] protocols;
	private String[] ciphersuites;
	
	private int port;
	private String[] configPaths;
	private char[] keyStorePassword;
	
	public SSLServer(String url, int port, String[] configPaths, char[] keyStorePassword) {
		this.port = port;
		this.configPaths = configPaths;
		this.keyStorePassword = keyStorePassword;
		readServerProperties();
		configureServer();
	}
	
	public Socket accept() {
		try {
			sslSocket = (SSLSocket) serverSocket.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sslSocket;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return sslSocket.getInputStream();
	}
	
	@Override
	public OutputStream getOutputStream() throws IOException {
		return sslSocket.getOutputStream();
	}
	
	private void configureServer() {
		try {
			
			ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(keyStorePath), keyStorePassword);
			keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyManagerFactory.init(ks, keyStorePassword);
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

			serverSocketFactory = sslContext.getServerSocketFactory();
			serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(port);
			serverSocket.setNeedClientAuth(mutualAuth);
			serverSocket.setEnabledProtocols(protocols);
			serverSocket.setEnabledCipherSuites(ciphersuites);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void readServerProperties() {

		try {
			Properties prop = new Properties();
			String filename = configPaths[0];
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
			
			filename = configPaths[1];
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
	
}
