package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.Site;
import edu.isi.misd.scanner.network.registry.data.repository.SiteRepository;
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
public class SiteController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(SiteController.class.getName());
    
    public static final String REQUEST_PARAM_SITE_NAME = "siteName";
    
    @Autowired
    private SiteRepository siteRepository;   
    
	@RequestMapping(value = "/sites", method = RequestMethod.GET)
	public @ResponseBody List<Site> getSites(
           @RequestParam Map<String, String> paramMap) 
    {
        String siteName = null;
        if (!paramMap.isEmpty()) 
        {
            siteName = paramMap.remove(REQUEST_PARAM_SITE_NAME);
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
            }            
        }
        
        List<Site> sites = new ArrayList<Site>();        
        if (siteName != null) {
            Site site = siteRepository.findBySiteName(siteName);
            if (site == null) {
                throw new ResourceNotFoundException(siteName);
            }
            sites.add(site);
        } else {
            Iterator iter = siteRepository.findAll().iterator();
            CollectionUtils.addAll(sites, iter);      
        }
        return sites;           
	}
    
    @RequestMapping(value = "/sites", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody Site createSite(
           @RequestBody Site site) 
    {
        try {
            siteRepository.save(site);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return siteRepository.findOne(site.getSiteId());
    }  
    
    @RequestMapping(value = "/sites/{id}", method = RequestMethod.GET)
    public @ResponseBody Site getSite(
           @PathVariable("id") Integer id) 
    {
        Site foundSite = siteRepository.findOne(id);

        if (foundSite == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundSite;
    }  
    
    @RequestMapping(value = "/sites/{id}", method = RequestMethod.PUT)
    public @ResponseBody Site updateSite(
           @PathVariable("id") Integer id, @RequestBody Site site) 
    {
        // find the requested resource
        Site foundSite = siteRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundSite == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = site.getSiteId();
        if (updateID == null) {
            site.setSiteId(id);
        } else if (!site.getSiteId().equals(foundSite.getSiteId())) {
            throw new ConflictException(
                "Update failed: specified object ID (" + 
                site.getSiteId() + 
                ") does not match referenced ID (" + 
                foundSite.getSiteId() + ")"); 
        }
        // ok, good to go
        try {
            siteRepository.save(site);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return siteRepository.findOne(site.getSiteId());
    }     
    
    @RequestMapping(value = "/sites/{id}", method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeSite(@PathVariable("id") Integer id) 
    {
        if (!siteRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        siteRepository.delete(id);
    } 
  
}
