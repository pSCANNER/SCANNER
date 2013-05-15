package edu.isi.misd.scanner.servlets;

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

import edu.isi.misd.scanner.client.JakartaClient;
import edu.isi.misd.scanner.client.RegistryClient;
import edu.isi.misd.scanner.client.RegistryClientResponse;
import edu.isi.misd.scanner.client.ScannerClient;
import edu.isi.misd.scanner.client.TagfilerClient;
import edu.isi.misd.scanner.client.JakartaClient.ClientURLResponse;

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
	private String tagfilerURL;
	private String tagfilerUser;
	private String tagfilerPassword;
	
	private String trustStoreType;
	private String trustStorePassword;
	private String trustStoreResource;
	private String keyStoreType;
	private String keyStorePassword;
	private String keyStoreResource;
	private String keyManagerPassword;
	
	private RegistryClient registryClient;
	/**
	 * The client to execute the network requests.
	 */
	ScannerClient scannerClient;
       
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
		System.out.println("Echo is initialized");
		servletConfig = config;
		tagfilerURL = servletConfig.getServletContext().getInitParameter("tagfilerURL");
		tagfilerUser = servletConfig.getServletContext().getInitParameter("tagfilerUser");
		tagfilerPassword = servletConfig.getServletContext().getInitParameter("tagfilerPassword");
		trustStoreType = servletConfig.getServletContext().getInitParameter("trustStoreType");
		trustStorePassword = servletConfig.getServletContext().getInitParameter("trustStorePassword");
		trustStoreResource = servletConfig.getServletContext().getInitParameter("trustStoreResource");
		keyStoreType = servletConfig.getServletContext().getInitParameter("keyStoreType");
		keyStorePassword = servletConfig.getServletContext().getInitParameter("keyStorePassword");
		keyStoreResource = servletConfig.getServletContext().getInitParameter("keyStoreResource");
		keyManagerPassword = servletConfig.getServletContext().getInitParameter("keyManagerPassword");
		String path = servletConfig.getServletContext().getRealPath("/index.html");
		int index = path.lastIndexOf(File.separator) + 1;
		path = path.substring(0, index);
		trustStoreResource = path + trustStoreResource;
		keyStoreResource = path + keyStoreResource;
		System.out.println("trustStoreResource: " + trustStoreResource);
		System.out.println("keyStoreResource: " + keyStoreResource);
		servletContext = config.getServletContext();
		JakartaClient client = new JakartaClient(4, 8192, 120000);
		ClientURLResponse rsp = client.login(tagfilerURL + "/session", tagfilerUser, tagfilerPassword);
		if (rsp != null) {
			registryClient = new TagfilerClient(client, tagfilerURL, client.getCookieValue());
			scannerClient = new ScannerClient(4, 8192, 300000,
					trustStoreType, trustStorePassword, trustStoreResource,
					keyStoreType, keyStorePassword, keyStoreResource, keyManagerPassword);
			(new EchoThread()).start();
		}
		
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
						Thread.sleep(5*60*1000);
					}
					count++;
					RegistryClientResponse clientResponse = registryClient.getSitesMap();
					JSONObject sites = clientResponse.toSitesMap();
					JSONObject sitesMap = sites.getJSONObject("map");
					JSONObject targets = sites.getJSONObject("targets");
					ret.put("sitesMap", sitesMap);
					clientResponse = registryClient.getMasterObject();
					String res = clientResponse.toMasterString();
					System.out.println("master string: " + res);
					JSONObject temp = new JSONObject(res);
					String masterURL = temp.getString("rURL");
					String url = masterURL + "/query/example/echo";
					StringBuffer buff = new StringBuffer();
					JSONArray names = targets.names();
					for (int i=0; i < names.length(); i++) {
						String key = names.getString(i);
						if (i != 0) {
							buff.append(",");
						}
						buff.append(key + "/dataset/example/echo");
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
					String contentType = rsp.getHeader("Content-Type");
					System.out.println("contentType received: " + contentType);
					res = rsp.getEntityString();
					System.out.println("Response Body: \n"+res);
					ret.put("timestamp", (new Date()).toString());
					if (contentType != null && contentType.indexOf("application/json") != -1) {
						JSONObject echoResult = new JSONObject(res);
						ret.put("echo", echoResult);
						if (echoResult.has("Error")) {
							JSONObject errorsStatistics = new JSONObject();
							ret.put("errorsStatistics", errorsStatistics);
							errorsStatistics.put("total", count);
							JSONArray errors = echoResult.optJSONArray("Error");
							if (errors == null) {
								errors = new JSONArray();
								errors.put(echoResult.getJSONObject("Error"));
							}
							for (int i=0; i < errors.length(); i++) {
								JSONObject obj = errors.getJSONObject(i);
								URI error_url = new URI(obj.getString("ErrorSource"));
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
