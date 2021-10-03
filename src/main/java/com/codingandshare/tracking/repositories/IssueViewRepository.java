package com.codingandshare.tracking.repositories;

import com.codingandshare.tracking.domains.IssueView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * The repository relate to issue_view in database
 *
 * @author Nhan Dinh
 * @since 10/2/21
 */
public interface IssueViewRepository extends JpaRepository<IssueView, Integer>,
    PagingAndSortingRepository<IssueView, Integer> {

  Page<IssueView> findAll(Specification<IssueView> specification, Pageable pageable);
}
