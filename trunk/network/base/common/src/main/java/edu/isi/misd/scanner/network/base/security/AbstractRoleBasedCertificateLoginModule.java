package edu.isi.misd.scanner.network.base.security;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.eclipse.jetty.plus.jaas.JAASPrincipal;
import org.eclipse.jetty.plus.jaas.JAASRole;
import org.eclipse.jetty.plus.jaas.callback.ObjectCallback;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  An abstract class that implements a Jetty JAAS login module.
 */
public abstract class AbstractRoleBasedCertificateLoginModule implements LoginModule 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(AbstractRoleBasedCertificateLoginModule.class);
    
    public abstract List<String> getRolesForUser(String user) throws Exception;
    
    private CallbackHandler callbackHandler;
    
    private boolean authState = false;
    private boolean commitState = false;
    private JAASUserInfo currentUser;
    private Subject subject;
    private Map<String,?> options;
    private static final String HOST_AUTHZ = 
        "edu.isi.misd.scanner.network.base.security.HostAuthorization";   

    public class JAASUserInfo
    {
        private String userName;
        private Principal principal;
        private List<JAASRole> roles;
              
        public JAASUserInfo (String user, List<String> roleNames)
        {
            setUserRoles(user,roleNames);
        }
        
        public String getUserName ()
        {
            return this.userName;
        }
        
        public Principal getPrincipal()
        {
            return this.principal;
        }
        
        public void setUserRoles (String userName, List<String> roleNames)
        {
            this.userName = userName;
            this.principal = new JAASPrincipal(this.userName);
            this.roles = new ArrayList<JAASRole>();
            if (roleNames != null)
            {
                Iterator<String> iter = roleNames.iterator();
                while (iter.hasNext()) {
                    this.roles.add(new JAASRole((String)iter.next()));
                }
            }
        }
               
        public void setJAASInfo (Subject subject)
        {
            subject.getPrincipals().add(this.principal);
            subject.getPrincipals().addAll(roles);
        }
        
        public void unsetJAASInfo (Subject subject)
        {
            subject.getPrincipals().remove(this.principal);
            subject.getPrincipals().removeAll(this.roles);
        }
    }
   
    public Map<String,?> getOptions() {
        return this.options;
    }
    
    public Subject getSubject ()
    {
        return this.subject;
    }
    
    public void setSubject (Subject s)
    {
        this.subject = s;
    }
    
    public JAASUserInfo getCurrentUser()
    {
        return this.currentUser;
    }
    
    public void setCurrentUser (JAASUserInfo u)
    {
        this.currentUser = u;
    }
    
    public CallbackHandler getCallbackHandler()
    {
        return this.callbackHandler;
    }
    
    public void setCallbackHandler(CallbackHandler h)
    {
        this.callbackHandler = h; 
    }
    
    public boolean isAuthenticated()
    {
        return this.authState;
    }
    
    public boolean isCommitted ()
    {
        return this.commitState;
    }
    
    public void setAuthenticated (boolean authState)
    {
        this.authState = authState;
    }
    
    public void setCommitted (boolean commitState)
    {
        this.commitState = commitState;
    }
    
    public Callback[] configureCallbacks ()
    {
     
        Callback[] callbacks = new Callback[2];
        callbacks[0] = new NameCallback("Username");
        callbacks[1] = new ObjectCallback();
        return callbacks;
    }
    
    private String getCommonNameFromDN(String dn)
    {
        String cn = "CN=";
        StringTokenizer tokenizer = new StringTokenizer(dn, ",");
        String result = null;
        while (tokenizer.hasMoreTokens())
        {
            int len = cn.length();

            String token = (String) tokenizer.nextToken();
            if (token.toUpperCase().startsWith(cn))
            {
                // Make sure the token actually contains something
                if (token.length() <= len) {
                    return null;
                }
                result = token.substring(len);
                break;
            }
        }
        return result;
    }
    
    protected void doHostAuthorization(String subjectDN) throws Exception
    {
        if (this.getOptions() == null) {
            return;
        }
        if (!Boolean.parseBoolean((String)options.get(HOST_AUTHZ))) {
            return;
        }            
            
        AbstractHttpConnection connection = 
            AbstractHttpConnection.getCurrentConnection();
        Request request = (connection == null? null : connection.getRequest());
        if (request != null)
        {
            String remoteHost = request.getRemoteHost();
            String remoteAddress = request.getRemoteAddr();                
            /* do a reverse lookup to get the hostname */
            try {
                InetAddress i = InetAddress.getByName(remoteHost);
                remoteHost =
                    InetAddress.getByName(
                        i.getHostAddress()).getCanonicalHostName();
            } catch (UnknownHostException e) {
                throw new Exception(
                    "Unable to resolve hostname " + 
                    remoteHost + " " + e.toString());
            }                
            log.info("Attempting to authorize request from host " +
                     remoteHost + " (" + remoteAddress + ")" + 
                     " using certificate DN: " + subjectDN); 
            String certCN = this.getCommonNameFromDN(subjectDN);
            if (certCN == null) {
                throw new LoginException(
                    "Unable to parse CN from DN: " + subjectDN);
            }
            if (!certCN.equalsIgnoreCase(remoteHost)) {
                throw new LoginException(
                    "Client certificate hostname (" + certCN + 
                    ") does not match remote host name of client (" + 
                    remoteHost + ")");
            }
        }         
    }
    
    /** 
     * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
     * @param subject
     * @param callbackHandler
     * @param sharedState
     * @param options
     */
    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler,
            Map<String,?> sharedState, Map<String,?> options)
    {
        this.callbackHandler = callbackHandler;
        this.subject = subject;
        this.options = options;       
    }
    
    /** 
     * @see javax.security.auth.spi.LoginModule#abort()
     * @throws LoginException
     */
    @Override
    public boolean abort() throws LoginException
    {
        this.currentUser = null;
        return (isAuthenticated() && isCommitted());
    }

    /** 
     * @see javax.security.auth.spi.LoginModule#commit()
     * @return true if committed, false if not (likely not authenticated)
     * @throws LoginException
     */
    @Override
    public boolean commit() throws LoginException
    {

        if (!isAuthenticated())
        {
            currentUser = null;
            setCommitted(false);
            return false;
        }
        
        setCommitted(true);
        currentUser.setJAASInfo(subject);
        return true;
    }    
    
    /** 
     * @see javax.security.auth.spi.LoginModule#login()
     * @return true if is authenticated, false otherwise
     * @throws LoginException
     */
    @Override
    public boolean login() throws LoginException
    {
        try
        {  
            if (callbackHandler == null) {
                throw new LoginException ("No callback handler");
            }
            Callback[] callbacks = configureCallbacks();
            callbackHandler.handle(callbacks);

            String subjectDN = ((NameCallback)callbacks[0]).getName();
            Object credential = ((ObjectCallback)callbacks[1]).getObject(); 
            if (credential == null)
            {
                throw new LoginException ("No credential supplied");
            }         
 
            this.doHostAuthorization(subjectDN);
            
            List<String> roleNames = getRolesForUser(subjectDN);            
            if (roleNames == null)
            {
                throw new LoginException(
                    "Unable to determine any configured roles for: " + subjectDN);
            }
            
            currentUser = new JAASUserInfo(subjectDN, roleNames);
            setAuthenticated(true);
            return isAuthenticated();
        }
        catch (LoginException e)
        {
            throw e;
        }         
        catch (IOException e)
        {
            throw new LoginException (e.toString());
        }
        catch (UnsupportedCallbackException e)
        {
            throw new LoginException (e.toString());
        }       
        catch (Exception e)
        {
            throw new LoginException (e.toString());
        }
    }

    /** 
     * @see javax.security.auth.spi.LoginModule#logout()
     * @return true always
     * @throws LoginException
     */
    @Override
    public boolean logout() throws LoginException
    {
        this.currentUser.unsetJAASInfo(this.subject);
        return true;
    }

}