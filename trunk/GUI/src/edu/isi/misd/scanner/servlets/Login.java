package edu.isi.misd.scanner.servlets;

/* 
 * Copyright 2012 University of Southern California
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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.isi.misd.scanner.client.JakartaClient;
import edu.isi.misd.scanner.utils.Utils;

/**
 * Servlet implementation class Login
 * 
 * @author Serban Voinea
 */

@WebServlet(description = "Servlet for Login", urlPatterns = { "/login" })
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String shibIdAttr = "ShibuscNetID";
	//private static final String shibDisplayNameAttr = "ShibdisplayName";
	//private static final String shibFirstNameAttr = "ShibgivenName";
	//private static final String shibLastNameAttr = "Shibsurname";
	//private static final String shibMailAttr = "Shibmail";
	//private static final String shibRolesAttr = "ShibuscAffiliation";
	private static final String shibPrimaryRoleAttr = "ShibuscPrimaryAffiliation";

	private static final String shibFirstNameAttr = "givenName";
	private static final String shibLastNameAttr = "sn";
	private static final String shibDisplayNameAttr = "displayName";
	private static final String eppn = "eppn";
	private static final String persistent_id = "persistent-id";
	private static final String shibMailAttr = "mail";
	private static final String shibRolesAttr = "protectNetworkEntitlement";
    /**
     * Default constructor. 
     */
    public Login() {
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = (String) request.getAttribute(shibIdAttr);
		String displayName = (String) request.getAttribute(shibDisplayNameAttr);
		String firstName = (String) request.getAttribute(shibFirstNameAttr);
		String lastName = (String) request.getAttribute(shibLastNameAttr);
		String mail = (String) request.getAttribute(shibMailAttr);
		String roles = (String) request.getAttribute(shibRolesAttr);
		String role = (String) request.getAttribute(shibPrimaryRoleAttr);
		String remoteUser = request.getRemoteUser();
		String eppn_val = (String) request.getAttribute(eppn);
		String persistent_id_val = (String) request.getAttribute(persistent_id);
		ArrayList<String> userRoles = new ArrayList<String>();
		if (roles != null) {
			StringTokenizer tokenizer = new StringTokenizer(roles, ";");
			while (tokenizer.hasMoreTokens()) {
				userRoles.add(tokenizer.nextToken());
			}
		}
		System.out.println("User \"" + remoteUser + "\" agreed." + "\n\t" +
				"id: " + remoteUser + "\n\t" +
				"displayName: " + displayName + "\n\t" +
				"firstName: " + firstName + "\n\t" +
				"lastName: " + lastName + "\n\t" +
				"mail: " + mail + "\n\t" +
				"roles: " + roles + "\n\t" +
				"remoteUser: " + remoteUser + "\n\t" +
				"eppn_val: " + eppn_val + "\n\t" +
				"persistent_id_val: " + persistent_id_val + "\n\t" +
				"primary role: " + role + "\n\t");
		HttpSession session = request.getSession(true);
		if (session.isNew() == false) {
			session.invalidate();
			session = request.getSession(true);
		}  
		try {
			JSONObject obj = new JSONObject();
			session.setAttribute("user", username);
			session.setAttribute("displayName", displayName);
			session.setAttribute("firstName", firstName);
			session.setAttribute("lastName", lastName);
			session.setAttribute("mail", mail);
			session.setAttribute("roles", userRoles);
			session.setAttribute("role", role);
			JakartaClient client = new JakartaClient(4, 8192, 120000);
			session.setAttribute("httpClient", client);
			obj.put("status", "success");
			//obj.put("mail", mail);
			obj.put("mail", displayName);
			obj.put("user", username);
			obj.put("role", roles);
			JSONArray description = new JSONArray();
			description.put(Utils.profileDescription[0]);
			description.put(Utils.profileDescription[1]);
			description.put(Utils.profileDescription[2]);
			obj.put("description", description);
			PrintWriter out = response.getWriter();
			out.print(obj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
