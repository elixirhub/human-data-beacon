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

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "mongo_submitter_user")
public class MongoInternalUser {

  @XmlElement(name = "id")
  private String id;

  @XmlElement(name = "username")
  private String username;

  @XmlElement(name = "full_name")
  private String fullName;
  
  @XmlElement(name = "password")
  private String password;
  
  @XmlElement(name = "email")
  private String email;
  
  @XmlElement(name = "institute")
  private String institute;
  
  @XmlElement(name = "telephone")
  private String telephone;
  
  @XmlElement(name = "fax")
  private String fax;
  
  @XmlElement(name = "address")
  private String address;

}
