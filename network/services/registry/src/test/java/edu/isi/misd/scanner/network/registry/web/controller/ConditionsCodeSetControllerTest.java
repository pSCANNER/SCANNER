package edu.isi.misd.scanner.network.registry.web.controller;

import edu.isi.misd.scanner.network.registry.data.domain.ConditionsCodeSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-repository-context.xml")
public class ConditionsCodeSetControllerTest extends BaseControllerTest
{
    @Test
    @Override
    public void testJacksonMapping() 
        throws Exception
    {
        assertCanBeMapped(ConditionsCodeSet.class);
    }    
}
