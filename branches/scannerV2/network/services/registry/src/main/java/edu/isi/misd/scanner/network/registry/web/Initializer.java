package edu.isi.misd.scanner.network.registry.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 
 */
public class Initializer implements WebApplicationInitializer 
{
	@Override
	public void onStartup(ServletContext container) {
		// Create the 'root' Spring application context
		AnnotationConfigWebApplicationContext rootContext = 
            new AnnotationConfigWebApplicationContext();
        rootContext.setDisplayName("SCANNER Registry");        
		rootContext.register(AppConfig.class);

		// Manage the lifecycle of the root application context
		container.addListener(new ContextLoaderListener(rootContext));

		// Register and map the dispatcher servlet
		ServletRegistration.Dynamic dispatcher = 
            container.addServlet(
                "dispatcher", new DispatcherServlet(rootContext));
		dispatcher.setLoadOnStartup(2);
		dispatcher.addMapping("/registry/*");
	}
}
