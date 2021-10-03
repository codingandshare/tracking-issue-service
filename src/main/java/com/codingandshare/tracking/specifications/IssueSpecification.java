package com.codingandshare.tracking.specifications;

import com.codingandshare.tracking.domains.IssueStatus;
import com.codingandshare.tracking.domains.IssueView;
import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Custom filter issue
 *
 * @author Nhan Dinh
 * @since 10/2/21
 **/
@Builder
public class IssueSpecification implements Specification<IssueView> {

  private static final String WIL_CARD = "%";

  private static final String VERSION_ID_PROPERTY = "versionId";

  private static final String SHORT_DESCRIPTION_PROPERTY = "shortDescription";

  private static final String TICKET_PROPERTY = "ticket";

  private static final String SEVERITY_PROPERTY = "severity";

  private static final String PRIORITY_PROPERTY = "priority";

  private static final String REPORTER_PROPERTY = "reporter";

  private static final String FIXER_PROPERTY = "fixer";

  private static final String STATUS_PROPERTY = "status";

  private final Integer versionId;

  private final Optional<String> shortDescription;

  private final Optional<String>  ticket;

  private final Optional<String>  severity;

  private final Optional<String>  priority;

  private final Optional<String>  reporter;

  private final Optional<String>  fixer;

  private final Optional<IssueStatus>  status;

  @Override
  public Predicate toPredicate(
      Root<IssueView> root,
      CriteriaQuery<?> query,
      CriteriaBuilder criteriaBuilder
  ) {
    List<Predicate> predicateList = new ArrayList<>();

    predicateList.add(criteriaBuilder.equal(root.get(VERSION_ID_PROPERTY), versionId));

    this.shortDescription.ifPresent((it) -> {
      predicateList.add(criteriaBuilder.like(root.get(SHORT_DESCRIPTION_PROPERTY), WIL_CARD + it + WIL_CARD));
    });

    this.ticket.ifPresent((it) -> {
      predicateList.add(criteriaBuilder.equal(root.get(TICKET_PROPERTY), it));
    });

    this.severity.ifPresent((it) -> {
      predicateList.add(criteriaBuilder.equal(root.get(SEVERITY_PROPERTY), it));
    });

    this.priority.ifPresent((it) -> {
      predicateList.add(criteriaBuilder.equal(root.get(PRIORITY_PROPERTY), it));
    });

    this.reporter.ifPresent((it) -> {
      predicateList.add(criteriaBuilder.equal(root.get(REPORTER_PROPERTY), it));
    });

    this.fixer.ifPresent((it) -> {
      predicateList.add(criteriaBuilder.equal(root.get(FIXER_PROPERTY), it));
    });

    this.status.ifPresent((it) -> {
      predicateList.add(criteriaBuilder.equal(root.get(STATUS_PROPERTY), it));
    });

    return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
  }
}
