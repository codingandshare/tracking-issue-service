package com.codingandshare.tracking.dtos;

import com.codingandshare.tracking.domains.Issue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Define request body for create or update issue
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Data
public class IssueRequest {

  @NotNull(message = "versionId is missing")
  private Integer versionId;

  @NotBlank(message = "shortDescription is missing")
  private String shortDescription;

  private String description;

  @NotBlank(message = "ticket is missing")
  private String ticket;

  private String platform;

  private String browser;

  @NotBlank(message = "severity is missing")
  private String severity;

  @NotBlank(message = "priority is missing")
  private String priority;

  @NotNull(message = "reporter is missing")
  private Integer reporter;

  @NotNull(message = "fixer is missing")
  private Integer fixer;

  private String rootcause;

  private String note;

  private static Issue convertIssue(Issue issue, IssueRequest issueRequest) {
    issue.setVersionId(issueRequest.getVersionId());
    issue.setShortDescription(issueRequest.getShortDescription());
    issue.setDescription(issueRequest.getDescription());
    issue.setTicket(issueRequest.getTicket());
    issue.setPlatform(issueRequest.getPlatform());
    issue.setBrowser(issueRequest.getBrowser());
    issue.setSeverity(issueRequest.getSeverity());
    issue.setPriority(issueRequest.getPriority());
    issue.setReporter(issueRequest.getReporter());
    issue.setFixer(issueRequest.getFixer());
    issue.setRootcause(issueRequest.getRootcause());
    issue.setNote(issueRequest.getNote());
    return issue;
  }

  public Issue toNewIssue() {
    Issue issue = new Issue();
    return convertIssue(issue, this);
  }

  public Issue toUpdateIssue(Issue issue) {
    Issue issueUpdating = convertIssue(issue, this);
    issueUpdating.setTicket(issue.getTicket());
    return issueUpdating;
  }
}
