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
 *
 */
public class TextFileRoleBasedCertLoginModule 
    extends AbstractRoleBasedCertificateLoginModule
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(TextFileRoleBasedCertLoginModule.class);
        
    private static final String ROLES_FILE = 
        "edu.isi.misd.scanner.network.base.security.roles.map.file";   

    protected Map<String, List<String>> map;

    // the file the map was loaded from
    private File file = null;
    // last time the file was modified
    private long lastModified;
    // log or throw exception on bad entries 
    private boolean ignoreErrors = false;
    
    private static final String COMMENT_CHARS = "#";
    
    public void setIgnoreErrors(boolean ignoreErrors) {
        this.ignoreErrors = ignoreErrors;
    }
    
    public boolean getIgnoreErrors() {
        return this.ignoreErrors;
    }

    public String getFileName() {
        
        if (this.file == null) {
            return null;
        }

        return this.file.getAbsolutePath();
    }

    public boolean load(String file)
        throws IOException 
    {
        return load(new File(file));
    }

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
    
    public boolean load(InputStream input) 
        throws IOException 
    {
        boolean success = true;

        BufferedReader reader = 
            new BufferedReader(new InputStreamReader(input));

        Map<String, List<String>> localMap = 
            new HashMap<String, List<String>>();

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
            ArrayList rolesList = new ArrayList();
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