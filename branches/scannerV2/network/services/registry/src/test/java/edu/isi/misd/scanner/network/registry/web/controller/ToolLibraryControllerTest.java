package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.AnalysisTool;
import edu.isi.misd.scanner.network.registry.data.domain.ToolLibrary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-repository-context.xml")
public class ToolLibraryControllerTest extends BaseControllerTest
{
    @Test
    public void testJacksonConversion() 
        throws Exception
    {
        assertCanBeMapped(ToolLibrary.class);
        assertCanBeMapped(AnalysisTool.class);
    }    
}
