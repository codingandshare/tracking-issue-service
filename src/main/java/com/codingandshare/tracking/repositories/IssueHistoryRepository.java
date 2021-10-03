package com.codingandshare.tracking.repositories;

import com.codingandshare.tracking.domains.IssueHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository relate to issue_history table
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
public interface IssueHistoryRepository extends JpaRepository<IssueHistory, Integer> {

  List<IssueHistory> findByIssueId(Integer issueId);
}
