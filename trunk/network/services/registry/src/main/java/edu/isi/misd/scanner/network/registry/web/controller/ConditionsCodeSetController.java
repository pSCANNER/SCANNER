package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.ConditionsCodeSet;
import edu.isi.misd.scanner.network.registry.data.repository.ConditionsCodeSetRepository;
import edu.isi.misd.scanner.network.registry.web.errors.ResourceNotFoundException;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 */
@Controller
public class ConditionsCodeSetController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(ConditionsCodeSetController.class.getName());
    
    public static final String BASE_PATH = "/conditions";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_SEARCH_TERM = "search";
    
    @Autowired
    private ConditionsCodeSetRepository conditionsCodeSetRepository;   
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<ConditionsCodeSet> getConditions(
           @RequestParam Map<String, String> paramMap) 
    {
        Map<String,String> params = 
            validateParameterMap(paramMap,REQUEST_PARAM_SEARCH_TERM);  
        
        String searchTerm = params.get(REQUEST_PARAM_SEARCH_TERM);
        if (searchTerm != null) {
            if (log.isDebugEnabled()) {
                log.debug("Search query issued using term: " + searchTerm);
            }            
            return 
                conditionsCodeSetRepository.
                    findByConceptNameIgnoreCaseContainingOrderByConceptNameAsc(
                        searchTerm); 
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Find all query issued");
            }                 
            return 
                conditionsCodeSetRepository.findAllOrderByConceptNameAsc();
        }           
	}
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody ConditionsCodeSet getCondition(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        ConditionsCodeSet foundCondition =
            conditionsCodeSetRepository.findOne(id);

        if (foundCondition == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundCondition;
    }  
  
}
