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

import edu.isi.misd.scanner.network.registry.data.domain.FavoritesCodeSet;
import edu.isi.misd.scanner.network.registry.data.repository.FavoritesCodeSetRepository;
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
 *  @author Mike D'Arcy 
 */
@Controller
public class FavoritesCodeSetController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(FavoritesCodeSetController.class.getName());
    
    public static final String BASE_PATH = "/favorites";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_SEARCH_TERM = "search";
    
    @Autowired
    private FavoritesCodeSetRepository favoritesCodeSetRepository;   
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<FavoritesCodeSet> getFavorites(
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
                favoritesCodeSetRepository.
                    findByConceptNameIgnoreCaseContainingOrderByConceptNameAsc(
                        searchTerm); 
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Find all query issued");
            }             
            return
                favoritesCodeSetRepository.findAllOrderByConceptNameAsc();
        }          
	}
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody FavoritesCodeSet getFavorite(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        FavoritesCodeSet foundFavorite =
            favoritesCodeSetRepository.findOne(id);

        if (foundFavorite == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundFavorite;
    }  
  
}
