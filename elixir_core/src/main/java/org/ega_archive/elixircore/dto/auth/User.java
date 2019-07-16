package org.ega_archive.elixircore.dto.auth;

import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Builder;

import org.ega_archive.elixircore.deserializer.GrantedAuthorityJsonDeserializer;
import org.ega_archive.elixircore.enums.LoginType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "user")
//@XmlSeeAlso(value = {GrantedAuthority.class}) JAXB can't handle interfaces!!
public class User implements UserDetails {

  /**
   *
   */
  private static final long serialVersionUID = 4960381520462667855L;
  
  @JsonProperty("authorities")
//  @XmlTransient
  private HashSet<GrantedAuthority> authorities;
  
  @XmlElement(name = "user_id")
  private String userId;
  
  @XmlElement(name = "username")
  private String username;
  
  @XmlElement(name = "password")
  private String password;
  
  @XmlElement(name = "full_name")
  private String fullName;
  
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
  
  @XmlElement(name = "login_type")
  private LoginType loginType;
  
  @XmlElement(name = "enabled")
  private Boolean enabled = true;
  
  @XmlElement(name = "credentials_non_expired")
  private Boolean credentialsNonExpired = true;
  
  @XmlElement(name = "account_non_locked")
  private Boolean accountNonLocked = true;
  
  @XmlElement(name = "account_non_expired")
  private Boolean accountNonExpired = true;
  
//  private MongoSubmitterUser mongoSubmitterUser;
  
  private PostgresSubmitterUser postgresSubmitterUser;
  
  private MongoInternalUser mongoInternalUser;
  
  private DrupalRequesterUser drupalRequesterUser;

  private List<FtpUser> ftpUsers;

  @Override
  public HashSet<GrantedAuthority> getAuthorities() {
    if (authorities == null) {
      return new HashSet<GrantedAuthority>();
    } else {
      return authorities;
    }
  }

  @JsonDeserialize(contentUsing = GrantedAuthorityJsonDeserializer.class)
  public void setAuthorities(HashSet<GrantedAuthority> authorities) {
    this.authorities = authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return this.credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public String toString() {
    return String.format(
        "User[id=%s, name='%s', email='%s', institute='%s',telephone='%s',fax='%s',address='%s', loginType='%s']",
        userId, fullName, email, institute, telephone, fax, address, loginType);
  }

  public String retrieveSubmitterId() {
    return postgresSubmitterUser != null ? postgresSubmitterUser.getDirName() : null;
  }

  public String retrieveCenterName() {
    return postgresSubmitterUser != null ? postgresSubmitterUser.getCenterName() : null;
  }

  public String retrieveRequesterId() {
    return drupalRequesterUser != null ? drupalRequesterUser.getName() : null;
  }

  public boolean hasRoleAdmin() {
    return authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")) ? true : false;
  }
  
  public boolean hasRoleSubmitter() {
    return authorities.contains(new SimpleGrantedAuthority("ROLE_SUBMITTER")) ? true : false;
  }
  
  public boolean hasRoleRequester() {
    return authorities.contains(new SimpleGrantedAuthority("ROLE_REQUESTER")) ? true : false;    
  }
  
}

