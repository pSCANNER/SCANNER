package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.ScannerUser;
import edu.isi.misd.scanner.network.registry.data.repository.ScannerUserRepository;
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
public class ScannerUserController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(ScannerUserController.class.getName());
    
    public static final String REQUEST_PARAM_USER_NAME = "userName";
    
    @Autowired
    private ScannerUserRepository scannerUserRepository;   
    
	@RequestMapping(value = "/users", method = RequestMethod.GET)
	public @ResponseBody List<ScannerUser> getScannerUsers(
           @RequestParam Map<String, String> paramMap) 
    {
        String userName = null;
        if (!paramMap.isEmpty()) 
        {
            userName = paramMap.remove(REQUEST_PARAM_USER_NAME);
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
            }            
        }
        
        List<ScannerUser> users = new ArrayList<ScannerUser>();        
        if (userName != null) {
            ScannerUser user = scannerUserRepository.findByUserName(userName);
            if (user == null) {
                throw new ResourceNotFoundException(userName);
            }
            users.add(user);
        } else {
            Iterator iter = scannerUserRepository.findAll().iterator();
            CollectionUtils.addAll(users, iter);      
        }
        return users;           
	}
    
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody ScannerUser createScannerUser(
           @RequestBody ScannerUser user) 
    {
        try {
            scannerUserRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return scannerUserRepository.findOne(user.getUserId());
    }  
    
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public @ResponseBody ScannerUser getScannerUser(
           @PathVariable("id") Integer id) 
    {
        ScannerUser foundUser = scannerUserRepository.findOne(id);

        if (foundUser == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundUser;
    }  
    
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public @ResponseBody ScannerUser updateScannerUser(
           @PathVariable("id") Integer id, @RequestBody ScannerUser user) 
    {
        // find the requested resource
        ScannerUser foundUser = scannerUserRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundUser == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = user.getUserId();
        if (updateID == null) {
            user.setUserId(id);
        } else if (!user.getUserId().equals(foundUser.getUserId())) {
            throw new ConflictException(
                "Update failed: specified object ID (" + 
                user.getUserId() + 
                ") does not match referenced ID (" + 
                foundUser.getUserId() + ")"); 
        }
        // ok, good to go
        try {
            scannerUserRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return scannerUserRepository.findOne(user.getUserId());
    }     
    
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeScannerUser(@PathVariable("id") Integer id) 
    {
        if (!scannerUserRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        scannerUserRepository.delete(id);
    } 
  
}
