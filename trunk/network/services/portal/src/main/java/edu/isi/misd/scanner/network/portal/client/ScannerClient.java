package edu.isi.misd.scanner.network.portal.client;

/* 
 * Copyright 2013 University of Southern California
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class to implement an HTTPS client.
 * 
 * @author Serban Voinea
 *
 */
public class ScannerClient extends JakartaClient {

	private static final transient Logger log = 
	        LoggerFactory.getLogger(ScannerClient.class);    
	/**
     * Constructor.
     * 
     * @param maxConnections
     *            the maximum number of HTTP connections.
     * @param socketBufferSize
     *            the socket buffer size.
     * @param socketTimeout
     *            the socket buffer timeout.
     * @param trustStoreType
     *            the TrustStore type.
     * @param trustStorePassword
     *            the Trust Store password.
     * @param trustStoreResource
     *            the Trust Store file.
     * @param keyStoreType
     *            the KeyStore type.
     * @param keyStorePassword
     *            the KeyStore password.
     * @param keyStoreResource
     *            the KeyStore file.
     * @param keyManagerPassword
     *            the KeyManager password.
     */
	public ScannerClient(int maxConnections, int socketBufferSize, int socketTimeout,
			String trustStoreType, String trustStorePassword, String trustStoreResource,
			String keyStoreType, String keyStorePassword, String keyStoreResource, String keyManagerPassword) {
		try {
			init(maxConnections, socketBufferSize, socketTimeout,
					trustStoreType, trustStorePassword, trustStoreResource,
					keyStoreType, keyStorePassword, keyStoreResource, keyManagerPassword);
		} catch (Throwable e) {
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
     * @param trustStoreType
     *            the TrustStore type
     * @param trustStorePassword
     *            the Trust Store password
     * @param trustStoreResource
     *            the Trust Store file
     * @param keyStoreType
     *            the KeyStore type
     * @param keyStorePassword
     *            the KeyStore password
     * @param keyStoreResource
     *            the KeyStore file
     * @param keyManagerPassword
     *            the KeyManager password
     */
	private void init(int maxConnections, int socketBufferSize, int socketTimeout,
			String trustStoreType, String trustStorePassword, String trustStoreResource,
			String keyStoreType, String keyStorePassword, String keyStoreResource, String keyManagerPassword) throws Throwable {
		/*
		if (log.isDebugEnabled()) log.debug("keyManagerPassword: "+keyManagerPassword +
				"\nkeyStoreType: "+keyStoreType +
				"\nkeyStoreResource: "+keyStoreResource +
				"\nkeyStorePassword: "+keyStorePassword +
				"\ntrustStoreType: "+trustStoreType +
				"\ntrustStoreResource: "+trustStoreResource +
				"\ntrustStorePassword: "+trustStorePassword);
		*/
		log.info("keyStoreResource: "+keyStoreResource);
		log.info("trustStoreResource: "+trustStoreResource);

		
		KeyStore trustKeyStore = KeyStore.getInstance(trustStoreType);
		FileInputStream fis = new FileInputStream(trustStoreResource);
		trustKeyStore.load(fis, trustStorePassword.toCharArray());
		fis.close();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(trustKeyStore);
		TrustManager[] tm = tmf.getTrustManagers();
		
		KeyStore keyStore = KeyStore.getInstance(keyStoreType);
		fis = new FileInputStream(keyStoreResource);
		keyStore.load(fis, keyStorePassword.toCharArray());
		fis.close();
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(keyStore, keyManagerPassword.toCharArray());
		KeyManager[] km = kmf.getKeyManagers();

		SSLContext sslcontext = SSLContext.getInstance("SSL");
		sslcontext.init(km, tm, null);
		SSLSocketFactory sf = new SSLSocketFactory(sslcontext); 
		sf.setHostnameVerifier(SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
		
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
    	
    	log.info("Scanner Client was successfully initialized.");
	}

}
