/**
 * 
 */
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
import org.apache.http.client.methods.HttpPut;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to implement an HTTP Client
 * @author Serban Voinea
 *
 */
public class JakartaClient {
	/**
	 * The HTTP client.
	 * 
	 */
	protected DefaultHttpClient httpclient;
	
	// error description header
	private static final String error_description_header = "X-Error-Description";
	
	// error description begin message
	private static final String error_description_begin = "<p>";
	
	// error description end message
	private static final String error_description_end = "</p>";
	
	/**
	 * The cookie.
	 * 
	 */
	protected String cookieValue;
	
	/**
	 * The maximum number of retries in case of a transient network failure.
	 * 
	 */
	protected int retries = 10;
	
	/**
	 * The last connect exception message.
	 * 
	 */
	protected String connectException;
	/**
	 * The last client protocol exception message.
	 * 
	 */
	protected String clientProtocolException;
	/**
	 * The last I/O exception message.
	 * 
	 */
	protected String ioException;
	private static final transient Logger log = 
	        LoggerFactory.getLogger(JakartaClient.class);    

	public JakartaClient() {
		
	}
	/**
     * Constructor.
     * 
     * @param maxConnections
     *            the maximum number of HTTP connections.
     * @param socketBufferSize
     *            the socket buffer size.
     * @param socketTimeout
     *            the socket buffer timeout.
     */
	public JakartaClient(int maxConnections, int socketBufferSize, int socketTimeout) {
		try {
			init(maxConnections, socketBufferSize, socketTimeout);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Initialize the HTTP client.
     * 
     * @param connections
     *            the maximum number of HTTP connections.
     * @param socketBufferSize
     *            the socket buffer size.
     * @param socketTimeout
     *            the socket buffer timeout.
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
     * <br/>If success, it will get a cookie.
     * 
     * @param url
     *            the request URL.
     * @param user
     *            the userid.
     * @param password
     *            the password.
     * @return The HTTP response.
     */
    public ClientURLResponse login(String url, String user, String password) {
        HttpPost httppost = new HttpPost(url);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("username", user));
		formparams.add(new BasicNameValuePair("password", password));
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(formparams, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		httppost.setEntity(entity);
		return execute(httppost, null);
    }
    
    /**
     * Execute a POST request.
     * 
     * @param url
     *            the query URL.
     * @param cookie
     *            the request cookie.
     * @return The HTTP response.
     */
    public ClientURLResponse postResource(String url, String cookie) {
    	ClientURLResponse response = null;
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("Accept", "text/uri-list");
        httppost.setHeader("Content-Type", "charset=UTF-8");
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("action", "post"));
		UrlEncodedFormEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(entity);
			response = execute(httppost, cookie);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		return response;
    }
    
    /**
     * Execute a PUT request.
     * 
     * @param url
     *            the request URL.
     * @param cookie
     *            the request cookie.
     * @return The HTTP response.
     */
    public ClientURLResponse putResource(String url, String cookie) {
    	ClientURLResponse response = null;
		HttpPut httpput = new HttpPut(url);
    	httpput.setHeader("Content-Type", "charset=UTF-8");
    	response = execute(httpput, cookie);
		return response;
    }
    
    /**
     * Execute a SCANNER network request.
     * 
     * @param url
     *            the query URL.
     * @param targets
     *            the targets in the request header. 
     * @param body
     *            the request body.
     * @return The HTTP response.
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
				e.printStackTrace();
			}
        }
        retries = 0;
        ClientURLResponse rsp = execute(httppost, null);
        retries = 10;
		return rsp;
    }
    
    /**
     * Execute a Registry request.
     * 
     * @param url
     *            the query URL.
     * @param body
     *            the request body.
     * @return The HTTP response.
     */
    public ClientURLResponse postRegistry(String url, String body, String user) {
    	return postRegistry(url, "application/json; charset=UTF-8", body, user);
    }
    
    /**
     * Execute a Registry request.
     * 
     * @param url
     *            the query URL.
     * @param body
     *            the request body.
     * @return The HTTP response.
     */
    public ClientURLResponse postRegistry(String url, String contentType, String body, String user) {
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("loginName", user);
        httppost.setHeader("Accept", "application/json");
        httppost.setHeader("Content-Type", contentType);
        if (body != null) {
			try {
				StringEntity entity = new StringEntity(body, "UTF-8");
	    		httppost.setEntity(entity);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        retries = 0;
        ClientURLResponse rsp = execute(httppost, null);
        retries = 10;
		return rsp;
    }
    
    /**
     * Execute a Registry request.
     * 
     * @param url
     *            the query URL.
     * @param body
     *            the request body.
     * @return The HTTP response.
     */
    public ClientURLResponse putRegistry(String url, String body, String user) {
        HttpPut httpput = new HttpPut(url);
        httpput.setHeader("loginName", user);
        httpput.setHeader("Accept", "application/json");
        httpput.setHeader("Content-Type", "application/json; charset=UTF-8");
        if (body != null) {
			try {
				StringEntity entity = new StringEntity(body, "UTF-8");
				httpput.setEntity(entity);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
        }
        retries = 0;
        ClientURLResponse rsp = execute(httpput, null);
        retries = 10;
		return rsp;
    }
    
    /**
     * Execute a SCANNER network request.
     * 
     * @param url
     *            the query URL.
     * @param targets
     *            the targets in the request header. 
     * @param body
     *            the request body.
     * @return The HTTP response.
     */
    public ClientURLResponse get(String url, StringBuffer targets) {
    	HttpGet httpget = new HttpGet(url);
    	httpget.setHeader("Accept", "application/json");
    	httpget.setHeader("Content-Type", "application/json; charset=UTF-8");
    	httpget.setHeader("targets", targets.toString());
        retries = 0;
        ClientURLResponse rsp = execute(httpget, null);
        retries = 10;
		return rsp;
    }
    
    /**
     * Delete a resource.
     * 
     * @param url
     *            the URL of the resource to be deleted.
     * @param cookie
     *            the cookie to be set in the request.
     * @return The HTTP response.
     */
    public ClientURLResponse delete(String url, String cookie, String user) {
		HttpDelete httpdelete = new HttpDelete(url);
		httpdelete.setHeader("loginName", user);
		return execute(httpdelete, cookie);
	}
    
    /**
     * Execute a GET request.
     * 
     * @param url
     *            the query URL.
     * @param cookie
     *            the cookie to be set in the request.
     * @return The HTTP response.
     */
    public ClientURLResponse get(String url, String cookie) {
		HttpGet httpget = new HttpGet(url);
    	httpget.setHeader("Accept", "application/json");
		return execute(httpget, cookie);
	}
    
    /**
     * Execute a GET request.
     * 
     * @param url
     *            the query URL.
     * @param cookie
     *            the cookie to be set in the request.
     * @return The HTTP response.
     */
    public ClientURLResponse get(String url, String cookie, String user) {
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("loginName", user);
    	httpget.setHeader("Accept", "application/json");
		return execute(httpget, cookie);
	}
    
    /**
     * Execute a GET request.
     * 
     * @param url
     *            the query URL.
     * @return The HTTP response.
     */
    public ClientURLResponse get(String url) {
		HttpGet httpget = new HttpGet(url);
    	httpget.setHeader("Accept", "application/json");
    	httpget.setHeader("Content-Type", "application/json; charset=UTF-8");
		return execute(httpget, null);
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
    	if (cookie != null) {
        	setCookie(cookie, request);
    	}
    	request.setHeader("X-Machine-Generated", "true");
    	ClientURLResponse response = null;
    	int count = 0;
    	connectException = clientProtocolException = ioException = null;
    	Exception lastException = null;
    	while (true) {
    		try {
    			response = new ClientURLResponse(httpclient.execute(request));
    			if (cookie != null) {
    				setCookieValue(response.getCookieValue());
    				if (getCookieValue() != null) {
    	        		if (log.isDebugEnabled()) log.debug("Response cookie: "+ getCookieValue());
    				}
    			}
    			break;
    		} catch (ConnectException e) {
    			// Can not connect and send the request
    			// Retry maximum 10 times
    			synchronized (this) {
        			if (connectException == null || !connectException.equals(e.getMessage())) {
            			System.err.println("ConnectException");
        				e.printStackTrace();
            			connectException = e.getMessage();
            			lastException = e;
        			}
    			}
    		} catch (ClientProtocolException e) {
    			synchronized (this) {
        			if (clientProtocolException == null || !clientProtocolException.equals(e.getMessage())) {
            			System.err.println("ClientProtocolException");
            			e.printStackTrace();
            			clientProtocolException = e.getMessage();
            			lastException = e;
        			}
    			}
    		} catch (IOException e) {
    			// The request was sent, but no response; connection might have been broken
    			synchronized (this) {
        			if (ioException == null || !ioException.equals(e.getMessage())) {
            			System.err.println("IOException");
            			e.printStackTrace();
            			ioException = e.getMessage();
            			lastException = e;
        			}
    			}
    		}
			if (++count > retries) {
    			response = new ClientURLResponse(lastException);
				break;
			} else {
				// just in case
				if (response != null) {
					response.release();
				}
				// sleep before retrying
				int delay = (int) Math.ceil((0.75 + Math.random() * 0.5) * Math.pow(10, count) * 0.00001);
				log.info("Retry delay: " + delay + " ms.");
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
    	}
		
		return response;
    }
    
    /**
     * Gets the content of a file to be downloaded.
     * 
     * @param url
     *            the query URL.
     * @param cookie
     *            the cookie to be set in the request.
     * @return The HTTP response.
     */
    public ClientURLResponse downloadFile(String url, String cookie) {
		HttpGet httpget = new HttpGet(url);
		return execute(httpget, cookie);
	}
    
    /**
     * Sets the cookie for the request
     * 
     * @param cookie
     *            the cookie to be set in the request
     * @param request
     *            the request to be sent
     */
    private void setCookie(String cookie, HttpUriRequest request) {
    	if (cookie != null) {
    		//if (log.isDebugEnabled()) log.debug("Request cookie: "+cookie);
        	//request.setHeader("Cookie", cookieName+"="+cookie);
        	request.setHeader("Cookie", cookie);
    	}
    }
    
	/**
     * Gets the response cookie.
	 * @return The response cookie.
	 */
	public String getCookieValue() {
		return cookieValue;
	}

	/**
     * Sets the request cookie.
	 * @param 
	 * 		cookieValue the cookie to be set in the request.
	 */
	public void setCookieValue(String cookieValue) {
		this.cookieValue = cookieValue;
	}

	/**
     * Class for wrapping the HTTP response.
	 */
	public class ClientURLResponse {
    	private HttpResponse response;
    	private Exception exception;
    	
    	/**
         * Constructor for the HTTP response.
         * 
         * @param response
         *            the HTTP response.
         */
    	ClientURLResponse(HttpResponse response) {
    		this.response = response;
    	}
    	
    	/**
         * Constructor for an exception.
         * 
         * @param exception
         *            the raised exception.
         */
    	ClientURLResponse(Exception exception) {
    		this.exception = exception;
    	}
    	
    	/**
         * Gets the response status code.
    	 * @return The response status code.
    	 */
        public int getStatus() {
    		return response.getStatusLine().getStatusCode();
    	}
    	
    	/**
         * Checks the response status code.
    	 * @return TRUE if it is a server error; FALSE otherwise.
    	 */
    	public boolean isError() {
    		return getStatus() > 400;
    	}

    	/**
         * Checks the response status.
    	 * @return TRUE if an exception was thrown during the execution of the HTTP request; FALSE otherwise.
    	 */
    	public boolean isException() {
    		return exception != null;
    	}

    	/**
         * Gets the response status.
    	 * @return The exception thrown during the execution of the HTTP request.
    	 */
    	public Exception getException() {
    		return exception;
    	}

    	/**
         * Gets the response body.
    	 * @return The string representing the HTTP response body.
    	 */
        public String getEntityString() {
        	if (response.getEntity() != null) {
               	try {
        			String result = EntityUtils.toString(response.getEntity());
        			return result;
        		} catch (ParseException e1) {
        			e1.printStackTrace();
        		} catch (IOException e1) {
        			e1.printStackTrace();
        		}
        	}
     		return null;
    	}

        
    	/**
         * Gets the response input stream.
    	 * @return The HTTP response input stream.
    	 */
        public InputStream getEntityInputStream() {
        	InputStream inputStream = null;
        	try {
        		inputStream = response.getEntity().getContent();
    		} catch (IllegalStateException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		
    		return inputStream;
    	}

    	/**
         * Gets the error message.
    	 * @return The HTTP error message.
    	 */
        public String getErrorMessage() {
        	String errormessage = "";
        	
        	// get the error message from the header
        	Header header = response.getFirstHeader(error_description_header);
        	if (header != null) {
        		try {
					errormessage = URLDecoder.decode(header.getValue(),  "UTF-8");
				} catch (UnsupportedEncodingException e) {
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
         * Gets the response size.
    	 * @return The HTTP response body size.
    	 */
        public long getResponseSize() {
        	long length = Long.parseLong(response.getFirstHeader("Content-Length").getValue());
        	return length;
    	}
        
        /**
         * Releases the response resources.
         * 
         */
        public void release() {
            if (response.getEntity() != null) {
            	try {
    				response.getEntity().consumeContent();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
            }
    	}
        
    	/**
         * Gets the response cookie.
    	 * @return The HTTP response cookie.
    	 */
        public String getCookieValue() {
        	String res = null;
        	try {
        		Header cookies[] = response.getHeaders("Set-Cookie");
        		if (cookies.length > 0) {
            		if (log.isDebugEnabled()) log.debug("Number of cookies: " + cookies.length);
    				res = URLDecoder.decode(response.getFirstHeader("Set-Cookie").getValue(), "UTF-8");
       		}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return res;
        }

        /**
         * Gets the value for the "id" header.
         * 
    	 * @return The value for the "id" header.
         */
        public String getIdHeader() {
        	String res = null;
        	Header id = response.getFirstHeader("id");
			if (id != null) {
				res = id.getValue();
    		}
			return res;
        }

        /**
         * Gets the value for a request header.
         * 
         * @param name
         *            the header name.
    	 * @return The value of the header.
         */
        public String getHeader(String name) {
        	String res = null;
        	Header value = response.getFirstHeader(name);
			if (value != null) {
				res = value.getValue();
    		}
			return res;
        }

        /**
         * Print the response status, cookie and headers.
         */
        public void debug() {
        	if (response != null) {
            	if (log.isDebugEnabled()) log.debug("Status: " + response.getStatusLine().getStatusCode());
    			Header headers[] = response.getAllHeaders();
    	        for (int i=0; i<headers.length; i++) {
    	        	try {
    					if (log.isDebugEnabled()) log.debug(headers[i].getName()+": "+URLDecoder.decode(headers[i].getValue(), "UTF-8"));
    				} catch (UnsupportedEncodingException e) {
    					e.printStackTrace();
    				}
    	        }
        	}
            for (Cookie cookie : httpclient.getCookieStore().getCookies()) {
    			if (log.isDebugEnabled()) log.debug("Cookie: " + cookie.getName() +"=" + cookie.getValue());
            }
        }
        
    }
    
	
}
