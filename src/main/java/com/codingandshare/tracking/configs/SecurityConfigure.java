package com.codingandshare.tracking.configs;

import com.codingandshare.tracking.components.JwtAuthenticationFilter;
import com.codingandshare.tracking.components.RestAccessDeniedHandler;
import com.codingandshare.tracking.components.RestAuthenticationEntryPoint;
import com.codingandshare.tracking.domains.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Security config for web application
 * Check authentication and check authorize roles
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    prePostEnabled = true,
    securedEnabled = true,
    jsr250Enabled = true
)
public class SecurityConfigure extends WebSecurityConfigurerAdapter {

  @Autowired
  @Qualifier("authenUserService")
  private UserDetailsService userDetailsService;

  @Autowired
  private RestAccessDeniedHandler restAccessDeniedHandler;

  @Autowired
  private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

  @Autowired
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  /**
   * New impl for PasswordEncoder
   *
   * @return PasswordEncoder
   */
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Override authentication default of spring securiy
   *
   * @return {@link AuthenticationManager}
   * @throws Exception authen failed
   */
  @Bean(BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    try {
      return super.authenticationManagerBean();
    } catch (AuthenticationException e) {
      log.error("Authentication failed", e);
      throw e;
    }
  }

  /**
   * Config service provider for get source user profile
   * Config encoder password
   *
   * @param auth
   * @throws Exception
   */
  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
        .userDetailsService(this.userDetailsService)
        .passwordEncoder(passwordEncoder());
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
        .csrf()
        .disable()
        .exceptionHandling()
        .authenticationEntryPoint(this.restAuthenticationEntryPoint)
        .accessDeniedHandler(this.restAccessDeniedHandler)
        .and()
        .headers()
        .contentSecurityPolicy(
            "default-src 'self'; frame-src 'self' data:; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' " +
                "'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
        .and()
        .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
        .and()
        .contentSecurityPolicy("geolocation 'none'; midi 'none'; sync-xhr 'none'; " +
            "microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; " +
            "speaker 'none'; fullscreen 'self'; payment 'none'")
        .and()
        .frameOptions().deny()
        .and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers("/auth/*").permitAll()
        .antMatchers("/tracking/*", "/user/*").authenticated()
        .antMatchers("/user/*").hasAuthority(UserRole.ADMIN.value)
        .antMatchers(
            "/tracking/user/*", "/tracking/issue/*", "/tracking/version/*"
        ).hasAnyAuthority(UserRole.ADMIN.value, UserRole.USER.value)
        .and()
        .httpBasic();
    http.addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
  }
}
