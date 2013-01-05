/*
 */
package edu.isi.misd.scanner.network.modules.worker.processors.oceans;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.ConfigUtils;
import edu.isi.misd.scanner.network.base.utils.ErrorUtils;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import edu.isi.misd.scanner.network.types.oceans.OceansLogisticRegressionCoefficient;
import edu.isi.misd.scanner.network.types.oceans.OceansLogisticRegressionParameters;
import edu.isi.misd.scanner.network.types.oceans.OceansLogisticRegressionResults;
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
 *
 */
public class OceansLogisticRegressionProcessor implements Processor
{
    private static Log log =
        LogFactory.getLog(OceansLogisticRegressionProcessor.class.getName());
        
    @Override
    public void process(Exchange exchange) throws Exception 
    {
        
        try {                      
            exchange.getIn().setBody(this.executeAnalysis(exchange)); 
        }
        catch (Exception e) {
            ErrorUtils.setHttpError(exchange, e, 500);
        }                
    }
    
    private OceansLogisticRegressionResults executeAnalysis(Exchange exchange) 
        throws Exception
    {
        OceansLogisticRegressionParameters request = 
            (OceansLogisticRegressionParameters)
                exchange.getIn().getBody(
                    OceansLogisticRegressionParameters.class); 
        
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
                exchange, BaseConstants.WORKER_INPUT_DIR) + "/oceans/lr";   
        File file = new File(baseInputDir,fileName);
        
        // setup and run the oceans lr analysis
        String uniqueID = MessageUtils.getID(exchange);           
        String dependentVariableName = request.getDependentVariableName();
        ArrayList<String> independentVariables = 
            new ArrayList(request.getIndependentVariableNames());    

        InstanceBuilder instBuilder = 
            new InstanceBuilder(file, dependentVariableName);

        LogisticRegressionAnalysis lrAnalysis = 
            new LogisticRegressionAnalysis(
                instBuilder.getInstances(), dependentVariableName);

        lrAnalysis.setUniqueID(uniqueID);
        lrAnalysis.setIndependentVariableNames(independentVariables);
        lrAnalysis.Analyze();
        
        // create, initialize and return the results object
        OceansLogisticRegressionResults results = 
            new OceansLogisticRegressionResults();
        this.formatCoefficientResultsObjects(
            lrAnalysis.getLRCoefficients(), results.getCoefficients());

        return results;
    }
    
    private void formatCoefficientResultsObjects(
        List<LogisticRegressionCoefficient> source, 
        List<OceansLogisticRegressionCoefficient> target)
    {
        for (LogisticRegressionCoefficient c: source) 
        {
            OceansLogisticRegressionCoefficient coefficient = 
                new OceansLogisticRegressionCoefficient();
            coefficient.setName(c.getName());
            coefficient.setEstimate(c.getEstimate());
            coefficient.setPValue(c.getPValue());
            coefficient.setDegreeOfFreedom(c.getDegreeOfFreedom());
            coefficient.setStandardError(c.getStandardError());
            target.add(coefficient);
        }        
    }    
}
