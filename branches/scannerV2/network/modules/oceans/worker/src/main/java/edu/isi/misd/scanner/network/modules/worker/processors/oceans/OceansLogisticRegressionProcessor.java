/*
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
import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class uses the OCEANS Java library to run a logistic regression analysis 
 * on the data source specified as the value of the 
 * {@link BaseConstants#DATASOURCE} header.
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
                new ArrayList(params.getIndependentVariableName());    
            
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
            lrResponse.setOutput(output); 
            
            response.setServiceResponseMetadata(
                MessageUtils.createServiceResponseMetadata(
                    exchange, 
                    ServiceRequestStateType.COMPLETE,
                    BaseConstants.STATUS_COMPLETE)); 
            response.setServiceResponseData(responseData);                  
        }
        catch (Exception e) 
        {    
            response.setServiceResponseMetadata(
                MessageUtils.createServiceResponseMetadata(
                    exchange, 
                    ServiceRequestStateType.ERROR,
                    "Unhandled exception during OCEANS processing. Caused by [" 
                        + e.toString() + "]"));                            
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
            coefficient.setB(c.getEstimate());
            coefficient.setPValue(c.getPValue());
            coefficient.setDegreeOfFreedom(c.getDegreeOfFreedom());
            coefficient.setSE(c.getStandardError());
            coefficient.setTStatistics(c.getTestStatistics());
            target.add(coefficient);
        }        
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
