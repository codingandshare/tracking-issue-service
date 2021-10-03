package com.codingandshare.tracking.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Define domain relate issue_view in database
 *
 * @author Nhan Dinh
 * @since 10/2/21
 **/
@Getter
@Entity
@Table(name = "issue_view")
public class IssueView {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @JsonIgnore
  private Integer versionId;

  private String shortDescription;

  private String ticket;

  private String severity;

  private String priority;

  @JsonIgnore
  private Integer reporter;

  @JsonIgnore
  private Integer fixer;

  @Enumerated(EnumType.STRING)
  private IssueStatus status;

  @JsonIgnore
  private Integer modifiedBy;

  private Timestamp modifiedTime;

  private String changedNote;

  private String fullNameFixer;

  private String fullNameReporter;

  private String fullNameActor;
}
