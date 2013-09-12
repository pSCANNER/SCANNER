package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.StudyRequestedSite;
import edu.isi.misd.scanner.network.registry.data.repository.StudyRequestedSiteRepository;
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
public class StudyRequestedSiteController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(StudyRequestedSiteController.class.getName());
    
    public static final String REQUEST_PARAM_SITE_ID = "siteId";
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";    
    
    @Autowired
    private StudyRequestedSiteRepository studyRequestedSiteRepository;   
    
	@RequestMapping(value = "/studyRequestedSites", method = RequestMethod.GET)
	public @ResponseBody List<StudyRequestedSite> getStudyRequestedSites(
           @RequestParam Map<String, String> paramMap) 
    {
        String siteId = null;
        String studyId = null;        
        if (!paramMap.isEmpty()) 
        {
            siteId = paramMap.remove(REQUEST_PARAM_SITE_ID);
            studyId = paramMap.remove(REQUEST_PARAM_STUDY_ID);
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
            }            
        }
        
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
    
    @RequestMapping(value = "/studyRequestedSites", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody StudyRequestedSite createStudyRequestedSite(
           @RequestBody StudyRequestedSite site) 
    {
        try {
            studyRequestedSiteRepository.save(site);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return 
            studyRequestedSiteRepository.findOne(
                site.getStudyRequestedSiteId());
    }  
    
    @RequestMapping(value = "/studyRequestedSites/{id}", method = RequestMethod.GET)
    public @ResponseBody StudyRequestedSite getStudyRequestedSite(
           @PathVariable("id") Integer id) 
    {
        StudyRequestedSite foundStudyRequestedSite = 
            studyRequestedSiteRepository.findOne(id);

        if (foundStudyRequestedSite == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundStudyRequestedSite;
    }  
    
    @RequestMapping(value = "/studyRequestedSites/{id}", method = RequestMethod.PUT)
    public @ResponseBody StudyRequestedSite updateStudyRequestedSite(
           @PathVariable("id") Integer id, 
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
                "Update failed: specified object ID (" + 
                site.getStudyRequestedSiteId() + 
                ") does not match referenced ID (" + 
                foundStudyRequestedSite.getStudyRequestedSiteId() + ")"); 
        }
        // ok, good to go
        try {
            studyRequestedSiteRepository.save(site);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return studyRequestedSiteRepository.findOne(site.getStudyRequestedSiteId());
    }     
    
    @RequestMapping(value = "/studyRequestedSites/{id}", method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeStudyRequestedSite(@PathVariable("id") Integer id) 
    {
        if (!studyRequestedSiteRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        studyRequestedSiteRepository.delete(id);
    } 
  
}
