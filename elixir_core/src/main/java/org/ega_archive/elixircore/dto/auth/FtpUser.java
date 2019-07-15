package org.ega_archive.elixircore.dto.auth;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.ega_archive.elixircore.enums.LoginType;

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
@XmlRootElement(name = "ftp_user")
@XmlSeeAlso(value = {LoginType.class})
public class FtpUser {

  @XmlElement(name = "ftp_id")
  private String ftpId;

  @XmlElement(name = "ftp_password")
  private String password;

  @XmlElement(name = "login_type")
  private LoginType loginType;

  @XmlElement(name = "auth_provider_id")
  private String authProviderId;

}
