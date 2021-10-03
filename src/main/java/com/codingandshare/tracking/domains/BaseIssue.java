package com.codingandshare.tracking.domains;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * Base class use by issue and issue_history
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@MappedSuperclass
public class BaseIssue extends AuditBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private Integer versionId;

  @Column(nullable = false)
  private String shortDescription;

  @Column(columnDefinition = "text")
  private String description;

  @Column(nullable = false)
  private String ticket;

  private String platform;

  private String browser;

  private String severity;

  private String priority;

  @Column(nullable = false)
  private Integer reporter;

  @Column(nullable = false)
  private Integer fixer;

  @Enumerated(EnumType.STRING)
  private IssueStatus status;

  @Column(columnDefinition = "text")
  private String rootcause;

  @Column(columnDefinition = "text")
  private String note;

}
