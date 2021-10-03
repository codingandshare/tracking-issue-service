package com.codingandshare.tracking.services;

import com.codingandshare.tracking.domains.*;
import com.codingandshare.tracking.dtos.IssueRequest;
import com.codingandshare.tracking.dtos.UpdateIssueStatusRequest;
import com.codingandshare.tracking.exceptions.NotFoundException;
import com.codingandshare.tracking.repositories.IssueHistoryRepository;
import com.codingandshare.tracking.repositories.IssueRepository;
import com.codingandshare.tracking.repositories.IssueViewRepository;
import com.codingandshare.tracking.specifications.IssueSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Handle business logic relate to issue
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Service
public class IssueService {

  @Autowired
  private IssueRepository issueRepository;

  @Autowired
  private IssueHistoryRepository issueHistoryRepository;

  @Autowired
  private AuthenUserService authenUserService;

  @Autowired
  private IssueViewRepository issueViewRepository;

  @Transactional
  public void addIssue(IssueRequest issueRequest) {
    User userCurrent = this.authenUserService.getUserCurrentLogin();
    Issue issue = issueRequest.toNewIssue();
    issue.setStatus(IssueStatus.OPEN);
    issue.setCreatedBy(userCurrent.getId());
    issue.setModifiedBy(userCurrent.getId());
    this.issueRepository.save(issue);
    this.issueHistoryRepository.save(new IssueHistory(issue, "Created issue"));
  }

  @Transactional
  public void updateIssue(Integer id, IssueRequest issueRequest) {
    User userCurrent = this.authenUserService.getUserCurrentLogin();
    Optional<Issue> issueOptional = this.issueRepository.findById(id);
    Issue issueUpdating = issueOptional.orElseThrow(() -> {
      throw new NotFoundException("Issue not found");
    });
    issueUpdating = issueRequest.toUpdateIssue(issueUpdating);
    issueUpdating.setModifiedBy(userCurrent.getId());
    this.issueRepository.save(issueUpdating);
    this.issueHistoryRepository.save(new IssueHistory(issueUpdating, "Updated issue"));
  }

  @Transactional
  public void updateIssueStatus(Integer id, UpdateIssueStatusRequest updateIssueStatusRequest) {
    User userCurrent = this.authenUserService.getUserCurrentLogin();
    Optional<Issue> issueOptional = this.issueRepository.findById(id);
    Issue issueUpdating = issueOptional.orElseThrow(() -> {
      throw new NotFoundException("Issue not found");
    });
    issueUpdating.setModifiedBy(userCurrent.getId());
    issueUpdating.setStatus(updateIssueStatusRequest.getStatus());
    this.issueRepository.save(issueUpdating);
    String changedNote = String.format("Changed %s status", updateIssueStatusRequest.getStatus());
    if (updateIssueStatusRequest.getChangedNote() != null) {
      changedNote += String.format(" - %s", updateIssueStatusRequest.getChangedNote());
    }
    this.issueHistoryRepository.save(new IssueHistory(issueUpdating, changedNote));
  }

  public Issue findIssueById(Integer id) {
    Optional<Issue> issueOptional = this.issueRepository.findById(id);
    return issueOptional.orElseThrow(() -> {
      throw new NotFoundException("Issue not found");
    });
  }

  public Page<IssueView> findIssues(
      Integer versionId,
      Optional<String> shortDescription,
      Optional<String> ticket,
      Optional<String> severity,
      Optional<String> priority,
      Optional<String> reporter,
      Optional<String> fixer,
      Optional<IssueStatus> status,
      Pageable pageable
  ) {
    IssueSpecification issueSpecification = IssueSpecification.builder()
        .versionId(versionId)
        .shortDescription(shortDescription)
        .ticket(ticket)
        .severity(severity)
        .priority(priority)
        .reporter(reporter)
        .fixer(fixer)
        .status(status).build();
    return this.issueViewRepository.findAll(issueSpecification, pageable);
  }

  public List<IssueHistory> findIssueHistories(Integer issueId) {
    return this.issueHistoryRepository.findByIssueId(issueId);
  }

  public IssueHistory findHistoryDetail(Integer historyId) {
    return this.issueHistoryRepository.findById(historyId).orElseThrow(() -> {
      throw new NotFoundException("Issue history not found");
    });
  }
}
