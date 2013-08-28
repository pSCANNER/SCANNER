package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.ConditionsCodeSet;
import edu.isi.misd.scanner.network.registry.data.repository.ConditionsCodeSetRepository;
import edu.isi.misd.scanner.network.registry.web.errors.BadRequestException;
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
    
    public static final String REQUEST_PARAM_SEARCH_TERM = "search";
    
    @Autowired
    private ConditionsCodeSetRepository conditionsCodeSetRepository;   
    
	@RequestMapping(value = "/conditions", method = RequestMethod.GET)
	public @ResponseBody List<ConditionsCodeSet> getConditions(
           @RequestParam Map<String, String> paramMap) 
    {
        String searchTerm = null;
        if (!paramMap.isEmpty()) 
        {
            searchTerm = paramMap.remove(REQUEST_PARAM_SEARCH_TERM);
            if (!paramMap.isEmpty()) {
                throw new BadRequestException(paramMap.keySet());
            }            
        }
              
        if (searchTerm != null) {
            return 
                conditionsCodeSetRepository.
                    findByConceptNameIgnoreCaseContainingOrderByConceptNameAsc(
                        searchTerm); 
        } else {
            return 
                conditionsCodeSetRepository.findAllOrderByConceptNameAsc();
        }           
	}
    
    @RequestMapping(value = "/conditions/{id}", method = RequestMethod.GET)
    public @ResponseBody ConditionsCodeSet getCondition(
           @PathVariable("id") Integer id) 
    {
        ConditionsCodeSet foundCondition =
            conditionsCodeSetRepository.findOne(id);

        if (foundCondition == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundCondition;
    }  
  
}
