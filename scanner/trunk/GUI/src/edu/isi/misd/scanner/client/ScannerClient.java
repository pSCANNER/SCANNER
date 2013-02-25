package edu.isi.misd.scanner.client;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;

public class ScannerClient {
    // client used to connect with the tagfiler server
	private DefaultHttpClient httpclient;
	/**
     * Constructor
     * 
     * @param connections
     *            the maximum number of HTTP connections
     */
	public ScannerClient(int maxConnections, int socketBufferSize, int socketTimeout,
			String trustStoreType, String trustPassword, String trustFile,
			String keyStoreType, String keyPassword, String keyFile) {
		try {
			init(maxConnections, socketBufferSize, socketTimeout,
					trustStoreType, trustPassword, trustFile,
					keyStoreType, keyPassword, keyFile);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    /**
     * Initialize the HTTP client
     * 
     * @param connections
     *            the maximum number of HTTP connections
     * @param socketBufferSize
     *            the socket buffer size
     * @param socketTimeout
     *            the socket buffer timeout
     */
	private void init(int maxConnections, int socketBufferSize, int socketTimeout,
			String trustStoreType, String trustPassword, String trustFile,
			String keyStoreType, String keyPassword, String keyFile) throws Throwable {
		//trustStoreType = "JKS"
		//trustPassword = "scannertest"
		//trustFile = "etc/security/scanner-test-ca-cert.jks"
		//keyStoreType = "PKCS12"
		//keyPassword = "master"
		//keyFile = "etc/security/scanner-test-master.p12"
		KeyStore trustKeyStore = KeyStore.getInstance(trustStoreType);
		FileInputStream fis = new FileInputStream(trustFile);
		trustKeyStore.load(fis, trustPassword.toCharArray());
		fis.close();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(trustKeyStore);
		TrustManager[] tm = tmf.getTrustManagers();
		
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		fis = new FileInputStream(keyFile);
		keyStore.load(fis, keyPassword.toCharArray());
		fis.close();
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, keyPassword.toCharArray());
		KeyManager[] km = kmf.getKeyManagers();

		//SSLContext sslcontext = SSLContext.getInstance("TLS");
		//SSLContext sslcontext = SSLContext.getInstance("SSL");
		//sslcontext.init(null, new TrustManager[] { easyTrustManager }, null);
		SSLContext sslcontext = SSLContext.getInstance("SSL");
		sslcontext.init(km, tm, null);
		SSLSocketFactory sf = new SSLSocketFactory(sslcontext); 
		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		
		BasicHttpParams params = new BasicHttpParams();
		params.setParameter("http.protocol.handle-redirects", false);
		params.setParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, socketBufferSize);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
		
		// enable parallelism
		ConnPerRouteBean connPerRoute = new ConnPerRouteBean(maxConnections);
		ConnManagerParams.setMaxTotalConnections(params, maxConnections >= 2 ? maxConnections : 2);
		ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);
		
        SchemeRegistry schemeRegistry = new SchemeRegistry(); 
        Scheme sch = new Scheme("https", sf, 443);
        schemeRegistry.register(sch);
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ClientConnectionManager cm = new ThreadSafeClientConnManager(params, schemeRegistry);

        httpclient = new DefaultHttpClient(cm, params);
    	BasicCookieStore cookieStore = new BasicCookieStore();
    	httpclient.setCookieStore(cookieStore);
    	
    	System.out.println("Scanner Client was successfully initialized");
	}


}
