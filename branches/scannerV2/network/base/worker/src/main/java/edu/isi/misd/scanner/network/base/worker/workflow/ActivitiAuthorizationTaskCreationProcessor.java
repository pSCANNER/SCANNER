package edu.isi.misd.scanner.network.base.worker.workflow;

import edu.isi.misd.scanner.network.base.BaseConstants;
import edu.isi.misd.scanner.network.base.utils.MessageUtils;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  This class creates a new Activiti task instance in the preconfigured Activiti engine.
 */
public class ActivitiAuthorizationTaskCreationProcessor implements Processor 
{
    public static final String SITE_ID = "siteID";
    public static final String FILE_PATH = "filePath";
    public static final String HOLDING_PATH = "holdingPath";
    public static final String PROCESS_INSTANCE_KEY = 
        "scannerQueryResultsReleaseAuthorization";
    public static final String MAIL_NOTIFICATIONS_ENABLED = 
        "{{worker.activiti.mail.mailNotificationsEnabled}}";
    public static final String DEFAULT_MAIL_SENDER = 
        "{{worker.activiti.mail.defaultSender}}";
    public static final String DEFAULT_MAIL_RECIPIENTS = 
        "{{worker.activiti.mail.defaultRecipients}}";
    public static final String DEFAULT_MAIL_RECIPIENT_NAME = 
        "{{worker.activiti.mail.defaultRecipientName}}";
    public static final String DEFAULT_MAIL_RECIPIENT_WEBAPP_LINK = 
        "{{worker.activiti.mail.defaultRecipientWebappLink}}";    
    
    protected static final Logger log = 
        LoggerFactory.getLogger(ActivitiAuthorizationTaskCreationProcessor.class);
  
    @Override
    public void process(Exchange exchange) throws Exception 
    {
        CamelContext context = exchange.getContext();
        String id = exchange.getIn().getHeader(BaseConstants.ID,String.class); 
        String url = 
            exchange.getProperty(BaseConstants.REQUEST_URL, String.class);
        String siteID = MessageUtils.getSiteName(exchange);
        // create Activiti process variables
        HashMap<String, Object> variables = new HashMap<String, Object>();
        variables.put(BaseConstants.ID, id);
        variables.put(BaseConstants.REQUEST_URL, url);
        variables.put(SITE_ID, siteID);
        variables.put(FILE_PATH, exchange.getProperty(FILE_PATH));
        variables.put(HOLDING_PATH, exchange.getProperty(HOLDING_PATH));
        variables.put("mailNotificationsEnabled",
            context.resolvePropertyPlaceholders(MAIL_NOTIFICATIONS_ENABLED));         
        variables.put("mailSender",
            context.resolvePropertyPlaceholders(DEFAULT_MAIL_SENDER));        
        variables.put("mailRecipients",
            context.resolvePropertyPlaceholders(DEFAULT_MAIL_RECIPIENTS));
        variables.put("mailRecipientName",
            context.resolvePropertyPlaceholders(DEFAULT_MAIL_RECIPIENT_NAME));
        variables.put("mailWebappLink",
            context.resolvePropertyPlaceholders(DEFAULT_MAIL_RECIPIENT_WEBAPP_LINK));        
        
        // get the Activiti engine and create a process instance, store the result id
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        ProcessInstance processInstance = 
            runtimeService.startProcessInstanceByKey(
                PROCESS_INSTANCE_KEY, variables);
        String processInstanceId = processInstance.getProcessInstanceId();
        
        // set the owner to the default assignee - may want to change this later
        TaskService taskService = processEngine.getTaskService();        
		List<Task> tasks = 
            taskService.createTaskQuery().processInstanceId(
                processInstanceId).list();   
        for (Task task : tasks) {
            task.setOwner(task.getAssignee()); 
            taskService.saveTask(task);
        }
        
        // add the output as an Activiti attachment to the created process instance
        File file = new File(exchange.getProperty(HOLDING_PATH,String.class));            
        BufferedInputStream bis = 
            new BufferedInputStream(new FileInputStream(file));       
        taskService.createAttachment(
            "application/xml",
            null,
            processInstanceId,
            id, 
            "Computational process output which requires authorization to be published at: " + 
            MessageUtils.getResultURL(exchange),
            bis); 
        bis.close();
    }
}
