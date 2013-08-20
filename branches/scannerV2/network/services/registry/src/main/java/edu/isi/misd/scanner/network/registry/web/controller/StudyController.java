package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.Study;
import edu.isi.misd.scanner.network.registry.data.repository.StudyRepository;
import edu.isi.misd.scanner.network.registry.web.errors.ConflictException;
import edu.isi.misd.scanner.network.registry.web.errors.ResourceNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
public class StudyController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(StudyController.class.getName());
    
    @Autowired
    private StudyRepository studyRepository;   
    
	@RequestMapping(value = "/studies", method = RequestMethod.GET)
	public @ResponseBody List<Study> getStudies(
           @RequestParam(value="studyName", required=false) String studyName,
           @RequestParam(value="userId", required=false) Integer userId,
           @RequestParam(value="userName", required=false) String userName)
    {
        List<Study> studies = new ArrayList<Study>();
        
        if (studyName != null) {
            Study study = studyRepository.findByStudyName(studyName);
            if (study == null) {
                throw new ResourceNotFoundException(studyName);
            }
            studies.add(study);
        } else if (userId != null) {
            return 
                studyRepository.findStudiesForUserId(userId);
        } else if (userName != null) {
            return 
                studyRepository.findStudiesForUserName(userName);
        } else {
            Iterator iter = studyRepository.findAll().iterator();
            CollectionUtils.addAll(studies, iter);      
        }
        return studies;   
    }
    
    @RequestMapping(value = "/studies", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody Study createStudy(
           @RequestBody Study study) 
    {
        try {
            studyRepository.save(study);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return studyRepository.findOne(study.getStudyId());
    }  
    
    @RequestMapping(value = "/studies/{id}", method = RequestMethod.GET)
    public @ResponseBody Study getStudy(
           @PathVariable("id") Integer id) 
    {
        Study foundStudy = studyRepository.findOne(id);

        if (foundStudy == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundStudy;
    }  
    
    @RequestMapping(value = "/studies/{id}", method = RequestMethod.PUT)
    public @ResponseBody Study updateStudy(
           @PathVariable("id") Integer id, @RequestBody Study study) 
    {
        // find the requested resource
        Study foundStudy = studyRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundStudy == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = study.getStudyId();
        if (updateID == null) {
            study.setStudyId(id);
        } else if (!study.getStudyId().equals(foundStudy.getStudyId())) {
            throw new ConflictException(
                "Update failed: specified object ID (" + 
                study.getStudyId() + 
                ") does not match referenced ID (" + 
                foundStudy.getStudyId() + ")"); 
        }
        // ok, good to go
        try {
            studyRepository.save(study);
        } catch (DataIntegrityViolationException e) {
            log.warn("DataIntegrityViolationException: " + e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return studyRepository.findOne(study.getStudyId());
    }     
    
    @RequestMapping(value = "/studies/{id}", method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeStudy(@PathVariable("id") Integer id) 
    {
        if (!studyRepository.exists(id)) {
            throw new ResourceNotFoundException(id);            
        }
        studyRepository.delete(id);
    } 
  
}
