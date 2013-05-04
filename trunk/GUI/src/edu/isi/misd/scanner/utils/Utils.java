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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that provides utilities functions.
 * 
 * @author Serban Voinea
 * 
 */
public class Utils {
    private static final String UTF_8 = "UTF-8";

    /**
     * Encodes URL a string.
     * 
     * @param value
     *            the string to encode.
     * @return A URL-safe version of the string.
     */
    public static String urlEncode(String value)
            throws UnsupportedEncodingException {
        if (value == null || value.length() == 0) throw new IllegalArgumentException(value);
        value = URLEncoder.encode(value, UTF_8);

        return value;
    }
    
    /**
     * Gets the parameters for the OCEANS library.
     * 
     * @return A JSONArray with the parameters description.
     */
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
			e.printStackTrace();
		}
		return ret;
    }
    
    /**
     * Extracts the "id" path of the result.
     * 
     * @param value
     *            the path of the result.
     * @return The "id" path of the result.
     */
    public static String extractId(String value) {
		String ret = value;
    	int index = value.lastIndexOf("/");
		if (index != -1) {
			ret = ret.substring(index+1);
		}
		return ret;
    }

    /**
     * This method is supplied for convenience to convert a NULL value to an empty string.
     * 
     * @param value
     *            the string value.
     * @return The string value or the empty string.
     */
    public static String getEntity(String value) {
		return (value != null) ? value : "";
    }
    
    /**
     * Mockup for a profile description.
     * 
     */
    public static String[] profileDescription = {
    	"",
    	"",
    	"",
    	"More stuff...",
    	"Furthermore, access to each data set is limited to study investigators as specified in an Institutional Review Board (IRB) approvals database, and the user must belong to one of the study sites and be authenticated by that site's enterprise authentication services.",
    	"SCANNER is supported by the Agency for Healthcare Research and Quality (AHRQ) through the American Recovery & Reinvestment Act of 2009, Grant R01 HS19913-01."
    };
    
}
