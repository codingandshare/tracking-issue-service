package com.codingandshare.tracking.domains;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * Define entity for version table
 *
 * @author Nhan Dinh
 * @since 9/26/21
 **/
@Entity
@Table(name = "version")
@Data
public class Version extends AuditBase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "version is missing")
  private String version;

  @NotBlank(message = "description is missing")
  @Column(columnDefinition = "text")
  private String description;
}
