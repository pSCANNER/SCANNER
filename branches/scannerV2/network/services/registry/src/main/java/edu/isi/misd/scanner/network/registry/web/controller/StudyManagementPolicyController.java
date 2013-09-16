package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.StudyManagementPolicy;
import edu.isi.misd.scanner.network.registry.data.repository.StudyManagementPolicyRepository;
import edu.isi.misd.scanner.network.registry.web.errors.ConflictException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
    public static final String REQUEST_PARAM_USER_ID = "userId";
    
    @Autowired
    private StudyManagementPolicyRepository studyManagementPolicyRepository;   
    
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
                REQUEST_PARAM_USER_ID);         
        
        String studyId = params.get(REQUEST_PARAM_STUDY_ID);
        String studyRoleId = params.get(REQUEST_PARAM_STUDY_ROLE_ID);
        String userId = params.get(REQUEST_PARAM_USER_ID);
        if ((studyId != null) && (userId != null)) {
            return 
                studyManagementPolicyRepository.
                    findByStudyStudyIdAndStudyRoleScannerUsersUserId(
                        validateIntegerParameter(
                            REQUEST_PARAM_STUDY_ID,studyId),
                        validateIntegerParameter(
                            REQUEST_PARAM_USER_ID,userId));     
        }
        else if (userId != null) {
            return 
                studyManagementPolicyRepository.
                    findByStudyRoleScannerUsersUserId(
                        validateIntegerParameter(
                            REQUEST_PARAM_USER_ID,userId));          
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
    
    @RequestMapping(value = BASE_PATH, 
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody StudyManagementPolicy createStudyManagementPolicy(
           @RequestBody StudyManagementPolicy studyManagementPolicy) 
    {
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
    
    @RequestMapping(value = ENTITY_PATH, 
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody StudyManagementPolicy updateStudyManagementPolicy(
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
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        if (!studyManagementPolicyRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        studyManagementPolicyRepository.delete(id);
    } 
  
}
