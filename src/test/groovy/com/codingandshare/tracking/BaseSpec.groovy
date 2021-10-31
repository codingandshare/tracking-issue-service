package com.codingandshare.tracking

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.util.StdDateFormat
import groovy.json.JsonSlurper
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import spock.lang.Specification

/**
 * Base class for define test cases
 *
 * @author Nhan Dinh
 * @since 11/24/21
 */
class BaseSpec extends Specification {

  static final JsonSlurper SLURPER = new JsonSlurper()
  static final ObjectMapper MAPPER = new ObjectMapper()
  static final HttpHeaders HEADERS = new HttpHeaders()

  static {
    MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL)
    HEADERS.setContentType(MediaType.APPLICATION_JSON)
  }

  static final String formatDateUTC(Date date) {
    new StdDateFormat().withColonInTimeZone(true).format(date)
  }
}
