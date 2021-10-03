package com.codingandshare.tracking.domains;

import lombok.Data;

import javax.persistence.*;

/**
 * The entity mapping to issue_metadata table in db
 *
 * @author Nhan Dinh
 * @since 10/2/21
 **/
@Data
@Entity
@Table(name = "issue_metadata")
public class IssueMetaData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String type;

  private String name;

  private String value;
}
