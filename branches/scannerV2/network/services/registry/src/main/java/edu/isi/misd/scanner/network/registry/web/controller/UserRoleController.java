package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.UserRole;
import edu.isi.misd.scanner.network.registry.data.repository.UserRoleRepository;
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
public class UserRoleController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(UserRoleController.class.getName());
    
    public static final String REQUEST_PARAM_USER_ID = "userId";      
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";  
    
    @Autowired
    private UserRoleRepository userRoleRepository;   
    
	@RequestMapping(value = "/userRoles", method = RequestMethod.GET)
	public @ResponseBody List<UserRole> getUserRoles(
           @RequestParam Map<String, String> paramMap) 
    {
        String userId = null;        
        String studyId = null;
        if (!paramMap.isEmpty()) 
        {
            userId = paramMap.remove(REQUEST_PARAM_USER_ID);            
            studyId = paramMap.remove(REQUEST_PARAM_STUDY_ID);                       
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
            }            
        }
        
        if ((userId != null) && (studyId != null)) 
        {
            return 
                userRoleRepository.findByUserUserIdAndStudyRoleStudyStudyId(
                    validateIntegerParameter(
                        REQUEST_PARAM_USER_ID, userId), 
                    validateIntegerParameter(
                        REQUEST_PARAM_STUDY_ID, studyId)
                );
        } else if (userId != null) {
            return 
                userRoleRepository.findByUserUserId(
                    validateIntegerParameter(
                        REQUEST_PARAM_USER_ID, userId));
        } else if (studyId != null) {         
            return
                userRoleRepository.findByStudyRoleStudyStudyId(
                    validateIntegerParameter(
                        REQUEST_PARAM_STUDY_ID, studyId));
        } else {
            List<UserRole> roles = new ArrayList<UserRole>();             
            Iterator iter = userRoleRepository.findAll().iterator();
            CollectionUtils.addAll(roles, iter);    
            return roles;
        }
    }
    
    @RequestMapping(value = "/userRoles", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody UserRole createUserRole(
           @RequestBody UserRole userRole) 
    {
        try {
            userRoleRepository.save(userRole);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return userRoleRepository.findOne(userRole.getUserRoleId());
    }  
    
    @RequestMapping(value = "/userRoles/{id}", method = RequestMethod.GET)
    public @ResponseBody UserRole getUserRole(
           @PathVariable("id") Integer id) 
    {
        UserRole foundUserRole = userRoleRepository.findOne(id);

        if (foundUserRole == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundUserRole;
    }  
    
    @RequestMapping(value = "/userRoles/{id}", method = RequestMethod.PUT)
    public @ResponseBody UserRole updateUserRole(
           @PathVariable("id") Integer id, @RequestBody UserRole userRole) 
    {
        // find the requested resource
        UserRole foundUserRole = userRoleRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundUserRole == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = userRole.getUserRoleId();
        if (updateID == null) {
            userRole.setUserRoleId(id);
        } else if (!userRole.getUserRoleId().equals(
                   foundUserRole.getUserRoleId())) {
            throw new ConflictException(
                "Update failed: specified object ID (" + 
                userRole.getUserRoleId() + 
                ") does not match referenced ID (" + 
                foundUserRole.getUserRoleId() + ")"); 
        }
        // ok, good to go
        try {
            userRoleRepository.save(userRole);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return userRoleRepository.findOne(userRole.getUserRoleId());
    }     
    
    @RequestMapping(value = "/userRoles/{id}", method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeUserRole(@PathVariable("id") Integer id) 
    {
        if (!userRoleRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        userRoleRepository.delete(id);
    } 
  
}