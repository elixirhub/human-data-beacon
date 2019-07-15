package org.ega_archive.elixircore.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

import org.ega_archive.elixircore.constant.CoreConstants;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Header {

  @XmlElement(name = "api_version")
  private String apiVersion = "v1";

  @XmlElement(name = "code")
  private String code = "200";

  @XmlElement(name = "service")
  private String service = "service";

  @XmlElement(name = "developer_message")
  private String developerMessage = "";

  @XmlElement(name = "user_message")
  private String userMessage = "OK";

  @XmlElement(name = "error_code")
  private String errorCode = "1";

  @XmlElement(name = "doc_link")
  private String docLink = "https://ega.crg.eu";

  @XmlElement(name = "error_stack")
  private String errorStack = "";

  public Header() {
    this.service = CoreConstants.MICROSERVICE;
    this.apiVersion = CoreConstants.API_VERSION;
  }

}
