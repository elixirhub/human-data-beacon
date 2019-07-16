package org.ega_archive.elixirbeacon.config;

import org.ega_archive.elixircore.filter.RestTokenPreAuthenticatedProcessingFilter;
import org.ega_archive.elixircore.security.RestTokenAuthenticationUserDetailsService;
import org.ega_archive.elixircore.security.RestWebAuthenticationDetailsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

@Configuration
@Profile("xtoken")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class XTokenSecurityConfig extends WebSecurityConfigurerAdapter {

  // REST token authentication Provider and filter
  @Bean
  public AuthenticationDetailsSource restWebAuthenticationDetailsSource() {
    return new RestWebAuthenticationDetailsSource();
  }

  @Bean
  public RestTokenPreAuthenticatedProcessingFilter restTokenPreAuthenticatedProcessingFilter(
      final AuthenticationManager authenticationManager) {
    RestTokenPreAuthenticatedProcessingFilter
        filter =
        new RestTokenPreAuthenticatedProcessingFilter();
    filter.setAuthenticationManager(authenticationManager);
    filter.setInvalidateSessionOnPrincipalChange(true);
    filter.setCheckForPrincipalChanges(false);
    filter.setContinueFilterChainOnUnsuccessfulAuthentication(false);
    filter.setAuthenticationDetailsSource(restWebAuthenticationDetailsSource());
    return filter;
  }

  @Bean
  public RestTokenAuthenticationUserDetailsService restTokenAuthenticationUserDetailsService() {
    return new RestTokenAuthenticationUserDetailsService();
  }

  @Bean
  public AuthenticationProvider restTokenAuthenticationProvider() {
    PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
    provider.setPreAuthenticatedUserDetailsService(restTokenAuthenticationUserDetailsService());
    return provider;
  }
  //END REST token authentication Provider

  //Access Authentication Manager Bean
  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
  //END Access Authentication Manager Bean

  //CONFIGURATION
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    // Add auth provider for token
    auth.authenticationProvider(restTokenAuthenticationProvider());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS); //Do not create sessions

    http.formLogin().disable();

    http.csrf().disable();

    //http.authorizeRequests().antMatchers("/**").permitAll();
    http.authorizeRequests()
        .antMatchers("/v1/info").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers("/v1/metrics/**").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers("/v1/dump").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers("/v1/trace").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers("/v1/mappings").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers("/v1/config/**").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers("/v1/autoconfig").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers("/v1/beans").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers("/v1/health").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers("/v1/configprops").hasAnyRole("ADMIN", "SYSTEM", "SYSTEM_BASIC")
        .antMatchers("/v1/login").permitAll();

    http.addFilterBefore(restTokenPreAuthenticatedProcessingFilter(authenticationManagerBean()),
        UsernamePasswordAuthenticationFilter.class);
  }
  //END CONFIGURATION

}
