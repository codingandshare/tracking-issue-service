package com.codingandshare.tracking

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

/**
 * Testing for load spring context and liquibase run as expected
 *
 * @author Nhan Dinh
 * @since 10/24/21
 */
@SpringBootTest
@ActiveProfiles('tests')
class TrackingIssueServiceApplicationSpec extends Specification {

  @Autowired
  ApplicationContext applicationContext

  def 'Load spring context'() {
    expect: 'Loaded context'
    applicationContext != null
  }
}
