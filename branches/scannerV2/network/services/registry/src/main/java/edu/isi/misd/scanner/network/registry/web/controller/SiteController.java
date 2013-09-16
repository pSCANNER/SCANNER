package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.Site;
import edu.isi.misd.scanner.network.registry.data.repository.SiteRepository;
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
    
    public static final String BASE_PATH = "/sites";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_SITE_NAME = "siteName";
    
    @Autowired
    private SiteRepository siteRepository;   
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<Site> getSites(
           @RequestParam Map<String, String> paramMap) 
    {
        Map<String,String> params = 
            validateParameterMap(paramMap, REQUEST_PARAM_SITE_NAME);    
        
        String siteName = params.get(REQUEST_PARAM_SITE_NAME);        
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
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody Site createSite(
           @RequestBody Site site) 
    {
        try {
            siteRepository.save(site);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return siteRepository.findOne(site.getSiteId());
    }  
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody Site getSite(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        Site foundSite = siteRepository.findOne(id);

        if (foundSite == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundSite;
    }  
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody Site updateSite(
           @PathVariable(ID_URL_PATH_VAR) Integer id, @RequestBody Site site) 
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
            throw new ConflictException(site.getSiteId(),foundSite.getSiteId()); 
        }
        
        try {
            siteRepository.save(site);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return siteRepository.findOne(site.getSiteId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeSite(@PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        if (!siteRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        siteRepository.delete(id);
    } 
  
}
