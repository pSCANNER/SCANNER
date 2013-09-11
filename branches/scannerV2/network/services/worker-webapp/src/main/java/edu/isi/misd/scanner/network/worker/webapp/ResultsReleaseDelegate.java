
package edu.isi.misd.scanner.network.worker.webapp;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.types.base.ServiceRequestStateType;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.activiti.engine.delegate.BpmnError;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import edu.isi.misd.scanner.network.types.base.ServiceResponse;
import edu.isi.misd.scanner.network.types.base.ServiceResponseMetadata;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class ResultsReleaseDelegate implements JavaDelegate 
{      
    public static final String SITE_ID = "siteID";
    public static final String APPROVED = "approved";
    public static final String COMMENTS= "comments";
    public static final String FILE_PATH = "filePath";
    public static final String HOLDING_PATH = "holdingPath";

    protected static final Logger log = 
      LoggerFactory.getLogger(ResultsReleaseDelegate.class);
  
    @Override
    public void execute(DelegateExecution execution) throws Exception 
    {
        if (!execution.hasVariable(BaseConstants.ID)) {
            throw new Exception(
                "Variable not present: " + BaseConstants.ID);            
        }

        if (!execution.hasVariable(BaseConstants.REQUEST_URL)) {
            throw new Exception(
                "Variable not present: " + BaseConstants.REQUEST_URL);            
        }

        if (!execution.hasVariable(SITE_ID)) {
            throw new Exception(
                "Variable not present: " + SITE_ID);            
        }
        
        if (!execution.hasVariable(COMMENTS)) {
            throw new Exception(
                "Variable not present: " + COMMENTS);            
        }
        
        if (!execution.hasVariable(APPROVED)) {
            throw new Exception(
                "Variable not present: " + APPROVED);            
        }
        
        if (!execution.hasVariable(FILE_PATH)) {
            throw new Exception(
                "Variable not present: " + FILE_PATH);            
        }
        
        if (!execution.hasVariable(HOLDING_PATH)) {
            throw new Exception(
                "Variable not present: " + HOLDING_PATH);            
        }
        
        File outputFile = 
            new File((String)execution.getVariable(FILE_PATH));       
        File holdingFile = 
            new File((String) execution.getVariable(HOLDING_PATH));
        
        String id = (String)execution.getVariable(BaseConstants.ID);
        String url = (String)execution.getVariable(BaseConstants.REQUEST_URL);
        String siteID = (String)execution.getVariable(SITE_ID);
        String approved = (String)execution.getVariable(APPROVED);
        String comments = (String)execution.getVariable(COMMENTS);
        if (!Boolean.parseBoolean(approved)) 
        {
            log.info("Release approval rejected for request [" + id + "]." +
                     "  The following file will be deleted: " +
                     holdingFile.getCanonicalPath());            
            try {
                writeRejectedServiceResponse(
                    id, url, siteID, comments, outputFile);                
            } catch (Exception e) {
                 log.warn("Could write service response output file " + 
                          outputFile.getCanonicalPath() + " : Exception: " + e);  
                throw e;               
            }
            try {
                FileUtils.forceDelete(holdingFile);    
            } catch (Exception e) {
                log.warn("Could not delete output file from holding directory: " + 
                          holdingFile.getCanonicalPath());  
                throw e;
            }
            return;
        }
        
        if (outputFile.exists()) {
            try {
                FileUtils.forceDelete(outputFile);
            } catch (Exception e) {
                log.warn("Could not delete output file: " + 
                          outputFile.getCanonicalPath());  
                throw e;
            }
        }
        
        try {
            FileUtils.moveFile(holdingFile,outputFile);
            log.info("Release approval accepted for request [" + id + "]." + 
                     "  The results file was moved from " +
                     holdingFile.getCanonicalPath() + " to " +
                     outputFile.getCanonicalPath());            
        } catch (Exception e) {
            log.warn(
                "Unable to move file " + holdingFile.getCanonicalPath() + 
                " to " + outputFile.getCanonicalPath()); 
            throw e;
        }
    }  
    
    private void writeRejectedServiceResponse(String id,
                                              String url,
                                              String siteID,
                                              String comments,
                                              File outputFile)
        throws Exception
    {
        // 1. Populate the rejected ServiceResponse
        ServiceResponse response = new ServiceResponse();
        ServiceResponseMetadata responseMetadata = 
            new ServiceResponseMetadata();
        responseMetadata.setRequestID(id);
        responseMetadata.setRequestURL(url);
        responseMetadata.setRequestState(
            ServiceRequestStateType.REJECTED);                
        responseMetadata.setRequestStateDetail("Reason: " + 
            (comments.isEmpty() ? "not specified." : comments));
        responseMetadata.setRequestSiteName(siteID);
        response.setServiceResponseMetadata(responseMetadata);  
        
        // 2. Write out the result response using JAXB
		JAXBContext jaxbContext = 
            JAXBContext.newInstance(ServiceResponse.class);
		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();        
        jaxbMarshaller.setProperty(
            Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        jaxbMarshaller.setProperty(
            Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        if (outputFile.exists()) {
            try {
                FileUtils.forceDelete(outputFile);
            } catch (Exception e) {
                log.warn("Could not delete output file: " + 
                          outputFile.getCanonicalPath());  
                throw e;
            }
        } 
        jaxbMarshaller.marshal(response, outputFile);        
    }
}