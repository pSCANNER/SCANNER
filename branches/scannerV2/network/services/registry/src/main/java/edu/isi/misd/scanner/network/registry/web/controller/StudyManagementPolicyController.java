package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.Study;
import edu.isi.misd.scanner.network.registry.data.domain.StudyManagementPolicy;
import edu.isi.misd.scanner.network.registry.data.repository.StudyManagementPolicyRepository;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import edu.isi.misd.scanner.network.registry.data.service.RegistryServiceConstants;
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
public class StudyManagementPolicyController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(StudyManagementPolicyController.class.getName());
    
    public static final String BASE_PATH = "/studyManagementPolicies";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";
    public static final String REQUEST_PARAM_STUDY_ROLE_ID = "studyRoleId";    
    public static final String REQUEST_PARAM_USER_NAME = "userName";
    
    @Autowired
    private StudyManagementPolicyRepository studyManagementPolicyRepository;   
    
    @Autowired
    private RegistryService registryService;  
    
	@RequestMapping(value = BASE_PATH, 
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<StudyManagementPolicy> getStudyManagementPolicies(
           @RequestParam Map<String, String> paramMap) 
    {
        Map<String,String> params = 
            validateParameterMap(
                paramMap,
                REQUEST_PARAM_STUDY_ID,
                REQUEST_PARAM_STUDY_ROLE_ID,
                REQUEST_PARAM_USER_NAME);         
        
        String studyId = params.get(REQUEST_PARAM_STUDY_ID);
        String studyRoleId = params.get(REQUEST_PARAM_STUDY_ROLE_ID);
        String userName = params.get(REQUEST_PARAM_USER_NAME);
        if ((studyId != null) && (userName != null)) {
            return 
                studyManagementPolicyRepository.
                    findByStudyStudyIdAndStudyRoleScannerUsersUserName(
                        validateIntegerParameter(
                            REQUEST_PARAM_STUDY_ID,studyId),
                        userName);     
        }
        else if (userName != null) {
            return 
                studyManagementPolicyRepository.
                    findByStudyRoleScannerUsersUserName(userName);          
        }         
        else if (studyId != null) {
            return 
                studyManagementPolicyRepository.findByStudyStudyId(
                    validateIntegerParameter(
                        REQUEST_PARAM_STUDY_ID,studyId));          
        } else if (studyRoleId != null) {
            return 
                studyManagementPolicyRepository.findByStudyRoleRoleId(
                    validateIntegerParameter(
                        REQUEST_PARAM_STUDY_ROLE_ID,studyRoleId));
        } else {
            List<StudyManagementPolicy> studyManagementPolicy = 
                new ArrayList<StudyManagementPolicy>();             
            Iterator iter = 
                studyManagementPolicyRepository.findAll().iterator();
            CollectionUtils.addAll(studyManagementPolicy, iter);      
            return studyManagementPolicy;                
        }       
	}
    
    @RequestMapping(value = ENTITY_PATH, 
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody StudyManagementPolicy getStudyManagementPolicy(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        StudyManagementPolicy foundStudyManagementPolicy = 
            studyManagementPolicyRepository.findOne(id);

        if (foundStudyManagementPolicy == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundStudyManagementPolicy;
    }
    
    @RequestMapping(value = BASE_PATH, 
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody StudyManagementPolicy createStudyManagementPolicy(
           @RequestHeader(HEADER_LOGIN_NAME) String loginName,             
           @RequestBody StudyManagementPolicy studyManagementPolicy) 
    {
        // first, check that the requested Study association is valid
        Assert.notNull(studyManagementPolicy.getStudy(), 
            nullVariableMsg(Study.class.getSimpleName()));  
        
        // check that the user can perform the create
        if (!registryService.userCanManageStudy(
            loginName,studyManagementPolicy.getStudy().getStudyId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);             
        }             
        try {
            studyManagementPolicyRepository.save(studyManagementPolicy);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return 
            studyManagementPolicyRepository.findOne(
                studyManagementPolicy.getStudyPolicyId());
    }    
    
    @RequestMapping(value = ENTITY_PATH, 
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody StudyManagementPolicy updateStudyManagementPolicy(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,             
        @PathVariable(ID_URL_PATH_VAR) Integer id,
        @RequestBody StudyManagementPolicy studyManagementPolicy) 
    {          
        // find the requested resource
        StudyManagementPolicy foundStudyManagementPolicy = 
            studyManagementPolicyRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundStudyManagementPolicy == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = studyManagementPolicy.getStudyPolicyId();
        if (updateID == null) {
            studyManagementPolicy.setStudyPolicyId(id);
        } else if (!studyManagementPolicy.getStudyPolicyId().equals(
                    foundStudyManagementPolicy.getStudyPolicyId())) {
            throw new ConflictException(
                studyManagementPolicy.getStudyPolicyId(),
                foundStudyManagementPolicy.getStudyPolicyId()); 
        }        
        // check that the user can perform the update
        if (!registryService.userCanManageStudy(
            loginName,studyManagementPolicy.getStudy().getStudyId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);             
        }          
        try {
            studyManagementPolicyRepository.save(studyManagementPolicy);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return 
            studyManagementPolicyRepository.findOne(
                studyManagementPolicy.getStudyPolicyId());
    }     
    
    @RequestMapping(value = ENTITY_PATH, 
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeStudyManagementPolicy(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,             
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        // find the requested resource
        StudyManagementPolicy studyManagementPolicy = 
            studyManagementPolicyRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (studyManagementPolicy == null) {
            throw new ResourceNotFoundException(id);            
        }
        // check that the user can perform the update
        if (!registryService.userCanManageStudy(
            loginName,studyManagementPolicy.getStudy().getStudyId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);             
        }         
        studyManagementPolicyRepository.delete(id);
    } 
  
}
