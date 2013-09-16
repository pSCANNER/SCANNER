package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.ScannerUser;
import edu.isi.misd.scanner.network.registry.data.repository.ScannerUserRepository;
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
    
    public static final String BASE_PATH = "/users";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_USER_NAME = "userName";
    
    @Autowired
    private ScannerUserRepository scannerUserRepository;   
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<ScannerUser> getScannerUsers(
           @RequestParam Map<String, String> paramMap) 
    {
        Map<String,String> params = 
            validateParameterMap(paramMap, REQUEST_PARAM_USER_NAME);    
        
        String userName = params.get(REQUEST_PARAM_USER_NAME);        
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
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody ScannerUser createScannerUser(
           @RequestBody ScannerUser user) 
    {
        try {
            scannerUserRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return scannerUserRepository.findOne(user.getUserId());
    }  
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody ScannerUser getScannerUser(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        ScannerUser foundUser = scannerUserRepository.findOne(id);

        if (foundUser == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundUser;
    }  
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody ScannerUser updateScannerUser(
           @PathVariable(ID_URL_PATH_VAR) Integer id,
           @RequestBody ScannerUser user) 
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
            throw new ConflictException(user.getUserId(),foundUser.getUserId()); 
        }

        try {
            scannerUserRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return scannerUserRepository.findOne(user.getUserId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeScannerUser(@PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        if (!scannerUserRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        scannerUserRepository.delete(id);
    } 
  
}
