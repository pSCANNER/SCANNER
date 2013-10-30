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
package edu.isi.misd.scanner.network.modules.worker.processors.ptr; 

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ConfigUtils;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.base.ServiceResponse;
import edu.isi.misd.scanner.network.types.base.ServiceRequestStateType;
import edu.isi.misd.scanner.network.types.base.ServiceResponseData;
import edu.isi.misd.scanner.network.types.ptr.PrepToResearchRequest;
import edu.isi.misd.scanner.network.types.ptr.PrepToResearchRecord;
import edu.isi.misd.scanner.network.types.ptr.PrepToResearchResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class prepares Prep to Research results based
 * on the data source specified as the value of the 
 * {@link BaseConstants#DATASOURCE} header.
 *
 * @author Mike D'Arcy 
 */
public class PrepToResearchProcessor implements Processor
{
    private static Log log =
        LogFactory.getLog(PrepToResearchProcessor.class.getName());               
    
    /**
     * Camel {@link org.apache.camel.Processor} implementation,
     * invokes {@link PrepToResearchProcessor#executeAnalysis(org.apache.camel.Exchange)}.
     */
    @Override
    public void process(Exchange exchange) throws Exception 
    {
        
        try {                      
            exchange.getIn().setBody(this.executeAnalysis(exchange)); 
        }
        catch (Exception e) {
            RuntimeException rtex = 
                new RuntimeException(
                    "Unhandled exception during Prep to Research processing. Caused by [" + 
                    e.toString() + "]");
            ErrorUtils.setHttpError(exchange, rtex, 500);
        }                
    }
    
    /**
     * Performs the Prep to Research analysis.
     * 
     * @param exchange The current exchange.
     * @return The formatted response.
     * @throws Exception 
     */    
    private ServiceResponse executeAnalysis(Exchange exchange) 
        throws Exception
    {
        // Create the service response data object
        ServiceResponseData responseData = new ServiceResponseData();                                          
        
        // Create the service response object
        ServiceResponse response = new ServiceResponse();             
                
        try 
        {             
            PrepToResearchRequest request = 
                (PrepToResearchRequest)
                    exchange.getIn().getBody(PrepToResearchRequest.class); 
                               
            // locate the specified input file
            String fileName = 
                (String)exchange.getIn().getHeader(BaseConstants.DATASOURCE);
            if (fileName == null) {
                FileNotFoundException fnf = 
                    new FileNotFoundException("A null file name was specified");
                throw fnf;
            }        
            String baseInputDir = 
                ConfigUtils.getBaseInputDir(
                    exchange, BaseConstants.WORKER_INPUT_DIR_PROPERTY);   
            File file = new File(baseInputDir,fileName);            
            
            // perform the analysis 
            responseData.setAny(analyzeFile(request,file));
            response.setServiceResponseData(responseData);          
            
            // write the response metadata
            response.setServiceResponseMetadata(
                MessageUtils.createServiceResponseMetadata(
                    exchange, 
                    ServiceRequestStateType.COMPLETE,
                    BaseConstants.STATUS_COMPLETE)); 
              
        }
        catch (Exception e) 
        {    
            response.setServiceResponseMetadata(
                MessageUtils.createServiceResponseMetadata(
                    exchange, 
                    ServiceRequestStateType.ERROR,
                    "Unhandled exception during Prep to Research processing. Caused by [" 
                        + e.toString() + "]"));                            
        }      
        return response;    
    }
    
    public enum ExpectedColumnName 
    {
        OMOP_CONCEPT_ID("omop_concept_id"),
        OMOP_CONCEPT_NAME("omop_concept_name"),
        CATEGORY("category"),
        CATEGORY_VALUE("category_value"),
        COUNT_FEMALES("count_females"),
        COUNT_MALES("count_males"),        
        COUNT_TOTAL("count_total");

        private final String columnName;
        ExpectedColumnName(String columnName) {
            this.columnName = columnName;
        }
        
        public static String[] getAllValues() {
            ArrayList<String> values = new ArrayList<String>();
            for (ExpectedColumnName column : ExpectedColumnName.values()) {
                values.add(column.toString());                
            }
            return values.toArray(new String[0]);
        }
        
        @Override
        public String toString() { return columnName; }         
    }  
    
    private PrepToResearchResponse analyzeFile(
            PrepToResearchRequest request, File analysisFile)
        throws Exception        
    {
        PrepToResearchResponse response = new PrepToResearchResponse();        
        Integer requestedOmopConceptID = request.getOmopConceptID();
        CSVFormat csvFormat = 
            CSVFormat.newFormat(',')
            .withHeader()
            .withCommentStart('#')
            .withQuoteChar('"');
        CSVParser parser = CSVParser.parse(analysisFile,csvFormat);                 
        for (CSVRecord csvRecord : parser)
        {
            try
            {                   
                this.validateCSVRecord(csvRecord);
                
                // check the ID first, if no match continue
                Integer omopConceptID = 
                    Integer.parseInt(
                        csvRecord.get(
                            ExpectedColumnName.OMOP_CONCEPT_ID.toString()));
                if (!requestedOmopConceptID.equals(omopConceptID)) {
                    continue;
                }
                
                // match found, create response output record
                if (log.isDebugEnabled()) {
                    log.debug(String.format(
                        "Found a match for requested ID %s, record: %s",
                        requestedOmopConceptID, csvRecord.toString()));                
                }
                PrepToResearchRecord ptrRecord = new PrepToResearchRecord();
                ptrRecord.setOmopConceptID(omopConceptID);
                ptrRecord.setOmopConceptName(
                    csvRecord.get(ExpectedColumnName.OMOP_CONCEPT_NAME));
                ptrRecord.setCategory(
                    csvRecord.get(ExpectedColumnName.CATEGORY));
                ptrRecord.setCategoryValue(
                    csvRecord.get(ExpectedColumnName.CATEGORY_VALUE));
                ptrRecord.setCountFemales(
                    Integer.parseInt(
                        csvRecord.get(ExpectedColumnName.COUNT_FEMALES)));
                ptrRecord.setCountMales(
                    Integer.parseInt(
                        csvRecord.get(ExpectedColumnName.COUNT_MALES)));
                ptrRecord.setCountTotal(
                    Integer.parseInt(
                        csvRecord.get(ExpectedColumnName.COUNT_TOTAL)));
                
                response.getPrepToResearchRecord().add(ptrRecord);
            }
            catch (Exception e) 
            {
                String error = 
                    String.format(
                        "An exception occured while processing row number %s with the following values %s: %s", 
                        csvRecord.getRecordNumber(),
                        csvRecord.toString(),
                        e.toString());
                parser.close();                
                throw new RuntimeException(error);
            }                 
        } 
        parser.close();         
        return response;
    }
    
    private void validateCSVRecord(CSVRecord csvRecord)
        throws Exception
    {
        String columnNotFound = "Unable to locate required column: ";
        
        if (!csvRecord.isSet(ExpectedColumnName.OMOP_CONCEPT_ID.toString())) {
            throw new Exception(columnNotFound + 
                ExpectedColumnName.OMOP_CONCEPT_ID.toString());
        }      
        if (!csvRecord.isSet(ExpectedColumnName.OMOP_CONCEPT_NAME.toString())) {
            throw new Exception(columnNotFound + 
                ExpectedColumnName.OMOP_CONCEPT_NAME.toString());
        }              
        if (!csvRecord.isSet(ExpectedColumnName.CATEGORY.toString())) {
            throw new Exception(columnNotFound + 
                ExpectedColumnName.CATEGORY.toString());
        }          
        if (!csvRecord.isSet(ExpectedColumnName.CATEGORY_VALUE.toString())) {
            throw new Exception(columnNotFound + 
                ExpectedColumnName.CATEGORY_VALUE.toString());
        }                
        if (!csvRecord.isSet(ExpectedColumnName.COUNT_FEMALES.toString())) {
            throw new Exception(columnNotFound + 
                ExpectedColumnName.COUNT_FEMALES.toString());
        }            
        if (!csvRecord.isSet(ExpectedColumnName.COUNT_MALES.toString())) {
            throw new Exception(columnNotFound + 
                ExpectedColumnName.COUNT_MALES.toString());
        }              
        if (!csvRecord.isSet(ExpectedColumnName.COUNT_TOTAL.toString())) {
            throw new Exception(columnNotFound + 
                ExpectedColumnName.COUNT_TOTAL.toString());
        }         
    }
}