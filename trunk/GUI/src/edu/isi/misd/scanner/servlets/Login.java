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
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		System.out.println("User: \"" + username + "\" logged in.");
		HttpSession session = request.getSession(true);
		if (session.isNew() == false) {
			session.invalidate();
			session = request.getSession(true);
		}  
		boolean valid = Utils.login(request, response, username, password);
		try {
			JSONObject obj = new JSONObject();
			if (valid) {
				session.setAttribute("user", username);
				JakartaClient client = new JakartaClient(4, 8192, 120000);
				session.setAttribute("httpClient", client);
				obj.put("status", "success");
				obj.put("user", username);
				obj.put("role", Utils.getUserRole(username));
				JSONArray description = new JSONArray();
				description.put(Utils.profileDescription[0]);
				description.put(Utils.profileDescription[1]);
				description.put(Utils.profileDescription[2]);
				obj.put("description", description);
			} else {
				obj.put("status", "Invalid userid and/or password.");
			}
			PrintWriter out = response.getWriter();
			out.print(obj.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
