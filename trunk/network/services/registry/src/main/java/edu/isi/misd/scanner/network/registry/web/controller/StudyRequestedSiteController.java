/*  
 * Copyright 2013 University of Southern California 
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0 
 *  
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */ 
package edu.isi.misd.scanner.network.registry.web.controller; 

import edu.isi.misd.scanner.network.registry.data.domain.Study;
import edu.isi.misd.scanner.network.registry.data.domain.StudyRequestedSite;
import edu.isi.misd.scanner.network.registry.data.repository.StudyRepository;
import edu.isi.misd.scanner.network.registry.data.repository.StudyRequestedSiteRepository;
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
 *  @author Mike D'Arcy 
 */
@Controller
public class StudyRequestedSiteController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(StudyRequestedSiteController.class.getName());
    
    public static final String BASE_PATH = "/studyRequestedSites";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_SITE_ID = "siteId";
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";    
    
    @Autowired
    private StudyRequestedSiteRepository studyRequestedSiteRepository;   

    @Autowired
    private StudyRepository studyRepository;   
    
    @Autowired
    private RegistryService registryService;   
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<StudyRequestedSite> getStudyRequestedSites(        
           @RequestParam Map<String, String> paramMap) 
    {
        Map<String,String> params = 
            validateParameterMap(
                paramMap, REQUEST_PARAM_SITE_ID, REQUEST_PARAM_STUDY_ID); 
        
        String siteId = params.get(REQUEST_PARAM_SITE_ID);
        String studyId = params.get(REQUEST_PARAM_STUDY_ID);                
        List<StudyRequestedSite> studyRequestedSites = 
            new ArrayList<StudyRequestedSite>();        
        if (siteId != null) {
            return 
                studyRequestedSiteRepository.findBySiteSiteId(
                    validateIntegerParameter(REQUEST_PARAM_SITE_ID, siteId));
        } else if (studyId != null) {
            return 
                studyRequestedSiteRepository.findByStudyStudyId(
                    validateIntegerParameter(REQUEST_PARAM_STUDY_ID, studyId));            
        } else {
            Iterator iter = studyRequestedSiteRepository.findAll().iterator();
            CollectionUtils.addAll(studyRequestedSites, iter);      
        }
        return studyRequestedSites;           
	}
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody StudyRequestedSite getStudyRequestedSite(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        StudyRequestedSite foundStudyRequestedSite = 
            studyRequestedSiteRepository.findOne(id);

        if (foundStudyRequestedSite == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundStudyRequestedSite;
    }
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody StudyRequestedSite createStudyRequestedSite(
           @RequestHeader(HEADER_LOGIN_NAME) String loginName,            
           @RequestBody StudyRequestedSite site) 
    {
       // first, check that the requested Study association is valid
        Assert.notNull(site.getStudy(), 
            nullVariableMsg(Study.class.getSimpleName()));         
        Integer studyId = site.getStudy().getStudyId();
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
        try {
            studyRequestedSiteRepository.save(site);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return 
            studyRequestedSiteRepository.findOne(
                site.getStudyRequestedSiteId());
    }    
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody StudyRequestedSite updateStudyRequestedSite(
           @RequestHeader(HEADER_LOGIN_NAME) String loginName,            
           @PathVariable(ID_URL_PATH_VAR) Integer id, 
           @RequestBody StudyRequestedSite site) 
    {
        // find the requested resource
        StudyRequestedSite foundStudyRequestedSite = 
            studyRequestedSiteRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundStudyRequestedSite == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = site.getStudyRequestedSiteId();
        if (updateID == null) {
            site.setStudyRequestedSiteId(id);
        } else if (!site.getStudyRequestedSiteId().equals(
                   foundStudyRequestedSite.getStudyRequestedSiteId())) {
            throw new ConflictException(
                site.getStudyRequestedSiteId(),
                foundStudyRequestedSite.getStudyRequestedSiteId()); 
        }
        // check that the user can perform the update
        if (!registryService.userCanManageStudy(
            loginName, site.getStudy().getStudyId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);          
        }   
        
        try {
            studyRequestedSiteRepository.save(site);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return 
            studyRequestedSiteRepository.findOne(site.getStudyRequestedSiteId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeStudyRequestedSite(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,    
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        // find the requested resource
        StudyRequestedSite site = studyRequestedSiteRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (site == null) {
            throw new ResourceNotFoundException(id);            
        }
        // check that the user can perform the delete
        if (!registryService.userCanManageStudy(
            loginName, site.getStudy().getStudyId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_STUDY_MANAGEMENT_ROLE_REQUIRED);         
        }        
        studyRequestedSiteRepository.delete(id);
    } 
  
}
