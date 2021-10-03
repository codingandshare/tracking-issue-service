package com.codingandshare.tracking.controllers;

import com.codingandshare.tracking.domains.Issue;
import com.codingandshare.tracking.domains.IssueHistory;
import com.codingandshare.tracking.domains.IssueStatus;
import com.codingandshare.tracking.domains.IssueView;
import com.codingandshare.tracking.dtos.IssueRequest;
import com.codingandshare.tracking.dtos.PageDTO;
import com.codingandshare.tracking.dtos.UpdateIssueStatusRequest;
import com.codingandshare.tracking.services.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * Define all endpoints for issue
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Validated
@RestController
@RequestMapping(path = "/tracking/issue")
public class IssueController {

  @Autowired
  private IssueService issueService;

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewIssue(@RequestBody @Valid IssueRequest issueRequest) {
    this.issueService.addIssue(issueRequest);
  }

  @PutMapping(
      path = "{id}",
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public void updateIssue(
      @PathVariable(name = "id") Integer id,
      @RequestBody @Valid IssueRequest issueRequest
  ) {
    this.issueService.updateIssue(id, issueRequest);
  }

  @PutMapping(
      path = "{id}/status",
      consumes = MediaType.APPLICATION_JSON_VALUE
  )
  public void changeIssueStatus(
      @PathVariable(name = "id") Integer id,
      @RequestBody @Valid UpdateIssueStatusRequest updateIssueStatusRequest
  ) {
    this.issueService.updateIssueStatus(id, updateIssueStatusRequest);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public PageDTO<IssueView> findIssues(
      @RequestParam(name = "versionId", required = false)
      @NotNull(message = "versionId is missing") Integer versionId,
      @RequestParam(name = "shortDescription", required = false) Optional<String> shortDescription,
      @RequestParam(name = "ticket", required = false) Optional<String> ticket,
      @RequestParam(name = "severity", required = false) Optional<String> severity,
      @RequestParam(name = "priority", required = false) Optional<String> priority,
      @RequestParam(name = "reporter", required = false) Optional<String> reporter,
      @RequestParam(name = "fixer", required = false) Optional<String> fixer,
      @RequestParam(name = "status", required = false) Optional<IssueStatus> status,
      @PageableDefault(sort = "modifiedTime") Pageable pageable
  ) {
    Page<IssueView> page = this.issueService.findIssues(
        versionId,
        shortDescription,
        ticket,
        severity,
        priority,
        reporter,
        fixer,
        status,
        pageable
    );
    return new PageDTO<>(page);
  }

  @GetMapping(
      path = "{id}",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public Issue getIssue(@PathVariable(name = "id") Integer id) {
    return this.issueService.findIssueById(id);
  }

  @GetMapping(
      path = "{id}/histories",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public List<IssueHistory> getIssueHistories(@PathVariable(name = "id") Integer id) {
    return this.issueService.findIssueHistories(id);
  }

  @GetMapping(
      path = "{historyId}/history",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  public IssueHistory getIssueHistory(@PathVariable(name = "historyId") Integer historyId) {
    return this.issueService.findHistoryDetail(historyId);
  }
}
