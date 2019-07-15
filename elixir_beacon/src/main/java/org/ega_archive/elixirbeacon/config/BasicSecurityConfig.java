package org.ega_archive.elixirbeacon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@Profile("basic")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class BasicSecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS); //Do not create sessions

    http.formLogin().disable();

    http.csrf().disable();

    http.httpBasic();

    http.authorizeRequests()
        .antMatchers("/v1/info").authenticated()
        .antMatchers("/v1/metrics/**").authenticated()
        .antMatchers("/v1/dump").authenticated()
        .antMatchers("/v1/trace").authenticated()
        .antMatchers("/v1/mappings").authenticated()
        .antMatchers("/v1/config/**").authenticated()
        .antMatchers("/v1/autoconfig").authenticated()
        .antMatchers("/v1/beans").authenticated()
        .antMatchers("/v1/health").authenticated()
        .antMatchers("/v1/configprops").authenticated()
        .antMatchers("/v1/login").permitAll();
  }
  //END CONFIGURATION

}
