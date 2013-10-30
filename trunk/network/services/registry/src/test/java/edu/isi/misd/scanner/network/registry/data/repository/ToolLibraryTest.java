
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
package edu.isi.misd.scanner.network.registry.data.repository; 

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisTool;
import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import java.util.ArrayList;
import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Mike D'Arcy
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-repository-context.xml")
public class ToolLibraryTest 
{   
	@Autowired
	ToolLibraryRepository toolLibraryRepository;
        
	AnalysisTool tool;
    ToolLibrary toolLibrary;    
    
    private static final String LIBRARY_NAME = "Test Tool Library";    
    private static final String LIBRARY_DESC = "Test tool library description";
    private static final String LIBRARY_VERSION = "1";
    private static final String TOOL_NAME = "Test Tool";
    private static final String TOOL_DESC = "Unit test tool instance";
    private static final String TOOL_PATH = "/tool/path";
    private static final String TOOL_INFORMATION_EMAIL = "nobody@localhost";
    private static final String TOOL_INPUT_FORMAT_SPEC = 
        "Unit test input specifications";
    private static final String TOOL_OUTPUT_FORMAT_SPEC = 
        "Unit test output specifications";    
    
	@Before
	public void setUp() 
    {
        toolLibrary = new ToolLibrary();
		tool = new AnalysisTool();        
        ArrayList<AnalysisTool> toolList = new ArrayList<AnalysisTool>();
        toolList.add(tool);  
        
        toolLibrary.setLibraryName(LIBRARY_NAME);
        toolLibrary.setDescription(LIBRARY_DESC);
        toolLibrary.setVersion(LIBRARY_VERSION);
        toolLibrary.setAnalysisTools(toolList);        
      
        tool.setToolName(TOOL_NAME);
        tool.setToolDescription(TOOL_DESC);
        tool.setToolPath(TOOL_PATH);
        tool.setInformationEmail(TOOL_INFORMATION_EMAIL);
        tool.setInputFormatSpecifications(TOOL_INPUT_FORMAT_SPEC);
        tool.setOutputFormatSpecifications(TOOL_OUTPUT_FORMAT_SPEC);
        tool.setToolParentLibrary(toolLibrary);
        
	}
  
	/**
	 * Tests inserting a tool and asserts it can be loaded again. 
     * All fields are checked to ensure child relation was populated correctly.
	 */
	@Test
	public void testInsertTool() 
    {
        toolLibrary = toolLibraryRepository.save(toolLibrary);
        ToolLibrary savedToolLibrary = 
            toolLibraryRepository.findOne(toolLibrary.getLibraryId());
        
        // this only checks the IDs
		assertEquals(toolLibrary, savedToolLibrary);               
        
        // check the rest of the fields
        assertEquals(toolLibrary.getLibraryId(), savedToolLibrary.getLibraryId());  
		assertEquals(toolLibrary.getLibraryName(), savedToolLibrary.getLibraryName());   
		assertEquals(toolLibrary.getVersion(), savedToolLibrary.getVersion());   
		assertEquals(toolLibrary.getDescription(), savedToolLibrary.getDescription());   
//		assertEquals(toolLibrary.getAnalysisTools().get(0).getToolName(),
//                     savedToolLibrary.getAnalysisTools().get(0).getToolName());   
//		assertEquals(toolLibrary.getAnalysisTools().get(0).getToolDescription(),
//                     savedToolLibrary.getAnalysisTools().get(0).getToolDescription());  
//		assertEquals(toolLibrary.getAnalysisTools().get(0).getToolPath(),
//                     savedToolLibrary.getAnalysisTools().get(0).getToolPath());
//        assertEquals(toolLibrary.getAnalysisTools().get(0).getInformationEmail(),
//                     savedToolLibrary.getAnalysisTools().get(0).getInformationEmail());    
//		assertEquals(toolLibrary.getAnalysisTools().get(0).getInputFormatSpecifications(),
//                     savedToolLibrary.getAnalysisTools().get(0).getInputFormatSpecifications());           
//		assertEquals(toolLibrary.getAnalysisTools().get(0).getOutputFormatSpecifications(),
//                     savedToolLibrary.getAnalysisTools().get(0).getOutputFormatSpecifications());           
	}
    
}
