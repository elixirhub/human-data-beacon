package org.ega_archive.elixircore.dto.auth;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.ega_archive.elixircore.enums.LoginType;
import org.ega_archive.elixircore.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Builder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "user_dto")
@XmlSeeAlso(value = {LoginType.class, UserRole.class})
public class UserDto {
  
  @XmlElement(name = "user_id")
  private String userId;
  
  @XmlElement(name = "username")
  private String username;
  
  @XmlElement(name = "ega_box")
  private String egaBox;
  
  @XmlElement(name = "password")
  private String password;
  
  @XmlElement(name = "full_name")
  private String fullName;
  
  @XmlElement(name = "email")
  private String email;
  
  @XmlElement(name = "institute")
  private String institute;
  
  @XmlElement(name = "acronym")
  private String acronym;
  
  @XmlElement(name = "country")
  private String country;
  
  @XmlElement(name = "region")
  private String region;
  
  @XmlElement(name = "address")
  private String address;

  @XmlElement(name = "telephone")
  private String telephone;
  
  @XmlElement(name = "fax")
  private String fax;
  
  @XmlElement(name = "sequence_type")
  private String sequenceType;
  
  @XmlElement(name = "login_type")
  private LoginType loginType;
  
  @XmlElement(name = "roles")
  private List<UserRole> roles;
  
  @XmlElement(name = "ftp_box")
  private List<String> ftpBoxes;
  
}
