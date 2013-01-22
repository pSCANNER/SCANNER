/**
 * 
 */
package edu.isi.misd.scanner.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

/**
 * @author serban
 *
 */
public class JakartaClient {
    // client used to connect with the tagfiler server
	private DefaultHttpClient httpclient;
	
    // client used to connect with the tagfiler server
	protected boolean browser = true;
	
	// error description header
	private static final String error_description_header = "X-Error-Description";
	
	// error description begin message
	private static final String error_description_begin = "<p>";
	
	// error description end message
	private static final String error_description_end = "</p>";
	
	// the cookie value
	protected String cookieValue;
	
	// number of retries if the connection is broken
	protected int retries = 10;
	
	// exception messages got during the retries if the connection is broken
	protected String connectException;
	protected String clientProtocolException;
	protected String ioException;
	/**
     * Constructor
     * 
     * @param connections
     *            the maximum number of HTTP connections
     */
	public JakartaClient(int maxConnections, int socketBufferSize, int socketTimeout) {
		try {
			init(maxConnections, socketBufferSize, socketTimeout);
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
	private void init(int maxConnections, int socketBufferSize, int socketTimeout) throws Throwable {
		TrustManager easyTrustManager = new X509TrustManager() {

		    public void checkClientTrusted(
		            X509Certificate[] chain,
		            String authType) throws CertificateException {
		        // Oh, I am easy!
		    }

		    public void checkServerTrusted(
		            X509Certificate[] chain,
		            String authType) throws CertificateException {
		    	if (chain != null) {
		    		for (int i=0; i < chain.length; i++) {
		    			chain[i].checkValidity();
		    		}
		    	}
		    }

		    public X509Certificate[] getAcceptedIssuers() {
		        return null;
		    }
		    
		};
		
		SSLContext sslcontext = SSLContext.getInstance("SSL");
		sslcontext.init(null, new TrustManager[] { easyTrustManager }, null);
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
	}

    /**
     * Execute a login request.
     * If success, it will get a cookie
     * 
     * @param url
     *            the query url
     * @param user
     *            the userid
     * @param password
     *            the password
     * @return the HTTP Response
     */
    public ClientURLResponse login(String url, String user, String password) {
		browser = false;
        HttpPost httppost = new HttpPost(url);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("username", user));
		formparams.add(new BasicNameValuePair("password", password));
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		httppost.setEntity(entity);
		return execute(httppost, null);
    }
    
    /**
     * Execute a login request.
     * If success, it will get a cookie
     * 
     * @param url
     *            the query url
     * @param user
     *            the userid
     * @param password
     *            the password
     * @return the HTTP Response
     * @throws JSONException 
     */
    public ClientURLResponse postScannerQuery(String url, String targets, String body) {
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-Type", "application/json; charset=UTF-8");
        httppost.setHeader("targets", targets);
        if (body != null) {
			try {
				StringEntity entity = new StringEntity(body, "UTF-8");
	    		httppost.setEntity(entity);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		return execute(httppost, null);
    }
    
    /**
     * Delete a resource
     * 
     * @param url
     *            the url of the resource to be deleted
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
    public ClientURLResponse delete(String url, String cookie) {
		HttpDelete httpdelete = new HttpDelete(url);
		return execute(httpdelete, cookie);
	}
    
    /**
     * Get the list of the file names to be downloaded.
     * 
     * @param url
     *            the query url
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
    public ClientURLResponse get(String url, String cookie) {
		HttpGet httpget = new HttpGet(url);
    	httpget.setHeader("Accept", "application/json");
		return execute(httpget, cookie);
	}
    
    /**
     * Execute a HttpUriRequest
     * 
     * @param request
     *            the request to be executed
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
    private ClientURLResponse execute(HttpUriRequest request, String cookie) {
    	setCookie(cookie, request);
    	request.setHeader("X-Machine-Generated", "true");
    	ClientURLResponse response = null;
    	int count = 0;
    	while (true) {
    		try {
    			response = new ClientURLResponse(httpclient.execute(request));
				setCookieValue(response.getCookieValue());
        		System.out.println("Response cookie: "+ getCookieValue());
    			break;
    		} catch (ConnectException e) {
    			// Can not connect and send the request
    			// Retry maximum 10 times
    			synchronized (this) {
        			if (connectException == null || !connectException.equals(e.getMessage())) {
            			System.err.println("ConnectException");
        				e.printStackTrace();
            			connectException = e.getMessage();
        			}
    			}
    		} catch (ClientProtocolException e) {
    			// TODO Auto-generated catch block
    			synchronized (this) {
        			if (clientProtocolException == null || !clientProtocolException.equals(e.getMessage())) {
            			System.err.println("ClientProtocolException");
            			e.printStackTrace();
            			clientProtocolException = e.getMessage();
        			}
    			}
    		} catch (IOException e) {
    			// The request was sent, but no response; connection might have been broken
    			synchronized (this) {
        			if (ioException == null || !ioException.equals(e.getMessage())) {
            			System.err.println("IOException");
            			e.printStackTrace();
            			ioException = e.getMessage();
        			}
    			}
    		}
			if (++count > retries) {
				break;
			} else {
				// just in case
				if (response != null) {
					response.release();
				}
				// sleep before retrying
				int delay = (int) Math.ceil((0.75 + Math.random() * 0.5) * Math.pow(10, count) * 0.00001);
				System.out.println("Retry delay: " + delay + " ms.");
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
    	}
		
		return response;
    }
    
    /**
     * Get the content of a file to be downloaded
     * 
     * @param url
     *            the query url
     * @param cookie
     *            the cookie to be set in the request
     * @return the HTTP Response
     */
    public ClientURLResponse downloadFile(String url, String cookie) {
		HttpGet httpget = new HttpGet(url);
		return execute(httpget, cookie);
	}
    
    /**
     * Set the cookie for the request
     * 
     * @param cookie
     *            the cookie to be set in the request
     * @param request
     *            the request to be sent
     */
    private void setCookie(String cookie, HttpUriRequest request) {
    	if (cookie != null) {
    		System.out.println("Request cookie: "+cookie);
        	//request.setHeader("Cookie", cookieName+"="+cookie);
        	request.setHeader("Cookie", cookie);
    	}
    }
    
	/**
	 * @return the cookieValue
	 */
	public String getCookieValue() {
		return cookieValue;
	}

	/**
	 * @param cookieValue the cookieValue to set
	 */
	public void setCookieValue(String cookieValue) {
		this.cookieValue = cookieValue;
	}

	public class ClientURLResponse {
    	private HttpResponse response;
    	
    	ClientURLResponse(HttpResponse response) {
    		this.response = response;
    	}
    	
        /**
         * Return the HTTP status code
         * 
         */
        public int getStatus() {
    		return response.getStatusLine().getStatusCode();
    	}

        /**
         * Return the body as a string
         * 
         */
        public String getEntityString() {
        	try {
    			String result = EntityUtils.toString(response.getEntity());
    			return result;
    		} catch (ParseException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		} catch (IOException e1) {
    			// TODO Auto-generated catch block
    			e1.printStackTrace();
    		}
    		return null;
    	}

        
        /**
         * Return the InputStream from where the body can be read
         * 
         */
        public InputStream getEntityInputStream() {
        	InputStream inputStream = null;
        	try {
        		inputStream = response.getEntity().getContent();
    		} catch (IllegalStateException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		return inputStream;
    	}

        /**
         * Return the error message of an HTTP Request
         * 
         */
        public String getErrorMessage() {
        	String errormessage = "";
        	
        	// get the error message from the header
        	Header header = response.getFirstHeader(error_description_header);
        	if (header != null) {
        		try {
					errormessage = URLDecoder.decode(header.getValue(),  "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	} 
        	
        	// no error message in the header
        	// try to get it from the response body
        	if (errormessage.length() == 0) {
        		String message = getEntityString();
        		if (message != null) {
            		int first = message.indexOf(error_description_begin);
            		int last = message.indexOf(error_description_end);
            		if (first != -1 && last != -1 && last > first) {
            			first += error_description_begin.length();
            			errormessage = message.substring(first, last);
            		}
        		}
        	}
    		return errormessage;
    	}

        /**
         * Get the response size
         * 
         */
        public long getResponseSize() {
        	long length = Long.parseLong(response.getFirstHeader("Content-Length").getValue());
        	return length;
    	}
        
        /**
         * Release the responses
         * 
         */
        public void release() {
            if (response.getEntity() != null) {
            	try {
    				response.getEntity().consumeContent();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            }
    	}
        public String getCookieValue() {
        	String res = null;
        	try {
        		Header cookies[] = response.getHeaders("Set-Cookie");
        		System.out.println("Number of cookies: " + cookies.length);
        		if (cookies.length > 0) {
    				res = URLDecoder.decode(response.getFirstHeader("Set-Cookie").getValue(), "UTF-8");
       		}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return res;
        }

        /**
         * Utility to print the status as well as the headers
         */
        public void debug() {
        	if (response != null) {
            	System.out.println("Status: " + response.getStatusLine().getStatusCode());
    			Header headers[] = response.getAllHeaders();
    	        for (int i=0; i<headers.length; i++) {
    	        	try {
    					System.out.println(headers[i].getName()+": "+URLDecoder.decode(headers[i].getValue(), "UTF-8"));
    				} catch (UnsupportedEncodingException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    	        }
        	}
            for (Cookie cookie : httpclient.getCookieStore().getCookies()) {
    			System.out.println("Cookie: " + cookie.getName() +"=" + cookie.getValue());
            }
        }
        
    }
    
	
}
