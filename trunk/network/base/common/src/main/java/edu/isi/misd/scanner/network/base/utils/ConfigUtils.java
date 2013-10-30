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
package edu.isi.misd.scanner.network.base.utils;

import org.apache.camel.Exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for common configuration tasks.
 *
 * @author Mike D'Arcy 
 */
public class ConfigUtils 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(ConfigUtils.class);
    
    /**
     * Gets the configured base input directory, or simply {@code data/input} relative to the current working directory.
     * 
     * @param exchange The current exchange
     * @param baseInputDirPropName The property name to resolve the directory path from
     */
    public static String getBaseInputDir(Exchange exchange,  
                                         String baseInputDirPropName)
    {
        String baseInputDir = null;
        try {
            baseInputDir = 
                exchange.getContext().resolvePropertyPlaceholders(
                    baseInputDirPropName); 
        } catch (Exception e) {
            log.debug("Unable to resolve property: " + baseInputDirPropName,e);
        } finally {
            if (baseInputDir == null) {
                baseInputDir = "data/input";
            }
        }
        return baseInputDir;
    }  
    
    /**
     * Gets the configured base output directory, or simply {@code data/output} relative to the current working directory.
     * 
     * @param exchange The current exchange
     * @param baseOutputDirPropName The property name to resolve the directory path from
     */
    public static String getBaseOutputDir(Exchange exchange, 
                                          String baseOutputDirPropName)
    {
        String baseOutputDir = null;
        try {
            baseOutputDir = 
                exchange.getContext().resolvePropertyPlaceholders(
                    baseOutputDirPropName); 
        } catch (Exception e) {
            log.debug("Unable to resolve property: " + baseOutputDirPropName,e);
        } finally {
            if (baseOutputDir == null) {
                baseOutputDir = "data/output";
            }
        }
        return baseOutputDir;        
    }       
    
    /**
     * Gets the configured base output holding directory, or simply {@code data/holding} relative to the current working directory.
     * 
     * @param exchange The current exchange
     * @param baseOutputDirPropName The property name to resolve the directory path from
     */
    public static String getBaseHoldingDir(Exchange exchange, 
                                          String baseHoldingDirPropName)
    {
        String baseOutputDir = null;
        try {
            baseOutputDir = 
                exchange.getContext().resolvePropertyPlaceholders(
                    baseHoldingDirPropName); 
        } catch (Exception e) {
            log.debug("Unable to resolve property: " + baseHoldingDirPropName,e);
        } finally {
            if (baseOutputDir == null) {
                baseOutputDir = "data/holding";
            }
        }
        return baseOutputDir;        
    }  
    
}
