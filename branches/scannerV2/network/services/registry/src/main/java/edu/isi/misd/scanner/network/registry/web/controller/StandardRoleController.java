package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.StandardRole;
import edu.isi.misd.scanner.network.registry.data.repository.StandardRoleRepository;
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
public class StandardRoleController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(StandardRoleController.class.getName());
    
    public static final String REQUEST_PARAM_SITE_NAME = "roleName";
    
    @Autowired
    private StandardRoleRepository standardRoleRepository;   
    
	@RequestMapping(value = "/standardRoles", method = RequestMethod.GET)
	public @ResponseBody List<StandardRole> getStandardRoles(
           @RequestParam Map<String, String> paramMap) 
    {
        String standardRoleName = null;
        if (!paramMap.isEmpty()) 
        {
            standardRoleName = paramMap.remove(REQUEST_PARAM_SITE_NAME);
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
            }            
        }
        
        List<StandardRole> standardRoles = new ArrayList<StandardRole>();        
        if (standardRoleName != null) {
            StandardRole standardRole = 
                standardRoleRepository.findByStandardRoleName(standardRoleName);
            if (standardRole == null) {
                throw new ResourceNotFoundException(standardRoleName);
            }
            standardRoles.add(standardRole);
        } else {
            Iterator iter = standardRoleRepository.findAll().iterator();
            CollectionUtils.addAll(standardRoles, iter);      
        }
        return standardRoles;           
	}
    
    @RequestMapping(value = "/standardRoles", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody StandardRole createStandardRole(
           @RequestBody StandardRole standardRole) 
    {
        try {
            standardRoleRepository.save(standardRole);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return standardRoleRepository.findOne(standardRole.getStandardRoleId());
    }  
    
    @RequestMapping(value = "/standardRoles/{id}", method = RequestMethod.GET)
    public @ResponseBody StandardRole getStandardRole(
           @PathVariable("id") Integer id) 
    {
        StandardRole foundStandardRole = standardRoleRepository.findOne(id);

        if (foundStandardRole == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundStandardRole;
    }  
    
    @RequestMapping(value = "/standardRoles/{id}", method = RequestMethod.PUT)
    public @ResponseBody StandardRole updateStandardRole(
           @PathVariable("id") Integer id, 
           @RequestBody StandardRole standardRole) 
    {
        // find the requested resource
        StandardRole foundStandardRole = standardRoleRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundStandardRole == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = standardRole.getStandardRoleId();
        if (updateID == null) {
            standardRole.setStandardRoleId(id);
        } else if (!standardRole.getStandardRoleId().equals(
                    foundStandardRole.getStandardRoleId())) {
            throw new ConflictException(
                "Update failed: specified object ID (" + 
                standardRole.getStandardRoleId() + 
                ") does not match referenced ID (" + 
                foundStandardRole.getStandardRoleId() + ")"); 
        }
        // ok, good to go
        try {
            standardRoleRepository.save(standardRole);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return standardRoleRepository.findOne(standardRole.getStandardRoleId());
    }     
    
    @RequestMapping(value = "/standardRoles/{id}", method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeStandardRole(@PathVariable("id") Integer id) 
    {
        if (!standardRoleRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        standardRoleRepository.delete(id);
    } 
  
}
