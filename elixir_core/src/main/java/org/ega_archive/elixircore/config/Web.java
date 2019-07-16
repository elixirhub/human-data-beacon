package org.ega_archive.elixircore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.support.DomainClassConverter;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class Web extends WebMvcConfigurerAdapter {

  @Autowired
  private ApplicationContext context;

  // This configures content negotiation, by default returns a json Param -> outputformat=json or
  // Header -> Header Accept: Application/json Also DISABLES extension format like in
  // /notifications.json
  @Override
  public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.favorParameter(true).parameterName("outputformat").favorPathExtension(false)
        .ignoreAcceptHeader(true).defaultContentType(MediaType.APPLICATION_JSON)
        .mediaType("xml", MediaType.APPLICATION_XML).mediaType("json", MediaType.APPLICATION_JSON);
  }

  // This beans and methods below enable a Sort handler in the controllers so if the method
  // signature requires a Sort, like @RequestParam int number, Sort sort it translates from a string
  // sort=id,desc or sort=userId,desc to a Spring domain Sort object
  @Bean
  public SortHandlerMethodArgumentResolver sortResolver() {
    SortHandlerMethodArgumentResolver sorthanderArgumentResolver =
        new SortHandlerMethodArgumentResolver();
    return sorthanderArgumentResolver;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

    argumentResolvers.add(sortResolver());
  }

  @Override
  public void addFormatters(FormatterRegistry registry) {

    if (!(registry instanceof FormattingConversionService)) {
      return;
    }

    registerDomainClassConverterFor((FormattingConversionService) registry);
  }

  private void registerDomainClassConverterFor(FormattingConversionService conversionService) {

    DomainClassConverter<FormattingConversionService> converter =
        new DomainClassConverter<FormattingConversionService>(conversionService);
    converter.setApplicationContext(context);
  }

}
