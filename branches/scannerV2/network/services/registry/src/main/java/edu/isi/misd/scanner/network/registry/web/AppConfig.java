package edu.isi.misd.scanner.network.registry.web;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 *
 */
@Configuration
@EnableWebMvc
@ImportResource("classpath:repository-context.xml")
@EnableJpaRepositories("edu.isi.misd.scanner.network.registry.data.repository")
@ComponentScan(basePackages = "edu.isi.misd.scanner.network.registry")
public class AppConfig extends WebMvcConfigurationSupport 
{
    @Bean
    public DomainClassConverter<?> domainClassConverter() {
      return new DomainClassConverter<FormattingConversionService>(mvcConversionService());
    }
}


