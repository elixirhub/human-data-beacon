package org.ega_archive.elixircore.enums;

import java.util.HashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

/**
 * This enum contains all roles that can be assigned to a user.<br><br>
 * 
 * But there are other roles in the system:
 * <ul> 
 * <li>ROLE_SYSTEM</li>
 * <li>ROLE_SYSTEM_BASIC</li>
 * </ul>
 *
 */
@Log4j
@Getter
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "user_role")
public enum UserRole {

  //If adding a new Role, in order to be saved when creating a new user via managerportal or authservice
  //it has to be added to authservice, see MSVC-45
  ROLE_SUBMITTER("ROLE_SUBMITTER"), ROLE_REQUESTER("ROLE_REQUESTER"), ROLE_ADMIN("ROLE_ADMIN"), ROLE_FTP(
      "ROLE_FTP"), ROLE_INTERNAL("ROLE_INTERNAL");

  private String value;
  
  public static UserRole parse(String role) {
    UserRole userRole = null;
    if(StringUtils.equalsIgnoreCase(ROLE_SUBMITTER.value, role)) {
      userRole = ROLE_SUBMITTER;
    } else if(StringUtils.equalsIgnoreCase(ROLE_REQUESTER.value, role)) {
      userRole = ROLE_REQUESTER;
    } else if(StringUtils.equalsIgnoreCase(ROLE_ADMIN.value, role)) {
      userRole = ROLE_ADMIN;
    } else if(StringUtils.equalsIgnoreCase(ROLE_FTP.value, role)) {
      userRole = ROLE_FTP;
    } else if(StringUtils.equalsIgnoreCase(ROLE_INTERNAL.value, role)) {
      userRole = ROLE_INTERNAL;
    } else {
     log.error("Role not valid"); 
    }
    
    return userRole;
  }
  
  /**
   * Check if there are the same roles in both collections.
   * 
   * @param roles
   * @param authorities
   * @return true if roles contains all role values in authorities; false otherwise.
   */
  public static boolean containsAuthorities(List<UserRole> roles, HashSet<GrantedAuthority> authorities) {
    if (((roles == null || roles.isEmpty()) && authorities != null && !authorities.isEmpty())
        || ((authorities == null || authorities.isEmpty()) && roles != null && !roles.isEmpty())
        || (authorities.size() != roles.size())) {
      /*
       * 1- One collections is empty but the other one is not 
       * or 
       * 2- Collections have different size
       */
      return false;
    }

    boolean result = true;
    for (GrantedAuthority authority : authorities) {
      if (roles.contains(UserRole.parse(authority.getAuthority()))) {
        result &= true;
      } else {
        result = false;
        // Do not continue
        break;
      }
    }
    return result;
  }
  
}
