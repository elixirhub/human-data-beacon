package org.ega_archive.elixircore.dto.auth;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "drupal_requester_user")
public class DrupalRequesterUser {

  @XmlElement(name = "uid")
  private Integer uid;

  @XmlElement(name = "access")
  private Integer access;

  @XmlElement(name = "created")
  private Integer created;

  @XmlElement(name = "data")
  private byte[] data;

  @XmlElement(name = "init")
  private String init;

  @XmlElement(name = "authorities")
  private String language;

  @XmlElement(name = "login")
  private Integer login;

  @XmlElement(name = "mail")
  private String mail;

  @XmlElement(name = "name")
  private String name;

  @XmlElement(name = "pass")
  private String pass;

  @XmlElement(name = "picture")
  private Integer picture;

  @XmlElement(name = "signature")
  private String signature;

  @XmlElement(name = "signature_format")
  private String signatureFormat;

  @XmlElement(name = "status")
  private byte status;

  @XmlElement(name = "theme")
  private String theme;

  @XmlElement(name = "timezone")
  private String timezone;

  @XmlElement(name = "uuid")
  private String uuid;

//  private List<Role> roles;

}