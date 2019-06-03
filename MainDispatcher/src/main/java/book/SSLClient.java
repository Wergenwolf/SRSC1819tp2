package book;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Properties;

public class SSLClient extends Socket {

	private SSLSocketFactory socketFactory;
	private SSLSocket clientSocket;
	private SSLContext sslContext;
	private KeyStore ks;
	private KeyManagerFactory keyManagerFactory;
	
	private Boolean mutualAuth;
	private String trustStorePath;
	private String keyStorePath;
	private String[] protocols;
	private String[] ciphersuites;
	
	private String url;
	private int port;
	private String configPath;
	private char[] keyStorePassword;
	
	public SSLClient(String url, int port, String configPaths, char[] keyStorePassword) {
		this.url = url;
		this.port = port;
		this.configPath = configPaths;
		this.keyStorePassword = keyStorePassword;
		readClientProperties();
		configureClient();
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		return clientSocket.getInputStream();
	}
	
	@Override
	public OutputStream getOutputStream() throws IOException {
		return clientSocket.getOutputStream();
	}
	
	private void configureClient() {
		try {
			
			if(!mutualAuth) {
				socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			}
			else {
				ks = KeyStore.getInstance("JKS");
				ks.load(new FileInputStream(keyStorePath), keyStorePassword);
				keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
				keyManagerFactory.init(ks, keyStorePassword);
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
				socketFactory = sslContext.getSocketFactory();
			}
			
			clientSocket = (SSLSocket) socketFactory.createSocket(url, port);
//			clientSocket.setEnabledCipherSuites(new String[] {"TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384"});
//			clientSocket.setEnabledProtocols(new String[]{"TLSv1.2"});
			clientSocket.startHandshake();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void readClientProperties() {

		try {
			Properties prop = new Properties();
			String filename = configPath;
			InputStream in = new FileInputStream(filename);
			prop.load(in);
			keyStorePath = prop.getProperty("keyStorePath");
			trustStorePath = prop.getProperty("trustStorePath");
			System.setProperty("javax.net.ssl.trustStore", trustStorePath);
            mutualAuth = prop.getProperty("TLS-AUTH").equalsIgnoreCase("MUTUAL");
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("[SSLClient] Failed to set truststore");
		}

	}
	
}
