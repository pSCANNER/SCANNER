package edu.isi.misd.scanner.network.base.utils;

import org.apache.camel.Exchange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ConfigUtils 
{
    private static final transient Logger log = 
        LoggerFactory.getLogger(ConfigUtils.class);
    
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
