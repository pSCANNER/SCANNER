package edu.isi.misd.scanner.network.base.utils;

import org.apache.camel.Exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility functions for common configuration tasks.
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
}
