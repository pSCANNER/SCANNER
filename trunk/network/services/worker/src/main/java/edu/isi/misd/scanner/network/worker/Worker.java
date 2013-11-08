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
package edu.isi.misd.scanner.network.worker; 

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.Main;

/**
 * This class provides the executable entry point for the Worker node of the 
 * SCANNER network.  It can take a single argument "cfg [filename]", which 
 * points to a supplemental properties file containing additional runtime options.  
 * Currently, this argument is not used in the actual network deployment, though
 * it is included for expansion.
 *
 * @author Mike D'Arcy 
 */
public class Worker extends Main
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(Worker.class); 

    protected CamelContext camelContext = null;
    protected String propertiesFile = null;
    protected Properties properties = null;
    
    public Worker()
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
        Worker main = new Worker();
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
            Map<String,String> contextProps = this.camelContext.getProperties();
            for (Map.Entry property : properties.entrySet()) {
                contextProps.put(
                    property.getKey().toString(),
                    property.getValue().toString());
            }       
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
