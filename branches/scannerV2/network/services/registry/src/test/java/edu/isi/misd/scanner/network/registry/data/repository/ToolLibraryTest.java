
package edu.isi.misd.scanner.network.registry.data.repository;

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisTool;
import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import java.util.ArrayList;
import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-repository-context.xml")
public class ToolLibraryTest 
{   
	@Autowired
	ToolLibraryRepository toolLibraryRepository;
        
	AnalysisTool tool;
    ToolLibrary toolLibrary;
    
    MappingJackson2HttpMessageConverter converter = 
        new MappingJackson2HttpMessageConverter();
    
	@Before
	public void setUp() 
    {
        toolLibrary = new ToolLibrary();
		tool = new AnalysisTool();        
        ArrayList<AnalysisTool> toolList = new ArrayList<AnalysisTool>();
        toolList.add(tool);  
        
        toolLibrary.setLibraryName("Test Tool Library");
        toolLibrary.setDescription("Fake tool library description");
        toolLibrary.setLibraryVersion("1");
        toolLibrary.setAnalysisTools(toolList);        
      
        tool.setToolName("Test Tool");
        tool.setToolDescription("Automated test fake tool");
        tool.setCuratorUid(007);
        tool.setInformationEmail("nobody@localhost");
        tool.setInputFormatSpecifications("input specifications");
        tool.setOutputFormatSpecifications("output specifications");
        tool.setToolParentLibrary(toolLibrary);
        
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
        
	}

    @Test
    public void testJacksonConversion() 
        throws Exception
    {
        assertCanBeMapped(ToolLibrary.class);
        assertCanBeMapped(AnalysisTool.class);
    }

    private void assertCanBeMapped(Class<?> classToTest)
    {
        String deser =
            String.format("%s is not deserializable by Jackson, check @Json annotations",
            classToTest.getName());
        assertTrue(deser, converter.canRead(classToTest, MediaType.APPLICATION_JSON));
        
        String ser =
            String.format("%s is not serializable by Jackson, check @Json annotations",
            classToTest.getName());
        assertTrue(ser, converter.canWrite(classToTest, MediaType.APPLICATION_JSON));        
    }
    
}
