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
package edu.isi.misd.scanner.network.modules.worker.processors.oceans; 

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ConfigUtils;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.base.DataSetMetadata;
import edu.isi.misd.scanner.network.types.base.FileDataSource;
import edu.isi.misd.scanner.network.types.base.ServiceResponse;
import edu.isi.misd.scanner.network.types.base.ServiceRequestStateType;
import edu.isi.misd.scanner.network.types.base.ServiceResponseData;
import edu.isi.misd.scanner.network.types.regression.Coefficient;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionOutput;
import edu.isi.misd.scanner.network.types.oceans.OceansLogisticRegressionRequest;
import edu.isi.misd.scanner.network.types.oceans.OceansLogisticRegressionResponse;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionInputParameters;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionResponse;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionVariable;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionVariableType;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionVariables;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionDataSetMetadata;
import edu.isi.misd.scanner.network.types.regression.LogisticRegressionInput;
import edu.vanderbilt.oceans.core.InstanceBuilder;
import edu.vanderbilt.oceans.statistics.analysis.LogisticRegressionAnalysis;
import edu.vanderbilt.oceans.statistics.analysis.LogisticRegressionAnalysis.LogisticRegressionCoefficient;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class uses the OCEANS Java library to run a logistic regression analysis 
 * on the data source specified as the value of the 
 * {@link BaseConstants#DATASOURCE} header.
 *
 * @author Mike D'Arcy 
 */
public class OceansLogisticRegressionProcessor implements Processor
{
    private static Log log =
        LogFactory.getLog(OceansLogisticRegressionProcessor.class.getName());
        
    /**
     * Camel {@link org.apache.camel.Processor} implementation,
     * invokes {@link OceansLogisticRegressionProcessor#executeAnalysis(org.apache.camel.Exchange)}.
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
                    "Unhandled exception during OCEANS processing. Caused by [" + 
                    e.toString() + "]");
            ErrorUtils.setHttpError(exchange, rtex, 500);
            log.warn("Unhandled exception during OCEANS processing: ", e);
        }                
    }
    
    /**
     * Performs the statistical analysis (logistic regression) using OCEANS.
     * 
     * @param exchange The current exchange.
     * @return The formatted response.
     * @throws Exception 
     */    
    private ServiceResponse executeAnalysis(Exchange exchange) 
        throws Exception
    {
        Calendar start = Calendar.getInstance();
        Calendar end;
        ServiceRequestStateType requestStatus = 
            ServiceRequestStateType.PROCESSING;
        String requestStatusDetail = BaseConstants.STATUS_PROCESSING;
        
        // Create the service response object
        ServiceResponse response = 
            new ServiceResponse(); 
        
        // Create the service response data object
        ServiceResponseData responseData = 
            new ServiceResponseData();         
            
        // Create the LogisticRegressionResponse object
        LogisticRegressionResponse lrResponse = 
            new LogisticRegressionResponse();                 
        
        // Create the OCEANS specific response object, and then set the default 
        // response to the new LogisticRegressionResponse object declared above
        OceansLogisticRegressionResponse oceansResponse = 
            new OceansLogisticRegressionResponse();
        oceansResponse.setLogisticRegressionResponse(lrResponse);                
      
        String uniqueID = MessageUtils.getID(exchange);        
        try 
        {             
            OceansLogisticRegressionRequest request = 
                (OceansLogisticRegressionRequest)
                    exchange.getIn().getBody(
                        OceansLogisticRegressionRequest.class); 
                               
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

            // get the input parameters for the oceans lr analysis   
            LogisticRegressionInput input = 
                request.getLogisticRegressionInput();          
            LogisticRegressionInputParameters params = 
                input.getInputParameters();
            // copy the input parameters as part of the response            
            lrResponse.setInput(input);                          
            // assemble the dependent and independent variables
            String dependentVariableName = params.getDependentVariableName();
            ArrayList<String> independentVariables = 
                new ArrayList<>(params.getIndependentVariableName());    
            
            // complete the response up until the analysis execution point, 
            // from here it will be returned regardless of any exception caught
            responseData.setAny(oceansResponse);
            
            // load the file and run the analysis            
            InstanceBuilder instBuilder = 
                new InstanceBuilder(file, dependentVariableName);

            LogisticRegressionAnalysis lrAnalysis = 
                new LogisticRegressionAnalysis(
                    instBuilder.getInstances(), dependentVariableName);

            lrAnalysis.setUniqueID(uniqueID);
            lrAnalysis.setIndependentVariableNames(independentVariables);
            lrAnalysis.Analyze();

            // populate the output results if successful       
            LogisticRegressionOutput output = new LogisticRegressionOutput();             
            this.formatCoefficientResultsObjects(
                lrAnalysis.getLRCoefficients(),
                output.getCoefficient());         
            lrResponse.getOutput().add(output); 
                   
            requestStatus = ServiceRequestStateType.COMPLETE;
            requestStatusDetail = BaseConstants.STATUS_COMPLETE;
            response.setServiceResponseData(responseData);                  
        }
        catch (Exception e) 
        {       
            requestStatus = ServiceRequestStateType.ERROR;
            requestStatusDetail = 
                "Unhandled exception during OCEANS processing. Caused by [" 
                        + e.toString() + "]";                           
        } 
        finally 
        {
            end = Calendar.getInstance();
            response.setServiceResponseMetadata(
                MessageUtils.createServiceResponseMetadata(
                    exchange, 
                    requestStatus,
                    requestStatusDetail,
                    MessageUtils.formatEventDuration(start, end)));             
        }
        return response;    
    }
    
    private void formatCoefficientResultsObjects(
        List<LogisticRegressionCoefficient> source, 
        List<Coefficient> target)
    {
        for (LogisticRegressionCoefficient c: source) 
        {
            Coefficient coefficient = new Coefficient();
            coefficient.setName(c.getName());
            coefficient.setB(df(c.getEstimate()));
            coefficient.setPValue(df(c.getPValue()));
            coefficient.setDegreeOfFreedom(c.getDegreeOfFreedom());
            coefficient.setSE(df(c.getStandardError()));
            coefficient.setTStatistics(df(c.getTestStatistics()));
            target.add(coefficient);
        }        
    }   

    /**
     * Formats a double to a fixed (currently three) number of decimal places.
     */    
    public static double df(double a)
    {
        DecimalFormat f = new DecimalFormat(".000");
        Double input = Double.valueOf(a);
        // check for special values so that they are not parsed
        if (input.equals(Double.NEGATIVE_INFINITY) ||
            input.equals(Double.POSITIVE_INFINITY) || 
            input.equals(Double.NaN)) {
            return input.doubleValue();
        }
        return Double.parseDouble(f.format(input));
    }
    
    // The hardcoding of values returned is just here for testing.  
    // It will be replaced by reading the local registry for metadata 

    private LogisticRegressionDataSetMetadata getMetadata()
    {
        LogisticRegressionDataSetMetadata metadata = 
            new LogisticRegressionDataSetMetadata();        
        LogisticRegressionVariables variables = 
            new LogisticRegressionVariables();
        metadata.setVariables(variables);        
        
        FileDataSource fileDataSource = new FileDataSource();
        fileDataSource.setFileType("csv");
        fileDataSource.setFilePath("/path/to/file.csv");
        fileDataSource.setDescription("Fake file");
        fileDataSource.setType("edu.isi.misd.scanner.network.types.base.FileDataSource");
        metadata.setDataSource(fileDataSource);
        metadata.setDataSetName("Fake Dataset Name");
        metadata.setDataSetID("001");
        
        LogisticRegressionVariable age = new LogisticRegressionVariable();
        age.setName("age");
        age.setType(LogisticRegressionVariableType.CONTINUOUS);
        variables.getVariable().add(age);
        LogisticRegressionVariable race_cat = new LogisticRegressionVariable();
        race_cat.setName("race_cat");
        race_cat.setType(LogisticRegressionVariableType.CATEGORICAL);
        variables.getVariable().add(race_cat);
        LogisticRegressionVariable creatinine = new LogisticRegressionVariable();
        creatinine.setName("creatinine");
        creatinine.setType(LogisticRegressionVariableType.CONTINUOUS);
        variables.getVariable().add(creatinine);
        LogisticRegressionVariable diabetes = new LogisticRegressionVariable();
        diabetes.setName("diabetes");
        diabetes.setType(LogisticRegressionVariableType.CONTINUOUS);
        variables.getVariable().add(diabetes);
        LogisticRegressionVariable cad = new LogisticRegressionVariable();
        cad.setName("cad");
        cad.setType(LogisticRegressionVariableType.CONTINUOUS);
        variables.getVariable().add(cad);        
        LogisticRegressionVariable los = new LogisticRegressionVariable();
        los.setName("los");
        los.setType(LogisticRegressionVariableType.CONTINUOUS);
        variables.getVariable().add(los); 
        LogisticRegressionVariable outcome = new LogisticRegressionVariable();
        outcome.setName("outcome");
        outcome.setType(LogisticRegressionVariableType.CONTINUOUS);
        variables.getVariable().add(outcome); 
        
        return metadata;
    }
}
