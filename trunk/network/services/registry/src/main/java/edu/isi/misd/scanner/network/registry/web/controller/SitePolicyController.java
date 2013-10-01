package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.Site;
import edu.isi.misd.scanner.network.registry.data.domain.SitePolicy;
import edu.isi.misd.scanner.network.registry.data.repository.SitePolicyRepository;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import edu.isi.misd.scanner.network.registry.data.service.RegistryServiceConstants;
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
public class SitePolicyController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(SitePolicyController.class.getName());
    
    public static final String BASE_PATH = "/sitePolicies";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;
    public static final String REQUEST_PARAM_USER_NAME = "userName";    
    public static final String REQUEST_PARAM_SITE_ID = "siteId";
    public static final String REQUEST_PARAM_STUDY_ID = "studyId";
    public static final String REQUEST_PARAM_STUDY_ROLE_ID = "studyRoleId";    
    
    @Autowired
    private SitePolicyRepository sitePolicyRepository;   
    
    @Autowired
    private RegistryService registryService;
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<SitePolicy> getSitePolicies(
           @RequestParam Map<String, String> paramMap) 
    {
        Map<String,String> params = 
            validateParameterMap(
                paramMap,
                REQUEST_PARAM_USER_NAME,
                REQUEST_PARAM_SITE_ID,
                REQUEST_PARAM_STUDY_ID,
                REQUEST_PARAM_STUDY_ROLE_ID);

        String userName = params.get(REQUEST_PARAM_USER_NAME);        
        String siteId = params.get(REQUEST_PARAM_SITE_ID);
        String studyId = params.get(REQUEST_PARAM_STUDY_ID);
        String studyRoleId = params.get(REQUEST_PARAM_STUDY_ROLE_ID);
        List<SitePolicy> sitePolicy = new ArrayList<SitePolicy>();        
        if ((siteId != null) && (userName != null)){
            return
                sitePolicyRepository.findBySiteIdAndUserName(
                    validateIntegerParameter(
                        REQUEST_PARAM_STUDY_ID,siteId),userName);   
        } else if (userName != null) {
            return
                sitePolicyRepository.findByUserName(userName);
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
            return sitePolicy;            
        }           
	}
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody SitePolicy getSitePolicy(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        SitePolicy foundSitePolicy = sitePolicyRepository.findOne(id);

        if (foundSitePolicy == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundSitePolicy;
    } 
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody SitePolicy createSitePolicy(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,            
        @RequestBody SitePolicy sitePolicy) 
    {
        // first, check that the requested Site association is valid
        Assert.notNull(sitePolicy.getSite(), 
            nullVariableMsg(Site.class.getSimpleName()));  
        
        // check that the user can perform the create
        if (!registryService.userCanManageSite(
            loginName,sitePolicy.getSite().getSiteId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SITE_MANAGEMENT_ROLE_REQUIRED);            
        }          
        try {
            sitePolicyRepository.save(sitePolicy);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return sitePolicyRepository.findOne(sitePolicy.getSitePolicyId());
    }   
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody SitePolicy updateSitePolicy(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,            
        @PathVariable(ID_URL_PATH_VAR) Integer id,
        @RequestBody SitePolicy sitePolicy) 
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
        Integer updateID = sitePolicy.getSitePolicyId();
        if (updateID == null) {
            sitePolicy.setSitePolicyId(id);
        } else if (!sitePolicy.getSitePolicyId().equals(
                   foundSitePolicy.getSitePolicyId())) {
            throw new ConflictException(
                sitePolicy.getSitePolicyId(), foundSitePolicy.getSitePolicyId()); 
        }
        // check that the user can perform the update
        if (!registryService.userCanManageSite(
            loginName,sitePolicy.getSite().getSiteId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SITE_MANAGEMENT_ROLE_REQUIRED);            
        }   
        try {
            sitePolicyRepository.save(sitePolicy);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return sitePolicyRepository.findOne(sitePolicy.getSitePolicyId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeSitePolicy(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,            
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        // find the requested resource
        SitePolicy sitePolicy = sitePolicyRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (sitePolicy == null) {
            throw new ResourceNotFoundException(id);            
        }
        // check that the user can perform the delete
        if (!registryService.userCanManageSite(
            loginName, sitePolicy.getSite().getSiteId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SITE_MANAGEMENT_ROLE_REQUIRED);            
        }         
        sitePolicyRepository.delete(id);
    } 
  
}
