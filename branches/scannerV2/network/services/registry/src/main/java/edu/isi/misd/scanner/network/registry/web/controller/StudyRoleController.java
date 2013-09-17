package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.Study;
import edu.isi.misd.scanner.network.registry.data.domain.StudyRole;
import edu.isi.misd.scanner.network.registry.data.repository.StudyRepository;
import edu.isi.misd.scanner.network.registry.data.repository.StudyRoleRepository;
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
public class StudyRoleController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(StudyRoleController.class.getName());
    
    public static final String BASE_PATH = "/studyRoles";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;     
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";
    public static final String REQUEST_PARAM_USER_ID = "userId";    
    
    @Autowired
    private StudyRoleRepository studyRoleRepository;   

    @Autowired
    private StudyRepository studyRepository;   
    
    @Autowired
    private RegistryService registryService; 
    
	@RequestMapping(value = BASE_PATH,                     
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<StudyRole> getStudyRoles(
           @RequestParam Map<String, String> paramMap) 
    {
        Map<String,String> params = 
            validateParameterMap(
                paramMap, REQUEST_PARAM_STUDY_ID, REQUEST_PARAM_USER_ID);  
        
        String studyId = params.get(REQUEST_PARAM_STUDY_ID);
        String userId = params.get(REQUEST_PARAM_USER_ID);           
        if ((studyId != null) && (userId != null)) 
        {
            return 
                studyRoleRepository.findByStudyStudyIdAndScannerUsersUserId(
                    validateIntegerParameter(
                        REQUEST_PARAM_STUDY_ID, studyId),
                    validateIntegerParameter(
                        REQUEST_PARAM_USER_ID, userId)
                );            
        } else if (studyId != null) {
            return
                studyRoleRepository.findByStudyStudyId(
                    validateIntegerParameter(
                        REQUEST_PARAM_STUDY_ID, studyId));
        } else if (userId != null) {
            return 
                studyRoleRepository.findByScannerUsersUserId(
                    validateIntegerParameter(
                        REQUEST_PARAM_USER_ID, userId));
        } else {
            List<StudyRole> roles = new ArrayList<StudyRole>();                
            Iterator iter = studyRoleRepository.findAll().iterator();
            CollectionUtils.addAll(roles, iter);    
            return roles;
        }
    }
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody StudyRole createStudyRole(
           @RequestHeader(value=HEADER_LOGIN_NAME) String loginName,        
           @RequestBody StudyRole studyRole) 
    {        
        // first, check that the requested Study association is valid
        Assert.notNull(studyRole.getStudy(), 
            nullVariableMsg(Study.class.getSimpleName()));           
        Integer studyId = studyRole.getStudy().getStudyId();
        Study study = studyRepository.findOne(studyId);
        if (study == null) {
            throw new BadRequestException(
                Study.class.getSimpleName(),studyId);
        }        
        // check that the user can perform the create
        if (!registryService.userCanManageStudy(study.getStudyId(),loginName)) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);         
        }          
        try {
            studyRoleRepository.save(studyRole);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return studyRoleRepository.findOne(studyRole.getRoleId());
    }  
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody StudyRole getStudyRole(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        StudyRole foundStudyRole = studyRoleRepository.findOne(id);

        if (foundStudyRole == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundStudyRole;
    }  
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody StudyRole updateStudyRole(
           @RequestHeader(value=HEADER_LOGIN_NAME) String loginName,        
           @PathVariable(ID_URL_PATH_VAR) Integer id,
           @RequestBody StudyRole studyRole) 
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
                studyRole.getRoleId(),foundStudyRole.getRoleId()); 
        }
        // check that the user can perform the update
        if (!registryService.userCanManageStudy(
            studyRole.getStudy().getStudyId(),loginName)) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);         
        }  
        try {
            studyRoleRepository.save(studyRole);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return studyRoleRepository.findOne(studyRole.getRoleId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeStudyRole(
        @RequestHeader(value=HEADER_LOGIN_NAME) String loginName,        
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        // find the requested resource
        StudyRole studyRole = studyRoleRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (studyRole == null) {
            throw new ResourceNotFoundException(id);            
        }
        // check that the user can perform the delete
        if (!registryService.userCanManageStudy(
            studyRole.getStudy().getStudyId(),loginName)) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);         
        }         
        studyRoleRepository.delete(id);
    } 
  
}
