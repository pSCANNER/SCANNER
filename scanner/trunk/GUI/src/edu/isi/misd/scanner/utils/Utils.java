/**
 * 
 */
package edu.isi.misd.scanner.utils;

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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Serban Voinea
 *
 */
public class Utils {
    private static final String UTF_8 = "UTF-8";

    /**
     * 
     * @param datasetName
     *            the string to encode
     * @return a URL-safe version of the string
     */
    public static String urlEncode(String datasetName)
            throws UnsupportedEncodingException {
        if (datasetName == null || datasetName.length() == 0) throw new IllegalArgumentException(datasetName);
        datasetName = URLEncoder.encode(datasetName, UTF_8);

        return datasetName;
    }
    
    public static JSONArray getOceansParameters() {
    	JSONArray ret = new JSONArray();
    	try {
        	JSONObject param = new JSONObject();
			param.put("name", "dependentVariableName");
			ret.put(param);
        	param = new JSONObject();
			param.put("name", "independentVariableNames");
			param.put("minOccurs", 0);
			param.put("maxOccurs", "unbounded");
			ret.put(param);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
    }
    
    public static boolean login(HttpServletRequest request, HttpServletResponse response, String user, String password) {
    	boolean ret = false;
    	if (user.equals("guest") && password.equals("just4you")) {
    		ret = true;
    	}
    	return ret;
    }
    
    public static String extractId(String value) {
		String ret = value;
    	int index = value.lastIndexOf("/");
		if (index != -1) {
			ret = ret.substring(index+1);
		}
		return ret;
    }

    public static String getEntity(String value) {
		return (value != null) ? value : "";
    }

    public static String oceansResult = "" +
    "{" +
    "\"OceansLogisticRegressionResults\": [" +
      "{" +
        "\"Coefficients\": [" +
          "{" +
            "\"Name\": \"intercept\"," +
            "\"Estimate\": \"-49.17319326200402\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"age\"," +
            "\"Estimate\": \"-0.0015326074927496935\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"race_cat\"," +
            "\"Estimate\": \"-0.09121409300196073\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"creatinine\"," +
            "\"Estimate\": \"6.511528405950525\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"cad\"," +
            "\"Estimate\": \"3.966338828149793\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"los\"," +
            "\"Estimate\": \"4.080943065244493\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"diabetes\"," +
            "\"Estimate\": \"1.6001542228424734\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}" +
        "]" +
      "}," +
      "{" +
        "\"Coefficients\": [" +
          "{" +
            "\"Name\": \"intercept\"," +
            "\"Estimate\": \"-49.003834917368614\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"age\"," +
            "\"Estimate\": \"0.002101516293679945\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"race_cat\"," +
            "\"Estimate\": \"-0.16047351645369307\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"creatinine\"," +
            "\"Estimate\": \"6.89574870531013\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"cad\"," +
            "\"Estimate\": \"4.015949011951625\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"los\"," +
            "\"Estimate\": \"3.42355259219059\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"diabetes\"," +
            "\"Estimate\": \"1.5550061118245901\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}" +
        "]" +
      "}," +
      "{" +
        "\"Coefficients\": [" +
          "{" +
            "\"Name\": \"intercept\"," +
            "\"Estimate\": \"-49.003834917368614\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"age\"," +
            "\"Estimate\": \"0.002101516293679945\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"race_cat\"," +
            "\"Estimate\": \"-0.16047351645369307\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"creatinine\"," +
            "\"Estimate\": \"6.89574870531013\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"cad\"," +
            "\"Estimate\": \"4.015949011951625\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"los\"," +
            "\"Estimate\": \"3.42355259219059\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"diabetes\"," +
            "\"Estimate\": \"1.5550061118245901\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}" +
        "]" +
      "}," +
      "{" +
        "\"Coefficients\": [" +
          "{" +
            "\"Name\": \"intercept\"," +
            "\"Estimate\": \"-49.17319326200402\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"age\"," +
            "\"Estimate\": \"-0.0015326074927496935\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"race_cat\"," +
            "\"Estimate\": \"-0.09121409300196073\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"creatinine\"," +
            "\"Estimate\": \"6.511528405950525\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"cad\"," +
            "\"Estimate\": \"3.966338828149793\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"los\"," +
            "\"Estimate\": \"4.080943065244493\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}," +
          "{" +
            "\"Name\": \"diabetes\"," +
            "\"Estimate\": \"1.6001542228424734\"," +
            "\"DegreeOfFreedom\": \"0\"," +
            "\"StandardError\": \"0.0\"," +
            "\"PValue\": \"0.0\"" +
          "}" +
        "]" +
      "}" +
    "]" +
  "}" +
    "";
}
