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
package edu.isi.misd.scanner.network.registry.web.controller; 

import com.fasterxml.jackson.databind.JsonNode;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import javax.servlet.ServletContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *  @author Mike D'Arcy 
 */
@Controller
public class DataSetSpecificationProcessingController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(DataSetSpecificationProcessingController.class.getName());

    public static final String BASE_PATH = "/datasetSpecProcessor";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;  
    public static final String EXECUTABLE = "python";
    public static final String PYTHON_BASE = "eqmxlate";    
    public static final String PYTHON_FILE = "parse_model.py";
    
    @Autowired    
    private RegistryService registryService;   
   
    @Autowired    
    private ServletContext servletContext;   
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody DataSetSpecProcessingContext 
        createDataSetProcessingCode(
            @RequestHeader(HEADER_LOGIN_NAME) String loginName,         
            @RequestBody DataSetSpecProcessingContext context) 
    {
        try 
        {
            String workingDir = servletContext.getRealPath(PYTHON_BASE);
            context.setDataSetSpecOutput(
                registryService.convertDataProcessingSpecification(
                    workingDir,
                    EXECUTABLE,
                    PYTHON_FILE, 
                    context.getDataSetSpecInput().toString(),
                    context.getDataSetId()));
           return context;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }   
  
    public static class DataSetSpecProcessingContext
    {
        private Integer dataSetId;
        private String dataSetSpecOutput;        
        private JsonNode dataSetSpecInput;
        
        public DataSetSpecProcessingContext() {};
        
        public DataSetSpecProcessingContext(Integer dataSetId,
                                            JsonNode input,
                                            String output) 
        {
            this.dataSetId = dataSetId;
            this.dataSetSpecInput = input;
            this.dataSetSpecOutput = output;
        }

        public Integer getDataSetId() {
            return dataSetId;
        }

        public void setDataSetId(Integer dataSetId) {
            this.dataSetId = dataSetId;
        }

        public String getDataSetSpecOutput() {
            return dataSetSpecOutput;
        }

        public void setDataSetSpecOutput(String dataSetSpecOutput) {
            this.dataSetSpecOutput = dataSetSpecOutput;
        }

        public JsonNode getDataSetSpecInput() {
            return dataSetSpecInput;
        }

        public void setDataSetSpecInput(JsonNode dataSetSpecInput) {
            this.dataSetSpecInput = dataSetSpecInput;
        }        
    }
}
