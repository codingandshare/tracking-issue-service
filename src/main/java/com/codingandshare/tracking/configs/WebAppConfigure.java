package com.codingandshare.tracking.configs;

import com.codingandshare.tracking.converters.IssueStatusConverter;
import com.codingandshare.tracking.converters.UserStatusConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * The class contain config web application
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Configuration
public class WebAppConfigure implements WebMvcConfigurer {

  /**
   * Init instance for parse Object to json string
   *
   * @return {@link ObjectMapper}
   */
  @Bean(name = "objectMapperJson")
  public ObjectMapper objectMapper() {
    return new ObjectMapper();
  }

  /**
   * Add custom formatter
   * Enum converter
   *
   * @param registry
   */
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new IssueStatusConverter());
    registry.addConverter(new UserStatusConverter());
  }
}
