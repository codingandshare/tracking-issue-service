package com.codingandshare.tracking.domains;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Entity
@Table(name = "issue_history")
public class IssueHistory extends BaseIssue {

  private Integer issueId;

  private String changedNote;

  public IssueHistory(Issue issue, String changedNote) {
    this.setVersionId(issue.getVersionId());
    this.setShortDescription(issue.getShortDescription());
    this.setDescription(issue.getDescription());
    this.setTicket(issue.getTicket());
    this.setPlatform(issue.getPlatform());
    this.setBrowser(issue.getBrowser());
    this.setSeverity(issue.getSeverity());
    this.setPriority(issue.getPriority());
    this.setReporter(issue.getReporter());
    this.setFixer(issue.getFixer());
    this.setStatus(issue.getStatus());
    this.setRootcause(issue.getRootcause());
    this.setNote(issue.getNote());
    this.setCreatedBy(issue.getCreatedBy());
    this.setModifiedBy(issue.getModifiedBy());
    this.issueId = issue.getId();
    this.changedNote = changedNote;
  }

  public IssueHistory() {
  }
}
