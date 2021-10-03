package com.codingandshare.tracking.domains;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity for issue table.
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Entity
@Table(name = "issue")
public class Issue extends BaseIssue {
}
