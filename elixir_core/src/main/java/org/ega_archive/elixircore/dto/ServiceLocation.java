package org.ega_archive.elixircore.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.ega_archive.elixircore.enums.ServiceType;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceLocation {

  @XmlElement(name = "service_id")
  private String serviceId;

  @XmlElement(name = "name")
  private String name;

  @XmlElement(name = "server")
  private String server;

  @XmlElement(name = "port")
  private String port;

  @XmlElement(name = "version")
  private String version;

  @XmlElement(name = "type")
  private ServiceType type;
  
  @XmlElement(name = "base_url")
  private String baseUrl;
  
  @XmlElement(name = "secure")
  private boolean secure;

  @Override
  public String toString() {
    return String.format("ServiceLocation[name='%s', server='%s', port='%s']", name, server, port);
  }

  public String toUrl() {
    String protocol = "http";
    if(secure) {
      protocol += "s";
    }
    return protocol + "://" + server + ":" + port + "/" + baseUrl + name + "/" + version + "/";
  }

}
