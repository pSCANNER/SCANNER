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
package edu.isi.misd.scanner.network.base.security;

import edu.isi.misd.scanner.network.base.utils.QuotedStringTokenizer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  This class provides an extension of 
 *  {@link edu.isi.misd.scanner.network.base.security.AbstractRoleBasedCertificateLoginModule} 
 *  that provides the list of configured roles via a text file that maps 
 *  Distinguished Names to a comma-delimited list of role names.
 *  <br/><br/>
 *  Example:
 *  <br/><br/>
 *  {@code  "CN=scanner.misd.isi.edu, OU=SCANNER Test Network, O=SCANNER Project, C=US" authorized,admin }
 * 
 *  @author Mike D'Arcy 
 */
public class TextFileRoleBasedCertLoginModule 
    extends AbstractRoleBasedCertificateLoginModule
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(TextFileRoleBasedCertLoginModule.class);
        
    protected static final String ROLES_FILE = 
        "edu.isi.misd.scanner.network.base.security.roles.map.file";   

    /**
     *  A map of user names to a {@link java.util.List} of roles
     */
    protected Map<String, List<String>> map;

    // the file the map was loaded from
    private File file = null;
    // last time the file was modified
    private long lastModified;
    // log or throw exception on bad entries 
    private boolean ignoreErrors = false;
    
    private static final String COMMENT_CHARS = "#";
    
    /**
     * Whether or not to ignore errors encountered while parsing the role map file.
     */
    public void setIgnoreErrors(boolean ignoreErrors) {
        this.ignoreErrors = ignoreErrors;
    }
    
    /**
     * Whether or not to ignore errors encountered while parsing the role map file.
     */
    public boolean getIgnoreErrors() {
        return this.ignoreErrors;
    }

    /**
     * @see edu.isi.misd.scanner.network.base.security.TextFileRoleBasedCertLoginModule#load(java.lang.String) 
     * 
     * @return The role map file name.
     */
    public String getFileName() {
        
        if (this.file == null) {
            return null;
        }

        return this.file.getAbsolutePath();
    }

    /**
     * Reread the map file and refresh the map.
     * 
     * @return Whether the file is current or not
     * @throws IOException
     */
    public boolean refresh() 
        throws IOException 
    {
        if (this.file != null &&
            this.file.lastModified() != this.lastModified) {
            return load(this.file);
        } else {
            return true;
        }
    }
    
    /**
     * Reads and parses the roles file.
     * @see edu.isi.misd.scanner.network.base.security.TextFileRoleBasedCertLoginModule#load(java.io.File) 
     * @param file The file path
     * @throws IOException
     */
    public boolean load(String file)
        throws IOException 
    {
        return load(new File(file));
    }

    /**
     * Reads and parses the roles file.      
     * @see edu.isi.misd.scanner.network.base.security.TextFileRoleBasedCertLoginModule#load(java.io.InputStream) 
     * @param file The file to load
     * @throws IOException
     */
    public boolean load(File file)
        throws IOException 
    {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            this.file = file;
            this.lastModified = file.lastModified();
            return load(in);
        } finally {
            if (in != null) {
                try { in.close(); } catch(Exception e) {}
            }
        }
    }
    
    /**
     * Reads and parses the roles file.
     * @param input The input stream of the file
     * @return If the file was parsed successfully or not.
     * @throws IOException
     */
    public boolean load(InputStream input) 
        throws IOException 
    {
        boolean success = true;

        BufferedReader reader = 
            new BufferedReader(new InputStreamReader(input));

        Map<String, List<String>> localMap = new HashMap<>();

        QuotedStringTokenizer dnTokenizer;
        StringTokenizer roleTokenizer;
        String line;
        while( (line = reader.readLine()) != null) 
        {
            line = line.trim();
            if ( (line.length() == 0) ||
                 ( COMMENT_CHARS.indexOf(line.charAt(0)) != -1) ) {
                continue;
            }
            
            dnTokenizer = new QuotedStringTokenizer(line);

            String userDN = null;

            if (dnTokenizer.hasMoreTokens()) {
                userDN = dnTokenizer.nextToken();
            } else {
                if (this.ignoreErrors) {
                    success = false;
                    log.warn("User DN missing: " + line);
                    continue;
                } else {
                    throw new IOException("User DN missing: " + line);
                }
            }

            String roles = null;
            if (dnTokenizer.hasMoreTokens()) {
                roles = dnTokenizer.nextToken();
            } else {
                if (this.ignoreErrors) {
                    success = false;
                    log.warn("Roles mapping missing: " + line);
                    continue;
                } else {
                    throw new IOException("Roles mapping missing: " + line);
                }
            }

            roleTokenizer = new StringTokenizer(roles, ",");
            ArrayList<String> rolesList = new ArrayList<>();
            while(roleTokenizer.hasMoreTokens()) {
                rolesList.add(roleTokenizer.nextToken());
            }
            
            localMap.put(userDN, rolesList);
        }
        
        this.map = localMap;
        if (log.isDebugEnabled()) {
            log.debug("Current role mappings:" + this.map);
        }
        return success;
    }
    
    /**
     * Gets the list of roles for the passed in user, based on the current state of the map.
     * @param user The userName
     * @return The list of roles for the user 
     * @throws Exception
     */    
    @Override
    public List<String> getRolesForUser(String user) throws Exception 
    {
        if (this.file != null) {
            this.refresh();
        } else {
            Map<String,?> options = this.getOptions();
            if (options == null) {
                throw new Exception("Roles file name cannot be null or unspecified");
            }
            String rolesFilePathname = (String)options.get(ROLES_FILE);            
            this.load(rolesFilePathname);
        }
        List<String> roles = map.get(user);
        log.info("Role check for user \"" + user + 
                 "\"" + ", current roles: " + roles);        
        return roles;
    }
}