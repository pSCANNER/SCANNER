package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.Study;
import edu.isi.misd.scanner.network.registry.data.domain.StudyPolicyStatement;
import edu.isi.misd.scanner.network.registry.data.repository.ScannerUserRepository;
import edu.isi.misd.scanner.network.registry.data.repository.StudyPolicyStatementRepository;
import edu.isi.misd.scanner.network.registry.data.repository.StudyRepository;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import edu.isi.misd.scanner.network.registry.data.service.RegistryServiceConstants;
import edu.isi.misd.scanner.network.registry.web.errors.BadRequestException;
import edu.isi.misd.scanner.network.registry.web.errors.ConflictException;
import edu.isi.misd.scanner.network.registry.web.errors.ForbiddenException;
import edu.isi.misd.scanner.network.registry.web.errors.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 */
@Controller
public class StudyPolicyStatementController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(StudyPolicyStatementController.class.getName());
    
    public static final String BASE_PATH = "/studyPolicies";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;     
    public static final String REQUEST_PARAM_USER_NAME = "userName";      
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";   
    public static final String REQUEST_PARAM_DATASET_ID = "dataSetId"; 
    public static final String REQUEST_PARAM_ANALYSIS_TOOL_ID = "analysisToolId";     
    
    @Autowired
    private StudyPolicyStatementRepository studyPolicyStatementRepository;   
    
    @Autowired
    private ScannerUserRepository scannerUserRepository;  

    @Autowired
    private StudyRepository studyRepository;  
    
    @Autowired
    private RegistryService registryService;  
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<StudyPolicyStatement> 
        getStudyPolicyStatements(@RequestParam Map<String, String> paramMap) 
    {
        Map<String,String> params = 
            validateParameterMap(
                paramMap,
                REQUEST_PARAM_USER_NAME,
                REQUEST_PARAM_STUDY_ID,
                REQUEST_PARAM_DATASET_ID,
                REQUEST_PARAM_ANALYSIS_TOOL_ID);   
        
        if (!params.isEmpty()) 
        {
            String userName = params.get(REQUEST_PARAM_USER_NAME);
            if (userName != null) {
                return 
                    studyPolicyStatementRepository.findByUserName(userName);
            }
            ArrayList<String> missingParams = new ArrayList<String>();            
            String studyId = params.get(REQUEST_PARAM_STUDY_ID);  
            if (studyId == null) {
                missingParams.add(REQUEST_PARAM_STUDY_ID);
            }              
            String dataSetId = params.get(REQUEST_PARAM_DATASET_ID);
            if (dataSetId == null) {
                missingParams.add(REQUEST_PARAM_DATASET_ID);
            }
            String toolId = params.get(REQUEST_PARAM_ANALYSIS_TOOL_ID);
            if (toolId == null) {
                missingParams.add(REQUEST_PARAM_ANALYSIS_TOOL_ID);
            }               
            if ((studyId == null) || 
                (dataSetId == null) ||                
                (toolId == null)) 
            {
                throw new BadRequestException(
                    "Required parameter(s) missing: " + missingParams);                
            }                           
            return
                studyPolicyStatementRepository.
                    findStudyPolicyStatementByStudyIdAndDataSetIdAndToolId(
                        validateIntegerParameter(
                            REQUEST_PARAM_STUDY_ID, studyId),
                        validateIntegerParameter(
                            REQUEST_PARAM_DATASET_ID, dataSetId),
                        validateIntegerParameter(
                            REQUEST_PARAM_ANALYSIS_TOOL_ID, toolId));                      
        }
        
        List<StudyPolicyStatement> studyPolicyStatements = 
            new ArrayList<StudyPolicyStatement>();
        Iterator iter = studyPolicyStatementRepository.findAll().iterator();
        CollectionUtils.addAll(studyPolicyStatements, iter);
        
        return studyPolicyStatements;         
	}
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody StudyPolicyStatement getStudyPolicyStatement(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        StudyPolicyStatement foundStudyPolicyStatement = 
            studyPolicyStatementRepository.findOne(id);

        if (foundStudyPolicyStatement == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundStudyPolicyStatement;
    } 
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody StudyPolicyStatement createStudyPolicyStatement(
           @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
           @RequestBody StudyPolicyStatement studyPolicyStatement) 
    {
       // first, check that the requested Study association is valid
        Assert.notNull(
            studyPolicyStatement.getStudy(), 
            nullVariableMsg(Study.class.getSimpleName()));           
        Integer studyId = studyPolicyStatement.getStudy().getStudyId();
        Study study = studyRepository.findOne(studyId);
        if (study == null) {
            throw new BadRequestException(
                Study.class.getSimpleName(),studyId);
        }           
        // check that the user can perform the create
        if (!registryService.userCanManageStudy(loginName,study.getStudyId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);         
        }
        studyPolicyStatement.setPolicyOriginator(
            scannerUserRepository.findByUserName(loginName));
        try {
            studyPolicyStatementRepository.save(studyPolicyStatement);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return studyPolicyStatementRepository.findOne(
            studyPolicyStatement.getStudyPolicyStatementId());
    }   
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody StudyPolicyStatement updateStudyPolicyStatement(
           @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
           @PathVariable(ID_URL_PATH_VAR) Integer id,
           @RequestBody StudyPolicyStatement studyPolicyStatement) 
    {
        // find the requested resource
        StudyPolicyStatement foundStudyPolicyStatement = 
            studyPolicyStatementRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundStudyPolicyStatement == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = 
            studyPolicyStatement.getStudyPolicyStatementId();
        if (updateID == null) {
            studyPolicyStatement.setStudyPolicyStatementId(id);
        } else if (!studyPolicyStatement.getStudyPolicyStatementId().equals(
                    foundStudyPolicyStatement.getStudyPolicyStatementId())) 
        {
            throw new ConflictException(
                studyPolicyStatement.getStudyPolicyStatementId(),
                foundStudyPolicyStatement.getStudyPolicyStatementId()); 
        }
        // check that the user can perform the update
        if (!registryService.userCanManageStudy(
            loginName,studyPolicyStatement.getStudy().getStudyId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);         
        }
        
        try {
            studyPolicyStatementRepository.save(studyPolicyStatement);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return studyPolicyStatementRepository.findOne(
            studyPolicyStatement.getStudyPolicyStatementId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeStudyPolicyStatement(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        // find the requested resource
        StudyPolicyStatement studyPolicyStatement = 
            studyPolicyStatementRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (studyPolicyStatement == null) {
            throw new ResourceNotFoundException(id);            
        }
        // check that the user can perform the delete
        if (!registryService.userCanManageStudy(
            loginName,studyPolicyStatement.getStudy().getStudyId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);         
        }         
        studyPolicyStatementRepository.delete(id);
    } 
  
}
