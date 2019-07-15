package org.ega_archive.elixircore.config;

import java.sql.Timestamp;
import java.util.Arrays;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.ega_archive.elixircore.converter.CustomBeanUtilsBean;
import org.ega_archive.elixircore.converter.DatetimeToTimestampConverter;
import org.ega_archive.elixircore.converter.TimestampToDatetimeConverter;
import org.ega_archive.elixircore.filter.CorrelationHeaderFilter;
import org.ega_archive.elixircore.filter.CustomClientAddressFilter;
import org.ega_archive.elixircore.filter.LogRequestFilter;
import org.ega_archive.elixircore.filter.LowercaseRequestParamsFilter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebInitializer implements ServletContextInitializer {

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    // This method adds a Filter to our Servlet so it "intercepts" the servlets calls
    // and if an action method is found, it translate the httpRequest from a get to the method
    // specified in the action parameter.
    
//    CustomHiddenHttpMethodFilter hiddenHttpMethodFilter = new CustomHiddenHttpMethodFilter();
//    hiddenHttpMethodFilter.setMethodParam("action");
//    servletContext.addFilter("hiddenHttpMethodFilter", hiddenHttpMethodFilter)
//        .addMappingForUrlPatterns(null, true, "/*");

    CustomClientAddressFilter xForwardedForFilter = new CustomClientAddressFilter();
    servletContext.addFilter("customXForwardedForFilter", xForwardedForFilter)
        .addMappingForUrlPatterns(null, true, "/*");

    LogRequestFilter logRequestFilter = new LogRequestFilter();
    servletContext.addFilter("logRequestFilter", logRequestFilter).addMappingForUrlPatterns(null,
        true, "/*");

    LowercaseRequestParamsFilter lowercaseRequestParamsFilter = new LowercaseRequestParamsFilter();
    servletContext.addFilter("lowercaseRequestParamsFilter", lowercaseRequestParamsFilter)
        .addMappingForUrlPatterns(null, true, "/*");

//    FilterRegistration.Dynamic corsFilter =
//        servletContext.addFilter("CORSFilter", CORSFilter.class);
//    corsFilter.addMappingForUrlPatterns(null, false, "/*");

    // Set Joda's DateTime default time zone to UTC
    DateTimeZone.setDefault(DateTimeZone.UTC);

    //Register CustomBeanUtilsBean
    BeanUtilsBean.setInstance(new CustomBeanUtilsBean());
    //Register typeConverter
    TimestampToDatetimeConverter timestampToDatetimeConverter = new TimestampToDatetimeConverter();
    ConvertUtils.register(timestampToDatetimeConverter, DateTime.class);
    DatetimeToTimestampConverter datetimeToTimestampConverter = new DatetimeToTimestampConverter();
    ConvertUtils.register(datetimeToTimestampConverter, Timestamp.class);
  }

  //Register correlationId filter
  @Bean
  public FilterRegistrationBean correlationHeaderFilter() {
    FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
    filterRegBean.setFilter(new CorrelationHeaderFilter());
    filterRegBean.setUrlPatterns(Arrays.asList("/*"));
    return filterRegBean;
  }

}
