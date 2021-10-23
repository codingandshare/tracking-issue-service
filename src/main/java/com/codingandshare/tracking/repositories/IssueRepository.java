package com.codingandshare.tracking.repositories;

import com.codingandshare.tracking.domains.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * Repository relate to issue table
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
public interface IssueRepository extends JpaRepository<Issue, Integer>,
    PagingAndSortingRepository<Issue, Integer> {

  Optional<Issue> findIssueByTicket(String ticket);
}
