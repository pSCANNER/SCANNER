package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.StudyRole;
import edu.isi.misd.scanner.network.registry.data.repository.StudyRoleRepository;
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
public class StudyRoleController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(StudyRoleController.class.getName());
    
    public static final String REQUEST_PARAM_STUDY_NAME = "studyName";
    public static final String REQUEST_PARAM_USER_NAME = "userName";
    public static final String REQUEST_PARAM_USER_ID = "userId";    
    
    @Autowired
    private StudyRoleRepository studyRoleRepository;   
    
	@RequestMapping(value = "/studyRoles", method = RequestMethod.GET)
	public @ResponseBody List<StudyRole> getStudies(
           @RequestParam Map<String, String> paramMap) 
    {
        String studyName = null;
        String userName = null;
        String userId = null;
        if (!paramMap.isEmpty()) 
        {
            studyName = paramMap.remove(REQUEST_PARAM_STUDY_NAME);            
            userName = paramMap.remove(REQUEST_PARAM_USER_NAME);
            userId = paramMap.remove(REQUEST_PARAM_USER_ID);            
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
            }            
        }
        
        List<StudyRole> roles = new ArrayList<StudyRole>();        
        if (studyName != null) {
            return
                studyRoleRepository.findByStudyStudyName(studyName);
        } else if (userId != null) {
            Integer id;
            try {
                id = Integer.parseInt(userId);
            } catch (NumberFormatException nfe) {
                throw new BadRequestException(nfe.toString());
            }
            return 
                studyRoleRepository.findByScannerUsersUserId(id);
        } else if (userName != null) {
            return 
                studyRoleRepository.findByScannerUsersUserName(userName);
        } else {
            Iterator iter = studyRoleRepository.findAll().iterator();
            CollectionUtils.addAll(roles, iter);    
            return roles;
        }
    }
    
    @RequestMapping(value = "/studyRoles", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody StudyRole createStudyRole(
           @RequestBody StudyRole studyRole) 
    {
        try {
            studyRoleRepository.save(studyRole);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return studyRoleRepository.findOne(studyRole.getRoleId());
    }  
    
    @RequestMapping(value = "/studyRoles/{id}", method = RequestMethod.GET)
    public @ResponseBody StudyRole getStudyRole(
           @PathVariable("id") Integer id) 
    {
        StudyRole foundStudyRole = studyRoleRepository.findOne(id);

        if (foundStudyRole == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundStudyRole;
    }  
    
    @RequestMapping(value = "/studyRoles/{id}", method = RequestMethod.PUT)
    public @ResponseBody StudyRole updateStudyRole(
           @PathVariable("id") Integer id, @RequestBody StudyRole studyRole) 
    {
        // find the requested resource
        StudyRole foundStudyRole = studyRoleRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundStudyRole == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = studyRole.getRoleId();
        if (updateID == null) {
            studyRole.setRoleId(id);
        } else if (!studyRole.getRoleId().equals(foundStudyRole.getRoleId())) {
            throw new ConflictException(
                "Update failed: specified object ID (" + 
                studyRole.getRoleId() + 
                ") does not match referenced ID (" + 
                foundStudyRole.getRoleId() + ")"); 
        }
        // ok, good to go
        try {
            studyRoleRepository.save(studyRole);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return studyRoleRepository.findOne(studyRole.getRoleId());
    }     
    
    @RequestMapping(value = "/studyRoles/{id}", method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeStudyRole(@PathVariable("id") Integer id) 
    {
        if (!studyRoleRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        studyRoleRepository.delete(id);
    } 
  
}
