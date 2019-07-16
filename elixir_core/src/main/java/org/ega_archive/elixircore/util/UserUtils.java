package org.ega_archive.elixircore.util;

import java.util.HashSet;
import java.util.List;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.ega_archive.elixircore.dto.auth.SessionUser;
import org.ega_archive.elixircore.dto.auth.User;
import org.ega_archive.elixircore.enums.ServiceType;
import org.ega_archive.elixircore.event.sender.RestEventSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Log4j
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SYSTEM','ROLE_SYSTEM_BASIC','ROLE_SUBMITTER','ROLE_REQUESTER')")
@Component
public class UserUtils {

  @Autowired
  private RestEventSender restEventSender;

  public static User getUserFromContext() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = null;
    if (authentication != null) {
      if (authentication.getPrincipal() instanceof User ) {
        user = (User) authentication.getPrincipal();
      } else if(authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
        org.springframework.security.core.userdetails.User userFrom = (org.springframework.security.core.userdetails.User)authentication.getPrincipal();
        user = User.builder()
            .userId(userFrom.getUsername())
            .username(userFrom.getUsername())
            .authorities(new HashSet<GrantedAuthority>(userFrom.getAuthorities()))
            .password(userFrom.getPassword())
            .accountNonExpired(userFrom.isAccountNonExpired())
            .accountNonLocked(userFrom.isAccountNonLocked())
            .credentialsNonExpired(userFrom.isCredentialsNonExpired())
            .enabled(userFrom.isEnabled())
            .build();
      } else if (authentication.getPrincipal() instanceof SessionUser) {
        SessionUser sessionUser = (SessionUser) authentication.getPrincipal();
        user = sessionUser.getUser();
      }
    }
    return user;
  }

  /**
   * Finds the internal user Id that corresponds to this ftpUser.
   * 
   * @param ftpUser FTP user.
   * @return internal user Id.
   */
  public String findUserIdFromFtpUser(String ftpUser) {
    List<String> usersList =
        restEventSender.sendMicroserviceObject(ServiceType.AUTH, "ftp/map/" + ftpUser,
            HttpMethod.GET, String.class);
    String userId = null;
    if (usersList.size() == 1) {
      userId = usersList.get(0);
      log.debug("userId = " + userId);
    }
    if(StringUtils.isBlank(userId)) {
      log.error("No matching user found for this FTP user: " + ftpUser);
    }
    return userId;
  }
  
  public static boolean currentUserIsASubmitter() {
    User user = getUserFromContext();
    return user != null && user.hasRoleSubmitter() ? true : false;
  }
  
  public static boolean currentUserIsARequester() {
    User user = getUserFromContext();
    return user != null && user.hasRoleRequester() ? true : false;
  }
  
  public static boolean currentUserIsAnAdmin() {
    User user = getUserFromContext();
    return user != null && user.hasRoleAdmin() ? true : false;
  }
  
}
