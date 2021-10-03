package com.codingandshare.tracking.components;

import com.codingandshare.tracking.dtos.ResponseObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Handle response json when status 403
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Slf4j
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

  @Autowired
  @Qualifier("objectMapperJson")
  private ObjectMapper objectMapper;

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException
  ) throws IOException, ServletException {
    log.error("Error access denied", accessDeniedException);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    ResponseObject responseJson = new ResponseObject();
    responseJson.setMessage(accessDeniedException.getMessage());
    response.getOutputStream().print(this.objectMapper.writeValueAsString(responseJson));
  }
}
