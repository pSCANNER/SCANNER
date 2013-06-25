/* Copyright (c) 2012 University of Southern California. All Rights Reserved. */

package edu.isi.misd.scanner.network.master;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.spring.Main;

/**
 * This class provides the executable entry point for the Master node of the 
 * SCANNER network.  It can take a single argument "cfg [filename]", which 
 * points to a supplemental properties file containing additional runtime options.  
 * Currently, this argument is not used in the actual network deployment, though
 * it is included for expansion.
 */
public class Master extends Main
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(Master.class); 

    protected CamelContext camelContext = null;
    protected String propertiesFile = null;
    protected Properties properties = null;

    public Master()
    {
        super.addOption(
            new ParameterOption(
            "cfg", "config", "Configuration properties file to use", "filename")
        {
            @Override
            protected void doProcess(
                String arg, String parameter, LinkedList<String> remainingArgs) 
            {
                setPropertiesFile(parameter);
            }
        });
    }

    public String getPropertiesFile() {
        return this.propertiesFile;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    /**
     * The main entry point.  Wraps the Camel Spring-based executable startup 
     * sequence of {@link org.apache.camel.spring.Main}.
     * @param args
     * @throws Exception
     */
    public static void main(String... args) throws Exception
    {
        Master main = new Master();
        instance = main;
        main.enableHangupSupport();
        main.run(args);
    }

    /**
     * @see org.apache.camel.spring.Main#doStart() 
     * @throws Exception
     */
    @Override
    protected void doStart() throws Exception
    {
        super.doStart();
        this.startEventProcessor();
    }

    /**
     * @see org.apache.camel.spring.Main#doStop() 
     * @throws Exception
     */
    @Override
    protected void doStop() throws Exception
    {
        try
        {
            // extra shutdown activity
        } catch (Exception e) {
            log.warn("Shutdown exception: ",e);
        }
        super.doStop();
    }

	/**
	 * Reads the supplemental properties file (if provided) and adds them to 
     * the current {@link CamelContext} before completing the startup sequence.
	 * @exception	Exception
     * @exception	IOException
	 */
    public void startEventProcessor()
        throws Exception,
               IOException
    {
        this.properties = new Properties();

        if (this.propertiesFile != null)
        {
            try
            {
                FileInputStream in = new FileInputStream(this.propertiesFile);
                properties.load(in);
                in.close();
            }
            catch (IOException e)
            {
                log.warn(
                    "Could not load configuration properties file: " +
                    this.propertiesFile, e);
            }
        } 

        if (log.isDebugEnabled()) {
            log.debug("Startup properties="+properties);
        }

        if (getCamelContexts().isEmpty()) {
            throw new IllegalArgumentException(
                "No CamelContexts are configured!");
        } else {
            this.camelContext = getCamelContexts().get(0);
            Map contextProps = this.camelContext.getProperties();
            contextProps.putAll(new HashMap(properties));   
            
            HttpComponent httpComponent = new HttpComponent(); 
            httpComponent.setConnectionsPerRoute(60);
            httpComponent.setMaxTotalConnections(600);
            this.camelContext.addComponent("http4", httpComponent); 
            httpComponent.doStart();
            
            HttpComponent httpsComponent = new HttpComponent(); 
            httpsComponent.setConnectionsPerRoute(60);
            httpsComponent.setMaxTotalConnections(600);
            this.camelContext.addComponent("https4", httpsComponent);
            httpsComponent.doStart();
        }
        
        try
        {
            // launch additional threads / do other stuff here if needed
            log.info("Startup complete");
        }
        catch (Exception e)
        {
            log.error("Startup exception: ", e);
            if (log.isDebugEnabled()) {
               e.printStackTrace();
            }
        }

    }
}
