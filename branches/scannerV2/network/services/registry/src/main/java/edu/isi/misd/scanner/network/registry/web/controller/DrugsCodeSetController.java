package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.DrugsCodeSet;
import edu.isi.misd.scanner.network.registry.data.repository.DrugsCodeSetRepository;
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
public class DrugsCodeSetController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(DrugsCodeSetController.class.getName());
    
    public static final String REQUEST_PARAM_SEARCH_TERM = "search";
    
    @Autowired
    private DrugsCodeSetRepository drugsCodeSetRepository;   
    
	@RequestMapping(value = "/drugs", method = RequestMethod.GET)
	public @ResponseBody List<DrugsCodeSet> getDrugs(
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
                drugsCodeSetRepository.
                    findByConceptNameIgnoreCaseContainingOrderByConceptNameAsc(
                        searchTerm); 
        } else {
            return 
                drugsCodeSetRepository.findAllOrderByConceptNameAsc();
        }          
	}
    
    @RequestMapping(value = "/drugs/{id}", method = RequestMethod.GET)
    public @ResponseBody DrugsCodeSet getDrug(
           @PathVariable("id") Integer id) 
    {
        DrugsCodeSet foundDrug =
            drugsCodeSetRepository.findOne(id);

        if (foundDrug == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundDrug;
    }  
  
}
