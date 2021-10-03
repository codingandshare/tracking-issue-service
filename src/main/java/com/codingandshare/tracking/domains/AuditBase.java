package com.codingandshare.tracking.domains;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

/**
 * Contain all field data audit
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Data
@MappedSuperclass
public class AuditBase {

  @Column(nullable = false)
  private Integer createdBy;

  @CreationTimestamp
  @Column(nullable = false)
  private Timestamp createdTime;

  @Column(nullable = false)
  private Integer modifiedBy;

  @UpdateTimestamp
  @Column(nullable = false)
  private Timestamp modifiedTime;
}
