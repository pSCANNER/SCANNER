package edu.isi.misd.scanner.network.portal.servlets;

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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.isi.misd.scanner.network.portal.client.ERDClient;
import edu.isi.misd.scanner.network.portal.client.RegistryClient;
import edu.isi.misd.scanner.network.portal.client.RegistryClientResponse;
import edu.isi.misd.scanner.network.portal.client.ScannerClient;
import edu.isi.misd.scanner.network.portal.client.JakartaClient.ClientURLResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet for pinging the sites nodes.
 * 
 * @author Serban Voinea
 */
@WebServlet(description = "Servlet for pinging the sites nodes")
public class Echo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletContext servletContext;
	private ServletConfig servletConfig;
	
	private String trustStoreType;
	private String trustStorePassword;
	private String trustStoreResource;
	private String keyStoreType;
	private String keyStorePassword;
	private String keyStoreResource;
	private String keyManagerPassword;
	
	private RegistryClient registryClient;
	private boolean debug = false;
	/**
	 * The client to execute the network requests.
	 */
	ScannerClient scannerClient;
	private String erdURL = "http://localhost:8088/scannerV2/registry/";
    
    private static final transient Logger log = 
        LoggerFactory.getLogger(Echo.class);    
    /**
     * Default constructor. 
     * @see HttpServlet#HttpServlet()
     */
    public Echo() {
        super();
    }

	/**
	 * Initialize the servlet.
	 * The servlet is loaded on startup.
	 * It creates a demon that every 5 minutes polls the sites nodes.
	 * The result is stored in the servlet context and logged into a file.
	 * @param config
     *            the servlet configuration.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		log.info("Echo is initialized");
		servletConfig = config;
		trustStoreType = servletConfig.getServletContext().getInitParameter("trustStoreType");
		trustStorePassword = servletConfig.getServletContext().getInitParameter("trustStorePassword");
		trustStoreResource = servletConfig.getServletContext().getInitParameter("trustStoreResource");
		keyStoreType = servletConfig.getServletContext().getInitParameter("keyStoreType");
		keyStorePassword = servletConfig.getServletContext().getInitParameter("keyStorePassword");
		keyStoreResource = servletConfig.getServletContext().getInitParameter("keyStoreResource");
		keyManagerPassword = servletConfig.getServletContext().getInitParameter("keyManagerPassword");
		String registryUrl = servletConfig.getServletContext().getInitParameter("registryURL");
		String debugMode = servletConfig.getServletContext().getInitParameter("debug");
		if (debugMode != null) {
			debug = Boolean.parseBoolean(debugMode);
		}
        String path = servletConfig.getServletContext().getRealPath("/index.html");
		int index = path.lastIndexOf(File.separator) + 1;
		path = path.substring(0, index);
		trustStoreResource = path + trustStoreResource;
		keyStoreResource = path + keyStoreResource;
        if (log.isDebugEnabled()) {
            log.debug("trustStoreResource: " + trustStoreResource);
            log.debug("keyStoreResource: " + keyStoreResource);
        }
		servletContext = config.getServletContext();
		registryClient = new ERDClient((registryUrl != null) ? registryUrl : erdURL, "user");
		scannerClient = new ScannerClient(4, 8192, 300000,
				trustStoreType, trustStorePassword, trustStoreResource,
				keyStoreType, keyStorePassword, keyStoreResource, keyManagerPassword);
		(new EchoThread()).start();
		
	}
	
	private class EchoThread extends Thread {
		EchoThread() {
		}
		
		public void run() {
			boolean ready = false;
			int count = 0;
			HashMap<String,Integer> errorsMap = new HashMap<String,Integer>();
			String path = servletConfig.getServletContext().getRealPath("/index.html");
			int index = path.lastIndexOf(File.separator);
			path = path.substring(0, index);
			index = path.lastIndexOf(File.separator) + 1;
			path = path.substring(0, index);
			String fileName = path + "scanner_echo.log";
			System.out.println("Echo log file: " + fileName);
			while (!ready) {
				JSONObject ret = new JSONObject();
				try {
					if (count != 0) {
						if (debug) {
							ready = true;
						}
						Thread.sleep(5*60*1000);
						if (debug) {
							continue;
						}
					}
					count++;
					RegistryClientResponse clientResponse = registryClient.getSitesMap();
					JSONObject sites = clientResponse.toSitesMap();
					clientResponse.release();
					JSONObject sitesMap = sites.getJSONObject("map");
					JSONObject targets = sites.getJSONObject("targets");
					ret.put("sitesMap", sitesMap);
					clientResponse = registryClient.getMasterObject();
					String res = clientResponse.toMasterString();
					clientResponse.release();
					System.out.println("master string: " + res);
					JSONObject temp = new JSONObject(res);
					String masterURL = temp.getString("hostUrl") + ":" + temp.getInt("hostPort") + temp.getString("basePath");
					String url = masterURL + "example/echo";
					StringBuffer buff = new StringBuffer();
					JSONArray names = targets.names();
					for (int i=0; i < names.length(); i++) {
						String key = names.getString(i);
						if (i != 0) {
							buff.append(",");
						}
						buff.append(key + "example/echo");
					}
					String targetsURLs = buff.toString();
					JSONObject body = new JSONObject();
					JSONArray simpleMapData = new JSONArray();
					body.put("simpleMapData", simpleMapData);
					JSONObject params = new JSONObject();
					simpleMapData.put(params);
					params.put("key", "echoParameter");
					params.put("value", "Test");
					System.out.println("URL: " + url + "\nTargets: "+targetsURLs+"\nBody: "+body);
					ClientURLResponse rsp = scannerClient.postScannerQuery(url, targetsURLs, body.toString());
					if (rsp.isException()) {
						continue;
					}
					String contentType = rsp.getHeader("Content-Type");
					System.out.println("contentType received: " + contentType);
					res = rsp.getEntityString();
					System.out.println("Response Body: \n"+res);
					System.out.println("Ret: \n"+ret);
					ret.put("timestamp", (new Date()).toString());
					if (contentType != null && contentType.indexOf("application/json") != -1) {
						JSONObject echoResult = new JSONObject(res);
						ret.put("echo", echoResult);
						JSONArray serviceResponse = echoResult.getJSONObject("ServiceResponses").getJSONArray("ServiceResponse");
						JSONArray errors = new JSONArray();
						for (int i=0; i < serviceResponse.length(); i++) {
							JSONObject serviceResponseMetadata = serviceResponse.getJSONObject(i).getJSONObject("ServiceResponseMetadata");
							if (!serviceResponseMetadata.getString("RequestState").equals("Complete")) {
								errors.put(serviceResponseMetadata);
							}
						}
						if (errors.length() > 0) {
							JSONObject errorsStatistics = new JSONObject();
							ret.put("errorsStatistics", errorsStatistics);
							errorsStatistics.put("total", count);
							for (int i=0; i < errors.length(); i++) {
								JSONObject obj = errors.getJSONObject(i);
								URI error_url = new URI(obj.getString("RequestURL"));
								String host = error_url.getHost();
								int port = error_url.getPort();
								String key = host + (port == -1 ? "" : ":" + port);
								Integer value = errorsMap.get(key);
								if (value == null) {
									value = 0;
								}
								errorsMap.put(key, ++value);
								errorsStatistics.put(key, value);
							}
							FileWriter fileWriter = new FileWriter(fileName, true);
							fileWriter.write(ret.toString() + "\n\n");
							fileWriter.close();
						}
						servletContext.setAttribute("echo", ret);
					} else {
						ret.put("echo", res);
						FileWriter fileWriter = new FileWriter(fileName, true);
						fileWriter.write(ret.toString() + "\n\n");
						fileWriter.close();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
