package org.ega_archive.elixircore.service;

import java.util.List;

import org.ega_archive.elixircore.dto.ServiceLocation;
import org.ega_archive.elixircore.enums.ServiceType;

public interface LocatorService {

  public List<ServiceLocation> getServices(ServiceType type);

  public List<ServiceLocation> getServices(ServiceType type, String version);

  public ServiceLocation getService(ServiceType type);

  public ServiceLocation getService(ServiceType type, String version);

  public String getServiceUrl(ServiceType type);

  public String getServiceUrl(ServiceType type, String version);
  
  public String getServiceUrlById(String serviceId);

  public List<String> getServicesUrl(ServiceType type);

  public List<String> getServicesUrl(ServiceType type, String version);

  public void resetAllEntries();
  
}
