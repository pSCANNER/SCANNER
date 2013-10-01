package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.Node;
import edu.isi.misd.scanner.network.registry.data.repository.NodeRepository;
import edu.isi.misd.scanner.network.registry.data.service.RegistryService;
import edu.isi.misd.scanner.network.registry.data.service.RegistryServiceConstants;
import edu.isi.misd.scanner.network.registry.web.errors.ConflictException;
import edu.isi.misd.scanner.network.registry.web.errors.ForbiddenException;
import edu.isi.misd.scanner.network.registry.web.errors.ResourceNotFoundException;
import java.util.ArrayList;
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
public class NodeController extends BaseController
{
    private static final Log log = 
        LogFactory.getLog(NodeController.class.getName());
    
    public static final String BASE_PATH = "/nodes";
    public static final String ENTITY_PATH = BASE_PATH + ID_URL_PATH;       
    public static final String REQUEST_PARAM_NODE_TYPE = "nodeType";
    public static final String REQUEST_PARAM_USER_NAME = "userName";    
    
    @Autowired
    private NodeRepository nodeRepository;     
        
    @Autowired
    private RegistryService registryService;  
    
	@RequestMapping(value = BASE_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
	public @ResponseBody List<Node> getNodes(  
           @RequestParam Map<String, String> paramMap) 
    {
        
        Map<String,String> params = 
            validateParameterMap(
                paramMap, 
                REQUEST_PARAM_NODE_TYPE, 
                REQUEST_PARAM_USER_NAME);   
        
        String nodeType = params.get(REQUEST_PARAM_NODE_TYPE);        
        String userName = params.get(REQUEST_PARAM_USER_NAME);         
        List<Node> nodes = new ArrayList<Node>();   
        boolean isMaster = false;
        if (nodeType != null) {
            if ("master".equalsIgnoreCase(nodeType)) {
                isMaster = true;
            }
            if (userName != null) {
                nodes = 
                    nodeRepository.
                        findBySiteSitePoliciesStudyRoleUserRolesUserUserNameAndIsMaster(
                            userName, isMaster); 
            } else {
                CollectionUtils.addAll(
                    nodes, nodeRepository.findByIsMaster(isMaster).iterator());                   
            }
        } else {
            if (userName != null) {            
                nodes = 
                    nodeRepository.
                        findBySiteSitePoliciesStudyRoleUserRolesUserUserName(
                            userName);          
            } else {
                CollectionUtils.addAll(
                    nodes, nodeRepository.findAll().iterator());  
            }
        }        
        return nodes;         
	}
    
    @RequestMapping(value = ENTITY_PATH,
                    method = {RequestMethod.GET, RequestMethod.HEAD},
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody Node getNode(
           @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        Node foundNode = nodeRepository.findOne(id);

        if (foundNode == null) {
            throw new ResourceNotFoundException(id);
        }
        return foundNode;
    }
    
    @RequestMapping(value = BASE_PATH,
                    method = RequestMethod.POST,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public @ResponseBody Node createNode(
           @RequestHeader(HEADER_LOGIN_NAME) String loginName,           
           @RequestBody Node node) 
    {
        // check that the user can perform the create
        if (!registryService.userCanManageNode(loginName, node.getNodeId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SITE_MANAGEMENT_ROLE_REQUIRED);            
        }          
        try {
            nodeRepository.save(node);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }
        // force the re-query to ensure a complete result view if updated
        return nodeRepository.findOne(node.getNodeId());
    }    
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.PUT,
                    consumes = HEADER_JSON_MEDIA_TYPE, 
                    produces = HEADER_JSON_MEDIA_TYPE)
    public @ResponseBody Node updateNode(
           @RequestHeader(HEADER_LOGIN_NAME) String loginName,            
           @PathVariable(ID_URL_PATH_VAR) Integer id, 
           @RequestBody Node node) 
    {
        // find the requested resource
        Node foundNode = nodeRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (foundNode == null) {
            throw new ResourceNotFoundException(id);            
        }
        // if the ID in the request body is null, use the ID parsed from the URL
        // if the ID is found in the request body but does not match the ID in 
        // the current data, then throw a ConflictException (409)
        Integer updateID = node.getNodeId();
        if (updateID == null) {
            node.setNodeId(id);
        } else if (!node.getNodeId().equals(foundNode.getNodeId())) {
            throw new ConflictException(node.getNodeId(),foundNode.getNodeId()); 
        }

        // check that the user can perform the update
        if (!registryService.userCanManageNode(loginName, node.getNodeId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SITE_MANAGEMENT_ROLE_REQUIRED);            
        }            
        try {
            nodeRepository.save(node);
        } catch (DataIntegrityViolationException e) {
            log.warn(e);
            throw new ConflictException(e.getMostSpecificCause());
        }        
        // force the re-query to ensure a complete result view if updated
        return nodeRepository.findOne(node.getNodeId());
    }     
    
    @RequestMapping(value = ENTITY_PATH,
                    method = RequestMethod.DELETE) 
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void removeNode(
        @RequestHeader(HEADER_LOGIN_NAME) String loginName,            
        @PathVariable(ID_URL_PATH_VAR) Integer id) 
    {
        // find the requested resource
        Node node = nodeRepository.findOne(id);
        // if the ID is not found then throw a ResourceNotFoundException (404)
        if (node == null) {
            throw new ResourceNotFoundException(id);            
        }
        // check that the user can perform the delete
        if (!registryService.userCanManageNode(loginName, node.getNodeId())) {
            throw new ForbiddenException(
                loginName,
                RegistryServiceConstants.MSG_SITE_MANAGEMENT_ROLE_REQUIRED);            
        }               
        nodeRepository.delete(id);
    } 
  
}
