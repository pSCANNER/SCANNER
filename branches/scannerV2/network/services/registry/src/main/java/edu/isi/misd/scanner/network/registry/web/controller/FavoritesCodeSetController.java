package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.FavoritesCodeSet;
import edu.isi.misd.scanner.network.registry.data.repository.FavoritesCodeSetRepository;
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
public class FavoritesCodeSetController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(FavoritesCodeSetController.class.getName());
    
    public static final String REQUEST_PARAM_SEARCH_TERM = "search";
    
    @Autowired
    private FavoritesCodeSetRepository favoritesCodeSetRepository;   
    
	@RequestMapping(value = "/favorites", method = RequestMethod.GET)
	public @ResponseBody List<FavoritesCodeSet> getFavorites(
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
                favoritesCodeSetRepository.
                    findByConceptNameIgnoreCaseContainingOrderByConceptNameAsc(
                        searchTerm); 
        } else {
            return
                favoritesCodeSetRepository.findAllOrderByConceptNameAsc();
        }          
	}
    
    @RequestMapping(value = "/favorites/{id}", method = RequestMethod.GET)
    public @ResponseBody FavoritesCodeSet getFavorite(
           @PathVariable("id") Integer id) 
    {
        FavoritesCodeSet foundFavorite =
            favoritesCodeSetRepository.findOne(id);

        if (foundFavorite == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundFavorite;
    }  
  
}
