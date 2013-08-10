
package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisTool;
import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-repository-context.xml")
public class AnalysisToolTest 
{   
	@Autowired
	ToolLibraryRepository toolLibraryRepository;
    
	@Autowired
	AnalysisToolRepository analysisToolsRepository;
    
	AnalysisTool tool;
    ToolLibrary toolLibrary;
    
	@Before
	public void setUp() 
    {
        toolLibrary = new ToolLibrary();
        toolLibrary.setLibraryName("Test Tool Library");
        toolLibrary.setDescription("Fake tool library description");
        toolLibrary.setLibraryVersion("1");
        
		tool = new AnalysisTool();
        tool.setToolName("Test Tool");
        tool.setToolDescription("Automated test fake tool");
        tool.setCuratorUid(007);
        tool.setInformationEmail("nobody@localhost");
        tool.setInputFormatSpecifications("input specifications");
        tool.setOutputFormatSpecifications("output specifications");
        tool.setToolParentLibraryId(toolLibrary);
        
	}
  
	/**
	 * Tests inserting a tool and asserts it can be loaded again.
	 */
	@Test
	public void testInsertTool() 
    {
        toolLibrary = toolLibraryRepository.save(toolLibrary);
		assertEquals(toolLibrary, 
            toolLibraryRepository.findOne(toolLibrary.getLibraryId()));
        
		tool = analysisToolsRepository.save(tool);
		assertEquals(tool, 
            analysisToolsRepository.findOne(tool.getToolId()));
	}

}
