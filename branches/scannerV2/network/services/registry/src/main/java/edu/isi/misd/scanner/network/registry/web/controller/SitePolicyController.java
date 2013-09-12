package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.SitePolicy;
import edu.isi.misd.scanner.network.registry.data.repository.SitePolicyRepository;
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
public class SitePolicyController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(SitePolicyController.class.getName());
    
    public static final String REQUEST_PARAM_SITE_NAME = "siteName";
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";
    public static final String REQUEST_PARAM_STUDY_ROLE_ID = "studyRoleId";    
    
    @Autowired
    private SitePolicyRepository sitePolicyRepository;   
    
	@RequestMapping(value = "/sitePolicies", method = RequestMethod.GET)
	public @ResponseBody List<SitePolicy> getSitePolicies(
           @RequestParam Map<String, String> paramMap) 
    {
        String siteName = null;
        String studyId = null;
        String studyRoleId = null;
        if (!paramMap.isEmpty()) 
        {
            siteName = paramMap.remove(REQUEST_PARAM_SITE_NAME);
            studyId = paramMap.remove(REQUEST_PARAM_STUDY_ID);            
            studyRoleId = paramMap.remove(REQUEST_PARAM_STUDY_ROLE_ID);
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
            }            
        }
        
        List<SitePolicy> sitePolicy = new ArrayList<SitePolicy>();        
        if (siteName != null) {
            SitePolicy site = 
                sitePolicyRepository.findBySiteSiteName(siteName);
            if (site == null) {
                throw new ResourceNotFoundException(siteName);
            }
            sitePolicy.add(site);
        } else if (studyId != null) {
            return 
                sitePolicyRepository.findByStudyRoleStudyStudyId(
                    validateIntegerParameter(
                        REQUEST_PARAM_STUDY_ID,studyId));          
        } else if (studyRoleId != null) {
            return 
                sitePolicyRepository.findByStudyRoleRoleId(
                    validateIntegerParameter(
                        REQUEST_PARAM_STUDY_ROLE_ID,studyRoleId));
        } else {
            Iterator iter = sitePolicyRepository.findAll().iterator();
            CollectionUtils.addAll(sitePolicy, iter);      
        }
        return sitePolicy;           
	}
    
    @RequestMapping(value = "/sitePolicies", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody SitePolicy createSitePolicy(
           @RequestBody SitePolicy site) 
    {
        try {
            sitePolicyRepository.save(site);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return sitePolicyRepository.findOne(site.getSitePolicyId());
    }  
    
    @RequestMapping(value = "/sitePolicies/{id}", method = RequestMethod.GET)
    public @ResponseBody SitePolicy getSitePolicy(
           @PathVariable("id") Integer id) 
    {
        SitePolicy foundSitePolicy = sitePolicyRepository.findOne(id);

        if (foundSitePolicy == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundSitePolicy;
    }  
    
    @RequestMapping(value = "/sitePolicies/{id}", method = RequestMethod.PUT)
    public @ResponseBody SitePolicy updateSitePolicy(
           @PathVariable("id") Integer id, @RequestBody SitePolicy site) 
    {
        // find the requested resource
        SitePolicy foundSitePolicy = sitePolicyRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundSitePolicy == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = site.getSitePolicyId();
        if (updateID == null) {
            site.setSitePolicyId(id);
        } else if (!site.getSitePolicyId().equals(foundSitePolicy.getSitePolicyId())) {
            throw new ConflictException(
                "Update failed: specified object ID (" + 
                site.getSitePolicyId() + 
                ") does not match referenced ID (" + 
                foundSitePolicy.getSitePolicyId() + ")"); 
        }
        // ok, good to go
        try {
            sitePolicyRepository.save(site);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return sitePolicyRepository.findOne(site.getSitePolicyId());
    }     
    
    @RequestMapping(value = "/sitePolicies/{id}", method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeSitePolicy(@PathVariable("id") Integer id) 
    {
        if (!sitePolicyRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        sitePolicyRepository.delete(id);
    } 
  
}