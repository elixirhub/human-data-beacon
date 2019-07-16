package org.ega_archive.elixircore.service;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.ega_archive.elixircore.dto.Base;
import org.ega_archive.elixircore.dto.ServiceLocation;
import org.ega_archive.elixircore.enums.ServiceType;
import org.ega_archive.elixircore.event.sender.RestEventSender;
import org.ega_archive.elixircore.exception.NotFoundException;
import org.ega_archive.elixircore.exception.ServiceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Log4j
@Service
public class LocatorServiceImpl implements LocatorService {

  @Autowired
  private Environment environment;

  private static final String NOT_FOUND = "Service Not Found";

  private static String VERSION;
  private static String CONFIG_URL;
  
  @Autowired
  private RestEventSender restEventSender;

  private List<ServiceLocation> getServices() {

    Base<ServiceLocation> result =
        restEventSender.sendMicroservice(CONFIG_URL + "/services/", HttpMethod.GET, null, null,
            null, ServiceLocation.class, false);

    if (result.getResponse().getResult().isEmpty()) {
      throw new ServiceNotFoundException(NOT_FOUND);
    }
    return result.getResponse().getResult();
  }

  @Override
  public List<ServiceLocation> getServices(ServiceType type) {
    return getServices(type, VERSION);
  }

  @Override
  public List<ServiceLocation> getServices(ServiceType type, String version) {
    if (type == null) {
      return getServices();
    }
    List<ServiceLocation> resultsList = new ArrayList<ServiceLocation>();

    for (ServiceLocation sl : getServices()) {
      if (sl.getType() == type && sl.getVersion().equals(version)) {
        resultsList.add(sl);
      }
    }
    if (resultsList.isEmpty()) {
      throw new ServiceNotFoundException(NOT_FOUND);
    }
    return resultsList;
  }

  @Override
  public ServiceLocation getService(ServiceType type) {
    return getService(type, VERSION);
  }

  @Override
  public ServiceLocation getService(ServiceType type, String version) {
    for (ServiceLocation sl : getServices()) {
      if (sl.getType() == type && sl.getVersion().equals(version)) {
        return sl;
      }
    }
    throw new ServiceNotFoundException(NOT_FOUND);
  }

  @Override
  @Cacheable(value = "serviceURL")
  public String getServiceUrl(ServiceType type) {
    log.debug("Entering getServiceURL(" + type + ")");
    return getServiceUrl(type, VERSION);
  }

  /**
   * Method to clean cache
   */
  @CacheEvict(value = "serviceURL", allEntries = true)
  public void resetAllEntries() {
    // Intentionally blank
  }

  @Override
  public String getServiceUrl(ServiceType type, String version) {
    ServiceLocation sl = getService(type, version);
    return sl.toUrl();
  }

  @Override
  public List<String> getServicesUrl(ServiceType type) {
    return getServicesUrl(type, VERSION);
  }

  @Override
  public List<String> getServicesUrl(ServiceType type, String version) {
    List<String> resultsList = new ArrayList<String>();

    for (ServiceLocation sl : getServices(type, version)) {
      resultsList.add(sl.toUrl());
    }

    if (resultsList.isEmpty()) {
      throw new ServiceNotFoundException(NOT_FOUND);
    }
    return resultsList;
  }

  @Override
  public String getServiceUrlById(String serviceId) {
    String url = null;
    Base<ServiceLocation> result =
        restEventSender.sendMicroservice(CONFIG_URL + "/services/" + serviceId, HttpMethod.GET,
            null, null, null, ServiceLocation.class, false);
    if (result != null && result.getResponse().getNumTotalResults() == 1) {
      ServiceLocation service = result.getResponse().getResult().get(0);
      url = service.toUrl();
    } else {
      throw new NotFoundException(NOT_FOUND, serviceId);
    }
    return url;
  }

  @PostConstruct
  public void init() throws Exception {
    if (environment.containsProperty("service.locator.version")) {
      VERSION = environment.getProperty("service.locator.version");
    } else {
      VERSION = "v1";
    }
    if (environment.containsProperty("service.locator.url")) {
      CONFIG_URL = environment.getProperty("service.locator.url");
    } else {
      CONFIG_URL ="http://app01:9000/configservice/v1";
    }
  }
}
