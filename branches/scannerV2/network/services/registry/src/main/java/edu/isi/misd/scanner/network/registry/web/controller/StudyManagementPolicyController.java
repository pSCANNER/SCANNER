package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.StudyManagementPolicy;
import edu.isi.misd.scanner.network.registry.data.repository.StudyManagementPolicyRepository;
import edu.isi.misd.scanner.network.registry.web.errors.BadRequestException;
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
    
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";
    public static final String REQUEST_PARAM_STUDY_ROLE_ID = "studyRoleId";    
    public static final String REQUEST_PARAM_USER_ID = "userId";
    
    @Autowired
    private StudyManagementPolicyRepository studyManagementPolicyRepository;   
    
	@RequestMapping(value = "/studyManagementPolicies", 
                    method = RequestMethod.GET)
	public @ResponseBody List<StudyManagementPolicy> getStudyManagementPolicies(
           @RequestParam Map<String, String> paramMap) 
    {
        String studyId = null;
        String studyRoleId = null;
        String userId = null;
        if (!paramMap.isEmpty()) 
        {
            studyId = paramMap.remove(REQUEST_PARAM_STUDY_ID);            
            studyRoleId = paramMap.remove(REQUEST_PARAM_STUDY_ROLE_ID);
            userId = paramMap.remove(REQUEST_PARAM_USER_ID);
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
            }            
        }
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
    
    @RequestMapping(value = "/studyManagementPolicies", 
                    method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody StudyManagementPolicy createStudyManagementPolicy(
           @RequestBody StudyManagementPolicy studyManagementPolicy) 
    {
        try {
            studyManagementPolicyRepository.save(studyManagementPolicy);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return 
            studyManagementPolicyRepository.findOne(
                studyManagementPolicy.getStudyPolicyId());
    }  
    
    @RequestMapping(value = "/studyManagementPolicies/{id}", 
                    method = RequestMethod.GET)
    public @ResponseBody StudyManagementPolicy getStudyManagementPolicy(
           @PathVariable("id") Integer id) 
    {
        StudyManagementPolicy foundStudyManagementPolicy = 
            studyManagementPolicyRepository.findOne(id);

        if (foundStudyManagementPolicy == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundStudyManagementPolicy;
    }  
    
    @RequestMapping(value = "/studyManagementPolicies/{id}", 
                    method = RequestMethod.PUT)
    public @ResponseBody StudyManagementPolicy updateStudyManagementPolicy(
           @PathVariable("id") Integer id,
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
                "Update failed: specified object ID (" + 
                studyManagementPolicy.getStudyPolicyId() + 
                ") does not match referenced ID (" + 
                foundStudyManagementPolicy.getStudyPolicyId() + ")"); 
        }
        // ok, good to go
        try {
            studyManagementPolicyRepository.save(studyManagementPolicy);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return 
            studyManagementPolicyRepository.findOne(
                studyManagementPolicy.getStudyPolicyId());
    }     
    
    @RequestMapping(value = "/studyManagementPolicies/{id}", 
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeStudyManagementPolicy(@PathVariable("id") Integer id) 
    {
        if (!studyManagementPolicyRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        studyManagementPolicyRepository.delete(id);
    } 
  
}
